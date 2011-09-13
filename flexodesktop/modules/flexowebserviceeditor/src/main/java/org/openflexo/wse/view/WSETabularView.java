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
package org.openflexo.wse.view;


import org.openflexo.components.tabular.TabularView;
import org.openflexo.components.tabular.model.AbstractModel;
import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.dm.DMObject;
import org.openflexo.foundation.dm.DMRepository;
import org.openflexo.foundation.wkf.ws.MessageEntry;
import org.openflexo.foundation.wkf.ws.ServiceInterface;
import org.openflexo.foundation.wkf.ws.ServiceMessageDefinition;
import org.openflexo.foundation.wkf.ws.ServiceOperation;
import org.openflexo.foundation.ws.WSObject;
import org.openflexo.foundation.ws.WSPortType;
import org.openflexo.foundation.ws.WSRepository;
import org.openflexo.wse.controller.WSEController;


public class WSETabularView extends TabularView {

    public WSETabularView(WSEController controller, AbstractModel model, int visibleRowCount)
    {
        super(controller,model,visibleRowCount);
    }

    public WSETabularView(WSEController controller, AbstractModel model)
    {
        super(controller,model);
    }
    
    @Override
	protected FlexoModelObject getParentObject(FlexoModelObject object)
    {
    		if(object instanceof WSObject){
    			return (WSObject) ((WSObject)object).getParent();
    		}
    		else if(object instanceof DMRepository){
        		WSRepository wsr = ((DMRepository)object).getProject().getFlexoWSLibrary().getWSRepositoryNamed(((DMRepository)object).getName());
             if(wsr!=null) return wsr.getWSService();
    		}
    		else if (object instanceof DMObject) {
                return (DMObject)(((DMObject)object).getParent());
            }
    		else if(object instanceof ServiceInterface){
 	           WSPortType wsp = ((ServiceInterface)object).getProject().getFlexoWSLibrary().getWSPortTypeNamed(((ServiceInterface)object).getName());
 	      		if(wsp !=null) return wsp.getWSService();
 	        }
		else if(object instanceof ServiceOperation)
			return ((ServiceOperation)object).getServiceInterface();
		
		else if(object instanceof ServiceMessageDefinition){
			return ((ServiceMessageDefinition)object).getOperation();
		}
		else if(object instanceof MessageEntry){
			return ((MessageEntry)object).getMessage();
		}
        
        return null;
    }

}
