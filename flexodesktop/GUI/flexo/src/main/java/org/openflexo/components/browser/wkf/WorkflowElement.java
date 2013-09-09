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
package org.openflexo.components.browser.wkf;

import java.util.Enumeration;

import javax.swing.Icon;

import org.openflexo.components.browser.BrowserElement;
import org.openflexo.components.browser.BrowserElementType;
import org.openflexo.components.browser.ProjectBrowser;
import org.openflexo.foundation.FlexoException;
import org.openflexo.foundation.wkf.FlexoProcess;
import org.openflexo.foundation.wkf.FlexoProcessNode;
import org.openflexo.foundation.wkf.FlexoWorkflow;
import org.openflexo.foundation.wkf.ProcessFolder;
import org.openflexo.icon.WKFIconLibrary;

/**
 * Browser element representing the workflow
 * 
 * @author sguerin
 * 
 */
public class WorkflowElement extends BrowserElement {

	private static final java.util.logging.Logger logger = org.openflexo.logging.FlexoLogger.getLogger(WorkflowElement.class.getPackage()
			.getName());

	public WorkflowElement(FlexoWorkflow workflow, ProjectBrowser browser, BrowserElement parent) {
		super(workflow, BrowserElementType.WORKFLOW, browser, parent);
	}

	@Override
	protected void buildChildrenVector() {
		// We add the roles
		addToChilds(getFlexoWorkflow().getRoleList());

		/*if (getFlexoWorkflow().getProject().getProjectData() != null) {
			for (FlexoProjectReference ref : getFlexoWorkflow().getProject().getProjectData().getImportedProjects()) {
				if (ref.getReferredProject() != null && ref.getReferredProject().getFlexoWorkflow(false) != null) {
					addToChilds(ref.getReferredProject().getWorkflow());
				}

			}
		}*/

		for (Enumeration<ProcessFolder> en = getFlexoWorkflow().getSortedFolders(); en.hasMoreElements();) {
			ProcessFolder next = en.nextElement();
			addToChilds(next);
		}
		// We add top-level processes
		for (Enumeration<FlexoProcessNode> en = getFlexoWorkflow().getSortedOrphanSubprocesses(); en.hasMoreElements();) {
			FlexoProcess next = en.nextElement().getProcess();
			addToChilds(next);
		}
	}

	@Override
	public Icon getIcon() {
		if (!isRoot() && getParent().getElementType() == BrowserElementType.WORKFLOW) {
			return WKFIconLibrary.IMPORTED_PROCESS_LIBRARY_ICON;
		} else {
			return super.getIcon();
		}
	}

	@Override
	public boolean isNameEditable() {
		return true;
	}

	@Override
	public String getName() {
		return getFlexoWorkflow().getWorkflowName();
	}

	@Override
	public void setName(String aName) throws FlexoException {
		try {
			getFlexoWorkflow().setWorkflowName(aName);
		} catch (Exception e) {
			e.printStackTrace();
			throw new FlexoException(e);
		}
	}

	public FlexoWorkflow getFlexoWorkflow() {
		return (FlexoWorkflow) getObject();
	}

}
