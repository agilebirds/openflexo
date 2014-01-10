package org.openflexo.model.factory;

import java.io.IOException;
import java.io.OutputStream;
import java.io.StringWriter;
import java.lang.reflect.InvocationTargetException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javassist.util.proxy.ProxyObject;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.output.Format;
import org.jdom2.output.LineSeparator;
import org.jdom2.output.XMLOutputter;
import org.openflexo.model.ModelContextLibrary;
import org.openflexo.model.ModelEntity;
import org.openflexo.model.ModelProperty;
import org.openflexo.model.StringEncoder;
import org.openflexo.model.annotations.XMLElement;
import org.openflexo.model.exceptions.InvalidDataException;
import org.openflexo.model.exceptions.ModelDefinitionException;
import org.openflexo.model.exceptions.ModelExecutionException;
import org.openflexo.model.exceptions.RestrictiveSerializationException;

class XMLSerializer {

	public static final String ID = "id";
	public static final String ID_REF = "idref";

	// Keys are objects and values are ObjectReference
	private Map<Object, ObjectReference> objectReferences;

	/**
	 * Stores already serialized objects where key is the serialized object and value is a
	 * 
	 * <pre>
	 * Object
	 * </pre>
	 * 
	 * instance coding the unique identifier of the object
	 */
	private Map<Object, Object> alreadySerialized;

	private int id = 0;
	private final ModelFactory modelFactory;
	private final SerializationPolicy policy;

	public XMLSerializer(ModelFactory modelFactory) {
		this(modelFactory, SerializationPolicy.PERMISSIVE);
	}

	public XMLSerializer(ModelFactory modelFactory, SerializationPolicy policy) {
		this.modelFactory = modelFactory;
		this.policy = policy;
	}

	private StringEncoder getStringEncoder() {
		return modelFactory.getStringEncoder();
	}

	public Document serializeDocument(Object object, OutputStream out) throws IOException, IllegalArgumentException,
			IllegalAccessException, InvocationTargetException, ModelDefinitionException {
		Document builtDocument = new Document();
		id = 0;
		objectReferences = new HashMap<Object, ObjectReference>();
		alreadySerialized = new HashMap<Object, Object>();
		Element rootElement = serializeElement(object, null);
		postProcess(rootElement);
		builtDocument.setRootElement(rootElement);
		Format prettyFormat = Format.getPrettyFormat();
		prettyFormat.setLineSeparator(LineSeparator.SYSTEM);
		XMLOutputter outputter = new XMLOutputter(prettyFormat);
		try {
			outputter.output(builtDocument, out);
		} catch (IOException e) {
			e.printStackTrace();
		}
		out.flush();
		return builtDocument;
	}

