package org.openflexo.model.factory;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.jdom2.Attribute;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.filter.ElementFilter;
import org.jdom2.input.SAXBuilder;
import org.openflexo.model.ModelContext.ModelPropertyXMLTag;
import org.openflexo.model.ModelEntity;
import org.openflexo.model.ModelProperty;
import org.openflexo.model.StringEncoder;
import org.openflexo.model.exceptions.InvalidDataException;
import org.openflexo.model.exceptions.ModelDefinitionException;
import org.openflexo.model.exceptions.ModelExecutionException;
import org.openflexo.model.exceptions.RestrictiveDeserializationException;

class XMLDeserializer {

	public static final String ID = "id";
	public static final String ID_REF = "idref";

	private ModelFactory modelFactory;

	private Map<String, Element> index;

	/**
	 * Stores already serialized objects where value is the serialized object and key is an object coding the unique identifier of the
	 * object
	 */
	private Map<Object, Object> alreadyDeserialized;

	private List<ProxyMethodHandler<?>> deserializingHandlers;
	private final DeserializationPolicy policy;

	public XMLDeserializer(ModelFactory factory) {
		this(factory, DeserializationPolicy.PERMISSIVE);
	}

	public XMLDeserializer(ModelFactory factory, DeserializationPolicy policy) {
		this.modelFactory = factory;
		this.policy = policy;
		alreadyDeserialized = new HashMap<Object, Object>();
		deserializingHandlers = new ArrayList<ProxyMethodHandler<?>>();
	}

	private StringEncoder getStringEncoder() {
		return modelFactory.getStringEncoder();
	}

	public Object deserializeDocument(InputStream in) throws IOException, JDOMException, InvalidDataException, ModelDefinitionException {
		alreadyDeserialized.clear();
		Document dataDocument = parseXMLData(in);
		Element rootElement = dataDocument.getRootElement();
		return buildObjectFromNode(rootElement);
	}

	public Object deserializeDocument(String xml) throws IOException, JDOMException, InvalidDataException, ModelDefinitionException {
		alreadyDeserialized.clear();
		Document dataDocument = parseXMLData(xml);
		Element rootElement = dataDocument.getRootElement();
		return buildObjectFromNode(rootElement);
	}

	private Object buildObjectFromNode(Element node) throws InvalidDataException, ModelDefinitionException {
		// System.out.println("What to do with "+node+" ?");

		ModelEntity<?> modelEntity = modelFactory.getModelContext().getModelEntity(node.getName());
		Object object = buildObjectFromNodeAndModelEntity(node, modelEntity);
		for (ProxyMethodHandler<?> handler : deserializingHandlers) {
			handler.setDeserializing(false);
		}
		return object;
	}

