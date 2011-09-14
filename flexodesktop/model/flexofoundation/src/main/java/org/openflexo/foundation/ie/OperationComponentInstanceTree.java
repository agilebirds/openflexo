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

import java.io.PrintStream;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

import org.openflexo.foundation.ie.cl.OperationComponentDefinition;


public class OperationComponentInstanceTree {

	private OperationComponentDefinition _operationComponentDefinition;
	private Vector<OperationComponentInstance> _operationComponentInstances;
	private Hashtable<OperationComponentInstance, InstanceTree> operationMap;
	private ProjectInstanceTree _projectTree;
	
	public OperationComponentInstanceTree(OperationComponentDefinition cd, ProjectInstanceTree tree) {
		super();
		_projectTree = tree;
		_operationComponentDefinition = cd;
		_operationComponentInstances = cd.getProject().getFlexoWorkflow().getAllComponentInstances(cd);
		operationMap = new Hashtable<OperationComponentInstance, InstanceTree>();
		Enumeration<OperationComponentInstance> en = _operationComponentInstances.elements();
		OperationComponentInstance ci = null;
		while(en.hasMoreElements()){
			ci = en.nextElement();
			operationMap.put(ci, new InstanceTree(ci,null,null,this));
		}
	}
	
	public void registerInstance(InstanceTree tree){
		_projectTree.registerInstance(tree);
	}
	
	public Collection<InstanceTree> getAllInstanceTree(){
		return operationMap.values();
	}
	
	public void print(PrintStream out){
		out.println("===================================================");
		out.println(_operationComponentDefinition.getName().toUpperCase());
		Enumeration<OperationComponentInstance> en = _operationComponentInstances.elements();
		while(en.hasMoreElements()){
			operationMap.get(en.nextElement()).print(out);
		}
	}
	@Override
	public String toString(){
		StringBuffer buf = new StringBuffer();
		Enumeration<OperationComponentInstance> en = _operationComponentInstances.elements();
		while(en.hasMoreElements()){
			buf.append(operationMap.get(en.nextElement()).toString(buf));
		}
		return buf.toString();
	}
}
