/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.centrica.relationshipalgorithm;

import com.centrica.commonfunction.CommonFunction;
import com.centrica.entity.TableRelationship;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author dandy
 */
public class SchemaKeyAlgorithm {

    List<TableRelationship> listTableRelationship;
    
    public SchemaKeyAlgorithm(List<TableRelationship> listTableRelationship) {
        this.listTableRelationship = listTableRelationship;
    }
    
    public TableRelationship getSingleJoinKey(){
        for(TableRelationship trTemp : listTableRelationship){
            if(CommonFunction.stringEquals(trTemp.getColumnNameFrom(), trTemp.getColumnNameTo())){
                return trTemp;
            }
        }
        return null;
    }
    
    public List<TableRelationship> getMultipleJoinKey(){
        List<TableRelationship> listTableRel = new ArrayList<>();
        for(TableRelationship trTemp : listTableRelationship){
            if(CommonFunction.stringEquals(trTemp.getColumnNameFrom(), trTemp.getColumnNameTo())){
                listTableRel.add(trTemp);
            }
        }
        return listTableRel;
    }
    
    
}
