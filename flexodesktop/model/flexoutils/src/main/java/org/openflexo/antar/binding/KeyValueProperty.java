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

package org.openflexo.antar.binding;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.TreeSet;
import java.util.logging.Logger;

import org.openflexo.antar.binding.AbstractBinding.BindingEvaluationContext;
import org.openflexo.toolbox.ToolBox;
import org.openflexo.xmlcode.KeyValueDecoder;

public class KeyValueProperty extends KeyValueBindingImpl implements SimplePathElement 
{

	static final Logger logger = Logger.getLogger(BindingValue.class.getPackage().getName());

    /** Stores property's name */
    protected String name;

    /** Stores related object'class */
    protected Class declaringClass;

    /** Stores related object'type */
    protected Type declaringType;

   /**
     * Stores related field (if this one is public) or null if field is
     * protected or non-existant
     */
    protected Field field;

    /** Stores related type (the class of related property) */
    protected Type type;

    /**
     * Stores related "get" method (if this one is public) or null if method is
     * protected or non-existant
     */
    protected Method getMethod;

    /**
     * Stores related "set" method (if this one is public) or null if method is
     * protected or non-existant
     */
    protected Method setMethod;
    
    private boolean settable = false;

    KeyValueProperty(Type aDeclaringType, String propertyName, boolean setMethodIsMandatory) throws InvalidKeyValuePropertyException
    {

    	super();
    	declaringClass = TypeUtils.getBaseClass(aDeclaringType);
    	declaringType = aDeclaringType;
    	init(propertyName,setMethodIsMandatory);
    }

    @Override
    public boolean equals(Object obj)
    {
    	if (obj instanceof KeyValueProperty) {
    		KeyValueProperty kvp = (KeyValueProperty)obj;
    		return ((declaringClass.equals(kvp.declaringClass))
    				|| (TypeUtils.isClassAncestorOf(declaringClass, kvp.declaringClass))
     				|| (TypeUtils.isClassAncestorOf(kvp.declaringClass, declaringClass)))
    				&& name.equals(kvp.name);
    	}
    	return super.equals(obj);
    }
    
    /**
     * Initialize this property, given a propertyName.<br>
     * This method is called during constructor invokation. NB: to be valid, a
     * property should be identified by at least the field or the get/set
     * methods pair. If the field is accessible, and only the get or the set
     * method is accessible, a warning will be thrown.
     */
    protected void init(String propertyName, boolean setMethodIsMandatory) throws InvalidKeyValuePropertyException
    {
         name = propertyName;

         //System.out.println("Declaring type = "+declaringType+" search property "+propertyName);
         
         String propertyNameWithFirstCharToUpperCase = name.substring(0, 1).toUpperCase() + name.substring(1, name.length());

        field = null;

        try {
            field = declaringClass.getField(name);
        } catch (NoSuchFieldException e) {
            // Debugging.debug ("NoSuchFieldException, trying to find get/set
            // methods pair");
        } catch (SecurityException e) {
            // Debugging.debug ("SecurityException, trying to find get/set
            // methods pair");
        }

        getMethod = searchMatchingGetMethod(declaringClass, name);

        if (field == null) {
            if (getMethod == null) {
                throw new InvalidKeyValuePropertyException("No public field " + name + " found, nor method matching " + name + "() nor " + "_"
                        + name + "() nor " + "get" + propertyNameWithFirstCharToUpperCase + "() nor " + "_get" + propertyNameWithFirstCharToUpperCase
                        + "() found in class:" + declaringClass.getName());
            } else {
                type = getMethod.getGenericReturnType();
            }
        } else { // field != null
            type = field.getGenericType();
            if (getMethod != null) {
                if (getMethod.getGenericReturnType() != type) {
                    logger.warning("Public field " + name + " found, with type " + type + " found " + " and method " + getMethod.getName()
                            + " found " + " declaring return type " + getMethod.getReturnType() + " Ignoring method...");
                    getMethod = null;
                }
            }
        }

        setMethod = searchMatchingSetMethod(declaringClass, name, type);

          if (setMethodIsMandatory) {
            if (setMethod == null) {
                if (field == null) {
                    throw new InvalidKeyValuePropertyException("No public field " + name + " found, nor method matching " + "set"
                            + propertyNameWithFirstCharToUpperCase + "(" + type + ") or " + "_set" + propertyNameWithFirstCharToUpperCase + "("
                            + type + ") found " + "in class " + declaringClass);
                } else {
                    if (getMethod != null) {
                        // Debugging.debug ("Public field "+propertyName+"
                        // found, with type "
                        // + type.getName()+ " found "
                        // + " and method "+getMethod.getName()+" found "
                        // + " but no method matching "
                        // +"set"+propertyNameWithFirstCharToUpperCase+"("+type.getName()+")
                        // or "
                        // +"_set"+propertyNameWithFirstCharToUpperCase+"("+type.getName()+")
                        // found."
                        // +" Will use directly the field to set values.");
                    }
                }
            }
        }
        
        settable = field != null || setMethod != null;

        if ((getMethod != null) && (setMethod != null)) {
            // If related field exist (is public) and accessors methods exists
            // also,
            // the operations are processed using accessors (field won't be used
            // directly,
            // and should be set to null).
            field = null;
        }
        
       // System.out.println("Made KeyValueProperty "+name+" for class "+declaringClass.getSimpleName()+" type="+type);

        if (TypeUtils.isGeneric(type)) {
        	type = TypeUtils.makeInstantiatedType(type, declaringType);
        }

        
        /*if (type instanceof TypeVariable) {
        	TypeVariable<GenericDeclaration> tv = (TypeVariable<GenericDeclaration>)type;
        	//System.out.println("Found type variable "+tv+" name="+tv.getName()+" GD="+tv.getGenericDeclaration());
        	if (declaringType instanceof ParameterizedType) {
            	GenericDeclaration gd = tv.getGenericDeclaration();
        		for (int i=0; i<gd.getTypeParameters().length; i++) {
        			if (gd.getTypeParameters()[i] == tv) {
        				type = ((ParameterizedType)declaringType).getActualTypeArguments()[i];
        				// Found matching parameterized type
        			}
        		}
        	}
        }*/
        
    }

