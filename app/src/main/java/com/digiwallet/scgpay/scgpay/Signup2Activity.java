package com.digiwallet.scgpay.scgpay;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Signup2Activity extends AppCompatActivity implements View.OnClickListener{


    EditText nameField;
    EditText mobileField;
    EditText addressField;
    EditText cityField;
    EditText upiField;
    FirebaseUser user;
    DatabaseReference databaseUsers;
    private FirebaseAuth mAuth;
    String[] Banks={"HDFC","ICICI","SBI","Axis"};
    java.util.Random random = new java.util.Random();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup2);
        nameField = findViewById(R.id.nameField);
        mobileField = findViewById(R.id.mobileField);
        addressField = findViewById(R.id.addressField);
        cityField = findViewById(R.id.cityField);
        upiField = findViewById(R.id.upiField);

        findViewById(R.id.continueButton).setOnClickListener(this);

        mAuth = FirebaseAuth.getInstance();

        user=mAuth.getCurrentUser();

    }

    public void userSignup()
    {

        databaseUsers = FirebaseDatabase.getInstance().getReference("Users");
        String name = nameField.getText().toString().trim();
        String mobile =mobileField.getText().toString().trim();
        String address=addressField.getText().toString().trim();
        String city=cityField.getText().toString().trim();
        String upi =upiField.getText().toString().trim();
        String bankName = Banks[random.nextInt(Banks.length)];


        if(name.isEmpty())
        {
            nameField.setError("Name Required");
            nameField.requestFocus();
            return;
        }
        if(mobile.isEmpty()||mobile.length()<10)
        {
            mobileField.setError("Email required");
            mobileField.requestFocus();
            return;
        }
        if(address.isEmpty())
        {
            addressField.setError("Password Required");
            addressField.requestFocus();
            return;
        }
        if(city.isEmpty())
        {
            cityField.setError("Password Required");
            cityField.requestFocus();
            return;
        }
        if(upi.isEmpty()||upi.length()<4)
        {
            upiField.setError("Email required");
            upiField.requestFocus();
            return;
        }


        User user = new User(mobile,upi,name,address,city,bankName);


        databaseUsers.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(getApplicationContext(), "Registered Successfully", Toast.LENGTH_SHORT).show();
                    finish();
                    startActivity(new Intent(Signup2Activity.this, MainActivity.class));

                } else {
                    Toast.makeText(getApplicationContext(), "Something Happened!!! Try Again", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }



    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.continueButton:
                userSignup();
                break;
        }
    }
}
