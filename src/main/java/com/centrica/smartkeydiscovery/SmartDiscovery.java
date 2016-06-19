/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.centrica.smartkeydiscovery;

import com.centrica.commonfunction.CommonFunction;
import com.centrica.entity.TableRelationshipDetail;
import com.centrica.entity.Table;
import com.centrica.entity.TableRelationship;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.HashSet;
import static java.lang.Integer.parseInt;

/**
 *
 * @author dandy
 */
public class SmartDiscovery {

    public SmartDiscovery() {
    }

    //Search keyword within table and return the matched rows
    public Table searchKeywordTable(String keyword, Table inputTable) throws Exception {
        int index = 0;
        Table matchedKeywordsTable = new Table(inputTable.getTableName());
        matchedKeywordsTable.columnName = inputTable.columnName;
        for (String[] arrayRecord : inputTable.rows) {
            //Exit loop if number of search is exceed the search limit
            if (index >= parseInt(CommonFunction.readProperty("searchlimit"))) {
                break;
            }

            for (String record : arrayRecord) {
                if (CommonFunction.stringEquals(record, keyword)) {
                    matchedKeywordsTable.rows.add(arrayRecord);
                    index++;
                    break;
                }
            }
        }
        return matchedKeywordsTable;
    }

    //Search additional field within table and return boolean (return TRUE, IF found)
    public HashSet<String> getTableContainsKeywords(String[] keywords, Table inputTable) throws Exception {
        HashSet<String> hashOutput = new HashSet<String>();
        for (String[] arrayRecord : inputTable.rows) {
            for (String record : arrayRecord) {
                for (String keywordTemp : keywords) {
                    if (CommonFunction.getCleanedString(record).contains(CommonFunction.getCleanedString(keywordTemp))) {
                        hashOutput.add(keywordTemp);
                    }
                }
            }
        }
        return hashOutput;
    }

    //Search additional field within table and return boolean (return TRUE, IF found)
    public String getTableContainsDestinationKeyword(String keyword, Table inputTable) throws Exception {
        for (String[] arrayRecord : inputTable.rows) {
            for (String record : arrayRecord) {
                if (CommonFunction.getCleanedString(record).contains(CommonFunction.getCleanedString(keyword))) {
                    return keyword;

                }
            }
        }
        return null;
    }

    //Search keyword within table and return the relationship object (Which contains indexes and the matching rows)
    //is by column means search each keyword column by column else row by row
    public List<TableRelationshipDetail> searchKeywordRelationship(String keyword, String tableFromName, String columnFromName, Table inputTable,
            List<TableRelationship> listTRException, boolean isByColumn) throws Exception {
        int index = 0;
        List<TableRelationshipDetail> rel = new ArrayList<TableRelationshipDetail>();

        if(isByColumn){
            for (int i = 0; i < inputTable.columnName.length; i++) {

                //Skip Relationship checking if it is on the black listed relationship
                if (!this.isRelationshipBlackListed(listTRException, tableFromName, columnFromName,
                        inputTable.getTableName(), inputTable.columnName[i])) {
                    for (int z = 0; z < inputTable.rows.size(); z++) {

                        //Exit loop if number of search is exceed the search limit
                        if (index >= parseInt(CommonFunction.readProperty("searchlimit"))) {
                            return rel;
                        }

                        if (CommonFunction.stringEquals(inputTable.rows.get(z)[i], keyword)) {
                            TableRelationshipDetail relPartial = new TableRelationshipDetail(keyword, inputTable.getTableName(), inputTable.columnName[i], i);
                            relPartial.tableTo = new Table(inputTable.getTableName());
                            relPartial.tableTo.columnName = inputTable.columnName;
                            relPartial.tableTo.rows.add(inputTable.rows.get(z));
                            rel.add(relPartial);
                            index++;
                        }
                        System.out.print(tableFromName + ": " + columnFromName + " -> " + inputTable.getTableName() + ": " + inputTable.columnName[i] + "\n");

                    }
                }
            }
        }else {//Skip Relationship checking if it is on the black listed relationship

            for (int z = 0; z < inputTable.rows.size(); z++) {
                for (int i = 0; i < inputTable.columnName.length; i++) {
                    //Exit loop if number of search is exceed the search limit
                    if (index >= parseInt(CommonFunction.readProperty("searchlimit"))) {
                        return rel;
                    }

                    if (this.isRelationshipBlackListed(listTRException, tableFromName, columnFromName,
                            inputTable.getTableName(), inputTable.columnName[i])) {
                        break;
                    }

                    if (CommonFunction.stringEquals(inputTable.rows.get(z)[i], keyword)) {
                        TableRelationshipDetail relPartial = new TableRelationshipDetail(keyword, inputTable.getTableName(), inputTable.columnName[i], i);
                        relPartial.tableTo = new Table(inputTable.getTableName());
                        relPartial.tableTo.columnName = inputTable.columnName;
                        relPartial.tableTo.rows.add(inputTable.rows.get(z));
                        rel.add(relPartial);
                        index++;
                    }
                    //System.out.print(tableFromName + ": " + columnFromName + " -> " + inputTable.getTableName() + ": " + inputTable.columnName[i] + "\n");

                }

            }
        }
        return rel;
    }

