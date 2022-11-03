package com.cscie97.ledger;

/**
 * Represents a transaction in the Ledger System.
 * Each transaction has a transaction ID, an amount, a fee, a note, a payer, and a receiver.
 * The transaction amount is transferred from the payer’s account balance to the receiver’s account balance.
 * The transaction fee is transferred from the payer's account balance to the master account.
 *
 * @author Burak Ufuktepe
 */
public class Transaction {

    /**
     * Class constant for minimum fee amount.
     */
    public static final Integer MIN_FEE = 10;

    /**
     * Class constant for maximum note length.
     */
    public static final Integer MAX_NOTE_LEN = 1024;

    /**
     * Class variable for determining the next transaction ID.
     */
    private static int nextTransactionId = 1;

    /**
     * Unique identifier for the transaction.
     */
    private String transactionId;

    /**
     * Note for the transaction.
     */
    private String note;

    /**
     * Non-negative value for the amount of the transaction.
     */
    private int amount;

    /**
     * Value for the fee of the transaction.
     */
    private int fee;

    /**
     * Payer address.
     */
    private String payer;

    /**
     * Receiver address.
     */
    private String receiver;

    /**
     * Class constructor.
     * Sets the transaction ID, amount, fee, note, payer and receiver addresses for the transaction.
     *
     * @param anAmount  Amount deducted from the payer's account & added to the receiver's account
     * @param aFee  Transaction fee transferred from the payer's account to the master account
     * @param aNote  Note for the transaction
     * @param aPayer  Payer's unique address
     * @param aReceiver  Receiver's unique address
     */
    public Transaction(int anAmount, int aFee, String aNote, String aPayer, String aReceiver) {
        this.transactionId = getNextTransactionId();
        this.amount = anAmount;
        this.fee = aFee;
        this.note = aNote;
        this.payer = aPayer;
        this.receiver = aReceiver;
    }

    /**
     * Copy constructor for deep copying Transaction objects.
     *
     * @param aTransaction  Transaction object to be deep copied
     */
    public Transaction(Transaction aTransaction) {
        this.transactionId = aTransaction.getTransactionId();
        this.amount = aTransaction.getAmount();
        this.fee = aTransaction.getFee();
        this.note = aTransaction.getNote();
        this.payer = aTransaction.getPayer();
        this.receiver = aTransaction.getReceiver();
    }

    /**
     * Retrieves the unique ID of the transaction.
     *
     * @return  Transaction's identifier
     */
    public String getTransactionId() {
        return this.transactionId;
    }

    /**
     * Retrieves the amount to be transferred from the payer to the receiver.
     *
     * @return  Transaction amount
     */
    public int getAmount() {
        return this.amount;
    }

    /**
     * Retrieves the fee to be transferred from the payer to the master account.
     *
     * @return  Transaction fee
     */
    public int getFee() {
        return this.fee;
    }

    /**
     * Retrieves the note for the transaction.
     *
     * @return  Transaction's note
     */
    public String getNote() {
        return this.note;
    }

    /**
     * Retrieves the unique address of the payer.
     *
     * @return  Payer's address
     */
    public String getPayer() {
        return this.payer;
    }

    /**
     * Retrieves the unique address of the receiver.
     *
     * @return  Receiver's address
     */
    public String getReceiver() {
        return this.receiver;
    }

    /**
     * Returns a transaction ID for the next transaction.
     *
     * @return  Next transaction ID
     */
    public static String getNextTransactionId() {
        String nextId = String.valueOf(nextTransactionId);
        nextTransactionId += 1;
        return nextId;
    }

    /**
     * Returns the string representation of the Transaction object that includes the transaction
     * ID, payer address, receiver address, amount, fee, and note of the transaction.
     *
     * @return  String representation of the Transaction object
     */
    public String toString() {
        return String.format("Transaction ID: %-5s Payer: %-15s Receiver: %-15s Amount: %-10s Fee: %-10s Note: "
                + "%-1024s", this.transactionId, this.payer, this.receiver, this.amount, this.fee, this.note);
    }
}
