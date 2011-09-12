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

import org.openflexo.components.browser.BrowserElement;
import org.openflexo.components.browser.BrowserElementType;
import org.openflexo.components.browser.ProjectBrowser;
import org.openflexo.foundation.DataModification;
import org.openflexo.foundation.FlexoException;
import org.openflexo.foundation.FlexoObservable;
import org.openflexo.foundation.rm.ImportedRoleLibraryCreated;
import org.openflexo.foundation.wkf.FlexoProcess;
import org.openflexo.foundation.wkf.FlexoProcessNode;
import org.openflexo.foundation.wkf.FlexoWorkflow;


/**
 * Browser element representing the workflow
 *
 * @author sguerin
 *
 */
public class WorkflowElement extends BrowserElement
{

    public WorkflowElement(FlexoWorkflow workflow, ProjectBrowser browser, BrowserElement parent)
    {
        super(workflow, BrowserElementType.WORKFLOW, browser, parent);
    }

    @Override
	protected void buildChildrenVector()
    {
    	// We add the roles
    	addToChilds(getFlexoWorkflow().getRoleList());
    	if (getFlexoWorkflow().getImportedRoleList()!=null)
    		addToChilds(getFlexoWorkflow().getImportedRoleList());
    	// We add top-level processes
    	for (Enumeration<FlexoProcessNode> en = getFlexoWorkflow().getSortedTopLevelProcesses(); en.hasMoreElements();) {
    		FlexoProcess next = en.nextElement().getProcess();
    		addToChilds(next);
    	}
    }

    @Override
    public boolean isNameEditable() {
    	return true;
    }

    @Override
	public String getName()
    {
        return getFlexoWorkflow().getWorkflowName();
    }

    @Override
    public void setName(String aName) throws FlexoException {
    	getFlexoWorkflow().setWorkflowName(aName);
    }

    public FlexoWorkflow getFlexoWorkflow()
    {
        return (FlexoWorkflow) getObject();
    }

    @Override
    public void update(FlexoObservable observable, DataModification dataModification) {
    	if (dataModification instanceof ImportedRoleLibraryCreated)
    		refreshWhenPossible();
    	super.update(observable, dataModification);
    }

}
