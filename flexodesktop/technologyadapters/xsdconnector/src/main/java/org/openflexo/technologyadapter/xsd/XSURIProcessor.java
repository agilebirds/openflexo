/*
 * (c) Copyright 2010-2012 AgileBirds
 * (c) Copyright 2012-2013 Openflexo
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

package org.openflexo.technologyadapter.xsd;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openflexo.foundation.ontology.DuplicateURIException;
import org.openflexo.foundation.technologyadapter.FlexoModelResource;
import org.openflexo.foundation.view.ModelSlotInstance;
import org.openflexo.technologyadapter.xml.XMLURIProcessor;
import org.openflexo.technologyadapter.xsd.metamodel.XSOntClass;
import org.openflexo.technologyadapter.xsd.metamodel.XSOntProperty;
import org.openflexo.technologyadapter.xsd.model.AbstractXSOntObject;
import org.openflexo.technologyadapter.xsd.model.XSOntIndividual;
import org.openflexo.technologyadapter.xsd.model.XSOntology;
import org.openflexo.technologyadapter.xsd.model.XSPropertyValue;
import org.openflexo.technologyadapter.xsd.rm.XSDMetaModelResource;



/* Correct processing of XML Objects URIs needs to add an internal class to store
 * for each XSOntClass wich are the XML Elements (attributes or CDATA, or...) that will be 
 * used to calculate URIs
 */

// TODO Manage the fact that URI May Change

public class XSURIProcessor extends XMLURIProcessor {

	// Properties actually used to calculate URis

	// TODO ça c'est CACA!
	// TODO getter/setter sur mappedClass + modif de l'URI en fonction de .....
	private XSOntClass mappedClass;
	private XSOntProperty baseAttributeForURI;


	private XSDModelSlot modelSlot;
	// Cache des URis Pour aller plus vite ??
	// TODO some optimization required
	private Map<String, AbstractXSOntObject> uriCache = new HashMap<String, AbstractXSOntObject>();


	public void setModelSlot(XSDModelSlot xsdModelSlot) {
		modelSlot = xsdModelSlot;
	}

	// Lifecycle management methods
	public void reset() {
		super.reset();
		baseAttributeForURI = null;
	}

	// TODO
	public XSOntClass getMappedClass() {
		if (mappedClass == null && typeURI != null){
			bindtypeURIToMappedClass();
		}
		return mappedClass;
	}
	
	// TODO
	public void setMappedClass(XSOntClass mappedClass) {
		this.mappedClass = mappedClass;
		if (mappedClass != null){
			_setTypeURI(mappedClass.getURI());
		}
		setChanged();
		notifyObservers();
	}

	// FIXME do this propre
	public XSOntProperty getBaseAttributeForURI() {
		if (mappedClass == null && typeURI != null){
			bindtypeURIToMappedClass();
		}
		return baseAttributeForURI;
	}

	// TODO
	public void setBaseAttributeForURI(XSOntProperty baseAttributeForURI) {
		this.baseAttributeForURI = baseAttributeForURI;
	}

	public void bindtypeURIToMappedClass() {
		if (modelSlot != null) {
			String mmURI = modelSlot.getMetaModelURI();
			if (mmURI != null) {
				// FIXME : to be re-factored
				XSDMetaModelResource mmResource = (XSDMetaModelResource) modelSlot.getMetaModelResource();
				if (mmResource != null) {
					mappedClass = mmResource.getMetaModelData().getClass(typeURI.toString());
					if (mappingStyle == MappingStyle.ATTRIBUTE_VALUE && attributeName != null) {
						baseAttributeForURI = (XSOntProperty) mappedClass.getPropertyByName(attributeName);
					}
				} else {
					XSDModelSlot.logger.warning("unable to map typeURI to an OntClass, as metaModelResource is Null ");
				}
			} else
				mappedClass = null;
		} else {
			XSDModelSlot.logger.warning("unable to map typeURI to an OntClass, as modelSlot is Null ");
		}
	}

	// URI Calculation

