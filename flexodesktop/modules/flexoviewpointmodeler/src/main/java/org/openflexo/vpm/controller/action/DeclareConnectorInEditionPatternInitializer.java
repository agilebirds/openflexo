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
package org.openflexo.vpm.controller.action;

import java.util.EventObject;
import java.util.logging.Logger;

import javax.swing.Icon;

import org.openflexo.FlexoCst;
import org.openflexo.foundation.action.FlexoActionFinalizer;
import org.openflexo.foundation.action.FlexoActionInitializer;
import org.openflexo.foundation.view.diagram.viewpoint.ExampleDiagramConnector;
import org.openflexo.foundation.view.diagram.viewpoint.ExampleDiagramObject;
import org.openflexo.foundation.view.diagram.viewpoint.action.DeclareExampleDiagramConnectorInEditionPattern;
import org.openflexo.icon.VPMIconLibrary;
import org.openflexo.view.controller.ActionInitializer;
import org.openflexo.view.controller.ControllerActionInitializer;
import org.openflexo.vpm.controller.VPMController;

public class DeclareConnectorInEditionPatternInitializer extends
		ActionInitializer<DeclareExampleDiagramConnectorInEditionPattern, ExampleDiagramConnector, ExampleDiagramObject> {

	private static final Logger logger = Logger.getLogger(ControllerActionInitializer.class.getPackage().getName());

	DeclareConnectorInEditionPatternInitializer(VPMControllerActionInitializer actionInitializer) {
		super(DeclareExampleDiagramConnectorInEditionPattern.actionType, actionInitializer);
	}

	@Override
	protected VPMControllerActionInitializer getControllerActionInitializer() {
		return (VPMControllerActionInitializer) super.getControllerActionInitializer();
	}

	@Override
	public VPMController getController() {
		return (VPMController) super.getController();
	}

	@Override
	protected FlexoActionInitializer<DeclareExampleDiagramConnectorInEditionPattern> getDefaultInitializer() {
		return new FlexoActionInitializer<DeclareExampleDiagramConnectorInEditionPattern>() {
			@Override
			public boolean run(EventObject e, DeclareExampleDiagramConnectorInEditionPattern action) {

				return instanciateAndShowDialog(action, FlexoCst.DECLARE_CONNECTOR_IN_EDITION_PATTERN_DIALOG_FIB);
			}
		};
	}

	@Override
	protected FlexoActionFinalizer<DeclareExampleDiagramConnectorInEditionPattern> getDefaultFinalizer() {
		return new FlexoActionFinalizer<DeclareExampleDiagramConnectorInEditionPattern>() {
			@Override
			public boolean run(EventObject e, DeclareExampleDiagramConnectorInEditionPattern action) {
				getController().setCurrentEditedObjectAsModuleView(action.getEditionPattern(), getController().VIEW_POINT_PERSPECTIVE);
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
