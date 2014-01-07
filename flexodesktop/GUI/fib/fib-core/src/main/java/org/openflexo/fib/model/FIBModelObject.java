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
package org.openflexo.fib.model;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Hashtable;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openflexo.antar.binding.Bindable;
import org.openflexo.antar.binding.BindingFactory;
import org.openflexo.antar.binding.BindingModel;
import org.openflexo.antar.binding.DataBinding;
import org.openflexo.fib.FIBLibrary;
import org.openflexo.fib.model.validation.FixProposal;
import org.openflexo.fib.model.validation.ProblemIssue;
import org.openflexo.fib.model.validation.ValidationError;
import org.openflexo.fib.model.validation.ValidationIssue;
import org.openflexo.fib.model.validation.ValidationReport;
import org.openflexo.fib.model.validation.ValidationRule;
import org.openflexo.fib.model.validation.ValidationWarning;
import org.openflexo.fib.utils.LocalizedDelegateGUIImpl;
import org.openflexo.model.annotations.Adder;
import org.openflexo.model.annotations.Finder;
import org.openflexo.model.annotations.Getter;
import org.openflexo.model.annotations.Getter.Cardinality;
import org.openflexo.model.annotations.ImplementationClass;
import org.openflexo.model.annotations.ModelEntity;
import org.openflexo.model.annotations.PropertyIdentifier;
import org.openflexo.model.annotations.Remover;
import org.openflexo.model.annotations.Setter;
import org.openflexo.model.annotations.XMLAttribute;
import org.openflexo.model.factory.AccessibleProxyObject;
import org.openflexo.model.factory.CloneableProxyObject;
import org.openflexo.model.factory.DeletableProxyObject;
import org.openflexo.model.factory.EmbeddingType;
import org.openflexo.toolbox.FileResource;
import org.openflexo.toolbox.StringUtils;

@ModelEntity(isAbstract = true)
@ImplementationClass(FIBModelObject.FIBModelObjectImpl.class)
public interface FIBModelObject extends Bindable, AccessibleProxyObject, CloneableProxyObject, DeletableProxyObject {

	@PropertyIdentifier(type = String.class)
	public static final String NAME_KEY = "name";
	@PropertyIdentifier(type = String.class)
	public static final String DESCRIPTION_KEY = "description";
	@PropertyIdentifier(type = List.class)
	public static final String PARAMETERS_KEY = "parameters";

	@Getter(value = NAME_KEY)
	@XMLAttribute
	public String getName();

	@Setter(NAME_KEY)
	public void setName(String name);

	@Getter(value = DESCRIPTION_KEY)
	public String getDescription();

	@Setter(DESCRIPTION_KEY)
	public void setDescription(String description);

	@Getter(value = PARAMETERS_KEY, cardinality = Cardinality.LIST)
	public List<FIBParameter> getParameters();

	@Setter(PARAMETERS_KEY)
	public void setParameters(List<FIBParameter> parameters);

	@Adder(PARAMETERS_KEY)
	public void addToParameters(FIBParameter aParameter);

	@Remover(PARAMETERS_KEY)
	public void removeFromParameters(FIBParameter aParameter);

	@Finder(collection = PARAMETERS_KEY, attribute = FIBParameter.NAME_KEY)
	public String getParameter(String parameterName);

	public FIBParameter createNewParameter();

	public FIBModelFactory getFactory();

	public boolean isValid();

	public ValidationReport validate();

	public void validate(ValidationReport report);

	public Collection<? extends FIBModelObject> getEmbeddedObjects();

	public List<FIBModelObject> getObjectsWithName(String aName);

	public String generateUniqueName(String baseName);

	public String getBaseName();

	/**
	 * Return the FIBComponent this model objects refer to
	 * 
	 * @return
	 */
	public FIBComponent getComponent();

	public void notify(FIBModelNotification notification);

	public static abstract class FIBModelObjectImpl implements FIBModelObject {

		private static final Logger logger = Logger.getLogger(FIBModelObject.class.getPackage().getName());

		public static final String DELETED_PROPERTY = "Deleted";

		// Instanciate a new localizer in directory src/dev/resources/FIBLocalizer
		// Little hack to be removed: linked to parent localizer (which is Openflexo main localizer)
		public static LocalizedDelegateGUIImpl LOCALIZATION = new LocalizedDelegateGUIImpl(new FileResource("FIBLocalized"),
				new LocalizedDelegateGUIImpl(new FileResource("Localized"), null, false), true);

