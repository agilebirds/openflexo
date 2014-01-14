package org.openflexo.foundation.viewpoint;

import java.lang.reflect.Type;
import java.util.logging.Logger;

import org.openflexo.foundation.view.ActorReference;
import org.openflexo.foundation.view.EditionPatternInstance;
import org.openflexo.foundation.viewpoint.FMLRepresentationContext.FMLRepresentationOutput;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.logging.FlexoLogger;

@ModelEntity
@ImplementationClass(PrimitivePatternRole.PrimitivePatternRoleImpl.class)
@XMLElement
public interface PrimitivePatternRole extends PatternRole<Object>{

@PropertyIdentifier(type=PrimitiveType.class)
public static final String PRIMITIVE_TYPE_KEY = "primitiveType";

@Getter(value=PRIMITIVE_TYPE_KEY)
@XMLAttribute
public PrimitiveType getPrimitiveType();

@Setter(PRIMITIVE_TYPE_KEY)
public void setPrimitiveType(PrimitiveType primitiveType);


public static abstract  class PrimitivePatternRoleImpl extends PatternRole<Object>Impl implements PrimitivePatternRole
{

	protected static final Logger logger = FlexoLogger.getLogger(PrimitivePatternRole.class.getPackage().getName());

	public static enum PrimitiveType {
		Boolean, String, LocalizedString, Integer, Float
	}

	private PrimitiveType primitiveType;

	public PrimitivePatternRoleImpl() {
		super();
	}

	@Override
	public String getFMLRepresentation(FMLRepresentationContext context) {
		FMLRepresentationOutput out = new FMLRepresentationOutput(context);
		out.append("PatternRole " + getName() + " as " + getPreciseType() + ";", context);
		return out.toString();
	}

	public PrimitiveType getPrimitiveType() {
		return primitiveType;
	}

	public void setPrimitiveType(PrimitiveType primitiveType) {
		this.primitiveType = primitiveType;
	}

	@Override
	public String getPreciseType() {
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

	@Override
	public Type getType() {
		if (primitiveType == null) {
			return null;
		}
		switch (primitiveType) {
		case String:
			return String.class;
		case LocalizedString:
			return String.class;
		case Boolean:
			return Boolean.class;
		case Integer:
			return Integer.TYPE;
		case Float:
			return Float.TYPE;
		default:
			return null;
		}
	}

	/*@Override
	public boolean getIsPrimaryRole() {
		return false;
	}

	@Override
	public void setIsPrimaryRole(boolean isPrimary) {
		// Not relevant
	}*/

	@Override
	public boolean defaultBehaviourIsToBeDeleted() {
		return true;
	}

	@Override
	public ActorReference<Object> makeActorReference(Object object, EditionPatternInstance epi) {
		logger.warning("Not implemented");
		return null;
	}

}
}
