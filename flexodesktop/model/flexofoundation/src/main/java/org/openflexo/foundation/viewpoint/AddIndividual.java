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
import java.util.Vector;
import java.util.logging.Logger;

import org.openflexo.antar.binding.DataBinding;
import org.openflexo.foundation.ontology.IFlexoOntologyClass;
import org.openflexo.foundation.ontology.IFlexoOntologyIndividual;
import org.openflexo.foundation.ontology.IndividualOfClass;
import org.openflexo.foundation.technologyadapter.TypeSafeModelSlot;
import org.openflexo.foundation.validation.FixProposal;
import org.openflexo.foundation.validation.ValidationError;
import org.openflexo.foundation.validation.ValidationIssue;
import org.openflexo.foundation.validation.ValidationRule;
import org.openflexo.foundation.viewpoint.ViewPointObject.FMLRepresentationContext.FMLRepresentationOutput;
import org.openflexo.logging.FlexoLogger;
import org.openflexo.toolbox.StringUtils;

public abstract class AddIndividual<MS extends TypeSafeModelSlot<?, ?>, T extends IFlexoOntologyIndividual> extends AddConcept<MS, T> {

	protected static final Logger logger = FlexoLogger.getLogger(AddIndividual.class.getPackage().getName());

	private Vector<DataPropertyAssertion> dataAssertions;
	private Vector<ObjectPropertyAssertion> objectAssertions;
	protected String ontologyClassURI = null;

	private DataBinding<String> individualName;

	public AddIndividual(VirtualModel.VirtualModelBuilder builder) {
		super(builder);
		dataAssertions = new Vector<DataPropertyAssertion>();
		objectAssertions = new Vector<ObjectPropertyAssertion>();
	}

	@Override
	public String getFMLRepresentation(FMLRepresentationContext context) {
		FMLRepresentationOutput out = new FMLRepresentationOutput(context);
		if (getAssignation().isSet()) {
			out.append(getAssignation().toString() + " = (", context);
		}
		out.append(getClass().getSimpleName() + (getOntologyClass() != null ? " conformTo " + getOntologyClass().getName() : "") + " from "
				+ getModelSlot().getName() + " {" + StringUtils.LINE_SEPARATOR, context);
		out.append(getAssertionsFMLRepresentation(context), context);
		out.append("}", context);
		if (getAssignation().isSet()) {
			out.append(")", context);
		}
		return out.toString();
	}

	protected String getAssertionsFMLRepresentation(FMLRepresentationContext context) {
		if (getDataAssertions().size() > 0) {
			StringBuffer sb = new StringBuffer();
			for (DataPropertyAssertion a : getDataAssertions()) {
				if (a.getOntologyProperty() != null) {
					sb.append("  " + a.getOntologyProperty().getName() + " = " + a.getValue().toString() + ";" + StringUtils.LINE_SEPARATOR);
				}
			}
			return sb.toString();
		}
		if (getObjectAssertions().size() > 0) {
			StringBuffer sb = new StringBuffer();
			for (ObjectPropertyAssertion a : getObjectAssertions()) {
				sb.append("  " + a.getOntologyProperty().getName() + " = " + a.getObject().toString() + ";" + StringUtils.LINE_SEPARATOR);
			}
			return sb.toString();
		}
		return null;
	}

	@Override
	public EditionActionType getEditionActionType() {
		return EditionActionType.AddIndividual;
	}

	public abstract Class<T> getOntologyIndividualClass();

	@Override
	public IndividualPatternRole getPatternRole() {
		PatternRole superPatternRole = super.getPatternRole();
		if (superPatternRole instanceof IndividualPatternRole) {
			return (IndividualPatternRole) superPatternRole;
		} else if (superPatternRole != null) {
			// logger.warning("Unexpected pattern role of type " + superPatternRole.getClass().getSimpleName());
			return null;
		}
		return null;
	}

	public IFlexoOntologyClass getType() {
		return getOntologyClass();
	}

	public void setType(IFlexoOntologyClass type) {
		setOntologyClass(type);
	}

	@Override
	public IFlexoOntologyClass getOntologyClass() {
		// System.out.println("AddIndividual: ontologyClassURI=" + ontologyClassURI);
		if (StringUtils.isNotEmpty(ontologyClassURI) && getVirtualModel() != null) {
			return getVirtualModel().getOntologyClass(ontologyClassURI);
		} else {
			if (getPatternRole() != null) {
				// System.out.println("Je reponds avec le pattern role " + getPatternRole());
				return getPatternRole().getOntologicType();
			}
		}
		// System.out.println("Je reponds null");
		return null;
	}

	@Override
	public void setOntologyClass(IFlexoOntologyClass ontologyClass) {
		if (ontologyClass != null) {
			if (getPatternRole() instanceof IndividualPatternRole) {
				if (getPatternRole().getOntologicType().isSuperConceptOf(ontologyClass)) {
					ontologyClassURI = ontologyClass.getURI();
				} else {
					getPatternRole().setOntologicType(ontologyClass);
				}
			} else {
				ontologyClassURI = ontologyClass.getURI();
			}
		} else {
			ontologyClassURI = null;
		}
		System.out.println("ontologyClassURI=" + ontologyClassURI);
	}

