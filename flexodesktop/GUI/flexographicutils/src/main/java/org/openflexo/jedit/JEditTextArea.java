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

/*
 * JEditTextArea.java - jEdit's text component
 * Copyright (C) 1999 Slava Pestov
 *
 * You may use and modify this package for any purpose. Redistribution is
 * permitted, in both source and binary form, provided that this notice
 * remains intact in all source distributions of this package.
 */

import java.awt.AWTEvent;
import java.awt.Adjustable;
import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.RenderingHints;
import java.awt.Window;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.util.Enumeration;
import java.util.StringTokenizer;
import java.util.Vector;

import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JPopupMenu;
import javax.swing.JScrollBar;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.WindowConstants;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.EventListenerList;
import javax.swing.text.BadLocationException;
import javax.swing.text.Element;
import javax.swing.text.PlainDocument;
import javax.swing.text.Segment;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.Utilities;
import javax.swing.undo.AbstractUndoableEdit;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.UndoableEdit;

import org.openflexo.localization.FlexoLocalization;
import org.openflexo.search.view.ITextComponent;
import org.openflexo.search.view.TextSearchPanel;
import org.openflexo.swing.DialogFactory;
import org.openflexo.toolbox.FontCst;
import org.openflexo.toolbox.ToolBox;

/**
 * jEdit's text area component. It is more suited for editing program source code than JEditorPane, because it drops the unnecessary
 * features (images, variable-width lines, and so on) and adds a whole bunch of useful goodies such as:
 * <ul>
 * <li>More flexible key binding scheme
 * <li>Supports macro recorders
 * <li>Rectangular selection
 * <li>Bracket highlighting
 * <li>Syntax highlighting
 * <li>Command repetition
 * <li>Block caret can be enabled
 * </ul>
 * It is also faster and doesn't have as many problems. It can be used in other applications; the only other part of jEdit it depends on is
 * the syntax package.
 * <p>
 * 
 * To use it in your app, treat it like any other component, for example:
 * 
 * <pre>
 * JEditTextArea ta = new JEditTextArea();
 * ta.setTokenMarker(new JavaTokenMarker());
 * ta.setText(&quot;public class Test {\n&quot; + &quot;    public static void main(String[] args) {\n&quot; + &quot;        System.out.println(\&quot;Hello World\&quot;);\n&quot;
 * 		+ &quot;    }\n&quot; + &quot;}&quot;);
 * </pre>
 * 
 * @author Slava Pestov
 * @version $Id: JEditTextArea.java,v 1.2 2011/09/12 11:47:08 gpolet Exp $
 */
public class JEditTextArea extends JComponent implements ITextComponent {
	public static DialogFactory DIALOG_FACTORY;

	/**
	 * Adding components with this name to the text area will place them left of the horizontal scroll bar. In jEdit, the status bar is
	 * added this way.
	 */
	public static String LEFT_OF_SCROLLBAR = "los";

	/**
	 * Creates a new JEditTextArea with the default settings.
	 */
	public JEditTextArea() {
		this(TextAreaDefaults.getNewDefaults());
	}

	@Override
	public void setFont(Font font) {
		super.setFont(font);
		painter.setFont(font);
	}

	private static final Font DEFAULT_FONT = FontCst.CODE_FONT;

	@Override
	public Font getFont() {
		Font returned = super.getFont();
		if (returned == null) {
			return DEFAULT_FONT;
		}
		return returned;
	}

	private MouseWheelListener _defaultMouseWheelListener;

	protected void disableDefaultMouseWheelListener() {
		getPainter().removeMouseWheelListener(_defaultMouseWheelListener);
	}

	protected TextAreaPainter initTextAreaPainter(JEditTextArea textArea, TextAreaDefaults defaults) {
		return new TextAreaPainter(this, defaults);
	}

	/**
	 * Creates a new JEditTextArea with the specified settings.
	 * 
	 * @param defaults
	 *            The default settings
	 */
	public JEditTextArea(TextAreaDefaults defaults) {
		// Enable the necessary events
		enableEvents(AWTEvent.KEY_EVENT_MASK);

		// Initialize some misc. stuff
		painter = initTextAreaPainter(this, defaults);
		documentHandler = new DocumentHandler();
		listenerList = new EventListenerList();
		caretEvent = new MutableCaretEvent();
		lineSegment = new Segment();
		bracketLine = bracketPosition = -1;
		blink = true;

		// Initialize the GUI
		setLayout(new ScrollLayout());
		add(CENTER, painter);
		add(RIGHT, vertical = new JScrollBar(Adjustable.VERTICAL));
		add(BOTTOM, horizontal = new JScrollBar(Adjustable.HORIZONTAL));
		validate();
		// vertical.setUnitIncrement(10);
		// vertical.setBlockIncrement(50);

		_defaultMouseWheelListener = new MouseWheelListener() {

			@Override
			public void mouseWheelMoved(MouseWheelEvent e) {
				if (vertical.isVisible() && e.getScrollAmount() != 0) {
					int direction = 0;
					direction = e.getWheelRotation() < 0 ? -1 : 1;
					if (e.getScrollType() == MouseWheelEvent.WHEEL_UNIT_SCROLL) {
						scrollByUnits(vertical, direction, e.getScrollAmount());
					} else if (e.getScrollType() == MouseWheelEvent.WHEEL_BLOCK_SCROLL) {
						scrollByBlock(vertical, direction);
					}
				}
			}
		};

		getPainter().addMouseWheelListener(_defaultMouseWheelListener);
		setCursor(Cursor.getPredefinedCursor(Cursor.TEXT_CURSOR));
		/*
		 * setLayout(new BorderLayout()); add(painter,BorderLayout.CENTER); vertical = new JScrollBar(JScrollBar.VERTICAL); horizontal = new
		 * JScrollBar(JScrollBar.HORIZONTAL);
		 */

		// Add some event listeners
		vertical.addAdjustmentListener(new AdjustHandler());
		horizontal.addAdjustmentListener(new AdjustHandler());
		painter.addComponentListener(new ComponentHandler());
		painter.addMouseListener(new MouseHandler());
		painter.addMouseMotionListener(new DragHandler());
		addFocusListener(new FocusHandler());

		// Load the defaults
		setInputHandler(defaults.inputHandler);
		setDocument(defaults.document);
		getDocument().getDocumentProperties().put(PlainDocument.tabSizeAttribute, 4);
		editable = defaults.editable;
		caretVisible = defaults.caretVisible;
		caretBlinks = defaults.caretBlinks;

		popup = defaults.popup;

		// We don't seem to get the initial focus event?
		focusedComponent = this;

		// _history = new TextEditionHistory(this);
	}

	// private TextEditionHistory _history;

	/*
	 * public void retain() { _history.retain(); }
	 * 
	 * public boolean isUndoable() { return _history.isUndoable(); }
	 * 
	 * public void undo() { _history.undo(); }
	 * 
	 * public boolean isRedoable() { return _history.isRedoable(); }
	 * 
	 * public void redo() { _history.redo(); }
	 */

	/*
	 * Method for scrolling by a unit increment. Added for mouse wheel scrolling support, RFE 4202656.
	 */
	public static void scrollByUnits(JScrollBar scrollbar, int direction, int units) {
		// This method is called from BasicScrollPaneUI to implement wheel
		// scrolling, as well as from scrollByUnit().
		int delta;

		for (int i = 0; i < units; i++) {
			if (direction > 0) {
				delta = scrollbar.getUnitIncrement(direction);
			} else {
				delta = -scrollbar.getUnitIncrement(direction);
			}

			int oldValue = scrollbar.getValue();
			int newValue = oldValue + delta;

			// Check for overflow.
			if (delta > 0 && newValue < oldValue) {
				newValue = scrollbar.getMaximum();
			} else if (delta < 0 && newValue > oldValue) {
				newValue = scrollbar.getMinimum();
			}
			if (oldValue == newValue) {
				break;
			}
			scrollbar.setValue(newValue);
		}
	}

