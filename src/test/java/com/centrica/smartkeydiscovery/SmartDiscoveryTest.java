/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.centrica.smartkeydiscovery;

import com.centrica.commonfunction.CommonFunction;
import com.centrica.entity.Table;
import com.centrica.entity.TableRelationship;
import com.centrica.relationshipalgorithm.SchemaKeyAlgorithm;
import static java.lang.Integer.parseInt;
import java.util.ArrayList;
import java.util.List;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author dandy
 */
public class SmartDiscoveryTest {
    private Table tbl;
    public SmartDiscoveryTest() throws Exception {
        tbl = new Table("Order");
        String orderFileLocation = "C:\\Users\\dandy\\OneDrive\\Documents\\NetBeansProjects\\SmartKeyDiscovery\\Data\\northwind-mongo-master\\orders.csv";
        tbl.loadFromCSV(orderFileLocation);
    }
    
    @BeforeClass
    public static void setUpClass() throws Exception {
        
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }

    // TODO add test methods here.
    // The methods must be annotated with annotation @Test. For example:
    //
    @Test
    public void searchKeywordTableRowSize() throws Exception {
        SmartDiscovery sd = new SmartDiscovery();
        Table temp = sd.searchKeywordTable("AROUT", tbl);
        assertEquals(parseInt(CommonFunction.readProperty("searchlimit")), temp.rows.size());
    }
    
    
    @Test
    public void searchKeywordTableColumnSize() throws Exception {
        SmartDiscovery sd = new SmartDiscovery();
        Table temp = sd.searchKeywordTable("AROUT", tbl);
        assertEquals(11, temp.columnName.length);
    }
    
    @Test
    public void searchKeywordRelationshipRelCount() throws Exception {
        SmartDiscovery sd = new SmartDiscovery();
        List<TableRelationship> lst = sd.searchKeywordRelationship("AROUT", tbl);
        
        assertEquals(parseInt(CommonFunction.readProperty("searchlimit")), lst.size());
    }
    
    @Test
    public void searchKeywordRelationshipRelRow() throws Exception {
        SmartDiscovery sd = new SmartDiscovery();
        List<TableRelationship> lst = sd.searchKeywordRelationship("AROUT", tbl);
        
        assertEquals(lst.get(0).tableTo.rows.get(0)[10],"Colchester");
    }
    
    @Test
    public void getTableContainsKeywordsDifferentUpperLowerCase() throws Exception {
        SmartDiscovery sd = new SmartDiscovery();
        String[] strArray = {"ARouT", "Dandy"};
        String test = sd.getTableContainsKeywords(strArray, tbl);
        
        assertEquals(test,"ARouT");
    }
    
    @Test
    public void isTableContainsKeywordDifferentNotFound() throws Exception {
        SmartDiscovery sd = new SmartDiscovery();
        String[] strArray = {"ARouT123"};
        String test = sd.getTableContainsKeywords(strArray, tbl);
        
        assertEquals(test,null);
    }
    
    @Test
    public void reorderRelationshipBasedonAdditionalKeywordREORDER() throws Exception {
        String keyword = "Blauer see delikatessen";
        String seededTableName = "customers";
        String additionalKeyword = "forsterstr. 57,Mannheim";
        
        String filesDir = "C:\\Users\\dandy\\OneDrive\\Documents\\NetBeansProjects\\SmartKeyDiscovery\\Data\\northwind-mongo-master";
        String[] fileNames = CommonFunction.getFilenamesInFolder(filesDir);

        //Look for seeded table
        Table tblSeeded = new Table(seededTableName);
        for (String tempTable : fileNames) {
            if (CommonFunction.stringContains(tempTable, seededTableName)) {
                tblSeeded.loadFromCSV(filesDir + "\\" + tempTable);
            }
        }

        SmartDiscovery sd = new SmartDiscovery();
        SchemaKeyAlgorithm ska;
        List<TableRelationship> listRelTable = new ArrayList<>();
        //First step look for keyword in seeded table     
        tblSeeded = sd.searchKeywordTable(keyword, tblSeeded);
        
        Table tblOrders = new Table("orders");
        tblOrders.loadFromCSV(filesDir + "\\" + "orders.csv");
        List<TableRelationship> listRelTemp1 = sd.searchTableRelationship(tblSeeded, tblOrders);
        ska = new SchemaKeyAlgorithm(listRelTemp1);
        TableRelationship relTemp1 = ska.getSingleJoinKey();
        String additionalKeywordTemp = sd.getTableContainsKeywords(additionalKeyword.split(","), relTemp1.tableTo);
        relTemp1.setAdditionalKeywordFound(additionalKeywordTemp);
        
        Table tblSuppliers = new Table("suppliers");
        tblSuppliers.loadFromCSV(filesDir + "\\" + "suppliers.csv");
        List<TableRelationship> listRelTemp2 = sd.searchTableRelationship(tblSeeded, tblSuppliers);
        ska = new SchemaKeyAlgorithm(listRelTemp2);
        TableRelationship relTemp2 = ska.getSingleJoinKey();
        additionalKeywordTemp = sd.getTableContainsKeywords(additionalKeyword.split(","), relTemp2.tableTo);
        relTemp2.setAdditionalKeywordFound(additionalKeywordTemp);
        
        listRelTable = new ArrayList<>();
        listRelTable.add(relTemp1);listRelTable.add(relTemp2);
        List<TableRelationship> listRelFINAL = sd.reorderRelationshipBasedonAdditionalKeyword(listRelTable);
        
        assertEquals(listRelFINAL.get(0).getTableNameTo(),"suppliers");
        assertEquals(listRelFINAL.get(1).getTableNameTo(),"orders");
        
    }
    
}
