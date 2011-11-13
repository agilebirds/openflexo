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
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Insets;

import javax.swing.SwingConstants;

/**
 * @author gpolet
 * 
 */
public class IETDFlowLayout extends FlowLayout {

	/**
     * 
     */
	public IETDFlowLayout() {
	}

	/**
	 * @param align
	 */
	public IETDFlowLayout(int align) {
		super(align);
	}

	/**
	 * @param align
	 * @param hgap
	 * @param vgap
	 */
	public IETDFlowLayout(int align, int hgap, int vgap, int vAlignement) {
		super(align, hgap, vgap);
		_vAlign = vAlignement;
	}

	/**
	 * Overrides preferredLayoutSize
	 * 
	 * @see java.awt.FlowLayout#preferredLayoutSize(java.awt.Container)
	 */
	@Override
	public Dimension preferredLayoutSize(Container target) {
		Dimension dim = new Dimension(0, 0);
		int nmembers = target.getComponentCount();
		Insets insets = target.getInsets();
		Dimension targetMaxSize = target.getMaximumSize();
		int maxWidth = targetMaxSize.width - (insets.left + insets.right + getHgap() * 2);
		if (maxWidth < 0)
			maxWidth = 0;
		int currentWidth = 0;
		int currentHeight = 0;
		boolean first = true;
		int width = 0;
		for (int i = 0; i < nmembers; i++) {
			Component m = target.getComponent(i);
			if (m.isVisible()) {
				Dimension d = m.getPreferredSize();
				if (currentWidth == 0 || currentWidth + d.width <= maxWidth) {
					// We add the component to the current line
					currentWidth += d.width;
					currentHeight = Math.max(currentHeight, d.height);
					// The max height of all components of this line is the
					// height of this line
					if (first)
						first = false;
					else
						currentWidth += getHgap();
				} else {// We add the component to a new line
					dim.height += currentHeight + getVgap();
					// We add the height of the previous line and the vertical
					// gap to the total height of the container
					width = Math.max(currentWidth, width);
					currentWidth = d.width;
					currentHeight = d.height;// The height of the new line
				}
			}
		}
		width = Math.max(currentWidth, width);
		maxWidth = Math.min(width + getHgap(), targetMaxSize.width);
		dim.height += currentHeight + getVgap() * 2 + insets.top + insets.bottom;
		dim.width = maxWidth + 2 * getHgap();
		return dim;
	}

	public int getVerticalAlignement() {
		return _vAlign;
	}

	public void setVerticalAlignement(int v) {
		_vAlign = v;
	}

	private int _vAlign = SwingConstants.TOP;

	@Override
	public void layoutContainer(Container target) {
		synchronized (target.getTreeLock()) {
			Insets insets = target.getInsets();
			int maxwidth = target.getWidth() - (insets.left + insets.right + getHgap() * 2);
			if (maxwidth < 0)
				maxwidth = 0;
			int nmembers = target.getComponentCount();
			int x = 0, y = getVgap();
			int rowh = 0, start = 0;
			int offset = target.getSize().height - preferredLayoutSize(target).height + 4;
			boolean ltr = target.getComponentOrientation().isLeftToRight();

			for (int i = 0; i < nmembers; i++) {
				Component m = target.getComponent(i);
				if (m.isVisible()) {
					Dimension d = m.getPreferredSize();
					m.setSize(d.width, d.height);

					if ((x == 0) || ((x + d.width) <= maxwidth)) {
						if (x > 0) {
							x += getHgap();
						}
						x += d.width;
						rowh = Math.max(rowh, d.height);
					} else {
						moveComponents(target, insets.left + getHgap(), y, maxwidth - x, rowh, start, i, ltr, offset);
						x = d.width;
						y += getVgap() + rowh;
						rowh = d.height;
						start = i;
					}
				}
			}
			moveComponents(target, insets.left + getHgap(), y, maxwidth - x, rowh, start, nmembers, ltr, offset);
		}
	}

	private void moveComponents(Container target, int x, int y, int width, int height, int rowStart, int rowEnd, boolean ltr, int offset) {

		synchronized (target.getTreeLock()) {
			if (x < 0)
				x = getHgap();
			if (y < 0)
				y = getVgap();
			switch (getVerticalAlignement()) {
			case SwingConstants.TOP:
				break;
			case SwingConstants.CENTER:
				y += offset / 2;
				break;
			case SwingConstants.BOTTOM:
				y += offset;
				break;
			}

			switch (getAlignment()) {
			case LEFT:
				x += ltr ? 0 : width;
				break;
			case CENTER:
				x += width / 2;
				break;
			case RIGHT:
				x += ltr ? width : 0;
				break;
			case LEADING:
				break;
			case TRAILING:
				x += width;
				break;
			}
			for (int i = rowStart; i < rowEnd; i++) {
				Component m = target.getComponent(i);
				if (m.isVisible()) {
					if (ltr) {
						m.setLocation(x, y + (height - m.getHeight()) / 2);
					} else {
						m.setLocation(target.getWidth() - x - m.getWidth(), y + (height - m.getHeight()) / 2);
					}
					x += m.getWidth() + getHgap();
				}
			}
		}
	}
}
