package org.yearup;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.HashMap;
import java.util.InputMismatchException;
import java.util.Scanner;

public class AccountingLedgerApp {

    private HashMap<Integer,AccountingLedger> accountingLedgerRecord = new HashMap<>();
    private Scanner scanner;
    private FileWriter fileWriter = null;

    private boolean validInput = false;
    private String header;
    private int id = 0; // stores unique identifier that acts as a key for a hashmap


    public void run(){
        loadaccountingLedgerRecord();
        homeScreen();
    }

    public void loadaccountingLedgerRecord(){
        try {
            FileInputStream fileInputStream = new FileInputStream("transactions.csv");
            scanner = new Scanner(fileInputStream);
            header = scanner.nextLine(); // read and store the header line
            while (scanner.hasNextLine()){

                id = id + 1; // increment the value of the id
                String line = scanner.nextLine(); // store the contents of the line
                String[] transaction = line.split("\\|"); // split the line on the '|'

                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"); // data time formatter
                // parse the date and time from the first two columns of the line
                LocalDateTime dateTime = LocalDateTime.parse(transaction[0] +" "+transaction[1],formatter);

                String description = transaction[2];// get the description from the third column of the line
                String vendor = transaction[3];// get the vendor from the fourth column of the line
                Double amount = Double.parseDouble(transaction[4]);// get the amount from the fifth column of the line

                // add a new AccountingLedger object to the map and id as key
                accountingLedgerRecord.put(id, new AccountingLedger(id,dateTime,description,vendor,amount));
            }

        } catch (FileNotFoundException e) // handle any FileNotFoundException that might occur
        {
            System.out.println("File Not Found");
        }
        finally {
            scanner.close();
        }
    }
    public void homeScreen(){

        scanner = new Scanner(System.in); // Get input stream from user keyboard
        //accounting ledger app header
        System.out.println("========================");
        System.out.println("ACCOUNTING LEDGER APP");
        System.out.println("========================");
        //display options to user
        System.out.println("'D' - Add Deposit");
        System.out.println("'P' - Make Payment (Debit)");
        System.out.println("'L' - Ledger");
        System.out.println("'X' - Exit");

        do {
            System.out.print("Please select a command: ");
            String userInput = scanner.nextLine().toUpperCase().trim(); // get user Input

            switch (userInput){
                case "D":
                {
                    validInput = true;
                    makeTransaction("deposit");
                    break;
                }
                case "P":
                {
                    validInput = true;
                    makeTransaction("payment");
                    break;
                }
                case "L":
                {
                    validInput = true;
                    ledgerScreen(); // navigate to ledger screen
                    break;
                }
                case "X":
                {
                    System.exit(1);// exit the program
                    break;
                }
                default : {
                    System.out.println("Unrecognized input! Please try again."); // error message
                }
            }
        }while(!validInput);
    }

    private void writeTransaction(String type, double amount) {
        try {
            id++;
            fileWriter= new FileWriter("transactions.csv",true);

            String description = "";
            String vendor = "";

            // Get non-blank description from user
            while (description.isBlank()) {
                System.out.print("Enter the description: ");
                description = scanner.nextLine().trim();
                if (description.isBlank()) {
                    System.out.println("Description cannot be empty.");
                }
            }

            // Get non-blank vendor from user
            while (vendor.isBlank()) {
                System.out.print("Enter the vendor: ");
                vendor = scanner.nextLine().trim();
                if (vendor.isBlank()) {
                    System.out.println("Vendor name cannot be blank.");
                }
            }

            if (type.equals("payment") && amount > 0) {
                amount = -amount; // negate the value of payment amount
            }

            fileWriter.write(LocalDate.now() +"|"
                    + LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"))
                    +"|"+description+"|"+vendor+"|"+amount+"\n"); // write to file

            accountingLedgerRecord.put(id, new AccountingLedger(id,LocalDateTime.now(),description,vendor,amount));

            System.out.println(type.equals("deposit") ? "Deposit made" : "Payment made"); // success message


        } catch (InputMismatchException e){
            System.out.println("Please enter a numeric value."); // error message
            homeScreen();
        } catch (IOException ex)
        {
            System.out.println("File Not Found!"); //error message
            homeScreen(); //navigate to home screen
        }
        finally
        {
            try {
                fileWriter.flush();
                fileWriter.close();

                homeScreen(); // navigate to home screen
            } catch (Exception e){
                System.out.println("Something went wrong!"); // error message
                homeScreen(); // navigate to home screen
            }
        }
    }

