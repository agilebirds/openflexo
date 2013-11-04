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
package org.openflexo.technologyadapter.powerpoint;

import java.lang.reflect.Type;
import java.util.logging.Logger;

import org.openflexo.foundation.resource.FlexoResourceCenter;
import org.openflexo.foundation.technologyadapter.DeclareEditionAction;
import org.openflexo.foundation.technologyadapter.DeclareEditionActions;
import org.openflexo.foundation.technologyadapter.DeclareFetchRequest;
import org.openflexo.foundation.technologyadapter.DeclareFetchRequests;
import org.openflexo.foundation.technologyadapter.DeclarePatternRole;
import org.openflexo.foundation.technologyadapter.DeclarePatternRoles;
import org.openflexo.foundation.technologyadapter.FreeModelSlot;
import org.openflexo.foundation.technologyadapter.TechnologyAdapterResource;
import org.openflexo.foundation.view.FreeModelSlotInstance;
import org.openflexo.foundation.view.View;
import org.openflexo.foundation.view.action.CreateVirtualModelInstance;
import org.openflexo.foundation.view.action.ModelSlotInstanceConfiguration;
import org.openflexo.foundation.viewpoint.EditionAction;
import org.openflexo.foundation.viewpoint.FetchRequest;
import org.openflexo.foundation.viewpoint.PatternRole;
import org.openflexo.foundation.viewpoint.VirtualModel;
import org.openflexo.foundation.viewpoint.VirtualModel.VirtualModelBuilder;
import org.openflexo.technologyadapter.powerpoint.model.PowerpointObject;
import org.openflexo.technologyadapter.powerpoint.model.PowerpointSlideshow;
import org.openflexo.technologyadapter.powerpoint.rm.PowerpointSlideshowResource;
import org.openflexo.technologyadapter.powerpoint.viewpoint.PowerpointShapePatternRole;
import org.openflexo.technologyadapter.powerpoint.viewpoint.PowerpointSlidePatternRole;
import org.openflexo.technologyadapter.powerpoint.viewpoint.editionaction.AddPowerpointShape;
import org.openflexo.technologyadapter.powerpoint.viewpoint.editionaction.AddPowerpointSlide;
import org.openflexo.technologyadapter.powerpoint.viewpoint.editionaction.SelectPowerpointShape;
import org.openflexo.technologyadapter.powerpoint.viewpoint.editionaction.SelectPowerpointSlide;

/**
 * Implementation of a basic ModelSlot class for the Powerpoint technology adapter<br>
 * 
 * @author Vincent Leildé, Sylvain Guérin
 * 
 */
@DeclarePatternRoles({ // All pattern roles available through this model slot
		@DeclarePatternRole(FML = "PowerpointSlide", patternRoleClass = PowerpointSlidePatternRole.class),
		@DeclarePatternRole(FML = "PowerpointShape", patternRoleClass = PowerpointShapePatternRole.class)
})
@DeclareEditionActions({ // All edition actions available through this model slot
@DeclareEditionAction(FML = "AddPowerpointSlide", editionActionClass = AddPowerpointSlide.class), 
		@DeclareEditionAction(FML = "AddPowerpointShape", editionActionClass = AddPowerpointShape.class)
})
@DeclareFetchRequests({ // All requests available through this model slot
@DeclareFetchRequest(FML = "RemoveReferencePropertyValue", fetchRequestClass = SelectPowerpointSlide.class),
@DeclareFetchRequest(FML = "RemoveReferencePropertyValue", fetchRequestClass = SelectPowerpointShape.class)
})
public class BasicPowerpointModelSlot extends FreeModelSlot<PowerpointSlideshow> {

	private static final Logger logger = Logger.getLogger(BasicPowerpointModelSlot.class.getPackage().getName());

	private BasicPowerpointModelSlotURIProcessor uriProcessor;

	public BasicPowerpointModelSlot(VirtualModel<?> virtualModel, PowerpointTechnologyAdapter adapter) {
		super(virtualModel, adapter);
		uriProcessor = new BasicPowerpointModelSlotURIProcessor();

	}

