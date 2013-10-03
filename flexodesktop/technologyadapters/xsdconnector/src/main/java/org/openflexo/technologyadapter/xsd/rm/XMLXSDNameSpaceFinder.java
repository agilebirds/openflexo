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

package org.openflexo.technologyadapter.xsd.rm;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.logging.Logger;

import org.openflexo.foundation.technologyadapter.FlexoMetaModel;
import org.openflexo.foundation.technologyadapter.FlexoModel;
import org.xml.sax.Attributes;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLReader;
import org.xml.sax.ext.DefaultHandler2;
import org.xml.sax.helpers.XMLReaderFactory;

/**
 * This is used to detect which XSD the given XML Model/File is bounded to
 * 
 * @author xtof
 *
 * @param <M>, the Model to populate
 * @param <MM>, The MetaModel that will be populated (if createMissingType is true) or that is given (if XML is XSD conformant)
 */

public final class XMLXSDNameSpaceFinder  {


	protected static final Logger logger = Logger.getLogger(XMLXSDNameSpaceFinder.class.getPackage().getName());


	private XMLReader saxParser =null;
	private MetamodelFinderSAXHandler saxHandler =null;
	private MetamodelFinderSAXErrorHandler errorHandler =null;

	private static String currentSchemaURI = null;
	private static boolean isSchema = false;


	private static XMLXSDNameSpaceFinder instance = new XMLXSDNameSpaceFinder();

	private  XMLXSDNameSpaceFinder() {
		super();

		try {


			saxHandler = new MetamodelFinderSAXHandler();
			errorHandler = new MetamodelFinderSAXErrorHandler();

			saxParser = XMLReaderFactory.createXMLReader();
			saxParser.setContentHandler(saxHandler);
			saxParser.setErrorHandler(errorHandler);

			saxParser.setFeature("http://xml.org/sax/features/namespace-prefixes",true);


		} catch (SAXNotRecognizedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXNotSupportedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * 
	 * @param model
	 * @return
	 */
	public static final String findNameSpace(File aXmlFile, boolean isXmlSchema) {

		instance.isSchema = isXmlSchema;

		try {
			currentSchemaURI = null;
			instance.saxParser.parse(new InputSource(new FileInputStream(aXmlFile)));
		} catch (SAXException e) {
			return currentSchemaURI;
		} catch (IOException e) {
			return null;
		}


		return null;

	}



	/** 
	 * 
	 * Handler used only to abort parsing
	 * 
	 * @author xtof
	 *
	 */

	final class MetamodelFinderSAXErrorHandler implements ErrorHandler {

		@Override
		public void warning(SAXParseException exception) throws SAXException {
			// TODO Auto-generated method stub

		}

		@Override
		public void error(SAXParseException exception) throws SAXException {
			// TODO Auto-generated method stub

		}

		@Override
		public void fatalError(SAXParseException exception) throws SAXException {
			// TODO Auto-generated method stub

		}

	}

	// Only parses the first element, then aborts

	final class MetamodelFinderSAXHandler extends DefaultHandler2 {



		public void startElement (String uri, String localName,String qName, 
				Attributes attributes) throws SAXException {


			int len = attributes.getLength();
			if (instance.isSchema) { 
				currentSchemaURI = attributes.getValue("targetNamespace");
			}
			else{			
				currentSchemaURI = uri;
			}

			throw new SAXException();
		}
	}

}