	/*
	 * Method for scrolling by a block increment. Added for mouse wheel scrolling support, RFE 4202656.
	 */
	public static void scrollByBlock(JScrollBar scrollbar, int direction) {
		// This method is called from BasicScrollPaneUI to implement wheel
		// scrolling, and also from scrollByBlock().
		int oldValue = scrollbar.getValue();
		int blockIncrement = scrollbar.getBlockIncrement(direction);
		int delta = blockIncrement * (direction > 0 ? +1 : -1);
		int newValue = oldValue + delta;

		// Check for overflow.
		if (delta > 0 && newValue < oldValue) {
			newValue = scrollbar.getMaximum();
		} else if (delta < 0 && newValue > oldValue) {
			newValue = scrollbar.getMinimum();
		}

		scrollbar.setValue(newValue);
	}

	/**
	 * Returns if this component can be traversed by pressing the Tab key. This returns false.
	 */
	@Override
	public final boolean isManagingFocus() {
		return true;
	}

	/**
	 * Returns the object responsible for painting this text area.
	 */
	public final TextAreaPainter getPainter() {
		return painter;
	}

	/**
	 * Returns the input handler.
	 */
	public final InputHandler getInputHandler() {
		return inputHandler;
	}

	/**
	 * Sets the input handler.
	 * 
	 * @param inputHandler
	 *            The new input handler
	 */
	public void setInputHandler(InputHandler inputHandler) {
		this.inputHandler = inputHandler;
	}

	/**
	 * Returns true if the caret is blinking, false otherwise.
	 */
	public final boolean isCaretBlinkEnabled() {
		return caretBlinks;
	}

	/**
	 * Toggles caret blinking.
	 * 
	 * @param caretBlinks
	 *            True if the caret should blink, false otherwise
	 */
	public void setCaretBlinkEnabled(boolean caretBlinks) {
		this.caretBlinks = caretBlinks;
		if (!caretBlinks) {
			blink = false;
		}

		painter.invalidateSelectedLines();
	}

	/**
	 * Returns true if the caret is visible, false otherwise.
	 */
	public final boolean isCaretVisible() {
		return (!caretBlinks || blink) && caretVisible;
	}

	/**
	 * Sets if the caret should be visible.
	 * 
	 * @param caretVisible
	 *            True if the caret should be visible, false otherwise
	 */
	public void setCaretVisible(boolean caretVisible) {
		this.caretVisible = caretVisible;
		blink = true;

		painter.invalidateSelectedLines();
	}

	/**
	 * Blinks the caret.
	 */
	public final void blinkCaret() {
		if (caretBlinks) {
			blink = !blink;
			painter.invalidateSelectedLines();
		} else {
			blink = true;
		}
	}

	/**
	 * Returns the number of lines from the top and button of the text area that are always visible.
	 */
	@Deprecated
	public final int getElectricScroll() {
		return electricScroll;
	}

	/**
	 * Sets the number of lines from the top and bottom of the text area that are always visible
	 * 
	 * @param electricScroll
	 *            The number of lines always visible from the top or bottom
	 */
	@Deprecated
	public final void setElectricScroll(int electricScroll) {
		// Electric scroll is some kind of ugly invention that causes
		// the textarea to always scroll, especially when we don't want
	}

	/**
	 * Updates the state of the scroll bars. This should be called if the number of lines in the document changes, or when the size of the
	 * text are changes.
	 */
	public void updateScrollBars() {
		if (vertical != null && visibleLines != 0) {
			vertical.setValues(firstLine, visibleLines, 0, getLineCount());
			vertical.setUnitIncrement(3);
			vertical.setBlockIncrement(visibleLines);
		}

		int width = painter.getWidth();
		if (horizontal != null && width != 0) {
			int max = width;
			StringTokenizer st = new StringTokenizer(getText(), "\n");
			while (st.hasMoreTokens()) {
				int w = (int) getFontMetrics(getFont()).getStringBounds(st.nextToken(), getGraphics()).getWidth();
				if (w > max) {
					max = w;
				}
			}
			horizontal.setValues(-horizontalOffset, width, 0, max + 10);
			horizontal.setUnitIncrement(painter.getFontMetrics().charWidth('W'));
			horizontal.setBlockIncrement(width / 2);
		}
	}

	/**
	 * Returns the line displayed at the text area's origin.
	 */
	public final int getFirstLine() {
		return firstLine;
	}

	/**
	 * Sets the line displayed at the text area's origin without updating the scroll bars.
	 */
	public void setFirstLine(int firstLine) {
		if (firstLine < 0 || firstLine == this.firstLine) {
			return;
		}
		this.firstLine = firstLine;
		if (firstLine != vertical.getValue()) {
			updateScrollBars();
		}
		painter.repaint();
	}

	/**
	 * Returns the number of lines visible in this text area.
	 */
	public final int getVisibleLines() {
		return visibleLines;
	}

	/**
	 * Recalculates the number of visible lines. This should not be called directly.
	 */
	public final void recalculateVisibleLines() {
		if (painter == null) {
			return;
		}
		int height = painter.getHeight();
		int lineHeight = painter.getFontMetrics().getHeight();
		visibleLines = height / lineHeight;
		if (visibleLines > 0) {
			visibleLines--;
		}
		updateScrollBars();
	}

	/**
	 * Returns the horizontal offset of drawn lines.
	 */
	public final int getHorizontalOffset() {
		return horizontalOffset;
	}

	/**
	 * Sets the horizontal offset of drawn lines. This can be used to implement horizontal scrolling.
	 * 
	 * @param horizontalOffset
	 *            offset The new horizontal offset
	 */
	public void setHorizontalOffset(int horizontalOffset) {
		if (horizontalOffset == this.horizontalOffset) {
			return;
		}
		this.horizontalOffset = horizontalOffset;
		if (horizontalOffset != horizontal.getValue()) {
			updateScrollBars();
		}
		painter.repaint();
	}

	/**
	 * A fast way of changing both the first line and horizontal offset.
	 * 
	 * @param firstLine
	 *            The new first line
	 * @param horizontalOffset
	 *            The new horizontal offset
	 * @return True if any of the values were changed, false otherwise
	 */
	public boolean setOrigin(int firstLine, int horizontalOffset) {
		boolean changed = false;

		if (horizontalOffset != this.horizontalOffset) {
			this.horizontalOffset = horizontalOffset;
			changed = true;
		}

		if (firstLine != this.firstLine) {
			this.firstLine = firstLine;
			changed = true;
		}

		if (changed) {
			updateScrollBars();
			painter.repaint();
		}

		return changed;
	}

	/**
	 * Ensures that the caret is visible by scrolling the text area if necessary.
	 * 
	 * @return True if scrolling was actually performed, false if the caret was already visible
	 */
	public boolean scrollToCaret() {
		int line = getCaretLine();
		int lineStart = getLineStartOffset(line);
		int offset = Math.max(0, Math.min(getLineLength(line) - 1, getCaretPosition() - lineStart));

		return scrollTo(line, offset);
	}

	/**
	 * Ensures that the specified line and offset is visible by scrolling the text area if necessary.
	 * 
	 * @param line
	 *            The line to scroll to
	 * @param offset
	 *            The offset in the line to scroll to
	 * @return True if scrolling was actually performed, false if the line and offset was already visible
	 */
	public boolean scrollTo(int line, int offset) {
		// visibleLines == 0 before the component is realized
		// we can't do any proper scrolling then, so we have
		// this hack...
		if (visibleLines == 0) {
			setFirstLine(Math.max(0, line - electricScroll));
			return true;
		}

		int newFirstLine = firstLine;
		int newHorizontalOffset = horizontalOffset;

		if (line < firstLine) { // line is above the first line
			newFirstLine = Math.max(0, line);
		} else if (line > firstLine + visibleLines) {// line is below the last line
			newFirstLine = Math.min(line - visibleLines, getLineCount() - visibleLines);// (line - visibleLines) + electricScroll + 1;
		} // line is currently between first and last shown line--> we don't move
		if (newFirstLine < 0) {
			newFirstLine = 0;
		}
		int x = _offsetToX(line, offset);
		int width = painter.getFontMetrics().charWidth('w');

		if (x < 0) {
			newHorizontalOffset = Math.min(0, horizontalOffset - x + width + 5);
		} else if (x + width >= painter.getWidth()) {
			newHorizontalOffset = horizontalOffset + painter.getWidth() - x - width - 5;
		}

		return setOrigin(newFirstLine, newHorizontalOffset);
	}