		// private String name;
		// private String description;
		// private boolean isDeleted = false;

		// private final List<FIBParameter> parameters = new Vector<FIBParameter>();

		// private final PropertyChangeSupport pcSupport;

		public FIBModelObjectImpl() {
			super();
			// pcSupport = new PropertyChangeSupport(this);
		}

		@Override
		public FIBModelFactory getFactory() {
			return FIBLibrary.instance().getFIBModelFactory();
		}

		/*@Override
		public PropertyChangeSupport getPropertyChangeSupport() {
			return pcSupport;
		}*/

		/*@Override
		public String getDeletedProperty() {
			return DELETED_PROPERTY;
		}*/

		/*public void delete() {
			isDeleted = true;
			getPropertyChangeSupport().firePropertyChange(DELETED_PROPERTY, this, null);
		}

		@Override
		public boolean isDeleted() {
			return isDeleted;
		}*/

		/*@Override
		public String getName() {
			return name;
		}

		@Override
		public void setName(String name) {
			FIBAttributeNotification<String> notification = requireChange(NAME_KEY, name);
			if (notification != null) {
				this.name = name;
				hasChanged(notification);
			}
		}*/

		/*@Override
		public String getDescription() {
			return description;
		}

		@Override
		public void setDescription(String description) {
			FIBAttributeNotification<String> notification = requireChange(Parameters.description, description);
			if (notification != null) {
				this.description = description;
				hasChanged(notification);
			}
		}*/

		/*public String getParameter(String parameterName) {
			for (FIBParameter p : parameters) {
				if (parameterName.equals(p.getName())) {
					return p.getValue();
				}
			}
			return null;
		}*/

		/*@Override
		public List<FIBParameter> getParameters() {
			return parameters;
		}

		@Override
		public void setParameters(List<FIBParameter> parameters) {
			FIBAttributeNotification<List<FIBParameter>> notification = requireChange(Parameters.parameters, parameters);
			if (notification != null) {
				this.parameters = parameters;
				hasChanged(notification);
			}
		}

		@Override
		public void addToParameters(FIBParameter p) {
			parameters.add(p);
			getPropertyChangeSupport().firePropertyChange(Parameters.parameters.name(), null, parameters);
		}

		@Override
		public void removeFromParameters(FIBParameter p) {
			parameters.remove(p);
			getPropertyChangeSupport().firePropertyChange(Parameters.parameters.name(), null, parameters);
		}*/

		@Override
		public FIBParameter createNewParameter() {
			FIBParameter returned = getFactory().newInstance(FIBParameter.class);
			returned.setName("param");
			returned.setValue("value");
			addToParameters(returned);
			return returned;
		}

		public void deleteParameter(FIBParameter p) {
			removeFromParameters(p);
		}

		public boolean isParameterAddable() {
			return true;
		}

		public boolean isParameterDeletable(FIBParameter p) {
			return true;
		}

		// public abstract FIBComponent getRootComponent();

		/**
		 * Return the FIBComponent this model objects refer to
		 * 
		 * @return
		 */
		@Override
		public abstract FIBComponent getComponent();

		@Override
		public BindingModel getBindingModel() {
			return getComponent().getBindingModel();
		}

		@Override
		public BindingFactory getBindingFactory() {
			if (getComponent() != null) {
				return getComponent().getBindingFactory();
			}
			return FIBLibrary.instance().getBindingFactory();
		}

		/**
		 * Called when supplied data binding has been decoded (syntaxic and semantics analysis performed)
		 * 
		 * @param dataBinding
		 */
		@Override
		public void notifiedBindingDecoded(DataBinding<?> dataBinding) {

		}

		/*public void initializeDeserialization() {

		}

		public void finalizeDeserialization() {
		}*/

		// *******************************************************************************
		// * Utils *
		// *******************************************************************************

		protected <T extends Object> void notifyChange(String key, T oldValue, T newValue) {
			// Never notify unchanged values
			if (oldValue != null && newValue != null && oldValue.equals(newValue)) {
				return;
			}
			getPropertyChangeSupport().firePropertyChange(key, oldValue, newValue);
		}

