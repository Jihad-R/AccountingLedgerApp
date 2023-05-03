package org.yearup;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.time.*;
import java.time.format.DateTimeFormatter;
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
        loadAccountingLedgerRecord();
        homeScreen();
    }

    public void loadAccountingLedgerRecord(){
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
        displayScreenHeader("Accounting Ledger App");

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
                    // error message
                    System.out.println(ColorCodes.YELLOW+"Unrecognized input! Please try again."+ColorCodes.RESET);
                    homeScreen();
                }
            }
        }while(!validInput);
    }

    private void recordTransaction(String type, double amount) {
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
                    //warning message
                    System.out.println(ColorCodes.YELLOW+"Description cannot be empty."+ColorCodes.RESET);
                }
            }

            // Get non-blank vendor from user
            while (vendor.isBlank()) {
                System.out.print("Enter the vendor: ");
                vendor = scanner.nextLine().trim();
                if (vendor.isBlank()) {
                    // warning message
                    System.out.println(ColorCodes.YELLOW+"Vendor name cannot be blank."+ColorCodes.YELLOW);
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
            // error message
            System.out.println(ColorCodes.RED+"Please enter a numeric value."+ColorCodes.RESET);
            homeScreen();
        } catch (IOException ex)
        {
            //error message
            System.out.println(ColorCodes.RED+"File Not Found!"+ColorCodes.RESET);
            homeScreen(); //navigate to home screen
        }
        finally
        {
            try {
                fileWriter.flush();
                fileWriter.close();

                homeScreen(); // navigate to home screen
            } catch (Exception e){
                // error message
                System.out.println(ColorCodes.RED+"Something went wrong!"+ColorCodes.RESET);
                homeScreen(); // navigate to home screen
            }
        }
    }

    public void makeTransaction(String transactionType){
            try {
                System.out.printf("Please enter the %s amount: ",transactionType);
                double amount = scanner.nextDouble();

                scanner.nextLine();
                recordTransaction(transactionType, amount);
            }
            catch (InputMismatchException e){
                //error message
                System.out.println(ColorCodes.RED+"\nInvalid amount! Please enter a numeric value."+ColorCodes.RESET);
                scanner.nextLine();
                makeTransaction(transactionType);
            }
    }

    public void ledgerScreen(){
        // display Ledger Screen Header
        displayScreenHeader("Ledger Screen");

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
                // warning message
                System.out.println(ColorCodes.YELLOW+"Unrecognized command! Please try again."+ColorCodes.RESET);
                ledgerScreen();
                break;
        }

    }

    public void displayTableHeader(){

        System.out.println("-------------------------------------------------------------------");
        System.out.printf("%-11s|%-9s|%-20s|%-15s|%-7s|\n",header.split("\\|"));
        System.out.println("-------------------------------------------------------------------");
    }

    private void displayScreenHeader(String screenTitle){
        System.out.println("\n========================");
        System.out.println(screenTitle.toUpperCase());
        System.out.println("========================");
    }

    public void displayEntries(String entryType) {
        HashMap<String,String> titleHeader =  new HashMap<String, String>() {{
            put("A","All Entries");
            put("D","Deposit Entries");
            put("P","Payment Entries");
        }};

        System.out.println();
        System.out.println(titleHeader.get(entryType));
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
        displayScreenHeader("Report Screen");

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

        System.out.println();

        switch (userInput){

            case "1": {
                System.out.println("Month to Date");
                makeReport("monthToDate");
                break;
            }
            case "2": {
                System.out.println("Previous Month");
                makeReport("previousMonth");
                break;
            }
            case "3": {

                System.out.println("Year to Date");
                makeReport("yearToDate");
                break;
            }
            case "4": {
                System.out.println("Previous Year");
                makeReport("previousYear");
                break;
            }
            case "5": {
                System.out.println("Search By Vendor");
                makeReport("vendor");

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
                System.out.println(ColorCodes.YELLOW+"Invalid input! Please try again."+ColorCodes.RESET); // warning message
                reportScreen();
                break;
            }
        }

    }


    private void makeReport(String reportType)
    {
        int previousYear = LocalDateTime.now().minusYears(1).getYear();
        int year = LocalDateTime.now().getYear(); // stores the current year
        Month currentMonth = LocalDateTime.now().getMonth();
        Month previousMonth = LocalDateTime.now().minusMonths(1).getMonth(); // stores the previous month
        String vendorName = null;

        if(reportType.equals("vendor"))
        {
            System.out.print("Enter vendor name: ");
            vendorName = scanner.nextLine();
            displayTableHeader();
        }

        else
        {
            displayTableHeader();
        }

        for (AccountingLedger accountingLedger: accountingLedgerRecord.values())
        {

            int accountLedgerYear = accountingLedger.getDate().getYear(); // stores the accounting ledger year
            Month accountingLedgerMonth = accountingLedger.getDate().getMonth();



            if (reportType.equals("vendor") && !(accountingLedger.getVendor().equalsIgnoreCase(vendorName))) {
                    continue;
                }

            if (reportType.equals("previousYear")&& !(accountLedgerYear == previousYear))
            {
                continue;
            }

            if (reportType.equals("monthToDate") && !(accountingLedgerMonth == currentMonth))
            {
                continue;
            }

            if ((reportType.equals("previousMonth")) &&
                    !(accountingLedgerMonth.equals(previousMonth) && accountLedgerYear == year))
            {
               continue;
            }

            if (reportType.equals("yearToDate") && !(accountLedgerYear == year))
            {
                continue;
            }
                System.out.print(accountingLedger.displayAsString());

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

        String amount = "";
        try {
            System.out.print("Amount (leave blank to skip): ");
            amount = scanner.nextLine().trim();
            Double.parseDouble(amount);
        }
        catch (NumberFormatException e){
            //error message
            System.out.println(ColorCodes.RED+"Invalid amount! Please try again."+ColorCodes.RESET);
            customSearch();
        }
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