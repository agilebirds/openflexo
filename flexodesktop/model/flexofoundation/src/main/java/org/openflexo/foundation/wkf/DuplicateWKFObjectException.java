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
package org.openflexo.foundation.wkf;

import org.openflexo.foundation.FlexoException;

/**
 * Thrown when trying to rename or create a port with name already existing
 * 
 * @author sguerin
 * 
 */
public class DuplicateWKFObjectException extends FlexoException
{

 

    private WKFObject _object;

    public DuplicateWKFObjectException(WKFObject anObject)
    {
        super();  
        _object = anObject;
    }

    public DuplicateWKFObjectException(WKFObject anObject, String localizationKey)
    {
        super(null, localizationKey);  
        _object = anObject;
    }
    
    @Override
	public String getMessage()
    {
        return "DuplicateWKFObjectException: " + _object.getFullyQualifiedName() + " already registered.";
    }
}
