package com.digiwallet.scgpay.scgpay;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
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
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;


import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.draw.LineSeparator;

public class TransactionSummaryActivity extends AppCompatActivity {


    FirebaseUser currentUser;
    DatabaseReference transactionsDataRef;
    TextView balanceField;
    public static final int REQUEST_PERM_WRITE_STORAGE = 102;
    Transaction transaction;
    String email1;
    ArrayList<Transaction> userTransactions;
    public ArrayList<DataSnapshot> dslist;
    private FirebaseAuth mAuth;
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle abdt;
    Button pdfButton;


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
        dslist = new ArrayList<>();

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
                userTransactions.clear();
                dslist.clear();
                for (DataSnapshot userT : dataSnapshot.getChildren()) {
                    dslist.add(userT);
                    Log.d("EXACTLY", userT.getValue() + "");
                    transaction = userT.getValue(Transaction.class);
                    userTransactions.add(transaction);
                }
                mAdapter = new MyAdapter(userTransactions);
                recyclerView.setAdapter(mAdapter);
                recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getApplicationContext(), recyclerView, new RecyclerTouchListener.ClickListener() {
                    @Override
                    public void onClick(View view, final int position) {

                        pdfButton = (Button) view.findViewById(R.id.pdfButton);


                        pdfButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                transaction = userTransactions.get(position);
                                if (ActivityCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.WRITE_EXTERNAL_STORAGE) !=
                                        PackageManager.PERMISSION_GRANTED) {
                                    ActivityCompat.requestPermissions(TransactionSummaryActivity.this,
                                            new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_PERM_WRITE_STORAGE);
                                } else {
                                    // PDF Generation
                                    Toast.makeText(getApplicationContext(), "Downloading...", Toast.LENGTH_SHORT).show();
                                    Document doc = new Document();
                                    try {
                                        String path = android.os.Environment.getExternalStorageDirectory().getAbsolutePath() + "/PDF";
                                        //Toast.makeText(this, path, Toast.LENGTH_SHORT).show();
                                        File dir = new File(path);
                                        if (!dir.exists()) {
                                            dir.mkdirs();
                                        }
                                        Log.d("PDFCreator", "PDF Path: " + path);

                                        //pdf of name invoice + vehicleType + vehicleNumber
                                        File file = new File(dir, "invoice" + transaction.txn_id.toString() + ".pdf");
                                        FileOutputStream fout = new FileOutputStream(file);

                                        PdfWriter.getInstance(doc, fout);
                                        doc.open();

                                        //Toast.makeText(MainActivity.this, "Pressed", Toast.LENGTH_LONG).show();

                                        Paragraph p = new Paragraph("SCGPAY");

                                        p.setAlignment(Paragraph.ALIGN_CENTER);
                                        doc.add(p);

                                        LineSeparator lineSeparator = new LineSeparator();
                                        lineSeparator.setLineColor(new BaseColor(0, 0, 0, 100));
                                        doc.add(new Chunk(lineSeparator));

                                        Paragraph title = new Paragraph("Transaction Summary");
                                        title.setAlignment(Paragraph.ALIGN_CENTER);
                                        doc.add(title);

                                        doc.add(new Chunk(lineSeparator));


                                        Paragraph date = new Paragraph("Transaction Date : " + transaction.timeStamp);
                                        date.setAlignment(Paragraph.ALIGN_LEFT);
                                        doc.add(date);

                                        //Section End
                                        doc.add(new Chunk(lineSeparator));


                                        Paragraph fromID = new Paragraph("From ID : " + transaction.from_id);
                                        fromID.setAlignment(Paragraph.ALIGN_LEFT);
                                        doc.add(fromID);

                                        //Section End
                                        doc.add(new Chunk(lineSeparator));


                                        Paragraph toID = new Paragraph("To ID : " + transaction.to_id);
                                        toID.setAlignment(Paragraph.ALIGN_LEFT);
                                        doc.add(toID);

                                        //Section End
                                        doc.add(new Chunk(lineSeparator));


                                        Paragraph amount = new Paragraph("Amount : " + transaction.amount);
                                        amount.setAlignment(Paragraph.ALIGN_LEFT);
                                        doc.add(amount);

                                        //Section End
                                        doc.add(new Chunk(lineSeparator));


                                        Paragraph txnID = new Paragraph("Txn ID :  " + transaction.txn_id);
                                        txnID.setAlignment(Paragraph.ALIGN_LEFT);
                                        doc.add(txnID);

                                        //Section End
                                        doc.add(new Chunk(lineSeparator));


                                        Paragraph issuerBank = new Paragraph("Issuer Bank: " + transaction.issuerBank);
                                        issuerBank.setAlignment(Paragraph.ALIGN_LEFT);
                                        doc.add(issuerBank);


                                        doc.add(new Chunk(lineSeparator));


                                        Toast.makeText(getApplicationContext(), "created PDF", Toast.LENGTH_LONG).show();
                                    } catch (DocumentException de) {
                                        // de.printStackTrace();
                                        Log.e("PDFCreator", "Document Exception: " + de);
                                    } catch (IOException ioe) {
                                        //ioe.printStackTrace();
                                        Log.e("PDFCreator", "IO Exception: " + ioe);
                                    } finally {
                                        doc.close();
                                    }

                                }
                            }
                        });
                    }

                    @Override
                    public void onLongClick(View view, int position) {

                    }

                }));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(getApplicationContext(), "Something Happened!!! Couldn't load your balance.", Toast.LENGTH_SHORT).show();
            }
        });


    }

    @Override
    public void onBackPressed() {

        finish();
        startActivity(new Intent(getApplicationContext(), MainActivity.class));
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
        public Button pdfButton;

        public MyViewHolder(View v) {
            super(v);
            amountField = (TextView) v.findViewById(R.id.amountField);
            txnidField = (TextView) v.findViewById(R.id.txnidField);
            toField = (TextView) v.findViewById(R.id.toField);
            timeStampField = (TextView) v.findViewById(R.id.timeStampField);
            issuerBankField = (TextView) v.findViewById(R.id.issuerBankField);
            pdfButton = (Button) v.findViewById(R.id.pdfButton);
            pdfButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    switch (view.getId()) {
                        case R.id.pdfButton: {
                            int pos = getAdapterPosition();
                            Toast.makeText(itemView.getContext(), "hehe", Toast.LENGTH_SHORT).show();

                        }
                    }
                }
            });
        }
    }
}

