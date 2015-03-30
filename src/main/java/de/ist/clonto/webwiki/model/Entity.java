/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.ist.clonto.webwiki.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 *
 * @author Marcel
 */
public class Entity extends Element{
    
    private final List<AttributeSet> attributeSetList;

    public Entity(){
        super();
        attributeSetList = new ArrayList<>();
    }
    
    public List<AttributeSet> getAttributeSetList() {
        return Collections.unmodifiableList(attributeSetList);
    }

    public void addAttributeSet(AttributeSet attributeSet) {
        attributeSetList.add(attributeSet);
    }
    
}
