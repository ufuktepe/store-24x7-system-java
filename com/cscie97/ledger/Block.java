package com.cscie97.ledger;

import com.cscie97.Utility;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Represents a block in the Ledger System.
 * Includes 10 transactions and has a map of account addresses to account objects that reflects the balance of all
 * accounts after all the transactions within the block have been applied.
 * Each block contains the hash of itself and the hash of the previous block.
 * The SHA-256 algorithm and Merkle tree are used to compute hash strings.
 *
 * @author Burak Ufuktepe
 */
public class Block {

    /**
     * Class constant for max number of transactions.
     */
    public static final Integer MAX_NUM_OF_TXNS = 10;

    /**
     * Class variable that keeps track of all transactions which maps transaction Ids to transaction objects.
     */
    private static Map<String, Transaction> transactionMap = new HashMap<>();

    /**
     * Unique identifier of the block.
     */
    private int blockNumber;

    /**
     * Hash of the previous block.
     */
    private String previousHash;

    /**
     * Hash of the current block.
     */
    private String hash;

    /**
     * Ordered list of transactions.
     */
    private List<Transaction> transactionList;

    /**
     * Maps account addresses to account objects.
     */
    private Map<String, Account> accountBalanceMap;

    /**
     * Preceding block.
     */
    private Block previousBlock;

    /**
     * Class constructor that sets the blockNumber, previousHash, previousBlock, and accountBalanceMap.
     * Initializes the transactionList.
     *
     * @param aBlockNumber  Integer that identifies the block
     * @param aPreviousHash  Previous block's hash
     * @param aPreviousBlock  The preceding block
     * @param anAccountBalanceMap  Account address and account object pairs
     */
    public Block(int aBlockNumber, String aPreviousHash, Block aPreviousBlock,
                 Map<String, Account> anAccountBalanceMap) {
        this.blockNumber = aBlockNumber;
        this.previousHash = aPreviousHash;
        this.previousBlock = aPreviousBlock;
        this.transactionList = new ArrayList<>();
        this.accountBalanceMap = anAccountBalanceMap;
    }

    /**
     * Copy constructor for deep copying Block objects.
     * Sets previousBlock to null to avoid deep copying all preceding blocks.
     *
     * @param aBlock  Block object to be deep copied
     */
    public Block(Block aBlock) {
        this.blockNumber = aBlock.getBlockNumber();
        this.previousHash = aBlock.getPreviousHash();
        this.previousBlock = null;
        this.transactionList = aBlock.getCopyOfTransactionList();
        this.accountBalanceMap = aBlock.getCopyOfAccountBalanceMap();
        this.hash = aBlock.hash;
    }

    /**
     * Retrieves the hash of the current block.
     *
     * @return  Current block's hash
     */
    public String getHash() {
        return this.hash;
    }

    /**
     * Retrieves the hash of the preceding block.
     *
     * @return  Preceding block's hash
     */
    public String getPreviousHash() {
        return this.previousHash;
    }

    /**
     * Retrieves the preceding block.
     *
     * @return  Preceding block
     */
    public Block getPreviousBlock() {
        return this.previousBlock;
    }

    /**
     * Retrieves the block number.
     *
     * @return  Unique identifier of the block
     */
    public int getBlockNumber() {
        return this.blockNumber;
    }

    /**
     * Retrieves the accountBalanceMap which reflects the balance of all accounts after all the transactions within
     * the last committed block have been applied.
     *
     * @return  accountBalanceMap that maps account addresses to account objects
     */
    public Map<String, Account> getAccountBalanceMap() {
        return this.accountBalanceMap;
    }

    /**
     * Deep copies the accountBalanceMap to ensure immutability of accountBalanceMap.
     *
     * @return  a copy of the accountBalanceMap
     */
    public Map<String, Account> getCopyOfAccountBalanceMap() {
        Map<String, Account> copyOfAccountBalanceMap = new HashMap<>();

        // Iterate over current block's accountBalanceMap and populate copyOfAccountBalanceMap by deep copying accounts
        for (Map.Entry<String, Account> entry : this.accountBalanceMap.entrySet()) {
            copyOfAccountBalanceMap.put(entry.getKey(), new Account(entry.getValue()));
        }

        return copyOfAccountBalanceMap;
    }

    /**
     * Retrieves the list of transaction objects in the block.
     *
     * @return transactionList of the current block
     */
    public List<Transaction> getTransactionList() {
        return this.transactionList;
    }

