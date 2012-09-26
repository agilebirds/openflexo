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
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * <p>
 * <code>XMLMapping</code> class represent a mapping that will be used for xml coding/decoding process.
 * </p>
 * Instance of this class will be instanciated from an XML file called a <i>model file</i> which contains all informations to map data from
 * xml data to object (xml decoding) and from object to xml data (xml coding).<br>
 * Suppose that you have following XML data:
 * 
 * <pre>
 *  &lt;?xml version=&quot;1.0&quot; encoding=&quot;UTF-8&quot;?&gt;
 *  &lt;command quantity=&quot;3&quot; commandIsAlreadyPaid=&quot;yes&quot;&gt; 
 *    &lt;customer&gt;
 *      &lt;name&gt;Ringle&lt;/name&gt;
 *    &lt;/customer&gt; 
 *    &lt;movie name=&quot;Alien&quot;&gt;
 *      here comes a description of the movie
 *      &lt;date&gt;2001.09.18 AD at 09:54:58 AM CEST&lt;/date&gt; 
 *      &lt;cat&gt;Horror&lt;/cat&gt; 
 *      &lt;role name=&quot;Brett&quot;&gt;&lt;/role&gt; 
 *      &lt;role name=&quot;Kane&quot;&gt;&lt;/role&gt;
 *      &lt;role name=&quot;Dallas&quot;&gt;&lt;/role&gt; 
 *      &lt;role name=&quot;Parker&quot;&gt;&lt;/role&gt; 
 *    &lt;/movie&gt; 
 *  &lt;/command&gt;
 * </pre>
 * 
 * and that you will map it to following <code>objects</code>:
 * 
 * <pre>
 * public class Command {
 * 	public int qty;
 * 
 * 	public boolean commandIsAlreadyPaid;
 * 
 * 	public Movie movie;
 * 
 * 	public Customer customer;
 * }
 * 
 * public class Movie {
 * 	public String title;
 * 
 * 	public String description;
 * 
 * 	public Date dateReleased;
 * 
 * 	public Vector roles;
 * 
 * 	public String category;
 * }
 * 
 * public class Customer {
 * 	public String name;
 * }
 * 
 * public class Role {
 * 	public String roleName;
 * }
 * </pre>
 * 
 * you will define a <i>model file</i> defining this mapping such as:
 * 
 * <pre>
 *    &lt;?xml version=&quot;1.0&quot; encoding=&quot;UTF-8&quot;?&gt;
 *    &lt;model&gt; 
 *      &lt;entity name=&quot;yourpackage.Command&quot; xmlTag=&quot;command&quot;&gt; 
 *        &lt;property name=&quot;qty&quot; xmlTag=&quot;quantity&quot; attribute=&quot;YES&quot;/&gt; 
 *        &lt;property name=&quot;commandIsAlreadyPaid&quot; xmlTag=&quot;commandIsAlreadyPaid&quot; attribute=&quot;YES&quot;/&gt; 
 *        &lt;property name=&quot;movie&quot; xmlTag=&quot;movie&quot;/&gt; 
 *        &lt;property name=&quot;customer&quot; xmlTag=&quot;customer&quot;/&gt; 
 *      &lt;/entity&gt; 
 *      &lt;entity name=&quot;yourpackage.Customer&quot; xmlTag=&quot;customer&quot;&gt; 
 *        &lt;property name=&quot;name&quot; xmlTag=&quot;name&quot;/&gt; 
 *      &lt;/entity&gt; 
 *      &lt;entity name=&quot;yourpackage.Movie&quot; xmlTag=&quot;movie&quot;&gt; 
 *        &lt;property name=&quot;title&quot; xmlTag=&quot;name&quot; attribute=&quot;YES&quot;/&gt; 
 *        &lt;property name=&quot;description&quot; text=&quot;YES&quot;/&gt;
 *        &lt;property name=&quot;dateReleased&quot; xmlTag=&quot;date&quot;/&gt; 
 *        &lt;property name=&quot;roles&quot; xmlTag=&quot;role&quot; forceList=&quot;YES&quot;/&gt; 
 *        &lt;property name=&quot;category&quot; xmlTag=&quot;cat&quot;/&gt; 
 *      &lt;/entity&gt; 
 *      &lt;entity name=&quot;yourpackage.Role&quot; xmlTag=&quot;role&quot;&gt; 
 *        &lt;property name=&quot;roleName&quot; xmlTag=&quot;name&quot; attribute=&quot;YES&quot;/&gt; 
 *      &lt;/entity&gt; 
 *    &lt;/model&gt;
 * </pre>
 * 
 * All valid tags are used in this example model file.<br>
 * NB1: Only one of those three tags <code>text</code> <code>forceList</code> and <code>attribute</code> could be set to true (which is
 * normal).<br>
 * NB2: All valid types are all the java primitives (<code>int</code>, <code>long</code>, <code>short</code>, <code>double</code>,
 * <code>float</code>, <code>boolean</code>, <code>byte</code>, <code>char</code>) and {@link java.util.Date} and {@link java.lang.String}
 * objects.<br>
 * NB3: Tag <code>xmlTag</code> could be ommited if and only if <code>text</code> is specified and set to <code>true</code> (
 * <code>YES</code> value).
 * 
 * @author <a href="mailto:Sylvain.Guerin@enst-bretagne.fr">Sylvain Guerin</a>
 * @see XMLCoder
 * @see XMLDecoder
 */
