package org.openflexo.foundation.ontology.calc;

import org.openflexo.localization.FlexoLocalization;

public class FlexoModelObjectPatternRole extends PatternRole {

	public static enum FlexoModelObjectType
	{
		Process,
		ProcessFolder,
		Role,
		Activity,
		Operation,
		Action,
		Event
	}

	private FlexoModelObjectType flexoModelObjectType;

	@Override
	public PatternRoleType getType()
	{
		return PatternRoleType.FlexoModelObject;
	}

	public FlexoModelObjectType getFlexoModelObjectType()
	{
		return flexoModelObjectType;
	}

	public void setFlexoModelObjectType(FlexoModelObjectType flexoModelObjectType)
	{
		this.flexoModelObjectType = flexoModelObjectType;
	}

	@Override
	public String getPreciseType()
	{
			if (flexoModelObjectType == null) {
				return null;
			}
			switch (flexoModelObjectType) {
			case Process:
				return FlexoLocalization.localizedForKey("process");
			case ProcessFolder:
				return FlexoLocalization.localizedForKey("process_folder");
			case Role:
				return FlexoLocalization.localizedForKey("role");
			case Activity:
				return FlexoLocalization.localizedForKey("activity");
			case Operation:
				return FlexoLocalization.localizedForKey("operation");
			case Action:
				return FlexoLocalization.localizedForKey("action");
			case Event:
				return FlexoLocalization.localizedForKey("event");
			default:
				return null;
			}
	}
	
}
