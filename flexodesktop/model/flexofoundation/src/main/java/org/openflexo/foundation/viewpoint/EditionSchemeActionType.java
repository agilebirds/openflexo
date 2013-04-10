package org.openflexo.foundation.viewpoint;

import java.lang.reflect.Type;

import org.openflexo.antar.binding.CustomType;
import org.openflexo.foundation.view.action.EditionSchemeAction;

/**
 * Represent the type of an EditionScheme
 * 
 * @author sylvain
 * 
 */
public class EditionSchemeActionType implements CustomType {

	public static EditionSchemeActionType getEditionSchemeActionType(EditionScheme anEditionScheme) {
		if (anEditionScheme == null) {
			return null;
		}
		return anEditionScheme.getEditionSchemeActionType();
	}

	private EditionScheme editionScheme;

	public EditionSchemeActionType(EditionScheme aEditionScheme) {
		this.editionScheme = aEditionScheme;
	}

	public EditionScheme getEditionScheme() {
		return editionScheme;
	}

	@Override
	public Class getBaseClass() {
		return EditionSchemeAction.class;
	}

	@Override
	public boolean isTypeAssignableFrom(Type aType, boolean permissive) {
		// System.out.println("isTypeAssignableFrom " + aType + " (i am a " + this + ")");
		if (aType instanceof EditionSchemeActionType) {
			return getEditionScheme() == (((EditionSchemeActionType) aType).getEditionScheme());
		}
		return false;
	}

	@Override
	public String simpleRepresentation() {
		return "EditionSchemeActionType" + ":" + getEditionScheme();
	}

	@Override
	public String fullQualifiedRepresentation() {
		return "EditionSchemeActionType" + ":" + getEditionScheme();
	}

	@Override
	public String toString() {
		return simpleRepresentation();
	}
}