package com.cscie97.ledger;

import com.cscie97.Utility;

import java.util.*;

/**
 * The Ledger manages the Blocks of the blockchain and provides the API used by clients of the Ledger.
 * The Ledger supports the following main actions:
 * Create accounts
 * Process transactions
 * Process queries
 * Validate the current state of the blockchain
 *
 * @author Burak Ufuktepe
 */
public class Ledger {

    /**
     * Block number for the genesisBlock.
     */
    private static final Integer INIT_BLOCK_NUM = 1;

    /**
     * Name of the ledger.
     */
    private String name;

    /**
     * Ledger description.
     */
    private String description;

    /**
     * Placeholder for the previous block in genesisBlock.
     */
    private String seed;

    /**
     * Current block that is not committed to the Ledger yet.
     */
    private Block currentBlock;

    /**
     * The initial block of the blockchain.
     */
    private Block genesisBlock;

    /**
     * A map of block numbers and the associated blocks.
     */
    private Map<Integer, Block> blockMap;

    /**
     * Class constructor. Sets the name, description, and seed of the ledger.
     * Creates the genesisBlock, sets the currentBlock, initializes the blockMap, and creates the master account.
     *
     * @param aName  Name of the ledger
     * @param aDescription  Ledger description
     * @param aSeed  Placeholder for the previous block in genesisBlock
     * @throws LedgerException  if a master account already exists
     */
    public Ledger(String aName, String aDescription, String aSeed) throws LedgerException {
        this.name = aName;
        this.description = aDescription;
        this.seed = aSeed;

        // Create the genesisBlock
        this.genesisBlock = new Block(INIT_BLOCK_NUM, Utility.hashString(this.seed), null,
                new HashMap<>());

        // Set the currentBlock to genesisBlock
        this.currentBlock = this.genesisBlock;

        // Initialize the blockMap
        this.blockMap = new HashMap<>();

        // Create the master account
        this.createAccount(Account.MASTER);

        System.out.println("Ledger created successfully.");
    }

    /**
     * Creates a new account. Takes an address as input and checks whether the address already exists.
     * Adds the new account to current block's accountBalanceMap
     *
     * @param anAddress unique address of the account
     * @return unique address of the account
     * @throws LedgerException  if address already exists
     */
    public String createAccount(String anAddress) throws LedgerException {
        // Check if account address already exists
        if (this.currentBlock.getAccountBalanceMap().containsKey(anAddress)) {
            throw new LedgerException("create account", "account address already exists");
        }

        // Create the account
        Account account = new Account(anAddress);

        // Add the account to current block's accountBalanceMap
        this.currentBlock.addAccountBalanceMap(account);

        System.out.println("Account created for " + account.getAddress() + ".");

        return account.getAddress();
    }

    /**
     * Creates a new transaction. Validates the number format of the transaction amount and fee.
     * Checks if the following requirements are satisfied:
     * - Minimum fee
     * - Non-negative amount
     * - Maximum note length
     *
     * @param anAmount  Amount deducted from the payer's account & added to the receiver's account
     * @param aFee  Transaction fee transferred from the payer's account to the master account
     * @param aNote  Note for the transaction
     * @param aPayer  Payer's unique address
     * @param aReceiver  Receiver's unique address
     * @return  a new transaction
     * @throws LedgerException  if the amount or fee cannot be parsed as integers or if any of the above requirements
     * are not satisfied
     */
    public Transaction createTransaction(String anAmount, String aFee, String aNote,
                                         String aPayer, String aReceiver) throws LedgerException {
        // Parse the amount and fee as integers
        int amountInt = this.parseInteger(anAmount);
        int feeInt = this.parseInteger(aFee);

        // Check if the min fee requirement is satisfied
        if (feeInt < Transaction.MIN_FEE) {
            throw new LedgerException("create transaction ", "the fee must be at least " + Transaction.MIN_FEE + " "
                    + "units");
        }

        // Check if the amount is non-negative
        if (amountInt < 0) {
            throw new LedgerException("create transaction ", "amount must be non-negative");
        }

        // Check if the note is longer than MAX_NOTE_LEN
        if (aNote.length() > Transaction.MAX_NOTE_LEN) {
            throw new LedgerException("create transaction ", "note length exceeds " + Transaction.MAX_NOTE_LEN + " "
                    + "characters");
        }

        return new Transaction(amountInt, feeInt, aNote, aPayer, aReceiver);
    }

