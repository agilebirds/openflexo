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

import java.awt.GridBagConstraints;
import java.awt.Insets;

import javax.swing.JComponent;

import org.openflexo.fib.model.FIBPanel.Layout;

public class TwoColsLayoutConstraints extends ComponentConstraints {

	private static final String LOCATION = "location";
	private static final String EXPAND_VERTICALLY = "expandVertically";
	private static final String EXPAND_HORIZONTALLY = "expandHorizontally";

	public TwoColsLayoutLocation getLocation() {
		return getEnumValue(LOCATION, TwoColsLayoutLocation.class, TwoColsLayoutLocation.center);
	}

	public void setLocation(TwoColsLayoutLocation location) {
		setEnumValue(LOCATION, location);
	}

	public boolean getExpandVertically() {
		return getBooleanValue(EXPAND_VERTICALLY, false);
	}

	public void setExpandVertically(boolean flag) {
		setBooleanValue(EXPAND_VERTICALLY, flag);
	}

	public boolean getExpandHorizontally() {
		return getBooleanValue(EXPAND_HORIZONTALLY, false);
	}

	public void setExpandHorizontally(boolean flag) {
		setBooleanValue(EXPAND_HORIZONTALLY, flag);
	}

	public static enum TwoColsLayoutLocation {
		left, right, center;
	}

	private static final String INSETS_TOP = "insetsTop";
	private static final String INSETS_BOTTOM = "insetsBottom";
	private static final String INSETS_LEFT = "insetsLeft";
	private static final String INSETS_RIGHT = "insetsRight";

	public int getInsetsTop() {
		return getIntValue(INSETS_TOP, 0);
	}

	public void setInsetsTop(int insetsTop) {
		setIntValue(INSETS_TOP, insetsTop);
	}

	public int getInsetsBottom() {
		return getIntValue(INSETS_BOTTOM, 0);
	}

	public void setInsetsBottom(int insetsBottom) {
		setIntValue(INSETS_BOTTOM, insetsBottom);
	}

	public int getInsetsLeft() {
		return getIntValue(INSETS_LEFT, 0);
	}

	public void setInsetsLeft(int insetsLeft) {
		setIntValue(INSETS_LEFT, insetsLeft);
	}

	public int getInsetsRight() {
		return getIntValue(INSETS_RIGHT, 0);
	}

	public void setInsetsRight(int insetsRight) {
		setIntValue(INSETS_RIGHT, insetsRight);
	}

	public TwoColsLayoutConstraints() {
		super();
	}

	public TwoColsLayoutConstraints(TwoColsLayoutLocation location, boolean expandHorizontally, boolean expandVertically) {
		super();
		setLocation(location);
		setExpandHorizontally(expandHorizontally);
		setExpandVertically(expandVertically);
	}

	protected TwoColsLayoutConstraints(String someConstraints) {
		super(someConstraints);
	}

	TwoColsLayoutConstraints(ComponentConstraints someConstraints) {
		super(someConstraints);
	}

	@Override
	protected Layout getType() {
		return Layout.twocols;
	}

	@Override
	public void performConstrainedAddition(JComponent container, JComponent contained) {
		GridBagConstraints c = new GridBagConstraints();
		// c.insets = new Insets(3, 3, 3, 3);
		c.insets = new Insets(getInsetsTop(), getInsetsLeft(), getInsetsBottom(), getInsetsRight());
		if (getLocation() == TwoColsLayoutLocation.left) {
			c.fill = GridBagConstraints.NONE;
			c.weightx = 0; // 1.0;
			c.gridwidth = 1;
			c.anchor = GridBagConstraints.NORTHEAST;
			if (getExpandVertically()) {
				// c.weighty = 1.0;
				c.fill = GridBagConstraints.VERTICAL;
			} else {
				// c.insets = new Insets(5, 2, 0, 2);
			}
		} else {
			if (getExpandHorizontally()) {
				c.fill = GridBagConstraints.BOTH;
				c.anchor = GridBagConstraints.CENTER;
				if (getExpandVertically()) {
					c.weighty = 1.0;
				}
			} else {
				c.fill = GridBagConstraints.NONE;
				c.anchor = GridBagConstraints.WEST;
			}
			c.weightx = 1.0; // 2.0;
			c.gridwidth = GridBagConstraints.REMAINDER;
		}

		container.add(contained, c);

		/*GridBagLayout gridbag = (GridBagLayout)getJComponent().getLayout();
		GridBagConstraints gridBagConstraints = (GridBagConstraints)getConstraints().get(c);
		gridbag.setConstraints(c,gridBagConstraints);
		getJComponent().add(c);

		container.add(contained);*/
	}

}
