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
package org.openflexo.swing.merge;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Rectangle;
import java.util.Hashtable;
import java.util.Vector;

import javax.swing.JScrollBar;
import javax.swing.JTextArea;
import javax.swing.JViewport;
import javax.swing.SwingUtilities;

import org.openflexo.diff.DiffSource;
import org.openflexo.diff.DiffSource.MergeToken;
import org.openflexo.diff.merge.IMerge;
import org.openflexo.diff.merge.MergeChange;
import org.openflexo.jedit.JEditTextAreaWithHighlights;
import org.openflexo.jedit.TokenMarker;
import org.openflexo.toolbox.FontCst;
import org.openflexo.toolbox.TokenMarkerStyle;

public class MergeTextArea extends JEditTextAreaWithHighlights {
	private MergeChange selectedChange;
	private Hashtable<MergeChange, MergeHighlight> highlights;

	public enum Side {
		Right, Left, Merge
	};

	private Side side;
	private IMerge _merge;

	private Line[] text;
	private String globalText;
	private String linesTAText;

	private JTextArea linesTA;

	protected class Line {
		int lineNb;
		boolean isExtraLine;
		String lineText;

		protected Line(String lineText, int lineNb) {
			isExtraLine = false;
			this.lineText = lineText;
			this.lineNb = lineNb;
		}

		protected Line(int lineNb) {
			isExtraLine = true;
			this.lineNb = lineNb;
		}

		protected String getStringValue() {
			if (isExtraLine) {
				return "";
			} else {
				return lineText;
			}
		}

		protected boolean isExtraLine() {
			return isExtraLine;
		}

	}

	private int maxCols = 40;

	private String buildText(MergeToken[] someText) {
		Vector<Line> lines = new Vector<Line>();
		for (int i = 0; i < someText.length; i++) {
			String line = someText[i].getToken();
			lines.add(new Line(line, i));
			if (line.length() > maxCols) {
				maxCols = line.length();
			}
		}

		for (MergeChange change : _merge.getChanges()) {
			int startIndex = isRight() ? change.getFirst2() : change.getFirst0();
			int endIndex = isRight() ? change.getLast2() : change.getLast0();
			int oppositeStartIndex = isLeft() ? change.getFirst2() : change.getFirst0();
			int oppositeEndIndex = isLeft() ? change.getLast2() : change.getLast0();
			int range = endIndex - startIndex;
			int oppositeRange = oppositeEndIndex - oppositeStartIndex;
			if (range != oppositeRange) {
				int indexOfLastLine = getIndexOfLineNb(lines, endIndex);
				for (int i = range; i < oppositeRange; i++) {
					lines.insertElementAt(new Line(endIndex), indexOfLastLine + 1);
				}
			}
		}

		text = lines.toArray(new Line[lines.size()]);
		StringBuffer returned = new StringBuffer();
		StringBuffer linesTASB = new StringBuffer();
		for (Line line : text) {
			if (!line.isExtraLine) {
				if (line.lineNb < 10) {
					linesTASB.append("   " + line.lineNb + " ");
				} else if (line.lineNb < 100) {
					linesTASB.append("  " + line.lineNb + " ");
				} else if (line.lineNb < 1000) {
					linesTASB.append(" " + line.lineNb + " ");
				} else {
					linesTASB.append("" + line.lineNb);
				}
				returned.append(line.getStringValue());
			}
			returned.append("\n");
			linesTASB.append("\n");
		}
		linesTAText = linesTASB.toString();
		return returned.toString();
	}

	private DiffSource _source;

	public MergeTextArea(DiffSource source, IMerge merge, Side side, TokenMarkerStyle style) {
		super();
		disableDefaultMouseWheelListener();
		setTokenMarker(TokenMarker.makeTokenMarker(style));
		setPreferredSize(new Dimension(10, 10));
		this.side = side;
		this._merge = merge;
		this._source = source;
		setFont(FontCst.TEXT_FONT);
		globalText = buildText(source.getTextTokens());
		setText(globalText);
		setColumns(maxCols);
		setEditable(false);

		_changes = new Hashtable<MergeChange, ChangeBounds>();

		// if (MergeReport.getChanges().size() > 0) {
		highlights = new Hashtable<MergeChange, MergeHighlight>();
		for (MergeChange change : _merge.getChanges()) {
			highlights.put(change, makeHighlightForChange(change));
		}
		// }
		int cols;
		if (text.length < 10) {
			cols = 2;
		} else if (text.length < 100) {
			cols = 3;
		} else if (text.length < 1000) {
			cols = 4;
		} else {
			cols = 5;
		}
		linesTA = new JTextArea(text.length, cols);
		linesTA.setEditable(false);
		linesTA.setFont(new Font("Verdana", Font.PLAIN, 11));
		linesTA.setBackground(null);
		linesTA.setText(linesTAText);
		linesTA.setFont(FontCst.TEXT_FONT.getStyle() != Font.PLAIN ? FontCst.TEXT_FONT.deriveFont(Font.PLAIN) : FontCst.TEXT_FONT);
		if (side != Side.Merge) {
			remove(vertical);
			remove(horizontal);
		}

	}

