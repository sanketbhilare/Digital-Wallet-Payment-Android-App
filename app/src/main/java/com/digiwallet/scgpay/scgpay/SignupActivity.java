package com.digiwallet.scgpay.scgpay;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;

public class SignupActivity extends AppCompatActivity implements View.OnClickListener {

    private FirebaseAuth mAuth;
    EditText emailField;
    EditText passwordField;
    ProgressBar progressBar;

    public void userSignup()
    {


        String email=emailField.getText().toString().trim();
        String password=passwordField.getText().toString().trim();
        if(email.isEmpty())
        {
            emailField.setError("Email required");
            emailField.requestFocus();
            return;
        }
        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches())
        {
            emailField.setError("Enter a valid email");
            emailField.requestFocus();
            return;
        }
        if(password.isEmpty())
        {
            passwordField.setError("Password Required");
            passwordField.requestFocus();
            return;
        }
        if(password.length()<6)
        {
            passwordField.setError("Minimum length of password should be 6.");
            passwordField.requestFocus();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        progressBar.setVisibility(View.GONE);
                        if (task.isSuccessful()) {
                            Toast.makeText(getApplicationContext(), "Signup successfull", Toast.LENGTH_SHORT).show();
                            finish();
                            startActivity(new Intent(SignupActivity.this, Signup2Activity.class));
                        } else {

                            if (task.getException() instanceof FirebaseAuthUserCollisionException) {
                                Toast.makeText(getApplicationContext(), "You are already registered", Toast.LENGTH_SHORT).show();

                            } else {
                                Toast.makeText(getApplicationContext(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }

                        }
                    }
                });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        emailField = (EditText) findViewById(R.id.emailField);
        passwordField = (EditText) findViewById(R.id.passwordField);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        mAuth = FirebaseAuth.getInstance();

        findViewById(R.id.signupButton).setOnClickListener(this);
        findViewById(R.id.loginText).setOnClickListener(this);



    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){

            case R.id.loginText:
                startActivity(new Intent(getApplicationContext(),LoginActivity.class));
                break;

            case R.id.signupButton:
                userSignup();
                break;
        }
    }
}
