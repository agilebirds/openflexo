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

import java.io.File;
import java.io.IOException;
import java.util.Vector;
import java.util.logging.Logger;

import org.openflexo.foundation.FlexoEditor;
import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.IOFlexoException;
import org.openflexo.foundation.TargetType;
import org.openflexo.foundation.action.FlexoAction;
import org.openflexo.foundation.action.FlexoActionType;
import org.openflexo.foundation.cg.templates.CGTemplate;
import org.openflexo.foundation.cg.templates.CGTemplateFile;
import org.openflexo.foundation.cg.templates.CGTemplateObject;
import org.openflexo.foundation.cg.templates.CGTemplateSet;
import org.openflexo.foundation.cg.templates.CustomCGTemplateRepository;
import org.openflexo.toolbox.FileUtils;


public class RedefineCustomTemplateFile extends FlexoAction<RedefineCustomTemplateFile, CGTemplate, CGTemplateObject>
{

    private static final Logger logger = Logger.getLogger(RedefineCustomTemplateFile.class.getPackage().getName());

	public static FlexoActionType<RedefineCustomTemplateFile, CGTemplate, CGTemplateObject> actionType = new FlexoActionType<RedefineCustomTemplateFile, CGTemplate, CGTemplateObject>(
			"redefine_template", FlexoActionType.defaultGroup, FlexoActionType.NORMAL_ACTION_TYPE) {

        /**
         * Factory method
         */
		@Override
		public RedefineCustomTemplateFile makeNewAction(CGTemplate focusedObject, Vector<CGTemplateObject> globalSelection, FlexoEditor editor)
        {
            return new RedefineCustomTemplateFile(focusedObject, globalSelection,editor);
        }

		@Override
		protected boolean isVisibleForSelection(CGTemplate object, Vector<CGTemplateObject> globalSelection)
        {
            return ((object != null) && (object.isApplicationTemplate()));
       }

		@Override
		protected boolean isEnabledForSelection(CGTemplate object, Vector<CGTemplateObject> globalSelection)
        {
            return ((object != null) && (object.isApplicationTemplate()));
       }
                
    };
    
    static {
		FlexoModelObject.addActionForClass(RedefineCustomTemplateFile.actionType, CGTemplate.class);
    }
    

    private CustomCGTemplateRepository _repository;
    private TargetType _target;
	private CGTemplateFile _newTemplateFile;
    
    RedefineCustomTemplateFile(CGTemplate focusedObject, Vector<CGTemplateObject> globalSelection, FlexoEditor editor)
    {
        super(actionType, focusedObject, globalSelection, editor);
    }

    @Override
	protected void doAction(Object context) throws IOFlexoException
    {
    	logger.info ("Redefine CustomTemplateFile "+getFocusedObject());
    	if ((getFocusedObject() != null) && (_repository != null)) {
    		File createdFile;
    		CGTemplateSet set;
    		if (getTarget() == null) {
    			createdFile = new File(_repository.getDirectory(),getFocusedObject().getRelativePath());
    			set = _repository.getCommonTemplates();
    		}
    		else {
				createdFile = new File(new File(_repository.getDirectory(), getTarget().getTemplateFolderName()), getFocusedObject().getRelativePath());
       			set = _repository.getTemplateSetForTarget(getTarget(),true);
    		}
			logger.info("CreatedFile="+createdFile.getAbsolutePath());
			try {
				FileUtils.saveToFile(createdFile, getFocusedObject().getContent());
			} catch (IOException e) {
				throw new IOFlexoException(e);
			}
			_repository.refresh();
			_newTemplateFile = (CGTemplateFile) set.getTemplate(getFocusedObject().getRelativePath());
     	}
     }

	public CustomCGTemplateRepository getRepository() 
	{
		return _repository;
	}

	public void setRepository(CustomCGTemplateRepository repository)
	{
		_repository = repository;
	}

	public TargetType getTarget() 
	{
		return _target;
	}

	public void setTarget(TargetType target) 
	{
		_target = target;
	}

	public CGTemplateFile getNewTemplateFile()
	{
		return _newTemplateFile;
	}


}
