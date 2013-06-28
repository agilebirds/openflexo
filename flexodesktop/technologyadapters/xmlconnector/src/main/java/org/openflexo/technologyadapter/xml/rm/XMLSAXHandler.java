package org.openflexo.technologyadapter.xml.rm;

import java.lang.reflect.Type;
import java.util.Stack;
import java.util.logging.Logger;

import org.openflexo.foundation.dkv.TestPopulateDKV;
import org.openflexo.foundation.technologyadapter.FlexoMetaModel;
import org.openflexo.foundation.technologyadapter.FlexoModel;
import org.openflexo.foundation.technologyadapter.FlexoModelResource;
import org.openflexo.technologyadapter.xml.model.IXMLAttribute;
import org.openflexo.technologyadapter.xml.model.IXMLIndividual;
import org.openflexo.technologyadapter.xml.model.IXMLMetaModel;
import org.openflexo.technologyadapter.xml.model.IXMLModel;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.ext.DefaultHandler2;
/**
 * This SaxHandler is used to de-serialize any XML file, either conformant or not
 * to an XSD file
 * The behavior of the Handler depends on the situation (existing XSD).
 * 
 * @author xtof
 *
 * @param <M>, the Model to populate
 * @param <MM>, The MetaModel that will be populated (if createMissingType is true) or that is given (if XML is XSD conformant)
 * @param <IC>, The Class of Individuals (mapping to XML Elements)
 * @param <AC>, The Class of Attributes (mapping to XML Attributes)
 */

public class XMLSAXHandler<M extends FlexoModel<M,MM>, MM extends FlexoMetaModel<MM>, IC, AC extends IXMLAttribute> extends DefaultHandler2 {


	protected static final Logger logger = Logger.getLogger(TestPopulateDKV.class.getPackage().getName());

	/**
	 * 
	 */
	private final FlexoModelResource<M,MM> xmlResource;

	private M aXMLModel;
	private MM aMetaModel;
	private boolean createMissingType;

	private IC currentContainer = null;
	private IC currentIndividual = null;
	private AC currentAttribute = null;

	private StringBuffer cdataBuffer = new StringBuffer();

	private Stack<IC> indivStack = new Stack<IC>();
	private Stack<AC> attributeStack = new Stack<AC>();

	public XMLSAXHandler( FlexoModelResource<M, MM> xmlFileResourceImpl, boolean allowTypeCreation) {
		super();
		xmlResource = xmlFileResourceImpl;
		aXMLModel = (M) xmlResource.getModel();
		aMetaModel = aXMLModel.getMetaModel();
		createMissingType = allowTypeCreation;
	}


