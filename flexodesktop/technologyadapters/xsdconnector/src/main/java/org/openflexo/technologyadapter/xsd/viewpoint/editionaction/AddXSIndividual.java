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
package org.openflexo.technologyadapter.xsd.viewpoint.editionaction;

import java.util.logging.Logger;

import org.openflexo.foundation.ontology.DuplicateURIException;
import org.openflexo.foundation.ontology.IFlexoOntologyClass;
import org.openflexo.foundation.view.ModelSlotInstance;
import org.openflexo.foundation.view.action.EditionSchemeAction;
import org.openflexo.foundation.viewpoint.AddIndividual;
import org.openflexo.foundation.viewpoint.DataPropertyAssertion;
import org.openflexo.foundation.viewpoint.ObjectPropertyAssertion;
import org.openflexo.foundation.viewpoint.VirtualModel;
import org.openflexo.technologyadapter.xsd.XSDModelSlot;
import org.openflexo.technologyadapter.xsd.model.XMLModel;
import org.openflexo.technologyadapter.xsd.model.XSDMetaModel;
import org.openflexo.technologyadapter.xsd.model.XSOntClass;
import org.openflexo.technologyadapter.xsd.model.XSOntDataProperty;
import org.openflexo.technologyadapter.xsd.model.XSOntIndividual;

public class AddXSIndividual extends AddIndividual<XMLModel, XSDMetaModel, XSOntIndividual> {

	@Override
	public void setOntologyClass(IFlexoOntologyClass ontologyClass) {
		// TODO Auto-generated method stub
		super.setOntologyClass(ontologyClass);
		if ( ontologyClassURI == null) {
			logger.warning("OntologyURI is null for XSIndividual");
		}
	}

	private static final Logger logger = Logger.getLogger(AddXSIndividual.class.getPackage().getName());

	public AddXSIndividual(VirtualModel.VirtualModelBuilder builder) {
		super(builder);
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

			ModelSlotInstance<XMLModel, XSDMetaModel> modelSlotInstance = getModelSlotInstance(action);
			XMLModel model = modelSlotInstance.getModel();
			XSDModelSlot modelSlot = (XSDModelSlot) modelSlotInstance.getModelSlot();
			
		
			newIndividual = model.createOntologyIndividual( father);
			
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
			Object o = modelSlot.retrieveObjectWithURI(modelSlotInstance, processedURI);
			if (o == null ) {
				model.addIndividual(newIndividual);
				model.setIsModified();
			}
			else {
				throw new DuplicateURIException("Error while creating Individual of type " + father.getURI());
			}
			
			return newIndividual;
		} catch (DuplicateURIException e) {
			e.printStackTrace();
			return null;
		}
	}

}
