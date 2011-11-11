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

import java.io.StringWriter;
import java.util.Iterator;
import java.util.Vector;

import javax.wsdl.Binding;
import javax.wsdl.BindingInput;
import javax.wsdl.BindingOperation;
import javax.wsdl.BindingOutput;
import javax.wsdl.Definition;
import javax.wsdl.Input;
import javax.wsdl.Message;
import javax.wsdl.Operation;
import javax.wsdl.Output;
import javax.wsdl.Part;
import javax.wsdl.Port;
import javax.wsdl.PortType;
import javax.wsdl.Service;
import javax.wsdl.Types;
import javax.wsdl.WSDLException;
import javax.wsdl.extensions.ExtensionRegistry;
import javax.wsdl.extensions.UnknownExtensibilityElement;
import javax.wsdl.extensions.soap.SOAPAddress;
import javax.wsdl.extensions.soap.SOAPBinding;
import javax.wsdl.extensions.soap.SOAPBody;
import javax.wsdl.factory.WSDLFactory;
import javax.wsdl.xml.WSDLWriter;
import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.openflexo.foundation.FlexoException;
import org.openflexo.foundation.bpel.BPELConstants;
import org.openflexo.foundation.bpel.BPELExportedPartnerLink;
import org.openflexo.foundation.bpel.BPELWriter;
import org.openflexo.foundation.dm.DMEntity;
import org.openflexo.foundation.dm.DMProperty;
import org.openflexo.foundation.wkf.ws.AbstractInPort;
import org.openflexo.foundation.wkf.ws.MessageEntry;
import org.openflexo.foundation.wkf.ws.OutputPort;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.ibm.wsdl.BindingImpl;
import com.ibm.wsdl.BindingInputImpl;
import com.ibm.wsdl.BindingOperationImpl;
import com.ibm.wsdl.BindingOutputImpl;
import com.ibm.wsdl.DefinitionImpl;
import com.ibm.wsdl.InputImpl;
import com.ibm.wsdl.OperationImpl;
import com.ibm.wsdl.OutputImpl;
import com.ibm.wsdl.PartImpl;
import com.ibm.wsdl.PortImpl;
import com.ibm.wsdl.PortTypeImpl;
import com.ibm.wsdl.ServiceImpl;
import com.ibm.wsdl.TypesImpl;
import com.ibm.wsdl.extensions.PopulatedExtensionRegistry;
import com.ibm.wsdl.extensions.schema.SchemaImpl;
import com.ibm.wsdl.extensions.soap.SOAPAddressImpl;
import com.ibm.wsdl.extensions.soap.SOAPBindingImpl;
import com.ibm.wsdl.extensions.soap.SOAPBodyImpl;

public class Exporter {

	private static final String NAMESPACE_SCHEMA = "http://www.w3.org/2001/XMLSchema";
	Definition wsd;
	NamespacePrefixMapper namespacePrefixMapper = new NamespacePrefixMapper();

	public Exporter() {
		wsd = new DefinitionImpl();
		ExtensionRegistry myReg = new PopulatedExtensionRegistry();
		/*
		QName schemaQN=new QName(NAMESPACE_SCHEMA,"schema");
		myReg.registerDeserializer(Types.class,schemaQN,new SchemaDeserializer());
		myReg.registerSerializer(Types.class, schemaQN, new SchemaSerializer());
		myReg.mapExtensionTypes(Types.class, schemaQN, Schema.class);
		*/
		wsd.setExtensionRegistry(myReg);
	}

	public void addNamespace(String prefix, String namespace) {
		if (wsd.getNamespaces().containsValue(namespace)) {
			// do nothing
		} else {
			wsd.addNamespace(prefix, namespace);
		}
	}

	/*
	 * Reminder : only one operation is currently supported per service
	 */

