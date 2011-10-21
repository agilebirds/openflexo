package org.openflexo.foundation.viewpoint;

import org.openflexo.foundation.view.View;
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

	@Override
	public Class<?> getAccessedClass()
	{
		return View.class;
	}
	
	@Override
	public boolean getIsPrimaryRole()
	{
		return false;
	}
	
	@Override
	public void setIsPrimaryRole(boolean isPrimary)
	{
		// Not relevant
	}


}
