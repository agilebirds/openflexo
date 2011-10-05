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
package org.openflexo.components.browser;

import org.openflexo.foundation.DataModification;
import org.openflexo.foundation.FlexoObservable;
import org.openflexo.foundation.rm.FlexoProject;
import org.openflexo.foundation.rm.ImportedObjectLibraryDeleted;
import org.openflexo.foundation.rm.ImportedProcessLibraryCreated;
import org.openflexo.foundation.rm.ImportedRoleLibraryCreated;
import org.openflexo.foundation.rm.ResourceAdded;
import org.openflexo.foundation.rm.ResourceRemoved;
import org.openflexo.module.ModuleLoader;


/**
 * Browser element representing the project
 *
 * @author sguerin
 *
 */
public class ProjectElement extends BrowserElement
{

	public ProjectElement(FlexoProject project, ProjectBrowser browser, BrowserElement parent)
	{
		super(project, BrowserElementType.PROJECT, browser, parent);
		project.getFlexoWorkflow().addObserver(this);
	}

	@Override
	public void delete() {
		getProject().getFlexoWorkflow().deleteObserver(this);
		super.delete();
	}

	@Override
	protected void buildChildrenVector()
	{
		addToChilds(getProject().getFlexoWorkflow());
		addToChilds(getProject().getDataModel());
		addToChilds(getProject().getFlexoComponentLibrary());
		addToChilds(getProject().getDKVModel());
		addToChilds(getProject().getFlexoNavigationMenu().getRootMenu());
		addToChilds(getProject().getFlexoWSLibrary());
		if (getProject().getProjectOntology(false)!=null) {
			addToChilds(getProject().getProjectOntology(false));
		}
		if (getProject().getShemaLibrary(false)!=null) {
			addToChilds(getProject().getShemaLibrary(false));
		}
		if (getProject().getImportedProcessLibrary()!=null) {
			addToChilds(getProject().getImportedProcessLibrary());
		}

		if (ModuleLoader.getFlexoResourceCenter(false) != null) {
			addToChilds(ModuleLoader.getFlexoResourceCenter().retrieveBaseOntologyLibrary());
			addToChilds(ModuleLoader.getFlexoResourceCenter().retrieveViewPointLibrary());
		}

		/*if (getProject().getOntologyLibrary(false)!=null)
        	addToChilds(getProject().getOntologyLibrary(false));
        if (getProject().getCalcLibrary(false)!=null)
        	addToChilds(getProject().getCalcLibrary(false));*/

	}

	@Override
	public void update(FlexoObservable observable, DataModification dataModification)
	{
		//logger.info("Project element received "+dataModification);
		if (dataModification instanceof ImportedRoleLibraryCreated
				|| dataModification instanceof ImportedProcessLibraryCreated
				|| dataModification instanceof ImportedObjectLibraryDeleted
				|| dataModification instanceof ResourceAdded
				|| dataModification instanceof ResourceRemoved) {
			refreshWhenPossible();
		} else {
			super.update(observable, dataModification);
		}
	}

	@Override
	public String getName()
	{
		return getProject().getProjectName();
	}

	@Override
	protected FlexoProject getProject()
	{
		return (FlexoProject) getObject();
	}

}
