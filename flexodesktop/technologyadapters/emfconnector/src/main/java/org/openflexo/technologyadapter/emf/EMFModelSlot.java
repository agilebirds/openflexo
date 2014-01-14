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

package org.openflexo.technologyadapter.emf;

import java.lang.reflect.Type;
import java.util.logging.Logger;

import org.openflexo.foundation.ontology.IFlexoOntologyObject;
import org.openflexo.foundation.resource.FileSystemBasedResourceCenter;
import org.openflexo.foundation.resource.FlexoResourceCenter;
import org.openflexo.foundation.technologyadapter.DeclareEditionAction;
import org.openflexo.foundation.technologyadapter.DeclareEditionActions;
import org.openflexo.foundation.technologyadapter.DeclareFetchRequest;
import org.openflexo.foundation.technologyadapter.DeclareFetchRequests;
import org.openflexo.foundation.technologyadapter.DeclarePatternRole;
import org.openflexo.foundation.technologyadapter.DeclarePatternRoles;
import org.openflexo.foundation.technologyadapter.FlexoMetaModelResource;
import org.openflexo.foundation.technologyadapter.TypeAwareModelSlot;
import org.openflexo.foundation.view.TypeAwareModelSlotInstance;
import org.openflexo.foundation.view.View;
import org.openflexo.foundation.view.action.CreateVirtualModelInstance;
import org.openflexo.foundation.viewpoint.EditionAction;
import org.openflexo.foundation.viewpoint.FetchRequest;
import org.openflexo.foundation.viewpoint.PatternRole;
import org.openflexo.foundation.viewpoint.VirtualModel;
import org.openflexo.technologyadapter.emf.metamodel.EMFMetaModel;
import org.openflexo.technologyadapter.emf.model.EMFModel;
import org.openflexo.technologyadapter.emf.rm.EMFMetaModelResource;
import org.openflexo.technologyadapter.emf.rm.EMFModelResource;
import org.openflexo.technologyadapter.emf.viewpoint.EMFClassClassPatternRole;
import org.openflexo.technologyadapter.emf.viewpoint.EMFEnumClassPatternRole;
import org.openflexo.technologyadapter.emf.viewpoint.EMFObjectIndividualPatternRole;
import org.openflexo.technologyadapter.emf.viewpoint.editionaction.AddEMFObjectIndividual;
import org.openflexo.technologyadapter.emf.viewpoint.editionaction.AddEMFObjectIndividualAttributeDataPropertyValue;
import org.openflexo.technologyadapter.emf.viewpoint.editionaction.AddEMFObjectIndividualAttributeObjectPropertyValue;
import org.openflexo.technologyadapter.emf.viewpoint.editionaction.AddEMFObjectIndividualReferenceObjectPropertyValue;
import org.openflexo.technologyadapter.emf.viewpoint.editionaction.RemoveEMFObjectIndividualAttributeDataPropertyValue;
import org.openflexo.technologyadapter.emf.viewpoint.editionaction.RemoveEMFObjectIndividualAttributeObjectPropertyValue;
import org.openflexo.technologyadapter.emf.viewpoint.editionaction.RemoveEMFObjectIndividualReferenceObjectPropertyValue;
import org.openflexo.technologyadapter.emf.viewpoint.editionaction.SelectEMFObjectIndividual;

/**
 * Implementation of the ModelSlot class for the EMF technology adapter<br>
 * We expect here to connect an EMF model conform to an EMFMetaModel
 * 
 * @author sylvain
 * 
 */
@DeclarePatternRoles({ // All pattern roles available through this model slot
@DeclarePatternRole(FML = "EMFObjectIndividual", patternRoleClass = EMFObjectIndividualPatternRole.class),
		@DeclarePatternRole(FML = "EMFClassClass", patternRoleClass = EMFClassClassPatternRole.class),
		@DeclarePatternRole(FML = "EMFEnumClass", patternRoleClass = EMFEnumClassPatternRole.class) })
