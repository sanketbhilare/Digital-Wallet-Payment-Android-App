package com.digiwallet.scgpay.scgpay;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class Transaction {

    String timeStamp;
    String from_id,to_id;
    double txn_id;
    String issuerBank;
    double amount;

    Transaction(){

    }

    Transaction(String from_id,String to_id,String issuerBank,double amount){
        this.from_id = from_id;
        this.to_id = to_id;
        this.issuerBank = issuerBank;
        this.amount=amount;
        timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(Calendar.getInstance().getTime());
        txn_id = System.currentTimeMillis();
    }
}
