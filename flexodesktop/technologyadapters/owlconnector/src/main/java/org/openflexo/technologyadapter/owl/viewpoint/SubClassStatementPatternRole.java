package org.openflexo.technologyadapter.owl.viewpoint;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.openflexo.foundation.view.ActorReference;
import org.openflexo.foundation.view.EditionPatternReference;
import org.openflexo.foundation.viewpoint.ViewPoint.ViewPointBuilder;
import org.openflexo.foundation.xml.FlexoProcessBuilder;
import org.openflexo.foundation.xml.FlexoWorkflowBuilder;
import org.openflexo.foundation.xml.VEShemaBuilder;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.logging.FlexoLogger;
import org.openflexo.technologyadapter.owl.model.OWLObject;
import org.openflexo.technologyadapter.owl.model.SubClassStatement;

public class SubClassStatementPatternRole extends StatementPatternRole<SubClassStatement> {

	static final Logger logger = FlexoLogger.getLogger(SubClassStatementPatternRole.class.getPackage().toString());

	public SubClassStatementPatternRole(ViewPointBuilder builder) {
		super(builder);
	}

	@Override
	public PatternRoleType getType() {
		return PatternRoleType.SubClassStatement;
	}

	@Override
	public String getPreciseType() {
		return FlexoLocalization.localizedForKey("sub_class_statement");
	}

	@Override
	public Class<SubClassStatement> getAccessedClass() {
		return SubClassStatement.class;
	}

	@Override
	public SubClassStatementActorReference makeActorReference(SubClassStatement object, EditionPatternReference epRef) {
		return new SubClassStatementActorReference(object, this, epRef);
	}

	public static class SubClassStatementActorReference extends ActorReference<SubClassStatement> {

		public SubClassStatement statement;
		public String subjectURI;
		public String parentURI;

		public SubClassStatementActorReference(SubClassStatement o, SubClassStatementPatternRole aPatternRole, EditionPatternReference ref) {
			super(ref.getProject());
			setPatternReference(ref);
			setPatternRole(aPatternRole);
			statement = o;
			subjectURI = o.getSubject().getURI();
			parentURI = o.getParent().getURI();
		}

		// Constructor used during deserialization
		public SubClassStatementActorReference(VEShemaBuilder builder) {
			super(builder.getProject());
			initializeDeserialization(builder);
		}

		// Constructor used during deserialization
		public SubClassStatementActorReference(FlexoProcessBuilder builder) {
			super(builder.getProject());
			initializeDeserialization(builder);
		}

		// Constructor used during deserialization
		public SubClassStatementActorReference(FlexoWorkflowBuilder builder) {
			super(builder.getProject());
			initializeDeserialization(builder);
		}

		@Override
		public String getClassNameKey() {
			return "sub_class_statement_actor_reference";
		}

		@Override
		public String getFullyQualifiedName() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public SubClassStatement retrieveObject() {
			if (statement == null) {
				OntologyObject subject = getProject().getOntologyObject(subjectURI);
				if (subject instanceof OWLObject == false) {
					if (logger.isLoggable(Level.WARNING)) {
						logger.warning("Statements aren't supported by non-owl ontologies, subject's URI: " + subjectURI);
					}
					return null;
				}
				OntologyObject parent = getProject().getOntologyObject(parentURI);
				if (subject != null && parent != null) {
					statement = ((OWLObject<?>) subject).getSubClassStatement(parent);
				}
				logger.info("Found statement: " + statement);
			}
			if (statement == null) {
				logger.warning("Could not retrieve object " + parentURI);
			}
			return statement;
		}
	}
}
