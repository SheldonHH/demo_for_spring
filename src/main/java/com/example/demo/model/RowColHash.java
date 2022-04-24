package com.example.demo.model;

import java.math.BigInteger;
import java.util.HashMap;

public class RowColHash {
    public String username;
    public HashMap<String, BigInteger[]> index_Hash;

    public RowColHash(String username, HashMap<String, BigInteger[]> index_Hash) {
        this.username = username;
        this.index_Hash = index_Hash;
    }
}
