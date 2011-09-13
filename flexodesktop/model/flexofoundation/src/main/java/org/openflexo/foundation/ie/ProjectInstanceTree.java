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
package org.openflexo.foundation.ie;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

import org.openflexo.foundation.ie.cl.ComponentDefinition;
import org.openflexo.foundation.ie.cl.OperationComponentDefinition;
import org.openflexo.foundation.rm.FlexoProject;


public class ProjectInstanceTree {
	
	private Hashtable<ComponentDefinition, Vector<InstanceTree>> bigMap;
	private FlexoProject _project;
	
	public ProjectInstanceTree(FlexoProject prj){
		super();
		_project = prj;
		bigMap = new Hashtable<ComponentDefinition, Vector<InstanceTree>>();
		Enumeration<OperationComponentDefinition> en = prj.getAllInstanciatedOperationComponentDefinition().elements();
		OperationComponentInstanceTree temp = null;
		OperationComponentDefinition opcd = null;
		while(en.hasMoreElements()){
			temp = new OperationComponentInstanceTree(en.nextElement(),this);
		}
	}
	
	public void registerInstance(InstanceTree tree){
		if(bigMap.get(tree.getComponentDefinition())==null){
			bigMap.put(tree.getComponentDefinition(), new Vector<InstanceTree>());
		}
		if(!bigMap.get(tree.getComponentDefinition()).contains(tree)){
			bigMap.get(tree.getComponentDefinition()).add(tree);
		}
	}
	
	public Vector<OperationComponentInstance> getAllOperationsWhereComponentIsUsed(ComponentDefinition cd){
		Vector<OperationComponentInstance> reply = new Vector<OperationComponentInstance>();
		Vector<InstanceTree> instances = bigMap.get(cd);
		if(instances!=null && instances.size()>0){
			InstanceTree t = null;
			Enumeration<InstanceTree> en = instances.elements();
			while (en.hasMoreElements()) {
				t = en.nextElement();
				if(!reply.contains(t.getOperationComponentInstance()))reply.add(t.getOperationComponentInstance());
			}
		}
		
		return reply;
	}
	
}
