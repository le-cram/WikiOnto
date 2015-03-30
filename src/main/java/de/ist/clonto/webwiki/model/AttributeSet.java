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
public class AttributeSet extends NamedElement{
    
    private final List<Attribute> attributes;
    
    public AttributeSet(){
        attributes = new ArrayList<>();
    }
    
    public List<Attribute> getAttributes(){
        return Collections.unmodifiableList(attributes);
    }
    
    public void addAttribute(Attribute attribute){
        attributes.add(attribute);
    }
}
