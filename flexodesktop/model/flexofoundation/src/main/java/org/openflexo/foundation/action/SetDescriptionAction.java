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

import java.util.Observer;
import java.util.Vector;
import java.util.logging.Logger;

import org.openflexo.foundation.DocType;
import org.openflexo.foundation.FlexoEditor;
import org.openflexo.foundation.FlexoException;
import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.xmlcode.AccessorInvocationException;


/**
 * @author gpolet
 *
 */
public class SetDescriptionAction extends FlexoUndoableAction<SetDescriptionAction,FlexoModelObject,FlexoModelObject>
{
    
    private static final Logger logger = Logger.getLogger(SetDescriptionAction.class.getPackage().getName());

    public static class SetPropertyActionType extends FlexoActionType<SetDescriptionAction, FlexoModelObject, FlexoModelObject>
    {
    	private Vector<Observer> _observers;
    	
    	protected SetPropertyActionType (String actionName)
    	{
    		super(actionName);
    	}

    	@Override
    	protected boolean isEnabledForSelection(FlexoModelObject object, Vector<FlexoModelObject> globalSelection)
    	{
    		return true;
    	}

    	@Override
    	protected boolean isVisibleForSelection(FlexoModelObject object, Vector<FlexoModelObject> globalSelection)
    	{
    		return false;
    	}

    	@Override
    	public SetDescriptionAction makeNewAction(FlexoModelObject focusedObject, Vector<FlexoModelObject> globalSelection, FlexoEditor editor)
    	{
    		return new SetDescriptionAction(focusedObject,globalSelection,editor);
    	}

    	/**
    	 * Overrides getPersistentProperties
    	 * @see org.openflexo.foundation.action.FlexoActionType#getPersistentProperties()
    	 */
    	@Override
    	protected String[] getPersistentProperties()
    	{
    		return new String[]{"docType","value"};
    	}
           
    }
    
    public static final SetPropertyActionType actionType  = new SetPropertyActionType("set_description");

    private DocType docType;
    
    private String value;
    
    private String previousValue;
    
    private String localizedPropertyName;
    
    /**
     * @param actionType
     * @param focusedObject
     * @param globalSelection
     * @param editor
     */
    protected SetDescriptionAction(FlexoModelObject focusedObject, Vector<FlexoModelObject> globalSelection, FlexoEditor editor)
    {
        super(actionType, focusedObject, globalSelection, editor);
    }

    /**
     * Overrides redoAction
     * @see org.openflexo.foundation.action.FlexoUndoableAction#redoAction(java.lang.Object)
     */
    @Override
    protected void redoAction(Object context) throws FlexoException
    {
        doAction(context);
    }

    /**
     * Overrides undoAction
     * @see org.openflexo.foundation.action.FlexoUndoableAction#undoAction(java.lang.Object)
     */
    @Override
    protected void undoAction(Object context) throws FlexoException
    {
    	logger.info("Undo: set description from "+value+" to "+previousValue+" again");
        getFocusedObject().setSpecificDescriptionsForKey(previousValue, getDocType().getName());
    }

    /**
     * Overrides doAction
     * @see org.openflexo.foundation.action.FlexoAction#doAction(java.lang.Object)
     */
    @Override
    protected void doAction(Object context) throws FlexoException
    {
    	previousValue = getFocusedObject().getSpecificDescriptionForKey(getDocType().getName());
    	try {
    		getFocusedObject().setSpecificDescriptionsForKey(getValue(), getDocType().getName());
    	}
    	catch (AccessorInvocationException exception) {
    		if (exception.getCause() instanceof FlexoException)
    			throw (FlexoException)exception.getCause();
       		logger.warning("Unexpected exception: see log for details");
       		exception.printStackTrace();
       		if (exception.getCause() instanceof Exception)
       			throw new UnexpectedException((Exception) exception.getCause());
    	}
    }
    
    public DocType getDocType()
    {
        return docType;
    }

    public void setDocType(DocType key)
    {
        this.docType = key;
    }

    public String getValue()
    {
        return value;
    }

    public void setValue(String value)
    {
        this.value = value;
    }

    public static SetDescriptionAction makeAction(FlexoModelObject object, DocType key, String value, FlexoEditor editor)
    {
    	SetDescriptionAction returned = actionType.makeNewAction(object, null, editor);
    	returned.setDocType(key);
       	returned.setValue(value);
       	return returned;
    }

    public String getLocalizedPropertyName()
    {
        return localizedPropertyName;
    }

    public void setLocalizedPropertyName(String localizedPropertyName)
    {
        this.localizedPropertyName = localizedPropertyName;
    }

	public Object getPreviousValue() {
		return previousValue;
	}

}
