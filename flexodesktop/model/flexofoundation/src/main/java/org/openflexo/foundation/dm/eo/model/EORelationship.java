/*
 * (c) Copyright 2010-2011 AgileBirds
 *
 * This file is part of OpenFlexo.
 *
 * OpenFlexo is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * OpenFlexo is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with OpenFlexo. If not, see <http://www.gnu.org/licenses/>.
 *
 */
package org.openflexo.foundation.dm.eo.model;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;


import org.openflexo.logging.FlexoLogger;
import org.openflexo.toolbox.PListHelper;

/**
 * @author gpolet
 * 
 */
public class EORelationship extends EOProperty
{
    public static final String InnerJoin = "EOInnerJoin";

    public static final String FullOuterJoin = "EOFullOuterJoin";

    public static final String LeftOuterJoin = "EOLeftOuterJoin";

    public static final String RightOuterJoin = "EORightOuterJoin";

    private static final Logger logger = FlexoLogger.getLogger(EORelationship.class.getPackage().getName());

    private static final String DELETE_RULE_KEY = "deleteRule";

    private static final String DEFINITION_KEY = "definition";

    private static final String DESTINATION_KEY = "destination";

    private static final String IS_MANDATORY_KEY = "isMandatory";

    private static final String IS_TO_MANY_KEY = "isToMany";

    private static final String JOIN_SEMANTIC = "joinSemantic";

    private static final String JOINS = "joins";

    private static final String NR_TO_MANY_FAULTS_TO_BATCH_FETCH_KEY = "numberOfToManyFaultsToBatchFetch";

    private static final String OWNS_DESTINATION_KEY = "ownsDestination";

    private static final String PROPAGATES_PRIMARY_KEY = "propagatesPrimaryKey";

    private String definition;

    private String deleteRule;

    private EOEntity destinationEntity;

    private String joinSemantic;

    private boolean isMandatory;

    private boolean isToMany;

    private boolean ownsDestination;

    private boolean propagatesPrimaryKey;

    private int numberOfToManyFaultsToBatchFetch;

    private List<EOAttribute> destinationAttributes;

    private List<EOAttribute> sourceAttributes;

    private List<EOJoin> joins;

    public static EORelationship createRelationshipFromMap(Map<Object, Object> map)
    {
        return createRelationshipFromMap(map, null);
    }

    @SuppressWarnings("unchecked")
    public static EORelationship createRelationshipFromMap(Map<Object, Object> map, EOEntity entity)
    {
        EORelationship relationship = new EORelationship(map);
        relationship.setName((String) map.get(NAME_KEY));
        relationship.setDeleteRule((String) map.get(DELETE_RULE_KEY));
        if (map.get(IS_MANDATORY_KEY) != null)
            relationship.setIsMandatory(PListHelper.getBoolean(map.get(IS_MANDATORY_KEY)));
        else
            relationship.setIsMandatory(false);
        if (map.get(IS_TO_MANY_KEY) != null)
            relationship.setIsToMany(PListHelper.getBoolean(map.get(IS_TO_MANY_KEY)));
        else
            relationship.setIsToMany(false);
        if (map.get(OWNS_DESTINATION_KEY) != null)
            relationship.setOwnsDestination(PListHelper.getBoolean(map.get(OWNS_DESTINATION_KEY)));
        else
            relationship.setOwnsDestination(false);
        if (map.get(PROPAGATES_PRIMARY_KEY) != null)
            relationship.setPropagatesPrimaryKey(PListHelper.getBoolean(map.get(PROPAGATES_PRIMARY_KEY)));
        else
            relationship.setPropagatesPrimaryKey(false);
        if (map.get(NR_TO_MANY_FAULTS_TO_BATCH_FETCH_KEY) != null)
            relationship.setNumberOfToManyFaultsToBatchFetch(PListHelper.getInteger(map.get(NR_TO_MANY_FAULTS_TO_BATCH_FETCH_KEY)));
        else
            relationship.setNumberOfToManyFaultsToBatchFetch(0);
        relationship.setJoinSemantic((String) map.get(JOIN_SEMANTIC));
        relationship.setEntity(entity);
        relationship.setDefinition((String) map.get(DEFINITION_KEY));
        List<Map<Object, Object>> list = (List<Map<Object, Object>>) map.get(JOINS);
        if (list != null) {
            List<EOJoin> joins = new Vector<EOJoin>();
            Iterator<Map<Object, Object>> i = list.iterator();
            while (i.hasNext()) {
                Map<Object, Object> m = i.next();
                joins.add(EOJoin.createJoinFromMap(m, relationship));
            }
            relationship.setJoins(joins);
        }
        return relationship;
    }