public class XMLMapping {

	/** ModelEntity objects stored in a dictionary where the key is the xml tag */
	protected Map<String, ModelEntity> modelEntitiesStoredByXMLTag;

	/**
	 * ModelEntity objects stored in a dictionary where the key is the class name
	 */
	protected Map<String, ModelEntity> modelEntitiesStoredByClassName;

	/** Boolean indicating if references must be handled during encoding */
	protected boolean handlesReferences;

	/** Serialization mode */
	protected int serializationMode = DEFAULT_SERIALIZATION_MODE;

	/** Stores custom id mapping */
	protected XMLMapId mapId = null;

	/**
	 * Boolean indicating if this mapping will be used only for encoding (does not require set methods and valid constructors )
	 */
	protected boolean serializeOnly;

	/** Builder class defined for this model, if any */
	protected Class<?> builderClass;

	/** Description of this model */
	protected String description;

	/** Defines 'model' label */
	public static final String modelLabel = "model";

	/** Defines 'entity' label */
	public static final String entityLabel = "entity";

	/** Defines 'mapId' label */
	public static final String mapIdLabel = "mapId";

	/** Defines 'map' label */
	public static final String mapLabel = "map";

	/** Defines 'entityClass' label */
	public static final String entityClassLabel = "entityClass";

	/** Defines 'identifierAccessor' label */
	public static final String identifierAccessorLabel = "identifierAccessor";

	/** Defines 'name' label */
	public static final String nameLabel = "name";

	/** Defines 'xmlTag' label */
	public static final String xmlTagLabel = "xmlTag";

	/** Defines 'property' label */
	public static final String propertyLabel = "property";

	/** Defines 'attribute' label */
	public static final String attributeLabel = "attribute";

	/** Defines 'abstract' label */
	public static final String abstractLabel = "abstract";

	/** Defines 'type' label */
	public static final String typeLabel = "type";

	/** Defines 'single' label */
	public static final String singleLabel = "single";

	/** Defines 'array' label */
	public static final String arrayLabel = "array";

	/** Defines 'vector' label */
	public static final String vectorLabel = "vector";

	/** Defines 'hashtable' label */
	public static final String hashtableLabel = "hashtable";

	/** Defines 'className' label */
	public static final String classNameLabel = "className";

	/** Defines 'key' label */
	public static final String keyLabel = "key";

	/** Defines 'text' label */
	public static final String textLabel = "text";

	/** Defines 'id' label */
	public static final String idLabel = "id";

	/** Defines 'idref' label */
	public static final String idrefLabel = "idref";

	/** Defines 'ignoreDefaultValue' label */
	public static final String ignoreDefaultValueLabel = "ignoreDefaultValue";

	/** Defines 'handlesReferences' label */
	public static final String handlesReferencesLabel = "handlesReferences";

	/** Defines 'serializationMode' label */
	public static final String serializationModeLabel = "serializationMode";

	/** Defines 'builder' label */
	public static final String builderLabel = "builder";

	/** Defines 'properties' label */
	public static final String propertiesLabel = "properties";

	/** Defines 'unmappedAttributes' label */
	public static final String unmappedAttributesLabel = "unmappedAttributes";

