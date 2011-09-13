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
package org.openflexo.foundation.ws.action;

import java.io.File;
import java.util.Enumeration;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openflexo.dataimporter.DataImporter;
import org.openflexo.dataimporter.DataImporterLoader.KnownDataImporter;
import org.openflexo.foundation.FlexoEditor;
import org.openflexo.foundation.FlexoException;
import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.InvalidArgumentException;
import org.openflexo.foundation.action.FlexoAction;
import org.openflexo.foundation.action.FlexoActionType;
import org.openflexo.foundation.dm.DMEntity;
import org.openflexo.foundation.dm.DMPackage;
import org.openflexo.foundation.dm.DMProperty;
import org.openflexo.foundation.dm.DMType;
import org.openflexo.foundation.dm.JDKRepository;
import org.openflexo.foundation.dm.WSDLRepository;
import org.openflexo.foundation.rm.FlexoProject;
import org.openflexo.foundation.wkf.FlexoProcess;
import org.openflexo.foundation.wkf.ws.PortRegistery;
import org.openflexo.foundation.wkf.ws.ServiceInterface;
import org.openflexo.foundation.ws.FlexoWSLibrary;
import org.openflexo.foundation.ws.InternalWSService;
import org.openflexo.foundation.ws.WSService;
import org.openflexo.localization.FlexoLocalization;

public class CreateNewWebService extends FlexoAction 
{
	
	private static final Logger logger = Logger.getLogger(CreateNewWebService.class.getPackage().getName());
	
	public static final String INTERNAL_WS = "INTERNAL_WS";
	public static final String EXTERNAL_WS = "EXTERNAL_WS";
	
	
	public static FlexoActionType actionType = new FlexoActionType ("ws_add_webservice...",FlexoActionType.newMenu,FlexoActionType.defaultGroup,FlexoActionType.ADD_ACTION_TYPE) {
		
		/**
		 * Factory method
		 */
		@Override
		public FlexoAction makeNewAction(FlexoModelObject focusedObject, Vector globalSelection, FlexoEditor editor) 
		{
			return new CreateNewWebService(focusedObject, globalSelection, editor);
		}
		
		@Override
		protected boolean isVisibleForSelection(FlexoModelObject object, Vector globalSelection) 
		{
			return KnownDataImporter.WSDL_IMPORTER.isAvailable();
		}
		
		@Override
		protected boolean isEnabledForSelection(FlexoModelObject object, Vector globalSelection) 
		{
			return true;
		}
		
	};
	
	CreateNewWebService (FlexoModelObject focusedObject, Vector globalSelection, FlexoEditor editor)
	{
		super(actionType, focusedObject, globalSelection, editor);
	}
	
	protected CreateNewWebService (FlexoActionType anActionType, FlexoModelObject focusedObject, Vector globalSelection, FlexoEditor editor)
	{
		super(anActionType, focusedObject, globalSelection, editor);        
	}
	
	private String _newWebServiceName;
	private PortRegistery _portRegistry;
	private File _wsdlFile;
	private FlexoProject _project;
	private ServiceInterface _serviceInterface;
	
	public String getNewWebServiceName()
	{
		return _newWebServiceName;
	}
	
	public void setNewWebServiceName(String name) 
	{
		_newWebServiceName = name;
	}

	public File getWsdlFile() 
	{
		return _wsdlFile;
	}
	
	public void setWsdlFile(File file) 
	{
		_wsdlFile = file;
	}

	public FlexoProject getProject() 
	{
		return _project;
	}
	
	public void setProject(FlexoProject project) 
	{
		_project = project;
	}
	

	/*
	 * For export, either we specify a 
	 */
	public PortRegistery getPortRegistry(){
		return _portRegistry;
	}
	public void setPortRegistry(PortRegistery a){
		_portRegistry = a;
	}
	
	public ServiceInterface getServiceInterface(){
		return _serviceInterface;
	}
	public void setServiceInterface(ServiceInterface a){
		_serviceInterface=a;
	}
	
	public FlexoProcess getFlexoProcess(){
		if (getServiceInterface()!=null) return getServiceInterface().getProcess();
		if (getPortRegistry()!=null) return getPortRegistry().getProcess();
		return null;
	}
	
	private FlexoWSLibrary getWSLibrary(){
		return getProject().getFlexoWSLibrary();
	}
	
	
	
	
	private String _webServiceType;
	
	public String getWebServiceType() 
	{
		return _webServiceType;
	}
	
	public void setWebServiceType(String type)
	{
		_webServiceType = type;
	}
	
	private WSService _newWebService=null;
	
	public WSService getNewWebService(){
		return _newWebService;
	}
	
	
	
