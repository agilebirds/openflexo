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

package org.openflexo.technologyadapter.xml;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import org.openflexo.antar.binding.BindingModel;
import org.openflexo.foundation.technologyadapter.FlexoModelResource;
import org.openflexo.foundation.technologyadapter.ModelSlot;
import org.openflexo.foundation.technologyadapter.TypeAwareModelSlot;
import org.openflexo.foundation.validation.Validable;
import org.openflexo.foundation.view.ModelSlotInstance;
import org.openflexo.foundation.viewpoint.FMLRepresentationContext;
import org.openflexo.foundation.viewpoint.NamedViewPointObject;
import org.openflexo.foundation.viewpoint.ViewPoint;
import org.openflexo.technologyadapter.xml.model.IXMLAttribute;
import org.openflexo.technologyadapter.xml.model.IXMLIndividual;
import org.openflexo.technologyadapter.xml.model.IXMLType;
import org.openflexo.technologyadapter.xml.model.XMLIndividual;
import org.openflexo.technologyadapter.xml.model.XMLModel;
import org.openflexo.technologyadapter.xml.rm.XMLFileResource;

/* Correct processing of XML Objects URIs needs to add an internal class to store
 * for each XMLType wich are the XML Elements (attributes or CDATA, or...) that will be 
 * used to calculate URIs
 */

// TODO Manage the fact that URI May Change

public interface XMLURIProcessor extends NamedViewPointObject {

	public enum MappingStyle {
		ATTRIBUTE_VALUE, SINGLETON;
	}

	public void setModelSlot(ModelSlot aModelSlot);

	public TypeAwareModelSlot<?, ?> getModelSlot();

	public abstract static class XMLURIProcessorImpl extends NamedViewPointObjectImpl {

		private static final Logger logger = Logger.getLogger(XMLURIProcessor.class.getPackage().getName());

		// mapping styles enumeration

		// Properties actually used to calculate URis

		// TODO Change to a common Type for XML & XMLXSD
		private IXMLType mappedClass;
		private TypeAwareModelSlot<?, ?> modelSlot;
		private IXMLAttribute baseAttributeForURI;

		// Cache des URis Pour aller plus vite ??
		// TODO some optimization required
		private final Map<String, XMLIndividual> uriCache = new HashMap<String, XMLIndividual>();

		// Serialized properties

		protected URI typeURI;
		protected MappingStyle mappingStyle;
		protected String attributeName;

		public XMLURIProcessorImpl(String typeURI) {
			super();
			this.typeURI = URI.create(typeURI);
		}

		public void setModelSlot(ModelSlot aModelSlot) {
			modelSlot = (TypeAwareModelSlot<?, ?>) aModelSlot;
		}

		public TypeAwareModelSlot<?, ?> getModelSlot() {
			return modelSlot;
		}

		public String _getTypeURI() {
			if (mappedClass != null) {
				// FIXME : update _typeURI si on supprime le champs...
				// Parce que mappedClass doit rester prioritaire partout.
				return mappedClass.getURI();
			} else {
				this.bindtypeURIToMappedClass();
				return typeURI.toString();
			}
		}

		// TODO WARNING!!! Pb avec les typeURI....
		public void _setTypeURI(String name) {
			typeURI = URI.create(name);
			bindtypeURIToMappedClass();
		}

		public MappingStyle getMappingStyle() {
			return mappingStyle;
		}

		public void setMappingStyle(MappingStyle mappingStyle) {
			this.mappingStyle = mappingStyle;
		}

		public String _getAttributeName() {
			return attributeName;
		}

		public void _setAttributeName(String attributeName) {
			this.attributeName = attributeName;
		}

		public IXMLAttribute getBaseAttributeForURI() {
			return baseAttributeForURI;
		}

		public void setBaseAttributeForURI(IXMLAttribute baseAttributeForURI) {
			this.baseAttributeForURI = baseAttributeForURI;
			if (this.baseAttributeForURI != null) {
				this.attributeName = this.baseAttributeForURI.getName();
			}
		}

