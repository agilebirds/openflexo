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
package org.openflexo.foundation.validation;

import java.util.Collection;

/**
 * Implemented by all objects on which validation is required
 * 
 * @author sguerin
 * 
 */
public interface Validable {

	/**
	 * Return default validation model for this object
	 * 
	 * @return ValidationModel
	 */
	public ValidationModel getDefaultValidationModel();

	/**
	 * Returns a flag indicating if this object is valid according to default validation model
	 * 
	 * @return boolean
	 */
	public boolean isValid();

	/**
	 * Returns a flag indicating if this object is valid according to specified validation model
	 * 
	 * @return boolean
	 */
	public boolean isValid(ValidationModel validationModel);

	/**
	 * Validates this object by building new ValidationReport object Default validation model is used to perform this validation.
	 */
	public ValidationReport validate();

	/**
	 * Validates this object by building new ValidationReport object Supplied validation model is used to perform this validation.
	 */
	public ValidationReport validate(ValidationModel validationModel);

	/**
	 * Validates this object by appending eventual issues to supplied ValidationReport. Default validation model is used to perform this
	 * validation.
	 * 
	 * @param report
	 *            , a ValidationReport object on which found issues are appened
	 */
	public void validate(ValidationReport report);

	/**
	 * Validates this object by appending eventual issues to supplied ValidationReport. Supplied validation model is used to perform this
	 * validation.
	 * 
	 * @param report
	 *            , a ValidationReport object on which found issues are appened
	 */
	public void validate(ValidationReport report, ValidationModel validationModel);

	/**
	 * Return an collection of all embedded objects on which the validation will be performed
	 * 
	 * @return a Vector of Validable objects
	 */
	public Collection<? extends Validable> getEmbeddedValidableObjects();

	/**
	 * Return by deep recursion (see {@link #getEmbeddedValidableObjects()} a collection containing all validable objects contained in this
	 * Validable object
	 * 
	 * @return
	 */
	public Collection<? extends Validable> getAllEmbeddedValidableObjects();

	/**
	 * Return a flag indicating if this object was deleted
	 * 
	 * @return
	 */
	public boolean isDeleted();
}
