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

import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.tree.TreeNode;

import org.openflexo.foundation.DataModification;
import org.openflexo.foundation.FlexoObservable;
import org.openflexo.foundation.FlexoObserver;
import org.openflexo.foundation.rm.FlexoXMLStorageResource;
import org.openflexo.foundation.wkf.FlexoProcess;
import org.openflexo.foundation.wkf.ws.ServiceInterface;
import org.openflexo.foundation.ws.dm.WSPortTypeRemoved;
import org.openflexo.foundation.xml.FlexoWSLibraryBuilder;
import org.openflexo.logging.FlexoLogger;


public class WSPortType extends WSObject implements FlexoObserver
	{

	

	    private static final Logger logger = FlexoLogger.getLogger(WSPortType.class.getPackage()
	            .getName());

	    /**currently, interfaceName is the same as portType name...*/
	    private WSService parentService;
	    private String interfaceName;
	    private String processName;
	    

	    public WSPortType(FlexoWSLibraryBuilder builder) {
	        this(builder.wsLibrary);
	        initializeDeserialization(builder);
	    }

	    
	    /**
	     * 
	     */
	    public WSPortType(FlexoWSLibrary lib)
	    {
	        super(lib);
	    }
	    /**
	     * dynamic constructor
	     * @param lib
	     * @param process
	     * @param parentGroup
	     */
	    public WSPortType(FlexoWSLibrary lib, ServiceInterface serviceInterface, WSService parentGroup){
	    		this(lib);
	    		try{
	    		setName(serviceInterface.getName());
	    		}
	    		catch(DuplicateWSObjectException e){e.printStackTrace();}
	    		setProcessName(serviceInterface.getProcess().getName());
	    		setWSService(parentService);
	    }
	    
	    /**
	     * Overrides getFullyQualifiedName
	     * 
	     * @see org.openflexo.foundation.FlexoModelObject#getFullyQualifiedName()
	     */
	    @Override
		public String getFullyQualifiedName()
	    {
	        return "WSPortType_"+getName()+"_"+getProject().getProjectName();
	    }

	    /**
	     * Overrides getFlexoXMLFileResource
	     * 
	     * @see org.openflexo.foundation.rm.XMLStorageResourceData#getFlexoXMLFileResource()
	     */
	    public FlexoXMLStorageResource getFlexoXMLFileResource()
	    {
	        return (FlexoXMLStorageResource) getFlexoResource();
	    } 
	    
	    

	    public static Logger getLogger()
	    {
	        return logger;
	    }


	    public WSService getWSService(){
	    		return parentService;
	    }
	    public void setWSService(WSService s){
	    		parentService = s;
	    }
	    
	    public String getProcessName(){
	    		return processName;
	    }
	    
	    public void setProcessName(String name){
	    		processName=name;
	    }
	    
	    @Override
		public void setName(String aName) throws DuplicateWSObjectException{
	    		if(!isDeserializing())checkName(getWSLibrary(), aName, this);
	    		super.setName(aName);
	    		interfaceName=aName;
	    }
	    public static void checkName(FlexoWSLibrary lib, String name, WSPortType currentPT) throws DuplicateWSObjectException{
	    		WSPortType pt = lib.getWSPortTypeNamed(name);
	    		if(pt!=null&&pt!=currentPT){
	    			throw new DuplicateWSObjectException(pt, "a_porttype_with_this_name_already_exists");
	    		}
	    }
	    
	    public String getInterfaceName(){
	    		return interfaceName;
	    }
	    
	    public void setInterfaceName(String name)throws DuplicateWSObjectException{
	    		setName(name);
	    }
	    
	    public FlexoProcess getFlexoProcess() {
	        
	    		return getProject().getLocalFlexoProcess(getProcessName());
	    }
	    
	    public ServiceInterface getServiceInterface()
	    {
	    	if (getFlexoProcess() != null) {
	    		return getFlexoProcess().getServiceInterfaceNamed(getInterfaceName());
	    	}
	    	else {
	    		logger.warning("WSPortType refer to null FlexoProcess !");
	    		return null;
	    	}
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
	    	/*
	        if (dataModification instanceof LanguageRemoved) {
	            Language lg = (Language) ((LanguageRemoved) dataModification).oldValue();
	            Enumeration en = getKeys().elements();
	            while (en.hasMoreElements()) {
	                Key key = (Key) en.nextElement();
	                values.remove(lg.getName() + "." + key.getName());
	            }
	            getValueList().setChanged();
	            getValueList().notifyObservers(dataModification);
	        } else if (dataModification instanceof LanguageAdded) {
	            Language lg = (Language) ((LanguageAdded) dataModification).newValue();
	            Enumeration en = keys.elements();
	            while (en.hasMoreElements()) {
	                Key key = (Key) en.nextElement();
	                Value v = new Value(getDkvModel(), key, lg);
	                values.put(v.getFullyQualifiedName(), v);
	            }
	            getValueList().setChanged();
	            getValueList().notifyObservers(dataModification);
	        } else if (dataModification instanceof DKVDataModification && ((DKVDataModification)dataModification).propertyName().equals("value")) {
	            setChanged();
	            notifyObservers(dataModification);
	        }
	        */
	    }

	    /**
	     * deletes the WSPortType BUT do not delete the underlying FlexoProcess.
	     * it is the wsgroup's responsibility to decide wheither the FlexoProcess
	     * should be deleted.
	     */
	    @Override
		public void delete(){
	    	if (logger.isLoggable(Level.FINE)) logger.fine("delete: WSPortType "+getName());
	    		parentService.removeFromWSPortTypes(this);
	    		parentService=null;
	    		processName=null;
	    		interfaceName=null;
	    		
	    		//Delete only deletes WSObjects by default.
	    		//It is the responsibility of the WSService to decide if it should delete also the
	    		//real FlexoProcess.
	    		
	        // getFlexoProcess().delete();
	          
	            
	    		super.delete();
	    		setChanged();
	    		notifyObservers(new WSPortTypeRemoved(this));
	    		deleteObservers();
	    }
	    
	    
	       // ==========================================================================
        // ======================== TreeNode implementation
        // =========================
        // ==========================================================================

        @Override
		public TreeNode getParent()
        {
            return getWSService().getWSPortTypeFolder();
        }

        @Override
		public boolean getAllowsChildren()
        {
            return false;
        }
        
        @Override
		public Vector getOrderedChildren(){
        		return null;
        }

        @Override
		public String getClassNameKey() {
        		return "ws_port_type";
        }
	}
