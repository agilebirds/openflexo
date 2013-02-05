package org.openflexo.technologyadapter.owl.viewpoint;

import java.lang.reflect.Type;

import org.openflexo.foundation.view.ActorReference;
import org.openflexo.foundation.view.EditionPatternInstance;
import org.openflexo.foundation.viewpoint.VirtualModel;
import org.openflexo.foundation.xml.VirtualModelInstanceBuilder;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.technologyadapter.owl.model.OWLRestriction;

// TODO: Rewrite this
@Deprecated
public class RestrictionStatementPatternRole extends StatementPatternRole {

	public RestrictionStatementPatternRole(VirtualModel.VirtualModelBuilder builder) {
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
	public ActorReference makeActorReference(Object object, EditionPatternInstance epi) {
		// TODO Auto-generated method stub
		return null;
	}

	public static class RestrictionStatementActorReference extends ActorReference<OWLRestriction> {

		public OWLRestriction restriction;
		public String objectURI;
		public String propertyURI;

		public RestrictionStatementActorReference(OWLRestriction o, OWLRestriction aPatternRole, EditionPatternInstance epi) {
			super(epi.getProject());
			setEditionPatternInstance(epi);
			// setPatternRole(aPatternRole);
			restriction = o;
			// subjectURI = o.getSubject().getURI();
			// parentURI = o.getParent().getURI();
		}

		// Constructor used during deserialization
		public RestrictionStatementActorReference(VirtualModelInstanceBuilder builder) {
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
