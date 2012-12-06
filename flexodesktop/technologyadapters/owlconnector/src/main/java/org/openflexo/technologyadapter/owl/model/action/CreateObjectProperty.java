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
package org.openflexo.technologyadapter.owl.model.action;

import java.util.Vector;
import java.util.logging.Logger;

import org.openflexo.foundation.FlexoEditor;
import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.action.FlexoAction;
import org.openflexo.foundation.action.FlexoActionType;
import org.openflexo.foundation.ontology.DuplicateURIException;
import org.openflexo.foundation.ontology.IFlexoOntologyClass;
import org.openflexo.foundation.ontology.IFlexoOntologyObjectProperty;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.technologyadapter.owl.model.OWLConcept;
import org.openflexo.technologyadapter.owl.model.OWLObject;
import org.openflexo.technologyadapter.owl.model.OWLOntology;
import org.openflexo.toolbox.StringUtils;

public class CreateObjectProperty extends FlexoAction<CreateObjectProperty, OWLObject, OWLConcept> {

	private static final Logger logger = Logger.getLogger(CreateObjectProperty.class.getPackage().getName());

	public static FlexoActionType<CreateObjectProperty, OWLObject, OWLConcept> actionType = new FlexoActionType<CreateObjectProperty, OWLObject, OWLConcept>(
			"create_object_property", FlexoActionType.newMenu, FlexoActionType.defaultGroup, FlexoActionType.ADD_ACTION_TYPE) {

		/**
		 * Factory method
		 */
		@Override
		public CreateObjectProperty makeNewAction(OWLObject focusedObject, Vector<OWLConcept> globalSelection, FlexoEditor editor) {
			return new CreateObjectProperty(focusedObject, globalSelection, editor);
		}

		@Override
		public boolean isVisibleForSelection(OWLObject object, Vector<OWLConcept> globalSelection) {
			return object != null;
		}

		@Override
		public boolean isEnabledForSelection(OWLObject object, Vector<OWLConcept> globalSelection) {
			return object != null && !object.getOntology().getIsReadOnly();
		}

	};

	static {
		FlexoModelObject.addActionForClass(CreateObjectProperty.actionType, OWLOntology.class);
	}

	public String newPropertyName;
	public String description;
	public IFlexoOntologyObjectProperty parentProperty;
	public IFlexoOntologyClass domainClass;
	public IFlexoOntologyClass rangeClass;

	public String validURILabel;

	private IFlexoOntologyObjectProperty newProperty;

	private static final String VALID_URI_LABEL = FlexoLocalization.localizedForKey("uri_is_well_formed_and_valid_regarding_its_unicity");
	private static final String INVALID_URI_LABEL = FlexoLocalization.localizedForKey("uri_is_not_valid_please_choose_another_class_name");

	CreateObjectProperty(OWLObject focusedObject, Vector<OWLConcept> globalSelection, FlexoEditor editor) {
		super(actionType, focusedObject, globalSelection, editor);
		newPropertyName = "newProperty";
		parentProperty = null;
		isValid();
	}

	@Override
	protected void doAction(Object context) throws DuplicateURIException {
		logger.info("Create IFlexoOntologyObjectProperty on " + getFocusedObject());
	}

	public IFlexoOntologyObjectProperty getNewProperty() {
		return newProperty;
	}

	public OWLOntology getOntology() {
		return getFocusedObject().getFlexoOntology();
	}

	public boolean isValid() {
		boolean returned = !StringUtils.isEmpty(newPropertyName) && getOntology().testValidURI(newPropertyName);
		validURILabel = returned ? VALID_URI_LABEL : INVALID_URI_LABEL;
		return returned;
	}

}
