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

package org.openflexo.xmlcode;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringWriter;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Vector;
import java.util.regex.Pattern;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;

import org.jdom2.Attribute;
import org.jdom2.DocType;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.Text;
import org.jdom2.output.Format;
import org.jdom2.output.LineSeparator;
import org.jdom2.output.XMLOutputter;
import org.openflexo.xmlcode.XMLMapId.NoMapIdEntryException;
import org.xml.sax.SAXException;

/**
 * <p>
 * Utility class providing XML coding facilities
 * </p>
 * This class allow you to encode object to an XML string or streams according to a mapping you define externaly (see {@link XMLMapping}).<br>
 * <p>
 * If you want to encode an object <code>anObject</code>, just do:
 * 
 * <pre>
 * XMLMapping myMapping = new XMLMapping(aModelFile);
 * 
 * String result = XMLCoder.encodeObjectWithMapping(anObject, myMapping);
 * </pre>
 * 
 * or
 * 
 * <pre>
 * String result = XMLCoder.encodeObjectWithMappingFile(anObject, aModelFile);
 * </pre>
 * 
 * if you want to work with <code>String</code> objects
 * </p>
 * <p>
 * But you can directly work with <code>OutputStream</code> objects, by doing:
 * 
 * <pre>
 * XMLMapping myMapping = new XMLMapping(exampleModelFile);
 * XMLCoder.encodeObjectWithMapping(myCommand, myMapping, out);
 * </pre>
 * 
 * or
 * 
 * <pre>
 * XMLCoder.encodeObjectWithMappingFile(myCommand, exampleModelFile, out);
 * </pre>
 * 
 * where <code>out</code> is a <code>OutputStream</code> object.
 * </p>
 * 
 * NB: To work properly, <code>XMLCoder</code> may require that you specify TransformerFactory implementation to use (see
 * {@link #setTransformerFactoryClass(String)}), for example by calling
 * <code>XMLCoder.setTransformerFactoryClass("org.apache.xalan.processor.TransformerFactoryImpl");</code> once, if you want to use Apache
 * Xalan transformer, which is provided with this distribution.
 * 
 * @author <a href="mailto:Sylvain.Guerin@enst-bretagne.fr">Sylvain Guerin</a>
 * @see XMLDecoder
 * @see XMLMapping
 */
public class XMLCoder {

	/** Stores mapping that will be used for decoding */
	protected XMLMapping xmlMapping;

	/**
	 * Stores already serialized objects where key is the serialized object and value is a
	 * 
	 * <pre>
	 * Integer
	 * </pre>
	 * 
	 * instance coding the unique identifier of the object
	 */
	private Hashtable<Object, Object> alreadySerialized;

	private Hashtable<Object, Object> serializationIdentifierForObject;

	protected OrderedElementReferenceList orderedElementReferenceList;

	// Keys are objects and values are ObjectReference
	private Hashtable<Object, ObjectReference> objectReferences;

	private StringEncoder stringEncoder;

	/**
	 * Internaly used to get a unique identifier
	 */
	private int nextReference;

	/**
	 * Set TransformerFactory class implementation to use for coding objects.<br>
	 * To work properly, TransformerFactory requires to get an implementation (a a class to load) to get new instances of transformer. The
	 * following ordered lookup procedure to determine the TransformerFactory implementation class to load is applied:
	 * <ul>
	 * <li>Use the javax.xml.transform.TransformerFactory system property</li>
	 * <li>Use the properties file "lib/jaxp.properties" in the JRE directory. This configuration file is in standard java.util.Properties
	 * format and contains the fully qualified name of the implementation class with the key being the system property defined above</li>
	 * <li>Use the Services API (as detailed in the JAR specification), if available, to determine the classname. The Services API will look
	 * for a classname in the file META-INF/services/javax.xml.transform.TransformerFactory in jars available to the runtime</li>
	 * <li>Platform default TransformerFactory instance</li>
	 * </ul>
	 * This method allows to statically sets the system property to the value you want.
	 * 
	 * @param transformerFactoryClassName
	 *            a <code>String</code> value (full qualified name of the TransformerFactory class, for example
	 *            <code>org.apache.xalan.processor.TransformerFactoryImpl</code>)
	 */
	public static void setTransformerFactoryClass(String transformerFactoryClassName) {
		Properties systemProps = System.getProperties();
		systemProps.setProperty("javax.xml.transform.TransformerFactory", transformerFactoryClassName);
	}

	private SerializationHandler _serializationHandler;

	public SerializationHandler getSerializationHandler() {
		return _serializationHandler;
	}

	public void setSerializationHandler(SerializationHandler handler) {
		_serializationHandler = handler;
	}

	/**
	 * Creates a new <code>XMLCoder</code> instance, given a mapping file
	 * 
	 * @param modelFile
	 *            a <code>File</code> value
	 * @exception IOException
	 *                if an IOException error occurs (eg. file not found)
	 * @exception SAXException
	 *                if an error occurs
	 * @exception ParserConfigurationException
	 *                if an error occurs
	 * @exception InvalidModelException
	 *                if an error occurs
	 */
	public XMLCoder(File modelFile) throws IOException, SAXException, ParserConfigurationException, InvalidModelException,
			InvalidModelException {
		this(new XMLMapping(modelFile), StringEncoder.getDefaultInstance());
	}

	/**
	 * Creates a new <code>XMLCoder</code> instance, given a mapping stream
	 * 
	 * @param modelInputStream
	 *            a <code>InputStream</code> value
	 * @exception IOException
	 *                if an IOException error occurs (eg. file not found)
	 * @exception SAXException
	 *                if an error occurs
	 * @exception ParserConfigurationException
	 *                if an error occurs
	 * @exception InvalidModelException
	 *                if an error occurs
	 */
	public XMLCoder(InputStream modelInputStream) throws IOException, SAXException, ParserConfigurationException, InvalidModelException,
			InvalidModelException {
		this(new XMLMapping(modelInputStream), StringEncoder.getDefaultInstance());
	}

	/**
	 * Creates a new <code>XMLCoder</code> instance, given a mapping file
	 * 
	 * @param modelFile
	 *            a <code>File</code> value
	 * 
	 * @param encoder
	 *            the string encoder to use
	 * @exception IOException
	 *                if an IOException error occurs (eg. file not found)
	 * @exception SAXException
	 *                if an error occurs
	 * @exception ParserConfigurationException
	 *                if an error occurs
	 * @exception InvalidModelException
	 *                if an error occurs
	 */
	public XMLCoder(File modelFile, StringEncoder encoder) throws IOException, SAXException, ParserConfigurationException,
			InvalidModelException, InvalidModelException {
		this(new XMLMapping(modelFile), encoder);
	}

	/**
	 * Creates a new <code>XMLCoder</code> instance given an <code>XMLMapping</code> object
	 * 
	 * @param anXmlMapping
	 *            an <code>XMLMapping</code> value
	 */
	public XMLCoder(XMLMapping anXmlMapping) {
		this(anXmlMapping, StringEncoder.getDefaultInstance());
	}

	/**
	 * Creates a new <code>XMLCoder</code> instance given an <code>XMLMapping</code> object
	 * 
	 * @param anXmlMapping
	 *            an <code>XMLMapping</code> value
	 * @param stringEncoder
	 *            the string encoder to use
	 */
	public XMLCoder(XMLMapping anXmlMapping, StringEncoder encoder) {

		super();
		xmlMapping = anXmlMapping;
		alreadySerialized = new Hashtable<Object, Object>();
		serializationIdentifierForObject = new Hashtable<Object, Object>();
		orderedElementReferenceList = new OrderedElementReferenceList();
		objectReferences = new Hashtable<Object, ObjectReference>();
		nextReference = 0;
		stringEncoder = encoder;
	}

	private void delete() {
		alreadySerialized.clear();
		serializationIdentifierForObject.clear();
		orderedElementReferenceList.delete();
		for (ObjectReference next : objectReferences.values()) {
			next.delete();
		}
		objectReferences.clear();
		alreadySerialized = null;
		serializationIdentifierForObject = null;
		orderedElementReferenceList = null;
		objectReferences = null;
		xmlMapping = null;
		stringEncoder = null;
		_serializationHandler = null;
	}

	/**
	 * Encode to an XML string object <code>anObject</code> according to mapping <code>xmlMapping</code>, and returns this newly created
	 * string
	 * 
	 * @param anObject
	 *            an <code>Object</code> value
	 * @param xmlMapping
	 *            a <code>XMLMapping</code> value
	 * @return an <code>Object</code> value
	 * @deprecated use the same method but with the StringEncoder argument
	 *             {@link #encodeObjectWithMapping(XMLSerializable, XMLMapping, StringEncoder)}
	 * @exception InvalidObjectSpecificationException
	 *                if an error occurs
	 * @exception SAXException
	 *                if an error occurs
	 * @exception ParserConfigurationException
	 *                if an error occurs
	 * @exception InvalidModelException
	 *                if an error occurs
	 * @exception InvalidXMLDataException
	 *                if an error occurs
	 * @exception AccessorInvocationException
	 *                if an error occurs during accessor invocation
	 * @throws DuplicateSerializationIdentifierException
	 */
	@Deprecated
	public static String encodeObjectWithMapping(XMLSerializable anObject, XMLMapping xmlMapping)
			throws InvalidObjectSpecificationException, InvalidModelException, AccessorInvocationException,
			DuplicateSerializationIdentifierException {
		return encodeObjectWithMapping(anObject, xmlMapping, (SerializationHandler) null);
	}

