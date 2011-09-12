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


/**
 * <p>
 * A Constant KeyValue property represents an access to a constant of given type
 * </p>
 * 
 * @author <a href="mailto:Sylvain.Guerin@enst-bretagne.fr">Sylvain Guerin</a>
 * @see KeyValueCoder
 * @see KeyValueDecoder
 * 
 */
public class ConstantKeyValueProperty<E extends Object> extends KeyValueProperty
{

	private E constant;
	
    /**
     * Creates a new <code>ConstantKeyValueProperty</code> instance
     */
    public ConstantKeyValueProperty(Class anObjectClass, E value) throws InvalidKeyValuePropertyException
    {

        super(anObjectClass, "CONSTANT");
        constant = value;
    }
    
	public E getConstant() 
	{
		return constant;
	}
    
    @Override
    public synchronized E getObjectValue(Object object)
    {
    	return getConstant();
    }

    @Override
    public synchronized void setObjectValue(Object aValue, Object object)
    {
    	// Not applicable
    }
    
     /**
     * Return a string representation of this object (debug purposes)
     */
    @Override
	public String toString()
    {
        return getClass().getSimpleName() + ":" + constant;

    }

    public static class StringConstantKeyValueProperty extends ConstantKeyValueProperty<String>
    {
		public StringConstantKeyValueProperty(Class anObjectClass, String value) throws InvalidKeyValuePropertyException {
			super(anObjectClass, value);
		}
    }

    public static class BooleanConstantKeyValueProperty extends ConstantKeyValueProperty<Boolean>
    {
		public BooleanConstantKeyValueProperty(Class anObjectClass, Boolean value) throws InvalidKeyValuePropertyException {
			super(anObjectClass, value);
		}
    }

    public static class LongConstantKeyValueProperty extends ConstantKeyValueProperty<Long>
    {
		public LongConstantKeyValueProperty(Class anObjectClass, Long value) throws InvalidKeyValuePropertyException {
			super(anObjectClass, value);
		}
    }

    public static class DoubleConstantKeyValueProperty extends ConstantKeyValueProperty<Double>
    {
		public DoubleConstantKeyValueProperty(Class anObjectClass, Double value) throws InvalidKeyValuePropertyException {
			super(anObjectClass, value);
		}
    }

}
