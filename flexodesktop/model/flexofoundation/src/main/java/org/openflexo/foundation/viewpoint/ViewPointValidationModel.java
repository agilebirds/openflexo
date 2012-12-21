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
package org.openflexo.foundation.viewpoint;

import org.openflexo.foundation.validation.Validable;
import org.openflexo.foundation.validation.ValidationModel;
import org.openflexo.foundation.view.diagram.viewpoint.editionaction.AddConnector;
import org.openflexo.foundation.view.diagram.viewpoint.editionaction.AddShape;
import org.openflexo.foundation.view.diagram.viewpoint.editionaction.GraphicalAction;
import org.openflexo.foundation.viewpoint.inspector.InspectorEntry;

/**
 * Please comment this class
 * 
 * @author sguerin
 * 
 */
public class ViewPointValidationModel extends ValidationModel {

	public ViewPointValidationModel() {
		super(null, null);

		registerRule(new EditionPattern.EditionPatternShouldHaveRoles());
		registerRule(new EditionPattern.EditionPatternShouldHaveEditionSchemes());
		registerRule(new EditionPattern.EditionPatternShouldHaveDeletionScheme());

		registerRule(new PatternRole.PatternRoleMustHaveAName());
		registerRule(new ClassPatternRole.ClassPatternRoleMustDefineAValidConceptClass());
		registerRule(new IndividualPatternRole.IndividualPatternRoleMustDefineAValidConceptClass());
		// registerRule(new DataPropertyStatementPatternRole.DataPropertyStatementPatternRoleMustDefineAValidProperty());
		// registerRule(new ObjectPropertyStatementPatternRole.ObjectPropertyStatementPatternRoleMustDefineAValidProperty());

		registerRule(new InspectorEntry.DataBindingIsRequiredAndMustBeValid());

		registerRule(new URIParameter.BaseURIBindingIsRequiredAndMustBeValid());

		registerRule(new EditionAction.ConditionalBindingMustBeValid());
		registerRule(new AssignableAction.AssignationBindingMustBeValid());

		registerRule(new AddIndividual.AddIndividualActionMustDefineAnOntologyClass());
		registerRule(new AddIndividual.URIBindingIsRequiredAndMustBeValid());

		registerRule(new AddClass.AddClassActionMustDefineAnOntologyClass());
		registerRule(new AddClass.URIBindingIsRequiredAndMustBeValid());

		// registerRule(new AddStatement.SubjectIsRequiredAndMustBeValid());
		// registerRule(new AddObjectPropertyStatement.AddObjectPropertyStatementActionMustDefineAnObjectProperty());
		// registerRule(new AddObjectPropertyStatement.ObjectIsRequiredAndMustBeValid());
		// registerRule(new AddDataPropertyStatement.AddDataPropertyStatementActionMustDefineADataProperty());
		// registerRule(new AddDataPropertyStatement.ValueIsRequiredAndMustBeValid());

		registerRule(new AddShape.AddShapeActionMustAdressAValidShapePatternRole());
		registerRule(new AddShape.AddShapeActionMustHaveAValidContainer());

		registerRule(new AddConnector.AddConnectorActionMustAdressAValidConnectorPatternRole());
		registerRule(new AddConnector.AddConnectorActionMustHaveAValidStartingShape());
		registerRule(new AddConnector.AddConnectorActionMustHaveAValidEndingShape());

		registerRule(new DeclarePatternRole.AssignationBindingIsRequiredAndMustBeValid());
		registerRule(new DeclarePatternRole.ObjectBindingIsRequiredAndMustBeValid());

		registerRule(new DeleteAction.ObjectToDeleteBindingIsRequiredAndMustBeValid());

		registerRule(new GraphicalAction.GraphicalActionMustHaveASubject());
		registerRule(new GraphicalAction.GraphicalActionMustDefineAValue());

		registerRule(new AddEditionPattern.ViewBindingIsRequiredAndMustBeValid());
		registerRule(new AddEditionPattern.AddEditionPatternMustAddressACreationScheme());
		registerRule(new AddEditionPattern.AddEditionPatternParametersMustBeValid());

		registerRule(new ConditionalAction.ConditionBindingIsRequiredAndMustBeValid());
		registerRule(new IterationAction.IterationBindingIsRequiredAndMustBeValid());

		// Notify that the validation model is complete and that inheritance
		// computation could be performed
		update();
	}

	/**
	 * Return a boolean indicating if validation of supplied object must be notified
	 * 
	 * @param next
	 * @return a boolean
	 */
	@Override
	protected boolean shouldNotifyValidation(Validable next) {
		return true;
	}

	/**
	 * Overrides fixAutomaticallyIfOneFixProposal
	 * 
	 * @see org.openflexo.foundation.validation.ValidationModel#fixAutomaticallyIfOneFixProposal()
	 */
	@Override
	public boolean fixAutomaticallyIfOneFixProposal() {
		return false;
	}
}
