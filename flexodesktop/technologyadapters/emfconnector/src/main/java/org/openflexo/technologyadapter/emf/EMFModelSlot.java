/*
 * (c) Copyright 2010-2011 AgileBirds
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

import java.util.logging.Logger;

import org.openflexo.antar.binding.Bindable;
import org.openflexo.antar.binding.BindingVariable;
import org.openflexo.foundation.technologyadapter.DeclareEditionAction;
import org.openflexo.foundation.technologyadapter.DeclareEditionActions;
import org.openflexo.foundation.technologyadapter.DeclarePatternRole;
import org.openflexo.foundation.technologyadapter.DeclarePatternRoles;
import org.openflexo.foundation.technologyadapter.FlexoOntologyModelSlot;
import org.openflexo.foundation.viewpoint.PatternRole;
import org.openflexo.foundation.viewpoint.ViewPoint;
import org.openflexo.foundation.viewpoint.ViewPoint.ViewPointBuilder;
import org.openflexo.technologyadapter.emf.metamodel.EMFMetaModel;
import org.openflexo.technologyadapter.emf.model.EMFModel;
import org.openflexo.technologyadapter.emf.viewpoint.EMFObjectIndividualPatternRole;
import org.openflexo.technologyadapter.emf.viewpoint.editionaction.AddEMFObjectIndividual;
import org.openflexo.technologyadapter.emf.viewpoint.editionaction.AddEMFObjectIndividualAttributeDataPropertyValue;
import org.openflexo.technologyadapter.emf.viewpoint.editionaction.AddEMFObjectIndividualAttributeObjectPropertyValue;
import org.openflexo.technologyadapter.emf.viewpoint.editionaction.AddEMFObjectIndividualReferenceObjectPropertyValue;
import org.openflexo.technologyadapter.emf.viewpoint.editionaction.RemoveEMFObjectIndividualAttributeDataPropertyValue;
import org.openflexo.technologyadapter.emf.viewpoint.editionaction.RemoveEMFObjectIndividualAttributeObjectPropertyValue;
import org.openflexo.technologyadapter.emf.viewpoint.editionaction.RemoveEMFObjectIndividualReferenceObjectPropertyValue;

/**
 * Implementation of the ModelSlot class for the EMF technology adapter
 * 
 * @author sylvain
 * 
 */
@DeclarePatternRoles({ @DeclarePatternRole(EMFObjectIndividualPatternRole.class) // Instances
})
@DeclareEditionActions({ @DeclareEditionAction(AddEMFObjectIndividual.class), // Add instance
		@DeclareEditionAction(AddEMFObjectIndividualAttributeDataPropertyValue.class), // Add Attribute Data Value
		@DeclareEditionAction(AddEMFObjectIndividualAttributeObjectPropertyValue.class), // Add Attribute Object Value
		@DeclareEditionAction(AddEMFObjectIndividualReferenceObjectPropertyValue.class), // Add Reference Object Value
		@DeclareEditionAction(RemoveEMFObjectIndividualAttributeDataPropertyValue.class), // Remove Attribute Data Value
		@DeclareEditionAction(RemoveEMFObjectIndividualAttributeObjectPropertyValue.class), // Remove Attribute Object Value
		@DeclareEditionAction(RemoveEMFObjectIndividualReferenceObjectPropertyValue.class) // Remove Reference Object Value
})
public class EMFModelSlot extends FlexoOntologyModelSlot<EMFModel, EMFMetaModel> {

	private static final Logger logger = Logger.getLogger(EMFModelSlot.class.getPackage().getName());

	/**
	 * 
	 * Constructor.
	 * 
	 * @param viewPoint
	 * @param adapter
	 */
	public EMFModelSlot(ViewPoint viewPoint, EMFTechnologyAdapter adapter) {
		super(viewPoint, adapter);
	}

	/**
	 * 
	 * Constructor.
	 * 
	 * @param builder
	 */
	public EMFModelSlot(ViewPointBuilder builder) {
		super(builder);
	}

	@Override
	public Class<EMFTechnologyAdapter> getTechnologyAdapterClass() {
		return EMFTechnologyAdapter.class;
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
		if (EMFObjectIndividualPatternRole.class.isAssignableFrom(patternRoleClass)) {
			return (PR) new EMFObjectIndividualPatternRole(null);
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
	@Deprecated
	public BindingVariable makePatternRolePathElement(PatternRole<?> pr, Bindable container) {
		return null;
	}

}