	/**
	 * Converts a line index to a y co-ordinate.
	 * 
	 * @param line
	 *            The line
	 */
	public int lineToY(int line) {
		FontMetrics fm = painter.getFontMetrics();
		return (line - firstLine) * fm.getHeight() - (fm.getLeading() + fm.getMaxDescent());
	}

	/**
	 * Converts a y co-ordinate to a line index.
	 * 
	 * @param y
	 *            The y co-ordinate
	 */
	public int yToLine(int y) {
		FontMetrics fm = painter.getFontMetrics();
		int height = fm.getHeight();
		return Math.max(0, Math.min(getLineCount() - 1, y / height + firstLine));
	}

	/**
	 * Converts an offset in a line into an x co-ordinate. This is a slow version that can be used any time.
	 * 
	 * @param line
	 *            The line
	 * @param offset
	 *            The offset, from the start of the line
	 */
	public final int offsetToX(int line, int offset) {
		// don't use cached tokens
		painter.currentLineTokens = null;
		return _offsetToX(line, offset);
	}

	/**
	 * Converts an offset in a line into an x co-ordinate. This is a fast version that should only be used if no changes were made to the
	 * text since the last repaint.
	 * 
	 * @param line
	 *            The line
	 * @param offset
	 *            The offset, from the start of the line
	 */
	public int _offsetToX(int line, int offset) {
		TokenMarker tokenMarker = getTokenMarker();

		/* Use painter's cached info for speed */
		FontMetrics fm = painter.getFontMetrics();

		getLineText(line, lineSegment);

		int segmentOffset = lineSegment.offset;
		int x = horizontalOffset;

		/* If syntax coloring is disabled, do simple translation */
		if (tokenMarker == null) {
			lineSegment.count = offset;
			return x + Utilities.getTabbedTextWidth(lineSegment, fm, x, painter, 0);
		}
		/*
		 * If syntax coloring is enabled, we have to do this because tokens can vary in width
		 */
		else {
			Token tokens;
			if (painter.currentLineIndex == line && painter.currentLineTokens != null) {
				tokens = painter.currentLineTokens;
			} else {
				painter.currentLineIndex = line;
				tokens = painter.currentLineTokens = tokenMarker.markTokens(lineSegment, line);
			}

			painter.getToolkit();
			Font defaultFont = painter.getFont();
			SyntaxStyle[] styles = painter.getStyles();

			for (;;) {
				byte id = tokens.id;
				if (id == Token.END) {
					return x;
				}

				if (id == Token.NULL) {
					fm = painter.getFontMetrics();
				} else {
					fm = styles[id].getFontMetrics(defaultFont);
				}

				int length = tokens.length;

				if (offset + segmentOffset < lineSegment.offset + length) {
					lineSegment.count = offset - (lineSegment.offset - segmentOffset);
					return x + Utilities.getTabbedTextWidth(lineSegment, fm, x, painter, 0);
				} else {
					lineSegment.count = length;
					x += Utilities.getTabbedTextWidth(lineSegment, fm, x, painter, 0);
					lineSegment.offset += length;
				}
				tokens = tokens.next;
			}
		}
	}

	/**
	 * Converts an x co-ordinate to an offset within a line.
	 * 
	 * @param line
	 *            The line
	 * @param x
	 *            The x co-ordinate
	 */
	public int xToOffset(int line, int x) {
		TokenMarker tokenMarker = getTokenMarker();

		/* Use painter's cached info for speed */
		FontMetrics fm = painter.getFontMetrics();

		getLineText(line, lineSegment);

		char[] segmentArray = lineSegment.array;
		int segmentOffset = lineSegment.offset;
		int segmentCount = lineSegment.count;

		int width = horizontalOffset;

		if (tokenMarker == null) {
			for (int i = 0; i < segmentCount; i++) {
				char c = segmentArray[i + segmentOffset];
				int charWidth;
				if (c == '\t') {
					charWidth = (int) painter.nextTabStop(width, i) - width;
				} else {
					charWidth = fm.charWidth(c);
				}

				if (painter.isBlockCaretEnabled()) {
					if (x - charWidth <= width) {
						return i;
					}
				} else {
					if (x - charWidth / 2 <= width) {
						return i;
					}
				}

				width += charWidth;
			}

			return segmentCount;
		} else {
			Token tokens;
			if (painter.currentLineIndex == line && painter.currentLineTokens != null) {
				tokens = painter.currentLineTokens;
			} else {
				painter.currentLineIndex = line;
				tokens = painter.currentLineTokens = tokenMarker.markTokens(lineSegment, line);
			}

			int offset = 0;
			painter.getToolkit();
			Font defaultFont = painter.getFont();
			SyntaxStyle[] styles = painter.getStyles();

			for (;;) {
				byte id = tokens.id;
				if (id == Token.END) {
					return offset;
				}

				if (id == Token.NULL) {
					fm = painter.getFontMetrics();
				} else {
					fm = styles[id].getFontMetrics(defaultFont);
				}

				int length = tokens.length;

				for (int i = 0; i < length; i++) {
					char c = segmentArray[segmentOffset + offset + i];
					int charWidth;
					if (c == '\t') {
						charWidth = (int) painter.nextTabStop(width, offset + i) - width;
					} else {
						charWidth = fm.charWidth(c);
					}

					if (painter.isBlockCaretEnabled()) {
						if (x - charWidth <= width) {
							return offset + i;
						}
					} else {
						if (x - charWidth / 2 <= width) {
							return offset + i;
						}
					}

					width += charWidth;
				}

				offset += length;
				tokens = tokens.next;
			}
		}
	}

	/**
	 * Converts a point to an offset, from the start of the text.
	 * 
	 * @param x
	 *            The x co-ordinate of the point
	 * @param y
	 *            The y co-ordinate of the point
	 */
	public int xyToOffset(int x, int y) {
		int line = yToLine(y);
		int start = getLineStartOffset(line);
		return start + xToOffset(line, x);
	}

	/**
	 * Returns the document this text area is editing.
	 */
	@Override
	public final SyntaxDocument getDocument() {
		return document;
	}

	/**
	 * Sets the document this text area is editing.
	 * 
	 * @param document
	 *            The document
	 */
	public void setDocument(SyntaxDocument document) {
		if (this.document == document) {
			return;
		}
		if (this.document != null) {
			this.document.removeDocumentListener(documentHandler);
		}
		this.document = document;

		document.addDocumentListener(documentHandler);

		select(0, 0);
		updateScrollBars();
		painter.repaint();
	}

	/**
	 * Returns the document's token marker. Equivalent to calling <code>getDocument().getTokenMarker()</code>.
	 */
	public final TokenMarker getTokenMarker() {
		return document.getTokenMarker();
	}

	/**
	 * Sets the document's token marker. Equivalent to caling <code>getDocument().setTokenMarker()</code>.
	 * 
	 * @param tokenMarker
	 *            The token marker
	 */
	public final void setTokenMarker(TokenMarker tokenMarker) {
		this.tokenMarker = tokenMarker;
		enableSyntaxColor();
	}

	/**
	 * Returns the length of the document. Equivalent to calling <code>getDocument().getLength()</code>.
	 */
	public final int getDocumentLength() {
		return document.getLength();
	}

	/**
	 * Returns the number of lines in the document.
	 */
	public final int getLineCount() {
		return document.getDefaultRootElement().getElementCount();
	}

	/**
	 * Returns the line containing the specified offset.
	 * 
	 * @param offset
	 *            The offset
	 */
	public final int getLineOfOffset(int offset) {
		return document.getDefaultRootElement().getElementIndex(offset);
	}

	/**
	 * Returns the start offset of the specified line.
	 * 
	 * @param line
	 *            The line
	 * @return The start offset of the specified line, or -1 if the line is invalid
	 */
	public int getLineStartOffset(int line) {
		Element lineElement = document.getDefaultRootElement().getElement(line);
		if (lineElement == null) {
			return -1;
		} else {
			return lineElement.getStartOffset();
		}
	}

	/**
	 * Returns the end offset of the specified line.
	 * 
	 * @param line
	 *            The line
	 * @return The end offset of the specified line, or -1 if the line is invalid.
	 */
	public int getLineEndOffset(int line) {
		Element lineElement = document.getDefaultRootElement().getElement(line);
		if (lineElement == null) {
			return -1;
		} else {
			return lineElement.getEndOffset();
		}
	}

