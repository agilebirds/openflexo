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
package org.openflexo.dg;

import java.io.File;

import org.openflexo.foundation.rm.FlexoProject;


/**
 * Please comment this class
 * 
 * @author gpolet
 * 
 */
public abstract class GeneratedDocument
{

    protected File _file;

    protected String _stringRepresentation;

    protected File _dmFile;

    protected File _wkfFile;
    
    protected File _ieFile;

    public GeneratedDocument(FlexoProject project, String fileName)
    {
        super();
        File generatedDir = new File(project.getProjectDirectory(), DGCst.DEFAULT_OUTPUT_DIRECTORY);
        if (!generatedDir.exists()) {
            generatedDir.mkdir();
        }
        _file = new File(generatedDir, fileName + ".tex");
        _dmFile = new File(generatedDir, fileName + ".dm.tex");
        _ieFile = new File(generatedDir, fileName + ".ie.tex");
        _wkfFile = new File(generatedDir, fileName + ".wkf.tex");
    }

    public File getFile()
    {
        return _file;
    }

    public void setFile(File file)
    {
        _file = file;
    }

    public abstract FlexoProject getProject();

    public abstract void generate();

    public abstract void save(boolean overWrite);

    public abstract void reset();
    
    public abstract boolean fileExists();

    public  String getStringRepresentation()
    {
        return _stringRepresentation;
    }

    public File getDmFile()
    {
        return _dmFile;
    }

    public void setDmFile(File dmFile)
    {
        _dmFile = dmFile;
    }

    public File getWkfFile()
    {
        return _wkfFile;
    }

    public void setWkfFile(File wkfFile)
    {
        _wkfFile = wkfFile;
    }

    public File getIeFile()
    {
        return _ieFile;
    }

    public void setIeFile(File ieFile)
    {
        _ieFile = ieFile;
    }
}
