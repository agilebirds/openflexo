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
package org.openflexo.foundation.viewpoint;

import java.util.Collection;

import org.openflexo.antar.binding.BindingModel;
import org.openflexo.foundation.validation.Validable;
import org.openflexo.foundation.viewpoint.ViewPoint.ViewPointBuilder;

public class LocalizedEntry extends ViewPointObject {

	private LocalizedDictionary _dictionary;

	private String key;
	private String language;
	private String value;

	public LocalizedEntry(ViewPointBuilder builder) {
		super(builder);
	}

	public LocalizedEntry(LocalizedDictionary localizedDictionary, String key, String language, String value) {
		super((ViewPointBuilder) null);
		setLocalizedDictionary(localizedDictionary);
		this.key = key;
		this.language = language;
		this.value = value;
	}

	public void setLocalizedDictionary(LocalizedDictionary dict) {
		_dictionary = dict;
	}

	@Override
	public LocalizedDictionary getLocalizedDictionary() {
		return _dictionary;
	}

	@Override
	public ViewPoint getViewPoint() {
		return getLocalizedDictionary().getViewPoint();
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	@Override
	public BindingModel getBindingModel() {
		return getViewPoint().getBindingModel();
	}

	@Override
	public String getLanguageRepresentation() {
		return "<not_implemented:" + getFullyQualifiedName() + ">";
	}

	@Override
	public Collection<? extends Validable> getEmbeddedValidableObjects() {
		return null;
	}
}
