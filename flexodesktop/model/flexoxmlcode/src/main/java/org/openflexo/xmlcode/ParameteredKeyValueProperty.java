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
import java.util.Enumeration;
import java.util.StringTokenizer;
import java.util.Vector;

/**
 * <p>
 * A KeyValue property represents a property accessible using parameters
 * </p>
 * 
 * @author <a href="mailto:Sylvain.Guerin@enst-bretagne.fr">Sylvain Guerin</a>
 * @see KeyValueCoder
 * @see KeyValueDecoder
 * 
 */
public class ParameteredKeyValueProperty extends SingleKeyValueProperty
{

	private Vector<KeyValueProperty> arguments;
	
	/**
     * Stores related "get" methods (if there is more that one method matching signature)
     */
    protected Vector<Method> getMethods;

    /**
     * Stores related "set" method (if there is more that one method matching signature)
     */
    protected Vector<Method> setMethods;

	public static boolean isParameteredKeyValuePropertyPattern(String propertyName)
	{
		return isParameteredPathElement(parsePath(propertyName).lastElement());
	}
	
    /**
     * Creates a new <code>KeyValueProperty</code> instance, given an object
     * class.<br>
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
    public ParameteredKeyValueProperty(Class anObjectClass, String propertyName, boolean setMethodIsMandatory) throws InvalidKeyValuePropertyException
    {

        super(anObjectClass, propertyName,setMethodIsMandatory);
    }

    /**
     * Initialize this property, given a propertyName.<br>
     * This method is called during constructor invokation.
     */
    @Override
	protected void init(String propertyName, boolean setMethodIsMandatory) throws InvalidKeyValuePropertyException
    {

    	arguments = parsePathElementArguments(parsePath(propertyName).lastElement());
        super.init(propertyName, setMethodIsMandatory);
   }
    
    /**
     * Returns Object value, asserting that this property represents an Object
     * property (if not, throw an InvalidKeyValuePropertyException exception)
     * 
     * @return an <code>Object</code> value
     * @exception InvalidKeyValuePropertyException
     *                if an error occurs
     */
    @Override
	protected synchronized Object getObjectValue(Object object, Object initialObject)
    {

    	if (object == null) {
            throw new InvalidKeyValuePropertyException("No object is specified");
        } else {
            Object currentObject = object;
            if (isCompound) {
            	for (KeyValueProperty p : compoundKeyValueProperties) {
            		if (currentObject != null) {
            			currentObject = p.getObjectValue(currentObject,initialObject);
            		}
                }
                if (currentObject == null) {
                    return null;
                }
            }

            if (getMethods.size() == 0) {
                throw new InvalidKeyValuePropertyException("InvalidKeyValuePropertyException: no get method found !!!");
            }        	
            
            for (Method m : getMethods) {
            	try {
                    Object[] parameters = new Object[arguments.size()];
                    //String debug = "";
                    for (int i=0; i<arguments.size(); i++) {
                    	parameters[i] = _translateValueForAssignability(m.getParameterTypes()[i],arguments.get(i).getObjectValue(initialObject));
                    	//debug = debug + (i>0?",":"") + parameters[i]+"/"+(parameters[i]!=null?parameters[i].getClass().getSimpleName():"null");
                    }
           		//System.out.println("Trying to invoke on object "+currentObject+" method "+m+" with "+debug);
					return m.invoke(currentObject, parameters);
				} catch (IllegalArgumentException e) {
					System.out.println("IllegalArgumentException: "+e);
				} catch (IllegalAccessException e) {
					System.out.println("IllegalAccessException: "+e);
				} catch (InvocationTargetException e) {
	                    e.getTargetException().printStackTrace();
	                    throw new AccessorInvocationException("AccessorInvocationException: class " + getObjectClass().getName() + ": method "
	                            + m + " Exception raised: " + e.getTargetException().toString(), e);
				} catch (NonAssignableParameterException e) {
					// Does NOT seem to be acceptable method
				}
            }
            
            throw new InvalidKeyValuePropertyException("InvalidKeyValuePropertyException: class " + getObjectClass().getName()
            		+ ": could not access ParameteredKeyValueProperty "+name);
        }
    }


    /**
     * Sets Object value, asserting that this property represents an Object
     * property (if not, throw an InvalidKeyValuePropertyException exception)
     * 
     * @param aValue
     *            an <code>Object</code> value
     * @exception InvalidKeyValuePropertyException
     *                if an error occurs
     */

