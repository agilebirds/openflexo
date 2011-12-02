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

import javax.swing.JComponent;

import org.openflexo.fib.model.FIBPanel.Layout;

public class BoxLayoutConstraints extends ComponentConstraints {

	private static final String ALIGNMENT_X = "alignmentX";
	private static final String ALIGNMENT_Y = "alignmentY";

	public BoxLayoutConstraints() {
		super();
	}

	protected BoxLayoutConstraints(String someConstraints) {
		super(someConstraints);
	}

	BoxLayoutConstraints(ComponentConstraints someConstraints) {
		super(someConstraints);
	}

	public BoxLayoutConstraints(int index) {
		super();
		setIndex(index);
	}

	@Override
	protected Layout getType() {
		return Layout.box;
	}

	public float getAlignmentX() {
		return getFloatValue(ALIGNMENT_X, 0.5f);
	}

	public void setAlignmentX(float x) {
		setFloatValue(ALIGNMENT_X, x);
	}

	public float getAlignmentY() {
		return getFloatValue(ALIGNMENT_Y, 0.5f);
	}

	public void setAlignmentY(float y) {
		setFloatValue(ALIGNMENT_Y, y);
	}

	@Override
	public void performConstrainedAddition(JComponent container, JComponent contained) {
		contained.setAlignmentX(getAlignmentX());
		contained.setAlignmentY(getAlignmentY());
		container.add(contained);
	}
}
