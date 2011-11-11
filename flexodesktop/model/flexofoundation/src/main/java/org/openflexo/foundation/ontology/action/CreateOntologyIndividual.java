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
import org.openflexo.foundation.ontology.OntologyIndividual;
import org.openflexo.foundation.ontology.OntologyObject;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.toolbox.StringUtils;

public class CreateOntologyIndividual extends FlexoAction<CreateOntologyIndividual, OntologyObject, OntologyObject> {

	private static final Logger logger = Logger.getLogger(CreateOntologyIndividual.class.getPackage().getName());

	public static FlexoActionType<CreateOntologyIndividual, OntologyObject, OntologyObject> actionType = new FlexoActionType<CreateOntologyIndividual, OntologyObject, OntologyObject>(
			"create_individual", FlexoActionType.newMenu, FlexoActionType.defaultGroup, FlexoActionType.ADD_ACTION_TYPE) {

		/**
		 * Factory method
		 */
		@Override
		public CreateOntologyIndividual makeNewAction(OntologyObject focusedObject, Vector<OntologyObject> globalSelection,
				FlexoEditor editor) {
			return new CreateOntologyIndividual(focusedObject, globalSelection, editor);
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
		FlexoModelObject.addActionForClass(CreateOntologyIndividual.actionType, FlexoOntology.class);
		FlexoModelObject.addActionForClass(CreateOntologyIndividual.actionType, OntologyClass.class);
	}

	public String newOntologyIndividualName;
	public String description;
	public OntologyClass fatherClass;

	public String validURILabel;

	private OntologyIndividual newIndividual;

	private static final String VALID_URI_LABEL = FlexoLocalization.localizedForKey("uri_is_well_formed_and_valid_regarding_its_unicity");
	private static final String INVALID_URI_LABEL = FlexoLocalization.localizedForKey("uri_is_not_valid_please_choose_another_class_name");

	CreateOntologyIndividual(OntologyObject focusedObject, Vector<OntologyObject> globalSelection, FlexoEditor editor) {
		super(actionType, focusedObject, globalSelection, editor);
		newOntologyIndividualName = "NewIndividual";
		fatherClass = (focusedObject instanceof OntologyClass ? (OntologyClass) focusedObject : focusedObject.getOntologyLibrary().THING);
		isValid();
	}

	@Override
	protected void doAction(Object context) throws DuplicateURIException {
		logger.info("Create OntologyIndividual on " + getFocusedObject());
		newIndividual = getOntology().createOntologyIndividual(newOntologyIndividualName, fatherClass);
	}

	public OntologyIndividual getNewIndividual() {
		return newIndividual;
	}

	public FlexoOntology getOntology() {
		return getFocusedObject().getFlexoOntology();
	}

	public boolean isValid() {
		boolean returned = !StringUtils.isEmpty(newOntologyIndividualName) && getOntology().testValidURI(newOntologyIndividualName);
		validURILabel = (returned ? VALID_URI_LABEL : INVALID_URI_LABEL);
		return returned;
	}

}
