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

/**
 * <p>
 * Exception thrown when invoking a class accessor during coding or decoding
 * process.
 * </p>
 * The <code>message</code> (see {@link #getMessage()}) contains the error
 * description, and <code>targetException</code> (see
 * {@link #getTargetException()})
 * 
 * @author <a href="mailto:Sylvain.Guerin@enst-bretagne.fr">Sylvain Guerin</a>
 * @see XMLCoder
 * @see XMLDecoder
 */
public class AccessorInvocationException extends RuntimeException
{

    protected Throwable targetException;

    /**
     * Creates a new <code>AccessorInvocationException</code> instance.
     * 
     * @param exception
     * 
     */
    public AccessorInvocationException(InvocationTargetException exception)
    {

        super("Exception raised during accessors execution: " + exception.getTargetException().getMessage());
        targetException = exception.getTargetException();
    }

    /**
     * Creates a new <code>AccessorInvocationException</code> instance given a
     * message <code>aMessage</code>
     * 
     * @param aMessage
     *            a <code>String</code> value
     * @param exception
     */
    public AccessorInvocationException(String aMessage, InvocationTargetException exception)
    {

        super(aMessage + " : " + exception.getTargetException().getClass().getName() + "[message=" + exception.getTargetException().getMessage() + "]");
        targetException = exception.getTargetException();
    }

    /**
     * Return the exception thrown during accessor invocation
     * 
     * @return
     */
    public Throwable getTargetException()
    {
        return targetException;
    }

	@Override
	public Throwable getCause()
	{
		return getTargetException();
	}
	

}
