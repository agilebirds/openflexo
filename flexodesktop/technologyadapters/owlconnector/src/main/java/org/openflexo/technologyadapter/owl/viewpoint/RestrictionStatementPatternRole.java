package org.openflexo.technologyadapter.owl.viewpoint;

import java.lang.reflect.Type;

import org.openflexo.foundation.FlexoProject;
import org.openflexo.foundation.view.ActorReference;
import org.openflexo.foundation.view.EditionPatternInstance;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.model.annotations.ImplementationClass;
import org.openflexo.model.annotations.ModelEntity;
import org.openflexo.model.annotations.XMLElement;
import org.openflexo.technologyadapter.owl.model.OWLRestriction;

// TODO: Rewrite this
@Deprecated
@ModelEntity
@ImplementationClass(RestrictionStatementPatternRole.RestrictionStatementPatternRoleImpl.class)
@XMLElement
public interface RestrictionStatementPatternRole extends StatementPatternRole {

	public static abstract class RestrictionStatementPatternRoleImpl extends StatementPatternRoleImpl implements
			RestrictionStatementPatternRole {

		public RestrictionStatementPatternRoleImpl() {
			super();
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
			public RestrictionStatementActorReference(FlexoProject project) {
				super(project);
			}

			@Override
			public OWLRestriction retrieveObject() {
				return null;
			}
		}

	}
}
