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
public class EOAttribute extends EOProperty
{
    private static final Logger logger = FlexoLogger.getLogger(EOAttribute.class.getPackage().getName());

    private static final String COLUMN_NAME_KEY = "columnName";

    private static final String PROTOTYPE_NAME_KEY = "prototypeName";

    private static final String CLASS_NAME_KEY = "className";

    private static final String ALLOWS_NULL_KEY = "allowsNull";

    private static final String WIDTH_KEY = "width";

    private static final String EXTERNAL_TYPE_KEY = "externalType";

    private static final String IS_READ_ONLY_KEY = "isReadOnly";

    private static final String VALUE_TYPE_KEY = "valueType";

    private static final String VALUE_CLASS_NAME_KEY = "valueClassName";

    private String className;

    private String columnName;

    private String valueType;

    private boolean allowsNull;

    private boolean isReadOnly;

    private int width;

    private String externalType;

    private EOAttribute prototype;

    /**
     * Incoming relationships are relationships that have one of their join that
     * has this attribute as a destination attribute
     */
    private Vector<EORelationship> incomingRelationships;

    /**
     * Outgoing relationships are relationships that have one of their join that
     * has this attribute as a source attribute
     */
    private Vector<EORelationship> outgoingRelationships;

    public static EOAttribute createAttributeFromMap(Map<Object, Object> map)
    {
        return createAttributeFromMap(map, null);
    }

    public static EOAttribute createAttributeFromMap(Map<Object, Object> map, EOEntity entity)
    {
        EOAttribute attribute = new EOAttribute();
        attribute.setOriginalMap(map);
        attribute.setName((String) map.get(NAME_KEY));
        attribute.setColumnName((String) map.get(COLUMN_NAME_KEY));
        attribute.setClassName((String) map.get(CLASS_NAME_KEY));
        if (map.get(CLASS_NAME_KEY) == null && map.get(VALUE_CLASS_NAME_KEY) != null)
            attribute.setClassName(getJavaNameForNSClassName((String) map.get(VALUE_CLASS_NAME_KEY)));
        attribute.setValueType((String) map.get(VALUE_TYPE_KEY));
        if (map.get(WIDTH_KEY) != null)
            attribute.setWidth(PListHelper.getInteger(map.get(WIDTH_KEY)));
        else
            attribute.setWidth(0);
        attribute.setExternalType((String) map.get(EXTERNAL_TYPE_KEY));
        if (map.get(ALLOWS_NULL_KEY) != null)
            attribute.setAllowsNull(PListHelper.getBoolean(map.get(ALLOWS_NULL_KEY)));
        else
            attribute.setAllowsNull(true);
        if (map.get(IS_READ_ONLY_KEY) != null)
            attribute.setIsReadOnly(PListHelper.getBoolean(map.get(IS_READ_ONLY_KEY)));
        else
            attribute.setIsReadOnly(false);
        attribute.setEntity(entity);
        if (attribute.getValueType()!=null && attribute.getValueType().trim().length()>0) {
        	if (attribute.getValueType().equals("b")) //Byte
        		attribute.setClassName("java.lang.Byte");
        	else if (attribute.getValueType().equals("s")) //Short
        		attribute.setClassName("java.lang.Short");
        	else if (attribute.getValueType().equals("i")) //Integer
        		attribute.setClassName("java.lang.Integer");
        	else if (attribute.getValueType().equals("l")) //Long
        		attribute.setClassName("java.lang.Long");
        	else if (attribute.getValueType().equals("f")) //Float
        		attribute.setClassName("java.lang.Float");
        	else if (attribute.getValueType().equals("d")) //Double
        		attribute.setClassName("java.lang.Double");
        	else if (attribute.getValueType().equals("B")) //BigDecimal
        		attribute.setClassName("java.math.BigDecimal");
        	else if (attribute.getValueType().equals("c")) //Boolean
        		attribute.setClassName("java.lang.Boolean");
        	else {
        		if (logger.isLoggable(Level.WARNING))
					logger.warning("Unknown valueType: "+attribute.getValueType()+". We will use String");
        		attribute.setClassName("java.lang.String");
        	}
        }
        return attribute;
    }

    public EOAttribute()
    {
        incomingRelationships = new Vector<EORelationship>();
        outgoingRelationships = new Vector<EORelationship>();
        createHashMap();
    }

    public String getClassName()
    {
        return className;
    }

    public void setClassName(String className)
    {
        this.className = className;
    }

    public String getColumnName()
    {
        return columnName;
    }

    public void setColumnName(String columnName)
    {
        this.columnName = columnName;
    }

