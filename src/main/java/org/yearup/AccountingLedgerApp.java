package org.yearup;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
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

    public void run(){
        loadaccountingLedgerRecord();
        homeScreen();
    }

    public void loadaccountingLedgerRecord(){
        try {
            FileInputStream fileInputStream = new FileInputStream("transactions.csv");
            scanner = new Scanner(fileInputStream);
            int id = 0;
            header = scanner.nextLine();
            while (scanner.hasNextLine()){

                id = id + 1; // increment the value of the id
                String line = scanner.nextLine(); // store the contents of the line
                String[] transaction = line.split("\\|"); // split the line on the '|'

                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"); // data time formater
                LocalDateTime dateTime = LocalDateTime.parse(transaction[0] +""+transaction[1],formatter);

                String description = transaction[2];
                String vendor = transaction[3];
                Double price = Double.parseDouble(transaction[4]);


                accountingLedgerRecord.put(id, new AccountingLedger(id,dateTime,description,vendor,price));
            }

        } catch (FileNotFoundException e) {
            System.out.println("File Not Found");
        }
        finally {
            scanner.close();
        }
    }
    public void homeScreen(){
    /*
    * Displays the homeScreen UI
    * */
        scanner = new Scanner(System.in); // Get input stream from user keyboard
        System.out.println("========================");
        System.out.println("ACCOUNTING LEDGER APP");
        System.out.println("========================");
        System.out.println("'D' - Add Deposit");
        System.out.println("'P' - Make Payment (Debit)");
        System.out.println("'L' - Ledger");
        System.out.println("'X' - Exit");

        do {
            System.out.print("Please select a command: ");
            String userInput = scanner.nextLine(); // get user Input

            switch (userInput){
                case "d" : {}
                case "D":{
                    validInput = true;
                    addDeposit();
                    break;
                }
                case "p":
                case "P":{
                    validInput = true;
                    makePayment();
                    break;
                }
                case "l":
                case "L": {
                    validInput = true;
                    displayAllEntries(); // navigate to ledger screen
                    break;
                }
                case "X": {System.exit(1);break; }// exit the program
                default : {
                    System.out.println("Unrecognized input! Please try again."); // error message
                }
            }


        }while(!validInput);
    }

    public void addDeposit(){

        System.out.print("Please Enter the deposit amount: ");
        try {
            double deposit = scanner.nextDouble();
            scanner.nextLine();

             fileWriter= new FileWriter("transactions.csv",true);

            System.out.print("Please enter the description: ");
            String description = scanner.nextLine().trim(); // store the description entered by the user

            if(description.equalsIgnoreCase("")){
                System.out.println("Description cannot be empty."); // warning message
                addDeposit();
            }

             fileWriter.write(LocalDate.now() +"| "
                     + LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"))
                     +"|"+description+"|Joe"+"|"+deposit+"\n"); // write to file

            System.out.println("Deposit made"); // success message

        }
        catch (InputMismatchException e){
            System.out.println("Please enter a numeric value."); // error message
            addDeposit(); // recursive call
        }
        catch (IOException ex){
            System.out.println("File Not Found!"); //error message
            addDeposit(); // recursive call
        }
        finally {
            try {
                fileWriter.flush();
                fileWriter.close();

                homeScreen(); // navigate to home screen
            }
            catch (Exception e){
                System.out.println("Something went wrong!"); // error message
                addDeposit();// recursive call
            }
        }
        }

    public void makePayment(){

        System.out.print("Please enter the payment amount: ");
        try {
            double deposit = scanner.nextDouble();
            scanner.nextLine();
            deposit = -deposit;

            fileWriter= new FileWriter("transactions.csv",true);

            System.out.print("Please enter the description: ");
            String description = scanner.nextLine().trim(); // store the description entered by the user

            if(description.equalsIgnoreCase("")){
                System.out.println("Description cannot be blank."); // warning message
                makePayment();
            }

            System.out.print("Please enter the vendor: ");
            String vendor = scanner.nextLine().trim(); // store the vendor name entered by the user

            if (vendor.equalsIgnoreCase("")){
                System.out.println("Vendor name cannot be blank."); // warning message
                makePayment();
            }

            fileWriter.write(LocalDate.now() +"| "
                    + LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"))
                    +"|"+description+"|"+vendor+"|"+deposit+"\n"); // write to file

            System.out.println("Payment made"); // success message
        }

        catch (InputMismatchException e){
            System.out.println("Please enter a numeric value."); // error message
            makePayment(); // recursive call
        }

        catch (IOException ex){
            System.out.println("File Not Found!"); //error message
            makePayment(); // recursive call
        }

        finally {
            try {
                fileWriter.flush();
                fileWriter.close();

                homeScreen(); // navigate to home screen
            }
            
            catch (Exception e){
                System.out.println("Something went wrong!"); // error message
                makePayment();// recursive call
            }
        }


        }

    public void displayAllEntries() {

        String[] headerContent = header.split("\\|");
        System.out.printf("%-11s|%-9s|%-20s|%-10s|%-7s|\n",headerContent[0],headerContent[1],headerContent[2],headerContent[3],headerContent[4]);
        System.out.println("------------------------------------------------------------");

        for (AccountingLedger accountingLedger: accountingLedgerRecord.values()){

            String date = String.valueOf(accountingLedger.getDate().toLocalDate());
            String time = String.valueOf(accountingLedger.getDate().toLocalTime());
            String description = accountingLedger.getDescription();
            String vendor = accountingLedger.getVendor();
            String amount = String.valueOf(accountingLedger.getAmount());

            System.out.printf("%-11s|%9s|%-20s|%-10s|%7s|\n",date,time,description,vendor,amount);
            System.out.println("------------------------------------------------------------");

        }
    }

}
