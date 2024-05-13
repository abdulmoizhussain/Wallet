package com.example.abdul.bank.modelscore;

public class WalletCore {
    public long id;
    public String dateString;
    public long dateLong;
    public long amount;
    public String details;

    public String amountString() {
        return Long.toString(amount);
    }
}