	/** Defines 'finalizer' label */
	public static final String finalizerLabel = "finalizer";

	/** Defines 'contexts' label */
	public static final String contextsLabel = "contexts";

	/** Defines 'context' label */
	public static final String contextLabel = "context";

	/** Defines 'contains' label */
	public static final String containsLabel = "contains";

	/** Defines 'serializeOnly' label */
	public static final String serializeOnlyLabel = "serializeOnly";

	/** Defines 'description' label */
	public static final String descriptionLabel = "description";

	/** Defines 'primary' label */
	public static final String primaryLabel = "primary";

	/** Defines 'cloneable' label */
	public static final String cloneableLabel = "cloneable";

	/** Defines 'copyable' label */
	public static final String copyableLabel = "copyable";

	/** Defines 'deepFirst' label */
	public static final String deepFirstLabel = "deepFirst";

	/** Defines 'pseudoTree' label */
	public static final String pseudoTreeLabel = "pseudoTree";

	/** Defines 'structureThenValues' label */
	public static final String orderedPseudoTreeLabel = "orderedPseudoTree";

	/** Defines 'default' label */
	public static final String DefaultLabel = "default";

	/** Defines 'genericTypingStoredIn' label */
	public static final String genericTypingStoredIn = "genericTypingStoredIn";

	/** Defines 'genericTypingClassName' label */
	public static final String genericTypingClassName = "genericTypingClassName";

	/** Defines 'deepFirst' serialization mode */
	public static final int DEEP_FIRST = 0;

	/** Defines 'pseudoTree' serialization mode */
	public static final int PSEUDO_TREE = 1;

	/** Defines 'structureThenValues' serialization mode */
	public static final int ORDERED_PSEUDO_TREE = 2;

	/** Defines default serialization mode */
	public static final int DEFAULT_SERIALIZATION_MODE = DEEP_FIRST;

	/**
	 * Creates a new <code>XMLMapping</code> instance<br>
	 * This constructor should be called for dynamically XMLMapping building purposes.<br>
	 * Use {@link #registerNewModelEntity(ModelEntity)} to register new <code>ModelEntity</code> objects.
	 * 
	 */
	public XMLMapping() {

		super();
		modelEntitiesStoredByXMLTag = new Hashtable<String, ModelEntity>();
		modelEntitiesStoredByClassName = new Hashtable<String, ModelEntity>();
		mapId = null;
	}

	/**
	 * Register new <code>ModelEntity</code> object in mapping<br>
	 * This method MUST be use to dynamically handle <code>XMLMapping</code> instances.
	 * 
	 * @exception InvalidModelException
	 *                if an error occurs during mapping construction (invalid model)
	 */
	public void registerNewModelEntity(ModelEntity aModelEntity) {

		System.out.println("registerNewModelEntity " + aModelEntity);
		updateAndRegisterNewModelEntity(aModelEntity);
	}

	/**
	 * Creates a new <code>XMLMapping</code> instance given a <code>File</code> object
	 * 
	 * @param modelFile
	 *            a <code>File</code> value
	 * @exception IOException
	 *                if an I/O error occurs (file not found, etc...)
	 * @exception SAXException
	 *                if parse error occurs
	 * @exception ParserConfigurationException
	 *                if a parser configuration error occurs
	 * @exception InvalidModelException
	 *                if an error occurs during mapping construction (invalid model)
	 */
	public XMLMapping(File modelFile) throws IOException, SAXException, ParserConfigurationException, InvalidModelException {
		super();
		initWithModelFile(modelFile);
	}

	/**
	 * Creates a new <code>XMLMapping</code> instance given a <code>InputStream</code> object
	 * 
	 * @param modelInputStream
	 *            a <code>InputStream</code> value
	 * @exception IOException
	 *                if an I/O error occurs (file not found, etc...)
	 * @exception SAXException
	 *                if parse error occurs
	 * @exception ParserConfigurationException
	 *                if a parser configuration error occurs
	 * @exception InvalidModelException
	 *                if an error occurs during mapping construction (invalid model)
	 */
	public XMLMapping(InputStream modelInputStream) throws IOException, SAXException, ParserConfigurationException, InvalidModelException {
		super();
		initWithModelInputStream(modelInputStream);
	}

