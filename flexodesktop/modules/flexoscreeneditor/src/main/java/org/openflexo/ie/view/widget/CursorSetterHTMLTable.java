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
package org.openflexo.ie.view.widget;

import java.awt.Component;
import java.awt.Cursor;
import java.awt.Point;
import java.awt.Window;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;

import javax.swing.SwingUtilities;

import org.openflexo.foundation.ie.widget.IEHTMLTableWidget;

public class CursorSetterHTMLTable extends MouseMotionAdapter {

	private static final Cursor HORIZONTAL = Cursor.getPredefinedCursor(Cursor.W_RESIZE_CURSOR);

	private Cursor previousCursor;

	private boolean ignoreLeftSide = false;
	private boolean ignoreRightSide = false;

	public CursorSetterHTMLTable(IETDWidgetView layedoutComponent, IEHTMLTableWidget htmlTable) {
		super();
		_layedoutComponent = layedoutComponent;
		_table = htmlTable;

	}

	private boolean mouseListenerAdded = false;

	/*
	 * (non-Javadoc)
	 *
	 * @see java.awt.event.MouseMotionListener#mouseMoved(java.awt.event.MouseEvent)
	 */
	@Override
	public void mouseMoved(MouseEvent e) {
		Component frame = SwingUtilities.getAncestorOfClass(Window.class, _layedoutComponent);
		if (isNearBorder(e.getPoint())) {
			if (!mouseListenerAdded) {
				previousCursor = Cursor.getDefaultCursor();
				frame.setCursor(HORIZONTAL);
				// int c = (e.getPoint().x < OFFSET ? ((IEWidgetView) _layedoutComponent).getCol() - 1 : ((IEWidgetView) _layedoutComponent)
				// .getCol());
				ColumnResizerHTMLTable resizer = ColumnResizerHTMLTable.instance(_table);
				_layedoutComponent.addMouseMotionListener(resizer);
				_layedoutComponent.addMouseListener(resizer);
				mouseListenerAdded = true;
			}
		} else {
			if (mouseListenerAdded) {
				frame.setCursor(previousCursor);
				if (ColumnResizerHTMLTable.inst != null) {
					_layedoutComponent.removeMouseMotionListener(ColumnResizerHTMLTable.inst);
					_layedoutComponent.removeMouseListener(ColumnResizerHTMLTable.inst);
					mouseListenerAdded = false;
				}
			}
		}

	}

	// private int findCol(int curX, IETableWidgetView tableView)
	// {
	// int answer = -1;
	// int curWidthTest = _borderWidth;
	// for (int i = 0; i < tableView.getColCount(); i++) {
	// curWidthTest = curWidthTest + _table.getWidthForCol(i) + hGap;
	// if (Math.abs(curX - curWidthTest) < OFFSET) {
	// return i;
	// }
	// }
	// return answer;
	// }

	public boolean isNearBorder(Point p) {
		int curX = p.x;
		return (curX < OFFSET && !ignoreLeftSide) || (Math.abs(curX - _layedoutComponent.getWidth()) < OFFSET && !ignoreRightSide);
	}

	public static int OFFSET = 3;

	private IEHTMLTableWidget _table;

	private IETDWidgetView _layedoutComponent;

	public boolean isIgnoreLeftSide() {
		return ignoreLeftSide;
	}

	public void setIgnoreLeftSide(boolean ignoreLeftSide) {
		this.ignoreLeftSide = ignoreLeftSide;
	}

	public boolean isIgnoreRightSide() {
		return ignoreRightSide;
	}

	public void setIgnoreRightSide(boolean ignoreRightSide) {
		this.ignoreRightSide = ignoreRightSide;
	}

}
