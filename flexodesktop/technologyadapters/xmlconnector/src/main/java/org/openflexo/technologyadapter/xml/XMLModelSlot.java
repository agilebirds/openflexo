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

import java.lang.reflect.Type;
import java.util.logging.Logger;

import org.openflexo.foundation.resource.FlexoResourceCenter;
import org.openflexo.foundation.technologyadapter.DeclareEditionAction;
import org.openflexo.foundation.technologyadapter.DeclareEditionActions;
import org.openflexo.foundation.technologyadapter.DeclarePatternRole;
import org.openflexo.foundation.technologyadapter.DeclarePatternRoles;
import org.openflexo.foundation.technologyadapter.FlexoMetaModelResource;
import org.openflexo.foundation.technologyadapter.FlexoModelResource;
import org.openflexo.foundation.technologyadapter.TechnologyAdapter;
import org.openflexo.foundation.technologyadapter.TypeAwareModelSlot;
import org.openflexo.foundation.view.TypeSafeModelSlotInstance;
import org.openflexo.foundation.view.View;
import org.openflexo.foundation.view.action.CreateVirtualModelInstance;
import org.openflexo.foundation.view.action.ModelSlotInstanceConfiguration;
import org.openflexo.foundation.viewpoint.EditionAction;
import org.openflexo.foundation.viewpoint.FetchRequest;
import org.openflexo.foundation.viewpoint.PatternRole;
import org.openflexo.foundation.viewpoint.VirtualModel;
import org.openflexo.technologyadapter.xml.editionaction.AddXMLIndividual;
import org.openflexo.technologyadapter.xml.model.XMLModel;
import org.openflexo.technologyadapter.xml.viewpoint.XMLIndividualPatternRole;

/**
 * @author xtof
 *
 */
@DeclarePatternRoles({ @DeclarePatternRole(patternRoleClass=XMLIndividualPatternRole.class, FML = "XMLIndividual"), // Instances
})
@DeclareEditionActions({ @DeclareEditionAction(editionActionClass=AddXMLIndividual.class, FML = "AddXMLIndividual"), // Add instance
})
public class XMLModelSlot extends TypeAwareModelSlot<XMLModel,XMLModel> {


	private static final Logger logger = Logger.getLogger(XMLModelSlot.class.getPackage().getName());

	

	protected XMLModelSlot(VirtualModel<?> virtualModel,
			TechnologyAdapter technologyAdapter) {
		super(virtualModel, technologyAdapter);
	}
	
	@Override
	public <PR extends PatternRole<?>> PR makePatternRole(
			Class<PR> patternRoleClass) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Type getType() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Class<? extends TechnologyAdapter> getTechnologyAdapterClass() {
		return XMLTechnologyAdapter.class;
	}


	@Override
	public <EA extends EditionAction<?, ?>> EA makeEditionAction(
			Class<EA> editionActionClass) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <FR extends FetchRequest<?, ?>> FR makeFetchRequest(
			Class<FR> fetchRequestClass) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public FlexoModelResource<XMLModel, XMLModel> createProjectSpecificEmptyModel(
			View view, String filename, String modelUri,
			FlexoMetaModelResource<XMLModel, XMLModel> metaModelResource) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public FlexoModelResource<XMLModel, XMLModel> createSharedEmptyModel(
			FlexoResourceCenter<?> resourceCenter, String relativePath,
			String filename, String modelUri,
			FlexoMetaModelResource<XMLModel, XMLModel> metaModelResource) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getURIForObject(
			TypeSafeModelSlotInstance<XMLModel, XMLModel, ? extends TypeAwareModelSlot<XMLModel, XMLModel>> msInstance,
			Object o) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object retrieveObjectWithURI(
			TypeSafeModelSlotInstance<XMLModel, XMLModel, ? extends TypeAwareModelSlot<XMLModel, XMLModel>> msInstance,
			String objectURI) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ModelSlotInstanceConfiguration<? extends TypeAwareModelSlot<XMLModel, XMLModel>, XMLModel> createConfiguration(
			CreateVirtualModelInstance<?> action) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isStrictMetaModelling() {
		// TODO Auto-generated method stub
		return false;
	}


}
