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

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import javax.xml.parsers.ParserConfigurationException;

import org.jdom.Attribute;
import org.jdom.Content;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.Text;
import org.jdom.filter.Filter;
import org.jdom.input.SAXBuilder;
import org.xml.sax.SAXException;

/**
 * <p>
 * Utility class providing XML decoding facilities
 * </p>
 * This class allow you to decode object to an XML string or streams according to a mapping you define externaly (see {@link XMLMapping}).
 * Objects are automatically instancied from XML strings or streams.<br>
 * Supposing you have your data in an object myData (a string or a stream object), decoding process is as simpler as this:
 * 
 * <pre>
 * XMLMapping myMapping = new XMLMapping(modelFile);
 * 
 * MyClass myObject = (MyClass) XMLDecoder.decodeObjectWithMapping(myData, myMapping);
 * </pre>
 * 
 * or directly by specifying model file:
 * 
 * <pre>
 * MyClass myObject = (MyClass) XMLDecoder.decodeObjectWithMappingFile(myData, modelFile);
 * </pre>
 * 
 * where <code>myData</code> is either a <code>String</code> or an <code>mInputStream</code> and <br>
 * In this example, a new instance of <code>MyClass</code> class is instancied and automatically sets with values and newly created
 * instances of others sub-classes.
 * 
 * @author <a href="mailto:Sylvain.Guerin@enst-bretagne.fr">Sylvain Guerin</a>
 * @see XMLCoder
 * @see XMLMapping
 */
public class XMLDecoder {

	/** Stores mapping that will be used for decoding */
	protected XMLMapping xmlMapping;

	/** Stores builder object that will be used for decoding */
	protected Object builder;

	/**
	 * Internaly used to get a unique identifier
	 */
	private int nextReference;

	/**
	 * Stores already serialized objects where value is the serialized object and key is a
	 * 
	 * <pre>
	 * Integer
	 * </pre>
	 * 
	 * instance coding the unique identifier of the object
	 */
	private Hashtable alreadyDeserialized;

	/**
	 * This the variable used to encode objects as strings. If it is not set, the default instance will be used.
	 */
	private StringEncoder stringEncoder;

	/**
	 * Creates a new <code>XMLDecoder</code> instance given an XMLMapping object
	 * 
	 * @param anXmlMapping
	 *            an <code>XMLMapping</code> value
	 */
	public XMLDecoder(XMLMapping anXmlMapping) {
		this(anXmlMapping, null, StringEncoder.getDefaultInstance());
	}

	/**
	 * Creates a new <code>XMLDecoder</code> instance given an XMLMapping object
	 * 
	 * @param anXmlMapping
	 *            an <code>XMLMapping</code> value
	 */
	public XMLDecoder(XMLMapping anXmlMapping, StringEncoder stringEncoder) {
		this(anXmlMapping, null, stringEncoder);
	}

	/**
	 * Creates a new <code>XMLDecoder</code> instance given an XMLMapping object and a builder object
	 * 
	 * @param anXmlMapping
	 *            an <code>XMLMapping</code> value
	 */
	public XMLDecoder(XMLMapping anXmlMapping, Object aBuilder) {
		this(anXmlMapping, aBuilder, StringEncoder.getDefaultInstance());
	}

	/**
	 * Creates a new <code>XMLDecoder</code> instance, given a mapping file
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
	public XMLDecoder(File modelFile) throws IOException, SAXException, ParserConfigurationException, InvalidModelException {
		this(new XMLMapping(modelFile), StringEncoder.getDefaultInstance());
	}

	/**
	 * Creates a new <code>XMLDecoder</code> instance, given a mapping stream
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
	public XMLDecoder(InputStream modelInputStream) throws IOException, SAXException, ParserConfigurationException, InvalidModelException {
		this(new XMLMapping(modelInputStream), StringEncoder.getDefaultInstance());
	}

	/**
	 * Creates a new <code>XMLDecoder</code> instance, given a mapping file
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
	public XMLDecoder(File modelFile, StringEncoder stringEncoder) throws IOException, SAXException, ParserConfigurationException,
			InvalidModelException {
		this(new XMLMapping(modelFile), null, stringEncoder);
	}

	/**
	 * Creates a new <code>XMLDecoder</code> instance, given a mapping file and a builder object
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
	public XMLDecoder(File modelFile, Object aBuilder) throws IOException, SAXException, ParserConfigurationException,
			InvalidModelException {
		this(new XMLMapping(modelFile), aBuilder, StringEncoder.getDefaultInstance());
	}

	/**
	 * Creates a new <code>XMLDecoder</code> instance, given a mapping file and a builder object
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
	public XMLDecoder(File modelFile, Object aBuilder, StringEncoder stringEncoder) throws IOException, SAXException,
			ParserConfigurationException, InvalidModelException {
		this(new XMLMapping(modelFile), aBuilder, stringEncoder);
	}

	/**
	 * Creates a new <code>XMLDecoder</code> instance given an XMLMapping object and a builder object
	 * 
	 * @param anXmlMapping
	 *            an <code>XMLMapping</code> value
	 * @param stringEncoder
	 *            - the string encoder to use with this decoder
	 */
	public XMLDecoder(XMLMapping anXmlMapping, Object aBuilder, StringEncoder encoder) {
		super();
		xmlMapping = anXmlMapping;
		alreadyDeserialized = new Hashtable();
		nextReference = 0;
		builder = aBuilder;
		stringEncoder = encoder;
	}

	private void delete() {
		alreadyDeserialized.clear();
		alreadyDeserialized = null;
		builder = null;
		xmlMapping = null;
		stringEncoder = null;
	}

	/**
	 * Decode and returns a newly created object from string <code>xmlString</code> according to mapping <code>xmlMapping</code>.
	 * 
	 * @param xmlString
	 *            a <code>String</code> value
	 * @param xmlMapping
	 *            a <code>XMLMapping</code> value
	 * @return an <code>XMLSerializable</code> value
	 * @exception InvalidXMLDataException
	 *                if an error occurs
	 * @exception InvalidObjectSpecificationException
	 *                if an error occurs
	 * @exception SAXException
	 *                if an error occurs
	 * @exception ParserConfigurationException
	 *                if an error occurs
	 * @exception IOException
	 *                if an error occurs
	 * @exception AccessorInvocationException
	 *                if an error occurs during accessor invocation
	 * @throws JDOMException
	 * @throws InvalidModelException
	 */
	public static XMLSerializable decodeObjectWithMapping(String xmlString, XMLMapping xmlMapping) throws InvalidXMLDataException,
			InvalidObjectSpecificationException, IOException, AccessorInvocationException, InvalidModelException, JDOMException {

		XMLDecoder decoder = new XMLDecoder(xmlMapping);
		return decoder.decodeObject(xmlString);
	}