    public void makeTransaction(String transactionType){
            try {
                System.out.printf("Please enter the %s amount: ",transactionType);
                double amount = scanner.nextDouble();

                scanner.nextLine();
                writeTransaction(transactionType, amount);
            }
            catch (InputMismatchException e){
                System.out.println("\nInvalid amount! Please enter a numeric value.");
                scanner.nextLine();
                makeTransaction(transactionType);
            }
    }

    public void ledgerScreen(){
        // display Ledger Screen Header
        System.out.println("========================");
        System.out.println("LEDGER SCREEN");
        System.out.println("========================");

        // display options for the ledger screen
        System.out.println("'A' - Display all entries");
        System.out.println("'D' - Display deposits");
        System.out.println("'P' - Display payments");
        System.out.println("'R' - Report");
        System.out.println("'0' - Home screen");
        System.out.print("Select a command: ");

        String userInput = scanner.nextLine().toUpperCase(); // store user input

        // execute the corresponding command based on the user input
        switch (userInput) {
            case "A":
            case "D":
            case "P":
                displayEntries(userInput);
                break;
            case "R":
                reportScreen();
                break;
            case "0":
                homeScreen();
                break;
            default:
                System.out.println("Unrecognized command! Please try again."); // warning message
                ledgerScreen();
                break;
        }

    }

    public void displayTableHeader(){

        System.out.println("-------------------------------------------------------------------");
        System.out.printf("%-11s|%-9s|%-20s|%-15s|%-7s|\n",header.split("\\|"));
        System.out.println("-------------------------------------------------------------------");
    }

    public void displayEntries(String entryType) {

        displayTableHeader();

        for (AccountingLedger accountingLedger: accountingLedgerRecord.values()){

            // display only payments
            if (entryType.equals("P") && accountingLedger.getAmount() < 0)
                System.out.print(accountingLedger.displayAsString());

            // display only deposits
            if (entryType.equals("D") && accountingLedger.getAmount() >= 0)
                System.out.printf(accountingLedger.displayAsString());

            //display all entries
            if (entryType.equals("A"))
                System.out.printf(accountingLedger.displayAsString());
        }

        System.out.print("\nPress any key to go to the ledger screen: ");
        scanner.nextLine();

        ledgerScreen();
    }

    public void reportScreen(){

        // display report screen header
        System.out.println("========================");
        System.out.println("Report SCREEN");
        System.out.println("========================");

        // display options for the report screen
        System.out.println("1 - Month To Date");
        System.out.println("2 - Previous Month");
        System.out.println("3 - Year To Date");
        System.out.println("4 - Previous Year");
        System.out.println("5 - Search By Vendor");
        System.out.println("6 - Home screen");
        System.out.println("7 - Custom Search");
        System.out.print("Please select command: ");
        String userInput = scanner.nextLine();

        switch (userInput){

            case "1": {
                System.out.println("Month to Date");

                monthToDateReport();

                ;break;
            }
            case "2": {
                System.out.println("Previous Month");

                previousMonthReport();

                break;
            }
            case "3": {

                System.out.println("Year to Date");

                yearToDateReport();

                break;
            }
            case "4": {
                System.out.println("Previous Year");

                previousYearReport();

                break;
            }
            case "5": {
                System.out.println("Search By Vendor");

                searchByVendorReport();

                break;
            }
            case "6": {
                homeScreen();
                break;
            }
            case "7":{
                customSearch();
                break;
            }
            default:{
                System.out.println("Invalid input! Please try again.");reportScreen();break;}
        }

    }

    private void searchByVendorReport() {

        System.out.print("Enter the vendor's name: ");
        String vendorName = scanner.nextLine();

        displayTableHeader();

        for (AccountingLedger accountingLedger: accountingLedgerRecord.values())
        {

            if (accountingLedger.getVendor().equalsIgnoreCase(vendorName))
            {
                System.out.printf(accountingLedger.displayAsString());
            }
        }


        System.out.print("\nPress any key to go to the ledger screen: ");
        scanner.nextLine();

        ledgerScreen();

    }

    private void previousYearReport() {

        int previousYear = LocalDateTime.now().minusYears(1).getYear();

        displayTableHeader();

        for(AccountingLedger accountingLedger: accountingLedgerRecord.values()){

            // stores the accounting ledger month
            int accountLedgerYear = accountingLedger.getDate().getYear(); // stores the accounting ledger year

            if (accountLedgerYear == previousYear)
            {
                System.out.printf(accountingLedger.displayAsString());
            }

        }

        System.out.print("\nPress any key to go to the ledger screen: ");
        scanner.nextLine();

        ledgerScreen();
    }

