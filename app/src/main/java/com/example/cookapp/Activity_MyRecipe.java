package com.example.cookapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.media.Image;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class Activity_MyRecipe extends AppCompatActivity {

    private GridView gridViewMyRecipe;
    private RecipeAdapter recipeAdapter;
    private ArrayList<Recipe> recipes;
    private PreferenceManager preferenceManager;
    private ImageButton btnBack;

    private EditText editTextSearch;

    private ImageView btnTimKiem;

    private Helper helper;

    public static int ID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_recipe);
        init();
//        getMyRecipeFirestore();
        getMyRecipeSQLite();
        setListeners();
    }

    public void  setListeners(){
        gridViewMyRecipe.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(Activity_MyRecipe.this, Activity_DetailMyRecipe.class);
                String name = recipes.get(position).getNameRecipe();
                String image = recipes.get(position).getImage();
                String cookingRecipe = recipes.get(position).getCookingRecipe();
                ID = recipes.get(position).getId();

                intent.putExtra(Constants.KEY_NAME_INTENT, name);
                intent.putExtra(Constants.KEY_IMAGE_INTENT, image);
                intent.putExtra(Constants.KEY_COOKING_INTENT, cookingRecipe);


                startActivity(intent);
            }
        });

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Activity_MyRecipe.this, Activity_Profile.class);
                startActivity(intent);
            }
        });


        // Xy ly nut tim kiem
        btnTimKiem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String searchText = editTextSearch.getText().toString().trim();
                if (!searchText.isEmpty()) {
                    ArrayList<Recipe> filteredRecipes = new ArrayList<>();
                    for (Recipe recipe : recipes) {
                        if (recipe.getNameRecipe().toLowerCase().contains(searchText.toLowerCase())) {
                            filteredRecipes.add(recipe);
                        }
                    }
                    if (filteredRecipes.size() > 0) {
                        recipeAdapter = new RecipeAdapter(filteredRecipes, Activity_MyRecipe.this);
                        gridViewMyRecipe.setAdapter(recipeAdapter);
                    } else {
                        Toast.makeText(Activity_MyRecipe.this, "Không có sản phầm nào phù hợp.", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    recipeAdapter = new RecipeAdapter(recipes, Activity_MyRecipe.this);
                    gridViewMyRecipe.setAdapter(recipeAdapter);
                }
            }
        });
    }

    public void getMyRecipeFirestore(){
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        firestore.collection(Constants.KEY_COLLECTION_RECIPE)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            recipes = new ArrayList<>();
                            for(QueryDocumentSnapshot documentSnapshot : task.getResult()){
                                if(documentSnapshot.getString(Constants.KEY_EMAIL_RECIPE).equals(preferenceManager.getString(Constants.KEY_EMAIL))){
                                    Recipe recipe = new Recipe();
                                    recipe.setNameRecipe(documentSnapshot.getString(Constants.KEY_NAME_RECIPE));
                                    recipe.setEmail(documentSnapshot.getString(Constants.KEY_EMAIL_RECIPE));
                                    recipe.setImage(documentSnapshot.getString(Constants.KEY_IMAGE_RECIPE));
                                    recipe.setCookingRecipe(documentSnapshot.getString(Constants.KEY_COOK_RECIPE));
//                                    recipe.setId(documentSnapshot.getId());
                                    recipes.add(recipe);
                                }
                            }
                            recipeAdapter = new RecipeAdapter(recipes, Activity_MyRecipe.this);
                            gridViewMyRecipe.setAdapter(recipeAdapter);
                        }
                    }
                });
    }

    private void getMyRecipeSQLite(){
        helper = new Helper(this, Constants.KEY_SQL_RECIPE, null, 1);
        helper.queryData("CREATE TABLE IF NOT EXISTS Recipe(ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "EMAIL TEXT, NAME_RECIPE TEXT, COOK_RECIPE TEXT, IMAGE TEXT)");
        Cursor cursor = helper.getData("SELECT * FROM Recipe");
        recipes = new ArrayList<>();
        while (cursor.moveToNext()){
            if(cursor.getString(1).equals(preferenceManager.getString(Constants.KEY_EMAIL))){
                Recipe recipe = new Recipe();
                recipe.setId(cursor.getInt(0));
                recipe.setNameRecipe(cursor.getString(2));
                recipe.setEmail(cursor.getString(1));
                recipe.setImage(cursor.getString(4));
                recipe.setCookingRecipe(cursor.getString(3));
                recipes.add(recipe);
            }
        }
        recipeAdapter = new RecipeAdapter(recipes, Activity_MyRecipe.this);
        gridViewMyRecipe.setAdapter(recipeAdapter);
    }

    public void init(){
        btnBack = findViewById(R.id.btnBack);
        btnTimKiem = findViewById(R.id.btnTimKiem);
        editTextSearch = findViewById(R.id.editTextSearch);

        gridViewMyRecipe = findViewById(R.id.gridViewMyRecipe);

        preferenceManager = new PreferenceManager(getApplicationContext());
    }
}