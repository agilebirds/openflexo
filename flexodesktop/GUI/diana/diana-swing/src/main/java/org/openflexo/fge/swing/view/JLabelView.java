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
package org.openflexo.fge.swing.view;

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

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.JViewport;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingUtilities;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.plaf.basic.BasicScrollPaneUI;
import javax.swing.plaf.basic.BasicTextPaneUI;
import javax.swing.plaf.basic.BasicViewportUI;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

import org.openflexo.fge.Drawing.DrawingTreeNode;
import org.openflexo.fge.FGEConstants;
import org.openflexo.fge.FGECoreUtils;
import org.openflexo.fge.GraphicalRepresentation;
import org.openflexo.fge.GraphicalRepresentation.LabelMetricsProvider;
import org.openflexo.fge.GraphicalRepresentation.ParagraphAlignment;
import org.openflexo.fge.ShapeGraphicalRepresentation;
import org.openflexo.fge.TextStyle;
import org.openflexo.fge.control.AbstractDianaEditor;
import org.openflexo.fge.control.DianaInteractiveViewer;
import org.openflexo.fge.control.tools.DianaPalette;
import org.openflexo.fge.notifications.FGENotification;
import org.openflexo.fge.notifications.LabelHasMoved;
import org.openflexo.fge.notifications.LabelWillMove;
import org.openflexo.fge.notifications.ObjectHasMoved;
import org.openflexo.fge.notifications.ObjectHasResized;
import org.openflexo.fge.notifications.ObjectWillMove;
import org.openflexo.fge.notifications.ObjectWillResize;
import org.openflexo.fge.notifications.ShapeNeedsToBeRedrawn;
import org.openflexo.fge.swing.SwingViewFactory;
import org.openflexo.fge.swing.paint.FGEPaintManager;
import org.openflexo.fge.view.FGEView;
import org.openflexo.swing.FlexoSwingUtils;
import org.openflexo.toolbox.ToolBox;

/**
 * The {@link JLabelView} is the SWING implementation of the panel displaying a floating label
 * 
 * @author sylvain
 * 
 */
@SuppressWarnings("serial")
public class JLabelView<O> extends JScrollPane implements JFGEView<O, JPanel>, LabelMetricsProvider {

	private static final Logger logger = Logger.getLogger(JLabelView.class.getPackage().getName());

	private DrawingTreeNode<O, ?> node;
	private FGEViewMouseListener mouseListener;
	private AbstractDianaEditor<?, SwingViewFactory, JComponent> controller;
	private FGEView<O, ? extends JComponent> delegateView;
	private boolean isEditing = false;

	private TextComponent textComponent;

	private boolean initialized = false;

	private boolean mouseInsideLabel = false;

