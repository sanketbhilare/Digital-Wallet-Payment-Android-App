package com.digiwallet.scgpay.scgpay;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.Patterns;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
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

public class TransferActivity extends AppCompatActivity implements View.OnClickListener {

    EditText emailField, amountField, upiField;
    TextView balanceField;
    double amount;
    private FirebaseAuth mAuth;
    FirebaseUser currentUser;
    DatabaseReference usersRef, userRef, emailUidRef, emailRef, transactionsDataRef;
    User user1,user2;
    Uid Uid1,Uid2;
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle abdt;
    Transaction transaction;
    String email, email1, fromEmail, fromEmail1;


    int flag = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transfer);

        emailField = (EditText) findViewById(R.id.emailField);
        amountField = (EditText) findViewById(R.id.amountField);
        upiField = (EditText) findViewById(R.id.upiField);
        balanceField = (TextView) findViewById(R.id.balanceField);

        findViewById(R.id.transferButton).setOnClickListener(this);

        mAuth = FirebaseAuth.getInstance();
        currentUser=mAuth.getCurrentUser();

        transactionsDataRef = FirebaseDatabase.getInstance().getReference().child("Transactions");

        Uid1 = new Uid(currentUser.getUid());
        Uid2 = new Uid();


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

        currentUser = mAuth.getCurrentUser();
        if(currentUser==null){
            finish();
            startActivity(new Intent(getApplicationContext(), LoginActivity.class));
        }

        usersRef = FirebaseDatabase.getInstance().getReference().child("Users");
        userRef = usersRef.child(Uid1.uid);

        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                user1 = dataSnapshot.getValue(User.class);
                balanceField.setText(String.valueOf(user1.bankBalance));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(getApplicationContext(), "Something Happened!!! Couldn't load your balance.", Toast.LENGTH_SHORT).show();

            }
        });

    }


    public void transact() {
        if (flag == 0) {
            flag = 1;
            email1 = emailField.getText().toString().trim();
            String amt = amountField.getText().toString().trim();
            final String upiPin = upiField.getText().toString().trim();

            if (email1.isEmpty()) {
                emailField.setError("Email required");
                emailField.requestFocus();
                return;
            }
            if (!Patterns.EMAIL_ADDRESS.matcher(email1).matches()) {
                emailField.setError("Enter a valid email");
                emailField.requestFocus();
                return;
            }
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

            amount = Double.parseDouble(amountField.getText().toString());

            email = email1.replace(".", ",");
            fromEmail1 = currentUser.getEmail().trim();
            fromEmail = fromEmail1.replace(".", ",");

            emailUidRef = FirebaseDatabase.getInstance().getReference().child("EmailUid");
            emailRef = emailUidRef.child(email);


            emailRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Uid2 = dataSnapshot.getValue(Uid.class);
                    Log.d("Uid2", Uid2.uid + "  1");


                    usersRef = FirebaseDatabase.getInstance().getReference().child("Users");
                    final String id = Uid2.uid;
                    userRef = usersRef.child(id);

                    userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            user2 = dataSnapshot.getValue(User.class);

                            if (user1.upiPin.equals(upiPin)) {
                                if (user1.bankBalance >= amount) {
                                    if (Uid2.uid != null) {
                                        user1.bankBalance -= amount;
                                        user2.bankBalance += amount;
                                        usersRef.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(user1).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    usersRef.child(Uid2.uid).setValue(user2).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            if (task.isSuccessful()) {
                                                                transaction = new Transaction(fromEmail1, email1, user1.bankName, amount);
                                                                String txn_id = String.valueOf(transaction.txn_id);
                                                                transactionsDataRef.child(fromEmail).child(txn_id).setValue(transaction).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                    @Override
                                                                    public void onComplete(@NonNull Task<Void> task) {
                                                                        if (task.isSuccessful()) {
                                                                            Toast.makeText(getApplicationContext(), "Rs. " + amount + " Transferred to " + email1, Toast.LENGTH_SHORT).show();
                                                                            finish();
                                                                            startActivity(new Intent(getApplicationContext(), TransferActivity.class));
                                                                        }
                                                                    }
                                                                });


                                                            } else {
                                                                Toast.makeText(getApplicationContext(), "Something Happened!!! Try Again  1", Toast.LENGTH_SHORT).show();
                                                            }
                                                        }
                                                    });

                                                } else {
                                                    Toast.makeText(getApplicationContext(), "Something Happened!!! Try Again   2", Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });

                                    } else {
                                        Toast.makeText(getApplicationContext(), "Please enter a valid user !!!", Toast.LENGTH_SHORT).show();
                                        emailField.setError("Enter Valid User");
                                        emailField.requestFocus();
                                        return;
                                    }
                                } else {
                                    Toast.makeText(getApplicationContext(), "Please enter a valid amount !!!", Toast.LENGTH_SHORT).show();
                                    amountField.setError("Enter Valid Amount");
                                    amountField.requestFocus();
                                    return;
                                }
                            } else {
                                Toast.makeText(getApplicationContext(), "Incorrect UPI Pin !!!", Toast.LENGTH_SHORT).show();
                                upiField.setError("Incorrect UPI Pin !!!");
                                upiField.requestFocus();
                                return;
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            Toast.makeText(getApplicationContext(), "Something Happened!!! Couldn't load your balance.", Toast.LENGTH_SHORT).show();
                            finish();
                            startActivity(new Intent(TransferActivity.this, TransferActivity.class));

                        }
                    });

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Toast.makeText(getApplicationContext(), "Invalid email ID !!!", Toast.LENGTH_SHORT).show();
                    finish();
                    startActivity(new Intent(TransferActivity.this, TransferActivity.class));
                }
            });
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.transferButton:
                transact();
                break;
        }
    }

    @Override
    public void onBackPressed() {

        finish();
        startActivity(new Intent(getApplicationContext(), MainActivity.class));
    }
}