/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.centrica.main;

import com.centrica.commonfunction.CommonFunction;
import com.centrica.entity.Table;
import com.centrica.entity.TableRelationship;
import com.centrica.entity.TableRelationshipDetail;
import com.centrica.relationshipalgorithm.SchemaKeyAlgorithm;
import com.centrica.smartkeydiscovery.SmartDiscovery;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Stack;

/**
 *
 * @author dandy
 */
public class App {

    public static int testing = 2;
    Stack<TableRelationshipDetail> stackTRs = new Stack<>();

    public static void main(String[] args) throws Exception {
        boolean isDestinationFound = false;        
        List<String> listTRComparison = new ArrayList<>();
        List<TableRelationship> listTRException = new ArrayList<>();
        HashSet<String> hashUsedTablenames = new HashSet<String>();
        HashSet<String> hashSeeded = new HashSet<String>();
        SmartDiscovery sd = new SmartDiscovery();
        
        String keyword = "Blauer see delikatessen";
        String seededTableName = "customers";
        String additionalKeyword = "forsterstr. 57,Mannheim";
        String destination = "meat pie";// "Peacock";//"Carnarvon Tigers";//Meat pie";//"laura"; //"0.15";//"Aniseed Syrup";// "Meat pie";
        
        if(testing==1){
            keyword = "laura";
            seededTableName = "employees";
            additionalKeyword = "";
            destination = "Philadelphia";//Where the employee works
        }
        
        if(testing==2){
            keyword = "Tofu";
            seededTableName = "products";
            additionalKeyword = "Luisenstr. 48"; //Shipping address
            destination = "Karin Josephs"; //Customer contact name
        }
               
        if(testing==3){
            keyword = "Sir Rodney's Scones";
            seededTableName = "products";
            additionalKeyword = "rue des Cinquante Otages"; //Ship city
            destination = "Janine Labrune"; //Customer contact name
        }
        //Need to add relationship exception and increase the searchkeyword limit
        
        if(testing==99){
            keyword = "meat pie";
            seededTableName = "products";
            additionalKeyword = "Mannheim";
            destination = "Romero";        
        }
        
        System.out.print("---------------------------------------------------------\n");
        System.out.print("-------------------System Parameters--------------------\n");
        System.out.print("Keyword             : " + keyword + "\n");
        System.out.print("Seed table          : " + seededTableName + "\n");
        System.out.print("Additional Keywords : " + additionalKeyword + "\n");
        System.out.print("Destination         : " + destination + "\n");
        System.out.print("---------------------------------------------------------\n");
        System.out.print("---------------------------------------------------------\n");
        //Load exception TableRelationship exception
        //type here, code here
        String filesDir = CommonFunction.readProperty("relationshipexceptionfile");
        listTRException = sd.loadListTRException(filesDir);
        
        filesDir = "C:\\Users\\dandy\\OneDrive\\Documents\\NetBeansProjects\\SmartKeyDiscovery\\Data\\northwind-mongo-master";
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
        System.out.print("Step 0 - Load the first seeded table - Done\n");

        Stack<TableRelationshipDetail> stackTableRel = new Stack<>();
        while (tblSeeded != null) {            
            SchemaKeyAlgorithm ska;
            List<TableRelationshipDetail> listRelTable = new ArrayList<>();
            
            //First step look for keyword in seeded table     
            System.out.print("Step 1 - Generate new Seeded table with keyword filter\n");
            tblSeeded = sd.searchKeywordTable(keyword, tblSeeded);
            
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
                        //If the table is one of the exception but this seeded table has been used for this relationship before then
                        //This relationship needs to be checked againts the same seeded
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
                    System.out.print("Step 2 - Look for possible relationship between ");
                    System.out.print("Table : " + tblSeeded.getTableName() + " - " + temp.getTableName() + "\n");
                    List<TableRelationshipDetail> listRelTemp = sd.searchTableRelationshipDetail(tblSeeded, temp,
                            listTRException);

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

                            //Only add relationship, if it is not yet exists before
                            if (!sd.isRelationshipdetailsExists(relTemp.getRelationshipInString(true), listTRComparison)) {
                                listRelTable.add(relTemp);

                                hashUsedTablenames.add(relTemp.getTableNameTo());
                                hashSeeded.add(tblSeeded.getTableName() + "," + relTemp.getTableNameTo());
                                                                
                                listTRComparison.add(relTemp.getRelationshipInString(true));

                                //If destination is found
                                if (destinationKeywordTemp != null) {
                                    isDestinationFound = true;
                                    break;
                                }

                                if (relTemp.getColumnNameTo().equals("RegionID")) {
                                    System.out.print("");
                                }                               
                            }

                        }
                    }
                }

            }

            

            //Step 3 - Put relationships into stack with priority of table that have additional keyword and also remove duplicate
            System.out.print("Step 3 - Put the relationships into stack and prioritise based on additional keywords!\n");
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

            System.out.print("Step 4 - End of loop iteration!\n");

        }
        StringBuilder sbTableRel = new StringBuilder("From,To,Keyword,ColumnFrom,ColumnTo,AdditionalKeyword,DestinationKeyword\n");
        LinkedHashSet linkHashTempRelationships = new LinkedHashSet();
        for(String temp : listTRComparison){
            sbTableRel.append(temp.toString() + "\n");
            String[] aTemp = temp.split(",");
            String strTempRel = aTemp[0] + "," + aTemp[1] + "," + aTemp[3] + "," + aTemp[4];
            linkHashTempRelationships.add(strTempRel);
        }
        CommonFunction.generateFile("output/table-relationship-details.csv", sbTableRel.toString(), false);
        
        sbTableRel = new StringBuilder("From,To,ColumnFrom,ColumnTo\n");
        Iterator it = linkHashTempRelationships.iterator();
        while(it.hasNext()){
            sbTableRel.append(it.next().toString() + "\n");
        }
        CommonFunction.generateFile("output/table-relationship-summary.csv", sbTableRel.toString(), false);
        
        System.out.print("Done\n");
        if (isDestinationFound) {
            System.out.println("Destination found!!!!!!!!!!!");
        } else {
            System.out.println("Destination NOT found!");
        }
    }

}