	public String export(BPELWriter writer, BPELExportedPartnerLink pro) throws Exception {
		if (pro == null)
			return null;
		try {

			WSDLFactory wsdlFactory = WSDLFactory.newInstance();
			WSDLWriter wsdlWriter = wsdlFactory.newWSDLWriter();

			int nsIndex = 0;
			wsd.setTargetNamespace(pro.getTargetNamespace());
			wsd.addNamespace("xs", NAMESPACE_SCHEMA);
			wsd.addNamespace("tns", pro.getTargetNamespace());
			wsd.addNamespace("soap", "http://schemas.xmlsoap.org/wsdl/soap/");

			namespacePrefixMapper.registerPrefixForNamespace(NAMESPACE_SCHEMA, "xs");
			namespacePrefixMapper.registerPrefixForNamespace(pro.getTargetNamespace(), "tns");

			/* Messages */
			// let's generate Messages before than type, so that from the messages, we can know which type we need to define.

			Vector<DMEntity> typesToBeDefined = new Vector<DMEntity>();

			AbstractInPort portIN = pro.getPortIN();
			Message mesIN = wsd.createMessage();
			mesIN.setUndefined(false);
			mesIN.setQName(pro.getMessageINType());
			wsd.addMessage(mesIN);

			Vector<MessageEntry> entriesIN = portIN.getInputMessageDefinition().getEntries();
			for (int i = 0; i < entriesIN.size(); i++) {
				MessageEntry ent = entriesIN.get(i);
				Part p = new PartImpl();
				p.setName(ent.getVariableName());
				mesIN.addPart(p);

				if (ent.getType().getBaseEntity() != null && ent.getType().getBaseEntity().getProperties().size() != 0) {
					typesToBeDefined.add(ent.getType().getBaseEntity());
					p.setElementName(new QName(ent.getType().getBaseEntity().getPackage().getName(), ent.getType().getName()));
					addNamespace("ns" + nsIndex++, ent.getType().getBaseEntity().getPackage().getName());
				} else {
					p.setTypeName(new QName(NAMESPACE_SCHEMA, "string"));
				}

			}

			OutputPort portOUT = pro.getPortOUT();
			Message mesOUT = wsd.createMessage();
			mesOUT.setUndefined(false);
			mesOUT.setQName(pro.getMessageOUTType());
			wsd.addMessage(mesOUT);

			Vector<MessageEntry> entriesOUT = portOUT.getOutputMessageDefinition().getEntries();
			for (int i = 0; i < entriesOUT.size(); i++) {
				MessageEntry ent = entriesOUT.get(i);
				Part p = new PartImpl();
				p.setName(ent.getVariableName());

				mesOUT.addPart(p);

				if (ent.getType().getBaseEntity() != null && ent.getType().getBaseEntity().getProperties().size() != 0) {
					typesToBeDefined.add(ent.getType().getBaseEntity());
					p.setElementName(new QName(ent.getType().getBaseEntity().getPackage().getName(), ent.getType().getName()));
					addNamespace("ns" + nsIndex++, ent.getType().getBaseEntity().getPackage().getName());
				} else {
					p.setElementName(new QName(NAMESPACE_SCHEMA, "string"));
				}

			}

			/* Types */

			DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			Document doc = builder.newDocument();

			Types types = new TypesImpl();

			SchemaStructure ss = new SchemaStructure();

			// this loop starts from the types to be defined by the message,
			// and recursively builds a strucure "SchemaStructure" containing all the
			// types definition, gathered by namespace.
			for (DMEntity currentEntity : typesToBeDefined) {
				getComplexTypeForDMEntity(doc, currentEntity, ss);

				Element instanciableElement = doc.createElement("element");
				instanciableElement.setAttribute("name", currentEntity.getName());
				instanciableElement.setAttribute("type", namespacePrefixMapper.getPrefixForNamespace(currentEntity.getPackage().getName())
						+ ":" + currentEntity.getName());

				ss.addElementInNamespace(currentEntity.getPackage().getName(), instanciableElement);
			}

			// for every different target namespace, the schema element is added in the wsdl definition.
			for (Element currentEl : ss.getSchemaDefinition(doc)) {
				SchemaImpl currentSchema = new SchemaImpl();
				currentSchema.setElement(currentEl);
				currentSchema.setElementType(new QName(NAMESPACE_SCHEMA, "schema"));
				// currentEl.setPrefix("xsd");
				types.addExtensibilityElement(currentSchema);
			}

			wsd.setTypes(types);

			/* Port types */
			PortType pt = new PortTypeImpl();
			pt.setQName(pro.getPortType());
			pt.setUndefined(false);
			wsd.addPortType(pt);

			// a port type defines a set of operations (one for now)
			Operation op = new OperationImpl();
			op.setUndefined(false);
			pt.addOperation(op);
			op.setName(pro.getOperationName());

			Input input = new InputImpl();
			op.setInput(input);
			input.setMessage(mesIN);
			input.setName(pro.getOperationName() + "_IN");

			Output output = new OutputImpl();
			op.setOutput(output);
			output.setMessage(mesOUT);
			output.setName(pro.getOperationName() + "_OUT");

			/* Binding */
			// let's generate a SOAP binding over HTTP
			Binding b = new BindingImpl();
			b.setQName(new QName(pro.getTargetNamespace(), normalise(pro.getProcess().getName()) + "Binding"));
			b.setUndefined(false);
			b.setPortType(pt);
			wsd.addBinding(b);

			SOAPBinding sb = new SOAPBindingImpl();
			sb.setStyle("document");
			sb.setTransportURI("http://schemas.xmlsoap.org/soap/http");
			b.addExtensibilityElement(sb);

			BindingOperation bo = new BindingOperationImpl();
			bo.setName(pro.getOperationName());
			b.addBindingOperation(bo);

			BindingInput bIN = new BindingInputImpl();
			bo.setBindingInput(bIN);
			bIN.setName(input.getName());
			SOAPBody sBody = new SOAPBodyImpl();
			sBody.setUse("literal");
			sBody.setNamespaceURI(pro.getTargetNamespace());
			bIN.addExtensibilityElement(sBody);

			BindingOutput bOUT = new BindingOutputImpl();
			bo.setBindingOutput(bOUT);
			bOUT.setName(output.getName());
			bOUT.addExtensibilityElement(sBody);

			/* Service */
			// let's make the service available on ODE deployement server

			Service ser = new ServiceImpl();
			wsd.addService(ser);
			ser.setQName(new QName(pro.getTargetNamespace(), normalise(pro.getProcess().getName())));
			Port port = new PortImpl();
			ser.addPort(port);

			port.setName(pro.getProcess().getName() + "Port");
			port.setBinding(b);

			SOAPAddress address = new SOAPAddressImpl();
			address.setLocationURI("http://localhost:4092/" + normalise(pro.getProcess().getName()));
			port.addExtensibilityElement(address);

			/* Partner Links */
			UnknownExtensibilityElement pLinkExtension = new UnknownExtensibilityElement();
			Element n = (Element) writer.getPartnerLinkTypeDefinition(writer.getExportedPartnerLink());

			pLinkExtension.setElement(n);
			wsd.addExtensibilityElement(pLinkExtension);

			/* Serialisation */
			// wsdlWriter.writeWSDL(wsd, out);

			Document docOutput = wsdlWriter.getDocument(wsd);

			Transformer transfo = TransformerFactory.newInstance().newTransformer();

			transfo.setOutputProperty(OutputKeys.METHOD, "xml");
			transfo.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
			transfo.setOutputProperty(OutputKeys.ENCODING, "utf-8");
			transfo.setOutputProperty(OutputKeys.INDENT, "yes");

			Source source = new DOMSource(docOutput);

			String stringResult = new String();
			StringWriter sw = new StringWriter();

			Result result = new StreamResult(sw);
			transfo.transform(source, result);
			stringResult = sw.toString();

			return stringResult;
		} catch (WSDLException e) {
			System.out.println("WSDL exception occured");
			e.printStackTrace();
			throw new FlexoException(e.getMessage());
		} catch (Exception e) {
			System.out.println("Unexpected Exception occured");
			e.printStackTrace();
			throw e;
		}
	}

