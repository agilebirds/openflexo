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

import java.util.logging.Logger;

import org.openflexo.antar.binding.BindingDefinition;
import org.openflexo.antar.binding.BindingDefinition.BindingDefinitionType;
import org.openflexo.antar.binding.BindingModel;
import org.openflexo.foundation.view.action.EditionSchemeAction;
import org.openflexo.foundation.viewpoint.binding.ViewPointDataBinding;
import org.openflexo.foundation.viewpoint.inspector.InspectorBindingAttribute;

/**
 * Abstract class representing a primitive to be executed as an atomic action of an EditionScheme
 * 
 * @author sylvain
 * 
 */
public abstract class EditionAction extends ViewPointObject {

	private static final Logger logger = Logger.getLogger(EditionAction.class.getPackage().getName());

	public static enum EditionActionType {
		AddClass,
		AddIndividual,
		AddObjectPropertyStatement,
		AddDataPropertyStatement,
		AddIsAStatement,
		AddRestrictionStatement,
		AddConnector,
		AddShape,
		AddDiagram,
		AddEditionPattern,
		DeclarePatternRole,
		GraphicalAction,
		GoToObject,
		Iteration,
		Conditional
	}

	public static enum EditionActionBindingAttribute implements InspectorBindingAttribute {
		conditional,
		assignation,
		individualName,
		className,
		container,
		fromShape,
		toShape,
		object,
		subject,
		father,
		value,
		restrictionType,
		cardinality,
		target,
		diagramName,
		view,
		condition,
		iteration
	}

	private EditionScheme _scheme;
	private String description;
	// private String patternRole;

	private ViewPointDataBinding conditional;

	private BindingDefinition CONDITIONAL = new BindingDefinition("conditional", Boolean.class, BindingDefinitionType.GET, false);

	public EditionAction() {
	}

	public abstract EditionActionType getEditionActionType();

	public void setScheme(EditionScheme scheme) {
		_scheme = scheme;
	}

	public EditionScheme getScheme() {
		return _scheme;
	}

	@Override
	public String getDescription() {
		return description;
	}

	@Override
	public void setDescription(String description) {
		this.description = description;
	}

	@Override
	public ViewPoint getViewPoint() {
		if (getScheme() != null) {
			return getScheme().getViewPoint();
		}
		return null;
	}

	public boolean evaluateCondition(EditionSchemeAction action) {
		if (getConditional().isValid()) {
			return (Boolean) getConditional().getBindingValue(action);
		}
		return true;
	}

	public EditionPattern getEditionPattern() {
		if (getScheme() == null) {
			return null;
		}
		return getScheme().getEditionPattern();
	}

	public int getIndex() {
		return getScheme().getActions().indexOf(this);
	}

	public EditionScheme getEditionScheme() {
		return _scheme;
	}

	@Override
	public BindingModel getBindingModel() {
		return getEditionScheme().getBindingModel();
	}

	public BindingDefinition getConditionalBindingDefinition() {
		return CONDITIONAL;
	}

	public ViewPointDataBinding getConditional() {
		if (conditional == null) {
			conditional = new ViewPointDataBinding(this, EditionActionBindingAttribute.conditional, getConditionalBindingDefinition());
		}
		return conditional;
	}

	public void setConditional(ViewPointDataBinding conditional) {
		if (conditional != null) {
			conditional.setOwner(this);
			conditional.setBindingAttribute(EditionActionBindingAttribute.conditional);
			conditional.setBindingDefinition(getConditionalBindingDefinition());
		}
		this.conditional = conditional;
	}

	public String getStringRepresentation() {
		return getClass().getSimpleName();
	}

	@Override
	public String toString() {
		return getStringRepresentation();
	}
}
