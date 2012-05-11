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

import java.util.List;
import java.util.logging.Logger;

import org.openflexo.antar.binding.BindingDefinition;
import org.openflexo.antar.binding.BindingDefinition.BindingDefinitionType;
import org.openflexo.foundation.view.action.EditionSchemeAction;
import org.openflexo.foundation.viewpoint.binding.ViewPointDataBinding;

public class IterationAction extends ControlStructureAction {

	private static final Logger logger = Logger.getLogger(IterationAction.class.getPackage().getName());

	private String iteratorName;

	public IterationAction() {
	}

	@Override
	public EditionActionType getEditionActionType() {
		return EditionActionType.Iteration;
	}

	@Override
	public List<PatternRole> getAvailablePatternRoles() {
		return getEditionPattern().getPatternRoles();
	}

	public List evaluateIteration(EditionSchemeAction action) {
		return (List) getIteration().getBindingValue(action);
	}

	private ViewPointDataBinding iteration;

	private BindingDefinition ITERATION = new BindingDefinition("iteration", List.class, BindingDefinitionType.GET, false);

	public BindingDefinition getIterationBindingDefinition() {
		return ITERATION;
	}

	public ViewPointDataBinding getIteration() {
		if (iteration == null) {
			iteration = new ViewPointDataBinding(this, EditionActionBindingAttribute.object, getIterationBindingDefinition());
		}
		return iteration;
	}

	public void setIteration(ViewPointDataBinding iteration) {
		if (iteration != null) {
			iteration.setOwner(this);
			iteration.setBindingAttribute(EditionActionBindingAttribute.iteration);
			iteration.setBindingDefinition(getIterationBindingDefinition());
		}
		this.iteration = iteration;
	}

	public String getIteratorName() {
		return iteratorName;
	}

	public void setIteratorName(String iteratorName) {
		this.iteratorName = iteratorName;
	}

}