	/*
	public Vector<Element> getAllTypesForDMEntity(Document doc, DMEntity ent,SchemaStructure ss) {
		SchemaStructure toReturn=new SchemaStructure();
		getComplexTypeForDMEntity(doc,ent,toReturn);
		return toReturn.getSchemaDefinition(doc);
		
	}
	*/

	private Element getComplexTypeForDMEntity(Document doc, DMEntity ent, SchemaStructure acc) {
		Element complexType = doc.createElement("complexType");
		complexType.setAttribute("name", ent.getName());
		Element sequence = doc.createElement("sequence");
		complexType.appendChild(sequence);
		namespacePrefixMapper.registerNamespace(ent.getPackage().getName());
		acc.addElementInNamespace(ent.getPackage().getName(), complexType);

		Iterator it = ent.getProperties().values().iterator();
		while (it.hasNext()) {
			DMProperty currentProp = (DMProperty) it.next();
			Element currentPropEl = doc.createElement("element");
			if (!currentProp.getType().isPrimitive() && currentProp.getType().getBaseEntity() != null
					&& currentProp.getType().getBaseEntity().getProperties().size() != 0) {
				getComplexTypeForDMEntity(doc, currentProp.getType().getBaseEntity(), acc);
				acc.addNamespaceDependency(ent.getPackage().getName(), currentProp.getType().getBaseEntity().getPackage().getName());
			}
			currentPropEl.setAttribute("name", currentProp.getName());
			currentPropEl.setAttribute("type", getXsdTypeForProperty(currentProp));
			sequence.appendChild(currentPropEl);
		}

		for (String ns : acc.getNamespaceDependency(ent.getPackage().getName())) {
			complexType.setAttribute("xmlns:" + namespacePrefixMapper.getPrefixForNamespace(ns), ns);
			Element imp = doc.createElement("import");
			complexType.insertBefore(imp, complexType.getFirstChild());
			imp.setAttribute("namespace", ns);
		}
		return complexType;
	}

	private String getXsdTypeForProperty(DMProperty prop) {
		if (!prop.getType().isPrimitive() && prop.getType().getBaseEntity() != null
				&& prop.getType().getBaseEntity().getProperties().size() != 0) {
			return namespacePrefixMapper.getPrefixForNamespace(prop.getType().getBaseEntity().getPackage().getName()) + ":"
					+ prop.getType().getBaseEntity().getName();
		} else {
			return namespacePrefixMapper.getPrefixForNamespace(NAMESPACE_SCHEMA) + ":string";
		}
	}

	public String normalise(String s) {
		return BPELConstants.normalise(s);
	}

}
