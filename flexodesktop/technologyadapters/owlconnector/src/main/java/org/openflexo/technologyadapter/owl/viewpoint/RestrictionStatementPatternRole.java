package org.openflexo.technologyadapter.owl.viewpoint;

import org.openflexo.foundation.view.ActorReference;
import org.openflexo.foundation.view.EditionPatternReference;
import org.openflexo.foundation.viewpoint.ViewPoint.ViewPointBuilder;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.technologyadapter.owl.model.SubClassStatement;

public class RestrictionStatementPatternRole extends StatementPatternRole {

	public RestrictionStatementPatternRole(ViewPointBuilder builder) {
		super(builder);
	}

	@Override
	public PatternRoleType getType() {
		return PatternRoleType.RestrictionStatement;
	}

	@Override
	public String getPreciseType() {
		return FlexoLocalization.localizedForKey("restriction_statement");
	}

	@Override
	public Class<SubClassStatement> getAccessedClass() {
		return SubClassStatement.class;
	}

	@Override
	public ActorReference makeActorReference(Object object, EditionPatternReference epRef) {
		// TODO Auto-generated method stub
		return null;
	}

}
