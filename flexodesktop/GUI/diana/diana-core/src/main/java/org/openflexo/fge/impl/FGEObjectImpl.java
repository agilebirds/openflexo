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
package org.openflexo.fge.impl;

import java.beans.PropertyChangeSupport;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openflexo.fge.FGEModelFactory;
import org.openflexo.fge.FGEObject;
import org.openflexo.fge.GRParameter;
import org.openflexo.fge.notifications.FGEAttributeNotification;
import org.openflexo.fge.notifications.FGENotification;
import org.openflexo.kvc.KVCObject;
import org.openflexo.model.ModelEntity;
import org.openflexo.model.exceptions.ModelDefinitionException;

public abstract class FGEObjectImpl extends KVCObject implements FGEObject {
	private static final Logger logger = Logger.getLogger(FGEObjectImpl.class.getPackage().getName());

	private FGEModelFactory factory;

	private PropertyChangeSupport pcSupport;
	private boolean isDeleted = false;

	private static int INDEX = 0;
	private int index = 0;

	public FGEObjectImpl() {
		index = INDEX++;
		pcSupport = new PropertyChangeSupport(this);
	}

	@Override
	public FGEModelFactory getFactory() {
		return factory;
	}

	@Override
	public void setFactory(FGEModelFactory factory) {
		this.factory = factory;
	}

	@Override
	public boolean delete() {
		if (!isDeleted) {
			isDeleted = true;
			performSuperDelete();
			// TODO: remove all listeners of PropertyChangedSupport
			// deleteObservers();
			if (getPropertyChangeSupport() != null) {
				// Property change support can be null if noone is listening. I noone is listening,
				// it is not needed to fire a property change.
				getPropertyChangeSupport().firePropertyChange(getDeletedProperty(), false, true);
				// Fixed huge bug with graphical representation (which are in the model) deleted when the diagram view was closed
				// TODO: Now we can really set the pcSupport to null here
				// Until now, it still create big issues
				// pcSupport = null;
				pcSupport = null;
			}
			return true;
		}
		return false;
	}

	@Override
	public boolean isDeleted() {
		return isDeleted;
	}

	@Override
	public final PropertyChangeSupport getPropertyChangeSupport() {
		if (pcSupport == null && !isDeleted) {
			pcSupport = new PropertyChangeSupport(this);
		}
		return pcSupport;
	}

	@Override
	public String getDeletedProperty() {
		return "delete";
	}

	// *******************************************************************************
	// * Utils *
	// *******************************************************************************

	@Override
	public <T> void notifyChange(GRParameter<T> parameter, T oldValue, T newValue) {
		// Never notify unchanged values
		if (oldValue != null && newValue != null && oldValue.equals(newValue)) {
			return;
		}
		hasChanged(new FGEAttributeNotification(parameter, oldValue, newValue));
		/*propagateConstraintsAfterModification(parameter);
		setChanged();
		notifyObservers(new FGENotification(parameter, oldValue, newValue));*/
	}

	@Override
	public <T> void notifyChange(GRParameter<T> parameter) {
		notifyChange(parameter, null, valueForParameter(parameter));
	}

	@Override
	public <T> void notifyAttributeChange(GRParameter<T> parameter) {
		notifyChange(parameter);
	}

	/**
	 * Build and return a new notification for a potential parameter change, given a new value. Change is required if values are different
	 * considering equals() method
	 * 
	 * @param parameter
	 * @param value
	 * @param useEquals
	 * @return
	 */
	protected <T> FGEAttributeNotification requireChange(GRParameter<T> parameter, T value) {
		return requireChange(parameter, value, true);
	}

