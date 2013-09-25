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
package org.openflexo.technologyadapter.excel.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Insets;
import java.util.logging.Logger;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;

import org.apache.poi.hssf.usermodel.HSSFPalette;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.util.CellRangeAddress;
import org.openflexo.swing.msct.CellSpan;
import org.openflexo.swing.msct.MultiSpanCellTable;
import org.openflexo.swing.msct.MultiSpanCellTableModel;
import org.openflexo.technologyadapter.excel.model.ExcelRow;
import org.openflexo.technologyadapter.excel.model.ExcelSheet;
import org.openflexo.view.controller.FlexoController;

/**
 * Widget allowing to edit/view a ExcelSheet.<br>
 * 
 * @author sguerin
 * 
 */
@SuppressWarnings("serial")
public class ExcelSheetView extends JPanel {
	static final Logger logger = Logger.getLogger(ExcelSheetView.class.getPackage().getName());

	private ExcelSheet sheet;
	private FlexoController controller;

	private ExcelSheetTableModel tableModel;
	private MultiSpanCellTable table;

	public ExcelSheetView(ExcelSheet sheet, FlexoController controller) {
		super(new BorderLayout());
		this.sheet = sheet;
		this.controller = controller;
		tableModel = new ExcelSheetTableModel();
		table = new MultiSpanCellTable(tableModel);
		table.setBackground(Color.WHITE);
		table.setShowGrid(true);
		table.setRowMargin(-1);
		table.getColumnModel().setColumnMargin(-1);

		// table.setShowHorizontalLines(true);
		// table.setShowVerticalLines(true);
		for (int i = 0; i < tableModel.getColumnCount(); i++) {
			TableColumn col = table.getColumnModel().getColumn(i);
			if (i == 0) {
				col.setWidth(25);
				col.setPreferredWidth(25);
				col.setMinWidth(25);
				col.setMaxWidth(25);
				col.setResizable(false);
				col.setHeaderValue(null);
			} else {
				col.setWidth(sheet.getSheet().getColumnWidth(i - 1) / 40);
				col.setPreferredWidth(sheet.getSheet().getColumnWidth(i - 1) / 40);
				col.setHeaderValue("" + Character.toChars(i + 64)[0]);
			}
		}
		table.setDefaultRenderer(Object.class, new ExcelSheetCellRenderer());
		table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		add(new JScrollPane(table), BorderLayout.CENTER);
		validate();

		for (Row row : sheet.getSheet()) {
			table.setRowHeight(row.getRowNum(), (int) row.getHeightInPoints());
		}

		/*for (Object p : sheet.getSheet().getWorkbook().getAllPictures()) {
			System.out.println("Picture: " + p);
		}

		System.out.println("class = " + sheet.getSheet().getClass());

		if (sheet.getSheet() instanceof HSSFSheet) {

			List<HSSFShape> shapes = ((HSSFSheet) sheet.getSheet()).getDrawingPatriarch().getChildren();
			System.out.println("Prout=" + shapes);
			for (int i = 0; i < shapes.size(); i++) {
				System.out.println("hop avec " + shapes.get(i));
				if (shapes.get(i) instanceof HSSFPicture) {
					HSSFPicture pic = (HSSFPicture) shapes.get(i);
					HSSFPictureData picdata = ((HSSFSheet) sheet.getSheet()).getWorkbook().getAllPictures().get(pic.getPictureIndex());

					System.out.println("New picture found : " + pic);
					System.out.println("Anchor : " + pic.getAnchor());
					System.out.println("file extension " + picdata.suggestFileExtension());

					// int pictureIndex = this.newSheet.getWorkbook().addPicture( picdata.getData(), picdata.getFormat());

					// this.newSheet.createDrawingPatriarch().createPicture((HSSFClientAnchor)pic.getAnchor()r, pictureIndex);

				}

			}
		}*/
	}

	public ExcelSheet getSheet() {
		return sheet;
	}

	class ExcelSheetTableModel extends MultiSpanCellTableModel {

		public ExcelSheetTableModel() {
			super(sheet.getExcelRows().size(), sheet.getMaxColNumber() + 1 /*+ 16*/);
			for (int i = 0; i < sheet.getSheet().getNumMergedRegions(); i++) {
				CellRangeAddress cellRange = sheet.getSheet().getMergedRegion(i);
				int[] rows = new int[cellRange.getLastRow() - cellRange.getFirstRow() + 1];
				for (int index = cellRange.getFirstRow(); index <= cellRange.getLastRow(); index++) {
					rows[index - cellRange.getFirstRow()] = index;
				}
				int[] columns = new int[cellRange.getLastColumn() - cellRange.getFirstColumn() + 1];
				for (int index = cellRange.getFirstColumn(); index <= cellRange.getLastColumn(); index++) {
					columns[index - cellRange.getFirstColumn()] = index + 1;
				}
				((CellSpan) getCellAttribute()).combine(rows, columns);
			}
		}

