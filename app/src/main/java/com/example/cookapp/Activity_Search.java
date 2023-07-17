package com.example.cookapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class Activity_Search extends AppCompatActivity {

    private EditText edSearch;
    private ImageButton btnSearch;
    private GridView lstViewFood;

    private RecipeAdapter recipeAdapter;
    private ArrayList<Recipe> recipes;
    private PreferenceManager preferenceManager;

    private Button btnBack;

    private Helper helper;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        init();
        setListeners();
    }

    private void setListeners(){
        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String search = edSearch.getText().toString().trim();

                if(TextUtils.isEmpty(search)){
                    Toast.makeText(Activity_Search.this, "Nhập email tìm kiếm", Toast.LENGTH_SHORT).show();
                }
                else {
//                    getMyRecipeFirestore(search);
                    getMyRecipeSQLite(search);
                }
            }
        });

        lstViewFood.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(Activity_Search.this, Activity_Recipe.class);

                String name = recipes.get(position).getNameRecipe();
                String image = recipes.get(position).getImage();
                String cookingRecipe = recipes.get(position).getCookingRecipe();

                intent.putExtra(Constants.KEY_NAME_INTENT, name);
                intent.putExtra(Constants.KEY_IMAGE_INTENT, image);
                intent.putExtra(Constants.KEY_COOKING_INTENT, cookingRecipe);

                startActivity(intent);
            }
        });

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

    public void getMyRecipeFirestore(String str){
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        firestore.collection(Constants.KEY_COLLECTION_RECIPE)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            recipes = new ArrayList<>();
                            for(QueryDocumentSnapshot documentSnapshot : task.getResult()){
                                if(documentSnapshot.getString(Constants.KEY_EMAIL_RECIPE).equals(str)){
                                    Recipe recipe = new Recipe();
                                    recipe.setNameRecipe(documentSnapshot.getString(Constants.KEY_NAME_RECIPE));
                                    recipe.setEmail(documentSnapshot.getString(Constants.KEY_EMAIL_RECIPE));
                                    recipe.setImage(documentSnapshot.getString(Constants.KEY_IMAGE_RECIPE));
                                    recipe.setCookingRecipe(documentSnapshot.getString(Constants.KEY_COOK_RECIPE));
//                                    recipe.setId(documentSnapshot.getId());
                                    recipes.add(recipe);
                                }
                            }
                            recipeAdapter = new RecipeAdapter(recipes, Activity_Search.this);
                            lstViewFood.setAdapter(recipeAdapter);
                        }
                    }
                });
    }

    private void getMyRecipeSQLite(String str){
        helper = new Helper(this, Constants.KEY_SQL_RECIPE, null, 1);
        helper.queryData("CREATE TABLE IF NOT EXISTS Recipe(ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "EMAIL TEXT, NAME_RECIPE TEXT, COOK_RECIPE TEXT, IMAGE TEXT)");
        Cursor cursor = helper.getData("SELECT * FROM Recipe");
        recipes = new ArrayList<>();
        while (cursor.moveToNext()){
            if(cursor.getString(1).equals(str)){
                Recipe recipe = new Recipe();
                recipe.setNameRecipe(cursor.getString(2));
                recipe.setEmail(cursor.getString(1));
                recipe.setImage(cursor.getString(4));
                recipe.setCookingRecipe(cursor.getString(3));
                recipes.add(recipe);
            }
        }
        recipeAdapter = new RecipeAdapter(recipes, Activity_Search.this);
        lstViewFood.setAdapter(recipeAdapter);
    }

    public void init(){
        edSearch = findViewById(R.id.edSearch);
        btnSearch = findViewById(R.id.btnSearch);
        lstViewFood = findViewById(R.id.listViewFood);
        btnBack = findViewById(R.id.btnBack);

        preferenceManager = new PreferenceManager(getApplicationContext());
    }
}