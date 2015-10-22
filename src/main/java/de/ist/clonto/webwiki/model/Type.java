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
public class Type extends Element{
    
    private final Set<Type> subtypes;
    private final Set<Entity> entities;

    private Entity mainEntity;

    private int minDepth;
    
    public Type(){
        super();
        subtypes = new HashSet<>();
        entities = new HashSet<>();
    }
    
    public Set<Type> getSubtypes(){
        return Collections.unmodifiableSet(subtypes);
    }
    
    public void addSubtype(Type subtype){
        if(!subtypes.contains(subtype)){
            subtypes.add(subtype);
            subtype.addType(this.getName());
        }
    }
    
    public Set<Entity> getEntities(){
        return Collections.unmodifiableSet(entities);
    }
    
    public void addEntity(Entity entity){
        if(!entities.contains(entity)){
            entities.add(entity);
        }
    }
    
    public Entity getMainEntity() {
        return mainEntity;
    }

    public void setMainEntity(Entity mainEntity) {
        this.mainEntity = mainEntity;
    }
    
    public int getMinDepth() {
        return minDepth;
    }

    public void setMinDepth(int minDepth) {
        if(this.minDepth==0 || this.minDepth> minDepth)
            this.minDepth = minDepth;
    }
}