	/**
	 * Initialize a <code>XMLMapping</code> object given a <code>File</code> object
	 * 
	 * @param modelFile
	 *            a <code>File</code> value
	 * @exception IOException
	 *                if an I/O error occurs (file not found, etc...)
	 * @exception SAXException
	 *                if parse error occurs
	 * @exception ParserConfigurationException
	 *                if a parser configuration error occurs
	 * @exception InvalidModelException
	 *                if an error occurs during mapping construction (invalid model)
	 */
	protected void initWithModelFile(File modelFile) throws IOException, SAXException, ParserConfigurationException, InvalidModelException {
		FileInputStream in = new FileInputStream(modelFile);
		try {
			initWithModelInputStream(in);
		} finally {
			in.close();
		}
	}

	/**
	 * Initialize a <code>XMLMapping</code> object given a <code>modelInputStream</code> object
	 * 
	 * @param modelInputStream
	 *            a <code>InputStream</code> value
	 * @exception IOException
	 *                if an I/O error occurs (file not found, etc...)
	 * @exception SAXException
	 *                if parse error occurs
	 * @exception ParserConfigurationException
	 *                if a parser configuration error occurs
	 * @exception InvalidModelException
	 *                if an error occurs during mapping construction (invalid model)
	 */
	protected void initWithModelInputStream(InputStream modelInputStream) throws IOException, SAXException, ParserConfigurationException,
			InvalidModelException {
		Document modelDocument = null;
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

		try {
			// ByteArrayInputStream Stream=new
			// ByteArrayInputStream(buf,0,length);

			DocumentBuilder builder = factory.newDocumentBuilder();
			modelDocument = builder.parse(modelInputStream);
			initWithModelDocument(modelDocument);
		} catch (SAXException e) {
			throw e;
		} catch (ParserConfigurationException e) {
			throw e;
		} catch (IOException e) {
			throw e;
		}
	}

	/**
	 * Initialize a <code>XMLMapping</code> object given a <code>Document</code> object representing the model file.
	 * 
	 * @param modelDocument
	 *            a <code>Document</code> value
	 * @exception InvalidModelException
	 *                if an error occurs during mapping construction (invalid model)
	 */
	protected void initWithModelDocument(Document modelDocument) throws InvalidModelException {

		NodeList modelNodeList;
		Node modelNode;
		NodeList entitiesNodeList;
		Node tempNode;

		modelNodeList = modelDocument.getElementsByTagName(modelLabel);

		if (modelNodeList.getLength() == 0) {
			throw new InvalidModelException("No tag 'model' found in model file");
		} else if (modelNodeList.getLength() > 1) {
			throw new InvalidModelException("More than one tag 'model' found in model file");
		}

		modelNode = modelNodeList.item(0);
		if (modelNode.hasAttributes()) {
			NamedNodeMap attributes = modelNode.getAttributes();
			for (int i = 0; i < attributes.getLength(); i++) {
				Node tempAttribute = attributes.item(i);
				if (tempAttribute.getNodeName().equals(XMLMapping.handlesReferencesLabel)) {
					handlesReferences = tempAttribute.getNodeValue().equalsIgnoreCase("yes");
				} else if (tempAttribute.getNodeName().equals(XMLMapping.serializationModeLabel)) {
					if (tempAttribute.getNodeValue().equalsIgnoreCase(deepFirstLabel)) {
						serializationMode = DEEP_FIRST;
					} else if (tempAttribute.getNodeValue().equalsIgnoreCase(pseudoTreeLabel)) {
						serializationMode = PSEUDO_TREE;
					} else if (tempAttribute.getNodeValue().equalsIgnoreCase(orderedPseudoTreeLabel)) {
						serializationMode = ORDERED_PSEUDO_TREE;
					} else if (tempAttribute.getNodeValue().equalsIgnoreCase(DefaultLabel)) {
						serializationMode = DEFAULT_SERIALIZATION_MODE;
					}
				} else if (tempAttribute.getNodeName().equals(XMLMapping.serializeOnlyLabel)) {
					serializeOnly = tempAttribute.getNodeValue().equalsIgnoreCase("yes");
				} else if (tempAttribute.getNodeName().equals(XMLMapping.builderLabel)) {
					try {
						builderClass = Class.forName(tempAttribute.getNodeValue());
					} catch (ClassNotFoundException e) {
						throw new InvalidModelException("Builder defined for this model matches no known class: '"
								+ tempAttribute.getNodeValue() + "'");
					}
				} else {
					throw new InvalidModelException("Invalid attribute '" + tempAttribute.getNodeName()
							+ "' found in model file for tag 'model'");
				}
			}
		}

		modelEntitiesStoredByXMLTag = new Hashtable<String, ModelEntity>();
		modelEntitiesStoredByClassName = new Hashtable<String, ModelEntity>();

		entitiesNodeList = modelNode.getChildNodes();
		for (int i = 0; i < entitiesNodeList.getLength(); i++) {
			tempNode = entitiesNodeList.item(i);
			if (tempNode.getNodeType() == Node.ELEMENT_NODE) {
				if (tempNode.getNodeName().equals(XMLMapping.descriptionLabel)) {
					if (tempNode.getChildNodes().getLength() == 1 && tempNode.getFirstChild().getNodeType() == Node.TEXT_NODE) {
						setDescription(tempNode.getFirstChild().getNodeValue());
					}
				} else if (tempNode.getNodeName().equals(XMLMapping.entityLabel)) {
					updateAndRegisterNewModelEntity(new ModelEntity(tempNode, this));
				} else if (tempNode.getNodeName().equals(XMLMapping.mapIdLabel)) {
					mapId = new XMLMapId(tempNode, this);
				} else {
					throw new InvalidModelException("Invalid tag " + tempNode.getNodeName() + " found in model file");
				}
			} else if (tempNode.getNodeType() == Node.TEXT_NODE) {
				// Non significative text will be simply ignored
				if (tempNode.getNodeValue().trim().length() > 0) {
					throw new InvalidModelException("Invalid text found in model file");
				}
			} else if (tempNode.getNodeType() == Node.COMMENT_NODE) {
				// Simply ignore it
			} else {
				throw new InvalidModelException("Invalid xml tag found as child of 'model' tag in model file");
			}
		}

	}