	public BasicPowerpointModelSlot(VirtualModelBuilder builder) {
		super(builder);
		uriProcessor = new BasicPowerpointModelSlotURIProcessor();
	}

	@Override
	public Class<PowerpointTechnologyAdapter> getTechnologyAdapterClass() {
		return PowerpointTechnologyAdapter.class;
	}

	/**
	 * @param patternRoleClass
	 * @return
	 */
	@Override
	public <PR extends PatternRole<?>> PR makePatternRole(Class<PR> patternRoleClass) {
		if (PowerpointSlidePatternRole.class.isAssignableFrom(patternRoleClass)) {
			return (PR) new PowerpointSlidePatternRole(null);
		} else if (PowerpointShapePatternRole.class.isAssignableFrom(patternRoleClass)) {
			return (PR) new PowerpointShapePatternRole(null);
		} 
		logger.warning("Unexpected pattern role: " + patternRoleClass.getName());
		return null;
	}

	@Override
	public <PR extends PatternRole<?>> String defaultPatternRoleName(Class<PR> patternRoleClass) {
		if (PowerpointSlidePatternRole.class.isAssignableFrom(patternRoleClass)) {
			return "slide";
		} else if (PowerpointShapePatternRole.class.isAssignableFrom(patternRoleClass)) {
			return "shape";
		} 
		return super.defaultPatternRoleName(patternRoleClass);
	}

	/**
	 * @param editionActionClass
	 * @return
	 */
	@Override
	public <EA extends EditionAction<?, ?>> EA makeEditionAction(Class<EA> editionActionClass) {
		if (AddPowerpointSlide.class.isAssignableFrom(editionActionClass)) {
			return (EA) new AddPowerpointSlide(null);
		} else if (AddPowerpointShape.class.isAssignableFrom(editionActionClass)) {
			return (EA) new AddPowerpointShape(null);
		} else {
			return null;
		}
	}

	@Override
	public Type getType() {
		return PowerpointSlideshow.class;
	}

	@Override
	public <FR extends FetchRequest<?, ?>> FR makeFetchRequest(Class<FR> fetchRequestClass) {
		if (SelectPowerpointSlide.class.isAssignableFrom(fetchRequestClass)) {
			return (FR) new SelectPowerpointSlide(null);
		} 
		if (SelectPowerpointShape.class.isAssignableFrom(fetchRequestClass)) {
			return (FR) new SelectPowerpointShape(null);
		}else {
			return null;
		}
	}

	@Override
	public ModelSlotInstanceConfiguration<BasicPowerpointModelSlot, PowerpointSlideshow> createConfiguration(CreateVirtualModelInstance<?> action) {
		return new BasicPowerpointModelSlotInstanceConfiguration(this, action);
	}

	@Override
	public String getURIForObject(FreeModelSlotInstance<PowerpointSlideshow, ? extends FreeModelSlot<PowerpointSlideshow>> msInstance, Object o) {
		PowerpointObject powerpointObject = (PowerpointObject) o;
		return uriProcessor.getURIForObject(msInstance, powerpointObject);
	}

	@Override
	public Object retrieveObjectWithURI(FreeModelSlotInstance<PowerpointSlideshow, ? extends FreeModelSlot<PowerpointSlideshow>> msInstance,
			String objectURI) {

		try {
			return uriProcessor.retrieveObjectWithURI(msInstance, objectURI);

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public PowerpointTechnologyAdapter getTechnologyAdapter() {
		return (PowerpointTechnologyAdapter) super.getTechnologyAdapter();
	}
	
	
	@Override
	public PowerpointSlideshowResource createProjectSpecificEmptyResource(View view, String filename, String modelUri) {
		return getTechnologyAdapter().createNewSlideshow(view.getProject(),filename, modelUri);
	}

	@Override
	public TechnologyAdapterResource<PowerpointSlideshow> createSharedEmptyResource(FlexoResourceCenter<?> resourceCenter, String relativePath,
			String filename, String modelUri) {
		// TODO Auto-generated method stub
		return null;
	}

}
