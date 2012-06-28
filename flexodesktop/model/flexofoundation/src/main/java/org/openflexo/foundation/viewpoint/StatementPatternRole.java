package org.openflexo.foundation.viewpoint;

import org.openflexo.foundation.ontology.OntologyStatement;
import org.openflexo.foundation.viewpoint.ViewPoint.ViewPointBuilder;

public abstract class StatementPatternRole extends OntologicObjectPatternRole {

	public StatementPatternRole(ViewPointBuilder builder) {
		super(builder);
	}

	@Override
	public Class<? extends OntologyStatement> getAccessedClass() {
		return OntologyStatement.class;
	}

}