		public IXMLType getMappedClass() {
			return mappedClass;
		}

		public void setMappedClass(IXMLType mappedClass) {
			this.mappedClass = mappedClass;
		}

		// Lifecycle management methods
		public void reset() {
			modelSlot = null;
			mappedClass = null;
			mappingStyle = null;
		}

		public void bindtypeURIToMappedClass() {
			if (modelSlot != null) {
				String mmURI = modelSlot.getMetaModelURI();
				if (mmURI != null) {
					// FIXME : to be re-factored
					XMLFileResource mmResource = (XMLFileResource) modelSlot.getMetaModelResource();
					if (mmResource != null) {
						mappedClass = mmResource.getModelData().getTypeFromURI(typeURI.toString());
					} else {
						logger.warning("unable to map typeURI to an XMLType, as metaModelResource is Null ");
					}
				} else
					mappedClass = null;
			} else {
				logger.warning("unable to map typeURI to an XMLType, as modelSlot is Null ");
			}
		}

		// URI Calculation

		public String getURIForObject(ModelSlotInstance msInstance, XMLIndividual xsO) {
			String builtURI = null;
			StringBuffer completeURIStr = new StringBuffer();

			// if processor not initialized
			if (mappedClass == null) {
				bindtypeURIToMappedClass();
			}
			// processor should be initialized
			if (mappedClass == null) {
				logger.warning("Cannot process URI as URIProcessor is not initialized for that class: " + typeURI);
				return null;
			} else {
				if (mappingStyle == MappingStyle.ATTRIBUTE_VALUE && attributeName != null) {

					Object value = ((IXMLIndividual) xsO).getAttributeValue(attributeName);
					try {
						builtURI = URLEncoder.encode(value.toString(), "UTF-8");
					} catch (UnsupportedEncodingException e) {
						logger.warning("Cannot process URI - Unexpected encoding error");
						e.printStackTrace();
					}
				} else if (mappingStyle == MappingStyle.SINGLETON) {
					// TODO singleton here
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
		public Object retrieveObjectWithURI(ModelSlotInstance msInstance, String objectURI) throws Exception {

			IXMLIndividual o = uriCache.get(objectURI);

			// if processor not initialized
			if (mappedClass == null) {
				bindtypeURIToMappedClass();
			}

			// modelResource must also be loaded!

			FlexoModelResource resource = (FlexoModelResource) msInstance.getResource();
			if (!resource.isLoaded()) {
				resource.getModelData();
			}

			// retrieve object
			if (o == null) {

				if (mappingStyle == MappingStyle.ATTRIBUTE_VALUE && attributeName != null) {

					for (IXMLIndividual obj : ((XMLModel) msInstance.getAccessedResourceData()).getIndividualsOfType(mappedClass)) {

						Object value = obj.getAttributeValue(attributeName);
						try {
							if (value.equals(URLDecoder.decode(objectURI, "UTF-8"))) {
								return obj;
							}
						} catch (UnsupportedEncodingException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}

				} else {
					logger.warning("Cannot process URI - Unexpected or Unspecified mapping parameters");
				}
			}

			return o;
		}

		// get the right URIProcessor for URI

		static public String retrieveTypeURI(ModelSlotInstance msInstance, String objectURI) {

			URI fullURI;
			StringBuffer typeURIStr = new StringBuffer();

			fullURI = URI.create(objectURI);
			typeURIStr.append(fullURI.getScheme()).append("://").append(fullURI.getHost()).append(fullURI.getPath()).append("#")
					.append(fullURI.getFragment());

			return typeURIStr.toString();
		}

		// TODO ... To support Notification

		@Override
		public BindingModel getBindingModel() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Collection<? extends Validable> getEmbeddedValidableObjects() {
			return Collections.emptyList();
		}

		@Override
		public String getURI() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public ViewPoint getViewPoint() {
			return this.modelSlot.getViewPoint();
		}

		@Override
		public String getFMLRepresentation(FMLRepresentationContext context) {
			// TODO Auto-generated method stub
			return null;
		}

	}
}