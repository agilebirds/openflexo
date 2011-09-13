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
package org.openflexo.foundation;

import org.openflexo.xmlcode.XMLSerializable;

public interface FlexoImportableObject extends XMLSerializable {

	public boolean isImported();
	
	public boolean isDeletedOnServer();
	
	public void setIsDeletedOnServer(boolean isDeletedOnServer);
	
	public String getName();
	
	public void setName(String name) throws Exception; // the exception is here only for compatibility with the definition of setName() in FlexoProcess and should therefore not be used by other sub-classes!
	
	public String getURI();
	
	public void setURI(String aURI);
	
	public String getVersionURI();
	
	public void setVersionURI(String aURI);
	
	public String getURIFromSourceObject();
	
	public void setURIFromSourceObject(String uri);
}
