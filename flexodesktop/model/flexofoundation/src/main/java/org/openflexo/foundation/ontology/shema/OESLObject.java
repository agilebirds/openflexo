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
package org.openflexo.foundation.ontology.shema;

import org.openflexo.foundation.rm.FlexoProject;
import org.openflexo.foundation.rm.XMLStorageResourceData;
import org.openflexo.xmlcode.XMLMapping;


/**
 * Abstract class implemented by all objects involved in Shema Library
 * coding
 *
 * @author sguerin
 *
 */
public abstract class OESLObject extends OEObject
{

    private OEShemaLibrary _shemaLibrary;

    // ==========================================================================
    // ============================= Constructor
    // ================================
    // ==========================================================================

    /**
     * Never use this constructor except for ComponentLibrary
     */
    public OESLObject(FlexoProject project)
    {
        super(project);
    }

    /**
     * Default constructor
     */
    public OESLObject(OEShemaLibrary shemaLibrary)
    {
        super(shemaLibrary.getProject());
        setShemaLibrary(shemaLibrary);
    }

    public OEShemaLibrary getShemaLibrary()
    {
        return _shemaLibrary;
    }

    public void setShemaLibrary(OEShemaLibrary aShemaLibrary)
    {
        _shemaLibrary = aShemaLibrary;
    }

    /**
     * Returns reference to the main object in which this XML-serializable
     * object is contained relating to storing scheme: here it's the component
     * library
     *
     * @return the component library
     */
    @Override
    public XMLStorageResourceData getXMLResourceData()
    {
        return getShemaLibrary();
    }

    /**
     * Overrides getXMLMapping
     * @see org.openflexo.foundation.ie.IEObject#getXMLMapping()
     */
    @Override
    public XMLMapping getXMLMapping()
    {
        return getShemaLibrary().getXMLMapping();
    }
}
