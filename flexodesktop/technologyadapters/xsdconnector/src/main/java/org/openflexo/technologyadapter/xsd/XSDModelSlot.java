/*
 * (c) Copyright 2010-2012 AgileBirds
 * (c) Copyright 2013 Openflexo
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

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.logging.Logger;

import org.openflexo.foundation.resource.FileSystemBasedResourceCenter;
import org.openflexo.foundation.resource.FlexoResourceCenter;
import org.openflexo.foundation.technologyadapter.DeclareEditionAction;
import org.openflexo.foundation.technologyadapter.DeclareEditionActions;
import org.openflexo.foundation.technologyadapter.DeclareFetchRequests;
import org.openflexo.foundation.technologyadapter.DeclarePatternRole;
import org.openflexo.foundation.technologyadapter.DeclarePatternRoles;
import org.openflexo.foundation.technologyadapter.FlexoMetaModelResource;
import org.openflexo.foundation.technologyadapter.TypeAwareModelSlot;
import org.openflexo.foundation.view.TypeSafeModelSlotInstance;
import org.openflexo.foundation.view.View;
import org.openflexo.foundation.view.action.CreateVirtualModelInstance;
import org.openflexo.foundation.viewpoint.EditionAction;
import org.openflexo.foundation.viewpoint.FetchRequest;
import org.openflexo.foundation.viewpoint.PatternRole;
import org.openflexo.foundation.viewpoint.VirtualModel;
import org.openflexo.foundation.viewpoint.VirtualModel.VirtualModelBuilder;
import org.openflexo.technologyadapter.xsd.model.AbstractXSOntObject;
import org.openflexo.technologyadapter.xsd.model.XMLModel;
import org.openflexo.technologyadapter.xsd.model.XSDMetaModel;
import org.openflexo.technologyadapter.xsd.model.XSOntClass;
import org.openflexo.technologyadapter.xsd.model.XSOntIndividual;
import org.openflexo.technologyadapter.xsd.model.XSOntRestriction;
import org.openflexo.technologyadapter.xsd.rm.XMLModelResource;
import org.openflexo.technologyadapter.xsd.rm.XSDMetaModelResource;
import org.openflexo.technologyadapter.xsd.viewpoint.XSClassPatternRole;
import org.openflexo.technologyadapter.xsd.viewpoint.XSIndividualPatternRole;
import org.openflexo.technologyadapter.xsd.viewpoint.editionaction.AddXSClass;
import org.openflexo.technologyadapter.xsd.viewpoint.editionaction.AddXSIndividual;
import org.openflexo.xmlcode.XMLSerializable;

/**
 * Implementation of the ModelSlot class for the XSD/XML technology adapter
 * 
 * @author Luka Le Roux, Sylvain Guerin, Christophe Guychard
 * 
 */
@DeclarePatternRoles({ // All pattern roles available through this model slot
@DeclarePatternRole(FML = "XSIndividual", patternRoleClass = XSIndividualPatternRole.class),
		@DeclarePatternRole(FML = "XSClass", patternRoleClass = XSClassPatternRole.class), })
@DeclareEditionActions({ // All edition actions available through this model slot
@DeclareEditionAction(FML = "AddXSIndividual", editionActionClass = AddXSIndividual.class),
		@DeclareEditionAction(FML = "AddXSClass", editionActionClass = AddXSClass.class) })
@DeclareFetchRequests({ // All requests available through this model slot
})
public class XSDModelSlot extends TypeAwareModelSlot<XMLModel, XSDMetaModel> {

	private static final Logger logger = Logger.getLogger(XSDModelSlot.class.getPackage().getName());

	/* Used to process URIs for XML Objects */
	private Hashtable<String, XSURIProcessor> uriProcessors;

	/*public XSDModelSlot(ViewPoint viewPoint, XSDTechnologyAdapter adapter) {
		super(viewPoint, adapter);
		if (uriProcessors == null)
			uriProcessors = new Hashtable<String, XSURIProcessor>();
	}*/

	public XSDModelSlot(VirtualModel<?> virtualModel, XSDTechnologyAdapter adapter) {
		super(virtualModel, adapter);
		if (uriProcessors == null)
			uriProcessors = new Hashtable<String, XSURIProcessor>();
	}

	public XSDModelSlot(VirtualModelBuilder builder) {
		super(builder);
		if (uriProcessors == null)
			uriProcessors = new Hashtable<String, XSURIProcessor>();
	}

	/*public XSDModelSlot(ViewPointBuilder builder) {
		super(builder);
		if (uriProcessors == null)
			uriProcessors = new Hashtable<String, XSURIProcessor>();
	}*/

	@Override
	public Class<XSDTechnologyAdapter> getTechnologyAdapterClass() {
		return XSDTechnologyAdapter.class;
	}

