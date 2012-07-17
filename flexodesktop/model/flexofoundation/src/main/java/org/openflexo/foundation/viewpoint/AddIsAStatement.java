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

import java.lang.reflect.Type;
import java.util.logging.Logger;

import org.openflexo.antar.binding.BindingDefinition;
import org.openflexo.antar.binding.BindingDefinition.BindingDefinitionType;
import org.openflexo.foundation.Inspectors;
import org.openflexo.foundation.ontology.OntologyObject;
import org.openflexo.foundation.ontology.owl.IsAStatement;
import org.openflexo.foundation.view.action.EditionSchemeAction;
import org.openflexo.foundation.viewpoint.ViewPoint.ViewPointBuilder;
import org.openflexo.foundation.viewpoint.binding.ViewPointDataBinding;

public class AddIsAStatement extends AddStatement {

	private static final Logger logger = Logger.getLogger(AddIsAStatement.class.getPackage().getName());

	public AddIsAStatement(ViewPointBuilder builder) {
		super(builder);
	}

	@Override
	public EditionActionType getEditionActionType() {
		return EditionActionType.AddIsAStatement;
	}

	/*@Override
	public List<IsAStatementPatternRole> getAvailablePatternRoles() {
		return getEditionPattern().getPatternRoles(IsAStatementPatternRole.class);
	}*/

	public OntologyObject getPropertyFather(EditionSchemeAction action) {
		return (OntologyObject) getFather().getBindingValue(action);
	}

	@Override
	public String getInspectorName() {
		return Inspectors.VPM.ADD_IS_A_PROPERTY_INSPECTOR;
	}

	private ViewPointDataBinding father;

	private BindingDefinition FATHER = new BindingDefinition("father", OntologyObject.class, BindingDefinitionType.GET, false);

	public BindingDefinition getFatherBindingDefinition() {
		return FATHER;
	}

	public ViewPointDataBinding getFather() {
		if (father == null) {
			father = new ViewPointDataBinding(this, EditionActionBindingAttribute.father, getFatherBindingDefinition());
		}
		return father;
	}

	public void setFather(ViewPointDataBinding father) {
		father.setOwner(this);
		father.setBindingAttribute(EditionActionBindingAttribute.father);
		father.setBindingDefinition(getFatherBindingDefinition());
		this.father = father;
	}

	@Override
	public Type getAssignableType() {
		return IsAStatement.class;
	}

}
