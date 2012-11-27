package org.openflexo.technologyadapter.owl.viewpoint;

import org.openflexo.foundation.viewpoint.ViewPoint.ViewPointBuilder;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.technologyadapter.owl.model.IsAStatement;

public class IsAStatementPatternRole extends StatementPatternRole {

	public IsAStatementPatternRole(ViewPointBuilder builder) {
		super(builder);
	}

	@Override
	public PatternRoleType getType() {
		return PatternRoleType.IsAStatement;
	}

	@Override
	public String getPreciseType() {
		return FlexoLocalization.localizedForKey("is_a_statement");
	}

	@Override
	public Class<IsAStatement> getAccessedClass() {
		return IsAStatement.class;
	}

}
