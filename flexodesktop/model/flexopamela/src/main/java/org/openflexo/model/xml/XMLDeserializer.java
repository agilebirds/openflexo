package org.openflexo.model.xml;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;

import org.jdom.Attribute;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.filter.ElementFilter;
import org.jdom.filter.Filter;
import org.jdom.input.SAXBuilder;
import org.openflexo.model.annotations.XMLElement;
import org.openflexo.model.factory.ModelDefinitionException;
import org.openflexo.model.factory.ModelEntity;
import org.openflexo.model.factory.ModelExecutionException;
import org.openflexo.model.factory.ModelFactory;
import org.openflexo.model.factory.ModelProperty;
import org.openflexo.model.factory.ProxyMethodHandler;

public class XMLDeserializer {

	public static final String ID = "id";
	public static final String ID_REF = "idref";

	private DefaultStringEncoder stringEncoder;
	private ModelFactory modelFactory;

	private Hashtable<String, Element> _index;

	/**
	 * Stores already serialized objects where value is the serialized object
	 * and key is an object coding the unique identifier of the object
	 */
	private Hashtable<Object,Object> alreadyDeserialized;

	public XMLDeserializer(ModelFactory factory)
	{
		this(factory.getStringEncoder(), factory);
	}

	public XMLDeserializer(DefaultStringEncoder stringEncoder, ModelFactory factory)
	{
		this.stringEncoder = stringEncoder;
		this.modelFactory = factory;
		alreadyDeserialized = new Hashtable<Object, Object>();
	}

	public Object deserializeDocument(InputStream in) throws IOException, JDOMException, InvalidXMLDataException, ModelDefinitionException
	{
		alreadyDeserialized.clear();
		Document dataDocument = parseXMLData(in);
		Element rootElement = dataDocument.getRootElement();
		return buildObjectFromNode(rootElement);
	}

	public Object deserializeDocument(String xml) throws IOException, JDOMException, InvalidXMLDataException, ModelDefinitionException {
		alreadyDeserialized.clear();
		Document dataDocument = parseXMLData(xml);
		Element rootElement = dataDocument.getRootElement();
		return buildObjectFromNode(rootElement);
	}

	private Object buildObjectFromNode(Element node) throws InvalidXMLDataException, ModelDefinitionException
	{
		//System.out.println("What to do with "+node+" ?");

		ModelEntity<?> modelEntity = modelFactory.getModelEntity(node.getName());

		return buildObjectFromNodeAndModelEntity(node, modelEntity);
	}

