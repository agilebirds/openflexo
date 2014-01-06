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
package org.openflexo.fib.model;

import java.util.List;
import java.util.Vector;

import org.openflexo.fib.model.FIBPanel.Layout;
import org.openflexo.swing.layout.MultiSplitLayout.ColSplit;
import org.openflexo.swing.layout.MultiSplitLayout.Divider;
import org.openflexo.swing.layout.MultiSplitLayout.Leaf;
import org.openflexo.swing.layout.MultiSplitLayout.Node;
import org.openflexo.swing.layout.MultiSplitLayout.RowSplit;
import org.openflexo.swing.layout.MultiSplitLayout.Split;

public class FIBSplitPanel extends FIBContainer {

	public static enum Parameters implements FIBModelAttribute {
		split
	}

	public static final String LEFT = "left";
	public static final String RIGHT = "right";
	public static final String TOP = "top";
	public static final String BOTTOM = "bottom";

	private Split split;

	@Override
	public Layout getLayout() {
		return Layout.split;
	}

	@Override
	public String getIdentifier() {
		return null;
	}

	public String getFirstEmptyPlaceHolder() {
		if (getAllLeaves().size() > 0) {
			return getAllLeaves().get(0).getName();
		}
		return "leaf";
	}

	public Split getSplit() {
		if (split == null) {
			split = getDefaultHorizontalLayout();
		}
		return split;
	}

	public void setSplit(Split split) {
		FIBAttributeNotification<Split> notification = requireChange(Parameters.split, split);
		if (notification != null) {
			this.split = split;
			hasChanged(notification);
		}
	}

	protected RowSplit getDefaultHorizontalLayout() {
		Leaf left = new Leaf(findNextAvailableLeaf(LEFT));
		left.setWeight(0.5);
		Leaf right = new Leaf(findNextAvailableLeaf(RIGHT));
		right.setWeight(0.5);
		return new RowSplit(left, new Divider(), right);
	}

	protected ColSplit getDefaultVerticalLayout() {
		Leaf left = new Leaf(findNextAvailableLeaf(TOP));
		left.setWeight(0.5);
		Leaf right = new Leaf(findNextAvailableLeaf(BOTTOM));
		right.setWeight(0.5);
		return new ColSplit(left, new Divider(), right);
	}

	public void makeDefaultHorizontalLayout() {
		setSplit(getDefaultHorizontalLayout());
	}

	public void makeDefaultVerticalLayout() {
		setSplit(getDefaultVerticalLayout());
	}

	public Divider addDivider(Split parent) {
		Divider returned = new Divider();
		parent.addToChildren(returned);
		notifySplitLayoutChange();
		return returned;
	}

	public Leaf addLeaf(Split parent) {
		Leaf returned = new Leaf(findNextAvailableLeaf("leaf"));
		parent.addToChildren(returned);
		notifySplitLayoutChange();
		return returned;
	}

	public ColSplit addVerticalSplit(Split parent) {
		ColSplit returned = new ColSplit();
		parent.addToChildren(returned);
		notifySplitLayoutChange();
		return returned;
	}

	public RowSplit addHorizontalSplit(Split parent) {
		RowSplit returned = new RowSplit();
		parent.addToChildren(returned);
		notifySplitLayoutChange();
		return returned;
	}

	public ColSplit addDefaultVerticalSplit(Split parent) {
		ColSplit returned = getDefaultVerticalLayout();
		parent.addToChildren(returned);
		notifySplitLayoutChange();
		return returned;
	}

	public RowSplit addDefaultHorizontalSplit(Split parent) {
		RowSplit returned = getDefaultHorizontalLayout();
		parent.addToChildren(returned);
		notifySplitLayoutChange();
		return returned;
	}

	public Node removeNode(Node node) {
		if (node != getSplit()) {
			node.getParent().removeFromChildren(node);
			notifySplitLayoutChange();
		}
		return node;
	}

	public void notifySplitLayoutChange() {
		FIBAttributeNotification<Split> notification = new FIBAttributeNotification<Split>(Parameters.split, null, split);
		hasChanged(notification);
	}

	public List<Leaf> getAllLeaves() {
		Vector<Leaf> returned = new Vector<Leaf>();
		appendToLeaves(getSplit(), returned);
		return returned;
	}

	private void appendToLeaves(Node n, List<Leaf> returned) {
		if (n instanceof Leaf) {
			returned.add((Leaf) n);
		} else if (n instanceof Split) {
			for (Node n2 : ((Split) n).getChildren()) {
				appendToLeaves(n2, returned);
			}
		}
	}

	public Leaf getLeafNamed(String aName) {
		for (Leaf l : getAllLeaves()) {
			if (l.getName().equals(aName)) {
				return l;
			}
		}
		return null;
	}

	public String findNextAvailableLeaf(String baseName) {
		if (split == null) {
			return baseName;
		}
		int i = 2;
		String tryMe = baseName;
		while (getLeafNamed(tryMe) != null) {
			tryMe = baseName + i;
			i++;
		}
		return tryMe;
	}

}
