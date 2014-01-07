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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;

import org.openflexo.foundation.DefaultFlexoObject;
import org.openflexo.foundation.validation.Validable;
import org.openflexo.foundation.validation.ValidationModel;
import org.openflexo.foundation.validation.ValidationReport;

// TODO: convert this model with PAMELA
public abstract class DRMObject extends DefaultFlexoObject implements Validable {

	static final Logger logger = Logger.getLogger(DRMObject.class.getPackage().getName());

	private DocItemFolder folder;

	public DRMObject() {
		super();
	}

	public DocResourceCenter getDocResourceCenter() {
		if (getFolder() instanceof DocResourceCenter) {
			return (DocResourceCenter) getFolder();
		}
		if (getFolder() != null) {
			return getFolder().getDocResourceCenter();
		}
		return null;
	}

	// ==========================================================================
	// ========================= Validable interface
	// ============================
	// ==========================================================================

	/**
	 * Return default validation model for this object
	 * 
	 * @return
	 */
	@Override
	public ValidationModel getDefaultValidationModel() {
		if (getDocResourceCenter() != null) {
			return getDocResourceCenter().getDefaultValidationModel();
		}
		return null;
	}

	/**
	 * Returns a flag indicating if this object is valid according to default validation model
	 * 
	 * @return boolean
	 */
	@Override
	public boolean isValid() {
		return isValid(getDefaultValidationModel());
	}

	/**
	 * Returns a flag indicating if this object is valid according to specified validation model
	 * 
	 * @return boolean
	 */
	@Override
	public boolean isValid(ValidationModel validationModel) {
		return validationModel.isValid(this);
	}

	/**
	 * Validates this object by building new ValidationReport object Default validation model is used to perform this validation.
	 */
	@Override
	public ValidationReport validate() {
		return validate(getDefaultValidationModel());
	}

	/**
	 * Validates this object by building new ValidationReport object Supplied validation model is used to perform this validation.
	 */
	@Override
	public ValidationReport validate(ValidationModel validationModel) {
		return validationModel.validate(this);
	}

	/**
	 * Validates this object by appending eventual issues to supplied ValidationReport. Default validation model is used to perform this
	 * validation.
	 * 
	 * @param report
	 *            , a ValidationReport object on which found issues are appened
	 */
	@Override
	public void validate(ValidationReport report) {
		validate(report, getDefaultValidationModel());
	}

	/**
	 * Validates this object by appending eventual issues to supplied ValidationReport. Supplied validation model is used to perform this
	 * validation.
	 * 
	 * @param report
	 *            , a ValidationReport object on which found issues are appened
	 */
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
	public List<? extends DRMObject> getAllEmbeddedValidableObjects() {
		List<DRMObject> returned = new ArrayList<DRMObject>();
		returned.add(this);
		if (getEmbeddedValidableObjects() != null) {
			for (Iterator<? extends DRMObject> it = getEmbeddedValidableObjects().iterator(); it.hasNext();) {
				DRMObject next = it.next();
				returned.addAll(next.getAllEmbeddedValidableObjects());
			}
		}
		return returned;
	}

	/**
	 * Return a vector of all embedded objects at this level does NOT include itself
	 * 
	 * @return a Vector of Validable objects
	 */
	/**
	 * Return a vector of all embedded objects at this level does NOT include itself
	 * 
	 * @return a Vector of Validable objects
	 */
	@Override
	public Collection<? extends DRMObject> getEmbeddedValidableObjects() {
		return Collections.emptyList();
	}

	public abstract String getIdentifier();

	public DocItemFolder getFolder() {
		return folder;
	}

	public void setFolder(DocItemFolder folder) {
		this.folder = folder;
	}

}
