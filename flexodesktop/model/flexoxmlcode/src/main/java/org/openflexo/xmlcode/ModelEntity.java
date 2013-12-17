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

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.Vector;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * <p>
 * <code>ModelEntity</code> is internally used in {@link org.openflexo.xmlcode.XMLMapping}
 * <p>
 * You never need to used it directly, this class maps <code>&lt;entity&gt;<code> tags in <i>model file</i>.
 * 
 * @author <a href="mailto:Sylvain.Guerin@enst-bretagne.fr">Sylvain Guerin</a>
 * @see XMLMapping
 */
public class ModelEntity {

	/** Stores name of this entity */
	protected String name;

	/** Stores defined xmlTag(s) of this entity */
	protected String[] definedXMLTags = null;

	/** Stores derived xmlTag(s) of this entity */
	protected String[] derivedXMLTags = null;

	/** Hashtable of ModelProperty objects (key is the name of the property) */
	protected Map<String, ModelProperty> modelProperties;

	/** Hashtable of ModelProperty objects (key is the name of the property) */
	protected Vector<ModelProperty> orderedModelProperties;

	/**
	 * Hashtable of ModelProperty objects (key is the name of the property and value is the ModelEntity related property inherits from)
	 */
	protected Map<String, ModelEntity> inheritedModelProperties;

	/**
	 * Stores a boolean indicating if this entity is abstract (won't never be instancied directly)
	 */
	protected boolean isAbstract = false;

	/** Stores related class */
	protected Class<?> relatedClass;

	/** Stores string indicating decoding finalizer to run, if required */
	protected String initializer = null;

	/** Stores string indicating decoding finalizer to run, if required */
	protected String finalizer = null;

	/** Stores string indicating decoding finalizer to run, if required */
	protected String contextsAsString = null;

	/** Stores string coding attribute coding generic typing, if any */
	protected String genericTypingStoredIn = null;

	/** Property coding generic typing, if any */
	protected SingleKeyValueProperty genericTypingKVProperty;

	/** Stores constructor without parameter, if any */
	protected Constructor<?> constructorWithoutParameter = null;

	/**
	 * Stores constructor with parameter (builder class defined in XMLMapping), if any
	 */
	protected Constructor<?> constructorWithParameter = null;

	/** Stores decoding finalizing method without parameter, if any */
	protected Method finalizerWithoutParameter = null;

	/**
	 * Stores decoding finalizing method with parameter (builder class defined in XMLMapping), if any
	 */
	protected Method finalizerWithParameter = null;

	/** Stores link to XMLMapping */
	protected XMLMapping model;

	/** Description of this entity */
	protected String description;

	private Method initializerWithoutParameter;

	private Method initializerWithParameter;

	/**
	 * Creates a new <code>ModelEntity</code> instance<br>
	 * This constructor should be called for dynamically XMLMapping building purposes.<br>
	 * Use {@link XMLMapping#registerNewModelEntity(ModelEntity)} to register this new <code>ModelEntity</code> object in an
	 * <code>XMLMapping</code> instance. Use {@link #registerNewModelProperty(ModelProperty)} to register new <code>ModelPropery</code>
	 * objects.
	 * 
	 * @param someXMLTags
	 *            comma separated strings coding all the values a XML tag could take
	 * @exception InvalidModelException
	 *                if an error occurs
	 */
	public ModelEntity(String aName, String someXMLTags, boolean abstractFlag, String aFinalizer, XMLMapping anXMLMapping,
			boolean permissive) throws InvalidModelException {

		super();

		name = aName;
		finalizer = aFinalizer;
		parseXMLTags(someXMLTags);
		isAbstract = abstractFlag;
		init(anXMLMapping, permissive);
	}

	/**
	 * Creates a new <code>ModelEntity</code> instance, given a node <code>anEntityNode</code>
	 * 
	 * @param anEntityNode
	 *            a <code>Node</code> value
	 * @exception InvalidModelException
	 *                if an error occurs
	 */
	public ModelEntity(Node anEntityNode, XMLMapping anXMLMapping) throws InvalidModelException {
		this(anEntityNode, anXMLMapping, false);
	}

