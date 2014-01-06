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
package org.openflexo.wsdl;

import java.io.File;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.wsdl.Definition;
import javax.wsdl.Operation;
import javax.wsdl.Part;
import javax.wsdl.PortType;
import javax.wsdl.WSDLException;
import javax.wsdl.factory.WSDLFactory;
import javax.wsdl.xml.WSDLReader;
import javax.xml.namespace.QName;

import org.apache.xmlbeans.SchemaGlobalElement;
import org.apache.xmlbeans.SchemaProperty;
import org.apache.xmlbeans.SchemaType;
import org.openflexo.dataimporter.DataImporter;
import org.openflexo.foundation.FlexoException;
import org.openflexo.foundation.InvalidArgumentException;
import org.openflexo.foundation.action.FlexoAction;
import org.openflexo.foundation.dm.DMCardinality;
import org.openflexo.foundation.dm.DMEntity;
import org.openflexo.foundation.dm.DMModel;
import org.openflexo.foundation.dm.DMPackage;
import org.openflexo.foundation.dm.DMProperty;
import org.openflexo.foundation.dm.DMPropertyImplementationType;
import org.openflexo.foundation.dm.DMType;
import org.openflexo.foundation.dm.WSDLRepository;
import org.openflexo.foundation.rm.DuplicateResourceException;
import org.openflexo.foundation.FlexoProject;
import org.openflexo.foundation.wkf.DuplicateWKFObjectException;
import org.openflexo.foundation.wkf.FlexoProcess;
import org.openflexo.foundation.wkf.FlexoWorkflow;
import org.openflexo.foundation.wkf.ws.AbstractInPort;
import org.openflexo.foundation.wkf.ws.FlexoPort;
import org.openflexo.foundation.wkf.ws.InOutPort;
import org.openflexo.foundation.wkf.ws.InPort;
import org.openflexo.foundation.wkf.ws.MessageDefinition;
import org.openflexo.foundation.wkf.ws.MessageEntry;
import org.openflexo.foundation.wkf.ws.OutPort;
import org.openflexo.foundation.wkf.ws.OutputPort;
import org.openflexo.foundation.wkf.ws.ServiceInterface;
import org.openflexo.foundation.ws.AbortedActionException;
import org.openflexo.foundation.ws.DuplicateWSObjectException;
import org.openflexo.foundation.ws.ExternalWSService;
import org.openflexo.foundation.ws.FlexoWSLibrary;
import org.openflexo.foundation.ws.WSService;
import org.openflexo.foundation.ws.dm.PortTypeAndOperationsAdded;
import org.openflexo.localization.FlexoLocalization;
import org.w3c.dom.Node;

public class Importer implements DataImporter {
	private static final Logger logger = Logger.getLogger(Importer.class.getPackage().getName());

	private FlexoProject _project;

	private FlexoAction flexoAction;

	private Hashtable simpleTypesBaseType = new Hashtable();

	private Hashtable<DMProperty, SchemaType> allProperties = new Hashtable<DMProperty, SchemaType>();

	private Vector treatedTypes = new Vector();

	public Importer() {
		super();
	}

	protected FlexoProject getProject() {
		return _project;
	}

