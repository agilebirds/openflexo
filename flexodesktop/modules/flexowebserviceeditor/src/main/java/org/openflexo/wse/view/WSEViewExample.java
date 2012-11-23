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
package org.openflexo.wse.view;

import java.awt.BorderLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import org.openflexo.foundation.DataModification;
import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.FlexoObservable;
import org.openflexo.foundation.GraphicalFlexoObserver;
import org.openflexo.view.ModuleView;
import org.openflexo.view.controller.model.FlexoPerspective;
import org.openflexo.wse.controller.WSEController;

/**
 * Please comment this class
 * 
 * @author yourname
 * 
 */
public class WSEViewExample extends JPanel implements ModuleView, GraphicalFlexoObserver {

	private WSEController _controller;
	private FlexoModelObject _object;

	public WSEViewExample(FlexoModelObject object, WSEController controller) {
		super(new BorderLayout());
		add(new JLabel(object.getFullyQualifiedName(), SwingConstants.CENTER), BorderLayout.CENTER);
		_controller = controller;
		_object = object;
	}

	public WSEController getWSEController() {
		return _controller;
	}

	@Override
	public FlexoModelObject getRepresentedObject() {
		return _object;
	}

	@Override
	public void update(FlexoObservable observable, DataModification dataModification) {
		// TODO: Implements this
	}

	@Override
	public void deleteModuleView() {
		getWSEController().removeModuleView(this);
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
		return false;
	}

	@Override
	public FlexoPerspective getPerspective() {
		return getWSEController().WSE_PERSPECTIVE;
	}

}