		@Override
		public Object getValueAt(int row, int column) {
			if (column == 0)
				return row + 1;
			Cell cell = getCellAt(row, column);
			if (cell == null) {
				return "";
			}
			switch (cell.getCellType()) {
			case Cell.CELL_TYPE_BLANK:
				return "";
			case Cell.CELL_TYPE_NUMERIC:
				return cell.getNumericCellValue();
			case Cell.CELL_TYPE_STRING:
				return cell.getStringCellValue();
			case Cell.CELL_TYPE_FORMULA:
				return cell.getNumericCellValue();
			case Cell.CELL_TYPE_BOOLEAN:
				return cell.getBooleanCellValue();
			case Cell.CELL_TYPE_ERROR:
				return "!ERROR:" + cell.getErrorCellValue();
			}
			return cell;
		};

		public Cell getCellAt(int row, int column) {
			if (column == 0)
				return null;
			if (row >= 0 && row < getSheet().getExcelRows().size()) {
				ExcelRow excelRow = getSheet().getExcelRows().get(row);
				if (excelRow.getRow() == null) {
					return null;
				}
				return excelRow.getRow().getCell(column - 1);
			}
			return null;
		}

		public ExcelSheet getSheet() {
			return sheet;
		}
	}

	class ExcelSheetCellRenderer extends DefaultTableCellRenderer implements TableCellRenderer {
		protected Border noFocusBorder;

		public ExcelSheetCellRenderer() {
			noFocusBorder = new EmptyBorder(0, 0, 0, 0);// new LineBorder(Color.LIGHT_GRAY, 1); // new EmptyBorder(1, 2, 1, 2);
			setOpaque(true);
			setBorder(noFocusBorder);
		}

		@Override
		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
			Color foreground = null;
			Color background = null;

			Border border = null;
			Font font = null;

			DefaultTableCellRenderer returned = (DefaultTableCellRenderer) super.getTableCellRendererComponent(table, value, isSelected,
					hasFocus, row, column);

			if (column == 0) {

				JTableHeader header = table.getTableHeader();

				if (header != null) {
					returned.setHorizontalAlignment(SwingConstants.CENTER);
					returned.setForeground(header.getForeground());
					returned.setBackground(header.getBackground());
					returned.setFont(header.getFont());
				}
				if (isSelected) {
					returned.setFont(getFont().deriveFont(Font.BOLD));
				}
				returned.setBorder(UIManager.getBorder("TableHeader.cellBorder"));
				return returned;
			}

			Cell cell = tableModel.getCellAt(row, column);
			if (cell == null) {
				// border = new EmptyCellBorder();
			} else {
				border = new CellBorder(row, column);
			}

			if (cell != null) {
				CellStyle style = cell.getCellStyle();
				if (style != null) {
					background = getBackgroundColor(style);
					foreground = getForegroundColor(style);
					font = getFont(style);

					switch (style.getAlignment()) {
					case CellStyle.ALIGN_CENTER:
						returned.setHorizontalAlignment(SwingConstants.CENTER);
						break;
					case CellStyle.ALIGN_LEFT:
						returned.setHorizontalAlignment(SwingConstants.LEFT);
						break;
					case CellStyle.ALIGN_RIGHT:
						returned.setHorizontalAlignment(SwingConstants.RIGHT);
						break;
					default:
						returned.setHorizontalAlignment(SwingConstants.LEFT);
					}
				}
			}
			if (isSelected) {
				returned.setForeground((foreground != null) ? foreground : table.getSelectionForeground());
				returned.setBackground(table.getSelectionBackground());
			} else {
				returned.setForeground((foreground != null) ? foreground : table.getForeground());
				returned.setBackground((background != null) ? background : table.getBackground());
			}
			returned.setFont((font != null) ? font : table.getFont());

			if (hasFocus) {
				returned.setBorder(UIManager.getBorder("Table.focusCellHighlightBorder"));
				if (table.isCellEditable(row, column)) {
					returned.setForeground((foreground != null) ? foreground : UIManager.getColor("Table.focusCellForeground"));
					returned.setBackground(Color.BLUE/*UIManager.getColor("Table.focusCellBackground")*/);
				}
			} else {
				returned.setBorder(border != null ? border : noFocusBorder);
			}

			setValue(value);
			return this;
		}

		/*class EmptyCellBorder implements Border {
			@Override
			public Insets getBorderInsets(Component c) {
				return new Insets(1, 1, 1, 1);
			}

			@Override
			public boolean isBorderOpaque() {
				return false;
			}

			@Override
			public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
				Color oldColor = g.getColor();
				g.setColor(Color.LIGHT_GRAY);
				g.drawLine(x, y, width - 1, y);
				g.drawLine(x, y, x, height - 1);
				g.drawLine(x, height - 1, width - 1, height - 1);
				g.drawLine(width - 1, y, width - 1, height - 1);
				g.setColor(oldColor);
			}

		}*/

