package org.yearup;

import java.util.Scanner;

public class AccountingLedgerApp {

    private Scanner scanner;
    private boolean validInput = false;
    public void run(){
        homeScreen();
    }

    public void homeScreen(){
    /*
    * Displays the homeScreen UI
    * */
        scanner = new Scanner(System.in); // Get input stream from user keyboard
        System.out.println("========================");
        System.out.println("ACCOUNTING LEDGER APP");
        System.out.println("========================");

        do {


        }while(!validInput);
    }

}
