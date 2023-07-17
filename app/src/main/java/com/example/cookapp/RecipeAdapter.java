package com.example.cookapp;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class RecipeAdapter extends BaseAdapter {

    private ArrayList<Recipe> recipes;
    private Context context;

    public RecipeAdapter(ArrayList<Recipe> recipes, Context context) {
        this.recipes = recipes;
        this.context = context;
    }

    @Override
    public int getCount() {
        return recipes.size();
    }

    @Override
    public Object getItem(int position) {
        return recipes.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @SuppressLint("ViewHolder")
    @Override
    public View getView(int position, View view, ViewGroup parent) {
        view = LayoutInflater.from(context)
                .inflate(R.layout.item_recipe, parent, false);
        ImageView imgViewFood = view.findViewById(R.id.imgViewFood);
        TextView txtViewName = view.findViewById(R.id.txtViewName);
        TextView txtViewEmail = view.findViewById(R.id.txtViewEmail);

        Recipe recipe = recipes.get(position);
        txtViewName.setText(recipe.getNameRecipe());
        txtViewEmail.setText(recipe.getEmail());
        if(recipe.getImage() != null){
            imgViewFood.setImageBitmap(getBitMapImage(recipe.getImage()));
        }
        return view;
    }

    private Bitmap getBitMapImage(String encodedImage) {
        byte[] bytes = Base64.decode(encodedImage, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    }
}
