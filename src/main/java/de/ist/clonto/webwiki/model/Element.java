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
    
    private final Set<String> supercategories;
    
    public Element(){
        supercategories = new HashSet<>();
    }

    /**
     * @return the supercategories
     */
    public Set<String> getSupercategories() {
        return Collections.unmodifiableSet(supercategories);
    }
    
    public void addSuperCategory(String supercat){
        if(supercat.contains("|"))
            System.out.println(getName()+" flawed with supercategory:"+supercat);
        supercategories.add(supercat);
    }
    
}
