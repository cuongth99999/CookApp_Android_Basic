package com.example.cookapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class Activity_EditProfile extends AppCompatActivity {

    private EditText inputEditName, inputEditEmail;
    private Button btnSave;

    private Helper helper;

    private User user;

    public static int ID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        inputEditName = findViewById(R.id.inputEditName);
        inputEditEmail = findViewById(R.id.inputEditEmail);
        btnSave = findViewById(R.id.btnSave);

        ID = getIntent().getIntExtra("USER_ID", 1);

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                helper = new Helper(Activity_EditProfile.this, Constants.KEY_ACCOUNT, null, 1);
                user = new User();
                user.setName(inputEditName.getText().toString().trim());
                user.setEmail(inputEditEmail.getText().toString().trim());
                user.setId(Activity_EditProfile.ID);

                if (helper.updateProfile(user, Activity_EditProfile.ID)) {
                    Toast.makeText(Activity_EditProfile.this, "Cập nhật thông tin thành công.", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(Activity_EditProfile.this, "Cập nhật thông tin thất bại.", Toast.LENGTH_SHORT).show();
                }

                Intent intent = new Intent(Activity_EditProfile.this, Activity_Profile.class);
                startActivity(intent);
            }
        });
    }
}