    /**
     * Sets Object value, asserting that this property represents an Object
     * property (if not, throw an InvalidKeyValuePropertyException exception)
     * 
     * @param aValue
     *            an <code>Object</code> value
     * @exception InvalidKeyValuePropertyException
     *                if an error occurs
     */
    @Override
	public synchronized void setObjectValue(Object aValue, Object object, Object initialObject)
    {

    	if (object == null) {
            throw new InvalidKeyValuePropertyException("No object is specified");
        } else {
        	
            Object currentObject = object;
            if (isCompound) {
            	for (KeyValueProperty p : compoundKeyValueProperties) {
            		if (currentObject != null) {
            			currentObject = p.getObjectValue(currentObject,initialObject);
            		}
                }
                if (currentObject == null) {
                    return;
                }
            }

        	
            if (setMethods.size() == 0) {
                throw new InvalidKeyValuePropertyException("InvalidKeyValuePropertyException: no set method found !!!");
            }        	
            
            for (Method m : setMethods) {
            	try {
                    Object[] parameters = new Object[arguments.size()+1];
                    parameters[0] = aValue;
                    //String debug = ""+aValue;
                    for (int i=0; i<arguments.size(); i++) {
                    	parameters[i+1] = _translateValueForAssignability(m.getParameterTypes()[i+1],arguments.get(i).getObjectValue(initialObject));
                    	//debug = debug + "," + parameters[i+1]+"/"+(parameters[i+1]!=null?parameters[i+1].getClass().getSimpleName():"null");
                    }
           		   //System.out.println("Trying to invoke on object "+currentObject+" method "+m+" with "+debug);
                    m.invoke(currentObject, parameters);
                    return;
				} catch (IllegalArgumentException e) {
					System.out.println("IllegalArgumentException: "+e);
				} catch (IllegalAccessException e) {
					System.out.println("IllegalAccessException: "+e);
				} catch (InvocationTargetException e) {
	                    e.getTargetException().printStackTrace();
	                    throw new AccessorInvocationException("AccessorInvocationException: class " + getObjectClass().getName() + ": method "
	                            + m + " Exception raised: " + e.getTargetException().toString(), e);
				} catch (NonAssignableParameterException e) {
					// Does NOT seem to be acceptable method
				}
            }
            
            throw new InvalidKeyValuePropertyException("InvalidKeyValuePropertyException: class " + getObjectClass().getName()
            		+ ": could not access ParameteredKeyValueProperty "+name);
            
        }
    }


    /**
     * Try to find a matching "get" method, such as (in order):
     * <ul>
     * <li>propertyName(...parameters...)</li>
     * <li>_propertyName(...parameters...)</li>
     * <li>getPropertyName(...parameters...)</li>
     * <li>_getPropertyName(...parameters...)</li>
     * </ul>
     * Returns corresponding method, null if no such method exist
     */
    @Override
	protected Method searchMatchingGetMethod(Class lastClass, String parameteredPropertyName)
    {
    	String propertyName = parameteredPathElementName(parameteredPropertyName);
    	
    	//System.out.println("searchMatchingGetMethod in "+lastClass+" for "+parameteredPropertyName);
        	
       	Object[] parameters = new Object[arguments.size()];
       	for (int i=0; i<arguments.size(); i++) {
       		if (arguments.get(i) instanceof ConstantKeyValueProperty) {
       			parameters[i] = ((ConstantKeyValueProperty)arguments.get(i)).getConstant();
       		}
       		else {
       			parameters[i] = null;
       		}
       	}
       	
       	
       	String propertyNameWithFirstCharToUpperCase = propertyName.substring(0, 1).toUpperCase() + propertyName.substring(1, propertyName.length());

        String[] tries = new String[4];

        tries[0]="get" + propertyNameWithFirstCharToUpperCase;
        tries[1]=propertyName;
        tries[2]="_" + propertyName;
        tries[3]="_get" + propertyNameWithFirstCharToUpperCase;

      	getMethods = _lookupMethods(lastClass,tries,parameters);
      	
      	if (getMethods.size() > 1) {
      		Class type = getMethods.firstElement().getReturnType();
      		for (int i=1; i<getMethods.size(); i++) {
      			if (!type.equals(getMethods.get(i).getReturnType())) {
                    throw new InvalidKeyValuePropertyException("InvalidKeyValuePropertyException: Ambigous methods found with different return types for property "+name);
      			}
      		}
      	}
      	
      	//System.out.println("When trying to find getter for "+parameteredPropertyName+" finding "+getMethods.size()+" methods: "+getMethods);
       	
      	if (getMethods.size() > 0) return getMethods.firstElement();
      	
      	else return null;
      	
    }

