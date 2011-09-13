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
package org.openflexo.foundation.ws;

import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openflexo.localization.FlexoLocalization;
import org.openflexo.logging.FlexoLogger;

public class InternalWSFolder extends WSFolder {

    private static final Logger logger = FlexoLogger.getLogger(InternalWSFolder.class.getPackage()
            .getName());
    /**
     * @param dl
     */
    public InternalWSFolder(FlexoWSLibrary dl)
    {
        super(dl);
    }

    public Vector getInternalWSServices() {
        return getWSLibrary().getInternalWSServices();
    }
    @Override
	public Vector getWSServices(){
    		return getInternalWSServices();
    }
    
    /**
     * Overrides getFullyQualifiedName
     * @see org.openflexo.foundation.FlexoModelObject#getFullyQualifiedName()
     */
    @Override
	public String getFullyQualifiedName()
    {
        return "INTERNAL_WS_FOLDER";
    }
    
    @Override
	public String getLocalizedDescription() {
		return FlexoLocalization.localizedForKey("ws_internal_folder_description");
    }
    
    @Override
	public String getName() {
    	   
    	return "ws_internal_ws_folder";
    }
    
    @Override
	public String getLocalizedName(){
    		return FlexoLocalization.localizedForKey(getName());
    }
    
    @Override
	public void delete(){
    		if (logger.isLoggable(Level.INFO)) logger.info("implements delete on InternalWSFolder");
    }
    /**
     * Overrides getClassNameKey
     * @see org.openflexo.foundation.FlexoModelObject#getClassNameKey()
     */
    @Override
	public String getClassNameKey()
    {
        return getName();
    }
    
}