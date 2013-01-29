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

import org.openflexo.foundation.action.FlexoActionFinalizer;
import org.openflexo.foundation.action.FlexoActionInitializer;
import org.openflexo.foundation.view.diagram.viewpoint.action.CreateDiagramSpecification;
import org.openflexo.foundation.viewpoint.ViewPoint;
import org.openflexo.foundation.viewpoint.ViewPointObject;
import org.openflexo.icon.VPMIconLibrary;
import org.openflexo.view.controller.ActionInitializer;
import org.openflexo.view.controller.ControllerActionInitializer;
import org.openflexo.vpm.VPMCst;
import org.openflexo.vpm.controller.VPMController;

public class CreateDiagramSpecificationInitializer extends ActionInitializer<CreateDiagramSpecification, ViewPoint, ViewPointObject> {

	private static final Logger logger = Logger.getLogger(ControllerActionInitializer.class.getPackage().getName());

	CreateDiagramSpecificationInitializer(VPMControllerActionInitializer actionInitializer) {
		super(CreateDiagramSpecification.actionType, actionInitializer);
	}

	@Override
	protected VPMControllerActionInitializer getControllerActionInitializer() {
		return (VPMControllerActionInitializer) super.getControllerActionInitializer();
	}

	@Override
	protected FlexoActionInitializer<CreateDiagramSpecification> getDefaultInitializer() {
		return new FlexoActionInitializer<CreateDiagramSpecification>() {
			@Override
			public boolean run(EventObject e, CreateDiagramSpecification action) {
				return instanciateAndShowDialog(action, VPMCst.CREATE_DIAGRAM_SPECIFICATION_DIALOG_FIB);
			}
		};
	}

	@Override
	protected FlexoActionFinalizer<CreateDiagramSpecification> getDefaultFinalizer() {
		return new FlexoActionFinalizer<CreateDiagramSpecification>() {
			@Override
			public boolean run(EventObject e, CreateDiagramSpecification action) {
				((VPMController) getController()).selectAndFocusObject(action.getNewDiagramSpecification());
				return true;
			}
		};
	}

	@Override
	protected Icon getEnabledIcon() {
		return VPMIconLibrary.DIAGRAM_SPECIFICATION_ICON;
	}

}