	/**
	 * Overrides setFont
	 * 
	 * @see org.openflexo.jedit.JEditTextArea#setFont(java.awt.Font)
	 */
	@Override
	public void setFont(Font font) {
		super.setFont(font);
		if (linesTA != null && linesTA.getFont() != font) {
			linesTA.setFont(font.getStyle() == Font.PLAIN ? font : font.deriveFont(Font.PLAIN));
		}
	}

	protected void update() {
		removeAllCustomHighlight();
		globalText = buildText(_source.getTextTokens());
		setText(globalText);
		_changes.clear();
		highlights = new Hashtable<MergeChange, MergeHighlight>();
		for (MergeChange change : _merge.getChanges()) {
			highlights.put(change, makeHighlightForChange(change));
		}
		int cols;
		if (text.length < 10) {
			cols = 2;
		} else if (text.length < 100) {
			cols = 3;
		} else if (text.length < 1000) {
			cols = 4;
		} else {
			cols = 5;
		}
		linesTA.setRows(text.length);
		linesTA.setColumns(cols);
		linesTA.setText(linesTAText);
		selectedChange = null;
	}

	public JScrollBar getHorizontalScrollBar() {
		return horizontal;
	}

	public JScrollBar getVerticalScrollBar() {
		return vertical;
	}

	private static int getIndexOfLineNb(Vector<Line> lines, int lineNb) {
		for (int i = 0; i < lines.size(); i++) {
			if (lines.get(i).lineNb == lineNb) {
				return i;
			}
		}
		return -1;
	}

	int lineToChar(int lineNb, boolean isFirst) {
		int returned = 0;
		int i;
		for (i = 0; i < text.length && text[i].lineNb < lineNb; i++) {
			returned += text[i].isExtraLine() ? 1 : text[i].getStringValue().length() + 1;
		}
		if (isFirst) {
			int j = i - 1;
			while (j > 0 && text[j/*-1*/].isExtraLine() && text[j - 1].isExtraLine()) {
				returned -= 1;
				j = j - 1;
			}
		}
		return returned;
	}

	int lineToPhysLine(int lineNb, boolean isFirst) {
		if (isFirst) {
			int i = 0;
			while (i < text.length && text[i].lineNb < lineNb) {
				i++;
			}
			return i;
		} else {
			int i = 0;
			while (i < text.length && text[i].lineNb <= lineNb) {
				i++;
			}
			return i - 1;
		}
	}