		protected <T extends Object> FIBPropertyNotification<T> requireChange(String key, T value) {
			FIBProperty<T> property = (FIBProperty<T>) FIBProperty.getFIBProperty(getClass(), key);
			if (property == null) {
				logger.warning("Cannot find property " + property + " in " + getClass());
			}
			T oldValue = (T) objectForKey(key);
			if (oldValue == null) {
				if (value == null) {
					return null; // No change
				} else {
					return new FIBPropertyNotification<T>(property, oldValue, value);
				}
			} else {
				if (oldValue.equals(value)) {
					return null; // No change
				} else {
					return new FIBPropertyNotification<T>(property, oldValue, value);
				}
			}
		}

		@Override
		public void notify(FIBModelNotification notification) {
			hasChanged(notification);
		}

		protected void hasChanged(FIBModelNotification notification) {
			if (logger.isLoggable(Level.FINE)) {
				logger.fine("Change attribute " + notification.getAttributeName() + " for object " + this + " was: "
						+ notification.oldValue() + " is now: " + notification.newValue());
			}
			getPropertyChangeSupport()
					.firePropertyChange(notification.getAttributeName(), notification.oldValue(), notification.newValue());
		}

		/**
		 * Called when supplied data binding changed its value
		 * 
		 * @param dataBinding
		 */
		@Override
		public void notifiedBindingChanged(DataBinding<?> binding) {
		}

		public static boolean equals(Object o1, Object o2) {
			if (o1 == o2) {
				return true;
			}
			if (o1 == null) {
				return o2 == null;
			} else {
				return o1.equals(o2);
			}
		}

		public static boolean notEquals(Object o1, Object o2) {
			return !equals(o1, o2);
		}

		@Override
		public final boolean isValid() {
			ValidationReport report = validate();
			return report.getErrorNb() == 0;
		}

		@Override
		public final ValidationReport validate() {
			ValidationReport returned = new ValidationReport(this);
			validate(returned);
			return returned;
		}

		@Override
		public void validate(ValidationReport report) {
			applyValidation(report);
			if (getEmbeddedObjects() != null) {
				for (FIBModelObject o : getEmbeddedObjects()) {
					o.validate(report);
				}
			}
		}

		protected void applyValidation(ValidationReport report) {
			performValidation(FIBModelObjectShouldHaveAUniqueName.class, report);
		}

		private static final Hashtable<Class<?>, ValidationRule<?, ?>> rules = new Hashtable<Class<?>, ValidationRule<?, ?>>();

		private static <R extends ValidationRule<R, C>, C extends FIBModelObject> R getRule(Class<R> validationRuleClass) {
			R returned = (R) rules.get(validationRuleClass);
			if (returned == null) {
				Constructor<R> c = (Constructor<R>) validationRuleClass.getConstructors()[0];
				try {
					returned = c.newInstance(null);
					rules.put(validationRuleClass, returned);
				} catch (IllegalArgumentException e) {
					e.printStackTrace();
				} catch (InstantiationException e) {
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				} catch (InvocationTargetException e) {
					e.printStackTrace();
				}
			}
			return returned;
		}

		/*protected final <R extends ValidationRule<R, C>, C extends FIBModelObject> void performValidation(Class<R> validationRuleClass,
				ValidationReport report) {
			R rule = getRule(validationRuleClass);
			ValidationIssue<R, C> issue = rule.applyValidation((C) this);
			if (issue != null) {
				report.addToValidationIssues(issue);
			}

		}*/

		protected final <R extends ValidationRule> void performValidation(Class<R> validationRuleClass, ValidationReport report) {
			R rule = (R) getRule(validationRuleClass);
			ValidationIssue issue = rule.applyValidation(this);
			if (issue != null) {
				report.addToValidationIssues(issue);
			}

		}

		@Override
		public String generateUniqueName(String baseName) {
			String currentName = baseName;
			int i = 2;
			while (isNameUsedInHierarchy(currentName)) {
				currentName = baseName + i;
				i++;
			}
			return currentName;
		}

		@Override
		public String getBaseName() {
			return getFactory().getModelEntityForInstance(this).getImplementedInterface().getSimpleName();
		}

		public boolean isNameUsedInHierarchy(String aName) {
			return isNameUsedInHierarchy(aName, getComponent().getRootComponent());
		}