	public String getURIForObject(ModelSlotInstance msInstance, AbstractXSOntObject xsO) {
		String builtURI = null;
		StringBuffer completeURIStr = new StringBuffer();

		// if processor not initialized
		if (mappedClass == null) {
			bindtypeURIToMappedClass();
		}
		// processor should be initialized
		if (mappedClass == null) {
			XSDModelSlot.logger.warning("Cannot process URI as URIProcessor is not initialized for that class: " + typeURI);
			return null;
		} else {
			if (mappingStyle == MappingStyle.ATTRIBUTE_VALUE && attributeName != null && mappedClass != null) {

				XSOntProperty aProperty = mappedClass.getPropertyByName(attributeName);
				XSPropertyValue value = ((XSOntIndividual) xsO).getPropertyValue(aProperty);
				try {
					builtURI = URLEncoder.encode(value.toString(),"UTF-8");
				} catch (UnsupportedEncodingException e) {
					XSDModelSlot.logger.warning("Cannot process URI - Unexpected encoding error");
					e.printStackTrace();
				}
			} 
			else if (mappingStyle  == MappingStyle.SINGLETON ){
				try {
					builtURI = URLEncoder.encode(((XSOntClass) ((XSOntIndividual) xsO).getType()).getURI(),"UTF-8");
				} catch (UnsupportedEncodingException e) {
					XSDModelSlot.logger.warning("Cannot process URI - Unexpected encoding error");
					e.printStackTrace();
				}
			} else{
				XSDModelSlot.logger.warning("Cannot process URI - Unexpected or Unspecified mapping parameters");
			}
		}

		if (builtURI != null){
			if(uriCache.get(builtURI) == null){
				// TODO Manage the fact that URI May Change
				uriCache.put(builtURI, xsO);
			}
		}
		completeURIStr.append(typeURI.getScheme()).append("://").append(typeURI.getHost() ).append(typeURI.getPath()).append("?").append(builtURI).append("#").append(typeURI.getFragment());
		return  completeURIStr.toString();
	}

	// get the Object given the URI
	public Object retrieveObjectWithURI(ModelSlotInstance msInstance, String objectURI) throws DuplicateURIException {

		AbstractXSOntObject o = uriCache.get(objectURI);

		// if processor not initialized
		if (mappedClass == null) {
			bindtypeURIToMappedClass();
		}

		// modelResource must also be loaded!

		FlexoModelResource resource = (FlexoModelResource) msInstance.getModel().getResource();
		if (!resource.isLoaded()) {
			resource.getModelData();
		}

		// retrieve object
		if (o == null) {

			if (mappingStyle == MappingStyle.ATTRIBUTE_VALUE && attributeName != null) {

				XSOntProperty aProperty = mappedClass.getPropertyByName(attributeName);
				String attrValue = URI.create(objectURI).getQuery();

				for (XSOntIndividual obj: ((XSOntology) msInstance.getModel()).getIndividualsOfClass(mappedClass)){

					XSPropertyValue value = ((XSOntIndividual) obj).getPropertyValue(aProperty);
					try {
						if (value.equals(URLDecoder.decode(attrValue,"UTF-8"))){
							return obj;
						}
					} catch (UnsupportedEncodingException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}

			} 	else if (mappingStyle == MappingStyle.SINGLETON ){
				List<?> indivList = ((XSOntology) msInstance.getModel()).getIndividualsOfClass(mappedClass);
				if (indivList.size() > 1) {
					throw new DuplicateURIException("Cannot process URI - Several individuals found for singleton of type " + this._getTypeURI().toString());
				}
				else if (indivList.size() ==0){
					XSDModelSlot.logger.warning("Cannot find Singleton for type : " + this._getTypeURI().toString());
				}
				else{
					o = (AbstractXSOntObject) indivList.get(0);
				}
			}
		}
		else{
			XSDModelSlot.logger.warning("Cannot process URI - Unexpected or Unspecified mapping parameters");

		}

		return o;
	}


	// get the right URIProcessor for URI

	static public String retrieveTypeURI(ModelSlotInstance msInstance, String objectURI){

		URI fullURI;
		StringBuffer typeURIStr = new StringBuffer();

		fullURI = URI.create(objectURI);
		typeURIStr.append(fullURI.getScheme()).append("://").append(fullURI.getHost() ).append(fullURI.getPath()).append("#").append(fullURI.getFragment());

		return typeURIStr.toString();
	}

}