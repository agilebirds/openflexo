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
import org.openflexo.fge.GraphicalRepresentation.GRParameter;
import org.openflexo.fge.notifications.FGENotification;
import org.openflexo.inspector.DefaultInspectableObject;

public abstract class FGEObjectImpl extends DefaultInspectableObject implements FGEObject {
	private static final Logger logger = Logger.getLogger(FGEObjectImpl.class.getPackage().getName());

	private FGEModelFactory factory;

	private PropertyChangeSupport pcSupport;
	private boolean isDeleted = false;

	@Override
	public FGEModelFactory getFactory() {
		return factory;
	}

	@Override
	public void setFactory(FGEModelFactory factory) {
		this.factory = factory;
	}

	@Override
	public void delete() {
		if (!isDeleted) {
			isDeleted = true;
			deleteObservers();
			if (getPropertyChangeSupport() != null) {
				// Property change support can be null if noone is listening. I noone is listening,
				// it is not needed to fire a property change.
				getPropertyChangeSupport().firePropertyChange(getDeletedProperty(), false, true);
				// Fixed huge bug with graphical representation (which are in the model) deleted when the diagram view was closed
				// TODO: Now we can really set the pcSupport to null here
				// Until now, it still create big issues
				// pcSupport = null;
			}
		}
	}

	@Override
	public boolean isDeleted() {
		return isDeleted;
	}

	@Override
	@Deprecated
	public String getInspectorName() {
		return null;
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
	public void notifyChange(GRParameter parameter, Object oldValue, Object newValue) {
		// Never notify unchanged values
		if (oldValue != null && newValue != null && oldValue.equals(newValue)) {
			return;
		}
		hasChanged(new FGENotification(parameter, oldValue, newValue));
		/*propagateConstraintsAfterModification(parameter);
		setChanged();
		notifyObservers(new FGENotification(parameter, oldValue, newValue));*/
	}

	@Override
	public void notifyChange(GRParameter parameter) {
		notifyChange(parameter, null, null);
	}

	@Override
	public void notifyAttributeChange(GRParameter parameter) {
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
	protected FGENotification requireChange(GRParameter parameter, Object value) {
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
	protected FGENotification requireChange(GRParameter parameter, Object value, boolean useEquals) {
		Class<?> type = getTypeForKey(parameter.name());
		Object oldValue = null;
		if (type.isPrimitive()) {
			if (type == Boolean.TYPE) {
				oldValue = booleanValueForKey(parameter.name());
			}
			if (type == Integer.TYPE) {
				oldValue = integerValueForKey(parameter.name());
			}
			if (type == Short.TYPE) {
				oldValue = shortValueForKey(parameter.name());
			}
			if (type == Long.TYPE) {
				oldValue = longValueForKey(parameter.name());
			}
			if (type == Float.TYPE) {
				oldValue = floatValueForKey(parameter.name());
			}
			if (type == Double.TYPE) {
				oldValue = doubleValueForKey(parameter.name());
			}
			if (type == Byte.TYPE) {
				oldValue = byteValueForKey(parameter.name());
			}
			if (type == Character.TYPE) {
				oldValue = characterForKey(parameter.name());
			}
		} else {
			oldValue = objectForKey(parameter.name());
			if (value == oldValue && value != null && !value.getClass().isEnum()) {
				// logger.warning(parameter.name() + ": require change called for same object: aren't you wrong ???");
			}
		}
		// System.out.println("param: "+parameterKey.name()+" value="+oldValue+" value="+value);
		if (oldValue == null) {
			if (value == null) {
				return null; // No change
			} else {
				return new FGENotification(parameter, oldValue, value);
			}
		} else {
			if (useEquals) {
				if (oldValue.equals(value)) {
					return null; // No change
				} else {
					return new FGENotification(parameter, oldValue, value);
				}
			} else {
				if (oldValue == value) {
					return null; // No change
				} else {
					return new FGENotification(parameter, oldValue, value);
				}
			}
		}
	}

	@Override
	public void notify(FGENotification notification) {
		hasChanged(notification);
	}

	/**
	 * This method is called whenever a notification is triggered from GR model
	 * 
	 * @param notification
	 */
	protected void hasChanged(FGENotification notification) {
		if (logger.isLoggable(Level.FINE)) {
			logger.fine("Change attribute " + notification.parameter + " for object " + this + " was: " + notification.oldValue
					+ " is now: " + notification.newValue);
		}
		// propagateConstraintsAfterModification(notification.parameter);
		setChanged();
		notifyObservers(notification);
		getPropertyChangeSupport().firePropertyChange(notification.propertyName(), notification.oldValue, notification.newValue);
	}

}
