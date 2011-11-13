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

import java.util.Collections;
import java.util.Vector;

import javax.swing.tree.TreePath;

import org.openflexo.components.browser.BrowserElement;
import org.openflexo.components.browser.BrowserElementType;
import org.openflexo.components.browser.ExpansionSynchronizedElement;
import org.openflexo.components.browser.ProjectBrowser;
import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.wkf.FlexoImportedProcessLibrary;

/**
 * Browser element representing the workflow
 * 
 * @author sguerin
 * 
 */
public class ImportedProcessLibraryElement extends BrowserElement implements ExpansionSynchronizedElement {

	public ImportedProcessLibraryElement(FlexoImportedProcessLibrary library, ProjectBrowser browser, BrowserElement parent) {
		super(library, BrowserElementType.IMPORTED_PROCESS_LIBRARY, browser, parent);
	}

	@Override
	public TreePath getTreePath() {
		return super.getTreePath();
	}

	@Override
	protected void buildChildrenVector() {
		Vector<FlexoModelObject> processes = new Vector<FlexoModelObject>(getImportedProcessLibrary().getImportedProcesses());
		Collections.sort(processes, FlexoModelObject.NAME_COMPARATOR);
		for (FlexoModelObject process : processes) {
			addToChilds(process);
		}
	}

	@Override
	public String getName() {
		return getObject().getLocalizedClassName();
	}

	protected FlexoImportedProcessLibrary getImportedProcessLibrary() {
		return (FlexoImportedProcessLibrary) getObject();
	}

	@Override
	public void collapse() {

	}

	@Override
	public void expand() {

	}

	@Override
	public boolean isExpanded() {
		return true;
	}

	@Override
	public boolean isExpansionSynchronizedWithData() {
		return true;
	}

	@Override
	public boolean requiresExpansionFor(BrowserElement next) {
		return false;
	}

}
