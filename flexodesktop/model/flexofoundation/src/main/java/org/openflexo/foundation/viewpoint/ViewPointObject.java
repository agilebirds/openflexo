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
import org.openflexo.foundation.FlexoObject;
import org.openflexo.foundation.FlexoServiceManager;
import org.openflexo.foundation.InnerResourceData;
import org.openflexo.foundation.technologyadapter.InformationSpace;
import org.openflexo.foundation.validation.FixProposal;
import org.openflexo.foundation.validation.ValidationError;
import org.openflexo.foundation.validation.ValidationIssue;
import org.openflexo.foundation.validation.ValidationModel;
import org.openflexo.foundation.validation.ValidationRule;
import org.openflexo.foundation.viewpoint.rm.ViewPointResource;
import org.openflexo.model.annotations.ImplementationClass;
import org.openflexo.model.annotations.ModelEntity;

/**
 * Represents an object which is part of the model of a ViewPoint
 * 
 * @author sylvain
 * 
 */
@ModelEntity(isAbstract = true)
@ImplementationClass(ViewPointObject.ViewPointObjectImpl.class)
public interface ViewPointObject extends FlexoObject, Bindable, InnerResourceData {

	@Override
	public FlexoServiceManager getServiceManager();

	public InformationSpace getInformationSpace();

	public ViewPoint getViewPoint();

	public ViewPointLibrary getViewPointLibrary();

	public ViewPointModelFactory getFactory();

	public String getFMLRepresentation();

	public String getStringRepresentation();

	public static abstract class ViewPointObjectImpl extends FlexoObjectImpl implements ViewPointObject {

		private static final Logger logger = Logger.getLogger(ViewPointObject.class.getPackage().getName());

		@Override
		public FlexoServiceManager getServiceManager() {
			return getViewPoint().getViewPointLibrary().getServiceManager();
		}

		@Override
		public ViewPointLibrary getViewPointLibrary() {
			return getViewPoint().getViewPointLibrary();
		}

		@Override
		public InformationSpace getInformationSpace() {
			if (getViewPoint().getViewPointLibrary() != null) {
				return getViewPoint().getViewPointLibrary().getServiceManager().getInformationSpace();
			}
			return null;
		}

		@Override
		public ViewPoint getResourceData() {
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

		@Override
		public abstract ViewPoint getViewPoint();

		// Voir du cote de GeneratorFormatter pour formatter tout ca
		public abstract String getFMLRepresentation(FMLRepresentationContext context);

		@Override
		public final String getFMLRepresentation() {
			return getFMLRepresentation(new FMLRepresentationContext());
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
						logger.info("Binding NOT valid: " + getBinding(object) + " for " + object.getStringRepresentation() + ". Reason: "
								+ getBinding(object).invalidBindingReason());
						DeleteBinding<C> deleteBinding = new DeleteBinding<C>(this);
						return new ValidationError<BindingMustBeValid<C>, C>(this, object, BindingMustBeValid.this.getNameKey(),
								deleteBinding);
					}
				}
				return null;
			}

			protected static class DeleteBinding<C extends ViewPointObject> extends FixProposal<BindingMustBeValid<C>, C> {

				private final BindingMustBeValid<C> rule;

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
				DataBinding<?> b = getBinding(object);
				if (b == null || !b.isSet()) {
					return new ValidationError<BindingIsRequiredAndMustBeValid<C>, C>(this, object,
							BindingIsRequiredAndMustBeValid.this.getNameKey());
				} else if (!b.isValid()) {
					logger.info(getClass().getName() + ": Binding NOT valid: " + b + " for " + object.getStringRepresentation()
							+ ". Reason: " + b.invalidBindingReason());
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

		@Override
		public ViewPointModelFactory getFactory() {
			return ((ViewPointResource) getResourceData().getResource()).getFactory();
		}

		@Override
		public String getStringRepresentation() {
			return getFactory().stringRepresentation(this);
		}
	}
}
