package org.openflexo.technologyadapter.owl.viewpoint;

import java.lang.reflect.Type;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openflexo.foundation.ontology.IFlexoOntologyStructuralProperty;
import org.openflexo.foundation.validation.ValidationError;
import org.openflexo.foundation.validation.ValidationIssue;
import org.openflexo.foundation.validation.ValidationRule;
import org.openflexo.foundation.view.ActorReference;
import org.openflexo.foundation.view.EditionPatternReference;
import org.openflexo.foundation.viewpoint.VirtualModel;
import org.openflexo.foundation.viewpoint.VirtualModel.VirtualModelBuilder;
import org.openflexo.foundation.xml.FlexoProcessBuilder;
import org.openflexo.foundation.xml.FlexoWorkflowBuilder;
import org.openflexo.foundation.xml.ViewBuilder;
import org.openflexo.logging.FlexoLogger;
import org.openflexo.technologyadapter.owl.model.OWLConcept;
import org.openflexo.technologyadapter.owl.model.OWLObject;
import org.openflexo.technologyadapter.owl.model.OWLObjectProperty;
import org.openflexo.technologyadapter.owl.model.ObjectPropertyStatement;
import org.openflexo.technologyadapter.owl.model.StatementWithProperty;

public class ObjectPropertyStatementPatternRole extends StatementPatternRole<ObjectPropertyStatement> {

	static final Logger logger = FlexoLogger.getLogger(ObjectPropertyStatementPatternRole.class.getPackage().toString());

	public ObjectPropertyStatementPatternRole(VirtualModel.VirtualModelBuilder builder) {
		super(builder);
	}

	@Override
	public Type getType() {
		if (getObjectProperty() == null) {
			return ObjectPropertyStatement.class;
		}
		return StatementWithProperty.getStatementWithProperty(getObjectProperty());
	}

	@Override
	public String getPreciseType() {
		if (getObjectProperty() != null) {
			return getObjectProperty().getName();
		}
		return "";
	}

	private String objectPropertyURI;

	public String _getObjectPropertyURI() {
		return objectPropertyURI;
	}

	public void _setObjectPropertyURI(String objectPropertyURI) {
		this.objectPropertyURI = objectPropertyURI;
	}

	public IFlexoOntologyStructuralProperty getObjectProperty() {
		return getVirtualModel().getOntologyObjectProperty(_getObjectPropertyURI());
	}

	public void setObjectProperty(IFlexoOntologyStructuralProperty p) {
		_setObjectPropertyURI(p != null ? p.getURI() : null);
	}

	@Override
	public ObjectPropertyStatementActorReference makeActorReference(ObjectPropertyStatement object, EditionPatternReference epRef) {
		return new ObjectPropertyStatementActorReference(object, this, epRef);
	}

	public static class ObjectPropertyStatementPatternRoleMustDefineAValidProperty extends
			ValidationRule<ObjectPropertyStatementPatternRoleMustDefineAValidProperty, ObjectPropertyStatementPatternRole> {
		public ObjectPropertyStatementPatternRoleMustDefineAValidProperty() {
			super(ObjectPropertyStatementPatternRole.class, "pattern_role_must_define_a_valid_object_property");
		}

		@Override
		public ValidationIssue<ObjectPropertyStatementPatternRoleMustDefineAValidProperty, ObjectPropertyStatementPatternRole> applyValidation(
				ObjectPropertyStatementPatternRole patternRole) {
			if (patternRole.getObjectProperty() == null) {
				return new ValidationError<ObjectPropertyStatementPatternRoleMustDefineAValidProperty, ObjectPropertyStatementPatternRole>(
						this, patternRole, "pattern_role_does_not_define_any_valid_object_property");
			}
			return null;
		}
	}

	public static class ObjectPropertyStatementActorReference extends ActorReference<ObjectPropertyStatement> {

		public ObjectPropertyStatement statement;
		public String subjectURI;
		public String objectPropertyURI;
		public String objectURI;

		public ObjectPropertyStatementActorReference(ObjectPropertyStatement o, ObjectPropertyStatementPatternRole aPatternRole,
				EditionPatternReference ref) {
			super(ref.getProject());
			setPatternReference(ref);
			setPatternRole(aPatternRole);
			statement = o;
			subjectURI = o.getSubject().getURI();
			objectURI = o.getStatementObject().getURI();
			objectPropertyURI = o.getProperty().getURI();
		}

		// Constructor used during deserialization
		public ObjectPropertyStatementActorReference(ViewBuilder builder) {
			super(builder.getProject());
			initializeDeserialization(builder);
		}

		// Constructor used during deserialization
		public ObjectPropertyStatementActorReference(FlexoProcessBuilder builder) {
			super(builder.getProject());
			initializeDeserialization(builder);
		}

		// Constructor used during deserialization
		public ObjectPropertyStatementActorReference(FlexoWorkflowBuilder builder) {
			super(builder.getProject());
			initializeDeserialization(builder);
		}

		@Override
		public String getClassNameKey() {
			return "object_property_statement_actor_reference";
		}

		@Override
		public String getFullyQualifiedName() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public ObjectPropertyStatement retrieveObject() {
			if (statement == null) {
				OWLObject subject = (OWLObject) getProject().getOntologyObject(subjectURI);
				if (subject == null) {
					if (logger.isLoggable(Level.WARNING)) {
						logger.warning("Could not find subject with URI " + subjectURI);
					}
					return null;
				}
				if (subject instanceof OWLConcept == false) {
					if (logger.isLoggable(Level.WARNING)) {
						logger.warning("Statements aren't supported by non-owl ontologies, subject's URI: " + subjectURI);
					}
					return null;
				}

				OWLConcept<?> object = (OWLConcept<?>) getProject().getOntologyObject(objectURI);
				if (object == null) {
					if (logger.isLoggable(Level.WARNING)) {
						logger.warning("Could not find object with URI " + objectURI);
					}
					return null;
				}

				OWLObjectProperty property = (OWLObjectProperty) getProject().getObject(objectPropertyURI);
				if (property != null) {
					// FIXED HUGE ISSUE HERE, with incorrect deserialization of statements
					statement = ((OWLConcept<?>) subject).getObjectPropertyStatement(property, object);
					// logger.info("Found statement: "+statement);
				}
			}
			if (statement == null) {
				logger.warning("Could not retrieve object " + objectURI);
			}
			return statement;
		}
	}
}
