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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.cayenne.wocompat.PropertyListSerialization;

import org.openflexo.logging.FlexoLogger;

/**
 * @author gpolet
 * 
 */
public class EOEntity extends EOObject
{
    private static final Logger logger = FlexoLogger.getLogger(EOEntity.class.getPackage().getName());

    private static final String ATTRIBUTES_KEY = "attributes";

    private static final String CLASS_NAME_KEY = "className";

    private static final String CLASS_PROPERTIES_KEY = "classProperties";

    private static final String EXTERNAL_NAME_KEY = "externalName";

    private static final String PRIMARY_KEY_ATTRIBUTES_KEY = "primaryKeyAttributes";

    private static final String ATTRIBUTES_USED_FOR_LOCKING_KEY = "attributesUsedForLocking";

    private static final String RELATIONSHIPS_KEY = "relationships";

    private static final String PARENT_KEY = "parent";

    private String className;

    private String externalName;

    private EOEntity parentEntity;

    private EOModel model;

    private File file;

    private List<EOAttribute> attributes;

    private List<EORelationship> relationships;

    private List<EOAttribute> primaryKeyAttributes;

    private List<EOAttribute> attributesUsedForLocking;

    private List<EOProperty> classProperties;

    /**
     * Incoming relationships are relationships that have this entity as their
     * destination entity
     */
    private Vector<EORelationship> incomingRelationships;

    public EOEntity()
    {
        attributes = new Vector<EOAttribute>();
        relationships = new Vector<EORelationship>();
        primaryKeyAttributes = new Vector<EOAttribute>();
        attributesUsedForLocking = new Vector<EOAttribute>();
        classProperties = new Vector<EOProperty>();
        incomingRelationships = new Vector<EORelationship>();
        createHashMap();
    }

    public List<EOAttribute> getAttributes()
    {
        return attributes;
    }

    /**
     * Overrides setName
     * @see org.openflexo.foundation.dm.eo.model.EOObject#setName(java.lang.String)
     */
    @Override
    public void setName(String name) throws IllegalStateException
    {
        if (getModel() != null) {
        	EOEntity e = getModel()._entityNamedIgnoreCase(name);
        	if (e!=null)
        		throw new IllegalStateException("duplicated entity '"+e.getName()+"'");
            if ((getName() == null && name != null) || (getName() != null && !getName().equals(name))) {
                if (getFile() != null && getFile().exists())
                    getModel().addToFilesToDelete(getFile());
                setFile(new File(getModel().getFile(), name + ".plist"));
            }
        }
        super.setName(name);
    }
    
    public void setAttributes(List<EOAttribute> attributes)
    {
        Vector<String> v = new Vector<String>();
        Iterator<EOAttribute> i = attributes.iterator();
        while (i.hasNext()) {
            EOAttribute a = i.next();
            if (a.getName() != null && !v.contains(a.getName()))
                v.add(a.getName());
            else {
                if (logger.isLoggable(Level.WARNING))
                    logger.warning("Removed attribute: " + a);
                i.remove();
            }
        }
        this.attributes = attributes;
    }

    public List<EOAttribute> getAttributesUsedForLocking()
    {
        return attributesUsedForLocking;
    }

    public void setAttributesUsedForLocking(List<EOAttribute> attributesUsedForLocking)
    {
        this.attributesUsedForLocking = attributesUsedForLocking;
    }

    public String getClassName()
    {
        return className;
    }

    public void setClassName(String className)
    {
        this.className = className;
    }

    public String getExternalName()
    {
        return externalName;
    }

    public void setExternalName(String externalName)
    {
        this.externalName = externalName;
    }

    public EOEntity getParentEntity()
    {
        return parentEntity;
    }

    public void setParentEntity(EOEntity parentEntity) throws IllegalArgumentException
    {
        if (parentEntity != null) {
            Iterator<EOAttribute> i = getAttributes().iterator();
            while (i.hasNext()) {
                EOAttribute a = i.next();
                if (parentEntity.propertyNamed(a.getName()) != null)
                    throw new IllegalArgumentException("Property " + a.getName() + " is defined in both entities " + getName() + " and "
                            + parentEntity.getName());
            }
            Iterator<EORelationship> j = getRelationships().iterator();
            while (i.hasNext()) {
                EORelationship r = j.next();
                if (parentEntity.propertyNamed(r.getName()) != null)
                    throw new IllegalArgumentException("Property " + r.getName() + " is defined in both entities " + getName() + " and "
                            + parentEntity.getName());
            }
        }
        this.parentEntity = parentEntity;
    }

