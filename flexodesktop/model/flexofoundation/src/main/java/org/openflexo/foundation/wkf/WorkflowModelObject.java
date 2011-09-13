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
package org.openflexo.foundation.wkf;

import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openflexo.foundation.RepresentableFlexoModelObject;
import org.openflexo.foundation.rm.FlexoProject;
import org.openflexo.foundation.rm.XMLStorageResourceData;
import org.openflexo.foundation.validation.Validable;
import org.openflexo.foundation.validation.ValidationModel;
import org.openflexo.foundation.validation.ValidationReport;
import org.openflexo.foundation.wkf.dm.WKFAttributeDataModification;
import org.openflexo.toolbox.EmptyVector;
import org.openflexo.xmlcode.XMLMapping;


/**
 * A WorkflowModelObject represents an object declared as workflow level.
 * This means that its visibility is not related to a particular process but
 * the entire workflow
 *
 * @author sylvain
 */

public abstract class WorkflowModelObject extends RepresentableFlexoModelObject implements Validable 
{

    private static final Logger logger = Logger.getLogger(WorkflowModelObject.class.getPackage().getName());
    
    protected static final Vector<WorkflowModelObject> EMPTY_VECTOR = EmptyVector.EMPTY_VECTOR(WorkflowModelObject.class);

    // ================================================================
    // ====================== Instance variables ======================
    // ================================================================

    private FlexoWorkflow _workflow;
    private FlexoProject _project;


    // ==========================================================
    // ================= Constructor ============================
    // ==========================================================

    /**
     * Create a new WorkflowModelObject.
     */
    public WorkflowModelObject(FlexoProject project, FlexoWorkflow workflow)
    {
        super(project);
        _project = project;
        _workflow = workflow;
    }

    /**
     * Create a new WorkflowModelObject.
     */
    public WorkflowModelObject(FlexoProject project)
    {
        this(project,null);
    }

    public FlexoWorkflow getFlexoWorkflow()
    {
    	if (_workflow != null) 
    		return _workflow;
       	if (_project != null && !isDeserializing) {
       		return _project.getWorkflow();
       	}
        return null;
     }
    
    public FlexoWorkflow getWorkflow()
    {
        return getFlexoWorkflow();
     }
    
    /**
     * Returns reference to the main object in which this XML-serializable
     * object is contained relating to storing scheme: here it's the workflow
     * itself
     *
     * @return this
     */
    @Override
    public XMLStorageResourceData getXMLResourceData()
    {
        return getFlexoWorkflow();
    }

     @Override
    public FlexoProject getProject()
    {
    	 if (getFlexoWorkflow()!=null)
    		 return getFlexoWorkflow().getProject();
    	 return null;
    }

    public void notifyAttributeModification(String attributeName, Object oldValue, Object newValue)
    {
        setChanged();
        notifyObservers(new WKFAttributeDataModification(attributeName, oldValue, newValue));
    }

    // ========================================================================
    // ========================= XML Serialization ============================
    // ========================================================================

    @Override
    public XMLMapping getXMLMapping()
    {
    	return getProject().getXmlMappings().getWorkflowMapping();
    }

    // ========================================================================
    // ========================= Validable interface ==========================
    // ========================================================================

    /**
     * Return default validation model for this object
     *
     * @return
     */
    @Override
	public ValidationModel getDefaultValidationModel()
    {
        if (getProject() != null) {
            return getProject().getWKFValidationModel();
        } else {
            if (logger.isLoggable(Level.WARNING))
                logger.warning("Could not access to project !");
        }
        return null;
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
	public abstract Vector<Validable> getAllEmbeddedValidableObjects();

}
