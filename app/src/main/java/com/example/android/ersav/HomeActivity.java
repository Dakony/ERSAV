package com.example.android.ersav;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class HomeActivity extends AppCompatActivity implements View.OnClickListener {
    private EditText userEmail,userPassword;
    private TextView createAccount,resetPassword;
    private Button submitBtn;
    private ProgressDialog loadingBar;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        intialization();
    }

    private void intialization()
    {
        userEmail = (EditText) findViewById(R.id.etemail);
        userPassword = (EditText) findViewById(R.id.etpassword);
        submitBtn = (Button) findViewById(R.id.btn_submit);
        createAccount = (TextView)findViewById(R.id.register);
        resetPassword = (TextView)findViewById(R.id.forgetPassword);

        loadingBar = new ProgressDialog(this);

        submitBtn.setOnClickListener(this);
        createAccount.setOnClickListener(this);
        resetPassword.setOnClickListener(this);


        mAuth = FirebaseAuth.getInstance();
    }

    @Override
    protected void onStart() {

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null)
        {
            LoginInUser();
        }
        super.onStart();
    }

    private void LoginInUser()
    {
        Intent intent = new Intent(HomeActivity.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId()){
            case R.id.btn_submit:
                AllowUserLogin();
                break;
            case R.id.register:
                startActivity(new Intent(HomeActivity.this,RegisterActivity.class));
                break;
            case R.id.forgetPassword:
                startActivity(new Intent(HomeActivity.this,ForgetPasswordActivity.class));
                break;
        }
    }

    private void AllowUserLogin()
    {
        final String email = userEmail.getText().toString().trim();
        String password = userPassword.getText().toString().trim();

        if(email.isEmpty()){
            userEmail.setError("Please email cannot be Empty");
            userEmail.requestFocus();
            return;
        }
        if (password.isEmpty()){
            userPassword.setError("Please Password Cannot be Empty");
            userPassword.requestFocus();
            return;
        }
        loadingBar.setTitle("Login");
        loadingBar.setMessage("Please wait, while we are authenticating your account...");
        loadingBar.show();
        loadingBar.setCanceledOnTouchOutside(true);

        mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    LoginInUser();
                    loadingBar.dismiss();
                }else{
                    Toast.makeText(HomeActivity.this,"Sorry incorrect Password or email",Toast.LENGTH_LONG).show();
                    loadingBar.dismiss();

                }
            }
        });
    }
}
