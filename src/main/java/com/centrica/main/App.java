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
import java.util.List;
import java.util.Stack;

/**
 *
 * @author dandy
 */
public class App {

    Stack<TableRelationshipDetail> stackTRs = new Stack<>();

    public static void main(String[] args) throws Exception {
        boolean isDestinationFound = false;
        StringBuilder sbTableRel = new StringBuilder("From,To,Keyword,ColumnFrom,ColumnTo\n");

        List<String> listUsedTablenames = new ArrayList<String>();
        String keyword = "Blauer see delikatessen";
        String seededTableName = "customers";
        String additionalKeyword = "forsterstr. 57,Mannheim";
        String destination = "Laura";// "Peacock";//"Carnarvon Tigers";//Meat pie";//"laura"; //"0.15";//"Aniseed Syrup";// "Meat pie";

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
        System.out.print("Step 1 - Get initial seeded table - Done\n");

        Stack<TableRelationshipDetail> stackTableRel = new Stack<>();
        while (tblSeeded != null) {

            SmartDiscovery sd = new SmartDiscovery();
            SchemaKeyAlgorithm ska;
            List<TableRelationshipDetail> listRelTable = new ArrayList<>();
            //First step look for keyword in seeded table     
            tblSeeded = sd.searchKeywordTable(keyword, tblSeeded);

            System.out.print("Step 1a - Generate keyword table from seed\n");

            //Step 2 - look for possible relationship with other tables
            for (String tempTable : fileNames) {
                String tempTableName = CommonFunction.getFilenameOnly(tempTable);
                //Exclude table that has been used
                if (!listUsedTablenames.contains(tempTableName)) {
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
                            String additionalKeywordTemp = sd.getTableContainsKeywords(additionalKeyword.split(","), relTemp.tableTo);
                            relTemp.setAdditionalKeywordFound(additionalKeywordTemp);

                            listRelTable.add(relTemp);

                            //Exclude used rel table for future
                            listUsedTablenames.add(relTemp.getTableNameTo());

                            if ((relTemp.getTableNameFrom() + relTemp.getTableNameTo()).equals("ordersshippers")) {
                                System.out.print("order-details,products");
                            }                            
                            System.out.print("");
                        }
                    }

                    //TableRelationshipDetail relTemp = ska.getSingleJoinKey();
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
                    sbTableRel.append(relTemp.getTableNameFrom()).append(",").append(relTemp.getTableNameTo()).append(",");
                    sbTableRel.append(relTemp.getKeyword()).append(",").append(relTemp.getColumnNameFrom()).append(",").append(relTemp.getColumnNameTo()).append("\n");

                }
            }

            //Method will check whether the stac is empty or not
            if (!stackTableRel.empty()) {
                TableRelationshipDetail relTemp = stackTableRel.pop();
                tblSeeded = relTemp.tableTo;
                keyword = relTemp.getKeyword();                
            } else {
                tblSeeded = null;
            }

            System.out.print("Step 3 - End of loop iteration!\n");

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