	public String buildXMLOutput(Document doc) {
		StringWriter writer = new StringWriter();
		Format prettyFormat = Format.getPrettyFormat();
		prettyFormat.setLineSeparator(LineSeparator.SYSTEM);
		XMLOutputter outputter = new XMLOutputter(prettyFormat);
		try {
			outputter.output(doc, writer);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return writer.toString();
	}

	private String generateReference(Object o) {
		return String.valueOf(id++);
	}

	private <I> Element serializeElement(Object object, XMLElement context) throws IllegalArgumentException, IllegalAccessException,
			InvocationTargetException, ModelDefinitionException {
		Element returned;
		if (object instanceof ProxyObject) {
			ProxyMethodHandler<I> handler = (ProxyMethodHandler<I>) ((ProxyObject) object).getHandler();
			ModelEntity<I> modelEntity = handler.getModelEntity();
			Class<I> implementedInterface = modelEntity.getImplementedInterface();
			boolean serializeModelEntityName = false;
			XMLElement xmlElement = modelEntity.getXMLElement();
			String xmlTag = modelEntity.getXMLTag();
			if (modelFactory.getModelContext().getModelEntity(implementedInterface) == null) {
				serializeModelEntityName = true;
				switch (policy) {
				case EXTENSIVE:
					List<ModelEntity<?>> upperEntities = modelFactory.getModelContext().getUpperEntities(object);
					if (upperEntities.size() == 0) {
						throw new ModelDefinitionException("Cannot serialize object of type: " + object.getClass().getName()
								+ " in context " + context.xmlTag() + ". No model entity could be found in the model mapping");
					} else if (upperEntities.size() > 1) {
						throw new ModelDefinitionException("Ambiguous entity for object " + object.getClass().getName()
								+ ". More than one entities are known in this model mapping.");
					}
					ModelEntity e = upperEntities.get(0);
					xmlTag = e.getXMLTag();
					modelEntity = ModelContextLibrary.getModelContext(implementedInterface).getModelEntity(implementedInterface);
					break;
				case PERMISSIVE:
					upperEntities = modelFactory.getModelContext().getUpperEntities(object);
					if (upperEntities.size() == 0) {
						throw new ModelDefinitionException("Cannot serialize object of type: " + object.getClass().getName()
								+ " in context " + context.xmlTag() + ". No model entity could be found in the model mapping");
					} else if (upperEntities.size() > 1) {
						throw new ModelDefinitionException("Ambiguous entity for object " + object.getClass().getName()
								+ ". More than one entities are known in this model mapping.");
					}
					modelEntity = (ModelEntity<I>) upperEntities.get(0);
					break;
				case RESTRICTIVE:
					throw new RestrictiveSerializationException("Entity of type " + implementedInterface.getName()
							+ " cannot be serialized in this model context");
				}
			}
			String contextString = context != null ? context.context() : "";
			String elementName = contextString + xmlTag;
			String namespace = null;
			if (xmlElement != null) {
				namespace = xmlElement.namespace() != XMLElement.NO_NAME_SPACE ? xmlElement.namespace() : null;
			}

			// Is this object already serialized ?
			Object reference = alreadySerialized.get(object);
			if (reference == null) {
				// First time i see this object
				try {
					handler.setSerializing(true);

					// Put this object in alreadySerialized objects
					reference = generateReference(object);
					alreadySerialized.put(object, reference);

					if (xmlElement != null) {
						returned = new Element(elementName, namespace);
						returned.setAttribute(ID, reference.toString());
						if (serializeModelEntityName) {
							returned.setAttribute(PAMELAConstants.MODEL_ENTITY_ATTRIBUTE, handler.getModelEntity()
									.getImplementedInterface().getName(), PAMELAConstants.NAMESPACE);
							if (handler.getOverridingSuperClass() != null) {
								returned.setAttribute(PAMELAConstants.CLASS_ATTRIBUTE, handler.getOverridingSuperClass().getName(),
										PAMELAConstants.NAMESPACE);
							}
						}
						Iterator<ModelProperty<? super I>> properties = modelEntity.getProperties();
						while (properties.hasNext()) {
							ModelProperty<? super I> p = properties.next();
							if (p.getXMLAttribute() != null) {
								Object oValue = handler.invokeGetter(p);
								if (oValue != null) {
									String value;
									try {
										value = getStringEncoder().toString(oValue);
										if (value != null) {
											returned.setAttribute(p.getXMLTag(), value);
										}
									} catch (InvalidDataException e) {
										throw new ModelExecutionException(e);
									}
								}
							} else if (p.getXMLElement() != null) {
								XMLElement propertyXMLElement = p.getXMLElement();
								switch (p.getCardinality()) {
								case SINGLE:
									Object oValue = handler.invokeGetter(p);
									if (oValue != null) {
										Element propertyElement = serializeElement(oValue, propertyXMLElement);
										returned.addContent(propertyElement);
									}
									break;
								case LIST:
									List<?> values = (List<?>) handler.invokeGetter(p);
									for (Object o : values) {
										if (o != null) {
											Element propertyElement2 = serializeElement(o, propertyXMLElement);
											returned.addContent(propertyElement2);
										}
									}
									break;
								case MAP:
									throw new UnsupportedOperationException("Cannot serialize maps for now");
								default:
									break;
								}
							}
						}
					} else if (getStringEncoder().isConvertable(modelEntity.getImplementedInterface())) {
						try {
							returned = new Element(elementName, namespace);
							returned.setText(getStringEncoder().toString(object));
							returned.setAttribute(ID, reference.toString());
						} catch (InvalidDataException e) {
							// This should not happen. If it does, then it is likely that the StringEncoder class is messed up by saying
							// that a
							// given type is convertable but does not convert it when asked
							throw new ModelDefinitionException(
									"Hu hoh, really don't know how you got into this state: your object is string convertable but conversion could not be performed",
									e);
						}
					} else {
						throw new ModelDefinitionException("No XML element for " + modelEntity.getImplementedInterface());
					}
				} finally {
					handler.setSerializing(false);
				}
			} else {
				// This object was already serialized somewhere, only put an idref
				// Debugging.debug ("This object has already been serialized
				// somewhere "+anObject);

				returned = new Element(elementName, namespace);
				returned.setAttribute(ID_REF, reference.toString());
			}

			// OK, Element is now built
			ObjectReference ref = objectReferences.get(object);
			if (ref != null) {
				ref.notifyNewElementReference(xmlElement, context, returned);
			} else {
				ref = new ObjectReference(object, xmlElement, context, returned);
				objectReferences.put(object, ref);
			}
			return returned;
		} else if (getStringEncoder().isConvertable(object.getClass())) {
			try {
				returned = new Element(context.xmlTag(), context.namespace());
				returned.setText(getStringEncoder().toString(object));
				return returned;
			} catch (InvalidDataException e) {
				throw new ModelDefinitionException(
						"Hu hoh, really don't know how you got into this state: your object is string convertable but conversion could not be performed",
						e);
			}
		} else {
			throw new ModelDefinitionException("Cannot serialize non-proxy object " + object);
		}

	}

	private void postProcess(Element rootElement) {
		int requiredSwaps = objectReferences.size();
		while (requiredSwaps > 0) {
			int newRequiredSwaps = 0;
			for (ObjectReference ref : objectReferences.values()) {
				if (!ref.postProcess()) {
					newRequiredSwaps++;
				}
			}
			if (newRequiredSwaps == requiredSwaps) {
				requiredSwaps = 0; // To avoid infinite loop
			} else {
				requiredSwaps = newRequiredSwaps;
			}
		}
	}

	protected static class ObjectReference {

		protected Object serializedObject;
		protected ElementReference primaryElement;
		protected Vector<ElementReference> referenceElements;

		protected ObjectReference(Object anObject, XMLElement xmlElement, XMLElement context, Element anElement) {
			super();
			serializedObject = anObject;
			referenceElements = new Vector<ElementReference>();
			addElementReference(new ElementReference(xmlElement, context, anElement));
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

		protected void notifyNewElementReference(XMLElement xmlElement, XMLElement context, Element anElement) {
			addElementReference(new ElementReference(xmlElement, context, anElement));
		}

		/*
		 * protected int getId() { return id; }
		 * 
		 * protected void changeId(int newId) { // System.out.println("changeId() to "+newId+" for "+primaryElement.element); if
		 * ((primaryElement != null) && (primaryElement.element != null)) changeIdForElement(newId,primaryElement.element); for (Enumeration
		 * en=referenceElements.elements(); en.hasMoreElements();) { ElementReference next = (ElementReference)en.nextElement(); if
		 * (next.element != null) changeIdForElement(newId,next.element); } }
		 * 
		 * protected void changeIdForElement(int newId, Element element) { if (element.getAttribute(ID) != null) {
		 * element.setAttribute(ID,encodeInteger(newId)); } else if (element.getAttribute(ID_REF) != null) {
		 * element.setAttribute(ID_REF,encodeInteger(newId)); } }
		 */

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
			return element.getAttribute(ID) != null;
		}

		private boolean done = false;

		protected boolean postProcess() {
			// System.out.println("***** postProcess("+this+")");
			// System.out.println("PRIMARY= (primary="+primaryElement.isPrimary()+") "+primaryElement.element);
			/*
			 * for (ElementReference ref : referenceElements) { System.out.println("REFERENCE= (primary="+ref.isPrimary()+") "+ref.element);
			 * }
			 */

			if (done) {
				return true;
			}
			if (primaryElement.context != null && primaryElement.context.primary()) { // That's OK
				return done = true;
			} else { // It might be NOK
				for (ElementReference ref : referenceElements) {
					if (ref.context != null && ref.context.primary()) {
						return setAsNewPrimaryElement(ref);
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
				return done = true;
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
				return true;
			}
		}

		/*
		 * private int idForElement(Element el) { int returned = decodeAsInteger(el.getAttributeValue(ID)); if (returned == -1) returned =
		 * decodeAsInteger(el.getAttributeValue(ID_REF)); return returned; }
		 */

		private boolean isAncestorOf(Element e1, Element e2) {
			return e1.isAncestor(e2);
		}

		protected class ElementReference {

			protected XMLElement xmlElement;
			protected XMLElement context;
			protected String xmlTag;
			protected Element element;

			protected ElementReference(XMLElement xmlElement, XMLElement context, Element anElement) {
				super();
				this.xmlElement = xmlElement;
				this.context = context;
				element = anElement;
				xmlTag = anElement.getName();
			}

			protected void delete() {
				xmlElement = null;
				context = null;
				xmlTag = null;
				element = null;
			}

			protected boolean isPrimary() {
				return context != null && context.primary();
			}
		}
	}

}
