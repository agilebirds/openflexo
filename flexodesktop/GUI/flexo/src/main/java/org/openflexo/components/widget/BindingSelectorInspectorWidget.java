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
package org.openflexo.components.widget;

import java.awt.Color;
import java.awt.event.FocusEvent;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JComponent;

import org.openflexo.components.widget.binding.BindingSelector;
import org.openflexo.foundation.bindings.AbstractBinding;
import org.openflexo.foundation.bindings.Bindable;
import org.openflexo.foundation.bindings.BindingDefinition;
import org.openflexo.foundation.bindings.BindingValue;
import org.openflexo.foundation.bindings.BindingVariable;
import org.openflexo.foundation.bindings.WidgetBindingDefinition;
import org.openflexo.foundation.dm.ComponentDMEntity;
import org.openflexo.foundation.dm.DMEntity;
import org.openflexo.foundation.dm.DMProperty;
import org.openflexo.foundation.dm.DMPropertyImplementationType;
import org.openflexo.foundation.dm.DMType;
import org.openflexo.inspector.AbstractController;
import org.openflexo.inspector.InspectableModification;
import org.openflexo.inspector.InspectableObject;
import org.openflexo.inspector.model.PropertyModel;
import org.openflexo.inspector.widget.WidgetFocusListener;

/**
 * Please comment this class
 * 
 * @author sguerin
 * 
 */
public class BindingSelectorInspectorWidget extends CustomInspectorWidget<AbstractBinding> {

	static final Logger logger = Logger.getLogger(BindingSelectorInspectorWidget.class.getPackage().getName());

	protected BindingSelector _selector;

	boolean isUpdatingEditedBinding = false;

	public BindingSelectorInspectorWidget(final PropertyModel model, AbstractController controller) {
		super(model, controller);
		_selector = new BindingSelector(null) {

			@Override
			public String toString() {
				return "BindingSelector[" + model.name + "]";
			}

			@Override
			public void apply() {
				try {
					BindingSelectorInspectorWidget.this.isUpdatingEditedBinding = true;
					super.apply();
					updateModelFromWidget();
				} finally {
					BindingSelectorInspectorWidget.this.isUpdatingEditedBinding = false;
				}
			}

			@Override
			public void cancel() {
				super.cancel();
				updateModelFromWidget();
			}

			@Override
			public DMProperty createsNewEntry(String newPropertyName, DMType newPropertyType,
					DMPropertyImplementationType implementationType, DMEntity parentEntity) {

				return parentEntity.createDMProperty(newPropertyName, newPropertyType, implementationType);

				/*if(parentEntity==null)parentEntity = getComponentEntity();
				
				if(getEditedObject()==null)setEditedObject(makeBindingValue());
				getEditedObject().setBindingVariable(bv);
				DMProperty newProperty = parentEntity.createsBindingVariable(newPropertyName, newPropertyType, implementationType);
				if(getBindingDefinition() instanceof WidgetBindingDefinition && newProperty.getEntity() instanceof ComponentDMEntity){
					((ComponentDMEntity)newProperty.getEntity()).setBindable(newProperty, false);
				}
				if(newProperty!=null) getEditedObject().addBindingPathElement(newProperty);
				apply();
				updateWidgetFromModel();
				return newProperty;
				*/
			}

		};
		getDynamicComponent().addFocusListener(new WidgetFocusListener(this) {
			@Override
			public void focusGained(FocusEvent arg0) {
				if (logger.isLoggable(Level.FINE))
					logger.fine("Focus gained in " + getClass().getName());
				super.focusGained(arg0);
				_selector.getTextField().requestFocus();
				_selector.getTextField().selectAll();
			}

			@Override
			public void focusLost(FocusEvent arg0) {
				if (logger.isLoggable(Level.FINE))
					logger.fine("Focus lost in " + getClass().getName());
				super.focusLost(arg0);
			}
		});
	}

	@Override
	public Class getDefaultType() {
		return BindingValue.class;
	}

	@Override
	public synchronized void updateWidgetFromModel() {
		if (logger.isLoggable(Level.FINE))
			logger.fine("BindingSelectorInspectorWidget, updateWidgetFromModel() for " + getObjectValue() + " isUpdatingModel="
					+ isUpdatingEditedBinding + " _selector.getIsUpdatingModel()=" + _selector.getIsUpdatingModel());

		if (isUpdatingEditedBinding)
			return;
		if (!_selector.getIsUpdatingModel()) {
			_selector.setEditedObjectAndUpdateBDAndOwner(getObjectValue());
			_selector.setRevertValue(getObjectValue());
		}
		/*BindingValue currentValue = (BindingValue)getObjectValue();
		if (currentValue != null) {
		    currentValue.setBindingDefinition(_selector.getBindingDefinition());
		 }*/

	}