	/**
	 * Returns the length of the specified line.
	 * 
	 * @param line
	 *            The line
	 */
	public int getLineLength(int line) {
		Element lineElement = document.getDefaultRootElement().getElement(line);
		if (lineElement == null) {
			return -1;
		} else {
			return lineElement.getEndOffset() - lineElement.getStartOffset() - 1;
		}
	}

	/**
	 * Returns the entire text of this text area.
	 */
	public String getText() {
		try {
			return document.getText(0, document.getLength());
		} catch (BadLocationException bl) {
			bl.printStackTrace();
			return null;
		}
	}

	/**
	 * Sets the entire text of this text area.
	 */
	public void setText(String text) {
		try {
			document.beginCompoundEdit();
			document.remove(0, document.getLength());
			document.insertString(0, text, null);
		} catch (BadLocationException bl) {
			bl.printStackTrace();
		} finally {
			document.endCompoundEdit();
		}
		setCaretPosition(0);
	}

	/**
	 * Returns the specified substring of the document.
	 * 
	 * @param start
	 *            The start offset
	 * @param len
	 *            The length of the substring
	 * @return The substring, or null if the offsets are invalid
	 */
	public final String getText(int start, int len) {
		try {
			return document.getText(start, len);
		} catch (BadLocationException bl) {
			bl.printStackTrace();
			return null;
		}
	}

	/**
	 * Copies the specified substring of the document into a segment. If the offsets are invalid, the segment will contain a null string.
	 * 
	 * @param start
	 *            The start offset
	 * @param len
	 *            The length of the substring
	 * @param segment
	 *            The segment
	 */
	public final void getText(int start, int len, Segment segment) {
		try {
			document.getText(start, len, segment);
		} catch (BadLocationException bl) {
			bl.printStackTrace();
			segment.offset = segment.count = 0;
		}
	}

	/**
	 * Returns the text on the specified line.
	 * 
	 * @param lineIndex
	 *            The line
	 * @return The text, or null if the line is invalid
	 */
	public final String getLineText(int lineIndex) {
		int start = getLineStartOffset(lineIndex);
		return getText(start, getLineEndOffset(lineIndex) - start - 1);
	}

	/**
	 * Copies the text on the specified line into a segment. If the line is invalid, the segment will contain a null string.
	 * 
	 * @param lineIndex
	 *            The line
	 */
	public final void getLineText(int lineIndex, Segment segment) {
		int start = getLineStartOffset(lineIndex);
		getText(start, getLineEndOffset(lineIndex) - start - 1, segment);
	}

	/**
	 * Returns the selection start offset.
	 */
	@Override
	public final int getSelectionStart() {
		return selectionStart;
	}

	/**
	 * Returns the offset where the selection starts on the specified line.
	 */
	public int getSelectionStart(int line) {
		if (line == selectionStartLine) {
			return selectionStart;
		} else if (rectSelect) {
			Element map = document.getDefaultRootElement();
			int start = selectionStart - map.getElement(selectionStartLine).getStartOffset();

			Element lineElement = map.getElement(line);
			int lineStart = lineElement.getStartOffset();
			int lineEnd = lineElement.getEndOffset() - 1;
			return Math.min(lineEnd, lineStart + start);
		} else {
			return getLineStartOffset(line);
		}
	}

	/**
	 * Returns the selection start line.
	 */
	public final int getSelectionStartLine() {
		return selectionStartLine;
	}

	/**
	 * Sets the selection start. The new selection will be the new selection start and the old selection end.
	 * 
	 * @param selectionStart
	 *            The selection start
	 * @see #select(int,int)
	 */
	public final void setSelectionStart(int selectionStart) {
		select(selectionStart, selectionEnd);
	}

	/**
	 * Returns the selection end offset.
	 */
	@Override
	public final int getSelectionEnd() {
		return selectionEnd;
	}

	/**
	 * Returns the offset where the selection ends on the specified line.
	 */
	public int getSelectionEnd(int line) {
		if (line == selectionEndLine) {
			return selectionEnd;
		} else if (rectSelect) {
			Element map = document.getDefaultRootElement();
			int end = selectionEnd - map.getElement(selectionEndLine).getStartOffset();

			Element lineElement = map.getElement(line);
			int lineStart = lineElement.getStartOffset();
			int lineEnd = lineElement.getEndOffset() - 1;
			return Math.min(lineEnd, lineStart + end);
		} else {
			return getLineEndOffset(line) - 1;
		}
	}

	/**
	 * Returns the selection end line.
	 */
	public final int getSelectionEndLine() {
		return selectionEndLine;
	}

	/**
	 * Sets the selection end. The new selection will be the old selection start and the bew selection end.
	 * 
	 * @param selectionEnd
	 *            The selection end
	 * @see #select(int,int)
	 */
	public final void setSelectionEnd(int selectionEnd) {
		select(selectionStart, selectionEnd);
	}

	/**
	 * Returns the caret position. This will either be the selection start or the selection end, depending on which direction the selection
	 * was made in.
	 */
	@Override
	public final int getCaretPosition() {
		return biasLeft ? selectionStart : selectionEnd;
	}

	/**
	 * Returns the caret line.
	 */
	public final int getCaretLine() {
		return biasLeft ? selectionStartLine : selectionEndLine;
	}

	/**
	 * Returns the mark position. This will be the opposite selection bound to the caret position.
	 * 
	 * @see #getCaretPosition()
	 */
	public final int getMarkPosition() {
		return biasLeft ? selectionEnd : selectionStart;
	}

	/**
	 * Returns the mark line.
	 */
	public final int getMarkLine() {
		return biasLeft ? selectionEndLine : selectionStartLine;
	}

	/**
	 * Sets the caret position. The new selection will consist of the caret position only (hence no text will be selected)
	 * 
	 * @param caret
	 *            The caret position
	 * @see #select(int,int)
	 */
	public final void setCaretPosition(int caret) {
		select(caret, caret);
	}

	/**
	 * Selects all text in the document.
	 */
	public final void selectAll() {
		select(0, getDocumentLength());
	}

	/**
	 * Moves the mark to the caret position.
	 */
	public final void selectNone() {
		select(getCaretPosition(), getCaretPosition());
	}

	/**
	 * Selects from the start offset to the end offset. This is the general selection method used by all other selecting methods. The caret
	 * position will be start if start &lt; end, and end if end &gt; start.
	 * 
	 * @param start
	 *            The start offset
	 * @param end
	 *            The end offset
	 */
	@Override
	public void select(int start, int end) {
		int newStart, newEnd;
		boolean newBias;
		if (start <= end) {
			newStart = start;
			newEnd = end;
			newBias = false;
		} else {
			newStart = end;
			newEnd = start;
			newBias = true;
		}

		if (newStart < 0) {
			newStart = 0;
		}
		if (newEnd > getDocumentLength()) {
			return;
			/*
			 * throw new IllegalArgumentException("Bounds out of" + " range: " + newStart + "," + newEnd);
			 */
		}

		// If the new position is the same as the old, we don't
		// do all this crap, however we still do the stuff at
		// the end (clearing magic position, scrolling)
		if (newStart != selectionStart || newEnd != selectionEnd || newBias != biasLeft) {
			int newStartLine = getLineOfOffset(newStart);
			int newEndLine = getLineOfOffset(newEnd);

			if (painter.isBracketHighlightEnabled()) {
				if (bracketLine != -1) {
					painter.invalidateLine(bracketLine);
				}
				updateBracketHighlight(end);
				if (bracketLine != -1) {
					painter.invalidateLine(bracketLine);
				}
			}

			painter.invalidateLineRange(selectionStartLine, selectionEndLine);
			painter.invalidateLineRange(newStartLine, newEndLine);

			document.addUndoableEdit(new CaretUndo(selectionStart, selectionEnd));

			selectionStart = newStart;
			selectionEnd = newEnd;
			selectionStartLine = newStartLine;
			selectionEndLine = newEndLine;
			biasLeft = newBias;

			fireCaretEvent();
		}

		// When the user is typing, etc, we don't want the caret
		// to blink
		blink = true;
		caretTimer.restart();

		// Disable rectangle select if selection start = selection end
		if (selectionStart == selectionEnd) {
			rectSelect = false;
		}

		// Clear the `magic' caret position used by up/down
		magicCaret = -1;

		scrollToCaret();
	}