    /**
     * Try to find a matching "get" method, such as (in order):
     * <ul>
     * <li>propertyName()</li>
     * <li>_propertyName()</li>
     * <li>getPropertyName()</li>
     * <li>_getPropertyName()</li>
     * </ul>
     * Returns corresponding method, null if no such method exist
     */
    protected Method searchMatchingGetMethod(Class lastClass, String propertyName)
    {

        Method returnedMethod;
        String propertyNameWithFirstCharToUpperCase = propertyName.substring(0, 1).toUpperCase() + propertyName.substring(1, propertyName.length());

        String[] tries = new String[4];

        tries[0]="get" + propertyNameWithFirstCharToUpperCase;
        tries[1]=propertyName;
        tries[2]="_" + propertyName;
        tries[3]="_get" + propertyNameWithFirstCharToUpperCase;

        for (int i=0;i<4;i++) {
            try {
                return lastClass.getMethod(tries[i], null);
            } catch (SecurityException err) {
                // we continue
            } catch (NoSuchMethodException err) {
                // we continue
            } catch (NoClassDefFoundError err) {
                // we continue
            }
        }

        // Debugging.debug ("No method matching "
        // +propertyName+"() or "
        // +"_"+propertyName+"() or "
        // +"get"+propertyNameWithFirstCharToUpperCase+"() or "
        // +"_get"+propertyNameWithFirstCharToUpperCase+"() found.");

        return null;

    }

    /**
     * Try to find a matching "set" method, such as (in order):
     * <ul>
     * <li>setPropertyName(Type)</li>
     * <li>_setPropertyName(Type)</li>
     * </ul>
     * Returns corresponding method, null if no such method exist
     */
    protected Method searchMatchingSetMethod(Class declaringClass, String propertyName, Type aType)
    {  
    	String propertyNameWithFirstCharToUpperCase = propertyName.substring(0, 1).toUpperCase() + propertyName.substring(1, propertyName.length());
    	String[] tries = new String[2];
    	tries[0]="set" + propertyNameWithFirstCharToUpperCase;
    	tries[1]="_set" + propertyNameWithFirstCharToUpperCase;

    	for (Method m : declaringClass.getMethods()) {
    		for (int i=0;i<2;i++) {
    			if (m.getName().equals(tries[i]) 
    					&& m.getGenericParameterTypes().length == 1
       					&& m.getGenericParameterTypes()[0].equals(aType)) {
    				return m;
    			}
    		}
    	}
    	
    	Type superType = TypeUtils.getSuperType(aType);
        if (superType != null) {
        	// Try with a super class
        	return searchMatchingSetMethod(declaringClass, propertyName, superType);
        }
     	
    	/*
    	Class typeClass = TypeUtils.getBaseClass(aType);
    	
         if (typeClass != null && typeClass.getSuperclass() != null) {
        	// Try with a super class
        	return searchMatchingSetMethod(declaringClass, propertyName, typeClass.getSuperclass());
        }*/
        
        return null;

    }

