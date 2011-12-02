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
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.xml.namespace.QName;

import org.apache.xmlbeans.SchemaTypeLoader;
import org.apache.xmlbeans.SimpleValue;
import org.apache.xmlbeans.XmlBeans;
import org.apache.xmlbeans.XmlCursor;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.XmlOptions;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

//import com.eviware.soapui.SoapUI;

/**
 * XML-Schema related tools
 * 
 * @author Ole.Matzura
 */

public class SchemaUtils {
	// private static final Logger log = Logger.getLogger( SchemaUtils.class );

	public static SchemaTypeLoader loadSchemaTypes(String wsdlUrl) throws Exception {
		// log.info( "Loading schema types from [" + wsdlUrl + "]");
		SchemaTypeLoader schemaTypes = loadSchemaTypes(getSchemas(wsdlUrl));
		return schemaTypes;
	}

	public static SchemaTypeLoader loadSchemaTypes(List schemas) throws Exception {
		XmlOptions options = new XmlOptions();
		options.setCompileNoValidation();
		options.setCompileNoPvrRule();
		options.setCompileDownloadUrls();
		options.setCompileNoUpaRule();

		ArrayList errorList = new ArrayList();
		options.setErrorListener(errorList);

		// schemas.add( XmlObject.Factory.parse( SoapUI.class.getResource("/soapEncoding.xsd") ));

		try {
			return XmlBeans.loadXsd((XmlObject[]) schemas.toArray(new XmlObject[schemas.size()]), options);
		} catch (Exception e) {
			for (int c = 0; c < errorList.size(); c++) {
				System.out.println(errorList.get(c));
			}
			throw e;
		}
	}

	public static List getSchemas(String wsdlUrl) throws Exception {
		System.out.println("loading schema types from " + wsdlUrl);
		XmlOptions options = new XmlOptions();
		options.setCompileNoValidation();
		options.setCompileDownloadUrls();
		options.setCompileNoUpaRule();
		options.setSaveUseOpenFrag();
		options.setSaveSyntheticDocumentElement(new QName("http://www.w3.org/2001/XMLSchema", "schema"));

		XmlObject xmlObject = XmlObject.Factory.parse(new URL(wsdlUrl), options);

		XmlObject[] schemas = xmlObject.selectPath("declare namespace s='http://www.w3.org/2001/XMLSchema' .//s:schema");

		for (int i = 0; i < schemas.length; i++) {
			XmlCursor xmlCursor = schemas[i].newCursor();
			String xmlText = xmlCursor.getObject().xmlText(options);
			schemas[i] = XmlObject.Factory.parse(xmlText, options);

			schemas[i].documentProperties().setSourceName(wsdlUrl);
			if (wsdlUrl.startsWith("file:")) {
				;// fixRelativeFileImports( schemas[i] );
			}
		}

		List result = new ArrayList(Arrays.asList(schemas));

		XmlObject[] imports = xmlObject.selectPath("declare namespace s='http://schemas.xmlsoap.org/wsdl/' .//s:import");
		for (int i = 0; i < imports.length; i++) {
			String location = ((Element) imports[i].newDomNode()).getAttribute("location");
			if (location != null) {
				if (location.indexOf("://") > 0) {
					result.addAll(getSchemas(location));
				} else {
					result.addAll(getSchemas(joinRelativeUrl(wsdlUrl, location)));
				}
			}
		}

		return result;
	}

	public static List getDefinitionUrls(String wsdlUrl) throws Exception {
		List result = new ArrayList();
		result.add(wsdlUrl);

		XmlOptions options = new XmlOptions();
		options.setCompileNoValidation();
		options.setCompileDownloadUrls();
		options.setCompileNoUpaRule();
		options.setSaveUseOpenFrag();
		options.setSaveSyntheticDocumentElement(new QName("http://www.w3.org/2001/XMLSchema", "schema"));

		XmlObject xmlObject = XmlObject.Factory.parse(new URL(wsdlUrl), options);

		XmlObject[] wsdlImports = xmlObject.selectPath("declare namespace s='http://schemas.xmlsoap.org/wsdl/' .//s:import");
		for (int i = 0; i < wsdlImports.length; i++) {
			String location = wsdlImports[i].newDomNode().getAttributes().getNamedItem("location").getNodeValue();
			if (location != null) {
				if (location.indexOf("://") > 0) {
					result.addAll(getDefinitionUrls(location));
				} else {
					result.addAll(getDefinitionUrls(joinRelativeUrl(wsdlUrl, location)));
				}
			}
		}

		XmlObject[] schemaImports = xmlObject
				.selectPath("declare namespace s='http://www.w3.org/2001/XMLSchema' .//s:import/@schemaLocation");
		for (int i = 0; i < schemaImports.length; i++) {
			String location = ((SimpleValue) schemaImports[i]).getStringValue();
			if (location != null) {
				if (location.indexOf("://") > 0) {
					result.addAll(getDefinitionUrls(location));
				} else {
					result.addAll(getDefinitionUrls(joinRelativeUrl(wsdlUrl, location)));
				}
			}
		}

		XmlObject[] schemaIncludes = xmlObject
				.selectPath("declare namespace s='http://www.w3.org/2001/XMLSchema' .//s:include/@schemaLocation");
		for (int i = 0; i < schemaIncludes.length; i++) {
			String location = ((SimpleValue) schemaIncludes[i]).getStringValue();
			if (location != null) {
				if (location.indexOf("://") > 0) {
					result.addAll(getDefinitionUrls(location));
				} else {
					result.addAll(getDefinitionUrls(joinRelativeUrl(wsdlUrl, location)));
				}
			}
		}

		return result;
	}

	public static String joinRelativeUrl(String root, String url) {
		int ix = root.startsWith("file:") ? root.lastIndexOf(File.separatorChar) : root.lastIndexOf('/');
		return root.substring(0, ix + 1) + url;
	}

	/**
	 * Fixes relative xsd imports in the specified schema. The schemaLocation of an xsd import must be an URI which is not the case if the
	 * loaded wsdl is loaded with the "file" schema (ie from the file system) and an import is relative.
	 */

	private static void fixRelativeFileImports(XmlObject xmlObject) throws Exception {
		XmlObject[] imports = xmlObject.selectPath("declare namespace s='http://www.w3.org/2001/XMLSchema' .//s:import");

		if (imports.length == 0) {
			return;
		}

		String source = xmlObject.documentProperties().getSourceName();
		int ix = source.lastIndexOf(File.separatorChar);
		if (ix != -1) {
			source = source.substring(0, ix + 1);
		}

		for (int c = 0; c < imports.length; c++) {
			XmlObject importElement = imports[c];
			System.out.println("XML IMPORT:" + importElement);
			Node node = importElement.newDomNode();
			System.out.println("node " + node);
			NamedNodeMap map = node.getAttributes();
			System.out.println("nodemap " + map);

			Node locationNode = map.getNamedItem("schemaLocation");
			System.out.println("locationNode: " + locationNode);
			// Node locationNode = imports[c].newDomNode().getAttributes().getNamedItem( "schemaLocation" );
			if (locationNode != null) {
				String location = locationNode.getNodeValue();
				if (location != null) {
					URI uri = new URI(location);
					if (!uri.isAbsolute()) {
						locationNode.setNodeValue(source + location);
					}
				}
			}
		}
	}
}