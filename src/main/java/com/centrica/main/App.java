/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.centrica.main;

import com.centrica.commonfunction.CommonFunction;
import com.centrica.entity.Table;
import com.centrica.entity.TableRelationship;
import com.centrica.relationshipalgorithm.SchemaKeyAlgorithm;
import com.centrica.smartkeydiscovery.SmartDiscovery;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

/**
 *
 * @author dandy
 */
public class App {

    Stack<TableRelationship> stackTRs = new Stack<>();

    public static void main(String[] args) throws Exception {
        StringBuilder sbTableRel = new StringBuilder("From,To\n");
        
        List<String> listUsedTablenames = new ArrayList<String>();
        String keyword = "Blauer see delikatessen";
        String seededTableName = "customers";
        String additionalKeyword = "forsterstr. 57,Mannheim";
        String destination = "Meat Pie";

        String filesDir = "C:\\Users\\dandy\\OneDrive\\Documents\\NetBeansProjects\\SmartKeyDiscovery\\Data\\northwind-mongo-master";
        String[] fileNames = CommonFunction.getFilenamesInFolder(filesDir);

        Table tblSeeded = new Table(seededTableName);

        //STEP 1 - Get initial Seeded table
        //Look for seeded table            
        for (String tempTable : fileNames) {
            //Exclude table that have already checked
            if (!listUsedTablenames.contains(CommonFunction.getFilenameOnly(tempTable))) {
                if (CommonFunction.stringContains(tempTable, seededTableName)) {
                    tblSeeded.loadFromCSV(filesDir + "\\" + tempTable);

                    listUsedTablenames.add(seededTableName);
                }
            }
        }

        Stack<TableRelationship> stackTableRel = new Stack<>();
        while (tblSeeded != null) {

            SmartDiscovery sd = new SmartDiscovery();
            SchemaKeyAlgorithm ska;
            List<TableRelationship> listRelTable = new ArrayList<>();
            //First step look for keyword in seeded table     
            tblSeeded = sd.searchKeywordTable(keyword, tblSeeded);
            System.out.print("Step 1 - Done");

            //Step 2 - look for possible relationship with other tables
            for (String tempTable : fileNames) {
                String tempTableName = CommonFunction.getFilenameOnly(tempTable);
                //Exclude table that has been used
                if (!listUsedTablenames.contains(tempTableName)) {
                    String fileLocationTemp = filesDir + "\\" + tempTable;
                    Table temp = new Table(tempTableName);
                    temp.loadFromCSV(fileLocationTemp);
                    List<TableRelationship> listRelTemp = sd.searchTableRelationship(tblSeeded, temp);

                    //Implement the ALGORITHM
                    ska = new SchemaKeyAlgorithm(listRelTemp);
                    TableRelationship relTemp = ska.getSingleJoinKey();

                    if (relTemp != null) {
                        //Check whether the table contains the destination keyword
                        String destinationKeywordTemp = sd.getTableContainsDestinationKeyword(destination, relTemp.tableTo);
                        relTemp.setDestinationKeywordFound(destinationKeywordTemp);
                        
                        //Get additional keyword (If exists from the table relationship)
                        String additionalKeywordTemp = sd.getTableContainsKeywords(additionalKeyword.split(","), relTemp.tableTo);
                        relTemp.setAdditionalKeywordFound(additionalKeywordTemp);
                        
                        listRelTable.add(relTemp);
                        
                        //Exclude used rel table for future
                        listUsedTablenames.add(relTemp.getTableNameTo());      
                        
                        sbTableRel.append(relTemp.getTableNameFrom() + "," + relTemp.getTableNameTo() + "\n");
                    }
                }

            }

            System.out.print("Step 2 - Done");

            //Step 3 - Put relationships into stack with priority of table that have additional keyword
            if (!listRelTable.isEmpty()) {
                listRelTable = sd.reorderRelationshipBasedonAdditionalKeyword(listRelTable);
                for (TableRelationship trTemp : listRelTable) {
                    stackTableRel.push(trTemp);
                }
            }
                
            //Method will check whether the stac is empty or not
            System.out.print("Step 3 - Done");
            if(!stackTableRel.empty()){
                TableRelationship tr = stackTableRel.pop();
                tblSeeded = tr.tableTo;
                keyword = tr.getKeyword();
                if(!CommonFunction.stringIsEmpty(tr.getDestinationKeywordFound())) {
                    tblSeeded = null;
                    System.out.println("Destination found");
                }
            }else{
                tblSeeded = null;
            }
            
            
        }
        
        System.out.print("Done");
        //sTR.push(item)

//      
//        
//      Table tblOrders = new Table("Orders");
//      String orderFileLocation = "C:\\Users\\dandy\\OneDrive\\Documents\\NetBeansProjects\\SmartKeyDiscovery\\Data\\northwind-mongo-master\\orders.csv";
//      tblOrders.loadFromCSV(orderFileLocation);
//      
//      SmartDiscovery sd = new SmartDiscovery();
//      Table tempCustomer = sd.searchKeywordTable("BLAUS", tblCustomers);            
//      
//      
//      
//      List<TableRelationship> temp = sd.searchTableRelationship(tempCustomer, tblOrders);
//      
//      
//      
//      System.out.print("Done" + " " + CommonFunction.readProperty("searchlimit"));
//      System.out.print("Done");      
//      
//      String tempFileName = "C:\\Users\\dandy\\OneDrive\\Documents\\NetBeansProjects\\SmartKeyDiscovery\\test.csv";
//      int index = 0;
//      CommonFunction.generateCsvFile(tempFileName, tempCustomer.toString() ,false);
//      TableRelationship tr = ska.getSingleJoinKey();
//      CommonFunction.generateCsvFile(tempFileName, tr.toString(true),true);
//      CommonFunction.generateCsvFile(tempFileName, "\n" + tr.tableTo.toString(),true);
//      
        //Print the relationship table
//      for (TableRelationship tr : temp){
//          if(index == 0) {
//              CommonFunction.generateCsvFile(tempFileName, tr.toString(true),true);
//          }
//          else{
//              CommonFunction.generateCsvFile(tempFileName, tr.toString(false),true);
//          }
//          index++;
//      }
//      
//      index = 0;
//      //Print each table that has relationship with source table
//      for (TableRelationship tr : temp){
//          if(index == 0) CommonFunction.generateCsvFile(tempFileName, "\n" + tr.tableTo.toString(),true);
//          else{
//              CommonFunction.generateCsvFile(tempFileName, tr.tableTo.toString(),true);
//          }
//          index++;
//      }
    }

}
