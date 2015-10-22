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
    
    private final List<Information> informationList;

    public Entity(){
        super();
        informationList = new ArrayList<>();
    }
    
    public List<Information> getInformationList() {
        return Collections.unmodifiableList(informationList);
    }

    public void addInformation(Information information) {
        informationList.add(information);
    }
    
}
