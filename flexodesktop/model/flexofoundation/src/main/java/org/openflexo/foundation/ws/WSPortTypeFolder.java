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

import java.util.Enumeration;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.tree.TreeNode;

import org.openflexo.localization.FlexoLocalization;
import org.openflexo.logging.FlexoLogger;

public class WSPortTypeFolder extends WSObject {

	 private static final Logger logger = FlexoLogger.getLogger(WSPortTypeFolder.class.getPackage()
	            .getName());
	private WSService parentService;
	
    /**
     * @param dl
     */
    public WSPortTypeFolder(WSService group)
    {
        super(group.getWSLibrary());
        parentService=group;
    }

    public Vector getWSPortTypes() {
        return parentService.getWSPortTypes();
    }
    
    
    public WSService getWSService(){
    		return parentService;
    }
    
    /**
     * Overrides getFullyQualifiedName
     * @see org.openflexo.foundation.FlexoModelObject#getFullyQualifiedName()
     */
    @Override
	public String getFullyQualifiedName()
    {
        return "WS_PORTTYPE_FOLDER";
    }
    
    @Override
	public String getName(){
    		return "ws_process_folder";
    }
    @Override
	public String getLocalizedName(){
		return FlexoLocalization.localizedForKey(getName());
}
    
    @Override
	public void delete(){
    		if (logger.isLoggable(Level.FINE)) logger.fine("delete: WSPortTypeFolder "+getName());
    		parentService=null;
    		super.delete();
    		deleteObservers();
    }
    // ==========================================================================
    // ======================== TreeNode implementation
    // =========================
    // ==========================================================================

    @Override
	public TreeNode getParent()
    {
        return parentService;
    }

    @Override
	public boolean getAllowsChildren()
    {
        return true;
    }
    
    @Override
	public Vector getOrderedChildren(){
    		Vector a = new Vector();
    		Enumeration en=getWSPortTypes().elements();
    		while (en.hasMoreElements()) {
				WSPortType element = (WSPortType) en.nextElement();
				a.add(element.getFlexoProcess());
			}
    		return a;
    }
    
    // ==========================================================================
    // ======================== Search WSPortType
    // =========================
    // ==========================================================================
    
    public WSPortType getWSPortTypeNamed(String name){
    		return parentService.getWSPortTypeNamed(name);
    }
    
    @Override
	public String getClassNameKey() {
    	// TODO Auto-generated method stub
    	return "ws_port_type_folder";
    }
}