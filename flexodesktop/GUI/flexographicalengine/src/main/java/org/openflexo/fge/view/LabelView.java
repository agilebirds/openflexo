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
package org.openflexo.fge.view;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.util.Arrays;
import java.util.Observable;
import java.util.concurrent.Callable;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.JViewport;
import javax.swing.SwingUtilities;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;

import org.openflexo.fge.DrawingGraphicalRepresentation;
import org.openflexo.fge.GraphicalRepresentation;
import org.openflexo.fge.GraphicalRepresentation.LabelMetricsProvider;
import org.openflexo.fge.GraphicalRepresentation.ParagraphAlignment;
import org.openflexo.fge.ShapeGraphicalRepresentation;
import org.openflexo.fge.controller.DrawingController;
import org.openflexo.fge.controller.DrawingPalette;
import org.openflexo.fge.graphics.TextStyle;
import org.openflexo.fge.notifications.FGENotification;
import org.openflexo.fge.notifications.LabelHasMoved;
import org.openflexo.fge.notifications.LabelWillMove;
import org.openflexo.fge.notifications.ObjectHasMoved;
import org.openflexo.fge.notifications.ObjectHasResized;
import org.openflexo.fge.notifications.ObjectWillMove;
import org.openflexo.fge.notifications.ObjectWillResize;
import org.openflexo.fge.view.listener.LabelViewMouseListener;
import org.openflexo.swing.FlexoSwingUtils;
import org.openflexo.toolbox.ToolBox;

public class LabelView<O> extends JScrollPane implements FGEView<O>, LabelMetricsProvider {

	private boolean mouseInsideLabel = false;

	/**
	 * This class tries to keep trace if the mouse is inside the label or not. This partially works but it heavily relies on the fact that
	 * FGEViewMouseListener will simulate mouse in/out events. In some cases, this may not work.
	 * 
	 * @author Guillaume
	 * 
	 */
	private class InOutMouseListener extends MouseAdapter {

		@Override
		public void mouseEntered(MouseEvent e) {
			mouseInsideLabel = true;
			textComponent.updateCursor();
		}

		@Override
		public void mouseExited(MouseEvent e) {
			mouseInsideLabel = false;
			textComponent.updateCursor();
		}
	}

	public boolean isMouseInsideLabel() {
		return mouseInsideLabel;
	}

	public class TextComponent extends JTextPane {

		public TextComponent() {
			setOpaque(false);
			setEditable(false);
			setAutoscrolls(false);
			setFocusable(true);
		}

		protected void updateCursor() {
			if (getDrawingView() != null) {
				getDrawingView().setCursor(isMouseInsideLabel() ? getCursor() : null);
			}
		}

		@Override
		public void setCursor(Cursor cursor) {
			super.setCursor(cursor);
			updateCursor();
		}

		@Override
		public boolean getScrollableTracksViewportWidth() {
			return true;
		}

		@Override
		public void setDoubleBuffered(boolean aFlag) {
			super.setDoubleBuffered(aFlag && ToolBox.getPLATFORM() == ToolBox.MACOS);
		}

		@Override
		public void setEditable(boolean b) {
			super.setEditable(b);
			setEnabled(b);
			if (!initialized) {
				return;
			}
			setDoubleBuffered(!b);
			if (b) {
				removeFGEMouseListener();
				requestFocus();
				selectAll();
			} else {
				addFGEMouseListener();
			}
		}

	}

	private static final Logger logger = Logger.getLogger(LabelView.class.getPackage().getName());

	private GraphicalRepresentation<O> graphicalRepresentation;
	private LabelViewMouseListener mouseListener;
	private DrawingController<?> controller;
	private FGEView<?> delegateView;
	private boolean isEditing = false;

	private TextComponent textComponent;

	private boolean initialized = false;

	public LabelView(GraphicalRepresentation<O> graphicalRepresentation, DrawingController<?> controller, FGEView<?> delegateView) {
		this.controller = controller;
		this.graphicalRepresentation = graphicalRepresentation;
		this.delegateView = delegateView;
		this.mouseListener = new LabelViewMouseListener(graphicalRepresentation, this);
		this.textComponent = new TextComponent();
		this.textComponentListener = new LabelDocumentListener();
		textComponent.addMouseListener(new InOutMouseListener());
		setViewportView(textComponent);
		setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
		// Note: if for debug purposes you add a Border to the textComponent, this could mess up the labels preferredSize computation.
		getViewport().setBorder(null);
		setBorder(null);
		setOpaque(false);
		getViewport().setOpaque(false);
		graphicalRepresentation.setLabelMetricsProvider(this);
		textComponent.setLocation(0, 0);
		updateFont();
		updateText();
		getGraphicalRepresentation().addObserver(this);
		validate();
		initialized = true;
		textComponent.setEditable(false);
	}

