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
package org.openflexo.foundation.rm.cg;

import java.io.IOException;

import org.openflexo.foundation.rm.FlexoGeneratedResource;
import org.openflexo.foundation.rm.LoadResourceException;


/**
 * 
 */
public class LoadGeneratedResourceIOException extends LoadResourceException
{

	private IOException ioException;
	private String _message = null;
	
    /**
     * @param fileResource
     */
    public LoadGeneratedResourceIOException(FlexoGeneratedResource fileResource, IOException exception)
    {
        super(fileResource, null);
		ioException = exception;
   }

    /**
     * @param fileResource
     */
    public LoadGeneratedResourceIOException(FlexoGeneratedResource fileResource, String message)
    {
        super(fileResource, null);
        _message = message;
   }

	public IOException getIOException()
	{
		return ioException;
	}
	
    @Override
	public String getMessage()
    {
    	if (_message != null) {
    		return _message;
    	}
    	else {
    		return super.getMessage();
    	}
    }

    @Override
	public void printStackTrace()
    {
    	super.printStackTrace();
    	if (ioException != null) {
    		System.err.println("Caused by:");
    		ioException.printStackTrace();
    	}
    }
}
