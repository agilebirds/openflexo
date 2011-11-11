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
package org.openflexo.jedit;

import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.event.MouseEvent;

import org.openflexo.jedit.TextAreaPainter.Highlight;

public class LinesHighlight implements TextAreaPainter.Highlight {

	private JEditTextArea textArea;
	private int beginLineNb;
	private int endLineNb;
	private Color bgColor = Color.RED;
	private Color fgColor = Color.BLACK;
	private Highlight next;

	public LinesHighlight() {
		super();
	}

	public LinesHighlight(int beginLineNb, int endLineNb) {
		this();
		this.beginLineNb = beginLineNb;
		this.endLineNb = endLineNb;
	}

	public LinesHighlight(int beginLineNb, int endLineNb, Color bgColor, Color fgColor) {
		this();
		this.beginLineNb = beginLineNb;
		this.endLineNb = endLineNb;
		this.bgColor = bgColor;
		this.fgColor = fgColor;
	}

	@Override
	public String getToolTipText(MouseEvent evt) {
		int y = evt.getY();
		int line = textArea.yToLine(y);
		if (line >= beginLineNb && line <= endLineNb) {
			return getToolTipText();
		}
		if (next != null && next != this)
			return next.getToolTipText(evt);
		return null;
	}

	public String getToolTipText() {
		return null;
	}

	@Override
	public void init(JEditTextArea textArea, Highlight next) {
		this.textArea = textArea;
		this.next = next;
	}

	@Override
	public void paintHighlight(Graphics gfx, int line, int y) {
		FontMetrics fm = textArea.getPainter().getFontMetrics();
		int height = fm.getHeight();
		y += fm.getLeading() + fm.getMaxDescent();
		if (line >= beginLineNb && line <= endLineNb) {
			int width = textArea.getPainter().getWidth() - 1;
			gfx.setColor(bgColor);
			gfx.fillRect(0, y, width, height);
			gfx.setColor(fgColor);
			gfx.drawLine(0, y, 0, y + height - 1);
			gfx.drawLine(width, y, width, y + height - 1);
			if (line == beginLineNb) {
				gfx.drawLine(0, y, width, y);
			}
			if (line == endLineNb) {
				gfx.drawLine(0, y + height - 1, width, y + height - 1);
			}
		}
	}

	public int getBeginLineNb() {
		return beginLineNb;
	}

	public void setBeginLineNb(int beginLineNb) {
		this.beginLineNb = beginLineNb;
		if ((textArea != null) && (textArea.getPainter() != null))
			textArea.getPainter().repaint();
	}

	public Color getBgColor() {
		return bgColor;
	}

	public void setBgColor(Color color) {
		this.bgColor = color;
		if ((textArea != null) && (textArea.getPainter() != null))
			textArea.getPainter().invalidateLineRange(beginLineNb, endLineNb);
	}

	public Color getFgColor() {
		return fgColor;
	}

	public void setFgColor(Color color) {
		this.fgColor = color;
		if ((textArea != null) && (textArea.getPainter() != null))
			textArea.getPainter().invalidateLineRange(beginLineNb, endLineNb);
	}

	public int getEndLineNb() {
		return endLineNb;
	}

	public void setEndLineNb(int endLineNb) {
		this.endLineNb = endLineNb;
		if ((textArea != null) && (textArea.getPainter() != null))
			textArea.getPainter().repaint();
	}

}
