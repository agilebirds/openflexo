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
package org.openflexo.foundation.dm.eo;

import java.io.FileNotFoundException;

import org.openflexo.foundation.FlexoException;
import org.openflexo.foundation.dm.eo.model.PropertyListDeserializationException;


/**
 * Thrown when exception occurs in EOAccess layer
 * 
 * @author sguerin
 * 
 */
public class EOAccessException extends FlexoException
{

    private Exception _eofException;

    public EOAccessException(IllegalArgumentException e)
    {
        super();
        _eofException = e;
    }

    public EOAccessException(IllegalStateException e)
    {
        super();
        _eofException = e;
    }

    /**
     * @param e
     */
    public EOAccessException(FileNotFoundException e)
    {
        super();
        _eofException = e;
    }

    /**
     * @param e
     */
    public EOAccessException(PropertyListDeserializationException e)
    {
        super();
        _eofException = e;
    }

    @Override
	public String getMessage()
    {
        return _eofException.getMessage();
    }
    
	@Override
	public Throwable getCause()
	{
		return _eofException;
	}
	

}