    public EORelationship()
    {
        this(null);
    }

    public EORelationship(Map<Object, Object> map)
    {
        joins = new Vector<EOJoin>();
        destinationAttributes = new Vector<EOAttribute>();
        sourceAttributes = new Vector<EOAttribute>();
        joinSemantic = InnerJoin;
        if (map == null)
            createHashMap();
        else
            setOriginalMap(map);
        if (getOriginalMap().get(JOINS) == null)
            getOriginalMap().put(JOINS, new Vector<Map<Object, Object>>());
    }

    public EOJoin joinWithSourceAttribute(EOAttribute attribute)
    {
        Iterator<EOJoin> i = getJoins().iterator();
        while (i.hasNext()) {
            EOJoin join = i.next();
            if (attribute.equals(join.getSourceAttribute()))
                return join;
        }
        return null;
    }

    public EOJoin joinWithDestinationAttribute(EOAttribute attribute)
    {
        Iterator<EOJoin> i = getJoins().iterator();
        while (i.hasNext()) {
            EOJoin join = i.next();
            if (attribute.equals(join.getDestinationAttribute()))
                return join;
        }
        return null;
    }

    public String getDefinition()
    {
        return definition;
    }

    public void setDefinition(String definition)
    {
        this.definition = definition;
        if(definition!=null){
        	setJoinSemantic(InnerJoin);
        	setJoins(new Vector<EOJoin>());
        	setDestinationEntity(null);
        }
    }

    public String getDeleteRule()
    {
        return deleteRule;
    }

    public void setDeleteRule(String deleteRule)
    {
        this.deleteRule = deleteRule;
    }

    public List<EOAttribute> getDestinationAttributes()
    {
        return destinationAttributes;
    }

    public void setDestinationAttributes(List<EOAttribute> destinationAttributes)
    {
        this.destinationAttributes = destinationAttributes;
    }

    public EOEntity getDestinationEntity()
    {
        if (getIsFlattened()) {
            String[] s = getDefinition().split("\\.");
            EOEntity e = getEntity();
            for (int i = 0; i < s.length; i++) {
                String string = s[i];
                EORelationship r = e.relationshipNamed(string);
                if (r != null) {
                    e = r.getDestinationEntity();
                    if (e == null) {
                        if (logger.isLoggable(Level.WARNING))
                            logger.warning("Destination entity for relationship '" + string + "' is null in entity "
                                    + r.getEntity().getName());
                        return null;
                    }
                } else {
                    if (logger.isLoggable(Level.WARNING))
                        logger.warning("Could not find relationship '" + string + "' on entity " + e.getName()
                                + " in relation definition: " + getDefinition());
                    return null;
                }

                if (i + 1 == s.length)
                    return e;
            }
            return null;
        }
        return destinationEntity;
    }

    public void setDestinationEntity(EOEntity destinationEntity)
    {
        if (this.destinationEntity != destinationEntity) {
            if (this.destinationEntity != null) {
                Iterator<EOJoin> i = getJoins().iterator();
                while (i.hasNext()) {
                    EOJoin j = i.next();
                    if (j.getDestinationAttribute() != null)
                        try{
                        	j.setDestinationAttribute(null);
                        }catch (InvalidJoinException e) {
                        	//NEVER APPEND because arg is null
            			}
                }
                this.destinationEntity.removeFromIncomingRelationships(this);
            }
            if (getDestinationAttributes().size() != 0) {
                if (logger.isLoggable(Level.WARNING))
                    logger.warning("All destinations attributes have not been properly removed. I will clear them now.");
                getDestinationAttributes().clear();
            }
            this.destinationEntity = destinationEntity;
            if (destinationEntity != null) {
                destinationEntity.addToIncomingRelationships(this);
                getOriginalMap().put(DESTINATION_KEY, destinationEntity.getName());
            } else {
                getOriginalMap().remove(DESTINATION_KEY);
            }
        }
    }