	public boolean implementsCustomIdMappingScheme() {
		return mapId != null;
	}

	/**
	 * Internally used to register new <code>ModelEntity</code> object in mapping<br>
	 * Inheritance is managed at this level.
	 * 
	 * @param aModelEntity
	 *            a <code>ModelEntity</code> value
	 */
	private void updateAndRegisterNewModelEntity(ModelEntity aModelEntity) {
		for (Map.Entry<String, ModelEntity> e : modelEntitiesStoredByClassName.entrySet()) {
			ModelEntity tempModelEntity = e.getValue();
			if (tempModelEntity.inheritsFrom(aModelEntity)) {
				tempModelEntity.takeParentUnderAccount(aModelEntity);
				updateEntitiesStoredByXMLTags(tempModelEntity);
			}
			if (aModelEntity.inheritsFrom(tempModelEntity)) {
				aModelEntity.takeParentUnderAccount(tempModelEntity);
			}
		}
		storesNewModelEntity(aModelEntity);
		updateProperties();
	}

	private void updateProperties() {
		for (Map.Entry<String, ModelEntity> e : modelEntitiesStoredByClassName.entrySet()) {
			ModelEntity tempModelEntity = e.getValue();
			tempModelEntity.updateProperties();
		}

	}

	/**
	 * Internally used to register new <code>ModelEntity</code> object in mapping
	 * 
	 * @param aModelEntity
	 *            a <code>ModelEntity</code> value
	 */
	private void storesNewModelEntity(ModelEntity aModelEntity) {
		updateEntitiesStoredByXMLTags(aModelEntity);

		if (modelEntitiesStoredByClassName.get(aModelEntity.getName()) != null
				&& modelEntitiesStoredByClassName.get(aModelEntity.getName()) != aModelEntity) {
			throw new InvalidModelException("Duplicated entity matching class name " + aModelEntity.getName());
		}

		modelEntitiesStoredByClassName.put(aModelEntity.getName(), aModelEntity);

	}

	private void updateEntitiesStoredByXMLTags(ModelEntity aModelEntity) {
		if (aModelEntity.getXmlTags() != null) {
			for (int i = 0; i < aModelEntity.getXmlTags().length; i++) {
				String xmlTag = aModelEntity.getXmlTags()[i];
				if (modelEntitiesStoredByXMLTag.get(xmlTag) != null && modelEntitiesStoredByXMLTag.get(xmlTag) != aModelEntity) {
					throw new InvalidModelException("Duplicated entity with XML tag " + xmlTag);
				}
				modelEntitiesStoredByXMLTag.put(xmlTag, aModelEntity);
			}
		}
	}

