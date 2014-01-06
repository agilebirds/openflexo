package org.openflexo.swing.msct;

/**
 * @version 1.0 11/22/98
 */

public interface CellSpan {
	public final int ROW = 0;
	public final int COLUMN = 1;

	public int[] getSpan(int row, int column);

	public void setSpan(int[] span, int row, int column);

	public boolean isVisible(int row, int column);

	public void combine(int[] rows, int[] columns);

	public void split(int row, int column);

}