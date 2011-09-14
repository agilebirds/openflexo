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
package org.openflexo.foundation.rm;

import java.util.Enumeration;
import java.util.Iterator;
import java.util.Vector;
import java.util.logging.Logger;

import org.openflexo.foundation.FlexoException;
import org.openflexo.xmlcode.XMLSerializable;


/**
 * Represents a list of resources sharing the same relationship, related to a
 * given resource.
 *
 * @author sguerin
 *
 */
public abstract class ResourceList extends Vector<FlexoResource<FlexoResourceData>> implements XMLSerializable
{
    private static final Logger logger = Logger.getLogger(ResourceList.class.getPackage().getName());

    private FlexoResource<FlexoResourceData> relatedResource;

    public ResourceList()
    {
        super();
    }

    public ResourceList(FlexoResource<FlexoResourceData> relatedResource)
    {
        super();
        setRelatedResource(relatedResource);
    }

    /**
     * The resource to which this resource list is connected.
     * @return
     */
    public FlexoResource<FlexoResourceData> getRelatedResource()
    {
        return relatedResource;
    }

    public void setRelatedResource(FlexoResource<FlexoResourceData> relatedResource)
    {
        this.relatedResource = relatedResource;
    }

    public Vector<FlexoResource<FlexoResourceData>> getResources()
    {
        return this;
    }

    public void setResources(Vector<FlexoResource<FlexoResourceData>> aVector)
    {
        removeAllElements();
        for (Enumeration<FlexoResource<FlexoResourceData>> e = aVector.elements(); e.hasMoreElements();) {
            addToResources(e.nextElement());
        }
        if (getRelatedResource() != null) {
            getRelatedResource().getProject().notifyResourceChanged(getRelatedResource());
        }
        update();
    }

    public void addToResources(FlexoResource<FlexoResourceData> resource)
    {
        if (resource.isDeleted())
            return;
        if (!contains(resource)) {
            add(resource);
            if (getRelatedResource() != null) {
            	getRelatedResource().getProject().notifyResourceChanged(getRelatedResource());
            }
            update();
        }
    }

    public void removeFromResources(FlexoResource<FlexoResourceData> resource)
    {
        if (contains(resource)) {
            remove(resource);
            if (getRelatedResource() != null) {
            	getRelatedResource().getProject().notifyResourceChanged(getRelatedResource());
            }
            update();
        }
    }

    public abstract void update();

    /**
     * This method overrides
     *
     * @see java.util.Vector#equals(java.lang.Object) in order to avoid XMLCoDe
     *      to consider an empty vector is equals to an other one, and avoid
     *      misleading references.
     *
     * Overrides
     * @see java.lang.Object#equals(java.lang.Object)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
	public boolean equals(Object obj)
    {
        return (obj == this);
    }

    public void loadAll()
    {
        Enumeration en = elements();
        while(en.hasMoreElements()){
            FlexoResource resource = (FlexoResource)en.nextElement();
            if (resource instanceof FlexoStorageResource) {
                try {
                    ((FlexoStorageResource)resource).loadResourceData();
                } catch (FlexoException e) {
                    // Warns about the exception
                    logger.warning ("Exception raised: "+e.getClass().getName()+". See console for details.");
                    e.printStackTrace();
                }
            }
        }
    }

    public abstract String getSerializationIdentifier();

    @Override
	public Enumeration<FlexoResource<FlexoResourceData>> elements()
    {
    	// If project is serializing (saving RM file, take only resources to be serialized)
    	if (getRelatedResource().getProject().isSerializing()) {
    		Vector<FlexoResource<FlexoResourceData>> returned = new Vector<FlexoResource<FlexoResourceData>>(this);
    		Iterator<FlexoResource<FlexoResourceData>> i = returned.iterator();
    		while(i.hasNext()) {
    			FlexoResource<FlexoResourceData> resource = i.next();
    			if (!resource.isToBeSerialized() || (getRelatedResource().getProject().getFlexoResource()!=null && !getRelatedResource().getProject().getFlexoResource().isInitializingProject() && !resource.checkIntegrity()))
    				i.remove();
    		}
    		return returned.elements();
    	}
    	return super.elements();
    }


}