	/**
	 * Returns the selected text, or null if no selection is active.
	 */
	@Override
	public final String getSelectedText() {
		if (selectionStart == selectionEnd) {
			return null;
		}

		if (rectSelect) {
			// Return each row of the selection on a new line

			Element map = document.getDefaultRootElement();

			int start = selectionStart - map.getElement(selectionStartLine).getStartOffset();
			int end = selectionEnd - map.getElement(selectionEndLine).getStartOffset();

			// Certain rectangles satisfy this condition...
			if (end < start) {
				int tmp = end;
				end = start;
				start = tmp;
			}

			StringBuffer buf = new StringBuffer();
			Segment seg = new Segment();

			for (int i = selectionStartLine; i <= selectionEndLine; i++) {
				Element lineElement = map.getElement(i);
				int lineStart = lineElement.getStartOffset();
				int lineEnd = lineElement.getEndOffset() - 1;
				int lineLen = lineEnd - lineStart;

				lineStart = Math.min(lineStart + start, lineEnd);
				lineLen = Math.min(end - start, lineEnd - lineStart);

				getText(lineStart, lineLen, seg);
				buf.append(seg.array, seg.offset, seg.count);

				if (i != selectionEndLine) {
					buf.append('\n');
				}
			}

			return buf.toString();
		} else {
			return getText(selectionStart, selectionEnd - selectionStart);
		}
	}

	/**
	 * Replaces the selection with the specified text.
	 * 
	 * @param selectedText
	 *            The replacement text for the selection
	 */
	public void setSelectedText(String selectedText) {
		if (!editable) {
			throw new InternalError("Text component" + " read only");
		}

		document.beginCompoundEdit();

		try {
			if (rectSelect) {
				Element map = document.getDefaultRootElement();

				int start = selectionStart - map.getElement(selectionStartLine).getStartOffset();
				int end = selectionEnd - map.getElement(selectionEndLine).getStartOffset();

				// Certain rectangles satisfy this condition...
				if (end < start) {
					int tmp = end;
					end = start;
					start = tmp;
				}

				int lastNewline = 0;
				int currNewline = 0;

				for (int i = selectionStartLine; i <= selectionEndLine; i++) {
					Element lineElement = map.getElement(i);
					int lineStart = lineElement.getStartOffset();
					int lineEnd = lineElement.getEndOffset() - 1;
					int rectStart = Math.min(lineEnd, lineStart + start);

					document.remove(rectStart, Math.min(lineEnd - rectStart, end - start));

					if (selectedText == null) {
						continue;
					}

					currNewline = selectedText.indexOf('\n', lastNewline);
					if (currNewline == -1) {
						currNewline = selectedText.length();
					}

					document.insertString(rectStart, selectedText.substring(lastNewline, currNewline), null);

					lastNewline = Math.min(selectedText.length(), currNewline + 1);
				}

				if (selectedText != null && currNewline != selectedText.length()) {
					int offset = map.getElement(selectionEndLine).getEndOffset() - 1;
					document.insertString(offset, "\n", null);
					document.insertString(offset + 1, selectedText.substring(currNewline + 1), null);
				}
			} else {
				document.remove(selectionStart, selectionEnd - selectionStart);
				if (selectedText != null) {
					document.insertString(selectionStart, selectedText, null);
				}
			}
		} catch (BadLocationException bl) {
			bl.printStackTrace();
			throw new InternalError("Cannot replace" + " selection");
		}
		// No matter what happends... stops us from leaving document
		// in a bad state
		finally {
			document.endCompoundEdit();
		}

		setCaretPosition(selectionEnd);
	}

	/**
	 * Returns true if this text area is editable, false otherwise.
	 */
	@Override
	public final boolean isEditable() {
		return editable;
	}

	/**
	 * Sets if this component is editable.
	 * 
	 * @param editable
	 *            True if this text area should be editable, false otherwise
	 */
	public final void setEditable(boolean editable) {
		this.editable = editable;
		if (editable) {
			this.grabFocus();
		}
		if (searchPanel != null && searchPanel.getTextComponent() == this) {
			searchPanel.notifyEditablePropertyChange();
		}
	}

	/**
	 * Returns the right click popup menu.
	 */
	public final JPopupMenu getRightClickPopup() {
		return popup;
	}

	/**
	 * Sets the right click popup menu.
	 * 
	 * @param popup
	 *            The popup
	 */
	public final void setRightClickPopup(JPopupMenu popup) {
		this.popup = popup;
	}

	/**
	 * Returns the `magic' caret position. This can be used to preserve the column position when moving up and down lines.
	 */
	public final int getMagicCaretPosition() {
		return magicCaret;
	}

	/**
	 * Sets the `magic' caret position. This can be used to preserve the column position when moving up and down lines.
	 * 
	 * @param magicCaret
	 *            The magic caret position
	 */
	public final void setMagicCaretPosition(int magicCaret) {
		this.magicCaret = magicCaret;
	}

	/**
	 * Similar to <code>setSelectedText()</code>, but overstrikes the appropriate number of characters if overwrite mode is enabled.
	 * 
	 * @param str
	 *            The string
	 * @see #setSelectedText(String)
	 * @see #isOverwriteEnabled()
	 */
	public void overwriteSetSelectedText(String str) {
		// Don't overstrike if there is a selection
		if (!overwrite || selectionStart != selectionEnd) {
			setSelectedText(str);
			return;
		}

		// Don't overstrike if we're on the end of
		// the line
		int caret = getCaretPosition();
		int caretLineEnd = getLineEndOffset(getCaretLine());
		if (caretLineEnd - caret <= str.length()) {
			setSelectedText(str);
			return;
		}

		document.beginCompoundEdit();

		try {
			document.remove(caret, str.length());
			document.insertString(caret, str, null);
		} catch (BadLocationException bl) {
			bl.printStackTrace();
		} finally {
			document.endCompoundEdit();
		}
	}

	/**
	 * Returns true if overwrite mode is enabled, false otherwise.
	 */
	public final boolean isOverwriteEnabled() {
		return overwrite;
	}

	/**
	 * Sets if overwrite mode should be enabled.
	 * 
	 * @param overwrite
	 *            True if overwrite mode should be enabled, false otherwise.
	 */
	public final void setOverwriteEnabled(boolean overwrite) {
		this.overwrite = overwrite;
		painter.invalidateSelectedLines();
	}

	/**
	 * Returns true if the selection is rectangular, false otherwise.
	 */
	public final boolean isSelectionRectangular() {
		return rectSelect;
	}

	/**
	 * Sets if the selection should be rectangular.
	 * 
	 * @param overwrite
	 *            True if the selection should be rectangular, false otherwise.
	 */
	public final void setSelectionRectangular(boolean rectSelect) {
		this.rectSelect = rectSelect;
		painter.invalidateSelectedLines();
	}

	/**
	 * Returns the position of the highlighted bracket (the bracket matching the one before the caret)
	 */
	public final int getBracketPosition() {
		return bracketPosition;
	}

	/**
	 * Returns the line of the highlighted bracket (the bracket matching the one before the caret)
	 */
	public final int getBracketLine() {
		return bracketLine;
	}

	/**
	 * Adds a caret change listener to this text area.
	 * 
	 * @param listener
	 *            The listener
	 */
	@Override
	public final void addCaretListener(CaretListener listener) {
		listenerList.add(CaretListener.class, listener);
	}

	/**
	 * Removes a caret change listener from this text area.
	 * 
	 * @param listener
	 *            The listener
	 */
	@Override
	public final void removeCaretListener(CaretListener listener) {
		listenerList.remove(CaretListener.class, listener);
	}

	/**
	 * Deletes the selected text from the text area and places it into the clipboard.
	 */
	public void cut() {
		if (editable) {
			copy();
			setSelectedText("");
		}
	}

	/**
	 * Places the selected text into the clipboard.
	 */
	public void copy() {
		if (selectionStart != selectionEnd) {
			Clipboard clipboard = getToolkit().getSystemClipboard();

			String selection = getSelectedText();

			int repeatCount = inputHandler.getRepeatCount();
			StringBuffer buf = new StringBuffer();
			for (int i = 0; i < repeatCount; i++) {
				buf.append(selection);
			}

			clipboard.setContents(new StringSelection(buf.toString()), null);
		}
	}

