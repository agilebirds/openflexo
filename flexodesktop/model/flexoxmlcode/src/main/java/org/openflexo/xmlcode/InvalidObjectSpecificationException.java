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
 * Exception thrown when trying to manipulate an object (coding, decoding) invalid regarding an XML model.
 * </p>
 * This exception is generally thrown when a field declared in model is missing, or when a class doesn't exists. The 'message' (see
 * {@link #getMessage()}) contains the error description.
 * 
 * @author <a href="mailto:Sylvain.Guerin@enst-bretagne.fr">Sylvain Guerin</a>
 * @see XMLCoder
 * @see XMLDecoder
 */
public class InvalidObjectSpecificationException extends RuntimeException {

	/**
	 * Creates a new <code>InvalidObjectSpecificationException</code> instance.
	 * 
	 */
	public InvalidObjectSpecificationException() {
		super();
	}

	/**
	 * Creates a new <code>InvalidObjectSpecificationException</code> instance given a message <code>aMessage</code>
	 * 
	 * @param aMessage
	 *            a <code>String</code> value
	 */
	public InvalidObjectSpecificationException(String aMessage) {
		super(aMessage);
	}
}
