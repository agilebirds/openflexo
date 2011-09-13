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
package org.openflexo.foundation;

import java.util.Hashtable;

import javax.swing.Icon;

import org.openflexo.foundation.action.FlexoAction;
import org.openflexo.foundation.action.FlexoActionEnableCondition;
import org.openflexo.foundation.action.FlexoActionFinalizer;
import org.openflexo.foundation.action.FlexoActionInitializer;
import org.openflexo.foundation.action.FlexoActionRedoFinalizer;
import org.openflexo.foundation.action.FlexoActionRedoInitializer;
import org.openflexo.foundation.action.FlexoActionType;
import org.openflexo.foundation.action.FlexoActionUndoFinalizer;
import org.openflexo.foundation.action.FlexoActionUndoInitializer;
import org.openflexo.foundation.action.FlexoActionVisibleCondition;
import org.openflexo.foundation.action.FlexoExceptionHandler;
import org.openflexo.foundation.rm.FlexoProject;
import org.openflexo.foundation.utils.FlexoProgressFactory;


public class DefaultFlexoEditor implements FlexoEditor
{
	private FlexoProject _project;
	
    private final Hashtable<FlexoActionType,FlexoActionInitializer> _initializers;
    private final Hashtable<FlexoActionType,FlexoActionFinalizer> _finalizers;
    private final Hashtable<FlexoActionType,FlexoActionUndoInitializer> _undoInitializers;
    private final Hashtable<FlexoActionType,FlexoActionUndoFinalizer> _undoFinalizers;
    private final Hashtable<FlexoActionType,FlexoActionRedoInitializer> _redoInitializers;
    private final Hashtable<FlexoActionType,FlexoActionRedoFinalizer> _redoFinalizers;
    private final Hashtable<FlexoActionType,FlexoExceptionHandler> _exceptionHandlers;
    private final Hashtable<FlexoActionType,FlexoActionEnableCondition> _enableConditions;
    private final Hashtable<FlexoActionType,FlexoActionVisibleCondition> _visibleConditions;

	public DefaultFlexoEditor()
	{
		_initializers = new Hashtable<FlexoActionType,FlexoActionInitializer>();
		_finalizers = new Hashtable<FlexoActionType,FlexoActionFinalizer>();
		_undoInitializers = new Hashtable<FlexoActionType,FlexoActionUndoInitializer>();
		_undoFinalizers = new Hashtable<FlexoActionType,FlexoActionUndoFinalizer>();
		_redoInitializers = new Hashtable<FlexoActionType,FlexoActionRedoInitializer>();
		_redoFinalizers = new Hashtable<FlexoActionType,FlexoActionRedoFinalizer>();
		_exceptionHandlers = new Hashtable<FlexoActionType,FlexoExceptionHandler>();
		_enableConditions = new Hashtable<FlexoActionType,FlexoActionEnableCondition>();
		_visibleConditions = new Hashtable<FlexoActionType,FlexoActionVisibleCondition>();
	}

	public DefaultFlexoEditor(FlexoProject project)
	{
		this();
		_project = project;
	}
	
	@Override
	public FlexoProject getProject()
	{
		return _project;
	}

	@Override
	public void actionWillBePerformed(FlexoAction action)
	{
	}

	@Override
	public void actionHasBeenPerformed(FlexoAction action, boolean success) 
	{
	}

	@Override
	public void actionWillBeUndone(FlexoAction action) 
	{
	}
	
	@Override
	public void actionHasBeenUndone(FlexoAction action, boolean success) 
	{
	}
	
	@Override
	public void actionWillBeRedone(FlexoAction action) 
	{
	}
	
	@Override
	public void actionHasBeenRedone(FlexoAction action, boolean success) 
	{
	}
	

	@Override
	public boolean isActionVisible(FlexoActionType actionType)
	{
		return true;
	}
	
	@Override
	public boolean isActionEnabled(FlexoActionType actionType)
	{
		return true;
	}
	
	@Override
	public void notifyObjectCreated (FlexoModelObject object) 
	{
	}
	
	@Override
	public void notifyObjectDeleted (FlexoModelObject object) 
	{
	}
	
	@Override
	public void notifyObjectChanged (FlexoModelObject object) 
	{
	}
	
	@Override
	public  <A extends FlexoAction<?,T1,T2>, T1 extends FlexoModelObject, T2 extends FlexoModelObject>
    FlexoActionFinalizer<? super A> getFinalizerFor(FlexoActionType<A,T1,T2> actionType)
    {
    	return _finalizers.get(actionType);
    }

    @Override
	public  <A extends FlexoAction<?,T1,T2>, T1 extends FlexoModelObject, T2 extends FlexoModelObject>
    FlexoActionInitializer<? super A> getInitializerFor(FlexoActionType<A,T1,T2> actionType)
    {
    	return _initializers.get(actionType);
    }

    @Override
	public  <A extends FlexoAction<?,T1,T2>, T1 extends FlexoModelObject, T2 extends FlexoModelObject>
    FlexoActionUndoFinalizer<? super A> getUndoFinalizerFor(FlexoActionType<A,T1,T2> actionType)
    {
    	return _undoFinalizers.get(actionType);
    }

    @Override
	public  <A extends FlexoAction<?,T1,T2>, T1 extends FlexoModelObject, T2 extends FlexoModelObject>
    FlexoActionUndoInitializer<? super A> getUndoInitializerFor(FlexoActionType<A,T1,T2> actionType)
    {
    	return _undoInitializers.get(actionType);
    }

    @Override
	public  <A extends FlexoAction<?,T1,T2>, T1 extends FlexoModelObject, T2 extends FlexoModelObject>
    FlexoActionRedoFinalizer<? super A> getRedoFinalizerFor(FlexoActionType<A,T1,T2> actionType)
    {
    	return _redoFinalizers.get(actionType);
    }

