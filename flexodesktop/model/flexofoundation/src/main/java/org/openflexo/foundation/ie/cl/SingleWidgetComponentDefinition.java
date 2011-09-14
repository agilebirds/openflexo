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
package org.openflexo.foundation.ie.cl;

import java.io.Serializable;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openflexo.foundation.ie.IEWOComponent;
import org.openflexo.foundation.rm.DuplicateResourceException;
import org.openflexo.foundation.rm.FlexoComponentResource;
import org.openflexo.foundation.rm.FlexoProject;
import org.openflexo.foundation.rm.FlexoReusableComponentResource;
import org.openflexo.foundation.xml.FlexoComponentLibraryBuilder;


/**
 * @author bmangez
 * <B>Class Description</B>
 * @deprecated use ReusableComponent instead
 */
@Deprecated
public class SingleWidgetComponentDefinition extends PartialComponentDefinition implements Serializable
{
    private static final Logger logger = Logger.getLogger(TabComponentDefinition.class.getPackage().getName());

    /**
     * Constructor used during deserialization
     * 
     * @throws DuplicateResourceException
     */
    public SingleWidgetComponentDefinition(FlexoComponentLibraryBuilder builder) throws DuplicateResourceException
    {
        this(null, builder.componentLibrary, null, builder.getProject());
        initializeDeserialization(builder);
    }

    public SingleWidgetComponentDefinition(FlexoComponentLibrary componentLibrary)
    {
        super(componentLibrary);
    }

    public SingleWidgetComponentDefinition(String aComponentName, FlexoComponentLibrary componentLibrary, FlexoComponentFolder aFolder, FlexoProject prj,
            boolean checkUnicity) throws DuplicateResourceException
    {
        super(aComponentName, componentLibrary, aFolder, prj);
        if (checkUnicity) {
            String resourceIdentifier = FlexoReusableComponentResource.resourceIdentifierForName(aComponentName);
            if ((aFolder != null) && (aFolder.getProject() != null) && (aFolder.getProject().isRegistered(resourceIdentifier))) {
                aFolder.removeFromComponents(this);
                throw new DuplicateResourceException(resourceIdentifier);
            }
        }
    }

    public SingleWidgetComponentDefinition(String aComponentName, FlexoComponentLibrary componentLibrary, FlexoComponentFolder aFolder, FlexoProject prj)
            throws DuplicateResourceException
    {
        this(aComponentName, componentLibrary, aFolder, prj, true);
    }

    /**
     * Overrides getClassNameKey
     * @see org.openflexo.foundation.FlexoModelObject#getClassNameKey()
     */
    @Override
	public String getClassNameKey()
    {
        return "single_widget_component_definition";
    }

	@Override
	public IEWOComponent createNewComponent() {
		if (logger.isLoggable(Level.SEVERE))
			logger.severe("SingleWigetComponentDefinition is deprecated");
		return null;
	}

	@Override
	public FlexoComponentResource getComponentResource(boolean createIfNotExists) {
		if (logger.isLoggable(Level.SEVERE))
			logger.severe("SingleWigetComponentDefinition is deprecated");
		return null;
	}

	@Override
	public String getInspectorName() {
		if (logger.isLoggable(Level.SEVERE))
			logger.severe("SingleWigetComponentDefinition is deprecated");
		return null;
	}
    
}
