/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.ist.clonto.webwiki.model;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author Marcel
 */
public class Element extends NamedElement{
    
    private final Set<String> types;
    
    public Element(){
        types = new HashSet<>();
    }

    /**
     * @return the types
     */
    public Set<String> getTypes() {
        return Collections.unmodifiableSet(types);
    }
    
    public void addType(String type){
        if(type.contains("|"))
            System.err.println(getName()+" flawed with type:"+type);
        types.add(type);
    }
    
}
