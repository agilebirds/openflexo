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
package org.openflexo.foundation.dm;

import org.openflexo.foundation.FlexoException;
import org.openflexo.localization.FlexoLocalization;

/**
 * Thrown to notify that this method signature is already registered 
 * 
 * @author sguerin
 * 
 */
public class DuplicatePropertyNameException extends FlexoException
{

    private String _duplicatedName;

    public DuplicatePropertyNameException(String duplicatedName)
    {
        super();
        _duplicatedName = duplicatedName;
    }

    @Override
	public String getMessage()
    {
        return "Duplicate propertyName name: " + _duplicatedName;
    }

    @Override
	public String getLocalizedMessage()
    {
        return FlexoLocalization.localizedForKey("duplicate_property_name") + " : " + _duplicatedName;
    }
}