    public boolean getIsMandatory()
    {
        return isMandatory;
    }

    public void setIsMandatory(boolean isMandatory)
    {
        this.isMandatory = isMandatory;
    }

    public boolean getIsToMany()
    {
        return isToMany;
    }

    public void setIsToMany(boolean isToMany)
    {
        this.isToMany = isToMany;
    }

    public List<EOJoin> getJoins()
    {
        return joins;
    }

    public void setJoins(List<EOJoin> joins)
    {
        if (this.joins!=null) {
            Iterator<EOJoin> i = this.joins.iterator();
            while (i.hasNext()) {
                EOJoin join = i.next();
                getJoinsList().remove(join.getOriginalMap());
            }
        }
        this.joins = joins;
    }

    public String getJoinSemantic()
    {
        return joinSemantic;
    }

    public void setJoinSemantic(String joinSemantic)
    {
        this.joinSemantic = joinSemantic;
    }

    public int getNumberOfToManyFaultsToBatchFetch()
    {
        return numberOfToManyFaultsToBatchFetch;
    }

    public void setNumberOfToManyFaultsToBatchFetch(int numberOfToManyFaultsToBatchFetch)
    {
        this.numberOfToManyFaultsToBatchFetch = numberOfToManyFaultsToBatchFetch;
    }

    public boolean getOwnsDestination()
    {
        return ownsDestination;
    }

    public void setOwnsDestination(boolean ownsDestination)
    {
        this.ownsDestination = ownsDestination;
    }

    public boolean getPropagatesPrimaryKey()
    {
        return propagatesPrimaryKey;
    }

    public void setPropagatesPrimaryKey(boolean propagatesPrimaryKey)
    {
        this.propagatesPrimaryKey = propagatesPrimaryKey;
    }

    public List<EOAttribute> getSourceAttributes()
    {
        return sourceAttributes;
    }

    public void setSourceAttributes(List<EOAttribute> sourceAttributes)
    {
        this.sourceAttributes = sourceAttributes;
    }

    public void addToSourceAttributes(EOAttribute attribute)
    {
        if (!sourceAttributes.contains(attribute)) {
            sourceAttributes.add(attribute);
            attribute.addToOutgoingRelationships(this);
        } else if (logger.isLoggable(Level.WARNING))
            logger.warning("Attempt to insert twice the same attribute: " + attribute.getName()
                    + " to sources attributes of relationshipd named " + getName());
    }

    public void removeFromSourceAttributes(EOAttribute attribute)
    {
        sourceAttributes.remove(attribute);
        attribute.removeFromOutgoingRelationships(this);
    }

    public void addToDestinationAttributes(EOAttribute attribute)
    {
        if (!destinationAttributes.contains(attribute)) {
            destinationAttributes.add(attribute);
            attribute.addToIncomingRelationships(this);
        } else if (logger.isLoggable(Level.WARNING))
            logger.warning("Attempt to insert twice the same attribute: " + attribute.getName()
                    + " to destination attributes of relationshipd named " + getName());
    }

    public void removeFromDestinationAttributes(EOAttribute attribute)
    {
        destinationAttributes.remove(attribute);
        attribute.removeFromIncomingRelationships(this);
    }

    public boolean getIsFlattened()
    {
        return getDefinition() != null && getDefinition().indexOf('.') > -1;
    }

