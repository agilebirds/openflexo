/*
 * (c) Copyright 2010-2011 AgileBirds
 *
 * This file is part of OpenFlexo.
 *
 * OpenFlexo is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * OpenFlexo is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with OpenFlexo. If not, see <http://www.gnu.org/licenses/>.
 *
 */
package org.openflexo.foundation.ontology.owl;

import java.util.Vector;

import org.openflexo.localization.Language;
import org.openflexo.toolbox.StringUtils;

import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.Statement;

public abstract class PropertyStatement extends OWLStatement {

	public PropertyStatement(OWLObject<?> subject, Statement s) {
		super(subject, s);
	}

	public abstract OWLProperty getProperty();

	public abstract Literal getLiteral();

	public boolean hasLitteralValue() {
		return getLiteral() != null;
	}

	public final boolean isStringValue() {
		return getLiteral() != null && getLiteral().getDatatype().getJavaClass().equals(String.class);
	}

	private Language language = null;
	private String stringValue = null;

	public final String getStringValue() {
		if (getLiteral() == null) {
			return null;
		}
		if (stringValue == null) {
			stringValue = getLiteral().getString();
		}
		return stringValue;
	}

	public final Language getLanguage() {
		if (getLiteral() == null) {
			return null;
		}
		if (language == null) {
			language = Language.retrieveLanguage(getLiteral().getLanguage());
		}
		return language;
	}

	public final String getLanguageTag() {
		if (getLanguage() == null) {
			return "";
		}
		return getLanguage().getTag();
	}

	public final void setLanguage(Language aLanguage) {
		if (aLanguage != getLanguage()) {
			// Take care to this point: this object will disappear and be replaced by a new one
			// during updateOntologyStatements() !!!!!
			if (aLanguage != null) {
				getSubject().getOntResource().addProperty(getProperty().getOntProperty(), getStringValue(), aLanguage.getTag());
			} else {
				getSubject().getOntResource().addProperty(getProperty().getOntProperty(), getStringValue());
			}
			getSubject().removePropertyStatement(this);
			language = aLanguage;
		}
	}

	public final void setStringValue(String aValue, String language) {
		if (StringUtils.isSame(aValue, getStringValue())) {
			return;
		}
		// Take care to this point: this object will disappear and be replaced by a new one
		// during updateOntologyStatements() !!!!!
		getSubject().getOntResource().addProperty(getProperty().getOntProperty(), aValue, language);
		getSubject().removePropertyStatement(this);
		stringValue = aValue;
	}

	/**
	 * Creates a new Statement equals to this one with a new String value
	 * 
	 * @param aValue
	 */
	public final void setStringValue(String aValue) {
		if (StringUtils.isSame(aValue, getStringValue())) {
			return;
		}
		// Take care to this point: this object will disappear and be replaced by a new one
		// during updateOntologyStatements() !!!!!
		if (getLanguage() != null) {
			getSubject().getOntResource().addProperty(getProperty().getOntProperty(), aValue, getLanguage().getTag());
		} else {
			getSubject().getOntResource().addProperty(getProperty().getOntProperty(), aValue);
		}
		getSubject().removePropertyStatement(this);
		stringValue = aValue;
	}

	public abstract boolean isAnnotationProperty();

	public Vector<Language> allLanguages() {
		return Language.availableValues();
	}

}
