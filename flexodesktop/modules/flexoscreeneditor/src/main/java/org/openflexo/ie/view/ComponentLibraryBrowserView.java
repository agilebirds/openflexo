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

import java.awt.Component;

import javax.swing.JTree;

import org.openflexo.ch.FCH;
import org.openflexo.components.browser.BrowserElement;
import org.openflexo.components.browser.ProjectBrowser;
import org.openflexo.components.browser.dnd.TreeDropTarget;
import org.openflexo.components.browser.view.BrowserView;
import org.openflexo.components.browser.view.BrowserViewCellRenderer;
import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.ie.ComponentInstance;
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

	// ==========================================================================
	// ============================= Variables
	// ==================================
	// ==========================================================================

	protected IEController _controller;

	// ==========================================================================
	// ============================= Constructor
	// ================================
	// ==========================================================================

	public ComponentLibraryBrowserView(IEController controller) {
		super(controller.getComponentLibraryBrowser(), controller.getKeyEventListener(), controller.getEditor());
		_controller = controller;
		treeView.setCellRenderer(new BrowserViewCellRenderer() {
			/**
			 * Overrides getTreeCellRendererComponent
			 * 
			 * @see javax.swing.tree.DefaultTreeCellRenderer#getTreeCellRendererComponent(javax.swing.JTree, java.lang.Object, boolean,
			 *      boolean, boolean, int, boolean)
			 */
			@Override
			public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf, int row,
					boolean hasFocus) {
				super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
				if (_controller.getCurrentModuleView() != null
						&& (((BrowserElement) value).getObject() == _controller.getCurrentModuleView().getRepresentedObject() || _controller
								.getCurrentModuleView().getRepresentedObject() instanceof ComponentInstance
								&& ((ComponentInstance) _controller.getCurrentModuleView().getRepresentedObject()).getComponentDefinition() == ((BrowserElement) value)
										.getObject())) {
					setBackground(getBackgroundSelectionColor());
					setForeground(getTextSelectionColor());
					selected = true;
				}
				return this;
			}
		});
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
			_controller.setSelectedComponent(((ReusableComponentDefinition) object).getDummyComponentInstance());
		}
		if (object instanceof ComponentDefinition && !(object instanceof ReusableComponentDefinition)) {
			_controller.setSelectedComponent(((ComponentDefinition) object).getDummyComponentInstance());
		}/*
			    * else if (object instanceof FlexoComponentFolder){ if
			    * (_controller.getCurrentPerspective()==_controller.EXAMPLE_VALUE_PERSPECTIVE)
			    * _controller.setSelectedFolder((FlexoComponentFolder)object); }
			    */
	}

}
