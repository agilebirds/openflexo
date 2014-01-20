/*
 * (c) Copyright 2010-2012 AgileBirds
 * (c) Copyright 2012-2013 Openflexo
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
package org.openflexo.technologyadapter.xsd.viewpoint.editionaction;

import java.util.logging.Logger;

import org.openflexo.foundation.ontology.DuplicateURIException;
import org.openflexo.foundation.ontology.IFlexoOntologyClass;
import org.openflexo.foundation.view.TypeAwareModelSlotInstance;
import org.openflexo.foundation.view.action.EditionSchemeAction;
import org.openflexo.foundation.viewpoint.AddIndividual;
import org.openflexo.foundation.viewpoint.DataPropertyAssertion;
import org.openflexo.foundation.viewpoint.ObjectPropertyAssertion;
import org.openflexo.model.annotations.ImplementationClass;
import org.openflexo.model.annotations.ModelEntity;
import org.openflexo.model.annotations.XMLElement;
import org.openflexo.technologyadapter.xsd.XSDModelSlot;
import org.openflexo.technologyadapter.xsd.metamodel.XSDMetaModel;
import org.openflexo.technologyadapter.xsd.metamodel.XSOntClass;
import org.openflexo.technologyadapter.xsd.metamodel.XSOntDataProperty;
import org.openflexo.technologyadapter.xsd.model.XMLXSDModel;
import org.openflexo.technologyadapter.xsd.model.XSOntIndividual;

@ModelEntity
@ImplementationClass(AddXSIndividual.AddXSIndividualImpl.class)
@XMLElement
public interface AddXSIndividual extends AddIndividual<XSDModelSlot, XSOntIndividual> {

	public static abstract class AddXSIndividualImpl extends AddIndividualImpl<XSDModelSlot, XSOntIndividual> implements AddXSIndividual {

		@Override
		public void setOntologyClass(IFlexoOntologyClass ontologyClass) {
			super.setOntologyClass(ontologyClass);
			if (ontologyClassURI == null) {
				logger.warning("OntologyURI is null for XSIndividual");
			}
		}

		private static final Logger logger = Logger.getLogger(AddXSIndividual.class.getPackage().getName());

		public AddXSIndividualImpl() {
			super();
		}

		@Override
		public XSOntClass getOntologyClass() {
			return (XSOntClass) super.getOntologyClass();
		}

		@Override
		public Class<XSOntIndividual> getOntologyIndividualClass() {
			return XSOntIndividual.class;
		}

		@Override
		public XSOntIndividual performAction(EditionSchemeAction action) {
			XSOntClass father = getOntologyClass();

			XSOntIndividual newIndividual = null;
			try {

				TypeAwareModelSlotInstance<XMLXSDModel, XSDMetaModel, XSDModelSlot> modelSlotInstance = (TypeAwareModelSlotInstance<XMLXSDModel, XSDMetaModel, XSDModelSlot>) getModelSlotInstance(action);
				XMLXSDModel model = modelSlotInstance.getAccessedResourceData();
				XSDModelSlot modelSlot = modelSlotInstance.getModelSlot();

				newIndividual = model.createOntologyIndividual(father);

				for (DataPropertyAssertion dataPropertyAssertion : getDataAssertions()) {
					if (dataPropertyAssertion.evaluateCondition(action)) {
						logger.info("DataPropertyAssertion=" + dataPropertyAssertion);
						XSOntDataProperty property = (XSOntDataProperty) dataPropertyAssertion.getOntologyProperty();
						logger.info("Property=" + property);
						Object value = dataPropertyAssertion.getValue(action);
						logger.info("Value=" + value);
						newIndividual.addToPropertyValue(property, value);
					}
				}

				for (ObjectPropertyAssertion objectPropertyAssertion : getObjectAssertions()) {
					if (objectPropertyAssertion.evaluateCondition(action)) {
						// ... TODO
						logger.warning("***** AddObjectProperty Not Implemented yet");
					}
				}

				// add it to the model
				// Two phase creation, then addition, to be able to process URIs once you have the property values
				// and verify that there is no duplicate URIs

				String processedURI = modelSlot.getURIForObject(modelSlotInstance, newIndividual);
				if (processedURI != null) {
					Object o = modelSlot.retrieveObjectWithURI(modelSlotInstance, processedURI);
					if (o == null) {
						model.addIndividual(newIndividual);
						modelSlotInstance.getResourceData().setIsModified();
					} else {
						throw new DuplicateURIException("Error while creating Individual of type " + father.getURI());
					}

					return newIndividual;
				} else
					return null;
			} catch (DuplicateURIException e) {
				e.printStackTrace();
				return null;
			}
		}

	}
}
