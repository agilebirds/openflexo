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
import java.util.logging.Logger;

import org.openflexo.foundation.ontology.DuplicateURIException;
import org.openflexo.foundation.technologyadapter.FlexoModelResource;
import org.openflexo.foundation.view.ModelSlotInstance;
import org.openflexo.model.annotations.Getter;
import org.openflexo.model.annotations.ImplementationClass;
import org.openflexo.model.annotations.ModelEntity;
import org.openflexo.model.annotations.PropertyIdentifier;
import org.openflexo.model.annotations.Setter;
import org.openflexo.model.annotations.XMLAttribute;
import org.openflexo.model.annotations.XMLElement;
import org.openflexo.technologyadapter.xml.XMLURIProcessor;
import org.openflexo.technologyadapter.xml.model.IXMLType;
import org.openflexo.technologyadapter.xsd.metamodel.XSDMetaModel;
import org.openflexo.technologyadapter.xsd.metamodel.XSOntClass;
import org.openflexo.technologyadapter.xsd.metamodel.XSOntProperty;
import org.openflexo.technologyadapter.xsd.model.AbstractXSOntObject;
import org.openflexo.technologyadapter.xsd.model.XMLXSDModel;
import org.openflexo.technologyadapter.xsd.model.XSOntIndividual;
import org.openflexo.technologyadapter.xsd.model.XSOntology;
import org.openflexo.technologyadapter.xsd.model.XSPropertyValue;
import org.openflexo.technologyadapter.xsd.rm.XSDMetaModelResource;

/* Correct processing of XML Objects URIs needs to add an internal class to store
 * for each XSOntClass wich are the XML Elements (attributes or CDATA, or...) that will be 
 * used to calculate URIs
 */

// TODO Manage the fact that URI May Change

@ModelEntity
@ImplementationClass(XSURIProcessor.XSURIProcessorImpl.class)
@XMLElement(xmlTag = "URIProcessor")
public interface XSURIProcessor extends XMLURIProcessor {

	@PropertyIdentifier(type = String.class)
	public static final String TYPE_URI_KEY = "typeURI";
	@PropertyIdentifier(type = MappingStyle.class)
	public static final String MAPPING_STYLE_KEY = "mappingStyle";
	@PropertyIdentifier(type = String.class)
	public static final String ATTRIBUTE_NAME_KEY = "attributeName";

	@Getter(value = TYPE_URI_KEY)
	@XMLAttribute
	public String _getTypeURI();

	@Setter(TYPE_URI_KEY)
	public void _setTypeURI(String typeURI);

	@Getter(value = MAPPING_STYLE_KEY)
	@XMLAttribute
	public MappingStyle getMappingStyle();

	@Setter(MAPPING_STYLE_KEY)
	public void setMappingStyle(MappingStyle mappingStyle);

	@Getter(value = ATTRIBUTE_NAME_KEY)
	@XMLAttribute
	public String _getAttributeName();

	@Setter(ATTRIBUTE_NAME_KEY)
	public void _setAttributeName(String attributeName);

	public Object retrieveObjectWithURI(ModelSlotInstance msInstance, String objectURI) throws DuplicateURIException;

	public String getURIForObject(ModelSlotInstance msInstance, AbstractXSOntObject xsO);

	public void reset();

	public static abstract class XSURIProcessorImpl extends XMLURIProcessorImpl implements XSURIProcessor {

		static final Logger logger = Logger.getLogger(XSURIProcessor.class.getPackage().getName());

		// Properties actually used to calculate URis

		// Cache des URis Pour aller plus vite ??
		// TODO some optimization required
		private final Map<String, AbstractXSOntObject> uriCache = new HashMap<String, AbstractXSOntObject>();

		public XSURIProcessorImpl(String typeURI) {
			super(typeURI);
		}

		// Lifecycle management methods
		@Override
		public void reset() {
			super.reset();
			this.setBaseAttributeForURI(null);
		}

		// TODO WARNING!!! Pb avec les typeURI....
		@Override
		public void _setTypeURI(String name) {
			typeURI = URI.create(name);
			bindtypeURIToMappedClass();
		}

		// TODO
		@Override
		public IXMLType getMappedClass() {
			IXMLType mappedClass = super.getMappedClass();
			if (mappedClass == null && typeURI != null) {
				bindtypeURIToMappedClass();
			}
			return mappedClass;
		}

		// TODO
		@Override
		public void setMappedClass(IXMLType mappedClass) {
			super.setMappedClass(mappedClass);
			if (mappedClass != null && !mappedClass.getURI().equals(_getTypeURI())) {
				_setTypeURI(mappedClass.getURI());
			}
			setChanged();
			notifyObservers();
		}

		@Override
		public void bindtypeURIToMappedClass() {
			XSDModelSlot modelSlot = (XSDModelSlot) getModelSlot();
			if (modelSlot != null) {
				String mmURI = modelSlot.getMetaModelURI();
				if (mmURI != null) {
					// FIXME : to be re-factored
					XSDMetaModelResource mmResource = (XSDMetaModelResource) modelSlot.getMetaModelResource();
					if (mmResource != null) {
						setMappedClass(mmResource.getMetaModelData().getClass(typeURI.toString()));
						if (mappingStyle == MappingStyle.ATTRIBUTE_VALUE && attributeName != null) {
							setBaseAttributeForURI(((XSOntClass) getMappedClass()).getPropertyByName(attributeName));
						}
					} else {
						logger.warning("unable to map typeURI to an OntClass, as metaModelResource is Null ");
					}
				} else
					setMappedClass(null);
			} else {
				logger.warning("unable to map typeURI to an OntClass, as modelSlot is Null ");
			}
		}

