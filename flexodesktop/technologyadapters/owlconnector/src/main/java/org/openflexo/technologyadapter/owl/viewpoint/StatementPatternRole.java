package org.openflexo.technologyadapter.owl.viewpoint;

import org.openflexo.foundation.viewpoint.OntologicObjectPatternRole;
import org.openflexo.technologyadapter.owl.model.OWLStatement;

@ModelEntity(isAbstract = true)
@ImplementationClass(StatementPatternRole.StatementPatternRoleImpl.class)
public abstract interface StatementPatternRole<T extends OWLStatement> extends OntologicObjectPatternRole<T>{


public static abstract  abstract class StatementPatternRole<TImpl extends OWLStatement> extends OntologicObjectPatternRole<T>Impl implements StatementPatternRole<T
{

	public StatementPatternRoleImpl() {
		super();
	}

	@Override
	public boolean defaultBehaviourIsToBeDeleted() {
		return true;
	}

	/*@Override
	public boolean getIsPrimaryRole() {
		return false;
	}

	@Override
	public void setIsPrimaryRole(boolean isPrimary) {
	}*/

}
}
