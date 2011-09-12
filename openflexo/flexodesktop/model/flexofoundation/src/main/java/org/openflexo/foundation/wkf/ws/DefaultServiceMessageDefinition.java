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
package org.openflexo.foundation.wkf.ws;

import java.util.Vector;
import java.util.logging.Logger;

import org.openflexo.foundation.ie.dm.BindingAdded;
import org.openflexo.foundation.ie.dm.BindingRemoved;
import org.openflexo.foundation.wkf.FlexoProcess;
import org.openflexo.inspector.InspectableObject;


/**
 * 
 * Used to store port's message definition
 * 
 * @author sguerin
 */
public class DefaultServiceMessageDefinition extends ServiceMessageDefinition implements InspectableObject {

    @SuppressWarnings("unused")
	private static final Logger logger = Logger.getLogger(DefaultServiceMessageDefinition.class.getPackage().getName());

    private MessageDefinition portMessageDefinition;

    
    /**
     * Dynamic constructor
     */
    public DefaultServiceMessageDefinition(FlexoProcess process,ServiceOperation operation, MessageDefinition def)
    {
        super(process, operation);
        portMessageDefinition=def;
    }
   

   
   @Override
public boolean isInputMessageDefinition(){
   		return portMessageDefinition.isInputMessageDefinition();
   }
   @Override
public boolean isOutputMessageDefinition(){
   		return portMessageDefinition.isOutputMessageDefinition();
   }
   @Override
public boolean isFaultMessageDefinition(){
   		return portMessageDefinition.isFaultMessageDefinition();
   }
   @Override
public void setIsInputMessageDefinition(){
   		portMessageDefinition.setIsInputMessageDefinition();
   }
   @Override
public void setIsOutputMessageDefinition(){
   		portMessageDefinition.setIsOutputMessageDefinition();
   }
   @Override
public void setIsFaultMessageDefinition(){
   		portMessageDefinition.setIsFaultMessageDefinition();
   }

   @Override
public String getName(){
   		return portMessageDefinition.getName();
   }
   
   @Override
public void setName(String n){
   		portMessageDefinition.setName(n);
   }
   
   @Override
public String getDescription(){
   		return portMessageDefinition.getDescription();
   }
   
   @Override
public void setDescription(String s){
   		portMessageDefinition.setDescription(s);
   }
   

   @Override
public Vector getEntries() 
   {
       return portMessageDefinition.getEntries();
   }

   @Override
public void setEntries(Vector entries) 
   {
       portMessageDefinition.setEntries(entries);
   }

   @Override
public void addToEntries(MessageEntry entry) 
   {
	   portMessageDefinition.addToEntries(entry);
	   setChanged();
       notifyObservers(new BindingAdded(entry));
   }

   @Override
public void removeFromEntries(MessageEntry entry) 
   {
	   portMessageDefinition.removeFromEntries(entry);
       setChanged();
       notifyObservers(new BindingRemoved(entry));
  }


   // ==========================================================================
   // ================================= Delete ===============================
   // ==========================================================================

  
   @Override
public void delete()
   {
	   portMessageDefinition.delete();
       super.delete();
       portMessageDefinition=null;
   }
 

}
