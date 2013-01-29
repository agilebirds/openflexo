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
package org.openflexo.technologyadapter.owl.viewpoint.editionaction;

import java.util.logging.Logger;

import org.openflexo.antar.expr.NullReferenceException;
import org.openflexo.antar.expr.TypeMismatchException;
import org.openflexo.foundation.ontology.DuplicateURIException;
import org.openflexo.foundation.view.action.EditionSchemeAction;
import org.openflexo.foundation.viewpoint.AddClass;
import org.openflexo.foundation.viewpoint.VirtualModel;
import org.openflexo.foundation.viewpoint.VirtualModel.VirtualModelBuilder;
import org.openflexo.technologyadapter.owl.model.OWLClass;
import org.openflexo.technologyadapter.owl.model.OWLOntology;

public class AddOWLClass extends AddClass<OWLOntology, OWLOntology, OWLClass> {

	private static final Logger logger = Logger.getLogger(AddOWLClass.class.getPackage().getName());

	private String dataPropertyURI = null;

	public AddOWLClass(VirtualModel.VirtualModelBuilder builder) {
		super(builder);
	}

	@Override
	public OWLClass getOntologyClass() {
		return (OWLClass) super.getOntologyClass();
	}

	@Override
	public Class<OWLClass> getOntologyClassClass() {
		return OWLClass.class;
	}

	@Override
	public OWLClass performAction(EditionSchemeAction action) {
		OWLClass father = getOntologyClass();
		String newClassName = null;
		try {
			newClassName = getClassName().getBindingValue(action);
		} catch (TypeMismatchException e1) {
			e1.printStackTrace();
		} catch (NullReferenceException e1) {
			e1.printStackTrace();
		}
		OWLClass newClass = null;
		try {
			logger.info("Adding class " + newClassName + " as " + father);
			newClass = getModelSlotInstance(action).getModel().createOntologyClass(newClassName, father);
			logger.info("Added class " + newClass.getName() + " as " + father);
		} catch (DuplicateURIException e) {
			e.printStackTrace();
		}
		return newClass;
	}

	@Override
	public void finalizePerformAction(EditionSchemeAction action, OWLClass initialContext) {
		// TODO Auto-generated method stub

	}

}
