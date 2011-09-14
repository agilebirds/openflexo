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

import java.util.Enumeration;
import java.util.Vector;

import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.rm.FlexoProject;
import org.openflexo.foundation.rm.XMLStorageResourceData;
import org.openflexo.foundation.validation.Validable;
import org.openflexo.foundation.validation.ValidationModel;
import org.openflexo.foundation.validation.ValidationReport;
import org.openflexo.xmlcode.XMLMapping;


public abstract class DRMObject extends FlexoModelObject implements Validable {

    protected DocResourceCenter _docResourceCenter;
    
    public DRMObject (DocResourceCenter docResourceCenter)
    {
        super();
        _docResourceCenter = docResourceCenter;
    }
    
    public DocResourceCenter getDocResourceCenter()
    {
        return _docResourceCenter;
    }
    
    @Override
	public FlexoProject getProject() 
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
	public String getFullyQualifiedName() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
	public XMLMapping getXMLMapping() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
	public XMLStorageResourceData getXMLResourceData() 
    {
        return getDocResourceCenter();
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
	public ValidationModel getDefaultValidationModel()
    {
        return DocResourceManager.instance().getDRMValidationModel();
    }

    /**
     * Returns a flag indicating if this object is valid according to default
     * validation model
     * 
     * @return boolean
     */
    @Override
	public boolean isValid()
    {
        return isValid(getDefaultValidationModel());
    }

    /**
     * Returns a flag indicating if this object is valid according to specified
     * validation model
     * 
     * @return boolean
     */
    @Override
	public boolean isValid(ValidationModel validationModel)
    {
        return validationModel.isValid(this);
    }

    /**
     * Validates this object by building new ValidationReport object Default
     * validation model is used to perform this validation.
     */
    @Override
	public ValidationReport validate()
    {
        return validate(getDefaultValidationModel());
    }

    /**
     * Validates this object by building new ValidationReport object Supplied
     * validation model is used to perform this validation.
     */
    @Override
	public ValidationReport validate(ValidationModel validationModel)
    {
        return validationModel.validate(this);
    }

    /**
     * Validates this object by appending eventual issues to supplied
     * ValidationReport. Default validation model is used to perform this
     * validation.
     * 
     * @param report,
     *            a ValidationReport object on which found issues are appened
     */
    @Override
	public void validate(ValidationReport report)
    {
        validate(report, getDefaultValidationModel());
    }

    /**
     * Validates this object by appending eventual issues to supplied
     * ValidationReport. Supplied validation model is used to perform this
     * validation.
     * 
     * @param report,
     *            a ValidationReport object on which found issues are appened
     */
    @Override
	public void validate(ValidationReport report, ValidationModel validationModel)
    {
        validationModel.validate(this, report);
    }

    /**
     * Return a vector of all embedded objects on which the validation will be
     * performed
     * 
     * @return a Vector of Validable objects
     */
    @Override
	public Vector<Validable> getAllEmbeddedValidableObjects()
    {
        Vector<Validable> returned = new Vector<Validable>();
        returned.add(this);
        if (getEmbeddedValidableObjects() != null) {
            for (Enumeration en=getEmbeddedValidableObjects().elements(); en.hasMoreElements();) {
                DRMObject next = (DRMObject)en.nextElement();
                returned.addAll(next.getAllEmbeddedValidableObjects());
            }
        }
        return returned;
    }

    /**
     * Return a vector of all embedded objects at this level
     * does NOT include itself
     * 
     * @return a Vector of Validable objects
     */
    /**
     * Return a vector of all embedded objects at this level
     * does NOT include itself
     * 
     * @return a Vector of Validable objects
     */
    public Vector getEmbeddedValidableObjects()
    {
        return null;
    }

    @Override
	public String getSerializationIdentifier()
    {
        return getIdentifier();
    }
    
    public abstract String getIdentifier();

}
