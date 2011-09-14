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

import javax.swing.tree.TreeNode;

import org.openflexo.foundation.ObjectDeleted;
import org.openflexo.foundation.dm.WSDLRepository;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.logging.FlexoLogger;

public class WSRepositoryFolder extends WSObject {

	 private static final Logger logger = FlexoLogger.getLogger(WSRepositoryFolder.class.getPackage()
	            .getName());
	private WSService parentGroup;
	
    /**
     * @param dl
     */
    public WSRepositoryFolder(WSService group)
    {
        super(group.getWSLibrary());
        parentGroup=group;
    }

    public Vector<WSRepository> getWSRepositories() {
        return parentGroup.getWSRepositories();
    }
    
    public WSService getWSGroup(){
    		return parentGroup;
    }
    
    
    /**
     * Overrides getFullyQualifiedName
     * @see org.openflexo.foundation.FlexoModelObject#getFullyQualifiedName()
     */
    @Override
	public String getFullyQualifiedName()
    {
        return "WS_Repository_FOLDER";
    }
    
    @Override
	public String getName(){
    		return "ws_repository_folder";
    }
    @Override
	public String getLocalizedName(){
		return FlexoLocalization.localizedForKey(getName());
}
    
    @Override
	public void delete(){
    		if (logger.isLoggable(Level.FINE)) logger.fine("delete: WSRepositoryFolder "+getName());
    		parentGroup=null;
    		super.delete();
    		notifyObservers(new ObjectDeleted(this));
    }
    // ==========================================================================
    // ======================== TreeNode implementation
    // =========================
    // ==========================================================================

    @Override
	public TreeNode getParent()
    {
        return parentGroup;
    }

    @Override
	public boolean getAllowsChildren()
    {
        return true;
    }
    
    @Override
	public Vector<WSDLRepository> getOrderedChildren(){
    		Vector<WSDLRepository> v = new Vector<WSDLRepository>();
    		java.util.Enumeration en = getWSRepositories().elements();
    		while (en.hasMoreElements()) {
				WSRepository element = (WSRepository)en.nextElement();
				v.add(element.getWSDLRepository());
			}
    		return v;
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