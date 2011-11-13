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

import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.action.FlexoActionType;
import org.openflexo.foundation.dkv.action.DKVDelete;
import org.openflexo.foundation.dkv.dm.DKVDataModification;
import org.openflexo.foundation.rm.FlexoProject;
import org.openflexo.foundation.rm.XMLStorageResourceData;
import org.openflexo.foundation.validation.Validable;
import org.openflexo.foundation.validation.ValidationModel;
import org.openflexo.foundation.validation.ValidationReport;
import org.openflexo.xmlcode.XMLMapping;

/**
 * @author gpolet
 * 
 */
public abstract class DKVObject extends FlexoModelObject implements Validable {

	private FlexoProject project;

	protected DKVModel dkvModel;

	protected String name;

	/**
     *
     */
	public DKVObject(DKVModel dl) {
		super(dl.getProject());
		this.dkvModel = dl;
		this.project = dkvModel.getProject();
	}

	// Should not be called by other object than DKVModel
	public DKVObject(FlexoProject project) {
		super(project);
	}

	/**
	 * Overrides getProject
	 * 
	 * @see org.openflexo.foundation.rm.FlexoResourceData#getProject()
	 */
	@Override
	public FlexoProject getProject() {
		return project;
	}

	public abstract boolean isDeleteAble();

	/**
	 * Overrides getSpecificActionListForThatClass
	 * 
	 * @see org.openflexo.foundation.FlexoModelObject#getSpecificActionListForThatClass()
	 */
	@Override
	protected Vector<FlexoActionType> getSpecificActionListForThatClass() {
		Vector<FlexoActionType> v = super.getSpecificActionListForThatClass();
		v.add(DKVDelete.actionType);
		return v;
	}

	/**
	 * Overrides setProject
	 * 
	 * @see org.openflexo.foundation.rm.FlexoResourceData#setProject(org.openflexo.foundation.rm.FlexoProject)
	 */
	public void setProject(FlexoProject aProject) {
		this.project = aProject;
	}

	/**
	 * Overrides getXMLMapping
	 * 
	 * @see org.openflexo.foundation.FlexoXMLSerializableObject#getXMLMapping()
	 */
	@Override
	public XMLMapping getXMLMapping() {
		return getDkvModel().getXMLMapping();
	}

	/**
	 * Overrides getXMLResourceData
	 * 
	 * @see org.openflexo.foundation.FlexoXMLSerializableObject#getXMLResourceData()
	 */
	@Override
	public XMLStorageResourceData getXMLResourceData() {
		return dkvModel;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public void setName(String name) throws DuplicateDKVObjectException {
		String old = this.name;
		this.name = name;
		setChanged();
		notifyObservers(new DKVDataModification(-1, "name", old, name));
	}

	public DKVModel getDkvModel() {
		return dkvModel;
	}

	public DKVObject getParent() {
		if (this instanceof Value) {
			return ((Value) this).getKey();
		} else if (this instanceof Key) {
			return ((Key) this).getDomain();
		} else if (this instanceof Domain) {
			return ((Domain) this).getDkvModel().getDomainList();
		}
		return null;
	}

	@Override
	public ValidationModel getDefaultValidationModel() {
		return getProject().getDKVValidationModel();
	}

	@Override
	public boolean isValid() {
		return isValid(getDefaultValidationModel());
	}

	@Override
	public boolean isValid(ValidationModel validationModel) {
		return validationModel.isValid(this);
	}

	@Override
	public ValidationReport validate() {
		return validate(getDefaultValidationModel());
	}

	@Override
	public ValidationReport validate(ValidationModel validationModel) {
		return validationModel.validate(this);
	}

	@Override
	public void validate(ValidationReport report) {
		validate(report, getDefaultValidationModel());
	}

	@Override
	public void validate(ValidationReport report, ValidationModel validationModel) {
		validationModel.validate(this, report);
	}

	/**
	 * Return a vector of all embedded objects on which the validation will be performed
	 * 
	 * @return a Vector of Validable objects
	 */
	@Override
	public abstract Vector getAllEmbeddedValidableObjects();

	/**
	 * Returns fully qualified name for this object
	 * 
	 * @return
	 */
	@Override
	public abstract String getFullyQualifiedName();
}
