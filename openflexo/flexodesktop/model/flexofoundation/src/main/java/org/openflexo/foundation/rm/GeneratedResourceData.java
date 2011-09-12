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

import java.io.File;

import org.openflexo.foundation.FlexoException;



/**
 * This interface is implemented by all classes which represents generated data related to
 * a given FlexoImportedResource
 * 
 * @author sguerin
 * 
 */
public interface GeneratedResourceData extends FlexoResourceData
{
	/**
	 * Return the resource related to this object
	 * 
	 * @return a FlexoResource instance
	 */
	@Override
	public FlexoGeneratedResource getFlexoResource();

	public void writeToFile(File aFile) throws FlexoException;
    
    public void generate() throws FlexoException;
    
    public void regenerate() throws FlexoException;
}
