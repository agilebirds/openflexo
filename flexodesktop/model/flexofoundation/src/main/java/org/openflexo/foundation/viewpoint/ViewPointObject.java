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
package org.openflexo.foundation.viewpoint;

import java.util.logging.Logger;

import org.openflexo.antar.binding.Bindable;
import org.openflexo.antar.binding.BindingFactory;
import org.openflexo.antar.binding.BindingModelChanged;
import org.openflexo.antar.binding.DataBinding;
import org.openflexo.foundation.XMLSerializableFlexoObject;
import org.openflexo.foundation.rm.FlexoProject;
import org.openflexo.foundation.technologyadapter.InformationSpace;
import org.openflexo.foundation.validation.FixProposal;
import org.openflexo.foundation.validation.Validable;
import org.openflexo.foundation.validation.ValidationError;
import org.openflexo.foundation.validation.ValidationIssue;
import org.openflexo.foundation.validation.ValidationModel;
import org.openflexo.foundation.validation.ValidationRule;
import org.openflexo.foundation.view.diagram.viewpoint.DiagramPaletteObject.DiagramPaletteBuilder;
import org.openflexo.foundation.view.diagram.viewpoint.ExampleDiagramObject.ExampleDiagramBuilder;
import org.openflexo.foundation.viewpoint.ViewPoint.ViewPointBuilder;
import org.openflexo.xmlcode.XMLMapping;

/**
 * Represents an object which is part of the model of a ViewPoint
 * 
 * @author sylvain
 * 
 */
public abstract class ViewPointObject extends XMLSerializableFlexoObject implements Bindable, Validable {

	private static final Logger logger = Logger.getLogger(ViewPointObject.class.getPackage().getName());

	// private ImportedOntology viewPointOntology = null;

	public ViewPointObject(ViewPointBuilder builder) {
		if (builder != null) {
			initializeDeserialization(builder);
		}
	}

	public ViewPointObject(ExampleDiagramBuilder builder) {
		if (builder != null) {
			initializeDeserialization(builder);
		}
	}

	public ViewPointObject(DiagramPaletteBuilder builder) {
		if (builder != null) {
			initializeDeserialization(builder);
		}
	}

	@Override
	public XMLMapping getXMLMapping() {
		if (getViewPointLibrary() != null) {
			return getViewPointLibrary().getViewPointModel();
		}
		return null;
	}

	/*@Override
	public void finalizeDeserialization(Object builder) {
		System.out.println("END deserialisation for " + getClass().getSimpleName());
		super.finalizeDeserialization(builder);
	}*/

	public ViewPointLibrary getViewPointLibrary() {
		if (getViewPoint() != null) {
			return getViewPoint().getViewPointLibrary();
		}
		return null;
	}

	public InformationSpace getInformationSpace() {
		if (getViewPointLibrary() != null) {
			return getViewPointLibrary().getFlexoServiceManager().getInformationSpace();
		}
		return null;
	}

	@Override
	public ViewPoint getXMLResourceData() {
		return getViewPoint();
	}

	@Override
	public ValidationModel getDefaultValidationModel() {
		if (getViewPoint() != null) {
			return getViewPoint().getDefaultValidationModel();
		}
		return null;
	}

	@Override
	public void setChanged() {
		super.setChanged();
		if (getViewPoint() != null) {
			getViewPoint().setIsModified();
		}
	}

	@Override
	public void notifiedBindingChanged(DataBinding<?> dataBinding) {
		if (getPropertyChangeSupport() != null) {
			if (dataBinding != null && dataBinding.getBindingName() != null) {
				getPropertyChangeSupport().firePropertyChange(dataBinding.getBindingName(), null, dataBinding);
			}
		}
	}

	@Override
	public void notifiedBindingDecoded(DataBinding<?> dataBinding) {
		// logger.info("Binding decoded: " + dataBinding);
	}

