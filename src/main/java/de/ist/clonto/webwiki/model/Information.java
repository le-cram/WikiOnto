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
public class Information extends NamedElement{
    
    private final List<Property> properties;

    public Information(){
        properties = new ArrayList<>();
    }

    public List<Property> getProperties(){
        return Collections.unmodifiableList(properties);
    }

    public void addProperty(Property property){
        properties.add(property);
    }
}