    /**
     * Parses a string as an integer.
     *
     * @param aStr  String input to be parsed as an integer
     * @return  output integer
     * @throws LedgerException  if the input string cannot be parsed as an integer
     */
    public int parseInteger(String aStr) throws LedgerException {
        try {
            return Integer.parseInt(aStr);
        } catch (NumberFormatException e) {
            throw new LedgerException(null, aStr + " cannot be parsed as an integer");
        }
    }

    /**
     * Adds the transaction to the current block. If the current block contains 10 transactions, validates each
     * transaction. If all transactions are valid, commits the current block and creates a new block.
     * @param txn  Transaction to be processed
     * @return  Transaction ID
     * @throws LedgerException  if the current block already has a hash value
     */
    public String processTransaction(Transaction txn) throws LedgerException {

        // Add transaction to current block
        this.currentBlock.addTransaction(txn);
        System.out.printf("Received Transaction %s%n", txn.getTransactionId());

        // Check if block is full
        if (this.currentBlock.getTransactionList().size() == Block.MAX_NUM_OF_TXNS) {

            // Get a copy of the account balance map
            Map<String, Account> accountBalanceMap = this.currentBlock.getCopyOfAccountBalanceMap();

            // Get current block's transactions
            List<Transaction> txns = this.currentBlock.getTransactionList();

            // Check if all transactions are valid
            if (this.validateTransactions(txns, accountBalanceMap)) {
                // Commit current block
                this.currentBlock.commitBlock(accountBalanceMap);

                // Add current block to blockMap
                this.blockMap.put(this.currentBlock.getBlockNumber(), this.currentBlock);

                // Create a new currentBlock
                this.currentBlock = new Block(this.blockMap.size() + 1, this.currentBlock.getHash(),
                        this.currentBlock, this.currentBlock.getCopyOfAccountBalanceMap());
            }
        }

        return txn.getTransactionId();
    }

    /**
     * Validates a list of transactions based on an accountBalanceMap. Adjusts balances in accountBalanceMap for each
     * valid transaction. Removes invalid transactions from the transactions list.
     *
     * @param txns  List of transactions to be validated
     * @param accountBalanceMap  Map of account addresses and account objects
     * @return  True if all transactions are valid. Otherwise, returns false.
     */
    public boolean validateTransactions(List<Transaction> txns, Map<String, Account> accountBalanceMap) {

        // Create an iterator to safely remove invalid transactions
        Iterator<Transaction> iter = txns.iterator();

        // Iterate over each transaction
        while (iter.hasNext()) {
            Transaction txn = iter.next();
            try {
                // Validate transaction
                this.validateTransaction(txn, accountBalanceMap);

                // Adjust account balances
                accountBalanceMap = this.adjustBalances(txn, accountBalanceMap);
            } catch (LedgerException e) {
                // Remove invalid transaction
                iter.remove();
                System.out.println(e);
            }
        }

        return txns.size() == Block.MAX_NUM_OF_TXNS;
    }

    /**
     * Validates a single transaction.
     * Checks if the payer and receiver accounts exist and the payer has enough funds to cover the amount and the fee.
     *
     * @param txn  Transaction to be validated
     * @param accountBalanceMap  Map of account addresses and account objects
     * @throws LedgerException
     */
    public void validateTransaction(Transaction txn, Map<String, Account> accountBalanceMap) throws LedgerException {

        // Check if the payer account exists
        if (!(accountBalanceMap.containsKey(txn.getPayer()))) {
            throw new LedgerException("validate transaction", "payer does not exist");
        }

        // Check if the receiver account exists
        if (!(accountBalanceMap.containsKey(txn.getReceiver()))) {
            throw new LedgerException("validate transaction", "receiver does not exist");
        }

        // Check if the payer account has enough funds
        if (accountBalanceMap.get(txn.getPayer()).getBalance() < txn.getAmount() + txn.getFee()) {
            throw new LedgerException("validate transaction", txn.getPayer() + " does not have enough funds");
        }
    }

    /**
     * Adjusts account balances of the payer, the receiver, and the master account based on a transaction.
     *
     * @param txn  Transaction to be processed
     * @param accountBalanceMap  Map of account addresses and account objects
     * @return  updated accountBalanceMap
     */
    public Map<String, Account> adjustBalances(Transaction txn, Map<String, Account> accountBalanceMap) {
        // Adjust the payer's account balance
        accountBalanceMap.get(txn.getPayer()).withdraw(txn.getAmount() + txn.getFee());

        // Adjust the receiver's account balance
        accountBalanceMap.get(txn.getReceiver()).deposit(txn.getAmount());

        // Adjust the master account's balance
        accountBalanceMap.get(Account.MASTER).deposit(txn.getFee());

        return accountBalanceMap;
    }

