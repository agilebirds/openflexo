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

import java.lang.reflect.Type;
import java.util.Stack;
import java.util.logging.Logger;

import org.openflexo.foundation.technologyadapter.FlexoMetaModel;
import org.openflexo.foundation.technologyadapter.FlexoModel;
import org.openflexo.foundation.technologyadapter.FlexoModelResource;
import org.openflexo.technologyadapter.xml.model.IXMLAttribute;
import org.openflexo.technologyadapter.xml.model.IXMLIndividual;
import org.openflexo.technologyadapter.xml.model.IXMLMetaModel;
import org.openflexo.technologyadapter.xml.model.IXMLModel;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.ext.DefaultHandler2;

/**
 * This SaxHandler is used to de-serialize any XML file, either conformant or not to an XSD file The behavior of the Handler depends on the
 * situation (existing XSD).
 * 
 * @author xtof
 * 
 * @param <M>, the Model to populate
 * @param <MM>, The MetaModel that will be populated (if createMissingType is true) or that is given (if XML is XSD conformant)
 * @param <IC>, The Class of Individuals (mapping to XML Elements)
 * @param <AC>, The Class of Attributes (mapping to XML Attributes)
 */

public class XMLReaderSAXHandler<M extends FlexoModel<M, MM>, MM extends FlexoMetaModel<MM>, IC, AC extends IXMLAttribute> extends
		DefaultHandler2 {

	protected static final Logger logger = Logger.getLogger(XMLReaderSAXHandler.class.getPackage().getName());

	/**
	 * 
	 */
	private final FlexoModelResource<M, MM, ?> xmlResource;

	private final M aXMLModel;
	private final MM aMetaModel;
	private final boolean createMissingType;

	private IC currentContainer = null;
	private IC currentIndividual = null;
	private AC currentAttribute = null;

	private final StringBuffer cdataBuffer = new StringBuffer();

	private final Stack<IC> indivStack = new Stack<IC>();

	public XMLReaderSAXHandler(FlexoModelResource<M, MM, ?> xmlFileResourceImpl, boolean allowTypeCreation) {
		super();
		xmlResource = xmlFileResourceImpl;
		aXMLModel = xmlResource.getModel();
		aMetaModel = aXMLModel.getMetaModel();
		createMissingType = allowTypeCreation;
	}

	@Override
	@SuppressWarnings("unchecked")
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {

		String NSPrefix = "p"; // default

		currentAttribute = null;

		try {

			// Depending on the choices made when interpreting MetaModel, an XML Element
			// might be translated as a Property or a new Type....
			Type currentType = ((IXMLMetaModel) aMetaModel).getTypeFromURI(uri + "#" + localName);

			// looking for the equally named property in currentContainer
			if (currentContainer != null) {
				currentAttribute = (AC) ((IXMLIndividual<?, ?>) currentContainer).getAttributeByName(localName);
			}

			if (currentAttribute != null) {
				Type attrType = ((IXMLAttribute) currentAttribute).getAttributeType();

				if (!currentAttribute.isSimpleAttribute()) {
					// this is a complex attribute, we will create an individual and then add to the attribute values
					currentType = attrType;
				}
			}

			// creates type on the Fly
			if (currentType == null && createMissingType) {
				logger.info("Creating a new Type");
				currentType = ((IXMLMetaModel) aMetaModel).createNewType(uri, localName, qName);
			}

			// creates individual
			if (currentType != null) {
				currentIndividual = (IC) ((IXMLModel) aXMLModel).addNewIndividual(currentType);
				((IXMLIndividual<IC, AC>) currentIndividual).setType(currentType);
				currentIndividual = currentIndividual;
				cdataBuffer.delete(0, cdataBuffer.length());
			}

			if (currentIndividual != null) {
				// ************************************
				// processing Attributes

				int len = attributes.getLength();

				for (int i = 0; i < len; i++) {

					Type aType = null;
					String typeName = attributes.getType(i);
					String attrQName = attributes.getQName(i);
					String attrName = attributes.getLocalName(i);
					String attrURI = attributes.getURI(i);
					NSPrefix = "p"; // default
					if (attrQName != null && attrName != null && currentContainer == null) { // we only set prefix if there is no other Root
																								// Element
						NSPrefix = attrQName;
						NSPrefix.replace(attrName, "");
					}

					aType = ((IXMLMetaModel) aMetaModel).getTypeFromURI(attrURI + "#" + attrName);

					if (typeName.equals(XMLFileResourceImpl.CDATA_TYPE_NAME)) {
						aType = String.class;

						if (attrName.equals(""))
							attrName = attrQName;

						((IXMLIndividual<?, ?>) currentIndividual).createAttribute(attrName, String.class, attributes.getValue(i));
					} else {
						if (aType == null) {
							XMLFileResourceImpl.logger.warning("XMLIndividual: cannot find a type for " + typeName
									+ " - falling back to String");
							aType = String.class;
						}

						((IXMLIndividual<?, ?>) currentIndividual).createAttribute(attrName, String.class, attributes.getValue(i));
					}

				}

				// ************************************
				// Current element is contained in another one

				if (currentContainer != null && currentContainer != currentIndividual) {

					if (currentAttribute != null) {
						// logger.info("ADDING " + anIndividual.toString() + " TO " + currentContainer.toString() + "   / Attribute is: " +
						// currentAttribute );
						currentAttribute.addValue((IXMLIndividual<IC, AC>) currentContainer, currentIndividual);
					} else {
						((IXMLIndividual<IC, AC>) currentContainer).addChild((IXMLIndividual<IC, AC>) currentIndividual);
					}
				}

				// ************************************
				// Current element is not contained in another one, it is root!

				if (currentContainer == null) {
					((IXMLModel) aXMLModel).setRoot((IXMLIndividual<IC, AC>) currentIndividual);
					if (uri != null && !uri.isEmpty()) {

						((IXMLModel) aXMLModel).setNamespace(uri, NSPrefix);
					}
					// logger.info("OPENING ROOT Container is + " + currentContainer.toString() + "   / Attribute is: " + currentAttribute
					// );

				}

				indivStack.push(currentContainer);
				currentContainer = currentIndividual;

			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	@Override
	@SuppressWarnings("unchecked")
	public void endElement(String uri, String localName, String qName) throws SAXException {

		// Allocation of CDATA information depends on the type of entity we have to allocate
		// content to (Individual or Attribute)
		// As such, it depends on the interpretation that has been done of XSD MetaModel
		//
		// Same stands for individuals to be allocated to ObjectProperties

		// CDATA allocation

		String str = cdataBuffer.toString().trim();

		if (str.length() > 0) {
			if (currentAttribute != null && currentAttribute.isSimpleAttribute()) {
				currentAttribute.addValue((IXMLIndividual<?, ?>) currentIndividual, str);
				cdataBuffer.delete(0, cdataBuffer.length());
			} else if (currentIndividual != null) {
				((IXMLIndividual<?, ?>) currentIndividual).createAttribute(IXMLIndividual.CDATA_ATTR_NAME, String.class, str);
				cdataBuffer.delete(0, cdataBuffer.length());
			} else {
				logger.info("ERROR: don't know where to allocate the CDATA textual information");
			}
		}

		// node stack management

		if (!indivStack.isEmpty()) {
			currentContainer = indivStack.pop();
		} else {
			currentContainer = null;
		}

	}

	@Override
	public void characters(char ch[], int start, int length) throws SAXException {

		if (length > 0)
			cdataBuffer.append(ch, start, length);
	}

}