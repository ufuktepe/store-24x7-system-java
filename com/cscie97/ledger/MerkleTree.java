package com.cscie97.ledger;

import com.cscie97.Utility;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Base64;

/**
 * Helper class for computing root hash of a Merkle tree and hashing a string.
 *
 * @author Burak Ufuktepe
 */
public class MerkleTree {

    /**
     * Computes the root hash from a list of leaf nodes.
     *
     * @param nodes  List of strings representing leaves of a Merkle Tree
     * @return  Root hash of a Merkle Tree
     */
    public static String getRootHash(ArrayList<String> nodes) {
        ArrayList<String> hashedNodes = hashNodes(nodes);
        return getRoot(hashedNodes).get(0);
    }

    /**
     * Hashes each element of a list and returns the hashed list.
     *
     * @param nodes  List of strings
     * @return  Hashed list
     */
    private static ArrayList<String> hashNodes(ArrayList<String> nodes) {
        ArrayList<String> hashedNodes = new ArrayList<>();

        // Hash each element and add it to hashedNodes
        for (String leaf : nodes) {
            hashedNodes.add(Utility.hashString(leaf));
        }

        return hashedNodes;
    }

    /**
     * Takes a list of hashed leaves of a tree and recursively computes the Merkle root.
     *
     * @param hashedNodes  List of strings representing hashed leaves of a tree
     * @return  List with one element which is the Merkle root
     */
    private static ArrayList<String> getRoot(ArrayList<String> hashedNodes) {
        //Return the Merkle Root
        if (hashedNodes.size() == 1) {
            return hashedNodes;
        }

        // Create a list for parent nodes
        ArrayList<String> parentNodes = new ArrayList<>();

        // Iterate over child pairs and hash them to get the parent node
        for (int i = 0; i < hashedNodes.size(); i += 2) {
            String hashedString;

            // Compute the parent node
            if (i + 1 < hashedNodes.size()) {
                // Concatenate two consecutive child nodes and hash it
                hashedString = Utility.hashString(hashedNodes.get(i).concat(hashedNodes.get(i + 1)));
            } else {
                // Hash the last child node
                hashedString = Utility.hashString(hashedNodes.get(i));
            }

            // Add the hash to the parentNodes
            parentNodes.add(hashedString);
        }

        // Call getRoot to find the parents of parentNodes
        return getRoot(parentNodes);
    }


}