    public String getValueType()
    {
        return valueType;
    }

    public void setValueType(String valueType)
    {
        this.valueType = valueType;
    }

    public String getExternalType()
    {
        return externalType;
    }

    public void setExternalType(String externalType)
    {
        this.externalType = externalType;
    }

    public boolean getAllowsNull()
    {
        return allowsNull;
    }

    public void setAllowsNull(boolean allowsNull)
    {
        this.allowsNull = allowsNull;
    }

    public boolean getIsReadOnly()
    {
        return isReadOnly;
    }

    public void setIsReadOnly(boolean isReadOnly)
    {
        this.isReadOnly = isReadOnly;
    }

    public EOAttribute getPrototype()
    {
        return prototype;
    }

    public void setPrototype(EOAttribute prototype)
    {
        this.prototype = prototype;
        if (prototype!=null) {
        	this.width=prototype.getWidth();
        }
    }

    public int getWidth()
    {
        if (this.width==0 && prototype!=null) {
            this.width=prototype.getWidth();
        }
        return width;
    }

    public void setWidth(int width)
    {
        this.width = width;
    }

    /**
     * @return
     */
    public boolean getIsPrimaryKey()
    {
        if (getEntity() != null) {
            return getEntity().getPrimaryKeyAttributes().contains(this);
        } else if (logger.isLoggable(Level.WARNING))
            logger.warning("Impossible to determine if attribute is primary key because it has no entity");
        return false;
    }

    public boolean getIsUsedForLocking()
    {
        if (getEntity() != null) {
            return getEntity().getAttributesUsedForLocking().contains(this);
        } else if (logger.isLoggable(Level.WARNING))
            logger.warning("Impossible to determine if attribute is used for locking because it has no entity");
        return false;
    }

