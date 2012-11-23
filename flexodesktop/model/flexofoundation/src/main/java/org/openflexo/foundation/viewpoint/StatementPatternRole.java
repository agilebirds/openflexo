package org.openflexo.foundation.viewpoint;

import org.openflexo.foundation.ontology.owl.OWLStatement;
import org.openflexo.foundation.viewpoint.ViewPoint.ViewPointBuilder;

public abstract class StatementPatternRole extends OntologicObjectPatternRole {

	public StatementPatternRole(ViewPointBuilder builder) {
		super(builder);
	}

	@Override
	public Class<? extends OWLStatement> getAccessedClass() {
		return OWLStatement.class;
	}

}
