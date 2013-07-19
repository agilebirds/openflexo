/*
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

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Type;
import java.util.logging.Logger;

import org.openflexo.antar.binding.BindingEvaluationContext;
import org.openflexo.antar.binding.BindingModel;
import org.openflexo.antar.binding.BindingVariable;
import org.openflexo.antar.binding.DataBinding;
import org.openflexo.antar.expr.NullReferenceException;
import org.openflexo.antar.expr.TypeMismatchException;
import org.openflexo.foundation.ontology.DuplicateURIException;
import org.openflexo.foundation.ontology.IFlexoOntologyClass;
import org.openflexo.foundation.view.ModelSlotInstance;
import org.openflexo.foundation.view.action.EditionSchemeAction;
import org.openflexo.foundation.viewpoint.AddIndividual;
import org.openflexo.foundation.viewpoint.AssignableAction;
import org.openflexo.foundation.viewpoint.DataPropertyAssertion;
import org.openflexo.foundation.viewpoint.EditionAction;
import org.openflexo.foundation.viewpoint.ProcedureAction;
import org.openflexo.foundation.viewpoint.ObjectPropertyAssertion;
import org.openflexo.foundation.viewpoint.VirtualModel;
import org.openflexo.foundation.viewpoint.EditionAction.EditionActionType;
import org.openflexo.foundation.viewpoint.ViewPointObject.BindingIsRequiredAndMustBeValid;
import org.openflexo.foundation.viewpoint.VirtualModel.VirtualModelBuilder;
import org.openflexo.technologyadapter.xsd.XSDModelSlot;
import org.openflexo.technologyadapter.xsd.metamodel.XSDMetaModel;
import org.openflexo.technologyadapter.xsd.metamodel.XSOntClass;
import org.openflexo.technologyadapter.xsd.metamodel.XSOntDataProperty;
import org.openflexo.technologyadapter.xsd.model.XMLXSDModel;
import org.openflexo.technologyadapter.xsd.model.XSOntIndividual;

public class SetXMLDocumentRoot extends ProcedureAction<XSDModelSlot, XSOntIndividual> {


	private static final Logger logger = Logger.getLogger(SetXMLDocumentRoot.class.getPackage().getName());


	public SetXMLDocumentRoot(VirtualModelBuilder builder) {
		super(builder);
	}


	@Override
	public XSOntIndividual performAction(EditionSchemeAction action) {

		ModelSlotInstance<XSDModelSlot, XMLXSDModel> modelSlotInstance = (ModelSlotInstance<XSDModelSlot, XMLXSDModel>) getModelSlotInstance(action);
		XMLXSDModel model = modelSlotInstance.getResourceData();
		XSDModelSlot modelSlot = (XSDModelSlot) modelSlotInstance.getModelSlot();

		XSOntIndividual rootIndiv = null;
		
		try {
			Object o = getParameter().getBindingValue(action);
			if (o instanceof XSOntIndividual){
				rootIndiv = (XSOntIndividual) o;
			}
			else{
				logger.warning("Invalid value in Binding :" + getParameter().getUnparsedBinding());
			}
		} catch (TypeMismatchException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NullReferenceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		if (rootIndiv != null){
			model.setRoot(rootIndiv);
		}
		
		return rootIndiv;
	}



}
