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
import java.util.List;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import org.openflexo.foundation.FlexoException;
import org.openflexo.foundation.resource.ResourceData;
import org.openflexo.foundation.resource.ResourceLoadingCancelledException;
import org.openflexo.foundation.technologyadapter.TechnologyAdapterResource;
import org.openflexo.technologyadapter.xml.model.IXMLAttribute;
import org.openflexo.technologyadapter.xml.model.IXMLIndividual;
import org.openflexo.technologyadapter.xml.model.IXMLModel;

/**
 * This SaxHandler is used to serialize any XML file, either conformant or not to an XSD file The behavior of the Handler depends on the
 * situation (existing XSD).
 * 
 * @author xtof
 * 
 */

public class XMLWriter<R extends TechnologyAdapterResource<RD, ?>, RD extends ResourceData<RD>> {

	private R taRes = null;
	private OutputStreamWriter outputStr = null;
	private XMLStreamWriter myWriter = null;
	private String NSURI = null;

	private static String LINE_SEP = "\n";
	private static String DEFAULT_NS = "ns1";

	private static XMLOutputFactory xmlOutputFactory = XMLOutputFactory.newInstance();

	public XMLWriter(R resource, OutputStreamWriter out) throws XMLStreamException, IOException {
		super();
		this.taRes = resource;
		outputStr = out;
	}

	public void writeDocument() throws XMLStreamException, ResourceLoadingCancelledException, FlexoException, IOException {

		String NSPrefix = DEFAULT_NS;

		if (outputStr != null) {
			xmlOutputFactory.setProperty(XMLOutputFactory.IS_REPAIRING_NAMESPACES, false);
			myWriter = xmlOutputFactory.createXMLStreamWriter(outputStr);

			IXMLModel model = ((IXMLModel) taRes.getResourceData(null));
			NSPrefix = model.getNamespacePrefix();
			NSURI = model.getNamespaceURI();

			if (NSURI != null && !NSURI.isEmpty()) {
				if (NSPrefix == null || NSPrefix.isEmpty()) {
					NSPrefix = DEFAULT_NS; // default
				}
				myWriter.setDefaultNamespace(NSURI);
				myWriter.setPrefix(NSPrefix, NSURI);
			}

			if (myWriter != null) {
				myWriter.writeStartDocument("UTF-8", "1.0");
				myWriter.writeCharacters(LINE_SEP);

				IXMLIndividual<?, ?> rootIndiv = ((IXMLModel) taRes.getResourceData(null)).getRoot();

				if (rootIndiv != null) {
					writeRootElement(rootIndiv, NSURI, NSPrefix);
					myWriter.writeCharacters(LINE_SEP);
				}

				myWriter.writeEndDocument();
				myWriter.flush();
				myWriter.close();

				myWriter = null;
			}
		}
	}

	private void writeRootElement(IXMLIndividual<?, ?> rootIndiv, String nSURI, String nSPrefix) throws XMLStreamException, IOException,
			ResourceLoadingCancelledException, FlexoException {

		myWriter.writeStartElement(nSURI, rootIndiv.getName());
		myWriter.writeNamespace(nSPrefix, nSURI);
		// Attributes
		writeAttributes(rootIndiv);
		myWriter.writeCharacters(LINE_SEP);

		// children node
		for (Object i : rootIndiv.getChildren()) {
			writeElement(i, ((IXMLIndividual) i).getName());
		}
		// CDATA
		String content = rootIndiv.getContentDATA();
		if (content != null && !content.isEmpty()) {
			myWriter.writeCData(content);
			myWriter.writeCharacters(LINE_SEP);
		}
		// Element End
		myWriter.writeEndElement();
		myWriter.writeCharacters(LINE_SEP);

	}

	private void writeElement(Object o, String name) throws XMLStreamException {
		IXMLIndividual indiv = (IXMLIndividual) o;

		myWriter.writeStartElement(NSURI, name);

		// Attributes
		writeAttributes(indiv);
		// children node
		for (Object i : indiv.getChildren()) {
			writeElement(i, ((IXMLIndividual) i).getName());
		}
		// CDATA
		String content = indiv.getContentDATA();
		if (content != null && !content.isEmpty()) {
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

		// Data Properties
		for (IXMLAttribute a : indiv.getAttributes()) {
			if (a.isSimpleAttribute() && !a.isElement()) {
				value = indiv.getAttributeStringValue(a);
				if (value != null) {
					myWriter.writeAttribute(a.getName(), value);
				}
			}
		}

		for (IXMLAttribute a : indiv.getAttributes()) {
			if (a.isSimpleAttribute() && a.isElement()) {

				List<?> valueList = (List<?>) indiv.getAttributeValue(a.getName());
				if (valueList != null && valueList.size() > 0) {
					myWriter.writeStartElement(a.getName());
					for (Object o : valueList) {
						if (o != null) {
							myWriter.writeCData(o.toString());
						}
					}
					myWriter.writeEndElement();
					myWriter.writeCharacters(LINE_SEP);
				}
			}
		}
		// Object Properties
		for (IXMLAttribute a : indiv.getAttributes()) {

			if (!a.isSimpleAttribute()) {
				List<?> valueList = (List<?>) indiv.getAttributeValue(a.getName());
				if (valueList != null) {
					for (Object o : valueList) {
						this.writeElement(o, a.getName());
					}
				}
			}
		}
	}

}
