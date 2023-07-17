package com.example.cookapp;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

public class Activity_Profile extends AppCompatActivity {

    private ImageView imgAvatar;
    private TextView txtNameProfile, txtEmailProfile, txtSignOut, txtPhone;
    private Button btnAddRecipe, btnMyRecipe;
    private PreferenceManager preferenceManager;
    private ImageButton btnBack;

    private ImageView btnEditProfile;
    private String encodedImg;
    private Helper helper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        init();
        loadName();
        setListeners();
    }

    public void setListeners(){
        txtSignOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                preferenceManager.clear();

                Intent intent = new Intent(Activity_Profile.this, Activity_lognIn.class);
                startActivity(intent);
                Toast.makeText(Activity_Profile.this, "Đăng xuất...", Toast.LENGTH_SHORT).show();
                finish();
            }
        });

        btnAddRecipe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Activity_Profile.this, Activity_AddRecipe.class);
                startActivity(intent);
            }
        });

        btnMyRecipe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Activity_Profile.this, Activity_MyRecipe.class);
                startActivity(intent);
            }
        });

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Activity_Profile.this, MainActivity.class);
                startActivity(intent);
            }
        });

        imgAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                pickImage.launch(intent);

            }
        });

        btnEditProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Activity_Profile.this, Activity_EditProfile.class);
                startActivity(intent);
            }
        });
    }

    public void loadName(){
        String name, email, image, phone;
        name = preferenceManager.getString(Constants.KEY_NAME);
        email = preferenceManager.getString(Constants.KEY_EMAIL);
        image = preferenceManager.getString(Constants.KEY_IMAGE);
        phone = preferenceManager.getString(Constants.KEY_SDT);
        if(phone != null){
            txtPhone.setText(phone);
        }
        else txtPhone.setText("");
        txtNameProfile.setText(name);
        txtEmailProfile.setText(email);
        if(image != null){
//            Uri uri = Uri.parse(image);
//            getContentResolver().takePersistableUriPermission(uri,
//                            Intent1.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
//            imgAvatar.setImageURI(uri);

            imgAvatar.setImageBitmap(getImage(image));
        }
    }

    private Bitmap getImage(String encodedImage) {
        byte[] bytes = Base64.decode(encodedImage, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
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
                            imgAvatar.setImageBitmap(bitmap);
                            encodedImg = encodeImage(bitmap);
                            helper = new Helper(Activity_Profile.this, Constants.KEY_ACCOUNT, null, 1);
                            User user = new User(preferenceManager.getInt(Constants.KEY_ID), preferenceManager.getString(Constants.KEY_NAME),
                                    preferenceManager.getString(Constants.KEY_EMAIL), preferenceManager.getString(Constants.KEY_PASSWORD),
                                    encodedImg);
                            if(helper.updateImgProfile(user)){
                                preferenceManager.putString(Constants.KEY_IMAGE, encodedImg);
                            }
                        }catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
    );

    public void init(){
        imgAvatar = findViewById(R.id.avatar);
        txtEmailProfile = findViewById(R.id.txtEmailProfile);
        txtNameProfile = findViewById(R.id.txtNameProfile);
        txtSignOut = findViewById(R.id.txtSignOut);
        btnAddRecipe = findViewById(R.id.btnAddRecipe);
        btnMyRecipe = findViewById(R.id.btnMyRecipe);
        btnBack = findViewById(R.id.btnBack);
        txtPhone = findViewById(R.id.txtPhone);
        btnEditProfile = findViewById(R.id.btnEditProfile);

        preferenceManager = new PreferenceManager(getApplicationContext());
    }
}