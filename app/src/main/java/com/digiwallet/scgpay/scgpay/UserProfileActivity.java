package com.digiwallet.scgpay.scgpay;

import android.content.Intent;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class UserProfileActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    FirebaseUser currentUser;
    DatabaseReference usersRef, userRef;

    TextView emailText, nameText, mobileText, bankNameText, addressText, cityText, bBalanceText, wBalanceText;
    User user;
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle abdt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        emailText = (TextView) findViewById(R.id.emailText);
        nameText = (TextView) findViewById(R.id.nameText);
        mobileText = (TextView) findViewById(R.id.mobileText);
        bankNameText = (TextView) findViewById(R.id.bankNameText);
        addressText = (TextView) findViewById(R.id.addressText);
        cityText = (TextView) findViewById(R.id.cityText);
        wBalanceText = (TextView) findViewById(R.id.wBalanceText);
        bBalanceText = (TextView) findViewById(R.id.bBalanceText);

        mAuth = FirebaseAuth.getInstance();
        currentUser=mAuth.getCurrentUser();


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

                        if (id == R.id.addToWallet) {
                            finish();
                            startActivity(new Intent(getApplicationContext(), MainActivity.class));
                        }

                        if (id == R.id.transfer) {
                            finish();
                            startActivity(new Intent(getApplicationContext(), TransferActivity.class));
                        }

                        if (id == R.id.myprofile) {
                            Toast.makeText(UserProfileActivity.this, "Your Profile !", Toast.LENGTH_SHORT).show();
                        }

                        if (id == R.id.signout) {
                            mAuth.signOut();
                            finish();
                            startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                        }

                        if (id == R.id.settings) {
                            Toast.makeText(UserProfileActivity.this, "Settings !", Toast.LENGTH_SHORT).show();
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
        final String email = mAuth.getCurrentUser().getEmail();

        usersRef = FirebaseDatabase.getInstance().getReference().child("Users");
        userRef = usersRef.child(FirebaseAuth.getInstance().getCurrentUser().getUid());

        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                user = dataSnapshot.getValue(User.class);
                emailText.setText("Email : "+email);
                nameText.setText("Name : "+user.Name);
                mobileText.setText("Mobile : "+user.mobileNo);
                bankNameText.setText("Bank : "+user.bankName);
                addressText.setText("Address : "+user.Address);
                cityText.setText("City : "+user.City);
                wBalanceText.setText("Wallet Balance : "+String.valueOf(user.walletBalance));
                bBalanceText.setText("Bank Balance : "+String.valueOf(user.bankBalance));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(getApplicationContext(), "Something Happened!!! Couldn't load your balance.", Toast.LENGTH_SHORT).show();

            }
        });
    }

}