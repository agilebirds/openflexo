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

import org.openflexo.model.annotations.Getter;
import org.openflexo.model.annotations.ImplementationClass;
import org.openflexo.model.annotations.ModelEntity;
import org.openflexo.model.annotations.PropertyIdentifier;
import org.openflexo.model.annotations.Setter;
import org.openflexo.model.annotations.XMLAttribute;
import org.openflexo.model.annotations.XMLElement;

@ModelEntity
@ImplementationClass(FIBPanel.FIBPanelImpl.class)
@XMLElement(xmlTag = "Panel")
public interface FIBPanel extends FIBContainer {

	public static enum Layout {
		none, flow, border, grid, box, twocols, gridbag, split, buttons;
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

	@PropertyIdentifier(type = Layout.class)
	public static final String LAYOUT_KEY = "layout";
	@PropertyIdentifier(type = Integer.class)
	public static final String H_GAP_KEY = "hGap";
	@PropertyIdentifier(type = Integer.class)
	public static final String V_GAP_KEY = "vGap";
	@PropertyIdentifier(type = Integer.class)
	public static final String COLS_KEY = "cols";
	@PropertyIdentifier(type = Integer.class)
	public static final String ROWS_KEY = "rows";
	@PropertyIdentifier(type = FlowLayoutAlignment.class)
	public static final String FLOW_ALIGNMENT_KEY = "flowAlignment";
	@PropertyIdentifier(type = BoxLayoutAxis.class)
	public static final String BOX_LAYOUT_AXIS_KEY = "boxLayoutAxis";
	@PropertyIdentifier(type = Border.class)
	public static final String BORDER_KEY = "border";
	@PropertyIdentifier(type = Color.class)
	public static final String BORDER_COLOR_KEY = "borderColor";
	@PropertyIdentifier(type = String.class)
	public static final String BORDER_TITLE_KEY = "borderTitle";
	@PropertyIdentifier(type = Integer.class)
	public static final String BORDER_TOP_KEY = "borderTop";
	@PropertyIdentifier(type = Integer.class)
	public static final String BORDER_BOTTOM_KEY = "borderBottom";
	@PropertyIdentifier(type = Integer.class)
	public static final String BORDER_LEFT_KEY = "borderLeft";
	@PropertyIdentifier(type = Integer.class)
	public static final String BORDER_RIGHT_KEY = "borderRight";
	@PropertyIdentifier(type = Font.class)
	public static final String TITLE_FONT_KEY = "titleFont";
	@PropertyIdentifier(type = int.class)
	public static final String DARK_LEVEL_KEY = "darkLevel";
	@PropertyIdentifier(type = boolean.class)
	public static final String PROTECT_CONTENT_KEY = "protectContent";
	@PropertyIdentifier(type = boolean.class)
	public static final String TRACK_VIEW_PORT_WIDTH_KEY = "trackViewPortWidth";
	@PropertyIdentifier(type = boolean.class)
	public static final String TRACK_VIEW_PORT_HEIGHT_KEY = "trackViewPortHeight";

	@Getter(value = LAYOUT_KEY)
	@XMLAttribute
	public Layout getLayout();

	@Setter(LAYOUT_KEY)
	public void setLayout(Layout layout);

	@Getter(value = H_GAP_KEY)
	@XMLAttribute
	public Integer getHGap();

	@Setter(H_GAP_KEY)
	public void setHGap(Integer hGap);

	@Getter(value = V_GAP_KEY)
	@XMLAttribute
	public Integer getVGap();

	@Setter(V_GAP_KEY)
	public void setVGap(Integer vGap);

	@Getter(value = COLS_KEY)
	@XMLAttribute
	public Integer getCols();

	@Setter(COLS_KEY)
	public void setCols(Integer cols);

	@Getter(value = ROWS_KEY)
	@XMLAttribute
	public Integer getRows();

	@Setter(ROWS_KEY)
	public void setRows(Integer rows);

	@Getter(value = FLOW_ALIGNMENT_KEY)
	@XMLAttribute
	public FlowLayoutAlignment getFlowAlignment();

	@Setter(FLOW_ALIGNMENT_KEY)
	public void setFlowAlignment(FlowLayoutAlignment flowAlignment);

	@Getter(value = BOX_LAYOUT_AXIS_KEY)
	@XMLAttribute
	public BoxLayoutAxis getBoxLayoutAxis();

	@Setter(BOX_LAYOUT_AXIS_KEY)
	public void setBoxLayoutAxis(BoxLayoutAxis boxLayoutAxis);

	@Getter(value = BORDER_KEY)
	@XMLAttribute
	public Border getBorder();

	@Setter(BORDER_KEY)
	public void setBorder(Border border);

	@Getter(value = BORDER_COLOR_KEY)
	@XMLAttribute
	public Color getBorderColor();