    //Search keyword within table and return the relationship list
    //It should be able to read exception relationship
    public List<TableRelationshipDetail> searchTableRelationshipDetail(Table inputTable, Table compareTable,
            List<TableRelationship> listTRException) throws Exception {
        List<TableRelationshipDetail> listRelOutput = new ArrayList<>();
        int rowIndex = 0;
        //Loop for rows
        for (String[] arrayRecord : inputTable.rows) {
            //Loop for columns
            for (int i = 0; i < arrayRecord.length; i++) {

                List<TableRelationshipDetail> listPartialRel = searchKeywordRelationship(arrayRecord[i], inputTable.getTableName(), inputTable.columnName[i],
                        compareTable, listTRException, Boolean.parseBoolean(CommonFunction.readProperty("searchrelationshipbycolumn")));
                String columnNameFromTemp = inputTable.columnName[i];
                if (listPartialRel.size() > 0) {
                    //Combine all the rows together
                    //But make sure it will only combine rows that have same column from and to
                    //Get all "columnto"
                    List<String> listExlcudeColumnTo = new ArrayList<>();

                    for (TableRelationshipDetail tempRel : listPartialRel) {
                        //Create a combination of relationship tables (To decrease the complexity)
                        if (!listExlcudeColumnTo.contains(tempRel.getColumnNameTo())) {
                            listExlcudeColumnTo.add(tempRel.getColumnNameTo());
                            tempRel.setTableNameFrom(inputTable.getTableName());
                            tempRel.setColumnNameFrom(columnNameFromTemp);
                            tempRel.setColumnIndexFrom(i);
                            tempRel.setPartialRelationship(false);
                            //tempRel.tableTo.columnName = compareTable.columnName;
                            List<String[]> relRows = new ArrayList<>();
                            for (TableRelationshipDetail tblRelTemp : listPartialRel) {
                                if (tempRel.getColumnNameTo() == tblRelTemp.getColumnNameTo()) {
                                    relRows.add(tblRelTemp.tableTo.rows.get(0));
                                }
                            }
                            tempRel.tableTo.rows = relRows;

                            listRelOutput.add(tempRel);
                        }

                    }

                }
            }
        }

        return listRelOutput;
    }

