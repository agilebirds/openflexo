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
package org.openflexo.view;

import javax.swing.JPanel;

import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.view.controller.FlexoController;
import org.openflexo.view.controller.model.FlexoPerspective;

public class EmptyPanel<O extends FlexoModelObject> extends JPanel implements ModuleView<O> {

	private FlexoController controller;
	private FlexoPerspective perspective;
	private O representedObject;
	private String title = "";

	/**
     * 
     */
	public EmptyPanel(FlexoController controller, FlexoPerspective perspective, O representedObject) {
		this.controller = controller;
		this.perspective = perspective;
		this.representedObject = representedObject;
	}

	@Override
	public O getRepresentedObject() {
		return representedObject;
	}

	@Override
	public void deleteModuleView() {
		if (controller != null) {
			controller.removeModuleView(this);
		}
	}

	@Override
	public FlexoPerspective getPerspective() {
		return perspective;
	}

	public FlexoController getController() {
		return controller;
	}

	/**
	 * Overrides willShow
	 * 
	 * @see org.openflexo.view.ModuleView#willShow()
	 */
	@Override
	public void willShow() {
		// TODO Auto-generated method stub

	}

	/**
	 * Overrides willHide
	 * 
	 * @see org.openflexo.view.ModuleView#willHide()
	 */
	@Override
	public void willHide() {
		// TODO Auto-generated method stub

	}

	/**
	 * Returns flag indicating if this view is itself responsible for scroll management When not, Flexo will manage it's own scrollbar for
	 * you
	 * 
	 * @return
	 */
	@Override
	public boolean isAutoscrolled() {
		return true;
	}

	public String getTitle() {
		if (title == null) {
			return FlexoLocalization.localizedForKey(getRepresentedObject().getClassNameKey());
		}
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

}
