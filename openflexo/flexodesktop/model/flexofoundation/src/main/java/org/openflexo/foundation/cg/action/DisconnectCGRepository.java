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
package org.openflexo.foundation.cg.action;

import java.util.Vector;
import java.util.logging.Logger;

import org.openflexo.foundation.FlexoEditor;
import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.action.FlexoActionType;
import org.openflexo.foundation.cg.CGObject;
import org.openflexo.foundation.cg.DuplicateCodeRepositoryNameException;
import org.openflexo.foundation.cg.GenerationRepository;


public class DisconnectCGRepository extends AbstractGCAction<DisconnectCGRepository,GenerationRepository>
{

    private static final Logger logger = Logger.getLogger(DisconnectCGRepository.class.getPackage().getName());

    public static FlexoActionType<DisconnectCGRepository,GenerationRepository,CGObject> actionType = new FlexoActionType<DisconnectCGRepository,GenerationRepository,CGObject> ("disconnect_repository",FlexoActionType.defaultGroup,FlexoActionType.NORMAL_ACTION_TYPE) {

        /**
         * Factory method
         */
        @Override
		public DisconnectCGRepository makeNewAction(GenerationRepository focusedObject, Vector<CGObject> globalSelection, FlexoEditor editor) 
        {
            return new DisconnectCGRepository(focusedObject, globalSelection,editor);
        }

        @Override
		protected boolean isVisibleForSelection(GenerationRepository repository, Vector<CGObject> globalSelection) 
        {
            return ((repository != null) 
            		&& (repository.isEnabled()));
        }

        @Override
		protected boolean isEnabledForSelection(GenerationRepository repository, Vector<CGObject> globalSelection) 
        {
            return ((repository != null) 
            		&& (repository.isEnabled()));
       }
                
    };
    
    static {
        FlexoModelObject.addActionForClass (DisconnectCGRepository.actionType, GenerationRepository.class);
    }
    
   DisconnectCGRepository (GenerationRepository focusedObject, Vector<CGObject> globalSelection, FlexoEditor editor)
    {
        super(actionType, focusedObject, globalSelection,editor);
    }

    @Override
	protected void doAction(Object context) throws DuplicateCodeRepositoryNameException
    {
        logger.info ("DisconnectCGRepository");
        getFocusedObject().disconnect();
    }


}