	/**
	 * Decode and returns a newly created object from string <code>xmlString</code> according to mapping defined in model file
	 * <code>modelFile</code>.
	 * 
	 * @param xmlString
	 *            a <code>String</code> value
	 * @param modelFile
	 *            a <code>File</code> value
	 * @return an <code>XMLSerializable</code> value
	 * @exception InvalidXMLDataException
	 *                if an error occurs
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
	 * @exception AccessorInvocationException
	 *                if an error occurs during accessor invocation
	 * @throws JDOMException
	 */
	public static XMLSerializable decodeObjectWithMapping(String xmlString, File modelFile) throws InvalidXMLDataException,
			InvalidObjectSpecificationException, IOException, SAXException, ParserConfigurationException, InvalidModelException,
			AccessorInvocationException, JDOMException {

		XMLDecoder decoder = new XMLDecoder(modelFile);
		return decoder.decodeObject(xmlString);
	}

	/**
	 * Decode and returns a newly created object from string <code>xmlString</code> according to mapping defined in model stream
	 * <code>modelInputStream</code>.
	 * 
	 * @param xmlString
	 *            a <code>String</code> value
	 * @param modelInputStream
	 *            a <code>InputStream</code> value
	 * @return an <code>XMLSerializable</code> value
	 * @exception InvalidXMLDataException
	 *                if an error occurs
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
	 * @exception AccessorInvocationException
	 *                if an error occurs during accessor invocation
	 * @throws JDOMException
	 */
	public static XMLSerializable decodeObjectWithMapping(String xmlString, InputStream modelInputStream) throws InvalidXMLDataException,
			InvalidObjectSpecificationException, IOException, SAXException, ParserConfigurationException, InvalidModelException,
			AccessorInvocationException, JDOMException {
		XMLDecoder decoder = new XMLDecoder(modelInputStream);
		return decoder.decodeObject(xmlString);
	}

	/**
	 * Decode and returns a newly created object from input stream <code>xmlStream</code> according to mapping <code>xmlMapping</code>.
	 * 
	 * @param xmlStream
	 *            a <code>InputStream</code> value
	 * @param xmlMapping
	 *            a <code>XMLMapping</code> value
	 * @return an <code>XMLSerializable</code> value
	 * @exception InvalidXMLDataException
	 *                if an error occurs
	 * @exception InvalidObjectSpecificationException
	 *                if an error occurs
	 * @exception SAXException
	 *                if an error occurs
	 * @exception ParserConfigurationException
	 *                if an error occurs
	 * @exception IOException
	 *                if an error occurs
	 * @exception AccessorInvocationException
	 *                if an error occurs during accessor invocation
	 * @throws JDOMException
	 * @throws InvalidModelException
	 */
	public static XMLSerializable decodeObjectWithMapping(InputStream xmlStream, XMLMapping xmlMapping) throws InvalidXMLDataException,
			InvalidObjectSpecificationException, IOException, AccessorInvocationException, InvalidModelException, JDOMException {

		XMLDecoder decoder = new XMLDecoder(xmlMapping);
		return decoder.decodeObject(xmlStream);
	}

	/**
	 * Decode and returns a newly created object from input stream <code>xmlStream</code> according to mapping defined in model file
	 * <code>modelFile</code>.
	 * 
	 * @param modelFile
	 *            a <code>File</code> value
	 * @param xmlStream
	 *            a <code>InputStream</code> value
	 * @return an <code>XMLSerializable</code> value
	 * @exception InvalidXMLDataException
	 *                if an error occurs
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
	 * @exception AccessorInvocationException
	 *                if an error occurs during accessor invocation
	 * @throws JDOMException
	 */
	public static XMLSerializable decodeObjectWithMappingFile(InputStream xmlStream, File modelFile) throws InvalidXMLDataException,
			InvalidObjectSpecificationException, IOException, SAXException, ParserConfigurationException, InvalidModelException,
			AccessorInvocationException, JDOMException {

		XMLDecoder decoder = new XMLDecoder(modelFile);
		return decoder.decodeObject(xmlStream);

	}

	/**
	 * Decode and returns a newly created object from string with a builder <code>xmlString</code> according to mapping
	 * <code>xmlMapping</code>.
	 * 
	 * @param xmlString
	 *            a <code>String</code> value
	 * @param xmlMapping
	 *            a <code>XMLMapping</code> value
	 * @return an <code>XMLSerializable</code> value
	 * @exception InvalidXMLDataException
	 *                if an error occurs
	 * @exception InvalidObjectSpecificationException
	 *                if an error occurs
	 * @exception SAXException
	 *                if an error occurs
	 * @exception ParserConfigurationException
	 *                if an error occurs
	 * @exception IOException
	 *                if an error occurs
	 * @exception AccessorInvocationException
	 *                if an error occurs during accessor invocation
	 * @throws JDOMException
	 * @throws InvalidModelException
	 */
	public static XMLSerializable decodeObjectWithMapping(String xmlString, XMLMapping xmlMapping, Object builder)
			throws InvalidXMLDataException, InvalidObjectSpecificationException, IOException, AccessorInvocationException,
			InvalidModelException, JDOMException {

		XMLDecoder decoder = new XMLDecoder(xmlMapping, builder, StringEncoder.getDefaultInstance());
		return decoder.decodeObject(xmlString);
	}

	/**
	 * Decode and returns a newly created object from string with a builder <code>xmlString</code> according to mapping
	 * <code>xmlMapping</code>.
	 * 
	 * @param xmlString
	 *            a <code>String</code> value
	 * @param xmlMapping
	 *            a <code>XMLMapping</code> value
	 * @return an <code>XMLSerializable</code> value
	 * @exception InvalidXMLDataException
	 *                if an error occurs
	 * @exception InvalidObjectSpecificationException
	 *                if an error occurs
	 * @exception SAXException
	 *                if an error occurs
	 * @exception ParserConfigurationException
	 *                if an error occurs
	 * @exception IOException
	 *                if an error occurs
	 * @exception AccessorInvocationException
	 *                if an error occurs during accessor invocation
	 * @throws JDOMException
	 * @throws InvalidModelException
	 */
	public static XMLSerializable decodeObjectWithMapping(String xmlString, XMLMapping xmlMapping, Object builder,
			StringEncoder stringEncoder) throws InvalidXMLDataException, InvalidObjectSpecificationException, IOException,
			AccessorInvocationException, InvalidModelException, JDOMException {

		XMLDecoder decoder = new XMLDecoder(xmlMapping, builder, stringEncoder);
		return decoder.decodeObject(xmlString);
	}

	/**
	 * Decode and returns a newly created object from string with a builder <code>xmlString</code> according to mapping defined in model
	 * file <code>modelFile</code>.
	 * 
	 * @param xmlString
	 *            a <code>String</code> value
	 * @param modelFile
	 *            a <code>File</code> value
	 * @return an <code>XMLSerializable</code> value
	 * @exception InvalidXMLDataException
	 *                if an error occurs
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
	 * @exception AccessorInvocationException
	 *                if an error occurs during accessor invocation
	 * @throws JDOMException
	 */
	public static XMLSerializable decodeObjectWithMapping(String xmlString, File modelFile, Object builder) throws InvalidXMLDataException,
			InvalidObjectSpecificationException, IOException, SAXException, ParserConfigurationException, InvalidModelException,
			AccessorInvocationException, JDOMException {

		XMLDecoder decoder = new XMLDecoder(modelFile, builder);
		return decoder.decodeObject(xmlString);
	}