	@Override
	public Object importInProject(FlexoProject project, File importedFile, Object[] parameters) throws FlexoException {
		_project = project;
		String wsGroupName = parameters.length >= 1 ? (String) parameters[0] : null;
		String repositoryName = wsGroupName + "-Data";
		flexoAction = parameters.length >= 2 ? (FlexoAction) parameters[1] : null;
		if (flexoAction != null) {
			flexoAction.setProgress(FlexoLocalization.localizedForKey("parsing") + " " + importedFile.getName());
		}

		ExternalWSService wsService = null;
		try {

			WSDLFactory wsdlFactory = WSDLFactory.newInstance();
			WSDLReader wsdlReader = wsdlFactory.newWSDLReader();
			wsdlReader.setFeature("javax.wsdl.verbose", true);
			Definition webServiceDefinition = wsdlReader.readWSDL(importedFile.getAbsolutePath());
			// TODO : explore the webServiceDefinition to insert valuable
			// information in FlexoModel
			// BMA: I suggest to use xmlBeans (http://xmlbeans.apache.org/)
			// to transform the <types> section of the wsdl
			// This will produce some java classes that can be packaged in a jar
			// and then importing the jar in Flexo will do the trick

			// Other sections are specific wsdl can be handeled with the wsdl
			// api.
			// When handeling the other sections keep in mind that we can use
			// entities
			// we have just create with the importation of the jar.

			SchemaTypeExtractor extractor = new SchemaTypeExtractor(importedFile.getAbsolutePath());

			FlexoWSLibrary wsLib = getProject().getFlexoWSLibrary();
			wsService = wsLib.createExternalWSService(wsGroupName);
			importTypesInFlexo(extractor, repositoryName, wsService);
			importServiceInWorkflow(webServiceDefinition, extractor, wsService);

			wsLib.addExternalWSService(wsService, importedFile);
			return wsService;

		} catch (DuplicateResourceException e) {
			if (logger.isLoggable(Level.WARNING)) {
				logger.warning("Attempt to insert twice the same object:" + e.getMessage());
			}
			if (logger.isLoggable(Level.FINE)) {
				logger.fine("Deleting new WSService and childrens");
			}
			if (wsService != null) {
				wsService.delete();
			}
			throw e;
		} catch (DuplicateWSObjectException e) {
			if (logger.isLoggable(Level.WARNING)) {
				logger.warning("Attempt to insert twice the same object:" + e.getMessage());
			}
			if (logger.isLoggable(Level.FINE)) {
				logger.fine("Deleting new WSService and childrens");
			}
			if (wsService != null) {
				wsService.delete();
			}
			throw e;
		} catch (DuplicateWKFObjectException e) {
			if (logger.isLoggable(Level.WARNING)) {
				logger.warning("Attempt to insert twice the same object:" + e.getMessage());
			}
			if (logger.isLoggable(Level.FINE)) {
				logger.fine("Deleting new WSService and childrens");
			}
			if (wsService != null) {
				wsService.delete();
			}
			throw e;
		} catch (InvalidArgumentException e) {
			if (logger.isLoggable(Level.WARNING)) {
				logger.warning("Invalid Argument exception:" + e.getMessage());
			}
			if (logger.isLoggable(Level.FINE)) {
				logger.fine("Deleting new WSService and childrens");
			}
			if (wsService != null) {
				wsService.delete();
			}
			throw e;
		} catch (FlexoException e) {
			e.printStackTrace();
			if (logger.isLoggable(Level.WARNING)) {
				logger.warning("Flexo Exception exception:" + e.getMessage());
			}
			if (logger.isLoggable(Level.FINE)) {
				logger.fine("Deleting new WSService and childrens");
			}
			if (wsService != null) {
				wsService.delete();
			}
			throw e;
		} catch (WSDLException e) {
			if (logger.isLoggable(Level.WARNING)) {
				logger.warning("Exception occured while importing :" + e.getMessage());
				e.printStackTrace();
			}
			if (logger.isLoggable(Level.FINE)) {
				logger.fine("Deleting new WSService and childrens");
			}
			if (wsService != null) {
				wsService.delete();
			}
			throw new FlexoException(e.getMessage());
		} catch (Exception e) {
			if (logger.isLoggable(Level.WARNING)) {
				logger.warning("Exception occured while importing:" + e.getMessage());
				e.printStackTrace();
			}
			if (logger.isLoggable(Level.FINE)) {
				logger.fine("Deleting new WSService and childrens");
			}
			if (wsService != null) {
				wsService.delete();
			}
			throw new AbortedActionException("Exception " + e.getMessage(), "exception_occured");
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	/*
	 * public void actionPerformed(ActionEvent arg0) { JFileChooser chooser =
	 * new JFileChooser("Please select an .wsdl file"); int returnVal =
	 * chooser.showOpenDialog(null); if(returnVal ==
	 * JFileChooser.APPROVE_OPTION) { try { WSDLFactory wsdlFactory =
	 * WSDLFactory.newInstance(); WSDLReader wsdlReader =
	 * wsdlFactory.newWSDLReader(); Definition webServiceDefinition =
	 * wsdlReader.readWSDL(chooser.getSelectedFile().getAbsolutePath());
	 * FlexoController.notify("A new WSDL definition has been read.\nIt still to
	 * include it properly in the Flexo model.\n" + "It's a developper job...
	 * and it can be done starting in class : org.openflexo.wsdl.Importer");
	 * //TODO : explore the webServiceDefinition to insert valuable information
	 * in FlexoModel // BMA: I suggest to use xmlBeans
	 * (http://xmlbeans.apache.org/) // to transform the <types> section of the
	 * wsdl // This will produce some java classes that can be packaged in a jar //
	 * and then importing the jar in Flexo will do the trick // Other sections
	 * are specific wsdl can be handeled with the wsdl api. // When handeling
	 * the other sections keep in mind that we can use entities // we have just
	 * create with the importation of the jar.
	 * 
	 * SchemaTypeExtractor extractor = new
	 * SchemaTypeExtractor(chooser.getSelectedFile().getAbsolutePath());
	 * importTypesInFlexo(extractor);
	 * 
	 * importServiceInWorkflow(webServiceDefinition, extractor); } catch
	 * (Exception e) { FlexoController.notify("An error occurs while processing
	 * wsdl.\n"+e.getMessage()); e.printStackTrace(); } } }
	 */

	protected DMModel getDMModel() {
		return getProject().getDataModel();
	}

	protected void importServiceInWorkflow(Definition webServiceDefinition, SchemaTypeExtractor extractor, WSService wsService)
			throws DuplicateResourceException, DuplicateWKFObjectException, DuplicateWSObjectException, FlexoException {

		if (flexoAction != null) {
			flexoAction.setProgress(FlexoLocalization.localizedForKey("importing_wsprocess_in_workflow"));
		}
		System.out.println("**** OPERATIONS ******");
		Map portTypes = webServiceDefinition.getPortTypes();

		wsService.setTargetNamespace(webServiceDefinition.getTargetNamespace());

		Iterator it = portTypes.values().iterator();
		while (it.hasNext()) {
			PortType pt = (PortType) it.next();
			if (logger.isLoggable(Level.FINE)) {
				logger.fine("PortType:" + pt.getQName());
			}
			String processName = pt.getQName().getLocalPart();

			FlexoWorkflow wkf = getProject().getFlexoWorkflow();

			// will throw a DuplicateResourceException if process already exist
			FlexoProcess _newProcess = FlexoProcess.createNewProcess(wkf, null, processName, false);
			// TODO: bug: getDocumentationElement always return null;

			// Description
			org.w3c.dom.Element docproc = pt.getDocumentationElement();
			if (docproc != null) {
				_newProcess.setDescription(getDocumentationString(docproc));
			}

			_newProcess.setIsWebService(true);

			List operations = pt.getOperations();
			Iterator it2 = operations.iterator();
			while (it2.hasNext()) {

				Operation op = (Operation) it2.next();
				if (logger.isLoggable(Level.FINE)) {
					logger.fine("Operation: " + op.getName());
				}
				// System.out.println("input:"+op.getInput().getName());
				boolean hasInput = false;
				boolean hasOutput = false;

				if (op.getInput() != null) {
					hasInput = true;
				}
				if (op.getOutput() != null) {
					hasOutput = true;
				}

				FlexoPort port = null;
				if (hasInput && hasOutput) {
					// INOUTPORT
					port = _newProcess.getPortRegistery().portWithName(op.getName());
					if (port != null) {
						if (logger.isLoggable(Level.FINE)) {
							logger.fine("Port: " + port.getName() + " already exists...");
						}
						throw new DuplicateResourceException("PORT." + port.getName(), "attempt_to_add_an_already_existing_port");
					} else {
						port = new InOutPort(_newProcess, op.getName());
					}
					_newProcess.getPortRegistery().addToInOutPorts((InOutPort) port);

				} else if (hasInput && !hasOutput) {
					// INPORT
					port = _newProcess.getPortRegistery().portWithName(op.getName());
					if (port != null) {
						if (logger.isLoggable(Level.FINE)) {
							logger.fine("Port: " + port.getName() + " already exists...");
						}
						throw new DuplicateResourceException("PORT." + port.getName(), "attempt_to_add_an_already_existing_port");
					} else {
						port = new InPort(_newProcess, op.getName());
					}
					_newProcess.getPortRegistery().addToInPorts((InPort) port);

				} else if (hasOutput && !hasInput) {
					// OUTPORT
					port = _newProcess.getPortRegistery().portWithName(op.getName());
					if (port != null) {
						if (logger.isLoggable(Level.FINE)) {
							logger.fine("Port: " + port.getName() + " already exists...");
						}
						throw new DuplicateResourceException("PORT." + port.getName(), "attempt_to_add_an_already_existing_port");
					} else {
						port = new OutPort(_newProcess, op.getName());
					}
					_newProcess.getPortRegistery().addToOutPorts((OutPort) port);
				}

				// Description
				org.w3c.dom.Element docOp = op.getDocumentationElement();
				if (docOp != null) {
					port.setDescription(docOp.getNodeValue());
				}

				// TODO: there can be an order in inputs....
				if (hasInput) {
					Iterator it3 = op.getInput().getMessage().getParts().values().iterator();
					MessageDefinition def = ((AbstractInPort) port).getInputMessageDefinition();
					def.setName(op.getInput().getMessage().getQName().getLocalPart());
					def.setDescription(getDocumentationString(op.getInput().getMessage().getDocumentationElement()));

					while (it3.hasNext()) {
						Part input = (Part) it3.next();
						if (logger.isLoggable(Level.FINEST)) {
							logger.finest("arg: " + input.getName() + " " + input.getTypeName());
						}
						addTypeToMessageDef(def, extractor, input, _newProcess);
					}
				}
				if (hasOutput) {
					Iterator it3 = op.getOutput().getMessage().getParts().values().iterator();
					MessageDefinition def = ((OutputPort) port).getOutputMessageDefinition();
					def.setName(op.getOutput().getMessage().getQName().getLocalPart());

					def.setDescription(getDocumentationString(op.getInput().getMessage().getDocumentationElement()));

					while (it3.hasNext()) {
						Part output = (Part) it3.next();
						// System.out.println()output.get
						if (logger.isLoggable(Level.FINEST)) {
							logger.finest("outp:" + output.getName() + " " + output.getTypeName());
						}
						addTypeToMessageDef(def, extractor, output, _newProcess);

					}
				}

			}
			// Add corresponding serviceOperation and ServiceMessageDefinition
			ServiceInterface _newServiceInterface = null;
			_newServiceInterface = _newProcess.addServiceInterface(processName);

			wsService.addServiceInterfaceAsPortType(_newServiceInterface);

			ServiceInterface.copyPortsFromRegistry(_newServiceInterface, _newProcess.getPortRegistery());

			_newProcess.getPortRegistery().setIsVisible(true);
			_newProcess.setChanged();
			_newProcess.notifyObservers(new PortTypeAndOperationsAdded(_newProcess));
		}
	}

	private void addTypeToMessageDef(MessageDefinition def, SchemaTypeExtractor extractor, Part messagePart, FlexoProcess newProcess)
			throws FlexoException {
		MessageEntry entry = new MessageEntry(newProcess, def);
		entry.setVariableName(messagePart.getName());

		// Find TYPE of input
		// take care of element
		QName elementName = messagePart.getElementName();
		QName typeName = messagePart.getTypeName();
		if (logger.isLoggable(Level.FINE)) {
			logger.fine("ElementName:" + elementName + " TypeName:" + typeName);
		}

		SchemaType inputType = null;
		// works with a schema declaration of this type
		// <s:complexType name="getJoke">....</s:complexType>
		// <s:element name="getJoke" type="tns:getJoke"/>

		// first if message type is given by the "type" attribute
		if (typeName != null) {
			inputType = extractor.schemaTypeLoader().findType(typeName);
		} else if (elementName != null) {
			inputType = extractor.schemaTypeLoader().findType(elementName);
		}

		if (logger.isLoggable(Level.FINE)) {
			logger.fine("input Type:" + inputType);
		}

		// third for cases like:
		// <s:element name="getJoke">
		// <s:complexType>....</s:complexType>
		// </s:element>
		if (inputType == null && elementName != null) {// try something else...
			SchemaGlobalElement element = extractor.schemaTypeSystem().findElement(elementName);
			SchemaType elementType = element.getType();
			inputType = elementType;// elementType.getAnonymousTypes()[0];
		}

		String[] typeArray = XMLTypeMapper.getFullJavaNameForType(inputType, extractor, simpleTypesBaseType);
		String typeString = typeArray[0];

		if ("VECTOR".equals(typeArray[1])) {
			// type of the binding is a vector
			typeString = "java.util.Vector";
		}

		// Extrat check for inner classes (third case gives a type of
		// pack.GetJokeDocument&GetJoke)
		if (typeString != null && typeString.indexOf("$") > 0) {
			typeString = typeString.replace('$', '.');
		}

		// removing all the [], and increasing the dimension in consequence.
		int dim = 0;
		int index = 0;
		while (index != -1) {
			index = typeString.indexOf("[]");
			if (index != -1) {
				dim++;
				typeString = typeString.substring(0, index);
			}
		}

		DMType dmType = null;
		if (typeString != null) {
			dmType = DMType.makeResolvedDMType(getProject().getDataModel().getDMEntity(typeString));
			dmType.setDimensions(dim);
			if (logger.isLoggable(Level.FINE)) {
				logger.fine("fullpath type:" + (dmType != null ? dmType.getName() : "null type"));
			}
		}

		if (dmType == null || dmType.getName() == null) {
			dmType = DMType.makeStringDMType(this.getProject());
			// throw new
			// FlexoException("The specified data type \""+typeString+"\" for message "+def.getName()+" could not be matched to any type in the DataModel.");
		}

		entry.setType(dmType);
		def.addToEntries(entry);
	}

	protected WSDLRepository importTypesInFlexo(SchemaTypeExtractor extractor, String projectRepositoryName, WSService wsGroup)
			throws FlexoException {

		// String projectRepositoryName = FlexoController.askForString("Please
		// enter a name of the new WebService Project Repository.");
		// packageName is the name of the namespace....
		// String packageName = FlexoController.askForString("Please enter a
		// name of the new package.");

		// WE JUST CREATE THE REPOSITORY. IT WILL BE ADDED TO THE DMMODEL LATER.
		// WSDLRepository repo =
		// WSDLRepository.createNewWSDLRepository(projectRepositoryName,
		// getDMModel());
		WSDLRepository repo = WSDLRepository.createNewWSDLRepository(projectRepositoryName, getDMModel(), extractor.getWsdlFile(),
				flexoAction.getFlexoProgress());

		wsGroup.addRepository(repo);

		// DMPackage pack = repo.createPackage(packageName);
		if (flexoAction != null) {
			flexoAction.setProgress(FlexoLocalization.localizedForKey("importing_wstypes_in_datamodel"));
		}
		Hashtable entities = createDMEntities2(extractor, repo);
		resolvePropertyType(extractor);
		return repo;

	}

	private DMProperty createProperty(DMEntity type, String newPropertyName, QName newPropertyType, boolean isVector) {

		// let's set the default type to null... The second pass will actually set it.
		DMType defaultType = DMType.makeResolvedDMType(type.getDMModel().getDMEntity(String.class));

		DMProperty newProperty = new DMProperty(type.getDMModel(), /* entity, */newPropertyName, defaultType,
				isVector ? DMCardinality.VECTOR : DMCardinality.SINGLE, type.getIsReadOnly(), true,
				DMPropertyImplementationType.PUBLIC_ACCESSORS_PRIVATE_FIELD);
		type.registerProperty(newProperty, false);
		return newProperty;
	}

	private DMEntity createEntity(String entityName, DMPackage pack) {
		DMEntity newEntity = new DMEntity(pack.getDMModel(), entityName, pack.getName(), entityName, null);
		pack.getRepository().registerEntity(newEntity);
		return newEntity;
	}

	private String getDocumentationString(org.w3c.dom.Element docElement) {
		if (docElement == null) {
			return null;
		}
		Node child = docElement.getFirstChild();
		if (child == null) {
			return null;
		}
		String comment = child.getNodeValue();
		return comment;
	}

	private Hashtable createDMEntities2(SchemaTypeExtractor extractor, WSDLRepository repository) {
		Hashtable toReturn = new Hashtable();
		Vector allTypes = new Vector(Arrays.asList(extractor.schemaTypeSystem().globalTypes()));

		/* We need to add the ElementTypes: ie Anonymous complex types of */
		List documentTypes = new Vector();
		documentTypes.addAll(Arrays.asList(extractor.schemaTypeSystem().documentTypes()));
		for (int i = 0; i < documentTypes.size(); i++) {
			SchemaType sType = (SchemaType) documentTypes.get(i);
			allTypes.addAll(Arrays.asList(sType.getAnonymousTypes()));
		}

		for (int i = 0; i < allTypes.size(); i++) {
			SchemaType sType = (SchemaType) allTypes.get(i);
			treatType(sType, toReturn, repository);
		}
		return toReturn;
	}

	private void treatType(SchemaType t, Hashtable ht, WSDLRepository repository) {
		if (treatedTypes.contains(t)) {
			return;
		}
		if (t.isBuiltinType()) {
			// do nothing
		}
		if (t.isSimpleType()) {
			if (t.getSimpleVariety() == SchemaType.ATOMIC) {
				// This defines a restriction of an existing type.
				// if(!t.isAnonymousType()) System.out.println("Adding simpleType : "+t.getName().getLocalPart()
				// +" of type : "+t.getBaseType().getName().getLocalPart());
				simpleTypesBaseType.put(t, t.getBaseType());
			} else {
				System.out.println("Unsupported simple type");
			}
		}
		if (!t.isSimpleType()) {
			if (t.getContentType() == SchemaType.SIMPLE_CONTENT) {
				// this defines an extension of an existing simple type
				// we include the simple type as a property.
				// TO-DO : handle the attributes

				DMEntity entity = buildEntity(ht, t, null, repository);

				SchemaType baseType = t.getBaseType();
				if (baseType.isSimpleType()) {
					treatProperty(ht, repository, entity, baseType.getName().getLocalPart(), baseType, false);
				} else {
					System.out.println("Not supported to extend complex types...");
				}
			} else if (t.getContentType() == SchemaType.EMPTY_CONTENT) {
				// unsupported
			} else {
				// we create an entity in this case
				Vector properties = new Vector(Arrays.asList(t.getElementProperties()));
				buildEntity(ht, t, properties, repository);
			}
		}
		treatedTypes.add(t);
	}

	/*
	 * creates a new entity in the DM, with its name extracted from the SchemaType and the properties as provided
	 */
	private DMEntity buildEntity(Hashtable ht, SchemaType t, Vector properties, WSDLRepository repository) {
		String className = t.getShortJavaName();
		String packName = t.getFullJavaName().substring(0, t.getFullJavaName().lastIndexOf(className) - 1);
		while (packName.lastIndexOf('$') != -1) {
			packName = packName.replace("$", ".");
		}
		DMPackage pack = repository.packageWithName(packName);
		if (pack == null) {
			pack = repository.createPackage(packName);
		}
		/*
		if (t.getName() != null) {
			
			
			//repository.addPackageAndNamespace(pack.getName(), t.getName().getNamespaceURI());
			repository.setPackageToNamespaceForKey(t.getName().getNamespaceURI(),pack.getName());
		    System.out.println("Setting namespace to package : "+t.getName().getNamespaceURI());
		} 
		if (t.getName()==null) {
			//repository.addPackageAndNamespace(pack.getName(), t.getProperties()[0].getName().getNamespaceURI());
			repository.setPackageToNamespaceForKey(t.getProperties()[0].getName().getNamespaceURI(),pack.getName());
			System.out.println("Anonymous type : setting namespace to package :" +t.getProperties()[0].getName().getNamespaceURI());
		}
		*/
		DMEntity entity = createEntity(className, pack);

		if (properties != null) {
			for (int i = 0; i < properties.size(); i++) {
				SchemaProperty currentProp = (SchemaProperty) properties.get(i);
				// it looks wierd but if the cardinlaity is "unbounded", getMaxOccurs returns null.
				boolean vector = currentProp.getMaxOccurs() == null || currentProp.getMaxOccurs().intValue() > 1;
				treatProperty(ht, repository, entity, currentProp.getName().getLocalPart(), currentProp.getType(), vector);
			}
		}
		return entity;
	}

	/*
	 * Adds the given property to entity and propagates the Entity generation if needed
	 */
	private void treatProperty(Hashtable ht, WSDLRepository repository, DMEntity entity, String propName, SchemaType propType,
			boolean isVector) {
		// create the property
		// do not set its type : the second pass (resolvePropertyType) will do that
		allProperties.put(createProperty(entity, propName, null, isVector), propType);

		// maybe it defines a new type (anonymous one)
		treatType(propType, ht, repository);

	}

	private void resolvePropertyType(SchemaTypeExtractor extractor) {
		Enumeration<DMProperty> en = allProperties.keys();
		while (en.hasMoreElements()) {
			DMProperty dmProp = en.nextElement();
			SchemaType sType = allProperties.get(dmProp);

			String[] attributeTypeArray = XMLTypeMapper.getFullJavaNameForType(sType, extractor, simpleTypesBaseType);// getJavaTypeForBuiltInType(prop.getType().getPrimitiveType().getBuiltinTypeCode());
			String attributeJavaType = attributeTypeArray[0];
			boolean isArray = false;
			if ("VECTOR".equals(attributeTypeArray[1])) {
				isArray = true;
			}
			DMType dmType = null;

			while (attributeJavaType.indexOf('$') != -1) {
				attributeJavaType = attributeJavaType.replace("$", ".");
			}

			dmType = DMType.makeResolvedDMType(dmProp.getDMModel().getDMEntity(attributeJavaType));

			// System.out.println("DM lookup for " + dmProp.getName() + "(java type :"+attributeJavaType+")"+ " returned "+dmType);

			dmProp.setType(dmType, false);
			if (isArray) {
				dmProp.setCardinality(DMCardinality.VECTOR);
			}
		}
	}

}