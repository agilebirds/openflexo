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

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Type;

import org.openflexo.antar.binding.DataBinding;
import org.openflexo.antar.binding.DataBinding.BindingDefinitionType;
import org.openflexo.antar.expr.NullReferenceException;
import org.openflexo.antar.expr.TypeMismatchException;
import org.openflexo.foundation.ontology.IFlexoOntologyClass;
import org.openflexo.foundation.ontology.IFlexoOntologyConcept;
import org.openflexo.foundation.ontology.IFlexoOntologyObjectProperty;
import org.openflexo.foundation.ontology.IFlexoOntologyStructuralProperty;
import org.openflexo.foundation.ontology.IndividualOfClass;
import org.openflexo.foundation.view.TypeSafeModelSlotInstance;
import org.openflexo.foundation.view.action.EditionSchemeAction;
import org.openflexo.foundation.viewpoint.SetObjectPropertyValueAction;
import org.openflexo.foundation.viewpoint.VirtualModel;
import org.openflexo.technologyadapter.emf.EMFModelSlot;
import org.openflexo.technologyadapter.emf.metamodel.EMFMetaModel;
import org.openflexo.technologyadapter.emf.metamodel.EMFReferenceObjectProperty;
import org.openflexo.technologyadapter.emf.model.EMFModel;
import org.openflexo.technologyadapter.emf.model.EMFObjectIndividual;
import org.openflexo.technologyadapter.emf.model.EMFObjectIndividualReferenceObjectPropertyValue;
import org.openflexo.toolbox.StringUtils;

/**
 * Add an Instance value to the reference of an object.
 * 
 * @author gbesancon
 * 
 */
public class AddEMFObjectIndividualReferenceObjectPropertyValue<T> extends
		SetEMFPropertyValue<EMFObjectIndividualReferenceObjectPropertyValue> implements SetObjectPropertyValueAction {

	private String objectPropertyURI = null;
	private DataBinding<Object> object;

	/**
	 * Constructor.
	 * 
	 * @param builder
	 */
	public AddEMFObjectIndividualReferenceObjectPropertyValue(VirtualModel.VirtualModelBuilder builder) {
		super(builder);
	}

	/**
	 * Follow the link.
	 * 
	 * @see org.openflexo.foundation.viewpoint.AssignableAction#getEditionActionType()
	 */
	@Override
	public EditionActionType getEditionActionType() {
		return EditionActionType.AddObjectPropertyStatement;
	}

	@Override
	public Type getSubjectType() {
		if (getObjectProperty() != null && getObjectProperty().getDomain() instanceof IFlexoOntologyClass) {
			return IndividualOfClass.getIndividualOfClass((IFlexoOntologyClass) getObjectProperty().getDomain());
		}
		return super.getSubjectType();
	}

	@Override
	public IFlexoOntologyStructuralProperty getProperty() {
		return getObjectProperty();
	}

	@Override
	public void setProperty(IFlexoOntologyStructuralProperty aProperty) {
		setObjectProperty((EMFReferenceObjectProperty) aProperty);
	}

	@Override
	public IFlexoOntologyObjectProperty getObjectProperty() {
		if (getVirtualModel() != null && StringUtils.isNotEmpty(objectPropertyURI)) {
			return getVirtualModel().getOntologyObjectProperty(objectPropertyURI);
		}
		return null;
	}

	@Override
	public void setObjectProperty(IFlexoOntologyObjectProperty ontologyProperty) {
		if (ontologyProperty != null) {
			objectPropertyURI = ontologyProperty.getURI();
		} else {
			objectPropertyURI = null;
		}
	}

	public String _getObjectPropertyURI() {
		if (getObjectProperty() != null) {
			return getObjectProperty().getURI();
		}
		return objectPropertyURI;
	}

	public void _setObjectPropertyURI(String objectPropertyURI) {
		this.objectPropertyURI = objectPropertyURI;
	}

	public EMFObjectIndividual getObject(EditionSchemeAction action) {
		try {
			return (EMFObjectIndividual) getObject().getBindingValue(action);
		} catch (TypeMismatchException e) {
			e.printStackTrace();
		} catch (NullReferenceException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
		return null;
	}

	public Type getObjectType() {
		if (getObjectProperty() != null && getObjectProperty().getRange() instanceof IFlexoOntologyClass) {
			return IndividualOfClass.getIndividualOfClass((IFlexoOntologyClass) getObjectProperty().getRange());
		}
		return IFlexoOntologyConcept.class;
	}

	@Override
	public DataBinding<Object> getObject() {
		if (object == null) {
			object = new DataBinding<Object>(this, getObjectType(), BindingDefinitionType.GET) {
				@Override
				public Type getDeclaredType() {
					return getObjectType();
				}
			};
			object.setBindingName("object");
		}
		return object;
	}

	@Override
	public void setObject(DataBinding<Object> object) {
		if (object != null) {
			object = new DataBinding<Object>(object.toString(), this, getObjectType(), BindingDefinitionType.GET) {
				@Override
				public Type getDeclaredType() {
					return getObjectType();
				}
			};
			object.setBindingName("object");
		}
		this.object = object;
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
	public EMFObjectIndividualReferenceObjectPropertyValue performAction(EditionSchemeAction action) {
		EMFObjectIndividualReferenceObjectPropertyValue result = null;
		TypeSafeModelSlotInstance<EMFModel, EMFMetaModel, EMFModelSlot> modelSlotInstance = getModelSlotInstance(action);
		EMFModel model = modelSlotInstance.getResourceData();
		// Add Reference in EMF
		getSubject(action).getObject().eSet(((EMFReferenceObjectProperty) getObjectProperty()).getObject(), getObject(action));
		// if (referenceObjectProperty.getObject().getUpperBound() != 1) {
		// List<T> values = (List<T>) objectIndividual.getObject().eGet(referenceObjectProperty.getObject());
		// values.add(value);
		// } else {
		// objectIndividual.getObject().eSet(referenceObjectProperty.getObject(), value);
		// }
		// // Instanciate Wrapper
		// result = model.getConverter().convertObjectIndividualReferenceObjectPropertyValue(model, objectIndividual.getObject(),
		// referenceObjectProperty.getObject());
		return result;
	}

}
