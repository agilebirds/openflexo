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

public class GridBagLayoutConstraints extends ComponentConstraints {

	private static final String ANCHOR = "anchor";
	private static final String FILL = "fill";

	public static enum AnchorType {
		north {
			@Override
			public int getAnchor() {
				return GridBagConstraints.NORTH;
			}
		},
		south {
			@Override
			public int getAnchor() {
				return GridBagConstraints.SOUTH;
			}
		},
		east {
			@Override
			public int getAnchor() {
				return GridBagConstraints.EAST;
			}
		},
		west {
			@Override
			public int getAnchor() {
				return GridBagConstraints.WEST;
			}
		},
		center {
			@Override
			public int getAnchor() {
				return GridBagConstraints.CENTER;
			}
		},
		north_east {
			@Override
			public int getAnchor() {
				return GridBagConstraints.NORTHEAST;
			}
		},
		north_west {
			@Override
			public int getAnchor() {
				return GridBagConstraints.NORTHWEST;
			}
		},
		south_east {
			@Override
			public int getAnchor() {
				return GridBagConstraints.SOUTHEAST;
			}
		},
		south_west {
			@Override
			public int getAnchor() {
				return GridBagConstraints.SOUTHWEST;
			}
		};
		public abstract int getAnchor();
	}

	public static enum FillType {
		none {
			@Override
			public int getFill() {
				return GridBagConstraints.NONE;
			}
		},
		horizontal {
			@Override
			public int getFill() {
				return GridBagConstraints.HORIZONTAL;
			}
		},
		vertical {
			@Override
			public int getFill() {
				return GridBagConstraints.VERTICAL;
			}
		},
		both {
			@Override
			public int getFill() {
				return GridBagConstraints.BOTH;
			}
		};
		public abstract int getFill();
	}

	public GridBagLayoutConstraints() {
		super();
	}

	protected GridBagLayoutConstraints(String someConstraints) {
		super(someConstraints);
	}

	GridBagLayoutConstraints(ComponentConstraints someConstraints) {
		super(someConstraints);
	}

	public GridBagLayoutConstraints(int index) {
		super();
		setIndex(index);
		setGridX(GridBagConstraints.RELATIVE);
		setGridY(GridBagConstraints.RELATIVE);
		setGridWidth(1);
		setGridHeight(1);
		setWeightX(0);
		setWeightY(0);
		setAnchor(AnchorType.center);
		setFill(FillType.none);
		setInsetsTop(0);
		setInsetsBottom(0);
		setInsetsLeft(0);
		setInsetsRight(0);
		setPadX(0);
		setPadY(0);
	}

	@Override
	protected Layout getType() {
		return Layout.gridbag;
	}

	@Override
	public void performConstrainedAddition(JComponent container, JComponent contained) {
		GridBagConstraints c = new GridBagConstraints();

		c.gridx = getGridX();
		c.gridy = getGridY();
		c.gridwidth = getGridWidth();
		c.gridheight = getGridHeight();

		c.weightx = getWeightX();
		c.weighty = getWeightY();
		c.anchor = getAnchor().getAnchor();
		c.fill = getFill().getFill();

		c.insets = new Insets(getInsetsTop(), getInsetsLeft(), getInsetsBottom(), getInsetsRight());
		c.ipadx = getPadX();
		c.ipady = getPadY();

		container.add(contained, c);
	}

	public AnchorType getAnchor() {
		return getEnumValue(ANCHOR, AnchorType.class, AnchorType.center);
	}

	public void setAnchor(AnchorType location) {
		setEnumValue(ANCHOR, location);
	}

	public FillType getFill() {
		return getEnumValue(FILL, FillType.class, FillType.none);
	}

	public void setFill(FillType fill) {
		setEnumValue(FILL, fill);
	}

	private static final String GRID_X = "gridX";
	private static final String GRID_Y = "gridY";

	public int getGridX() {
		return getIntValue(GRID_X, GridBagConstraints.RELATIVE);
	}

	public void setGridX(int gridX) {
		setIntValue(GRID_X, gridX);
	}

	public boolean getGridXRelative() {
		return getGridX() == GridBagConstraints.RELATIVE;
	}

	public void setGridXRelative(boolean flag) {
		if (flag) {
			setGridX(GridBagConstraints.RELATIVE);
		} else {
			setGridX(0);
		}
	}

	public int getGridY() {
		return getIntValue(GRID_Y, GridBagConstraints.RELATIVE);
	}

	public void setGridY(int gridY) {
		setIntValue(GRID_Y, gridY);
	}

	public boolean getGridYRelative() {
		return getGridY() == GridBagConstraints.RELATIVE;
	}

	public void setGridYRelative(boolean flag) {
		if (flag) {
			setGridY(GridBagConstraints.RELATIVE);
		} else {
			setGridY(0);
		}
	}

	private static final String GRID_WIDTH = "gridWidth";
	private static final String GRID_HEIGHT = "gridHeight";

	public int getGridWidth() {
		return getIntValue(GRID_WIDTH, 1);
	}

	public void setGridWidth(int gridWidth) {
		setIntValue(GRID_WIDTH, gridWidth);
	}

	public boolean getGridWidthRelative() {
		return getGridWidth() == GridBagConstraints.RELATIVE;
	}

	public void setGridWidthRelative(boolean flag) {
		if (flag) {
			setGridWidth(GridBagConstraints.RELATIVE);
		} else {
			setGridWidth(1);
		}
	}

	public boolean getGridWidthRemainder() {
		return getGridWidth() == GridBagConstraints.REMAINDER;
	}

	public void setGridWidthRemainder(boolean flag) {
		if (flag) {
			setGridWidth(GridBagConstraints.REMAINDER);
		} else {
			setGridWidth(1);
		}
	}

	public int getGridHeight() {
		return getIntValue(GRID_HEIGHT, 1);
	}

	public void setGridHeight(int gridHeight) {
		setIntValue(GRID_HEIGHT, gridHeight);
	}

	public boolean getGridHeightRelative() {
		return getGridHeight() == GridBagConstraints.RELATIVE;
	}

	public void setGridHeightRelative(boolean flag) {
		if (flag) {
			setGridHeight(GridBagConstraints.RELATIVE);
		} else {
			setGridHeight(1);
		}
	}

	public boolean getGridHeightRemainder() {
		return getGridHeight() == GridBagConstraints.REMAINDER;
	}

	public void setGridHeightRemainder(boolean flag) {
		if (flag) {
			setGridHeight(GridBagConstraints.REMAINDER);
		} else {
			setGridHeight(1);
		}
	}

	private static final String WEIGHT_X = "weightX";
	private static final String WEIGHT_Y = "weightY";

	public double getWeightX() {
		return getDoubleValue(WEIGHT_X, 0);
	}

	public void setWeightX(double weightX) {
		setDoubleValue(WEIGHT_X, weightX);
	}

	public double getWeightY() {
		return getDoubleValue(WEIGHT_Y, 0);
	}

	public void setWeightY(double weightY) {
		setDoubleValue(WEIGHT_Y, weightY);
	}

	private static final String PAD_X = "padX";
	private static final String PAD_Y = "padY";

	public int getPadX() {
		return getIntValue(PAD_X, 0);
	}

	public void setPadX(int padX) {
		setIntValue(PAD_X, padX);
	}

	public int getPadY() {
		return getIntValue(PAD_Y, 0);
	}

	public void setPadY(int padY) {
		setIntValue(PAD_Y, padY);
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

}
