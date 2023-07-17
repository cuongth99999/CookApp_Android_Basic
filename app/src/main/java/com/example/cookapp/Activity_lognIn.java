package com.example.cookapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

public class Activity_lognIn extends AppCompatActivity {

    private EditText inputEmailLogIn, inputPasswordLogIn;
    private Button btnSignIn;
    private TextView texCreateNewAccount,txtViewEmailIncorec,txtViewPasswordIncorec;
    private PreferenceManager preferenceManager;
    private ProgressDialog progressDialog;
    private SharedPreferences sharedPreferences;

    private Helper helper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logn_in);
        init();
        autoLogin();
        setListeners();
        txtViewEmailIncorec.setText("");
        txtViewPasswordIncorec.setText("");
    }

    private void autoLogin(){
        boolean check = preferenceManager.getBoolean(Constants.KEY_IS_SIGNED_IN);
        if(check){
            Intent intent = new Intent(Activity_lognIn.this, MainActivity.class);
            startActivity(intent);
        }
    }

    private void setListeners(){
        texCreateNewAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Activity_lognIn.this, Activity_LognUp.class);
                startActivity(intent);
            }
        });

        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isValidSignIn()){
                    txtViewEmailIncorec.setText("");
                    txtViewPasswordIncorec.setText("");
                    SignIn();
                }
            }
        });
    }

//    private void signIn(){
//        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
////        progressDialog.show();
//        firestore.collection(Constants.KEY_COLLECTION_USERS)
//                .whereEqualTo(Constants.KEY_EMAIL, inputEmailLogIn.getText().toString().trim())
//                .whereEqualTo(Constants.KEY_PASSWORD, inputPasswordLogIn.getText().toString().trim())
//                .get()
//                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//                    @Override
//                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                        if(task.isSuccessful() && task.getResult() != null && task.getResult().getDocuments().size() > 0){
////                            progressDialog.dismiss();
//                            DocumentSnapshot documentSnapshot = task.getResult().getDocuments().get(0);
//                            preferenceManager.putBoolean(Constants.KEY_IS_SIGNED_IN, true);
//                            preferenceManager.putString(Constants.KEY_USER_ID, documentSnapshot.getId());
//                            preferenceManager.putString(Constants.KEY_IMAGE, documentSnapshot.getString(Constants.KEY_IMAGE));
//                            preferenceManager.putString(Constants.KEY_NAME, documentSnapshot.getString(Constants.KEY_NAME));
//                            preferenceManager.putString(Constants.KEY_EMAIL, documentSnapshot.getString(Constants.KEY_EMAIL));
//
//                            Intent intent = new Intent(Activity_lognIn.this, MainActivity.class);
//                            startActivity(intent);
//                            showToast("Đang đăng nhập...");
//                        }
//                        else{
//                            progressDialog.dismiss();
//                            showToast("Email hoặc mật khẩu không chính xác");
//                        }
//                    }
//                });
//    }

    private void SignIn(){
        helper = new Helper(this, Constants.KEY_ACCOUNT, null, 1);
        helper.queryData("CREATE TABLE IF NOT EXISTS Account(ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "NAME TEXT, EMAIL TEXT, PHONE TEXT, PASSWORD TEXT, IMAGE TEXT)");
        Cursor account = helper.getData("SELECT * FROM Account");
        while (account.moveToNext()){
            if(inputEmailLogIn.getText().toString().trim().equals(account.getString(2)) ||
            inputEmailLogIn.getText().toString().trim().equals(account.getString(3))){
                 if(inputPasswordLogIn.getText().toString().trim().equals(account.getString(4))) {
                     preferenceManager.putBoolean(Constants.KEY_IS_SIGNED_IN, true);
                     preferenceManager.putString(Constants.KEY_IMAGE, account.getString(5));
                     preferenceManager.putString(Constants.KEY_NAME, account.getString(1));
                     preferenceManager.putString(Constants.KEY_EMAIL, account.getString(2));
                     preferenceManager.putString(Constants.KEY_PASSWORD, account.getString(4));
                     preferenceManager.putString(Constants.KEY_SDT, account.getString(3));
                     preferenceManager.putInt(Constants.KEY_ID, account.getInt(0));


                     Intent intent = new Intent(Activity_lognIn.this, MainActivity.class);
                     startActivity(intent);
                     showToast("Đang đăng nhập...");
                 }
                 else {
//                     txtViewPasswordIncorec.setText("Password is incorrect");
                     showToast("Mật khẩu không chính xác");
                 }
                return;
            }
        }

//        txtViewEmailIncorec.setText("Email is incorrect");
//        txtViewPasswordIncorec.setText("Password is incorrect");

        showToast("Email hoặc mật khẩu không chính xác");
    }

    private void showToast(String mess){
        Toast.makeText(Activity_lognIn.this, mess, Toast.LENGTH_SHORT).show();
    }

    private Boolean isValidSignIn(){
        if(inputEmailLogIn.getText().toString().trim().isEmpty()){
            showToast("Email không được để trống");
//            txtViewEmailIncorec.setText("Enter your email");
            return false;
        }
//        else if(!Patterns.EMAIL_ADDRESS.matcher(inputEmailLogIn.getText().toString()).matches()){
//            showToast("Enter valid email");
////            txtViewPasswordIncorec.setText("Enter valid email");
//            return false;
//        }
        else if(inputPasswordLogIn.getText().toString().trim().isEmpty()){
            showToast("Hãy nhập mật khẩu của bạn");
//            txtViewPasswordIncorec.setText("Enter your password");
            return false;
        }
        else{
            return true;
        }
    }

    public void init(){
        inputEmailLogIn = findViewById(R.id.inputEmailLogIn);
        inputPasswordLogIn = findViewById(R.id.inputPasswordLogIn);
        btnSignIn = findViewById(R.id.btnSignIn);
        texCreateNewAccount = findViewById(R.id.texCreateNewAccount);
        txtViewEmailIncorec = findViewById(R.id.txtViewEmailIncorec);
        txtViewPasswordIncorec = findViewById(R.id.txtViewPasswordIncorec);

        preferenceManager = new PreferenceManager(getApplicationContext());

        progressDialog = new ProgressDialog(this);

        sharedPreferences = getSharedPreferences(Constants.KEY_SHAREPREFERENCES, Context.MODE_PRIVATE);
    }
}