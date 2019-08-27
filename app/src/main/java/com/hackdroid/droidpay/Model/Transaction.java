package com.hackdroid.droidpay.Model;

public class Transaction {

    String uid , date , src , amount , type;

    public Transaction() {
    }

    public Transaction(String uid, String date, String src, String amount, String type) {

        this.uid = uid;
        this.date = date;
        this.src = src;
        this.amount = amount;
        this.type = type;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getSrc() {
        return src;
    }

    public void setSrc(String src) {
        this.src = src;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
