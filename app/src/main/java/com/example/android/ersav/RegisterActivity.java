package com.example.android.ersav;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {
    private EditText etEmail, etPass, etFullame, etPhone;
    private Button btnRegister;
    private ProgressDialog loadingBar;

    private FirebaseAuth mAuth;
    private DatabaseReference UsersRef;
    FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        intialization();
    }

    private void intialization()
    {
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        UsersRef = FirebaseDatabase.getInstance().getReference().child("Users");

        etEmail = (EditText) findViewById(R.id.etEmail);
        etPass = (EditText) findViewById(R.id.etPass);
        etEmail = (EditText) findViewById(R.id.etEmail);
        etFullame = (EditText) findViewById(R.id.etFname);
        etPhone = (EditText) findViewById(R.id.etPhone);
        btnRegister = (Button) findViewById(R.id.btn_register);

        loadingBar = new ProgressDialog(this);

        btnRegister.setOnClickListener(this);

    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null)
        {
            LoginInUser();
        }
    }

    private void LoginInUser()
    {
        Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    public void onClick(View v)
    {
        if(v == btnRegister){
            Register();
        }
    }

    private void Register()
    {
        final String email = etEmail.getText().toString().trim();
        String password = etPass.getText().toString().trim();

        if(email.isEmpty()){
            etEmail.setError("Please email cannot be Empty");
            etEmail.requestFocus();
            return;
        }

        if (password.isEmpty()){
            etPass.setError("Please Password Cannot be Empty");
            etPass.requestFocus();
            return;
        }

        loadingBar.setTitle("Register");
        loadingBar.setMessage("Please wait, while we are creating your account...");
        loadingBar.show();
        loadingBar.setCanceledOnTouchOutside(true);

        mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    registerUser();
                    loadingBar.dismiss();
                }else {
                    Toast.makeText(RegisterActivity.this,"Sorry we could not create your account",Toast.LENGTH_LONG).show();
                    loadingBar.dismiss();
                }
            }
        });
    }

    private void registerUser()
    {
        String fullname =  etFullame.getText().toString();
        String phone = etPhone.getText().toString();

        if(TextUtils.isEmpty(fullname)){
            etFullame.setError("Please Type your Full name");
            etFullame.requestFocus();
            return;
        }
        if(TextUtils.isEmpty(phone)){
            etPhone.setError("Please Type your Phone number");
            etPhone.requestFocus();
            return;
        }else {
            loadingBar.setTitle("Register");
            loadingBar.setMessage("Please wait, while we are creating your account...");
            loadingBar.show();
            loadingBar.setCanceledOnTouchOutside(true);

            HashMap userMap = new HashMap<>();
            userMap.put("fullname",fullname);
            userMap.put("phone",phone);
            UsersRef.push().updateChildren(userMap).addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task) {
                    if(task.isSuccessful()){
                        sendUserToMainActivity();
                        Toast.makeText(RegisterActivity.this, "Account Created Successfully", Toast.LENGTH_LONG).show();
                    }else {
                        String message = task.getException().getMessage();
                        Toast.makeText(RegisterActivity.this, "Error has Occurred" + message, Toast.LENGTH_LONG).show();
                    }
                }
            });

        }
    }

    private void sendUserToMainActivity()
    {
        Intent mainIntent = new Intent(RegisterActivity.this,MainActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainIntent);
        finish();
    }
}
