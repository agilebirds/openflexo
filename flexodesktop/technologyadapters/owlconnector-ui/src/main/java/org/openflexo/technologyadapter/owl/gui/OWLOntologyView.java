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
package org.openflexo.technologyadapter.owl.gui;

import org.openflexo.components.widget.OntologyBrowserModel;
import org.openflexo.components.widget.OntologyView;
import org.openflexo.technologyadapter.owl.model.OWLOntology;
import org.openflexo.view.controller.FlexoController;
import org.openflexo.view.controller.model.FlexoPerspective;

/**
 * This class represent the module view for an OWL ontology.<br>
 * Underlying representation is supported by OntologyView implementation.
 * 
 * @author sylvain
 * 
 */
@SuppressWarnings("serial")
public class OWLOntologyView extends OntologyView<OWLOntology> {

	public OWLOntologyView(OWLOntology object, FlexoController controller, FlexoPerspective perspective) {
		super(object, controller, perspective);
	}

	@Override
	protected OntologyBrowserModel makeBrowserModel() {
		return new OWLOntologyBrowserModel(getOntology());
	}

}