	/**
	 * Creates a new <code>ModelEntity</code> instance, given a node <code>anEntityNode</code>
	 * 
	 * @param anEntityNode
	 *            a <code>Node</code> value
	 * @exception InvalidModelException
	 *                if an error occurs
	 */
	public ModelEntity(Node anEntityNode, XMLMapping anXMLMapping, boolean permissive) throws InvalidModelException {

		super();

		Node tempAttribute;
		NamedNodeMap attributes;
		boolean nameIsSpecified = false;
		boolean xmlTagIsSpecified = false;
		NodeList propertiesNodeList;
		Node tempNode;

		isAbstract = false;

		if (!anEntityNode.getNodeName().equals(XMLMapping.entityLabel)) {
			throw new InvalidModelException("Invalid tag '" + anEntityNode.getNodeName() + "' found in model file");
		} // end of if ()

		attributes = anEntityNode.getAttributes();
		for (int i = 0; i < attributes.getLength(); i++) {
			tempAttribute = attributes.item(i);
			if (tempAttribute.getNodeName().equals(XMLMapping.nameLabel)) {
				nameIsSpecified = true;
				name = tempAttribute.getNodeValue();
			} else if (tempAttribute.getNodeName().equals(XMLMapping.xmlTagLabel)) {
				xmlTagIsSpecified = true;
				parseXMLTags(tempAttribute.getNodeValue());
			} else if (tempAttribute.getNodeName().equals(XMLMapping.abstractLabel)) {
				isAbstract = tempAttribute.getNodeValue().equalsIgnoreCase("yes") || tempAttribute.getNodeValue().equalsIgnoreCase("true");
			} else if (tempAttribute.getNodeName().equals(XMLMapping.finalizerLabel)) {
				finalizer = tempAttribute.getNodeValue();
			} else if (tempAttribute.getNodeName().equals(XMLMapping.initializerLabel)) {
				initializer = tempAttribute.getNodeValue();
			} else if (tempAttribute.getNodeName().equals(XMLMapping.contextsLabel)) {
				contextsAsString = tempAttribute.getNodeValue();
			} else if (tempAttribute.getNodeName().equals(XMLMapping.genericTypingStoredIn)) {
				genericTypingStoredIn = tempAttribute.getNodeValue();
			} else {
				throw new InvalidModelException("Invalid attribute '" + tempAttribute.getNodeName()
						+ "' found in model file for tag 'entity'");
			}
		}

		init(anXMLMapping, permissive);

		if (genericTypingStoredIn != null) {
			// System.out.println("genericTypingStoredIn="+genericTypingStoredIn);
			genericTypingKVProperty = new SingleKeyValueProperty(getRelatedClass(), genericTypingStoredIn, true);
		}

		propertiesNodeList = anEntityNode.getChildNodes();
		for (int i = 0; i < propertiesNodeList.getLength(); i++) {
			tempNode = propertiesNodeList.item(i);
			if (tempNode.getNodeType() == Node.ELEMENT_NODE) {
				if (tempNode.getNodeName().equals(XMLMapping.descriptionLabel)) {
					if (tempNode.getChildNodes().getLength() == 1 && tempNode.getFirstChild().getNodeType() == Node.TEXT_NODE) {
						setDescription(tempNode.getFirstChild().getNodeValue());
					}
				} else if (tempNode.getNodeName().equals(XMLMapping.propertyLabel)) {
					try {
						ModelProperty newModelProperty = new ModelProperty(tempNode, this, permissive);
						if (modelProperties != null) {
							if (modelProperties.get(newModelProperty.getName()) != null) {
								throw new InvalidModelException("Duplicate property " + newModelProperty.getName() + " found in model file");
							} else {
								modelProperties.put(newModelProperty.getName(), newModelProperty);
								orderedModelProperties.add(newModelProperty);
							}
						}
					} catch (InvalidKeyValuePropertyException e) {
						if (!permissive) {
							throw e;
						} else {
							System.err.println("[XMLCODE_ISSUE] " + e.getMessage());
						}
					} catch (InvalidModelException e) {
						if (!permissive) {
							throw e;
						} else {
							System.err.println("[XMLCODE_ISSUE] " + e.getMessage());
						}
					}
				} else {
					if (!permissive) {
						throw new InvalidModelException("Invalid tag '" + tempNode.getNodeName() + "' found in model file for tag 'entity'");
					} else {
						System.err.println("[XMLCODE_ISSUE] " + "Invalid tag '" + tempNode.getNodeName()
								+ "' found in model file for tag 'entity'");
					}
				}
			} else if (tempNode.getNodeType() == Node.TEXT_NODE) {
				// Non significative text will be simply ignored
				if (tempNode.getNodeValue().trim().length() > 0) {
					throw new InvalidModelException("Invalid text found in model file");
				}
			} else if (tempNode.getNodeType() == Node.ATTRIBUTE_NODE) {
				// Simply ignore it
			} else if (tempNode.getNodeType() == Node.COMMENT_NODE) {
				// Simply ignore it
			} else {
				throw new InvalidModelException("Invalid xml tag found as child of 'entity' tag in model file");
			}
		}

		if (!nameIsSpecified) {
			throw new InvalidModelException("No attribute 'name' defined for tag 'entity' in model file");
		}
		if (!xmlTagIsSpecified && !isAbstract()) {
			throw new InvalidModelException("No attribute 'xmlTag' defined for tag 'entity' in model file");
		}

	}

