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
package org.openflexo.foundation.view;

import org.openflexo.foundation.rm.FlexoProject;
import org.openflexo.foundation.rm.XMLStorageResourceData;
import org.openflexo.xmlcode.XMLMapping;

/**
 * Abstract class implemented by all objects involved in Shema Library coding
 * 
 * @author sguerin
 * 
 */
public abstract class ViewLibraryObject extends AbstractViewObject {

	private ViewLibrary _shemaLibrary;

	// ==========================================================================
	// ============================= Constructor
	// ================================
	// ==========================================================================

	/**
	 * Never use this constructor except for ComponentLibrary
	 */
	public ViewLibraryObject(FlexoProject project) {
		super(project);
	}

	/**
	 * Default constructor
	 */
	public ViewLibraryObject(ViewLibrary shemaLibrary) {
		super(shemaLibrary.getProject());
		setShemaLibrary(shemaLibrary);
	}

	public ViewLibrary getViewLibrary() {
		return getShemaLibrary();
	}

	@Deprecated
	public ViewLibrary getShemaLibrary() {
		return _shemaLibrary;
	}

	public void setShemaLibrary(ViewLibrary aShemaLibrary) {
		_shemaLibrary = aShemaLibrary;
	}

	/**
	 * Returns reference to the main object in which this XML-serializable object is contained relating to storing scheme: here it's the
	 * component library
	 * 
	 * @return the component library
	 */
	@Override
	public XMLStorageResourceData getXMLResourceData() {
		return getShemaLibrary();
	}

	/**
	 * Overrides getXMLMapping
	 * 
	 * @see org.openflexo.foundation.ie.IEObject#getXMLMapping()
	 */
	@Override
	public XMLMapping getXMLMapping() {
		return getShemaLibrary().getXMLMapping();
	}
}
