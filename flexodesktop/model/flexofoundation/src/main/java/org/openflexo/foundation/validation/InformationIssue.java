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
package org.openflexo.foundation.validation;

import java.util.logging.Logger;


/**
 * Represents a validation issue requiring to attention: contains an information
 * message only
 *
 * @author sguerin
 *
 */
public class InformationIssue<R extends ValidationRule<R,V>, V extends Validable> extends ValidationIssue<R,V>
{

    @SuppressWarnings("unused")
	private static final Logger logger = Logger.getLogger(InformationIssue.class.getPackage().getName());

    public InformationIssue(V anObject, String aLocalizedMessage)
    {
        super(anObject, aLocalizedMessage);
    }

    public InformationIssue(V anObject, String aMessage, boolean isLocalized)
    {
        super(anObject, aMessage,isLocalized);
    }


   @Override
public String toString()
   {
	   return "VALIDATION / INFO:    "+getMessage();
   }

}
