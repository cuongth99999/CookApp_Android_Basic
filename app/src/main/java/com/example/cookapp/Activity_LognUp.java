package com.example.cookapp;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.DialogPreference;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashMap;

public class Activity_LognUp extends AppCompatActivity {

    private EditText inputName, inputEmail, inputPassword, inputConfirmPassword, inputPhone;
    private Button btnSignUp;
    private TextView txtSignIn;
    private ImageView imgProfile;
    private Uri uri;
    private ProgressDialog progressDialog;
    private String encodedImg;
    private PreferenceManager preferenceManager;

    private Helper helper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logn_up);
        init();
        setListeners();
    }

    private void setListeners(){
        txtSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        imgProfile.setOnClickListener(new View.OnClickListener() {
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

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isValidSign()){
                    progressDialog.show();
                    SignUp();
                    onBackPressed();
                }
            }
        });
    }

//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if(requestCode == 12 && resultCode == RESULT_OK && data != null){
//            uri = data.getData();
//            imgProfile.setImageURI(uri);
//            getContentResolver().takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION);
//        }
//    }

    private void signUp(){
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        HashMap<String, Object> user = new HashMap<>();
        user.put(Constants.KEY_NAME, inputName.getText().toString().trim());
        user.put(Constants.KEY_EMAIL, inputEmail.getText().toString().trim());
        user.put(Constants.KEY_PASSWORD, inputPassword.getText().toString().trim());
        if(encodedImg != null) {
//            user.put(Constants.KEY_IMAGE, uri.toString());
            user.put(Constants.KEY_IMAGE, encodedImg);
        }
        else user.put(Constants.KEY_IMAGE, null);

        firestore.collection(Constants.KEY_COLLECTION_USERS)
                .add(user)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        showToast("Tạo tài khoản mới thành công!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        showToast(e.getMessage());
                    }
                });
    }

    private void SignUp(){
        helper = new Helper(this, Constants.KEY_ACCOUNT, null, 1);
//        helper.queryData("ALTER TABLE Account ADD COLUMN PHONE TEXT");
        helper.queryData("CREATE TABLE IF NOT EXISTS Account(ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "NAME TEXT, EMAIL TEXT, PHONE TEXT, PASSWORD TEXT, IMAGE TEXT)");
        String name, email, password, image = "", phone;
        name = inputName.getText().toString().trim();
        email = inputEmail.getText().toString().trim();
        password = inputPassword.getText().toString().trim();
        image = encodedImg;
        phone = inputPhone.getText().toString().trim();

        helper.queryData("INSERT INTO Account VALUES(NULL, '" + name + "', '" + email + "', '" + phone
                + "', '"+ password + "', '" + image +"')");
        showToast("Tạo tài khoản mới thành công!");
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
                            imgProfile.setImageBitmap(bitmap);
                            encodedImg = encodeImage(bitmap);
                        }catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
    );

    private void showToast(String mess){
        Toast.makeText(Activity_LognUp.this, mess, Toast.LENGTH_SHORT).show();
    }

    private Boolean isValidSign(){
        if(inputName.getText().toString().trim().isEmpty()){
            showToast("Tên không được để trống");
            return false;
        }
        else if(inputEmail.getText().toString().trim().isEmpty()){
            showToast("Email không được để trống");
            return false;
        }
        else if(!Patterns.EMAIL_ADDRESS.matcher(inputEmail.getText().toString()).matches()){
            showToast("Email không đúng định dạng");
            return false;
        }
        else if(inputPhone.getText().toString().trim().isEmpty()){
            showToast("Số điện thoại không được để trống");
            return false;
        }
        else if(inputPassword.getText().toString().trim().isEmpty()){
            showToast("Mật khẩu không được để trống");
            return false;
        }
        else if(inputConfirmPassword.getText().toString().trim().isEmpty()){
            showToast("Xác nhận mật khẩu không được để trống");
            return false;
        }
        else if(!inputPassword.getText().toString().equals(inputConfirmPassword.getText().toString())){
            showToast("Mật khẩu và xác nhận mật khẩu không trùng khớp");
            return false;
        }
        else {
            return true;
        }
    }

    public void init(){
        inputName = findViewById(R.id.inputName);
        inputEmail = findViewById(R.id.inputEmail);
        inputPassword = findViewById(R.id.inputPassword);
        inputConfirmPassword = findViewById(R.id.inputConfirmPassword);
        btnSignUp = findViewById(R.id.btnSignUp);
        txtSignIn = findViewById(R.id.txtSignIn);
        imgProfile = findViewById(R.id.imgProfile);
        inputPhone = findViewById(R.id.inputSDT);

        preferenceManager = new PreferenceManager(getApplicationContext());

        progressDialog = new ProgressDialog(this);
    }
}