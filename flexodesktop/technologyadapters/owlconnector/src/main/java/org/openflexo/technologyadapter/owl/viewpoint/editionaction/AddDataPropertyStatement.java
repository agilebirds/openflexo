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
import java.util.Vector;
import java.util.logging.Logger;

import org.openflexo.antar.binding.BindingDefinition;
import org.openflexo.antar.binding.BindingDefinition.BindingDefinitionType;
import org.openflexo.foundation.Inspectors;
import org.openflexo.foundation.ontology.IndividualOfClass;
import org.openflexo.foundation.ontology.OntologyClass;
import org.openflexo.foundation.ontology.OntologyDataProperty;
import org.openflexo.foundation.ontology.OntologyObject;
import org.openflexo.foundation.ontology.OntologyProperty;
import org.openflexo.foundation.validation.FixProposal;
import org.openflexo.foundation.validation.ValidationError;
import org.openflexo.foundation.validation.ValidationIssue;
import org.openflexo.foundation.validation.ValidationRule;
import org.openflexo.foundation.view.action.EditionSchemeAction;
import org.openflexo.foundation.viewpoint.PatternRole;
import org.openflexo.foundation.viewpoint.ViewPoint.ViewPointBuilder;
import org.openflexo.foundation.viewpoint.binding.ViewPointDataBinding;
import org.openflexo.technologyadapter.owl.model.DataPropertyStatement;
import org.openflexo.technologyadapter.owl.viewpoint.DataPropertyStatementPatternRole;
import org.openflexo.toolbox.StringUtils;

public class AddDataPropertyStatement extends AddStatement<DataPropertyStatement> {

	private static final Logger logger = Logger.getLogger(AddDataPropertyStatement.class.getPackage().getName());

	private String dataPropertyURI = null;

	public AddDataPropertyStatement(ViewPointBuilder builder) {
		super(builder);
	}

	@Override
	public EditionActionType getEditionActionType() {
		return EditionActionType.AddDataPropertyStatement;
	}

	@Override
	public Type getSubjectType() {
		if (getDataProperty() != null && getDataProperty().getDomain() instanceof OntologyClass) {
			return IndividualOfClass.getIndividualOfClass((OntologyClass) getDataProperty().getDomain());
		}
		return super.getSubjectType();
	}

	/*@Override
	public List<DataPropertyStatementPatternRole> getAvailablePatternRoles() {
		return getEditionPattern().getPatternRoles(DataPropertyStatementPatternRole.class);
	}*/

	@Override
	public DataPropertyStatementPatternRole getPatternRole() {
		PatternRole superPatternRole = super.getPatternRole();
		if (superPatternRole instanceof DataPropertyStatementPatternRole) {
			return (DataPropertyStatementPatternRole) superPatternRole;
		} else if (superPatternRole != null) {
			// logger.warning("Unexpected pattern role of type " + superPatternRole.getClass().getSimpleName());
			return null;
		}
		return null;
	}

	public OntologyProperty getDataProperty() {
		if (getViewPoint() != null) {
			getViewPoint().loadWhenUnloaded();
		}
		if (StringUtils.isNotEmpty(dataPropertyURI)) {
			return getViewPoint().getOntologyDataProperty(dataPropertyURI);
		} else {
			if (getPatternRole() != null) {
				return getPatternRole().getDataProperty();
			}
		}
		return null;
	}

	public void setDataProperty(OntologyProperty ontologyProperty) {
		if (ontologyProperty != null) {
			if (getPatternRole() != null) {
				if (getPatternRole().getDataProperty().isSuperConceptOf(ontologyProperty)) {
					dataPropertyURI = ontologyProperty.getURI();
				} else {
					getPatternRole().setDataProperty(ontologyProperty);
				}
			} else {
				dataPropertyURI = ontologyProperty.getURI();
			}
		} else {
			dataPropertyURI = null;
		}
	}

	public String _getDataPropertyURI() {
		if (getDataProperty() != null) {
			if (getPatternRole() != null && getPatternRole().getDataProperty() == getDataProperty()) {
				// No need to store an overriding type, just use default provided by pattern role
				return null;
			}
			return getDataProperty().getURI();
		}
		return dataPropertyURI;
	}

	public void _setDataPropertyURI(String dataPropertyURI) {
		this.dataPropertyURI = dataPropertyURI;
	}

	public Object getValue(EditionSchemeAction action) {
		return getValue().getBindingValue(action);
	}

	@Override
	public String getInspectorName() {
		return Inspectors.VPM.ADD_OBJECT_PROPERTY_INSPECTOR;
	}

