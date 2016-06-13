/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.centrica.entity;

import com.google.common.base.Preconditions;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author dandy
 */
public class Table {

    private String tableName;
    public String[] columnName;
    public String[] columnType;
    public List<String[]> rows;

    public Table(String tableName) {
        this.tableName = Preconditions.checkNotNull(tableName);
        rows = new ArrayList<String[]>();
    }

    public void loadFromCSV(String csvFile) throws Exception {
        BufferedReader br = null;
        String line = "";
        String cvsSplitBy = ",";
        boolean isHeader = true;
        try {
            br = new BufferedReader(new FileReader(csvFile));
            while ((line = br.readLine()) != null) {
                if (isHeader) {
                    columnName = line.split(cvsSplitBy);
                    isHeader = false;
                } else {
                    rows.add(line.split(cvsSplitBy));
                }
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
            throw (new Exception("File not found"));
        } catch (IOException e) {
            e.printStackTrace();
            throw (new Exception("IO Exception"));
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                    throw (new Exception("IO Exception"));
                }
            }
        }
    }

    /**
     * @return the tableName
     */
    public String getTableName() {
        return tableName;
    }

    /**
     * @param tableName the tableName to set
     */
    public void setTableName(String tableName) {
        this.tableName = tableName;
    }
    
    public String toString(){
        StringBuffer str = new StringBuffer();
        str.append(getTableName()+"\n");
        for(String temp : columnName){
            str.append(temp + ",");
        }
        str.deleteCharAt(str.length()-1); 
        str.append("\n");
        
        for(String[] arrayRows: rows){
            for(String temp : arrayRows){
                str.append(temp + ",");
            }
            str.deleteCharAt(str.length()-1); 
            str.append("\n");
        }
               
        return str.toString();
    }
}
