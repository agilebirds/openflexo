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
package org.openflexo.inspector.widget;

import java.awt.Color;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Enumeration;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openflexo.inspector.AbstractController;
import org.openflexo.inspector.InspectableObject;
import org.openflexo.inspector.model.PropertyModel;
import org.openflexo.kvc.KeyValueCoding;


/**
 * Defines an abstract custom widget
 * 
 * @author sguerin
 * 
 */
public abstract class CustomWidget<T> extends DenaliWidget<T>
{

    private static final Logger logger = Logger.getLogger(CustomWidget.class.getPackage().getName());

    public CustomWidget(PropertyModel model, AbstractController controller)
    {
        super(model,controller);
        applyCancelListener = new Vector<ApplyCancelListener>();
    }

    @Override
	public void setModel(InspectableObject value)
    {
        super.setModel(value);
        if (logger.isLoggable(Level.FINE))
            logger.fine("setModel in " + this.getClass().getName() + " with " + value);
        performModelUpdating(value);
    }

    public boolean hasValueForParameter(String parameterName)
    {
        return getPropertyModel().hasValueForParameter(parameterName);
    }

    public String getValueForParameter(String parameterName)
    {
        return getPropertyModel().getValueForParameter(parameterName);
    }

    public boolean getBooleanValueForParameter(String parameterName)
    {
        String valueAsString = getPropertyModel().getValueForParameter(parameterName);
        return (valueAsString.equalsIgnoreCase("true") || valueAsString.equalsIgnoreCase("yes"));
    }

    public Object getDynamicValueForParameter(String parameterName, InspectableObject object)
    {
        if (hasValueForParameter(parameterName)) {
            try {
                String listAccessor = getPropertyModel().getValueForParameter(parameterName);
                Object currentObject = PropertyModel.getObjectForMultipleAccessors(object, listAccessor);
                return currentObject;
            } catch (Exception e) {
                if (logger.isLoggable(Level.WARNING))
                    logger.warning("getDynamicValueForParameter() failed for property " + getPropertyModel().name + " for object " + object + " and parameter "
                            + parameterName + ": exception " + e.getMessage());
                e.printStackTrace();
            }
        }
        return null;
    }

    private Method getMethod(String methodPath, Class[] paramClasses)
    {
         KeyValueCoding targetObject = PropertyModel.getTargetObject(getModel(),methodPath);
                String methodName = PropertyModel.getLastAccessor(methodPath);
        if (targetObject == null)
            return null;
        Class targetClass = targetObject.getClass();
        try {
            return targetClass.getMethod(methodName, paramClasses);
        } catch (SecurityException e) {
            // Warns about the exception
            if (logger.isLoggable(Level.WARNING))
                logger.warning("SecurityException raised: " + e.getClass().getName() + ". See console for details.");
            e.printStackTrace();
            return null;
        } catch (NoSuchMethodException e) {
        	// Try to find less specialized methods
        	// The first matching is the good one !
        	for (Method m : targetClass.getMethods()) {
        		if (m.getName().equals(methodName) && m.getParameterTypes().length == paramClasses.length) {
        			boolean lookedUp = true;
        			int paramId = 0;
        			for (Class c : m.getParameterTypes()) {
        				if (!c.isAssignableFrom(paramClasses[paramId])) lookedUp = false;
        				paramId++;
        			}
        			if (lookedUp) return m;
        		}
        	}
        	
        	
            // Warns about the exception
            if (logger.isLoggable(Level.WARNING))
                logger.warning("NoSuchMethodException raised: unable to find method " + methodName + " for class " + targetClass);
            e.printStackTrace();
            return null;
       }
    }

    private Class[] classesArrayFor(Object[] parameters)
    {
        Class[] returned = new Class[parameters.length];
        for (int i=0; i<parameters.length; i++) {
            returned[i] = parameters[i].getClass();
        }
        return returned;
    }
    
    public Object getParameteredValue(String methodPath, Object[] parameters)
    {
        Class[] paramClasses = classesArrayFor(parameters);
        Method method = getMethod(methodPath,paramClasses);
        if (method != null) {
            try {
                KeyValueCoding targetObject = PropertyModel.getTargetObject(getModel(),methodPath);
                if (logger.isLoggable(Level.FINE))
                    logger.fine("invoking " + method + " on object" + targetObject);
                return method.invoke(targetObject, parameters);
            } catch (IllegalArgumentException e) {
                // Warns about the exception
                if (logger.isLoggable(Level.WARNING))
                    logger.warning("Exception raised: " + e.getClass().getName() + ". See console for details.");
                e.printStackTrace();
                return null;
            } catch (IllegalAccessException e) {
                // Warns about the exception
                if (logger.isLoggable(Level.WARNING))
                    logger.warning("Exception raised: " + e.getClass().getName() + ". See console for details.");
                e.printStackTrace();
                return null;
           } catch (InvocationTargetException e) {
                // Warns about the exception
                if (logger.isLoggable(Level.WARNING))
                    logger.warning("Exception raised: " + e.getClass().getName() + ". See console for details.");
                e.printStackTrace();
                return null;
           }
        }
        else {
            logger.warning("Could not find method "+methodPath+" for "+getModel());
            return null;
        }
    }
    
    public Object getParameteredValue(String methodPath, Object parameter)
    {
        Object[] params = { parameter };
        return getParameteredValue(methodPath,params);
    }
    
    public boolean getBooleanParameteredValue(String methodPath, Object parameter)
    {
        Object returned = getParameteredValue(methodPath,parameter);
        if (returned == null) return false;
        if (returned instanceof Boolean) {
            return ((Boolean)returned).booleanValue();
        }
        else {
            logger.warning("Return type mismatch: "+returned.getClass().getName());
            return false;
        }
    }
    
    /**
     * Must be overriden in sub-classes if required
     * 
     * @param value
     */
    protected void performModelUpdating(InspectableObject value)
    {
    }

    public void performModelUpdating()
    {
        performModelUpdating(getModel());
    }

    public T getEditedValue()
    {
        return getObjectValue();
    }

    public Color getColorForObject(T value) 
    {
        return Color.BLACK;
    }
    
    public static interface ApplyCancelListener {
        public void fireApplyPerformed();
        public void fireCancelPerformed();
    }
    
    private Vector<ApplyCancelListener> applyCancelListener;
    
	public void addApplyCancelListener(ApplyCancelListener l) 
	{
		applyCancelListener.add(l);
	}

	public void removeApplyCancelListener(ApplyCancelListener l)
	{
		applyCancelListener.remove(l);
	}

	public void notifyApply()
	{
		if (logger.isLoggable(Level.FINE))
            logger.fine("notifyApply()");
		Enumeration<ApplyCancelListener> en = applyCancelListener.elements();
		while (en.hasMoreElements()) {
			ApplyCancelListener l = en.nextElement();
			l.fireApplyPerformed();
		}
	}

	public void notifyCancel()
	{
		if (logger.isLoggable(Level.FINE))
            logger.fine("notifyCancel()");
		Enumeration<ApplyCancelListener> en = applyCancelListener.elements();
		while (en.hasMoreElements()) {
			ApplyCancelListener l = en.nextElement();
			l.fireCancelPerformed();
		}
	}

	public boolean disableTerminateEditOnFocusLost() {
		return false;
	}
	
	public abstract void fireEditingCanceled() ;
	public abstract void fireEditingStopped() ;

}
