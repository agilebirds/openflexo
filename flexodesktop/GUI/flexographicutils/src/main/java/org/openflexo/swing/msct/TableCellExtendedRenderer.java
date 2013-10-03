package org.openflexo.swing.msct;

import java.awt.Graphics;

import javax.swing.table.TableCellRenderer;

public interface TableCellExtendedRenderer extends TableCellRenderer {

	// public Rectangle getExtendedBounds();

	public void paintExtendedContents(Graphics g, int row, int column);

}