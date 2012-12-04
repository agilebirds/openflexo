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

import org.openflexo.foundation.ontology.DuplicateURIException;
import org.openflexo.foundation.view.action.EditionSchemeAction;
import org.openflexo.foundation.viewpoint.AddClass;
import org.openflexo.foundation.viewpoint.ViewPoint.ViewPointBuilder;
import org.openflexo.technologyadapter.emf.model.EMFClass;
import org.openflexo.technologyadapter.emf.model.EMFMetaModel;
import org.openflexo.technologyadapter.emf.model.EMFModel;

public class AddEMFClass extends AddClass<EMFModel, EMFMetaModel, EMFClass> {

	private static final Logger logger = Logger.getLogger(AddEMFClass.class.getPackage().getName());

	private String dataPropertyURI = null;

	public AddEMFClass(ViewPointBuilder builder) {
		super(builder);
	}

	@Override
	public EMFClass getOntologyClass() {
		return (EMFClass) super.getOntologyClass();
	}

	@Override
	public EMFClass performAction(EditionSchemeAction action) {
		EMFClass father = getOntologyClass();
		String newClassName = (String) getClassName().getBindingValue(action);
		EMFClass newClass = null;
		try {
			logger.info("Adding class " + newClassName + " as " + father);
			newClass = (EMFClass) getModelSlotInstance(action).getModel().createOntologyClass(newClassName, father);
			logger.info("Added class " + newClass.getName() + " as " + father);
		} catch (DuplicateURIException e) {
			e.printStackTrace();
		}
		return newClass;
	}

	@Override
	public void finalizePerformAction(EditionSchemeAction action, EMFClass initialContext) {
		// TODO Auto-generated method stub

	}

}
