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
import java.util.Hashtable;
import java.util.List;
import java.util.Observable;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openflexo.antar.binding.Bindable;
import org.openflexo.antar.binding.BindingFactory;
import org.openflexo.antar.binding.BindingModel;
import org.openflexo.fib.FIBLibrary;
import org.openflexo.fib.model.validation.FixProposal;
import org.openflexo.fib.model.validation.ValidationError;
import org.openflexo.fib.model.validation.ValidationIssue;
import org.openflexo.fib.model.validation.ValidationReport;
import org.openflexo.fib.model.validation.ValidationRule;
import org.openflexo.toolbox.StringUtils;
import org.openflexo.xmlcode.KeyValueDecoder;
import org.openflexo.xmlcode.XMLSerializable;

public abstract class FIBModelObject extends Observable implements Bindable, XMLSerializable {

	private static final Logger logger = Logger.getLogger(FIBModelObject.class.getPackage().getName());

	public static interface FIBModelAttribute {
		public String name();
	}

	public static enum Parameters implements FIBModelAttribute {
		name, description, parameters
	}

	private String name;
	private String description;

	private Vector<FIBParameter> parameters = new Vector<FIBParameter>();

	public FIBModelObject() {
		super();
	}

	public void delete() {
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
			if (parameterName.equals(p.name)) {
				return p.value;
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

	public abstract FIBComponent getRootComponent();

	@Override
	public BindingModel getBindingModel() {
		if (getRootComponent() != null && getRootComponent() != this) {
			return getRootComponent().getBindingModel();
		}
		return null;
	}

	@Override
	public BindingFactory getBindingFactory() {
		return FIBLibrary.instance().getBindingFactory();
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

	public void notifyBindingChanged(DataBinding binding) {
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

	public final ValidationReport validate() {
		ValidationReport returned = new ValidationReport(this);
		validate(returned);
		System.out.println("validation: " + returned.reportAsString());
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
		performValidation(FIBModelObjectShouldHaveAName.class, report);
	}

	private static Hashtable<Class, ValidationRule> rules = new Hashtable<Class, ValidationRule>();

	private static <R extends ValidationRule<R, C>, C extends FIBModelObject> R getRule(Class<R> validationRuleClass) {
		R returned = (R) rules.get(validationRuleClass);
		if (returned == null) {
			Constructor<R> c = (Constructor<R>) validationRuleClass.getConstructors()[0];
			try {
				returned = c.newInstance(null);
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (InstantiationException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			}
			rules.put(validationRuleClass, returned);
		}
		return returned;
	}

	protected final <R extends ValidationRule<R, C>, C extends FIBModelObject> void performValidation(Class<R> validationRuleClass,
			ValidationReport report) {
		R rule = getRule(validationRuleClass);
		ValidationIssue<R, C> issue = rule.applyValidation((C) this);
		if (issue != null) {
			report.addToValidationIssues(issue);
		}

	}

	protected String generateDefaultName() {
		return "prout";
	}

	public abstract List<? extends FIBModelObject> getEmbeddedObjects();

	public static class FIBModelObjectShouldHaveAName extends ValidationRule<FIBModelObjectShouldHaveAName, FIBModelObject> {
		public FIBModelObjectShouldHaveAName() {
			super(FIBModelObject.class, "object_should_have_a_name");
		}

		@Override
		public ValidationIssue<FIBModelObjectShouldHaveAName, FIBModelObject> applyValidation(FIBModelObject object) {
			if (StringUtils.isEmpty(object.getName())) {
				GenerateDefaultName fixProposal = new GenerateDefaultName();
				return new ValidationError<FIBModelObjectShouldHaveAName, FIBModelObject>(this, object,
						"object_($object.toString)_has_no_name", fixProposal);
			}
			return null;
		}

		protected static class GenerateDefaultName extends FixProposal<FIBModelObjectShouldHaveAName, FIBModelObject> {

			public GenerateDefaultName() {
				super("generate_default_name_:_($defaultName)");
			}

			@Override
			protected void fixAction() {
				getObject().setName(getDefaultName());
			}

			public String getDefaultName() {
				return getObject().generateDefaultName();
			}

		}
	}

}