    public void addJoin(EOJoin join)
    {
        // logger.info("addJoin() "+join);
        if (joins.contains(join))
            throw new IllegalArgumentException("The join of " + join.getSourceAttribute().getName() + " and "
                    + join.getDestinationAttribute().getName() + " is already in relationship " + getName());
        if (joinWithSourceAttribute(join.getSourceAttribute()) != null)
            throw new IllegalArgumentException("Another join with source attribute " + join.getSourceAttribute().getName()
                    + " already exists in relationship " + getName());
        if (joinWithDestinationAttribute(join.getSourceAttribute()) != null)
            throw new IllegalArgumentException("Another join with destination attribute " + join.getDestinationAttribute().getName()
                    + " already exists in relationship " + getName());
        joins.add(join);
        join.setRelationship(this);
        getJoinsList().add(join.getOriginalMap());
    }

    public void removeJoin(EOJoin join)
    {
        // logger.info("removeJoin() "+join);
        if (join.getSourceAttribute() != null)
            removeFromSourceAttributes(join.getSourceAttribute());
        if (join.getDestinationAttribute() != null)
            removeFromDestinationAttributes(join.getDestinationAttribute());
        joins.remove(join);
        getJoinsList().remove(join.getOriginalMap());
    }

    /**
     * Overrides resolveObjects
     * 
     * @see org.openflexo.foundation.dm.eo.model.EOObject#resolveObjects()
     */
    @Override
    protected void resolveObjects()
    {
        String dest = (String) getOriginalMap().get(DESTINATION_KEY);
        if (dest != null) {
            EOEntity destination = getEntity().getModel().entityNamed(dest);
            if (destination != null) {
                setDestinationEntity(destination);
            } else {
                getEntity().getModel().addToMissingEntities(dest);
                if (logger.isLoggable(Level.WARNING))
                    logger.warning("Could not resolve destination entity named: " + dest);
            }
        }
        Iterator<EOJoin> i = getJoins().iterator();
        while (i.hasNext()) {
            EOJoin j = i.next();
            j.resolveObjects();
        }
    }

    /**
     * Updates the original map so that it matches the in-memory model
     */
    public void synchronizeObjectWithOriginalMap()
    {
        Map<Object, Object> map = getOriginalMap();
        if (getName() != null)
            map.put(NAME_KEY, getName());
        else
            map.remove(NAME_KEY);
        if (getDeleteRule() != null)
            map.put(DELETE_RULE_KEY, getDeleteRule());
        else
            map.remove(DELETE_RULE_KEY);
        if (getIsMandatory())
            map.put(IS_MANDATORY_KEY, PListHelper.getObject(getIsMandatory()));
        else
            map.remove(IS_MANDATORY_KEY);
        map.put(IS_TO_MANY_KEY, PListHelper.getObject(getIsToMany()));
        if (getOwnsDestination())
            map.put(OWNS_DESTINATION_KEY, PListHelper.getObject(getOwnsDestination()));
        else
            map.remove(OWNS_DESTINATION_KEY);
        if (getPropagatesPrimaryKey())
            map.put(PROPAGATES_PRIMARY_KEY, PListHelper.getObject(getPropagatesPrimaryKey()));
        else
            map.remove(PROPAGATES_PRIMARY_KEY);
        if (getNumberOfToManyFaultsToBatchFetch() != 0)
            map.put(NR_TO_MANY_FAULTS_TO_BATCH_FETCH_KEY, PListHelper.getObject(getNumberOfToManyFaultsToBatchFetch()));
        else
            map.remove(NR_TO_MANY_FAULTS_TO_BATCH_FETCH_KEY);
        if (getJoinSemantic() != null)
            map.put(JOIN_SEMANTIC, getJoinSemantic());
        else
            map.remove(JOIN_SEMANTIC);
        if (getDefinition() != null)
            map.put(DEFINITION_KEY, getDefinition());
        else
            map.remove(DEFINITION_KEY);
        if (getDestinationEntity() != null && !getIsFlattened()) {
            map.put(DESTINATION_KEY, getDestinationEntity().getName());
        } else
            map.remove(DESTINATION_KEY);
        Iterator<EOJoin> i = getJoins().iterator();
        while (i.hasNext()) {
            EOJoin j = i.next();
            j.synchronizeObjectWithOriginalMap();
        }
    }

