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
 * Exception thrown when trying to decode an object from invalid data regarding
 * a given mapping or model file. The 'message' (see {@link #getMessage()})
 * contains the error description. Those exceptions are raised during String
 * encoding/decoding
 * 
 * @author <a href="mailto:Sylvain.Guerin@enst-bretagne.fr">Sylvain Guerin</a>
 */
public class InvalidDataException extends RuntimeException
{

    /**
     * Creates a new <code>InvalidDataException</code> instance.
     * 
     */
    public InvalidDataException()
    {
        super();
    }

    /**
     * Creates a new <code>InvalidDataException</code> instance, given a
     * message <code>aMessage</code>
     * 
     * @param aMessage
     *            a <code>String</code> value
     */
    public InvalidDataException(String aMessage)
    {
        super(aMessage);
    }
}
