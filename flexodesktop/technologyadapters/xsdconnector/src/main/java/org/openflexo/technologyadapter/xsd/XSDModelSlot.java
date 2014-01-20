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

import org.openflexo.foundation.ontology.DuplicateURIException;
import org.openflexo.foundation.resource.FileSystemBasedResourceCenter;
import org.openflexo.foundation.resource.FlexoResourceCenter;
import org.openflexo.foundation.technologyadapter.DeclareEditionAction;
import org.openflexo.foundation.technologyadapter.DeclareEditionActions;
import org.openflexo.foundation.technologyadapter.DeclareFetchRequests;
import org.openflexo.foundation.technologyadapter.DeclarePatternRole;
import org.openflexo.foundation.technologyadapter.DeclarePatternRoles;
import org.openflexo.foundation.technologyadapter.FlexoMetaModelResource;
import org.openflexo.foundation.technologyadapter.TypeAwareModelSlot;
import org.openflexo.foundation.view.TypeAwareModelSlotInstance;
import org.openflexo.foundation.view.View;
import org.openflexo.foundation.view.action.CreateVirtualModelInstance;
import org.openflexo.foundation.viewpoint.PatternRole;
import org.openflexo.foundation.viewpoint.VirtualModel;
import org.openflexo.model.annotations.Adder;
import org.openflexo.model.annotations.Getter;
import org.openflexo.model.annotations.Getter.Cardinality;
import org.openflexo.model.annotations.ImplementationClass;
import org.openflexo.model.annotations.ModelEntity;
import org.openflexo.model.annotations.PropertyIdentifier;
import org.openflexo.model.annotations.Remover;
import org.openflexo.model.annotations.Setter;
import org.openflexo.model.annotations.XMLElement;
import org.openflexo.technologyadapter.xsd.XSURIProcessor.XSURIProcessorImpl;
import org.openflexo.technologyadapter.xsd.metamodel.XSDMetaModel;
import org.openflexo.technologyadapter.xsd.metamodel.XSOntClass;
import org.openflexo.technologyadapter.xsd.model.XMLXSDModel;
import org.openflexo.technologyadapter.xsd.model.XSOntIndividual;
import org.openflexo.technologyadapter.xsd.rm.XMLXSDFileResource;
import org.openflexo.technologyadapter.xsd.rm.XSDMetaModelResource;
import org.openflexo.technologyadapter.xsd.viewpoint.XSClassPatternRole;
import org.openflexo.technologyadapter.xsd.viewpoint.XSIndividualPatternRole;
import org.openflexo.technologyadapter.xsd.viewpoint.editionaction.AddXSClass;
import org.openflexo.technologyadapter.xsd.viewpoint.editionaction.AddXSIndividual;
import org.openflexo.technologyadapter.xsd.viewpoint.editionaction.GetXMLDocumentRoot;
import org.openflexo.technologyadapter.xsd.viewpoint.editionaction.SetXMLDocumentRoot;

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
		@DeclareEditionAction(FML = "SetXMLDocumentRoot", editionActionClass = SetXMLDocumentRoot.class),
		@DeclareEditionAction(FML = "GetXMLDocumentRoot", editionActionClass = GetXMLDocumentRoot.class),
		@DeclareEditionAction(FML = "AddXSClass", editionActionClass = AddXSClass.class) })
@DeclareFetchRequests({ // All requests available through this model slot
})
@ModelEntity
@ImplementationClass(XSDModelSlot.XSDModelSlotImpl.class)
@XMLElement
public interface XSDModelSlot extends TypeAwareModelSlot<XMLXSDModel, XSDMetaModel> {

	@PropertyIdentifier(type = List.class)
	public static final String URI_PROCESSORS_LIST_KEY = "uriProcessorsList";

	@Getter(value = URI_PROCESSORS_LIST_KEY, cardinality = Cardinality.LIST)
	@XMLElement
	public List<XSURIProcessor> getUriProcessorsList();

	@Setter(URI_PROCESSORS_LIST_KEY)
	public void setUriProcessorsList(List<XSURIProcessor> uriProcessorsList);

	@Adder(URI_PROCESSORS_LIST_KEY)
	public void addToUriProcessorsList(XSURIProcessor aUriProcessorsList);

