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
package org.openflexo.ve.view;

import org.openflexo.foundation.ontology.FlexoOntology;
import org.openflexo.ve.VECst;
import org.openflexo.ve.controller.OEController;
import org.openflexo.view.FIBModuleView;
import org.openflexo.view.FlexoPerspective;

/**
 * Please comment this class
 * 
 * @author sguerin
 * 
 */
public class OntologyView extends FIBModuleView<FlexoOntology> {

	private FlexoPerspective<? super FlexoOntology> declaredPerspective;

	public OntologyView(FlexoOntology ontology, OEController controller, FlexoPerspective<? super FlexoOntology> perspective) {
		super(ontology, controller, VECst.ONTOLOGY_VIEW_FIB);
		declaredPerspective = perspective;
	}

	@Override
	public OEController getFlexoController() {
		return (OEController) super.getFlexoController();
	}

	@Override
	public FlexoPerspective<? super FlexoOntology> getPerspective() {
		return declaredPerspective;
	}
}
