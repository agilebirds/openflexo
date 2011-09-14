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
package org.openflexo.foundation.wkf.node;

import java.util.Vector;

import org.openflexo.foundation.bindings.BindingAssignment;
import org.openflexo.foundation.bindings.BindingValue;
import org.openflexo.foundation.bindings.WKFBindingDefinition;
import org.openflexo.foundation.wkf.FlexoPetriGraph;


/**
 * Please comment this class
 * 
 * @author sguerin
 * 
 */
public interface SelfExecutableNode
{

     public WKFBindingDefinition getExecutionPrimitiveBindingDefinition();

    public BindingValue getExecutionPrimitive();

    public void setExecutionPrimitive(BindingValue executionPrimitive);

	public Vector<BindingAssignment> getAssignments();

	public void setAssignments(Vector<BindingAssignment> someAssignments);

	public void addToAssignments(BindingAssignment assignment);

	public void removeFromAssignments(BindingAssignment assignment);

	public FlexoPetriGraph getExecutionPetriGraph();
	
	public boolean hasExecutionPetriGraph();
}
