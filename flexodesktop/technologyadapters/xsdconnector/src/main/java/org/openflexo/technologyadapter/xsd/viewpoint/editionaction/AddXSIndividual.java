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
import org.openflexo.foundation.ontology.IFlexoOntologyClass;
import org.openflexo.foundation.view.action.EditionSchemeAction;
import org.openflexo.foundation.viewpoint.AddIndividual;
import org.openflexo.foundation.viewpoint.DataPropertyAssertion;
import org.openflexo.foundation.viewpoint.ObjectPropertyAssertion;
import org.openflexo.foundation.viewpoint.VirtualModel;
import org.openflexo.technologyadapter.xsd.model.XMLModel;
import org.openflexo.technologyadapter.xsd.model.XSDMetaModel;
import org.openflexo.technologyadapter.xsd.model.XSOntClass;
import org.openflexo.technologyadapter.xsd.model.XSOntIndividual;

public class AddXSIndividual extends AddIndividual<XMLModel, XSDMetaModel, XSOntIndividual> {

	@Override
	public void setOntologyClass(IFlexoOntologyClass ontologyClass) {
		// TODO Auto-generated method stub
		super.setOntologyClass(ontologyClass);
		if ( ontologyClassURI == null) {
			logger.info ("Ya comme un beugue");
		}
	}

	private static final Logger logger = Logger.getLogger(AddXSIndividual.class.getPackage().getName());

	private String dataPropertyURI = null;

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
		} catch (InvocationTargetException e1) {
			e1.printStackTrace();
		}
		// System.out.println("individualName="+individualName);
		XSOntIndividual newIndividual = null;
		try {
			newIndividual = getModelSlotInstance(action).getModel().createOntologyIndividual(individualName, father);
			logger.info("********* Added individual " + newIndividual.getName() + " as " + father);

			for (DataPropertyAssertion dataPropertyAssertion : getDataAssertions()) {
				if (dataPropertyAssertion.evaluateCondition(action)) {
					// ... TODO
				}
			}
			for (ObjectPropertyAssertion objectPropertyAssertion : getObjectAssertions()) {
				if (objectPropertyAssertion.evaluateCondition(action)) {
					// ... TODO
				}
			}

			return newIndividual;
		} catch (DuplicateURIException e) {
			e.printStackTrace();
			return null;
		}
	}

}
