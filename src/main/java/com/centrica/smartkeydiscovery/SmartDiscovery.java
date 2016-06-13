/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.centrica.smartkeydiscovery;

import com.centrica.commonfunction.CommonFunction;
import com.centrica.entity.TableRelationship;
import com.centrica.entity.Table;
import java.util.ArrayList;
import java.util.List;
import static java.lang.Integer.parseInt;
import java.util.HashSet;
import java.util.Set;

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
    public String getTableContainsKeywords(String[] keywords, Table inputTable) throws Exception {
        for (String[] arrayRecord : inputTable.rows) {
            for (String record : arrayRecord) {
                for (String keywordTemp : keywords) {
                    if (CommonFunction.getCleanedString(record).contains(CommonFunction.getCleanedString(keywordTemp))) {
                        return keywordTemp;
                    }
                }
            }
        }
        return null;
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
    public List<TableRelationship> searchKeywordRelationship(String keyword, Table inputTable) throws Exception {
        int index = 0;
        List<TableRelationship> rel = new ArrayList<TableRelationship>();

        for (int z = 0; z < inputTable.rows.size(); z++) {

            //Exit loop if number of search is exceed the search limit
            if (index >= parseInt(CommonFunction.readProperty("searchlimit"))) {
                break;
            }

            for (int i = 0; i < inputTable.columnName.length; i++) {
                if (CommonFunction.stringEquals(inputTable.rows.get(z)[i], keyword)) {
                    TableRelationship relPartial = new TableRelationship(keyword, inputTable.getTableName(), inputTable.columnName[i], i);
                    relPartial.tableTo = new Table(inputTable.getTableName());
                    relPartial.tableTo.columnName = inputTable.columnName;
                    relPartial.tableTo.rows.add(inputTable.rows.get(z));
                    rel.add(relPartial);
                    index++;
                }
            }
        }
        return rel;
    }

    //Search keyword within table and return the relationship list
    public List<TableRelationship> searchTableRelationship(Table inputTable, Table compareTable) throws Exception {
        List<TableRelationship> listRel = new ArrayList<>();
        int rowIndex = 0;
        for (String[] arrayRecord : inputTable.rows) {
            for (int i = 0; i < arrayRecord.length; i++) {
                List<TableRelationship> listPartialRel = searchKeywordRelationship(arrayRecord[i], compareTable);
                String columnNameFromTemp = inputTable.columnName[i];
                if (listPartialRel.size() > 0) {
                    //Combine all the rows together
                    //But make sure it will only combine rows that have same column from and to
                    //Get all "columnto"
                    List<String> listExlcudeColumnTo = new ArrayList<>();

                    for (TableRelationship tempRel : listPartialRel) {
                        //Create a combination of relationship tables (To decrease the complexity)
                        if (!listExlcudeColumnTo.contains(tempRel.getColumnNameTo())) {
                            listExlcudeColumnTo.add(tempRel.getColumnNameTo());
                            tempRel.setTableNameFrom(inputTable.getTableName());
                            tempRel.setColumnNameFrom(columnNameFromTemp);
                            tempRel.setColumnIndexFrom(i);
                            tempRel.setPartialRelationship(false);
                            //tempRel.tableTo.columnName = compareTable.columnName;
                            List<String[]> relRows = new ArrayList<>();
                            for (TableRelationship tblRelTemp : listPartialRel) {
                                if (tempRel.getColumnNameTo() == tblRelTemp.getColumnNameTo()) {
                                    relRows.add(tblRelTemp.tableTo.rows.get(0));
                                }
                            }
                            tempRel.tableTo.rows = relRows;

                            listRel.add(tempRel);
                        }

                    }

                }
            }
        }
        
        //Combine listRel that has same column from and to
//        List<TableRelationship> listRelOutput = new ArrayList<>();
//        List<String> listExlcudeColumnFromTo = new ArrayList<>();
//        
//        for (TableRelationship tempRel : listRel) {
//             if (!listExlcudeColumnFromTo.contains(tempRel.getColumnNameTo())) {
//                listExlcudeColumnFromTo.add(tempRel.getColumnNameFrom()+tempRel.getColumnNameTo());
//                List<String[]> relRows = new ArrayList<>();
//                for (TableRelationship tempRelInside : listRel) {
//                    if((tempRelInside.getColumnNameFrom()+tempRelInside.getColumnNameTo()).equals(tempRel.getColumnNameFrom()+tempRel.getColumnNameTo())){
//                        for(String[] tempRow : tempRelInside.tableTo.rows){
//                            relRows.add(tempRow);
//                        }
//                    }
//                }
//                tempRel.tableTo.rows = relRows;                
//                listRelOutput.add(tempRel);
//             }
//        }
        
        return listRel;
    }

    //This prioritizes relationship without additional keyword 
    //Because we will use stack which will be LIFO (Last In First Out)
    public List<TableRelationship> reorderRelationshipBasedonAdditionalKeyword(List<TableRelationship> listTRInput) throws Exception {
        List<TableRelationship> trOutput = new ArrayList<>();
        //Put relationship without additional keywords and destination first
        for (int i = 0; i < listTRInput.size(); i++) {
            if (CommonFunction.stringIsEmpty(listTRInput.get(i).getAdditionalKeywordFound())
                    && CommonFunction.stringIsEmpty(listTRInput.get(i).getDestinationKeywordFound())) {
                trOutput.add(listTRInput.get(i));
            }
        }
        //Put relationship WITH additional keywords later
        for (int i = 0; i < listTRInput.size(); i++) {
            if (!CommonFunction.stringIsEmpty(listTRInput.get(i).getAdditionalKeywordFound())
                    && CommonFunction.stringIsEmpty(listTRInput.get(i).getDestinationKeywordFound())) {
                trOutput.add(listTRInput.get(i));
            }
        }

        //Put destination Table at the last
        for (int i = 0; i < listTRInput.size(); i++) {
            if (!CommonFunction.stringIsEmpty(listTRInput.get(i).getDestinationKeywordFound())) {
                trOutput.add(listTRInput.get(i));
            }
        }

        return trOutput;
    }

}
