package com.example.demo.model.vmatrixhash;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.UUID;

public class ResponseVRowCol {
    UUID user_id;
    ArrayList<ArrayList<String>> rowVs;
    ArrayList<ArrayList<String>> colVs;

    public ResponseVRowCol(@JsonProperty("user_id")UUID user_id,  @JsonProperty("rowVs") ArrayList<ArrayList<String>> rowVs, @JsonProperty("colVs")ArrayList<ArrayList<String>> colVs) {
        this.user_id = user_id;
        this.rowVs = rowVs;
        this.colVs = colVs;
    }

    public UUID getUser_id() {
        return user_id;
    }

    public void setUser_id(UUID user_id) {
        this.user_id = user_id;
    }

    public ArrayList<ArrayList<String>> getRowVs() {
        return rowVs;
    }

    public void setRowVs(ArrayList<ArrayList<String>> rowVs) {
        this.rowVs = rowVs;
    }

    public ArrayList<ArrayList<String>> getColVs() {
        return colVs;
    }

    public void setColVs(ArrayList<ArrayList<String>> colVs) {
        this.colVs = colVs;
    }
}