    @Override
	public  <A extends FlexoAction<?,T1,T2>, T1 extends FlexoModelObject, T2 extends FlexoModelObject>
    FlexoActionRedoInitializer<? super A> getRedoInitializerFor(FlexoActionType<A,T1,T2> actionType)
    {
    	return _redoInitializers.get(actionType);
    }

    @Override
	public  <A extends FlexoAction<?,T1,T2>, T1 extends FlexoModelObject, T2 extends FlexoModelObject>
    FlexoActionEnableCondition<? super A,T1,T2> getEnableConditionFor(FlexoActionType<A,T1,T2> actionType)
    {
    	return _enableConditions.get(actionType);
    }

    @Override
	public <A extends FlexoAction<?,T1,T2>, T1 extends FlexoModelObject, T2 extends FlexoModelObject> FlexoActionVisibleCondition<? super A, T1, T2> getVisibleConditionFor(
    		FlexoActionType<A, T1, T2> actionType) {
    	return _visibleConditions.get(actionType);
    }
    
    @Override
	public  <A extends FlexoAction<?,T1,T2>, T1 extends FlexoModelObject, T2 extends FlexoModelObject>
     FlexoExceptionHandler<? super A> getExceptionHandlerFor(FlexoActionType<A,T1,T2> actionType)
     {
     	return _exceptionHandlers.get(actionType);
     }

    public  <A extends FlexoAction<?,T1,T2>, T1 extends FlexoModelObject, T2 extends FlexoModelObject>
    void registerFinalizerFor(FlexoActionType<A,T1,T2> actionType, FlexoActionFinalizer<? super A> finalizer)
    {
    	_finalizers.put(actionType,finalizer);
    }

    public <A extends FlexoAction<?,T1,T2>, T1 extends FlexoModelObject, T2 extends FlexoModelObject> 
    void registerInitializerFor(FlexoActionType<A,T1,T2> actionType, FlexoActionInitializer<? super A> initializer)
    {
    	_initializers.put(actionType,initializer);
    }

    public  <A extends FlexoAction<?,T1,T2>, T1 extends FlexoModelObject, T2 extends FlexoModelObject>
    void registerUndoFinalizerFor(FlexoActionType<A,T1,T2> actionType, FlexoActionUndoFinalizer<? super A> undoFinalizer)
    {
    	_undoFinalizers.put(actionType,undoFinalizer);
    }

    public  <A extends FlexoAction<?,T1,T2>, T1 extends FlexoModelObject, T2 extends FlexoModelObject>
    void registerUndoInitializerFor(FlexoActionType<A,T1,T2> actionType, FlexoActionUndoInitializer<? super A> undoInitializer)
    {
    	_undoInitializers.put(actionType,undoInitializer);
    }

    public  <A extends FlexoAction<?,T1,T2>, T1 extends FlexoModelObject, T2 extends FlexoModelObject>
    void registerRedoFinalizerFor(FlexoActionType<A,T1,T2> actionType, FlexoActionRedoFinalizer<? super A> redoFinalizer)
    {
    	_redoFinalizers.put(actionType,redoFinalizer);
    }

    public  <A extends FlexoAction<?,T1,T2>, T1 extends FlexoModelObject, T2 extends FlexoModelObject>
    void registerRedoInitializerFor(FlexoActionType<A,T1,T2> actionType, FlexoActionRedoInitializer<? super A> redoInitializer)
    {
    	_redoInitializers.put(actionType,redoInitializer);
    }

    public  <A extends FlexoAction<?,T1,T2>, T1 extends FlexoModelObject, T2 extends FlexoModelObject>
    void registerEnableConditionFor(FlexoActionType<A,T1,T2> actionType, FlexoActionEnableCondition<? super A,T1,T2> enableCondition)
    {
    	_enableConditions.put(actionType,enableCondition);
    }

    public  <A extends FlexoAction<?,T1,T2>, T1 extends FlexoModelObject, T2 extends FlexoModelObject>
    void registerVisibleConditionFor(FlexoActionType<A,T1,T2> actionType, FlexoActionVisibleCondition<? super A,T1,T2> visibleCondition)
    {
    	_visibleConditions.put(actionType,visibleCondition);
    }
    
    public  <A extends FlexoAction<?,T1,T2>, T1 extends FlexoModelObject, T2 extends FlexoModelObject>
    void registerExceptionHandlerFor(FlexoActionType<A,T1,T2> actionType,FlexoExceptionHandler<? super A> exceptionHandler)
    {
    	_exceptionHandlers.put(actionType,exceptionHandler);
    }

	@Override
	public boolean performResourceScanning() {
		return true;
	}

	@Override
	public FlexoProgressFactory getFlexoProgressFactory() {
		// Only interactive editor have a progress window
		return null;
	}

	@Override
	public void performPendingActions() {
	}

	@Override
	public void focusOn(FlexoModelObject object)
	{
		// Only interactive editor handle this
	}
	
	@Override
	public <A extends FlexoAction<?, T1, T2>, T1 extends FlexoModelObject, T2 extends FlexoModelObject> Icon getDisabledIconFor(
			FlexoActionType<A, T1, T2> actionType) 
	{
		// Only interactive editor handle this
		return null;
	}
	
	@Override
	public <A extends FlexoAction<?, T1, T2>, T1 extends FlexoModelObject, T2 extends FlexoModelObject> Icon getEnabledIconFor(
			FlexoActionType<A, T1, T2> actionType) 
	{
		// Only interactive editor handle this
		return null;
	}
}
