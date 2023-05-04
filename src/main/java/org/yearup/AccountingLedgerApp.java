package org.yearup;

import java.io.*;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.InputMismatchException;
import java.util.Scanner;

public class AccountingLedgerApp {

    // Create a private HashMap to store AccountingLedger objects
    private HashMap<Integer, AccountingLedger> accountingLedgerRecord = new HashMap<>();

    // Create a private Scanner object
    private Scanner scanner;

    // Create a FileWriter object and initialize it to null
    private FileWriter fileWriter = null;

    // Create a boolean variable to check for valid input
    private boolean validInput = false;

    // Create String variables to store header and user input
    private String header;
    private String userInput;

    // Create an integer variable to store a unique identifier for each object in the HashMap
    private int id = 0;


    public void run() {
        loadAccountingLedgerRecord(); // load the accounting ledger hashmap with data in 'transactions.csv'
        homeScreen(); // display home screen
    }

    public void loadAccountingLedgerRecord() {
        try {
            FileInputStream fileInputStream = new FileInputStream("transactions.csv");
            scanner = new Scanner(fileInputStream);
            header = scanner.nextLine(); // read and store the header line
            while (scanner.hasNextLine()) {

                id = id + 1; // increment the value of the id
                String line = scanner.nextLine(); // store the contents of the line
                String[] transaction = line.split("\\|"); // split the line on the '|'

                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"); // data time formatter
                // parse the date and time from the first two columns of the line
                LocalDateTime dateTime = LocalDateTime.parse(transaction[0] + " " + transaction[1], formatter);

                String description = transaction[2];// get the description from the third column of the line
                String vendor = transaction[3];// get the vendor from the fourth column of the line
                Double amount = Double.parseDouble(transaction[4]);// get the amount from the fifth column of the line

                // add a new AccountingLedger object to the map and id as key
                accountingLedgerRecord.put(id, new AccountingLedger(id, dateTime, description, vendor, amount));
            }

        } catch (FileNotFoundException e) // handle any FileNotFoundException that might occur
        {
            System.out.println("File Not Found");
        } finally {
            scanner.close();
        }
    }

    private String tableHeader() {

        return String.format("---------------------------------" +
                "----------------------------------\n"
                + "|%-11s|%-9s|%-20s|%-15s|%-7s|\n" +
                "---------------------------------------" +
                "----------------------------\n",header.split("\\|"));
    }

    private void displayScreenHeader(String screenTitle) {

        System.out.println("\n========================");
        System.out.println(screenTitle.toUpperCase());
        System.out.println("========================");
    }

