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

import java.lang.reflect.Type;
import java.util.logging.Logger;

import org.openflexo.foundation.view.ModelSlotInstance;
import org.openflexo.foundation.view.action.EditionSchemeAction;
import org.openflexo.foundation.viewpoint.AssignableAction;
import org.openflexo.foundation.viewpoint.annotations.FIBPanel;
import org.openflexo.technologyadapter.xsd.XSDModelSlot;
import org.openflexo.technologyadapter.xsd.model.XMLXSDModel;
import org.openflexo.technologyadapter.xsd.model.XSOntIndividual;

@FIBPanel("Fib/GetXMLDocumentRoot.fib")
@ModelEntity
@ImplementationClass(GetXMLDocumentRoot.GetXMLDocumentRootImpl.class)
@XMLElement
public interface GetXMLDocumentRoot extends AssignableAction<XSDModelSlot, XSOntIndividual>{


public static abstract  class GetXMLDocumentRootImpl extends AssignableAction<XSDModelSlot, XSOntIndividual>Impl implements GetXMLDocumentRoot
{

	private static final Logger logger = Logger.getLogger(GetXMLDocumentRoot.class.getPackage().getName());

	public GetXMLDocumentRootImpl() {
		super();
	}

	@Override
	public XSOntIndividual performAction(EditionSchemeAction action) {

		ModelSlotInstance<XSDModelSlot, XMLXSDModel> modelSlotInstance = (ModelSlotInstance<XSDModelSlot, XMLXSDModel>) getModelSlotInstance(action);
		XMLXSDModel model = modelSlotInstance.getAccessedResourceData();

		XSOntIndividual rootIndiv = (XSOntIndividual) model.getRoot();

		return rootIndiv;
	}

	@Override
	public Type getAssignableType() {
		return Object.class;
	}

}
}
