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

import java.util.Observable;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openflexo.antar.binding.Bindable;
import org.openflexo.antar.binding.BindingFactory;
import org.openflexo.antar.binding.BindingModel;
import org.openflexo.fib.FIBLibrary;
import org.openflexo.xmlcode.KeyValueDecoder;
import org.openflexo.xmlcode.XMLSerializable;


public abstract class FIBModelObject extends Observable implements Bindable, XMLSerializable {

	private static final Logger logger = Logger.getLogger(FIBModelObject.class.getPackage().getName());

	public static interface FIBModelAttribute
	{
		public String name();
	}

	public static enum Parameters implements FIBModelAttribute
	{
		name,
		description,
		parameters
	}


	private String name;
	private String description;

	private Vector<FIBParameter> parameters = new Vector<FIBParameter>();

	public FIBModelObject()
	{
		super();
	}

	public void delete()
	{
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		FIBAttributeNotification<String> notification = requireChange(Parameters.name,name);
		if (notification != null) {
			this.name = name;
			hasChanged(notification);
		}
	}

	public String getDescription()
	{
		return description;
	}

	public void setDescription(String description)
	{
		FIBAttributeNotification<String> notification = requireChange(Parameters.description,description);
		if (notification != null) {
			this.description = description;
			hasChanged(notification);
		}
	}

	public String getParameter(String parameterName)
	{
		for (FIBParameter p : parameters) {
			if (parameterName.equals(p.name)) {
				return p.value;
			}
		}
		return null;
	}

	public Vector<FIBParameter> getParameters()
	{
		return parameters;
	}

	public void setParameters(Vector<FIBParameter> parameters)
	{
		FIBAttributeNotification<Vector<FIBParameter>> notification = requireChange(Parameters.parameters,parameters);
		if (notification != null) {
			this.parameters = parameters;
			hasChanged(notification);
		}
	}

	public void addToParameters(FIBParameter p)
	{
		parameters.add(p);
		setChanged();
		notifyObservers(new FIBAddingNotification<FIBParameter>(Parameters.parameters, p));
	}

	public void removeFromParameters(FIBParameter p)
	{
		parameters.remove(p);
		setChanged();
		notifyObservers(new FIBRemovingNotification<FIBParameter>(Parameters.parameters, p));
	}

	public FIBParameter createNewParameter()
	{
		FIBParameter returned = new FIBParameter("param","value");
		addToParameters(returned);
		System.out.println("getParameters()="+getParameters());
		return returned;
	}

	public void deleteParameter(FIBParameter p)
	{
		removeFromParameters(p);
	}

	public boolean isParameterAddable()
	{
		return true;
	}

	public boolean isParameterDeletable(FIBParameter p)
	{
		return true;
	}

	public abstract FIBComponent getRootComponent();

	@Override
	public BindingModel getBindingModel()
	{
		if (getRootComponent() != null && getRootComponent() != this) {
			return getRootComponent().getBindingModel();
		}
		return null;
	}

	@Override
	public BindingFactory getBindingFactory()
	{
		return FIBLibrary.instance().getBindingFactory();
	}

	public void finalizeDeserialization()
	{
	}

	// *******************************************************************************
	// *                                   Utils                                     *
	// *******************************************************************************

	protected <T extends Object> void notifyChange(Enum<?> parameterKey, T oldValue, T newValue)
	{
		// Never notify unchanged values
		if (oldValue != null && newValue != null && oldValue.equals(newValue)) {
			return;
		}
		setChanged();
		notifyObservers(new FIBModelNotification<T>(parameterKey.name(),oldValue,newValue));
	}

	protected void notifyChange(Enum<?> parameterKey)
	{
		notifyChange(parameterKey,null,null);
	}

	protected <T extends Object> void notifyChange(String parameterName, T oldValue, T newValue)
	{
		setChanged();
		notifyObservers(new FIBModelNotification<T>(parameterName,oldValue,newValue));
	}

	protected <T extends Object> FIBAttributeNotification<T> requireChange(FIBModelAttribute parameterKey, T value)
	{
		T oldValue = (T)KeyValueDecoder.objectForKey(this,((Enum<?>)parameterKey).name());
		if (oldValue == null) {
			if (value == null) {
				return null; // No change
			} else {
				return new FIBAttributeNotification<T>(parameterKey,oldValue,value);
			}
		}
		else {
			if (oldValue.equals(value)) {
				return null; // No change
			} else {
				return new FIBAttributeNotification<T>(parameterKey,oldValue,value);
			}
		}
	}

	public void notify(FIBModelNotification notification)
	{
		hasChanged(notification);
	}

	protected void hasChanged(FIBModelNotification notification)
	{
		if (logger.isLoggable(Level.FINE)) {
			logger.fine("Change attribute "+notification.getAttributeName()+" for object "+this+" was: "+notification.oldValue()+" is now: "+notification.newValue());
		}
		setChanged();
		notifyObservers(notification);
	}

	public void notifyBindingChanged(DataBinding binding)
	{
	}

	public static boolean equals(Object o1, Object o2)
	{
		if (o1 == o2) {
			return true;
		}
		if (o1 == null) {
			return o2 == null;
		} else {
			return o1.equals(o2);
		}
	}

	public static boolean notEquals(Object o1, Object o2)
	{
		return !equals(o1,o2);
	}

}
