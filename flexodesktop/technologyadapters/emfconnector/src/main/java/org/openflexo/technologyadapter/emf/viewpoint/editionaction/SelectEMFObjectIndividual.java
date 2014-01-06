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
package org.openflexo.technologyadapter.emf.viewpoint.editionaction;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.openflexo.foundation.ontology.IFlexoOntologyClass;
import org.openflexo.foundation.view.TypeAwareModelSlotInstance;
import org.openflexo.foundation.view.action.EditionSchemeAction;
import org.openflexo.foundation.viewpoint.FetchRequest;
import org.openflexo.foundation.viewpoint.SelectIndividual;
import org.openflexo.technologyadapter.emf.EMFModelSlot;
import org.openflexo.technologyadapter.emf.metamodel.EMFClassClass;
import org.openflexo.technologyadapter.emf.metamodel.EMFEnumClass;
import org.openflexo.technologyadapter.emf.metamodel.EMFMetaModel;
import org.openflexo.technologyadapter.emf.model.EMFModel;
import org.openflexo.technologyadapter.emf.model.EMFObjectIndividual;

/**
 * EMF technology - specific {@link FetchRequest} allowing to retrieve a selection of some {@link EMFObjectIndividual} matching some
 * conditions and a given type.<br>
 * 
 * @author sylvain
 */
public class SelectEMFObjectIndividual extends SelectIndividual<EMFModelSlot, EMFObjectIndividual> {

	private static final Logger logger = Logger.getLogger(SelectEMFObjectIndividual.class.getPackage().getName());

	public SelectEMFObjectIndividual() {
		super();
	}

	@Override
	public List<EMFObjectIndividual> performAction(EditionSchemeAction action) {
		if (getModelSlotInstance(action) == null) {
			logger.warning("Could not access model slot instance. Abort.");
			return null;
		}
		if (getModelSlotInstance(action).getResourceData() == null) {
			logger.warning("Could not access model adressed by model slot instance. Abort.");
			return null;
		}

		// System.out.println("Selecting EMFObjectIndividuals in " + getModelSlotInstance(action).getModel() + " with type=" + getType());
		List<EMFObjectIndividual> selectedIndividuals = new ArrayList<EMFObjectIndividual>(0);
		EMFModel emfModel = getModelSlotInstance(action).getAccessedResourceData();
		Resource resource = emfModel.getEMFResource();
		IFlexoOntologyClass flexoOntologyClass = getType();
		List<EObject> selectedEMFIndividuals = new ArrayList<EObject>();
		if (flexoOntologyClass instanceof EMFClassClass) {
			TreeIterator<EObject> iterator = resource.getAllContents();
			while (iterator.hasNext()) {
				EObject eObject = iterator.next();
				// FIXME: following commented code was written by gilles
				// Seems to not working
				// Replaced by following
				// Gilles, could you check and explain ?
				/*selectedEMFIndividuals.addAll(EcoreUtility.getAllContents(eObject, ((EMFClassClass) flexoOntologyClass).getObject()
						.getClass()));*/
				EMFClassClass emfObjectIndividualType = emfModel.getMetaModel().getConverter().getClasses().get(eObject.eClass());
				if (emfObjectIndividualType.equals(flexoOntologyClass)
						|| ((EMFClassClass) flexoOntologyClass).isSuperClassOf(emfObjectIndividualType)) {
					selectedEMFIndividuals.add(eObject);
				}
				/*if (eObject.eClass().equals(((EMFClassClass) flexoOntologyClass).getObject())) {
					selectedEMFIndividuals.add(eObject);
				}*/
			}
		} else if (flexoOntologyClass instanceof EMFEnumClass) {
			System.err.println("We shouldn't browse enum individuals of type " + ((EMFEnumClass) flexoOntologyClass).getObject().getName()
					+ ".");
		}

		// System.out.println("selectedEMFIndividuals=" + selectedEMFIndividuals);

		for (EObject eObject : selectedEMFIndividuals) {
			EMFObjectIndividual emfObjectIndividual = emfModel.getConverter().getIndividuals().get(eObject);
			if (emfObjectIndividual != null) {
				selectedIndividuals.add(emfObjectIndividual);
			} else {
				logger.warning("It's weird there shoud be an existing OpenFlexo wrapper existing for EMF Object : " + eObject.toString());
				selectedIndividuals.add(emfModel.getConverter().convertObjectIndividual(emfModel, eObject));
			}
		}

		List<EMFObjectIndividual> returned = filterWithConditions(selectedIndividuals, action);

		// System.out.println("SelectEMFObjectIndividual, without filtering =" + selectedIndividuals + " after filtering=" + returned);

		return returned;
	}

	@Override
	public TypeAwareModelSlotInstance<EMFModel, EMFMetaModel, EMFModelSlot> getModelSlotInstance(EditionSchemeAction action) {
		return (TypeAwareModelSlotInstance<EMFModel, EMFMetaModel, EMFModelSlot>) super.getModelSlotInstance(action);
	}

}