    /**
     * Stores related "get" method (if this one is public) or null if method is
     * protected/private or non-existant
     */
    public Method getGetMethod()
    {

        return getMethod;
    }

    /**
     * Stores related "set" method (if this one is public) or null if method is
     * protected/private or non-existant
     */
    public Method getSetMethod()
    {

        return setMethod;
    }

    /**
     * Returns name of this property
     */
    public String getName()
    {

        return name;
    }


   /**
     * Returns related object class (never null)
     */
    @Override
	public Class getDeclaringClass()
    {

        return declaringClass;
    }

    /**
     * Returns related field (if this one is public) or null if field is
     * protected or non-existant
     */
    public Field getField()
    {

        return field;
    }

    /**
     * Returns related type
     */
    @Override
	public Type getType()
    {
        return type;
    }

    /**
     * Search and returns all methods (as {@link AccessorMethod} objects) of
     * related class whose names is in the specified string list, with exactly
     * the specified number of parameters, ascendant ordered regarding
     * parameters specialization.
     * 
     * @see AccessorMethod
     */
    protected TreeSet searchMethodsWithNameAndParamsNumber(String[] searchedNames, int paramNumber)
    {

        TreeSet returnedTreeSet = new TreeSet();
        Method[] allMethods = declaringClass.getMethods();

        for (int i = 0; i < allMethods.length; i++) {
            Method tempMethod = allMethods[i];
            for (int j = 0; j < searchedNames.length; j++) {
                if ((tempMethod.getName().equalsIgnoreCase(searchedNames[j])) && (tempMethod.getParameterTypes().length == paramNumber)) {
                    // This is a good candidate
                    returnedTreeSet.add(new AccessorMethod(this, tempMethod));
                }
            }
        }
        // Debugging.debug ("Class "+objectClass.getName()+": found "
        // +returnedTreeSet.size()+" accessors:");
        // for (Iterator i = returnedTreeSet.iterator(); i.hasNext();) {
        // Debugging.debug ("> "+((AccessorMethod)i.next()).getMethod());
        // }
        return returnedTreeSet;
    }

	@Override
	public String getSerializationRepresentation()
	{
		return name;
	}

	@Override
	public boolean isBindingValid() 
	{
		return true;
	}

    public boolean isSettable() 
    {
		return settable;
	}

    @Override
    public String toString()
    {
    	return "KeyValueProperty: "+(declaringClass != null ? declaringClass.getSimpleName() : declaringType)+"."+name;
    }

    public String getLabel()
    {
    	return getName();
    }
    
	public String getTooltipText(Type resultingType)
	{
		String returned = "<html>";
		String resultingTypeAsString;
		if (resultingType!=null) {
			resultingTypeAsString = TypeUtils.simpleRepresentation(resultingType);
			resultingTypeAsString = ToolBox.replaceStringByStringInString("<", "&LT;", resultingTypeAsString);
			resultingTypeAsString = ToolBox.replaceStringByStringInString(">", "&GT;", resultingTypeAsString);
		}
		else {
			resultingTypeAsString = "???";
		}
		returned += "<p><b>"+resultingTypeAsString+" "+getName()+"</b></p>";
		//returned += "<p><i>"+(property.getDescription()!=null?property.getDescription():FlexoLocalization.localizedForKey("no_description"))+"</i></p>";
		returned += "</html>";
		return returned;
	}

	@Override
    public Object evaluateBinding(Object target, BindingEvaluationContext context)
	{
		return 	KeyValueDecoder.objectForKey( target,getName());
	}
}
