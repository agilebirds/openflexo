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

import java.util.Enumeration;
import java.util.Vector;

/**
 * Thrown when one or more SaveResourceException were raised during resource
 * saving
 * 
 * @author sguerin
 * 
 */
public class SaveResourceExceptionList extends Exception
{

    protected Vector _saveExceptions;

    public SaveResourceExceptionList(SaveResourceException exception)
    {
        super();
        _saveExceptions = new Vector();
        registerNewException(exception);
    }

    public void registerNewException(SaveResourceException exception)
    {
        _saveExceptions.add(exception);
    }

    public Vector getSaveExceptions()
    {
        return _saveExceptions;
    }

    public String errorFilesList()
    {
        String errorFiles = "";
        for (Enumeration en = getSaveExceptions().elements(); en.hasMoreElements();) {
            SaveResourceException excep = (SaveResourceException) en.nextElement();
            errorFiles += errorFiles + excep.getFileResource().getFile().getName() + "\n";
        }
        return errorFiles;
    }
}