	protected void setChange(final MergeChange change, boolean shouldScroll) {
		if (change == null) {
			return;
		}

		// TODO: (SGU) quelqu'un (moi ?) a supprime le test if (change != selectedChange) ...
		// Je me rappelle que ca greve pas mal les performances, il faudrait regarder ca un jour.

		// if ((change != selectedChange) && (getPainter().isValid())) {
		if (selectedChange != null && highlights.get(selectedChange) != null) {
			highlights.get(selectedChange).deselect();
		}
		if (highlights.get(change) != null) {
			highlights.get(change).select();
		}
		selectedChange = change;
		if (shouldScroll) {
			JViewport viewPort = getViewPort();
			ChangeBounds cb = boundsForChange(change);
			final Rectangle bounds = new Rectangle();
			bounds.x = 0;
			bounds.y = cb.beginPLine * getLineHeight();
			if (bounds.y < 50) {
				bounds.y = 0;
			} else {
				bounds.y -= 50;
			}
			// int requiredHeight = (cb.endPLine-cb.beginPLine+1)*getLineHeight()+50;
			bounds.height = viewPort.getHeight();
			bounds.width = viewPort.getWidth();
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					scrollRectToVisible(bounds);
				}
			});
		}

		// }
	}

	private JViewport getViewPort() {
		Component current = getPainter();
		while (current != null && !(current instanceof JViewport)) {
			current = current.getParent();
		}
		if (current instanceof JViewport) {
			return (JViewport) current;
		}
		return null;
	}

	/*	   public void scrollRectToVisible(Rectangle aRect) {
		   System.out.println("scrollRectToVisible() with "+aRect);
		   super.scrollRectToVisible(aRect);
	   }*/

	private MergeHighlight makeHighlightForChange(MergeChange change) {
		ChangeBounds cb = boundsForChange(change);
		MergeHighlight returned = new MergeHighlight(change, this);
		returned.setBeginLineNb(cb.beginPLine);
		returned.setEndLineNb(cb.endPLine);
		addCustomHighlight(returned);
		return returned;
	}

	private Hashtable<MergeChange, ChangeBounds> _changes;

	protected ChangeBounds boundsForChange(MergeChange change) {
		ChangeBounds returned = _changes.get(change);
		if (returned == null) {
			returned = new ChangeBounds(change);
			_changes.put(change, returned);
		}
		return returned;
	}

	private class ChangeBounds {
		int beginLine;
		int endLine;
		int beginIndex;
		int endIndex;
		int beginPLine;
		int endPLine;

		ChangeBounds(MergeChange change) {
			if (!isMerge()) {
				beginLine = isRight() ? change.getFirst2() : change.getFirst0();
				endLine = isRight() ? change.getLast2() : change.getLast0();
				beginIndex = lineToChar(beginLine, true);
				endIndex = lineToChar(endLine, false);
				if (isRight() && change.getFirst2() > change.getLast2() || isLeft() && change.getFirst0() > change.getLast0()) {
					beginIndex = beginIndex - 1;
				}

				if (beginLine <= endLine) {
					beginPLine = lineToPhysLine(beginLine, true);
					endPLine = lineToPhysLine(endLine, false);
				} else {
					int oppositeLength = (isRight() ? change.getLast0() - change.getFirst0() : change.getLast2() - change.getFirst2()) + 1;
					endPLine = lineToPhysLine(endLine, false);
					beginPLine = endPLine - oppositeLength + 1;
				}
			} else {
				beginLine = change.getFirstMergeIndex();
				endLine = change.getLastMergeIndex();
				beginIndex = lineToChar(beginLine, true);
				endIndex = lineToChar(endLine, false);
				beginPLine = beginLine;
				endPLine = endLine;
			}
		}

	}

	boolean isMerge() {
		return side == Side.Merge;
	}

	boolean isRight() {
		return side == Side.Right;
	}

	boolean isLeft() {
		return side == Side.Left;
	}

	public JTextArea getLinesTA() {
		return linesTA;
	}

	protected boolean readyToDisplay() {
		return getPainter().getFontMetrics() != null;
	}

	private int getLineHeight() {
		if (readyToDisplay()) {
			return getPainter().getFontMetrics().getHeight();
		}
		return -1;
	}

	public float heightAboveChange(MergeChange change, int buttonHeight) {
		if (!readyToDisplay()) {
			return -1;
		}
		MergeChange previousChange = _merge.changeBefore(change);
		if (previousChange == null) {
			return getFirstHeight(change) + (getHeightForChange(change) - buttonHeight) / 2;
		} else {
			return (getHeightForChange(previousChange) - buttonHeight) / 2 + getHeightBetweenChange(previousChange, change)
					+ (getHeightForChange(change) - buttonHeight) / 2;
		}
	}

	private float getHeightForChange(MergeChange change) {
		ChangeBounds cb = boundsForChange(change);
		return (cb.endPLine - cb.beginPLine + 1) * getLineHeight();
	}

	private float getHeightBetweenChange(MergeChange change1, MergeChange change2) {
		ChangeBounds cb1 = boundsForChange(change1);
		ChangeBounds cb2 = boundsForChange(change2);
		return (cb2.beginPLine - cb1.endPLine - 1) * getLineHeight();
	}

	private float getFirstHeight(MergeChange firstChange) {
		ChangeBounds cb = boundsForChange(firstChange);
		return cb.beginPLine * getLineHeight();
	}

	/*public void setText(String text)
	{
		(new Exception("setText()")).printStackTrace();
		super.setText(text);
	}*/
}
