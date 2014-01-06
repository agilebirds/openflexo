package org.openflexo.technologyadapter.owl.viewpoint;

import java.lang.reflect.Type;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openflexo.foundation.FlexoProject;
import org.openflexo.foundation.view.ActorReference;
import org.openflexo.foundation.view.EditionPatternInstance;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.logging.FlexoLogger;
import org.openflexo.technologyadapter.owl.model.OWLConcept;
import org.openflexo.technologyadapter.owl.model.OWLObject;
import org.openflexo.technologyadapter.owl.model.SubClassStatement;

public class SubClassStatementPatternRole extends StatementPatternRole<SubClassStatement> {

	static final Logger logger = FlexoLogger.getLogger(SubClassStatementPatternRole.class.getPackage().toString());

	public SubClassStatementPatternRole() {
		super();
	}

	@Override
	public Type getType() {
		return null;
	}

	@Override
	public String getPreciseType() {
		return FlexoLocalization.localizedForKey("sub_class_statement");
	}

	@Override
	public SubClassStatementActorReference makeActorReference(SubClassStatement object, EditionPatternInstance epi) {
		return new SubClassStatementActorReference(object, this, epi);
	}

	public static class SubClassStatementActorReference extends ActorReference<SubClassStatement> {

		public SubClassStatement statement;
		public String subjectURI;
		public String parentURI;

		public SubClassStatementActorReference(SubClassStatement o, SubClassStatementPatternRole aPatternRole, EditionPatternInstance epi) {
			super(epi.getProject());
			setEditionPatternInstance(epi);
			setPatternRole(aPatternRole);
			statement = o;
			subjectURI = o.getSubject().getURI();
			parentURI = o.getParent().getURI();
		}

		// Constructor used during deserialization
		public SubClassStatementActorReference(FlexoProject project) {
			super(project);
		}

		@Override
		public SubClassStatement retrieveObject() {
			if (statement == null) {
				OWLObject subject = (OWLObject) getProject().getOntologyObject(subjectURI);
				if (subject instanceof OWLConcept == false) {
					if (logger.isLoggable(Level.WARNING)) {
						logger.warning("Statements aren't supported by non-owl ontologies, subject's URI: " + subjectURI);
					}
					return null;
				}
				OWLConcept<?> parent = (OWLConcept<?>) getProject().getOntologyObject(parentURI);
				if (subject != null && parent != null) {
					statement = ((OWLConcept<?>) subject).getSubClassStatement(parent);
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
