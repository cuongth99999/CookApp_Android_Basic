package com.example.cookapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Base64;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.appbar.CollapsingToolbarLayout;

public class Activity_Recipe extends AppCompatActivity {

    private ImageView imgViewRecipe;
    private TextView textviewCongThuc;
    private Toolbar toolbar;
    private CollapsingToolbarLayout collapsingToolbarLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe);
        init();
        loadRecipe();
    }

    public void loadRecipe(){
        Intent intent = getIntent();
        String name = intent.getStringExtra(Constants.KEY_NAME_INTENT);
        String image = intent.getStringExtra(Constants.KEY_IMAGE_INTENT);
        String cookingRecipe = intent.getStringExtra(Constants.KEY_COOKING_INTENT);

//        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        collapsingToolbarLayout.setTitle(name);
        textviewCongThuc.setText(cookingRecipe);

        if(image != null){
//            Uri uri = Uri.parse(image);
//            imgViewRecipe.setImageURI(uri);
            imgViewRecipe.setImageBitmap(getBitMapImage(image));
        }
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

    public void init(){
        imgViewRecipe = findViewById(R.id.imgViewRecipe);
        textviewCongThuc = findViewById(R.id.textviewCongThuc);
        toolbar = findViewById(R.id.toolbar);
        collapsingToolbarLayout = findViewById(R.id.collapsingtoolbarLayout);

//        ActionBar actionBar = getSupportActionBar();
//        actionBar.hide();
//        actionBar.setTitle("sdjfhsuj");
    }
}