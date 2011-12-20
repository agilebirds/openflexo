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
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openflexo.foundation.ie.menu.FlexoItemMenu;
import org.openflexo.foundation.ie.menu.FlexoNavigationMenu;
import org.openflexo.foundation.utils.FlexoProgress;
import org.openflexo.foundation.utils.FlexoProjectFile;
import org.openflexo.foundation.utils.ProjectLoadingCancelledException;
import org.openflexo.foundation.utils.ProjectLoadingHandler;
import org.openflexo.foundation.xml.FlexoNavigationMenuBuilder;

/**
 * Represents the navigation menu resource
 * 
 * @author sguerin
 */
public class FlexoNavigationMenuResource extends FlexoXMLStorageResource<FlexoNavigationMenu> {

	private static final Logger logger = Logger.getLogger(FlexoNavigationMenuResource.class.getPackage().getName());

	/**
	 * Constructor used for XML Serialization: never try to instanciate resource from this constructor
	 * 
	 * @param builder
	 * @throws InvalidFileNameException
	 */
	public FlexoNavigationMenuResource(FlexoProjectBuilder builder) throws InvalidFileNameException {
		this(builder.project);
		builder.notifyResourceLoading(this);
	}

	public FlexoNavigationMenuResource(FlexoProject aProject) throws InvalidFileNameException {
		this(aProject, new FlexoProjectFile(ProjectRestructuration.getExpectedNavigationMenuFile(aProject), aProject));
	}

	public FlexoNavigationMenuResource(FlexoProject aProject, FlexoProjectFile navigationMenuFile) throws InvalidFileNameException {
		super(aProject);
		setResourceFile(navigationMenuFile);
	}

	public FlexoNavigationMenuResource(FlexoProject aProject, FlexoNavigationMenu menu, FlexoProjectFile menuFile)
			throws InvalidFileNameException {
		this(aProject, menuFile);
		_resourceData = menu;
		try {
			menu.setFlexoResource(this);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public ResourceType getResourceType() {
		return ResourceType.NAVIGATION_MENU;
	}

	@Override
	public String getName() {
		return getProject().getProjectName();
	}

	@Override
	public Class getResourceDataClass() {
		return FlexoNavigationMenu.class;
	}

	@Override
	public FlexoNavigationMenu performLoadResourceData(FlexoProgress progress, ProjectLoadingHandler loadingHandler)
			throws LoadXMLResourceException, ProjectLoadingCancelledException, MalformedXMLException {
		FlexoNavigationMenu menu;
		if (logger.isLoggable(Level.FINE)) {
			logger.fine("loadResourceData() in FlexoNavigationMenuResource");
		}
		try {
			menu = super.performLoadResourceData(progress, loadingHandler);
		} catch (FlexoFileNotFoundException e) {
			if (logger.isLoggable(Level.SEVERE)) {
				logger.severe("File " + getFile().getName() + " NOT found");
			}
			e.printStackTrace();
			return null;
		}
		menu.setProject(getProject());
		return menu;
	}

	@Override
	public boolean hasBuilder() {
		return true;
	}

	/**
	 * Returns the required newly instancied builder if this resource needs a builder to be loaded
	 * 
	 * @return boolean
	 */
	@Override
	public Object instanciateNewBuilder() {
		FlexoNavigationMenuBuilder builder = new FlexoNavigationMenuBuilder(this);
		builder.navigationMenu = _resourceData;
		return builder;
	}

	/**
	 * Rebuild resource dependancies for this resource
	 */
	@Override
	public void rebuildDependancies() {
		super.rebuildDependancies();
		FlexoItemMenu menu = getResourceData().getRootMenu();
		rebuildDependancyForMenu(menu);
	}

	/**
	 * @param menu
	 */
	private void rebuildDependancyForMenu(FlexoItemMenu menu) {
		if (menu.getProcess() != null) {
			addToDependentResources(menu.getProcess().getFlexoResource());
		}
		Enumeration en = menu.getSubItems().elements();
		while (en.hasMoreElements()) {
			FlexoItemMenu element = (FlexoItemMenu) en.nextElement();
			rebuildDependancyForMenu(element);
		}
	}

	@Override
	protected boolean isDuplicateSerializationIdentifierRepairable() {
		return false;
	}

	@Override
	protected boolean repairDuplicateSerializationIdentifier() {
		return false;
	}

}
