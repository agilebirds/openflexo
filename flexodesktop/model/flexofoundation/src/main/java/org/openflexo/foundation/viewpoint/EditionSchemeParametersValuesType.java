package org.openflexo.foundation.viewpoint;

import java.lang.reflect.Type;
import java.util.Hashtable;

import org.openflexo.antar.binding.CustomType;

/**
 * Represent the type of the list of all parameters of an EditionScheme (run-time). Internal representation is given by an
 * Hashtable<EditionSchemeParameter,Object)
 * 
 * @author sylvain
 * 
 */
public class EditionSchemeParametersValuesType implements CustomType {

	public static EditionSchemeParametersValuesType getEditionSchemeParametersValuesType(EditionScheme anEditionScheme) {
		if (anEditionScheme == null) {
			return null;
		}
		return anEditionScheme.getEditionSchemeParametersValuesType();
	}

	private EditionScheme editionScheme;

	public EditionSchemeParametersValuesType(EditionScheme aEditionScheme) {
		this.editionScheme = aEditionScheme;
	}

	public EditionScheme getEditionScheme() {
		return editionScheme;
	}

	@Override
	public Class getBaseClass() {
		return Hashtable.class;
	}

	@Override
	public boolean isTypeAssignableFrom(Type aType, boolean permissive) {
		// System.out.println("isTypeAssignableFrom " + aType + " (i am a " + this + ")");
		if (aType instanceof EditionSchemeParametersValuesType) {
			return getEditionScheme() == (((EditionSchemeParametersValuesType) aType).getEditionScheme());
		}
		return false;
	}

	@Override
	public String simpleRepresentation() {
		return "EditionSchemeParametersValuesType" + ":" + getEditionScheme();
	}

	@Override
	public String fullQualifiedRepresentation() {
		return "EditionSchemeParametersValuesType" + ":" + getEditionScheme();
	}

	@Override
	public String toString() {
		return simpleRepresentation();
	}
}