package org.yearup;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class AccountingLedger {
    int id;
    private LocalDateTime date;
    private String description;
    private String vendor;
    private double amount;

    public AccountingLedger(int id, LocalDateTime date, String description, String vendor, double amount) {
        this.id = id;
        this.date = date;
        this.description = description;
        this.vendor = vendor;
        this.amount = amount;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getVendor() {
        return vendor;
    }

    public void setVendor(String vendor) {
        this.vendor = vendor;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String displayAsString() {
        return String.format("%-11s|%9s|%-20s|%-15s|%7s|\n" +
                        "-------------------------------------------------------------------\n"
                ,date.toLocalDate(),date.toLocalTime().format(DateTimeFormatter.ofPattern("HH:mm:ss")),description,vendor,amount);
    }

}
