package org.openflexo.foundation.viewpoint;

import org.openflexo.foundation.ontology.IsAStatement;
import org.openflexo.foundation.viewpoint.ViewPoint.ViewPointBuilder;
import org.openflexo.localization.FlexoLocalization;

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
