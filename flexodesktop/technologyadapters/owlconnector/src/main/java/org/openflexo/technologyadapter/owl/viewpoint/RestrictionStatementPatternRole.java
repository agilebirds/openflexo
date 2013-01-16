package org.openflexo.technologyadapter.owl.viewpoint;

import java.lang.reflect.Type;

import org.openflexo.foundation.view.ActorReference;
import org.openflexo.foundation.view.EditionPatternReference;
import org.openflexo.foundation.viewpoint.ViewPoint.ViewPointBuilder;
import org.openflexo.foundation.xml.FlexoProcessBuilder;
import org.openflexo.foundation.xml.FlexoWorkflowBuilder;
import org.openflexo.foundation.xml.VEShemaBuilder;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.technologyadapter.owl.model.OWLRestriction;

// TODO: Rewrite this
@Deprecated
public class RestrictionStatementPatternRole extends StatementPatternRole {

	public RestrictionStatementPatternRole(ViewPointBuilder builder) {
		super(builder);
	}

	@Override
	public Type getType() {
		return null;
	}

	@Override
	public String getPreciseType() {
		return FlexoLocalization.localizedForKey("restriction_statement");
	}

	@Override
	public ActorReference makeActorReference(Object object, EditionPatternReference epRef) {
		// TODO Auto-generated method stub
		return null;
	}

	public static class RestrictionStatementActorReference extends ActorReference<OWLRestriction> {

		public OWLRestriction restriction;
		public String objectURI;
		public String propertyURI;

		public RestrictionStatementActorReference(OWLRestriction o, OWLRestriction aPatternRole, EditionPatternReference ref) {
			super(ref.getProject());
			setPatternReference(ref);
			// setPatternRole(aPatternRole);
			restriction = o;
			// subjectURI = o.getSubject().getURI();
			// parentURI = o.getParent().getURI();
		}

		// Constructor used during deserialization
		public RestrictionStatementActorReference(VEShemaBuilder builder) {
			super(builder.getProject());
			initializeDeserialization(builder);
		}

		// Constructor used during deserialization
		public RestrictionStatementActorReference(FlexoProcessBuilder builder) {
			super(builder.getProject());
			initializeDeserialization(builder);
		}

		// Constructor used during deserialization
		public RestrictionStatementActorReference(FlexoWorkflowBuilder builder) {
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
		public OWLRestriction retrieveObject() {
			return null;
		}
	}

}
