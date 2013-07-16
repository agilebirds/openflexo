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
import org.openflexo.foundation.viewpoint.NamedViewPointObject;
import org.openflexo.foundation.viewpoint.PatternRole;
import org.openflexo.foundation.viewpoint.VirtualModel;
import org.openflexo.foundation.viewpoint.VirtualModel.VirtualModelBuilder;
import org.openflexo.technologyadapter.xsd.metamodel.XSDMetaModel;
import org.openflexo.technologyadapter.xsd.metamodel.XSOntClass;
import org.openflexo.technologyadapter.xsd.model.AbstractXSOntObject;
import org.openflexo.technologyadapter.xsd.model.XMLXSDModel;
import org.openflexo.technologyadapter.xsd.model.XSOntIndividual;
import org.openflexo.technologyadapter.xsd.rm.XMLXSDFileResource;
import org.openflexo.technologyadapter.xsd.rm.XSDMetaModelResource;
import org.openflexo.technologyadapter.xsd.viewpoint.XSClassPatternRole;
import org.openflexo.technologyadapter.xsd.viewpoint.XSIndividualPatternRole;
import org.openflexo.technologyadapter.xsd.viewpoint.editionaction.AddXSClass;
import org.openflexo.technologyadapter.xsd.viewpoint.editionaction.AddXSIndividual;

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
public class XSDModelSlot extends TypeAwareModelSlot<XMLXSDModel, XSDMetaModel> {
	static final Logger logger = Logger.getLogger(XSDModelSlot.class.getPackage().getName());

	/* Used to process URIs for XML Objects */
	private Hashtable<String, XSURIProcessor> uriProcessors;

	/*public XSDModelSlot(ViewPoint viewPoint, XSDTechnologyAdapter adapter) {
		super(viewPoint, adapter);
		if (uriProcessors == null)
			uriProcessors = new Hashtable<String, XSURIProcessor>();
	}*/

	public XSDModelSlot(VirtualModel<?> virtualModel, XSDTechnologyAdapter adapter) {
		super(virtualModel, adapter);
		if (uriProcessors == null) {
			uriProcessors = new Hashtable<String, XSURIProcessor>();
		}
	}

	public XSDModelSlot(VirtualModelBuilder builder) {
		super(builder);
		if (uriProcessors == null) {
			uriProcessors = new Hashtable<String, XSURIProcessor>();
		}
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
	 * URI Accessors
	 */

	// TODO Manage the fact that URI May Change


	@Override
	public String getURIForObject(
			TypeSafeModelSlotInstance<XMLXSDModel, XSDMetaModel, ? extends TypeAwareModelSlot<XMLXSDModel, XSDMetaModel>> msInstance, Object o) {
		XSOntIndividual xsO = (XSOntIndividual) o;

		String ltypeURI = ((XSOntClass) xsO.getType()).getURI();
		XSURIProcessor mapParams = uriProcessors.get(ltypeURI);

		if (mapParams != null){
			return mapParams.getURIForObject((TypeSafeModelSlotInstance<XMLXSDModel, XSDMetaModel, XSDModelSlot>) msInstance, xsO);	
		}
		else {
			logger.warning("XSDModelSlot: unable to get the URIProcessor for element of type: "+((XSOntClass) xsO.getType()).getName());
			return null;
		}

	}

	@Override
	public Object retrieveObjectWithURI(TypeSafeModelSlotInstance<XMLXSDModel, XSDMetaModel, ? extends TypeAwareModelSlot<XMLXSDModel, XSDMetaModel>> msInstance, String objectURI) {

		XSURIProcessor mapParams = uriProcessors.get(XSURIProcessor.retrieveTypeURI(msInstance, objectURI));
		if (mapParams != null){
			return mapParams.retrieveObjectWithURI(msInstance, objectURI);
		}
		else 
			return null;
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
		uriProcessors.put(xsuriProc.typeURI.toString(), xsuriProc);
	}

	public void removeFromUriProcessors(XSURIProcessor xsuriProc) {
		uriProcessors.remove(xsuriProc.typeURI.toString());
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
		return XMLXSDModel.class;
	}

	@Override
	public XSDTechnologyAdapter getTechnologyAdapter() {
		return (XSDTechnologyAdapter) super.getTechnologyAdapter();
	}

	@Override
	public XMLXSDFileResource createProjectSpecificEmptyModel(View view, String filename, String modelUri,
			FlexoMetaModelResource<XMLXSDModel, XSDMetaModel> metaModelResource) {
		return getTechnologyAdapter().createNewXMLFile(view.getProject(), filename, modelUri, metaModelResource);
	}

	@Override
	public XMLXSDFileResource createSharedEmptyModel(FlexoResourceCenter<?> resourceCenter, String relativePath, String filename,
			String modelUri, FlexoMetaModelResource<XMLXSDModel, XSDMetaModel> metaModelResource) {
		return (XMLXSDFileResource) getTechnologyAdapter().createNewXMLFile((FileSystemBasedResourceCenter) resourceCenter, relativePath, filename, modelUri,
				(XSDMetaModelResource) metaModelResource);
	}

	@Override
	public boolean isStrictMetaModelling() {
		return true;
	}

}
