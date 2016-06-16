/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.centrica.main;

import com.centrica.commonfunction.CommonFunction;
import com.centrica.entity.Table;
import com.centrica.entity.TableRelationshipDetail;
import com.centrica.relationshipalgorithm.SchemaKeyAlgorithm;
import com.centrica.smartkeydiscovery.SmartDiscovery;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Stack;

/**
 *
 * @author dandy
 */
public class App {

    public static int testing = 1;
    Stack<TableRelationshipDetail> stackTRs = new Stack<>();

    public static void main(String[] args) throws Exception {
        boolean isDestinationFound = false;
        List<String> allTableRelationship = new ArrayList<>();              
        
        HashSet<String> hashUsedTablenames = new HashSet<String>();
        HashSet<String> hashSeeded = new HashSet<String>();
        
        String keyword = "Blauer see delikatessen";
        String seededTableName = "customers";
        String additionalKeyword = "forsterstr. 57,Mannheim";
        String destination = "meat pie";// "Peacock";//"Carnarvon Tigers";//Meat pie";//"laura"; //"0.15";//"Aniseed Syrup";// "Meat pie";
        
        if(testing==0){
            keyword = "Blauer see delikatessen";
            seededTableName = "customers";
            additionalKeyword = "forsterstr. 57,Mannheim";
            destination = "meat pie";        
        }
        

        String filesDir = "C:\\Users\\dandy\\OneDrive\\Documents\\NetBeansProjects\\SmartKeyDiscovery\\Data\\northwind-mongo-master";
        String[] fileNames = CommonFunction.getFilenamesInFolder(filesDir);

        Table tblSeeded = new Table(seededTableName);

        //STEP 1 - Get initial Seeded table
        //Look for seeded table            
        for (String tempTable : fileNames) {
            //Exclude table that have already checked
            if (!hashUsedTablenames.contains(CommonFunction.getFilenameOnly(tempTable))) {
                if (CommonFunction.stringContains(tempTable, seededTableName)) {
                    tblSeeded.loadFromCSV(filesDir + "\\" + tempTable);

                    hashUsedTablenames.add(seededTableName);
                }
            }
        }
        System.out.print("Step 1 - Get initial seeded table - Done\n");

        Stack<TableRelationshipDetail> stackTableRel = new Stack<>();
        while (tblSeeded != null) {            
            SmartDiscovery sd = new SmartDiscovery();
            SchemaKeyAlgorithm ska;
            List<TableRelationshipDetail> listRelTable = new ArrayList<>();
            //First step look for keyword in seeded table     
            tblSeeded = sd.searchKeywordTable(keyword, tblSeeded);

            System.out.print("Step 1a - Generate keyword table from seed\n");
            boolean skipTable = true;
            
            //Step 2 - look for possible relationship with other tables
            for (String tempTable : fileNames) {
                String tempTableName = CommonFunction.getFilenameOnly(tempTable);
                if(isDestinationFound) break;
                //Cannot check own table
                if(tempTableName.equals(tblSeeded.getTableName())) continue;
                
                skipTable = true;
                //If user choose smartsearch then every table that has already been used, 
                //will not be considered in the next iteration
                //Else, disable smart search
                //Which means it will implement brute force Which will always consider all table
                if (Boolean.parseBoolean(CommonFunction.readProperty("smartsearch"))) {
                    //Exclude table that has been used
                    if (!hashUsedTablenames.contains(tempTableName)) {
                        skipTable = false;
                        //TableRelationshipDetail relTemp = ska.getSingleJoinKey();
                    } else {
                        //If the table is one of the exception
                        //Same seeded table should be also checked againts the same table
                        for (String temp : hashSeeded) {
                            if (temp.split(",")[0].equals(tblSeeded.getTableName())
                                    && temp.split(",")[1].equals(tempTableName)) {
                                skipTable = false;
                                break;
                            }
                        }
                    }
                }else{
                    skipTable = false;
                }
                                                                                
                if(!skipTable){
                    String fileLocationTemp = filesDir + "\\" + tempTable;
                    Table temp = new Table(tempTableName);
                    temp.loadFromCSV(fileLocationTemp);
                    List<TableRelationshipDetail> listRelTemp = sd.searchTableRelationshipDetail(tblSeeded, temp);

                    if (!listRelTemp.isEmpty()) {
                        //Implement the ALGORITHM
                        ska = new SchemaKeyAlgorithm(listRelTemp);
                        listRelTemp = ska.getMultipleJoinKey();

                        for (TableRelationshipDetail relTemp : listRelTemp) {

                            //Check whether the table contains the destination keyword
                            String destinationKeywordTemp = sd.getTableContainsDestinationKeyword(destination, relTemp.tableTo);
                            relTemp.setDestinationKeywordFound(destinationKeywordTemp);
                            
                            //Get additional keyword (If exists from the table relationship)
                            HashSet<String> additionalKeywordTemp = sd.getTableContainsKeywords(additionalKeyword.split(","), relTemp.tableTo);
                            relTemp.setAdditionalKeywordFound(additionalKeywordTemp);

                            listRelTable.add(relTemp);                                                     
                                                        
                            hashUsedTablenames.add(relTemp.getTableNameTo());                            
                            hashSeeded.add(tblSeeded.getTableName() + "," + relTemp.getTableNameTo());
                            
                            allTableRelationship.add(relTemp.getRelationshipInString(true));
                            
                            //If destination is found
                            if(destinationKeywordTemp != null) {
                                isDestinationFound = true;
                                break;
                            }
                            
//                            if(relTemp.getColumnNameTo().equals("ShipperID")){
//                                System.out.print("");
//                            }
                            
                        }
                    }
                }

            }

            System.out.print("Step 2 - Look for possible relationship with other tables - Done\n");

            //Step 3 - Put relationships into stack with priority of table that have additional keyword
            if (!listRelTable.isEmpty()) {
                listRelTable = sd.reorderRelationshipBasedonPriorities(listRelTable);
                for (TableRelationshipDetail relTemp : listRelTable) {
                    //To check whether the destination keyword has been found
                    if (!CommonFunction.stringIsEmpty(relTemp.getDestinationKeywordFound())) {
                        tblSeeded = null;
                        isDestinationFound = true;
                        stackTableRel.clear();
                    } else {
                        stackTableRel.push(relTemp);
                    }                                    
                }
            }

            //Method will check whether the stack is empty or not
            if (!stackTableRel.empty()) {
                TableRelationshipDetail relTemp = stackTableRel.pop();
                tblSeeded = relTemp.tableTo;
                keyword = relTemp.getKeyword();
            } else {
                tblSeeded = null;
            }                        

            System.out.print("Step 3 - End of loop iteration!\n");

        }
        StringBuilder sbTableRel = new StringBuilder("From,To,Keyword,ColumnFrom,ColumnTo,AdditionalKeyword,DestinationKeyword\n");
        for(String temp : allTableRelationship){
            sbTableRel.append(temp + "\n");
        }
        CommonFunction.generateFile("rel-output_1.1.csv", sbTableRel.toString(), false);
        System.out.print("Done\n");
        if (isDestinationFound) {
            System.out.println("Destination found!!!!!!!!!!!");
        } else {
            System.out.println("Destination NOT found!");
        }
    }

}
