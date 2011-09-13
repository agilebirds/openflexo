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

package org.openflexo.xmlcode;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.TreeSet;

// addTo > setForKey
// removeFrom > removeWithKey

/**
 * <p>
 * A HashtableKeyValueProperty property represents a hashtable-like property,
 * accessible directly by related field or related accessors.
 * </p>
 *
 * @author <a href="mailto:Sylvain.Guerin@enst-bretagne.fr">Sylvain Guerin</a>
 * @see KeyValueCoder
 * @see KeyValueDecoder
 *
 */
public class HashtableKeyValueProperty extends KeyValueProperty
{

    /**
     * Stores all 'setForKey...' methods, in order of the specialization of
     * their arguments, as {@link AccessorMethod} objects. (the first methods
     * are the first we will try to invoke, that's why vector needs to be
     * sorted).<br>
     * NB: 'setForKey...' methods are methods with general form:
     * <code>setXXXForKey(Class anObject, Class aKey)</code> or
     * <code>_setXXXForKey(Class anObject, Class aKey)</code>, where XXX is
     * the property name (try with or without a terminal 's' character), and
     * Class could be anything...
     */
    protected TreeSet setForKeyMethods;

    /**
     * Stores all 'removeWithKey...' methods, in order of the specialization of
     * their arguments, as {@link AccessorMethod} objects. (the first methods
     * are the first we will try to invoke, that's why vector needs to be
     * sorted).<br>
     * NB: 'removeWithKey...' methods are methods with general form:
     * <code>removeXXXWithKey(Class aKey)</code> or
     * <code>_removeXXXWithKey(Class aKey)</code>, where XXX is the property
     * name (try with or without a terminal 's' character), and Class could be
     * anything...
     */
    protected TreeSet removeWithKeyMethods;

    /**
     * Creates a new <code>HashtableKeyValueProperty</code> instance, given an
     * object class.<br>
     * To be usable, this property should be set with a correct object
     * (according to object class)
     *
     * @param anObject
     *            an <code>Object</code> value
     * @param propertyName
     *            a <code>String</code> value
     * @exception InvalidKeyValuePropertyException
     *                if an error occurs
     */
    public HashtableKeyValueProperty(Class anObjectClass, String propertyName, boolean setMethodIsMandatory) throws InvalidKeyValuePropertyException
    {

        super(anObjectClass, propertyName);
        init(propertyName, setMethodIsMandatory);
    }

    /**
     * Returns boolean indicating if related type inherits from Hastable
     */
    public boolean typeInheritsFromHashtable()
    {

        return Hashtable.class.isAssignableFrom(getType());
    }

    /**
     * Initialize this property, given a propertyName.<br>
     * This method is called during constructor invokation. NB: to be valid, a
     * property should be identified by at least the field or the accessors
     * methods. If the field is accessible, and only some of accessors methods
     * are accessible, a warning will be thrown.
     */
    @Override
	protected void init(String propertyName, boolean setMethodIsMandatory) throws InvalidKeyValuePropertyException
    {

        super.init(propertyName, setMethodIsMandatory);

        if (!typeInheritsFromHashtable()) {
            throw new InvalidKeyValuePropertyException("Property " + propertyName + " found, but doesn't seem inherits from java.util.Hashtable");
        }

        // If related type is a sub-class of hashtable, check that there is a
        // a trivial constructor
        if (!getType().equals(Hashtable.class)) {
            try {
                Object testInstanciation = type.newInstance();
            } catch (InstantiationException e) {
                throw new InvalidKeyValuePropertyException("Class " + type.getName()
                        + " cannot be instanciated directly (check that this class has a constructor with no arguments [public " + type.getName()
                        + "()] and that this class is not abstract.");
            } catch (Exception e) {
                throw new InvalidModelException("Unexpected error occurs during model initialization. Please send a bug report.");
            }
        }

        if (field == null) {

            // Debugging.debug ("Trying to find hashtable-specific accessors
            // methods");

            setForKeyMethods = searchMatchingSetForKeyMethods(propertyName);
            if ((setForKeyMethods.size() == 0) && (setMethodIsMandatory)) {
                throw new InvalidKeyValuePropertyException("No public field " + propertyName + " found, and no 'setForKey' methods accessors found");
            }

            removeWithKeyMethods = searchMatchingRemoveWithKeyMethods(propertyName);
            if ((removeWithKeyMethods.size() == 0) && (setMethodIsMandatory)) {
                throw new InvalidKeyValuePropertyException("No public field " + propertyName + " found, and no 'removeWithKey' methods accessors found");
            }

        }

    }

