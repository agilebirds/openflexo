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
package org.openflexo.foundation.dkv;

import java.util.Vector;

import org.openflexo.foundation.Inspectors;
import org.openflexo.foundation.dkv.dm.DKVDataModification;
import org.openflexo.foundation.xml.FlexoDKVModelBuilder;
import org.openflexo.inspector.InspectableObject;

/**
 * @author gpolet
 * 
 */
public class Value extends DKVObject implements InspectableObject {

	private String value;

	private Language language;

	private Key key;

	public Value(FlexoDKVModelBuilder builder) {
		this(builder.dkvModel);
		initializeDeserialization(builder);
	}

	protected Value(DKVModel dkvModel) {
		super(dkvModel);
	}

	/**
	 * @param dl
	 */
	protected Value(DKVModel dl, Key key, Language lg) {
		this(dl);
		this.key = key;
		this.language = lg;
	}

	public Long getObjectFlexoID() {
		return new Long(getFlexoID());
	}

	/**
	 * Overrides getFullyQualifiedName
	 * 
	 * @see org.openflexo.foundation.FlexoModelObject#getFullyQualifiedName()
	 */
	@Override
	public String getFullyQualifiedName() {
		return language.name + "." + key.name;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		String old = this.value;
		if (value != null && value.trim().length() == 0) {
			this.value = null;
		} else {
			this.value = value;
		}
		setChanged();
		notifyObservers(new DKVDataModification("value", old, this.value));
	}

	public Key getKey() {
		return key;
	}

	public void setKey(Key key) {
		this.key = key;
	}

	public Language getLanguage() {
		return language;
	}

	public void setLanguage(Language language) {
		this.language = language;
	}

	/**
	 * Overrides getInspectorName
	 * 
	 * @see org.openflexo.inspector.InspectableObject#getInspectorName()
	 */
	@Override
	public String getInspectorName() {
		return Inspectors.IE.VALUE_INSPECTOR;
	}

	public String getDisplayString() {
		if (value == null) {
			return getFullyQualifiedName();
		}
		return value;
	}

	/**
	 * Overrides getClassNameKey
	 * 
	 * @see org.openflexo.foundation.FlexoModelObject#getClassNameKey()
	 */
	@Override
	public String getClassNameKey() {
		return "dkv_value";
	}

	/**
	 * Overrides isDeleteAble
	 * 
	 * @see org.openflexo.foundation.dkv.DKVObject#isDeleteAble()
	 */
	@Override
	public boolean isDeleteAble() {
		return false;
	}

	@Override
	public void undelete() {

	}

	@Override
	public Vector getAllEmbeddedValidableObjects() {
		return null;
	}

}