@DeclareEditionActions({ // All edition actions available through this model slot
@DeclareEditionAction(FML = "AddEMFObjectIndividual", editionActionClass = AddEMFObjectIndividual.class)

// Removed because it is unusable
// @DeclareEditionAction(FML = "AddDataPropertyValue", editionActionClass = AddEMFObjectIndividualAttributeDataPropertyValue.class),
// @DeclareEditionAction(FML = "AddObjectPropertyValue", editionActionClass = AddEMFObjectIndividualAttributeObjectPropertyValue.class),
// @DeclareEditionAction(
// FML = "AddReferencePropertyValue",
// editionActionClass = AddEMFObjectIndividualReferenceObjectPropertyValue.class),
// @DeclareEditionAction(
// FML = "RemoveDataPropertyValue",
// editionActionClass = RemoveEMFObjectIndividualAttributeDataPropertyValue.class),
// @DeclareEditionAction(
// FML = "RemoveObjectPropertyValue",
// editionActionClass = RemoveEMFObjectIndividualAttributeObjectPropertyValue.class),
// @DeclareEditionAction(
// FML = "RemoveReferencePropertyValue",
// editionActionClass = RemoveEMFObjectIndividualReferenceObjectPropertyValue.class)
})
@DeclareFetchRequests({ // All requests available through this model slot
@DeclareFetchRequest(FML = "SelectEMFObjectIndividual", fetchRequestClass = SelectEMFObjectIndividual.class) })
@ModelEntity
@ImplementationClass(EMFModelSlot.EMFModelSlotImpl.class)
@XMLElement
public interface EMFModelSlot extends TypeAwareModelSlot<EMFModel, EMFMetaModel>{


public static abstract  class EMFModelSlotImpl extends TypeAwareModelSlot<EMFModel, EMFMetaModel>Impl implements EMFModelSlot
{

	private static final Logger logger = Logger.getLogger(EMFModelSlot.class.getPackage().getName());

	/**
	 * 
	 * Constructor.
	 * 
	 * @param viewPoint
	 * @param adapter
	 */
	/*public EMFModelSlotImpl(ViewPoint viewPoint, EMFTechnologyAdapter adapter) {
		super(viewPoint, adapter);
	}*/

	/**
	 * 
	 * Constructor.
	 * 
	 * @param virtualModel
	 * @param adapter
	 */
	public EMFModelSlotImpl(VirtualModel virtualModel, EMFTechnologyAdapter adapter) {
		super(virtualModel, adapter);
	}

	/**
	 * 
	 * Constructor.
	 * 
	 * @param builder
	 */
	public EMFModelSlotImpl() {
		super();
	}

	/**
	 * 
	 * Constructor.
	 * 
	 * @param builder
	 */
	/*public EMFModelSlotImpl(ViewPointBuilder builder) {
		super(builder);
	}*/

	@Override
	public Class<EMFTechnologyAdapter> getTechnologyAdapterClass() {
		return EMFTechnologyAdapter.class;
	}

	/**
	 * Instanciate a new model slot instance configuration for this model slot
	 */
	@Override
	public EMFModelSlotImplInstanceConfiguration createConfiguration(CreateVirtualModelInstance<?> action) {
		return new EMFModelSlotInstanceConfiguration(this, action);
	}

	/**
	 * Creates and return a new {@link PatternRole} of supplied class.<br>
	 * This responsability is delegated to the EMF-specific {@link EMFModelSlot} which manages with introspection its own
	 * {@link PatternRole} types related to EMF technology
	 * 
	 * @param patternRoleClass
	 * @return
	 */
	@Override
	public <PR extends PatternRole<?>> PR makePatternRole(Class<PR> patternRoleClass) {
		if (EMFObjectIndividualPatternRole.class.isAssignableFrom(patternRoleClass)) {
			return (PR) new EMFObjectIndividualPatternRole();
		} else if (EMFClassClassPatternRole.class.isAssignableFrom(patternRoleClass)) {
			return (PR) new EMFClassClassPatternRole();
		} else if (EMFEnumClassPatternRole.class.isAssignableFrom(patternRoleClass)) {
			return (PR) new EMFEnumClassPatternRole();
		}
		logger.warning("Unexpected pattern role: " + patternRoleClass.getName());
		return null;
	}

