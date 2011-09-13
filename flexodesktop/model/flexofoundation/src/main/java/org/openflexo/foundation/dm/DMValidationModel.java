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
package org.openflexo.foundation.dm;

import java.util.logging.Logger;

import org.openflexo.foundation.TargetType;
import org.openflexo.foundation.dm.eo.DMEOAttribute;
import org.openflexo.foundation.dm.eo.DMEOEntity;
import org.openflexo.foundation.dm.eo.DMEOJoin;
import org.openflexo.foundation.dm.eo.DMEORelationship;
import org.openflexo.foundation.rm.FlexoProject;
import org.openflexo.foundation.validation.Validable;
import org.openflexo.foundation.validation.ValidationModel;


/**
 * Please comment this class
 * 
 * @author sguerin
 * 
 */
public class DMValidationModel extends ValidationModel
{

    @SuppressWarnings("unused")
	private static final Logger logger = Logger.getLogger(DMValidationModel.class.getPackage().getName());

    public DMValidationModel(FlexoProject project)
    {
        this(project,project.getTargetType());
    }
    
    public DMValidationModel(FlexoProject project, TargetType targetType)
    {
        super(project,targetType);
        registerRule(new DMObject.DataModelObjectNameMustBeValid());
        registerRule(new DMEntity.EntityInPackageCannotUseEntityOfTheDefaultPackage());
        registerRule(new DMProperty.PropertyMustDefineType());
        registerRule(new DMProperty.PropertyNameMustStartWithALetterAndFollowedByDigitsOrLetters());
        registerRule(new DMEOAttribute.DMEOAttributeMustReferToAValidEORelationship() );
        registerRule(new DMEOAttribute.DMEOAttributeMustHaveAJavaClass() );
        registerRule(new DMEORelationship.DMEORelationshipMustReferToAValidEORelationship() );
        registerRule(new DMEORelationship.DMEORelationshipToManyIsRarellyMandatory() );
        registerRule(new DMEORelationship.ForeignKeyTypeMustMatchPrimaryKey());
        registerRule(new DMEOEntity.DMEOEntityMustReferToAValidEOEntity() );
        registerRule(new DMEOEntity.DMEOEntityMustAtLeastOnePrimaryKey() );
        registerRule(new DMModel.DMModelMustDefineASuitableConnectionString() );
        registerRule(new DMEOJoin.JoinMustBeValid());
        registerRule(new WORepository.ADefaultComponentEntityMustBeDeclared());
               
        
        // Notify that the validation model is complete and that inheritance
        // computation could be performed
        update();
    }

    /**
     * Return a boolean indicating if validation of supplied object must be
     * notified
     * 
     * @param next
     * @return a boolean
     */
    @Override
	protected boolean shouldNotifyValidation(Validable next)
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