	/**
	 * Decode and returns a newly created object from input stream with a builder <code>xmlStream</code> according to mapping
	 * <code>xmlMapping</code>.
	 * 
	 * @param xmlStream
	 *            a <code>InputStream</code> value
	 * @param xmlMapping
	 *            a <code>XMLMapping</code> value
	 * @return an <code>XMLSerializable</code> value
	 * @exception InvalidXMLDataException
	 *                if an error occurs
	 * @exception InvalidObjectSpecificationException
	 *                if an error occurs
	 * @exception SAXException
	 *                if an error occurs
	 * @exception ParserConfigurationException
	 *                if an error occurs
	 * @exception IOException
	 *                if an error occurs
	 * @exception AccessorInvocationException
	 *                if an error occurs during accessor invocation
	 * @throws JDOMException
	 * @throws InvalidModelException
	 */
	public static XMLSerializable decodeObjectWithMapping(InputStream xmlStream, XMLMapping xmlMapping, Object builder)
			throws InvalidXMLDataException, InvalidObjectSpecificationException, IOException, AccessorInvocationException,
			InvalidModelException, JDOMException {

		XMLDecoder decoder = new XMLDecoder(xmlMapping, builder, StringEncoder.getDefaultInstance());
		return decoder.decodeObject(xmlStream);
	}

	/**
	 * Decode and returns a newly created object from input stream with a builder <code>xmlStream</code> according to mapping
	 * <code>xmlMapping</code>.
	 * 
	 * @param xmlStream
	 *            a <code>InputStream</code> value
	 * @param xmlMapping
	 *            a <code>XMLMapping</code> value
	 * @return an <code>XMLSerializable</code> value
	 * @exception InvalidXMLDataException
	 *                if an error occurs
	 * @exception InvalidObjectSpecificationException
	 *                if an error occurs
	 * @exception SAXException
	 *                if an error occurs
	 * @exception ParserConfigurationException
	 *                if an error occurs
	 * @exception IOException
	 *                if an error occurs
	 * @exception AccessorInvocationException
	 *                if an error occurs during accessor invocation
	 * @throws JDOMException
	 * @throws InvalidModelException
	 */
	public static XMLSerializable decodeObjectWithMapping(InputStream xmlStream, XMLMapping xmlMapping, Object builder,
			StringEncoder stringEncoder) throws InvalidXMLDataException, InvalidObjectSpecificationException, IOException,
			AccessorInvocationException, InvalidModelException, JDOMException {

		XMLDecoder decoder = new XMLDecoder(xmlMapping, builder, stringEncoder);
		return decoder.decodeObject(xmlStream);
	}

	/**
	 * Decode and returns a newly created object from input stream with a builder <code>xmlStream</code> according to mapping defined in
	 * model file <code>modelFile</code>.
	 * 
	 * @param modelFile
	 *            a <code>File</code> value
	 * @param xmlStream
	 *            a <code>InputStream</code> value
	 * @return an <code>XMLSerializable</code> value
	 * @exception InvalidXMLDataException
	 *                if an error occurs
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
	 * @exception AccessorInvocationException
	 *                if an error occurs during accessor invocation
	 * @throws JDOMException
	 */
	public static XMLSerializable decodeObjectWithMappingFile(InputStream xmlStream, File modelFile, Object builder)
			throws InvalidXMLDataException, InvalidObjectSpecificationException, IOException, SAXException, ParserConfigurationException,
			InvalidModelException, AccessorInvocationException, JDOMException {

		XMLDecoder decoder = new XMLDecoder(modelFile, builder);
		return decoder.decodeObject(xmlStream);

	}

	/**
	 * Decode and returns a newly created object from string <code>xmlString</code> according to mapping defined in this
	 * <code>XMLDecoder</code> object.
	 * 
	 * @param xmlString
	 *            a <code>String</code> value
	 * @return an <code>XMLSerializable</code> value
	 * @exception InvalidXMLDataException
	 *                if an error occurs
	 * @exception InvalidObjectSpecificationException
	 *                if an error occurs
	 * @exception SAXException
	 *                if an error occurs
	 * @exception ParserConfigurationException
	 *                if an error occurs
	 * @exception InvalidModelException
	 *                if no valid mapping nor mapping file were specified
	 * @exception IOException
	 *                if an error occurs
	 * @exception AccessorInvocationException
	 *                if an error occurs during accessor invocation
	 * @throws JDOMException
	 */
	public XMLSerializable decodeObject(String xmlString) throws InvalidXMLDataException, InvalidObjectSpecificationException,
			InvalidModelException, IOException, AccessorInvocationException, JDOMException {

		if (xmlMapping == null) {
			throw new InvalidModelException("No mapping specified.");
		}

		XMLSerializable returned = buildObjectFromDocument(parseXMLData(xmlString));
		delete();
		return returned;

	}

	/**
	 * Decode and returns a newly created object from input stream <code>xmlStream</code> according to mapping defined in this
	 * <code>XMLDecoder</code> object.
	 * 
	 * @param xmlStream
	 *            a <code>String</code> value
	 * @return an <code>XMLSerializable</code> value
	 * @exception InvalidXMLDataException
	 *                if an error occurs
	 * @exception InvalidObjectSpecificationException
	 *                if an error occurs
	 * @exception SAXException
	 *                if an error occurs
	 * @exception ParserConfigurationException
	 *                if an error occurs
	 * @exception InvalidModelException
	 *                if no valid mapping nor mapping file were specified
	 * @exception IOException
	 *                if an error occurs
	 * @exception AccessorInvocationException
	 *                if an error occurs during accessor invocation
	 * @throws JDOMException
	 */
	public XMLSerializable decodeObject(InputStream xmlStream) throws InvalidXMLDataException, InvalidObjectSpecificationException,
			InvalidModelException, IOException, AccessorInvocationException, JDOMException {

		if (xmlMapping == null) {
			throw new InvalidModelException("No mapping specified.");
		}

		XMLSerializable returned = buildObjectFromDocument(parseXMLData(xmlStream));
		delete();
		return returned;

	}

	/**
	 * Returns flag indicating if specified <code>xmlString</code> is well formed.
	 * 
	 * @param xmlString
	 *            a <code>String</code> value
	 * @return a <code>boolean</code> value
	 * @exception SAXException
	 *                if an error occurs (independantly of well-forming)
	 * @exception ParserConfigurationException
	 *                if an error occurs (independantly of well-forming)
	 * @exception IOException
	 *                if an error occurs (independantly of well-forming)
	 */
	public static boolean isWellFormed(String xmlString) throws IOException {

		try {
			XMLDecoder decoder = new XMLDecoder((XMLMapping) null);
			decoder.parseXMLData(xmlString);
			return true;
		} catch (JDOMException e) {
			return false;
		}
	}

