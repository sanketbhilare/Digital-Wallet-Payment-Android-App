package com.digiwallet.scgpay.scgpay;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.EditText;

public class TransferActivity extends AppCompatActivity {

    EditText emailField, amountField, upiField;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transfer);

        emailField = findViewById(R.id.emailField);
        amountField = findViewById(R.id.amountField);
        upiField = findViewById(R.id.upiField);

        String email = emailField.getText().toString().trim();
        Double amount = Double.parseDouble(amountField.getText().toString().trim());
        String upiPin = upiField.getText().toString().trim();

    }
}