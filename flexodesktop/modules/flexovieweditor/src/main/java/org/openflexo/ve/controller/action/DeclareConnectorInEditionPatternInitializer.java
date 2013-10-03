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
package org.openflexo.ve.controller.action;

import java.util.EventObject;
import java.util.logging.Logger;

import javax.swing.Icon;

import org.openflexo.components.widget.CommonFIB;
import org.openflexo.foundation.action.FlexoActionFinalizer;
import org.openflexo.foundation.action.FlexoActionInitializer;
import org.openflexo.foundation.view.diagram.model.DiagramConnector;
import org.openflexo.foundation.view.diagram.model.DiagramElement;
import org.openflexo.foundation.view.diagram.viewpoint.action.DeclareConnectorInEditionPattern;
import org.openflexo.icon.VPMIconLibrary;
import org.openflexo.ve.controller.VEController;
import org.openflexo.view.controller.ActionInitializer;
import org.openflexo.view.controller.ControllerActionInitializer;

public class DeclareConnectorInEditionPatternInitializer extends
		ActionInitializer<DeclareConnectorInEditionPattern, DiagramConnector, DiagramElement> {

	private static final Logger logger = Logger.getLogger(ControllerActionInitializer.class.getPackage().getName());

	DeclareConnectorInEditionPatternInitializer(VEControllerActionInitializer actionInitializer) {
		super(DeclareConnectorInEditionPattern.actionType, actionInitializer);
	}

	@Override
	protected VEControllerActionInitializer getControllerActionInitializer() {
		return (VEControllerActionInitializer) super.getControllerActionInitializer();
	}

	@Override
	public VEController getController() {
		return (VEController) super.getController();
	}

	@Override
	protected FlexoActionInitializer<DeclareConnectorInEditionPattern> getDefaultInitializer() {
		return new FlexoActionInitializer<DeclareConnectorInEditionPattern>() {
			@Override
			public boolean run(EventObject e, DeclareConnectorInEditionPattern action) {
				return instanciateAndShowDialog(action, CommonFIB.DECLARE_CONNECTOR_IN_EDITION_PATTERN_DIALOG_FIB);
			}
		};
	}

	@Override
	protected FlexoActionFinalizer<DeclareConnectorInEditionPattern> getDefaultFinalizer() {
		return new FlexoActionFinalizer<DeclareConnectorInEditionPattern>() {
			@Override
			public boolean run(EventObject e, DeclareConnectorInEditionPattern action) {
				// getController().setCurrentEditedObjectAsModuleView(action.getEditionPattern(), getController().VIEW_POINT_PERSPECTIVE);
				getController().getSelectionManager().setSelectedObject(action.getPatternRole());
				return true;
			}
		};
	}

	@Override
	protected Icon getEnabledIcon() {
		return VPMIconLibrary.EDITION_PATTERN_ICON;
	}
}
