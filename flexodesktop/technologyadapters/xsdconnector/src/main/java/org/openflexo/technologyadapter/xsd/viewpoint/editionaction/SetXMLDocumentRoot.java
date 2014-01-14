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
import java.util.logging.Logger;

import org.openflexo.antar.expr.NullReferenceException;
import org.openflexo.antar.expr.TypeMismatchException;
import org.openflexo.foundation.view.ModelSlotInstance;
import org.openflexo.foundation.view.action.EditionSchemeAction;
import org.openflexo.foundation.viewpoint.ProcedureAction;
import org.openflexo.technologyadapter.xsd.XSDModelSlot;
import org.openflexo.technologyadapter.xsd.model.XMLXSDModel;
import org.openflexo.technologyadapter.xsd.model.XSOntIndividual;

@ModelEntity
@ImplementationClass(SetXMLDocumentRoot.SetXMLDocumentRootImpl.class)
@XMLElement
public interface SetXMLDocumentRoot extends ProcedureAction<XSDModelSlot, XSOntIndividual>{

@PropertyIdentifier(type=DataBinding.class)
public static final String PARAMETER_KEY = "parameter";

@Getter(value=PARAMETER_KEY)
@XMLAttribute
public DataBinding getParameter();

@Setter(PARAMETER_KEY)
public void setParameter(DataBinding parameter);


public static abstract  class SetXMLDocumentRootImpl extends ProcedureAction<XSDModelSlot, XSOntIndividual>Impl implements SetXMLDocumentRoot
{

	private static final Logger logger = Logger.getLogger(SetXMLDocumentRoot.class.getPackage().getName());

	public SetXMLDocumentRootImpl() {
		super();
	}

	@Override
	public XSOntIndividual performAction(EditionSchemeAction action) {

		ModelSlotInstance<XSDModelSlot, XMLXSDModel> modelSlotInstance = (ModelSlotInstance<XSDModelSlot, XMLXSDModel>) getModelSlotInstance(action);
		XMLXSDModel model = modelSlotInstance.getAccessedResourceData();
		XSDModelSlot modelSlot = modelSlotInstance.getModelSlot();

		XSOntIndividual rootIndiv = null;

		try {
			Object o = getParameter().getBindingValue(action);
			if (o instanceof XSOntIndividual) {
				rootIndiv = (XSOntIndividual) o;
			} else {
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

		if (rootIndiv != null) {
			model.setRoot(rootIndiv);
		}

		return rootIndiv;
	}

}
}
