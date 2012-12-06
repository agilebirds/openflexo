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
package org.openflexo.wkf.view;

import java.util.logging.Logger;

import org.openflexo.ch.FCH;
import org.openflexo.components.browser.view.BrowserView;
import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.wkf.controller.RoleListBrowser;
import org.openflexo.wkf.controller.WKFController;

/**
 * Represents the view for WKF Browser
 * 
 * @author sguerin
 * 
 */
public class RoleListBrowserView extends BrowserView {

	private static final Logger logger = Logger.getLogger(RoleListBrowserView.class.getPackage().getName());

	protected WKFController _controller;

	// ==========================================================================
	// ============================= Constructor
	// ================================
	// ==========================================================================

	public RoleListBrowserView(RoleListBrowser roleListBrowser, WKFController controller) {
		super(roleListBrowser, controller);
		_controller = controller;
		FCH.setHelpItem(this, "rolelist-browser");
	}

	/*
	private class RoleViewModeButton extends JButton implements ActionListener {
		private RoleViewMode viewMode;

		public RoleViewModeButton(RoleViewMode mode) {
			this.viewMode = mode;
			setIcon(viewMode.getIcon());
			setToolTipText(viewMode.getLocalizedName());
			setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
			addActionListener(this);
			if (RoleListBrowserView.this.getBrowser().getRoleViewMode() == viewMode) {
				SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {
						doClick();
					}
				});
			}
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			RoleListBrowserView.this.getBrowser().setRoleViewMode(viewMode);
		}
	}
	*/
	@Override
	public void treeSingleClick(FlexoModelObject object) {
	}

	@Override
	public void treeDoubleClick(FlexoModelObject object) {
	}

}
