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
package org.openflexo.swing.diff;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.Rectangle;
import java.util.Hashtable;
import java.util.Vector;

import javax.swing.JScrollBar;
import javax.swing.JTextArea;

import org.openflexo.diff.ComputeDiff;
import org.openflexo.diff.ComputeDiff.DiffChange;
import org.openflexo.diff.ComputeDiff.DiffReport;
import org.openflexo.jedit.JEditTextAreaWithHighlights;
import org.openflexo.jedit.TokenMarker;
import org.openflexo.toolbox.FontCst;
import org.openflexo.toolbox.TokenMarkerStyle;

public class DiffTextArea /* extends JTextArea */extends JEditTextAreaWithHighlights {
	// This flag is used to get either left perspective or right perspective
	private boolean isLeftOriented = true;

	private DiffChange selectedChange;

	private Hashtable<DiffChange, DiffHighlight> highlights;

	public enum Side {
		Right, Left
	};

	private Side side;

	private DiffReport diffReport;

	// private DefaultHighlighter hl;
	// private DiffHighlightPainter selectedHLPainter;
	// private DiffHighlightPainter unselectedHLPainter;

	private Line[] text;

	private String globalText;

	private String linesTAText;

	private JTextArea linesTA;

	public DiffTextArea(String[] someText, DiffReport report, Side side, TokenMarkerStyle style, boolean isLeftOriented) {
		super();
		this.isLeftOriented = isLeftOriented;
		disableDefaultMouseWheelListener();
		setTokenMarker(TokenMarker.makeTokenMarker(style));
		setPreferredSize(new Dimension(10, 10));
		this.side = side;
		this.diffReport = report;
		setFont(FontCst.TEXT_FONT);
		globalText = buildText(someText);
		setText(globalText);
		setColumns(maxCols);
		setEditable(false);
		// System.out.println("DiffTextArea: columns="+maxCols+"\n"+globalText);

		_changes = new Hashtable<DiffChange, ChangeBounds>();

		// if (diffReport.getChanges().size() > 0) {
		highlights = new Hashtable<DiffChange, DiffHighlight>();
		for (DiffChange change : diffReport.getChanges()) {
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
		linesTA.setFont(FontCst.TEXT_FONT.getStyle() != Font.PLAIN ? FontCst.TEXT_FONT.deriveFont(Font.PLAIN) : FontCst.TEXT_FONT);
		linesTA.setBackground(null);
		linesTA.setText(linesTAText);
		/*  addKeyListener(new KeyListener() {
		      public void keyPressed(KeyEvent e)
		      {
		          SwingUtilities.invokeLater(new Runnable() {
		              public void run()
		              {
		                  logger.info("Position " + getCaretPosition());
		              }
		          });
		      }

		      public void keyReleased(KeyEvent e)
		      {
		      }

		      public void keyTyped(KeyEvent e)
		      {
		      }

		  });*/
		remove(vertical);
		remove(horizontal);
		validate();

		// System.out.println("DiffTextArea: columns="+getSize()+"
		// "+getMinimumSize()+" "+getPreferredSize()+"\n");

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

	protected class Line {
		int lineNb;

		boolean isExtraLine;

		private String lineText;

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

	private String buildText(String[] someText) {
		Vector<Line> lines = new Vector<Line>();
		for (int i = 0; i < someText.length; i++) {
			String line = someText[i];
			lines.add(new Line(line, i));
			if (line.length() > maxCols) {
				maxCols = line.length();
			}
		}

		for (DiffChange change : diffReport.getChanges()) {
			int startIndex = (isRight() ? change.getFirst1() : change.getFirst0());
			int endIndex = (isRight() ? change.getLast1() : change.getLast0());
			int oppositeStartIndex = (isLeft() ? change.getFirst1() : change.getFirst0());
			int oppositeEndIndex = (isLeft() ? change.getLast1() : change.getLast0());
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
			returned += (text[i].isExtraLine() ? 1 : text[i].getStringValue().length() + 1);
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

	protected void setChange(ComputeDiff.DiffChange change, boolean shouldScroll, boolean forceSelect) {
		if (change != selectedChange || forceSelect) {
			if (selectedChange != null) {
				highlights.get(selectedChange).deselect();
			}
			highlights.get(change).select();
			selectedChange = change;
			if (shouldScroll) {
				ChangeBounds cb = boundsForChange(change);
				Rectangle bounds = new Rectangle();
				bounds.x = 0;
				bounds.y = cb.beginPLine * getLineHeight();
				bounds.height = (cb.endPLine - cb.beginPLine + 1) * getLineHeight();
				bounds.width = getWidth();
				scrollRectToVisible(bounds);
			}

		}
	}

	private DiffHighlight makeHighlightForChange(ComputeDiff.DiffChange change) {
		ChangeBounds cb = boundsForChange(change);
		DiffHighlight returned = new DiffHighlight(change, this);
		returned.setBeginLineNb(cb.beginPLine);
		returned.setEndLineNb(cb.endPLine);
		addCustomHighlight(returned);
		return returned;
	}

	private Hashtable<DiffChange, ChangeBounds> _changes;

	protected ChangeBounds boundsForChange(DiffChange change) {
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

		ChangeBounds(DiffChange change) {
			beginLine = (isRight() ? change.getFirst1() : change.getFirst0());
			endLine = (isRight() ? change.getLast1() : change.getLast0());
			beginIndex = lineToChar(beginLine, true);
			endIndex = lineToChar(endLine, false);
			if ((isRight() && change.getFirst1() > change.getLast1()) || (isLeft() && change.getFirst0() > change.getLast0())) {
				beginIndex = beginIndex - 1;
			}

			if (beginLine <= endLine) {
				beginPLine = lineToPhysLine(beginLine, true);
				endPLine = lineToPhysLine(endLine, false);
			} else {
				int oppositeLength = (isRight() ? change.getLast0() - change.getFirst0() : change.getLast1() - change.getFirst1()) + 1;
				endPLine = lineToPhysLine(endLine, false);
				beginPLine = endPLine - oppositeLength + 1;
			}
		}

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

	public int heightAboveChange(DiffChange change, int buttonHeight) {
		if (!readyToDisplay()) {
			return -1;
		}
		DiffChange previousChange = diffReport.changeBefore(change);
		if (previousChange == null) {
			return getFirstHeight(change) + (getHeightForChange(change) - buttonHeight) / 2;
		} else {
			return (getHeightForChange(previousChange) - buttonHeight) / 2 + getHeightBetweenChange(previousChange, change)
					+ (getHeightForChange(change) - buttonHeight) / 2;
		}
	}

	private int getHeightForChange(DiffChange change) {
		ChangeBounds cb = boundsForChange(change);
		return (cb.endPLine - cb.beginPLine + 1) * getLineHeight();
	}

	private int getHeightBetweenChange(DiffChange change1, DiffChange change2) {
		ChangeBounds cb1 = boundsForChange(change1);
		ChangeBounds cb2 = boundsForChange(change2);
		return (cb2.beginPLine - cb1.endPLine - 1) * getLineHeight();
	}

	private int getFirstHeight(DiffChange firstChange) {
		ChangeBounds cb = boundsForChange(firstChange);
		return cb.beginPLine * getLineHeight();
	}

	public boolean isLeftOriented() {
		return isLeftOriented;
	}

}