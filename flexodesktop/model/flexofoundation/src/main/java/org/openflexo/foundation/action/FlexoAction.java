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
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Icon;

import org.openflexo.foundation.FlexoEditor;
import org.openflexo.foundation.FlexoException;
import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.utils.FlexoProgress;
import org.openflexo.foundation.utils.FlexoProgressFactory;
import org.openflexo.kvc.KeyValueCoding;
import org.openflexo.logging.FlexoLogger;
import org.openflexo.xmlcode.KeyValueCoder;
import org.openflexo.xmlcode.KeyValueDecoder;

/**
 * Abstract representation of an action on Flexo model
 * (model edition primitive) 
 * 
 * @author sguerin
 */
public abstract class FlexoAction<A extends FlexoAction<?,T1,T2>, T1 extends FlexoModelObject,T2 extends FlexoModelObject> 
extends AbstractAction implements KeyValueCoding {

    private static final Logger logger = FlexoLogger.getLogger(FlexoAction.class.getPackage().getName());

    private FlexoActionType<A,T1,T2> _actionType;
    private T1 _focusedObject;
    private Vector<T2> _globalSelection;
 //   private FlexoActionInitializer<? super A> _initializer;
 //   private FlexoActionFinalizer<? super A> _finalizer;
 //   private FlexoExceptionHandler<? super A> _exceptionHandler;
    private Object _context;
    private Object _invoker;
    private FlexoProgress _flexoProgress;
    private FlexoEditor _editor;
    private ExecutionContext _executionContext;
    
	public void delete() 
	{
		if (_executionContext != null) {
			_executionContext.delete();
		}
		_editor = null;
		_flexoProgress = null;
		_invoker = null;
		_context = null;
		if (_globalSelection != null) {
			_globalSelection.clear();
		}
		_globalSelection = null;
		_focusedObject = null;
		_actionType = null;
	}


    public enum ExecutionStatus
    {
    	NEVER_EXECUTED,
    	EXECUTING_INITIALIZER,
       	EXECUTING_CORE,
       	EXECUTING_FINALIZER,
       	HAS_SUCCESSFULLY_EXECUTED,
      	FAILED_EXECUTION,
       	EXECUTING_UNDO_INITIALIZER,
       	EXECUTING_UNDO_CORE,
       	EXECUTING_UNDO_FINALIZER,
       	HAS_SUCCESSFULLY_UNDONE,
      	FAILED_UNDO_EXECUTION,
       	EXECUTING_REDO_INITIALIZER,
       	EXECUTING_REDO_CORE,
       	EXECUTING_REDO_FINALIZER,
       	HAS_SUCCESSFULLY_REDONE,
      	FAILED_REDO_EXECUTION;
      	
        public boolean isExecuting ()
        {
        	return ((this == EXECUTING_INITIALIZER) 
        			|| (this == EXECUTING_CORE)
           			|| (this == EXECUTING_FINALIZER));
        }

        public boolean isExecutingUndo ()
        {
        	return ((this == EXECUTING_UNDO_INITIALIZER) 
        			|| (this == EXECUTING_UNDO_CORE)
           			|| (this == EXECUTING_UNDO_FINALIZER));
        }

        public boolean isExecutingRedo ()
        {
        	return ((this == EXECUTING_REDO_INITIALIZER) 
        			|| (this == EXECUTING_REDO_CORE)
           			|| (this == EXECUTING_REDO_FINALIZER));
        }

        public boolean hasActionExecutionSucceeded ()
        {
        	if (this == NEVER_EXECUTED) {
				return false;
			} else if (isExecuting()) {
				return false;
			} else if (this == FAILED_EXECUTION) {
				return false;
			} else {
				return true;
			}
        }

        public boolean hasActionUndoExecutionSucceeded ()
        {
        	if (!hasActionExecutionSucceeded()) {
				return false;
			} else if (isExecutingUndo()) {
				return false;
			} else if (this == FAILED_UNDO_EXECUTION) {
				return false;
			} else {
				return true;
			}
        }

        public boolean hasActionRedoExecutionSucceeded ()
        {
           	if (!hasActionExecutionSucceeded()) {
				return false;
			} else if (!hasActionUndoExecutionSucceeded()) {
				return false;
			} else if (isExecutingRedo()) {
				return false;
			} else if (this == FAILED_REDO_EXECUTION) {
				return false;
			} else {
				return true;
			}
       }

   }
    
    /*private boolean _isExecutingInitializer = false;
    private boolean _isExecutingFinalizer = false;
    private boolean _isExecutingCore = false;
    private boolean _isExecuting = false;*/
    
    //private boolean actionExecutionSucceeded = false;
    
    protected ExecutionStatus executionStatus = ExecutionStatus.NEVER_EXECUTED;
    private FlexoException thrownException = null;
       

    private final Vector<FlexoAction> _embbededActionsExecutedDuringInitializer;
    private final Vector<FlexoAction> _embbededActionsExecutedDuringFinalizer;
    private final Vector<FlexoAction> _embbededActionsExecutedDuringCore;

    private boolean _logActionTime = true;
    
    public FlexoAction (FlexoActionType<A,T1,T2> actionType, T1 focusedObject, Vector<T2> globalSelection, FlexoEditor editor)
    {
        super();
        _editor = editor;
        _actionType = actionType;
        _focusedObject = focusedObject;
        if (globalSelection!=null) {
        	_globalSelection = new Vector<T2>();
        	for (T2 o : globalSelection) {
        		_globalSelection.add(o);
         	}
        } else {
			_globalSelection = null;
		}
        _embbededActionsExecutedDuringInitializer = new Vector<FlexoAction>();
        _embbededActionsExecutedDuringFinalizer = new Vector<FlexoAction>();
        _embbededActionsExecutedDuringCore = new Vector<FlexoAction>();
         setEnabled(actionType.isEnabled(focusedObject,globalSelection,editor));
         update(editor);
    }

    public void update(FlexoEditor editor)
    {
        if (getLocalizedName() != null) {
			putValue(Action.NAME, getLocalizedName());
		}
        if (getEnabledIcon(editor) != null) {
			putValue(Action.SMALL_ICON, getEnabledIcon(editor));
		}                
    }
    
    public FlexoActionType<A,T1,T2> getActionType() 
    {
        return _actionType;
    }

    public String getLocalizedName ()
    {
        if (getActionType() != null) {
            return getActionType().getLocalizedName();
        }
        return null;
    }

    public String getLocalizedDescription ()
    {
        if (getActionType() != null) {
            return getActionType().getLocalizedDescription();
        }
        return null;
    }
 
    public Icon getEnabledIcon(FlexoEditor editor) 
    {
        if (getActionType() != null && editor != null) {
        	return editor.getEnabledIconFor(getActionType());
        }
        return null;
     }

    public Icon getDisabledIcon(FlexoEditor editor) 
    {
        if (getActionType() != null && editor != null) {
        	return editor.getDisabledIconFor(getActionType());
        }
        return null;
     }

    @Override
	public final void actionPerformed(ActionEvent e) 
    {
        try {
            // In this case, we need to perform this verification so that disabled action aren't executed.
            if (getActionType().isEnabled(getFocusedObject(), getGlobalSelection(), _editor)) {
				;
			}
                doAction(e);
        } catch (FlexoException exception) {
            // Warns about the exception
            logger.warning ("Exception raised: "+exception.getClass().getName()+" : "+exception.getMessage()+". See console for details.");
            exception.printStackTrace();
        }
    }

    /**
     * Sets focused object
     */
    public void setFocusedObject(T1 focusedObject) 
    {
        _focusedObject = focusedObject;
    }

    /**
     * Return focused object, according to the one used in action constructor
     * (see FlexoAction factory). If no focused object was defined, and global
     * selection is not empty, return first object in the selection
     * @return a FlexoModelObject instance, representing focused object
     */
    public T1 getFocusedObject() 
    {
        if (_focusedObject != null) {
			return _focusedObject;
		}
        if ((_globalSelection != null)
                && (_globalSelection.size() > 0)) {
            return (T1)_globalSelection.firstElement();
        }
        return null;
    }
    
    public Vector<T2> getGlobalSelection() 
    {
        return _globalSelection;
    }
    
    public A doAction ()
    {
        try {
            return doAction(null);
        } catch (FlexoException exception) {
        	thrownException = exception;
        	hideFlexoProgress();
            // Warns about the exception
            logger.warning ("Exception raised: "+exception.getClass().getName()+" : "+exception.getMessage()+". See console for details.");
            exception.printStackTrace();
            return (A)this;
        }
    }
    
	public ExecutionStatus getExecutionStatus()
	{
		return executionStatus;
	}

	public boolean hasActionExecutionSucceeded ()
    {
    	return getExecutionStatus().hasActionExecutionSucceeded();
    }
    
    public FlexoException getThrownException() 
    {
		return thrownException;
	}

    private void notifyEmbeddedExecution(FlexoAction embeddedAction)
    {
    	if (getExecutionStatus() == ExecutionStatus.EXECUTING_CORE) {
    		_embbededActionsExecutedDuringCore.add(embeddedAction);
    	}
    	else if (getExecutionStatus() == ExecutionStatus.EXECUTING_INITIALIZER) {
    		_embbededActionsExecutedDuringInitializer.add(embeddedAction);
    	}
    	else if (getExecutionStatus() == ExecutionStatus.EXECUTING_FINALIZER) {
    		_embbededActionsExecutedDuringFinalizer.add(embeddedAction);
    	}
    	else {
    		logger.warning("FlexoAction "+embeddedAction+" exectuted outside scope of owner action execution owner="+this);
    	}
    }
    
    public A doAction (ActionEvent e) throws FlexoException
    {
    	long start=0, end=0;
    	if (_logActionTime) {
			start = System.currentTimeMillis();
		}
        // First notify owner action
    	if (getOwnerAction() != null) {
    		getOwnerAction().notifyEmbeddedExecution(this);
    	}
    	
    	boolean confirmDoAction = true;
     	if (getInitializer() != null) {
    		executionStatus = ExecutionStatus.EXECUTING_INITIALIZER;
    		confirmDoAction = getInitializer().run(e, (A)this);
    	}
     	
     	String actionName = getLocalizedName();
    	if (confirmDoAction) {
    		if (getEditor() != null) {
    			getEditor().actionWillBePerformed(this);
    		}
    		try {
    			if ((getEditor() != null) && (getEditor().getProject()!=null)) {
    				getEditor().getProject().clearRecentlyCreatedObjects();
    			}
    	   		executionStatus = ExecutionStatus.EXECUTING_CORE;
    			doAction(getContext());
    			if ((getEditor() != null) && (getEditor().getProject()!=null)) {
    				getEditor().getProject().notifyRecentlyCreatedObjects();
    			}
       	   		executionStatus = ExecutionStatus.HAS_SUCCESSFULLY_EXECUTED;
       	   		if (getEditor() != null) {
    				getEditor().actionHasBeenPerformed(this,true); // Action succeeded
    			}
    		}
    		catch (FlexoException exception) {
    	   		executionStatus = ExecutionStatus.FAILED_EXECUTION;
       			if (getEditor() != null) {
    				getEditor().actionHasBeenPerformed(this,false); // Action failed
    			}
    			hideFlexoProgress();
    			if (getExceptionHandler() != null) {
    				if (getExceptionHandler().handleException(exception, (A)this)) {
    					// The exception has been handled, we may still have to execute finalizer, if any
    					executionStatus = ExecutionStatus.HAS_SUCCESSFULLY_EXECUTED;
    				}
    				else {
    					thrownException = exception;
    					throw exception;
    				}
    			}
    			else {
    				throw exception;
    			}
    		}

    		if ((getFinalizer() != null) && (executionStatus == ExecutionStatus.HAS_SUCCESSFULLY_EXECUTED)) {
    	   		executionStatus = ExecutionStatus.EXECUTING_FINALIZER;
    	   		executionStatus = (getFinalizer().run(e, (A)this)?ExecutionStatus.HAS_SUCCESSFULLY_EXECUTED:ExecutionStatus.FAILED_EXECUTION);
    		}
    	}
    	hideFlexoProgress();
    	if (_logActionTime) {
    		end = System.currentTimeMillis();
	        if (logger.isLoggable(Level.INFO)) {
				logger.info("Action "+actionName+" took "+(end-start)+"ms to complete");
			}
        }
    	return (A)this;
    }

    public void hideFlexoProgress()
    {
    	if (getFlexoProgress() != null) {
    		getFlexoProgress().hideWindow();
    		setFlexoProgress(null);
        }
    }
    
    protected abstract void doAction (Object context) throws FlexoException;

    public FlexoActionFinalizer<? super A> getFinalizer() 
    {
        return getActionType().getFinalizerForEditor(getEditor());
    }

    public FlexoActionInitializer<? super A> getInitializer() 
    {
        return getActionType().getInitializerForEditor(getEditor());
    }

    public FlexoExceptionHandler<? super A> getExceptionHandler() 
    {
        return getActionType().getExceptionHandlerForEditor(getEditor());
    }

    public Object getContext()
    {
        return _context;
    }

    public void setContext(Object context) 
    {
        _context = context;
    }

    public Object getInvoker() {
        return _invoker;
    }

    public void setInvoker(Object invoker) {
        _invoker = invoker;
    }

    public FlexoProgressFactory getFlexoProgressFactory()
    {
    	if(getEditor()!=null) {
			return getEditor().getFlexoProgressFactory();
		}
        return null;
    }

    public FlexoProgress getFlexoProgress() 
    {
        return _flexoProgress;
    }

    public void setFlexoProgress(FlexoProgress flexoProgress) 
    {
        _flexoProgress = flexoProgress;
    }

    public FlexoProgress makeFlexoProgress(String title, int steps)
    {
        if (getFlexoProgressFactory() != null) {
            setFlexoProgress(getFlexoProgressFactory().makeFlexoProgress(title,steps));
            return getFlexoProgress();
        }
        return null;
    }

    public void setProgress(String stepName)
    {
        if (getFlexoProgress() != null) {
			getFlexoProgress().setProgress(stepName);
		}
    }

    public void resetSecondaryProgress(int steps)
    {
        if (getFlexoProgress() != null) {
			getFlexoProgress().resetSecondaryProgress(steps);
		}  
    }

    public void setSecondaryProgress(String stepName)
    {
        if (getFlexoProgress() != null) {
			getFlexoProgress().setSecondaryProgress(stepName);
		}         
    }
    
    public Vector<FlexoModelObject> getGlobalSelectionAndFocusedObject() {
        return getGlobalSelectionAndFocusedObject(getFocusedObject(), getGlobalSelection());
    }

    public static Vector<FlexoModelObject> getGlobalSelectionAndFocusedObject(FlexoModelObject focusedObject, Vector<? extends FlexoModelObject> globalSelection) {
        Vector<FlexoModelObject> v = globalSelection!=null?new Vector<FlexoModelObject>(globalSelection.size()+1):new Vector<FlexoModelObject>(1);
        if (globalSelection!=null) {
			v.addAll(globalSelection);
		}
        if ((focusedObject!=null) && !v.contains(focusedObject)) {
			v.add(focusedObject);
		}
        return v;
    }

	public FlexoEditor getEditor() 
	{
		return _editor;
	}
	
	private FlexoAction ownerAction;

	public FlexoAction getOwnerAction() 
	{
		return ownerAction;
	}

	protected void setOwnerAction(FlexoAction ownerAction) 
	{
		this.ownerAction = ownerAction;
	}
	
	public boolean isEmbedded()
	{
		return (getOwnerAction() != null);
	}
	
	public Vector<FlexoAction> getEmbbededActionsExecutedDuringCore() 
	{
		return _embbededActionsExecutedDuringCore;
	}

	public Vector<FlexoAction> getEmbbededActionsExecutedDuringFinalizer() 
	{
		return _embbededActionsExecutedDuringFinalizer;
	}

	public Vector<FlexoAction> getEmbbededActionsExecutedDuringInitializer() 
	{
		return _embbededActionsExecutedDuringInitializer;
	}


	public String toSimpleString()
	{
		return getClass().getSimpleName() + "@" + Integer.toHexString(hashCode());
	}

	@Override
	public String toString()
	{
		boolean isFirst = true;
		StringBuffer returned = new StringBuffer();
		returned.append(getClass().getSimpleName() + "@" + Integer.toHexString(hashCode())+"[");
		for (FlexoAction a : _embbededActionsExecutedDuringInitializer) {
			returned.append((isFirst?"":",")+"#"+a.toSimpleString());
			isFirst = false;
		}
		for (FlexoAction a : _embbededActionsExecutedDuringCore) {
			returned.append((isFirst?"":",")+a.toSimpleString());
			isFirst = false;
		}
		for (FlexoAction a : _embbededActionsExecutedDuringFinalizer) {
			returned.append((isFirst?"":",")+"@"+a.toSimpleString());
			isFirst = false;
		}
		if (getExecutionContext() != null) {
			for (String key : getExecutionContext().getObjectsCreatedWhileExecutingAction().keySet()) {
				FlexoModelObject o = getExecutionContext().getObjectsCreatedWhileExecutingAction().get(key);
				returned.append((isFirst?"":" ")+"CREATED:"+key+"/"+o.getClass().getSimpleName()+"/"+o.getFlexoID());
				isFirst = false;
			}
			for (String key : getExecutionContext().getObjectsDeletedWhileExecutingAction().keySet()) {
				FlexoModelObject o = getExecutionContext().getObjectsDeletedWhileExecutingAction().get(key);
				returned.append((isFirst?"":" ")+"DELETED:"+key+"/"+o.getClass().getSimpleName()+"/"+o.getFlexoID());
				isFirst = false;
			}
		}
		returned.append("]");
		return returned.toString();
	}

	public ExecutionContext getExecutionContext() 
	{
		// If not supplied, create default ExecutionContext
		if (_executionContext == null) {
			_executionContext = createDefaultExecutionContext();
		}
		return _executionContext;
	}

	public void setExecutionContext(ExecutionContext executionContext) 
	{
		_executionContext = executionContext;
	}
	
	protected ExecutionContext createDefaultExecutionContext() 
	{
		return new ExecutionContext(this);
	}

	public void objectCreated(String key, FlexoModelObject object)
	{
		getExecutionContext().objectCreated(key,object);
	}

	public void objectDeleted(String key, FlexoModelObject object)
	{
		if (getExecutionContext() != null) {
			getExecutionContext().objectDeleted(key,object);
		}
	}
	
	/**
	 * Hook that might be overriden in sub-classes while implementing dynamic reference replacement scheme
	 * 
	 * @param propertyKey
	 * @param newValue
	 * @param oldValue TODO
	 * @param originalValue TODO
	 */
	protected void replacedSinglePropertyValue (String propertyKey, Object newValue, Object oldValue, Object originalValue)
	{		
	}
	
	/**
	 * Hook that might be overriden in sub-classes while implementing dynamic reference replacement scheme
	 * 
	 * @param propertyKey
	 * @param index
	 * @param newValue
	 * @param oldValue TODO
	 * @param originalValue TODO
	 */
	protected void replacedVectorPropertyValue (String propertyKey, int index, Object newValue, Object oldValue, Object originalValue)
	{	
	}
	
	public void __setLogActionTime(boolean log) {
		this._logActionTime = log;
	}
	


    // ==============================================================
    // ============= Key/Value coding implementation ================
    // ==============================================================
	
	@Override
	public String valueForKey(String key)
	{
	    if (_editor!=null) {
			return KeyValueDecoder.valueForKey(this, key, _editor.getProject().getStringEncoder());
		} else {
			return KeyValueDecoder.valueForKey(this, key);
		}
	}

	@Override
	public void setValueForKey(String valueAsString, String key)
	{
	    if (_editor!=null) {
			KeyValueCoder.setValueForKey(this, valueAsString, key,_editor.getProject().getStringEncoder());
		} else {
			KeyValueCoder.setValueForKey(this, valueAsString, key);
		}
	}

	@Override
	public boolean booleanValueForKey(String key)
	{
		return KeyValueDecoder.booleanValueForKey(this, key);
	}

	@Override
	public byte byteValueForKey(String key)
	{
		return KeyValueDecoder.byteValueForKey(this, key);
	}

	@Override
	public char characterForKey(String key)
	{
		return KeyValueDecoder.characterValueForKey(this, key);
	}

	@Override
	public double doubleValueForKey(String key)
	{
		return KeyValueDecoder.doubleValueForKey(this, key);
	}

	@Override
	public float floatValueForKey(String key)
	{
		return KeyValueDecoder.floatValueForKey(this, key);
	}

	@Override
	public int integerValueForKey(String key)
	{
		return KeyValueDecoder.integerValueForKey(this, key);
	}

	@Override
	public long longValueForKey(String key)
	{
		return KeyValueDecoder.longValueForKey(this, key);
	}

	@Override
	public short shortValueForKey(String key)
	{
		return KeyValueDecoder.shortValueForKey(this, key);
	}

	@Override
	public void setBooleanValueForKey(boolean value, String key)
	{
		KeyValueCoder.setBooleanValueForKey(this, value, key);
	}

	@Override
	public void setByteValueForKey(byte value, String key)
	{
		KeyValueCoder.setByteValueForKey(this, value, key);
	}

	@Override
	public void setCharacterForKey(char value, String key)
	{
		KeyValueCoder.setCharacterValueForKey(this, value, key);
	}

	@Override
	public void setDoubleValueForKey(double value, String key)
	{
		KeyValueCoder.setDoubleValueForKey(this, value, key);
	}

	@Override
	public void setFloatValueForKey(float value, String key)
	{
		KeyValueCoder.setFloatValueForKey(this, value, key);
	}

	@Override
	public void setIntegerValueForKey(int value, String key)
	{
		KeyValueCoder.setIntegerValueForKey(this, value, key);
	}

	@Override
	public void setLongValueForKey(long value, String key)
	{
		KeyValueCoder.setLongValueForKey(this, value, key);
	}

	@Override
	public void setShortValueForKey(short value, String key)
	{
		KeyValueCoder.setShortValueForKey(this, value, key);
	}

	@Override
	public Object objectForKey(String key)
	{
		return KeyValueDecoder.objectForKey(this, key);
	}

	@Override
	public void setObjectForKey(Object value, String key)
	{
		KeyValueCoder.setObjectForKey(this, value, key);
	}

	// Retrieving type

	@Override
	public Class getTypeForKey(String key)
	{
		return KeyValueDecoder.getTypeForKey(this, key);
	}

	@Override
	public boolean isSingleProperty(String key)
	{
		return KeyValueDecoder.isSingleProperty(this, key);
	}

	@Override
	public boolean isArrayProperty(String key)
	{
		return KeyValueDecoder.isArrayProperty(this, key);
	}

	@Override
	public boolean isVectorProperty(String key)
	{
		return KeyValueDecoder.isVectorProperty(this, key);
	}

	@Override
	public boolean isHashtableProperty(String key)
	{
		return KeyValueDecoder.isHashtableProperty(this, key);
	}

}