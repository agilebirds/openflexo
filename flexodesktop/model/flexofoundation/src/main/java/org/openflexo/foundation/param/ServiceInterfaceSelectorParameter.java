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

import org.openflexo.foundation.wkf.FlexoProcess;
import org.openflexo.foundation.wkf.WKFObject;
import org.openflexo.foundation.wkf.ws.PortRegistery;
import org.openflexo.foundation.wkf.ws.ServiceInterface;
import org.openflexo.inspector.widget.DenaliWidget;


/*
 * selector of either a ServiceInterface, either a Process (defaultServiceInterface),
 * either a defaultServiceInterface (=portRegistry)
 *
 */
public class ServiceInterfaceSelectorParameter extends ParameterDefinition<WKFObject> {

	private ProcessSelectingConditional _processSelectingConditional;
	
    public ServiceInterfaceSelectorParameter(String name, String label, FlexoProcess defaultValue)
    {
        super(name,label,defaultValue);
        addParameter("className","org.openflexo.components.widget.ServiceInterfaceInspectorWidget");
        _processSelectingConditional = null;
    }
    
   @Override
public String getWidgetName() 
   {
        return DenaliWidget.CUSTOM;
    }

   public boolean isAcceptableProcess(FlexoProcess process){
	   if (_processSelectingConditional != null) return _processSelectingConditional.isSelectable(process);
	   return true;
   }
   public boolean isAcceptableProcess(PortRegistery apt){
	   return true;
   }
   
   public boolean isAcceptableProcess(ServiceInterface anObject) 
   {		
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
}
