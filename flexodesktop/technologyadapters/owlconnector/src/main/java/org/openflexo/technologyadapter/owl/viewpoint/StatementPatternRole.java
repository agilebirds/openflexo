package org.openflexo.technologyadapter.owl.viewpoint;

import org.openflexo.foundation.viewpoint.OntologicObjectPatternRole;
import org.openflexo.foundation.viewpoint.ViewPoint.ViewPointBuilder;
import org.openflexo.technologyadapter.owl.model.OWLStatement;

public abstract class StatementPatternRole<T extends OWLStatement> extends OntologicObjectPatternRole<T> {

	public StatementPatternRole(ViewPointBuilder builder) {
		super(builder);
	}

	@Override
	public boolean defaultBehaviourIsToBeDeleted() {
		return true;
	}

	@Override
	public boolean getIsPrimaryRole() {
		return false;
	}

	@Override
	public void setIsPrimaryRole(boolean isPrimary) {
	}

}
