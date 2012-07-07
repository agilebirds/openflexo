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
package org.openflexo.dm.view;

/*
 * MainScrollPane.java
 * Project WorkflowEditor
 * 
 * Created by benoit on Mar 10, 2004
 */

import org.openflexo.dm.view.controller.DMController;
import org.openflexo.view.FlexoMainPane;

/**
 * Represents the main pane where process can be edited
 * 
 * @author benoit,sylvain
 */
public class DMMainPane extends FlexoMainPane {
	public DMMainPane(DMController controller) {
		super(controller);
	}

	public void showDataModelBrowser() {
		showLeftView();
	}

	public void hideDataModelBrowser() {
		hideLeftView();
	}

	@Override
	public DMController getController() {
		return (DMController) super.getController();
	}

}