	public void registerTextListener() {
		textComponent.addKeyListener(textComponentListener);
		textComponent.getDocument().addDocumentListener(textComponentListener);
		textComponent.addCaretListener(textComponentListener);
	}

	public void unregisterTextListener() {
		textComponent.removeKeyListener(textComponentListener);
		textComponent.getDocument().removeDocumentListener(textComponentListener);
		textComponent.removeCaretListener(textComponentListener);
	}

	@Override
	protected JViewport createViewport() {
		return new JViewport() {
			@Override
			public void setViewPosition(Point p) {
				// We don't want to scroll so we prevent the view port
				// from moving.
			}
		};
	}

	public TextComponent getTextComponent() {
		return textComponent;
	}

	private volatile boolean isDeleted = false;

	private LabelDocumentListener textComponentListener;

	@Override
	public boolean isDeleted() {
		return isDeleted;
	}

	@Override
	public synchronized void delete() {
		if (logger.isLoggable(Level.FINE)) {
			logger.fine("Delete LabelView for " + getGraphicalRepresentation());
		}
		if (getController() != null && getController().getEditedLabel() == this) {
			getController().resetEditedLabel(this);
		}
		removeFGEMouseListener();
		FGELayeredView<?> parentView = getParentView();
		if (parentView != null) {
			// logger.warning("Unexpected not null parent, proceeding anyway");
			parentView.remove(this);
			parentView.revalidate();
			if (getPaintManager() != null) {
				getPaintManager().repaint(parentView);
			}
		}
		if (getGraphicalRepresentation() != null) {
			getGraphicalRepresentation().deleteObserver(this);
			if (graphicalRepresentation instanceof ShapeGraphicalRepresentation) {
				((ShapeGraphicalRepresentation<O>) graphicalRepresentation).setLabelMetricsProvider(null);
			}
		}
		isDeleted = true;
		controller = null;
		mouseListener = null;
		graphicalRepresentation = null;
	}

	public void enableTextComponentMouseListeners() {
		removeFGEMouseListener();
	}

	public void disableTextComponentMouseListeners() {
		addFGEMouseListener();
	}

	@Override
	public O getModel() {
		return getDrawable();
	}

	public O getDrawable() {
		return getGraphicalRepresentation().getDrawable();
	}

	@Override
	public DrawingView<?> getDrawingView() {
		if (getController() != null) {
			return getController().getDrawingView();
		}
		return null;
	}

	@Override
	public LabelView<O> getLabelView() {
		return this;
	}

	@Override
	public FGELayeredView<?> getParent() {
		return (FGELayeredView<?>) super.getParent();
	}

	public FGELayeredView<?> getParentView() {
		return getParent();
	}

	@Override
	public GraphicalRepresentation<O> getGraphicalRepresentation() {
		return graphicalRepresentation;
	}

	public DrawingGraphicalRepresentation<?> getDrawingGraphicalRepresentation() {
		return graphicalRepresentation.getDrawingGraphicalRepresentation();
	}

	@Override
	public double getScale() {
		if (getController() != null) {
			return getController().getScale();
		} else {
			return 1.0;
		}
	}

	@Override
	public void rescale() {
		updateFont();
	}

	@Override
	public void paint(Graphics g) {
		boolean skipPaint = getPaintManager().isPaintingCacheEnabled() && getPaintManager().getDrawingView().isBuffering()
				&& (getPaintManager().isTemporaryObject(getGraphicalRepresentation()) || isEditing);
		if (skipPaint) {
			return;
		}
		super.paint(g);
	}

	@Override
	public DrawingController<?> getController() {
		return controller;
	}

