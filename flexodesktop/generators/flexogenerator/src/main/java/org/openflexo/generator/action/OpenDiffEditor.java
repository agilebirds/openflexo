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
package org.openflexo.generator.action;

import java.util.Vector;

import org.openflexo.foundation.FlexoEditor;
import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.action.FlexoActionType;
import org.openflexo.foundation.action.FlexoGUIAction;
import org.openflexo.foundation.cg.CGFile;
import org.openflexo.foundation.cg.CGObject;
import org.openflexo.foundation.rm.cg.ContentSource;
import org.openflexo.generator.file.AbstractCGFile;


public class OpenDiffEditor extends FlexoGUIAction<OpenDiffEditor,CGFile,CGObject>
{

    public static FlexoActionType<OpenDiffEditor,CGFile,CGObject> actionType 
    = new FlexoActionType<OpenDiffEditor,CGFile,CGObject> (
    		"open_in_diff_editor",GCAction.SHOW_GROUP,FlexoActionType.NORMAL_ACTION_TYPE) {

        /**
         * Factory method
         */
        @Override
		public OpenDiffEditor makeNewAction(CGFile focusedObject, Vector<CGObject> globalSelection, FlexoEditor editor) 
        {
            return new OpenDiffEditor(focusedObject, globalSelection,editor);
        }

        @Override
		protected boolean isVisibleForSelection(CGFile object, Vector<CGObject> globalSelection) 
        {
            return (object instanceof AbstractCGFile);
       }

        @Override
		protected boolean isEnabledForSelection(CGFile object, Vector<CGObject> globalSelection) 
        {
            return (object != null);
       }
                
    };
    
    static {
        FlexoModelObject.addActionForClass (OpenDiffEditor.actionType, CGFile.class);
    }
    

    OpenDiffEditor (CGFile focusedObject, Vector<CGObject> globalSelection, FlexoEditor editor)
    {
        super(actionType, focusedObject, globalSelection,editor);
    }
	
	private ContentSource _leftSource;
	private ContentSource _rightSource;
	
	public ContentSource getLeftSource() 
	{
		return _leftSource;
	}
	
	public void setLeftSource(ContentSource leftSource)
	{
		_leftSource = leftSource;
	}
	
	public ContentSource getRightSource() 
	{
		return _rightSource;
	}
	
	public void setRightSource(ContentSource rightSource)
	{
		_rightSource = rightSource;
	}

}