	/**
	 * Inserts the clipboard contents into the text.
	 */
	public void paste() {
		if (editable) {
			Clipboard clipboard = getToolkit().getSystemClipboard();
			try {
				// The MacOS MRJ doesn't convert \r to \n,
				// so do it here
				String selection = ((String) clipboard.getContents(this).getTransferData(DataFlavor.stringFlavor)).replace('\r', '\n');

				int repeatCount = inputHandler.getRepeatCount();
				StringBuffer buf = new StringBuffer();
				for (int i = 0; i < repeatCount; i++) {
					buf.append(selection);
				}
				selection = buf.toString();
				setSelectedText(selection);
			} catch (Exception e) {
				getToolkit().beep();
				System.err.println("Clipboard does not" + " contain a string");
			}
		}
	}

	public void copyLine(boolean down) {
		if (!editable) {
			return;
		}
		int start = getSelectionStart();
		int end = getSelectionEnd();
		if (start == end) {
			return;
		}
		String s = getText();
		if (s.length() == 0) {
			return;
		}
		while (start > 0 && s.charAt(start - 1) != '\n') {
			start--;
		}
		if (end < 1) {
			end = 1;
		}
		while (end < s.length() && s.charAt(end - 1) != '\n') {
			end++;
		}
		String text = s.substring(start, end);
		int insertionPoint;
		if (down) {
			insertionPoint = end;
		} else {
			insertionPoint = start;
		}
		try {
			getDocument().insertString(insertionPoint, text, new SimpleAttributeSet());
			select(insertionPoint, insertionPoint + text.length());
		} catch (BadLocationException e) {
			e.printStackTrace();
			return;
		}

	}

	public void moveLine(boolean down) {
		if (!editable) {
			return;
		}
		int start = getSelectionStart();
		int end = getSelectionEnd();
		if (start == end) {
			return;
		}
		String s = getText();
		if (s.length() == 0) {
			return;
		}
		while (start > 0 && s.charAt(start - 1) != '\n') {
			start--;
		}
		if (end < 1) {
			end = 1;
		}
		while (end < s.length() && s.charAt(end - 1) != '\n') {
			end++;
		}
		String text = s.substring(start, end);
		int insertionPoint;
		if (down) {
			insertionPoint = end + 1;
			while (insertionPoint < s.length() && s.charAt(insertionPoint - 1) != '\n') {
				insertionPoint++;
			}
			insertionPoint -= text.length();
		} else {
			insertionPoint = start > 0 ? start - 1 : 0;
			while (insertionPoint > 0 && s.charAt(insertionPoint - 1) != '\n') {
				insertionPoint--;
			}
		}
		try {
			getDocument().remove(start, text.length());
			getDocument().insertString(insertionPoint, text, new SimpleAttributeSet());
			select(insertionPoint, insertionPoint + text.length());
		} catch (BadLocationException e) {
			e.printStackTrace();
			return;
		}
	}

	/**
	 * Called by the AWT when this component is removed from it's parent. This stops clears the currently focused component.
	 */
	@Override
	public void removeNotify() {
		super.removeNotify();
		if (focusedComponent == this) {
			focusedComponent = null;
		}
	}

	/**
	 * Forwards key events directly to the input handler. This is slightly faster than using a KeyListener because some Swing overhead is
	 * avoided.
	 */
	@Override
	public void processKeyEvent(KeyEvent evt) {
		if (inputHandler == null) {
			return;
		}
		switch (evt.getID()) {
		case KeyEvent.KEY_TYPED:
			inputHandler.keyTyped(evt);
			break;
		case KeyEvent.KEY_PRESSED:
			inputHandler.keyPressed(evt);
			break;
		case KeyEvent.KEY_RELEASED:
			inputHandler.keyReleased(evt);
			break;
		}
	}

	public int getCursorX() {
		return getCaretPosition() - getLineStartOffset(getCaretLine()) + 1;
	}

	public int getCursorY() {
		return getCaretLine() + 1;
	}

	// protected members
	protected static String CENTER = "center";
	protected static String RIGHT = "right";
	protected static String BOTTOM = "bottom";

	protected static JEditTextArea focusedComponent;

	protected static Timer caretTimer;

	protected TextAreaPainter painter;

	JPopupMenu popup;

	protected EventListenerList listenerList;
	protected MutableCaretEvent caretEvent;

	protected boolean caretBlinks;
	protected boolean caretVisible;
	protected boolean blink;

	protected boolean editable;

	protected int firstLine;
	protected int visibleLines;
	@Deprecated
	protected int electricScroll = 0;

	protected int horizontalOffset;

	protected JScrollBar vertical;
	protected JScrollBar horizontal;
	protected boolean scrollBarsInitialized;

	protected InputHandler inputHandler;
	protected SyntaxDocument document;
	protected DocumentHandler documentHandler;

	protected Segment lineSegment;

	protected int selectionStart;
	protected int selectionStartLine;
	protected int selectionEnd;
	protected int selectionEndLine;
	protected boolean biasLeft;

	protected int bracketPosition;
	protected int bracketLine;

	protected int magicCaret;
	protected boolean overwrite;
	protected boolean rectSelect;

	protected TokenMarker tokenMarker = new TextTokenMarker();

	protected void fireCaretEvent() {
		Object[] listeners = listenerList.getListenerList();
		for (int i = listeners.length - 2; i >= 0; i--) {
			if (listeners[i] == CaretListener.class) {
				((CaretListener) listeners[i + 1]).caretUpdate(caretEvent);
			}
		}
	}

	protected void updateBracketHighlight(int newCaretPosition) {
		if (newCaretPosition == 0) {
			bracketPosition = bracketLine = -1;
			return;
		}

		try {
			int offset = TextUtilities.findMatchingBracket(document, newCaretPosition - 1);
			if (offset != -1) {
				bracketLine = getLineOfOffset(offset);
				bracketPosition = offset - getLineStartOffset(bracketLine);
				return;
			}
		} catch (BadLocationException bl) {
			bl.printStackTrace();
		}

		bracketLine = bracketPosition = -1;
	}

	protected void documentChanged(DocumentEvent evt) {
		DocumentEvent.ElementChange ch = evt.getChange(document.getDefaultRootElement());

		int count;
		if (ch == null) {
			count = 0;
		} else {
			count = ch.getChildrenAdded().length - ch.getChildrenRemoved().length;
		}

		int line = getLineOfOffset(evt.getOffset());
		if (count == 0) {
			painter.invalidateLine(line);
		}
		// do magic stuff
		else if (line < firstLine) {
			setFirstLine(firstLine + count);
		}
		// end of magic stuff
		else {
			painter.invalidateLineRange(line, firstLine + visibleLines);
		}
		updateScrollBars();
	}

	class ScrollLayout implements LayoutManager {
		@Override
		public void addLayoutComponent(String name, Component comp) {
			if (name.equals(CENTER)) {
				center = comp;
			} else if (name.equals(RIGHT)) {
				right = comp;
			} else if (name.equals(BOTTOM)) {
				bottom = comp;
			} else if (name.equals(LEFT_OF_SCROLLBAR)) {
				leftOfScrollBar.addElement(comp);
			}
		}

		@Override
		public void removeLayoutComponent(Component comp) {
			if (center == comp) {
				center = null;
			} else if (right == comp) {
				// right = null;
			}
			if (bottom == comp) {
				// bottom = null;
			} else {
				leftOfScrollBar.removeElement(comp);
			}
		}

		@Override
		public Dimension preferredLayoutSize(Container parent) {
			Dimension dim = new Dimension();
			Insets insets = getInsets();
			dim.width = insets.left + insets.right;
			dim.height = insets.top + insets.bottom;

			Dimension centerPref = center.getPreferredSize();
			dim.width += centerPref.width;
			dim.height += centerPref.height;
			Dimension rightPref = right.getPreferredSize();
			dim.width += rightPref.width;
			Dimension bottomPref = bottom.getPreferredSize();
			dim.height += bottomPref.height;

			return dim;
		}