	/**
	 * Instanciate a new model slot instance configuration for this model slot
	 */
	@Override
	public XSDModelSlotInstanceConfiguration createConfiguration(CreateVirtualModelInstance<?> action) {
		return new XSDModelSlotInstanceConfiguration(this, action);
	}

	/**
	 * Creates and return a new {@link PatternRole} of supplied class.<br>
	 * This responsability is delegated to the OWL-specific {@link OWLModelSlot} which manages with introspection its own
	 * {@link PatternRole} types related to OWL technology
	 * 
	 * @param patternRoleClass
	 * @return
	 */
	@Override
	public <PR extends PatternRole<?>> PR makePatternRole(Class<PR> patternRoleClass) {
		if (XSClassPatternRole.class.isAssignableFrom(patternRoleClass)) {
			return (PR) new XSClassPatternRole(null);
		} else if (XSIndividualPatternRole.class.isAssignableFrom(patternRoleClass)) {
			return (PR) new XSIndividualPatternRole(null);
		}
		logger.warning("Unexpected pattern role: " + patternRoleClass.getName());
		return null;
	}

	@Override
	public <PR extends PatternRole<?>> String defaultPatternRoleName(Class<PR> patternRoleClass) {
		if (XSClassPatternRole.class.isAssignableFrom(patternRoleClass)) {
			return "class";
		} else if (XSIndividualPatternRole.class.isAssignableFrom(patternRoleClass)) {
			return "individual";
		}
		return super.defaultPatternRoleName(patternRoleClass);
	}

	/**
	 * Creates and return a new {@link EditionAction} of supplied class.<br>
	 * This responsability is delegated to the XSD-specific {@link XSDModelSlot} which manages with introspection its own
	 * {@link EditionAction} types related to XSD/XML technology
	 * 
	 * @param editionActionClass
	 * @return
	 */
	@Override
	public <EA extends EditionAction<?, ?>> EA makeEditionAction(Class<EA> editionActionClass) {
		if (AddXSIndividual.class.isAssignableFrom(editionActionClass)) {
			return (EA) new AddXSIndividual(null);
		} else if (AddXSClass.class.isAssignableFrom(editionActionClass)) {
			return (EA) new AddXSClass(null);
		} else {
			return null;
		}
	}

	@Override
	public <FR extends FetchRequest<?, ?>> FR makeFetchRequest(Class<FR> fetchRequestClass) {
		return null;
	}

	/*=====================================================================================
	 * ================ inner class XSURIProcessor ========================================
	  =====================================================================================*/

	/* Correct processing of XML Objects URIs needs to add an internal class to store
	 * for each XSOntClass wich are the XML Elements (attributes or CDATA, or...) that will be 
	 * used to calculate URIs
	 */

	public static class XSURIProcessor implements XMLSerializable {

		// mapping styles enumeration

		public static final String ATTRIBUTE_VALUE = "attribute";

		// Properties actually used to calculate URis

		private XSOntClass mappedClass;
		// private XSOntProperty baseAttributeForURI; Not sure we need this
		private XSDModelSlot modelSlot;

		// c

		public void setModelSlot(XSDModelSlot xsdModelSlot) {
			modelSlot = xsdModelSlot;
		}

		// Serialized properties

		private String typeURI;
		private String mappingStyle;
		private String attributeName;

		public String _getTypeURI() {
			if (mappedClass != null) {
				return mappedClass.getURI();
			} else {
				this.bindtypeURIToMappedClass();
				return typeURI;
			}
		}

		public void _setTypeURI(String name) {
			typeURI = name;
			bindtypeURIToMappedClass();
		}

		public String _getMappingStyle() {
			return mappingStyle;
		}

		public void _setMappingStyle(String mappingStyle) {
			this.mappingStyle = mappingStyle;
		}

		public String _getAttributeName() {
			return attributeName;
		}

		public void _setAttributeName(String attributeName) {
			this.attributeName = attributeName;
		}

		// Lifecycle management methods
		public void reset() {
			modelSlot = null;
			mappedClass = null;
			mappingStyle = null;
			// baseAttributeForURI = null;
		}

		public void bindtypeURIToMappedClass() {
			if (modelSlot != null) {
				String mmURI = modelSlot.getMetaModelURI();
				if (mmURI != null) {
					XSDMetaModelResource mmResource = (XSDMetaModelResource) modelSlot.getMetaModelResource();
					if (mmResource != null) {
						mappedClass = mmResource.getMetaModelData().getClass(typeURI);
						/* TODO Retrieve the attribues for that Class */
						/* if (attributeName != null) 
						 
							baseAttributeForURI = (XSOntProperty) mappedClass.getPropertyNamed(attributeName);
							*/
						// mappedClass.getFeatureAssociations()
					} else {
						logger.warning("unable to map typeURI to an OntClass, as metaModelResource is Null ");
					}
				} else
					mappedClass = null;
			} else {
				logger.warning("unable to map typeURI to an OntClass, as modelSlot is Null ");
			}
		}

