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

import javax.swing.JComponent;

import org.openflexo.fib.model.FIBPanel.Layout;
import org.openflexo.swing.layout.MultiSplitLayout.Leaf;

public class SplitLayoutConstraints extends ComponentConstraints {

	private static final String SPLIT_IDENTIFIER = "splitIdentifier";

	public SplitLayoutConstraints() {
		super();
	}

	public static SplitLayoutConstraints makeSplitLayoutConstraints(String splitIdentifier) {
		SplitLayoutConstraints returned = new SplitLayoutConstraints();
		returned.setSplitIdentifier(splitIdentifier);
		return returned;
	}

	protected SplitLayoutConstraints(String someConstraints) {
		super(someConstraints);
	}

	SplitLayoutConstraints(ComponentConstraints someConstraints) {
		super(someConstraints);
	}

	@Override
	protected Layout getType() {
		return Layout.split;
	}

	@Override
	public void performConstrainedAddition(JComponent container, JComponent contained) {
		container.add(contained);
	}

	public String getSplitIdentifier() {
		return getStringValue(SPLIT_IDENTIFIER, "id");
	}

	public void setSplitIdentifier(String splitIdentifier) {
		setStringValue(SPLIT_IDENTIFIER, splitIdentifier);
	}

	public Leaf getSplitLeaf() {
		if (getComponent() != null && getComponent().getParent() instanceof FIBSplitPanel) {
			return ((FIBSplitPanel) getComponent().getParent()).getLeafNamed(getSplitIdentifier());
		}
		return null;
	}

	public void setSplitLeaf(Leaf aLeaf) {
		if (getComponent() != null && getComponent().getParent() instanceof FIBSplitPanel) {
			setSplitIdentifier(aLeaf.getName());
		}
	}

	public List<Leaf> getAvailableLeaves() {
		if (getComponent() != null && getComponent().getParent() instanceof FIBSplitPanel) {
			return ((FIBSplitPanel) getComponent().getParent()).getAllLeaves();
		}
		return null;
	}

	public double getWeight() {
		if (getSplitLeaf() != null) {
			return getSplitLeaf().getWeight();
		}
		return 0;
	}

	public void setWeight(double weight) {
		if (getSplitLeaf() != null) {
			getSplitLeaf().setWeight(weight);
			((FIBSplitPanel) getComponent().getParent()).notifySplitLayoutChange();
		}
	}

}
