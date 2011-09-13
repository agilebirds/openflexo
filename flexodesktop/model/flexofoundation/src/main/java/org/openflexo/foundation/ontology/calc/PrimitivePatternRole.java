package org.openflexo.foundation.ontology.calc;

import org.openflexo.localization.FlexoLocalization;

public class PrimitivePatternRole extends PatternRole {

	
	public static enum PrimitiveType
	{
		Boolean,
		String,
		LocalizedString,
		Integer,
		Float
	}
	
	private PrimitiveType primitiveType;

	@Override
	public PatternRoleType getType()
	{
		return PatternRoleType.Primitive;
	}

	public PrimitiveType getPrimitiveType()
	{
		return primitiveType;
	}

	public void setPrimitiveType(PrimitiveType primitiveType)
	{
		this.primitiveType = primitiveType;
	}
	
	@Override
	public String getPreciseType()
	{
			if (primitiveType == null) {
				return null;
			}
			switch (primitiveType) {
			case String:
				return FlexoLocalization.localizedForKey("string");
			case LocalizedString:
				return FlexoLocalization.localizedForKey("localized_string");
			case Boolean:
				return FlexoLocalization.localizedForKey("boolean");
			case Integer:
				return FlexoLocalization.localizedForKey("integer");
			case Float:
				return FlexoLocalization.localizedForKey("float");
			default:
				return null;
			}
	}
	

}
