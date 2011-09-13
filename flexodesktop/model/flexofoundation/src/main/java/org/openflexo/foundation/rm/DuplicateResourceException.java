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
package org.openflexo.foundation.rm;

import org.openflexo.foundation.FlexoException;

/**
 * Thown when trying to register a resource with the same identifier as an other
 * resource already registered
 * 
 * @author sguerin
 * 
 */
public class DuplicateResourceException extends FlexoException
{
    private String resourceIdentifier;

    public DuplicateResourceException(FlexoResource aResource)
    {
        this(aResource.getResourceIdentifier());
    }

    public DuplicateResourceException(String aResourceIdentifier)
    {
        super();
        resourceIdentifier = aResourceIdentifier;
    }
    public DuplicateResourceException(String aResourceIdentifier, String localizationKey)
    {
        super(null, localizationKey);
        resourceIdentifier = aResourceIdentifier;
    }

    @Override
	public String getMessage()
    {
        return "DuplicateResourceException: resource " + resourceIdentifier + " already registered.";
    }
}
