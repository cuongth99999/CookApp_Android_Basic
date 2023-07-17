package com.example.cookapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ListView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.checkerframework.checker.units.qual.C;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class MainActivity extends AppCompatActivity {

    private ImageButton btnProfile, btnSearch;
    private GridView listViewFood;
    private PreferenceManager preferenceManager;
    private RecipeAdapter recipeAdapter;
    private ArrayList<Recipe> recipes;

    private Helper helper;
    public static int ID;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
        setListeners();
//        getListFireBase();
        getListSQLite();
    }

    public void setListeners(){
        btnProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, Activity_Profile.class);
                startActivity(intent);
            }
        });

        listViewFood.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                if(recipes.get(position).getEmail().equals(preferenceManager.getString(Constants.KEY_EMAIL))){
//                    Intent intent = new Intent(MainActivity.this, Activity_DetailMyRecipe.class);
//                    String name = recipes.get(position).getNameRecipe();
//                    String image = recipes.get(position).getImage();
//                    String cookingRecipe = recipes.get(position).getCookingRecipe();
//                    ID = recipes.get(position).getId();
//
//                    intent.putExtra(Constants.KEY_NAME_INTENT, name);
//                    intent.putExtra(Constants.KEY_IMAGE_INTENT, image);
//                    intent.putExtra(Constants.KEY_COOKING_INTENT, cookingRecipe);
//
//
//                    startActivity(intent);
//                }
                    Intent intent = new Intent(MainActivity.this, Activity_Recipe.class);

                    String name = recipes.get(position).getNameRecipe();
                    String image = recipes.get(position).getImage();
                    String cookingRecipe = recipes.get(position).getCookingRecipe();

                    intent.putExtra(Constants.KEY_NAME_INTENT, name);
                    intent.putExtra(Constants.KEY_IMAGE_INTENT, image);
                    intent.putExtra(Constants.KEY_COOKING_INTENT, cookingRecipe);

                    startActivity(intent);

            }
        });

        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, Activity_Search.class);
                startActivity(intent);
            }
        });
    }

    public void getListFireBase(){
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        firestore.collection(Constants.KEY_COLLECTION_RECIPE)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            recipes = new ArrayList<>();
                            for(QueryDocumentSnapshot documentSnapshot : task.getResult()){
                                Recipe recipe = new Recipe();
                                recipe.setNameRecipe(documentSnapshot.getString(Constants.KEY_NAME_RECIPE));
                                recipe.setEmail(documentSnapshot.getString(Constants.KEY_EMAIL_RECIPE));
                                recipe.setImage(documentSnapshot.getString(Constants.KEY_IMAGE_RECIPE));
                                recipe.setCookingRecipe(documentSnapshot.getString(Constants.KEY_COOK_RECIPE));
//                                recipe.setId(documentSnapshot.getId());
                                recipes.add(recipe);
                            }
                            recipeAdapter = new RecipeAdapter(recipes, MainActivity.this);
                            listViewFood.setAdapter(recipeAdapter);
                        }
                    }
                });
    }

    public void getListSQLite(){
        helper = new Helper(this, Constants.KEY_SQL_RECIPE, null, 1);
        helper.queryData("CREATE TABLE IF NOT EXISTS Recipe(ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "EMAIL TEXT, NAME_RECIPE TEXT, COOK_RECIPE TEXT, IMAGE TEXT)");
        Cursor cursor = helper.getData("SELECT * FROM Recipe");
        recipes = new ArrayList<>();
        while (cursor.moveToNext()){
            Recipe recipe = new Recipe();
            recipe.setNameRecipe(cursor.getString(2));
            recipe.setEmail(cursor.getString(1));
            recipe.setImage(cursor.getString(4));
            recipe.setCookingRecipe(cursor.getString(3));
            recipe.setId(cursor.getInt(0));
            recipes.add(recipe);
        }

        Collections.sort(recipes, new Comparator<Recipe>() {
            @Override
            public int compare(Recipe o1, Recipe o2) {
                return (int) o2.getId() - o1.getId();
            }
        });

        recipeAdapter = new RecipeAdapter(recipes, MainActivity.this);
        listViewFood.setAdapter(recipeAdapter);
    }

    public void init(){
        btnProfile = findViewById(R.id.btnProfile);
        listViewFood = findViewById(R.id.listViewFood);
        btnSearch = findViewById(R.id.btnSearch);

        preferenceManager = new PreferenceManager(getApplicationContext());
    }
}