package com.cscie97.test;

import com.cscie97.CommandProcessor;

/**
 * Test class to test the Store Model System. Takes a command file as input that includes various commands for feeding
 * the Store Model Service a set of operations.
 *
 * @author Burak Ufuktepe
 */
public class TestDriver {

    /**
     * Accepts a command file and calls the processCommandFile method of the CommandProcessor class.
     *
     * @param args  Command file
     */
    public static void main(String[] args) {
        CommandProcessor cp = new CommandProcessor();
        cp.processCommandFile(args[0]);
    }
}
