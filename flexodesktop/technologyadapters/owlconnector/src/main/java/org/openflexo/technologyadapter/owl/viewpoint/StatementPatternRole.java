package org.openflexo.technologyadapter.owl.viewpoint;

import org.openflexo.foundation.viewpoint.OntologicObjectPatternRole;
import org.openflexo.model.annotations.ImplementationClass;
import org.openflexo.model.annotations.ModelEntity;
import org.openflexo.technologyadapter.owl.model.OWLStatement;

@ModelEntity(isAbstract = true)
@ImplementationClass(StatementPatternRole.StatementPatternRoleImpl.class)
public abstract interface StatementPatternRole<T extends OWLStatement> extends OntologicObjectPatternRole<T> {

	public static abstract class StatementPatternRoleImpl<T extends OWLStatement> extends OntologicObjectPatternRoleImpl<T> implements
			StatementPatternRole<T> {

		@Override
		public boolean defaultBehaviourIsToBeDeleted() {
			return true;
		}

	}
}
