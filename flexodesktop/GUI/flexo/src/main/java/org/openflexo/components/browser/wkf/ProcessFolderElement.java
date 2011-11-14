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
import org.openflexo.foundation.wkf.FlexoProcessNode;
import org.openflexo.foundation.wkf.ProcessFolder;
import org.openflexo.foundation.wkf.dm.ProcessAddedToFolder;
import org.openflexo.foundation.wkf.dm.ProcessRemovedFromFolder;

/**
 * Browser element representing the workflow
 * 
 * @author sguerin
 * 
 */
public class ProcessFolderElement extends BrowserElement {

	public ProcessFolderElement(ProcessFolder folder, ProjectBrowser browser, BrowserElement parent) {
		super(folder, BrowserElementType.PROCESS_FOLDER, browser, parent);
	}

	@Override
	protected void buildChildrenVector() {
		for (Enumeration<ProcessFolder> en = getFolder().getSortedFolders(); en.hasMoreElements();) {
			addToChilds(en.nextElement());
		}
		for (FlexoProcessNode node : getFolder().getProcesses()) {
			addToChilds(node.getProcess());
		}
	}

	@Override
	public String getName() {
		return getFolder().getName();
	}

	@Override
	public void setName(String aName) throws FlexoException {
		try {
			getFolder().setName(aName);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public boolean isNameEditable() {
		return true;
	}

	public ProcessFolder getFolder() {
		return (ProcessFolder) getObject();
	}

	@Override
	public void update(FlexoObservable observable, DataModification dataModification) {
		if (dataModification instanceof ProcessAddedToFolder || dataModification instanceof ProcessRemovedFromFolder) {
			refreshWhenPossible();
		} else {
			super.update(observable, dataModification);
		}
	}

}
