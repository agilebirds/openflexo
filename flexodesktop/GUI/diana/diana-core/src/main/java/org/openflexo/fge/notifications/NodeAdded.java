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
package org.openflexo.fge.notifications;

import org.openflexo.fge.Drawing.ContainerNode;
import org.openflexo.fge.Drawing.DrawingTreeNode;

/**
 * This notification is thrown when a node has been added in the drawing tree
 * 
 * @author sylvain
 * 
 */
public class NodeAdded extends FGENotification {

	public static final String EVENT_NAME = "NodeAdded";

	private DrawingTreeNode<?, ?> addedNode;
	private ContainerNode<?, ?> parent;

	public NodeAdded(DrawingTreeNode<?, ?> addedNode, ContainerNode<?, ?> parent) {
		super(EVENT_NAME, null, addedNode);
		this.addedNode = addedNode;
		this.parent = parent;
	}

	public DrawingTreeNode<?, ?> getAddedNode() {
		return addedNode;
	}

	public ContainerNode<?, ?> getParent() {
		return parent;
	}

}