		@Override
		public Dimension minimumLayoutSize(Container parent) {
			Dimension dim = new Dimension();
			Insets insets = getInsets();
			dim.width = insets.left + insets.right;
			dim.height = insets.top + insets.bottom;

			Dimension centerPref = center.getMinimumSize();
			dim.width += centerPref.width;
			dim.height += centerPref.height;
			Dimension rightPref = right.getMinimumSize();
			dim.width += rightPref.width;
			Dimension bottomPref = bottom.getMinimumSize();
			dim.height += bottomPref.height;

			return dim;
		}

		@Override
		public void layoutContainer(Container parent) {
			Dimension size = parent.getSize();
			Insets insets = parent.getInsets();
			int itop = insets.top;
			int ileft = insets.left;
			int ibottom = insets.bottom;
			int iright = insets.right;

			int rightWidth = right != null ? right.getPreferredSize().width : 0;
			int bottomHeight = bottom.getPreferredSize().height;
			int centerWidth = size.width - rightWidth - ileft - iright;
			int centerHeight = size.height - bottomHeight - itop - ibottom;

			center.setBounds(ileft, itop, centerWidth, centerHeight);

			if (right != null) {
				right.setBounds(ileft + centerWidth, itop, rightWidth, centerHeight);
			}
			// Lay out all status components, in order
			Enumeration status = leftOfScrollBar.elements();
			while (status.hasMoreElements()) {
				Component comp = (Component) status.nextElement();
				Dimension dim = comp.getPreferredSize();
				comp.setBounds(ileft, itop + centerHeight, dim.width, bottomHeight);
				ileft += dim.width;
			}

			bottom.setBounds(ileft, itop + centerHeight, size.width - rightWidth - ileft - iright, bottomHeight);
		}

		// private members
		private Component center;
		private Component right;
		private Component bottom;
		private Vector<Component> leftOfScrollBar = new Vector<Component>();
	}