	public void notifyChange(String propertyName, Object oldValue, Object newValue) {
		if (getPropertyChangeSupport() != null) {
			getPropertyChangeSupport().firePropertyChange(propertyName, oldValue, newValue);
		}
	}

	@Override
	public BindingFactory getBindingFactory() {
		if (getViewPoint() != null) {
			return getViewPoint().getBindingFactory();
		}
		return null;
	}

	public void notifyBindingModelChanged() {
		getPropertyChangeSupport().firePropertyChange(BindingModelChanged.BINDING_MODEL_CHANGED, null, null);
	}

	public LocalizedDictionary getLocalizedDictionary() {
		return getViewPoint().getLocalizedDictionary();
	}

	public abstract ViewPoint getViewPoint();

	public abstract String getLanguageRepresentation();

	@Override
	public String getFullyQualifiedName() {
		return (getViewPoint() != null ? getViewPoint().getFullyQualifiedName() : "null") + "#" + getClass().getSimpleName();
	}

	public static abstract class BindingMustBeValid<C extends ViewPointObject> extends ValidationRule<BindingMustBeValid<C>, C> {
		public BindingMustBeValid(String ruleName, Class<C> clazz) {
			super(clazz, ruleName);
		}

		public abstract DataBinding<?> getBinding(C object);

		@Override
		public ValidationIssue<BindingMustBeValid<C>, C> applyValidation(C object) {
			if (getBinding(object) != null && getBinding(object).isSet()) {
				if (!getBinding(object).isValid()) {
					logger.info("Binding NOT valid: " + getBinding(object) + " for " + object.getFullyQualifiedName() + ". Reason: "
							+ getBinding(object).invalidBindingReason());
					DeleteBinding<C> deleteBinding = new DeleteBinding<C>(this);
					return new ValidationError<BindingMustBeValid<C>, C>(this, object, BindingMustBeValid.this.getNameKey(), deleteBinding);
				}
			}
			return null;
		}

		protected static class DeleteBinding<C extends ViewPointObject> extends FixProposal<BindingMustBeValid<C>, C> {

			private BindingMustBeValid<C> rule;

			public DeleteBinding(BindingMustBeValid<C> rule) {
				super("delete_this_binding");
				this.rule = rule;
			}

			@Override
			protected void fixAction() {
				rule.getBinding(getObject()).reset();
			}

		}
	}

	public static abstract class BindingIsRequiredAndMustBeValid<C extends ViewPointObject> extends
			ValidationRule<BindingIsRequiredAndMustBeValid<C>, C> {
		public BindingIsRequiredAndMustBeValid(String ruleName, Class<C> clazz) {
			super(clazz, ruleName);
		}

		public abstract DataBinding<?> getBinding(C object);

		@Override
		public ValidationIssue<BindingIsRequiredAndMustBeValid<C>, C> applyValidation(C object) {
			if (getBinding(object) == null || !getBinding(object).isSet()) {
				return new ValidationError<BindingIsRequiredAndMustBeValid<C>, C>(this, object,
						BindingIsRequiredAndMustBeValid.this.getNameKey());
			} else if (!getBinding(object).isValid()) {
				logger.info(getClass().getName() + ": Binding NOT valid: " + getBinding(object) + " for " + object.getFullyQualifiedName()
						+ ". Reason: " + getBinding(object).invalidBindingReason());
				return new ValidationError<BindingIsRequiredAndMustBeValid<C>, C>(this, object,
						BindingIsRequiredAndMustBeValid.this.getNameKey());
			}
			return null;
		}

		public String retrieveIssueDetails(C object) {
			if (getBinding(object) == null || !getBinding(object).isSet()) {
				return "Binding not set";
			} else if (!getBinding(object).isValid()) {
				return "Binding not valid [" + getBinding(object) + "], reason: " + getBinding(object).invalidBindingReason();
			}
			return null;
		}
	}

	@Deprecated
	public FlexoProject getProject() {
		return null;
	}

}
