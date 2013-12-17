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

import java.lang.reflect.InvocationTargetException;
import java.util.logging.Logger;

import org.openflexo.antar.expr.NullReferenceException;
import org.openflexo.antar.expr.TypeMismatchException;
import org.openflexo.foundation.ontology.DuplicateURIException;
import org.openflexo.foundation.view.TypeAwareModelSlotInstance;
import org.openflexo.foundation.view.action.EditionSchemeAction;
import org.openflexo.foundation.viewpoint.AddClass;
import org.openflexo.technologyadapter.xsd.XSDModelSlot;
import org.openflexo.technologyadapter.xsd.metamodel.XSDMetaModel;
import org.openflexo.technologyadapter.xsd.metamodel.XSOntClass;
import org.openflexo.technologyadapter.xsd.model.XMLXSDModel;

public class AddXSClass extends AddClass<XSDModelSlot, XSOntClass> {

	private static final Logger logger = Logger.getLogger(AddXSClass.class.getPackage().getName());

	private final String dataPropertyURI = null;

	public AddXSClass() {
		super();
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
		String newClassName = null;
		try {
			newClassName = getClassName().getBindingValue(action);
		} catch (TypeMismatchException e1) {
			e1.printStackTrace();
		} catch (NullReferenceException e1) {
			e1.printStackTrace();
		} catch (InvocationTargetException e1) {
			e1.printStackTrace();
		}
		XSOntClass newClass = null;
		try {
			logger.info("Adding class " + newClassName + " as " + father);
			// FIXME : Something wrong here!
			// newClass = getModelSlotInstance(action).getModel().getMetaModel().createOntologyClass(newClassName, father);
			newClass = getModelSlotInstance(action).getResourceData().getMetaModel().createOntologyClass(newClassName, father);
			logger.info("Added class " + newClass.getName() + " as " + father);
		} catch (DuplicateURIException e) {
			e.printStackTrace();
		}
		return newClass;
	}

	@Override
	public TypeAwareModelSlotInstance<XMLXSDModel, XSDMetaModel, XSDModelSlot> getModelSlotInstance(EditionSchemeAction action) {
		return (TypeAwareModelSlotInstance<XMLXSDModel, XSDMetaModel, XSDModelSlot>) super.getModelSlotInstance(action);
	}

}
