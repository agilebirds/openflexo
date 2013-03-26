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
import java.util.Observable;
import java.util.Vector;
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
import org.openflexo.toolbox.FileResource;
import org.openflexo.toolbox.StringUtils;
import org.openflexo.xmlcode.KeyValueDecoder;
import org.openflexo.xmlcode.XMLSerializable;

public abstract class FIBModelObject extends Observable implements Bindable, XMLSerializable {

	private static final Logger logger = Logger.getLogger(FIBModelObject.class.getPackage().getName());

	// Instanciate a new localizer in directory src/dev/resources/FIBLocalizer
	// Little hack to be removed: linked to parent localizer (which is Openflexo main localizer)
	public static LocalizedDelegateGUIImpl LOCALIZATION = new LocalizedDelegateGUIImpl(new FileResource("FIBLocalized"),
			new LocalizedDelegateGUIImpl(new FileResource("Localized"), null, false), true);

	public static interface FIBModelAttribute {
		public String name();
	}

	public static enum Parameters implements FIBModelAttribute {
		name, description, parameters
	}

	private String name;
	private String description;
	private boolean isDeleted = false;

	private Vector<FIBParameter> parameters = new Vector<FIBParameter>();

	public FIBModelObject() {
		super();
	}

	public void delete() {
		isDeleted = true;
	}

	public boolean isDeleted() {
		return isDeleted;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		FIBAttributeNotification<String> notification = requireChange(Parameters.name, name);
		if (notification != null) {
			this.name = name;
			hasChanged(notification);
		}
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		FIBAttributeNotification<String> notification = requireChange(Parameters.description, description);
		if (notification != null) {
			this.description = description;
			hasChanged(notification);
		}
	}

	public String getParameter(String parameterName) {
		for (FIBParameter p : parameters) {
			if (parameterName.equals(p.getName())) {
				return p.getValue();
			}
		}
		return null;
	}

	public Vector<FIBParameter> getParameters() {
		return parameters;
	}

	public void setParameters(Vector<FIBParameter> parameters) {
		FIBAttributeNotification<Vector<FIBParameter>> notification = requireChange(Parameters.parameters, parameters);
		if (notification != null) {
			this.parameters = parameters;
			hasChanged(notification);
		}
	}

	public void addToParameters(FIBParameter p) {
		parameters.add(p);
		setChanged();
		notifyObservers(new FIBAddingNotification<FIBParameter>(Parameters.parameters, p));
	}

	public void removeFromParameters(FIBParameter p) {
		parameters.remove(p);
		setChanged();
		notifyObservers(new FIBRemovingNotification<FIBParameter>(Parameters.parameters, p));
	}

	public FIBParameter createNewParameter() {
		FIBParameter returned = new FIBParameter("param", "value");
		addToParameters(returned);
		System.out.println("getParameters()=" + getParameters());
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
	 * Return the FIBComponent this component refer to
	 * 
	 * @return
	 */
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

	public void finalizeDeserialization() {
	}

	// *******************************************************************************
	// * Utils *
	// *******************************************************************************

	protected <T extends Object> void notifyChange(FIBModelAttribute parameterKey, T oldValue, T newValue) {
		// Never notify unchanged values
		if (oldValue != null && newValue != null && oldValue.equals(newValue)) {
			return;
		}
		setChanged();
		notifyObservers(new FIBAttributeNotification<T>(parameterKey, oldValue, newValue));
	}

	protected void notifyChange(FIBModelAttribute parameterKey) {
		setChanged();
		notifyObservers(new FIBAttributeNotification(parameterKey, null, null));
	}

	protected <T extends Object> void notifyChange(String parameterName, T oldValue, T newValue) {
		setChanged();
		notifyObservers(new FIBModelNotification<T>(parameterName, oldValue, newValue));
	}

	protected <T extends Object> FIBAttributeNotification<T> requireChange(FIBModelAttribute parameterKey, T value) {
		T oldValue = (T) KeyValueDecoder.objectForKey(this, ((Enum<?>) parameterKey).name());
		if (oldValue == null) {
			if (value == null) {
				return null; // No change
			} else {
				return new FIBAttributeNotification<T>(parameterKey, oldValue, value);
			}
		} else {
			if (oldValue.equals(value)) {
				return null; // No change
			} else {
				return new FIBAttributeNotification<T>(parameterKey, oldValue, value);
			}
		}
	}

	public void notify(FIBModelNotification notification) {
		hasChanged(notification);
	}

	protected void hasChanged(FIBModelNotification notification) {
		if (logger.isLoggable(Level.FINE)) {
			logger.fine("Change attribute " + notification.getAttributeName() + " for object " + this + " was: " + notification.oldValue()
					+ " is now: " + notification.newValue());
		}
		setChanged();
		notifyObservers(notification);
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

	public final boolean isValid() {
		ValidationReport report = validate();
		return report.getErrorNb() == 0;
	}

	public final ValidationReport validate() {
		ValidationReport returned = new ValidationReport(this);
		validate(returned);
		return returned;
	}

	protected final void validate(ValidationReport report) {
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

	protected String generateUniqueName(String baseName) {
		String currentName = baseName;
		int i = 2;
		while (isNameUsedInHierarchy(currentName)) {
			currentName = baseName + i;
			i++;
		}
		return currentName;
	}

	protected String getBaseName() {
		return null;
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

	public abstract Collection<? extends FIBModelObject> getEmbeddedObjects();

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
					return new ValidationError<BindingMustBeValid<C>, C>(this, object, BindingMustBeValid.this.getNameKey(), deleteBinding);
				}
			}
			return null;
		}

		protected static class DeleteBinding<C extends FIBModelObject> extends FixProposal<BindingMustBeValid<C>, C> {

			private BindingMustBeValid rule;

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
