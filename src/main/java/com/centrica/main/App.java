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


import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
/**
 *
 * @author dandy
 */
public class App {
    public static void main(String[] args) throws Exception { 
      String keyword = "Blauer see delikatessen";
      String seededTable = "customers";
      String additionalKeyword = "forsterstr. 57,Mannheim";
      String destination = "Meat pie";
      
      String filesDir = "C:\\Users\\dandy\\OneDrive\\Documents\\NetBeansProjects\\SmartKeyDiscovery\\Data\\northwind-mongo-master";
      String[] fileNames = CommonFunction.getFilenamesInFolder(filesDir);     
      
      //Look for seeded table
      Table tblSeeded = new Table(seededTable);
      for(String tempTable : fileNames){
          if(CommonFunction.stringContains(tempTable, seededTable)){
              tblSeeded.loadFromCSV(filesDir + "\\" + tempTable);
          }
      }
      
      SmartDiscovery sd = new SmartDiscovery();
      SchemaKeyAlgorithm ska;
      List<TableRelationship> listRelTable = new ArrayList<>();
      //First step look for keyword in seeded table     
      tblSeeded = sd.searchKeywordTable(keyword, tblSeeded);   
      System.out.print("Step 1 - Done"); 
      
      //Second step to look for possible relationships with other tables
      for(String tempTable : fileNames){
          String tempTableName = tempTable.split("\\.")[0];
          if(!CommonFunction.stringEquals(tempTableName, seededTable)){
              String fileLocationTemp = filesDir + "\\" + tempTable;
              Table temp = new Table(tempTableName);
              temp.loadFromCSV(fileLocationTemp);
              List<TableRelationship> listRelTemp = sd.searchTableRelationship(tblSeeded, temp);
              ska = new SchemaKeyAlgorithm(listRelTemp);
              TableRelationship relTemp = ska.getSingleJoinKey();
              
              if(relTemp != null) {
                    //Get additional keyword (If exists from the table relationship)
                    String additionalKeywordTemp = sd.getTableContainsKeywords(additionalKeyword.split(","), relTemp.tableTo);
                    relTemp.setAdditionalKeywordFound(additionalKeywordTemp);
                    listRelTable.add(relTemp);
              }
          }
      }
      
      System.out.print("Step 2 - Done");
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
