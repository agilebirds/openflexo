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
package org.openflexo.vpm.view;

import org.openflexo.foundation.ontology.FlexoOntology;
import org.openflexo.view.FIBModuleView;
import org.openflexo.view.FlexoPerspective;
import org.openflexo.vpm.CEDCst;
import org.openflexo.vpm.controller.VPMController;

/**
 * Please comment this class
 * 
 * @author sguerin
 * 
 */
public class OntologyView extends FIBModuleView<FlexoOntology> {

	private FlexoPerspective<? super FlexoOntology> declaredPerspective;

	public OntologyView(FlexoOntology ontology, VPMController controller, FlexoPerspective<? super FlexoOntology> perspective) {
		super(ontology, controller, CEDCst.ONTOLOGY_VIEW_FIB);
		declaredPerspective = perspective;
		controller.manageResource(ontology);
	}

	@Override
	public VPMController getFlexoController() {
		return (VPMController) super.getFlexoController();
	}

	@Override
	public FlexoPerspective<? super FlexoOntology> getPerspective() {
		return declaredPerspective;
	}
}
