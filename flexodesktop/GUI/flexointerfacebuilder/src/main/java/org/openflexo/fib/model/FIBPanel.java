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

import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.util.logging.Logger;

import javax.swing.BoxLayout;

public class FIBPanel extends FIBContainer {

	private static final Logger logger = Logger.getLogger(FIBPanel.class.getPackage().getName());

	private Layout layout;

	private FlowLayoutAlignment flowAlignment = null;
	private BoxLayoutAxis boxLayoutAxis = null;

	private Integer hGap = null;
	private Integer vGap = null;

	private Integer cols = null;
	private Integer rows = null;

	private Border border = Border.empty;
	private Color borderColor = null;
	private String borderTitle = null;
	private Integer borderTop = null;
	private Integer borderBottom = null;
	private Integer borderLeft = null;
	private Integer borderRight = null;

	private Font titleFont = null;
	private int darkLevel = 0;

	private boolean trackViewPortWidth = true;
	private boolean trackViewPortHeight = true;

	private boolean protectContent = false;

	public static enum Parameters implements FIBModelAttribute {
		layout,
		flowAlignment,
		boxLayoutAxis,
		hGap,
		vGap,
		cols,
		rows,
		border,
		borderColor,
		borderTitle,
		borderTop,
		borderBottom,
		borderLeft,
		borderRight,
		titleFont,
		darkLevel,
		protectContent,
		trackViewPortWidth,
		trackViewPortHeight;
	}

	public static enum Layout {
		none, flow, border, grid, box, twocols, gridbag, split
	}

	public static enum FlowLayoutAlignment {
		LEFT {
			@Override
			public int getAlign() {
				return FlowLayout.LEFT;
			}
		},
		RIGHT {
			@Override
			public int getAlign() {
				return FlowLayout.RIGHT;
			}
		},
		CENTER {
			@Override
			public int getAlign() {
				return FlowLayout.CENTER;
			}
		},
		LEADING {
			@Override
			public int getAlign() {
				return FlowLayout.LEADING;
			}
		},
		TRAILING {
			@Override
			public int getAlign() {
				return FlowLayout.TRAILING;
			}
		};

		public abstract int getAlign();
	}

	public static enum BoxLayoutAxis {
		X_AXIS {
			@Override
			public int getAxis() {
				return BoxLayout.X_AXIS;
			}
		},
		Y_AXIS {
			@Override
			public int getAxis() {
				return BoxLayout.Y_AXIS;
			}
		};

		public abstract int getAxis();
	}

	public static enum Border {
		empty, line, etched, raised, lowered, titled, rounded3d
	}

	public FIBPanel() {
		super();
		layout = Layout.none;
	}

	@Override
	public String getIdentifier() {
		return null;
	}

	@Override
	public Layout getLayout() {
		return layout;
	}

	@Override
	public void setLayout(Layout layout) {
		FIBAttributeNotification<Layout> notification = requireChange(Parameters.layout, layout);
		if (notification != null) {
			this.layout = layout;
			switch (layout) {
			case none:
				break;
			case flow:
				if (flowAlignment == null) {
					flowAlignment = FlowLayoutAlignment.LEADING;
				}
				if (hGap == null) {
					hGap = 5;
				}
				if (vGap == null) {
					vGap = 5;
				}
				break;
			case grid:
				if (hGap == null) {
					hGap = 5;
				}
				if (vGap == null) {
					vGap = 5;
				}
				if (rows == null) {
					rows = 2;
				}
				if (cols == null) {
					cols = 2;
				}
				break;
			case box:
				if (boxLayoutAxis == null) {
					boxLayoutAxis = BoxLayoutAxis.X_AXIS;
				}
				break;
			case border:
				break;
			case twocols:
				break;
			case gridbag:
				break;

			default:
				break;
			}

			hasChanged(notification);
		}
	}

	public Border getBorder() {
		return border;
	}

	public void setBorder(Border border) {
		FIBAttributeNotification<Border> notification = requireChange(Parameters.border, border);
		if (notification != null) {
			this.border = border;
			switch (border) {
			case line:
				if (borderColor == null) {
					borderColor = Color.BLACK;
				}
				break;
			case titled:
				if (borderTitle == null) {
					borderTitle = "Panel";
				}
				break;
			case rounded3d:
				if (borderTop == null) {
					borderTop = 2;
				}
				if (borderBottom == null) {
					borderBottom = 2;
				}
				if (borderLeft == null) {
					borderRight = 2;
				}
				if (borderRight == null) {
					borderRight = 2;
				}
				break;

			default:
				break;
			}
			hasChanged(notification);
		}
	}

	public Integer getHGap() {
		return hGap;
	}

	public void setHGap(Integer hGap) {
		FIBAttributeNotification<Integer> notification = requireChange(Parameters.hGap, hGap);
		if (notification != null) {
			this.hGap = hGap;
			hasChanged(notification);
		}
	}

	public Integer getVGap() {
		return vGap;
	}

	public void setVGap(Integer vGap) {
		FIBAttributeNotification<Integer> notification = requireChange(Parameters.vGap, vGap);
		if (notification != null) {
			this.vGap = vGap;
			hasChanged(notification);
		}
	}

	public FlowLayoutAlignment getFlowAlignment() {
		return flowAlignment;
	}