    /**
     * Try to find a matching "set" method, such as (in order):
     * <ul>
     * <li>setPropertyName(Type,...parameters...)</li>
     * <li>_setPropertyName(Type,...parameters...)</li>
     * </ul>
     * Returns corresponding method, null if no such method exist
     */
    @Override
	protected Method searchMatchingSetMethod(Class lastClass, String parameteredPropertyName, Class aType)
    {
    	String propertyName = parameteredPathElementName(parameteredPropertyName);
    	
    	//System.out.println("searchMatchingSetMethod in "+lastClass+" for "+parameteredPropertyName);
        	
       	Object[] parameters = new Object[arguments.size()+1];
       	parameters[0] = null;
       	for (int i=0; i<arguments.size(); i++) {
       		if (arguments.get(i) instanceof ConstantKeyValueProperty) {
       			parameters[i+1] = ((ConstantKeyValueProperty)arguments.get(i)).getConstant();
       		}
       		else {
       			parameters[i+1] = null;
       		}
       	}
       	
       	
       	String propertyNameWithFirstCharToUpperCase = propertyName.substring(0, 1).toUpperCase() + propertyName.substring(1, propertyName.length());

        String[] tries = new String[2];

        tries[0]="set" + propertyNameWithFirstCharToUpperCase;
        tries[1]="_set" + propertyNameWithFirstCharToUpperCase;

        setMethods = new Vector<Method>();
        Vector<Method> potentialSetMethods = _lookupMethods(lastClass,tries,parameters);
        for (Method m : potentialSetMethods) {
        	// Check that first param is of right type !!!
        	if (m.getParameterTypes()[0].equals(getType())) setMethods.add(m);
        }
       	
      	//System.out.println("When trying to find setter for "+parameteredPropertyName+" finding "+setMethods.size()+" methods: "+setMethods);
       	
      	if (setMethods.size() > 0) return setMethods.firstElement();
      	
      	else return null;
      	
    }


    /**
     * Return a string representation of this object (debug purposes)
     */
    @Override
	public String toString()
    {
        return "Field: " + field + "\nGetMethod: " + getMethod + "\nSetMethod: " + setMethod + "\nType: " + type;

    }

    
 
    /*private synchronized Object getObjectValue(Object inspectable, String aPropertyName, Object[] parameters) 
    throws InvalidKeyValuePropertyException,AccessorInvocationException
    {
        Method m = _lookupGetter(inspectable.getClass(), aPropertyName, parameters);
               
       	if (m == null) {
       		throw new InvalidKeyValuePropertyException("getObjectValue() failed for property " + name
                        + " for object " + inspectable.getClass().getName() 
                        + " : cannot find GETTER method for "
                        + name+" with "+parameters);
        	}
       	else {
       		Object returned;
			try {
				returned = m.invoke(inspectable,parameters);
			} catch (IllegalArgumentException e) {
				throw new InvalidKeyValuePropertyException("getObjectValue() failed for property " + name
	                        + " for object " + inspectable.getClass().getName() + " : exception "
	                        + e.getMessage());
			} catch (IllegalAccessException e) {
				throw new InvalidKeyValuePropertyException("getObjectValue() failed for property " + name
	                        + " for object " + inspectable.getClass().getName() + " : exception "
	                        + e.getMessage());
			} catch (InvocationTargetException e) {
				throw new AccessorInvocationException("getObjectValue() failed for property " + name
                        + " for object " + inspectable.getClass().getName(),e);
			}
       		return returned;
       	}
    }

     private synchronized void setObjectValue(Object inspectable, String aPropertyName, Object[] initialParameters, Object aValue)
     throws InvalidKeyValuePropertyException,AccessorInvocationException
     
     {
    	 Object[] parameters = new Object[initialParameters.length+1];
      	 parameters[0] = aValue;
      	 for (int i=0; i<initialParameters.length; i++) parameters[i+1] = initialParameters[i];
    	 Method m = _lookupSetter(inspectable.getClass(), aPropertyName, parameters, aValue);

    	 if (m == null) {
    		 throw new InvalidKeyValuePropertyException("setObjectValue() failed for property " + name
						 + " for object " + inspectable.getClass().getName()+" and value "+aValue 
						 + " : cannot find SETTER method for "
    					 + name+" with "+parameters);
    	 }
    	 else {
    		 try {
    			 m.invoke(inspectable,parameters);
    		 } catch (IllegalArgumentException e) {
    			 throw new InvalidKeyValuePropertyException("setObjectValue() failed for property " + name
    						 + " for object " + inspectable.getClass().getName()+" and value "+aValue + " : exception "
    						 + e.getMessage());
    		 } catch (IllegalAccessException e) {
    			 throw new InvalidKeyValuePropertyException("setObjectValue() failed for property " + name
    						 + " for object " + inspectable.getClass().getName()+" and value "+aValue + " : exception "
    						 + e.getMessage());
    		 } catch (InvocationTargetException e) {
    			 throw new AccessorInvocationException("setObjectValue() failed for property " + name
    					 + " for object " + inspectable.getClass().getName()+" and value "+aValue,e);
    		 }
    	 }
     }*/