	static class CaretBlinker implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent evt) {
			if (focusedComponent != null && focusedComponent.hasFocus()) {
				focusedComponent.blinkCaret();
			}
		}
	}

	class MutableCaretEvent extends CaretEvent {
		MutableCaretEvent() {
			super(JEditTextArea.this);
		}

		@Override
		public int getDot() {
			return getCaretPosition();
		}

		@Override
		public int getMark() {
			return getMarkPosition();
		}
	}

	class AdjustHandler implements AdjustmentListener {
		@Override
		public void adjustmentValueChanged(final AdjustmentEvent evt) {
			if (!scrollBarsInitialized) {
				return;
			}

			// If this is not done, mousePressed events accumilate
			// and the result is that scrolling doesn't stop after
			// the mouse is released
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					if (evt.getAdjustable() == vertical) {
						setFirstLine(vertical.getValue());
					} else {
						setHorizontalOffset(-horizontal.getValue());
					}
				}
			});
		}
	}

	class ComponentHandler extends ComponentAdapter {
		@Override
		public void componentResized(ComponentEvent evt) {
			recalculateVisibleLines();
			scrollBarsInitialized = true;
		}
	}

	class DocumentHandler implements DocumentListener {
		@Override
		public void insertUpdate(DocumentEvent evt) {
			documentChanged(evt);

			int offset = evt.getOffset();
			int length = evt.getLength();

			int newStart;
			int newEnd;

			if (selectionStart > offset || selectionStart == selectionEnd && selectionStart == offset) {
				newStart = selectionStart + length;
			} else {
				newStart = selectionStart;
			}

			if (selectionEnd >= offset) {
				newEnd = selectionEnd + length;
			} else {
				newEnd = selectionEnd;
			}

			select(newStart, newEnd);
		}

		@Override
		public void removeUpdate(DocumentEvent evt) {
			documentChanged(evt);

			int offset = evt.getOffset();
			int length = evt.getLength();

			int newStart;
			int newEnd;

			if (selectionStart > offset) {
				if (selectionStart > offset + length) {
					newStart = selectionStart - length;
				} else {
					newStart = offset;
				}
			} else {
				newStart = selectionStart;
			}

			if (selectionEnd > offset) {
				if (selectionEnd > offset + length) {
					newEnd = selectionEnd - length;
				} else {
					newEnd = offset;
				}
			} else {
				newEnd = selectionEnd;
			}

			select(newStart, newEnd);
		}

		@Override
		public void changedUpdate(DocumentEvent evt) {
		}
	}

	class DragHandler implements MouseMotionListener {
		@Override
		public void mouseDragged(MouseEvent evt) {
			if (popup != null && popup.isVisible()) {
				return;
			}

			setSelectionRectangular((evt.getModifiers() & InputEvent.CTRL_MASK) != 0);
			select(getMarkPosition(), xyToOffset(evt.getX(), evt.getY()));
		}

		@Override
		public void mouseMoved(MouseEvent evt) {
		}
	}

	class FocusHandler implements FocusListener {
		@Override
		public void focusGained(FocusEvent evt) {
			setCaretVisible(true);
			if (searchPanel != null) {
				searchPanel.setTextComponent(JEditTextArea.this);
			}
			focusedComponent = JEditTextArea.this;
		}

		@Override
		public void focusLost(FocusEvent evt) {
			/*
			 * if (searchDialog!=null && evt.getOppositeComponent()!=null &&
			 * SwingUtilities.isDescendingFrom(evt.getOppositeComponent(),searchDialog)) return;
			 */
			setCaretVisible(false);
			focusedComponent = null;
		}
	}

	class MouseHandler extends MouseAdapter {
		@Override
		public void mousePressed(MouseEvent evt) {
			requestFocus();

			// Focus events not fired sometimes?
			setCaretVisible(true);
			focusedComponent = JEditTextArea.this;

			if ((evt.getModifiers() & InputEvent.BUTTON3_MASK) != 0 && popup != null) {
				popup.show(painter, evt.getX(), evt.getY());
				return;
			}

			int line = yToLine(evt.getY());
			int offset = xToOffset(line, evt.getX());
			int dot = getLineStartOffset(line) + offset;

			switch (evt.getClickCount()) {
			case 1:
				doSingleClick(evt, line, offset, dot);
				break;
			case 2:
				// It uses the bracket matching stuff, so
				// it can throw a BLE
				try {
					doDoubleClick(evt, line, offset, dot);
				} catch (BadLocationException bl) {
					bl.printStackTrace();
				}
				break;
			case 3:
				doTripleClick(evt, line, offset, dot);
				break;
			}
		}

		private void doSingleClick(MouseEvent evt, int line, int offset, int dot) {
			if ((evt.getModifiers() & InputEvent.SHIFT_MASK) != 0) {
				rectSelect = (evt.getModifiers() & InputEvent.CTRL_MASK) != 0;
				select(getMarkPosition(), dot);
			} else {
				setCaretPosition(dot);
			}
		}

		private void doDoubleClick(MouseEvent evt, int line, int offset, int dot) throws BadLocationException {
			// Ignore empty lines
			if (getLineLength(line) == 0) {
				return;
			}

			try {
				int bracket = TextUtilities.findMatchingBracket(document, Math.max(0, dot - 1));
				if (bracket != -1) {
					int mark = getMarkPosition();
					// Hack
					if (bracket > mark) {
						bracket++;
						mark--;
					}
					select(mark, bracket);
					return;
				}
			} catch (BadLocationException bl) {
				bl.printStackTrace();
			}

			// Ok, it's not a bracket... select the word
			String lineText = getLineText(line);
			char ch = lineText.charAt(Math.max(0, offset - 1));

			String noWordSep = (String) document.getProperty("noWordSep");
			if (noWordSep == null) {
				noWordSep = "";
			}

			// If the user clicked on a non-letter char,
			// we select the surrounding non-letters
			boolean selectNoLetter = !Character.isLetterOrDigit(ch) && noWordSep.indexOf(ch) == -1;

			int wordStart = 0;

			for (int i = offset - 1; i >= 0; i--) {
				ch = lineText.charAt(i);
				if (selectNoLetter ^ (!Character.isLetterOrDigit(ch) && noWordSep.indexOf(ch) == -1)) {
					wordStart = i + 1;
					break;
				}
			}

			int wordEnd = lineText.length();
			for (int i = offset; i < lineText.length(); i++) {
				ch = lineText.charAt(i);
				if (selectNoLetter ^ (!Character.isLetterOrDigit(ch) && noWordSep.indexOf(ch) == -1)) {
					wordEnd = i;
					break;
				}
			}

			int lineStart = getLineStartOffset(line);
			select(lineStart + wordStart, lineStart + wordEnd);

			/*
			 * String lineText = getLineText(line); String noWordSep = (String)document.getProperty("noWordSep"); int wordStart =
			 * TextUtilities.findWordStart(lineText,offset,noWordSep); int wordEnd = TextUtilities.findWordEnd(lineText,offset,noWordSep);
			 * 
			 * int lineStart = getLineStartOffset(line); select(lineStart + wordStart,lineStart + wordEnd);
			 */
		}

		private void doTripleClick(MouseEvent evt, int line, int offset, int dot) {
			select(getLineStartOffset(line), getLineEndOffset(line) - 1);
		}
	}

	class CaretUndo extends AbstractUndoableEdit {
		private int start;
		private int end;

		CaretUndo(int start, int end) {
			this.start = start;
			this.end = end;
		}

		@Override
		public boolean isSignificant() {
			return false;
		}

		@Override
		public String getPresentationName() {
			return "caret move";
		}

		@Override
		public void undo() throws CannotUndoException {
			super.undo();

			select(start, end);
		}

		@Override
		public void redo() throws CannotRedoException {
			super.redo();

			select(start, end);
		}

		@Override
		public boolean addEdit(UndoableEdit edit) {
			if (edit instanceof CaretUndo) {
				CaretUndo cedit = (CaretUndo) edit;
				start = cedit.start;
				end = cedit.end;
				cedit.die();

				return true;
			} else {
				return false;
			}
		}
	}

	/**
	 * Overrides paint
	 * 
	 * @see javax.swing.JComponent#paint(java.awt.Graphics)
	 */
	@Override
	public void paint(Graphics g) {
		((Graphics2D) g).setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		((Graphics2D) g).setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
		super.paint(g);
		((Graphics2D) g).setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_DEFAULT);
		((Graphics2D) g).setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_DEFAULT);
	}

	static {
		caretTimer = new Timer(500, new CaretBlinker());
		caretTimer.setInitialDelay(500);
		caretTimer.start();
	}

	public int getColumns() {
		return painter.getColumns();
	}

	public void setColumns(int columns) {
		painter.setColumns(columns);
	}

	public int getRows() {
		return painter.getRows();
	}

	public void setRows(int rows) {
		painter.setRows(rows);
	}

	protected Vector<CursorPositionListener> _cursorPositionListeners = new Vector<CursorPositionListener>();

	public void addToCursorPositionListener(CursorPositionListener listener) {
		_cursorPositionListeners.add(listener);
	}

	public void removeFromCursorPositionListener(CursorPositionListener listener) {
		_cursorPositionListeners.remove(listener);
	}

	public static class DisplayContext {

		private String content;
		private int firstVisibleLine;
		private int selectionStart;
		private int selectionEnd;
		private int caretPosition;

		public DisplayContext(String content, int firstVisibleLine, int selectionStart, int selectionEnd, int caretPosition) {
			this.content = content;
			this.firstVisibleLine = firstVisibleLine;
			this.selectionStart = selectionStart;
			this.selectionEnd = selectionEnd;
			this.caretPosition = caretPosition;
		}

		public DisplayContext(DisplayContext context) {
			this.content = context.content;
			this.firstVisibleLine = context.firstVisibleLine;
			this.selectionStart = context.selectionStart;
			this.selectionEnd = context.selectionEnd;
			this.caretPosition = context.caretPosition;
		}

		public String getContent() {
			return content;
		}

		public void setContent(String content) {
			this.content = content;
		}

		public int getFirstVisibleLine() {
			return firstVisibleLine;
		}

		public int getSelectionStart() {
			return selectionStart;
		}

		public int getSelectionEnd() {
			return selectionEnd;
		}

		public int getCaretPosition() {
			return caretPosition;
		}

	}

	public DisplayContext getDisplayContext() {
		return new DisplayContext("default", getFirstLine(), getSelectionStart(), getSelectionEnd(), getCaretPosition());
	}

	public static interface CursorPositionListener {
		public void positionChanged(int newPosX, int newPosY);
	}

	public void applyDisplayContext(DisplayContext context) {
		if (context == null) {
			return;
		}
		try {
			if (context.getCaretPosition() >= 0 && context.getCaretPosition() < getDocumentLength()) {
				setCaretPosition(context.getCaretPosition());
			}
			setSelectionEnd(context.getSelectionEnd());
			setSelectionStart(context.getSelectionStart());
			scrollTo(context.getFirstVisibleLine(), 0);
		} catch (IllegalArgumentException e) {
			System.err.println("org.openflexo.jedit.JEditTextArea : Unexpected IllegalArgumentException occured: " + e.getMessage());
		}
	}

	public void setTextKeepDisplayContext(String someText) {
		DisplayContext context = getDisplayContext();
		setText(someText);
		applyDisplayContext(context);
	}

	public void showFindDialog() {
		if (searchDialog == null) {
			createSearchDialog();
		} else if (SwingUtilities.getWindowAncestor(this) != null && searchDialog.getOwner() != SwingUtilities.getWindowAncestor(this)) {
			createSearchDialog();
		}
		if (getSelectedText() != null && getSelectedText().length() != 0) {
			searchPanel.setSearchedText(getSelectedText());
		}
		searchDialog.setVisible(true);
		searchPanel.onFocus();
	}

	private void createSearchDialog() {
		if (searchDialog != null) {
			searchDialog.dispose();
		}
		Window windowAncestor = SwingUtilities.getWindowAncestor(this);
		if (windowAncestor != null) {
			if (windowAncestor instanceof Dialog) {
				if (DIALOG_FACTORY != null) {
					searchDialog = DIALOG_FACTORY.getNewDialog((Dialog) windowAncestor,
							FlexoLocalization.localizedForKey("find_and_replace"), false);
				} else {
					searchDialog = new Dialog((Dialog) windowAncestor, FlexoLocalization.localizedForKey("find_and_replace"), false);
				}
			} else if (windowAncestor instanceof Frame) {
				if (DIALOG_FACTORY != null) {
					searchDialog = DIALOG_FACTORY.getNewDialog((Frame) windowAncestor,
							FlexoLocalization.localizedForKey("find_and_replace"), false);
				} else {
					searchDialog = new Dialog((Frame) windowAncestor, FlexoLocalization.localizedForKey("find_and_replace"), false);
				}
			}
		}
		if (searchDialog == null) {
			if (DIALOG_FACTORY != null) {
				searchDialog = DIALOG_FACTORY.getNewDialog((Frame) windowAncestor, FlexoLocalization.localizedForKey("find_and_replace"),
						false);
			} else {
				searchDialog = new Dialog((Frame) windowAncestor, FlexoLocalization.localizedForKey("find_and_replace"), false);
			}
		}
		if (searchPanel == null) {
			searchDialog.add(searchPanel = new TextSearchPanel());
		} else {
			searchDialog.add(searchPanel);
		}
		searchPanel.setTextComponent(this);
		searchDialog.pack();
		if (searchDialog instanceof JDialog) {
			((JDialog) searchDialog).setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
			((JDialog) searchDialog).getRootPane().setDefaultButton(searchPanel.getDefaultButton());
		}
		searchDialog.setMinimumSize(searchDialog.getSize());
		if (ToolBox.getPLATFORM() == ToolBox.MACOS) {
			searchDialog.setResizable(false);
		}
	}

	static TextSearchPanel searchPanel;
	private static Dialog searchDialog;

	public void enableSyntaxColor() {
		document.setTokenMarker(tokenMarker);
		repaint();
	}

	private TokenMarker defaultMarker = new TextTokenMarker();

	public void disableSyntaxColor() {
		document.setTokenMarker(defaultMarker);
		repaint();
	}

	public void toggleSyntaxColoring() {
		if (!isSyntaxColoringEnabled()) {
			enableSyntaxColor();
		} else {
			disableSyntaxColor();
		}
	}

	/**
	 * @return
	 */
	public boolean isSyntaxColoringEnabled() {
		return document.getTokenMarker() != defaultMarker;
	}

}
