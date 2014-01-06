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
package org.openflexo.fge.control.notifications;

import org.openflexo.fge.Drawing.DrawingTreeNode;

/**
 * This notification is thrown when a node has been added to selection
 * 
 * @author sylvain
 * 
 */
public class ObjectAddedToSelection extends ControlNotification {

	public static final String EVENT_NAME = "ObjectAddedToSelection";

	private DrawingTreeNode<?, ?> newSelectedNode;

	public ObjectAddedToSelection(DrawingTreeNode<?, ?> newSelectedNode) {
		super(EVENT_NAME, null, newSelectedNode);
		this.newSelectedNode = newSelectedNode;
	}

	public DrawingTreeNode<?, ?> getNewSelectedNode() {
		return newSelectedNode;
	}

}