	@Override
	public <PR extends PatternRole<?>> String defaultPatternRoleName(Class<PR> patternRoleClass) {
		if (EMFObjectIndividualPatternRole.class.isAssignableFrom(patternRoleClass)) {
			return "individual";
		}
		return super.defaultPatternRoleName(patternRoleClass);
	}

	@Override
	public <EA extends EditionAction<?, ?>> EA makeEditionAction(Class<EA> editionActionClass) {
		if (AddEMFObjectIndividual.class.isAssignableFrom(editionActionClass)) {
			return (EA) new AddEMFObjectIndividual();
		} else if (AddEMFObjectIndividualAttributeDataPropertyValue.class.isAssignableFrom(editionActionClass)) {
			return (EA) new AddEMFObjectIndividualAttributeDataPropertyValue();
		} else if (AddEMFObjectIndividualAttributeObjectPropertyValue.class.isAssignableFrom(editionActionClass)) {
			return (EA) new AddEMFObjectIndividualAttributeObjectPropertyValue();
		} else if (AddEMFObjectIndividualReferenceObjectPropertyValue.class.isAssignableFrom(editionActionClass)) {
			return (EA) new AddEMFObjectIndividualReferenceObjectPropertyValue();
		} else if (RemoveEMFObjectIndividualAttributeDataPropertyValue.class.isAssignableFrom(editionActionClass)) {
			return (EA) new RemoveEMFObjectIndividualAttributeDataPropertyValue();
		} else if (RemoveEMFObjectIndividualAttributeObjectPropertyValue.class.isAssignableFrom(editionActionClass)) {
			return (EA) new RemoveEMFObjectIndividualAttributeObjectPropertyValue();
		} else if (RemoveEMFObjectIndividualReferenceObjectPropertyValue.class.isAssignableFrom(editionActionClass)) {
			return (EA) new RemoveEMFObjectIndividualReferenceObjectPropertyValue();
		}
		return null;
	}

	@Override
	public <FR extends FetchRequest<?, ?>> FR makeFetchRequest(Class<FR> fetchRequestClass) {
		if (SelectEMFObjectIndividual.class.isAssignableFrom(fetchRequestClass)) {
			return (FR) new SelectEMFObjectIndividual();
		}
		return null;
	}

	@Override
	public String getURIForObject(
			TypeAwareModelSlotInstance<EMFModel, EMFMetaModel, ? extends TypeAwareModelSlot<EMFModel, EMFMetaModel>> msInstance, Object o) {
		if (o instanceof IFlexoOntologyObject) {
			return ((IFlexoOntologyObject) o).getURI();
		}
		return null;
	}

	@Override
	public Object retrieveObjectWithURI(
			TypeAwareModelSlotInstance<EMFModel, EMFMetaModel, ? extends TypeAwareModelSlot<EMFModel, EMFMetaModel>> msInstance,
			String objectURI) {
		return msInstance.getResourceData().getObject(objectURI);
	}

	@Override
	public Type getType() {
		return EMFModel.class;
	}

	@Override
	public EMFTechnologyAdapter getTechnologyAdapter() {
		return (EMFTechnologyAdapter) super.getTechnologyAdapter();
	}

	@Override
	public EMFModelResource createProjectSpecificEmptyModel(View view, String filename, String modelUri,
			FlexoMetaModelResource<EMFModel, EMFMetaModel, ?> metaModelResource) {
		return getTechnologyAdapter().createNewEMFModel(view.getProject(), filename, modelUri, (EMFMetaModelResource) metaModelResource);
	}

	@Override
	public EMFModelResource createSharedEmptyModel(FlexoResourceCenter<?> resourceCenter, String relativePath, String filename,
			String modelUri, FlexoMetaModelResource<EMFModel, EMFMetaModel, ?> metaModelResource) {
		return getTechnologyAdapter().createNewEMFModel((FileSystemBasedResourceCenter) resourceCenter, relativePath, filename, modelUri,
				(EMFMetaModelResource) metaModelResource);
	}

	@Override
	public boolean isStrictMetaModelling() {
		return true;
	}

}
}
