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
import org.openflexo.foundation.wkf.ws.FlexoPort;
import org.openflexo.inspector.widget.DenaliWidget;


public class PortParameter extends ParameterDefinition<FlexoPort> {

   // private ProcessSelectingConditional _processSelectingConditional;
    public FlexoProcess fatherProcess;
	
    public PortParameter(String name, String label, FlexoProcess fatherProcess, FlexoPort defaultValue)
    {
        super(name,label,defaultValue);
        this.fatherProcess = fatherProcess;
        addParameter("className","org.openflexo.components.widget.FlexoPortInspectorWidget");
        
        //keypath depuis l'object inspecte.
        //ici object inspecte = ParametersModel.
        //dans un inspecteur normal, l'objet inspecte = par exemple un FlexoProcess, ou un ServiceInterface.
        //donc on aurait un keypath genre: "this" (si objet inspecte est un flexoprocess
        //ou "process" (si objet inspecte est un portRegistry).
        //
        // pq? params.name... ?
        // dans ParametersModel, on a overrider les getValueForKey pour pointer directement sur l'objet inspecte
        // donc avec "params" est une bidouille pour remonter sur le ParametersModel et "name", pour recuperer
        // la bonne parameterDefinition.(celle-ci)
        addParameter("process", "params."+name+".fatherProcess");
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
