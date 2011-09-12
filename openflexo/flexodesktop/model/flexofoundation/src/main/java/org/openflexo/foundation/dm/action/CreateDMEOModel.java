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
package org.openflexo.foundation.dm.action;

import java.io.File;
import java.io.IOException;
import java.util.Vector;
import java.util.logging.Logger;

import org.openflexo.foundation.FlexoEditor;
import org.openflexo.foundation.FlexoException;
import org.openflexo.foundation.IOFlexoException;
import org.openflexo.foundation.action.FlexoAction;
import org.openflexo.foundation.action.FlexoActionType;
import org.openflexo.foundation.dm.DMObject;
import org.openflexo.foundation.dm.eo.DMEOAdaptorType;
import org.openflexo.foundation.dm.eo.DMEOModel;
import org.openflexo.foundation.dm.eo.DMEORepository;


public class CreateDMEOModel extends FlexoAction<CreateDMEOModel, DMEORepository, DMObject> 
{

    private static final Logger logger = Logger.getLogger(CreateDMEOModel.class.getPackage().getName());

    public static FlexoActionType<CreateDMEOModel, DMEORepository, DMObject> actionType = new FlexoActionType<CreateDMEOModel, DMEORepository, DMObject>("creates_new_eo_model",FlexoActionType.newMenu,FlexoActionType.defaultGroup,FlexoActionType.ADD_ACTION_TYPE) {

        /**
         * Factory method
         */
        @Override
		public CreateDMEOModel makeNewAction(DMEORepository focusedObject, Vector<DMObject> globalSelection, FlexoEditor editor) 
        {
            return new CreateDMEOModel(focusedObject, globalSelection, editor);
        }

        @Override
		protected boolean isVisibleForSelection(DMEORepository object, Vector<DMObject> globalSelection) 
        {
            return true;
        }

        @Override
		protected boolean isEnabledForSelection(DMEORepository object, Vector<DMObject> globalSelection) 
        {
            return object != null && !object.isReadOnly();
         }
                
    };
    
    CreateDMEOModel (DMEORepository focusedObject, Vector<DMObject> globalSelection, FlexoEditor editor)
    {
        super(actionType, focusedObject, globalSelection, editor);
    }

    private File _eoModelFile;
    private DMEOAdaptorType _adaptorType;
    private String _userName;
    private String _password;
    private String _databaseServerURL;
    private String _plugin;
    private String _driver;
        
    private DMEOModel _newDMEOModel;
    private DMEORepository _repository;

    @Override
	protected void doAction(Object context) throws FlexoException 
    {
      logger.info ("CreateDMEOModel"); 
      if (getRepository() != null) {
    	  try {
          _newDMEOModel = getRepository().createsNewEOModel(getEOModelFile(),
                      getAdaptorType(), 
                      getUserName(), 
                      getPassword(),
                      getDatabaseServerURL(), 
                      getPlugin(), 
                      getDriver());
    	  } catch (IOException e) {
    		  throw new IOFlexoException(e);
    	  }
      }
    }
    
    public DMEORepository getRepository() 
    {
        if (_repository == null) {
            if (getFocusedObject() != null) {
                _repository = getFocusedObject();
             }           
        }
        return _repository;
    }

    public DMEOModel getNewDMEOModel() 
    {
        return _newDMEOModel;
    }

    public DMEOAdaptorType getAdaptorType() 
    {
        return _adaptorType;
    }

    public void setAdaptorType(DMEOAdaptorType adaptorType) 
    {
        _adaptorType = adaptorType;
    }

    public String getDatabaseServerURL() 
    {
        return _databaseServerURL;
    }

    public void setDatabaseServerURL(String databaseServerURL)
    {
        _databaseServerURL = databaseServerURL;
    }

    public String getDriver() 
    {
        return _driver;
    }

    public void setDriver(String driver)
    {
        _driver = driver;
    }

    public File getEOModelFile() 
    {
        return _eoModelFile;
    }

    public void setEOModelFile(File eoModelFile) 
    {
        _eoModelFile = eoModelFile;
    }

    public String getPassword() 
    {
        return _password;
    }

    public void setPassword(String password) {
        _password = password;
    }

    public String getPlugin()
    {
        return _plugin;
    }

    public void setPlugin(String plugin)
    {
        _plugin = plugin;
    }

    public String getUserName() 
    {
        return _userName;
    }

    public void setUserName(String userName) 
    {
        _userName = userName;
    }
   
}
