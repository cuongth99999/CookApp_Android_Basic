package com.example.cookapp;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Date;
import java.util.HashMap;

public class Activity_AddRecipe extends AppCompatActivity {

    private ImageView imgRecipe, imgBack, imgAddRecipe;
    private EditText txtCongThuc,txtTenMonAn;
    private PreferenceManager preferenceManager;
    private Uri uri;
    private String encodedImg;
    private ProgressDialog progressDialog;

    private Helper helper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_recipe);
        init();
        setListeners();
    }

    public void setListeners(){
        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
                finish();
            }
        });

        imgRecipe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
//                intent.setType("image/*");
//                startActivityForResult(intent, 12);
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                pickImage.launch(intent);
            }
        });

        imgAddRecipe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isValid()){
                    AddRecipe();
                    Intent intent = new Intent(Activity_AddRecipe.this, Activity_Profile.class);
                    startActivity(intent);
                }
            }
        });
    }

//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if(requestCode == 12 && resultCode == RESULT_OK && data != null){
//            uri =data.getData();
//            imgRecipe.setImageURI(uri);
//        }
//    }

//    public void addRecipe(){
//        progressDialog.show();
//        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
//        HashMap<String, Object> recipe = new HashMap<>();
//        recipe.put(Constants.KEY_NAME_RECIPE, txtTenMonAn.getText().toString().trim());
//        recipe.put(Constants.KEY_COOK_RECIPE, txtCongThuc.getText().toString().trim());
//        recipe.put(Constants.KEY_EMAIL_RECIPE, preferenceManager.getString(Constants.KEY_EMAIL));
//        recipe.put(Constants.KEY_IMAGE_RECIPE, encodedImg);
//
//        firestore.collection(Constants.KEY_COLLECTION_RECIPE)
//                .add(recipe)
//                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
//                    @Override
//                    public void onSuccess(DocumentReference documentReference) {
//                        progressDialog.dismiss();
//                        showToast("Thêm công thức thành công!");
//                        Intent intent = new Intent(Activity_AddRecipe.this, MainActivity.class);
//                        startActivity(intent);
//                        finish();
//                    }
//                })
//                .addOnFailureListener(new OnFailureListener() {
//                    @Override
//                    public void onFailure(@NonNull Exception e) {
//                        showToast(e.getMessage());
//                    }
//                });
//    }

    private void AddRecipe(){
        progressDialog.show();
        helper = new Helper(this, Constants.KEY_SQL_RECIPE, null, 1);
        helper.queryData("CREATE TABLE IF NOT EXISTS Recipe(ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "EMAIL TEXT, NAME_RECIPE TEXT, COOK_RECIPE TEXT, IMAGE TEXT)");
        String email, name, cook, image = "";
        email = preferenceManager.getString(Constants.KEY_EMAIL);
        name = txtTenMonAn.getText().toString().trim();
        cook = txtCongThuc.getText().toString().trim();
        image = encodedImg;

        Date date = new Date(System.currentTimeMillis());
        int s = date.getDate() + '/' + (date.getMonth() + 1) + '/' + (date.getYear() + 1900);

        helper.queryData("INSERT INTO Recipe VALUES(NULL, '" + email + "', '" + name + "', '" +
                cook + "', '" + image + "')");
        progressDialog.dismiss();
        showToast("Thêm công thức thành công");
    }

    private String encodeImage(Bitmap bitmap){
        int previewWidth = 150;
        int previewHeight = bitmap.getHeight() * previewWidth / bitmap.getWidth();
        Bitmap previewBitmap = Bitmap.createScaledBitmap(bitmap, previewWidth, previewHeight, false);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        previewBitmap.compress(Bitmap.CompressFormat.JPEG, 50, byteArrayOutputStream);
        byte[] bytes = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(bytes, Base64.DEFAULT);
    }

    private final ActivityResultLauncher<Intent> pickImage = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if(result.getResultCode() == RESULT_OK){
                    if(result.getData() != null){
                        Uri imageUri = result.getData().getData();
                        try {
                            InputStream inputStream = getContentResolver().openInputStream(imageUri);
                            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                            imgRecipe.setImageBitmap(bitmap);
                            encodedImg = encodeImage(bitmap);
                        }catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
    );

    public void showToast(String mess){
        Toast.makeText(this, mess, Toast.LENGTH_SHORT).show();
    }

    public Boolean isValid(){
        if(txtTenMonAn.getText().toString().isEmpty()){
            showToast("Hãy nhập tên món ăn");
            return false;
        }
        else if(txtCongThuc.getText().toString().isEmpty()){
            showToast("Hãy nhập công thức món ăn");
            return false;
        }
        return true;
    }

    public void init(){
        imgRecipe = findViewById(R.id.imgRecipe);
        imgBack = findViewById(R.id.imgBack);
        imgAddRecipe = findViewById(R.id.imgAddRecipe);
        txtCongThuc = findViewById(R.id.txtCongThuc);
        txtTenMonAn = findViewById(R.id.txtTenMonAn);

        progressDialog = new ProgressDialog(this);

        preferenceManager = new PreferenceManager(getApplicationContext());
    }
}