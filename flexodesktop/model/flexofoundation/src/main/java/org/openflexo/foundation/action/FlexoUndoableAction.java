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
package org.openflexo.foundation.action;

import java.awt.event.ActionEvent;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Vector;
import java.util.WeakHashMap;

import org.openflexo.foundation.FlexoEditor;
import org.openflexo.foundation.FlexoException;
import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.rm.FlexoProject;
import org.openflexo.foundation.rm.FlexoResource;
import org.openflexo.foundation.rm.FlexoResourceData;
import org.openflexo.foundation.rm.FlexoStorageResource;


/**
 * 
 * Abstract representation of an action on Flexo model (model edition primitive)
 * which can be undone and redone
 * 
 * @author sguerin
 */
public abstract class FlexoUndoableAction<A extends FlexoUndoableAction<?, T1, T2>, T1 extends FlexoModelObject, T2 extends FlexoModelObject>
        extends FlexoAction<A, T1, T2>
{
    private WeakHashMap<FlexoStorageResource,Object> _modifiedResources;
    
    public FlexoUndoableAction(FlexoActionType<A, T1, T2> actionType, T1 focusedObject, Vector<T2> globalSelection, FlexoEditor editor)
    {
        super(actionType, focusedObject, globalSelection, editor);
        _modifiedResources = new WeakHashMap<FlexoStorageResource,Object>();
    }

    public final void undoAction() throws UndoException
    {
        try {
            undoAction(null);
        } catch (FlexoException e) {
            throw new UndoException(e);
        }
    }

    protected FlexoProject getProject() {
    	if (getEditor()!=null)
    		return getEditor().getProject();
    	return null;
    }
    
    /**
     * Overrides doAction
     * 
     * @see org.openflexo.foundation.action.FlexoAction#doAction(java.awt.event.ActionEvent)
     */
    @Override
    public final A doAction(ActionEvent e) throws FlexoException
    {
        // Let's keep in memory the modified resources
        _modifiedResources.clear();
        if (getProject()!=null) {
            Enumeration<FlexoResource<FlexoResourceData>> en = getProject().getResources().elements();
            while (en.hasMoreElements()) {
                FlexoResource r = en.nextElement();
                if (r instanceof FlexoStorageResource && !((FlexoStorageResource)r).isModified())
                    _modifiedResources.put((FlexoStorageResource) r,null);
            }
        }
        A action;
        try {
            action = super.doAction(e);
        } catch (FlexoException e1) {
            _modifiedResources.clear();
            throw e1;
        }
        
        // Now we remove all the resources that are still not modified. The left delta are the resources that have been directly modified by this action (and the embedded ones)
        if (getProject()!=null) {
            Enumeration<FlexoResource<FlexoResourceData>> en = getProject().getResources().elements();
            while (en.hasMoreElements()) {
                FlexoResource r = en.nextElement();
                if (r instanceof FlexoStorageResource && !((FlexoStorageResource)r).isModified())
                    _modifiedResources.remove(r);
            }
        }
        
        // Finally, we remove the modified storage resources that have been modified by the embedded undoable actions
        if (getProject()!=null) {
            Enumeration<FlexoAction> en = getEmbbededActionsExecutedDuringCore().elements();
            while (en.hasMoreElements()) {
                FlexoAction action2 = en.nextElement();
                if (action2 instanceof FlexoUndoableAction) {
                    Iterator<FlexoStorageResource> i = ((FlexoUndoableAction)action2)._modifiedResources.keySet().iterator();
                    while (i.hasNext()) {
                        FlexoStorageResource r = i.next();
                        if (_modifiedResources.get(r)!=null)
                            i.remove();
                    }
                }
            }
            
            en = getEmbbededActionsExecutedDuringInitializer().elements();
            while (en.hasMoreElements()) {
                FlexoAction action2 = en.nextElement();
                if (action2 instanceof FlexoUndoableAction) {
                    Iterator<FlexoStorageResource> i = ((FlexoUndoableAction)action2)._modifiedResources.keySet().iterator();
                    //while (en.hasMoreElements()) {
                    while (i.hasNext()) {
                        FlexoStorageResource r = i.next();
                        if (_modifiedResources.get(r)!=null)
                        	i.remove();
                    }
                }
            }
            
            en = getEmbbededActionsExecutedDuringFinalizer().elements();
            while (en.hasMoreElements()) {
                FlexoAction action2 = en.nextElement();
                if (action2 instanceof FlexoUndoableAction) {
                    Iterator<FlexoStorageResource> i = ((FlexoUndoableAction)action2)._modifiedResources.keySet().iterator();
                    //while (en.hasMoreElements()) {
                    while (i.hasNext()) {
                       FlexoStorageResource r = i.next();
                        if (_modifiedResources.get(r)!=null)
                        	i.remove();
                    }
                }
            }
        }
        return action;
    }

    public final void undoAction(ActionEvent e) throws FlexoException
    {
        if (getUndoInitializer() != null) {
            executionStatus = ExecutionStatus.EXECUTING_UNDO_INITIALIZER;
            getUndoInitializer().run(e, (A) this);
        }
        if (getEditor() != null) {
            getEditor().actionWillBeUndone(this);
        }
        try {
            executionStatus = ExecutionStatus.EXECUTING_UNDO_CORE;
            undoAction(getContext());
            executionStatus = ExecutionStatus.HAS_SUCCESSFULLY_UNDONE;
            if (getEditor() != null) {
                getEditor().actionHasBeenUndone(this, true);
            }
        } catch (FlexoException exception) {
            executionStatus = ExecutionStatus.FAILED_UNDO_EXECUTION;
            if (getEditor() != null) {
                getEditor().actionHasBeenUndone(this, false);
            }
            if (getExceptionHandler() != null) {
                if (getExceptionHandler().handleException(exception, (A) this)) {
                    return;
                } else {
                    throw exception;
                }
            }
        }
        if (getUndoFinalizer() != null && executionStatus == ExecutionStatus.HAS_SUCCESSFULLY_UNDONE) {
            executionStatus = ExecutionStatus.EXECUTING_UNDO_FINALIZER;
            executionStatus = (getUndoFinalizer().run(e, (A) this) ? ExecutionStatus.HAS_SUCCESSFULLY_UNDONE
                    : ExecutionStatus.FAILED_UNDO_EXECUTION);
        }
        Iterator<FlexoStorageResource> i = _modifiedResources.keySet().iterator();
        while (i.hasNext()) {
            FlexoStorageResource r = i.next();
            r.getResourceData().clearIsModified(true);
        }
    }

    protected abstract void undoAction(Object context) throws FlexoException;

    public FlexoActionUndoFinalizer<? super A> getUndoFinalizer()
    {
        return getActionType().getUndoFinalizerForEditor(getEditor());
    }

    public FlexoActionUndoInitializer<? super A> getUndoInitializer()
    {
        return getActionType().getUndoInitializerForEditor(getEditor());
    }

    public final void redoAction() throws RedoException
    {
        try {
            redoAction(null);
        } catch (FlexoException e) {
            throw new RedoException(e);
        }
    }

    public final void redoAction(ActionEvent e) throws FlexoException
    {
        if (getRedoInitializer() != null) {
            executionStatus = ExecutionStatus.EXECUTING_REDO_INITIALIZER;
            getRedoInitializer().run(e, (A) this);
        }
        if (getEditor() != null) {
            getEditor().actionWillBeRedone(this);
        }
        try {
            executionStatus = ExecutionStatus.EXECUTING_REDO_CORE;
            redoAction(getContext());
            executionStatus = ExecutionStatus.HAS_SUCCESSFULLY_REDONE;
            if (getEditor() != null) {
                getEditor().actionHasBeenRedone(this, true);
            }
        } catch (FlexoException exception) {
            executionStatus = ExecutionStatus.FAILED_REDO_EXECUTION;
            if (getEditor() != null) {
                getEditor().actionHasBeenRedone(this, false);
            }
            if (getExceptionHandler() != null) {
                if (getExceptionHandler().handleException(exception, (A) this)) {
                    return;
                } else {
                    throw exception;
                }
            }
        }
        if (getRedoFinalizer() != null && executionStatus == ExecutionStatus.HAS_SUCCESSFULLY_REDONE) {
            executionStatus = ExecutionStatus.EXECUTING_REDO_FINALIZER;
            executionStatus = (getRedoFinalizer().run(e, (A) this) ? ExecutionStatus.HAS_SUCCESSFULLY_REDONE
                    : ExecutionStatus.FAILED_REDO_EXECUTION);
        }
    }

    protected abstract void redoAction(Object context) throws FlexoException;

    public FlexoActionRedoFinalizer<? super A> getRedoFinalizer()
    {
        return getActionType().getRedoFinalizerForEditor(getEditor());
    }

     public FlexoActionRedoInitializer<? super A> getRedoInitializer()
    {
         return getActionType().getRedoInitializerForEditor(getEditor());
            }

     @Override
	public void delete()
    {
        _modifiedResources.clear();
        super.delete();
    }

}