    public List<EOAttribute> getPrimaryKeyAttributes()
    {
        return primaryKeyAttributes;
    }

    public void setPrimaryKeyAttributes(List<EOAttribute> primaryKeyAttributes)
    {
        this.primaryKeyAttributes = primaryKeyAttributes;
    }

    public List<EORelationship> getRelationships()
    {
        return relationships;
    }

    public void setRelationships(List<EORelationship> relationships)
    {
        Vector<String> v = new Vector<String>();
        Iterator<EORelationship> i = relationships.iterator();
        while (i.hasNext()) {
            EORelationship r = i.next();
            if (r.getName() != null && !v.contains(r.getName()))
                v.add(r.getName());
            else {
                if (logger.isLoggable(Level.WARNING))
                    logger.warning("Removed relationships: " + r);
                i.remove();
            }
        }
        this.relationships = relationships;
    }

    public List<EOProperty> getClassProperties()
    {
        return classProperties;
    }

    public void setClassProperties(List<EOProperty> classProperties)
    {
        this.classProperties = classProperties;
    }

    public void addAttribute(EOAttribute attribute) throws IllegalArgumentException
    {
        if (attributes.contains(attribute))
            throw new IllegalArgumentException("Attribute " + attribute.getName() + " is already in entity " + getName());
        if (propertyNamed(attribute.getName()) != null)
            throw new IllegalArgumentException("An other attribute named " + attribute.getName() + " already exists in entity " + getName());
        attributes.add(attribute);
        classProperties.add(attribute);
        attribute.setEntity(this);
        getAttributesList().add(attribute.getOriginalMap());
    }

    public void removeAttribute(EOAttribute attribute)
    {
        if (attributes.contains(attribute)) {
            attributes.remove(attribute);
            classProperties.remove(attribute);
            attributesUsedForLocking.remove(attribute);
            primaryKeyAttributes.remove(attribute);
            getAttributesList().remove(attribute.getOriginalMap());
            attribute.setEntity(null);
        }
    }

    public void addRelationship(EORelationship rel) throws IllegalArgumentException
    {
        if (relationships.contains(rel))
            throw new IllegalArgumentException("Relationship " + rel.getName() + " is already in entity " + getName());
        if (propertyNamed(rel.getName()) != null)
            throw new IllegalArgumentException("An other relationship named " + rel.getName() + " already exists in entity " + getName());
        relationships.add(rel);
        classProperties.add(rel);
        rel.setEntity(this);
        getRelationshipsList().add(rel.getOriginalMap());
    }

    public void removeRelationship(EORelationship rel)
    {
        if (relationships.contains(rel)) {
            relationships.remove(rel);
            classProperties.remove(rel);
            getRelationshipsList().remove(rel.getOriginalMap());
            rel.setEntity(null);
        }
    }

    public EOAttribute attributeNamed(String name)
    {
        if (name == null)
            return null;
        Iterator<EOAttribute> i = attributes.iterator();
        while (i.hasNext()) {
            EOAttribute a = i.next();
            if (name.equals(a.getName())) {
                return a;
            }
        }
        return getParentEntity() != null ? getParentEntity().attributeNamed(name) : null;
    }

    /**
     * Returns the first attribute found that has the same name (ignoring case)
     * than <code>name</code>. Using this method is not intended directly for
     * exploring the model but for completion and hints to the end-user
     * 
     * @param name -
     *            the name to match
     * @return the first attribute found that has the same name (ignoring case)
     *         than <code>name</code>
     */
    public EOAttribute attributeNamedIgnoreCase(String name)
    {
        if (name == null)
            return null;
        Iterator<EOAttribute> i = attributes.iterator();
        while (i.hasNext()) {
            EOAttribute a = i.next();
            if (name.equalsIgnoreCase(a.getName())) {
                return a;
            }
        }
        return getParentEntity() != null ? getParentEntity().attributeNamed(name) : null;
    }

