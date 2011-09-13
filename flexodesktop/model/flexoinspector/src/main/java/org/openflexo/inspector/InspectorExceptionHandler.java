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
package org.openflexo.inspector;

/**
 * Implemented by all classes able to handle exception raised by the inspector
 * in the context of an exception raised during object inspection (due to the
 * fact that dynamic invokation is performed)
 * 
 * @author sguerin
 * 
 */
public interface InspectorExceptionHandler
{

    /**
     * Tries to handle an exception raised during object inspection
     * 
     * @param inspectable
     *            the object on which exception was raised
     * @param propertyName
     *            the concerned property name
     * @param value
     *            the value that raised an exception
     * @param exception
     *            the exception that was raised
     * @return a boolean indicating if this handler has handled this exception,
     *         or not
     */
    public boolean handleException(InspectableObject inspectable, String propertyName, Object value, Throwable exception);
}
