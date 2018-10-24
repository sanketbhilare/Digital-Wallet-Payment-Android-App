package com.digiwallet.scgpay.scgpay;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;


public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private FirebaseAuth mAuth;

    EditText emailField;
    EditText passwordField;
    ProgressBar progressBar;
    Button loginButton;
    TextView signUpText;


    public void userLogin()
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


        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                            finish();
                        } else {
                            Toast.makeText(getApplicationContext(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();

                        }

                        // ...
                    }
                });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        emailField = (EditText) findViewById(R.id.emailField);
        passwordField = (EditText) findViewById(R.id.passwordField);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        loginButton = (Button) findViewById(R.id.loginButton);
        signUpText = (TextView) findViewById(R.id.signUpText);


        if (AppStatus.getInstance(this).isOnline()) {

            Toast.makeText(this, "You are online!!!!", Toast.LENGTH_SHORT).show();

        } else {

            emailField.setFocusable(false);
            passwordField.setFocusable(false);

            Toast.makeText(this, "Please check your connection!!! And restart the app", Toast.LENGTH_LONG).show();
        }

        mAuth = FirebaseAuth.getInstance();

        findViewById(R.id.loginButton).setOnClickListener(this);
        findViewById(R.id.signUpText).setOnClickListener(this);

    }

//    @Override
//    protected void onStart() {
//        super.onStart();
//
//        if (mAuth.getCurrentUser() != null) {
//            finish();
//            startActivity(new Intent(this, MainActivity.class));
//        }
//    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.signUpText:
                startActivity(new Intent(getApplicationContext(),SignupActivity.class));
                break;

            case R.id.loginButton:
                userLogin();
                break;
        }
    }
}
