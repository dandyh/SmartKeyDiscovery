/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.centrica.smartkeydiscovery;

import com.centrica.commonfunction.CommonFunction;
import com.centrica.entity.Table;
import com.centrica.entity.TableRelationship;
import static java.lang.Integer.parseInt;
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
}
