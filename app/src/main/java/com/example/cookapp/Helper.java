package com.example.cookapp;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class Helper extends SQLiteOpenHelper {

    public Helper(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    public void queryData(String sql){
        SQLiteDatabase database = getWritableDatabase();
        database.execSQL(sql);
    }
    //
    public Cursor getData(String sql){
        SQLiteDatabase database = getReadableDatabase();
        return database.rawQuery(sql, null);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public boolean update(Recipe recipe, int ID){
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("EMAIL", recipe.getEmail());
        values.put("NAME_RECIPE", recipe.getNameRecipe());
        values.put("COOK_RECIPE", recipe.getCookingRecipe());
        values.put("IMAGE", recipe.getImage());
        int row = db.update("Recipe", values, "ID=?", new String[]{ID + ""});
        return (row>0);
    }

    public boolean updateProfile(User user, int ID){
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("NAME", user.getName());
        values.put("EMAIL", user.getEmail());
        int row = db.update("Account", values, "ID=?", new String[]{ID + ""});
        return (row>0);
    }

    public boolean delete(int ID){
        SQLiteDatabase db = getWritableDatabase();
        int row = db.delete("Recipe","ID=?", new String[]{ID + ""});
        return (row>0);
    }

    public boolean insert(Recipe recipe){
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("ID", recipe.getId());
        values.put("EMAIL", recipe.getEmail());
        values.put("NAME_RECIPE", recipe.getNameRecipe());
        values.put("COOK_RECIPE", recipe.getCookingRecipe());
        values.put("IMAGE", recipe.getImage());
        long row = db.insert("Recipe", null, values);
        return row > 0;
    }

    public boolean updateImgProfile(User user){
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("ID", user.getId());
        values.put("NAME", user.getName());
        values.put("EMAIL", user.getEmail());
        values.put("PASSWORD", user.getPassword());
        values.put("IMAGE", user.getImage());
        int row = db.update("Account", values, "EMAIL=?", new String[]{user.getEmail() + ""});
        return  (row > 0);
    }
}
