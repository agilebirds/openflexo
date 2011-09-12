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
package org.openflexo.drm;

import java.util.Vector;

import org.openflexo.kvc.ChoiceList;
import org.openflexo.localization.FlexoLocalization;

public class Language extends DRMObject implements ChoiceList,
		Comparable<Language> {

	private String identifier;
	private String name;

	public Language(DRMBuilder builder) {
		this(builder.docResourceCenter);
		initializeDeserialization(builder);
	}

	public Language(DocResourceCenter docResourceCenter) {
		super(docResourceCenter);
	}

	protected static Language createLanguage(String anIdentifier, String aName,
			DocResourceCenter docResourceCenter) {
		Language returned = new Language(docResourceCenter);
		returned.identifier = anIdentifier;
		returned.name = aName;
		return returned;
	}

	@Override
	public String toString() {
		return getLocalizedName();
	}

	@Override
	public String getIdentifier() {
		return identifier;
	}

	public void setIdentifier(String identifier) {
		this.identifier = identifier;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public void setName(String name) {
		this.name = name;
	}

	public String getLocalizedName() {
		return FlexoLocalization.localizedForKey(name);
	}

	@Override
	public Vector getAvailableValues() {
		return getDocResourceCenter().getLanguages();
	}

	@Override
	public String getClassNameKey() {
		return "language";
	}

	@Override
	public int compareTo(Language o) {
		if (this == o) {
			return 0;
		} else if (identifier.equalsIgnoreCase("ENGLISH")) {
			return -1;
		} else {
			return 1;
		}
	}

}
