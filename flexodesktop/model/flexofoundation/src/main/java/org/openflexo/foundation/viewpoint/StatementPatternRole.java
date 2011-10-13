package org.openflexo.foundation.viewpoint;

import org.openflexo.foundation.ontology.OntologyStatement;

public abstract class StatementPatternRole extends OntologicObjectPatternRole {

	@Override
	public Class<? extends OntologyStatement> getAccessedClass()
	{
		return OntologyStatement.class;
	}


}