	/**
	 * Internally used durinf XML decoding process.<br>
	 * Parse xml data from a string <code>xmlString</code> and returns a newly created XML document.
	 * 
	 * @param xmlString
	 *            a <code>String</code> value
	 * @return a <code>Document</code> value
	 * @exception SAXException
	 *                if an error occurs
	 * @exception ParserConfigurationException
	 *                if an error occurs
	 * @exception IOException
	 *                if an error occurs
	 * @throws JDOMException
	 */
	protected Document parseXMLData(String xmlString) throws IOException, JDOMException {

		ByteArrayInputStream xmlStream;
		byte[] bytes = xmlString.getBytes("UTF-8");// Maybe it is a too big assumption that UTF-8 is the default encoding for XML, but it
													// is definitely better to retrieve the bytes with a charset than without.
		xmlStream = new ByteArrayInputStream(bytes, 0, bytes.length);// xmlString.length() is not correct unless the charsets codes every
																		// character in a single byte.
		return parseXMLData(xmlStream);
	}

	/**
	 * Internally used durinf XML decoding process.<br>
	 * Parse xml data from an input stream <code>xmlStream</code> and returns a newly created XML document.
	 * 
	 * @param xmlStream
	 *            an <code>InputStream</code> value
	 * @return a <code>Document</code> value
	 * @exception SAXException
	 *                if an error occurs
	 * @exception ParserConfigurationException
	 *                if an error occurs
	 * @exception IOException
	 *                if an error occurs
	 * @throws JDOMException
	 */
	protected Document parseXMLData(InputStream xmlStream) throws IOException, JDOMException {
		SAXBuilder parser = new SAXBuilder();
		Document reply = parser.build(xmlStream);
		makeIndex(reply);
		return reply;
	}

	static private class ElementWithIDFilter implements Filter {

		public ElementWithIDFilter() {
			super();
		}

		@Override
		public boolean matches(Object arg0) {
			if (arg0 instanceof Element) {
				return (((Element) arg0).getAttributeValue("id")) != null;
			}
			return false;
		}

	}

	private Hashtable<String, Element> _index;

	public Document makeIndex(Document doc) {
		_index = new Hashtable<String, Element>();
		Iterator<Element> it = doc.getDescendants(new ElementWithIDFilter());
		Element e = null;
		while (it.hasNext()) {
			e = it.next();
			_index.put(e.getAttributeValue("id"), e);
		}
		return doc;
	}

	private Element findElementWithId(String id) {
		return _index.get(id);
	}

	/**
	 * Internally used during XML decoding process.<br>
	 * Build and returns newly created object from an XML document <code>dataDocument</code>.
	 * 
	 * @param dataDocument
	 *            a <code>Document</code> value
	 * @return an <code>XMLSerializable</code> value
	 * @exception InvalidXMLDataException
	 *                if an error occurs
	 * @exception InvalidObjectSpecificationException
	 *                if an error occurs
	 * @exception AccessorInvocationException
	 *                if an error occurs during accessor invocation
	 */
	protected XMLSerializable buildObjectFromDocument(Document dataDocument) throws InvalidXMLDataException,
			InvalidObjectSpecificationException, AccessorInvocationException {
		Element rootElement = dataDocument.getRootElement();

		XMLSerializable returnedObject = (XMLSerializable) buildObjectFromNode(rootElement);
		runDecodingFinalization(returnedObject);
		return returnedObject;
	}

	private void runDecodingFinalization(Object rootObject) throws AccessorInvocationException {
		// Debugging.debug("Run decoding finalization");
		Object[] params = { builder };
		for (Enumeration e = alreadyDeserialized.elements(); e.hasMoreElements();) {
			Object next = e.nextElement();
			if (next != rootObject) {
				// Gros truc degeulasse a modifier plus tard,
				// quand on aura repondu a la question:
				// pourquoi le root object est-il parfois dans les
				// alreadyDeserialized, parfois non
				runDecodingFinalizationForObject(next, params);
			}
		}
		runDecodingFinalizationForObject(rootObject, params);
		for (Enumeration e = alreadyDeserialized.keys(); e.hasMoreElements();) {
			Object next = e.nextElement();
			alreadyDeserialized.remove(next);
		}
	}

	private void runDecodingFinalizationForObject(Object next, Object[] params) throws AccessorInvocationException {
		ModelEntity entity = xmlMapping.entityForClass(next.getClass());
		if (entity != null) {
			try {
				boolean finalizationHasBeenPerformed = false;
				if (xmlMapping.hasBuilderClass()) {
					if (entity.hasFinalizerWithParameter()) {
						entity.getFinalizerWithParameter().invoke(next, params);
						finalizationHasBeenPerformed = true;
					}
				}
				if ((entity.hasFinalizerWithoutParameter() && (!finalizationHasBeenPerformed))) {
					entity.getFinalizerWithoutParameter().invoke(next, null);
					finalizationHasBeenPerformed = true;
				}
			} catch (IllegalAccessException e1) {
				e1.printStackTrace();
			} catch (InvocationTargetException e2) {
				e2.getTargetException().printStackTrace();
				throw new AccessorInvocationException("Exception " + e2.getClass().getName() + " caught during finalization.", e2);
			}
		}
	}

	/**
	 * Internally used during XML decoding process.<br>
	 * Build and returns newly created object from an XML element <code>node</code>.
	 * 
	 * @param node
	 *            an <code>Element</code> value
	 * @return an <code>XMLSerializable</code> value
	 * @exception InvalidXMLDataException
	 *                if an error occurs
	 * @exception InvalidObjectSpecificationException
	 *                if an error occurs
	 */
	protected Object buildObjectFromNode(Element node) throws InvalidXMLDataException, InvalidObjectSpecificationException {

		ModelEntity modelEntity;
		String elementName;

		// Debugging.debug ("Building object from node:"+node.toString());

		elementName = node.getName();
		modelEntity = xmlMapping.entityWithXMLTag(elementName);
		if (modelEntity != null) {
			return buildObjectFromNodeAndModelEntity(node, modelEntity);
		} // end of if ()
		else {
			ModelProperty modelProperty = xmlMapping.propertyWithXMLTag(elementName);
			if (modelProperty == null) {
				throw new InvalidXMLDataException("Tag '" + elementName + "' not found in model");
			} else {

				/*NodeList childNodesList = node.getChildNodes();
				String textValue = "";
				for (int i = 0; i < childNodesList.getLength(); i++) {
				    Node tempNode = childNodesList.item(i);
				    if (tempNode.getNodeType() == Node.TEXT_NODE) {
				        textValue += tempNode.getNodeValue();
				    }
				}*/
				String textValue = node.getText();

				if (modelProperty.isArray()) {
					ArrayKeyValueProperty keyValueProperty = (ArrayKeyValueProperty) modelProperty.getKeyValueProperty();
					return stringEncoder._decodeObject(textValue, keyValueProperty.getComponentType());
				}

				if (modelProperty.isVector()) {
					VectorKeyValueProperty keyValueProperty = (VectorKeyValueProperty) modelProperty.getKeyValueProperty();
					return stringEncoder._decodeObject(textValue, keyValueProperty.getContentType());
				}

				return textValue;
			}
		}
	}

