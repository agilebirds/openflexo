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

import org.openflexo.fib.model.FIBPanel.Layout;
import org.openflexo.swing.layout.MultiSplitLayout.Divider;
import org.openflexo.swing.layout.MultiSplitLayout.Leaf;
import org.openflexo.swing.layout.MultiSplitLayout.RowSplit;
import org.openflexo.swing.layout.MultiSplitLayout.Split;

public class FIBSplitPanel extends FIBContainer {

	public static final String LEFT = "left";
	public static final String RIGHT = "right";

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
		return "prout";
	}

	public Split getSplit() {
		if (split == null) {
			split = getDefaultLayout();
		}
		return split;
	}

	public void setSplit(Split split) {
		this.split = split;
	}

	protected Split getDefaultLayout() {
		Leaf left = new Leaf(LEFT);
		left.setWeight(0.5);
		Leaf right = new Leaf(RIGHT);
		right.setWeight(0.5);
		return new RowSplit(left, new Divider(), right);
	}

}
