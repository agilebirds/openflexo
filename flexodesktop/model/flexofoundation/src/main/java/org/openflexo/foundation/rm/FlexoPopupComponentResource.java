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

import java.io.Serializable;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openflexo.foundation.ie.IEPopupComponent;
import org.openflexo.foundation.ie.cl.FlexoComponentFolder;
import org.openflexo.foundation.ie.cl.PopupComponentDefinition;
import org.openflexo.foundation.utils.FlexoProjectFile;
import org.openflexo.foundation.xml.FlexoComponentBuilder;


/**
 * Please comment this class
 * 
 * @author sguerin
 * 
 */
public class FlexoPopupComponentResource extends FlexoComponentResource implements Serializable
{
	

	private static final Logger logger = Logger.getLogger(FlexoPopupComponentResource.class.getPackage().getName());

    private PopupComponentDefinition _componentDefinition;

    /**
     * Constructor used for XML Serialization: never try to instanciate resource
     * from this constructor
     * 
     * @param builder
     */
    public FlexoPopupComponentResource(FlexoProjectBuilder builder)
    {
        this(builder.project);
        builder.notifyResourceLoading(this);
   }

    public FlexoPopupComponentResource(FlexoProject aProject)
    {
        super(aProject);
    }

    public FlexoPopupComponentResource(FlexoProject aProject, String aName, FlexoComponentLibraryResource clResource, FlexoProjectFile componentFile) throws InvalidFileNameException
    {
        super(aProject, aName, clResource, componentFile);
    }

    @Override
	public ResourceType getResourceType()
    {
        return ResourceType.POPUP_COMPONENT;
    }

    public IEPopupComponent getIEPopupComponent()
    {
        return (IEPopupComponent) getResourceData();
    }

    /*
     * public FlexoResourceData loadResourceData() throws
     * LoadXMLResourceException, FlexoFileNotFoundException { if
     * (logger.isLoggable(Level.INFO)) logger.info("Loading component
     * "+getName()); IEPopupComponent popupComponent =
     * (IEPopupComponent)super.loadResourceData();
     * popupComponent.setProject(getProject()); return popupComponent; }
     */

    @Override
	public PopupComponentDefinition getComponentDefinition()
    {
        if (_componentDefinition == null) {

            _componentDefinition = (PopupComponentDefinition) getProject().getFlexoComponentLibrary().getComponentNamed(getName());
            if (_componentDefinition == null) {
                if (logger.isLoggable(Level.SEVERE))
                    logger.severe("cant find an PopupComponentDefinition for popup: " + getName() + " in library");
                if (logger.isLoggable(Level.INFO))
                    logger.info("Trying to repair...");
                FlexoComponentFolder rootFolder = getProject().getFlexoComponentLibrary().getRootFolder();
                try {
                    _componentDefinition = new PopupComponentDefinition(getName(), getProject().getFlexoComponentLibrary(), rootFolder, getProject(), false);
                } catch (DuplicateResourceException e) {
                }
            }
        }
        return _componentDefinition;
    }

    public static String resourceIdentifierForName(String aComponentName)
    {
        return ResourceType.POPUP_COMPONENT.getName() + "." + aComponentName;
    }

    @Override
	public Object instanciateNewBuilder()
    {
        Object returned = super.instanciateNewBuilder();
        ((FlexoComponentBuilder) returned).componentDefinition = getComponentDefinition();
        return returned;
    }

}
