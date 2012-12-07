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
package org.openflexo.foundation.view.diagram.viewpoint;

import org.openflexo.antar.binding.BindingDefinition;
import org.openflexo.antar.binding.BindingDefinition.BindingDefinitionType;
import org.openflexo.antar.binding.BindingModel;
import org.openflexo.foundation.view.EditionPatternReference;
import org.openflexo.foundation.viewpoint.AbstractActionScheme;
import org.openflexo.foundation.viewpoint.EditionPattern;
import org.openflexo.foundation.viewpoint.EditionPatternObject;
import org.openflexo.foundation.viewpoint.ViewPoint;
import org.openflexo.foundation.viewpoint.AbstractActionScheme.ActionSchemeBindingAttribute;
import org.openflexo.foundation.viewpoint.ViewPoint.ViewPointBuilder;
import org.openflexo.foundation.viewpoint.binding.ViewPointDataBinding;

public class GraphicalElementAction extends EditionPatternObject {

	private ActionMask actionMask = ActionMask.DoubleClick;
	private AbstractActionScheme abstractActionScheme;
	private GraphicalElementPatternRole graphicalElementPatternRole;

	public static enum ActionMask {
		SingleClick, DoubleClick, ShiftClick, AltClick, CtrlClick, MetaClick;
	}

	private ViewPointDataBinding conditional;

	public GraphicalElementAction(ViewPointBuilder builder) {
		super(builder);
	}

	public GraphicalElementPatternRole getGraphicalElementPatternRole() {
		return graphicalElementPatternRole;
	}

	public void setGraphicalElementPatternRole(GraphicalElementPatternRole graphicalElementPatternRole) {
		this.graphicalElementPatternRole = graphicalElementPatternRole;
	}

	private BindingDefinition CONDITIONAL = new BindingDefinition("conditional", Boolean.class, BindingDefinitionType.GET, false);

	public BindingDefinition getConditionalBindingDefinition() {
		return CONDITIONAL;
	}

	public ViewPointDataBinding getConditional() {
		if (conditional == null) {
			conditional = new ViewPointDataBinding(this, ActionSchemeBindingAttribute.conditional, getConditionalBindingDefinition());
		}
		return conditional;
	}

	public void setConditional(ViewPointDataBinding conditional) {
		conditional.setOwner(this);
		conditional.setBindingAttribute(ActionSchemeBindingAttribute.conditional);
		conditional.setBindingDefinition(getConditionalBindingDefinition());
		this.conditional = conditional;
	}

	public boolean evaluateCondition(EditionPatternReference editionPatternReference) {
		if (getConditional().isValid()) {
			return (Boolean) getConditional().getBindingValue(editionPatternReference);
		}
		return true;
	}

	@Override
	public EditionPattern getEditionPattern() {
		return getGraphicalElementPatternRole() != null ? getGraphicalElementPatternRole().getEditionPattern() : null;
	}

	@Override
	public BindingModel getBindingModel() {
		return getEditionPattern().getBindingModel();
	}

	@Override
	public String getInspectorName() {
		return null;
	}

	@Override
	public ViewPoint getViewPoint() {
		return getEditionPattern().getViewPoint();
	}

	public AbstractActionScheme getAbstractActionScheme() {
		return abstractActionScheme;
	}

	public void setAbstractActionScheme(AbstractActionScheme abstractActionScheme) {
		this.abstractActionScheme = abstractActionScheme;
	}

	public ActionMask getActionMask() {
		return actionMask;
	}

	public void setActionMask(ActionMask actionMask) {
		this.actionMask = actionMask;
	}

}
