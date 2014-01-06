package org.openflexo.foundation.viewpoint;

import java.lang.reflect.Type;

import org.openflexo.antar.binding.CustomType;

/**
 * Represent the type of an EditionScheme
 * 
 * @author sylvain
 * 
 */
public class EditionSchemeType implements CustomType {

	public static EditionSchemeType getEditionSchemeType(EditionScheme anEditionScheme) {
		if (anEditionScheme == null) {
			return null;
		}
		return anEditionScheme.getEditionSchemeType();
	}

	private EditionScheme editionScheme;

	public EditionSchemeType(EditionScheme aEditionScheme) {
		this.editionScheme = aEditionScheme;
	}

	public EditionScheme getEditionScheme() {
		return editionScheme;
	}

	public static String getName() {
		return EditionSchemeType.class.getSimpleName();
	}
	
	@Override
	public Class getBaseClass() {
		return EditionScheme.class;
	}

	@Override
	public boolean isTypeAssignableFrom(Type aType, boolean permissive) {
		// System.out.println("isTypeAssignableFrom " + aType + " (i am a " + this + ")");
		if (aType instanceof EditionSchemeType) {
			return getEditionScheme() == (((EditionSchemeType) aType).getEditionScheme());
		}
		return false;
	}

	@Override
	public String simpleRepresentation() {
		return "EditionSchemeType" + ":" + getEditionScheme();
	}

	@Override
	public String fullQualifiedRepresentation() {
		return "EditionSchemeType" + ":" + getEditionScheme();
	}

	@Override
	public String toString() {
		return simpleRepresentation();
	}
}