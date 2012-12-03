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
package org.openflexo.technologyadapter.owl.viewpoint.editionaction;

import java.lang.reflect.Type;
import java.util.logging.Logger;

import org.openflexo.antar.binding.BindingDefinition;
import org.openflexo.antar.binding.BindingDefinition.BindingDefinitionType;
import org.openflexo.foundation.Inspectors;
import org.openflexo.foundation.ontology.OntologyClass;
import org.openflexo.foundation.ontology.OntologyIndividual;
import org.openflexo.foundation.ontology.IFlexoOntologyConcept;
import org.openflexo.foundation.view.action.EditionSchemeAction;
import org.openflexo.foundation.viewpoint.ViewPoint.ViewPointBuilder;
import org.openflexo.foundation.viewpoint.binding.ViewPointDataBinding;
import org.openflexo.technologyadapter.owl.model.IsAStatement;

public class AddIsAStatement extends AddStatement<IsAStatement> {

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

	public IFlexoOntologyConcept getPropertyFather(EditionSchemeAction action) {
		return (IFlexoOntologyConcept) getFather().getBindingValue(action);
	}

	@Override
	public String getInspectorName() {
		return Inspectors.VPM.ADD_IS_A_PROPERTY_INSPECTOR;
	}

	private ViewPointDataBinding father;

	private BindingDefinition FATHER = new BindingDefinition("father", IFlexoOntologyConcept.class, BindingDefinitionType.GET, false);

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

	@Override
	public IsAStatement performAction(EditionSchemeAction action) {
		OntologyObject subject = getPropertySubject(action);
		OntologyObject father = getPropertyFather(action);
		if (father instanceof OntologyClass) {
			if (subject instanceof OntologyClass) {
				return (IsAStatement) ((OntologyClass) subject).addSuperClass((OntologyClass) father);
			} else if (subject instanceof OntologyIndividual) {
				return (IsAStatement) ((OntologyIndividual) subject).addType((OntologyClass) father);
			}
		}
		return null;
	}

	@Override
	public void finalizePerformAction(EditionSchemeAction action, IsAStatement initialContext) {
	}

}