    /**
     * Retrieves a transaction based on a transaction ID. The transaction must be contained in a block that is
     * committed to the ledger.
     *
     * @param txnId  Unique identifier of the transaction to be retrieved
     * @return  Transaction object
     * @throws LedgerException  if transaction ID does not exist
     */
    public Transaction getTransaction(String txnId) throws LedgerException {
        // Check if the transaction ID exists
        if (!Block.getTransactionMap().containsKey(txnId)) {
            throw new LedgerException("get transaction", "transaction " + txnId + " does not exist");
        }

        return Block.getTransactionMap().get(txnId);
    }

    /**
     * Retrieves the balance of an account address.
     * Checks if any block has been committed yet and if the account address exists.
     *
     * @param address  unique identifier of the given account
     * @return  balance of the given account
     * @throws LedgerException  if no block has been committed yet or if the given account address does not exist
     */
    public int getAccountBalance(String address) throws LedgerException {
        // Get the previous block
        Block previousBlock = this.currentBlock.getPreviousBlock();

        // Check if any block has been committed yet
        if (previousBlock == null) {
            throw new LedgerException("get account balance", "no block has been committed yet");
        }

        // Check if the given account address exists
        if (!previousBlock.getAccountBalanceMap().containsKey(address)) {
            throw new LedgerException("get account balance", "account has not been committed to a block");
        }

        return previousBlock.getAccountBalanceMap().get(address).getBalance();
    }

    /**
     * Retrieves the account balances for the most recently completed block.
     *
     * @return  Map of account addresses and account balances
     * @throws LedgerException  if no block has been committed yet
     */
    public Map<String, Integer> getAccountBalances() throws LedgerException {

        // Get the previous block
        Block previousBlock = this.currentBlock.getPreviousBlock();

        // Check if any block has been committed yet
        if (previousBlock == null) {
            throw new LedgerException("get account balances", "no block has been committed yet");
        }

        // Create a map of account addresses and account balances
        Map<String, Integer> result = new HashMap<>();
        for (Map.Entry<String, Account> entry : previousBlock.getCopyOfAccountBalanceMap().entrySet()) {
            result.put(entry.getKey(), entry.getValue().getBalance());
        }

        return result;
    }

    /**
     * Deep copies the requested block to ensure immutability of the committed block.
     *
     * @param blockNumber  Unique identifier of the block
     * @return  a copy of the requested block
     * @throws LedgerException  if the block number does not exist
     */
    public Block getBlock(int blockNumber) throws LedgerException {
        // Check if the block number exists
        if (!this.blockMap.containsKey(blockNumber)) {
            throw new LedgerException("get block", "block number " + blockNumber + " does not exist");
        }

        // Return a copy of the block
        return new Block(this.blockMap.get(blockNumber));
    }

    /**
     * Validates the current state of the blockchain.
     * For each block checks if the preceding block's hash is correct, the account balances total to
     * Integer.MAX_VALUE, the number of transactions is equal to 10.
     *
     * @throws LedgerException  if any of the above requirements are not satisfied
     */
    public void validate() throws LedgerException {
        // Iterate over each committed block
        for (Map.Entry<Integer, Block> entry : this.blockMap.entrySet()) {
            Block currBlock = entry.getValue();

            // Check the hash of the previous block
            if (entry.getKey() != 1) {
                Block previousBlock = this.blockMap.get(entry.getKey() - 1);
                String previousBlockHash = previousBlock.computeHash();
                if (!previousBlockHash.equals(currBlock.getPreviousHash())) {
                    throw new LedgerException("blockchain validation",
                            "hash values in blocks " + entry.getKey() + " & " + (entry.getKey() - 1) +  " don't match");
                } else {
                    System.out.println("Successfully validated previous hash of block " + entry.getKey());
                }
            }

            // Check that the account balances total to the max value
            Map<String, Account> currAccountBalanceMap = currBlock.getAccountBalanceMap();
            int total = 0;
            for (Account account : currAccountBalanceMap.values()) {
                total += account.getBalance();
            }
            if (total != Integer.MAX_VALUE) {
                throw new LedgerException("blockchain validation", "sum of account balances is " + total);
            }
            System.out.println("Successfully validated sum of account balances for block " + entry.getKey());

            // Check if num of transactions is correct
            if (currBlock.getTransactionList().size() != Block.MAX_NUM_OF_TXNS) {
                throw new LedgerException("blockchain validation", "number of transactions is not "
                        + Block.MAX_NUM_OF_TXNS);
            }
        }

        System.out.println("Validation of the current state of the blockchain completed successfully.");
    }



















}