    /**
     * Overrides resolveObjects
     * 
     * @see org.openflexo.foundation.dm.eo.model.EOObject#resolveObjects()
     */
    @Override
    protected void resolveObjects()
    {
        String prototype = (String) getOriginalMap().get(PROTOTYPE_NAME_KEY);
        if (prototype != null) {
            EOAttribute a = getEntity().getModel().getPrototypeNamed(prototype);
            if (a != null)
                this.prototype = a;
        }
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

    public void addToOutgoingRelationships(EORelationship relationship)
    {
        if (!outgoingRelationships.contains(relationship))
            outgoingRelationships.add(relationship);
        else if (logger.isLoggable(Level.WARNING))
            logger.warning("Attempt to add twice the same relationship to outgoing relationships of entity " + getName());
    }

    public void removeFromOutgoingRelationships(EORelationship relationship)
    {
        outgoingRelationships.remove(relationship);
    }

    public String getPListRepresentation() 
    {
        return FlexoPropertyListSerialization.getPListRepresentation(getMapRepresentation());
    }
    
    public Map<Object,Object> getMapRepresentation()
    {
        Map<Object, Object> map = new HashMap<Object, Object>();
        if (getName() != null)
            map.put(NAME_KEY, getName());
        else
            map.remove(NAME_KEY);
        if (getColumnName() != null)
            map.put(COLUMN_NAME_KEY, getColumnName());
        else
            map.remove(COLUMN_NAME_KEY);
        if (getClassName() != null)
            map.put(CLASS_NAME_KEY, getClassName());
        else
            map.remove(CLASS_NAME_KEY);
        if (getValueType() != null)
            map.put(VALUE_TYPE_KEY, getValueType());
        else
            map.remove(VALUE_TYPE_KEY);
        if (getWidth() != 0)
            map.put(WIDTH_KEY, PListHelper.getObject(getWidth()));
        else
            map.remove(WIDTH_KEY);
        if (getExternalType() != null)
            map.put(EXTERNAL_TYPE_KEY, getExternalType());
        else
            map.remove(EXTERNAL_TYPE_KEY);
        if (!getAllowsNull())
            map.put(ALLOWS_NULL_KEY, PListHelper.getObject(getAllowsNull()));
        else
            map.remove(ALLOWS_NULL_KEY);
        if (getIsReadOnly())
            map.put(IS_READ_ONLY_KEY, PListHelper.getObject(getIsReadOnly()));
        else
            map.remove(IS_READ_ONLY_KEY);
        if (getPrototype() != null)
            map.put(PROTOTYPE_NAME_KEY, getPrototype().getName());
        else
            map.remove(PROTOTYPE_NAME_KEY);
        return map;
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
        if (getColumnName() != null)
            map.put(COLUMN_NAME_KEY, getColumnName());
        else
            map.remove(COLUMN_NAME_KEY);
        if (getClassName() != null)
            map.put(CLASS_NAME_KEY, getClassName());
        else
            map.remove(CLASS_NAME_KEY);
        if (getValueType() != null)
            map.put(VALUE_TYPE_KEY, getValueType());
        else
            map.remove(VALUE_TYPE_KEY);
        if (getWidth() != 0)
            map.put(WIDTH_KEY, PListHelper.getObject(getWidth()));
        else
            map.remove(WIDTH_KEY);
        if (getExternalType() != null)
            map.put(EXTERNAL_TYPE_KEY, getExternalType());
        else
            map.remove(EXTERNAL_TYPE_KEY);
        if (!getAllowsNull())
            map.put(ALLOWS_NULL_KEY, PListHelper.getObject(getAllowsNull()));
        else
            map.remove(ALLOWS_NULL_KEY);
        if (getIsReadOnly())
            map.put(IS_READ_ONLY_KEY, PListHelper.getObject(getIsReadOnly()));
        else
            map.remove(IS_READ_ONLY_KEY);
        if (getPrototype() != null)
            map.put(PROTOTYPE_NAME_KEY, getPrototype().getName());
        else
            map.remove(PROTOTYPE_NAME_KEY);
    }

    /**
     * Overrides delete
     * 
     * @see org.openflexo.foundation.dm.eo.model.EOObject#delete()
     */
    @SuppressWarnings("unchecked")
    @Override
    public void delete()
    {
        Iterator<EORelationship> i = ((Vector) incomingRelationships.clone()).iterator();
        while (i.hasNext()) {
            EORelationship r = i.next();
            EOJoin j = r.joinWithDestinationAttribute(this);
            try {
            	j.setDestinationAttribute(null);
            } catch (InvalidJoinException e) {
            	//NEVER happen because arg is null
			}
        }

        Iterator<EORelationship> k = ((Vector) outgoingRelationships.clone()).iterator();
        while (k.hasNext()) {
            EORelationship r = k.next();
            EOJoin j = r.joinWithSourceAttribute(this);
            try {
            	j.setSourceAttribute(null);
            } catch (InvalidJoinException e) {
            	//NEVER happen because arg is null
			}
        }
        getEntity().removeAttribute(this);
    }

    /**
     * Overrides clearObjects
     * 
     * @see org.openflexo.foundation.dm.eo.model.EOObject#clearObjects()
     */
    @Override
    protected void clearObjects()
    {
        incomingRelationships.clear();
        outgoingRelationships.clear();
        prototype=null;
    }

    private static String getJavaNameForNSClassName(String nsClassName)
    {
        if (nsClassName.startsWith("NS")) {
            if (nsClassName.equals("NSString"))
                return "java.lang.String";
            if (nsClassName.equals("NSNumber"))
                return "java.lang.Number";
            if (nsClassName.equals("NSDecimalNumber"))
                return "java.math.BigDecimal";
            if (nsClassName.equals("NSCalendarDate"))
                return "java.util.Date";
            if (nsClassName.equals("NSGregorianDate"))
                return "java.util.Date";
            if (nsClassName.equals("NSData"))
                return "com.webobjects.foundation.NSData";
        } else if (nsClassName.equals(""))
            return null;
        return nsClassName;
    }

    public Vector<EORelationship> getIncomingRelationships()
    {
        return incomingRelationships;
    }

    public Vector<EORelationship> getOutgoingRelationships()
    {
        return outgoingRelationships;
    }

    /**
     * @param isPK
     */
    public void setIsPrimaryKey(boolean isPK)
    {
        if (getEntity() == null)
            throw new IllegalStateException("Trying to set a attribute as primary key while the attribute has no entity");
        if (getIsPrimaryKey() != isPK) {
            if (isPK)
                getEntity().getPrimaryKeyAttributes().add(this);
            else
                getEntity().getPrimaryKeyAttributes().remove(this);
        }
    }

    /**
     * @param isUsedForLocking
     */
    public void setIsUsedForLocking(boolean isUsedForLocking)
    {
        if (getEntity() == null)
            throw new IllegalStateException("Trying to set a attribute as an attribute used for locking while the attribute has no entity");
        if (getIsUsedForLocking() != isUsedForLocking) {
            if (isUsedForLocking)
                getEntity().getAttributesUsedForLocking().add(this);
            else
                getEntity().getAttributesUsedForLocking().remove(this);
        }
    }
    
    /**
     * Overrides toString
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        return "EOAttribute "+getName()+" "+getEntity()!=null?getEntity().getName():"no entity";
    }

}