	/**
	 * Build and return a new notification for a potential parameter change, given a new value. Change is required if values are different
	 * considering:
	 * <ul>
	 * <li>If useEquals flag set to true, equals() method
	 * <li>
	 * <li>If useEquals flag set to false, same reference for objects, same value for primitives</li>
	 * </ul>
	 * 
	 * @param parameter
	 * @param value
	 * @param useEquals
	 * @return
	 */
	protected <T> FGEAttributeNotification requireChange(GRParameter<T> parameter, T value, boolean useEquals) {
		T oldValue = valueForParameter(parameter);
		if (value == oldValue && value != null && !value.getClass().isEnum()) {
			// logger.warning(parameter.name() + ": require change called for same object: aren't you wrong ???");
		}
		// System.out.println("param: "+parameterKey.name()+" value="+oldValue+" value="+value);
		if (oldValue == null) {
			if (value == null) {
				return null; // No change
			} else {
				return new FGEAttributeNotification(parameter, oldValue, value);
			}
		} else {
			if (useEquals) {
				if (oldValue.equals(value)) {
					return null; // No change
				} else {
					return new FGEAttributeNotification(parameter, oldValue, value);
				}
			} else {
				if (oldValue == value) {
					return null; // No change
				} else {
					return new FGEAttributeNotification(parameter, oldValue, value);
				}
			}
		}
	}

	/**
	 * Computes value for supplied parameter
	 * 
	 * @param parameter
	 * @return
	 */
	@SuppressWarnings("unchecked")
	protected <T> T valueForParameter(GRParameter<T> parameter) {
		if (parameter == null) {
			return null;
		}
		Class<?> type = getTypeForKey(parameter.getName());
		T returned = null;
		if (type.isPrimitive()) {
			if (type == Boolean.TYPE) {
				returned = (T) (Boolean) booleanValueForKey(parameter.getName());
			}
			if (type == Integer.TYPE) {
				returned = (T) (Integer) integerValueForKey(parameter.getName());
			}
			if (type == Short.TYPE) {
				returned = (T) (Short) shortValueForKey(parameter.getName());
			}
			if (type == Long.TYPE) {
				returned = (T) (Long) longValueForKey(parameter.getName());
			}
			if (type == Float.TYPE) {
				returned = (T) (Float) floatValueForKey(parameter.getName());
			}
			if (type == Double.TYPE) {
				returned = (T) (Double) doubleValueForKey(parameter.getName());
			}
			if (type == Byte.TYPE) {
				returned = (T) (Byte) byteValueForKey(parameter.getName());
			}
			if (type == Character.TYPE) {
				returned = (T) (Character) characterForKey(parameter.getName());
			}
		} else {
			returned = (T) objectForKey(parameter.getName());
		}
		return returned;
	}

	@Override
	public void notify(FGEAttributeNotification notification) {
		hasChanged(notification);
	}

	/**
	 * This method is called whenever a notification is triggered from GR model
	 * 
	 * @param notification
	 */
	protected void hasChanged(FGEAttributeNotification notification) {
		if (logger.isLoggable(Level.FINE)) {
			logger.fine("Change attribute " + notification.parameter + " for object " + this + " was: " + notification.oldValue
					+ " is now: " + notification.newValue);
		}
		// propagateConstraintsAfterModification(notification.parameter);
		setChanged();
		notifyObservers(notification);
		// getPropertyChangeSupport().firePropertyChange(notification.propertyName(), notification.oldValue, notification.newValue);
	}

	@Override
	public final boolean equals(Object object) {
		if (object instanceof FGEObject) {
			return equalsObject(object);
		}
		return super.equals(object);
	}

	@Override
	public final Object clone() {
		FGEObject returned = (FGEObject) cloneObject();
		returned.setFactory(getFactory());
		return returned;
	}

	@Override
	public final String toString() {
		if (getFactory() != null) {
			ModelEntity<?> entity = getFactory().getModelEntityForInstance(this);
			if (entity != null) {
				try {
					String returned = entity.getImplementedInterface().getSimpleName() + index
							+ (entity.getImplementingClass() != null ? "[" + entity.getImplementingClass().getSimpleName() + "]" : "");
					return entity.getImplementedInterface().getSimpleName() + index
							+ (entity.getImplementingClass() != null ? "[" + entity.getImplementingClass().getSimpleName() + "]" : "");
				} catch (ModelDefinitionException e) {
				}
			}
		}
		return super.toString();
	}

	public void notifyObservers(FGENotification notification) {
		if (!isDeleted) {
			pcSupport.firePropertyChange(notification.propertyName(), notification.oldValue, notification.newValue);
		}
	}

	@Deprecated
	public void setChanged() {
	}
}