	/**
	 * Internaly used by constructors
	 * 
	 * @param anXMLMapping
	 */
	private void init(XMLMapping anXMLMapping, boolean permissive) {
		Class<XMLSerializable> XMLSerialisableClass = org.openflexo.xmlcode.XMLSerializable.class;

		model = anXMLMapping;

		try {

			try {
				relatedClass = Class.forName(getName());
			} catch (ClassNotFoundException e) {
				if (!permissive) {
					throw new InvalidModelException("Class " + getName() + " not found.");
				} else {
					System.err.println("[XMLCODE_ISSUE] " + "Class " + getName() + " not found.");
					return;
				}
			}

			// Looking for constructors
			try {
				constructorWithoutParameter = relatedClass.getConstructor((Class<?>[]) null);
			} catch (NoSuchMethodException e) {
				// Ignore for now
			}

			if (model.hasBuilderClass()) {
				findConstructorWithParameter(model.builderClass);
			}

			if (!model.serializeOnly) {
				if (!hasConstructorWithoutParameter() && !hasConstructorWithParameter() && !isAbstract()) {
					if (!model.hasBuilderClass()) {
						String message = "Class "
								+ getName()
								+ " cannot be instanciated directly (check that this class has a constructor with no arguments [public "
								+ getName()
								+ "()] and that this class is not abstract. If this class should never be instanciated directly, declares it as abstract (see abtract xml tag).";
						if (!permissive) {
							throw new InvalidModelException(message);
						} else {
							System.err.println("[XMLCODE_ISSUE] " + message);
							return;
						}
					} else {
						String message = "Class "
								+ getName()
								+ " cannot be instanciated directly or with a parameter of "
								+ model.builderClass().getName()
								+ " and this class is not abstract. If this class should never be instanciated directly, declares it as abstract (see abtract xml tag).";
						if (!permissive) {
							throw new InvalidModelException(message);

						} else {
							System.err.println("[XMLCODE_ISSUE] " + message);
							return;
						}
					}
				}
			}

			// Looking for decoding initializing methods
			if (initializer != null) {
				try {
					initializerWithoutParameter = relatedClass.getMethod(initializer, (Class<?>[]) null);
				} catch (NoSuchMethodException e) {
					// Ignore for now
				}

				if (model.hasBuilderClass()) {
					boolean initializerHasBeenFound = false;
					Class currentClass = model.builderClass();
					Class[] params = new Class[1];
					while (!initializerHasBeenFound && currentClass != null) {
						try {
							params[0] = currentClass;
							initializerWithParameter = relatedClass.getMethod(finalizer, params);
							initializerHasBeenFound = true;
						} catch (NoSuchMethodException e) {
							currentClass = currentClass.getSuperclass();
						}
					}

				}

				if (!hasInitializerWithoutParameter() && !hasFinalizerWithParameter()) {
					throw new InvalidModelException("Class " + getName() + " does not implement specified decoding initializing method : "
							+ initializer);
				}
			}

			// Looking for decoding finalizing methods
			if (finalizer != null) {
				try {
					finalizerWithoutParameter = relatedClass.getMethod(finalizer, (Class<?>[]) null);
				} catch (NoSuchMethodException e) {
					// Ignore for now
				}

				if (model.hasBuilderClass()) {
					boolean finalizerHasBeenFound = false;
					Class currentClass = model.builderClass();
					Class[] params = new Class[1];
					while (!finalizerHasBeenFound && currentClass != null) {
						try {
							params[0] = currentClass;
							finalizerWithParameter = relatedClass.getMethod(finalizer, params);
							finalizerHasBeenFound = true;
						} catch (NoSuchMethodException e) {
							currentClass = currentClass.getSuperclass();
						}
					}

				}

				if (!hasFinalizerWithoutParameter() && !hasFinalizerWithParameter()) {
					throw new InvalidModelException("Class " + getName() + " does not implement specified decoding finalizing method : "
							+ finalizer);
				}
			}
		} catch (InvalidModelException e) {
			throw e;
		} catch (Exception e) {
			throw new InvalidModelException("Unexpected error " + e.getClass().getName()
					+ " occurs during model initialization. Please send a bug report.");
		}

		if (!XMLSerialisableClass.isAssignableFrom(relatedClass)) {
			if (!permissive) {
				throw new InvalidModelException("Class " + getName() + " MUST implement XMLSerializable interface");
			} else {
				System.err.println("[XMLCODE_ISSUE] " + "Class " + getName() + " MUST implement XMLSerializable interface");
			}
		}

		modelProperties = new LinkedHashMap<String, ModelProperty>();
		orderedModelProperties = new Vector<ModelProperty>();
		inheritedModelProperties = new LinkedHashMap<String, ModelEntity>();

		locallyDefinedContexts = new Vector<String>();
		if (contextsAsString != null) {
			StringTokenizer st = new StringTokenizer(contextsAsString, ",");
			while (st.hasMoreTokens()) {
				locallyDefinedContexts.add(st.nextToken());
			}
		}
		_availableContexts = new Vector<String>();
		_availableContexts.addAll(locallyDefinedContexts);

		_xmlTagsForContext = new Hashtable<String, String[]>();
	}

