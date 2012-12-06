package org.openflexo.technologyadapter.owl.viewpoint;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.openflexo.foundation.ontology.IFlexoOntologyDataProperty;
import org.openflexo.foundation.ontology.IFlexoOntologyStructuralProperty;
import org.openflexo.foundation.validation.ValidationError;
import org.openflexo.foundation.validation.ValidationIssue;
import org.openflexo.foundation.validation.ValidationRule;
import org.openflexo.foundation.view.ActorReference;
import org.openflexo.foundation.view.EditionPatternReference;
import org.openflexo.foundation.viewpoint.ViewPoint.ViewPointBuilder;
import org.openflexo.foundation.xml.FlexoProcessBuilder;
import org.openflexo.foundation.xml.FlexoWorkflowBuilder;
import org.openflexo.foundation.xml.VEShemaBuilder;
import org.openflexo.logging.FlexoLogger;
import org.openflexo.technologyadapter.owl.model.DataPropertyStatement;
import org.openflexo.technologyadapter.owl.model.OWLConcept;

public class DataPropertyStatementPatternRole extends StatementPatternRole<DataPropertyStatement> {

	static final Logger logger = FlexoLogger.getLogger(DataPropertyStatementPatternRole.class.getPackage().toString());

	public DataPropertyStatementPatternRole(ViewPointBuilder builder) {
		super(builder);
	}

	@Override
	public PatternRoleType getType() {
		return PatternRoleType.DataPropertyStatement;
	}

	@Override
	public String getPreciseType() {
		if (getDataProperty() != null) {
			return getDataProperty().getName();
		}
		return "";
	}

	@Override
	public Class<DataPropertyStatement> getAccessedClass() {
		return DataPropertyStatement.class;
	}

	private String dataPropertyURI;

	public String _getDataPropertyURI() {
		return dataPropertyURI;
	}

	public void _setDataPropertyURI(String dataPropertyURI) {
		this.dataPropertyURI = dataPropertyURI;
	}

	public IFlexoOntologyDataProperty getDataProperty() {
		getViewPoint().loadWhenUnloaded();
		return getViewPoint().getOntologyDataProperty(_getDataPropertyURI());
	}

	public void setDataProperty(IFlexoOntologyStructuralProperty p) {
		_setDataPropertyURI(p != null ? p.getURI() : null);
	}

	@Override
	public DataPropertyStatementActorReference makeActorReference(DataPropertyStatement object, EditionPatternReference epRef) {
		return new DataPropertyStatementActorReference(object, this, epRef);
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
				EditionPatternReference ref) {
			super(ref.getProject());
			setPatternReference(ref);
			setPatternRole(aPatternRole);
			statement = o;
			subjectURI = o.getSubject().getURI();
			value = o.getLiteral().toString();
			dataPropertyURI = o.getProperty().getURI();
		}

		// Constructor used during deserialization
		public DataPropertyStatementActorReference(VEShemaBuilder builder) {
			super(builder.getProject());
			initializeDeserialization(builder);
		}

		// Constructor used during deserialization
		public DataPropertyStatementActorReference(FlexoProcessBuilder builder) {
			super(builder.getProject());
			initializeDeserialization(builder);
		}

		// Constructor used during deserialization
		public DataPropertyStatementActorReference(FlexoWorkflowBuilder builder) {
			super(builder.getProject());
			initializeDeserialization(builder);
		}

		@Override
		public String getClassNameKey() {
			return "data_property_statement_actor_reference";
		}

		@Override
		public String getFullyQualifiedName() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public DataPropertyStatement retrieveObject() {
			if (statement == null) {
				OntologyObject subject = getProject().getOntologyObject(subjectURI);
				if (subject instanceof OWLConcept == false) {
					if (logger.isLoggable(Level.WARNING)) {
						logger.warning("Statements aren't supported by non-owl ontologies, subject's URI: " + subjectURI);
					}
					return null;
				}
				OntologyDataProperty property = (OntologyDataProperty) getProject().getObject(dataPropertyURI);
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