    /**
     * Overrides delete
     * 
     * @see org.openflexo.foundation.dm.eo.model.EOObject#delete()
     */
    @Override
    public void delete()
    {
        if (getDestinationEntity() != null)
            getDestinationEntity().removeFromIncomingRelationships(this);
        Iterator<EOAttribute> i = getSourceAttributes().iterator();
        while (i.hasNext()) {
            EOAttribute att = i.next();
            att.removeFromOutgoingRelationships(this);
        }
        Iterator<EOAttribute> j = getDestinationAttributes().iterator();
        while (j.hasNext()) {
            EOAttribute att = j.next();
            att.removeFromIncomingRelationships(this);
        }
        getEntity().removeRelationship(this);
    }

    @SuppressWarnings("unchecked")
    public List<Map<Object, Object>> getJoinsList()
    {
        if (getOriginalMap().get(JOINS) == null)
            getOriginalMap().put(JOINS, new Vector<Map<Object, Object>>());
        return (List<Map<Object, Object>>) getOriginalMap().get(JOINS);
    }

    /**
     * Overrides clearObjects
     * 
     * @see org.openflexo.foundation.dm.eo.model.EOObject#clearObjects()
     */
    @Override
    protected void clearObjects()
    {
        Iterator<EOJoin> i = getJoins().iterator();
        while (i.hasNext()) {
            EOJoin j = i.next();
            j.clearObjects();
        }
        sourceAttributes.clear();
        destinationAttributes.clear();
        destinationEntity = null;
    }

    /**
     * Overrides toString
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        return "EORelationship " + getName() + " " + getEntity() != null ? getEntity().getName() : "no entity";
    }

    public String getPListRepresentation() 
    {
        return FlexoPropertyListSerialization.getPListRepresentation(getMapRepresentation());
    }
    
    /**
     * @return
     */
    public Map<Object, Object> getMapRepresentation()
    {
        Map<Object, Object> map = new HashMap<Object, Object>();
        if (getName() != null)
            map.put(NAME_KEY, getName());
        else
            map.remove(NAME_KEY);
        if (getDeleteRule() != null)
            map.put(DELETE_RULE_KEY, getDeleteRule());
        else
            map.remove(DELETE_RULE_KEY);
        if (getIsMandatory())
            map.put(IS_MANDATORY_KEY, PListHelper.getObject(getIsMandatory()));
        else
            map.remove(IS_MANDATORY_KEY);
        map.put(IS_TO_MANY_KEY, PListHelper.getObject(getIsToMany()));
        if (getOwnsDestination())
            map.put(OWNS_DESTINATION_KEY, PListHelper.getObject(getOwnsDestination()));
        else
            map.remove(OWNS_DESTINATION_KEY);
        if (getPropagatesPrimaryKey())
            map.put(PROPAGATES_PRIMARY_KEY, PListHelper.getObject(getPropagatesPrimaryKey()));
        else
            map.remove(PROPAGATES_PRIMARY_KEY);
        if (getNumberOfToManyFaultsToBatchFetch() != 0)
            map.put(NR_TO_MANY_FAULTS_TO_BATCH_FETCH_KEY, PListHelper.getObject(getNumberOfToManyFaultsToBatchFetch()));
        else
            map.remove(NR_TO_MANY_FAULTS_TO_BATCH_FETCH_KEY);
        if (getJoinSemantic() != null)
            map.put(JOIN_SEMANTIC, getJoinSemantic());
        else
            map.remove(JOIN_SEMANTIC);
        if (getDefinition() != null)
            map.put(DEFINITION_KEY, getDefinition());
        else
            map.remove(DEFINITION_KEY);
        if (getDestinationEntity() != null && !getIsFlattened()) {
            map.put(DESTINATION_KEY, getDestinationEntity().getName());
        } else
            map.remove(DESTINATION_KEY);
        List<Map<Object, Object>> jList = new Vector<Map<Object, Object>>();
        Iterator<EOJoin> i = getJoins().iterator();
        while (i.hasNext()) {
            EOJoin j = i.next();
            jList.add(j.getMapRepresentation());
        }
        map.put(JOINS, jList);
        return map;
    }

}