	/**
	 * 
	 */
	private void findConstructorWithParameter(Class<?> builderClass) {
		try {
			Class[] builder = { builderClass };
			constructorWithParameter = relatedClass.getConstructor(builder);
		} catch (NoSuchMethodException e) {
			// Ignore for now
		}
		if (constructorWithoutParameter == null && builderClass.getSuperclass() != null) {
			findConstructorWithParameter(builderClass.getSuperclass());
		}
	}

	private Vector<String> _availableContexts = null;
	private Vector<String> locallyDefinedContexts;

	public Vector getAvailableContexts() {
		/*if (_availableContexts == null) {
		    _availableContexts = new Vector();
		    _availableContexts.addAll(locallyDefinedContexts);
		    if (getParentEntity() != null) {
		        Vector inheritedContexts = getParentEntity().getAvailableContexts();
		        for (Enumeration en=inheritedContexts.elements(); en.hasMoreElements();) {
		            String next = (String)en.nextElement();
		            if (!_availableContexts.contains(next)) _availableContexts.add(next);
		        }
		    }
		}*/
		return _availableContexts;
	}

	private void updateAvailableContexts(ModelEntity parent) {
		if (parent != null) {
			Vector inheritedContexts = parent.getAvailableContexts();
			for (Enumeration en = inheritedContexts.elements(); en.hasMoreElements();) {
				String next = (String) en.nextElement();
				if (_availableContexts != null && !_availableContexts.contains(next)) {
					_availableContexts.add(next);
				}
			}
		}
	}

	private void parseXMLTags(String someXMLTags) {
		StringTokenizer st = new StringTokenizer(someXMLTags, ",");
		Vector<String> temp = new Vector<String>();
		while (st.hasMoreElements()) {
			String anXMLTag = (String) st.nextElement();
			temp.add(anXMLTag);
		}
		if (temp.size() == 0) {
			throw new InvalidModelException("No XML tags specified in model file for entity " + getName());
		} else {
			definedXMLTags = new String[temp.size()];
			int i = 0;
			for (Enumeration e = temp.elements(); e.hasMoreElements(); i++) {
				String next = (String) e.nextElement();
				definedXMLTags[i] = next;
				// Debugging.debug ("Found tag "+next+" for entity "+getName());
			}
		}
	}

