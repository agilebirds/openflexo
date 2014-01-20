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

import java.lang.reflect.InvocationTargetException;
import java.util.logging.Logger;

import org.openflexo.antar.expr.NullReferenceException;
import org.openflexo.antar.expr.TypeMismatchException;
import org.openflexo.foundation.ontology.DuplicateURIException;
import org.openflexo.foundation.ontology.IFlexoOntologyConcept;
import org.openflexo.foundation.view.TypeAwareModelSlotInstance;
import org.openflexo.foundation.view.action.EditionSchemeAction;
import org.openflexo.foundation.viewpoint.AddIndividual;
import org.openflexo.foundation.viewpoint.DataPropertyAssertion;
import org.openflexo.foundation.viewpoint.ObjectPropertyAssertion;
import org.openflexo.model.annotations.ImplementationClass;
import org.openflexo.model.annotations.ModelEntity;
import org.openflexo.model.annotations.XMLElement;
import org.openflexo.technologyadapter.owl.OWLModelSlot;
import org.openflexo.technologyadapter.owl.model.OWLClass;
import org.openflexo.technologyadapter.owl.model.OWLConcept;
import org.openflexo.technologyadapter.owl.model.OWLIndividual;
import org.openflexo.technologyadapter.owl.model.OWLObjectProperty;
import org.openflexo.technologyadapter.owl.model.OWLOntology;
import org.openflexo.technologyadapter.owl.model.OWLProperty;

@ModelEntity
@ImplementationClass(AddOWLIndividual.AddOWLIndividualImpl.class)
@XMLElement
public interface AddOWLIndividual extends AddIndividual<OWLModelSlot, OWLIndividual> {

	public static abstract class AddOWLIndividualImpl extends AddIndividualImpl<OWLModelSlot, OWLIndividual> implements AddOWLIndividual {

		private static final Logger logger = Logger.getLogger(AddOWLIndividual.class.getPackage().getName());

		private final String dataPropertyURI = null;

		public AddOWLIndividualImpl() {
			super();
		}

		@Override
		public OWLClass getOntologyClass() {
			return (OWLClass) super.getOntologyClass();
		}

		@Override
		public Class<OWLIndividual> getOntologyIndividualClass() {
			return OWLIndividual.class;
		}

		@Override
		public OWLIndividual performAction(EditionSchemeAction action) {
			OWLClass father = getOntologyClass();
			// IFlexoOntologyConcept father = action.getOntologyObject(getProject());
			// System.out.println("Individual name param = "+action.getIndividualNameParameter());
			// String individualName = (String)getParameterValues().get(action.getIndividualNameParameter().getName());
			String individualName = null;
			try {
				individualName = getIndividualName().getBindingValue(action);
			} catch (TypeMismatchException e1) {
				e1.printStackTrace();
			} catch (NullReferenceException e1) {
				e1.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			}
			// System.out.println("individualName="+individualName);
			OWLIndividual newIndividual = null;
			try {
				if (getModelSlotInstance(action) != null) {
					if (getModelSlotInstance(action).getResourceData() != null) {
						logger.info("Adding individual individualName=" + getIndividualName() + " father =" + getOntologyClass());
						logger.info("Adding individual individualName=" + individualName + " father =" + father);
						newIndividual = getModelSlotInstance(action).getAccessedResourceData().createOntologyIndividual(individualName,
								father);
						logger.info("********* Added individual " + newIndividual.getName() + " as " + father);

						for (DataPropertyAssertion dataPropertyAssertion : getDataAssertions()) {
							if (dataPropertyAssertion.evaluateCondition(action)) {
								logger.info("DataPropertyAssertion=" + dataPropertyAssertion);
								OWLProperty property = (OWLProperty) dataPropertyAssertion.getOntologyProperty();
								logger.info("Property=" + property);
								Object value = dataPropertyAssertion.getValue(action);
								if (value != null) {
									newIndividual.addPropertyStatement(property, value);
								}
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
										if (value != null) {
											newIndividual.addPropertyStatement(property, value);
										}
									} else {
										OWLConcept<?> assertionObject = (OWLConcept<?>) objectPropertyAssertion.getAssertionObject(action);
										if (assertionObject != null) {
											newIndividual.getOntResource().addProperty(((OWLObjectProperty) property).getOntProperty(),
													assertionObject.getOntResource());
										}
									}
								}
								IFlexoOntologyConcept assertionObject = objectPropertyAssertion.getAssertionObject(action);
								// logger.info("assertionObject="+assertionObject);
								if (assertionObject != null && newIndividual instanceof OWLIndividual && property instanceof OWLProperty
										&& assertionObject instanceof OWLConcept) {
									newIndividual.getOntResource().addProperty(property.getOntProperty(),
											((OWLConcept) assertionObject).getOntResource());
								} else {
									// logger.info("assertion object is null");
								}
							}
						}
						newIndividual.updateOntologyStatements();
					} else {
						logger.warning("No model defined for ModelSlotInstance " + getModelSlotInstance(action));
					}
				} else {
					logger.warning("No model slot instance defined for " + getModelSlot());
				}
				return newIndividual;
			} catch (DuplicateURIException e) {
				e.printStackTrace();
				return null;
			}
		}

		@Override
		public TypeAwareModelSlotInstance<OWLOntology, OWLOntology, OWLModelSlot> getModelSlotInstance(EditionSchemeAction action) {
			return (TypeAwareModelSlotInstance<OWLOntology, OWLOntology, OWLModelSlot>) super.getModelSlotInstance(action);
		}

	}
}