	/**
	 * Encode to an XML string object <code>anObject</code> according to mapping <code>xmlMapping</code>, and returns this newly created
	 * string
	 * 
	 * @param anObject
	 *            an <code>Object</code> value
	 * @param xmlMapping
	 *            a <code>XMLMapping</code> value
	 * @param serializationHandler
	 *            TODO
	 * @deprecated use the same method with the StringEncoder argument
	 *             {@link #encodeObjectWithMapping(XMLSerializable, XMLMapping, StringEncoder, SerializationHandler)}
	 * @return an <code>Object</code> value
	 * @exception InvalidObjectSpecificationException
	 *                if an error occurs
	 * @exception SAXException
	 *                if an error occurs
	 * @exception ParserConfigurationException
	 *                if an error occurs
	 * @exception InvalidModelException
	 *                if an error occurs
	 * @exception InvalidXMLDataException
	 *                if an error occurs
	 * @exception AccessorInvocationException
	 *                if an error occurs during accessor invocation
	 * @throws DuplicateSerializationIdentifierException
	 */
	@Deprecated
	public static String encodeObjectWithMapping(XMLSerializable anObject, XMLMapping xmlMapping, SerializationHandler serializationHandler)
			throws InvalidObjectSpecificationException, InvalidModelException, AccessorInvocationException,
			DuplicateSerializationIdentifierException {
		XMLCoder encoder = new XMLCoder(xmlMapping, StringEncoder.getDefaultInstance());
		encoder.setSerializationHandler(serializationHandler);
		return encoder.encodeObject(anObject);
	}

	/**
	 * @param resourceData
	 * @param currentMapping
	 * @param stringEncoder2
	 * @return
	 * @throws DuplicateSerializationIdentifierException
	 * @throws AccessorInvocationException
	 * @throws InvalidModelException
	 * @throws InvalidObjectSpecificationException
	 */
	public static String encodeObjectWithMapping(XMLSerializable anObject, XMLMapping xmlMapping, StringEncoder stringEncoder)
			throws InvalidObjectSpecificationException, InvalidModelException, AccessorInvocationException,
			DuplicateSerializationIdentifierException {
		return encodeObjectWithMapping(anObject, xmlMapping, stringEncoder, null);
	}

	/**
	 * @param serializationHandler
	 *            TODO
	 * @param resourceData
	 * @param currentMapping
	 * @param stringEncoder2
	 * @return
	 * @throws DuplicateSerializationIdentifierException
	 * @throws AccessorInvocationException
	 * @throws InvalidModelException
	 * @throws InvalidObjectSpecificationException
	 */
	public static String encodeObjectWithMapping(XMLSerializable anObject, XMLMapping xmlMapping, StringEncoder stringEncoder,
			SerializationHandler serializationHandler) throws InvalidObjectSpecificationException, InvalidModelException,
			AccessorInvocationException, DuplicateSerializationIdentifierException {
		XMLCoder encoder = new XMLCoder(xmlMapping, stringEncoder);
		encoder.setSerializationHandler(serializationHandler);
		return encoder.encodeObject(anObject);
	}

	/**
	 * Encode to an XML string object <code>anObject</code> according to mapping defined in file <code>modelFile</code>, and returns this
	 * newly created string
	 * 
	 * @param anObject
	 *            an <code>Object</code> value
	 * @param modelFile
	 *            a <code>File</code> value
	 * @return an <code>Object</code> value
	 * @exception InvalidObjectSpecificationException
	 *                if an error occurs
	 * @exception IOException
	 *                if an error occurs
	 * @exception SAXException
	 *                if an error occurs
	 * @exception ParserConfigurationException
	 *                if an error occurs
	 * @exception InvalidModelException
	 *                if an error occurs
	 * @exception InvalidXMLDataException
	 *                if an error occurs
	 * @exception AccessorInvocationException
	 *                if an error occurs during accessor invocation
	 * @throws DuplicateSerializationIdentifierException
	 */
	public static String encodeObjectWithMappingFile(XMLSerializable anObject, File modelFile) throws InvalidObjectSpecificationException,
			IOException, SAXException, ParserConfigurationException, InvalidModelException, AccessorInvocationException,
			DuplicateSerializationIdentifierException {
		return encodeObjectWithMappingFile(anObject, modelFile, (SerializationHandler) null);
	}

	/**
	 * Encode to an XML string object <code>anObject</code> according to mapping defined in stream <code>modelInputStream</code>, and
	 * returns this newly created string
	 * 
	 * @param anObject
	 *            an <code>Object</code> value
	 * @param modelInputStream
	 *            a <code>InputStream</code> value
	 * @return an <code>Object</code> value
	 * @exception InvalidObjectSpecificationException
	 *                if an error occurs
	 * @exception IOException
	 *                if an error occurs
	 * @exception SAXException
	 *                if an error occurs
	 * @exception ParserConfigurationException
	 *                if an error occurs
	 * @exception InvalidModelException
	 *                if an error occurs
	 * @exception InvalidXMLDataException
	 *                if an error occurs
	 * @exception AccessorInvocationException
	 *                if an error occurs during accessor invocation
	 * @throws DuplicateSerializationIdentifierException
	 */
	public static String encodeObjectWithMappingStream(XMLSerializable anObject, InputStream modelInputStream)
			throws InvalidObjectSpecificationException, IOException, SAXException, ParserConfigurationException, InvalidModelException,
			AccessorInvocationException, DuplicateSerializationIdentifierException {
		return encodeObjectWithMappingStream(anObject, modelInputStream, (SerializationHandler) null);
	}

	/**
	 * Encode to an XML string object <code>anObject</code> according to mapping defined in file <code>modelFile</code>, and returns this
	 * newly created string
	 * 
	 * @param anObject
	 *            an <code>Object</code> value
	 * @param modelFile
	 *            a <code>File</code> value
	 * @param serializationHandler
	 *            TODO
	 * @return an <code>Object</code> value
	 * @exception InvalidObjectSpecificationException
	 *                if an error occurs
	 * @exception IOException
	 *                if an error occurs
	 * @exception SAXException
	 *                if an error occurs
	 * @exception ParserConfigurationException
	 *                if an error occurs
	 * @exception InvalidModelException
	 *                if an error occurs
	 * @exception InvalidXMLDataException
	 *                if an error occurs
	 * @exception AccessorInvocationException
	 *                if an error occurs during accessor invocation
	 * @throws DuplicateSerializationIdentifierException
	 */
	public static String encodeObjectWithMappingFile(XMLSerializable anObject, File modelFile, SerializationHandler serializationHandler)
			throws InvalidObjectSpecificationException, IOException, SAXException, ParserConfigurationException, InvalidModelException,
			AccessorInvocationException, DuplicateSerializationIdentifierException {
		XMLCoder encoder = new XMLCoder(modelFile);
		encoder.setSerializationHandler(serializationHandler);
		return encoder.encodeObject(anObject);

	}

	/**
	 * Encode to an XML string object <code>anObject</code> according to mapping defined in stream <code>modelInputStream</code>, and
	 * returns this newly created string
	 * 
	 * @param anObject
	 *            an <code>Object</code> value
	 * @param modelInputStream
	 *            a <code>InputStream</code> value
	 * @param serializationHandler
	 *            TODO
	 * @return an <code>Object</code> value
	 * @exception InvalidObjectSpecificationException
	 *                if an error occurs
	 * @exception IOException
	 *                if an error occurs
	 * @exception SAXException
	 *                if an error occurs
	 * @exception ParserConfigurationException
	 *                if an error occurs
	 * @exception InvalidModelException
	 *                if an error occurs
	 * @exception InvalidXMLDataException
	 *                if an error occurs
	 * @exception AccessorInvocationException
	 *                if an error occurs during accessor invocation
	 * @throws DuplicateSerializationIdentifierException
	 */
	public static String encodeObjectWithMappingStream(XMLSerializable anObject, InputStream modelInputStream,
			SerializationHandler serializationHandler) throws InvalidObjectSpecificationException, IOException, SAXException,
			ParserConfigurationException, InvalidModelException, AccessorInvocationException, DuplicateSerializationIdentifierException {
		XMLCoder encoder = new XMLCoder(modelInputStream);
		encoder.setSerializationHandler(serializationHandler);
		return encoder.encodeObject(anObject);
	}

	/**
	 * Encode to an XML string object <code>anObject</code> according to mapping <code>xmlMapping</code>, and writes it to output stream
	 * <code>out</code>.
	 * 
	 * @param anObject
	 *            an <code>Object</code> value
	 * @param xmlMapping
	 *            a <code>XMLMapping</code> value
	 * @param out
	 *            an <code>OutputStream</code> value
	 * @exception InvalidObjectSpecificationException
	 *                if an error occurs
	 * @exception SAXException
	 *                if an error occurs
	 * @exception ParserConfigurationException
	 *                if an error occurs
	 * @exception InvalidModelException
	 *                if an error occurs
	 * @exception InvalidXMLDataException
	 *                if an error occurs
	 * @exception AccessorInvocationException
	 *                if an error occurs during accessor invocation
	 * @throws DuplicateSerializationIdentifierException
	 */
	public static void encodeObjectWithMapping(XMLSerializable anObject, XMLMapping xmlMapping, OutputStream out)
			throws InvalidObjectSpecificationException, InvalidModelException, AccessorInvocationException,
			DuplicateSerializationIdentifierException {
		encodeObjectWithMapping(anObject, xmlMapping, out, (SerializationHandler) null);
	}

