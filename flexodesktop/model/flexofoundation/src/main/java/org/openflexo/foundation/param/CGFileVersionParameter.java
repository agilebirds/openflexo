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
package org.openflexo.foundation.param;

import org.openflexo.foundation.cg.CGFile;
import org.openflexo.foundation.cg.version.AbstractCGFileVersion;
import org.openflexo.inspector.widget.DenaliWidget;


public class CGFileVersionParameter extends ParameterDefinition<AbstractCGFileVersion> {

    public CGFile file;
	
    public CGFileVersionParameter(String name, String label, CGFile file, AbstractCGFileVersion defaultValue)
    {
        super(name,label,defaultValue);
        this.file = file;
        addParameter("className","org.openflexo.cgmodule.view.CGVersionInspectorWidget");
         addParameter("file", "params."+name+".file");
     }
    
   @Override
public String getWidgetName() 
   {
        return DenaliWidget.CUSTOM;
    }

}
