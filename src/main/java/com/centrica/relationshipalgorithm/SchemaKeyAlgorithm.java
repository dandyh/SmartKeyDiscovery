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

    public List<TableRelationshipDetail> getMultipleJoinKey() {
        List<TableRelationshipDetail> listTableRel = new ArrayList<>();
        for (TableRelationshipDetail trTemp : listTableRelationshipDetail) {
            if (CommonFunction.stringEquals(trTemp.getColumnNameFrom(), trTemp.getColumnNameTo())) {
                listTableRel.add(trTemp);
            }
        }
        return listTableRel;
    }

}