    public EORelationship relationshipNamed(String name)
    {
        if (name == null)
            return null;
        Iterator<EORelationship> i = relationships.iterator();
        while (i.hasNext()) {
            EORelationship a = i.next();
            if (name.equals(a.getName())) {
                return a;
            }
        }
        return getParentEntity() != null ? getParentEntity().relationshipNamed(name) : null;
    }

    /**
     * Returns the first relationship found that has the same name (ignoring
     * case) than <code>name</code>. Using this method is not intended
     * directly for exploring the model but for completion and hints to the
     * end-user
     * 
     * @param name -
     *            the name to match
     * @return the first relationship found that has the same name (ignoring case)
     *         than <code>name</code>
     */
    public EORelationship relationshipNamedIgnoreCase(String name)
    {
        if (name == null)
            return null;
        Iterator<EORelationship> i = relationships.iterator();
        while (i.hasNext()) {
            EORelationship a = i.next();
            if (name.equalsIgnoreCase(a.getName())) {
                return a;
            }
        }
        return getParentEntity() != null ? getParentEntity().relationshipNamed(name) : null;
    }

    public EOProperty propertyNamed(String name)
    {
        EOProperty retval = null;
        retval = attributeNamed(name);
        if (retval == null)
            retval = relationshipNamed(name);
        return retval;
    }

    /**
     * Returns the first property found that has the same name (ignoring case)
     * than <code>name</code>. It will first look in the attribute list then
     * in the relationship one. Using this method is not intended directly for
     * exploring the model but for completion and hints to the end-user
     * 
     * @param name -
     *            the name to match
     * @return the first property found that has the same name (ignoring case)
     *         than <code>name</code>
     */
    public EOProperty propertyNamedIgnoreCase(String name)
    {
        EOProperty retval = null;
        retval = attributeNamedIgnoreCase(name);
        if (retval == null)
            retval = relationshipNamedIgnoreCase(name);
        return retval;
    }

    @SuppressWarnings("unchecked")
    public static EOEntity createEntityFromFile(EOModel model, File plistFile) throws FileNotFoundException
    {
        Map<Object, Object> map = (Map<Object, Object>) PropertyListSerialization.propertyListFromFile(plistFile);
        EOEntity entity = new EOEntity();
        entity.setOriginalMap(map);
        entity.setName((String) map.get(NAME_KEY));
        entity.setClassName((String) map.get(CLASS_NAME_KEY));
        entity.setExternalName((String) map.get(EXTERNAL_NAME_KEY));
        entity.setModel(model);
        List<Map<Object, Object>> attributesList = (List<Map<Object, Object>>) map.get(ATTRIBUTES_KEY);
        if (attributesList != null) {
            Iterator<Map<Object, Object>> i = attributesList.iterator();
            List<EOAttribute> attributes = new Vector<EOAttribute>();
            while (i.hasNext()) {
                Map<Object, Object> attributeMap = i.next();
                try {
                    EOAttribute att = EOAttribute.createAttributeFromMap(attributeMap, entity);
                    attributes.add(att);
                } catch (RuntimeException e) {
                    e.printStackTrace();
                    if (logger.isLoggable(Level.WARNING))
                        logger.warning("Removing this attribute from original map: " + attributeMap);
                    i.remove();
                }
            }
            entity.setAttributes(attributes);
        }

        List<Map<Object, Object>> relationshipsList = (List<Map<Object, Object>>) map.get(RELATIONSHIPS_KEY);
        if (relationshipsList != null) {
            List<EORelationship> relationships = new Vector<EORelationship>();
            Iterator<Map<Object, Object>> i = relationshipsList.iterator();
            while (i.hasNext()) {
                Map<Object, Object> relationshipMap = i.next();
                relationships.add(EORelationship.createRelationshipFromMap(relationshipMap, entity));
            }
            entity.setRelationships(relationships);
        }

        List<String> classPropertiesList = (List<String>) map.get(CLASS_PROPERTIES_KEY);
        if (classPropertiesList != null) {
            List<EOProperty> classProperties = new Vector<EOProperty>();
            Iterator<String> i = classPropertiesList.iterator();
            while (i.hasNext()) {
                String classProperty = i.next();
                EOProperty p = null;
                p = entity.propertyNamed(classProperty);
                if (p != null)
                    classProperties.add(p);
                else if (logger.isLoggable(Level.WARNING))
                    logger.warning("Could not find class property named " + classProperty + " in entity named " + entity.getName());
            }
            entity.setClassProperties(classProperties);
        }

        List<String> primaryKeysList = (List<String>) map.get(PRIMARY_KEY_ATTRIBUTES_KEY);
        if (primaryKeysList != null) {
            List<EOAttribute> primaryKeyAttributes = new Vector<EOAttribute>();
            Iterator<String> i = primaryKeysList.iterator();
            while (i.hasNext()) {
                String attribute = i.next();
                EOAttribute a = entity.attributeNamed(attribute);
                if (a != null)
                    primaryKeyAttributes.add(a);
                else {
                    if (logger.isLoggable(Level.WARNING))
                        logger.warning("Could not find primary key named " + attribute + " in entity named " + entity.getName());
                }
            }
            Collections.sort(primaryKeyAttributes, new Comparator<EOAttribute>() {

                @Override
				public int compare(EOAttribute o1, EOAttribute o2)
                {
                    return o1.getName().compareTo(o2.getName());
                }

            });
            entity.setPrimaryKeyAttributes(primaryKeyAttributes);
        }

        List<String> lockList = (List<String>) map.get(ATTRIBUTES_USED_FOR_LOCKING_KEY);
        if (lockList != null) {
            List<EOAttribute> attributesUsedForLocking = new Vector<EOAttribute>();
            Iterator<String> i = lockList.iterator();
            while (i.hasNext()) {
                String attribute = i.next();
                EOAttribute a = entity.attributeNamed(attribute);
                if (a != null)
                    attributesUsedForLocking.add(a);
                else {
                    if (logger.isLoggable(Level.WARNING))
                        logger.warning("Could not find attribute used for locking named " + attribute + " in entity named "
                                + entity.getName());
                }
            }
            entity.setAttributesUsedForLocking(attributesUsedForLocking);
        }
        entity.setFile(plistFile);
        return entity;
    }

