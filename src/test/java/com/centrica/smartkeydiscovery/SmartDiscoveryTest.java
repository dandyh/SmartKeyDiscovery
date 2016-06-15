/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.centrica.smartkeydiscovery;

import com.centrica.commonfunction.CommonFunction;
import com.centrica.entity.Table;
import com.centrica.entity.TableRelationshipDetail;
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
import static java.lang.Integer.parseInt;

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
        List<TableRelationshipDetail> lst = sd.searchKeywordRelationship("AROUT", tbl);
        
        assertEquals(parseInt(CommonFunction.readProperty("searchlimit")), lst.size());
    }
    
    @Test
    public void searchKeywordRelationshipRelRow() throws Exception {
        SmartDiscovery sd = new SmartDiscovery();
        List<TableRelationshipDetail> lst = sd.searchKeywordRelationship("AROUT", tbl);
        
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
        List<TableRelationshipDetail> listRelTable = new ArrayList<>();
        //First step look for keyword in seeded table     
        tblSeeded = sd.searchKeywordTable(keyword, tblSeeded);
        
        Table tblOrders = new Table("orders");
        tblOrders.loadFromCSV(filesDir + "\\" + "orders.csv");
        List<TableRelationshipDetail> listRelTemp1 = sd.searchTableRelationshipDetail(tblSeeded, tblOrders);
        ska = new SchemaKeyAlgorithm(listRelTemp1);
        TableRelationshipDetail relTemp1 = ska.getSingleJoinKey();
        String additionalKeywordTemp = sd.getTableContainsKeywords(additionalKeyword.split(","), relTemp1.tableTo);
        relTemp1.setAdditionalKeywordFound(additionalKeywordTemp);
        
        Table tblSuppliers = new Table("suppliers");
        tblSuppliers.loadFromCSV(filesDir + "\\" + "suppliers.csv");
        List<TableRelationshipDetail> listRelTemp2 = sd.searchTableRelationshipDetail(tblSeeded, tblSuppliers);
        ska = new SchemaKeyAlgorithm(listRelTemp2);
        TableRelationshipDetail relTemp2 = ska.getSingleJoinKey();
        additionalKeywordTemp = sd.getTableContainsKeywords(additionalKeyword.split(","), relTemp2.tableTo);
        relTemp2.setAdditionalKeywordFound(additionalKeywordTemp);
        
        listRelTable = new ArrayList<>();
        listRelTable.add(relTemp1);listRelTable.add(relTemp2);
        List<TableRelationshipDetail> listRelFINAL = sd.reorderRelationshipBasedonPriorities(listRelTable);
        
        assertEquals(listRelFINAL.get(0).getTableNameTo(),"suppliers");
        assertEquals(listRelFINAL.get(1).getTableNameTo(),"orders");
        
    }
    
    
    @Test
    public void searchTableRelationshipDetailTableSize() throws Exception {
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
        List<TableRelationshipDetail> listRelTable = new ArrayList<>();
        //First step look for keyword in seeded table     
        tblSeeded = sd.searchKeywordTable(keyword, tblSeeded);
        
        Table tblOrders = new Table("orders");
        tblOrders.loadFromCSV(filesDir + "\\" + "orders.csv");
        
        listRelTable = sd.searchTableRelationshipDetail(tblSeeded, tblOrders);
        
        System.out.print("dada3");
        assertEquals(parseInt(CommonFunction.readProperty("searchlimit")),listRelTable.get(0).tableTo.rows.size());
    }
    
    @Test
     public void searchTableRelationshipDetailContent() throws Exception {
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
        List<TableRelationshipDetail> listRelTable = new ArrayList<>();
        //First step look for keyword in seeded table     
        tblSeeded = sd.searchKeywordTable(keyword, tblSeeded);
        
        Table tblOrders = new Table("orders");
        tblOrders.loadFromCSV(filesDir + "\\" + "orders.csv");
        
        listRelTable = sd.searchTableRelationshipDetail(tblSeeded, tblOrders);
        
        System.out.print("dada3");
        assertEquals("BLAUS",listRelTable.get(0).getKeyword());
        assertEquals("Blauer See Delikatessen",listRelTable.get(1).getKeyword());
        assertEquals(parseInt(CommonFunction.readProperty("searchlimit")),listRelTable.get(1).tableTo.rows.size());
        assertEquals("Mannheim",listRelTable.get(3).getKeyword());
    }
     
     @Test
    public void searchTableRelationshipDetailKeywordJoinMultipleColumn() throws Exception {
        Table a = new Table("employee");
        a.columnName = new String[] {"EmpID","Name","City","Country","WorkLocation"};
        a.rows.add(new String[] {"1001","Dandy","Jakarta","Indonesia","UK"});
        a.rows.add(new String[] {"1002","John","London","UK","UK"});
        a.rows.add(new String[] {"1002","John","London","UK","UK"});
    
    }
    
    
}
