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
package org.openflexo.foundation.ontology.owl.action;

import java.util.Vector;
import java.util.logging.Logger;

import org.openflexo.foundation.FlexoEditor;
import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.action.FlexoAction;
import org.openflexo.foundation.action.FlexoActionType;
import org.openflexo.foundation.ontology.DuplicateURIException;
import org.openflexo.foundation.ontology.owl.OWLClass;
import org.openflexo.foundation.ontology.owl.OWLObject;
import org.openflexo.foundation.ontology.owl.OWLOntology;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.toolbox.StringUtils;

public class CreateOntologyClass extends FlexoAction<CreateOntologyClass, OWLObject, OWLObject> {

	private static final Logger logger = Logger.getLogger(CreateOntologyClass.class.getPackage().getName());

	public static FlexoActionType<CreateOntologyClass, OWLObject, OWLObject> actionType = new FlexoActionType<CreateOntologyClass, OWLObject, OWLObject>(
			"create_class", FlexoActionType.newMenu, FlexoActionType.defaultGroup, FlexoActionType.ADD_ACTION_TYPE) {

		/**
		 * Factory method
		 */
		@Override
		public CreateOntologyClass makeNewAction(OWLObject focusedObject, Vector<OWLObject> globalSelection, FlexoEditor editor) {
			return new CreateOntologyClass(focusedObject, globalSelection, editor);
		}

		@Override
		public boolean isVisibleForSelection(OWLObject object, Vector<OWLObject> globalSelection) {
			return object != null;
		}

		@Override
		public boolean isEnabledForSelection(OWLObject object, Vector<OWLObject> globalSelection) {
			return object != null && !object.getIsReadOnly();
		}

	};

	static {
		FlexoModelObject.addActionForClass(CreateOntologyClass.actionType, OWLOntology.class);
		FlexoModelObject.addActionForClass(CreateOntologyClass.actionType, OWLClass.class);
	}

	public String newOntologyClassName;
	public String description;
	public OWLClass fatherClass;

	public String validURILabel;

	private OWLClass newClass;

	private static final String VALID_URI_LABEL = FlexoLocalization.localizedForKey("uri_is_well_formed_and_valid_regarding_its_unicity");
	private static final String INVALID_URI_LABEL = FlexoLocalization.localizedForKey("uri_is_not_valid_please_choose_another_class_name");

	CreateOntologyClass(OWLObject focusedObject, Vector<OWLObject> globalSelection, FlexoEditor editor) {
		super(actionType, focusedObject, globalSelection, editor);
		newOntologyClassName = "NewClass";
		fatherClass = (focusedObject instanceof OWLClass ? (OWLClass) focusedObject : focusedObject.getOntologyLibrary().getOWLOntology()
				.getRootClass());
		isValid();
	}

	@Override
	protected void doAction(Object context) throws DuplicateURIException {
		logger.info("Create OntologyClass on " + getFocusedObject());
		newClass = getOntology().createOntologyClass(newOntologyClassName, fatherClass);
	}

	public OWLClass getNewClass() {
		return newClass;
	}

	public OWLOntology getOntology() {
		return getFocusedObject().getFlexoOntology();
	}

	public boolean isValid() {
		boolean returned = !StringUtils.isEmpty(newOntologyClassName) && getOntology().testValidURI(newOntologyClassName);
		validURILabel = (returned ? VALID_URI_LABEL : INVALID_URI_LABEL);
		return returned;
	}

}
