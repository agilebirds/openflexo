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
import org.openflexo.foundation.view.action.EditionSchemeAction;
import org.openflexo.foundation.viewpoint.AddClass;
import org.openflexo.foundation.viewpoint.ViewPoint.ViewPointBuilder;
import org.openflexo.technologyadapter.xsd.model.XMLModel;
import org.openflexo.technologyadapter.xsd.model.XSDMetaModel;
import org.openflexo.technologyadapter.xsd.model.XSOntClass;

public class AddXSClass extends AddClass<XMLModel, XSDMetaModel, XSOntClass> {

	private static final Logger logger = Logger.getLogger(AddXSClass.class.getPackage().getName());

	private String dataPropertyURI = null;

	public AddXSClass(ViewPointBuilder builder) {
		super(builder);
	}

	@Override
	public XSOntClass getOntologyClass() {
		return (XSOntClass) super.getOntologyClass();
	}

	@Override
	public Class<XSOntClass> getOntologyClassClass() {
		return XSOntClass.class;
	}

	@Override
	public XSOntClass performAction(EditionSchemeAction action) {
		XSOntClass father = getOntologyClass();
		String newClassName = (String) getClassName().getBindingValue(action);
		XSOntClass newClass = null;
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
	public void finalizePerformAction(EditionSchemeAction action, XSOntClass initialContext) {
	}

}