	@Setter(BORDER_COLOR_KEY)
	public void setBorderColor(Color borderColor);

	@Getter(value = BORDER_TITLE_KEY)
	@XMLAttribute
	public String getBorderTitle();

	@Setter(BORDER_TITLE_KEY)
	public void setBorderTitle(String borderTitle);

	@Getter(value = BORDER_TOP_KEY)
	@XMLAttribute
	public Integer getBorderTop();

	@Setter(BORDER_TOP_KEY)
	public void setBorderTop(Integer borderTop);

	@Getter(value = BORDER_BOTTOM_KEY)
	@XMLAttribute
	public Integer getBorderBottom();

	@Setter(BORDER_BOTTOM_KEY)
	public void setBorderBottom(Integer borderBottom);

	@Getter(value = BORDER_LEFT_KEY)
	@XMLAttribute
	public Integer getBorderLeft();

	@Setter(BORDER_LEFT_KEY)
	public void setBorderLeft(Integer borderLeft);

	@Getter(value = BORDER_RIGHT_KEY)
	@XMLAttribute
	public Integer getBorderRight();

	@Setter(BORDER_RIGHT_KEY)
	public void setBorderRight(Integer borderRight);

	@Getter(value = TITLE_FONT_KEY)
	@XMLAttribute
	public Font getTitleFont();

	@Setter(TITLE_FONT_KEY)
	public void setTitleFont(Font titleFont);

	@Getter(value = DARK_LEVEL_KEY, defaultValue = "0")
	@XMLAttribute
	public int getDarkLevel();

	@Setter(DARK_LEVEL_KEY)
	public void setDarkLevel(int darkLevel);

	@Getter(value = PROTECT_CONTENT_KEY, defaultValue = "false")
	@XMLAttribute
	public boolean getProtectContent();

	@Setter(PROTECT_CONTENT_KEY)
	public void setProtectContent(boolean protectContent);

	@Getter(value = TRACK_VIEW_PORT_WIDTH_KEY, defaultValue = "false")
	@XMLAttribute
	public boolean isTrackViewPortWidth();

	@Setter(TRACK_VIEW_PORT_WIDTH_KEY)
	public void setTrackViewPortWidth(boolean trackViewPortWidth);

	@Getter(value = TRACK_VIEW_PORT_HEIGHT_KEY, defaultValue = "false")
	@XMLAttribute
	public boolean isTrackViewPortHeight();

	@Setter(TRACK_VIEW_PORT_HEIGHT_KEY)
	public void setTrackViewPortHeight(boolean trackViewPortHeight);

	public static abstract class FIBPanelImpl extends FIBContainerImpl implements FIBPanel {

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

