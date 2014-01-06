package org.openflexo.foundation.viewpoint;

import java.lang.reflect.Type;
import java.util.List;

import org.openflexo.antar.binding.CustomType;

/**
 * Represent the type of the list of all parameters of an EditionScheme (definition layer)
 * 
 * @author sylvain
 * 
 */
public class EditionSchemeParametersType implements CustomType {

	public static EditionSchemeParametersType getEditionSchemeParametersType(EditionScheme anEditionScheme) {
		if (anEditionScheme == null) {
			return null;
		}
		return anEditionScheme.getEditionSchemeParametersType();
	}

	private EditionScheme editionScheme;

	public EditionSchemeParametersType(EditionScheme aEditionScheme) {
		this.editionScheme = aEditionScheme;
	}

	public EditionScheme getEditionScheme() {
		return editionScheme;
	}

	@Override
	public Class<List> getBaseClass() {
		return List.class;
	}

	@Override
	public boolean isTypeAssignableFrom(Type aType, boolean permissive) {
		// System.out.println("isTypeAssignableFrom " + aType + " (i am a " + this + ")");
		if (aType instanceof EditionSchemeParametersType) {
			return getEditionScheme() == (((EditionSchemeParametersType) aType).getEditionScheme());
		}
		return false;
	}

	@Override
	public String simpleRepresentation() {
		return "EditionSchemeParametersType" + ":" + getEditionScheme();
	}

	@Override
	public String fullQualifiedRepresentation() {
		return "EditionSchemeParametersType" + ":" + getEditionScheme();
	}

	@Override
	public String toString() {
		return simpleRepresentation();
	}
}