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
package org.openflexo.foundation.ontology.action;

import java.util.Vector;
import java.util.logging.Logger;

import org.openflexo.foundation.FlexoEditor;
import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.action.FlexoAction;
import org.openflexo.foundation.action.FlexoActionType;
import org.openflexo.foundation.ontology.DuplicateURIException;
import org.openflexo.foundation.ontology.FlexoOntology;
import org.openflexo.foundation.ontology.OntologyClass;
import org.openflexo.foundation.ontology.OntologyObject;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.toolbox.StringUtils;

public class CreateOntologyClass extends FlexoAction<CreateOntologyClass, OntologyObject, OntologyObject> {

	private static final Logger logger = Logger.getLogger(CreateOntologyClass.class.getPackage().getName());

	public static FlexoActionType<CreateOntologyClass, OntologyObject, OntologyObject> actionType = new FlexoActionType<CreateOntologyClass, OntologyObject, OntologyObject>(
			"create_class", FlexoActionType.newMenu, FlexoActionType.defaultGroup, FlexoActionType.ADD_ACTION_TYPE) {

		/**
		 * Factory method
		 */
		@Override
		public CreateOntologyClass makeNewAction(OntologyObject focusedObject, Vector<OntologyObject> globalSelection, FlexoEditor editor) {
			return new CreateOntologyClass(focusedObject, globalSelection, editor);
		}

		@Override
		protected boolean isVisibleForSelection(OntologyObject object, Vector<OntologyObject> globalSelection) {
			return object != null;
		}

		@Override
		protected boolean isEnabledForSelection(OntologyObject object, Vector<OntologyObject> globalSelection) {
			return object != null && !object.getIsReadOnly();
		}

	};

	static {
		FlexoModelObject.addActionForClass(CreateOntologyClass.actionType, FlexoOntology.class);
		FlexoModelObject.addActionForClass(CreateOntologyClass.actionType, OntologyClass.class);
	}

	public String newOntologyClassName;
	public String description;
	public OntologyClass fatherClass;

	public String validURILabel;

	private OntologyClass newClass;

	private static final String VALID_URI_LABEL = FlexoLocalization.localizedForKey("uri_is_well_formed_and_valid_regarding_its_unicity");
	private static final String INVALID_URI_LABEL = FlexoLocalization.localizedForKey("uri_is_not_valid_please_choose_another_class_name");

	CreateOntologyClass(OntologyObject focusedObject, Vector<OntologyObject> globalSelection, FlexoEditor editor) {
		super(actionType, focusedObject, globalSelection, editor);
		newOntologyClassName = "NewClass";
		fatherClass = (focusedObject instanceof OntologyClass ? (OntologyClass) focusedObject : focusedObject.getOntologyLibrary()
				.getOWLOntology().getRootClass());
		isValid();
	}

	@Override
	protected void doAction(Object context) throws DuplicateURIException {
		logger.info("Create OntologyClass on " + getFocusedObject());
		newClass = getOntology().createOntologyClass(newOntologyClassName, fatherClass);
	}

	public OntologyClass getNewClass() {
		return newClass;
	}

	public FlexoOntology getOntology() {
		return getFocusedObject().getFlexoOntology();
	}

	public boolean isValid() {
		boolean returned = !StringUtils.isEmpty(newOntologyClassName) && getOntology().testValidURI(newOntologyClassName);
		validURILabel = (returned ? VALID_URI_LABEL : INVALID_URI_LABEL);
		return returned;
	}

}