    /**
     * Search and return matching "setForKey" methods<br>
     * NB: 'setForKey...' methods are methods with general form:
     * <code>setXXXForKey(Class anObject, Class aKey)</code> or
     * <code>_setXXXForKey(Class anObject, Class aKey)</code>, where XXX is
     * the property name (try with or without a terminal 's' character), and
     * Class could be anything... Returns an ordered TreeSet of
     * {@link AccessorMethod} objects
     */
    protected TreeSet searchMatchingSetForKeyMethods(String propertyName)
    {

        String singularPropertyName;
        String pluralPropertyName;

        if (propertyName.endsWith("ies")) {
            singularPropertyName = propertyName.substring(0, propertyName.length() - 3) + "y";
            pluralPropertyName = propertyName;
        } else if ((propertyName.endsWith("s")) || (propertyName.endsWith("S"))) {
            singularPropertyName = propertyName.substring(0, propertyName.length() - 1);
            pluralPropertyName = propertyName;
        } else {
            singularPropertyName = propertyName;
            pluralPropertyName = propertyName + "s";
        } // end of else

        String[] methodNameCondidates = new String[4];
        methodNameCondidates[0] = "set" + singularPropertyName + "ForKey";
        methodNameCondidates[1] = "_set" + singularPropertyName + "ForKey";
        methodNameCondidates[2] = "set" + pluralPropertyName + "ForKey";
        methodNameCondidates[3] = "_set" + pluralPropertyName + "ForKey";

        return searchMethodsWithNameAndParamsNumber(methodNameCondidates, 2);
    }

    /**
     * Search and return matching "removeWithKey" methods<br>
     * NB: 'removeWithKey...' methods are methods with general form:
     * <code>removeXXXWithKey(Class aKey)</code> or
     * <code>_removeXXXWithKey(Class aKey)</code>, where XXX is the property
     * name (try with or without a terminal 's' character), and Class could be
     * anything... Returns an ordered TreeSet of {@link AccessorMethod} objects
     */
    protected TreeSet searchMatchingRemoveWithKeyMethods(String propertyName)
    {

        String singularPropertyName;
        String pluralPropertyName;

        if (propertyName.endsWith("ies")) {
            singularPropertyName = propertyName.substring(0, propertyName.length() - 3) + "y";
            pluralPropertyName = propertyName;
        } else if ((propertyName.endsWith("s")) || (propertyName.endsWith("S"))) {
            singularPropertyName = propertyName.substring(0, propertyName.length() - 1);
            pluralPropertyName = propertyName;
        } else {
            singularPropertyName = propertyName;
            pluralPropertyName = propertyName + "s";
        } // end of else

        String[] methodNameCondidates = new String[4];
        methodNameCondidates[0] = "remove" + singularPropertyName + "WithKey";
        methodNameCondidates[1] = "_remove" + singularPropertyName + "WithKey";
        methodNameCondidates[2] = "remove" + pluralPropertyName + "WithKey";
        methodNameCondidates[3] = "_remove" + pluralPropertyName + "WithKey";

        return searchMethodsWithNameAndParamsNumber(methodNameCondidates, 1);
    }