	/**
	 * Register new <code>ModelProperty</code> object in mapping<br>
	 * This method MUST be use to dynamically handle <code>XMLMapping</code> instances.
	 * 
	 * @exception InvalidModelException
	 *                if an error occurs during mapping construction (invalid model)
	 */
	public void registerNewModelProperty(ModelProperty aModelProperty) {

		if (modelProperties.get(aModelProperty.getName()) != null) {
			throw new InvalidModelException("Duplicate property " + aModelProperty.getName() + " found in model file");
		} else {
			modelProperties.put(aModelProperty.getName(), aModelProperty);
			orderedModelProperties.add(aModelProperty);
		}
	}

	/**
	 * Returns <code>name</code> of this <code>ModelEntity</code>
	 * 
	 * @return a <code>String</code> value
	 */
	public String getName() {
		return name;
	}

	/**
	 * Returns related class
	 */
	public Class<?> getRelatedClass() {
		return relatedClass;
	}

	/**
	 * Returns default <code>xmlTag</code> of this <code>ModelEntity</code>
	 * 
	 * @return a <code>String</code> value
	 */
	public String getDefaultXmlTag() {

		if (definedXMLTags != null && definedXMLTags.length > 0) {
			return definedXMLTags[0];
		} else {
			throw new InvalidModelException("No XML tag defined for entity '" + getName() + "' Is it an abstract entity ?");
		}
	}

	/**
	 * Returns an array of all <code>xmlTag</code> of this <code>ModelEntity</code>
	 * 
	 * @return a <code>String[]</code> value
	 */
	public String[] getXmlTags() {
		if (definedXMLTags == null) {
			return null;
		}
		if (derivedXMLTags == null && getAvailableContexts() != null) {
			if (getAvailableContexts().size() == 0) {
				derivedXMLTags = definedXMLTags;
			} else {
				derivedXMLTags = new String[definedXMLTags.length * (getAvailableContexts().size() + 1)];
				for (int i = 0; i < definedXMLTags.length; i++) {
					derivedXMLTags[i] = definedXMLTags[i];
				}
				for (int j = 0; j < getAvailableContexts().size(); j++) {
					for (int i = 0; i < definedXMLTags.length; i++) {
						derivedXMLTags[(j + 1) * definedXMLTags.length + i] = (String) getAvailableContexts().elementAt(j)
								+ definedXMLTags[i];
					}
				}
			}
		}
		return derivedXMLTags;
	}

	private Hashtable<String, String[]> _xmlTagsForContext;

	/**
	 * Returns an array of all <code>xmlTag</code> of this <code>ModelEntity</code> given a suppliedContext
	 * 
	 * @return a <code>String[]</code> value
	 */
	public String[] getXmlTags(String context) {
		if (getAvailableContexts().contains(context) || context == null) {
			if (definedXMLTags == null) {
				return null;
			}
			if (context == null) {
				context = "";
			}
			if (_xmlTagsForContext.get(context) == null) {
				String[] tags = new String[definedXMLTags.length];
				for (int i = 0; i < definedXMLTags.length; i++) {
					tags[i] = context + definedXMLTags[i];
				}
				_xmlTagsForContext.put(context, tags);
			}
			return _xmlTagsForContext.get(context);
		} else {
			throw new InvalidModelException("Undefined context '" + context + "' for entity '" + getName() + "'");
		}
	}

	/**
	 * Returns a string representation of all <code>xmlTag</code> of this <code>ModelEntity</code>
	 * 
	 * @return a <code>String</code> value
	 */
	public String getConcatenedXmlTag() {
		String returned = "";
		if (derivedXMLTags != null) {
			for (int i = 0; i < derivedXMLTags.length; i++) {
				if (i > 0) {
					returned += ",";
				}
				returned += derivedXMLTags[i];
			}
			return returned;
		} else {
			return "null";
		}
	}

	/**
	 * Returns a string representation of all <code>context</code> of this <code>ModelEntity</code>
	 * 
	 * @return a <code>String</code> value
	 */
	public String getConcatenedContexts() {
		String returned = "";
		if (getAvailableContexts() != null) {
			for (int i = 0; i < getAvailableContexts().size(); i++) {
				if (i > 0) {
					returned += ",";
				}
				returned += getAvailableContexts().elementAt(i);
			}
			return returned;
		} else {
			return "null";
		}
	}