	public String _getOntologyClassURI() {
		if (getOntologyClass() != null) {
			if (getPatternRole() instanceof IndividualPatternRole && getPatternRole().getOntologicType() == getOntologyClass()) {
				// No need to store an overriding type, just use default provided by pattern role
				return null;
			}
			return getOntologyClass().getURI();
		}
		return ontologyClassURI;
	}

	public void _setOntologyClassURI(String ontologyClassURI) {
		this.ontologyClassURI = ontologyClassURI;
	}

	public Vector<DataPropertyAssertion> getDataAssertions() {
		return dataAssertions;
	}

	public void setDataAssertions(Vector<DataPropertyAssertion> assertions) {
		this.dataAssertions = assertions;
	}

	public void addToDataAssertions(DataPropertyAssertion assertion) {
		assertion.setAction(this);
		dataAssertions.add(assertion);
	}

	public void removeFromDataAssertions(DataPropertyAssertion assertion) {
		assertion.setAction(null);
		dataAssertions.remove(assertion);
	}

	public DataPropertyAssertion createDataPropertyAssertion() {
		DataPropertyAssertion newDataPropertyAssertion = new DataPropertyAssertion(null);
		addToDataAssertions(newDataPropertyAssertion);
		return newDataPropertyAssertion;
	}

	public DataPropertyAssertion deleteDataPropertyAssertion(DataPropertyAssertion assertion) {
		removeFromDataAssertions(assertion);
		assertion.delete();
		return assertion;
	}

	public Vector<ObjectPropertyAssertion> getObjectAssertions() {
		return objectAssertions;
	}

	public void setObjectAssertions(Vector<ObjectPropertyAssertion> assertions) {
		this.objectAssertions = assertions;
	}

	public void addToObjectAssertions(ObjectPropertyAssertion assertion) {
		assertion.setAction(this);
		objectAssertions.add(assertion);
	}

	public void removeFromObjectAssertions(ObjectPropertyAssertion assertion) {
		assertion.setAction(null);
		objectAssertions.remove(assertion);
	}

	public ObjectPropertyAssertion createObjectPropertyAssertion() {
		ObjectPropertyAssertion newObjectPropertyAssertion = new ObjectPropertyAssertion(null);
		addToObjectAssertions(newObjectPropertyAssertion);
		return newObjectPropertyAssertion;
	}

	public ObjectPropertyAssertion deleteObjectPropertyAssertion(ObjectPropertyAssertion assertion) {
		removeFromObjectAssertions(assertion);
		assertion.delete();
		return assertion;
	}

	public DataBinding<String> getIndividualName() {
		if (individualName == null) {
			individualName = new DataBinding<String>(this, String.class, DataBinding.BindingDefinitionType.GET);
			individualName.setBindingName("individualName");
		}
		return individualName;
	}

	public void setIndividualName(DataBinding<String> individualName) {
		if (individualName != null) {
			individualName.setOwner(this);
			individualName.setDeclaredType(String.class);
			individualName.setBindingDefinitionType(DataBinding.BindingDefinitionType.GET);
			individualName.setBindingName("individualName");
		}
		this.individualName = individualName;
	}

	@Override
	public Type getAssignableType() {
		if (getOntologyClass() == null) {
			return IFlexoOntologyIndividual.class;
		}
		return IndividualOfClass.getIndividualOfClass(getOntologyClass());
	}

	public static class AddIndividualActionMustDefineAnOntologyClass extends
			ValidationRule<AddIndividualActionMustDefineAnOntologyClass, AddIndividual> {
		public AddIndividualActionMustDefineAnOntologyClass() {
			super(AddIndividual.class, "add_individual_action_must_define_an_ontology_class");
		}

		@Override
		public ValidationIssue<AddIndividualActionMustDefineAnOntologyClass, AddIndividual> applyValidation(AddIndividual action) {
			if (action.getOntologyClass() == null) {
				Vector<FixProposal<AddIndividualActionMustDefineAnOntologyClass, AddIndividual>> v = new Vector<FixProposal<AddIndividualActionMustDefineAnOntologyClass, AddIndividual>>();
				for (IndividualPatternRole pr : action.getEditionPattern().getIndividualPatternRoles()) {
					v.add(new SetsPatternRole(pr));
				}
				return new ValidationError<AddIndividualActionMustDefineAnOntologyClass, AddIndividual>(this, action,
						"add_individual_action_does_not_define_any_ontology_class", v);
			}
			return null;
		}

		protected static class SetsPatternRole extends FixProposal<AddIndividualActionMustDefineAnOntologyClass, AddIndividual> {

			private IndividualPatternRole patternRole;

			public SetsPatternRole(IndividualPatternRole patternRole) {
				super("assign_action_to_pattern_role_($patternRole.patternRoleName)");
				this.patternRole = patternRole;
			}

			public IndividualPatternRole<?> getPatternRole() {
				return patternRole;
			}

			@Override
			protected void fixAction() {
				AddIndividual<?, ?> action = getObject();
				action.setAssignation(new DataBinding<Object>(patternRole.getPatternRoleName()));
			}

		}
	}

	public static class URIBindingIsRequiredAndMustBeValid extends BindingIsRequiredAndMustBeValid<AddIndividual> {
		public URIBindingIsRequiredAndMustBeValid() {
			super("'uri'_binding_is_required_and_must_be_valid", AddIndividual.class);
		}

		@Override
		public DataBinding<String> getBinding(AddIndividual object) {
			return object.getIndividualName();
		}

	}

}
