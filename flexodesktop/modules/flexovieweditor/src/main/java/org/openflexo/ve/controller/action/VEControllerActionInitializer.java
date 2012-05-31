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

import java.util.logging.Logger;

import org.openflexo.foundation.view.action.ActionSchemeActionType;
import org.openflexo.foundation.view.action.NavigationSchemeActionType;
import org.openflexo.ve.controller.VEController;
import org.openflexo.ve.controller.VESelectionManager;
import org.openflexo.view.controller.ControllerActionInitializer;

/**
 * 
 * Action initializing for this module
 * 
 * @author yourname
 */
public class VEControllerActionInitializer extends ControllerActionInitializer {

	private static final Logger logger = Logger.getLogger(ControllerActionInitializer.class.getPackage().getName());

	private VEController _oeController;

	public VEControllerActionInitializer(VEController controller) {
		super(controller);
		_oeController = controller;
	}

	protected VEController getOEController() {
		return _oeController;
	}

	protected VESelectionManager getOESelectionManager() {
		return getOEController().getOESelectionManager();
	}

	@Override
	public void initializeActions() {
		super.initializeActions();

		(new VESetPropertyInitializer(this)).init();

		// Shema library perspective
		(new AddViewInitializer(this)).init();
		(new AddViewFolderInitializer(this)).init();
		(new DeleteViewInitializer(this)).init();
		(new DeleteViewFolderInitializer(this)).init();
		(new ResetGraphicalRepresentationInitializer(this)).init();

		// Diagram perspective
		(new AddShapeInitializer(this)).init();
		(new AddConnectorInitializer(this)).init();
		(new DeleteViewElementsInitializer(this)).init();
		(new DropSchemeActionInitializer(this)).init();
		(new LinkSchemeActionInitializer(this)).init();
		(new ActionSchemeActionInitializer(this)).initForClass(ActionSchemeActionType.class);
		(new NavigationSchemeActionInitializer(this)).initForClass(NavigationSchemeActionType.class);

		// Ontology perspective
		(new CreateOntologyClassInitializer(this)).init();
		(new CreateOntologyIndividualInitializer(this)).init();
		(new CreateObjectPropertyInitializer(this)).init();
		(new CreateDataPropertyInitializer(this)).init();
		(new DeleteOntologyObjectsInitializer(this)).init();
		(new AddAnnotationStatementInitializer(this)).init();
	}

}
