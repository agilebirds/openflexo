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

import java.util.logging.Logger;

import org.openflexo.view.controller.ControllerActionInitializer;
import org.openflexo.vpm.controller.VPMController;
import org.openflexo.vpm.controller.VPMSelectionManager;

/**
 * 
 * Action initializing for this module
 * 
 * @author yourname
 */
public class CEDControllerActionInitializer extends ControllerActionInitializer {

	private static final Logger logger = Logger.getLogger(ControllerActionInitializer.class.getPackage().getName());

	private VPMController _cedController;

	public CEDControllerActionInitializer(VPMController controller) {
		super(controller);
		_cedController = controller;
	}

	protected VPMController getCEDController() {
		return _cedController;
	}

	protected VPMSelectionManager getCEDSelectionManager() {
		return getCEDController().getCEDSelectionManager();
	}

	@Override
	public void initializeActions() {
		super.initializeActions();

		(new CEDSetPropertyInitializer(this)).init();

		// CalcEditor perspective
		(new CreateViewPointInitializer(this)).init();
		(new AddEditionPatternInitializer(this)).init();
		(new DeleteEditionPatternInitializer(this)).init();
		(new DuplicateEditionPatternInitializer(this)).init();

		// CalcDrawing edition
		(new CreateCalcDrawingShemaInitializer(this)).init();
		(new DeleteCalcDrawingShemaInitializer(this)).init();
		(new PushToPaletteInitializer(this)).init();
		(new DeclareShapeInEditionPatternInitializer(this)).init();
		(new DeclareConnectorInEditionPatternInitializer(this)).init();
		(new DeleteCalcShemaElementsInitializer(this)).init();

		// Palette edition
		(new CreateCalcPaletteInitializer(this)).init();
		(new DeleteCalcPaletteInitializer(this)).init();
		(new AddCalcPaletteElementInitializer(this)).init();
		(new DeleteCalcPaletteElementInitializer(this)).init();

		// OntologyEditor perspective
		(new CreateOntologyClassInitializer(this)).init();
		(new CreateOntologyIndividualInitializer(this)).init();
		(new CreateObjectPropertyInitializer(this)).init();
		(new CreateDataPropertyInitializer(this)).init();
		(new DeleteOntologyObjectsInitializer(this)).init();
		(new AddAnnotationStatementInitializer(this)).init();
	}

}
