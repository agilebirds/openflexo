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
package org.openflexo.technologyadapter.diagram.model;

import org.openflexo.foundation.validation.Validable;
import org.openflexo.foundation.validation.ValidationModel;
import org.openflexo.technologyadapter.diagram.fml.editionaction.AddConnector;
import org.openflexo.technologyadapter.diagram.fml.editionaction.AddShape;
import org.openflexo.technologyadapter.diagram.fml.editionaction.GraphicalAction;

/**
 * Please comment this class
 * 
 * @author sguerin
 * 
 */
public class ViewPointValidationModel extends ValidationModel {

	public ViewPointValidationModel() {
		super(null, null);

		registerRule(new AddShape.AddShapeActionMustAdressAValidShapePatternRole());
		registerRule(new AddShape.AddShapeActionMustHaveAValidContainer());

		registerRule(new AddConnector.AddConnectorActionMustAdressAValidConnectorPatternRole());
		registerRule(new AddConnector.AddConnectorActionMustHaveAValidStartingShape());
		registerRule(new AddConnector.AddConnectorActionMustHaveAValidEndingShape());

		registerRule(new GraphicalAction.GraphicalActionMustHaveASubject());
		registerRule(new GraphicalAction.GraphicalActionMustDefineAValue());

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