    /**
     * Add Object value for considered object <code>anObject</code>,
     * asserting that this property represents a Hashtable-like property (if
     * not, throw an InvalidKeyValuePropertyException exception)
     *
     * @param aValue
     *            an <code>Object</code> value
     * @exception InvalidKeyValuePropertyException
     *                if an error occurs
     */
    public synchronized void setObjectValueForKey(Object aValue, Object aKey, Object object)
    {
        if (object == null) {
            throw new InvalidKeyValuePropertyException("No object is specified");
        } else {
            if (field != null) {
                try {
                    Hashtable hashtable = (Hashtable) field.get(object);
                    hashtable.put(aKey, aValue);
                } catch (Exception e) {
                    throw new InvalidKeyValuePropertyException("InvalidKeyValuePropertyException: class " + getObjectClass().getName() + ": field "
                            + field.getName() + " Exception raised: " + e.toString());
                }
            } else {
                Object params[] = new Object[2];
                params[0] = aValue;
                params[1] = aKey;
                for (Iterator i = setForKeyMethods.iterator(); i.hasNext();) {
                    Method method = null;
                    try {
                        method = ((AccessorMethod) i.next()).getMethod();
                        method.invoke(object, params);
                        return;
                    } catch (InvocationTargetException e) {
                        throw new AccessorInvocationException("Exception thrown while invoking: " + method, e);
                    } catch (IllegalArgumentException e) {
                        // try next one
                        /*System.out.println("method: "+method);
                        System.out.println("aValue: "+aValue);
                        System.out.println("aKey: "+aKey);*/
                    } catch (Exception e) {
                        // try next one
                        e.printStackTrace();
                    }
                }
                throw new InvalidKeyValuePropertyException("addObjectValue, class " + getObjectClass().getName()
                        + ": could not find a valid 'setForKey' accessor method: object class was " + aValue.getClass().getName());
            }
        }
    }

    /**
     * Remove Object value for considered object <code>anObject</code>,
     * asserting that this property represents a Hashtable-like property (if
     * not, throw an InvalidKeyValuePropertyException exception)
     *
     * @param aValue
     *            an <code>Object</code> value
     * @exception InvalidKeyValuePropertyException
     *                if an error occurs
     */
    public synchronized void removeWithKeyValue(Object aKey, Object object)
    {

    	if (object == null) {
            throw new InvalidKeyValuePropertyException("No object is specified");
        } else {
            if (field != null) {
                try {
                    Hashtable hashtable = (Hashtable) field.get(object);
                    hashtable.remove(aKey);
                } catch (Exception e) {
                    throw new InvalidKeyValuePropertyException("InvalidKeyValuePropertyException: class " + getObjectClass().getName() + ": field "
                            + field.getName() + " Exception raised: " + e.toString());
                }
            } else {
                Object params[] = new Object[1];
                params[0] = aKey;
                for (Iterator i = removeWithKeyMethods.iterator(); i.hasNext();) {
                    Method method = null;
                    try {
                        method = ((AccessorMethod) i.next()).getMethod();
                        method.invoke(object, params);
                        return;
                    } catch (InvocationTargetException e) {
                        throw new AccessorInvocationException("Exception thrown while invoking: " + method, e);
                    } catch (Exception e) {
                        // try next one
                    }
                }
                throw new InvalidKeyValuePropertyException("removeObjectValue, class " + getObjectClass().getName()
                        + ": could not find a valid 'removeWithKey' accessor method: key class was " + aKey.getClass().getName());
            }
        }
    }

    /**
     * Creates a new instance of this represented class, which MUST be a
     * {@link Hashtable} or a subclass of {@link Hashtable}.
     */
    public Hashtable newInstance() throws InvalidObjectSpecificationException
    {

        try {
            return (Hashtable) type.newInstance();
        } catch (Exception e) {
            throw new InvalidObjectSpecificationException("Could not instanciate a new " + type.getName() + ": reason " + e);
        }
    }

}