	/**
	 * Encode to an XML string object <code>anObject</code> according to mapping <code>xmlMapping</code>, and writes it to output stream
	 * <code>out</code>.
	 * 
	 * @param anObject
	 *            an <code>Object</code> value
	 * @param xmlMapping
	 *            a <code>XMLMapping</code> value
	 * @param out
	 *            an <code>OutputStream</code> value
	 * @param serializationHandler
	 *            TODO
	 * @exception InvalidObjectSpecificationException
	 *                if an error occurs
	 * @exception SAXException
	 *                if an error occurs
	 * @exception ParserConfigurationException
	 *                if an error occurs
	 * @exception InvalidModelException
	 *                if an error occurs
	 * @exception InvalidXMLDataException
	 *                if an error occurs
	 * @exception AccessorInvocationException
	 *                if an error occurs during accessor invocation
	 * @throws DuplicateSerializationIdentifierException
	 */
	public static void encodeObjectWithMapping(XMLSerializable anObject, XMLMapping xmlMapping, OutputStream out,
			SerializationHandler serializationHandler) throws InvalidObjectSpecificationException, InvalidModelException,
			AccessorInvocationException, DuplicateSerializationIdentifierException {

		XMLCoder encoder = new XMLCoder(xmlMapping, StringEncoder.getDefaultInstance());
		encoder.setSerializationHandler(serializationHandler);
		encoder.encodeObject(anObject, out);
	}

	/**
	 * Encode to an XML string object <code>anObject</code> according to mapping <code>xmlMapping</code>, and writes it to output stream
	 * <code>out</code>.
	 * 
	 * @param anObject
	 *            an <code>Object</code> value
	 * @param xmlMapping
	 *            a <code>XMLMapping</code> value
	 * @param out
	 *            an <code>OutputStream</code> value
	 * @exception InvalidObjectSpecificationException
	 *                if an error occurs
	 * @exception SAXException
	 *                if an error occurs
	 * @exception ParserConfigurationException
	 *                if an error occurs
	 * @exception InvalidModelException
	 *                if an error occurs
	 * @exception InvalidXMLDataException
	 *                if an error occurs
	 * @exception AccessorInvocationException
	 *                if an error occurs during accessor invocation
	 * @throws DuplicateSerializationIdentifierException
	 */
	public static void encodeObjectWithMapping(XMLSerializable anObject, XMLMapping xmlMapping, OutputStream out,
			StringEncoder stringEncoder) throws InvalidObjectSpecificationException, InvalidModelException, AccessorInvocationException,
			DuplicateSerializationIdentifierException {
		encodeObjectWithMapping(anObject, xmlMapping, out, stringEncoder, (SerializationHandler) null);
	}

	/**
	 * Encode to an XML string object <code>anObject</code> according to mapping <code>xmlMapping</code>, and writes it to output stream
	 * <code>out</code>.
	 * 
	 * @param anObject
	 *            an <code>Object</code> value
	 * @param xmlMapping
	 *            a <code>XMLMapping</code> value
	 * @param out
	 *            an <code>OutputStream</code> value
	 * @param serializationHandler
	 *            TODO
	 * @exception InvalidObjectSpecificationException
	 *                if an error occurs
	 * @exception SAXException
	 *                if an error occurs
	 * @exception ParserConfigurationException
	 *                if an error occurs
	 * @exception InvalidModelException
	 *                if an error occurs
	 * @exception InvalidXMLDataException
	 *                if an error occurs
	 * @exception AccessorInvocationException
	 *                if an error occurs during accessor invocation
	 * @throws DuplicateSerializationIdentifierException
	 */
	public static void encodeObjectWithMapping(XMLSerializable anObject, XMLMapping xmlMapping, OutputStream out,
			StringEncoder stringEncoder, SerializationHandler serializationHandler) throws InvalidObjectSpecificationException,
			InvalidModelException, AccessorInvocationException, DuplicateSerializationIdentifierException {

		XMLCoder encoder = new XMLCoder(xmlMapping, stringEncoder);
		encoder.setSerializationHandler(serializationHandler);
		encoder.encodeObject(anObject, out);
	}

	public static void encodeObjectWithMapping(XMLSerializable anObject, XMLMapping xmlMapping, OutputStream out, Format format)
			throws InvalidObjectSpecificationException, InvalidModelException, AccessorInvocationException,
			DuplicateSerializationIdentifierException {
		encodeObjectWithMapping(anObject, xmlMapping, out, format, (SerializationHandler) null);
	}

	public static void encodeObjectWithMapping(XMLSerializable anObject, XMLMapping xmlMapping, OutputStream out, Format format,
			SerializationHandler serializationHandler) throws InvalidObjectSpecificationException, InvalidModelException,
			AccessorInvocationException, DuplicateSerializationIdentifierException {

		XMLCoder encoder = new XMLCoder(xmlMapping, StringEncoder.getDefaultInstance());
		encoder.setSerializationHandler(serializationHandler);
		encoder.encodeObject(anObject, out, format);
	}

	public static void encodeObjectWithMapping(XMLSerializable anObject, XMLMapping xmlMapping, OutputStream out, Format format,
			StringEncoder stringEncoder) throws InvalidObjectSpecificationException, InvalidModelException, AccessorInvocationException,
			DuplicateSerializationIdentifierException {
		encodeObjectWithMapping(anObject, xmlMapping, out, format, stringEncoder, (SerializationHandler) null);
	}

	public static void encodeObjectWithMapping(XMLSerializable anObject, XMLMapping xmlMapping, OutputStream out, Format format,
			StringEncoder stringEncoder, SerializationHandler serializationHandler) throws InvalidObjectSpecificationException,
			InvalidModelException, AccessorInvocationException, DuplicateSerializationIdentifierException {

		XMLCoder encoder = new XMLCoder(xmlMapping, stringEncoder);
		encoder.setSerializationHandler(serializationHandler);
		encoder.encodeObject(anObject, out, format);
	}

	/**
	 * Encode to an XML string object <code>anObject</code> according to mapping defined in file <code>modelFile</code>, and writes it to
	 * output stream <code>out</code>.
	 * 
	 * @param anObject
	 *            an <code>Object</code> value
	 * @param modelFile
	 *            a <code>File</code> value
	 * @param out
	 *            an <code>OutputStream</code> value
	 * @exception InvalidObjectSpecificationException
	 *                if an error occurs
	 * @exception IOException
	 *                if an error occurs
	 * @exception SAXException
	 *                if an error occurs
	 * @exception ParserConfigurationException
	 *                if an error occurs
	 * @exception InvalidModelException
	 *                if an error occurs
	 * @exception InvalidXMLDataException
	 *                if an error occurs
	 * @exception AccessorInvocationException
	 *                if an error occurs during accessor invocation
	 * @throws DuplicateSerializationIdentifierException
	 */
	public static void encodeObjectWithMappingFile(XMLSerializable anObject, File modelFile, OutputStream out)
			throws InvalidObjectSpecificationException, IOException, SAXException, ParserConfigurationException, InvalidModelException,
			AccessorInvocationException, DuplicateSerializationIdentifierException {
		encodeObjectWithMappingFile(anObject, modelFile, out, (SerializationHandler) null);
	}

	/**
	 * Encode to an XML string object <code>anObject</code> according to mapping defined in file <code>modelFile</code>, and writes it to
	 * output stream <code>out</code>.
	 * 
	 * @param anObject
	 *            an <code>Object</code> value
	 * @param modelFile
	 *            a <code>File</code> value
	 * @param out
	 *            an <code>OutputStream</code> value
	 * @param serializationHandler
	 *            TODO
	 * @exception InvalidObjectSpecificationException
	 *                if an error occurs
	 * @exception IOException
	 *                if an error occurs
	 * @exception SAXException
	 *                if an error occurs
	 * @exception ParserConfigurationException
	 *                if an error occurs
	 * @exception InvalidModelException
	 *                if an error occurs
	 * @exception InvalidXMLDataException
	 *                if an error occurs
	 * @exception AccessorInvocationException
	 *                if an error occurs during accessor invocation
	 * @throws DuplicateSerializationIdentifierException
	 */
	public static void encodeObjectWithMappingFile(XMLSerializable anObject, File modelFile, OutputStream out,
			SerializationHandler serializationHandler) throws InvalidObjectSpecificationException, IOException, SAXException,
			ParserConfigurationException, InvalidModelException, AccessorInvocationException, DuplicateSerializationIdentifierException {
		XMLCoder encoder = new XMLCoder(modelFile);
		encoder.setSerializationHandler(serializationHandler);
		encoder.encodeObject(anObject, out);
	}

	/**
	 * Encode to an XML string object <code>anObject</code> according to mapping <code>xmlMapping</code>, and writes it to output stream
	 * <code>out</code>.
	 * 
	 * @param anObject
	 *            an <code>Object</code> value
	 * @param xmlMapping
	 *            a <code>XMLMapping</code> value
	 * @param out
	 *            an <code>OutputStream</code> value
	 * @exception InvalidObjectSpecificationException
	 *                if an error occurs
	 * @exception SAXException
	 *                if an error occurs
	 * @exception ParserConfigurationException
	 *                if an error occurs
	 * @exception InvalidModelException
	 *                if an error occurs
	 * @exception InvalidXMLDataException
	 *                if an error occurs
	 * @exception AccessorInvocationException
	 *                if an error occurs during accessor invocation
	 * @throws DuplicateSerializationIdentifierException
	 */
	public static void encodeObjectWithMapping(XMLSerializable anObject, XMLMapping xmlMapping, OutputStream out, DocType docType)
			throws InvalidObjectSpecificationException, InvalidModelException, AccessorInvocationException,
			DuplicateSerializationIdentifierException {
		encodeObjectWithMapping(anObject, xmlMapping, out, docType, (SerializationHandler) null);
	}

