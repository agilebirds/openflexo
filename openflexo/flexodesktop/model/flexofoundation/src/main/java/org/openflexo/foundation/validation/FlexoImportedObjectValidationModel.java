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

import org.openflexo.foundation.TargetType;
import org.openflexo.foundation.rm.FlexoProject;
import org.openflexo.foundation.wkf.FlexoProcess;
import org.openflexo.foundation.wkf.Role;

/**
 * @author gpolet
 *
 */
public class FlexoImportedObjectValidationModel extends ValidationModel
{

    public FlexoImportedObjectValidationModel(FlexoProject project)
    {
        this(project,project.getTargetType());
    }
    
    /**
     * @param project
     */
    public FlexoImportedObjectValidationModel(FlexoProject project, TargetType targetType)
    {
        super(project,targetType);
        registerRule(new FlexoProcess.ImportedProcessShouldExistOnServer());
        registerRule(new Role.ImportedRoleShouldExistOnServer());
        
        // Notify that the validation model is complete and that inheritance
        // computation could be performed
        update();
    }

    /**
     * Overrides shouldNotifyValidation
     * @see org.openflexo.foundation.validation.ValidationModel#shouldNotifyValidation(org.openflexo.foundation.validation.Validable)
     */
    @Override
    protected boolean shouldNotifyValidation(Validable next)
    {
        return true;
    }

    /**
     * Return a boolean indicating if validation of each rule must be
     * notified
     * 
     * @param next
     * @return a boolean
     */
    @Override
	protected boolean shouldNotifyValidationRules()
    {
    	return true;
    }


    /**
     * Overrides fixAutomaticallyIfOneFixProposal
     * @see org.openflexo.foundation.validation.ValidationModel#fixAutomaticallyIfOneFixProposal()
     */
    @Override
    public boolean fixAutomaticallyIfOneFixProposal()
    {
        return false;
    }

}
