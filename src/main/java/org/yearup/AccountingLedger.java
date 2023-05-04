package org.yearup;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class AccountingLedger {
    int id;
    private LocalDateTime date; // stores the date and time of the transaction
    private String description; // stores the description of the transaction
    private String vendor; // stores the name of the vendor
    private double amount; // stores the transaction amount

    // constructor to create an AccountingLedger object
    public AccountingLedger(int id, LocalDateTime date, String description, String vendor, double amount) {
        this.id = id;
        this.date = date;
        this.description = description;
        this.vendor = vendor;
        this.amount = amount;
    }

    // getter method for id
    public int getId() {
        return id;
    }

    // setter method for id
    public void setId(int id) {
        this.id = id;
    }

    // getter method for date
    public LocalDateTime getDate() {
        return date;
    }

    // setter method for date
    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    // getter method for description
    public String getDescription() {
        return description;
    }

    // setter method for description
    public void setDescription(String description) {
        this.description = description;
    }

    // getter method for vendor
    public String getVendor() {
        return vendor;
    }

    // setter method for vendor
    public void setVendor(String vendor) {
        this.vendor = vendor;
    }

    // getter method for amount
    public double getAmount() {
        return amount;
    }

    // setter method for amount
    public void setAmount(double amount) {
        this.amount = amount;
    }

    // method to display the AccountingLedger object as a formatted string
    public String displayAsString() {
        return String.format("%-11s|%9s|%-20s|%-15s|%7s|\n" +
                        "-------------------------------------------------------------------\n"
                , date.toLocalDate(), date.toLocalTime().format(DateTimeFormatter.ofPattern("HH:mm:ss")),
                description, vendor, amount);
    }
}
