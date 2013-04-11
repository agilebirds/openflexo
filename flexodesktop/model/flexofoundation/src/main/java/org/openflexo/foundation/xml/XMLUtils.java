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
package org.openflexo.foundation.xml;

/*
 * XMLUtils.java
 * Project WorkflowEditor
 *
 * Created by benoit on Mar 1, 2004
 */

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.xerces.parsers.DOMParser;
import org.jdom2.input.DOMBuilder;
import org.jdom2.output.Format;
import org.jdom2.output.LineSeparator;
import org.jdom2.output.XMLOutputter;
import org.openflexo.foundation.rm.FlexoProject;
import org.openflexo.foundation.wkf.node.AbstractNode;
import org.openflexo.xmlcode.XMLDecoder;
import org.openflexo.xmlcode.XMLMapping;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Some static util methods for xml parsing.
 * 
 * @author benoit, sylvain
 */
public class XMLUtils {

	private static final Logger logger = Logger.getLogger(XMLUtils.class.getPackage().getName());

	// ==========================================================================
	// ============================= JDOM ================================
	// ==========================================================================

	public static org.jdom2.Document getJDOMDocument(File f) throws Exception {
		Document domDoc = parseXMLFile(f);
		return convertDOMToJDOM(domDoc);
	}

	public static org.jdom2.Document getJDOMDocument(InputStream is) throws Exception {
		Document domDoc = parseXML(is);
		return convertDOMToJDOM(domDoc);
	}

	public static org.jdom2.Document convertDOMToJDOM(Document domDoc) {
		DOMBuilder jDomBuilder = new DOMBuilder();
		org.jdom2.Document jdomDoc = jDomBuilder.build(domDoc);
		return jdomDoc;
	}

	public static boolean saveXMLFile(Document document, File aFile) {
		return saveXMLFile(convertDOMToJDOM(document), aFile);
	}

	public static boolean saveXMLFile(org.jdom2.Document document, File aFile) {
		try {
			return saveXMLFile(document, new FileOutputStream(aFile));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return false;
	}

	public static boolean saveXMLFile(org.jdom2.Document document, OutputStream os) {
		try {
			Format prettyFormat = Format.getPrettyFormat();
			prettyFormat.setLineSeparator(LineSeparator.SYSTEM);
			XMLOutputter outputter = new XMLOutputter(prettyFormat);
			outputter.output(document, os);
			return true;
		} catch (Exception e) {
			// Warns about the exception
			if (logger.isLoggable(Level.WARNING)) {
				logger.warning("Exception raised: " + e.getClass().getName() + ". See console for details.");
			}
			e.printStackTrace();
		} finally {
			if (os != null) {
				try {
					os.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return false;
	}

	// ==========================================================================
	// ======================= DOM > deprecated =============================
	// ==========================================================================

	public static Element elementFromFilePath(String xmlFilePath) throws Exception {
		return parseXMLFile(xmlFilePath).getDocumentElement();
	}

	public static Element elementFromFile(File xmlFile) throws Exception {
		return parseXMLFile(xmlFile).getDocumentElement();
	}

	public static Document parseXMLFile(String xmlFilePath) throws Exception {
		File f = new File(xmlFilePath);
		FileInputStream fis = new FileInputStream(f);
		return parseXML(fis);
	}

	public static Document parseXMLFile(File f) throws Exception {
		FileInputStream fis = new FileInputStream(f);
		return parseXML(fis);
	}

	/**
	 * @param inputStream
	 * @return
	 * @throws Exception
	 * @throws IOException
	 */
	private static Document parseXML(InputStream inputStream) throws Exception, IOException {
		InputStreamReader reader = new InputStreamReader(inputStream, Charset.forName("utf8"));
		org.xml.sax.InputSource is = new org.xml.sax.InputSource(reader);
		Document d = parseXMLFile(is);
		inputStream.close();
		return d;
	}

	private static Document parseXMLFile(org.xml.sax.InputSource is) throws Exception {
		DOMParser parser = new DOMParser();
		parser.parse(is);

		Document d = parser.getDocument();
		parser.reset();
		return d;
	}

	public static Element elementFromString(String s) throws Exception {
		ByteArrayInputStream bis = new ByteArrayInputStream(s.getBytes());
		InputStreamReader reader = new InputStreamReader(bis, Charset.forName("utf8"));
		org.xml.sax.InputSource is = new org.xml.sax.InputSource(reader);
		// org.xml.sax.InputSource is = new org.xml.sax.InputSource(bis);
		Element e = parseXMLFile(is).getDocumentElement();
		bis.close();
		return e;
	}

	public static String replaceAllSpecialChar(String s) {
		s = replaceStringByStringInString("&", "&amp;", s);
		s = replaceStringByStringInString("\"", "&quot;", s);
		s = replaceStringByStringInString(">", "&gt;", s);
		s = replaceStringByStringInString("<", "&lt;", s);
		return s;
	}

	public static String replaceStringByStringInString(String replacedString, String aNewString, String message) {
		// return replaceString(message,replacedString,aNewString);
		// DON'T WORK FOR (" ","",message)

		if (message == null || message.equals("")) {
			return "";
		}
		if (replacedString == null || replacedString.equals("")) {
			return message;
		}
		if (aNewString == null || aNewString.equals("")) {
			aNewString = "";
		}

		String newString = "";
		int replacedStringLength = replacedString.length();
		int indexOfTag = message.indexOf(replacedString);
		while (indexOfTag != -1) {
			newString = newString + message.substring(0, indexOfTag) + aNewString;
			message = message.substring(indexOfTag + replacedStringLength);
			indexOfTag = message.indexOf(replacedString);
		}
		return newString + message;
	}

	public static AbstractNode getNodeFromFile(File xmlFile, FlexoProject project) {
		AbstractNode node;
		try {
			if (logger.isLoggable(Level.FINE)) {
				logger.fine("Trying to load " + xmlFile.getAbsolutePath());
			}
			XMLMapping wkfMapping = project.getXmlMappings().getWKFMapping();
			node = (AbstractNode) XMLDecoder.decodeObjectWithMapping(new FileInputStream(xmlFile), wkfMapping, new FlexoProcessBuilder(
					project), project.getStringEncoder());
			if (logger.isLoggable(Level.FINE)) {
				logger.fine("Succeeded loading palette element: " + xmlFile.getName());
			}
		} catch (Exception e) {
			if (logger.isLoggable(Level.WARNING)) {
				logger.warning("Failed loading palette element: " + xmlFile.getAbsolutePath());
			}
			e.printStackTrace();
			node = null;
		}
		return node;

	}

}