	/**
	 * Returns an ordered enumeration of <code>ModelProperty</code> objects of this <code>ModelEntity</code>
	 * 
	 * @return a <code>Vector</code> value
	 */
	public Enumeration<ModelProperty> getModelProperties() {
		if (orderedModelProperties == null) {
			return null;
		}
		return orderedModelProperties.elements();
		// return modelProperties.elements();
	}

	public Iterator<ModelProperty> getModelPropertiesIterator() {
		return orderedModelProperties.iterator();
		// return modelProperties.elements();
	}

	/**
	 * Returns <code>ModelProperty</code> object with specified name, null if such an object doesn't exist
	 * 
	 * @return a <code>ModelProperty</code> value
	 */
	public ModelProperty getModelPropertyWithName(String aPropertyName) {

		if (modelProperties == null) {
			return null;
		}
		return modelProperties.get(aPropertyName);
	}

	/**
	 * Returns first <code>ModelProperty</code> object with XML tag name matching <code>aTagName</code> value, <code>null</code> if such an
	 * object doesn't exist.
	 * 
	 * @return a <code>ModelProperty</code> value
	 */
	public ModelProperty getModelPropertyWithXMLTag(String aTagName) {

		for (Enumeration e = getModelProperties(); e.hasMoreElements();) {
			ModelProperty tempModelProperty = (ModelProperty) e.nextElement();
			if (tempModelProperty.handlesXMLTag(aTagName)) {
				return tempModelProperty;
			}
		}
		return null;

	}

	private Hashtable<ModelProperty, Vector<String>> _nonAttributeProperties;

	public Hashtable<ModelProperty, Vector<String>> getAllNonAttributeProperties() {
		if (_nonAttributeProperties == null) {
			_nonAttributeProperties = new Hashtable<ModelProperty, Vector<String>>();
			ModelEntity currentEntity = this;
			while (currentEntity != null) {
				Enumeration<ModelProperty> en = currentEntity.getModelProperties();
				ModelProperty item = null;
				while (en.hasMoreElements()) {
					item = en.nextElement();
					if (item.getIsAttribute()) {
						continue;
					}
					String[] tags = item.getXmlTags();
					Vector<String> v = new Vector<String>();
					for (int i = 0; i < tags.length; i++) {
						v.add(tags[i]);
					}
					_nonAttributeProperties.put(item, v);
				}
				currentEntity = currentEntity.getParentEntity();
			}
		}
		return _nonAttributeProperties;
	}

	/**
	 * Returns a String representation of this object suitable for debugging purposes
	 * 
	 * @return a <code>String</code> value
	 */
	@Override
	public String toString() {

		String returnedString = "  <entity name=" + '"' + getName() + '"' + " xmlTag=" + '"' + getConcatenedXmlTag() + '"' + " contexts="
				+ '"' + getConcatenedContexts() + '"';

		if (isAbstract()) {
			returnedString += " " + XMLMapping.abstractLabel + "=" + '"' + "yes" + '"';
		}

		if (finalizer != null) {
			returnedString += " " + XMLMapping.finalizerLabel + "=" + '"' + finalizer + '"';
		}

		returnedString += ">\n";

		for (Enumeration e = getModelProperties(); e.hasMoreElements();) {
			ModelProperty property = (ModelProperty) e.nextElement();
			if (!property.isInherited(this)) {
				returnedString += property.toString();
			}
		}
		returnedString += "  </entity>\n";
		return returnedString;
	}

	/**
	 * Returns a boolean indicating if this entity is abstract (won't never be instancied directly). An abstract class should be declared in
	 * mapping file with abstract XML tag set to true ('YES' value).
	 */
	public boolean isAbstract() {

		return isAbstract;
	}

	/**
	 * Returns a boolean indicating if entity represents a class which inherits from specified ModelEntity's represented class
	 */
	public boolean inheritsFrom(ModelEntity aModelEntity) {
		if (getRelatedClass() == null) {
			return false;
		}
		if (aModelEntity.getRelatedClass() == null) {
			return false;
		}
		return aModelEntity.getRelatedClass().isAssignableFrom(getRelatedClass());
	}

