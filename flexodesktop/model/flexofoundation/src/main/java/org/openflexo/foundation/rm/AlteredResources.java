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
import java.util.Hashtable;
import java.util.Vector;

import org.openflexo.foundation.rm.FlexoResource.DependancyAlgorithmScheme;


/**
 * Represents all the resources related resource alters (or more exactely, MAY
 * alters). A modification on related resource may modify one or more of altered
 * resources.
 * 
 * @author sguerin
 * 
 */
public class AlteredResources extends ResourceList
{

    public AlteredResources()
    {
        super();
		_resourceIncludingInactive = new Hashtable<DependancyAlgorithmScheme,Vector<FlexoResource<FlexoResourceData>>>();
		_resourceExcludingInactive = new Hashtable<DependancyAlgorithmScheme,Vector<FlexoResource<FlexoResourceData>>>();
   }

    public AlteredResources(FlexoResource relatedResource)
    {
        super(relatedResource);
		_resourceIncludingInactive = new Hashtable<DependancyAlgorithmScheme,Vector<FlexoResource<FlexoResourceData>>>();
		_resourceExcludingInactive = new Hashtable<DependancyAlgorithmScheme,Vector<FlexoResource<FlexoResourceData>>>();
    }

    @Override
	public String getSerializationIdentifier()
    {
        return getRelatedResource().getSerializationIdentifier()+"_AR";
    }
    
    public Enumeration<FlexoResource<FlexoResourceData>> elements(boolean includeInactiveResource, DependancyAlgorithmScheme dependancyScheme) 
    {
    	return getResources(includeInactiveResource,dependancyScheme).elements();
    }

    /** 
     * Clear cache scheme
     */
    @Override
	public void update()
    {
    	for (FlexoResource resource : this) {
    		resource.getDependantResources().update();
    	}
    	_resourceIncludingInactive.clear();
    	_resourceExcludingInactive.clear();
    }
    
    private Vector<FlexoResource<FlexoResourceData>> buildResources(boolean includeInactiveResource, DependancyAlgorithmScheme dependancyScheme) 
    {
    	Vector<FlexoResource<FlexoResourceData>> returned = new Vector<FlexoResource<FlexoResourceData>>();
    	for (FlexoResource<FlexoResourceData> resource : this) {
    		if (includeInactiveResource || resource.isActive()) {
    			if (resource.dependsOf(getRelatedResource(),dependancyScheme)) {
    	  			returned.add(resource);
    			}
    		}
    	}
    	return returned;
    }

    private Hashtable<DependancyAlgorithmScheme,Vector<FlexoResource<FlexoResourceData>>> _resourceIncludingInactive;
    private Hashtable<DependancyAlgorithmScheme,Vector<FlexoResource<FlexoResourceData>>> _resourceExcludingInactive;
    
    /**
     * Return list of resources with supplied options
     * 
     * TAKE CARE that trying to retrieve dependant resources with an optimistic scheme require that
     * an update() was done on this object after the last modifications on the model, because this 
     * method use a cache scheme. To be sure to get the good result in optimist scheme, do:
     * update() then getResources(aBoolean,DependancyAlgorithmScheme.Optimistic).
     * 
     * @param includeInactiveResource
     * @param dependancyScheme
     * @return
     */
    public Vector<FlexoResource<FlexoResourceData>> getResources(boolean includeInactiveResource, DependancyAlgorithmScheme dependancyScheme) 
    {
    	Vector<FlexoResource<FlexoResourceData>> returned = null;
    	if (includeInactiveResource) {
    		returned = _resourceIncludingInactive.get(dependancyScheme);
    		if (returned == null) {
    			returned = buildResources(includeInactiveResource, dependancyScheme);
    			_resourceIncludingInactive.put(dependancyScheme, returned);
    		}
    		return returned;
    	}
    	else {
    		returned = _resourceExcludingInactive.get(dependancyScheme);
    		if (returned == null) {
    			returned = buildResources(includeInactiveResource, dependancyScheme);
    			_resourceExcludingInactive.put(dependancyScheme, returned);
    		}
    		return returned;
    	}
    }

}
