package com.cscie97;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utility {

    /**
     * Hashes a string using SHA-256.
     *
     * @param input  String to be hashed
     * @return  Hashed string
     */
    public static String hashString(String input) {
        try {
            // Hash the string using SHA-256 and encode the result to string
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] messageDigest = md.digest(input.getBytes());
            return Base64.getEncoder().encodeToString(messageDigest);
        } catch (NoSuchAlgorithmException e) {
            System.out.println(e);
            return null;
        }
    }

    /**
     * Splits the command by whitespace. Keeps the strings between quotation marks as is.
     *
     * @param cmd  String to be split
     * @return  List of strings split by whitespace
     */
    public static ArrayList<String> splitString(String cmd) {
        ArrayList<String> result = new ArrayList<>();

        // Split by quotation marks
        String[] temp = cmd.split("\"");

        for (int i = 0; i < temp.length; i++) {
            // Remove leading and trailing whitespace and skip empty strings
            temp[i] = temp[i].trim();
            if (temp[i].isEmpty()) {
                continue;
            }

            if (i % 2 == 1) {
                // Odd indexed elements are strings in quotation marks, add them to result
                result.add(temp[i]);
            } else {
                // Split even indexed elements by whitespace
                String[] unquotedStrings = temp[i].split("\\s+");

                // Add them to result
                result.addAll(Arrays.asList(unquotedStrings));
            }
        }

        return result;
    }

}