	/**
	 * Encode to an XML string object <code>anObject</code> according to mapping <code>xmlMapping</code>, and writes it to output stream
	 * <code>out</code>.
	 * 
	 * @param anObject
	 *            an <code>Object</code> value
	 * @param xmlMapping
	 *            a <code>XMLMapping</code> value
	 * @param out
	 *            an <code>OutputStream</code> value
	 * @param serializationHandler
	 *            TODO
	 * @exception InvalidObjectSpecificationException
	 *                if an error occurs
	 * @exception SAXException
	 *                if an error occurs
	 * @exception ParserConfigurationException
	 *                if an error occurs
	 * @exception InvalidModelException
	 *                if an error occurs
	 * @exception InvalidXMLDataException
	 *                if an error occurs
	 * @exception AccessorInvocationException
	 *                if an error occurs during accessor invocation
	 * @throws DuplicateSerializationIdentifierException
	 */
	public static void encodeObjectWithMapping(XMLSerializable anObject, XMLMapping xmlMapping, OutputStream out, DocType docType,
			SerializationHandler serializationHandler) throws InvalidObjectSpecificationException, InvalidModelException,
			AccessorInvocationException, DuplicateSerializationIdentifierException {

		XMLCoder encoder = new XMLCoder(xmlMapping, StringEncoder.getDefaultInstance());
		encoder.setSerializationHandler(serializationHandler);
		encoder.encodeObject(anObject, out, docType);
	}

	/**
	 * Encode to an XML string object <code>anObject</code> according to mapping defined in file <code>modelFile</code>, and writes it to
	 * output stream <code>out</code>.
	 * 
	 * @param anObject
	 *            an <code>Object</code> value
	 * @param modelFile
	 *            a <code>File</code> value
	 * @param out
	 *            an <code>OutputStream</code> value
	 * @exception InvalidObjectSpecificationException
	 *                if an error occurs
	 * @exception IOException
	 *                if an error occurs
	 * @exception SAXException
	 *                if an error occurs
	 * @exception ParserConfigurationException
	 *                if an error occurs
	 * @exception InvalidModelException
	 *                if an error occurs
	 * @exception InvalidXMLDataException
	 *                if an error occurs
	 * @exception AccessorInvocationException
	 *                if an error occurs during accessor invocation
	 * @throws DuplicateSerializationIdentifierException
	 */
	public static void encodeObjectWithMappingFile(XMLSerializable anObject, File modelFile, OutputStream out, DocType docType)
			throws InvalidObjectSpecificationException, IOException, SAXException, ParserConfigurationException, InvalidModelException,
			AccessorInvocationException, DuplicateSerializationIdentifierException {
		encodeObjectWithMappingFile(anObject, modelFile, out, docType, (SerializationHandler) null);
	}

	/**
	 * Encode to an XML string object <code>anObject</code> according to mapping defined in file <code>modelFile</code>, and writes it to
	 * output stream <code>out</code>.
	 * 
	 * @param anObject
	 *            an <code>Object</code> value
	 * @param modelFile
	 *            a <code>File</code> value
	 * @param out
	 *            an <code>OutputStream</code> value
	 * @param serializationHandler
	 *            TODO
	 * @exception InvalidObjectSpecificationException
	 *                if an error occurs
	 * @exception IOException
	 *                if an error occurs
	 * @exception SAXException
	 *                if an error occurs
	 * @exception ParserConfigurationException
	 *                if an error occurs
	 * @exception InvalidModelException
	 *                if an error occurs
	 * @exception InvalidXMLDataException
	 *                if an error occurs
	 * @exception AccessorInvocationException
	 *                if an error occurs during accessor invocation
	 * @throws DuplicateSerializationIdentifierException
	 */
	public static void encodeObjectWithMappingFile(XMLSerializable anObject, File modelFile, OutputStream out, DocType docType,
			SerializationHandler serializationHandler) throws InvalidObjectSpecificationException, IOException, SAXException,
			ParserConfigurationException, InvalidModelException, AccessorInvocationException, DuplicateSerializationIdentifierException {

		XMLCoder encoder = new XMLCoder(modelFile);
		encoder.setSerializationHandler(serializationHandler);
		encoder.encodeObject(anObject, out, docType);

	}

	/**
	 * Encode to an XML string object <code>anObject</code> according to mapping defined for this <code>XMLCoder</code>, and returns this
	 * newly created string.
	 * 
	 * @param anObject
	 *            an <code>Object</code> value
	 * @return an <code>Object</code> value
	 * @exception InvalidObjectSpecificationException
	 *                if an error occurs
	 * @exception SAXException
	 *                if an error occurs
	 * @exception ParserConfigurationException
	 *                if an error occurs
	 * @exception InvalidModelException
	 *                if no valid mapping nor mapping file were specified
	 * @exception InvalidXMLDataException
	 *                if an error occurs
	 * @exception AccessorInvocationException
	 *                if an error occurs during accessor invocation
	 * @throws DuplicateSerializationIdentifierException
	 */
	public String encodeObject(XMLSerializable anObject) throws InvalidObjectSpecificationException, InvalidModelException,
			AccessorInvocationException, DuplicateSerializationIdentifierException {

		if (xmlMapping == null) {
			throw new InvalidModelException("No mapping specified.");
		}

		StringWriter writer = new StringWriter();

		buildDocumentAndSetStringWriter(anObject, writer);

		delete();

		return writer.toString();
	}

	/**
	 * Encode to an XML string object <code>anObject</code> according to mapping defined for this <code>XMLCoder</code>, and writes it to
	 * output stream <code>out</code>.
	 * 
	 * @param anObject
	 *            an <code>Object</code> value
	 * @param out
	 *            an <code>OutputStream</code> value
	 * @exception InvalidObjectSpecificationException
	 *                if an error occurs
	 * @exception SAXException
	 *                if an error occurs
	 * @exception ParserConfigurationException
	 *                if an error occurs
	 * @exception InvalidModelException
	 *                if no valid mapping nor mapping file were specified
	 * @exception InvalidXMLDataException
	 *                if an error occurs
	 * @exception AccessorInvocationException
	 *                if an error occurs during accessor invocation
	 * @throws DuplicateSerializationIdentifierException
	 */
	public void encodeObject(XMLSerializable anObject, OutputStream out) throws InvalidObjectSpecificationException, InvalidModelException,
			AccessorInvocationException, DuplicateSerializationIdentifierException {

		if (xmlMapping == null) {
			throw new InvalidModelException("No mapping specified.");
		}

		buildDocumentAndSendToOutputStream(anObject, out, null);

		delete();
	}

	public void encodeObject(XMLSerializable anObject, OutputStream out, Format format) throws InvalidObjectSpecificationException,
			InvalidModelException, AccessorInvocationException, DuplicateSerializationIdentifierException {

		if (xmlMapping == null) {
			throw new InvalidModelException("No mapping specified.");
		}

		buildDocumentAndSendToOutputStream(anObject, out, null, format);

		delete();
	}

	/**
	 * Encode to an XML string object <code>anObject</code> according to mapping defined for this <code>XMLCoder</code>, and writes it to
	 * output stream <code>out</code>.
	 * 
	 * @param anObject
	 *            an <code>Object</code> value
	 * @param out
	 *            an <code>OutputStream</code> value
	 * @exception InvalidObjectSpecificationException
	 *                if an error occurs
	 * @exception SAXException
	 *                if an error occurs
	 * @exception ParserConfigurationException
	 *                if an error occurs
	 * @exception InvalidModelException
	 *                if no valid mapping nor mapping file were specified
	 * @exception InvalidXMLDataException
	 *                if an error occurs
	 * @exception AccessorInvocationException
	 *                if an error occurs during accessor invocation
	 * @throws DuplicateSerializationIdentifierException
	 */
	public void encodeObject(XMLSerializable anObject, OutputStream out, DocType docType) throws InvalidObjectSpecificationException,
			InvalidModelException, AccessorInvocationException, DuplicateSerializationIdentifierException {

		if (xmlMapping == null) {
			throw new InvalidModelException("No mapping specified.");
		}

		buildDocumentAndSendToOutputStream(anObject, out, docType);

		delete();
	}

