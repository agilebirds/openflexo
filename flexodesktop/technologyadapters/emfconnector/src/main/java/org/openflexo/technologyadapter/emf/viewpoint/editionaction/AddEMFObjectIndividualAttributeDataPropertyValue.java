/** Copyright (c) 2013, THALES SYSTEMES AEROPORTES - All Rights Reserved
 * Author : Gilles BesanÃ§on
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
import org.openflexo.foundation.ontology.IFlexoOntologyDataProperty;
import org.openflexo.foundation.ontology.IFlexoOntologyStructuralProperty;
import org.openflexo.foundation.ontology.IndividualOfClass;
import org.openflexo.foundation.view.TypeAwareModelSlotInstance;
import org.openflexo.foundation.view.action.EditionSchemeAction;
import org.openflexo.foundation.viewpoint.SetDataPropertyValueAction;
import org.openflexo.foundation.viewpoint.annotations.FIBPanel;
import org.openflexo.model.annotations.Getter;
import org.openflexo.model.annotations.ImplementationClass;
import org.openflexo.model.annotations.ModelEntity;
import org.openflexo.model.annotations.PropertyIdentifier;
import org.openflexo.model.annotations.Setter;
import org.openflexo.model.annotations.XMLAttribute;
import org.openflexo.model.annotations.XMLElement;
import org.openflexo.technologyadapter.emf.EMFModelSlot;
import org.openflexo.technologyadapter.emf.metamodel.EMFAttributeDataProperty;
import org.openflexo.technologyadapter.emf.metamodel.EMFMetaModel;
import org.openflexo.technologyadapter.emf.model.EMFModel;
import org.openflexo.technologyadapter.emf.model.EMFObjectIndividualAttributeDataPropertyValue;
import org.openflexo.toolbox.StringUtils;

/**
 * Assign a simple DataType value to the attribute of an object.
 * 
 * @author gbesancon
 * 
 */
@FIBPanel("Fib/AddEMFObjectIndividualAttributeDataPropertyValuePanel.fib")
@ModelEntity
@ImplementationClass(AddEMFObjectIndividualAttributeDataPropertyValue.AddEMFObjectIndividualAttributeDataPropertyValueImpl.class)
@XMLElement
public interface AddEMFObjectIndividualAttributeDataPropertyValue extends
		SetEMFPropertyValue<EMFObjectIndividualAttributeDataPropertyValue>, SetDataPropertyValueAction {

	@PropertyIdentifier(type = DataBinding.class)
	public static final String VALUE_KEY = "value";
	@PropertyIdentifier(type = String.class)
	public static final String DATA_PROPERTY_URI_KEY = "dataPropertyURI";

	@Override
	@Getter(value = VALUE_KEY)
	@XMLAttribute
	public DataBinding<?> getValue();

	@Override
	@Setter(VALUE_KEY)
	public void setValue(DataBinding<?> value);

	@Getter(value = DATA_PROPERTY_URI_KEY)
	@XMLAttribute
	public String _getDataPropertyURI();

	@Setter(DATA_PROPERTY_URI_KEY)
	public void _setDataPropertyURI(String dataPropertyURI);

	public static abstract class AddEMFObjectIndividualAttributeDataPropertyValueImpl extends
			SetEMFPropertyValueImpl<EMFObjectIndividualAttributeDataPropertyValue> implements
			AddEMFObjectIndividualAttributeDataPropertyValue {

		private String dataPropertyURI = null;
		private DataBinding<?> value;

		/**
		 * Constructor.
		 * 
		 * @param builder
		 */
		public AddEMFObjectIndividualAttributeDataPropertyValueImpl() {
			super();
		}

		@Override
		public Type getSubjectType() {
			if (getDataProperty() != null && getDataProperty().getDomain() instanceof IFlexoOntologyClass) {
				return IndividualOfClass.getIndividualOfClass((IFlexoOntologyClass) getDataProperty().getDomain());
			}
			return super.getSubjectType();
		}

		@Override
		public IFlexoOntologyStructuralProperty getProperty() {
			return getDataProperty();
		}

		@Override
		public void setProperty(IFlexoOntologyStructuralProperty aProperty) {
			setDataProperty((EMFAttributeDataProperty) aProperty);
		}

		@Override
		public IFlexoOntologyDataProperty getDataProperty() {
			if (getVirtualModel() != null && StringUtils.isNotEmpty(dataPropertyURI)) {
				return getVirtualModel().getOntologyDataProperty(dataPropertyURI);
			}
			return null;
		}

		@Override
		public void setDataProperty(IFlexoOntologyDataProperty ontologyProperty) {
			if (ontologyProperty != null) {
				dataPropertyURI = ontologyProperty.getURI();
			} else {
				dataPropertyURI = null;
			}
		}

		@Override
		public String _getDataPropertyURI() {
			if (getDataProperty() != null) {
				return getDataProperty().getURI();
			}
			return dataPropertyURI;
		}

		@Override
		public void _setDataPropertyURI(String dataPropertyURI) {
			this.dataPropertyURI = dataPropertyURI;
		}

		public Object getValue(EditionSchemeAction action) {
			try {
				return getValue().getBindingValue(action);
			} catch (TypeMismatchException e) {
				e.printStackTrace();
			} catch (NullReferenceException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			}
			return null;
		}

		public Type getType() {
			if (getDataProperty() != null) {
				return getDataProperty().getRange().getAccessedType();
			}
			return Object.class;
		};

		@Override
		public DataBinding<?> getValue() {
			if (value == null) {
				value = new DataBinding<Object>(this, getType(), BindingDefinitionType.GET) {
					@Override
					public Type getDeclaredType() {
						return getType();
					}
				};
				value.setBindingName("value");
			}
			return value;
		}

		@Override
		public void setValue(DataBinding<?> value) {
			if (value != null) {
				value = new DataBinding<Object>(value.toString(), this, getType(), BindingDefinitionType.GET) {
					@Override
					public Type getDeclaredType() {
						return getType();
					}
				};
				value.setBindingName("value");
			}
			this.value = value;
		}

		@Override
		public Type getAssignableType() {
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
			TypeAwareModelSlotInstance<EMFModel, EMFMetaModel, EMFModelSlot> modelSlotInstance = getModelSlotInstance(action);
			EMFModel model = modelSlotInstance.getAccessedResourceData();
			// // Add Attribute in EMF
			getSubject(action).getObject().eSet(((EMFAttributeDataProperty) getDataProperty()).getObject(), getValue(action));
			// // Instanciate Wrapper
			// result = model.getConverter().convertObjectIndividualAttributeDataPropertyValue(model, objectIndividual.getObject(),
			// attributeDataProperty.getObject());
			return result;
		}

	}
}
