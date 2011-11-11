package org.openflexo.foundation.viewpoint;

import org.openflexo.foundation.ontology.RestrictionStatement;
import org.openflexo.localization.FlexoLocalization;

public class RestrictionStatementPatternRole extends StatementPatternRole {

	@Override
	public PatternRoleType getType() {
		return PatternRoleType.RestrictionStatement;
	}

	@Override
	public String getPreciseType() {
		return FlexoLocalization.localizedForKey("restriction_statement");
	}

	@Override
	public Class<RestrictionStatement> getAccessedClass() {
		return RestrictionStatement.class;
	}

}