	/**
	 * Internally used during XML decoding process.<br>
	 * Build and returns newly created object from an XML element <code>node</code> and model entity <code>modelEntity</code>.
	 * 
	 * @param node
	 *            an <code>Element</code> value
	 * @param modelEntity
	 *            a <code>ModelEntity</code> value
	 * @return an <code>XMLSerializable</code> value
	 * @exception InvalidXMLDataException
	 *                if an error occurs
	 * @exception InvalidObjectSpecificationException
	 *                if an error occurs
	 */
	protected Object buildObjectFromNodeAndModelEntity(Element node, ModelEntity modelEntity) throws InvalidXMLDataException,
			InvalidObjectSpecificationException {

		Class returnedObjectClass;
		XMLSerializable returnedObject;
		// Integer currentDeserializedReference = null;
		Object currentDeserializedReference = null;

		// This is the class name given by the model entity
		returnedObjectClass = modelEntity.getRelatedClass();

		if (xmlMapping.handlesReferences()) {
			Attribute idAttribute = node.getAttribute(XMLMapping.idLabel);
			Attribute idrefAttribute = node.getAttribute(XMLMapping.idrefLabel);
			// System.out.println ("buildObjectFromNodeAndModelEntity node
			// "+node.getNodeName()+" /
			// "+modelEntity.getName()+"/"+modelEntity.getXmlTags());
			if (idrefAttribute != null) {
				// This seems to be an already deserialized object
				Object reference;
				if (implementsCustomIdMappingScheme()) {
					reference = idrefAttribute.getValue();
				} else {
					reference = new Integer(StringEncoder.decodeAsInteger(idrefAttribute.getValue()));
				}
				// System.out.println ("Object with idref = "+reference);
				Object referenceObject = alreadyDeserialized.get(reference);
				if (referenceObject == null) {
					// Try to find this object elsewhere in the document
					// NOTE: This should never occur, except if the file was
					// manually edited, or
					// if the file was generated BEFORE development of ordered
					// properties feature

					// TODO: Throw here an error in future release but for backward compatibility we leave it for now.
					Element idRefElement = findElementWithId(node.getDocument(), idrefAttribute.getValue());
					if (xmlMapping.entityWithXMLTag(idRefElement.getName()) != modelEntity)
						System.err.println("SEVERE: Found a referencing object with a non-corresponding entity tag '"
								+ idRefElement.getName() + "' than the referred object" + node.getName());
					if (idRefElement != null) {
						return buildObjectFromNodeAndModelEntity(idRefElement, modelEntity);
					}
					throw new InvalidXMLDataException("No reference to object with identifier " + reference);
				} else {
					// No need to go further: i've got my object
					// Debugging.debug ("Stopping decoding: object found as a
					// reference "+reference+" "+referenceObject);
					return referenceObject;
				}
			}
			if (idAttribute != null) {
				if (implementsCustomIdMappingScheme()) {
					currentDeserializedReference = idAttribute.getValue();
				} else {
					currentDeserializedReference = new Integer(StringEncoder.decodeAsInteger(idAttribute.getValue()));
				}
				// System.out.println ("Object with id =
				// "+currentDeserializedReference);
				Object referenceObject = alreadyDeserialized.get(currentDeserializedReference);
				if (referenceObject != null) {
					// No need to go further: i've got my object
					// Debugging.debug ("Stopping decoding: object found as a
					// reference "+reference+" "+referenceObject);
					return referenceObject;
				}
			}
		} else {
			currentDeserializedReference = getNextReference();
		}

		if ((modelEntity.implementsGenericTypingKVProperty()) && (node.getAttribute(XMLMapping.genericTypingClassName) != null)) {
			// Generic typing implementation
			Attribute classNameAttribute = node.getAttribute(XMLMapping.genericTypingClassName);
			try {
				returnedObjectClass = Class.forName(classNameAttribute.getValue());
				returnedObject = instanciateMoreSpecializedObject(modelEntity, returnedObjectClass);
				if (!modelEntity.getRelatedClass().isAssignableFrom(returnedObjectClass)) {
					throw new InvalidXMLDataException("Class" + classNameAttribute.getValue() + " was found, but doesn't inherit from "
							+ modelEntity.getRelatedClass().getName());
				}
			} catch (ClassNotFoundException e) {
				// System.out.println("modelEntity="+modelEntity);
				// System.out.println("modelEntity.getParentEntity()="+modelEntity.getParentEntity());
				returnedObject = instanciateNewObject(modelEntity);
				KeyValueCoder.setValueForKey(returnedObject, classNameAttribute.getValue(), modelEntity.getGenericTypingKVProperty(),
						stringEncoder);
			}
		}

		else {
			// Or, may be the class name to instanciate is more specialized
			Attribute classNameAttribute = node.getAttribute(XMLMapping.classNameLabel);
			// System.out.println("classNameAttribute="+classNameAttribute);
			if (classNameAttribute != null) {
				try {
					returnedObjectClass = Class.forName(classNameAttribute.getValue());
					returnedObject = instanciateMoreSpecializedObject(modelEntity, returnedObjectClass);
				} catch (ClassNotFoundException e) {
					// System.out.println("node qui foire="+node);
					// throw new InvalidXMLDataException("Cannot find " + classNameAttribute.getValue() + " class.");
					System.err.println("Cannot find class " + classNameAttribute.getValue());
					returnedObject = instanciateNewObject(modelEntity);
				}
				if (!modelEntity.getRelatedClass().isAssignableFrom(returnedObjectClass)) {
					throw new InvalidXMLDataException("Class" + classNameAttribute.getValue() + " was found, but doesn't inherit from "
							+ modelEntity.getRelatedClass().getName());
				}
			} else {
				returnedObject = instanciateNewObject(modelEntity);
			}
		}

		if (currentDeserializedReference != null) {
			// Debugging.debug ("Registering object reference
			// "+currentDeserializedReference);
			// System.out.println ("Registering ref:
			// "+currentDeserializedReference+" object
			// "+returnedObject.getClass().getName());
			alreadyDeserialized.put(currentDeserializedReference, returnedObject);
		}

		for (Enumeration e = modelEntity.getModelProperties(); e.hasMoreElements();) {
			ModelProperty modelProperty = (ModelProperty) e.nextElement();
			setPropertyForObject(node, returnedObject, returnedObjectClass, modelProperty, modelEntity);
		}

		return returnedObject;
	}

