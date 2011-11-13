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
package org.openflexo.utils;

import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JComponent;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;

import org.openflexo.logging.FlexoLogger;

/**
 * @author gpolet
 * 
 */
public class FlexoAutoScroll {
	private static final Logger logger = FlexoLogger.getLogger(FlexoAutoScroll.class.getPackage().getName());

	/**
	 * 
	 * @param scrollable
	 *            - a component contained in a JScrollPane
	 * @param p
	 * @param margin
	 *            - the width of your insets where to scroll (imagine a border of that width all around your component. Whenever the mouse
	 *            enters that border, it will start scrolling
	 */
	public static void autoscroll(JComponent scrollable, Point p, int margin) {
		JScrollPane scroll = (JScrollPane) SwingUtilities.getAncestorOfClass(JScrollPane.class, scrollable);
		if (scroll == null) {
			if (logger.isLoggable(Level.WARNING))
				logger.warning("Not inside a scroll pane, cannot scroll!");
			return;
		}
		Rectangle visible = scrollable.getVisibleRect();
		p.x -= visible.x;
		p.y -= visible.y;
		Rectangle inner = scrollable.getParent().getBounds();
		inner.x += margin;
		inner.y += margin;
		inner.height -= (2 * margin);
		inner.width -= (2 * margin);
		if (p.x < inner.x) {// Move Left
			JScrollBar bar = scroll.getHorizontalScrollBar();
			if (bar != null) {
				if (bar.getValue() > bar.getMinimum()) {
					bar.setValue(bar.getValue() - bar.getUnitIncrement(-1));
				}
			}
		} else if (p.x > inner.x + inner.width) { // Move right
			JScrollBar bar = scroll.getHorizontalScrollBar();
			if (bar != null) {
				if (bar.getValue() < bar.getMaximum()) {
					bar.setValue(bar.getValue() + bar.getUnitIncrement(1));
				}
			}
		}
		if (p.y < inner.y) { // Move up
			JScrollBar bar = scroll.getVerticalScrollBar();
			if (bar != null) {
				if (bar.getValue() > bar.getMinimum()) {
					bar.setValue(bar.getValue() - bar.getUnitIncrement(-1));
				}
			}
		} else if (p.y > inner.y + inner.height) { // Move down
			JScrollBar bar = scroll.getVerticalScrollBar();
			if (bar != null) {
				if (bar.getValue() < bar.getMaximum()) {
					bar.setValue(bar.getValue() + bar.getUnitIncrement(1));
				}
			}
		}
	}

	public static Insets getAutoscrollInsets(JComponent scrollable, int margin) {
		Rectangle outer = scrollable.getBounds();
		Rectangle inner = scrollable.getParent().getBounds();
		return new Insets(inner.y - outer.y + margin, inner.x - outer.x + margin, outer.height - inner.height - inner.y + outer.y + margin,
				outer.width - inner.width - inner.x + outer.x + margin);
	}
}