	/**
	 * Update the model given the actual state of the widget
	 */
	@Override
	public synchronized void updateModelFromWidget() {
		isUpdatingEditedBinding = true;
		try {
			setObjectValue(_selector.getEditedObject());
		} finally {
			isUpdatingEditedBinding = false;
		}
		super.updateModelFromWidget();
	}

	@Override
	public JComponent getDynamicComponent() {
		return _selector;
	}

	public static final String ALLOWS_COMPOUND_BINDINGS = "allows_compound_bindings";
	public static final String ACTIVATE_COMPOUND_BINDINGS = "activate_compound_bindings";
	public static final String ALLOWS_STATIC_VALUES = "allows_static_values";
	public static final String ALLOWS_EXPRESSIONS = "allows_expressions";
	public static final String ALLOWS_TRANSTYPERS = "allows_transtypers";

	@Override
	protected void performModelUpdating(InspectableObject value) {
		super.performModelUpdating(value);

		_selector.setEditedObject(getObjectValue());
		_selector.setRevertValue(getObjectValue());

		if (logger.isLoggable(Level.FINE))
			logger.fine("BindingSelectorInspectorWidget, performModelUpdating for " + getObjectValue());

		if (hasValueForParameter(ACTIVATE_COMPOUND_BINDINGS)) {
			String boolValue = getValueForParameter(ACTIVATE_COMPOUND_BINDINGS);
			if (boolValue.equalsIgnoreCase("true") || boolValue.equalsIgnoreCase("yes"))
				_selector.activateCompoundBindingMode();
			else
				_selector.activateNormalBindingMode();
		}
		/*else {
		 	if (getObjectValue() == null 
		 			|| !(getObjectValue() instanceof BindingValue && ((BindingValue)getObjectValue()).isCompoundBinding()))
				_selector.activateNormalBindingMode();
		}*/

		if (value instanceof Bindable) {
			_selector.setBindable((Bindable) value);
		} else if (value != null) {
			if (logger.isLoggable(Level.WARNING))
				logger.warning("BindingSelectorInspectorWidget declared for a non-bindable inspectable object");
		}

		if (hasValueForParameter("binding_definition")) {
			BindingDefinition newBindingDefinition = (BindingDefinition) getDynamicValueForParameter("binding_definition", value);
			_selector.setBindingDefinition(newBindingDefinition);
		}

		if (hasValueForParameter("creates_entry")) {
			_selector.setAllowsEntryCreation(true);
		} else {
			_selector.setAllowsEntryCreation(false);
		}

		if (hasValueForParameter(ALLOWS_COMPOUND_BINDINGS)) {
			String boolValue = getValueForParameter(ALLOWS_COMPOUND_BINDINGS);
			if (boolValue.equalsIgnoreCase("true") || boolValue.equalsIgnoreCase("yes")) {
				_selector.setAllowsCompoundBindings(true);
			} else {
				_selector.setAllowsCompoundBindings(false);
			}
		}

		if (hasValueForParameter(ALLOWS_STATIC_VALUES)) {
			String boolValue = getValueForParameter(ALLOWS_STATIC_VALUES);
			if (boolValue.equalsIgnoreCase("true") || boolValue.equalsIgnoreCase("yes"))
				_selector.setAllowsStaticValues(true);
			else
				_selector.setAllowsStaticValues(false);
		}

		if (hasValueForParameter(ALLOWS_EXPRESSIONS)) {
			String boolValue = getValueForParameter(ALLOWS_EXPRESSIONS);
			if (boolValue.equalsIgnoreCase("true") || boolValue.equalsIgnoreCase("yes"))
				_selector.setAllowsBindingExpressions(true);
			else
				_selector.setAllowsBindingExpressions(false);
		}

		if (hasValueForParameter(ALLOWS_TRANSTYPERS)) {
			String boolValue = getValueForParameter(ALLOWS_TRANSTYPERS);
			if (boolValue.equalsIgnoreCase("true") || boolValue.equalsIgnoreCase("yes"))
				_selector.setAllowsTranstypers(true);
			else
				_selector.setAllowsTranstypers(false);
		}

	}

	private Method _createsEntryMethod;