	@Override
	public synchronized void update(final Observable o, final Object aNotification) {
		if (isDeleted) {
			// logger.warning("Received notifications for deleted view: observable="+(o!=null?o.getClass().getSimpleName():"null"));
			return;
		}
		if (!SwingUtilities.isEventDispatchThread()) {
			SwingUtilities.invokeLater(new Runnable() {

				@Override
				public void run() {
					update(o, aNotification);
				}
			});
		} else {
			// System.out.println("Received: "+aNotification);

			if (aNotification instanceof FGENotification) {
				FGENotification notification = (FGENotification) aNotification;
				if (notification.getParameter() == GraphicalRepresentation.Parameters.text) {
					updateText();
					getPaintManager().repaint(this);
				} else if (notification.getParameter() == GraphicalRepresentation.Parameters.textStyle) {
					updateFont();
					getPaintManager().repaint(this);
				} else if (notification.getParameter() == GraphicalRepresentation.Parameters.paragraphAlignment) {
					updateFont();
					getPaintManager().repaint(this);
				} else if (notification.getParameter() == GraphicalRepresentation.Parameters.horizontalTextAlignment
						|| notification.getParameter() == GraphicalRepresentation.Parameters.verticalTextAlignment) {
					updateBounds();
					getPaintManager().repaint(this);
				} else if (notification.getParameter() == ShapeGraphicalRepresentation.Parameters.relativeTextX
						|| notification.getParameter() == ShapeGraphicalRepresentation.Parameters.relativeTextY
						|| notification.getParameter() == GraphicalRepresentation.Parameters.absoluteTextX
						|| notification.getParameter() == GraphicalRepresentation.Parameters.absoluteTextY
						|| notification.getParameter() == ShapeGraphicalRepresentation.Parameters.isFloatingLabel) {
					updateBounds();
					getPaintManager().repaint(this);
				} else if (notification instanceof ObjectWillMove || notification instanceof ObjectWillResize
						|| notification instanceof LabelWillMove) {
					setDoubleBuffered(false);
					if (notification instanceof LabelWillMove) {
						getPaintManager().addToTemporaryObjects(getGraphicalRepresentation());
						getPaintManager().invalidate(getGraphicalRepresentation());
					}
				} else if (notification instanceof ObjectHasMoved || notification instanceof ObjectHasResized
						|| notification instanceof LabelHasMoved) {
					setDoubleBuffered(true);
					if (notification instanceof LabelHasMoved) {
						getPaintManager().removeFromTemporaryObjects(getGraphicalRepresentation());
					}
				}
			}
		}
	}

	protected void updateBounds() {
		updateBounds(true);
	}

