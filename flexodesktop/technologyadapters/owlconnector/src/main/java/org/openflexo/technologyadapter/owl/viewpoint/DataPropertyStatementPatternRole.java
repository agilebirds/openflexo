package org.openflexo.technologyadapter.owl.viewpoint;

import java.lang.reflect.Type;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openflexo.foundation.FlexoProject;
import org.openflexo.foundation.ontology.IFlexoOntologyDataProperty;
import org.openflexo.foundation.ontology.IFlexoOntologyObject;
import org.openflexo.foundation.ontology.IFlexoOntologyStructuralProperty;
import org.openflexo.foundation.validation.ValidationError;
import org.openflexo.foundation.validation.ValidationIssue;
import org.openflexo.foundation.validation.ValidationRule;
import org.openflexo.foundation.view.ActorReference;
import org.openflexo.foundation.view.EditionPatternInstance;
import org.openflexo.logging.FlexoLogger;
import org.openflexo.technologyadapter.owl.model.DataPropertyStatement;
import org.openflexo.technologyadapter.owl.model.OWLConcept;
import org.openflexo.technologyadapter.owl.model.OWLDataProperty;
import org.openflexo.technologyadapter.owl.model.StatementWithProperty;

public class DataPropertyStatementPatternRole extends StatementPatternRole<DataPropertyStatement> {

	static final Logger logger = FlexoLogger.getLogger(DataPropertyStatementPatternRole.class.getPackage().toString());

	public DataPropertyStatementPatternRole() {
		super();
	}

	@Override
	public Type getType() {
		if (getDataProperty() == null) {
			return DataPropertyStatement.class;
		}
		return StatementWithProperty.getStatementWithProperty(getDataProperty());
	}

	@Override
	public String getPreciseType() {
		if (getDataProperty() != null) {
			return getDataProperty().getName();
		}
		return "";
	}

	private String dataPropertyURI;

	public String _getDataPropertyURI() {
		return dataPropertyURI;
	}

	public void _setDataPropertyURI(String dataPropertyURI) {
		this.dataPropertyURI = dataPropertyURI;
	}

	public IFlexoOntologyDataProperty getDataProperty() {
		return getVirtualModel().getOntologyDataProperty(_getDataPropertyURI());
	}

	public void setDataProperty(IFlexoOntologyStructuralProperty p) {
		_setDataPropertyURI(p != null ? p.getURI() : null);
	}

	@Override
	public DataPropertyStatementActorReference makeActorReference(DataPropertyStatement object, EditionPatternInstance epi) {
		return new DataPropertyStatementActorReference(object, this, epi);
	}

	public static class DataPropertyStatementPatternRoleMustDefineAValidProperty extends
			ValidationRule<DataPropertyStatementPatternRoleMustDefineAValidProperty, DataPropertyStatementPatternRole> {
		public DataPropertyStatementPatternRoleMustDefineAValidProperty() {
			super(DataPropertyStatementPatternRole.class, "pattern_role_must_define_a_valid_data_property");
		}

		@Override
		public ValidationIssue<DataPropertyStatementPatternRoleMustDefineAValidProperty, DataPropertyStatementPatternRole> applyValidation(
				DataPropertyStatementPatternRole patternRole) {
			if (patternRole.getDataProperty() == null) {
				return new ValidationError<DataPropertyStatementPatternRoleMustDefineAValidProperty, DataPropertyStatementPatternRole>(
						this, patternRole, "pattern_role_does_not_define_any_valid_data_property");
			}
			return null;
		}
	}

	public static class DataPropertyStatementActorReference extends ActorReference<DataPropertyStatement> {

		public DataPropertyStatement statement;
		public String subjectURI;
		public String dataPropertyURI;
		public String value;

		public DataPropertyStatementActorReference(DataPropertyStatement o, DataPropertyStatementPatternRole aPatternRole,
				EditionPatternInstance epi) {
			super(epi.getProject());
			setEditionPatternInstance(epi);
			setPatternRole(aPatternRole);
			statement = o;
			subjectURI = o.getSubject().getURI();
			value = o.getLiteral().toString();
			dataPropertyURI = o.getProperty().getURI();
		}

		// Constructor used during deserialization
		public DataPropertyStatementActorReference(FlexoProject project) {
			super(project);
		}

		@Override
		public DataPropertyStatement retrieveObject() {
			if (statement == null) {
				IFlexoOntologyObject subject = getProject().getOntologyObject(subjectURI);
				if (subject instanceof OWLConcept == false) {
					if (logger.isLoggable(Level.WARNING)) {
						logger.warning("Statements aren't supported by non-owl ontologies, subject's URI: " + subjectURI);
					}
					return null;
				}
				OWLDataProperty property = (OWLDataProperty) getProject().getObject(dataPropertyURI);
				if (property != null) {
					statement = ((OWLConcept<?>) subject).getDataPropertyStatement(property);
					// logger.info("Found statement: "+statement);
				}
			}
			if (statement == null) {
				logger.warning("Could not retrieve object " + value);
			}
			return statement;
		}
	}
}
