package com.example.cookapp;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
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

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

public class Activity_Edit extends AppCompatActivity {

    private ImageView imgRecipe, imgBack, imgSaveRecipe;
    private EditText txtCongThuc,txtTenMonAn;
    private String encodedImg;

    private Helper helper;
    private Recipe recipe;
    private PreferenceManager preferenceManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);
        init();
        loadRecipe();
        setListeners();
    }

    private void setListeners(){
        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
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

        imgSaveRecipe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                helper = new Helper(Activity_Edit.this, Constants.KEY_SQL_RECIPE, null, 1);
                recipe = new Recipe();
                recipe.setImage(encodedImg);
                recipe.setNameRecipe(txtTenMonAn.getText().toString().trim());
                recipe.setCookingRecipe(txtCongThuc.getText().toString().trim());
                recipe.setEmail(preferenceManager.getString(Constants.KEY_EMAIL));
                recipe.setId(Activity_MyRecipe.ID);

                if(helper.update(recipe, Activity_MyRecipe.ID)){
                    showToast("Cập nhật công thức thành công!");
                }

                Intent intent = new Intent(Activity_Edit.this, Activity_DetailMyRecipe.class);

                intent.putExtra(Constants.KEY_NAME_INTENT, txtTenMonAn.getText().toString().trim());
                intent.putExtra(Constants.KEY_COOKING_INTENT, txtCongThuc.getText().toString().trim());
                intent.putExtra(Constants.KEY_IMAGE_INTENT, encodedImg);

                startActivity(intent);
            }
        });
    }

    private void loadRecipe(){
        Intent intent = getIntent();
        String name = intent.getStringExtra(Constants.KEY_NAME_INTENT);
        encodedImg = intent.getStringExtra(Constants.KEY_IMAGE_INTENT);
        String cook = intent.getStringExtra(Constants.KEY_COOKING_INTENT);

        txtCongThuc.setText(cook);
        txtTenMonAn.setText(name);
        if(encodedImg != null){
            imgRecipe.setImageBitmap(getBitMapImage(encodedImg));
        }
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

    private Bitmap getBitMapImage(String encodedImage) {
        byte[] bytes = Base64.decode(encodedImage, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    }

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
        imgSaveRecipe = findViewById(R.id.imgSaveRecipe);
        txtCongThuc = findViewById(R.id.txtCongThuc);
        txtTenMonAn = findViewById(R.id.txtTenMonAn);

        preferenceManager = new PreferenceManager(getApplicationContext());
    }
}