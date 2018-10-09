package com.digiwallet.scgpay.scgpay;



public class User {
    String mobileNo,upiPin,Name,Address,City,bankName;
    double bankBalance,walletBalance;

    User(){

    }

    User(String mobileNo,String upiPin, String Name,String Address,String City,String bankName){
        this.mobileNo=mobileNo;
        this.upiPin=upiPin;
        this.Name=Name;
        this.Address=Address;
        this.City=City;
        bankBalance=1000.0;
        walletBalance=0.0;
        this.bankName = bankName;
    }

}