    /**
     * @return
     */
    public List<String> getPrimaryKeyAttributeNames()
    {
        Vector<String> v = new Vector<String>();
        Iterator<EOAttribute> i = primaryKeyAttributes.iterator();
        while (i.hasNext()) {
            EOAttribute a = i.next();
            v.add(a.getName());
        }
        return v;
    }

    /**
     * Overrides resolveObjects
     * 
     * @see org.openflexo.foundation.dm.eo.model.EOObject#resolveObjects()
     */
    @Override
    protected void resolveObjects()
    {
        Iterator<EOAttribute> i = new Vector<EOAttribute>(getAttributes()).iterator();
        while (i.hasNext()) {
            EOAttribute a = i.next();
            a.resolveObjects();
        }

        Iterator<EORelationship> j = new Vector<EORelationship>(getRelationships()).iterator();
        while (j.hasNext()) {
            EORelationship r = j.next();
            r.resolveObjects();
        }
        String parent = (String) getOriginalMap().get(PARENT_KEY);
        if (parent != null) {
            EOEntity p = getModel().entityNamed(parent);
            if (p != null) {
                this.parentEntity = p;
            } else {
                getModel().addToMissingEntities(parent);
                if (logger.isLoggable(Level.WARNING))
                    logger.warning("Could not find parent entity named " + parent + " for entity " + getName());
            }
        }
    }

    public EOModel getModel()
    {
        return model;
    }

    public void setModel(EOModel model)
    {
        this.model = model;
    }

    public void addToIncomingRelationships(EORelationship relationship)
    {
        if (!incomingRelationships.contains(relationship))
            incomingRelationships.add(relationship);
        else if (logger.isLoggable(Level.WARNING))
            logger.warning("Attempt to add twice the same relationship to incoming relationships of entity " + getName());
    }

    public void removeFromIncomingRelationships(EORelationship relationship)
    {
        incomingRelationships.remove(relationship);
    }