	public JLabelView(final DrawingTreeNode<O, ?> node, AbstractDianaEditor<?, SwingViewFactory, JComponent> controller,
			FGEView<O, ? extends JComponent> delegateView) {

		logger.info("Build JLabelView for " + node.getDrawable() + " with text " + node.getText());

		setUI(new BasicScrollPaneUI());
		getViewport().setUI(new BasicViewportUI());
		this.controller = controller;
		this.node = node;
		this.delegateView = delegateView;
		mouseListener = controller.getDianaFactory().makeViewMouseListener(node, this, controller);
		this.textComponent = new TextComponent();
		this.textComponentListener = new LabelDocumentListener();
		textComponent.addMouseListener(new InOutMouseListener());
		((AbstractDocument) textComponent.getDocument()).setDocumentFilter(new DocumentFilter() {
			@Override
			public void insertString(FilterBypass fb, int offset, String text, AttributeSet attr) throws BadLocationException {
				if (!node.getGraphicalRepresentation().getIsMultilineAllowed()) {
					if (text.equals("\n") || text.equals("\r\n")) {
						return;
					}
				}
				text = filteredText(text);
				super.insertString(fb, offset, text, attr);
			}

			@Override
			public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs) throws BadLocationException {
				if (!node.getGraphicalRepresentation().getIsMultilineAllowed()) {
					if (length == 0) {
						if (text.equals("\n") || text.equals("\r\n")) {
							return;
						}
					}
				}
				text = filteredText(text);
				super.replace(fb, offset, length, text, attrs);
			}

			private String filteredText(String text) {
				if (!node.getGraphicalRepresentation().getIsMultilineAllowed()) {
					return text.replaceAll("\r?\n", " ");
				}
				return text;
			}
		});
		getViewport().setBorder(null);
		getViewport().setOpaque(false);
		setViewportView(textComponent);
		setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
		// Note: if for debug purposes you add a Border to the textComponent, this could mess up the labels preferredSize computation.
		setBorder(null);
		setViewportBorder(null);
		setOpaque(false);
		node.setLabelMetricsProvider(this);
		textComponent.setLocation(0, 0);
		updateFont();
		updateText();
		node.addObserver(this);
		validate();
		initialized = true;
		textComponent.setEditable(false);
	}

	@Override
	public DrawingTreeNode<O, ?> getNode() {
		return node;
	}

	@Override
	public void updateUI() {
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

			@Override
			public void updateUI() {
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
			logger.fine("Delete JLabelView for " + node);
		}
		if (getController() instanceof DianaInteractiveViewer
				&& ((DianaInteractiveViewer<?, SwingViewFactory, JComponent>) getController()).getEditedLabel() == this) {
			((DianaInteractiveViewer<?, SwingViewFactory, JComponent>) getController()).resetEditedLabel(this);
		}
		removeFGEMouseListener();
		JDianaLayeredView<?> parentView = getParentView();
		if (parentView != null) {
			// logger.warning("Unexpected not null parent, proceeding anyway");
			parentView.remove(this);
			parentView.revalidate();
			if (getPaintManager() != null) {
				getPaintManager().repaint(parentView);
			}
		}
		if (node != null) {
			node.deleteObserver(this);
			node.setLabelMetricsProvider(null);
		}
		isDeleted = true;
		controller = null;
		mouseListener = null;
		node = null;
	}

	public void enableTextComponentMouseListeners() {
		removeFGEMouseListener();
	}

	public void disableTextComponentMouseListeners() {
		addFGEMouseListener();
	}

	@Override
	public O getDrawable() {
		return delegateView.getDrawable();
	}

	@Override
	public JDrawingView<?> getDrawingView() {
		if (getController() != null) {
			return (JDrawingView<?>) getController().getDrawingView();
		}
		return null;
	}

	public JLabelView<O> getLabelView() {
		return this;
	}

	@Override
	public JDianaLayeredView<?> getParent() {
		return (JDianaLayeredView<?>) super.getParent();
	}

	public JDianaLayeredView<?> getParentView() {
		return getParent();
	}

	public FGEView<O, ? extends JComponent> getDelegateView() {
		return delegateView;
	}

	/*@Override
	public GraphicalRepresentation getGraphicalRepresentation() {
		return graphicalRepresentation;
	}*/

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
				&& (getPaintManager().isTemporaryObject(node) || isEditing);
		if (skipPaint || isDeleted() || !node.hasText()) {
			return;
		}
		super.paint(g);
	}

	@Override
	public AbstractDianaEditor<?, SwingViewFactory, JComponent> getController() {
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
			// System.out.println("Received event in JLabelView: " + aNotification + " observers=" + o.countObservers());

			if (aNotification instanceof FGENotification) {
				FGENotification notification = (FGENotification) aNotification;
				if (notification.getParameter() == GraphicalRepresentation.TEXT
				// There are some GR in WKF that rely on ShapeNeedsToBeRedrawn notification to update text (this can be removed once we
				// properly use appropriate bindings
						|| aNotification instanceof ShapeNeedsToBeRedrawn) {
					updateText();
					getPaintManager().repaint(this);
				} else if (notification.getParameter() == GraphicalRepresentation.TEXT_STYLE) {
					updateFont();
					getPaintManager().repaint(this);
				} else if (notification.getParameter() == GraphicalRepresentation.PARAGRAPH_ALIGNEMENT) {
					updateFont();
					getPaintManager().repaint(this);
				} else if (notification.getParameter() == GraphicalRepresentation.HORIZONTAL_TEXT_ALIGNEMENT
						|| notification.getParameter() == GraphicalRepresentation.VERTICAL_TEXT_ALIGNEMENT) {
					updateBounds();
					getPaintManager().repaint(this);
				} else if (notification.getParameter() == ShapeGraphicalRepresentation.RELATIVE_TEXT_X
						|| notification.getParameter() == ShapeGraphicalRepresentation.RELATIVE_TEXT_Y
						|| notification.getParameter() == GraphicalRepresentation.ABSOLUTE_TEXT_X
						|| notification.getParameter() == GraphicalRepresentation.ABSOLUTE_TEXT_Y
						|| notification.getParameter() == ShapeGraphicalRepresentation.IS_FLOATING_LABEL) {
					updateBounds();
					getPaintManager().repaint(this);
				} else if (notification instanceof ObjectWillMove || notification instanceof ObjectWillResize
						|| notification instanceof LabelWillMove) {
					setDoubleBuffered(false);
					if (notification instanceof LabelWillMove) {
						getPaintManager().addToTemporaryObjects(node);
						getPaintManager().invalidate(node);
					}
				} else if (notification instanceof ObjectHasMoved || notification instanceof ObjectHasResized
						|| notification instanceof LabelHasMoved) {
					setDoubleBuffered(true);
					if (notification instanceof LabelHasMoved) {
						getPaintManager().removeFromTemporaryObjects(node);
					}
				}
			}
		}
	}

	protected void updateBounds() {
		updateBounds(true);
		// System.out.println("JLabelView for " + getNode() + " for " + getNode().getDrawable() + " update bounds to " + getBounds());
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
		// System.out.println("JLabelView " + Integer.toHexString(hashCode()) + " for " + node);
		Rectangle bounds = node.getLabelBounds(getScale());
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
		if (isDeleted || node == null) {
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
		int width = node.getAvailableLabelWidth(scale);
		if (node.getGraphicalRepresentation().getLineWrap()) {
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
		TextStyle ts = node.getGraphicalRepresentation().getTextStyle();
		if (ts == null) {
			if (node.getFactory() != null) {
				ts = node.getFactory().makeDefaultTextStyle();
			} else {
				ts = FGECoreUtils.TOOLS_FACTORY.makeDefaultTextStyle();
			}
		}
		if (ts.getOrientation() != 0) {
			at.concatenate(AffineTransform.getRotateInstance(Math.toRadians(ts.getOrientation())));
		}
		Font font = ts.getFont();
		if (font == null) {
			font = FGEConstants.DEFAULT_TEXT_FONT;
		}
		font = font.deriveFont(at);
		textComponent.setFont(font);
		SimpleAttributeSet set = new SimpleAttributeSet();
		if (node.getGraphicalRepresentation().getParagraphAlignment() == ParagraphAlignment.CENTER) {
			StyleConstants.setAlignment(set, StyleConstants.ALIGN_CENTER);
		} else if (node.getGraphicalRepresentation().getParagraphAlignment() == ParagraphAlignment.LEFT) {
			StyleConstants.setAlignment(set, StyleConstants.ALIGN_LEFT);
		} else if (node.getGraphicalRepresentation().getParagraphAlignment() == ParagraphAlignment.RIGHT) {
			StyleConstants.setAlignment(set, StyleConstants.ALIGN_RIGHT);
		} else if (node.getGraphicalRepresentation().getParagraphAlignment() == ParagraphAlignment.JUSTIFY) {
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
		StyledDocument document = textComponent.getStyledDocument();
		document.setParagraphAttributes(0, document.getLength(), set, true);
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
		if (node.hasText()) {
			textComponent.setText(node.getText());
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
		if (!node.getDrawing().isEditable() || !node.getGraphicalRepresentation().getIsLabelEditable()) {
			return;
		}
		if (logger.isLoggable(Level.INFO)) {
			logger.info("Start edition of " + node + " with text " + node.getText());
		}
		isEditing = true;
		registerTextListener();
		textComponent.setEditable(true);
		setDoubleBuffered(false);
		if (getController() instanceof DianaInteractiveViewer) {
			((DianaInteractiveViewer<?, SwingViewFactory, JComponent>) getController()).setEditedLabel(JLabelView.this);
		}
		node.notifyLabelWillBeEdited();
		getPaintManager().invalidate(node);
		getPaintManager().addToTemporaryObjects(node);
		repaint();
		textComponent.selectAll();
		textComponent.requestFocusInWindow();
	}

	public void stopEdition() {
		if (!isEditing) {
			return;
		}

		// If not continuous edition, do it now
		/*if (node.getGraphicalRepresentation().getContinuousTextEditing()) {
		node.getGraphicalRepresentation().setText(textComponent.getText());
		}*/
		if (node.getPropertyValue(GraphicalRepresentation.CONTINUOUS_TEXT_EDITING)) {
			node.setText(textComponent.getText());
		}
		isEditing = false;
		unregisterTextListener();
		if (logger.isLoggable(Level.INFO)) {
			logger.info("Stop edition of " + node + " getController()=" + getController());
		}
		textComponent.setEditable(false);
		setDoubleBuffered(true);
		if (getController() instanceof DianaInteractiveViewer) {
			((DianaInteractiveViewer<?, SwingViewFactory, JComponent>) getController()).resetEditedLabel(JLabelView.this);
		}
		if (logger.isLoggable(Level.FINE)) {
			logger.fine("Stop edition of " + node);
		}
		if (node == null || node.isDeleted()) {
			return;
		}
		node.notifyLabelHasBeenEdited();
		getPaintManager().removeFromTemporaryObjects(node);
		getPaintManager().invalidate(node);
		getPaintManager().repaint(this);
	}

	@Override
	public void stopLabelEdition() {
		stopEdition();
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
			if (node.getGraphicalRepresentation().getIsMultilineAllowed()) {
				// Hack to detect trivial edition (RETURN pressed)
				// logger.info("event: "+event);
				if (!wasEdited && enterPressed) {
					return;
				}
			}
			wasEdited = true;
			/*if (node.getGraphicalRepresentation().getContinuousTextEditing()) {
			node.getGraphicalRepresentation().setText(textComponent.getText());
			}*/
			if (node.getPropertyValue(GraphicalRepresentation.CONTINUOUS_TEXT_EDITING)) {
				node.setText(textComponent.getText());
			}
			updateBoundsLater();
		}

		@Override
		public void removeUpdate(DocumentEvent event) {
			if (!isEditing) {
				return;
			}
			if (node.getGraphicalRepresentation().getIsMultilineAllowed()) {
				// Hack to detect trivial edition (RETURN pressed)
				// logger.info("event: "+event);
				if (!wasEdited && enterPressed) {
					return;
				}
				// if (!wasEdited) return;
			}
			wasEdited = true;
			/*if (node.getGraphicalRepresentation().getContinuousTextEditing()) {
				node.getGraphicalRepresentation().setText(textComponent.getText());
			}*/
			if (node.getPropertyValue(GraphicalRepresentation.CONTINUOUS_TEXT_EDITING)) {
				node.setText(textComponent.getText());
			}
			updateBoundsLater();
		}

		@Override
		public void changedUpdate(DocumentEvent event) {
			if (!isEditing) {
				return;
			}
			wasEdited = true;
			/*if (node.getGraphicalRepresentation().getContinuousTextEditing()) {
			node.getGraphicalRepresentation().setText(textComponent.getText());
			}*/
			if (node.getPropertyValue(GraphicalRepresentation.CONTINUOUS_TEXT_EDITING)) {
				node.setText(textComponent.getText());
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
	public void activatePalette(DianaPalette<?, ?> aPalette) {
		// Not applicable
	}

	public FGEPaintManager getPaintManager() {
		return getDrawingView().getPaintManager();
	}

	public boolean isEditing() {
		return isEditing;
	}

	@Override
	public String getToolTipText(MouseEvent event) {
		if (getController() instanceof DianaInteractiveViewer) {
			return ((DianaInteractiveViewer<?, SwingViewFactory, JComponent>) getController()).getToolTipText();
		}
		return super.getToolTipText(event);
	}

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
			setUI(new BasicTextPaneUI());
			setOpaque(false);
			setEditable(false);
			setAutoscrolls(false);
			setFocusable(true);
		}

		@Override
		public Dimension getPreferredSize() {
			if (getText().length() == 0) {
				return new Dimension(30, getFont().getSize());
			}
			return super.getPreferredSize();
		}

		@Override
		public void updateUI() {
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
				requestFocusInWindow();
				selectAll();
			} else {
				addFGEMouseListener();
			}
		}

	}

}