	/**
	 * Update this <code>ModelEntity</code> by taking under account specified <code>parentModelEntity</code>
	 */
	public void takeParentUnderAccount(ModelEntity parentModelEntity) {

		if (!inheritsFrom(parentModelEntity)) {
			throw new InvalidModelException("Incoherent data. Please send a bug report.");
		}

		else {
			// _availableContexts = null;
			if (parentModelEntity.getModelProperties() != null) {
				for (Enumeration<ModelProperty> e = parentModelEntity.getModelProperties(); e.hasMoreElements();) {
					ModelProperty parentModelProperty = e.nextElement();
					if (getModelPropertyWithName(parentModelProperty.getName()) != null) {
						// This entity already has this property defined,
						// which overrides the parent one > do nothing
					} else if (modelProperties != null) {
						modelProperties.put(parentModelProperty.getName(), parentModelProperty);
						orderedModelProperties.add(parentModelProperty);
						inheritedModelProperties.put(parentModelProperty.getName(), parentModelEntity);
						// System.out.println ("Setting property
						// "+parentModelProperty.getName()+" for "+getName()+" as
						// property inherited from "+parentModelEntity.getName());
					}
				}
				updateAvailableContexts(parentModelEntity);
			}
			derivedXMLTags = null;
		}
	}

	public void updateProperties() {
		if (getModelProperties() != null) {
			for (Enumeration e = getModelProperties(); e.hasMoreElements();) {
				ModelProperty property = (ModelProperty) e.nextElement();
				property.updateHandledXMLTags();
			}
		}
	}

	/**
	 * Returns constructor without parameter, if any
	 */
	public Constructor getConstructorWithoutParameter() {
		return constructorWithoutParameter;
	}

	/**
	 * Returns boolean indicating if related class has a constructor without parameters
	 * 
	 * @return
	 */
	public boolean hasConstructorWithoutParameter() {
		return constructorWithoutParameter != null;
	}

	/**
	 * Returns constructor with parameter (builder class defined in XMLMapping), if any
	 */
	public Constructor getConstructorWithParameter() {
		return constructorWithParameter;
	}

	/**
	 * Returns boolean indicating if related class has a constructor with parameters
	 * 
	 * @return
	 */
	public boolean hasConstructorWithParameter() {
		return constructorWithParameter != null;
	}

	/**
	 * Returns decoding finalizing method without parameter, if any
	 */
	public Method getFinalizerWithoutParameter() {
		if (finalizerWithoutParameter == null) {
			if (getParentEntity() != null) {
				return getParentEntity().getFinalizerWithoutParameter();
			} else {
				return null;
			}
		}
		return finalizerWithoutParameter;
	}

	/**
	 * Returns decoding finalizing method with parameter (builder class defined in XMLMapping), if any
	 */
	public Method getFinalizerWithParameter() {
		if (finalizerWithParameter == null) {
			if (getParentEntity() != null) {
				return getParentEntity().getFinalizerWithParameter();
			} else {
				return null;
			}
		}
		return finalizerWithParameter;
	}

	/**
	 * Returns boolean indicating if this entity has a decoding finalizing method without parameter, if any
	 */
	public boolean hasFinalizerWithoutParameter() {
		if (getParentEntity() != null) {
			return finalizerWithoutParameter != null || getParentEntity().hasFinalizerWithoutParameter();
		}
		return finalizerWithoutParameter != null;
	}

	/**
	 * Returns boolean indicating if this entity has a decoding finalizing method with parameter (builder class defined in XMLMapping), if
	 * any
	 */
	public boolean hasFinalizerWithParameter() {
		if (getParentEntity() != null) {
			return finalizerWithParameter != null || getParentEntity().hasFinalizerWithParameter();
		}
		return finalizerWithParameter != null;
	}

	/**
	 * Returns decoding finalizing method without parameter, if any
	 */
	public Method getInitializerWithoutParameter() {
		if (initializerWithoutParameter == null) {
			if (getParentEntity() != null) {
				return getParentEntity().getInitializerWithoutParameter();
			} else {
				return null;
			}
		}
		return initializerWithoutParameter;
	}

	/**
	 * Returns decoding finalizing method with parameter (builder class defined in XMLMapping), if any
	 */
	public Method getInitializerWithParameter() {
		if (initializerWithParameter == null) {
			if (getParentEntity() != null) {
				return getParentEntity().getInitializerWithParameter();
			} else {
				return null;
			}
		}
		return initializerWithParameter;
	}