    public Map<Object, Object> getMapRepresentation()
    {
        Map<Object, Object> map = new HashMap<Object, Object>();
        // Updating attributes
        if (getName() != null)
            map.put(NAME_KEY, getName());
        else
            map.remove(NAME_KEY);
        if (getClassName() != null)
            map.put(CLASS_NAME_KEY, getClassName());
        else
            map.remove(CLASS_NAME_KEY);
        if (getExternalName() != null)
            map.put(EXTERNAL_NAME_KEY, getExternalName());
        else
            map.remove(EXTERNAL_NAME_KEY);

        // Updating parent
        if (getParentEntity() != null)
            map.put(PARENT_KEY, getParentEntity().getName());
        else
            map.remove(PARENT_KEY);

        // Updating attributes
        List<Map<Object, Object>> aList = new Vector<Map<Object, Object>>();
        Iterator<EOAttribute> i = getAttributes().iterator();
        while (i.hasNext()) {
            EOAttribute a = i.next();
            aList.add(a.getMapRepresentation());
        }
        map.put(ATTRIBUTES_KEY, aList);

        // Updating relationships
        List<Map<Object, Object>> rList = new Vector<Map<Object, Object>>();
        Iterator<EORelationship> j = getRelationships().iterator();
        while (j.hasNext()) {
            EORelationship r = j.next();
            rList.add(r.getMapRepresentation());
        }
        map.put(RELATIONSHIPS_KEY, rList);

        // Updating class properties
        Vector<String> v = new Vector<String>();
        Iterator<EOProperty> k = getClassProperties().iterator();
        while (k.hasNext()) {
            EOProperty p = k.next();
            v.add(p.getName());
        }
        map.put(CLASS_PROPERTIES_KEY, v);

        // Updating primary keys
        List<EOAttribute> pk = getPrimaryKeyAttributes();
        Collections.sort(pk, new Comparator<EOAttribute>() {

            @Override
			public int compare(EOAttribute o1, EOAttribute o2)
            {
                return o1.getName().compareTo(o2.getName());
            }

        });
        v = new Vector<String>();
        Iterator<EOAttribute> l = pk.iterator();
        while (l.hasNext()) {
            EOAttribute a = l.next();
            v.add(a.getName());
        }
        map.put(PRIMARY_KEY_ATTRIBUTES_KEY, v);

        // Updating attributes used for locking
        Vector<String> locks = new Vector<String>();
        Iterator<EOAttribute> m = getAttributesUsedForLocking().iterator();
        while (m.hasNext()) {
            EOAttribute a = m.next();
            locks.add(a.getName());
        }
        map.put(ATTRIBUTES_USED_FOR_LOCKING_KEY, locks);
        return map;
    }
    
    public String getPListRepresentation() 
    {
        return FlexoPropertyListSerialization.getPListRepresentation(getMapRepresentation());
    }
    
    /**
     * @param file
     * @throws IOException
     */
    public void writeToFile(File file) throws IOException
    {
        Map<Object, Object> map = getOriginalMap();
        // Updating attributes
        if (getName() != null)
            map.put(NAME_KEY, getName());
        else
            map.remove(NAME_KEY);
        if (getClassName() != null)
            map.put(CLASS_NAME_KEY, getClassName());
        else
            map.remove(CLASS_NAME_KEY);
        if (getExternalName() != null)
            map.put(EXTERNAL_NAME_KEY, getExternalName());
        else
            map.remove(EXTERNAL_NAME_KEY);

        // Updating parent
        if (getParentEntity() != null)
            map.put(PARENT_KEY, getParentEntity().getName());
        else
            map.remove(PARENT_KEY);

        // Updating attributes
        List<Map<Object, Object>> aList = new Vector<Map<Object, Object>>();
        Iterator<EOAttribute> i = getAttributes().iterator();
        while (i.hasNext()) {
            EOAttribute a = i.next();
            a.synchronizeObjectWithOriginalMap();
            aList.add(a.getOriginalMap());
        }
        map.put(ATTRIBUTES_KEY, aList);

        // Updating relationships
        List<Map<Object, Object>> rList = new Vector<Map<Object, Object>>();
        Iterator<EORelationship> j = getRelationships().iterator();
        while (j.hasNext()) {
            EORelationship r = j.next();
            r.synchronizeObjectWithOriginalMap();
            rList.add(r.getOriginalMap());
        }
        map.put(RELATIONSHIPS_KEY, rList);

        // Updating class properties
        Vector<String> v = new Vector<String>();
        Iterator<EOProperty> k = getClassProperties().iterator();
        while (k.hasNext()) {
            EOProperty p = k.next();
            v.add(p.getName());
        }
        map.put(CLASS_PROPERTIES_KEY, v);

        // Updating primary keys
        List<EOAttribute> pk = getPrimaryKeyAttributes();
        Collections.sort(pk, new Comparator<EOAttribute>() {

            @Override
			public int compare(EOAttribute o1, EOAttribute o2)
            {
                return o1.getName().compareTo(o2.getName());
            }

        });
        v = new Vector<String>();
        Iterator<EOAttribute> l = pk.iterator();
        while (l.hasNext()) {
        	EOAttribute a =  l.next();
            v.add(a.getName());
        }
        map.put(PRIMARY_KEY_ATTRIBUTES_KEY, v);

        // Updating attributes used for locking
        Vector<String> locks = new Vector<String>();
        Iterator<EOAttribute> m = getAttributesUsedForLocking().iterator();
        while (m.hasNext()) {
            EOAttribute a = m.next();
            locks.add(a.getName());
        }
        map.put(ATTRIBUTES_USED_FOR_LOCKING_KEY, locks);

        // Serialization
        setFile(new File(file, getName() + ".plist"));
        FlexoPropertyListSerialization.propertyListToFile(getFile(), getOriginalMap());
    }