		private static boolean isNameUsedInHierarchy(String aName, FIBModelObject object) {
			if (object.getName() != null && object.getName().equals(aName)) {
				return true;
			}
			if (object.getEmbeddedObjects() != null) {
				for (FIBModelObject o : object.getEmbeddedObjects()) {
					if (isNameUsedInHierarchy(aName, o)) {
						return true;
					}
				}
			}
			return false;
		}

		@Override
		public List<FIBModelObject> getObjectsWithName(String aName) {
			return retrieveObjectsWithName(aName, getComponent().getRootComponent(), new ArrayList<FIBModelObject>());
		}

		private static List<FIBModelObject> retrieveObjectsWithName(String aName, FIBModelObject object, List<FIBModelObject> list) {
			if (object.getName() != null && object.getName().equals(aName)) {
				list.add(object);
			}
			if (object.getEmbeddedObjects() != null) {
				for (FIBModelObject o : object.getEmbeddedObjects()) {
					retrieveObjectsWithName(aName, o, list);
				}
			}
			return list;
		}

		@Override
		public final Collection<? extends FIBModelObject> getEmbeddedObjects() {
			return (Collection<? extends FIBModelObject>) getFactory().getEmbeddedObjects(this, EmbeddingType.CLOSURE);
		}

	}

	public static class FIBModelObjectShouldHaveAUniqueName extends ValidationRule<FIBModelObjectShouldHaveAUniqueName, FIBModelObject> {

		public FIBModelObjectShouldHaveAUniqueName() {
			super(FIBModelObject.class, "object_should_not_have_duplicated_name");
		}

		@Override
		public ValidationIssue<FIBModelObjectShouldHaveAUniqueName, FIBModelObject> applyValidation(FIBModelObject object) {
			if (StringUtils.isNotEmpty(object.getName())) {
				List<FIBModelObject> allObjectsWithThatName = object.getObjectsWithName(object.getName());
				if (allObjectsWithThatName.size() > 1) {
					allObjectsWithThatName.remove(object);
					GenerateUniqueName fixProposal = new GenerateUniqueName();
					ProblemIssue<FIBModelObjectShouldHaveAUniqueName, FIBModelObject> returned;
					if (object instanceof FIBWidget && ((FIBWidget) object).getManageDynamicModel()) {
						returned = new ValidationError<FIBModelObjectShouldHaveAUniqueName, FIBModelObject>(this, object,
								"object_($object.toString)_has_duplicated_name", fixProposal);
					} else {
						returned = new ValidationWarning<FIBModelObjectShouldHaveAUniqueName, FIBModelObject>(this, object,
								"object_($object.toString)_has_duplicated_name", fixProposal);
					}
					returned.addToRelatedValidableObjects(allObjectsWithThatName);
					return returned;
				}
			}
			return null;
		}

		protected static class GenerateUniqueName extends FixProposal<FIBModelObjectShouldHaveAUniqueName, FIBModelObject> {

			public GenerateUniqueName() {
				super("generate_unique_name_:_($uniqueName)");
			}

			@Override
			protected void fixAction() {
				getObject().setName(getUniqueName());
			}

			public String getUniqueName() {
				return getObject().generateUniqueName(getObject().getBaseName());
			}

		}
	}

	public static abstract class BindingMustBeValid<C extends FIBModelObject> extends ValidationRule<BindingMustBeValid<C>, C> {
		public BindingMustBeValid(String ruleName, Class<C> clazz) {
			super(clazz, ruleName);
		}

		public abstract DataBinding<?> getBinding(C object);

		// public abstract BindingDefinition getBindingDefinition(C object);

		@Override
		public ValidationIssue<BindingMustBeValid<C>, C> applyValidation(C object) {
			if (getBinding(object) != null && getBinding(object).isSet()) {
				if (!getBinding(object).isValid()) {
					DeleteBinding<C> deleteBinding = new DeleteBinding<C>(this);
					return new ValidationError<BindingMustBeValid<C>, C>(this, object, BindingMustBeValid.this.getNameKey() + " '"
							+ getBinding(object) + "'", deleteBinding);
				}
			}
			return null;
		}

		protected static class DeleteBinding<C extends FIBModelObject> extends FixProposal<BindingMustBeValid<C>, C> {

			private final BindingMustBeValid rule;

			public DeleteBinding(BindingMustBeValid rule) {
				super("delete_this_binding");
				this.rule = rule;
			}

			@Override
			protected void fixAction() {
				rule.getBinding(getObject()).setExpression(null);
			}

		}
	}

}
