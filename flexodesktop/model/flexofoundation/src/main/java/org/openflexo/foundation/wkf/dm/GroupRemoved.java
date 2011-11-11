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
package org.openflexo.foundation.wkf.dm;

import java.util.Vector;

import org.openflexo.foundation.wkf.WKFGroup;
import org.openflexo.foundation.wkf.node.PetriGraphNode;

public class GroupRemoved extends WKFDataModification {

	private Vector<PetriGraphNode> nodesThatWereInGroup;

	public GroupRemoved(WKFGroup removedGroup) {
		super(removedGroup, null);
	}

	public GroupRemoved(WKFGroup removedGroup, Vector<PetriGraphNode> nodesThatWereInGroup) {
		super(removedGroup, null);
		this.nodesThatWereInGroup = nodesThatWereInGroup;
	}

	public Vector<PetriGraphNode> getNodesThatWereInGroup() {
		return nodesThatWereInGroup;
	}

	@Override
	public WKFGroup oldValue() {
		return (WKFGroup) super.oldValue();
	}

}
