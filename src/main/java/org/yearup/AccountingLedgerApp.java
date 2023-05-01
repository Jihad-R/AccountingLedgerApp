package org.yearup;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.sql.Time;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Scanner;

public class AccountingLedgerApp {

    private HashMap<Integer,AccountingLedger> accountingLedgerRecord = new HashMap<>();
    private Scanner scanner;
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
                    System.out.println("Add Deposit");
                    break;
                }
                case "p":
                case "P":{
                    validInput = true;
                    System.out.println("Make Payment");
                    break;
                }
                case "l":
                case "L": {
                    validInput = true;
                    ledgerScreen(); // navigate to ledger screen
                    break;
                }
                case "X": {System.exit(1);break; }// exit the program
                default : {
                    System.out.println("Unrecognized input! Please try again."); // error message
                }
            }


        }while(!validInput);
    }

    public void ledgerScreen() {

        String[] headerContent = header.split("\\|");
        System.out.printf("%-11s|%-9s|%-20s|%-10s|%-7s|\n",headerContent[0],headerContent[1],headerContent[2],headerContent[3],headerContent[4]);
        System.out.println("------------------------------------------------------------");
        for (AccountingLedger accountingLedger: accountingLedgerRecord.values()){
            String date = String.valueOf(accountingLedger.getDate()).split("T")[0];
            String time = (String.valueOf(Time.valueOf(accountingLedger.getDate())).split("T")[1]);
            String description = accountingLedger.getDescription();
            String vendor = accountingLedger.getVendor();
            String amount = String.valueOf(accountingLedger.getAmount());

            System.out.printf("%-11s|%9s|%-20s|%-10s|%7s|\n",date,time,description,vendor,amount);
            System.out.println("------------------------------------------------------------");

        }
    }

}