	@Override
	protected void doAction(Object context) throws FlexoException
	{
		logger.info ("CREATE EXTERNAL WebService");
		
		if (_webServiceType.equals(CreateNewWebService.EXTERNAL_WS)) {
			logger.info("Importing from WSDL...");
			DataImporter wsdlImporter = KnownDataImporter.WSDL_IMPORTER.getImporter();
			if (wsdlImporter != null) {
				Object[] params = new Object[2];
				params[0] = getNewWebServiceName();
				params[1] = this;
				makeFlexoProgress(FlexoLocalization
						.localizedForKey("importing")
						+ " " + getWsdlFile().getName(), 4);
				_newWebService = (WSService)wsdlImporter.importInProject(getProject(),getWsdlFile(),params);
				hideFlexoProgress();
			}
			else {
				logger.warning("Sorry, data importer "+KnownDataImporter.WSDL_IMPORTER.getClassName()+" not found ");
			}
			logger.info("Importing from WSDL... DONE.");
		} 
		else if (_webServiceType.equals(CreateNewWebService.INTERNAL_WS)) {
			logger.info ("EXPORT FLEXO PROCESS AS WEBSERVICE");
			
			
			makeFlexoProgress(FlexoLocalization
					.localizedForKey("exporting")
					+ " " + getFlexoProcess().getName(), 4);
			//_newWebService = (WSService)wsdlImporter.importInProject(getProject(),getWsdlFile(),params);
			
			// 1. Get/Create internal service group
			_newWebService = getProject().getFlexoWSLibrary().getInternalWSServiceNamed(getNewWebServiceName());
			if(_newWebService==null){
				
				_newWebService = getWSLibrary().createInternalWSService(getNewWebServiceName());	
				_newWebService = getWSLibrary().addInternalWSServiceNamed((InternalWSService)_newWebService);
			}
			
			
			//2. add ServiceInterface to the group
			
			if(getServiceInterface()!=null){
				if (logger.isLoggable(Level.INFO)) logger.info("adding ServiceInterface:"+getServiceInterface().getName());
				_newWebService.addServiceInterfaceAsPortType(getServiceInterface());
			}
			else if (getPortRegistry()!=null){
				if (logger.isLoggable(Level.INFO)) logger.info("adding portRegistry:"+getPortRegistry().getName());
				ServiceInterface si = getFlexoProcess().addServiceInterface(getNewWebServiceName());
				si = ServiceInterface.copyPortsFromRegistry(si, getPortRegistry());
				_newWebService.addServiceInterfaceAsPortType(si);
				setServiceInterface(si);
			}
			else throw new InvalidArgumentException("Incorrect Argument: missing ServiceInterface or PortRegistery","ws_no_service_interface_specified");
			
	/* No more Copy
			//3. if subprocess is defined with WSDLRepository's data, it's ok.
			//  	else refactoring: duplicate data in all messageDefinitionBinding
			//   and copy them in a WSDLRepository .
			
				
			
			Vector ports = getFlexoProcess().getPortRegistery().getAllPorts();
			if(ports==null) return;
			Enumeration en = ports.elements();
			while (en.hasMoreElements()) {
				FlexoPort port = (FlexoPort)en.nextElement();
				Vector entries = new Vector();
				if(port.isInPort()){
					System.out.println("inport");
					AbstractInPort inport=(AbstractInPort)port;
					entries.addAll( inport.getInputMessageDefinition().getEntries() );
				}
				if(port.isOutPort()){
					System.out.println("outport");
					OutputPort outport=(OutputPort)port;
					entries.addAll(outport.getOutputMessageDefinition().getEntries());
				}
				if(entries!=null){
					System.out.println("enumeration on entries");
					Enumeration en1 = entries.elements();
					while (en1.hasMoreElements()) {
						MessageEntry entry = (MessageEntry)en1.nextElement();
						DMEntity type = entry.getType();
						System.out.println("Entry:"+ entry.getVariableName()+ " type:"+ entry.getType());
						
						if( type.getRepository() instanceof JDKRepository){
							// do nothing.
						}
						else{
							System.out.println("duplicating");
							// duplicate repository entry into a the WSDLRepository of this WSService.
							WSRepository wsRep = _newWebService.getWSRepositoryNamed(getNewWebServiceName());
							WSDLRepository repo = null;
							if(wsRep==null){
								repo = WSDLRepository.createNewWSDLRepository(getNewWebServiceName(),getProject().getDataModel(),null,getFlexoProgress());
								_newWebService.addRepository(repo);
							}
							else repo = wsRep.getWSDLRepository();
							
							DMEntity newType = copyEntity(type, repo);
							entry.setType(newType);
							
						}
					}
				}
				
			}
			*/
			
			hideFlexoProgress();
		}       
	}
	
	private DMEntity copyEntity(DMEntity entity, WSDLRepository toRep){
		
		//0. Check if the entity has no been already duplicated.
		if(toRep.getDMEntity(entity.getEntityPackageName(), entity.getName())!=null){
			return toRep.getDMEntity(entity.getEntityPackageName(), entity.getName());
		}
		
		System.out.println("Copying entity:"+entity.getName());
		//1. if has a parent entity, copy parent entity.
		DMEntity newParent = null;
		if(entity.getParentBaseEntity()!=null) newParent = copyEntity(entity.getParentBaseEntity(),toRep); 
		
		//2. copy entity;
		DMPackage fromPack = entity.getPackage();
		DMPackage toPack = toRep.getPackageWithName(fromPack.getName());
		if(toPack==null){
			toPack = toRep.createPackage(fromPack.getName());
		}
		
		DMEntity newEntity = new DMEntity(toPack.getDMModel(), entity.getName(), toPack.getName(), entity.getName(), DMType.makeResolvedDMType(newParent));
		toPack.getRepository().registerEntity(newEntity);
		
		//3. copy properties
		Enumeration en = entity.getProperties().keys();
		while (en.hasMoreElements()) {
			DMProperty property = entity.getProperty((String)en.nextElement());
			DMEntity propertyType=null;
			if(property!=null){
				//1. duplicate propertyType. is a complexType not in the wsdl repository...
				propertyType = property.getType().getBaseEntity();
				if(propertyType!=null){
					if(!propertyType.getRepository().equals(toRep)&& !(propertyType.getRepository() instanceof JDKRepository)){
						//copy entity's property
						propertyType = copyEntity(propertyType, toRep);
					}
				}
			}
			DMProperty newProperty = new DMProperty(
					entity.getDMModel(), 
					property.getName(), 
					DMType.makeResolvedDMType(propertyType), 
					property.getCardinality(),
					property.getIsReadOnly(), 
					property.getIsSettable(),
					property.getImplementationType());
			newEntity.registerProperty(newProperty, false);
		}
		
		
		return newEntity;
	}
	
}
