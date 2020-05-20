package com.example.learnbitadmin.main.home.tab.withdraw.model;

public class Withdraw {

    private String bankName;
    private int bankNumber;
    private String destinationName;
    private String isSent;
    private int amount;
    private String userUid;
    private String dateTime;
    private String message;

    public Withdraw(String bankName, int bankNumber, String destinationName, String isSent, int amount, String userUid, String dateTime, String message) {
        this.bankName = bankName;
        this.bankNumber = bankNumber;
        this.destinationName = destinationName;
        this.isSent = isSent;
        this.amount = amount;
        this.userUid = userUid;
        this.dateTime = dateTime;
        this.message = message;
    }

    public Withdraw() {
    }

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    public int getBankNumber() {
        return bankNumber;
    }

    public void setBankNumber(int bankNumber) {
        this.bankNumber = bankNumber;
    }

    public String getDestinationName() {
        return destinationName;
    }

    public void setDestinationName(String destinationName) {
        this.destinationName = destinationName;
    }

    public void setSent(String sent) {
        isSent = sent;
    }

    public String getSent() {
        return isSent;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public String getUserUid() {
        return userUid;
    }

    public void setUserUid(String userUid) {
        this.userUid = userUid;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