	@Remover(URI_PROCESSORS_LIST_KEY)
	public void removeFromUriProcessorsList(XSURIProcessor aUriProcessorsList);

	@Override
	public XSDTechnologyAdapter getTechnologyAdapter();

	public static abstract class XSDModelSlotImpl extends TypeAwareModelSlotImpl<XMLXSDModel, XSDMetaModel> implements XSDModelSlot {
		static final Logger logger = Logger.getLogger(XSDModelSlot.class.getPackage().getName());

		/* Used to process URIs for XML Objects */
		private List<XSURIProcessor> uriProcessors;
		private Hashtable<String, XSURIProcessor> uriProcessorsMap;

		/*public XSDModelSlotImpl(ViewPoint viewPoint, XSDTechnologyAdapter adapter) {
			super(viewPoint, adapter);
			if (uriProcessors == null)
				uriProcessors = new Hashtable<String, XSURIProcessor>();
		}*/

		public XSDModelSlotImpl(VirtualModel virtualModel, XSDTechnologyAdapter adapter) {
			super(virtualModel, adapter);
			if (uriProcessorsMap == null) {
				uriProcessorsMap = new Hashtable<String, XSURIProcessor>();
			}
			if (uriProcessors == null) {
				uriProcessors = new ArrayList<XSURIProcessor>();
			}
		}

		public XSDModelSlotImpl() {
			super(null, null);
			if (uriProcessorsMap == null) {
				uriProcessorsMap = new Hashtable<String, XSURIProcessor>();
			}
			if (uriProcessors == null) {
				uriProcessors = new ArrayList<XSURIProcessor>();
			}
		}

		/*public XSDModelSlotImpl(ViewPointBuilder builder) {
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

		@Override
		public <PR extends PatternRole<?>> String defaultPatternRoleName(Class<PR> patternRoleClass) {
			if (XSClassPatternRole.class.isAssignableFrom(patternRoleClass)) {
				return "class";
			} else if (XSIndividualPatternRole.class.isAssignableFrom(patternRoleClass)) {
				return "individual";
			}
			return super.defaultPatternRoleName(patternRoleClass);
		}

		/*=====================================================================================
		 * URI Accessors
		 */
		// TODO Manage the fact that URI May Change

		@Override
		public String getURIForObject(
				TypeAwareModelSlotInstance<XMLXSDModel, XSDMetaModel, ? extends TypeAwareModelSlot<XMLXSDModel, XSDMetaModel>> msInstance,
				Object o) {
			XSOntIndividual xsO = (XSOntIndividual) o;

			XSOntClass lClass = ((XSOntClass) xsO.getType());
			XSURIProcessor mapParams = retrieveURIProcessorForType(lClass);

			if (mapParams != null) {
				return mapParams.getURIForObject(msInstance, xsO);
			} else {
				logger.warning("XSDModelSlot: unable to get the URIProcessor for element of type: "
						+ ((XSOntClass) xsO.getType()).getName());
				return null;
			}

		}

		public XSURIProcessor retrieveURIProcessorForType(XSOntClass aClass) {

			logger.info("SEARCHING for an uriProcessor for " + aClass.getURI());

			XSURIProcessor mapParams = uriProcessorsMap.get(aClass.getURI());

			if (mapParams == null) {
				for (XSOntClass s : aClass.getSuperClasses()) {
					if (mapParams == null) {
						// on ne cherche que le premier...
						logger.info("SEARCHING for an uriProcessor for " + s.getURI());
						if (mapParams == null) {
							mapParams = retrieveURIProcessorForType(s);
						}
					}
				}
				if (mapParams != null) {
					logger.info("UPDATING the MapUriProcessors for an uriProcessor for " + aClass.getURI());
					uriProcessorsMap.put(aClass.getURI(), mapParams);
				}
			}
			return mapParams;
		}

