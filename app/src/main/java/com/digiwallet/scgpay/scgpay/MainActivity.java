package com.digiwallet.scgpay.scgpay;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.support.design.widget.NavigationView;
import android.support.v7.widget.Toolbar;

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
    DatabaseReference userData, usersRef, userRef, transactionsDataRef;
    TextView balanceField;
    EditText amountField,upiField;
    User user;
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle abdt;
    Transaction transaction;
    String email1, email2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();

        balanceField = (TextView) findViewById(R.id.balanceField);
        amountField = (EditText) findViewById(R.id.amountField);
        upiField = (EditText) findViewById(R.id.upiField);

        transactionsDataRef = FirebaseDatabase.getInstance().getReference().child("Transactions");
        email1 = mAuth.getCurrentUser().getEmail();
        email2 = email1.replace(".", ",");




        findViewById(R.id.atwButton).setOnClickListener(this);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setHomeAsUpIndicator(R.drawable.ic_menu_black_24dp);

        mDrawerLayout = findViewById(R.id.drawer_layout);

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        // set item as selected to persist highlight
                        menuItem.setChecked(true);
                        // close drawer when item is tapped
                        mDrawerLayout.closeDrawers();

                        int id = menuItem.getItemId();
//
                        if (id == R.id.addToWallet) {
                            finish();
                            startActivity(new Intent(getApplicationContext(), MainActivity.class));
                        }

                        if (id == R.id.transfer) {
                            finish();
                            startActivity(new Intent(getApplicationContext(), TransferActivity.class));
                        }

                        if (id == R.id.myprofile) {
                            finish();
                            startActivity(new Intent(getApplicationContext(), UserProfileActivity.class));
                        }

                        if (id == R.id.signout) {
                            mAuth.signOut();
                            finish();
                            startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                        }

                        if (id == R.id.transactionSummary) {
                            finish();
                            startActivity(new Intent(getApplicationContext(), TransactionSummaryActivity.class));
                        }

                        return true;
                    }
                });

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        currentUser = mAuth.getCurrentUser();
//        if(currentUser==null){
//            finish();
//            startActivity(new Intent(MainActivity.this, LoginActivity.class));
//        }

        usersRef=FirebaseDatabase.getInstance().getReference().child("Users");
        userRef=usersRef.child(FirebaseAuth.getInstance().getCurrentUser().getUid());

        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
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
                break;
            }
        }
    }

    private void addToWallet(){
        String upiPin = upiField.getText().toString().trim();
        String amt = amountField.getText().toString().trim();

        if (amt.isEmpty()) {
            amountField.setError("Enter Valid Amount");
            amountField.requestFocus();
            return;
        }
        if (upiPin.isEmpty()) {
            upiField.setError("UPI Pin Required");
            upiField.requestFocus();
            return;
        }
        if (upiPin.length() != 4) {
            upiField.setError("Enter 4 digit UPI Pin");
            upiField.requestFocus();
            return;
        }

        final Double amount = Double.parseDouble(amt);

        if(upiPin.equals(user.upiPin)){
            if(!amountField.getText().toString().isEmpty()){
                if(amount<=user.bankBalance){
                    user.bankBalance-=amount;
                    user.walletBalance+=amount;
                    userData = FirebaseDatabase.getInstance().getReference().child("Users");
                    userData.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(getApplicationContext(), "Rs. "+amount.toString()+" Added to Wallet Successfully", Toast.LENGTH_SHORT).show();
                                transaction = new Transaction(email1, email1, user.bankName, amount);
                                String txn_id = String.valueOf(transaction.txn_id);
                                transactionsDataRef.child(email2).child(txn_id).setValue(transaction).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            Toast.makeText(getApplicationContext(), "Rs. " + amount.toString() + " Transaction Complete", Toast.LENGTH_SHORT).show();
                                            finish();
                                            startActivity(new Intent(MainActivity.this, MainActivity.class));
                                        }
                                    }
                                });
                                Toast.makeText(getApplicationContext(), "Rs. " + amount.toString() + " Added to Wallet Successfully", Toast.LENGTH_SHORT).show();


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
                amountField.setError("Enter Valid Amount");
                amountField.requestFocus();
                return;
            }
        }
        else{

            Toast.makeText(getApplicationContext(), "Incorrect UPI Pin !!!", Toast.LENGTH_SHORT).show();
            upiField.setError("Incorrect UPI Pin !!!");
            upiField.requestFocus();
            return;
        }
    }

    @Override
    public void onBackPressed() {
        mAuth.signOut();
        finish();
        startActivity(new Intent(getApplicationContext(), LoginActivity.class));
    }
}
