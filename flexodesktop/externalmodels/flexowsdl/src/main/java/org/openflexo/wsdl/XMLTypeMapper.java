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
import java.util.Hashtable;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.namespace.QName;

import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.XmlOptions;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

public class XMLTypeMapper {

	private static final Logger logger = Logger.getLogger(XMLTypeMapper.class.getPackage().getName());

	/**
	 * 
	 * @param type
	 * @param extractor
	 * @return a 2 String array composed of 1. java name of the type. 2 the cardinality of the type. (SINGLE, VECTOR)
	 */
	public static String[] getFullJavaNameForType(SchemaType type, SchemaTypeExtractor extractor, Hashtable simpleTypes) {

		// Test an Array
		if (type == null) {
			if (logger.isLoggable(Level.WARNING))
				logger.warning("Type is NULL. Cannot find java Name for a NULL Type");
			return new String[] { null, "SINGLE" };
		}

		// System.out.println("attributeModel:"+ type.getAttributeModel());
		SchemaType baseType = type.getBaseType();
		if (baseType != null && baseType.getName().getLocalPart().equals("Array")
				&& baseType.getName().getNamespaceURI().equals("http://schemas.xmlsoap.org/soap/encoding/")) {
			System.out.println("This is an array");

			String innerType = getArrayType(type, extractor, simpleTypes);
			return new String[] { innerType, "VECTOR" };
		}

		// Handle the simple Types. We are just interested in their base type.
		if (type.isAnonymousType() && type.isSimpleType()) {
			type = type.getBaseType();
		} else if (!type.isAnonymousType() && type.isSimpleType() && !type.isBuiltinType()) {
			// || ! type.isAnonymousType() && ! type.isSimpleType() && type.getContentType()==SchemaType.SIMPLE_CONTENT) {
			type = (SchemaType) simpleTypes.get(type);
		} else if (type.isAnonymousType() && !type.isSimpleType()) {
			// handle the complex types here ??
		} else {

		}

		// try a primitive type
		String typeString = null;

		if (type.isBuiltinType()) {

			typeString = XMLTypeMapper.getJavaTypeForBuiltInType(type.getBuiltinTypeCode());
		} else {
			// ATTENTION
			// get java Type For NOT BUILTIN type
			// TODO....
			// look for the package

			typeString = type.getFullJavaName();
		}

		return new String[] { typeString, "SINGLE" };
	}

