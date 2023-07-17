package com.example.cookapp;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Base64;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.appbar.CollapsingToolbarLayout;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

public class Activity_DetailMyRecipe extends AppCompatActivity {

    private ImageButton btnDelete, btnSet;
    private ImageView imgViewRecipe;
    private TextView textviewCongThuc;
    private Toolbar toolbar;
    private CollapsingToolbarLayout collapsingToolbarLayout;
    private Helper helper;
    public Integer ID;
    private String encodedImg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_my_recipe);
        init();
        loadRecipe();
        setListeners();
    }

    public void loadRecipe(){
        Intent intent = getIntent();
        String name = intent.getStringExtra(Constants.KEY_NAME_INTENT);
        encodedImg = intent.getStringExtra(Constants.KEY_IMAGE_INTENT);
        String cookingRecipe = intent.getStringExtra(Constants.KEY_COOKING_INTENT);
//        ID = (intent.getStringExtra(Constants.KEY_RECIPE));

//        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        collapsingToolbarLayout.setTitle(name);
        textviewCongThuc.setText(cookingRecipe);

        if(encodedImg != null){
//            Uri uri = Uri.parse(image);
//            imgViewRecipe.setImageURI(uri);
            imgViewRecipe.setImageBitmap(getBitMapImage(encodedImg));
        }
    }

    public void setListeners(){
        btnSet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Activity_DetailMyRecipe.this, Activity_Edit.class);

                intent.putExtra(Constants.KEY_NAME_INTENT, collapsingToolbarLayout.getTitle());
                intent.putExtra(Constants.KEY_COOKING_INTENT, textviewCongThuc.getText());
                intent.putExtra(Constants.KEY_IMAGE_INTENT, encodedImg);

                startActivity(intent);
            }
        });

        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//                Dialog dialog = new Dialog(Activity_DetailMyRecipe.this);
//                dialog.setContentView(R.layout.item_dialog);
//                Button btnOk = dialog.findViewById(R.id.btnOK);
//                Button btnCancel = dialog.findViewById(R.id.btnCancel);
//                TextView txtNoti = dialog.findViewById(R.id.txtViewDialog);
//
//                txtNoti.setText("Ban co chac xoa cong thuc khong?");
//
//                btnOk.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
                        helper = new Helper(Activity_DetailMyRecipe.this, Constants.KEY_SQL_RECIPE, null, 1);
                        if(helper.delete(Activity_MyRecipe.ID)){
                            Toast.makeText(Activity_DetailMyRecipe.this, "Delete successfully", Toast.LENGTH_SHORT).show();
                        }
                        Intent intent = new Intent(Activity_DetailMyRecipe.this, Activity_MyRecipe.class);
                        startActivity(intent);
//                    }
//                });
//
//                btnCancel.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        dialog.dismiss();
//                    }
//                });
//                dialog.show();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private Bitmap getBitMapImage(String encodedImage) {
        byte[] bytes = Base64.decode(encodedImage, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    }

    private void init(){
        btnDelete = findViewById(R.id.btnDelete);
        btnSet = findViewById(R.id.btnSet);
        imgViewRecipe = findViewById(R.id.imgViewRecipe);
        textviewCongThuc = findViewById(R.id.textviewCongThuc);
        collapsingToolbarLayout = findViewById(R.id.collapsingtoolbarLayout);
    }
}