		public XSURIProcessor() {
			super();
		}

		public XSURIProcessor(String typeURI) {
			super();
			this.typeURI = typeURI;
		}

		// URI Calculation

		public String processURI(TypeSafeModelSlotInstance<XMLModel, XSDMetaModel, XSDModelSlot> msInstance, AbstractXSOntObject xsO) {
			// if processor not initialized
			if (mappedClass == null) {
				bindtypeURIToMappedClass();
			}
			// processor should be initialized
			if (mappedClass == null) {
				logger.warning("Cannot process URI as URIProcessor is not initialized for that class: " + typeURI);
				return null;
			} else {
				if (attributeName != null) {

					// TODO FlexoProperty property = mappedClass.

					XSOntRestriction restriction = mappedClass.getFeatureAssociationNamed(attributeName);
					logger.info("to stop");
					Object value = ((XSOntIndividual) xsO).getPropertyValue(restriction.getProperty());
					return msInstance.getModelURI() + "#" + (String) value;

				} else
					logger.warning("Cannot process URI - Unexpected or Unspecified mapping parameters");
				return null;
			}
		}

	}

	/*=====================================================================================
	* URI Accessors
	*/

	@Override
	public String getURIForObject(
			TypeSafeModelSlotInstance<XMLModel, XSDMetaModel, ? extends TypeAwareModelSlot<XMLModel, XSDMetaModel>> msInstance, Object o) {
		XSOntIndividual xsO = (XSOntIndividual) o;

		String typeURI = xsO.getType().getURI().replace(this.getMetaModelURI(), "");
		XSURIProcessor mapParams = uriProcessors.get(typeURI);

		return mapParams.processURI((TypeSafeModelSlotInstance<XMLModel, XSDMetaModel, XSDModelSlot>) msInstance, xsO);
	}

	@Override
	public Object retrieveObjectWithURI(
			TypeSafeModelSlotInstance<XMLModel, XSDMetaModel, ? extends TypeAwareModelSlot<XMLModel, XSDMetaModel>> msInstance,
			String objectURI) {
		return msInstance.getResourceData().getObject(objectURI);
	}

	// ==========================================================================
	// ============================== uriProcessors Map ===================
	// ==========================================================================

	public void setUriProcessors(Hashtable<String, XSURIProcessor> uriProcessingParameters) {
		this.uriProcessors = uriProcessingParameters;
	}

	public Hashtable<String, XSURIProcessor> getUriProcessors() {
		return uriProcessors;
	}

	public void addToUriProcessors(XSURIProcessor xsuriProc) {
		xsuriProc.setModelSlot(this);
		uriProcessors.put(xsuriProc.typeURI, xsuriProc);
	}

	public void removeFromUriProcessors(XSURIProcessor xsuriProc) {
		uriProcessors.remove(xsuriProc.typeURI);
		xsuriProc.reset();
	}

	// Do not use this since not efficient, used in deserialization only
	public List<XSURIProcessor> getUriProcessorsList() {
		List<XSURIProcessor> returned = new ArrayList<XSURIProcessor>();
		for (XSURIProcessor xsuriProc : uriProcessors.values()) {
			returned.add(xsuriProc);
		}
		return returned;
	}

	public void setUriProcessorsList(List<XSURIProcessor> uriProcList) {
		for (XSURIProcessor xsuriProc : uriProcList) {
			addToUriProcessorsList(xsuriProc);
		}
	}

	public void addToUriProcessorsList(XSURIProcessor xsuriProc) {
		addToUriProcessors(xsuriProc);
	}

	public void removeFromUriProcessorsList(XSURIProcessor xsuriProc) {
		removeFromUriProcessors(xsuriProc);
	}

	@Override
	public Type getType() {
		return XMLModel.class;
	}

	@Override
	public XSDTechnologyAdapter getTechnologyAdapter() {
		return (XSDTechnologyAdapter) super.getTechnologyAdapter();
	}

	@Override
	public XMLModelResource createProjectSpecificEmptyModel(View view, String filename, String modelUri,
			FlexoMetaModelResource<XMLModel, XSDMetaModel> metaModelResource) {
		return getTechnologyAdapter().createNewXMLFile(view.getProject(), filename, modelUri, metaModelResource);
	}

	@Override
	public XMLModelResource createSharedEmptyModel(FlexoResourceCenter<?> resourceCenter, String relativePath, String filename,
			String modelUri, FlexoMetaModelResource<XMLModel, XSDMetaModel> metaModelResource) {
		return getTechnologyAdapter().createNewXMLFile((FileSystemBasedResourceCenter) resourceCenter, relativePath, filename, modelUri,
				(XSDMetaModelResource) metaModelResource);
	}

}