	/**
	 * Internally used during coding process.<br>
	 * Build XML document from object, transform it, and writes result of coding to a <code>StringWriter</code> object
	 * 
	 * @param anObject
	 *            an <code>Object</code> value
	 * @param aWriter
	 *            a <code>StringWriter</code> value
	 * @exception InvalidObjectSpecificationException
	 *                if an error occurs
	 * @exception ParserConfigurationException
	 *                if an error occurs
	 * @exception TransformerConfigurationException
	 *                if an error occurs
	 * @exception TransformerException
	 *                if an error occurs
	 * @exception InvalidModelException
	 *                if an error occurs
	 * @throws DuplicateSerializationIdentifierException
	 * @throws AccessorInvocationException
	 */
	protected void buildDocumentAndSetStringWriter(Object anObject, StringWriter aWriter) throws InvalidObjectSpecificationException,
			InvalidModelException, AccessorInvocationException, DuplicateSerializationIdentifierException {

		Document builtDocument = buildDocument(anObject);
		Format prettyFormat = Format.getPrettyFormat();
		prettyFormat.setLineSeparator(LineSeparator.SYSTEM);
		XMLOutputter outputter = new XMLOutputter(prettyFormat);
		try {
			outputter.output(builtDocument, aWriter);
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	/**
	 * Internally used during coding process.<br>
	 * Build XML document from object, transform it, and writes result of coding to an <code>OutputStream</code> object
	 * 
	 * @param anObject
	 *            an <code>Object</code> value
	 * @param out
	 *            an <code>OutputStream</code> value
	 * @exception InvalidObjectSpecificationException
	 *                if an error occurs
	 * @exception ParserConfigurationException
	 *                if an error occurs
	 * @exception TransformerConfigurationException
	 *                if an error occurs
	 * @exception TransformerException
	 *                if an error occurs
	 * @exception InvalidModelException
	 *                if an error occurs
	 * @throws DuplicateSerializationIdentifierException
	 * @throws AccessorInvocationException
	 */
	protected void buildDocumentAndSendToOutputStream(Object anObject, OutputStream out, DocType docType)
			throws InvalidObjectSpecificationException, InvalidModelException, AccessorInvocationException,
			DuplicateSerializationIdentifierException {
		Format prettyFormat = Format.getPrettyFormat();
		prettyFormat.setLineSeparator(LineSeparator.SYSTEM);
		buildDocumentAndSendToOutputStream(anObject, out, docType, prettyFormat);
	}

	protected void buildDocumentAndSendToOutputStream(Object anObject, OutputStream out, DocType docType, Format format)
			throws InvalidObjectSpecificationException, InvalidModelException, AccessorInvocationException,
			DuplicateSerializationIdentifierException {

		Document builtDocument = buildDocument(anObject);
		if (docType != null) {
			builtDocument.setDocType(docType);
		}
		XMLOutputter outputter = new XMLOutputter(format);
		try {
			outputter.output(builtDocument, out);
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	/**
	 * Internally used during coding process.<br>
	 * Returns root element given an XML document <code>aDocument</code>
	 * 
	 * @param aDocument
	 *            a <code>Document</code> value
	 * @return a <code>Node</code> value
	 * @exception InvalidXMLDataException
	 *                if an error occurs
	 */
	/*protected Node getRootElement(Document aDocument) throws InvalidXMLDataException
	{

	    NodeList childNodes;
	    Node rootElement;

	    childNodes = aDocument.getChildNodes();
	    if (childNodes.getLength() != 1) {
	        throw new InvalidXMLDataException("XML data should have one and only one root element (" + childNodes.getLength() + " elements found)");
	    }

	    rootElement = aDocument.getFirstChild();
	    if (rootElement.getNodeType() != Node.ELEMENT_NODE) {
	        throw new InvalidXMLDataException("Invalid root element found in XML data");
	    }

	    return rootElement;
	}*/

	/**
	 * Internally used during coding process.<br>
	 * Build and returns XML document given an object <code>anObject</code>
	 * 
	 * @param anObject
	 *            an <code>Object</code> value
	 * @return a <code>Document</code> value
	 * @exception ParserConfigurationException
	 *                if an error occurs
	 * @exception InvalidObjectSpecificationException
	 *                if an error occurs
	 * @exception InvalidModelException
	 *                if an error occurs
	 * @exception AccessorInvocationException
	 *                if an error occurs during accessor invocation
	 * @throws DuplicateSerializationIdentifierException
	 */
	protected Document buildDocument(Object anObject) throws InvalidObjectSpecificationException, InvalidModelException,
			AccessorInvocationException, DuplicateSerializationIdentifierException {

		Document returnedDocument;

		// First, instanciate a new document
		returnedDocument = new Document();

		// Now, we build the document...

		Element rootElement = buildNewElementFrom(anObject, null, returnedDocument);

		postProcess(rootElement);

		returnedDocument.setRootElement(rootElement);

		// ...and we return it
		return returnedDocument;
	}

	/**
	 * Internally used during coding process.<br>
	 * Build and returns new element given an object <code>anObject</code> and an XML document <code>aDocument</code>, and accept that the
	 * encoded object could be a String (and not defined in the model). In this case, use the specified property <code>aProperty</code>.
	 * 
	 * @param anObject
	 *            an <code>Object</code> value
	 * @param aDocument
	 *            a <code>Document</code> value
	 * @return an <code>Element</code> value
	 * @exception InvalidObjectSpecificationException
	 *                if an error occurs
	 * @exception InvalidModelException
	 *                if an error occurs
	 * @exception AccessorInvocationException
	 *                if an error occurs during accessor invocation
	 * @throws DuplicateSerializationIdentifierException
	 */
	protected Element buildNewElementFrom(Object anObject, Document aDocument, ModelProperty aProperty)
			throws InvalidObjectSpecificationException, InvalidModelException, AccessorInvocationException,
			DuplicateSerializationIdentifierException {

		try {
			OrderedElementReferenceList.OrderedElementReference orderedElementReference = null;
			if (xmlMapping.serializationMode == XMLMapping.ORDERED_PSEUDO_TREE) {
				orderedElementReference = orderedElementReferenceList.initEntry();
			}
			Element returned = buildNewElementFrom(anObject, aProperty.getXmlTags(), aDocument);
			if (xmlMapping.serializationMode == XMLMapping.PSEUDO_TREE || xmlMapping.serializationMode == XMLMapping.ORDERED_PSEUDO_TREE) {
				// In those cases, try to handle references
				if (!(anObject instanceof XMLSerializable)) {
					throw new InvalidObjectSpecificationException("This object is not XML-serializable, object=" + anObject);
				}
				ObjectReference ref = objectReferences.get(anObject);
				if (ref != null) {
					ref.notifyNewElementReference(aProperty, returned);
				} else {
					ref = new ObjectReference((XMLSerializable) anObject, aProperty, returned);
					objectReferences.put(anObject, ref);
				}
				if (xmlMapping.serializationMode == XMLMapping.ORDERED_PSEUDO_TREE) {
					orderedElementReferenceList.updateEntry(returned, orderedElementReference, ref);
				}
			}
			return returned;
		} catch (InvalidModelException e) {
			// Remove stack trace because tests failed because of out of memory
			// TODO: investigate this: we should not come into this section !!!!!
			// e.printStackTrace();
			if (aProperty != null) {
				String textValue = null;
				if (stringEncoder._isEncodable(anObject.getClass())) {
					textValue = stringEncoder._encodeObject(anObject);
				}
				if (textValue != null) {
					String xmlTag;
					if (aProperty.getXmlTags().length > 0) {
						xmlTag = aProperty.getXmlTags()[0];
					} else {
						throw e;
					}
					Element returnedElement = new Element(xmlTag);
					returnedElement.addContent(new Text(textValue));
					return returnedElement;
				}
			}
			throw e;
		}
	}

	/**
	 * Internally used during coding process.<br>
	 * Build and returns new element given an object <code>anObject</code> and an XML document <code>aDocument</code>
	 * 
	 * @param anObject
	 *            an <code>Object</code> value
	 * @param aDocument
	 *            a <code>Document</code> value
	 * @return an <code>Element</code> value
	 * @exception InvalidObjectSpecificationException
	 *                if an error occurs
	 * @exception InvalidModelException
	 *                if an error occurs
	 * @exception AccessorInvocationException
	 *                if an error occurs during accessor invocation
	 * @throws DuplicateSerializationIdentifierException
	 */
	protected Element buildNewElementFrom(Object anObject, String[] someXmlTags, Document aDocument)
			throws InvalidObjectSpecificationException, InvalidModelException, AccessorInvocationException,
			DuplicateSerializationIdentifierException {

		ModelEntity modelEntity = null;
		String xmlTag = null;
		boolean xmlTagIsCompound = false;

		// Debugging.debug ("buildNewElementFrom(Object,Document) called with
		// "+anObject);

		if (anObject == null) {
			return null;
		}

		// First search the right ModelEntity from class name
		// NB: the best one is the more specialized.

		Class searchedClass = anObject.getClass();
		while (searchedClass != null && xmlMapping.entityWithClassName(searchedClass.getName()) == null) {
			searchedClass = searchedClass.getSuperclass();
		}

		if (searchedClass != null) {
			modelEntity = xmlMapping.entityWithClassName(searchedClass.getName());
			if (modelEntity != null && modelEntity.isAbstract()) {
				throw new InvalidModelException("Entity matching '" + anObject.getClass().getName() + "' (" + modelEntity.getName()
						+ ") is declared to be abstract in this model and could subsequently not be serialized.");
			}
		}

		if (someXmlTags == null) {
			try {
				xmlTag = modelEntity.getDefaultXmlTag();
				// System.out.println("Coding a "+anObject.getClass().getName()+": chosing TAG "+xmlTag);
			} catch (Exception e) {
				e.printStackTrace();
				throw new InvalidModelException("Unexpected exception " + e.getMessage());
			}
		} else if (someXmlTags.length == 1) {
			xmlTag = someXmlTags[0];
		} else {
			// xmlTag if compound, take the most appropriate
			xmlTagIsCompound = true;
			if (modelEntity == null) {
				throw new InvalidModelException("Tag matching '" + anObject.getClass().getName() + "' not found in model");
			}
			String[] entityTags = modelEntity.getXmlTags();
			if (entityTags == null) {
				throw new InvalidModelException("XML tags for entity '" + modelEntity.getName()
						+ "' not found in model (while trying to encode " + anObject.getClass().getName() + ")");
			}
			String tag;
			for (int j = 0; j < someXmlTags.length; j++) {
				tag = someXmlTags[j];
				for (int i = 0; i < entityTags.length; i++) {
					if (tag.equals(entityTags[i]) && xmlTag == null) {
						xmlTag = tag;
						break;
						// Debugging.debug ("Look up with tag "+xmlTag);
					}
				}
				if (xmlTag != null) {
					break;
				}
			}
			if (xmlTag == null) {
				// Could notLook up tag, choosing first one
				StringBuilder sb = new StringBuilder();
				for (String tags : someXmlTags) {
					if (sb.length() > 0) {
						sb.append(",");
					}
					sb.append(tags);
				}
				xmlTag = someXmlTags[0];
				System.err.println("SEVERE: None of " + sb + " seemed to match " + modelEntity.getName() + " using first one " + xmlTag);
			}
		}

		if (modelEntity == null) {
			throw new InvalidModelException("Tag matching '" + anObject.getClass().getName() + "' not found in model");
		} else if (modelEntity != xmlMapping.entityWithXMLTag(xmlTag) && !xmlTagIsCompound) {
			// System.out.println ("Mapping: "+xmlMapping);
			// System.out.println ("modelEntity: "+modelEntity);
			// System.out.println ("xmlMapping.entityWithXMLTag(xmlTag): "+xmlMapping.entityWithXMLTag(xmlTag));
			throw new InvalidModelException("Entity matching '" + anObject.getClass().getName() + "' does not contain tag " + xmlTag);
		} else {
			return buildNewElementFrom(anObject, xmlTag, modelEntity, aDocument);
		}

	}

	/**
	 * Internally used during coding process.<br>
	 * Build and returns new element given an object <code>anObject</code>, an XML document <code>aDocument</code> and a model entity
	 * <code>aModelEntity</code>
	 * 
	 * @param anObject
	 *            an <code>Object</code> value
	 * @param aModelEntity
	 *            a <code>ModelEntity</code> value
	 * @param aDocument
	 *            a <code>Document</code> value
	 * @return an <code>Element</code> value
	 * @exception InvalidObjectSpecificationException
	 *                if an error occurs
	 * @exception InvalidModelException
	 *                if an error occurs
	 * @exception AccessorInvocationException
	 *                if an error occurs during accessor invocation
	 * @throws DuplicateSerializationIdentifierException
	 *             if two different objects uses the same serialization identifier
	 */
	protected Element buildNewElementFrom(Object anObject, String xmlTag, ModelEntity aModelEntity, Document aDocument)
			throws InvalidObjectSpecificationException, InvalidModelException, AccessorInvocationException,
			DuplicateSerializationIdentifierException {

		Element returnedElement;
		ModelProperty modelProperty;
		boolean primitiveProperty;
		KeyValueProperty keyValueProperty = null;

		// Debugging.debug ("buildNewElementFrom(Object,ModelEntity,Document)
		// called with entity "+aModelEntity.getName()+" and object "+anObject);

		if (anObject == null) {
			return null;
		}

		if (!(anObject instanceof XMLSerializable)) {
			throw new InvalidObjectSpecificationException("This object is not XML-serializable, object=" + anObject);
		}
		// Is this object already serialized ?
		Object reference = alreadySerialized.get(anObject);
		// Integer reference = (Integer) alreadySerialized.get(anObject);

		if (reference == null) {

			if (_serializationHandler != null) {
				_serializationHandler.objectWillBeSerialized((XMLSerializable) anObject);
			}

			// First time i see this object
			// Put this object in alreadySerialized objects
			if (implementsCustomIdMappingScheme()) {
				try {
					reference = xmlMapping.mapId.getIdentifierAsStringForObject((XMLSerializable) anObject, stringEncoder);
				} catch (NoMapIdEntryException e) {
					reference = getNextReference();
				}
			} else {
				reference = getNextReference();
			}
			if (serializationIdentifierForObject.get(reference) != null && serializationIdentifierForObject.get(reference) != anObject) {
				throw new DuplicateSerializationIdentifierException(reference, serializationIdentifierForObject.get(reference), anObject,
						aModelEntity, xmlTag);
			}
			alreadySerialized.put(anObject, reference);
			serializationIdentifierForObject.put(reference, anObject);

			// and then continue to serialize
			// Debugging.debug ("This object has not been serialized until now
			// "+anObject);

			returnedElement = new Element(xmlTag);

			if (xmlMapping.handlesReferences()) {
				returnedElement.setAttribute(XMLMapping.idLabel, reference.toString());
			}

			if (aModelEntity.implementsGenericTypingKVProperty()) {
				String classNameValue = KeyValueDecoder.valueForKey(anObject, aModelEntity.getGenericTypingKVProperty(), stringEncoder);
				// System.out.println("On essaie de faire "+XMLMapping.genericTypingStoredIn+"="+classNameValue);
				returnedElement.setAttribute(XMLMapping.genericTypingClassName, classNameValue);
			} else {
				if (anObject.getClass() != aModelEntity.getRelatedClass()) {
					returnedElement.setAttribute(XMLMapping.classNameLabel, anObject.getClass().getName());
				}
			}

			for (Enumeration en = aModelEntity.getModelProperties(); en.hasMoreElements();) {

				modelProperty = (ModelProperty) en.nextElement();

				// Debugging.debug ("Now working on "+modelProperty.getName());

				// First, get the key-value property
				keyValueProperty = modelProperty.getKeyValueProperty();

				if (keyValueProperty instanceof SingleKeyValueProperty) {

					SingleKeyValueProperty singleKeyValueProperty = (SingleKeyValueProperty) keyValueProperty;

					primitiveProperty = singleKeyValueProperty.classIsPrimitive(stringEncoder);

					// Check if default value should be ignored in serialization
					if (modelProperty.getIgnoreDefaultValue() != null
							&& modelProperty.getIgnoreDefaultValue().equals(
									KeyValueDecoder.valueForKey(anObject, singleKeyValueProperty, stringEncoder))) {

						// System.out.println("Ignore default value "+modelProperty.getIgnoreDefaultValue());
					}

					// Check if property is a text property attribute

					else if (modelProperty.getIsText()) {
						returnedElement.addContent(buildTextNodeFrom(anObject, singleKeyValueProperty, modelProperty, aDocument));
					}

					// Check if property is an attribute

					else if (modelProperty.getIsAttribute()) {
						Attribute attr = buildAttributeNodeFrom(anObject, singleKeyValueProperty, modelProperty, aDocument);
						if (attr != null) {
							returnedElement.setAttribute(attr);
						}
					}

					else { // Property seem to match a unique element

						if (primitiveProperty) {
							Element newElement = new Element(modelProperty.getDefaultXmlTag());
							String value = KeyValueDecoder.valueForKey(anObject, singleKeyValueProperty, stringEncoder);
							if (value != null) {
								Text textValue = new Text(value);
								newElement.addContent(textValue);
								returnedElement.addContent(newElement);
							}
						} else {
							Object newObject = KeyValueDecoder.objectForKey(anObject, keyValueProperty);
							if (newObject != null) {
								returnedElement.addContent(buildNewElementFrom(newObject, aDocument, modelProperty));
							}
						}
					}
				}

				else if (keyValueProperty instanceof VectorKeyValueProperty) {

					List<?> values = KeyValueDecoder.vectorForKey(anObject, (VectorKeyValueProperty) keyValueProperty);
					if (values != null && values.size() > 0) {
						for (Object o : values) {
							returnedElement.addContent(buildNewElementFrom(o, aDocument, modelProperty));
						}
					}
				}

				else if (keyValueProperty instanceof ArrayKeyValueProperty) {

					Object[] values = KeyValueDecoder.arrayForKey(anObject, (ArrayKeyValueProperty) keyValueProperty);
					if (values != null && values.length > 0) {
						for (int i = 0; i < values.length; i++) {
							returnedElement.addContent(buildNewElementFrom(values[i], aDocument, modelProperty));
						}
					}
				}

				else if (keyValueProperty instanceof PropertiesKeyValueProperty) {

					if (modelProperty.isProperties()) {
						Map<?, ?> values = KeyValueDecoder.hashtableForKey(anObject, (PropertiesKeyValueProperty) keyValueProperty);
						if (values != null && values.size() > 0) {
							Element propertiesElement = new Element(modelProperty.getDefaultXmlTag());
							for (Entry<?, ?> e : values.entrySet()) {
								Object keyAsObject = e.getKey();
								if (!(keyAsObject instanceof String)) {
									throw new InvalidDataException("Properties keys must be only String values");
								}
								String key = (String) keyAsObject;
								Object value = e.getValue();

								Element valueElement = new Element(key);

								if (value instanceof PropertiesKeyValueProperty.UndecodableProperty) {
									// In this case, class matching property is not loaded, and thus
									// Object is not instanciated. But, we must keep serialized version
									Text textValue = new Text(((PropertiesKeyValueProperty.UndecodableProperty) value).value);
									valueElement.addContent(textValue);
									valueElement.setAttribute(XMLMapping.classNameLabel,
											((PropertiesKeyValueProperty.UndecodableProperty) value).className);
								} else {
									String valueAsString = stringEncoder._encodeObject(value);
									if (valueAsString != null) {
										Text textValue = new Text(valueAsString);
										valueElement.addContent(textValue);
									}
									valueElement.setAttribute(XMLMapping.classNameLabel, value.getClass().getName());
								}

								propertiesElement.addContent(valueElement);
							}
							returnedElement.addContent(propertiesElement);
						}
					} else if (modelProperty.isSafeProperties()) {
						Map<?, ?> values = KeyValueDecoder.hashtableForKey(anObject, (PropertiesKeyValueProperty) keyValueProperty);
						if (values != null && values.size() > 0) {
							Element propertiesElement = new Element(modelProperty.getDefaultXmlTag());
							for (Entry<?, ?> e : values.entrySet()) {
								Object keyAsObject = e.getKey();
								if (!(keyAsObject instanceof String)) {
									throw new InvalidDataException("Properties keys must be instance of String");
								}
								String key = (String) keyAsObject;
								Object value = e.getValue();

								Element valueElement = new Element(XMLMapping.entryLabel);
								valueElement.setAttribute(XMLMapping.keyLabel, key);
								String valueAsText = null;
								String classNameLabel = null;
								if (value instanceof PropertiesKeyValueProperty.UndecodableProperty) {
									// In this case, class matching property is not loaded, and thus
									// Object is not instanciated. But, we must keep serialized version
									valueAsText = ((PropertiesKeyValueProperty.UndecodableProperty) value).value;
									classNameLabel = ((PropertiesKeyValueProperty.UndecodableProperty) value).className;
								} else if (value != null) {
									valueAsText = stringEncoder._encodeObject(value);
									classNameLabel = value.getClass().getName();
								}
								if (valueAsText != null) {
									valueElement.setAttribute(XMLMapping.classNameLabel, classNameLabel);
									valueElement.setAttribute(XMLMapping.valueLabel, valueAsText);
								}

								propertiesElement.addContent(valueElement);
							}
							returnedElement.addContent(propertiesElement);
						}
					} else if (modelProperty.isUnmappedAttributes()) {

						Map<?, ?> values = KeyValueDecoder.hashtableForKey(anObject, (HashtableKeyValueProperty) keyValueProperty);
						if (values != null) {
							for (Entry<?, ?> e : values.entrySet()) {
								Object keyAsObject = e.getKey();
								if (!(keyAsObject instanceof String)) {
									throw new InvalidDataException("Properties keys must be only String values");
								}
								String key = (String) keyAsObject;
								String valueAsString;
								Object value = e.getValue();
								if (value instanceof String) {
									valueAsString = (String) value;
								} else {
									valueAsString = stringEncoder._encodeObject(value);
								}
								returnedElement.setAttribute(key, valueAsString);
							}
						}
					}

				}

				else if (keyValueProperty instanceof HashtableKeyValueProperty) {

					Map<?, ?> values = KeyValueDecoder.hashtableForKey(anObject, (HashtableKeyValueProperty) keyValueProperty);
					if (values != null && values.size() > 0) {
						if (modelProperty.getKeyToUse() == null) {
							for (Entry<?, ?> e : values.entrySet()) {
								Object key = e.getKey();
								Object value = e.getValue();
								Element valueElement = buildNewElementFrom(value, aDocument, modelProperty);
								if (key instanceof String) {
									valueElement.setAttribute(XMLMapping.keyLabel, (String) key);
								} else {
									Element keyElement = new Element(XMLMapping.keyLabel);
									Element keyValueElement = buildNewElementFrom(key, null, aDocument);
									keyElement.addContent(keyValueElement);
									valueElement.addContent(keyElement);
								}
								returnedElement.addContent(valueElement);
							}
						} else {
							for (Entry<?, ?> e : values.entrySet()) {
								Object key = e.getKey();
								Object value = e.getValue();
								Object expectedKey = KeyValueDecoder.objectForKey(value, modelProperty.getKeyToUse());
								if (key.equals(expectedKey)) {
									// This is the good key, nice !
									Element valueElement = buildNewElementFrom(value, aDocument, modelProperty);
									returnedElement.addContent(valueElement);
								} else {
									/*System.out.println("Object="+anObject);
									System.out.println("values="+values.hashCode()+" "+values);
									for (Enumeration e2 = values.keys(); e2.hasMoreElements();) {
									    Object key2 = e2.nextElement();
									    Object value2 = values.get(key2);
									 	System.out.println("k="+key2+" v="+value2);
									}  */
									throw new InvalidDataException("Strange key found: does not match property specification "
											+ modelProperty.getKeyToUse() + " Found key: " + key + " of " + key.getClass().getName()
											+ " Expected key: " + expectedKey + " of " + expectedKey.getClass().getName()
											+ " current modelProperty is " + modelProperty.getName());
								}
							}
						}
					}
				}

			} // end of for ()

			if (_serializationHandler != null) {
				_serializationHandler.objectHasBeenSerialized((XMLSerializable) anObject);
			}

		}

		else {
			// This object was already serialized somewhere, only put an idref
			// Debugging.debug ("This object has already been serialized
			// somewhere "+anObject);

			if (!xmlMapping.handlesReferences()) {
				throw new InvalidObjectSpecificationException("Loop detected in data structure while 'handlesReferences' in XML model "
						+ "has been disabled.");
			}

			else {
				returnedElement = new Element(xmlTag);
				returnedElement.setAttribute(XMLMapping.idrefLabel, reference.toString());
			}
		}

		return returnedElement;
	}

	/**
	 * Internally used during coding process.<br>
	 * Build and returns new text node given an object <code>anObject</code>, an XML document <code>aDocument</code>, a model property
	 * <code>aModelProperty</code> and a field <code>aField</code>
	 * 
	 * @param anObject
	 *            an <code>Object</code> value
	 * @param aField
	 *            a <code>Field</code> value
	 * @param aModelProperty
	 *            a <code>ModelProperty</code> value
	 * @param aDocument
	 *            a <code>Document</code> value
	 * @return a <code>Text</code> value
	 * @exception InvalidObjectSpecificationException
	 *                if an error occurs
	 * @exception InvalidModelException
	 *                if an error occurs
	 * @exception AccessorInvocationException
	 *                if an error occurs during accessor invocation
	 */
	protected Text buildTextNodeFrom(Object anObject, SingleKeyValueProperty aKeyValueProperty, ModelProperty aModelProperty,
			Document aDocument) throws InvalidObjectSpecificationException, InvalidModelException, AccessorInvocationException {

		Text returnedTextNode;

		// Debugging.debug
		// ("buildTextNodeFrom(Object,Field,ModelProperty,Document) called with
		// property "+aModelProperty.getName());

		String textValue = KeyValueDecoder.valueForKey(anObject, aKeyValueProperty, stringEncoder);
		returnedTextNode = new Text(textValue);

		return returnedTextNode;
	}

	private static final String ILLEGAL_XML_CHARS = "[\\x00-\\x08\\x0B\\x0C\\x0E\\x0F]";
	private static final Pattern ILLEGAL_XML_CHARS_PATTERN = Pattern.compile(ILLEGAL_XML_CHARS);

	/**
	 * Internally used during coding process.<br>
	 * Build and returns new attribute node given an object <code>anObject</code>, an XML document <code>aDocument</code>, a model property
	 * <code>aModelProperty</code> and a field <code>aField</code>
	 * 
	 * @param anObject
	 *            an <code>Object</code> value
	 * @param aField
	 *            a <code>Field</code> value
	 * @param aModelProperty
	 *            a <code>ModelProperty</code> value
	 * @param aDocument
	 *            a <code>Document</code> value
	 * @return an <code>Attr</code> value
	 * @exception InvalidObjectSpecificationException
	 *                if an error occurs
	 * @exception InvalidModelException
	 *                if an error occurs
	 * @exception AccessorInvocationException
	 *                if an error occurs during accessor invocation
	 */

	protected Attribute buildAttributeNodeFrom(Object anObject, SingleKeyValueProperty aKeyValueProperty, ModelProperty aModelProperty,
			Document aDocument) throws InvalidObjectSpecificationException, InvalidModelException, AccessorInvocationException {

		// Debugging.debug
		// ("buildAttributeNodeFrom(Object,Field,ModelProperty,Document) called
		// with property "+aModelProperty.getName());
		String value = KeyValueDecoder.valueForKey(anObject, aKeyValueProperty, stringEncoder);
		if (value != null) {
			return new Attribute(aModelProperty.getDefaultXmlTag(), removeUnacceptableChars(value));
			// return new Attribute(aModelProperty.getDefaultXmlTag(),value);
		} else {
			return null;
		}
	}

	public static String removeUnacceptableChars(String value) {
		return ILLEGAL_XML_CHARS_PATTERN.matcher(value).replaceAll(" ");
	}

	private Integer getNextReference() {
		nextReference++;
		return new Integer(nextReference);
	}

	public boolean implementsCustomIdMappingScheme() {
		return xmlMapping.implementsCustomIdMappingScheme();
	}

	protected class OrderedElementReferenceList {
		protected OrderedElementReference first;
		protected OrderedElementReference last;
		protected int size;

		// Keys are Element and values are OrderedElementReference
		private Hashtable<Element, OrderedElementReference> elementReferences;

		public OrderedElementReferenceList() {
			first = null;
			last = null;
			size = 0;
			elementReferences = new Hashtable<Element, OrderedElementReference>();
		}

		protected void delete() {
			OrderedElementReference previous = null;
			for (Enumeration en = elements(); en.hasMoreElements();) {
				OrderedElementReference ref = (OrderedElementReference) en.nextElement();
				ref.previous = null;
				ref.element = null;
				ref.objectReference = null;
				if (previous != null) {
					previous.next = null;
				}
				previous = ref;
			}
			if (previous != null) {
				previous.next = null;
			}
		}

		public OrderedElementReference initEntry() {
			OrderedElementReference returned = new OrderedElementReference();
			appendElement(returned);
			return returned;
		}

		public void updateEntry(Element element, OrderedElementReference reference, ObjectReference ref) {
			reference.element = element;
			reference.objectReference = ref;
			elementReferences.put(element, reference);
		}

		public void swap(Element element1, Element element2) {
			OrderedElementReference startRef1 = elementReferences.get(element1);
			OrderedElementReference endRef1 = lastDescendantElement(startRef1);
			OrderedElementReference startRef2 = elementReferences.get(element2);
			OrderedElementReference endRef2 = lastDescendantElement(startRef2);
			// System.out.println("swap indexes ("+startRef1.index+"-"+endRef1.index+") and ("+startRef2.index+"-"+endRef2.index+")");
			OrderedElementReference pRef1 = startRef1.previous;
			OrderedElementReference nRef1 = endRef1.next;
			OrderedElementReference pRef2 = startRef2.previous;
			OrderedElementReference nRef2 = endRef2.next;
			startRef1.previous = pRef2;
			endRef1.next = nRef2;
			startRef2.previous = pRef1;
			endRef2.next = nRef1;
			if (pRef1 != null) {
				pRef1.next = startRef2;
			}
			if (nRef1 != null) {
				nRef1.previous = endRef2;
			}
			if (pRef2 != null) {
				pRef2.next = startRef1;
			}
			if (nRef2 != null) {
				nRef2.previous = endRef1;
			}

			if (first == startRef1) {
				first = startRef2;
			}
			if (first == startRef2) {
				first = startRef1;
			}
			if (last == endRef1) {
				last = endRef2;
			}
			if (last == endRef2) {
				last = endRef1;
			}

		}

		private OrderedElementReference lastDescendantElement(OrderedElementReference ref) {
			OrderedElementReference current = ref;
			int i = 0;
			while (current != null) {
				OrderedElementReference next = current.next;
				if (next == null || !ref.element.isAncestor(next.element)) {
					return current;
				}
				current = next;
				i++;
			}
			return null;
		}

		private void appendElement(OrderedElementReference e) {
			e.index = size;
			if (first == null) {
				first = e;
			}
			if (last != null) {
				last.next = e;
			}
			e.previous = last;
			last = e;
			size++;
		}

		public Enumeration elements() {
			return new OrderedElementReferenceListEnumeration();
		}

		protected class OrderedElementReferenceListEnumeration implements Enumeration {
			private OrderedElementReference current;

			protected OrderedElementReferenceListEnumeration() {
				current = first;
			}

			@Override
			public boolean hasMoreElements() {
				return current != null;
			}

			@Override
			public Object nextElement() {
				Object returned = current;
				if (current != null) {
					current = current.next;
				}
				return returned;
			}

		}

		protected class OrderedElementReference {
			protected OrderedElementReference previous;
			protected OrderedElementReference next;
			protected Element element;
			protected ObjectReference objectReference;
			protected int index;

			protected boolean isPrimary() {
				return objectReference.isFullyDescribed(element);
			}

		}

	}

	protected class ObjectReference {
		private int id = -1;
		private String customIdMappingValue = null;
		protected XMLSerializable serializedObject;
		protected ElementReference primaryElement;
		protected Vector<ElementReference> referenceElements;

		protected ObjectReference(XMLSerializable anObject, ModelProperty aProperty, Element anElement) {
			super();
			serializedObject = anObject;
			referenceElements = new Vector<ElementReference>();
			addElementReference(new ElementReference(aProperty, anElement));
			if (implementsCustomIdMappingScheme()) {
				try {
					customIdMappingValue = xmlMapping.mapId.getIdentifierAsStringForObject(anObject, stringEncoder);
				} catch (NoMapIdEntryException e) {
					id = idForElement(anElement);
				}
			} else {
				id = idForElement(anElement);
			}
		}

		protected void delete() {
			serializedObject = null;
			primaryElement.delete();
			for (Enumeration<ElementReference> en = referenceElements.elements(); en.hasMoreElements();) {
				ElementReference next = en.nextElement();
				next.delete();
			}
			primaryElement = null;
			referenceElements.clear();
			referenceElements = null;
		}

		protected void notifyNewElementReference(ModelProperty aProperty, Element anElement) {
			addElementReference(new ElementReference(aProperty, anElement));
		}

		protected int getId() {
			return id;
		}

		protected void changeId(int newId) {
			// System.out.println("changeId() to "+newId+" for "+primaryElement.element);
			if (primaryElement != null && primaryElement.element != null) {
				changeIdForElement(newId, primaryElement.element);
			}
			for (Enumeration en = referenceElements.elements(); en.hasMoreElements();) {
				ElementReference next = (ElementReference) en.nextElement();
				if (next.element != null) {
					changeIdForElement(newId, next.element);
				}
			}
		}

		protected void changeIdForElement(int newId, Element element) {
			if (element.getAttribute(XMLMapping.idLabel) != null) {
				element.setAttribute(XMLMapping.idLabel, StringEncoder.encodeInteger(newId));
			} else if (element.getAttribute(XMLMapping.idrefLabel) != null) {
				element.setAttribute(XMLMapping.idrefLabel, StringEncoder.encodeInteger(newId));
			}
		}

		private void addElementReference(ElementReference elementReference) {
			if (isFullyDescribed(elementReference.element)) {
				// System.out.println("object: "+serializedObject.getClass().getName()+"/"+serializedObject.hashCode()+" PRIMARY "+outputter.outputString(elementReference.element));
				primaryElement = elementReference;
			} else {
				// System.out.println("object: "+serializedObject.getClass().getName()+"/"+serializedObject.hashCode()+"         "+outputter.outputString(elementReference.element));
				referenceElements.add(elementReference);
			}
		}

		protected boolean isFullyDescribed(Element element) {
			return element.getAttribute("id") != null;
		}

		private boolean done = false;

		protected boolean postProcess() {
			if (done) {
				return true;
			}
			if (primaryElement.property.isPrimary()) { // That's OK
				done = true;
				return true;
			} else { // It might be NOK
				for (Enumeration en = referenceElements.elements(); en.hasMoreElements();) {
					ElementReference next = (ElementReference) en.nextElement();
					if (next.property.isPrimary()) {
						return setAsNewPrimaryElement(next);
					}
				}
				done = true;
				return true;
			}
		}

		private boolean setAsNewPrimaryElement(ElementReference newElementReference) {
			// System.out.println("Need to exchange "+primaryElement.element+" and "+newElementReference.element);
			if (exchange(primaryElement, newElementReference)) {
				referenceElements.remove(newElementReference);
				referenceElements.add(primaryElement);
				primaryElement = newElementReference;
				done = true;
				return true;
			} else {
				return false;
			}
		}

		private boolean exchange(ElementReference ref1, ElementReference ref2) {
			Element element1 = ref1.element;
			Element element2 = ref2.element;
			Element father1 = element1.getParentElement();
			Element father2 = element2.getParentElement();
			if (isAncestorOf(element1, element2)) {
				// In this case, do nothing and try later (in another loop)
				return false;
			} else if (isAncestorOf(element2, element1)) {
				// In this case, do nothing and try later (in another loop)
				return false;
			} else {
				int index1 = father1.indexOf(element1);
				father1.removeContent(index1);
				int index2 = father2.indexOf(element2);
				father2.removeContent(index2);
				father2.addContent(index2, element1);
				father1.addContent(index1, element2);
				if (!ref1.xmlTag.equals(ref2.xmlTag)) {
					// System.out.println("Exchange names "+ref1.xmlTag+" and "+ref2.xmlTag);
					element1.setName(ref2.xmlTag);
					element2.setName(ref1.xmlTag);
				}
				if (xmlMapping.serializationMode == XMLMapping.ORDERED_PSEUDO_TREE) {
					orderedElementReferenceList.swap(element1, element2);
				}
				return true;
			}
		}

		private int idForElement(Element el) {
			int returned = StringEncoder.decodeAsInteger(el.getAttributeValue(XMLMapping.idLabel));
			if (returned == -1) {
				returned = StringEncoder.decodeAsInteger(el.getAttributeValue(XMLMapping.idrefLabel));
			}
			return returned;
		}

		private boolean isAncestorOf(Element e1, Element e2) {
			return e1.isAncestor(e2);
		}

		protected class ElementReference {

			protected ModelProperty property;
			protected String xmlTag;
			protected Element element;

			protected ElementReference(ModelProperty aProperty, Element anElement) {
				super();
				property = aProperty;
				element = anElement;
				xmlTag = anElement.getName();
			}

			protected void delete() {
				property = null;
				xmlTag = null;
				element = null;
			}
		}
	}

	private void postProcess(Element rootElement) {
		if (xmlMapping.serializationMode == XMLMapping.DEEP_FIRST) {
			return;
		} else if (xmlMapping.serializationMode == XMLMapping.PSEUDO_TREE || xmlMapping.serializationMode == XMLMapping.ORDERED_PSEUDO_TREE) {

			int requiredSwaps = objectReferences.size();
			while (requiredSwaps > 0) {
				// System.out.println("Still "+requiredSwaps+" post processing");
				int newRequiredSwaps = 0;
				for (Enumeration en = objectReferences.elements(); en.hasMoreElements();) {
					ObjectReference next = (ObjectReference) en.nextElement();
					if (!next.postProcess()) {
						newRequiredSwaps++;
					}
				}
				if (newRequiredSwaps == requiredSwaps) {
					requiredSwaps = 0; // To avoid infinite loop
				} else {
					requiredSwaps = newRequiredSwaps;
				}
			}

			if (xmlMapping.serializationMode == XMLMapping.ORDERED_PSEUDO_TREE && !implementsCustomIdMappingScheme()) {
				int i = 0;
				int newIndex = 1;
				Enumeration en = orderedElementReferenceList.elements();
				for (; en.hasMoreElements(); i++) {
					OrderedElementReferenceList.OrderedElementReference next = (OrderedElementReferenceList.OrderedElementReference) en
							.nextElement();
					// System.out.println(">: "+i+((next.isPrimary())?" * ":"   ")+next.index+"  "+next.element);
					if (next.isPrimary()) {
						next.objectReference.changeId(++newIndex);
					}
				}
			}
		}
	}

}
