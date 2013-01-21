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
import java.util.List;

import org.openflexo.foundation.view.action.EditionSchemeAction;
import org.openflexo.foundation.viewpoint.AssignableAction;
import org.openflexo.foundation.viewpoint.ViewPoint.ViewPointBuilder;
import org.openflexo.technologyadapter.emf.metamodel.EMFAttributeObjectProperty;
import org.openflexo.technologyadapter.emf.metamodel.EMFMetaModel;
import org.openflexo.technologyadapter.emf.model.EMFModel;
import org.openflexo.technologyadapter.emf.model.EMFObjectIndividual;
import org.openflexo.technologyadapter.emf.model.EMFObjectIndividualAttributeObjectPropertyValue;

/**
 * Remove an Enum value from the attribute of an object.
 * 
 * @author gbesancon
 * 
 */
public class RemoveEMFObjectIndividualAttributeObjectPropertyValue<T> extends
		AssignableAction<EMFModel, EMFMetaModel, EMFObjectIndividualAttributeObjectPropertyValue> {

	/**
	 * Constructor.
	 * 
	 * @param builder
	 */
	public RemoveEMFObjectIndividualAttributeObjectPropertyValue(ViewPointBuilder builder) {
		super(builder);
	}

	/**
	 * Follow the link.
	 * 
	 * @see org.openflexo.foundation.viewpoint.AssignableAction#getEditionActionType()
	 */
	@Override
	public EditionActionType getEditionActionType() {
		return EditionActionType.AddObjectPropertyStatement;// FIXME
	}

	/**
	 * Follow the link.
	 * 
	 * @see org.openflexo.foundation.viewpoint.AssignableAction#getAssignableType()
	 */
	@Override
	public Type getAssignableType() {
		if (value != null) {
			return value.getClass();
		}
		return Object.class;
	}

	/**
	 * Follow the link.
	 * 
	 * @see org.openflexo.foundation.viewpoint.EditionAction#performAction(org.openflexo.foundation.view.action.EditionSchemeAction)
	 */
	@Override
	public EMFObjectIndividualAttributeObjectPropertyValue performAction(EditionSchemeAction action) {
		EMFModel model = (EMFModel) objectIndividual.getOntology();
		if (attributeObjectProperty.getObject().getUpperBound() != 1) {
			List<T> values = (List<T>) objectIndividual.getObject().eGet(attributeObjectProperty.getObject());
			values.remove(value);
		} else {
			objectIndividual.getObject().eUnset(attributeObjectProperty.getObject());
		}
		return model.getConverter().convertObjectIndividualAttributeObjectPropertyValue(model, objectIndividual.getObject(),
				attributeObjectProperty.getObject());
	}

	/**
	 * Follow the link.
	 * 
	 * @see org.openflexo.foundation.viewpoint.EditionAction#finalizePerformAction(org.openflexo.foundation.view.action.EditionSchemeAction,
	 *      java.lang.Object)
	 */
	@Override
	public void finalizePerformAction(EditionSchemeAction action, EMFObjectIndividualAttributeObjectPropertyValue initialContext) {
	}

	protected EMFObjectIndividual objectIndividual;

	/**
	 * Setter of objectIndividual.
	 * 
	 * @param objectIndividual
	 *            the objectIndividual to set
	 */
	public void setObjectIndividual(EMFObjectIndividual objectIndividual) {
		this.objectIndividual = objectIndividual;
	}

	protected EMFAttributeObjectProperty attributeObjectProperty;

	/**
	 * Setter of attributeObjectProperty.
	 * 
	 * @param attributeObjectProperty
	 *            the attributeObjectProperty to set
	 */
	public void setAttributeObjectProperty(EMFAttributeObjectProperty attributeObjectProperty) {
		this.attributeObjectProperty = attributeObjectProperty;
	}

	protected T value;

	/**
	 * Setter of value.
	 * 
	 * @param value
	 *            the value to set
	 */
	public void setValue(T value) {
		this.value = value;
	}
}
