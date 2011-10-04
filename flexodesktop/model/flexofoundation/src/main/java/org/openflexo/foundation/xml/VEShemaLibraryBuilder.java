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
package org.openflexo.foundation.xml;

import java.util.logging.Logger;

import org.openflexo.foundation.rm.FlexoOEShemaLibraryResource;
import org.openflexo.foundation.view.ViewLibrary;
import org.openflexo.foundation.viewpoint.ViewPointLibrary;


/**
 * Used only during XML serialization to build shema library
 * 
 * @author bmangez
 */
public class VEShemaLibraryBuilder extends FlexoBuilder<FlexoOEShemaLibraryResource>
{
    @SuppressWarnings("unused")
	private static final Logger logger = Logger.getLogger(VEShemaLibraryBuilder.class.getPackage().getName());

    public ViewLibrary shemaLibrary;
    public ViewPointLibrary viewPointLibrary;

    public VEShemaLibraryBuilder(FlexoOEShemaLibraryResource library, ViewPointLibrary viewPointLibrary)
    {
        super(library);
        this.viewPointLibrary = viewPointLibrary;
    }

}
