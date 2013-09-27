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
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Rectangle2D;
import java.util.logging.Logger;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableColumnModelEvent;
import javax.swing.event.TableColumnModelListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumn;

import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFPalette;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.util.CellRangeAddress;
import org.openflexo.swing.msct.CellSpan;
import org.openflexo.swing.msct.MultiSpanCellTable;
import org.openflexo.swing.msct.MultiSpanCellTableModel;
import org.openflexo.swing.msct.TableCellExtendedRenderer;
import org.openflexo.technologyadapter.excel.model.ExcelCell;
import org.openflexo.technologyadapter.excel.model.ExcelSheet;
import org.openflexo.view.controller.FlexoController;

/**
 * Widget allowing to edit/view a ExcelSheet.<br>
 * We use here an implementation of a MultiSpanCellTable to do it.
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

	private JTextField cellIdentifier;
	private JTextField cellValue;

	public ExcelSheetView(ExcelSheet sheet, FlexoController controller) {
		super(new BorderLayout());
		this.sheet = sheet;
		this.controller = controller;
		tableModel = new ExcelSheetTableModel();
		table = new MultiSpanCellTable(tableModel);
		table.setBackground(Color.WHITE);
		table.setShowGrid(true);
		table.setGridColor(Color.LIGHT_GRAY);
		table.setRowMargin(0);
		table.getColumnModel().setColumnMargin(0);

		for (int i = 0; i < tableModel.getColumnCount(); i++) {
			TableColumn col = table.getColumnModel().getColumn(i);
			if (i == 0) {
				col.setWidth(25);
				col.setPreferredWidth(25);
				col.setMinWidth(25);
				col.setMaxWidth(100);
				// col.setResizable(false);
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

		cellIdentifier = new JTextField(6);
		cellIdentifier.setEditable(false);
		cellIdentifier.setHorizontalAlignment(SwingConstants.CENTER);
		cellValue = new JTextField();
		cellValue.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				valueEditedForSelectedCell(cellValue.getText());
			}
		});
		/*cellValue.getDocument().addDocumentListener(new DocumentListener() {
			@Override
			public void removeUpdate(DocumentEvent e) {
				valueEditedForSelectedCell(cellValue.getText());
			}

			@Override
			public void insertUpdate(DocumentEvent e) {
				valueEditedForSelectedCell(cellValue.getText());
			}

			@Override
			public void changedUpdate(DocumentEvent e) {
				valueEditedForSelectedCell(cellValue.getText());
			}
		});*/
		JPanel topPanel = new JPanel(new BorderLayout());
		topPanel.add(cellIdentifier, BorderLayout.WEST);
		topPanel.add(cellValue, BorderLayout.CENTER);
		add(topPanel, BorderLayout.NORTH);

		table.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e) {
				selectionChanged();
			}
		});
		table.getColumnModel().addColumnModelListener(new TableColumnModelListener() {

			@Override
			public void columnSelectionChanged(ListSelectionEvent e) {
				selectionChanged();
			}

			@Override
			public void columnRemoved(TableColumnModelEvent e) {
			}

			@Override
			public void columnMoved(TableColumnModelEvent e) {
			}

			@Override
			public void columnMarginChanged(ChangeEvent e) {
			}

			@Override
			public void columnAdded(TableColumnModelEvent e) {
			}
		});

		updateRowHeights();

		validate();

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

	/**
	 * Inner class encoding a table model wrapping the sheet model.<br>
	 * Note than index of the row is used as the first column, then all columns indexes are shift of one unit
	 * 
	 * @author sylvain
	 * 
	 */
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
			ExcelCell cell = getCellAt(row, column);
			return cell.getDisplayValue();
		};

		@Override
		public void setValueAt(Object aValue, int row, int column) {
			if (column == 0)
				return;
			ExcelCell cell = getCellAt(row, column);
			setValueForCell(cell, aValue.toString());
		}

		@Override
		public boolean isCellEditable(int row, int column) {
			if (column == 0)
				return false;
			return true;
		}

		public ExcelCell getCellAt(int row, int column) {
			if (column == 0)
				return null;
			return getSheet().getCellAt(row, column - 1);
		}

		public ExcelSheet getSheet() {
			return sheet;
		}
	}

	/**
	 * This is the renderer of an excel sheet
	 * 
	 * @author sylvain
	 * 
	 */
	class ExcelSheetCellRenderer extends DefaultTableCellRenderer implements TableCellExtendedRenderer {
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

			DefaultTableCellRenderer returned = (DefaultTableCellRenderer) super.getTableCellRendererComponent(table, column == 0 ? value
					: "", isSelected, hasFocus, row, column);

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

			ExcelCell cell = tableModel.getCellAt(row, column);
			if (cell.getCell() == null) {
				// border = new EmptyCellBorder();
			} else {
				border = new CellBorder(row, column);
			}

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
				switch (style.getVerticalAlignment()) {
				case CellStyle.VERTICAL_TOP:
					returned.setVerticalAlignment(SwingConstants.TOP);
					break;
				case CellStyle.VERTICAL_BOTTOM:
					returned.setVerticalAlignment(SwingConstants.BOTTOM);
					break;
				case CellStyle.VERTICAL_CENTER:
					returned.setVerticalAlignment(SwingConstants.CENTER);
					break;
				case CellStyle.VERTICAL_JUSTIFY:
					returned.setVerticalAlignment(SwingConstants.CENTER);
					break;
				default:
					returned.setVerticalAlignment(SwingConstants.CENTER);
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
					// returned.setBackground(Color.BLUE/*UIManager.getColor("Table.focusCellBackground")*/);
				}
			} else {
				returned.setBorder(border != null ? border : noFocusBorder);
			}

			// setValue(value);
			return this;
		}

		@Override
		public void paintExtendedContents(Graphics g, int row, int column) {
			ExcelCell cell = tableModel.getCellAt(row, column);
			if (cell == null) {
				return;
			}
			if (StringUtils.isNotEmpty(cell.getDisplayValue()) && cell.getCellStyle() != null) {
				Rectangle cellBounds = table.getCellRect(row, column, true);
				Font f = g.getFont();
				Color c = g.getColor();
				Font font = getFont(cell.getCellStyle());
				Color foreground = getForegroundColor(cell.getCellStyle());
				g.setFont(font != null ? font : table.getFont());
				g.setColor(foreground != null ? foreground : table.getForeground());
				FontMetrics fm = g.getFontMetrics(getFont(cell.getCellStyle()));
				Rectangle2D stringBounds = fm.getStringBounds(cell.getDisplayValue(), g);
				int x;
				int y;
				switch (cell.getCellStyle().getAlignment()) {
				case CellStyle.ALIGN_CENTER:
					x = (int) (cellBounds.x - stringBounds.getCenterX() + cellBounds.getWidth() / 2);
					break;
				case CellStyle.ALIGN_LEFT:
					x = (int) (cellBounds.x - stringBounds.getX());
					break;
				case CellStyle.ALIGN_RIGHT:
					x = (int) (cellBounds.x - stringBounds.getX());
					break;
				default:
					x = (int) (cellBounds.x - stringBounds.getX());
				}
				switch (cell.getCellStyle().getVerticalAlignment()) {
				case CellStyle.VERTICAL_TOP:
					y = (int) (cellBounds.y - stringBounds.getY());
					break;
				case CellStyle.VERTICAL_BOTTOM:
					y = (cellBounds.y + cellBounds.height - 3);
					break;
				case CellStyle.VERTICAL_CENTER:
					y = (int) (cellBounds.y - stringBounds.getCenterY() + cellBounds.getHeight() / 2);
					break;
				case CellStyle.VERTICAL_JUSTIFY:
					y = (int) (cellBounds.y - stringBounds.getCenterY() + cellBounds.getHeight() / 2);
					break;
				default:
					y = (int) (cellBounds.y - stringBounds.getY());
				}

				g.drawString(cell.getDisplayValue(), x, y);
				g.setColor(c);
				g.setFont(f);
			}
		}

		/*
		FontMetrics fm = SwingUtilities2.getFontMetrics(label, g);

		private String layout(JLabel label, FontMetrics fm,
		        int width, int height) {
		Insets insets = label.getInsets(null);
		String text = label.getText();
		Icon icon = (label.isEnabled()) ? label.getIcon() :
		                        label.getDisabledIcon();
		Rectangle paintViewR = new Rectangle();
		paintViewR.x = insets.left;
		paintViewR.y = insets.top;
		paintViewR.width = width - (insets.left + insets.right);
		paintViewR.height = height - (insets.top + insets.bottom);
		paintIconR.x = paintIconR.y = paintIconR.width = paintIconR.height = 0;
		paintTextR.x = paintTextR.y = paintTextR.width = paintTextR.height = 0;
		return layoutCL(label, fm, text, icon, paintViewR, paintIconR,
		      paintTextR);
		}*/

		/**
		 * Inner class implementing cell border rendering.<br>
		 * A trick happen here: to avoid representing bold lines for two adjacents cells requiring a border, we represent in the rendering
		 * of the cell only the top and left borders, when necessary.<br>
		 * The rendering of right and bottom borders are necessary if and only if respectively the right and bottom cells require a left and
		 * top border.
		 * 
		 * @author sylvain
		 * 
		 */
		class CellBorder implements Border {

			private ExcelCell cell;

			public CellBorder(int row, int column) {
				this.cell = tableModel.getCellAt(row, column);
			}

			/**
			 * Paint the border<br>
			 * A trick happen here: to avoid representing bold lines for two adjacents cells requiring a border, we represent in the
			 * rendering of the cell only the top and left borders, when necessary.<br>
			 * The rendering of right and bottom borders are necessary if and only if respectively the right and bottom cells require a left
			 * and top border.
			 */
			@Override
			public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
				Color oldColor = g.getColor();

				if (hasTopBorder()) {
					g.setColor(getColor(cell.getCell().getCellStyle().getTopBorderColor()));
					g.drawLine(x, y, width - 1, y);
				}
				if (hasLeftBorder()) {
					g.setColor(getColor(cell.getCell().getCellStyle().getLeftBorderColor()));
					g.drawLine(x, y, x, height - 1);
				}
				if (hasBottomBorder()) {
					g.setColor(getColor(cell.getCell().getCellStyle().getBottomBorderColor()));
					g.drawLine(x, height - 1, width - 1, height - 1);
				}
				if (hasRightBorder()) {
					g.setColor(getColor(cell.getCell().getCellStyle().getRightBorderColor()));
					g.drawLine(width - 1, y, width - 1, height - 1);
				}

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
				return cell.hasTopBorder();
			}

			private boolean hasLeftBorder() {
				return cell.hasLeftBorder();
			}

			private boolean hasRightBorder() {
				return cell.hasRightBorder() && !cell.getNextCell().hasLeftBorder();
			}

			private boolean hasBottomBorder() {
				return cell.hasBottomBorder() && !cell.getLowerCell().hasTopBorder();
			}

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

	protected void selectionChanged() {
		ExcelCell selected = sheet.getCellAt(table.getSelectedRow(), table.getSelectedColumn() - 1);
		if (selected == null) {
			cellIdentifier.setText("");
			cellValue.setText("");
		} else {
			cellIdentifier.setText(selected.getCellIdentifier());
			cellValue.setText(selected.getDisplayCellSpecification());
		}
	}

	protected void valueEditedForSelectedCell(String value) {
		ExcelCell selected = sheet.getCellAt(table.getSelectedRow(), table.getSelectedColumn() - 1);
		if (selected == null) {
			return;
		} else {
			setValueForCell(selected, value);
		}
	}

	protected void setValueForCell(ExcelCell cell, String value) {
		cell.setCellValue(value);
		tableModel.fireTableDataChanged();
		updateRowHeights();
		table.changeSelection(cell.getRowIndex(), cell.getColumnIndex() + 1, false, false);
	}

	private void updateRowHeights() {
		for (Row row : sheet.getSheet()) {
			table.setRowHeight(row.getRowNum(), (int) row.getHeightInPoints());
		}
	}
}
