package com.digiwallet.scgpay.scgpay;

import android.content.Intent;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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

import java.util.ArrayList;
import java.util.HashMap;

public class TransactionSummaryActivity extends AppCompatActivity {


    FirebaseUser currentUser;
    DatabaseReference transactionsDataRef;
    TextView balanceField;
    Transaction transaction;
    String email1;
    ArrayList<Transaction> userTransactions;
    private FirebaseAuth mAuth;
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle abdt;


    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction_summary);

        mAuth = FirebaseAuth.getInstance();
        transactionsDataRef = FirebaseDatabase.getInstance().getReference().child("Transactions");
        email1 = mAuth.getCurrentUser().getEmail().replace(".", ",");

        userTransactions = new ArrayList<>();

        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);

        mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);


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
                            Toast.makeText(getApplicationContext(), "Already Open !!!", Toast.LENGTH_SHORT).show();

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

        userTransactions.clear();
        // Check if user is signed in (non-null) and update UI accordingly.
        currentUser = mAuth.getCurrentUser();
//        if(currentUser==null){
//            finish();
//            startActivity(new Intent(MainActivity.this, LoginActivity.class));
//        }

        transactionsDataRef.child(email1).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot userT : dataSnapshot.getChildren()) {
                    Log.d("EXACTLY", userT.getValue() + "");
                    userTransactions.add(userT.getValue(Transaction.class));
                }
                mAdapter = new MyAdapter(userTransactions);
                recyclerView.setAdapter(mAdapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(getApplicationContext(), "Something Happened!!! Couldn't load your balance.", Toast.LENGTH_SHORT).show();
            }
        });


    }


}


class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {
    private ArrayList<Transaction> mDataset;

    public MyAdapter(ArrayList<Transaction> myDataset) {
        mDataset = myDataset;
    }

    @Override
    public MyAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_card, parent, false);
        MyViewHolder vh = new MyViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Transaction t = mDataset.get(position);
        holder.amountField.setText(String.valueOf(t.getAmount()));
        holder.txnidField.setText(String.valueOf(t.getTxn_id()));
        holder.toField.setText(t.getTo_id());
        holder.timeStampField.setText(t.getTimeStamp());
        holder.issuerBankField.setText(t.getIssuerBank());

    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView amountField, txnidField, toField, timeStampField, issuerBankField;

        public MyViewHolder(View v) {
            super(v);
            amountField = (TextView) v.findViewById(R.id.amountField);
            txnidField = (TextView) v.findViewById(R.id.txnidField);
            toField = (TextView) v.findViewById(R.id.toField);
            timeStampField = (TextView) v.findViewById(R.id.timeStampField);
            issuerBankField = (TextView) v.findViewById(R.id.issuerBankField);

        }
    }
}

