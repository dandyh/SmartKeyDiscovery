/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.centrica.relationshipalgorithm;

import com.centrica.commonfunction.CommonFunction;
import com.centrica.entity.TableRelationship;
import com.centrica.entity.TableRelationshipDetail;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/**
 *
 * @author dandy
 */
public class SchemaKeyAlgorithm {

    
    List<TableRelationshipDetail> listTableRelationshipDetail;

    public SchemaKeyAlgorithm(List<TableRelationshipDetail> listTableRelationshipDetail) {
        this.listTableRelationshipDetail = listTableRelationshipDetail;
    }

    public TableRelationshipDetail getSingleJoinKey() {
        for (TableRelationshipDetail trTemp : listTableRelationshipDetail) {
            if (CommonFunction.stringEquals(trTemp.getColumnNameFrom(), trTemp.getColumnNameTo())) {
                return trTemp;
            }
        }
        return null;
    }

    //Concatinate relationships that have same column name    
    public List<TableRelationshipDetail> getMultipleJoinKey() {
        //List<TableRelationshipDetail> listTableRelOutput = new ArrayList<>();
        //Get distinct string relationship details
        HashSet<String> hRelNames = new HashSet<String>();
        
        //Algorithm runs here to detect same column type
        List<TableRelationshipDetail> listTableRel = new ArrayList<>();
        for (TableRelationshipDetail trTemp : listTableRelationshipDetail) {
            if (CommonFunction.stringEquals(trTemp.getColumnNameFrom(), trTemp.getColumnNameTo())) {
                listTableRel.add(trTemp);
                hRelNames.add(trTemp.getRelationshipInString(false));
            }
        }
                                
        //Concatinate relationship that have same column from and to
//        for (String strTemp : hRelNames) {            
//            for (TableRelationshipDetail trTemp : listTableRel) {
//                if(strTemp.equals(trTemp.getRelationshipInString())){
//                    if(listTableRelOutput.isEmpty()){
//                        listTableRelOutput.add(trTemp);
//                    }else{
//                        for (TableRelationshipDetail trTempOutput : listTableRelOutput){
//                            if(trTempOutput.getRelationshipInString().equals(strTemp)){
//                                for(int i=0;i<trTemp.tableTo.rows.size();i++){
//                                    trTempOutput.tableTo.rows.add(trTemp.tableTo.rows.get(i));
//                                }                                
//                            }
//                        }
//                    }
//                }
//            }            
//        }
        
        return listTableRel;
    }

}
