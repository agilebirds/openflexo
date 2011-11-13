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
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionAdapter;

import org.openflexo.foundation.ie.widget.IEHTMLTableWidget;
import org.openflexo.foundation.ie.widget.IETDWidget;

public class ColumnResizerHTMLTable extends MouseMotionAdapter implements MouseListener {

	public static ColumnResizerHTMLTable inst;
	// public IEHTMLTableWidget htmltable;
	private boolean startFromEnd = false;
	private IETDWidgetView view;

	private ColumnResizerHTMLTable(IEHTMLTableWidget _htmlTable) {
		super();
		resizing = false;
		// htmltable = _htmlTable;
	}

	public static ColumnResizerHTMLTable instance(IEHTMLTableWidget _htmlTable) {
		if (inst == null) {
			inst = new ColumnResizerHTMLTable(_htmlTable);
		} else {
			inst.resizing = false;
		}
		return inst;
	}

	public void reset() {
	}

	private boolean update = false;

	/*
	 * (non-Javadoc)
	 *
	 * @see java.awt.event.MouseMotionListener#mouseDragged(java.awt.event.MouseEvent)
	 */
	@Override
	public void mouseDragged(MouseEvent arg0) {
		if (view != null)
			resizing = true;
		else
			resizing = false;
		if (update) {
			update(arg0);
			update = false;
		} else
			update = true;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see java.awt.event.MouseMotionListener#mouseMoved(java.awt.event.MouseEvent)
	 */
	@Override
	public void mouseMoved(MouseEvent arg0) {

	}

	/*
	 * (non-Javadoc)
	 *
	 * @see java.awt.event.MouseListener#mouseClicked(java.awt.event.MouseEvent)
	 */
	@Override
	public void mouseClicked(MouseEvent arg0) {

	}

	/*
	 * (non-Javadoc)
	 *
	 * @see java.awt.event.MouseListener#mouseEntered(java.awt.event.MouseEvent)
	 */
	@Override
	public void mouseEntered(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 *
	 * @see java.awt.event.MouseListener#mouseExited(java.awt.event.MouseEvent)
	 */
	@Override
	public void mouseExited(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 *
	 * @see java.awt.event.MouseListener#mousePressed(java.awt.event.MouseEvent)
	 */
	@Override
	public void mousePressed(MouseEvent arg0) {
		startX = arg0.getPoint().x;
		startFromEnd = startX > 10;
		if (arg0.getSource() instanceof IETDWidgetView)
			view = ((IETDWidgetView) arg0.getSource());
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see java.awt.event.MouseListener#mouseReleased(java.awt.event.MouseEvent)
	 */
	@Override
	public void mouseReleased(MouseEvent arg0) {
		update(arg0);
		resizing = false;
	}

	private void update(MouseEvent arg0) {
		try {
			if (resizing) {
				int delta = arg0.getPoint().x - startX;
				int newWidth = ((Component) arg0.getSource()).getWidth() + delta;
				if (newWidth < IETDWidget.MIN_WIDTH) {
					newWidth = IETDWidget.MIN_WIDTH;
				}
				view.adjustPourcentage(delta, startFromEnd);
				view.doLayout();
				view.repaint();
				if (startFromEnd)
					startX = arg0.getPoint().x;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private boolean resizing = false;

	private int startX;
}