		@Override
		public Object retrieveObjectWithURI(
				TypeAwareModelSlotInstance<XMLXSDModel, XSDMetaModel, ? extends TypeAwareModelSlot<XMLXSDModel, XSDMetaModel>> msInstance,
				String objectURI) {
			String typeUri = XSURIProcessorImpl.retrieveTypeURI(msInstance, objectURI);
			XMLXSDModel model = msInstance.getModel();
			XSURIProcessor mapParams = uriProcessorsMap.get(XSURIProcessorImpl.retrieveTypeURI(msInstance, objectURI));
			if (mapParams == null) {
				// Look for a processor in superClasses
				XSOntClass aClass = (XSOntClass) model.getMetaModel().getTypeFromURI(typeUri);
				mapParams = retrieveURIProcessorForType(aClass);
			}

			if (mapParams != null) {
				try {
					return mapParams.retrieveObjectWithURI(msInstance, objectURI);
				} catch (DuplicateURIException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

			return null;
		}

		// ==========================================================================
		// ============================== uriProcessors Map ===================
		// ==========================================================================

		public void setUriProcessors(List<XSURIProcessor> uriProcessingParameters) {
			this.uriProcessors = uriProcessingParameters;
		}

		public List<XSURIProcessor> getUriProcessors() {
			return uriProcessors;
		}

		public XSURIProcessor createURIProcessor() {
			XSURIProcessor xsuriProc = getVirtualModelFactory().newInstance(XSURIProcessor.class);
			xsuriProc.setModelSlot(this);
			uriProcessors.add(xsuriProc);
			return xsuriProc;
		}

		public void updateURIMapForProcessor(XSURIProcessor xsuriProc) {
			String uri = xsuriProc._getTypeURI();
			if (uri != null) {
				for (String k : uriProcessorsMap.keySet()) {
					XSURIProcessor p = uriProcessorsMap.get(k);
					if (p.equals(xsuriProc)) {
						uriProcessorsMap.remove(k);
					}
				}
				uriProcessorsMap.put(uri, xsuriProc);
			}
		}

		public void addToUriProcessors(XSURIProcessor xsuriProc) {
			xsuriProc.setModelSlot(this);
			uriProcessors.add(xsuriProc);
			uriProcessorsMap.put(xsuriProc._getTypeURI().toString(), xsuriProc);
		}

		public void removeFromUriProcessors(XSURIProcessor xsuriProc) {
			String uri = xsuriProc._getTypeURI();
			if (uri != null) {
				for (String k : uriProcessorsMap.keySet()) {
					XSURIProcessor p = uriProcessorsMap.get(k);
					if (p.equals(xsuriProc)) {
						uriProcessorsMap.remove(k);
					}
				}
				uriProcessors.remove(xsuriProc);
				xsuriProc.reset();
			}
		}

		// Do not use this since not efficient, used in deserialization only
		@Override
		public List<XSURIProcessor> getUriProcessorsList() {
			return uriProcessors;
		}

		@Override
		public void setUriProcessorsList(List<XSURIProcessor> uriProcList) {
			for (XSURIProcessor xsuriProc : uriProcList) {
				addToUriProcessorsList(xsuriProc);
			}
		}

		@Override
		public void addToUriProcessorsList(XSURIProcessor xsuriProc) {
			addToUriProcessors(xsuriProc);
		}

		@Override
		public void removeFromUriProcessorsList(XSURIProcessor xsuriProc) {
			removeFromUriProcessors(xsuriProc);
		}

		@Override
		public Type getType() {
			return XMLXSDModel.class;
		}

		@Override
		public XSDTechnologyAdapter getTechnologyAdapter() {
			return (XSDTechnologyAdapter) super.getTechnologyAdapter();
		}

		@Override
		public XMLXSDFileResource createProjectSpecificEmptyModel(View view, String filename, String modelUri,
				FlexoMetaModelResource<XMLXSDModel, XSDMetaModel, ?> metaModelResource) {
			return getTechnologyAdapter().createNewXMLFile(view.getProject(), filename, modelUri, metaModelResource);
		}

		@Override
		public XMLXSDFileResource createSharedEmptyModel(FlexoResourceCenter<?> resourceCenter, String relativePath, String filename,
				String modelUri, FlexoMetaModelResource<XMLXSDModel, XSDMetaModel, ?> metaModelResource) {
			return (XMLXSDFileResource) getTechnologyAdapter().createNewXMLFile((FileSystemBasedResourceCenter) resourceCenter,
					relativePath, filename, modelUri, (XSDMetaModelResource) metaModelResource);
		}

		@Override
		public boolean isStrictMetaModelling() {
			return true;
		}

	}
}