   /* private Method _lookupGetter(Class aClass, String aPropertyName, Object[] parameters)
    {
    	if (true) return aClass.getMethods()[0];
    	
      	Class[] parameterTypes = new Class[parameters.length];
    	for (int i=0; i<parameters.length; i++) {
    		if (parameters[i] != null)
    			parameterTypes[i] = parameters[i].getClass();
    		else parameterTypes[i] = null;
    	}
    	
        String propertyNameWithFirstCharToUpperCase = aPropertyName.substring(0, 1).toUpperCase() + aPropertyName.substring(1, aPropertyName.length());

        String[] tries = new String[4];

        tries[0]="get" + propertyNameWithFirstCharToUpperCase;
        tries[1]=aPropertyName;
        tries[2]="_" + name;
        tries[3]="_get" + propertyNameWithFirstCharToUpperCase;

        Method returned = _lookupMethod(aClass, tries, parameterTypes, parameters);
        
        if (returned != null) {
           	// Now convert parameters
           	for (int i=0; i<returned.getParameterTypes().length; i++) {
           		parameters[i] = _translateValueForAssignability(returned.getParameterTypes()[i], parameters[i]);
           	}
        }
    
        return returned;
    }
    
    private Method _lookupSetter(Class aClass, String aPropertyName, Object[] parameters, Object aValue)
    {
    	if (true) return aClass.getMethods()[0];

    	Class[] parameterTypes = new Class[parameters.length];
       	for (int i=0; i<parameters.length; i++) {
    		if (parameters[i] != null)
    			parameterTypes[i] = parameters[i].getClass();
    		else parameterTypes[i] = null;
    	}
    	
        String propertyNameWithFirstCharToUpperCase = aPropertyName.substring(0, 1).toUpperCase() + aPropertyName.substring(1, aPropertyName.length());

        String[] tries = new String[2];

        tries[0]="set" + propertyNameWithFirstCharToUpperCase;
        tries[1]="_set" + propertyNameWithFirstCharToUpperCase;

        Method returned = _lookupMethod(aClass, tries, parameterTypes, parameters);
        
        if (returned != null) {
           	// Now convert parameters
           	for (int i=0; i<returned.getParameterTypes().length; i++) {
           		parameters[i] = _translateValueForAssignability(returned.getParameterTypes()[i], parameters[i]);
           	}
        }
    
        return returned;
    }*/
    
    private Vector<Method> _lookupMethods(Class aClass, String[] methodNames, Object[] parameters)
    {
    	Vector<Method> returned = new Vector<Method>();
    	
    	for (Method m : aClass.getMethods()) {
    		//System.out.println("Method: "+m+" parameters.length="+parameters.length);
    		for (String methodName : methodNames) {
    			if (m.getName().equals(methodName) && m.getParameterTypes().length == parameters.length) {
    				boolean ok = true;
    				for (int i=0; i<m.getParameterTypes().length; i++) {
     					if (!_checkAssignability(m.getParameterTypes()[i],parameters[i])) {
     						ok = false;
    					}
    				}
    				if (ok) returned.add(m);
    			}
    		}
    	}
    	return returned;
    }

