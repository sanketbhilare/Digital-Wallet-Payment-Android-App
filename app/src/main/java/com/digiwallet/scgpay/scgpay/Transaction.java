package com.digiwallet.scgpay.scgpay;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class Transaction {

    String timeStamp;
    String from_id,to_id;
    Long txn_id;
    String issuerBank;
    double amount;

    Transaction(){

    }

    Transaction(String from_id,String to_id,String issuerBank,double amount){
        this.from_id = from_id;
        this.to_id = to_id;
        this.issuerBank = issuerBank;
        this.amount=amount;
        timeStamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSSSS").format(Calendar.getInstance().getTime());
        txn_id = System.currentTimeMillis();
    }

    public double getAmount() {
        return amount;
    }

    public Long getTxn_id() {
        return txn_id;
    }

    public String getIssuerBank() {
        return issuerBank;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public String getTo_id() {
        return to_id;
    }
}
