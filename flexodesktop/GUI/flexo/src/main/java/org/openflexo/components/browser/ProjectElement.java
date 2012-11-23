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
import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.FlexoObservable;
import org.openflexo.foundation.rm.FlexoProject;
import org.openflexo.foundation.rm.FlexoProjectReference;
import org.openflexo.foundation.rm.ImportedObjectLibraryDeleted;
import org.openflexo.foundation.rm.ImportedProcessLibraryCreated;
import org.openflexo.foundation.rm.ImportedRoleLibraryCreated;
import org.openflexo.foundation.rm.ResourceAdded;
import org.openflexo.foundation.rm.ResourceRemoved;

/**
 * Browser element representing the project
 * 
 * @author sguerin
 * 
 */
public class ProjectElement extends BrowserElement {

	public ProjectElement(FlexoProject project, ProjectBrowser browser, BrowserElement parent) {
		super(project, BrowserElementType.PROJECT, browser, parent);
		if (getProject().getFlexoWorkflow(false) != null) {
			project.getFlexoWorkflow().addObserver(this);
		} else {
			project.addObserver(this);
		}
	}

	@Override
	public void delete() {
		getProject().deleteObserver(this);
		if (getProject().getFlexoWorkflow(false) != null) {
			getProject().getFlexoWorkflow().deleteObserver(this);
		}
		super.delete();
	}

	@Override
	protected void buildChildrenVector() {
		if (getProject().getFlexoWorkflow(false) != null) {
			addToChilds(getProject().getFlexoWorkflow());
		}
		if (getProject().getDataModel(false) != null) {
			addToChilds(getProject().getDataModel());
		}

		if (getProject().getFlexoComponentLibrary(false) != null) {
			addToChilds(getProject().getFlexoComponentLibrary());
		}
		if (getProject().getDKVModel(false) != null) {
			addToChilds(getProject().getDKVModel());
		}
		if (getProject().getFlexoNavigationMenu(false) != null) {
			addToChilds(getProject().getFlexoNavigationMenu().getRootMenu());
		}
		if (getProject().getFlexoWSLibrary(false) != null) {
			addToChilds(getProject().getFlexoWSLibrary());
		}
		if (getProject().getProjectOntology(false) != null) {
			addToChilds((FlexoModelObject) getProject().getProjectOntology(false));
		}
		if (getProject().getShemaLibrary(false) != null) {
			addToChilds(getProject().getShemaLibrary(false));
		}
		if (getProject().getImportedProcessLibrary() != null) {
			addToChilds(getProject().getImportedProcessLibrary());
		}
		if (getProject().getProjectData() != null) {
			for (FlexoProjectReference ref : getProject().getProjectData().getImportedProjects()) {
				addToChilds(ref.getReferredProject());

			}
		}

	}

	@Override
	public void update(FlexoObservable observable, DataModification dataModification) {
		// logger.info("Project element received "+dataModification);
		if (dataModification instanceof ImportedRoleLibraryCreated || dataModification instanceof ImportedProcessLibraryCreated
				|| dataModification instanceof ImportedObjectLibraryDeleted || dataModification instanceof ResourceAdded
				|| dataModification instanceof ResourceRemoved) {
			refreshWhenPossible();
		} else {
			super.update(observable, dataModification);
		}
	}

	@Override
	public String getName() {
		return getProject().getProjectName();
	}

	@Override
	protected FlexoProject getProject() {
		return (FlexoProject) getObject();
	}

}
