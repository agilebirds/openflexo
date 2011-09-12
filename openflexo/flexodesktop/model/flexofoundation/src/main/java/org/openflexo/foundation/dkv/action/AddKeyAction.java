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
package org.openflexo.foundation.dkv.action;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openflexo.foundation.FlexoEditor;
import org.openflexo.foundation.FlexoException;
import org.openflexo.foundation.action.FlexoActionType;
import org.openflexo.foundation.action.FlexoUndoableAction;
import org.openflexo.foundation.dkv.DKVObject;
import org.openflexo.foundation.dkv.Domain;
import org.openflexo.foundation.dkv.Key;
import org.openflexo.foundation.dkv.Language;
import org.openflexo.logging.FlexoLogger;

/**
 * @author gpolet
 * 
 */
public class AddKeyAction extends FlexoUndoableAction<AddKeyAction,DKVObject,DKVObject>
{
    protected static final Logger logger = FlexoLogger.getLogger(AddKeyAction.class.getPackage().getName());

    public static FlexoActionType<AddKeyAction,DKVObject,DKVObject> actionType = new FlexoActionType<AddKeyAction,DKVObject,DKVObject>("add_key", FlexoActionType.newMenu, FlexoActionType.defaultGroup,
            FlexoActionType.ADD_ACTION_TYPE) {

        /**
         * Factory method
         */
        @Override
		public AddKeyAction makeNewAction(DKVObject focusedObject, Vector globalSelection, FlexoEditor editor)
        {
            return new AddKeyAction(focusedObject, globalSelection, editor);
        }

        @Override
		protected boolean isVisibleForSelection(DKVObject object, Vector globalSelection)
        {
            return true;
        }

        @Override
		protected boolean isEnabledForSelection(DKVObject object, Vector globalSelection)
        {
            return object instanceof Domain || object instanceof Domain.KeyList;
        }
        
        private String[] persistentProperties = {"keyName","keyDescription","domain","values"};
        
        @Override
		protected String[] getPersistentProperties(){
        	return persistentProperties;
        }

    };

    private Key newKey;
    private Domain _domain;
    private String _keyName;
    private String _keyDescription;
    private Hashtable<Language,String> _valuesForLanguage;
    
    /**
     * @param actionType
     * @param focusedObject
     * @param globalSelection
     */
    public AddKeyAction(DKVObject focusedObject, Vector globalSelection, FlexoEditor editor)
    {
        super(actionType, focusedObject, globalSelection, editor);
        _valuesForLanguage = new Hashtable<Language,String>();
    }

    public void setValueForLanguage (String value, Language language)
    {
        _valuesForLanguage.put(language, value);
    }
    
    public Hashtable values(){
    	return _valuesForLanguage;
    }
    
    public void setValues(Hashtable t){
    	_valuesForLanguage = t;
    }
    /**
     * Overrides doAction
     * 
     * @see org.openflexo.foundation.action.FlexoAction#doAction(java.lang.Object)
     */
    @Override
	protected void doAction(Object context) throws FlexoException
    {
        newKey = getDomain().addKeyNamed(getKeyName());
        Enumeration en = getDomain().getDkvModel().getLanguageList().getLanguages().elements();
        Language lg = null;
        while (en.hasMoreElements()) {
            lg = (Language) en.nextElement();
            getDomain().getValue(newKey, lg).setValue(_valuesForLanguage.get(lg));
        }
        newKey.setDescription(getKeyDescription());
        objectCreated("NEW_KEY", newKey);
        if (logger.isLoggable(Level.INFO))
            logger.info("Key added in DKV");
    }

    @Override
	protected void redoAction(Object context) throws FlexoException {
		doAction(context);
	}

	@Override
	protected void undoAction(Object context) throws FlexoException {
		getNewKey().delete();
	}
	
    public Key getNewKey()
    {
        return newKey;
    }

    public Domain getDomain() 
    {
        return _domain;
    }

    public void setDomain(Domain domain) 
    {
        _domain = domain;
    }

    public String getKeyDescription() 
    {
        return _keyDescription;
    }

    public void setKeyDescription(String keyDescription) 
    {
        _keyDescription = keyDescription;
    }

    public String getKeyName() 
    {
        return _keyName;
    }

    public void setKeyName(String keyName) 
    {
        _keyName = keyName;
    }

    
	
}