	@SuppressWarnings("unchecked")
	public void startElement (String uri, String localName,String qName, 
			Attributes attributes) throws SAXException {


		try {

			IC anIndividual = null;


			// Depending on the choices made when interpreting MetaModel, an XML Element
			// might be translated as a Property or a new Type....
			// If type is not found maybe we should look if it the particular element
			// has been translated as an ObjectProperty or DataProperty

			Type currentType = ((IXMLMetaModel) aMetaModel).getTypeFromURI(uri+"#"+localName);

			if (currentContainer != null && currentType == null){
				currentAttribute = (AC) ((IXMLIndividual<?, ?>) currentContainer).getAttributeByName(localName);

				if (currentAttribute != null){
					if (!currentAttribute.isSimpleAttribute() ){
						// this is a complex attribute, we will create an individual and then add to the attribute values
						currentType = ((IXMLAttribute) currentAttribute).getAttributeType();
						attributeStack.push(currentAttribute);
					}
				}
			}

			// creates type on the Fly
			if ( currentType == null && createMissingType){
				logger.info("Creating a new Type");
				currentType = ((IXMLMetaModel) aMetaModel).createNewType(uri,localName,qName);
			}

			if (currentType != null){
				// creates individual
				anIndividual = (IC) ((IXMLModel) aXMLModel).addNewIndividual(currentType);
				((IXMLIndividual<IC,AC>) anIndividual).setType(currentType);
				currentIndividual = (IC) anIndividual;
				cdataBuffer.delete(0,cdataBuffer.length());
			}

			if (anIndividual != null){
				// Attributes

				int len = attributes.getLength();


				for (int i=0 ; i<len ; i++ ) {

					Type aType = null;
					String typeName = attributes.getType(i);
					String attrQName = attributes.getQName(i);
					String attrName = attributes.getLocalName(i);
					String attrURI= attributes.getURI(i);

					aType = ((IXMLMetaModel) aMetaModel).getTypeFromURI(attrURI+"#"+attrName);

					if (typeName.equals(XMLFileResourceImpl.CDATA_TYPE_NAME)){
						aType = String.class;

						if (attrName.equals("")) attrName=attrQName;

						AC attr = (AC) ((IXMLModel) aXMLModel).createAttribute(attrName, aType , attributes.getValue(i));
						((IXMLIndividual<IC, AC>) anIndividual).addAttribute(attrName, (AC) attr);
					}
					else {
						if (aType == null){
							XMLFileResourceImpl.logger.warning("XMLIndividual: cannot find a type for " + typeName + " - falling back to String");
							aType = String.class;
						}

						AC attr = (AC) ((IXMLModel) aXMLModel).createAttribute(attrName, aType , attributes.getValue(i));
						((IXMLIndividual<IC, AC>) anIndividual).addAttribute(attrName, (AC) attr);
					}

				}


				if ( currentContainer == null) {
					((IXMLModel) aXMLModel).setRoot( (IXMLIndividual<IC, AC>) anIndividual);
					currentContainer = anIndividual;
				}
				else {
					((IXMLIndividual<IC, AC>) currentContainer).addChild((IXMLIndividual<IC, AC>) anIndividual);
				}
				indivStack.push(anIndividual);
				currentContainer = anIndividual;

			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}


	}

	@SuppressWarnings("unchecked")
	public void endElement(String uri, String localName,
			String qName) throws SAXException {

		// Allocation of CDATA information depends on the type of entity we have to allocate
		// content to (Individual or Attribute)
		// As such, it depends on the interpretation that has been done of XSD MetaModel
		//
		// Same stands for individuals to be allocated to ObjectProperties

		// CDATA allocation

		String str = cdataBuffer.toString().trim();

		if (currentAttribute != null && currentAttribute.isSimpleAttribute() && str.length() > 0){
			currentAttribute.addValue((IXMLIndividual<?, ?>) currentContainer, str);
			cdataBuffer.delete(0,cdataBuffer.length());
		}
		else {
			if (currentAttribute != null && currentIndividual != null && !currentAttribute.isSimpleAttribute()){
				currentAttribute.addValue((IXMLIndividual<?, ?>) currentContainer, currentIndividual);
			}
			else 
				if (currentIndividual != null && str.length() >0){
					AC attr = (AC) ((IXMLModel) aXMLModel).createAttribute(IXMLIndividual.CDATA_ATTR_NAME, String.class ,str);
					((IXMLIndividual<IC, AC>) currentIndividual).addAttribute(IXMLIndividual.CDATA_ATTR_NAME, (AC)attr);
					cdataBuffer.delete(0,cdataBuffer.length());
				}

			// node stack management

			if (!indivStack.isEmpty()) currentIndividual = indivStack.pop();
			if (!indivStack.isEmpty()){
				currentContainer = indivStack.lastElement();
			}
			else {
				currentContainer = null;
			}
		}


		// cleans stuff

		if (!attributeStack.isEmpty()) currentAttribute = attributeStack.lastElement();
		// currentIndividual = null;
	}



	public void characters(char ch[], int start, int length) throws SAXException {

		if (length >0)
			cdataBuffer.append(ch,start,length);
	}

}