		public FIBPanelImpl() {
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
			FIBPropertyNotification<Layout> notification = requireChange(LAYOUT_KEY, layout);
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
				case buttons:
					if (hGap == null) {
						hGap = 5;
					}
					if (vGap == null) {
						vGap = 5;
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

		@Override
		public Border getBorder() {
			return border;
		}

		@Override
		public void setBorder(Border border) {
			FIBPropertyNotification<Border> notification = requireChange(BORDER_KEY, border);
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

		@Override
		public Integer getHGap() {
			return hGap;
		}

		@Override
		public void setHGap(Integer hGap) {
			FIBPropertyNotification<Integer> notification = requireChange(H_GAP_KEY, hGap);
			if (notification != null) {
				this.hGap = hGap;
				hasChanged(notification);
			}
		}

		@Override
		public Integer getVGap() {
			return vGap;
		}

		@Override
		public void setVGap(Integer vGap) {
			FIBPropertyNotification<Integer> notification = requireChange(V_GAP_KEY, vGap);
			if (notification != null) {
				this.vGap = vGap;
				hasChanged(notification);
			}
		}

		@Override
		public FlowLayoutAlignment getFlowAlignment() {
			return flowAlignment;
		}

		@Override
		public void setFlowAlignment(FlowLayoutAlignment flowAlignment) {
			FIBPropertyNotification<FlowLayoutAlignment> notification = requireChange(FLOW_ALIGNMENT_KEY, flowAlignment);
			if (notification != null) {
				this.flowAlignment = flowAlignment;
				hasChanged(notification);
			}
		}

		@Override
		public Integer getCols() {
			return cols;
		}

		@Override
		public void setCols(Integer cols) {
			// logger.info("setCols with "+cols);
			FIBPropertyNotification<Integer> notification = requireChange(COLS_KEY, cols);
			if (notification != null) {
				this.cols = cols;
				hasChanged(notification);
			}
		}

		@Override
		public Integer getRows() {
			return rows;
		}

		@Override
		public void setRows(Integer rows) {
			// logger.info("setRows with "+rows);
			FIBPropertyNotification<Integer> notification = requireChange(ROWS_KEY, rows);
			if (notification != null) {
				this.rows = rows;
				hasChanged(notification);
			}
		}

		@Override
		public Color getBorderColor() {
			return borderColor;
		}

		@Override
		public void setBorderColor(Color borderColor) {
			FIBPropertyNotification<Color> notification = requireChange(BORDER_COLOR_KEY, borderColor);
			if (notification != null) {
				this.borderColor = borderColor;
				hasChanged(notification);
			}
		}

		@Override
		public String getBorderTitle() {
			return borderTitle;
		}

		@Override
		public void setBorderTitle(String borderTitle) {
			FIBPropertyNotification<String> notification = requireChange(BORDER_TITLE_KEY, borderTitle);
			if (notification != null) {
				this.borderTitle = borderTitle;
				hasChanged(notification);
			}
		}

		@Override
		public BoxLayoutAxis getBoxLayoutAxis() {
			return boxLayoutAxis;
		}

		@Override
		public void setBoxLayoutAxis(BoxLayoutAxis boxLayoutAxis) {
			FIBPropertyNotification<BoxLayoutAxis> notification = requireChange(BOX_LAYOUT_AXIS_KEY, boxLayoutAxis);
			if (notification != null) {
				this.boxLayoutAxis = boxLayoutAxis;
				hasChanged(notification);
			}
		}

		@Override
		public boolean getProtectContent() {
			return protectContent;
		}

		@Override
		public void setProtectContent(boolean protectContent) {
			FIBPropertyNotification<Boolean> notification = requireChange(PROTECT_CONTENT_KEY, protectContent);
			if (notification != null) {
				this.protectContent = protectContent;
				hasChanged(notification);
			}

		}

		@Override
		public Integer getBorderTop() {
			return borderTop;
		}

		@Override
		public void setBorderTop(Integer borderTop) {
			FIBPropertyNotification<Integer> notification = requireChange(BORDER_TOP_KEY, borderTop);
			if (notification != null) {
				this.borderTop = borderTop;
				hasChanged(notification);
			}
		}

		@Override
		public Integer getBorderBottom() {
			return borderBottom;
		}

		@Override
		public void setBorderBottom(Integer borderBottom) {
			FIBPropertyNotification<Integer> notification = requireChange(BORDER_BOTTOM_KEY, borderBottom);
			if (notification != null) {
				this.borderBottom = borderBottom;
				hasChanged(notification);
			}
		}

		@Override
		public Integer getBorderLeft() {
			return borderLeft;
		}

		@Override
		public void setBorderLeft(Integer borderLeft) {
			FIBPropertyNotification<Integer> notification = requireChange(BORDER_LEFT_KEY, borderLeft);
			if (notification != null) {
				this.borderLeft = borderLeft;
				hasChanged(notification);
			}
		}

		@Override
		public Integer getBorderRight() {
			return borderRight;
		}

		@Override
		public void setBorderRight(Integer borderRight) {
			FIBPropertyNotification<Integer> notification = requireChange(BORDER_RIGHT_KEY, borderRight);
			if (notification != null) {
				this.borderRight = borderRight;
				hasChanged(notification);
			}
		}

		@Override
		public Font getTitleFont() {
			if (titleFont == null) {
				return retrieveValidFont();
			}
			return titleFont;
		}

		@Override
		public void setTitleFont(Font titleFont) {
			FIBPropertyNotification<Font> notification = requireChange(TITLE_FONT_KEY, titleFont);
			if (notification != null) {
				this.titleFont = titleFont;
				hasChanged(notification);
			}
		}

		@Override
		public int getDarkLevel() {
			return darkLevel;
		}

		@Override
		public void setDarkLevel(int darkLevel) {
			FIBPropertyNotification<Integer> notification = requireChange(DARK_LEVEL_KEY, darkLevel);
			if (notification != null) {
				this.darkLevel = darkLevel;
				hasChanged(notification);
			}
		}

		@Override
		public boolean isTrackViewPortWidth() {
			return trackViewPortWidth;
		}

		@Override
		public void setTrackViewPortWidth(boolean trackViewPortWidth) {
			FIBPropertyNotification<Boolean> notification = requireChange(TRACK_VIEW_PORT_WIDTH_KEY, trackViewPortWidth);
			if (notification != null) {
				this.trackViewPortWidth = trackViewPortWidth;
				hasChanged(notification);
			}
		}

		@Override
		public boolean isTrackViewPortHeight() {
			return trackViewPortHeight;
		}

		@Override
		public void setTrackViewPortHeight(boolean trackViewPortHeight) {
			FIBPropertyNotification<Boolean> notification = requireChange(TRACK_VIEW_PORT_HEIGHT_KEY, trackViewPortHeight);
			if (notification != null) {
				this.trackViewPortHeight = trackViewPortHeight;
				hasChanged(notification);
			}
		}

	}
}
