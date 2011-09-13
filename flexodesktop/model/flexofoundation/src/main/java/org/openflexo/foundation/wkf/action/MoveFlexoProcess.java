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
package org.openflexo.foundation.wkf.action;

import java.util.Enumeration;
import java.util.Vector;
import java.util.logging.Logger;

import org.openflexo.foundation.FlexoEditor;
import org.openflexo.foundation.FlexoException;
import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.action.FlexoActionType;
import org.openflexo.foundation.action.FlexoUndoableAction;
import org.openflexo.foundation.action.SetPropertyAction;
import org.openflexo.foundation.wkf.FlexoProcess;
import org.openflexo.foundation.wkf.WKFObject;


public class MoveFlexoProcess extends FlexoUndoableAction<MoveFlexoProcess,FlexoProcess,WKFObject>
{

    @SuppressWarnings("unused")
	private static final Logger logger = Logger.getLogger(MoveFlexoProcess.class.getPackage().getName());

    public static FlexoActionType<MoveFlexoProcess,FlexoProcess,WKFObject> actionType
    = new FlexoActionType<MoveFlexoProcess,FlexoProcess,WKFObject> (
    		"move_process",
    		FlexoActionType.defaultGroup,
    		FlexoActionType.NORMAL_ACTION_TYPE) {

        /**
         * Factory method
         */
        @Override
		public MoveFlexoProcess makeNewAction(FlexoProcess focusedObject, Vector<WKFObject> globalSelection, FlexoEditor editor)
        {
            return new MoveFlexoProcess(focusedObject, globalSelection, editor);
        }

        @Override
		protected boolean isVisibleForSelection(FlexoProcess object, Vector<WKFObject> globalSelection)
        {
            return object!=null && !object.isImported();
        }

        @Override
		protected boolean isEnabledForSelection(FlexoProcess object, Vector<WKFObject> globalSelection)
        {
            return isVisibleForSelection(object, globalSelection);
        }

    };

    static {
        FlexoModelObject.addActionForClass (MoveFlexoProcess.actionType, FlexoProcess.class);
    }

    private SetPropertyAction _setParentProcessAction;
    private FlexoProcess _newParentProcess;

    private boolean doImmediately = false;

	private boolean performValidate = true;

    MoveFlexoProcess (FlexoProcess focusedObject, Vector<WKFObject> globalSelection, FlexoEditor editor)
    {
        super(actionType, focusedObject, globalSelection, editor);
    }

	@Override
	protected void doAction(Object context) throws FlexoException
	{
		Vector<FlexoProcess> potentialRoot = new Vector<FlexoProcess>();
		boolean isMovingRoot = getFocusedObject().isRootProcess();
		if(isMovingRoot){
			Enumeration<FlexoProcess> en = getFocusedObject().getWorkflow().getAllLocalFlexoProcesses().elements();
			while(en.hasMoreElements()){
				FlexoProcess candidate = en.nextElement();
				if(candidate!=getFocusedObject()){
					if(candidate.getParentProcess()==null){
						potentialRoot.add(candidate);
					}
				}
			}


			if(potentialRoot.size()==0)
				throw new FlexoException("Sorry, you cannot move Root Process since there is no other potential root process.");
		}
		_setParentProcessAction = SetPropertyAction.actionType.makeNewEmbeddedAction(getFocusedObject(), null, this);
		_setParentProcessAction.setKey("parentProcess");
		_setParentProcessAction.setValue(getNewParentProcess());
		_setParentProcessAction.setPerformValidate(getPerformValidate());
		_setParentProcessAction.doAction();
		if(isMovingRoot) {
			FlexoProcess newRoot = potentialRoot.elementAt(0);
			newRoot.getWorkflow().setRootProcess(newRoot);
		}
	}

	@Override
	protected void undoAction(Object context) throws FlexoException
	{
		_setParentProcessAction.undoAction();
	}

	@Override
	protected void redoAction(Object context) throws FlexoException
	{
		_setParentProcessAction.redoAction();
	}

	public FlexoProcess getNewParentProcess()
	{
		return _newParentProcess;
	}

	public void setNewParentProcess(FlexoProcess newParentProcess)
	{
		_newParentProcess = newParentProcess;
	}

	public boolean isDoImmediately() {
		return doImmediately;
	}

	public void setDoImmediately(boolean doImmediately) {
		this.doImmediately = doImmediately;
	}

	public boolean getPerformValidate() {
		return performValidate;
	}

	public void setPerformValidate(boolean b) {
		this.performValidate = b;
	}

}