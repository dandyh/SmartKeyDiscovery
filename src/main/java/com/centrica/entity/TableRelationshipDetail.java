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
public class TableRelationshipDetail extends TableRelationship {
    private String keyword;
    private boolean partialRelationship;    
    public Table tableTo;
    
     public TableRelationshipDetail(String keyword, String tableNameTo, String columnNameTo, int columnIndexTo) {
        super("", "", -1, tableNameTo, columnNameTo, columnIndexTo);
        partialRelationship = true;
        this.keyword = keyword;        
    }
    
    public TableRelationshipDetail(String keyword, String tableNameFrom, String columnNameFrom, int columnIndexFrom, String tableNameTo, String columnNameTo, int columnIndexTo) {
        super(tableNameFrom, columnNameFrom, columnIndexFrom, tableNameTo, columnNameTo, columnIndexTo);
        this.partialRelationship = false;
        this.keyword = keyword;        
    }
    

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }
    
    public boolean isPartialRelationship() {
        return partialRelationship;
    }

    public void setPartialRelationship(boolean partialRelationship) {
        this.partialRelationship = partialRelationship;
    }
    
    public String getRelationshipInString(){
        String separator = "_";
        return this.tableNameFrom + separator + this.columnNameFrom + separator +
                this.tableNameTo + separator + this.columnNameTo;
    }
}
