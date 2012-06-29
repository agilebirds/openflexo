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
package org.openflexo.ie.view;

import org.openflexo.ch.FCH;
import org.openflexo.components.browser.ProjectBrowser;
import org.openflexo.components.browser.dnd.TreeDropTarget;
import org.openflexo.components.browser.view.BrowserView;
import org.openflexo.components.browser.view.BrowserViewCellRenderer;
import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.ie.cl.ComponentDefinition;
import org.openflexo.foundation.ie.cl.ReusableComponentDefinition;
import org.openflexo.ie.view.controller.IEController;
import org.openflexo.ie.view.controller.dnd.IECLTreeDropTarget;

/**
 * @author bmangez
 * 
 *         <B>Class Description</B>
 */
public class ComponentLibraryBrowserView extends BrowserView {

	public ComponentLibraryBrowserView(IEController controller) {
		super(controller.getComponentLibraryBrowser(), controller);
		treeView.setCellRenderer(new BrowserViewCellRenderer());
		FCH.setHelpItem(this, "component-library-browser");
	}

	@Override
	protected TreeDropTarget createTreeDropTarget(FlexoJTree treeView2, ProjectBrowser _browser2) {
		return new IECLTreeDropTarget(treeView2, _browser2);
	}

	@Override
	public void treeSingleClick(FlexoModelObject object) {
	}

	@Override
	public void treeDoubleClick(FlexoModelObject object) {
		if (object instanceof ReusableComponentDefinition) {
			getController().setCurrentEditedObjectAsModuleView(((ReusableComponentDefinition) object).getDummyComponentInstance());
		}
		if (object instanceof ComponentDefinition && !(object instanceof ReusableComponentDefinition)) {
			getController().setCurrentEditedObjectAsModuleView(((ComponentDefinition) object).getDummyComponentInstance());
		}
	}

}
