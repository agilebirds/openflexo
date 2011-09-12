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
package org.openflexo.foundation.ws;

import java.util.Enumeration;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.tree.TreeNode;

import org.openflexo.foundation.DataModification;
import org.openflexo.foundation.FlexoObservable;
import org.openflexo.foundation.FlexoObserver;
import org.openflexo.foundation.Inspectors;
import org.openflexo.foundation.ws.action.CreateNewWebService;
import org.openflexo.foundation.ws.dm.InternalWSServiceRemoved;
import org.openflexo.foundation.xml.FlexoWSLibraryBuilder;
import org.openflexo.inspector.InspectableObject;
import org.openflexo.logging.FlexoLogger;

public class InternalWSService extends WSService implements FlexoObserver, InspectableObject
	{


	
	    private static final Logger logger = FlexoLogger.getLogger(InternalWSService.class.getPackage()
	            .getName());

	    public InternalWSService(FlexoWSLibraryBuilder builder) {
	        this(builder.wsLibrary);
	        initializeDeserialization(builder);
	    }

	    /**
	     * 
	     */
	    public InternalWSService(FlexoWSLibrary wsLib)
	    {
	        super(wsLib);
	        
	    }

	    /**
	     * Overrides getFullyQualifiedName
	     * 
	     * @see org.openflexo.foundation.FlexoModelObject#getFullyQualifiedName()
	     */
	    @Override
		public String getFullyQualifiedName()
	    {
	        return "INT_WSSERVICE_"+getName();//+"_"+getProject().getProjectName();
	    }

	 
	    
	    @Override
		protected Vector getSpecificActionListForThatClass()
	    {
	         Vector returned = super.getSpecificActionListForThatClass();
	         returned.add(CreateNewWebService.actionType);
	         return returned;
	    }
	
	    
	

	    public static Logger getLogger()
	    {
	        return logger;
	    }


	    
	 
	    
	    /**
	     * Overrides update
	     * 
	     * @see org.openflexo.foundation.FlexoObserver#update(org.openflexo.foundation.FlexoObservable,
	     *      org.openflexo.foundation.DataModification)
	     */
	    @Override
		public void update(FlexoObservable observable, DataModification dataModification)
	    {
	    	//fucking things to do.

	    }
	    
	    /**
	     * Overrides getInspectorName
	     * 
	     * @see org.openflexo.inspector.InspectableObject#getInspectorName()
	     */
	    @Override
		public String getInspectorName()
	    {
	        return Inspectors.WSE.WSINTERNALSERVICE_INSPECTOR;
	    }
	    
	    @Override
		public void delete(){
    		if (logger.isLoggable(Level.FINE)) logger.fine("delete: internalWSGroup "+getName());
	        Vector processesToDelete = new Vector();
	        processesToDelete.addAll(getWSPortTypes());
	        for (Enumeration en = processesToDelete.elements(); en.hasMoreElements();) {
	            WSPortType next = (WSPortType) en.nextElement();
	    			//Delete only deletes WSObjects by default.
	    			//It is the responsibility of the WSService to decide if it should delete also the
	    			//real FlexoProcess.
	            // getFlexoProcess().delete();
	            next.delete();
	        }
	        
	        Vector repositoriesToDelete = new Vector();
	        repositoriesToDelete.addAll(getWSRepositories());
	        for (Enumeration en = repositoriesToDelete.elements(); en.hasMoreElements();) {
	            WSRepository next = (WSRepository) en.nextElement();
	            //Delete only deletes WSObjects by default.
	    			//It is the responsibility of the WSService to decide if it should delete also the
	    			//real DataRepository.
	    			//next.getWSDLRepository().delete();
	            next.delete();
	        }
	        
	        //FileUtils.recursiveDeleteFile(getWSDLFile().getFile());
	        
	        getWSLibrary().removeFromInternalWSServices(this);
	        super.delete();
	        setChanged();
	        notifyObservers(new InternalWSServiceRemoved(this));
	        deleteObservers();
	    }
	    
	    // ==========================================================================
        // ======================== TreeNode implementation
        // =========================
        // ==========================================================================

        @Override
		public TreeNode getParent()
        {
            return getWSLibrary().getInternalWSFolder();
        }

        @Override
		public boolean getAllowsChildren()
        {
            return true;
        }
        
        @Override
		public Vector getOrderedChildren(){
        		Vector a = new Vector();
        		a.add(getWSPortTypeFolder());
        		a.add(getWSRepositoryFolder());
        		return a;
        }
	 
        @Override
		public String getClassNameKey() {
        		return "internal_ws_service";
        }
}
