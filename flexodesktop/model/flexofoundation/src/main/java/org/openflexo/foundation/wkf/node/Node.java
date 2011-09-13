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

import java.util.TreeMap;
import java.util.Vector;

import org.openflexo.foundation.wkf.FlexoProcess;
import org.openflexo.foundation.wkf.Role;
import org.openflexo.foundation.wkf.edge.FlexoPostCondition;


public abstract class Node extends AbstractNode {

	public Node(FlexoProcess process) {
		super(process);
	}

	public void getBestRole(Vector<Node> visited, TreeMap<Integer, Vector<Role>> roles, int depth) {
		getBestRole(this, visited, roles, depth);
	}
	
	@Override
	public abstract PetriGraphNode getNode();
	
	public static void getBestRole(Node endNode, Vector<Node> visited, TreeMap<Integer, Vector<Role>> roles, int depth) {
		if (visited.contains(endNode))
			return;
		visited.add(endNode);
		for (FlexoPostCondition<AbstractNode, AbstractNode> pc : endNode.getIncomingPostConditions()) {
			AbstractNode start = pc.getStartNode();
			if (start != null) {
				AbstractActivityNode activity = null;
				if (start instanceof PetriGraphNode)
					activity = ((PetriGraphNode) start).getAbstractActivityNode();
				if (activity != null && activity.getRole() != null) {
					Vector<Role> v = roles.get(depth);
					if (v == null) {
						roles.put(depth, v = new Vector<Role>());
					}
					v.add(activity.getRole());
				} else {
					if (start instanceof Node)
						((Node) start).getBestRole(visited, roles, depth + 1);
				}
			}
		}
	}
}