    private void yearToDateReport() {

        LocalDateTime currentDate = LocalDateTime.now();

        displayTableHeader();

        for(AccountingLedger accountingLedger: accountingLedgerRecord.values()){

            if (accountingLedger.getDate().toLocalDate()
                    .isAfter(currentDate.toLocalDate().minusYears(1)) &&
                    accountingLedger.getDate().toLocalDate()
                            .isBefore(currentDate.toLocalDate()))
            {
                System.out.print(accountingLedger.displayAsString());
            }

        }

        System.out.print("\nPress any key to go to the ledger screen: ");
        scanner.nextLine();

        ledgerScreen();

    }

    private void previousMonthReport() {

        int year = LocalDateTime.now().getYear(); // stores the current year
        Month previousMonth = LocalDateTime.now().minusMonths(1).getMonth(); // stores the previous month

        displayTableHeader();

        for(AccountingLedger accountingLedger: accountingLedgerRecord.values()){

            // stores the accounting ledger month
            Month accountingLedgerMonth = accountingLedger.getDate().getMonth();
            int accountLedgerYear = accountingLedger.getDate().getYear(); // stores the accounting ledger year

            if ((accountingLedgerMonth.equals(previousMonth)) && (accountLedgerYear == year))
            {
                System.out.print(accountingLedger.displayAsString());
            }

        }

        System.out.print("\nPress any key to go to the ledger screen: ");
        scanner.nextLine();

        ledgerScreen();
    }

    private void monthToDateReport() {

        displayTableHeader();

        int year = LocalDateTime.now().getYear();

        for(AccountingLedger accountingLedger: accountingLedgerRecord.values()){

            // stores the accounting ledger month
            int accountLedgerYear = accountingLedger.getDate().getYear(); // stores the accounting ledger year

            if ((accountLedgerYear == year))
            {
                System.out.print(accountingLedger.displayAsString());
            }

        }

        System.out.print("\nPress any key to go to the ledger screen: ");
        scanner.nextLine();

        ledgerScreen();
    }

    private void customSearch() {

        System.out.println("Custom Search");

        // Prompt user for search values and trim whitespace
        System.out.print("Start Date (leave blank to skip): ");
        String start = scanner.nextLine().trim();

        System.out.print("End Date (leave blank to skip): ");
        String end = scanner.nextLine().trim();

        System.out.print("Description (leave blank to skip): ");
        String description = scanner.nextLine().trim();

        System.out.print("Vendor (leave blank to skip): ");
        String vendor = scanner.nextLine().trim();

        System.out.print("Amount (leave blank to skip): ");
        String amount = scanner.nextLine().trim();

        // Convert string start and end date values to LocalDate objects if not empty
        LocalDate startDate = null;
        LocalDate endDate = null;

        if (!start.isEmpty()) {
            startDate = LocalDate.parse(start);
        }
        if (!end.isEmpty()) {
            endDate = LocalDate.parse(end);
        }

        System.out.println("\nCUSTOM REPORT");
        displayTableHeader();

        // Loop through accountingLedgerRecord and filter results based on search values
        for (AccountingLedger accountingLedger : accountingLedgerRecord.values()) {

            // Filter by start date
            if (startDate != null && !startDate.isBefore(accountingLedger.getDate().toLocalDate())) {
                continue; // Skip to next iteration
            }

            // Filter by end date
            if (endDate != null && !endDate.isAfter(accountingLedger.getDate().toLocalDate())) {
                continue; // Skip to next iteration
            }

            // Filter by description
            if (!description.isEmpty() && !accountingLedger.getDescription().equalsIgnoreCase(description)) {
                continue; // Skip to next iteration
            }

            // Filter by vendor
            if (!vendor.isEmpty() && !accountingLedger.getVendor().equalsIgnoreCase(vendor)) {
                continue; // Skip to next iteration
            }

            // Filter by amount
            if (!amount.isEmpty() && Double.parseDouble(amount) != accountingLedger.getAmount()) {
                continue; // Skip to next iteration
            }

            // Print accountingLedger object if it passed all filters
            System.out.print(accountingLedger.displayAsString());
        }

        System.out.print("\nPress any key to go to the ledger screen: ");
        scanner.nextLine();

        ledgerScreen();

    }

}