	/**
	 * Internally used to instanciate new object from known constructors given the declared builder, if necessary
	 * 
	 * @param modelEntity
	 * @return
	 */
	protected XMLSerializable instanciateNewObject(ModelEntity modelEntity) {
		try {
			if (xmlMapping.hasBuilderClass() && (builder != null)) {
				Object[] params = { builder };
				if (modelEntity.hasConstructorWithParameter()) {
					return (XMLSerializable) modelEntity.getConstructorWithParameter().newInstance(params);
				} else if (modelEntity.hasConstructorWithoutParameter()) {
					return (XMLSerializable) modelEntity.getConstructorWithoutParameter().newInstance(null);
				} else {
					throw new InvalidObjectSpecificationException("Class " + modelEntity.getName()
							+ " is not instanciable because no constructor with builder is declared.");
				}
			} else {
				if (modelEntity.hasConstructorWithoutParameter()) {
					return (XMLSerializable) modelEntity.getConstructorWithoutParameter().newInstance(null);
				} else {
					throw new InvalidObjectSpecificationException("Class " + modelEntity.getName()
							+ " is not instanciable because no constructor without parameter is declared.");
				}
			}
		} catch (InvocationTargetException e) {
			e.getTargetException().printStackTrace();
			throw new AccessorInvocationException("Class " + modelEntity.getName()
					+ " is not instanciable because an exception raised in object constructor.", e);
		} catch (InstantiationException e) {
			throw new InvalidObjectSpecificationException("Class " + modelEntity.getName() + " is not instanciable.");
		} catch (IllegalAccessException e) {
			throw new InvalidObjectSpecificationException("Class " + modelEntity.getName() + " is not accessible.");
		} catch (ExceptionInInitializerError e) {
			throw new InvalidObjectSpecificationException("Class " + modelEntity.getName() + ": initialization failed");
		} catch (SecurityException e) {
			throw new InvalidObjectSpecificationException("Class " + modelEntity.getName() + ": security exception.");
		}
	}

	/**
	 * Internally used to instanciate new object which is more specialized than the one defined in the model. In this case, known
	 * constructors are not usable !
	 * 
	 * @param modelEntity
	 * @return
	 */
	private XMLSerializable instanciateMoreSpecializedObject(ModelEntity modelEntity, Class returnedObjectClass) {
		Constructor constructorWithoutParameter = null;
		Constructor constructorWithParameter = null;

		try {
			constructorWithoutParameter = returnedObjectClass.getConstructor(null);
		} catch (NoSuchMethodException e) {
			// Ignore for now
		}

		if (modelEntity.getModel().hasBuilderClass()) {
			try {
				Class[] builderParams = { modelEntity.getModel().builderClass() };
				constructorWithParameter = returnedObjectClass.getConstructor(builderParams);
			} catch (NoSuchMethodException e) {
				// Ignore for now
			}
		}

		if ((constructorWithoutParameter == null) && (constructorWithParameter == null)) {
			throw new InvalidObjectSpecificationException("Class " + modelEntity.getName()
					+ " is not instanciable because no constructor with or without builder is declared.");
		}

		Object returned = null;

		try {
			if ((modelEntity.getModel().hasBuilderClass()) && (constructorWithParameter != null)) {
				Object[] params = { builder };
				returned = constructorWithParameter.newInstance(params);
				return (XMLSerializable) returned;
			} else {
				returned = constructorWithoutParameter.newInstance(null);
				return (XMLSerializable) returned;
			}
		} catch (InvocationTargetException e) {
			throw new AccessorInvocationException("Class " + modelEntity.getName()
					+ " is not instanciable because an exception raised in object constructor.", e);
		} catch (InstantiationException e) {
			throw new InvalidObjectSpecificationException("Class " + modelEntity.getName() + " is not instanciable.");
		} catch (ClassCastException e) {
			e.printStackTrace();
			throw new InvalidObjectSpecificationException("Class " + modelEntity.getName() + " is not XMLSerializable.");
		} catch (IllegalAccessException e) {
			throw new InvalidObjectSpecificationException("Class " + modelEntity.getName() + " is not accessible.");
		} catch (ExceptionInInitializerError e) {
			throw new InvalidObjectSpecificationException("Class " + modelEntity.getName() + ": initialization failed");
		} catch (SecurityException e) {
			throw new InvalidObjectSpecificationException("Class " + modelEntity.getName() + ": security exception.");
		}
	}

	/**
	 * Internally used during XML decoding process.<br>
	 * Build and returns newly created object matching the key of current stored object (required for hashtable-like data structures). The
	 * key is extracted from the first 'key' element found.
	 * 
	 * @param node
	 *            an <code>Element</code> value
	 * @param modelEntity
	 *            a <code>ModelEntity</code> value
	 * @return an <code>Object</code> value
	 * @exception InvalidXMLDataException
	 *                if an error occurs
	 * @exception InvalidObjectSpecificationException
	 *                if an error occurs
	 */
	protected Object buildKeyFromNode(Element node) throws InvalidXMLDataException, InvalidObjectSpecificationException {

		List matchingElements;

		// Trying to find an attribute (it may be a string object)
		if (node.getAttribute(XMLMapping.keyLabel) != null) {
			return node.getAttributeValue(XMLMapping.keyLabel);
		}

		// I don't think so
		matchingElements = node.getChildren(XMLMapping.keyLabel);
		if (matchingElements.size() >= 1) {
			return buildKeyFromKeyNode((Element) matchingElements.get(0));
		}

		// I've found nothing
		throw new InvalidXMLDataException("No key found for storing object in a hastable-like data structure: looking for key in " + node);

	}

	/**
	 * Internally used during XML decoding process.<br>
	 * Build and returns newly created object matching the specified node, asserting <code>node</code> is the key node.<br>
	 * Note that if some attributes are here defined, the first one will be interpreted as a String and returned as the key (eventual other
	 * child nodes will be ignored) else the first child node will be interpreted as the key.
	 * 
	 * @param node
	 *            an <code>Element</code> value
	 * @param modelEntity
	 *            a <code>ModelEntity</code> value
	 * @return an <code>Object</code> value
	 * @exception InvalidXMLDataException
	 *                if an error occurs
	 * @exception InvalidObjectSpecificationException
	 *                if an error occurs
	 */
	protected Object buildKeyFromKeyNode(Element node) throws InvalidXMLDataException, InvalidObjectSpecificationException {

		List childNodesList = node.getContent();
		List attributes = node.getAttributes();

		if (attributes.size() > 0) {
			// the first one will be interpreted as a String and returned as
			// the key (eventual other child nodes will be ignored)
			return ((Attribute) attributes.get(0)).getValue();
		}

		else if (childNodesList.size() > 0) {

			int i = 0;

			while (i < childNodesList.size()) {

				Content tempNode = (Content) childNodesList.get(i);

				if (tempNode instanceof Text) {
					String potentialValue = ((Text) tempNode).getTextTrim();
					if (potentialValue.length() > 0) {
						return potentialValue;
					}
				} else if (tempNode instanceof Element) {
					Element element = (Element) tempNode;
					ModelEntity relatedModelEntity = xmlMapping.entityWithXMLTag(element.getName());
					if (relatedModelEntity == null) {
						throw new InvalidXMLDataException("Tag '" + element.getName() + "' not found in model");
					} else {
						return buildObjectFromNodeAndModelEntity(element, relatedModelEntity);
					}
				}
				i++;
			}
		}

		throw new InvalidXMLDataException("No key found for storing object in a hastable-like data structure");

	}

