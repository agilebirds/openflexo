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

import org.openflexo.foundation.ontology.DuplicateURIException;
import org.openflexo.foundation.ontology.OntologyClass;
import org.openflexo.foundation.ontology.OntologyObject;
import org.openflexo.foundation.ontology.OntologyProperty;
import org.openflexo.foundation.view.action.EditionSchemeAction;
import org.openflexo.foundation.viewpoint.AddIndividual;
import org.openflexo.foundation.viewpoint.DataPropertyAssertion;
import org.openflexo.foundation.viewpoint.ObjectPropertyAssertion;
import org.openflexo.foundation.viewpoint.ViewPoint.ViewPointBuilder;
import org.openflexo.technologyadapter.owl.OWLModelSlot;
import org.openflexo.technologyadapter.owl.model.OWLClass;
import org.openflexo.technologyadapter.owl.model.OWLIndividual;
import org.openflexo.technologyadapter.owl.model.OWLMetaModel;
import org.openflexo.technologyadapter.owl.model.OWLModel;
import org.openflexo.technologyadapter.owl.model.OWLObject;
import org.openflexo.technologyadapter.owl.model.OWLObjectProperty;
import org.openflexo.technologyadapter.owl.model.OWLProperty;

public class AddOWLIndividual extends AddIndividual<OWLModelSlot, OWLModel, OWLMetaModel, OWLIndividual> {

	private static final Logger logger = Logger.getLogger(AddOWLIndividual.class.getPackage().getName());

	private String dataPropertyURI = null;

	public AddOWLIndividual(ViewPointBuilder builder) {
		super(builder);
	}

	@Override
	public OWLClass getOntologyClass() {
		return (OWLClass) super.getOntologyClass();
	}

	@Override
	public OWLIndividual performAction(EditionSchemeAction action) {
		OntologyClass father = getOntologyClass();
		// OntologyObject father = action.getOntologyObject(getProject());
		// System.out.println("Individual name param = "+action.getIndividualNameParameter());
		// String individualName = (String)getParameterValues().get(action.getIndividualNameParameter().getName());
		String individualName = (String) getIndividualName().getBindingValue(action);
		// System.out.println("individualName="+individualName);
		OWLIndividual newIndividual = null;
		try {
			newIndividual = getModelSlotInstance(action).getModel().createOntologyIndividual(individualName, father);
			logger.info("********* Added individual " + newIndividual.getName() + " as " + father);

			for (DataPropertyAssertion dataPropertyAssertion : getDataAssertions()) {
				if (dataPropertyAssertion.evaluateCondition(action)) {
					logger.info("DataPropertyAssertion=" + dataPropertyAssertion);
					OntologyProperty property = dataPropertyAssertion.getOntologyProperty();
					logger.info("Property=" + property);
					Object value = dataPropertyAssertion.getValue(action);
					newIndividual.addPropertyStatement(property, value);
				}
			}
			for (ObjectPropertyAssertion objectPropertyAssertion : getObjectAssertions()) {
				if (objectPropertyAssertion.evaluateCondition(action)) {
					// logger.info("ObjectPropertyAssertion="+objectPropertyAssertion);
					OWLProperty property = (OWLProperty) objectPropertyAssertion.getOntologyProperty();
					// logger.info("Property="+property);
					if (property instanceof OWLObjectProperty) {
						if (((OWLObjectProperty) property).isLiteralRange()) {
							Object value = objectPropertyAssertion.getValue(action);
							newIndividual.addPropertyStatement(property, value);
						} else {
							OWLObject<?> assertionObject = (OWLObject<?>) objectPropertyAssertion.getAssertionObject(action);
							if (assertionObject != null) {
								newIndividual.getOntResource().addProperty(((OWLObjectProperty) property).getOntProperty(),
										assertionObject.getOntResource());
							}
						}
					}
					OntologyObject assertionObject = objectPropertyAssertion.getAssertionObject(action);
					// logger.info("assertionObject="+assertionObject);
					if (assertionObject != null && newIndividual instanceof OWLIndividual && property instanceof OWLProperty
							&& assertionObject instanceof OWLObject) {
						newIndividual.getOntResource().addProperty(property.getOntProperty(),
								((OWLObject) assertionObject).getOntResource());
					} else {
						// logger.info("assertion object is null");
					}
				}
			}
			newIndividual.updateOntologyStatements();

			// Register reference
			newIndividual.registerEditionPatternReference(action.getEditionPatternInstance(), getPatternRole());

			return newIndividual;
		} catch (DuplicateURIException e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public void finalizePerformAction(EditionSchemeAction action, OWLIndividual initialContext) {
		// TODO Auto-generated method stub

	}

}
