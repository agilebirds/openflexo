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
import javax.swing.KeyStroke;

import org.openflexo.FlexoCst;
import org.openflexo.foundation.action.FlexoActionFinalizer;
import org.openflexo.foundation.action.FlexoActionInitializer;
import org.openflexo.foundation.view.diagram.action.DeleteDiagramElements;
import org.openflexo.foundation.view.diagram.model.DiagramElement;
import org.openflexo.icon.IconLibrary;
import org.openflexo.ve.VECst;
import org.openflexo.view.controller.ActionInitializer;
import org.openflexo.view.controller.ControllerActionInitializer;

public class DeleteDiagramElementsInitializer extends ActionInitializer<DeleteDiagramElements, DiagramElement, DiagramElement> {

	private static final Logger logger = Logger.getLogger(ControllerActionInitializer.class.getPackage().getName());

	DeleteDiagramElementsInitializer(VEControllerActionInitializer actionInitializer) {
		super(DeleteDiagramElements.actionType, actionInitializer);
	}

	@Override
	protected VEControllerActionInitializer getControllerActionInitializer() {
		return (VEControllerActionInitializer) super.getControllerActionInitializer();
	}

	@Override
	protected FlexoActionInitializer<DeleteDiagramElements> getDefaultInitializer() {
		return new FlexoActionInitializer<DeleteDiagramElements>() {
			@Override
			public boolean run(EventObject e, DeleteDiagramElements action) {
				return instanciateAndShowDialog(action, VECst.DELETE_DIAGRAM_ELEMENTS_DIALOG_FIB);
			}
		};
	}

	@Override
	protected FlexoActionFinalizer<DeleteDiagramElements> getDefaultFinalizer() {
		return new FlexoActionFinalizer<DeleteDiagramElements>() {
			@Override
			public boolean run(EventObject e, DeleteDiagramElements action) {
				if (getControllerActionInitializer().getVEController().getSelectionManager().getLastSelectedObject() != null
						&& getControllerActionInitializer().getVEController().getSelectionManager().getLastSelectedObject().isDeleted()) {
					getControllerActionInitializer().getVEController().getSelectionManager().resetSelection();
				}
				return true;
			}
		};
	}

	@Override
	protected Icon getEnabledIcon() {
		return IconLibrary.DELETE_ICON;
	}

	@Override
	protected KeyStroke getShortcut() {
		return KeyStroke.getKeyStroke(FlexoCst.DELETE_KEY_CODE, 0);
	}

}