    //This prioritizes relationship without additional keyword 
    //Because we will use stack which will be LIFO (Last In First Out)
    //Also remove duplicate
    public List<TableRelationshipDetail> reorderRelationshipBasedonPriorities(List<TableRelationshipDetail> listTRInput) throws Exception {
        //To remove duplicate
        List<String> listComparator = new ArrayList<>();
        
        List<TableRelationshipDetail> trOutput = new ArrayList<>();
        //Put relationship without additional keywords and destination first (at the bottom)
        for (int i = 0; i < listTRInput.size(); i++) {
            if (listTRInput.get(i).getAdditionalKeywordFound().isEmpty()
                    && CommonFunction.stringIsEmpty(listTRInput.get(i).getDestinationKeywordFound())) {
                
                if(!listComparator.contains(listTRInput.get(i).getRelationshipInString(true))){
                    trOutput.add(listTRInput.get(i));
                    listComparator.add(listTRInput.get(i).getRelationshipInString(true));
                }
            }
        }
        //Put relationship WITH additional keywords later (in the middle)
        for (int i = 0; i < listTRInput.size(); i++) {
            if (!listTRInput.get(i).getAdditionalKeywordFound().isEmpty()
                    && CommonFunction.stringIsEmpty(listTRInput.get(i).getDestinationKeywordFound())) {                
                if(!listComparator.contains(listTRInput.get(i).getRelationshipInString(true))){
                    trOutput.add(listTRInput.get(i));
                    listComparator.add(listTRInput.get(i).getRelationshipInString(true));
                }
            }
        }

        //Put destination Table at the last (On top)
        for (int i = 0; i < listTRInput.size(); i++) {
            if (!CommonFunction.stringIsEmpty(listTRInput.get(i).getDestinationKeywordFound())) {
                if(!listComparator.contains(listTRInput.get(i).getRelationshipInString(true))){
                    trOutput.add(listTRInput.get(i));
                    listComparator.add(listTRInput.get(i).getRelationshipInString(true));
                }
            }
        }

        return trOutput;
    }

    //Check whether the relationship is blacklisted
    public boolean isRelationshipBlackListed(List<TableRelationship> listTRException,
            String tableNameFrom, String columnNameFrom,
            String tableNameTo, String columnNameTo) {
        if (listTRException.isEmpty()) {
            return false;
        }
        for (TableRelationship trTemp : listTRException) {
            if (CommonFunction.stringEquals(trTemp.getTableNameFrom(), tableNameFrom)
                    && CommonFunction.stringEquals(trTemp.getColumnNameFrom(), columnNameFrom)
                    && CommonFunction.stringEquals(trTemp.getTableNameTo(), tableNameTo)
                    && CommonFunction.stringEquals(trTemp.getColumnNameTo(), columnNameTo)) {
                return true;
            }
        }
        return false;
    }

    public List<TableRelationship> loadListTRException(String fileLocation) throws Exception {
        List<TableRelationship> listTRException = new ArrayList<>();
        //Check if file exists
        File f = new File(fileLocation);

        if (!f.exists() || f.isDirectory()) {
            return listTRException;
        }

        //From,To,ColumnFrom,ColumnTo
        String[] columnName;
        BufferedReader br = null;
        String line = "";
        String csvSplitBy = ",";
        boolean isHeader = true;
        try {
            br = new BufferedReader(new FileReader(fileLocation));
            while ((line = br.readLine()) != null) {
                String[] temp = line.split(csvSplitBy);
                if (isHeader) {
                    columnName = temp;
                    isHeader = false;
                } else {
                    if(!line.startsWith("#")){
                        listTRException.add(new TableRelationship(temp[0], temp[2], -1, temp[1], temp[3], -1));
                    }                    
                }
            }
        } catch (IOException e) {
            throw (new Exception("IO Exception"));
        } catch (Exception e) {
            throw (new Exception(e));
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    throw (new Exception("IO Exception"));
                }
            }
        }
        return listTRException;
    }
    
    public boolean isRelationshipdetailsExists(String input, List<String> allTableRelationship){
//        for(TableRelationshipDetail temp : listRelTemp){
//            if(temp.getTableNameFrom().equals(input.getTableNameFrom()) &&
//                    temp.getTableNameTo().equals(input.getTableNameTo()) &&
//                        temp.getColumnNameFrom().equals(input.getColumnNameFrom()) &&
//                            temp.getColumnNameTo().equals(input.getColumnNameTo()) &&
//                                temp.getKeyword().equals(input.getKeyword()) && 
//                                    temp.tableTo.rows.size() == input.tableTo.rows.size()
//                    )
//                return true;
//        }
        if(allTableRelationship.isEmpty()) return false;
        if(allTableRelationship.contains(input)) return true;
        return false;        
    }
}
