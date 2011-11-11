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

import java.util.Comparator;
import java.util.Vector;

import org.openflexo.foundation.AttributeDataModification;
import org.openflexo.foundation.Inspectors;
import org.openflexo.foundation.dkv.dm.DKVDataModification;
import org.openflexo.foundation.dkv.dm.ValueAdded;
import org.openflexo.foundation.dkv.dm.ValueRemoved;
import org.openflexo.foundation.utils.FlexoIndexManager;
import org.openflexo.foundation.utils.Sortable;
import org.openflexo.foundation.validation.Validable;
import org.openflexo.foundation.xml.FlexoDKVModelBuilder;
import org.openflexo.inspector.InspectableObject;

/**
 * @author gpolet
 * 
 */
public class Key extends DKVObject implements InspectableObject, Comparator, Sortable {
	private int index = -1;

	private Domain domain;

	public Key(FlexoDKVModelBuilder builder) {
		this(builder.dkvModel);
		initializeDeserialization(builder);
	}

	/**
     * 
     */
	public Key(DKVModel dl) {
		super(dl);
	}

	/**
     * 
     */
	public Key(DKVModel dl, Domain dom) {
		this(dl);
		this.domain = dom;
		this.index = dom.getKeys().size() + 1;
	}

	@Override
	public void delete() {
		getDomain().removeFromKeys(this);
		super.delete();
		this.deleteObservers();
	}

	@Override
	public void undelete() {
		super.undelete();
		getDomain().addToKeys(this);
	}

	/**
	 * Overrides getFullyQualifiedName
	 * 
	 * @see org.openflexo.foundation.FlexoModelObject#getFullyQualifiedName()
	 */
	@Override
	public String getFullyQualifiedName() {
		return "KEY." + name;
	}

	public Domain getDomain() {
		return domain;
	}

	public void setDomain(Domain domain) {
		this.domain = domain;
	}

	/**
	 * Overrides getInspectorName
	 * 
	 * @see org.openflexo.inspector.InspectableObject#getInspectorName()
	 */
	@Override
	public String getInspectorName() {
		return Inspectors.IE.KEY_INSPECTOR;
	}

	@Override
	public int getIndex() {
		return index;
	}

	@Override
	public void setIndex(int index) {
		if (isDeserializing()) {
			setIndexValue(index);
			return;
		}
		FlexoIndexManager.switchIndexForKey(this.index, index, this);
		if (getIndex() != index) {
			setChanged();
			AttributeDataModification dm = new AttributeDataModification("index", null, getIndex());
			dm.setReentrant(true);
			notifyObservers(dm);
		}
	}

	@Override
	public int getIndexValue() {
		return getIndex();
	}

	/**
	 * Sets the value for the index. This method should only be used
	 * 
	 * @param index
	 */
	@Override
	public void setIndexValue(int index) {
		int old = this.index;
		if (old == index)
			return;
		this.index = index;
		if (isDeserializing())
			return;
		DKVDataModification dm = new DKVDataModification(-1, "index", new Integer(old), new Integer(index));
		setChanged();
		notifyObservers(dm);
		getDomain().getKeyList().setChanged();
		getDomain().getValueList().setChanged();
		getDomain().getKeyList().notifyObservers(dm);
		getDomain().getValueList().notifyObservers(dm);
	}

	/**
	 * Overrides compare
	 * 
	 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
	 */
	@Override
	public int compare(Object o1, Object o2) {
		return ((Key) o1).index - ((Key) o2).index;
	}

	/**
	 * Overrides getClassNameKey
	 * 
	 * @see org.openflexo.foundation.FlexoModelObject#getClassNameKey()
	 */
	@Override
	public String getClassNameKey() {
		return "dkv_key";
	}

	/**
	 * Overrides isDeleteAble
	 * 
	 * @see org.openflexo.foundation.dkv.DKVObject#isDeleteAble()
	 */
	@Override
	public boolean isDeleteAble() {
		return true;
	}

	public void notifyValueAdded(Value aValue) {
		setChanged();
		notifyObservers(new ValueAdded(aValue));
	}

	public void notifyValueRemoved(Value aValue) {
		setChanged();
		notifyObservers(new ValueRemoved(aValue));
	}

	/**
	 * Overrides setName
	 * 
	 * @throws DuplicateDKVObjectException
	 * @see org.openflexo.foundation.dkv.DKVObject#setName(java.lang.String)
	 */
	@Override
	public void setName(String name) throws DuplicateDKVObjectException {
		if (name.equals(getName()))
			return;
		if (isDeserializing()) {
			super.setName(name);
		} else {
			if (domain.getKeyNamed(name) != null)
				throw new DuplicateDKVObjectException(this);
			boolean reg_unreg = domain.getKeys().indexOf(this) > -1;
			if (reg_unreg)
				domain.removeFromKeys(this);
			super.setName(name);
			if (reg_unreg)
				domain.addToKeys(this);
		}
	}

	@Override
	public Key[] getCollection() {
		return getDomain().getKeys().toArray(new Key[0]);
	}

	@Override
	public Vector<Validable> getAllEmbeddedValidableObjects() {
		return null;
	}
}