	public void setFlowAlignment(FlowLayoutAlignment flowAlignment) {
		FIBAttributeNotification<FlowLayoutAlignment> notification = requireChange(Parameters.flowAlignment, flowAlignment);
		if (notification != null) {
			this.flowAlignment = flowAlignment;
			hasChanged(notification);
		}
	}

	public Integer getCols() {
		return cols;
	}

	public void setCols(Integer cols) {
		// logger.info("setCols with "+cols);
		FIBAttributeNotification<Integer> notification = requireChange(Parameters.cols, cols);
		if (notification != null) {
			this.cols = cols;
			hasChanged(notification);
		}
	}

	public Integer getRows() {
		return rows;
	}

	public void setRows(Integer rows) {
		// logger.info("setRows with "+rows);
		FIBAttributeNotification<Integer> notification = requireChange(Parameters.rows, rows);
		if (notification != null) {
			this.rows = rows;
			hasChanged(notification);
		}
	}

	public Color getBorderColor() {
		return borderColor;
	}

	public void setBorderColor(Color borderColor) {
		FIBAttributeNotification<Color> notification = requireChange(Parameters.borderColor, borderColor);
		if (notification != null) {
			this.borderColor = borderColor;
			hasChanged(notification);
		}
	}

	public String getBorderTitle() {
		return borderTitle;
	}

	public void setBorderTitle(String borderTitle) {
		FIBAttributeNotification<String> notification = requireChange(Parameters.borderTitle, borderTitle);
		if (notification != null) {
			this.borderTitle = borderTitle;
			hasChanged(notification);
		}
	}

	public BoxLayoutAxis getBoxLayoutAxis() {
		return boxLayoutAxis;
	}

	public void setBoxLayoutAxis(BoxLayoutAxis boxLayoutAxis) {
		FIBAttributeNotification<BoxLayoutAxis> notification = requireChange(Parameters.boxLayoutAxis, boxLayoutAxis);
		if (notification != null) {
			this.boxLayoutAxis = boxLayoutAxis;
			hasChanged(notification);
		}
	}

	public boolean getProtectContent() {
		return protectContent;
	}

	public void setProtectContent(boolean protectContent) {
		FIBAttributeNotification<Boolean> notification = requireChange(Parameters.protectContent, protectContent);
		if (notification != null) {
			this.protectContent = protectContent;
			hasChanged(notification);
		}

	}

	public Integer getBorderTop() {
		return borderTop;
	}

	public void setBorderTop(Integer borderTop) {
		FIBAttributeNotification<Integer> notification = requireChange(Parameters.borderTop, borderTop);
		if (notification != null) {
			this.borderTop = borderTop;
			hasChanged(notification);
		}
	}

	public Integer getBorderBottom() {
		return borderBottom;
	}

	public void setBorderBottom(Integer borderBottom) {
		FIBAttributeNotification<Integer> notification = requireChange(Parameters.borderBottom, borderBottom);
		if (notification != null) {
			this.borderBottom = borderBottom;
			hasChanged(notification);
		}
	}

	public Integer getBorderLeft() {
		return borderLeft;
	}

	public void setBorderLeft(Integer borderLeft) {
		FIBAttributeNotification<Integer> notification = requireChange(Parameters.borderLeft, borderLeft);
		if (notification != null) {
			this.borderLeft = borderLeft;
			hasChanged(notification);
		}
	}

	public Integer getBorderRight() {
		return borderRight;
	}

	public void setBorderRight(Integer borderRight) {
		FIBAttributeNotification<Integer> notification = requireChange(Parameters.borderRight, borderRight);
		if (notification != null) {
			this.borderRight = borderRight;
			hasChanged(notification);
		}
	}

	public Font getTitleFont() {
		if (titleFont == null) {
			return retrieveValidFont();
		}
		return titleFont;
	}

	public void setTitleFont(Font titleFont) {
		FIBAttributeNotification<Font> notification = requireChange(Parameters.titleFont, titleFont);
		if (notification != null) {
			this.titleFont = titleFont;
			hasChanged(notification);
		}
	}

	public int getDarkLevel() {
		return darkLevel;
	}

	public void setDarkLevel(int darkLevel) {
		FIBAttributeNotification<Integer> notification = requireChange(Parameters.darkLevel, darkLevel);
		if (notification != null) {
			this.darkLevel = darkLevel;
			hasChanged(notification);
		}
	}

	public boolean isTrackViewPortWidth() {
		return trackViewPortWidth;
	}

	public void setTrackViewPortWidth(boolean trackViewPortWidth) {
		FIBAttributeNotification<Boolean> notification = requireChange(Parameters.trackViewPortWidth, trackViewPortWidth);
		if (notification != null) {
			this.trackViewPortWidth = trackViewPortWidth;
			hasChanged(notification);
		}
	}

	public boolean isTrackViewPortHeight() {
		return trackViewPortHeight;
	}

	public void setTrackViewPortHeight(boolean trackViewPortHeight) {
		FIBAttributeNotification<Boolean> notification = requireChange(Parameters.trackViewPortHeight, trackViewPortHeight);
		if (notification != null) {
			this.trackViewPortHeight = trackViewPortHeight;
			hasChanged(notification);
		}
	}

}
