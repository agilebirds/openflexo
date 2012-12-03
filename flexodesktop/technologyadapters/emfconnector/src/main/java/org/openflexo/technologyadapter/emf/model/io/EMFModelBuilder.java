/** Copyright (c) 2012, THALES SYSTEMES AEROPORTES - All Rights Reserved
 * Author : Gilles Besançon
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
 * Contributors :
 *
 */
package org.openflexo.technologyadapter.emf.model.io;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.resource.Resource;
import org.openflexo.foundation.ontology.io.IFlexoOntologyBuilder;
import org.openflexo.technologyadapter.emf.metamodel.EMFMetaModel;
import org.openflexo.technologyadapter.emf.model.EMFModel;
import org.openflexo.technologyadapter.emf.model.EMFObjectIndividual;
import org.openflexo.technologyadapter.emf.model.EMFObjectIndividualAttributeDataPropertyValue;
import org.openflexo.technologyadapter.emf.model.EMFObjectIndividualAttributeObjectPropertyValue;
import org.openflexo.technologyadapter.emf.model.EMFObjectIndividualReferenceObjectPropertyValue;

/**
 * EMF Model Builder.
 * 
 * @author gbesancon
 */
public class EMFModelBuilder implements IFlexoOntologyBuilder<EMFModel> {
	/**
	 * Build Model.
	 * 
	 * @param eObject
	 * @return
	 */
	public EMFModel buildModel(EMFMetaModel metaModel, EMFModelConverter converter, Resource resource) {
		return new EMFModel(metaModel, converter, resource);
	}

	/**
	 * Build Object Individual.
	 * 
	 * @param model
	 * @param eObject
	 * @return
	 */
	public EMFObjectIndividual buildObjectIndividual(EMFModel model, EObject eObject) {
		return new EMFObjectIndividual(model, eObject);
	}

	/**
	 * Build Object Individual Attribute Data Property Value.
	 * 
	 * @param model
	 * @param eObject
	 * @param eAttribute
	 * @return
	 */
	public EMFObjectIndividualAttributeDataPropertyValue buildObjectIndividualAttributeDataPropertyValue(EMFModel model, EObject eObject,
			EAttribute eAttribute) {
		return new EMFObjectIndividualAttributeDataPropertyValue(model, eObject, eAttribute);
	}

	/**
	 * Build Object Individual Attribute Object Property Value.
	 * 
	 * @param model
	 * @param eObject
	 * @param eAttribute
	 * @return
	 */
	public EMFObjectIndividualAttributeObjectPropertyValue buildObjectIndividualAttributeObjectPropertyValue(EMFModel model,
			EObject eObject, EAttribute eAttribute) {
		return new EMFObjectIndividualAttributeObjectPropertyValue(model, eObject, eAttribute);
	}

	/**
	 * Build Object Individual Reference Object Property Value.
	 * 
	 * @param model
	 * @param eObject
	 * @param eReference
	 * @return
	 */
	public EMFObjectIndividualReferenceObjectPropertyValue buildObjectIndividualReferenceObjectPropertyValue(EMFModel model,
			EObject eObject, EReference eReference) {
		return new EMFObjectIndividualReferenceObjectPropertyValue(model, eObject, eReference);
	}
}
