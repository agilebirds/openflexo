/*
 * (c) Copyright 2010-2012 AgileBirds
 * (c) Copyright 2012-2013 Openflexo
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

package org.openflexo.technologyadapter.xml.rm;

import java.io.IOException;
import java.io.OutputStreamWriter;

import javax.xml.namespace.NamespaceContext;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import org.openflexo.foundation.FlexoException;
import org.openflexo.foundation.resource.ResourceData;
import org.openflexo.foundation.resource.ResourceLoadingCancelledException;
import org.openflexo.foundation.rm.ResourceDependencyLoopException;
import org.openflexo.foundation.technologyadapter.TechnologyAdapterResource;
import org.openflexo.technologyadapter.xml.model.IXMLAttribute;
import org.openflexo.technologyadapter.xml.model.IXMLIndividual;
import org.openflexo.technologyadapter.xml.model.IXMLModel;

/**
 * This SaxHandler is used to serialize any XML file, either conformant or not
 * to an XSD file The behavior of the Handler depends on the situation (existing
 * XSD).
 * 
 * @author xtof
 * 
 */

public class XMLWriter<R extends TechnologyAdapterResource<RD>, RD extends ResourceData<RD>> {

	private R taRes = null;
	private OutputStreamWriter outputStr = null;
	private XMLStreamWriter myWriter = null;

	private static String LINE_SEP = "\n";

	private static XMLOutputFactory xmlOutputFactory = XMLOutputFactory
			.newInstance();

	public XMLWriter(R resource, OutputStreamWriter out)
			throws XMLStreamException, IOException {
		super();
		this.taRes = resource;
		outputStr = out;
	}


	public void writeEmptyElement(String namespaceURI, String localName)
			throws XMLStreamException {
		// TODO Auto-generated method stub

	}

	public void writeEmptyElement(String prefix, String localName,
			String namespaceURI) throws XMLStreamException {
		// TODO Auto-generated method stub

	}

	public void writeEmptyElement(String localName) throws XMLStreamException {
		// TODO Auto-generated method stub

	}

	public void writeEndElement() throws XMLStreamException {
		myWriter.writeEndElement();

	}

	public void writeEndDocument() throws XMLStreamException {
		myWriter.writeEndDocument();

	}

	public void close() throws XMLStreamException {
		myWriter.flush();
		myWriter.close();

	}

	public void writeAttribute(String localName, String value)
			throws XMLStreamException {
		myWriter.writeAttribute(localName, value);

	}

	public void writeAttribute(String prefix, String namespaceURI,
			String localName, String value) throws XMLStreamException {
		// TODO Auto-generated method stub

	}

	public void writeAttribute(String namespaceURI, String localName,
			String value) throws XMLStreamException {
		// TODO Auto-generated method stub

	}

	public void writeNamespace(String prefix, String namespaceURI)
			throws XMLStreamException {
		// TODO Auto-generated method stub

	}

	public void writeDefaultNamespace(String namespaceURI)
			throws XMLStreamException {
		// TODO Auto-generated method stub

	}

	public void writeComment(String data) throws XMLStreamException {
		// TODO Auto-generated method stub

	}

	public void writeProcessingInstruction(String target)
			throws XMLStreamException {
		// TODO Auto-generated method stub

	}

	public void writeProcessingInstruction(String target, String data)
			throws XMLStreamException {
		// TODO Auto-generated method stub

	}


	public void writeEntityRef(String name) throws XMLStreamException {
		// TODO Auto-generated method stub

	}

	public void writeDocument() throws XMLStreamException, ResourceLoadingCancelledException, ResourceDependencyLoopException, FlexoException, IOException {

		if (outputStr != null) {
			myWriter = xmlOutputFactory.createXMLStreamWriter(outputStr);
		}
		if (myWriter != null) {
			myWriter.writeStartDocument();
			myWriter.writeCharacters(LINE_SEP);
		}

		IXMLIndividual<?,?> rootIndiv = ((IXMLModel) taRes.getResourceData(null)).getRoot();

		if (rootIndiv != null ){
			writeRootElement(rootIndiv);
			myWriter.writeCharacters(LINE_SEP);
		}

		if (myWriter != null) {
			myWriter.writeEndDocument();
		}
		myWriter = null;
	}


	private void writeRootElement(IXMLIndividual<?, ?> rootIndiv) throws XMLStreamException, IOException {
		// myWriter.writeStartElement(PREFIX,NAMESPACE, rootIndiv.getName());
		// TODO SetNamespace!!!
		
		writeElement(rootIndiv);

	}


	private void writeElement(Object o) throws XMLStreamException {
		IXMLIndividual indiv = (IXMLIndividual) o;
		// myWriter.writeStartElement(PREFIX,NAMESPACE, rootIndiv.getName());
		myWriter.writeStartElement(indiv.getName());
		// Attributes
		writeAttributes(indiv);
		myWriter.writeCharacters(LINE_SEP);
		// children node 
		for (Object i : indiv.getChildren()){
			writeElement(i);			
		}
		// CDATA
		String content = indiv.getContentDATA();
		if (content != null && !content.isEmpty()){
			myWriter.writeCData(content);
			myWriter.writeCharacters(LINE_SEP);
		}
		// Element End
		myWriter.writeEndElement();		
		myWriter.writeCharacters(LINE_SEP);
	}


	private void writeAttributes(IXMLIndividual<?, ?> indiv) throws XMLStreamException {
		// Simple Attributes First
		String value = null;
		// FIXME : maybe better if we get attributeValues 
		for (IXMLAttribute a : indiv.getAttributes()){
			if (a.isSimpleAttribute()){
				value = (String) indiv.getAttributeStringValue(a);
				if (value != null){
					myWriter.writeAttribute(a.getName(), value);
				}
			}
		}
		// Object Attributes then
		// TODO
	}


	public String getPrefix(String uri) throws XMLStreamException {
		// TODO Auto-generated method stub
		return null;
	}

	public void setPrefix(String prefix, String uri) throws XMLStreamException {
		// TODO Auto-generated method stub

	}

	public void setDefaultNamespace(String uri) throws XMLStreamException {
		// TODO Auto-generated method stub

	}

	public void setNamespaceContext(NamespaceContext context)
			throws XMLStreamException {
		// TODO Auto-generated method stub

	}

	public NamespaceContext getNamespaceContext() {
		// TODO Auto-generated method stub
		return null;
	}

	public Object getProperty(String name) throws IllegalArgumentException {
		// TODO Auto-generated method stub
		return null;
	}

}