    private void homeScreen() {

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
            userInput = scanner.nextLine().toUpperCase().trim(); // get user Input

            switch (userInput) {
                case "D": {
                    validInput = true;
                    makeTransaction("deposit");
                    break;
                }
                case "P": {
                    validInput = true;
                    makeTransaction("payment");
                    break;
                }
                case "L": {
                    validInput = true;
                    ledgerScreen(); // navigate to ledger screen
                    break;
                }
                case "X": {
                    System.exit(1);// exit the program
                    break;
                }
                default: {
                    // error message
                    System.out.println(ColorCodes.YELLOW + "Unrecognized input! Please try again." + ColorCodes.RESET);
                    homeScreen();
                }
            }
        } while (!validInput);
    }

    private void ledgerScreen() {
        // display Ledger Screen Header
        displayScreenHeader("Ledger Screen");

        // display options for the ledger screen
        System.out.println("'A' - Display all entries");
        System.out.println("'D' - Display deposits");
        System.out.println("'P' - Display payments");
        System.out.println("'R' - Report");
        System.out.println("'0' - Home screen");
        System.out.print("Select a command: ");

        userInput = scanner.nextLine().toUpperCase(); // store user input

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
                homeScreen(); // go back to home screen
                break;
            default:
                // warning message
                System.out.println(ColorCodes.YELLOW + "Unrecognized command! Please try again." + ColorCodes.RESET);
                ledgerScreen();
                break;
        }

    }

    private void reportScreen() {

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
        userInput = scanner.nextLine();

        System.out.println();

        //switch cases
        switch (userInput) {

            case "1": {
                // generate month-to-date report
                System.out.println("Month to Date Report");
                makeReport("monthToDate");
                break;
            }
            case "2": {
                // generate previous month report
                System.out.println("Previous Month Report");
                makeReport("previousMonth");
                break;
            }
            case "3": {
                // generate year-to-date report
                System.out.println("Year to Date Report");
                makeReport("yearToDate");
                break;
            }
            case "4": {
                // generate previous year report
                System.out.println("Previous Year Report");
                makeReport("previousYear");
                break;
            }
            case "5": {
                // generate vendor report
                System.out.println("Search By Vendor Report");
                makeReport("vendor");

                break;
            }
            case "6": {
                // navigate to home screen
                homeScreen();
                break;
            }
            case "7": {
                // perform custom search
                customSearch();
                break;
            }
            default: {
                // display warning message for invalid input and prompt for input again
                System.out.println(ColorCodes.YELLOW + "Invalid input! Please try again." + ColorCodes.RESET);
                reportScreen();
                break;
            }
        }

    }
    private void makeTransaction(String transactionType) {
        System.out.println(("\nMake " + transactionType + ": ").toUpperCase());
        try {
            // Prompt the user to enter the amount for the specified transaction type
            System.out.printf("Please enter the %s amount: ", transactionType);
            double amount = scanner.nextDouble();

            // Consume the newline character left in the input buffer
            scanner.nextLine();

            // Call the recordTransaction method, passing in the transaction type and amount as parameters
            recordTransaction(transactionType, amount);
        } catch (InputMismatchException e) {
            // If the user inputs an invalid amount (i.e. not a double), catch the InputMismatchException
            // Display an error message in red text
            System.out.println(ColorCodes.RED + "\nInvalid amount! Please enter a numeric value." + ColorCodes.RESET);

            // Consume the newline character left in the input buffer
            scanner.nextLine();

            // Call the makeTransaction method again, passing in the transaction type as a parameter
            makeTransaction(transactionType);
        }
    }

    private void recordTransaction(String type, double amount) {
        try {
            id++;
            fileWriter = new FileWriter("transactions.csv", true);

            String description = "";
            String vendor = "";

            // Get non-blank description from user
            while (description.isBlank()) {
                System.out.print("Enter the description: ");
                description = scanner.nextLine().trim();
                if (description.isBlank()) {
                    //warning message
                    System.out.println(ColorCodes.YELLOW + "Description cannot be empty." + ColorCodes.RESET);
                }
            }

            // Get non-blank vendor from user
            while (vendor.isBlank()) {
                System.out.print("Enter the vendor: ");
                vendor = scanner.nextLine().trim();
                if (vendor.isBlank()) {
                    // warning message
                    System.out.println(ColorCodes.YELLOW + "Vendor name cannot be blank." + ColorCodes.YELLOW);
                }
            }

            if (type.equals("payment") && amount > 0) {
                amount = -amount; // negate the value of payment amount
            }

            fileWriter.write(LocalDate.now() + "|"
                    + LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"))
                    + "|" + description + "|" + vendor + "|" + amount + "\n"); // write to file

            accountingLedgerRecord.put(id, new AccountingLedger(id, LocalDateTime.now(), description, vendor, amount));

            // success message in blue
            System.out.println(type.equals("deposit") ? ColorCodes.PURPLE + "Deposit made" + ColorCodes.RESET :
                    ColorCodes.PURPLE + "Payment made" + ColorCodes.RESET);


        } catch (InputMismatchException e) {
            // error message in red
            System.out.println(ColorCodes.RED + "Please enter a numeric value." + ColorCodes.RESET);
            homeScreen();
        } catch (IOException ex) {
            //error message in red
            System.out.println(ColorCodes.RED + "File Not Found!" + ColorCodes.RESET);
            homeScreen(); //navigate to home screen
        } finally {
            try {
                fileWriter.flush();
                fileWriter.close();

                homeScreen(); // navigate to home screen
            } catch (Exception e) {
                // error message in red
                System.out.println(ColorCodes.RED + "Something went wrong!" + ColorCodes.RESET);
                homeScreen(); // navigate to home screen
            }
        }
    }


    private void displayEntries(String entryType) {
        // create a HashMap to store the titles of the different entry types
        HashMap<String, String> titleHeader = new HashMap<String, String>() {{
            put("A", "All Entries");
            put("D", "Deposit Entries");
            put("P", "Payment Entries");
        }};
        String entryTable = "";

        // print a blank line and the title of the entry type
        System.out.println();
        System.out.println(titleHeader.get(entryType));

        // Use a StringBuilder to accumulate the entries to display
        StringBuilder entries = new StringBuilder();

        // Loop through all entries in the accounting ledger
        for (AccountingLedger accountingLedger : accountingLedgerRecord.values()) {

            // Only include entries that match the requested entry type
            if ((entryType.equals("P") && accountingLedger.getAmount() < 0)
                    || (entryType.equals("D") && accountingLedger.getAmount() >= 0)
                    || (entryType.equals("A"))) {

                entries.append(accountingLedger.displayAsString());
            }
        }

        // If there are entries to display, concatenate the table header and entries into a single string
        if (entries.length() > 0)
            entryTable = tableHeader() + entries.toString();

        // Print the table header and entries
        System.out.println(entryTable);

        // Prompt the user to press any key to return to the ledger screen
        System.out.print("\nPress any key to go to the ledger screen: ");
        scanner.nextLine();

        ledgerScreen();// Return to the ledger screen
    }


    private void makeReport(String reportType) {
        // Get the current year and months, and the previous year and month.
        int year = LocalDateTime.now().getYear();
        Month currentMonth = LocalDateTime.now().getMonth();
        Month previousMonth = LocalDateTime.now().minusMonths(1).getMonth();
        int previousYear = LocalDateTime.now().minusYears(1).getYear();

        // Initialize the vendor name to null.
        String vendorName = null;

        // Create a StringBuilder to store the report.
        StringBuilder reportBuilder = new StringBuilder();

        // If the report type is "vendor", prompt the user to enter the vendor name.
        if (reportType.equals("vendor")) {
            System.out.print("Enter vendor name: ");
            vendorName = scanner.nextLine();
        }

        // Loop through each AccountingLedger in the accountingLedgerRecord HashMap.
        for (AccountingLedger accountingLedger : accountingLedgerRecord.values()) {

            // Get the year and month of the AccountingLedger.
            int accountLedgerYear = accountingLedger.getDate().getYear();
            Month accountingLedgerMonth = accountingLedger.getDate().getMonth();

            // Check if the AccountingLedger should be skipped based on the report type.
            if (reportType.equals("vendor") && !(accountingLedger.getVendor().equalsIgnoreCase(vendorName))) {
                continue;
            }

            if (reportType.equals("previousYear") && !(accountLedgerYear == previousYear)) {
                continue;
            }

            if (reportType.equals("monthToDate") && !(accountingLedgerMonth == currentMonth)) {
                continue;
            }

            if ((reportType.equals("previousMonth")) &&
                    !(accountingLedgerMonth.equals(previousMonth) && accountLedgerYear == year)) {
                continue;
            }

            if (reportType.equals("yearToDate") && !(accountLedgerYear == year)) {
                continue;
            }

            // If the AccountingLedger is not skipped, add its string representation to the report.
            reportBuilder.append(accountingLedger.displayAsString());

        }

        if (!reportBuilder.toString().isBlank())
            reportBuilder.insert(0, tableHeader());// Append table header to the report builder.

        String report = reportBuilder.toString();
        // Display the report or display 'Report Unavailable'.
        System.out.println(!report.isBlank() ? report : ColorCodes.YELLOW+"Report Unavailable!"+ColorCodes.RESET);

        // Prompt the user to press any key to return to the ledger screen.
        System.out.print("\nPress any key to go to the ledger screen: ");
        scanner.nextLine();

        ledgerScreen(); // return to the ledger screen.

    }

    private void customSearch() {

        // Create a StringBuilder to store the report.
        StringBuilder customReportBuilder = new StringBuilder();

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
        } catch (NumberFormatException e) {
            //error message
            System.out.println(ColorCodes.RED + "Invalid amount! Please try again." + ColorCodes.RESET);
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
        tableHeader();

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

            customReportBuilder.append(accountingLedger.displayAsString());
        }

        if(!(customReportBuilder.toString().isBlank())) // condition check if the is not blank
            customReportBuilder.insert(0, tableHeader()); // insert table header at index 0

        String customReport = customReportBuilder.toString(); // store custom report

        // Display the custom report or display 'Report Unavailable'.
        System.out.println(!customReport.isBlank() ? customReport :
                ColorCodes.YELLOW+"Report Unavailable!"+ColorCodes.RESET);

        System.out.print("\nPress any key to go to the ledger screen: ");
        scanner.nextLine();

        ledgerScreen(); // Return to ledger screen.

    }
}