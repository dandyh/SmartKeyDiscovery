/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.centrica.relationshipalgorithm;

import com.centrica.entity.Table;
import com.centrica.entity.TableRelationship;
import com.centrica.entity.TableRelationshipDetail;
import com.centrica.smartkeydiscovery.SmartDiscovery;
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
public class SchemaKeyAlgorithmTest {
    
    Table tblOrders = new Table("Orders");
    Table tblCustomers = new Table("Customers");
    
    public SchemaKeyAlgorithmTest() throws Exception {
        String customerFileLocation = "C:\\Users\\dandy\\OneDrive\\Documents\\NetBeansProjects\\SmartKeyDiscovery\\Data\\northwind-mongo-master\\customers.csv";        
        tblCustomers.loadFromCSV(customerFileLocation);
        
        String orderFileLocation = "C:\\Users\\dandy\\OneDrive\\Documents\\NetBeansProjects\\SmartKeyDiscovery\\Data\\northwind-mongo-master\\orders.csv";
        tblOrders.loadFromCSV(orderFileLocation);
    }
    
    @BeforeClass
    public static void setUpClass() {
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
    public void getSingleJoinKeyGetCorrectColumnJoin() throws Exception {        
        SmartDiscovery sd = new SmartDiscovery();
        Table tempCustomer = sd.searchKeywordTable("BLAUS", tblCustomers); 
        List<TableRelationshipDetail> temp = sd.searchTableRelationshipDetail(tempCustomer, tblOrders, 
                new ArrayList<TableRelationship>());
        SchemaKeyAlgorithm ska = new SchemaKeyAlgorithm(temp);
        assertEquals("CustomerID",ska.getSingleJoinKey().getColumnNameTo());
    }
    
    @Test
    public void getSingleJoinKeyGetCorrectTableJoin() throws Exception {
        
        SmartDiscovery sd = new SmartDiscovery();
        Table tempCustomer = sd.searchKeywordTable("BLAUS", tblCustomers); 
        List<TableRelationshipDetail> temp = sd.searchTableRelationshipDetail(tempCustomer, tblOrders, 
                new ArrayList<TableRelationship>());
        SchemaKeyAlgorithm ska = new SchemaKeyAlgorithm(temp);
        assertEquals(temp.get(0).tableTo,ska.getSingleJoinKey().tableTo);
    }
    
}
