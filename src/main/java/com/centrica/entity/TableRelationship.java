/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.centrica.entity;

/**
 *
 * @author dandy
 */
public class TableRelationship {

    private String keyword;
    private String tableNameFrom;
    private String columnNameFrom;
    private int columnIndexFrom;
    private String tableNameTo;
    private String columnNameTo;
    private int columnIndexTo;
    private boolean partialRelationship;
    private String additionalKeywordFound;
    private String destinationKeywordFound;

    public Table tableTo;

    public TableRelationship(String keyword, String tableNameTo, String columnNameTo, int columnIndexTo) {
        this.keyword = keyword;
        this.tableNameTo = tableNameTo;
        this.columnNameTo = columnNameTo;
        this.columnIndexTo = columnIndexTo;
        this.partialRelationship = true;
    }

    public TableRelationship(String keyword, String tableNameFrom, String columnNameFrom, int columnIndexFrom,
            String tableNameTo, String columnNameTo, int columnIndexTo) {

        this.keyword = keyword;
        this.tableNameFrom = tableNameFrom;
        this.columnNameFrom = columnNameFrom;
        this.columnIndexFrom = columnIndexFrom;
        this.tableNameTo = tableNameTo;
        this.columnNameTo = columnNameTo;
        this.columnIndexTo = columnIndexTo;
        this.partialRelationship = false;
    }

    public String toString(boolean includeHeader) {
        StringBuffer str = new StringBuffer();
        if (includeHeader) {
            str.append("keyword,tableNameFrom,columnNameFrom,columnIndexFrom,tableNameTo,columnNameTo,columnIndexTo\n");
        }

        str.append(String.format("%s,%s,%s,%s,%s,%s,%s",
                this.keyword,
                this.tableNameFrom,
                this.columnNameFrom,
                this.columnIndexFrom,
                this.tableNameTo,
                this.columnNameTo,
                this.columnIndexTo));

        return str.toString();
    }

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
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

    public boolean isPartialRelationship() {
        return partialRelationship;
    }

    public void setPartialRelationship(boolean partialRelationship) {
        this.partialRelationship = partialRelationship;
    }

    public String getDestinationKeywordFound() {
        return destinationKeywordFound;
    }

    public void setDestinationKeywordFound(String destinationKeywordFound) {
        this.destinationKeywordFound = destinationKeywordFound;
    }

    public String getAdditionalKeywordFound() {
        return additionalKeywordFound;
    }

    public void setAdditionalKeywordFound(String additionalKeywordFound) {
        this.additionalKeywordFound = additionalKeywordFound;
    }
}
