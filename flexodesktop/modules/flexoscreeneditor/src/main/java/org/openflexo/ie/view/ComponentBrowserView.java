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

import java.util.Enumeration;

import org.openflexo.components.browser.ProjectBrowser;
import org.openflexo.components.browser.dnd.TreeDropTarget;
import org.openflexo.components.browser.view.BrowserView;
import org.openflexo.drm.FCH;
import org.openflexo.foundation.FlexoObject;
import org.openflexo.foundation.ie.IEPageComponent;
import org.openflexo.foundation.ie.widget.IESequenceTab;
import org.openflexo.foundation.ie.widget.IETabWidget;
import org.openflexo.foundation.ie.widget.IEWidget;
import org.openflexo.ie.view.controller.IEController;
import org.openflexo.ie.view.controller.dnd.IETreeDropTarget;

public class ComponentBrowserView extends BrowserView {

	public ComponentBrowserView(IEController controller) {
		super(controller.getComponentBrowser(), controller);
		FCH.setHelpItem(this, "component-browser");
	}

	@Override
	public IEController getController() {
		return (IEController) super.getController();
	}

	@Override
	protected TreeDropTarget createTreeDropTarget(FlexoJTree treeView2, ProjectBrowser _browser2) {
		return new IETreeDropTarget(treeView2, _browser2);
	}

	@Override
	public void treeSingleClick(FlexoObject object) {
		if (object instanceof IETabWidget) {
			if (getController().getCurrentEditedComponent() != null) {
				if (getController().getCurrentEditedComponent().getComponentDefinition().getWOComponent() instanceof IEPageComponent) {
					Enumeration<IEWidget> en = ((IEPageComponent) getController().getCurrentEditedComponent().getComponentDefinition()
							.getWOComponent()).topComponents();
					while (en.hasMoreElements()) {
						IEWidget obj = en.nextElement();
						if (obj instanceof IESequenceTab) {
							((IESequenceTab) obj).setSelectedTab((IETabWidget) object);
						}
					}
				}
			}
		}
	}

	@Override
	public void treeDoubleClick(FlexoObject object) {
	}

}
