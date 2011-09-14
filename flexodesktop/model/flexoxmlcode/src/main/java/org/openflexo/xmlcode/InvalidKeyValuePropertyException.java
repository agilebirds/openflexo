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
 * Exception thrown when trying to handle invalid key-value properties. This
 * exception is raised in following cases:
 * <ul>
 * <li>trying to instanciate a property which doesn't exist (no field or no
 * get/set pair accessing methods)</li>
 * <li>trying to get or set a value from/to an invalid type</li>
 * </ul>
 * The 'message' (see {@link #getMessage()}) contains the error description.
 * 
 * @author <a href="mailto:Sylvain.Guerin@enst-bretagne.fr">Sylvain Guerin</a>
 * @see KeyValueProperty
 */
public class InvalidKeyValuePropertyException extends RuntimeException
{

    /**
     * Creates a new <code>InvalidKeyValuePropertyException</code> instance.
     * 
     */
    public InvalidKeyValuePropertyException()
    {
        super();
    }

    /**
     * Creates a new <code>InvalidKeyValuePropertyException</code> instance,
     * given a message <code>aMessage</code>
     * 
     * @param aMessage
     *            a <code>String</code> value
     */
    public InvalidKeyValuePropertyException(String aMessage)
    {
        super(aMessage);
    }
}
