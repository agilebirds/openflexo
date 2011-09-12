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
package org.openflexo.foundation.dm;

import java.util.Enumeration;

import org.openflexo.foundation.Inspectors;
import org.openflexo.foundation.ie.cl.ComponentDefinition;
import org.openflexo.foundation.xml.FlexoDMBuilder;
import org.openflexo.localization.FlexoLocalization;

/**
 * Represents a logical group of objects representing WO components
 * 
 * @author sguerin
 * 
 */
public class ComponentRepository extends DMRepository
{

    /**
     * Constructor used during deserialization
     */
    public ComponentRepository(FlexoDMBuilder builder)
    {
        this(builder.dmModel);
        initializeDeserialization(builder);
    }

    /**
     * Default constructor
     */
    public ComponentRepository(DMModel dmModel)
    {
        super(dmModel);
    }

    @Override
	public DMRepositoryFolder getRepositoryFolder()
    {
        return getDMModel().getInternalRepositoryFolder();
    }
    
    @Override
	public int getOrder()
    {
        return 3;
    }

    @Override
	public String getName()
    {
        return "component_repository";
    }

    @Override
	public String getLocalizedName()
    {
        return FlexoLocalization.localizedForKey(getName());
    }

    @Override
	public void setName(String name)
    {
        // Not allowed
    }

    /**
     * @param dmModel
     * @return
     */
    public static ComponentRepository createNewComponentRepository(DMModel dmModel)
    {
        ComponentRepository newComponentRepository = new ComponentRepository(dmModel);
        dmModel.setComponentRepository(newComponentRepository);
        return newComponentRepository;
    }

    @Override
	public String getFullyQualifiedName()
    {
        return getDMModel().getFullyQualifiedName() + ".COMPONENTS";
    }

    @Override
	public String getInspectorName()
    {
    	return Inspectors.DM.DM_COMPONENTS_REPOSITORY_INSPECTOR;
    }

    @Override
	public boolean isReadOnly()
    {
        return false;
    }

    @Override
	public boolean isDeletable()
    {
        return false;
    }

    public DMPackage getDefaultComponentPackage()
    {
        return getDefaultPackage();
    }

    public ComponentDMEntity getComponentDMEntity(ComponentDefinition componentDefinition)
    { 
        for (Enumeration en = getPackages().elements(); en.hasMoreElements();) {
            DMPackage next = (DMPackage) en.nextElement();
            DMEntity found = getDMEntity(next.getName(), componentDefinition.getName());
            if (found != null) {
                return (ComponentDMEntity) found;
            }
        }
        return null;
    }
    /**
     * Overrides getClassNameKey
     * @see org.openflexo.foundation.FlexoModelObject#getClassNameKey()
     */
    @Override
	public String getClassNameKey()
    {
        return getName();
    }
    
}