	private ViewPointDataBinding value;

	private BindingDefinition VALUE = new BindingDefinition("value", Object.class, BindingDefinitionType.GET, true) {
		@Override
		public java.lang.reflect.Type getType() {
			if (getDataProperty() != null) {
				return ((OntologyDataProperty) getDataProperty()).getDataType().getAccessedType();
			}
			return Object.class;
		};
	};

	public BindingDefinition getValueBindingDefinition() {
		return VALUE;
	}

	public ViewPointDataBinding getValue() {
		if (value == null) {
			value = new ViewPointDataBinding(this, EditionActionBindingAttribute.value, getValueBindingDefinition());
		}
		return value;
	}

	public void setValue(ViewPointDataBinding value) {
		value.setOwner(this);
		value.setBindingAttribute(EditionActionBindingAttribute.value);
		value.setBindingDefinition(getValueBindingDefinition());
		this.value = value;
	}

	@Override
	public Type getAssignableType() {
		return DataPropertyStatement.class;
	}

	@Override
	public String getStringRepresentation() {
		if (getSubject() == null || getDataProperty() == null || getValue() == null) {
			return "Add DataPropertyStatement";
		}
		return getSubject() + " " + (getDataProperty() != null ? getDataProperty().getName() : "null") + " " + getValue();
	}

	@Override
	public DataPropertyStatement performAction(EditionSchemeAction action) {
		OntologyDataProperty property = (OntologyDataProperty) getDataProperty();
		OntologyObject subject = getPropertySubject(action);
		Object value = getValue(action);
		if (property == null) {
			return null;
		}
		if (subject == null) {
			return null;
		}
		if (value == null) {
			return null;
		}
		return (DataPropertyStatement) subject.addDataPropertyStatement(property, value);
	}

	@Override
	public void finalizePerformAction(EditionSchemeAction action, DataPropertyStatement initialContext) {
	}

	public static class AddDataPropertyStatementActionMustDefineADataProperty extends
			ValidationRule<AddDataPropertyStatementActionMustDefineADataProperty, AddDataPropertyStatement> {
		public AddDataPropertyStatementActionMustDefineADataProperty() {
			super(AddDataPropertyStatement.class, "add_data_property_statement_action_must_define_a_data_property");
		}

		@Override
		public ValidationIssue<AddDataPropertyStatementActionMustDefineADataProperty, AddDataPropertyStatement> applyValidation(
				AddDataPropertyStatement action) {
			if (action.getDataProperty() == null) {
				Vector<FixProposal<AddDataPropertyStatementActionMustDefineADataProperty, AddDataPropertyStatement>> v = new Vector<FixProposal<AddDataPropertyStatementActionMustDefineADataProperty, AddDataPropertyStatement>>();
				for (DataPropertyStatementPatternRole pr : action.getEditionPattern().getPatternRoles(
						DataPropertyStatementPatternRole.class)) {
					v.add(new SetsPatternRole(pr));
				}
				return new ValidationError<AddDataPropertyStatementActionMustDefineADataProperty, AddDataPropertyStatement>(this, action,
						"add_data_property_statement_action_does_not_define_an_data_property", v);
			}
			return null;
		}

		protected static class SetsPatternRole extends
				FixProposal<AddDataPropertyStatementActionMustDefineADataProperty, AddDataPropertyStatement> {

			private DataPropertyStatementPatternRole patternRole;

			public SetsPatternRole(DataPropertyStatementPatternRole patternRole) {
				super("assign_action_to_pattern_role_($patternRole.patternRoleName)");
				this.patternRole = patternRole;
			}

			public DataPropertyStatementPatternRole getPatternRole() {
				return patternRole;
			}

			@Override
			protected void fixAction() {
				AddDataPropertyStatement action = getObject();
				action.setAssignation(new ViewPointDataBinding(patternRole.getPatternRoleName()));
			}

		}
	}

	public static class ValueIsRequiredAndMustBeValid extends BindingIsRequiredAndMustBeValid<AddDataPropertyStatement> {
		public ValueIsRequiredAndMustBeValid() {
			super("'value'_binding_is_required_and_must_be_valid", AddDataPropertyStatement.class);
		}

		@Override
		public ViewPointDataBinding getBinding(AddDataPropertyStatement object) {
			return object.getValue();
		}

		@Override
		public BindingDefinition getBindingDefinition(AddDataPropertyStatement object) {
			return object.getValueBindingDefinition();
		}

	}

}
