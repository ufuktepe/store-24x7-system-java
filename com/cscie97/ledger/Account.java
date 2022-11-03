package com.cscie97.ledger;

/**
 * Represents an individual account within the Ledger Service.
 * Each account is assigned a unique address and has a balance that can be credited/debited.
 *
 * @author Burak Ufuktepe
 */
public class Account {

    /**
     * Class constant to identify the master account.
     */
    public static final String MASTER = "master";

    /**
     * Unique identifier of the account.
     */
    private String address;

    /**
     * Account balance that reflects total transfers.
     */
    private int balance;

    /**
     * Class constructor that sets the address of the account. If this is the master account, it funds the account with
     * all available funds. Otherwise, sets the balance to 0.
     *
     * @param anAddress Account's unique identifier
     */
    public Account(String anAddress) {
        this.address = anAddress;
        if (anAddress.equals(MASTER)) {
            this.balance = Integer.MAX_VALUE;
        } else {
            this.balance = 0;
        }
    }

    /**
     * Copy constructor for deep copying Account objects.
     *
     * @param account Account object to be deep copied
     */
    public Account(Account account) {
        this.address = account.getAddress();
        this.balance = account.getBalance();
    }

    /**
     * Retrieves the balance of the Account.
     *
     * @return Account balance
     */
    public int getBalance() {
        return this.balance;
    }

    /**
     * Retrieves the unique address of the account.
     *
     * @return Account address
     */
    public String getAddress() {
        return this.address;
    }

    /**
     * deposits funds into this account.
     *
     * @param amount The value of funds to be deposited
     */
    public void deposit(int amount) {
        this.balance += amount;
    }

    /**
     * Withdraws funds from this account.
     *
     * @param amount The value of funds to be withdrawn
     */
    public void withdraw(int amount) {
        this.balance -= amount;
    }
}