	/**
	 * Returns <code>ModelEntity</code> object with XML tag name matching <code>aTagName</code> value, <code>null</code> if such an object
	 * doesn't exist.
	 * 
	 * @param aTagName
	 *            a <code>String</code> value
	 * @return a <code>ModelEntity</code> value
	 */
	public ModelEntity entityWithXMLTag(String aTagName) {

		return modelEntitiesStoredByXMLTag.get(aTagName);

	}

	/**
	 * Returns first <code>ModelProperty</code> object with XML tag name matching <code>aTagName</code> value, <code>null</code> if such an
	 * object doesn't exist.
	 * 
	 * @param aTagName
	 *            a <code>String</code> value
	 * @return a <code>ModelEntity</code> value
	 */
	public ModelProperty propertyWithXMLTag(String aTagName) {

		for (Map.Entry<String, ModelEntity> e : modelEntitiesStoredByXMLTag.entrySet()) {
			ModelEntity tempModelEntity = e.getValue();
			ModelProperty tempModelProperty = tempModelEntity.getModelPropertyWithXMLTag(aTagName);
			if (tempModelProperty != null) {
				return tempModelProperty;
			}
		}
		return null;
	}

	/**
	 * Returns <code>ModelEntity</code> object with class name matches <code>aClassName</code> value, <code>null</code> if such an object
	 * doesn't exist.
	 * 
	 * @param aClassName
	 *            a <code>String</code> value
	 * @return a <code>ModelEntity</code> value
	 */
	public ModelEntity entityWithClassName(String aClassName) {

		ModelEntity returnedModelEntity;
		returnedModelEntity = modelEntitiesStoredByClassName.get(aClassName);
		return returnedModelEntity;
	}

	/**
	 * Returns <code>ModelEntity</code> object with class matches <code>aClass</code> value, <code>null</code> if such an object doesn't
	 * exist. Look in inheritance tree.
	 * 
	 * @param aClass
	 *            a <code>Class</code> value
	 * @return a <code>ModelEntity</code> value
	 */
	public ModelEntity entityForClass(Class<?> aClass) {
		Class<?> currentClass = aClass;
		ModelEntity returned = entityWithClassName(currentClass.getName());

		while (currentClass != null && currentClass.getSuperclass() != null && returned == null) {
			currentClass = currentClass.getSuperclass();
			returned = entityWithClassName(currentClass.getName());
		}
		return returned;
	}

	/**
	 * Returns a String representation of this object suitable for debugging purposes
	 * 
	 * @return a <code>String</code> value
	 */
	@Override
	public String toString() {

		String returnedString = "<model";
		if (hasBuilderClass()) {
			returnedString += " " + builderLabel + "=" + '"' + builderClass().getName() + '"';
		}
		if (handlesReferences()) {
			returnedString += " " + handlesReferencesLabel + "=" + '"' + "YES" + '"';
		}
		returnedString += " " + serializationModeLabel + "=" + '"' + serializationMode + '"';
		returnedString += ">\n";

		for (Map.Entry<String, ModelEntity> e : modelEntitiesStoredByClassName.entrySet()) {
			ModelEntity tempModelEntity = e.getValue();
			returnedString += tempModelEntity.toString();
		}
		returnedString += "</model>\n";
		return returnedString;
	}

	/**
	 * Returns all <code>ModelEntity</code> objects stored in an Enumeration
	 */
	public Iterator<ModelEntity> allModelEntities() {
		if (modelEntitiesStoredByXMLTag != null) {
			return modelEntitiesStoredByXMLTag.values().iterator();
		} else {
			return null;
		}
	}

	/**
	 * Appends to this <code>XMLMapping</code> all entries concerning the other mapping <code>aMapping</code>, so that this mapping will be
	 * able to maps the two old mappings.
	 */
	public void appendXMLMapping(XMLMapping aMapping) {
		Iterator<ModelEntity> i = aMapping.allModelEntities();
		if (i != null) {
			while (i.hasNext()) {
				storesNewModelEntity(i.next());
			}
		}
	}

