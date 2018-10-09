package com.digiwallet.scgpay.scgpay;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private FirebaseAuth mAuth;
    FirebaseUser currentUser;
    DatabaseReference userData,usersRef,userRef;
    TextView balanceField;
    TextView amountField;
    TextView upiField;
    User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();

        balanceField = (TextView) findViewById(R.id.balanceField);
        amountField = (TextView) findViewById(R.id.amountField);
        upiField = (TextView) findViewById(R.id.upiField);


        findViewById(R.id.atwButton).setOnClickListener(this);
        findViewById(R.id.signOutButton).setOnClickListener(this);

    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        currentUser = mAuth.getCurrentUser();
        if(currentUser==null){
            finish();
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
        }

        usersRef=FirebaseDatabase.getInstance().getReference("Users");
        userRef=usersRef.child(FirebaseAuth.getInstance().getCurrentUser().getUid());

        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                user = dataSnapshot.getValue(User.class);
                balanceField.setText(String.valueOf(user.walletBalance));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(getApplicationContext(), "Something Happened!!! Couldn't load your balance.", Toast.LENGTH_SHORT).show();

            }
        });


    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.atwButton: {
                addToWallet();
                finish();
                startActivity(new Intent(MainActivity.this, MainActivity.class));
                break;
            }

            case R.id.signOutButton:{
                FirebaseAuth.getInstance().signOut();
                finish();
                startActivity(new Intent(MainActivity.this, LoginActivity.class));
                break;
            }
        }
    }

    private void addToWallet(){
        String upiPin = upiField.getText().toString().trim();
        final Double amount = Double.parseDouble(amountField.getText().toString().trim());

        if(upiPin.equals(user.upiPin)){
            if(!amountField.getText().toString().isEmpty()){
                if(amount<=user.bankBalance){
                    user.bankBalance-=amount;
                    user.walletBalance+=amount;
                    userData = FirebaseDatabase.getInstance().getReference("Users");
                    userData.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(getApplicationContext(), "Rs. "+amount.toString()+" Added to Wallet Successfully", Toast.LENGTH_SHORT).show();
                                finish();
                                startActivity(new Intent(MainActivity.this, MainActivity.class));

                            } else {
                                Toast.makeText(getApplicationContext(), "Something Happened!!! Try Again", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
                else{
                    Toast.makeText(getApplicationContext(), "Not Enough Balance in your Bank Account !!!.", Toast.LENGTH_SHORT).show();
                }
            }
            else {
                Toast.makeText(getApplicationContext(), "Please enter a valid amount !!!", Toast.LENGTH_SHORT).show();
            }
        }
        else{
            Toast.makeText(getApplicationContext(), "Incorrect UPI Pin !!!", Toast.LENGTH_SHORT).show();
        }
    }
}
