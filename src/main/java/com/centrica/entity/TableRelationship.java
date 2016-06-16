/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.centrica.entity;

import java.util.HashSet;

/**
 *
 * @author dandy
 */
public class TableRelationship {

    protected String tableNameFrom;
    protected String columnNameFrom;
    protected int columnIndexFrom;
    protected String tableNameTo;
    protected String columnNameTo;
    protected int columnIndexTo;
    protected HashSet<String> additionalKeywordFound;
    
    protected String destinationKeywordFound;

    public TableRelationship(String tableNameFrom, String columnNameFrom, int columnIndexFrom,
            String tableNameTo, String columnNameTo, int columnIndexTo) {

        this.tableNameFrom = tableNameFrom;
        this.columnNameFrom = columnNameFrom;
        this.columnIndexFrom = columnIndexFrom;
        this.tableNameTo = tableNameTo;
        this.columnNameTo = columnNameTo;
        this.columnIndexTo = columnIndexTo;
    }
    
    public TableRelationship(TableRelationshipDetail trdTEmp){
        this.tableNameFrom = trdTEmp.tableNameFrom;
        this.columnNameFrom = trdTEmp.columnNameFrom;
        this.columnIndexFrom = trdTEmp.columnIndexFrom;
        this.tableNameTo = trdTEmp.tableNameTo;
        this.columnNameTo = trdTEmp.columnNameTo;
        this.columnIndexTo = trdTEmp.columnIndexTo;
    }

    public String toString(boolean includeHeader) {
        StringBuffer str = new StringBuffer();
        if (includeHeader) {
            str.append("keyword,tableNameFrom,columnNameFrom,columnIndexFrom,tableNameTo,columnNameTo,columnIndexTo\n");
        }

        str.append(String.format("%s,%s,%s,%s,%s,%s,%s",
                this.tableNameFrom,
                this.columnNameFrom,
                this.columnIndexFrom,
                this.tableNameTo,
                this.columnNameTo,
                this.columnIndexTo));

        return str.toString();
    }

    public String getTableNameFrom() {
        return tableNameFrom;
    }

    public void setTableNameFrom(String tableNameFrom) {
        this.tableNameFrom = tableNameFrom;
    }

    public String getColumnNameFrom() {
        return columnNameFrom;
    }

    public void setColumnNameFrom(String columnNameFrom) {
        this.columnNameFrom = columnNameFrom;
    }

    public int getColumnIndexFrom() {
        return columnIndexFrom;
    }

    public void setColumnIndexFrom(int columnIndexFrom) {
        this.columnIndexFrom = columnIndexFrom;
    }

    public String getTableNameTo() {
        return tableNameTo;
    }

    public void setTableNameTo(String tableNameTo) {
        this.tableNameTo = tableNameTo;
    }

    public String getColumnNameTo() {
        return columnNameTo;
    }

    public void setColumnNameTo(String columnNameTo) {
        this.columnNameTo = columnNameTo;
    }

    public int getColumnIndexTo() {
        return columnIndexTo;
    }

    public void setColumnIndexTo(int columnIndexTo) {
        this.columnIndexTo = columnIndexTo;
    }

    public String getDestinationKeywordFound() {
        return destinationKeywordFound;
    }

    public void setDestinationKeywordFound(String destinationKeywordFound) {
        this.destinationKeywordFound = destinationKeywordFound;
    }
    
    public HashSet<String> getAdditionalKeywordFound() {
        return additionalKeywordFound;
    }

    public void setAdditionalKeywordFound(HashSet<String> additionalKeywordFound) {
        this.additionalKeywordFound = additionalKeywordFound;
    }
  
}