		class CellBorder implements Border {

			private Cell cell;
			private Cell bottomCell;
			private Cell rightCell;

			public CellBorder(int row, int column) {
				this.cell = tableModel.getCellAt(row, column);
				bottomCell = tableModel.getCellAt(row + 1, column);
				rightCell = tableModel.getCellAt(row, column + 1);
			}

			@Override
			public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
				Color oldColor = g.getColor();

				if (hasTopBorder()) {
					g.setColor(getColor(cell.getCellStyle().getTopBorderColor()));
					g.drawLine(x, y, width - 1, y);
				}
				if (hasLeftBorder()) {
					g.setColor(getColor(cell.getCellStyle().getLeftBorderColor()));
					g.drawLine(x, y, x, height - 1);
				}
				if (hasBottomBorder()) {
					g.setColor(getColor(cell.getCellStyle().getBottomBorderColor()));
					g.drawLine(x, height - 2, width - 1, height - 2);
				}
				if (hasRightBorder()) {
					g.setColor(getColor(cell.getCellStyle().getRightBorderColor()));
					g.drawLine(width - 2, y, width - 2, height - 1);
				}

				// g.setColor(lineColor);
				// g.drawRect(x, y, width - 1, height - 1);
				g.setColor(oldColor);
			}

			/**
			 * Returns the insets of the border.
			 * 
			 * @param c
			 *            the component for which this border insets value applies
			 */
			@Override
			public Insets getBorderInsets(Component c) {
				return new Insets(hasTopBorder() ? 1 : 0, hasLeftBorder() ? 1 : 0, hasBottomBorder() ? 1 : 0, hasRightBorder() ? 1 : 0);
			}

			private boolean hasTopBorder() {
				return (cell != null && cell.getCellStyle().getBorderTop() != CellStyle.BORDER_NONE)
				/*|| (topCell != null && topCell.getCellStyle().getBorderBottom() != CellStyle.BORDER_NONE)*/;
			}

			private boolean hasLeftBorder() {
				return (cell != null && cell.getCellStyle().getBorderLeft() != CellStyle.BORDER_NONE)
				/*|| (leftCell != null && leftCell.getCellStyle().getBorderRight() != CellStyle.BORDER_NONE)*/;
			}

			private boolean hasRightBorder() {
				return (cell != null && cell.getCellStyle().getBorderRight() != CellStyle.BORDER_NONE)
						&& (rightCell == null || rightCell.getCellStyle().getBorderLeft() == CellStyle.BORDER_NONE);
			}

			private boolean hasBottomBorder() {
				return (cell != null && cell.getCellStyle().getBorderBottom() != CellStyle.BORDER_NONE)
						&& (bottomCell == null || bottomCell.getCellStyle().getBorderTop() == CellStyle.BORDER_NONE);
			}

			/*public Insets getBorderInsets(Component c, Insets insets) {
				insets.left = hasLeftBorder() ? 1 : 0;
				insets.top = hasTopBorder() ? 1 : 0;
				insets.right = hasRightBorder() ? 1 : 0;
				insets.bottom = hasBottomBorder() ? 1 : 0;
				return insets;
			}*/

			@Override
			public boolean isBorderOpaque() {
				return false;
			}

		}

		@Override
		protected void setValue(Object value) {
			setText((value == null) ? "" : value.toString());
		}

		protected Font getFont(CellStyle cellStyle) {
			org.apache.poi.ss.usermodel.Font poiFont = sheet.getWorkbook().getWorkbook().getFontAt(cellStyle.getFontIndex());
			int fontStyle = Font.PLAIN;
			if (poiFont.getItalic()) {
				fontStyle = Font.PLAIN;
			} else if (poiFont.getBoldweight() == org.apache.poi.ss.usermodel.Font.BOLDWEIGHT_BOLD) {
				fontStyle = Font.BOLD;
			}
			return new Font(poiFont.getFontName(), fontStyle, poiFont.getFontHeightInPoints());
		}

		protected Color getForegroundColor(CellStyle cellStyle) {
			org.apache.poi.ss.usermodel.Font poiFont = sheet.getWorkbook().getWorkbook().getFontAt(cellStyle.getFontIndex());
			return getColor(poiFont.getColor());
		}

		protected Color getBackgroundColor(CellStyle cellStyle) {
			Color returned = getColor(cellStyle.getFillForegroundColor());
			// Hack: don't know why
			if (cellStyle.getFillBackgroundColor() == 64) {
				return null;
			}
			return returned;
		}

		private Color getColor(int colorIdx) {
			HSSFPalette palette = ((HSSFWorkbook) sheet.getWorkbook().getWorkbook()).getCustomPalette();
			HSSFColor color = palette.getColor(colorIdx);
			if (color == null) {
				return null;
			}
			short[] rgb = color.getTriplet();
			short red = rgb[0];
			short green = rgb[1];
			short blue = rgb[2];
			return new Color(red, green, blue);
		}
	}

}
