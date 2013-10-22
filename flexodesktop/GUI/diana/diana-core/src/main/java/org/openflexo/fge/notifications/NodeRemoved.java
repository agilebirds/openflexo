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
 * This notification is thrown when a node has been removed from the drawing tree
 * 
 * @author sylvain
 * 
 */
public class NodeRemoved extends FGENotification {

	public static final String EVENT_NAME = "NodeRemoved";

	private DrawingTreeNode<?, ?> removedNode;
	private ContainerNode<?, ?> parent;

	// Hack: stores the ex-parent in new value
	public NodeRemoved(DrawingTreeNode<?, ?> removedNode, ContainerNode<?, ?> parent) {
		super(EVENT_NAME, removedNode, parent);
		this.removedNode = removedNode;
		this.parent = parent;
	}

	public DrawingTreeNode<?, ?> getRemovedNode() {
		return removedNode;
	}

	public ContainerNode<?, ?> getParent() {
		return parent;
	}

}