	protected synchronized void updateBounds(final boolean repeat) {
		if (isDeleted()) {
			return;
		}
		if (!SwingUtilities.isEventDispatchThread()) {
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					updateBounds(repeat);
				}
			});
			return;
		}
		Rectangle bounds = graphicalRepresentation.getLabelBounds(getScale());
		if (bounds.isEmpty() || bounds.width < 5) {
			bounds.width = 20;
			bounds.height = getFont().getSize();
		}
		if (!bounds.equals(getBounds())) {
			setBounds(bounds);
			validate();
			if (repeat) {
				updateBoundsLater();
			}
		}
	}

	public void updateBoundsLater() {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				updateBounds(true);
			}
		});
	}

	@Override
	public Dimension getScaledPreferredDimension(double scale) {
		Dimension preferredSize = getCurrentPreferredSize(scale);
		if (scale == getScale()) {
			return preferredSize;
		} else {
			Dimension d = preferredSize;
			d.width *= scale;
			d.height *= scale;
			d.width /= getScale();
			d.height /= getScale();
			return d;
		}
	}

	private class PreferredSizeRetriever implements Callable<Dimension> {

		private double scale;

		protected PreferredSizeRetriever(double scale) {
			super();
			this.scale = scale;
		}

		@Override
		public Dimension call() {
			if (isDeleted()) {
				return getSize();
			}
			return getCurrentPreferredSize(scale);
		}

	}

	private Dimension getCurrentPreferredSize(final double scale) {
		if (isDeleted || getGraphicalRepresentation() == null) {
			return getSize();
		}
		if (!SwingUtilities.isEventDispatchThread()) {
			final PreferredSizeRetriever retriever = new PreferredSizeRetriever(scale);
			try {
				return FlexoSwingUtils.syncRunInEDT(retriever);
			} catch (Exception e) {
				if (logger.isLoggable(Level.SEVERE)) {
					logger.log(Level.SEVERE, "Exception when computing preferredSize of " + this, e);
				}

			}
		}
		int width = getGraphicalRepresentation().getAvailableLabelWidth(scale);
		if (getGraphicalRepresentation().getLineWrap()) {
			textComponent.setSize(width, Short.MAX_VALUE);
		}

		Dimension preferredSize = textComponent.getPreferredScrollableViewportSize();
		if (preferredSize.width > width) {
			preferredSize.width = width;
		}
		return preferredSize;
	}

	private void updateFont() {
		AffineTransform at = AffineTransform.getScaleInstance(getScale(), getScale());
		TextStyle ts = getGraphicalRepresentation().getTextStyle();
		if (ts == null) {
			ts = TextStyle.makeDefault();
		}
		if (ts.getOrientation() != 0) {
			at.concatenate(AffineTransform.getRotateInstance(Math.toRadians(ts.getOrientation())));
		}
		Font font = ts.getFont().deriveFont(at);
		textComponent.setFont(font);
		SimpleAttributeSet set = new SimpleAttributeSet();
		if (getGraphicalRepresentation().getParagraphAlignment() == ParagraphAlignment.CENTER) {
			StyleConstants.setAlignment(set, StyleConstants.ALIGN_CENTER);
		} else if (getGraphicalRepresentation().getParagraphAlignment() == ParagraphAlignment.LEFT) {
			StyleConstants.setAlignment(set, StyleConstants.ALIGN_LEFT);
		} else if (getGraphicalRepresentation().getParagraphAlignment() == ParagraphAlignment.RIGHT) {
			StyleConstants.setAlignment(set, StyleConstants.ALIGN_RIGHT);
		} else if (getGraphicalRepresentation().getParagraphAlignment() == ParagraphAlignment.JUSTIFY) {
			StyleConstants.setAlignment(set, StyleConstants.ALIGN_JUSTIFIED);
		}
		textComponent.setOpaque(ts.getIsBackgroundColored());
		textComponent.setBackground(ts.getBackgroundColor());
		StyleConstants.setFontFamily(set, font.getFamily());
		StyleConstants.setFontSize(set, (int) (ts.getFont().getSize() * getScale()));
		if (font.isBold()) {
			StyleConstants.setBold(set, true);
		}
		if (font.isItalic()) {
			StyleConstants.setItalic(set, true);
		}
		Color color = ts.getColor();
		if (color == null) {
			color = Color.BLACK;
		}
		StyleConstants.setForeground(set, color);
		textComponent.setForeground(color);
		textComponent.setDisabledTextColor(color);
		textComponent.setParagraphAttributes(set, true);
		textComponent.validate();
		updateBounds();
	}

	@Override
	public void setDoubleBuffered(boolean aFlag) {
		super.setDoubleBuffered(aFlag && ToolBox.getPLATFORM() == ToolBox.MACOS);
		if (textComponent != null) {
			textComponent.setDoubleBuffered(aFlag);
		}
	}

	private void updateText() {
		if (isEditing || isDeleted) {
			return;
		}
		if (!SwingUtilities.isEventDispatchThread()) {
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					updateText();
				}
			});
			return;
		}
		if (getGraphicalRepresentation().hasText()) {
			textComponent.setText(getGraphicalRepresentation().getText());
		} else {
			textComponent.setText("");
		}
		updateBounds();
	}

	public void addFGEMouseListener() {
		/*if (!Arrays.asList(getMouseListeners()).contains(mouseListener)) {
			addMouseListener(mouseListener);
		}
		if (!Arrays.asList(getMouseMotionListeners()).contains(mouseListener)) {
			addMouseMotionListener(mouseListener);
		}*/
		if (!Arrays.asList(textComponent.getMouseListeners()).contains(mouseListener)) {
			textComponent.addMouseListener(mouseListener);
		}
		if (!Arrays.asList(textComponent.getMouseMotionListeners()).contains(mouseListener)) {
			textComponent.addMouseMotionListener(mouseListener);
		}
	}

	public void removeFGEMouseListener() {
		// removeMouseListener(mouseListener);
		// removeMouseMotionListener(mouseListener);
		textComponent.removeMouseListener(mouseListener);
		textComponent.removeMouseMotionListener(mouseListener);
	}

	public void startEdition() {
		if (!getGraphicalRepresentation().getIsLabelEditable()) {
			return;
		}
		if (logger.isLoggable(Level.INFO)) {
			logger.info("Start edition of " + getGraphicalRepresentation());
		}
		isEditing = true;
		registerTextListener();
		textComponent.setEditable(true);
		setDoubleBuffered(false);
		if (getController() != null) {
			getController().setEditedLabel(LabelView.this);
		}
		getGraphicalRepresentation().notifyLabelWillBeEdited();
		getPaintManager().invalidate(getGraphicalRepresentation());
		getPaintManager().addToTemporaryObjects(getGraphicalRepresentation());
		repaint();
	}

	public void stopEdition() {
		if (!isEditing) {
			return;
		}

		// If not continuous edition, do it now
		if (!getGraphicalRepresentation().getContinuousTextEditing()) {
			getGraphicalRepresentation().setText(textComponent.getText());
		}
		isEditing = false;
		unregisterTextListener();
		if (logger.isLoggable(Level.INFO)) {
			logger.info("Stop edition of " + getGraphicalRepresentation() + " getController()=" + getController());
		}
		textComponent.setEditable(false);
		setDoubleBuffered(true);
		if (getController() != null) {
			getController().resetEditedLabel(LabelView.this);
		}
		if (logger.isLoggable(Level.FINE)) {
			logger.fine("Stop edition of " + getGraphicalRepresentation());
		}
		if (getGraphicalRepresentation() == null || getGraphicalRepresentation().isDeleted()) {
			return;
		}
		getGraphicalRepresentation().notifyLabelHasBeenEdited();
		getPaintManager().removeFromTemporaryObjects(getGraphicalRepresentation());
		getPaintManager().invalidate(getGraphicalRepresentation());
		getPaintManager().repaint(this);
	}

	class LabelDocumentListener extends KeyAdapter implements DocumentListener, CaretListener {
		private boolean wasEdited = false;
		private boolean enterPressed = false;

		@Override
		public void keyPressed(KeyEvent e) {
			if (e.getKeyCode() == KeyEvent.VK_ENTER) {
				enterPressed = true;
				if (!wasEdited) {
					stopEdition();
				}
			}
		}

		@Override
		public void keyReleased(KeyEvent e) {
			if (e.getKeyCode() == KeyEvent.VK_ENTER) {
				enterPressed = false;
				if (!wasEdited) {
					stopEdition();
				}
			} else if (e.getKeyChar() != KeyEvent.CHAR_UNDEFINED && !e.isActionKey()) {
				wasEdited = true;
			}
		}

		@Override
		public void insertUpdate(DocumentEvent event) {
			if (!isEditing) {
				return;
			}
			if (getGraphicalRepresentation().getIsMultilineAllowed()) {
				// Hack to detect trivial edition (RETURN pressed)
				// logger.info("event: "+event);
				if (!wasEdited && enterPressed) {
					return;
				}
			}
			wasEdited = true;
			if (getGraphicalRepresentation().getContinuousTextEditing()) {
				getGraphicalRepresentation().setText(textComponent.getText());
			}
			updateBoundsLater();
		}

		@Override
		public void removeUpdate(DocumentEvent event) {
			if (!isEditing) {
				return;
			}
			if (getGraphicalRepresentation().getIsMultilineAllowed()) {
				// Hack to detect trivial edition (RETURN pressed)
				// logger.info("event: "+event);
				if (!wasEdited && enterPressed) {
					return;
				}
				// if (!wasEdited) return;
			}
			wasEdited = true;
			if (getGraphicalRepresentation().getContinuousTextEditing()) {
				getGraphicalRepresentation().setText(textComponent.getText());
			}
			updateBoundsLater();
		}

		@Override
		public void changedUpdate(DocumentEvent event) {
			if (!isEditing) {
				return;
			}
			wasEdited = true;
			if (getGraphicalRepresentation().getContinuousTextEditing()) {
				getGraphicalRepresentation().setText(textComponent.getText());
			}
			updateBoundsLater();
		}

		@Override
		public void caretUpdate(CaretEvent e) {
			// If the whole text is selected, we say that the text was not edited
			int start = textComponent.getSelectionStart();
			int end = textComponent.getSelectionEnd();
			if (start != end) {
				if (end == 0 && start == textComponent.getDocument().getLength() || start == 0
						&& end == textComponent.getDocument().getLength()) {
					wasEdited = false;
				}
			} else {// Otherwise, it means that user has moved manually the cursor, and we consider he wants to edit the text
				wasEdited = true;
			}
		}
	}

	@Override
	public void registerPalette(DrawingPalette aPalette) {
		// Not applicable
	}

	@Override
	public FGEPaintManager getPaintManager() {
		return getDrawingView().getPaintManager();
	}

	public boolean isEditing() {
		return isEditing;
	}

	@Override
	public String getToolTipText(MouseEvent event) {
		return getController().getToolTipText();
	}

}
