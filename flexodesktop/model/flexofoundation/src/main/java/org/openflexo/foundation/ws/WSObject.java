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
package org.openflexo.foundation.ws;

import java.util.Vector;

import org.openflexo.foundation.FlexoException;
import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.rm.FlexoProject;
import org.openflexo.foundation.rm.FlexoResource;
import org.openflexo.foundation.rm.RMNotification;
import org.openflexo.foundation.rm.XMLStorageResourceData;

public abstract class WSObject extends FlexoModelObject {

	private FlexoWSLibrary wsLibrary;

	private boolean isModified = false;

	private String name;

	/**
	 * Should only be used by FlexoLibrary
	 */
	public WSObject(FlexoProject project) {
		super(project);
	}

	/**
	     *
	     */
	public WSObject(FlexoWSLibrary wsl) {
		super(wsl.getProject());
		this.wsLibrary = wsl;
	}

	/**
	 * Overrides getFlexoResource
	 * 
	 * @see org.openflexo.foundation.rm.FlexoResourceData#getFlexoResource()
	 */
	public FlexoResource getFlexoResource() {
		return wsLibrary.getFlexoResource();
	}

	/**
	 * Overrides setIsModified
	 * 
	 * @see org.openflexo.foundation.rm.FlexoResourceData#setIsModified()
	 */
	@Override
	public void setIsModified() {
		isModified = true;
	}

	/**
	 * Overrides clearIsModified
	 * 
	 * @see org.openflexo.foundation.rm.FlexoResourceData#clearIsModified(boolean clearLastMemoryUpdate)
	 */
	@Override
	public void clearIsModified(boolean clearLastMemoryUpdate) {
		isModified = false;
	}

	/**
	 * Overrides isModified
	 * 
	 * @see org.openflexo.foundation.rm.FlexoResourceData#isModified()
	 */
	@Override
	public boolean isModified() {
		return isModified;
	}

	/**
	 * Overrides notifyRM
	 * 
	 * @see org.openflexo.foundation.rm.FlexoResourceData#notifyRM(org.openflexo.foundation.rm.RMNotification)
	 */
	@Override
	public void notifyRM(RMNotification notification) throws FlexoException {
		// TODO Auto-generated method stub

	}

	/**
	 * Overrides receiveRMNotification
	 * 
	 * @see org.openflexo.foundation.rm.FlexoResourceData#receiveRMNotification(org.openflexo.foundation.rm.RMNotification)
	 */
	@Override
	public void receiveRMNotification(RMNotification notification) throws FlexoException {
		// TODO Auto-generated method stub

	}

	/**
	 * Overrides getXMLResourceData
	 * 
	 * @see org.openflexo.foundation.FlexoXMLSerializableObject#getXMLResourceData()
	 */
	@Override
	public XMLStorageResourceData getXMLResourceData() {
		return wsLibrary;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public void setName(String name) throws DuplicateWSObjectException {

		String old = this.name;
		this.name = name;
		setChanged();
		// notifyObservers(new DKVDataModification(-1, "name", old, name));
	}

	public FlexoWSLibrary getWSLibrary() {
		return wsLibrary;
	}

	public void setWSLibrary(FlexoWSLibrary lib) {
		wsLibrary = lib;
	}

	public boolean isDeletable() {
		return true;
	}

	@Override
	public void delete() {
		try {
			setName(null);
		} catch (DuplicateWSObjectException e) {
			e.printStackTrace();
		}
		super.delete();
		setDescription(null);
		// What is the goal of this... Setting the project to null caused
		// several null pointers exception
		// setProject(null);
		setWSLibrary(null);
	}

	public abstract Vector<? extends FlexoModelObject> getOrderedChildren();

	public abstract WSObject getParent();

	@Override
	public String toString() {
		return getFullyQualifiedName();
	}

	public String getLocalizedName() {
		return getName();
	}

}