		// URI Calculation

		@Override
		public String getURIForObject(ModelSlotInstance msInstance, AbstractXSOntObject xsO) {
			String builtURI = null;
			StringBuffer completeURIStr = new StringBuffer();

			// if processor not initialized
			if (getMappedClass() == null) {
				bindtypeURIToMappedClass();
			}
			// processor should be initialized
			if (getMappedClass() == null) {
				logger.warning("Cannot process URI as URIProcessor is not initialized for that class: " + typeURI);
				return null;
			} else {
				if (mappingStyle == MappingStyle.ATTRIBUTE_VALUE && attributeName != null && getMappedClass() != null) {

					XSOntProperty aProperty = ((XSOntClass) getMappedClass()).getPropertyByName(attributeName);
					XSPropertyValue value = ((XSOntIndividual) xsO).getPropertyValue(aProperty);
					try {
						// NPE protection
						if (value != null) {
							builtURI = URLEncoder.encode(value.toString(), "UTF-8");
						} else {
							logger.severe("XSURI: unable to compute an URI for given object");
							builtURI = null;
						}
					} catch (UnsupportedEncodingException e) {
						logger.warning("Cannot process URI - Unexpected encoding error");
						e.printStackTrace();
					}
				} else if (mappingStyle == MappingStyle.SINGLETON) {
					try {
						builtURI = URLEncoder.encode(((XSOntClass) ((XSOntIndividual) xsO).getType()).getURI(), "UTF-8");
					} catch (UnsupportedEncodingException e) {
						logger.warning("Cannot process URI - Unexpected encoding error");
						e.printStackTrace();
					}
				} else {
					logger.warning("Cannot process URI - Unexpected or Unspecified mapping parameters");
				}
			}

			if (builtURI != null) {
				if (uriCache.get(builtURI) == null) {
					// TODO Manage the fact that URI May Change
					uriCache.put(builtURI, xsO);
				}
			}
			completeURIStr.append(typeURI.getScheme()).append("://").append(typeURI.getHost()).append(typeURI.getPath()).append("?")
					.append(builtURI).append("#").append(typeURI.getFragment());
			return completeURIStr.toString();
		}

		// get the Object given the URI

		@Override
		public Object retrieveObjectWithURI(ModelSlotInstance msInstance, String objectURI) throws DuplicateURIException {

			AbstractXSOntObject o = uriCache.get(objectURI);

			// if processor not initialized
			if (getMappedClass() == null) {
				bindtypeURIToMappedClass();
			}

			// modelResource must also be loaded!

			FlexoModelResource<XMLXSDModel, XSDMetaModel, XSDTechnologyAdapter> resource = (FlexoModelResource<XMLXSDModel, XSDMetaModel, XSDTechnologyAdapter>) msInstance
					.getResource();

			// should not be a preoccupation of XSURI
			// if (!resource.isLoaded()) {
			// resource.getModelData();
			// }

			// retrieve object
			if (o == null) {

				if (mappingStyle == MappingStyle.ATTRIBUTE_VALUE && attributeName != null) {

					XSOntProperty aProperty = ((XSOntClass) getMappedClass()).getPropertyByName(attributeName);
					String attrValue = URI.create(objectURI).getQuery();

					for (XSOntIndividual obj : ((XSOntology) resource.getModel()).getIndividualsOfClass(getMappedClass())) {

						XSPropertyValue value = obj.getPropertyValue(aProperty);
						try {
							if (value.equals(URLDecoder.decode(attrValue, "UTF-8"))) {
								return obj;
							}
						} catch (UnsupportedEncodingException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}

				} else if (mappingStyle == MappingStyle.SINGLETON) {
					List<?> indivList = ((XSOntology) msInstance.getAccessedResourceData()).getIndividualsOfClass(getMappedClass());
					if (indivList.size() > 1) {
						throw new DuplicateURIException("Cannot process URI - Several individuals found for singleton of type "
								+ this._getTypeURI().toString());
					} else if (indivList.size() == 0) {
						logger.warning("Cannot find Singleton for type : " + this._getTypeURI().toString());
					} else {
						o = (AbstractXSOntObject) indivList.get(0);
					}
				}
			} else {
				logger.warning("Cannot process URI - Unexpected or Unspecified mapping parameters");

			}

			return o;
		}

		// get the right URIProcessor for URI

		public static String retrieveTypeURI(ModelSlotInstance msInstance, String objectURI) {

			URI fullURI;
			StringBuffer typeURIStr = new StringBuffer();

			fullURI = URI.create(objectURI);
			typeURIStr.append(fullURI.getScheme()).append("://").append(fullURI.getHost()).append(fullURI.getPath()).append("#")
					.append(fullURI.getFragment());

			return typeURIStr.toString();
		}

	}
}