	private <I> Object buildObjectFromNodeAndModelEntity(Element node, ModelEntity<I> modelEntity) throws InvalidDataException,
			ModelDefinitionException {
		Object currentDeserializedReference = null;
		Attribute idAttribute = node.getAttribute(ID);
		Attribute idrefAttribute = node.getAttribute(ID_REF);
		if (idrefAttribute != null) {
			// This seems to be an already deserialized object
			Object reference;
			reference = idrefAttribute.getValue();
			Object referenceObject = alreadyDeserialized.get(reference);
			if (referenceObject == null) {
				// Try to find this object elsewhere in the document
				// NOTE: This should never occur, except if the file was
				// manually edited, or
				// if the file was generated BEFORE development of ordered
				// properties feature

				// TODO: Throw here an error in future release but for backward compatibility we leave it for now.
				Element idRefElement = findElementWithId(idrefAttribute.getValue());
				if (idRefElement != null) {
					return buildObjectFromNodeAndModelEntity(idRefElement, modelEntity);
				}
				throw new InvalidDataException("No reference to object with identifier " + reference);
			} else {
				// No need to go further: i've got my object
				// Debugging.debug ("Stopping decoding: object found as a
				// reference "+reference+" "+referenceObject);
				return referenceObject;
			}
		}
		if (idAttribute != null) {
			currentDeserializedReference = idAttribute.getValue();
			Object referenceObject = alreadyDeserialized.get(currentDeserializedReference);
			if (referenceObject != null) {
				// No need to go further: i've got my object
				return referenceObject;
			}
		}

		// I need to rebuild it

		I returned;
		String text = node.getText();
		if (text != null && getStringEncoder().isConvertable(modelEntity.getImplementedInterface())) {
			// GPO: I am not sure this is still useful.
			try {
				returned = getStringEncoder().fromString(modelEntity.getImplementedInterface(), text);
			} catch (InvalidDataException e) {
				throw new ModelExecutionException(e);
			}
		} else {
			returned = modelFactory.newInstance(modelEntity.getImplementedInterface());
		}

		if (currentDeserializedReference != null) {
			alreadyDeserialized.put(currentDeserializedReference, returned);
		}

		ProxyMethodHandler<I> handler = modelFactory.getHandler(returned);
		deserializingHandlers.add(handler);
		handler.setDeserializing(true);
		for (Attribute attribute : node.getAttributes()) {
			ModelProperty<? super I> property = modelEntity.getPropertyForXMLAttributeName(attribute.getName());
			if (property == null) {
				if (attribute.getNamespace().equals(PAMELAConstants.NAMESPACE)
						&& (attribute.getName().equals(PAMELAConstants.CLASS_ATTRIBUTE) || attribute.getName().equals(
								PAMELAConstants.MODEL_ENTITY_ATTRIBUTE))) {
					continue;
				}
				if (attribute.getName().equals(ID) || attribute.getName().equals(ID_REF)) {
					continue;
				}
				switch (policy) {
				case PERMISSIVE:
					continue;
				case RESTRICTIVE:
					throw new RestrictiveDeserializationException("No attribute found for the attribute named: " + attribute.getName());
				case EXTENSIVE:
					// TODO: handle extra values
					break;
				}
			}
			Object value = getStringEncoder().fromString(property.getType(), attribute.getValue());
			if (value != null) {
				handler.invokeSetterForDeserialization(property, value);
			}

		}
		for (Element child : node.getChildren()) {
			ModelPropertyXMLTag<I> modelPropertyXMLTag = modelFactory.getModelContext().getPropertyForXMLTag(modelEntity, child.getName());
			ModelProperty<? super I> property = null;
			ModelEntity<?> entity = null;
			if (modelPropertyXMLTag != null) {
				property = modelPropertyXMLTag.getProperty();
				entity = modelPropertyXMLTag.getAccessedEntity();
			} else if (policy == DeserializationPolicy.RESTRICTIVE) {
				throw new RestrictiveDeserializationException("Element with name does not fit any properties within entity " + modelEntity);
			}
			Class<?> implementedInterface = null;
			Class<?> implementingClass = null;
			String entityName = child.getAttributeValue(PAMELAConstants.MODEL_ENTITY_ATTRIBUTE, PAMELAConstants.NAMESPACE);
			String className = child.getAttributeValue(PAMELAConstants.CLASS_ATTRIBUTE, PAMELAConstants.NAMESPACE);
			if (entityName != null) {
				try {
					implementedInterface = Class.forName(entityName);
				} catch (ClassNotFoundException e) {
					// TODO: log something here
				}
				switch (policy) {
				case PERMISSIVE:
					break;
				case RESTRICTIVE:
					break;
				case EXTENSIVE:
					if (entityName != null) {
						modelFactory.importClass(implementedInterface);
						if (className != null) {
							try {
								implementingClass = Class.forName(className);
								if (implementedInterface.isAssignableFrom(implementingClass)) {
									modelFactory.setImplementingClassForInterface((Class) implementingClass, implementedInterface);
								} else {
									throw new ModelExecutionException(className + " does not implement " + implementedInterface
											+ " for node " + child.getName());
								}
							} catch (ClassNotFoundException e) {
								// TODO: log something here
							}
						}
					}
					break;
				}
				if (implementedInterface != null) {
					entity = modelFactory.getModelContext().getModelEntity(implementedInterface);
				}
				if (entity == null && policy == DeserializationPolicy.RESTRICTIVE) {
					if (entityName != null) {
						throw new RestrictiveDeserializationException("Entity " + entityName + " is not part of this model context");
					} else {
						throw new RestrictiveDeserializationException("No entity found for tag " + child.getName());
					}
				}
			}
			if (property != null) {
				Object value = null;
				if (entity != null && !getStringEncoder().isConvertable(property.getType())) {
					value = buildObjectFromNodeAndModelEntity(child, entity);
				} else if (getStringEncoder().isConvertable(property.getType())) {
					value = getStringEncoder().fromString(property.getType(), child.getText());
				} else {
					// Should not happen
					throw new ModelExecutionException("Found property " + property + " but was unable to deserialize the content of node "
							+ child);
				}
				switch (property.getCardinality()) {
				case SINGLE:
					handler.invokeSetterForDeserialization(property, value);
					break;
				case LIST:
					handler.invokeAdderForDeserialization(property, value);
					break;
				case MAP:
					throw new UnsupportedOperationException("Cannot deserialize maps for now");
				default:
					break;
				}
			}

		}

		return returned;
	}

	private class MatchingElement {
		private Element element;
		private ModelEntity<?> modelEntity;

		private MatchingElement(Element element, ModelEntity<?> modelEntity) {
			super();
			this.element = element;
			this.modelEntity = modelEntity;
		}

		@Override
		public String toString() {
			return element.toString() + "/" + modelEntity;
		}
	}

	protected Document parseXMLData(InputStream xmlStream) throws IOException, JDOMException {
		SAXBuilder parser = new SAXBuilder();
		Document reply = parser.build(xmlStream);
		makeIndex(reply);
		return reply;
	}

	protected Document parseXMLData(String xml) throws IOException, JDOMException {
		SAXBuilder parser = new SAXBuilder();
		Document reply = parser.build(new StringReader(xml));
		makeIndex(reply);
		return reply;
	}

	static private class ElementWithIDFilter extends ElementFilter {

		public ElementWithIDFilter() {
			super();
		}

		@Override
		public Element filter(Object arg0) {
			Element element = super.filter(arg0);
			if (element != null && element.getAttributeValue("id") != null) {
				return element;
			}
			return null;
		}

	}

	public Document makeIndex(Document doc) {
		index = new Hashtable<String, Element>();
		Iterator<Element> it = doc.getDescendants(new ElementWithIDFilter());
		Element e = null;
		while (it.hasNext()) {
			e = it.next();
			index.put(e.getAttributeValue("id"), e);
		}
		return doc;
	}

	private Element findElementWithId(String id) {
		return index.get(id);
	}

}
