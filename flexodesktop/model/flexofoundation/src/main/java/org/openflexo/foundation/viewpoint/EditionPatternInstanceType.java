package org.openflexo.foundation.viewpoint;

import java.lang.reflect.Type;

import org.openflexo.antar.binding.CustomType;
import org.openflexo.foundation.view.EditionPatternInstance;
import org.openflexo.foundation.view.VirtualModelInstance;

/**
 * Represent the type of a EditionPatternInstance of a given EditionPattern
 * 
 * @author sylvain
 * 
 */
public class EditionPatternInstanceType implements CustomType {

	public static EditionPatternInstanceType getEditionPatternInstanceType(EditionPattern anEditionPattern) {
		if (anEditionPattern == null) {
			return null;
		}
		return anEditionPattern.getInstanceType();
	}

	private EditionPattern editionPattern;

	public EditionPatternInstanceType(EditionPattern anEditionPattern) {
		this.editionPattern = anEditionPattern;
	}

	public EditionPattern getEditionPattern() {
		return editionPattern;
	}

	@Override
	public Class getBaseClass() {
		if (getEditionPattern() instanceof VirtualModel) {
			return VirtualModelInstance.class;
		} else {
			return EditionPatternInstance.class;
		}
	}

	@Override
	public boolean isTypeAssignableFrom(Type aType, boolean permissive) {
		// System.out.println("isTypeAssignableFrom " + aType + " (i am a " + this + ")");
		if (aType instanceof EditionPatternInstanceType) {
			return editionPattern.isAssignableFrom(((EditionPatternInstanceType) aType).getEditionPattern());
		}
		return false;
	}

	@Override
	public String simpleRepresentation() {
		return "EditionPatternInstanceType" + ":" + editionPattern;
	}

	@Override
	public String fullQualifiedRepresentation() {
		return "EditionPatternInstanceType" + ":" + editionPattern;
	}

	@Override
	public String toString() {
		return simpleRepresentation();
	}
}