	/**
	 * Returns boolean indicating if this entity has a decoding finalizing method without parameter, if any
	 */
	public boolean hasInitializerWithoutParameter() {
		if (getParentEntity() != null) {
			return initializerWithoutParameter != null || getParentEntity().hasInitializerWithoutParameter();
		}
		return initializerWithoutParameter != null;
	}

	/**
	 * Returns boolean indicating if this entity has a decoding finalizing method with parameter (builder class defined in XMLMapping), if
	 * any
	 */
	public boolean hasInitializerWithParameter() {
		if (getParentEntity() != null) {
			return initializer != null || getParentEntity().hasInitializerWithParameter();
		}
		return initializerWithParameter != null;
	}

	/**
	 * Return related model as XMLMapping
	 */
	public XMLMapping getModel() {
		return model;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * Return the most specialized parent entity registered in related model
	 * 
	 * @return
	 */
	public ModelEntity getParentEntity() {
		Class current = getRelatedClass().getSuperclass();
		boolean parentIsFound = false;
		while (current != null && !parentIsFound) {
			if (model.entityWithClassName(current.getName()) != null) {
				ModelEntity parentEntity = model.entityWithClassName(current.getName());
				return parentEntity;
			}
			current = current.getSuperclass();
		}
		return null;

	}

	public Vector<ModelEntity> getAllChildrenEntities() {
		Vector<ModelEntity> children = new Vector<ModelEntity>();
		for (ModelEntity e : getModel().modelEntitiesStoredByClassName.values()) {
			if (e.getParentEntity() == this) {
				children.add(e);
			}
		}
		return children;
	}

	/**
	 * Build and returns an Hashtable composed of all ancestors registered in related model, associated with an Integer object representing
	 * ancestor level.
	 * 
	 * @return Hashtable of ModelEntity keys associated to Integer values
	 */
	public Hashtable<ModelEntity, Integer> getAncestors() {
		Hashtable<ModelEntity, Integer> returned = new Hashtable<ModelEntity, Integer>();
		returned.put(this, new Integer(0));
		buildAncestors(0, returned);
		// System.out.println ("Ancestors for "+getName());
		/*
		 * for (Enumeration e = returned.keys(); e.hasMoreElements();) {
		 * ModelEntity temp = (ModelEntity)e.nextElement(); System.out.println
		 * ("Found "+temp.getName()+" with level
		 * "+((Integer)returned.get(temp)).intValue()); }
		 */
		return returned;
	}

	/**
	 * Internally used to build ancestors hashtable
	 * 
	 * @param level
	 * @param ancestors
	 * @param anEntity
	 */
	private void buildAncestors(int level, Hashtable<ModelEntity, Integer> ancestors) {
		ModelEntity nextParentEntity = null;
		Class current = getRelatedClass().getSuperclass();
		int nextLevel = level;
		boolean parentIsFound = false;
		while (current != null && !parentIsFound) {
			if (model.entityWithClassName(current.getName()) != null) {
				nextParentEntity = model.entityWithClassName(current.getName());
				parentIsFound = true;
			} else {
				current = current.getSuperclass();
				nextLevel++;
			}
		}
		if (nextParentEntity != null) {
			ancestors.put(nextParentEntity, new Integer(nextLevel + 1));
			nextParentEntity.buildAncestors(nextLevel + 1, ancestors);
		}

		for (int i = 0; i < getRelatedClass().getInterfaces().length; i++) {
			Class nextInterface = getRelatedClass().getInterfaces()[i];
			ModelEntity parentInterfaceEntity = model.entityWithClassName(nextInterface.getName());
			if (parentInterfaceEntity != null) {
				ancestors.put(parentInterfaceEntity, new Integer(level + 1));
				parentInterfaceEntity.buildAncestors(level + 1, ancestors);
			}
		}

	}

	public boolean implementsGenericTypingKVProperty() {
		if (genericTypingKVProperty != null) {
			return true;
		}
		if (getParentEntity() != null) {
			return getParentEntity().implementsGenericTypingKVProperty();
		} else {
			return false;
		}
	}

	public SingleKeyValueProperty getGenericTypingKVProperty() {
		if (genericTypingKVProperty != null) {
			return genericTypingKVProperty;
		}
		if (getParentEntity() != null) {
			return getParentEntity().getGenericTypingKVProperty();
		} else {
			return null;
		}
	}

}