	/**
	 * Internally used during XML decoding process.<br>
	 * Sets property <code>modelProperty</code> for object <code>object</code> according to node <code>node</code> and class
	 * <code>objectClass</code>.
	 * 
	 * @param node
	 *            an <code>Element</code> value
	 * @param object
	 *            an <code>Object</code> value
	 * @param objectClass
	 *            a <code>Class</code> value
	 * @param modelProperty
	 *            a <code>ModelProperty</code> value
	 * @exception InvalidXMLDataException
	 *                if an error occurs
	 * @exception InvalidObjectSpecificationException
	 *                if an error occurs
	 */
	protected void setPropertyForObject(Element node, Object object, Class objectClass, ModelProperty modelProperty, ModelEntity modelEntity)
			throws InvalidXMLDataException, InvalidObjectSpecificationException {

		KeyValueProperty keyValueProperty;

		// First, get the key-value property
		keyValueProperty = modelProperty.getKeyValueProperty();

		if (keyValueProperty instanceof SingleKeyValueProperty) {

			List childNodesList = node.getContent();
			Content tempNode;

			SingleKeyValueProperty singleKeyValueProperty = (SingleKeyValueProperty) keyValueProperty;

			boolean primitiveProperty = singleKeyValueProperty.classIsPrimitive(stringEncoder);

			// Then, check if property is a text property attribute

			if (modelProperty.getIsText()) {
				StringBuffer textValue = new StringBuffer();
				for (int i = 0; i < childNodesList.size(); i++) {
					tempNode = (Content) childNodesList.get(i);
					if (tempNode instanceof Text) {
						textValue.append(((Text) tempNode).getTextTrim());
					}
				}
				KeyValueCoder.setValueForKey(object, textValue.toString(), singleKeyValueProperty, stringEncoder);
			}

			// Check if property is an attribute

			else if (modelProperty.getIsAttribute()) {
				Attribute attribute = node.getAttribute(modelProperty.getDefaultXmlTag());
				if (attribute != null) {
					// System.out.println ("Property "+modelProperty.getName()+"
					// is not null and match attribute
					// "+modelProperty.getDefaultXmlTag()+"
					// value="+attribute.getNodeValue());
					KeyValueCoder.setValueForKey(object, attribute.getValue(), singleKeyValueProperty, stringEncoder);
				}
			}

			else { // Property seem to match an element

				Element element;

				Iterator matchingElements = elementsMatchingHandledXMLTags(node, modelProperty);

				if (matchingElements.hasNext()) {
					element = (Element) matchingElements.next();
					if (primitiveProperty) {
						List childNodes = element.getContent();
						if (childNodes.size() == 0) {
							// Nothing to do
						} else if (childNodes.size() >= 1) {
							if ((childNodes.get(0)) instanceof Text) {
								KeyValueCoder.setValueForKey(object, ((Text) childNodes.get(0)).getTextTrim(), singleKeyValueProperty,
										stringEncoder);
							} else {
								throw new InvalidXMLDataException("Tag '" + modelProperty.getName()
										+ "' is not well-formed according to model file");
							}
						} else {
							throw new InvalidXMLDataException("Tag '" + modelProperty.getName()
									+ "' is not well-formed according to model file (more than one child node)");
						}

					} else {
						KeyValueCoder.setObjectForKey(object, buildObjectFromNode(element), keyValueProperty);
					} // end of else
				}

				else {
					// No XML tag matching this property, ignoring
					// Nothing to do
				}
			}
		}

		else if (keyValueProperty instanceof ArrayKeyValueProperty) {

			KeyValueCoder.setArrayForKey(object, vectorOfObjectsMatchingHandledXMLTags(node, modelProperty),
					(ArrayKeyValueProperty) keyValueProperty);

		}

		else if (keyValueProperty instanceof VectorKeyValueProperty) {

			Vector myVector = vectorOfObjectsMatchingHandledXMLTags(node, modelProperty);
			// System.out.println ("modelProperty="+modelProperty);
			// System.out.println ("node="+node);
			// System.out.println ("vector="+myVector);
			KeyValueCoder.setVectorForKey(object, myVector, (VectorKeyValueProperty) keyValueProperty);
		}

		else if (keyValueProperty instanceof PropertiesKeyValueProperty) {

			if (modelProperty.isProperties()) {
				KeyValueCoder.setHashtableForKey(object, propertiesHashtableOfObjectsMatchingHandledXMLTags(node, modelProperty),
						(HashtableKeyValueProperty) keyValueProperty);
			} else if (modelProperty.isUnmappedAttributes()) {
				Vector unmappedAttributes = unmappedAttributes(node, modelProperty, modelEntity);
				if (unmappedAttributes.size() > 0) {
					// System.out.println("Found unmapped attribute(s) : "+unmappedAttributes);
					HashtableKeyValueProperty kvProperty = (HashtableKeyValueProperty) keyValueProperty;
					for (Enumeration en = unmappedAttributes.elements(); en.hasMoreElements();) {
						Attribute att = (Attribute) en.nextElement();
						kvProperty.setObjectValueForKey(att.getValue(), att.getName(), object);
					}
				}
			}
		}

		else if (keyValueProperty instanceof HashtableKeyValueProperty) {

			KeyValueCoder.setHashtableForKey(object, hashtableOfObjectsMatchingHandledXMLTags(node, modelProperty),
					(HashtableKeyValueProperty) keyValueProperty);

		}

		else {

			throw new InvalidXMLDataException("Unexpected KeyValueProperty. Please send a bug report.");
		}

	}

	protected Vector unmappedAttributes(Element node, ModelProperty modelProperty, ModelEntity modelEntity) {
		Vector returned = new Vector();
		Iterator it = node.getAttributes().iterator();
		while (it.hasNext()) {
			Attribute att = (Attribute) it.next();
			if (attributeNameMatchesWildcard(att.getName(), modelProperty.getDefaultXmlTag())) {
				boolean isMapped = false;
				ModelEntity currentEntity = modelEntity;
				while (!isMapped && currentEntity != null) {
					for (Enumeration e = currentEntity.getModelProperties(); e.hasMoreElements();) {
						ModelProperty temp = (ModelProperty) e.nextElement();
						if (temp != modelProperty) {
							// System.out.println("Comparing "+temp.getDefaultXmlTag()+" with "+att.getName());
							if (temp.hasXmlTag() && temp.getDefaultXmlTag().equals(att.getName())) {
								isMapped = true;
								break;
							}
						}
					}
					if (!isMapped)
						currentEntity = currentEntity.getParentEntity();
				}
				if (!isMapped)
					returned.add(att);
			}
		}
		return returned;
	}

	private boolean attributeNameMatchesWildcard(String attributeName, String wildcard) {
		// TODO: We might implement this later interpreting wildcard
		return (!attributeName.equals("id"));
	}