    private boolean _checkAssignability (Class<?> methodParamType, Object myValue)
    {
    	if (methodParamType == null) {
    		System.err.println("Unexpected null type");
    		return false;
    	}
    	
      	if (myValue == null) return true;
        
       	Class<?> myType = myValue.getClass();

    	if (methodParamType.equals(Double.TYPE)) methodParamType = Double.class;
    	if (methodParamType.equals(Float.TYPE)) methodParamType = Float.class;
    	if (methodParamType.equals(Long.TYPE)) methodParamType = Long.class;
    	if (methodParamType.equals(Integer.TYPE)) methodParamType = Integer.class;
    	if (methodParamType.equals(Short.TYPE)) methodParamType = Short.class;
    	if (methodParamType.equals(Byte.TYPE)) methodParamType = Byte.class;
    	if (methodParamType.equals(Character.TYPE)) methodParamType = Character.class;
    	if (methodParamType.equals(Boolean.TYPE)) methodParamType = Boolean.class;

    	if (myType.equals(Double.TYPE)) myType = Double.class;
    	if (myType.equals(Float.TYPE)) myType = Float.class;
    	if (myType.equals(Long.TYPE)) myType = Long.class;
    	if (myType.equals(Integer.TYPE)) myType = Integer.class;
    	if (myType.equals(Short.TYPE)) myType = Short.class;
    	if (myType.equals(Byte.TYPE)) myType = Byte.class;
    	if (myType.equals(Character.TYPE)) myType = Character.class;
    	if (myType.equals(Boolean.TYPE)) myType = Boolean.class;

    	if (methodParamType.isAssignableFrom(myType)) return true;
    	
    	if (Number.class.isAssignableFrom(methodParamType) 
    			&& Number.class.isAssignableFrom(myType)) {
    		// Last chance with using values
    		
    		Number nb = (Number)myValue;
    	   	if (methodParamType.equals(Double.class)) return true;
    	   	if (methodParamType.equals(Float.class)) return true; // Will require truncation
    	   	if (methodParamType.equals(Long.class)) return nb.doubleValue() == nb.longValue();
    	   	if (methodParamType.equals(Integer.class)) return nb.doubleValue() == nb.intValue();
    	   	if (methodParamType.equals(Short.class)) return nb.doubleValue() == nb.shortValue();
    	   	if (methodParamType.equals(Byte.class)) return nb.doubleValue() == nb.byteValue();
    	}
    	
    	return false;
    }
    
    private class NonAssignableParameterException extends Exception { }
    
    private Object _translateValueForAssignability (Class<?> methodParamType, Object myValue) throws NonAssignableParameterException
    {
    	if (methodParamType == null) {
    		System.err.println("Unexpected null type");
    		return false;
    	}
    	
       	if (myValue == null) return null;
        
       	Class<?> myType = myValue.getClass();
    	
    	if (methodParamType.equals(Double.TYPE)) methodParamType = Double.class;
    	if (methodParamType.equals(Float.TYPE)) methodParamType = Float.class;
    	if (methodParamType.equals(Long.TYPE)) methodParamType = Long.class;
    	if (methodParamType.equals(Integer.TYPE)) methodParamType = Integer.class;
    	if (methodParamType.equals(Short.TYPE)) methodParamType = Short.class;
    	if (methodParamType.equals(Byte.TYPE)) methodParamType = Byte.class;
    	if (methodParamType.equals(Character.TYPE)) methodParamType = Character.class;
    	if (methodParamType.equals(Boolean.TYPE)) methodParamType = Boolean.class;

    	if (myType.equals(Double.TYPE)) myType = Double.class;
    	if (myType.equals(Float.TYPE)) myType = Float.class;
    	if (myType.equals(Long.TYPE)) myType = Long.class;
    	if (myType.equals(Integer.TYPE)) myType = Integer.class;
    	if (myType.equals(Short.TYPE)) myType = Short.class;
    	if (myType.equals(Byte.TYPE)) myType = Byte.class;
    	if (myType.equals(Character.TYPE)) myType = Character.class;
    	if (myType.equals(Boolean.TYPE)) myType = Boolean.class;

    	if (methodParamType.isAssignableFrom(myType)) return myValue;
    	
    	if (Number.class.isAssignableFrom(methodParamType) 
    			&& Number.class.isAssignableFrom(myType)) {
     		Number nb = (Number)myValue;
    	   	if (methodParamType.equals(Double.class)) return nb.doubleValue();
    	   	if (methodParamType.equals(Float.class)) return nb.floatValue();
    	   	if (methodParamType.equals(Long.class)) return nb.longValue();
    	   	if (methodParamType.equals(Integer.class)) return nb.intValue();
    	   	if (methodParamType.equals(Short.class)) return nb.shortValue();
    	   	if (methodParamType.equals(Byte.class)) return nb.byteValue();
    	}
    	
    	throw new NonAssignableParameterException();
    }

