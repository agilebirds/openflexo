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

import org.openflexo.foundation.ws.WSService;
import org.openflexo.inspector.widget.DenaliWidget;


public class WSServiceParameter extends ParameterDefinition<WSService> {

   // private ProcessSelectingConditional _processSelectingConditional;
    
    public WSServiceParameter(String name, String label, WSService defaultValue)
    {
        super(name,label,defaultValue);
        addParameter("className","org.openflexo.components.widget.WSServiceInspectorWidget");
       // _processSelectingConditional = null;
    }
    
   @Override
public String getWidgetName() 
   {
        return DenaliWidget.CUSTOM;
    }
/*
   public boolean isAcceptableProcess(FlexoProcess aProcess) 
   {
       if (_processSelectingConditional != null) return _processSelectingConditional.isSelectable(aProcess);
       return true;
   }

   public void setProcessSelectingConditional(ProcessSelectingConditional processSelectingConditional) 
   {
       _processSelectingConditional = processSelectingConditional;
   }
   
   public abstract static class ProcessSelectingConditional
   {
       public abstract boolean isSelectable(FlexoProcess process);
   }
*/
}