	/*
	protected Vector elementsMatchingHandledXMLTags(Element node, ModelProperty modelProperty)
	{

	    String[] tagNames = modelProperty.getXmlTags();

	    Vector matchingElements = new Vector();
	    for (int i = 0; i < tagNames.length; i++) {
	        List childElements = node.getChildren();
	        for (int j = 0; j < childElements.size(); j++) {
	            Element element = (Element) childElements.get(j);
	            for (int k = 0; k < tagNames.length; k++) {
	                if (element.getName().equals(tagNames[k])) {
	                    if (!matchingElements.contains(element)) {
	                        matchingElements.add(element);
	                    }
	                }
	            }
	        }
	    }
	    return matchingElements;
	}
	*/
	private Iterator elementsMatchingHandledXMLTags(Element node, ModelProperty modelProperty) {
		return node.getContent(new MultipleNameFilter(modelProperty.getXmlTags(), node)).iterator();
	}

	private class MultipleNameFilter implements Filter {
		private String[] _tags;
		private Element _parent;

		MultipleNameFilter(String[] tags, Element node) {
			super();
			_tags = tags;
			_parent = node;
		}

		@Override
		public boolean matches(Object arg0) {
			if (!(arg0 instanceof Element))
				return false;
			if (_parent.equals(((Element) arg0).getParentElement())) {
				for (int i = 0; i < _tags.length; i++) {
					if ((((Element) arg0).getName()).equals(_tags[i]))
						return true;
				}
			}
			return false;
		}
	}

	/**
	 * Return a vector of {@link XMLSerializable} objects, decoded from elements matching declared handled XML tag of specified
	 * <code>modelProperty</code>
	 */
	protected Vector vectorOfObjectsMatchingHandledXMLTags(Element node, ModelProperty modelProperty) {

		Vector returnedVector = new Vector();

		// Vector matchingElements = elementsMatchingHandledXMLTags(node, modelProperty);
		Iterator it = elementsMatchingHandledXMLTags(node, modelProperty);
		while (it.hasNext()) {
			returnedVector.addElement(buildObjectFromNode((Element) it.next()));
		}

		return returnedVector;
	}

	/**
	 * Return a hashtable of {@link XMLSerializable} objects, decoded from elements matching declared handled XML tag of specified
	 * <code>modelProperty</code>
	 */
	protected Hashtable hashtableOfObjectsMatchingHandledXMLTags(Element node, ModelProperty modelProperty) {

		Hashtable returnedHashtable = new Hashtable();

		Iterator matchingElements = elementsMatchingHandledXMLTags(node, modelProperty);

		while (matchingElements.hasNext()) {
			Element element = (Element) matchingElements.next();
			Object key;
			Object object = buildObjectFromNode(element);
			if (modelProperty.getKeyToUse() == null) {
				key = buildKeyFromNode(element);
			} else {
				key = KeyValueDecoder.objectForKey(object, modelProperty.getKeyToUse());
			}
			returnedHashtable.put(key, object);
		}

		return returnedHashtable;
	}

	/**
	 * Return a properties hashtable decoded from elements matching declared handled XML tag of specified <code>modelProperty</code>
	 */
	protected Hashtable propertiesHashtableOfObjectsMatchingHandledXMLTags(Element node, ModelProperty modelProperty) {

		Hashtable returnedHashtable = new Hashtable();

		List propertiesNodeList = (node.getChildren());
		Element propertiesNode;
		if (propertiesNodeList.size() == 0) {
			// throw new InvalidXMLDataException ("Properties tag
			// '"+modelProperty.getName()+"' not found");
			return new Hashtable(); // Returns an empty hashtable
		} else {
			int i = 0;
			do {
				propertiesNode = (Element) propertiesNodeList.get(i);
				i++;
			} while ((!propertiesNode.getName().equals(modelProperty.getDefaultXmlTag())) && (i < propertiesNodeList.size()));
		}
		if (!propertiesNode.getName().equals(modelProperty.getDefaultXmlTag())) {
			// throw new InvalidXMLDataException ("Properties tag
			// '"+modelProperty.getDefaultXmlTag()+"' not found");
			return new Hashtable(); // Returns an empty hashtable
		}

		List childElements = propertiesNode.getChildren();
		for (int j = 0; j < childElements.size(); j++) {
			Element element = (Element) childElements.get(j);
			Attribute classNameAttr = element.getAttribute(XMLMapping.classNameLabel);
			Class objectType = null;
			if (classNameAttr != null) {
				try {
					objectType = Class.forName(classNameAttr.getValue());
				} catch (ClassNotFoundException e) {
					System.err.println("Class not found " + classNameAttr.getValue() + " for " + element.getName());
					// throw new InvalidXMLDataException("Class named '" + classNameAttr.getValue() + "' not found");
				}
				String value = element.getTextTrim();
				if (objectType == null || stringEncoder._converterForClass(objectType) == null) {
					// No converter, just keep reference of that object for persistance
					// (without instanciating it)
					// In this case, class matching property is not loaded, and thus
					// Object is not instanciated. But, we must keep serialized version
					returnedHashtable.put(element.getName(), new PropertiesKeyValueProperty.UndecodableProperty(classNameAttr.getValue(),
							value));
				} else {
					Object decodedValue = stringEncoder._decodeObject(value, objectType);
					if (decodedValue == null) {
						System.err.println("Value for " + value + " is null !");
					} else {
						returnedHashtable.put(element.getName(), decodedValue);
					}
				}
			}
		}

		return returnedHashtable;
	}

	private Integer getNextReference() {
		nextReference++;
		return new Integer(nextReference);
	}

	private Element findElementWithId(Document document, String idRef) {
		if (idRef == null)
			return null;
		return findElementWithId(idRef);
		/*Iterator it = document.getDescendants(new IDFilter(idRef));
		try{
			return (Element)it.next();
		}catch(NoSuchElementException e){
			return null;
		}*/
		// return (Element)document.getDescendants(new IDFilter(idRef)).next();
		// Element returned = findElementWithId(document.getRootElement(), idRef);
		// return returned;
	}

	private Element findElementWithId(Element element, String idRef) {
		Element returnedElement = null;
		String id = element.getAttributeValue(XMLMapping.idLabel);
		if (id != null) {
			if (id.equals(idRef)) {
				return element;
			}
		}
		List childs = element.getChildren();
		for (int i = 0; i < childs.size(); i++) {
			Element next = (Element) childs.get(i);
			returnedElement = findElementWithId(next, idRef);
			if (returnedElement != null) {
				return returnedElement;
			}
		}
		return returnedElement;
	}

	public boolean implementsCustomIdMappingScheme() {
		return xmlMapping.implementsCustomIdMappingScheme();
	}

	private class IDFilter implements Filter {

		private String _searchedID;

		public IDFilter(String searchedID) {
			super();
			_searchedID = searchedID;
		}

		@Override
		public boolean matches(Object arg0) {
			if (arg0 instanceof Element) {
				return _searchedID.equals(((Element) arg0).getAttributeValue(XMLMapping.idLabel));
			}
			return false;
		}

	}

}
