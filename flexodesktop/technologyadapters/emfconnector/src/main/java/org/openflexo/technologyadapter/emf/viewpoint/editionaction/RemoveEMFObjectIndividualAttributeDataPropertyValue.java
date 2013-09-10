/** Copyright (c) 2013, THALES SYSTEMES AEROPORTES - All Rights Reserved
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
 * Additional permission under GNU GPL version 3 section 7
 *
 * If you modify this Program, or any covered work, by linking or 
 * combining it with eclipse EMF (or a modified version of that library), 
 * containing parts covered by the terms of EPL 1.0, the licensors of this 
 * Program grant you additional permission to convey the resulting work.
 *
 * Contributors :
 *
 */
package org.openflexo.technologyadapter.emf.viewpoint.editionaction;

import java.lang.reflect.Type;

import org.openflexo.foundation.view.action.EditionSchemeAction;
import org.openflexo.foundation.viewpoint.AssignableAction;
import org.openflexo.foundation.viewpoint.VirtualModel;
import org.openflexo.foundation.viewpoint.annotations.FIBPanel;
import org.openflexo.technologyadapter.emf.EMFModelSlot;
import org.openflexo.technologyadapter.emf.model.EMFObjectIndividualAttributeDataPropertyValue;

/**
 * Remove a simple DataType value from the attribute of an object.
 * 
 * @author gbesancon
 * 
 */
@FIBPanel("Fib/RemoveEMFObjectIndividualAttributeDataPropertyValuePanel.fib")
public class RemoveEMFObjectIndividualAttributeDataPropertyValue extends
		AssignableAction<EMFModelSlot, EMFObjectIndividualAttributeDataPropertyValue> {

	/**
	 * Constructor.
	 * 
	 * @param builder
	 */
	public RemoveEMFObjectIndividualAttributeDataPropertyValue(VirtualModel.VirtualModelBuilder builder) {
		super(builder);
	}

	/**
	 * Follow the link.
	 * 
	 * @see org.openflexo.foundation.viewpoint.AssignableAction#getAssignableType()
	 */
	@Override
	public Type getAssignableType() {
		// if (value != null) {
		// return value.getClass();
		// }
		return Object.class;
	}

	/**
	 * Follow the link.
	 * 
	 * @see org.openflexo.foundation.viewpoint.EditionAction#performAction(org.openflexo.foundation.view.action.EditionSchemeAction)
	 */
	@Override
	public EMFObjectIndividualAttributeDataPropertyValue performAction(EditionSchemeAction action) {
		EMFObjectIndividualAttributeDataPropertyValue result = null;
		// ModelSlotInstance<EMFModel, EMFMetaModel> modelSlotInstance = getModelSlotInstance(action);
		// EMFModel model = modelSlotInstance.getModel();
		// // Remove Attribute in EMF
		// if (attributeDataProperty.getObject().getUpperBound() != 1) {
		// List<T> values = (List<T>) objectIndividual.getObject().eGet(attributeDataProperty.getObject());
		// values.remove(value);
		// } else {
		// objectIndividual.getObject().eUnset(attributeDataProperty.getObject());
		// }
		// // Instanciate Wrapper
		// result = model.getConverter().convertObjectIndividualAttributeDataPropertyValue(model, objectIndividual.getObject(),
		// attributeDataProperty.getObject());
		return result;
	}

}