    /**
     * Deep copies the transactionList to ensure immutability of transactionList.
     *
     * @return  a copy of the transactionList
     */
    public List<Transaction> getCopyOfTransactionList() {
        List<Transaction> copyOfTransactionList = new ArrayList<>();

        // Iterate over current block's transactionList and populate copyOfTransactionList by deep copying transactions
        for (Transaction txn : this.transactionList) {
            copyOfTransactionList.add(new Transaction(txn));
        }
        return copyOfTransactionList;
    }

    /**
     * Retrieves the class variable transactionMap which keeps track of all transactions.
     *
     * @return  transactionMap that maps transaction IDs to transaction objects
     */
    public static Map<String, Transaction> getTransactionMap() {
        return transactionMap;
    }

    /**
     * Adds a new account to accountBalanceMap.
     *
     * @param anAccount  Account to be added to accountBalanceMap
     */
    public void addAccountBalanceMap(Account anAccount) {
        this.accountBalanceMap.put(anAccount.getAddress(), anAccount);
    }

    /**
     * Adds a new transaction to transactionList. Before adding the transaction, checks whether a hash has already
     * been calculated for the block.
     *
     * @param txn  Transaction to be added to transactionList
     * @throws LedgerException if the block has already been committed to the ledger
     */
    public void addTransaction(Transaction txn) throws LedgerException {
        this.validateBlock();

        // Check if transactionId already exists in transactionMap
        if (transactionMap.containsKey(txn.getTransactionId())) {
            throw new LedgerException("Transaction " + txn.getTransactionId() + " cannot be processed.",
                    "Transaction Id was already used.");
        }

        // Check if transactionId already exists in transactionList
        for (Transaction transaction : this.transactionList) {
            if (transaction.getTransactionId().equals(txn.getTransactionId())) {
                throw new LedgerException("Transaction " + txn.getTransactionId() + " cannot be processed.",
                        "Transaction Id was already used.");
            }
        }

        this.transactionList.add(txn);
    }

    /**
     * Sets the accountBalanceMap, updates the transactionMap, and sets the hash. Takes a map of account address and
     * account object pairs as an input which must be validated by the caller.
     *
     * @param anAccountBalanceMap  a validated accountBalanceMap that maps account addresses to updated account objects
     * @throws LedgerException  if the block has already been committed to the ledger
     */
    public void commitBlock(Map<String, Account> anAccountBalanceMap) throws LedgerException  {
        // Check if a hash has already been calculated for the block.
        this.validateBlock();

        // Set the accountBalanceMap
        this.accountBalanceMap = anAccountBalanceMap;

        // Add transactions from transactionList to transactionMap
        for (Transaction txn : this.transactionList) {
            transactionMap.put(txn.getTransactionId(), txn);
        }

        // Set the hash
        this.hash = this.computeHash();
        System.out.println("Block " + this.blockNumber + " committed successfully.");
    }

    /**
     * Computes the hash for the block using the transactionList, previous block's hash, and the block number.
     *
     * @return  hash string for the block
     */
    public String computeHash() {
        // Create an array of string representations of transactions
        ArrayList<String> transactions = new ArrayList<>();
        for (Transaction txn : this.transactionList) {
            transactions.add(txn.toString());
        }

        // Compute the Merkle Root of the transactions
        String merkleRoot = MerkleTree.getRootHash(transactions);

        // Compute the hash of the current block's number
        String blockNumHash = Utility.hashString(String.valueOf(this.blockNumber));

        // Return the hash of the concatenated string
        return Utility.hashString(blockNumHash + this.previousHash + merkleRoot);
    }

    /**
     * Checks whether the block has already been committed to the ledger.
     *
     * @throws LedgerException  if the block already has a hash value
     */
    private void validateBlock() throws LedgerException {
        if (this.hash != null) {
            throw new LedgerException(null, "Block has already been committed to the ledger.");
        }
    }

    /**
     * Returns a string that includes the block's number, hash, previous block's hash, transaction details, account
     * addresses and their balances.
     *
     * @return  String representation of the block
     */
    public String toString() {
        // Add the block number, hash, and the previous block's hash to the output string
        StringBuilder output = new StringBuilder(
                String.format("%-22s: %s%n%-22s: %s%n%-22s: %s%n%nTRANSACTIONS%n",
                "Block", this.blockNumber, "Hash", this.hash, "Previous Block's Hash", this.previousHash));

        // Add transactions to the output string
        for (Transaction txn : this.transactionList) {
            output.append(txn).append("\n");
        }

        // Add account addresses and their balances to the output string
        output.append(String.format("%n%-15s%-15s%n", "ACCOUNT", "BALANCE"));
        for (Map.Entry<String, Account> entry : this.accountBalanceMap.entrySet()) {
            output.append(String.format("%-15s%-15s%n", entry.getKey(), entry.getValue().getBalance()));
        }

        return output.toString();
    }
}