    /**
     * Overrides delete
     * 
     * @see org.openflexo.foundation.dm.eo.model.EOObject#delete()
     */
    @Override
    public void delete()
    {
        Iterator<EOAttribute> i = new Vector<EOAttribute>(getAttributes()).iterator();
        while (i.hasNext()) {
            EOAttribute a = i.next();
            a.delete();
        }

        Iterator<EORelationship> j = new Vector<EORelationship>(getRelationships()).iterator();
        while (j.hasNext()) {
            EORelationship r = j.next();
            r.delete();
        }

        Iterator<EORelationship> k = new Vector<EORelationship>(incomingRelationships).iterator();
        while (k.hasNext()) {
            EORelationship r = k.next();
            r.setDestinationEntity(null);
        }
        if (model != null) {
            if (getFile() != null)
                model.addToFilesToDelete(getFile());
            model.removeEntity(this);
        }
    }

    public File getFile()
    {
        return file;
    }

    public void setFile(File file)
    {
        this.file = file;
    }

    @SuppressWarnings("unchecked")
    public List<Map<Object, Object>> getAttributesList()
    {
        Map<Object, Object> map = getOriginalMap();
        if (map.get(ATTRIBUTES_KEY) == null)
            map.put(ATTRIBUTES_KEY, new Vector<Map<Object, Object>>());
        return (List<Map<Object, Object>>) map.get(ATTRIBUTES_KEY);
    }

    @SuppressWarnings("unchecked")
    public List<Map<Object, Object>> getRelationshipsList()
    {
        Map<Object, Object> map = getOriginalMap();
        if (map.get(RELATIONSHIPS_KEY) == null)
            map.put(RELATIONSHIPS_KEY, new Vector<Map<Object, Object>>());
        return (List<Map<Object, Object>>) map.get(RELATIONSHIPS_KEY);
    }

    /**
     * Overrides clearObjects
     * 
     * @see org.openflexo.foundation.dm.eo.model.EOObject#clearObjects()
     */
    @Override
    protected void clearObjects()
    {
        parentEntity = null;
        incomingRelationships.clear();
        Iterator<EOAttribute> i = new Vector<EOAttribute>(getAttributes()).iterator();
        while (i.hasNext()) {
            EOAttribute a = i.next();
            a.clearObjects();
        }

        Iterator<EORelationship> j = new Vector<EORelationship>(getRelationships()).iterator();
        while (j.hasNext()) {
            EORelationship r = j.next();
            r.clearObjects();
        }
    }

    /**
     * @param property
     */
    public void removeProperty(EOProperty property)
    {
        if (property instanceof EOAttribute) {
            removeAttribute((EOAttribute) property);
        } else if (property instanceof EORelationship) {
            removeRelationship((EORelationship) property);
        } else if (logger.isLoggable(Level.SEVERE))
            logger.severe("Trying to remove unknown type of EOProperty!!!");
    }

    public Vector<EORelationship> getIncomingRelationships()
    {
        return incomingRelationships;
    }

    public int getPropertiesSize()
    {
        return attributes.size() + relationships.size();
    }
};
