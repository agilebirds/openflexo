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

import java.util.logging.Logger;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.openflexo.foundation.ontology.IFlexoOntologyClass;
import org.openflexo.foundation.view.ModelSlotInstance;
import org.openflexo.foundation.view.action.EditionSchemeAction;
import org.openflexo.foundation.viewpoint.AddIndividual;
import org.openflexo.foundation.viewpoint.VirtualModel;
import org.openflexo.technologyadapter.emf.metamodel.EMFClassClass;
import org.openflexo.technologyadapter.emf.metamodel.EMFMetaModel;
import org.openflexo.technologyadapter.emf.model.EMFModel;
import org.openflexo.technologyadapter.emf.model.EMFObjectIndividual;

/**
 * Create EMF Object.
 * 
 * @author gbesancon
 * 
 */
public class AddEMFObjectIndividual extends AddIndividual<EMFModel, EMFMetaModel, EMFObjectIndividual> {

	private static final Logger logger = Logger.getLogger(AddEMFObjectIndividual.class.getPackage().getName());

	public AddEMFObjectIndividual(VirtualModel.VirtualModelBuilder builder) {
		super(builder);
	}

	@Override
	public EMFClassClass getOntologyClass() {
		return (EMFClassClass) super.getOntologyClass();
	}

	@Override
	public Class<EMFObjectIndividual> getOntologyIndividualClass() {
		return EMFObjectIndividual.class;
	}

	@Override
	public EMFObjectIndividual performAction(EditionSchemeAction action) {
		EMFObjectIndividual result = null;
		ModelSlotInstance<EMFModel, EMFMetaModel> modelSlotInstance = getModelSlotInstance(action);
		IFlexoOntologyClass aClass = getOntologyClass();
		if (aClass instanceof EMFClassClass) {
			EMFClassClass emfClassClass = (EMFClassClass) aClass;
			// Create EMF Object
			EObject eObject = EcoreUtil.create(emfClassClass.getObject());
			modelSlotInstance.getModel().getEMFResource().getContents().add(eObject);
			// Instanciate Wrapper.
			result = modelSlotInstance.getModel().getConverter().convertObjectIndividual(modelSlotInstance.getModel(), eObject);
			logger.info("********* Added individual " + result.getName() + " as " + aClass.getName());
		} else {
			logger.warning("Not allowed to create new Enum values.");
			return null;
		}

		return result;
	}

	@Override
	public void finalizePerformAction(EditionSchemeAction action, EMFObjectIndividual initialContext) {
	}
}
