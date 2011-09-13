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
import java.util.Map;

/**
 * @author gpolet
 * 
 */
public abstract class EOObject
{
    protected static final String NAME_KEY = "name";

    private String name;

    private Map<Object, Object> originalMap;

    /**
     * Resolve all links between entities, attributes and relationships.
     * Therefore, this will resolve:
     * <ul>
     * <li> All the destination entities
     * <li> All the source attributes of each relationship
     * <li> All the destination attributes of each relationship
     * <li> All the parent entity for EOEntity
     * </ul>
     * 
     */
    protected abstract void resolveObjects();

    /**
     * Remove all links between entities, attributes and relationships.
     * Therefore, this will remove:
     * <ul>
     * <li> All the destination entities
     * <li> All the source attributes of each relationship
     * <li> All the destination attributes of each relationship
     * <li> All the parent entity for EOEntity
     * </ul>
     */
    protected abstract void clearObjects();

    public abstract void delete();

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    protected void createHashMap()
    {
        originalMap = new HashMap<Object, Object>();
    }
    
    /**
     * Returns the original map used for deserializing this object. If the
     * object is new, the map will be automatically created. The original map is
     * created by the deserialization process and is used as well for
     * serialization.
     * 
     * @return the original map for this object.
     */
    public Map<Object, Object> getOriginalMap()
    {
        return originalMap;
    }

    public void setOriginalMap(Map<Object, Object> originalMap)
    {
        this.originalMap = originalMap;
    }

}