	public static String getArrayType(SchemaType type, SchemaTypeExtractor extractor, Hashtable simpleTypes) {
		try {

			// 1. Get Xml fragment of Schemas. Either uses schemas in extractor or get them with XmlObject.
			// With extractor, the wsdl namespace is changed...

			boolean useSchemaFromExtractor = false;
			XmlObject[] schemas = null;

			String wsdlUrl = extractor.getWsdlUrl();
			XmlOptions options = new XmlOptions();
			options.setCompileNoValidation();
			options.setCompileDownloadUrls();
			options.setCompileNoUpaRule();
			options.setSaveUseOpenFrag();
			options.setSaveSyntheticDocumentElement(new QName("http://www.w3.org/2001/XMLSchema", "schema"));
			XmlObject wsdlObject = XmlObject.Factory.parse(new File(wsdlUrl)/* new URL(wsdlUrl) */, options);

			// a. get schema with XMLObject
			if (!useSchemaFromExtractor) {
				schemas = wsdlObject.selectPath("declare namespace s='http://www.w3.org/2001/XMLSchema' .//s:schema");
			}
			// b. get schema with Extractor.
			else {
				List schemaList = extractor.getSchemaList();
				schemas = (XmlObject[]) schemaList.toArray();
			}

			// 2. Find attribute: find the name of the object contained in the array.
			for (int i = 0; i < schemas.length; i++) {

				XmlObject schema = schemas[i];// schemas.get(i);
				System.out.println("anonymous:" + type.isAnonymousType());
				System.out.println("document:" + type.isDocumentType());

				String typeName = null;
				if (type.isDocumentType()) {
					// a DocumentType is associated to an element. But has no QName ! The element has the QName.
					// to get it, take the contentModel of the documentType (element)
					typeName = type.getContentModel().getName().getLocalPart();
				} else if (type.isAnonymousType()) {
					// recup�rer le element Name car non topLevel type with no QNAME
					typeName = type.getOuterType().getContentModel().getName().getLocalPart();
					// typeName = type.getName().getLocalPart();
				} else {
					// type with a QNAME
					typeName = type.getName().getLocalPart();
				}

				XmlObject[] attributes = null;

				// When parsed With WSDL2Java the wsdl namespace http://schemas.xmlsoap.org/wsdl/ is subsituted...
				String wsdlNS = "http://schemas.xmlsoap.org/wsdl/";
				if (useSchemaFromExtractor)
					wsdlNS = "http://www.apache.org/internal/xmlbeans/wsdlsubst";

				// Find Elements of with correct name
				if (type.isAnonymousType() || type.isDocumentType()) {
					attributes = schema.selectPath("declare namespace s='http://www.w3.org/2001/XMLSchema' declare namespace wsdl='"
							+ wsdlNS + "' .//s:element[@name='" + typeName + "']//s:attribute[@wsdl:arrayType]");
				}
				// Or Find ComplexTypes with correct name
				else {
					attributes = schema.selectPath("declare namespace s='http://www.w3.org/2001/XMLSchema' declare namespace wsdl='"
							+ wsdlNS + "' .//s:complexType[@name='" + typeName + "']//s:attribute[@wsdl:arrayType]");

				}

				if (attributes.length > 0) {
					XmlObject attribut = attributes[0];
					NamedNodeMap map = attribut.getDomNode().getAttributes();
					Node s = map.getNamedItemNS(wsdlNS, "arrayType");
					System.out.println("s:" + s.getNodeValue());

					// now we have something like "tns:MyObject[]"
					// we need to separate tns and MyObject. And get the value of tns.

					// for (int j = 0; j < map.getLength(); j++) {
					// Node n = map.item(j);
					// System.out.println("node name:"+n.getNodeName()+" value:"+n.getNodeValue() );
					//
					// }
					String value = s.getNodeValue();
					String tns = value.substring(0, value.indexOf(":"));
					String obj = value.substring(value.indexOf(":") + 1, value.indexOf("["));

					// resovle tns
					// find xmlns:tns1="http://DefaultNamespace" qui est un attribut de wsdl !!!
					// with xmlsn = xmlns="http://schemas.xmlsoap.org/
					XmlObject[] wsdlDef = wsdlObject.selectPath("declare namespace s='http://schemas.xmlsoap.org/wsdl/' .//s:definitions");

					XmlObject def = wsdlDef[0];

					// Try nr O: returns null.
					// XmlObject defXmlns = def.selectAttribute("http://schemas.xmlsoap.org/wsdl/", "xmlns");

					Node defNode = def.getDomNode();
					NamedNodeMap defmap = defNode.getAttributes();
					// for (int j = 0; j < defmap.getLength(); j++) {
					// Node n = defmap.item(j);
					// System.out.println("node name:"+n.getNodeName()+" value:"+n.getNodeValue() );
					//
					// }

					// Try nr 1:
					// Node xmlnsNode = defmap.getNamedItem("xmlns");
					// String xmlNS = xmlnsNode.getNodeValue();
					// Node tnsNode = defmap.getNamedItemNS(xmlNS,tns); returns null...

					// Try nr 2:
					String nodeName = "xmlns:" + tns;
					Node tnsNode = defmap.getNamedItem(nodeName);
					String tnsNS = tnsNode.getNodeValue();

					// now we have: tnsNS= http://something/something

					// find the correct Type:
					SchemaType objectOfTableType = extractor.schemaTypeLoader().findType(new QName(tnsNS, obj));
					String[] javaType = getFullJavaNameForType(objectOfTableType, extractor, simpleTypes);
					String toReturn = javaType[0];
					return toReturn;
				}

			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return "java.lang.Object";
	}

	// see: http://xmlbeans.apache.org/docs/2.0.0/guide/conXMLBeansSupportBuiltInSchemaTypes.html
	public static String getJavaTypeForBuiltInType(int typeCode) {
		switch (typeCode) {
		case SchemaType.BTC_ANY_TYPE:
			return "java.lang.Object";
		case SchemaType.BTC_ANY_SIMPLE:
			return "java.lang.String";
		case SchemaType.BTC_BOOLEAN:
			return "boolean";
		case SchemaType.BTC_BASE_64_BINARY:
			return "byte[]";
		case SchemaType.BTC_HEX_BINARY:
			return "byte[]";
		case SchemaType.BTC_ANY_URI:
			return "java.lang.String";// "anyURI", "org.apache.xmlbeans.XmlAnyURI");
		case SchemaType.BTC_QNAME:
			return "javax.xml.namespace.QName";// "QName", "org.apache.xmlbeans.XmlQName");
		case SchemaType.BTC_NOTATION:
			return "java.lang.Object";// "NOTATION", "org.apache.xmlbeans.XmlNOTATION");
		case SchemaType.BTC_FLOAT:
			return "float";// "float", "org.apache.xmlbeans.XmlFloat");
		case SchemaType.BTC_DOUBLE:
			return "double";// "double", "org.apache.xmlbeans.XmlDouble");
			// case SchemaType.BTC_DECIMAL:
			// return "java.math.BigDecimal";// , "decimal", "org.apache.xmlbeans.XmlDecimal");
		case SchemaType.BTC_DECIMAL:
			return "long";
		case SchemaType.BTC_STRING:
			return "java.lang.String";// "string", "org.apache.xmlbeans.XmlString");

		case SchemaType.BTC_DURATION:
			return "java.lang.String";// "org.apache.xmlbeans.XmlDuration";// , "duration", "org.apache.xmlbeans.XmlDuration");
		case SchemaType.BTC_DATE_TIME:
			return "java.util.Calendar";// "dateTime", "org.apache.xmlbeans.XmlDateTime");
		case SchemaType.BTC_TIME:
			return "java.util.Calendar";// "time", "org.apache.xmlbeans.XmlTime");
		case SchemaType.BTC_DATE:
			return "java.util.Calendar";// "date", "org.apache.xmlbeans.XmlDate");
		case SchemaType.BTC_G_YEAR_MONTH:
			return "java.util.Calendar";// "gYearMonth", "org.apache.xmlbeans.XmlGYearMonth");
		case SchemaType.BTC_G_YEAR:
			return "java.util.Calendar";// "gYear", "org.apache.xmlbeans.XmlGYear");
		case SchemaType.BTC_G_MONTH_DAY:
			return "java.util.Calendar";// "gMonthDay", "org.apache.xmlbeans.XmlGMonthDay");
		case SchemaType.BTC_G_DAY:
			return "java.util.Calendar";// "gDay", "org.apache.xmlbeans.XmlGDay");
		case SchemaType.BTC_G_MONTH:
			return "java.util.Calendar";// "gMonth", "org.apache.xmlbeans.XmlGMonth");

			// derived numerics
		case SchemaType.BTC_INTEGER:
			return "java.lang.Integer";// "integer", "org.apache.xmlbeans.XmlInteger");
		case SchemaType.BTC_LONG:
			return "long";// "long", "org.apache.xmlbeans.XmlLong");
		case SchemaType.BTC_INT:
			return "int";// "int", "org.apache.xmlbeans.XmlInt");
		case SchemaType.BTC_SHORT:
			return "short";// , "short", "org.apache.xmlbeans.XmlShort");
		case SchemaType.BTC_BYTE:
			return "byte";// , "byte", "org.apache.xmlbeans.XmlByte");
		case SchemaType.BTC_NON_POSITIVE_INTEGER:
			return "java.lang.Integer";// "nonPositiveInteger", "org.apache.xmlbeans.XmlNonPositiveInteger");
		case SchemaType.BTC_NEGATIVE_INTEGER:
			return "java.lang.Integer";// "negativeInteger", "org.apache.xmlbeans.XmlNegativeInteger");
		case SchemaType.BTC_NON_NEGATIVE_INTEGER:
			return "java.lang.Integer";// "nonNegativeInteger", "org.apache.xmlbeans.XmlNonNegativeInteger");
		case SchemaType.BTC_POSITIVE_INTEGER:
			return "java.lang.Integer";// "positiveInteger", "org.apache.xmlbeans.XmlPositiveInteger");
		case SchemaType.BTC_UNSIGNED_LONG:
			return "long";// ????? "unsignedLong", "org.apache.xmlbeans.XmlUnsignedLong");
		case SchemaType.BTC_UNSIGNED_INT:
			return "int";// , "unsignedInt", "org.apache.xmlbeans.XmlUnsignedInt");
		case SchemaType.BTC_UNSIGNED_SHORT:
			return "short";// "unsignedShort", "org.apache.xmlbeans.XmlUnsignedShort");
		case SchemaType.BTC_UNSIGNED_BYTE:
			return "byte";// "unsignedByte", "org.apache.xmlbeans.XmlUnsignedByte");

			// derived strings
		case SchemaType.BTC_NORMALIZED_STRING:
			return "java.lang.String";// "normalizedString", "org.apache.xmlbeans.XmlNormalizedString");
		case SchemaType.BTC_TOKEN:
			return "java.lang.String";// , "token", "org.apache.xmlbeans.XmlToken");
		case SchemaType.BTC_NAME:
			return "java.lang.String";// , "Name", "org.apache.xmlbeans.XmlName");
		case SchemaType.BTC_NCNAME:
			return "java.lang.String";// , "NCName", "org.apache.xmlbeans.XmlNCName");
		case SchemaType.BTC_LANGUAGE:
			return "java.lang.String";// , "language", "org.apache.xmlbeans.XmlLanguage");
		case SchemaType.BTC_ID:
			return "java.lang.String";// , "ID", "org.apache.xmlbeans.XmlID");
		case SchemaType.BTC_IDREF:
			return "java.lang.String";// , "IDREF", "org.apache.xmlbeans.XmlIDREF");
		case SchemaType.BTC_IDREFS:
			return "java.lang.String";// , "IDREFS", "org.apache.xmlbeans.XmlIDREFS");
		case SchemaType.BTC_ENTITY:
			return "java.lang.String";// , "ENTITY", "org.apache.xmlbeans.XmlENTITY");
		case SchemaType.BTC_ENTITIES:
			return "java.lang.String";// , "ENTITIES", "org.apache.xmlbeans.XmlENTITIES");
		case SchemaType.BTC_NMTOKEN:
			return "java.lang.String";// , "NMTOKEN", "org.apache.xmlbeans.XmlNMTOKEN");
		case SchemaType.BTC_NMTOKENS:
			return "java.lang.String";// , "NMTOKENS", "org.apache.xmlbeans.XmlNMTOKENS");

			// the no-type
		case SchemaType.BTC_NOT_BUILTIN:
			return null;// , null, null);
		default:
			return null;
		}
	}

	/*
	 * Built-In Schema Type XMLBean Type Natural Java Type xs:anyType XmlObject org.apache.xmlbeans.XmlObject
	 * xs:anySimpleType XmlAnySimpleType String xs:anyURI XmlAnyURI String xs:base64Binary XmlBase64Binary byte[]
	 * xs:boolean XmlBoolean boolean xs:byte XmlByte byte xs:date XmlDate java.util.Calendar xs:dateTime XmlDateTime
	 * java.util.Calendar xs:decimal XmlDecimal java.math.BigDecimal xs:double XmlDouble double xs:duration XmlDuration
	 * org.apache.xmlbeans.GDuration xs:ENTITIES XmlENTITIES String xs:ENTITY XmlENTITY String xs:float XmlFloat float
	 * xs:gDay XmlGDay java.util.Calendar xs:gMonth XmlGMonth java.util.Calendar xs:gMonthDay XmlGMonthDay
	 * java.util.Calendar xs:gYear XmlGYear java.util.Calendar xs:gYearMonth XmlGYearMonth java.util.Calendar
	 * xs:hexBinary XmlHexBinary byte[] xs:ID XmlID String xs:IDREF XmlIDREF String xs:IDREFS XmlIDREFS String xs:int
	 * XmlInt int xs:integer XmlInteger java.math.BigInteger xs:language XmlLanguage String xs:long XmlLong long xs:Name
	 * XmlName String xs:NCName XmlNCNAME String xs:negativeInteger XmlNegativeInteger java.math.BigInteger xs:NMTOKEN
	 * XmlNMTOKEN String xs:NMTOKENS XmlNMTOKENS String xs:nonNegativeInteger XmlNonNegativeInteger java.math.BigInteger
	 * xs:nonPositiveInteger XmlNonPositiveInteger java.math.BigInteger xs:normalizedString XmlNormalizedString String
	 * xs:NOTATION XmlNOTATION Not supported xs:positiveInteger XmlPositiveInteger java.math.BigInteger xs:QName
	 * XmlQName javax.xml.namespace.QName xs:short XmlShort short xs:string XmlString String xs:time XmlTime
	 * java.util.Calendar xs:token XmlToken String xs:unsignedByte XmlUnsignedByte short xs:unsignedInt XmlUnsignedInt
	 * long xs:unsignedLong XmlUnsignedLong java.math.BigInteger xs:unsignedShort XmlUnsignedShort int
	 */
}