	private Object buildObjectFromNodeAndModelEntity(Element node, ModelEntity modelEntity) throws InvalidXMLDataException, ModelDefinitionException
	{
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
				throw new InvalidXMLDataException("No reference to object with identifier " + reference);
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
				// Debugging.debug ("Stopping decoding: object found as a
				// reference "+reference+" "+referenceObject);
				return referenceObject;
			}
		}

		// I need to rebuild it

		Object returned;
		String text = node.getText();
		if (text != null && stringEncoder.isConvertable(modelEntity.getImplementedInterface())) {
			try {
				returned = stringEncoder.fromString(modelEntity.getImplementedInterface(), text);
			} catch (InvalidDataException e) {
				throw new ModelExecutionException(e);
			}
		} else {
			try {
				returned = modelEntity.newInstance();
			} catch (IllegalArgumentException e) {
				throw new ModelExecutionException(e);
			} catch (NoSuchMethodException e) {
				throw new ModelExecutionException(e);
			} catch (InstantiationException e) {
				throw new ModelExecutionException(e);
			} catch (IllegalAccessException e) {
				throw new ModelExecutionException(e);
			} catch (InvocationTargetException e) {
				throw new ModelExecutionException(e);
			} catch (ModelDefinitionException e) {
				throw new ModelExecutionException(e);
			}
		}

		if (currentDeserializedReference != null) {
			// Debugging.debug ("Registering object reference
			// "+currentDeserializedReference);
			// System.out.println ("Registering ref:
			// "+currentDeserializedReference+" object
			// "+returnedObject.getClass().getName());
			alreadyDeserialized.put(currentDeserializedReference, returned);
		}

		ProxyMethodHandler handler = modelFactory.getHandler(returned);

		Iterator<ModelProperty> properties = modelEntity.getProperties();
		while (properties.hasNext()) {
			ModelProperty p = properties.next();
			if (p.getXMLAttribute() != null) {
				Object value;
				try {
					value = stringEncoder.fromString(p.getType(),
							node.getAttributeValue(p.getXMLTag()));
					if (value != null) {
						handler.invokeSetter(p,value);
					}
				} catch (InvalidDataException e) {
					throw new InvalidXMLDataException(e.getMessage());
				}
			}
			else if (p.getXMLElement() != null) {
				XMLElement propertyXMLElement = p.getXMLElement();
				//System.out.println("Handle element "+p);
				if (p.getAccessedEntity() != null) {
					Iterator<MatchingElement> matchingElements = elementsMatchingHandledXMLTags(node,p);
					switch (p.getCardinality()) {
					case SINGLE:
						if (matchingElements.hasNext()) {
							MatchingElement matchingElement = matchingElements.next();
							//System.out.println("SINGLE, "+matchingElement);
							Object value = buildObjectFromNodeAndModelEntity(matchingElement.element,matchingElement.modelEntity);
							handler.invokeSetter(p, value);
						}
						break;
					case LIST:
						while (matchingElements.hasNext()) {
							MatchingElement matchingElement = matchingElements.next();
							//System.out.println("LIST, "+matchingElement);
							Object value = buildObjectFromNodeAndModelEntity(matchingElement.element,matchingElement.modelEntity);
							handler.invokeAdder(p, value);
						}
						break;
					default:
						break;
					}
				} else if (stringEncoder.isConvertable(p.getType())) {
					// TODO: deserialize string convertable elements.
					List<Element> elements = node.getContent(new ElementFilter(p.getXMLElement().xmlTag()));
					for (Element element : elements) {
						Object value;
						try {
							value = stringEncoder.fromString(p.getType(), element.getText());
						} catch (InvalidDataException e) {
							throw new InvalidXMLDataException("'" + element.getText() + "' cannot be converted to " + p.getType().getName());
						}
						switch (p.getCardinality()) {
						case SINGLE:
							handler.invokeSetter(p, value);
							break;
						case LIST:
							handler.invokeAdder(p, value);
							break;
						default:
							break;
						}
					}
				}
			}
		}

		return returned;
	}

	private class MatchingElement
	{
		private Element element;
		private ModelEntity modelEntity;
		private MatchingElement(Element element, ModelEntity modelEntity) {
			super();
			this.element = element;
			this.modelEntity = modelEntity;
		}
		@Override
		public String toString() {
			return element.toString()+"/"+modelEntity;
		}
	}

	private Iterator<MatchingElement> elementsMatchingHandledXMLTags(Element node, ModelProperty<?> modelProperty) throws ModelDefinitionException
	{
		ArrayList<MatchingElement> returned = new ArrayList<MatchingElement>();
		String contextString = modelProperty.getXMLElement() != null ? modelProperty.getXMLElement().context() : "";
		if(modelProperty.getAccessedEntity()!=null) {
			for (ModelEntity entity : modelProperty.getAccessedEntity().getAllDescendantsAndMe()) {
				if (entity.getXMLElement() != null || stringEncoder.isConvertable(entity.getImplementedInterface())) { // Only consider
					// "XML-concrete"
					// entities and string
					// convertable entities
					List<Element> elements = node.getContent(new ElementFilter(contextString + entity.getXMLTag()));
					for (Element element : elements) {
						returned.add(new MatchingElement(element, entity));
					}
				}
			}
		}
		//System.out.println("elementsMatchingHandledXMLTags="+returned);
		return returned.iterator();
	}


	protected Document parseXMLData(InputStream xmlStream) throws IOException, JDOMException
	{
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

	static private class ElementWithIDFilter implements Filter {

		public ElementWithIDFilter(){
			super();
		}
		@Override
		public boolean matches(Object arg0) {
			if(arg0 instanceof Element){
				return ((Element)arg0).getAttributeValue("id")!=null;
			}
			return false;
		}

	}

	public Document makeIndex(Document doc)
	{
		_index = new Hashtable<String, Element>();
		Iterator<Element>  it = doc.getDescendants(new ElementWithIDFilter());
		Element e = null;
		while (it.hasNext()){
			e=it.next();
			_index.put(e.getAttributeValue("id"), e);
		}
		return doc;
	}

	private Element findElementWithId(String id){
		return _index.get(id);
	}

}
