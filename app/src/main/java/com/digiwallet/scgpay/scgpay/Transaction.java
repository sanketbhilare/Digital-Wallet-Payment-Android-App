package com.digiwallet.scgpay.scgpay;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class Transaction {

    String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(Calendar.getInstance().getTime());
    String from_id,to_id;
    double txn_id = System.currentTimeMillis();
    String issuerBank;
    double amount;

}