	private Method getCreatesEntryMethod() {
		if (_createsEntryMethod == null) {
			String methodName = PropertyModel.getLastAccessor(getValueForParameter("creates_entry"));
			if (getModel() != null) {
				Class targetClass = PropertyModel.getTargetObject(getModel(), getValueForParameter("creates_entry")).getClass();
				Class[] methodClassParams = new Class[3];
				methodClassParams[0] = String.class;
				methodClassParams[1] = DMType.class;
				methodClassParams[2] = DMPropertyImplementationType.class;
				try {
					_createsEntryMethod = targetClass.getMethod(methodName, methodClassParams);
				} catch (SecurityException e) {
					// Warns about the exception
					if (logger.isLoggable(Level.WARNING))
						logger.warning("SecurityException raised: " + e.getClass().getName() + ". See console for details.");
					e.printStackTrace();
				} catch (NoSuchMethodException e) {
					// Warns about the exception
					if (logger.isLoggable(Level.WARNING))
						logger.warning("NoSuchMethodException raised: unable to find method " + methodName + " for class " + targetClass);
					e.printStackTrace();
				}
			}
		}
		return _createsEntryMethod;
	}

	BindingVariable performCreatesEntry(String name, DMType type, DMPropertyImplementationType implementationType, DMEntity newEntryEntity,
			BindingValue editedObject) {
		if (getCreatesEntryMethod() != null) {
			Object[] params = new Object[3];
			params[0] = name;
			params[1] = type;
			params[2] = implementationType;
			// DMEntity whishedEntity = getEditedValue().getAccessedType();
			// System.err.println("whishedEntity:"+whishedEntity.getName());
			try {
				if (newEntryEntity == null) {
					Object targetObject = PropertyModel.getTargetObject(getModel(), getValueForParameter("creates_entry"));
					if (logger.isLoggable(Level.INFO))
						logger.info("invoking " + getCreatesEntryMethod() + " on object" + targetObject);
					return (BindingVariable) getCreatesEntryMethod().invoke(targetObject, params);
				} else {
					DMProperty newProperty = newEntryEntity.createDMProperty(name, type, implementationType);
					if (editedObject.getBindingDefinition() instanceof WidgetBindingDefinition
							&& newProperty.getEntity() instanceof ComponentDMEntity) {
						((ComponentDMEntity) newProperty.getEntity()).setBindable(newProperty, false);
					}
					if (newProperty != null)
						editedObject.addBindingPathElement(newProperty);
					return editedObject.getBindingVariable();
				}
			} catch (IllegalArgumentException e) {
				// Warns about the exception
				if (logger.isLoggable(Level.WARNING))
					logger.warning("Exception raised: " + e.getClass().getName() + ". See console for details.");
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				// Warns about the exception
				if (logger.isLoggable(Level.WARNING))
					logger.warning("Exception raised: " + e.getClass().getName() + ". See console for details.");
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				// Warns about the exception
				if (logger.isLoggable(Level.WARNING))
					logger.warning("Exception raised: " + e.getClass().getName() + ". See console for details.");
				e.printStackTrace();
			}
		}
		return null;
	}

	/*private String getLastAccessor(String listAccessor)
	{
	    int lastDotPosition = listAccessor.lastIndexOf(".");
	    if (lastDotPosition < 0)
	        return listAccessor;
	    return listAccessor.substring(lastDotPosition + 1, listAccessor.length());
	}

	private KeyValueCoding getTargetObject(KeyValueCoding sourceObject, String listAccessor)
	{
	    StringTokenizer strTok = new StringTokenizer(listAccessor, ".");
	    String accessor;
	    Object currentObject = sourceObject;
	    while (strTok.hasMoreTokens() && (currentObject != null) && (currentObject instanceof KeyValueCoding)) {
	        accessor = strTok.nextToken();
	        if (strTok.hasMoreTokens()) {
	            if (currentObject != null) {
	                currentObject = ((KeyValueCoding) currentObject).objectForKey(accessor);
	            }
	        }
	    }
	    if (currentObject instanceof KeyValueCoding) {
	        return (KeyValueCoding) currentObject;
	    } else {
	        if (logger.isLoggable(Level.WARNING))
	            logger.warning("Could not find target object : must be a non null KeyValueCoding object !");
	        return null;
	    }
	}*/

	public Color getColorForObject(BindingValue value) {
		return (value.isBindingValid() ? Color.BLACK : Color.RED);
	}

	@Override
	public void update(InspectableObject inspectable, InspectableModification modification) {
		if ("wOComponent".equals(modification.propertyName()))
			_selector.refreshBindingModel();
		super.update(inspectable, modification);
	}

	@Override
	public void fireEditingCanceled() {
		if (_selector != null)
			_selector.closePopup();
	}

	@Override
	public void fireEditingStopped() {
		if (_selector != null)
			_selector.closePopup();
	}

	@Override
	public boolean disableTerminateEditOnFocusLost() {
		return true;
	}
}