    private static Vector<String> parsePath (String aPath)
    {
    	Vector<String> returned = new Vector<String>();
    	//System.out.println("BEGIN parse "+aPath);
     	PathTokenizer t = new PathTokenizer(aPath);
    	while (t.hasMoreTokens()) {
    		returned.add(t.nextToken());
    		//System.out.println("token: "+returned.lastElement());
    	}
    	return returned;
    }
    
    private static boolean isParameteredPathElement(String pathElement)
	{
		return pathElement.indexOf("(") > 0;
	}
	
    private static String parameteredPathElementName(String pathElement)
	{
		return pathElement.substring(0,pathElement.indexOf("("));
	}
	
    private Vector<KeyValueProperty> parsePathElementArguments (String pathElement)
    {
    	String argsAsString = pathElement.substring(pathElement.indexOf("(")+1,pathElement.lastIndexOf(")"));
    	Vector<KeyValueProperty> returned = new Vector<KeyValueProperty>();
    	//System.out.println("BEGIN parse args "+argsAsString);
    	ArgsTokenizer t = new ArgsTokenizer(argsAsString);
    	while (t.hasMoreTokens()) {
    		String next = t.nextToken();
    		KeyValueProperty newProperty;
    		boolean isParsableAsALong = false;
    		long parsedLong = 0;
    		try { 
    			parsedLong = Long.parseLong(next);
    			isParsableAsALong = true;
    		}
    		catch (NumberFormatException e) {};
    		boolean isParsableAsADouble = false;
    		double parsedDouble = 0;
    		if (!isParsableAsALong) {
    			try { 
    				parsedDouble = Double.parseDouble(next);
    				isParsableAsADouble = true;
    			}
    			catch (NumberFormatException e) {};
    		}
    		if (isParsableAsADouble) {
    			newProperty = new ConstantKeyValueProperty.DoubleConstantKeyValueProperty(getObjectClass(),parsedDouble);
    		}
    		else if (isParsableAsALong) {
    			newProperty = new ConstantKeyValueProperty.LongConstantKeyValueProperty(getObjectClass(),parsedLong);
    		}
    		else if (next.equalsIgnoreCase("true") || next.equalsIgnoreCase("yes")) {
    			newProperty = new ConstantKeyValueProperty.BooleanConstantKeyValueProperty(getObjectClass(),true);
    		}
    		else if (next.equalsIgnoreCase("false") || next.equalsIgnoreCase("no")) {
    			newProperty = new ConstantKeyValueProperty.BooleanConstantKeyValueProperty(getObjectClass(),false);
    		}
       		else if (next.startsWith("\"") && next.endsWith("\"")) {
    			newProperty = new ConstantKeyValueProperty.StringConstantKeyValueProperty(getObjectClass(),next.substring(1,next.length()-1));
    		}
       		else if (next.startsWith("'") && next.endsWith("'")) {
    			newProperty = new ConstantKeyValueProperty.StringConstantKeyValueProperty(getObjectClass(),next.substring(1,next.length()-1));
    		}
       		else if (isParameteredKeyValuePropertyPattern(next)) {
       			newProperty = new ParameteredKeyValueProperty(getObjectClass(),next,false);
       		}
       		else {
      			newProperty = new SingleKeyValueProperty(getObjectClass(),next,false);
       		}
  		
    		returned.add(newProperty);
    	}
    	return returned;
    }
    
    protected static class ArgsTokenizer
    {
        private Vector<String> _tokens;

        private Enumeration<String> enumeration;

        protected ArgsTokenizer(String value)
        {
            super();
            _tokens = new Vector<String>();
            StringTokenizer st = new StringTokenizer(value, ",()", true);
            String current = "";
            int level = 0;
            while (st.hasMoreElements()) {
                String next = st.nextToken();
                if ((next.equals(",")) && (current.trim().length() > 0) && (level == 0)) {
                    _tokens.add(current);
                    current = "";
                } else if (next.equals("(")) {
                    current += next;
                    level++;
                } else if (next.equals(")")) {
                    current += next;
                    level--;
                } else {
                    current += next;
                }
            }
            if ((current.trim().length() > 0) && (level == 0)) {
                _tokens.add(current);
                current = "";
            }
            enumeration = _tokens.elements();
        }

        public boolean hasMoreTokens()
        {
            return enumeration.hasMoreElements();
        }

        public String nextToken()
        {
            String returned = enumeration.nextElement();
            return returned;
        }
    }



}
