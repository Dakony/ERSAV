package com.example.android.ersav;

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
import com.google.firebase.auth.FirebaseAuth;

public class ForgetPasswordActivity extends AppCompatActivity {
    private EditText ResetPasswordInput;
    private Button ResetPasswordButton;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password);

        mAuth = FirebaseAuth.getInstance();
        ResetPasswordInput = (EditText) findViewById(R.id.reset_password_input);
        ResetPasswordButton = (Button) findViewById(R.id.reset_password_btn);

        ResetPasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                String userEmail = ResetPasswordInput.getText().toString();
                if(TextUtils.isEmpty(userEmail))
                {
                    Toast.makeText(ForgetPasswordActivity.this, "Please Provide an Email Address...", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    mAuth.sendPasswordResetEmail(userEmail).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task)
                        {
                            if(task.isSuccessful())
                            {
                                Toast.makeText(ForgetPasswordActivity.this, "Please Check your Email to Reset Your Password...", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(ForgetPasswordActivity.this,HomeActivity.class));
                            }else
                            {
                                String message = task.getException().getMessage();
                                Toast.makeText(ForgetPasswordActivity.this, "Error has Occurred " + message, Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });
    }
}
