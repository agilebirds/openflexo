package org.openflexo.foundation.ontology.calc;

import org.openflexo.localization.FlexoLocalization;

public class ShemaPatternRole extends PatternRole {

	@Override
	public PatternRoleType getType()
	{
		return PatternRoleType.Shema;
	}


	@Override
	public String getPreciseType()
	{
		return FlexoLocalization.localizedForKey("shema");
	}

}