	/**
	 * Return Boolean indicating if references must be handled during encoding
	 */
	public boolean handlesReferences() {
		return handlesReferences;
	}

	/**
	 * Return Builder class defined for this model, if any, null if not defined
	 */
	public Class<?> builderClass() {
		return builderClass;
	}

	/**
	 * Return boolean indicating if a builder class has been defined for this model
	 */
	public boolean hasBuilderClass() {
		return builderClass != null;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * Return the most specialized registered entity matching all supplied XML tags
	 * 
	 * @param xmlTags
	 * @return
	 */
	public ModelEntity mostSpecializedEntityMatchingAllXMLTags(String[] xmlTags) {
		/*
		 * String concatenedXMLTags = ""; for (int i=0; i<xmlTags.length; i++) {
		 * concatenedXMLTags += xmlTags[i]; } System.out.println
		 * ("mostSpecializedEntityMatchingAllXMLTags() with
		 * "+concatenedXMLTags);
		 */

		if (xmlTags.length == 0) {
			// System.out.println ("mostSpecializedEntityMatchingAllXMLTags()
			// not found, supplied tag is null !");
			return null;
		} else if (xmlTags.length == 1) {
			return entityWithXMLTag(xmlTags[0]);
		} else {

			Hashtable potentialEntities = null;
			if (xmlTags.length > 0) {
				potentialEntities = entityWithXMLTag(xmlTags[0]).getAncestors();
			}

			for (int i = 1; i < xmlTags.length; i++) {
				String xmlTag = xmlTags[i];
				ModelEntity tempEntity = entityWithXMLTag(xmlTag);
				Hashtable<ModelEntity, Integer> tempAncestors = tempEntity.getAncestors();
				Hashtable<ModelEntity, Integer> keptEntities = new Hashtable<ModelEntity, Integer>();
				for (Enumeration e = tempAncestors.keys(); e.hasMoreElements();) {
					ModelEntity ent = (ModelEntity) e.nextElement();
					if (potentialEntities.get(ent) != null) {
						int potentialLevel = ((Integer) potentialEntities.get(ent)).intValue();
						int tempLevel = tempAncestors.get(ent).intValue();
						keptEntities.put(ent, new Integer(potentialLevel + tempLevel));
					}
				}
				potentialEntities = keptEntities;
			}

			if (potentialEntities.size() > 0) {
				ModelEntity returned = null;
				int bestLevel = 0;
				for (Enumeration e = potentialEntities.keys(); e.hasMoreElements();) {
					ModelEntity ent = (ModelEntity) e.nextElement();
					int entLevel = ((Integer) potentialEntities.get(ent)).intValue();
					// System.out.println ("I have "+ent.getName()+" with level
					// "+entLevel);
					if (returned == null || entLevel < bestLevel) {
						returned = ent;
						bestLevel = entLevel;
					}
				}
				// System.out.println ("Returning "+returned.getName()+" with
				// level "+bestLevel);
				return returned;
			} else {
				// System.out.println
				// ("mostSpecializedEntityMatchingAllXMLTags() not found !");
				return null;
			}
		}

	}

	public int getSerializationMode() {
		return serializationMode;
	}

	public void setSerializationMode(int serializationMode) {
		this.serializationMode = serializationMode;
	}

	/**
	 * Finds all objects embedded by the object object and of the class klass. This method is not recursive.
	 * 
	 * @param <T>
	 * @param object
	 * @param klass
	 * @return
	 */
	public <T> Collection<T> getEmbeddedObjectsForObject(Object object, Class<T> klass) {
		return getEmbeddedObjectsForObject(object, klass, false);
	}

	/**
	 * Finds all objects embedded by the object object and of the class klass. The method will maintain the natural order of objects if
	 * specified by the flag. This method is not recursive.
	 * 
	 * @param <T>
	 * @param object
	 * @param klass
	 * @param maintainNaturalOrder
	 * @return
	 */
	public <T> Collection<T> getEmbeddedObjectsForObject(Object object, Class<T> klass, boolean maintainNaturalOrder) {
		return getEmbeddedObjectsForObject(object, klass, maintainNaturalOrder, false);
	}

	/**
	 * Finds all objects embedded by the object object and of the class klass. The method will maintain the natural order of objects if
	 * specified by the flag. The method will go into objects recursively if specified by the flag.
	 * 
	 * @param <T>
	 * @param object
	 * @param klass
	 * @param maintainNaturalOrder
	 * @param recursive
	 * @return
	 */
	public <T> Collection<T> getEmbeddedObjectsForObject(Object object, Class<T> klass, boolean maintainNaturalOrder, boolean recursive) {
		return getEmbeddedObjectsForObject(object, maintainNaturalOrder ? new Vector<T>() : new HashSet<T>(), klass, recursive);
	}

	/**
	 * Finds all objects embedded by the object object and of the class klass. The method will go into objects recursively if specified by
	 * the flag. All objects will be added into the passed collection.
	 * 
	 * @param <T>
	 * @param object
	 * @param v
	 * @param klass
	 * @param recursive
	 * @return
	 */
	public <T> Collection<T> getEmbeddedObjectsForObject(Object object, Collection<T> v, Class<T> klass, boolean recursive) {
		return getEmbeddedObjectsForObject(object, v, klass, recursive, false, new HashSet<Object>());
	}

	/**
	 * Finds all objects embedded by the object object and of the class klass. This method is not recursive.
	 * 
	 * @param <T>
	 * @param object
	 * @param klass
	 * @return
	 */
	public <T> Collection<T> getRestrictedEmbeddedObjectsForObject(Object object, Class<T> klass) {
		return getRestrictedEmbeddedObjectsForObject(object, klass, false);
	}

	public <T> Collection<T> getRestrictedEmbeddedObjectsForObject(Object object, Class<T> klass, boolean maintainNaturalOrder) {
		return getRestrictedEmbeddedObjectsForObject(object, klass, maintainNaturalOrder, false);
	}

	public <T> Collection<T> getRestrictedEmbeddedObjectsForObject(Object object, Class<T> klass, boolean maintainNaturalOrder,
			boolean recursive) {
		return getRestrictedEmbeddedObjectsForObject(object, maintainNaturalOrder ? new Vector<T>() : new HashSet<T>(), klass, recursive);
	}

	public <T> Collection<T> getRestrictedEmbeddedObjectsForObject(Object object, Collection<T> v, Class<T> klass, boolean recursive) {
		return getEmbeddedObjectsForObject(object, v, klass, recursive, true, new HashSet<Object>());
	}

	private <T> Collection<T> getEmbeddedObjectsForObject(Object object, Collection<T> v, Class<T> klass, boolean recursive,
			boolean followPrimaryAndCopiableOnly, Collection<Object> visited) {
		// First we add the object to the visited objects!
		visited.add(object);
		if (klass.isAssignableFrom(object.getClass()) && !v.contains(object)) {
			v.add((T) object);
		}
		ModelEntity entity = entityForClass(object.getClass());
		if (entity == null) {
			return v;
		}
		Enumeration<ModelProperty> en = entity.getModelProperties();
		while (en.hasMoreElements()) {
			ModelProperty property = en.nextElement();
			if (followPrimaryAndCopiableOnly && (!property.isPrimary() || !property.isCopyable())) {
				continue;
			}
			Object o = property.getKeyValueProperty().getObjectValue(object);
			if (o != null) {
				if (recursive) {
					if (o instanceof Hashtable) {
						Hashtable<Object, Object> hash = (Hashtable<Object, Object>) o;
						for (Object oInHash : hash.values()) {
							if (!visited.contains(oInHash)) {
								v.addAll(getEmbeddedObjectsForObject(oInHash, v, klass, recursive, followPrimaryAndCopiableOnly, visited));
							}
						}
					} else if (o instanceof Vector) {
						Vector<Object> vector = (Vector<Object>) o;
						for (Object oInVector : vector) {
							if (!visited.contains(oInVector)) {
								v.addAll(getEmbeddedObjectsForObject(oInVector, v, klass, recursive, followPrimaryAndCopiableOnly, visited));
							}
						}
					} else {
						if (!visited.contains(o)) {
							v.addAll(getEmbeddedObjectsForObject(o, v, klass, recursive, followPrimaryAndCopiableOnly, visited));
						}
					}
				} else {
					if (!v.contains(o) && klass.isAssignableFrom(o.getClass())) {
						v.add((T) o);
					}
				}
			}
		}
		return v;
	}

}
