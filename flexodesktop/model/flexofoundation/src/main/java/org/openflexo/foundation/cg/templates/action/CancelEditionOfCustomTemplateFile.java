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
package org.openflexo.foundation.cg.templates.action;

import java.util.Vector;
import java.util.logging.Logger;

import org.openflexo.foundation.FlexoEditor;
import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.action.FlexoAction;
import org.openflexo.foundation.action.FlexoActionType;
import org.openflexo.foundation.cg.DuplicateCodeRepositoryNameException;
import org.openflexo.foundation.cg.templates.CGTemplateFile;
import org.openflexo.foundation.cg.templates.CGTemplateObject;


public class CancelEditionOfCustomTemplateFile extends FlexoAction<CancelEditionOfCustomTemplateFile, CGTemplateFile, CGTemplateObject>
{

    private static final Logger logger = Logger.getLogger(CancelEditionOfCustomTemplateFile.class.getPackage().getName());

	public static FlexoActionType<CancelEditionOfCustomTemplateFile, CGTemplateFile, CGTemplateObject> actionType = new FlexoActionType<CancelEditionOfCustomTemplateFile, CGTemplateFile, CGTemplateObject>(
			"cancel_template_edition", FlexoActionType.defaultGroup, FlexoActionType.NORMAL_ACTION_TYPE) {

        /**
         * Factory method
         */
		@Override
		public CancelEditionOfCustomTemplateFile makeNewAction(CGTemplateFile focusedObject, Vector<CGTemplateObject> globalSelection, FlexoEditor editor)
        {
            return new CancelEditionOfCustomTemplateFile(focusedObject, globalSelection, editor);
        }

		@Override
		protected boolean isVisibleForSelection(CGTemplateFile object, Vector<CGTemplateObject> globalSelection)
        {
            return ((object != null) && (object.isCustomTemplate()));
       }

		@Override
		protected boolean isEnabledForSelection(CGTemplateFile object, Vector<CGTemplateObject> globalSelection)
        {
            return ((object != null) && (object.isCustomTemplate()) && (object.isEdited()));
       }
                
    };
    
    static {
		FlexoModelObject.addActionForClass(CancelEditionOfCustomTemplateFile.actionType, CGTemplateFile.class);
    }
    

    
	CancelEditionOfCustomTemplateFile(CGTemplateFile focusedObject, Vector<CGTemplateObject> globalSelection, FlexoEditor editor)
    {
        super(actionType, focusedObject, globalSelection, editor);
    }

    @Override
	protected void doAction(Object context) throws DuplicateCodeRepositoryNameException
    {
    	logger.info ("Cancel edition of customTemplateFile "+getFocusedObject());
    	if (getFocusedObject() != null) {
    		getFocusedObject().cancelEdition();
     	}
     }

}
