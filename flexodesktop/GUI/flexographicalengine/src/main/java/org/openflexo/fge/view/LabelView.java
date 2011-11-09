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

import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.AffineTransform;
import java.util.Observable;
import java.util.StringTokenizer;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.SwingConstants;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.JTextComponent;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

import org.openflexo.fge.DrawingGraphicalRepresentation;
import org.openflexo.fge.FGEConstants;
import org.openflexo.fge.GraphicalRepresentation;
import org.openflexo.fge.GraphicalRepresentation.TextAlignment;
import org.openflexo.fge.ShapeGraphicalRepresentation;
import org.openflexo.fge.controller.DrawingController;
import org.openflexo.fge.controller.DrawingPalette;
import org.openflexo.fge.graphics.DrawUtils;
import org.openflexo.fge.graphics.TextStyle;
import org.openflexo.fge.notifications.FGENotification;
import org.openflexo.fge.view.listener.LabelViewMouseListener;
import org.openflexo.toolbox.StringUtils;


public class LabelView<O> extends JPanel implements FGEView<O> {

	private static final Logger logger = Logger.getLogger(LabelView.class.getPackage().getName());

	private GraphicalRepresentation<O> graphicalRepresentation;
	private LabelViewMouseListener mouseListener;
	private DrawingController _controller;
	private FGEView _delegateView;

	public LabelView(GraphicalRepresentation<O> aGraphicalRepresentation,DrawingController controller,FGEView delegateView)
	{
		super(null);

		_controller = controller;
		graphicalRepresentation = aGraphicalRepresentation;
		_delegateView = delegateView;

		updateBounds();
		mouseListener = new LabelViewMouseListener(aGraphicalRepresentation,this);
		addMouseListener(mouseListener);
		addMouseMotionListener(mouseListener);
		getGraphicalRepresentation().addObserver(this);
		setOpaque(false);

		//setToolTipText(getClass().getSimpleName()+hashCode());

	}

	private boolean isDeleted = false;

	@Override
	public boolean isDeleted()
	{
		return isDeleted;
	}
	
	@Override
	public void delete()
	{
		if (logger.isLoggable(Level.FINE)) logger.fine("Delete LabelView for "+getGraphicalRepresentation());
		if (getController() != null && getController().getEditedLabel() == this) getController().resetEditedLabel();
		if (getParentView() != null) {
			FGELayeredView parentView = getParentView();
			//logger.warning("Unexpected not null parent, proceeding anyway");
			parentView.remove(this);
			parentView.revalidate();
			if (getPaintManager() != null) getPaintManager().repaint(parentView);
		}
		if (getGraphicalRepresentation() != null) getGraphicalRepresentation().deleteObserver(this);
		disableMouseListeners();
		_controller = null;
		mouseListener = null;
		graphicalRepresentation = null;
		isDeleted = true;
	}

	private Vector<MouseListener> disabledMouseListeners = new Vector<MouseListener>();
	private Vector<MouseMotionListener> disabledMouseMotionListeners = new Vector<MouseMotionListener>();

	public void enableMouseListeners()
	{
		for (MouseListener ml : disabledMouseListeners) {
			addMouseListener(ml);
		}
		for (MouseMotionListener mml : disabledMouseMotionListeners) {
			addMouseMotionListener(mml);
		}
	}

	public void disableMouseListeners()
	{
		disabledMouseListeners.clear();
		disabledMouseMotionListeners.clear();
		for (MouseListener ml : getMouseListeners()) {
			disabledMouseListeners.add(ml);
			removeMouseListener(ml);
		}
		for (MouseMotionListener mml : getMouseMotionListeners()) {
			disabledMouseMotionListeners.add(mml);
			removeMouseMotionListener(mml);
		}
	}

	@Override
	public O getModel()
	{
		return getDrawable();
	}

	public O getDrawable()
	{
		return getGraphicalRepresentation().getDrawable();
	}

	@Override
	public DrawingView getDrawingView()
	{
		if (getController() != null)
			return getController().getDrawingView();
		return null;
	}

	@Override
	public FGELayeredView getParent()
	{
		return (FGELayeredView)super.getParent();
	}

	public FGELayeredView getParentView()
	{
		return getParent();
	}

	@Override
	public GraphicalRepresentation<O> getGraphicalRepresentation()
	{
		return graphicalRepresentation;
	}

	public DrawingGraphicalRepresentation<?> getDrawingGraphicalRepresentation()
	{
		return graphicalRepresentation.getDrawingGraphicalRepresentation();
	}

	@Override
	public double getScale()
	{
		return getController().getScale();
	}

	@Override
	public void paint(Graphics g)
	{
		if (getPaintManager().isPaintingCacheEnabled()
				&& getDrawingView().isBuffering()
				&& isEditing) {
			// Dont' call super paint
			//System.err.println("Skipping print in buffering "+this);
		}
		else {
			/*if (logger.isLoggable(Level.INFO))
				logger.info("Normal painting"+this);*/
			super.paint(g);
		}

		//super.paint(g);

		if (isEditing) return;

		if (getPaintManager().isPaintingCacheEnabled()) {
			if (getDrawingView().isBuffering()) {
				// Buffering painting
				if (getPaintManager().isTemporaryObject(getGraphicalRepresentation())) {
					// This object is declared to be a temporary object, to be redrawn
					// continuously, so we need to ignore it: do nothing
					if (FGEPaintManager.paintPrimitiveLogger.isLoggable(Level.FINE))
						FGEPaintManager.paintPrimitiveLogger.fine("LabelView: buffering paint, ignore: "+getGraphicalRepresentation());
				}
				else {
					if (FGEPaintManager.paintPrimitiveLogger.isLoggable(Level.FINE))
						FGEPaintManager.paintPrimitiveLogger.fine("LabelView: buffering paint, draw: "+getGraphicalRepresentation()+" clip="+g.getClip());
					doPaint(g);
				}
			} else {
				// TODO: implement usage of buffer
				doPaint(g);
			}
		}
		else {
			// Normal painting
			doPaint(g);
		}

	}

	protected void doPaint(Graphics g)
	{
		// When editing, escape now
		if (isEditing) {
			//logger.info("Ignore paint beause editing");
			return;
		}
		/*else {
			logger.info("I'm NOT editing, so i paint myself");
		}*/

		// When no text, escape now
		if (!getGraphicalRepresentation().hasText()) return;

		Graphics2D g2 = (Graphics2D) g.create();
		DrawUtils.turnOnAntiAlising(g2);
		DrawUtils.setRenderQuality(g2);
		DrawUtils.setColorRenderQuality(g2);

		TextStyle ts = getGraphicalRepresentation().getTextStyle();
		if (ts == null) ts = TextStyle.makeDefault();

		if (ts.getIsBackgroundColored()) {
			g2.setColor(ts.getBackgroundColor());
			g2.fillRect(0,0,getWidth(),getHeight());
		}

		g2.setColor(ts.getColor());

		AffineTransform at = AffineTransform.getScaleInstance(getScale(), getScale());
		if (ts.getOrientation() != 0) at.concatenate(AffineTransform.getRotateInstance(Math.toRadians(getGraphicalRepresentation().getTextStyle().getOrientation())));
		Font font = ts.getFont().deriveFont(at);

		g2.setFont(font);

		FontMetrics fm = getDrawingView().getFontMetrics(font);
		int height = fm.getHeight();

		if (getGraphicalRepresentation().getIsMultilineAllowed()) {
			int maxWidth = -1;
			StringTokenizer st1 = new StringTokenizer(getGraphicalRepresentation().getText(),StringUtils.LINE_SEPARATOR);
			while (st1.hasMoreTokens()) {
				int currentWidth = fm.stringWidth(st1.nextToken());
				if (currentWidth > maxWidth) maxWidth = currentWidth;
			}
			StringTokenizer st = new StringTokenizer(getGraphicalRepresentation().getText(),StringUtils.LINE_SEPARATOR);
			int lineIndex = 0;
			while (st.hasMoreTokens()) {
				String nextLine = st.nextToken();
				int currentWidth = fm.stringWidth(nextLine);
				if (getGraphicalRepresentation().getTextAlignment() == TextAlignment.LEFT) {
					g2.drawString(nextLine,2*FGEConstants.CONTROL_POINT_SIZE,(lineIndex+1)*height/*-(int)(1*getScale())-1*/);
				}
				else if (getGraphicalRepresentation().getTextAlignment() == TextAlignment.CENTER) {
					g2.drawString(nextLine,2*FGEConstants.CONTROL_POINT_SIZE+(maxWidth-currentWidth)/2,(lineIndex+1)*height/*-(int)(1*getScale())-1*/);
				}
				else if (getGraphicalRepresentation().getTextAlignment() == TextAlignment.RIGHT) {
					g2.drawString(nextLine,2*FGEConstants.CONTROL_POINT_SIZE+maxWidth-currentWidth,(lineIndex+1)*height/*-(int)(1*getScale())-1*/);
				}
				lineIndex++;
			}
		}
		else {
			g2.drawString(getGraphicalRepresentation().getText(),2*FGEConstants.CONTROL_POINT_SIZE,height/*-(int)(1*getScale())-1*/);
		}
		/*if (logger.isLoggable(Level.INFO))
			logger.info("Drawing string "+g2);*/

		//g2.drawString(getGraphicalRepresentation().getText(),2*FGEConstants.CONTROL_POINT_SIZE,getHeight()-(int)(1*getScale())-1);

		/*if (getGraphicalRepresentation().hasFloatingLabel()
				&& ((getGraphicalRepresentation().getIsFocused() && getController().getFocusedFloatingLabel() == graphicalRepresentation)
						|| getGraphicalRepresentation().getIsSelected())) {
			if (getGraphicalRepresentation().getIsFocused()) {
				g2.setColor(getDrawingView().getDrawingGraphicalRepresentation().getFocusColor());
			}
			if (getGraphicalRepresentation().getIsSelected()) {
				g2.setColor(getDrawingView().getDrawingGraphicalRepresentation().getSelectionColor());
			}
			g2.fillRect(0,
					getHeight()/2-FGEConstants.CONTROL_POINT_SIZE,
					(int)(FGEConstants.CONTROL_POINT_SIZE*2),
					(int)(FGEConstants.CONTROL_POINT_SIZE*2));
			g2.fillRect(getWidth()-FGEConstants.CONTROL_POINT_SIZE*2,
					getHeight()/2-FGEConstants.CONTROL_POINT_SIZE,
					(int)(FGEConstants.CONTROL_POINT_SIZE*2),
					(int)(FGEConstants.CONTROL_POINT_SIZE*2));
		}*/

		g2.dispose();

	}

	@Override
	public DrawingController<?> getController()
	{
		return _controller;
	}

	@Override
	public void update(Observable o, Object aNotification)
	{
		if (isDeleted) {
			//logger.warning("Received notifications for deleted view: observable="+(o!=null?o.getClass().getSimpleName():"null"));
			return;
		}

		//System.out.println("Received: "+aNotification);

		if (aNotification instanceof FGENotification) {
			FGENotification notification = (FGENotification)aNotification;
			if ((notification.getParameter() == GraphicalRepresentation.Parameters.text)
					|| (notification.getParameter() == GraphicalRepresentation.Parameters.textStyle)
					|| (notification.getParameter() == GraphicalRepresentation.Parameters.relativeTextX)
					|| (notification.getParameter() == GraphicalRepresentation.Parameters.relativeTextY)
					|| (notification.getParameter() == GraphicalRepresentation.Parameters.absoluteTextX)
					|| (notification.getParameter() == GraphicalRepresentation.Parameters.absoluteTextY)
					|| (notification.getParameter() == ShapeGraphicalRepresentation.Parameters.isFloatingLabel)) {
				if (isEditing && (notification.getParameter() == GraphicalRepresentation.Parameters.textStyle)) {
					if (logger.isLoggable(Level.WARNING))
						logger.warning("Text style attributes are not taken into account when editing");
					//TODO: update textcomponent style during edition.
				}
				updateBounds();
				revalidate();
				getPaintManager().repaint(this);
			}
		}
	}

	protected void updateBounds()
	{
		Rectangle newBounds = computeBounds();

		/*if (getGraphicalRepresentation().getText() != null && getGraphicalRepresentation().getText().equals("AreteA")) {
			logger.info("updateBounds() for AreteA with "+newBounds);
		}
		if (getGraphicalRepresentation().getText() != null && getGraphicalRepresentation().getText().equals("AreteB")) {
			logger.info("updateBounds() for AreteB with "+newBounds);
		}*/

		if (!newBounds.equals(getBounds())) {
			setBounds(newBounds);
		}
	}

	protected Rectangle computeBounds()
	{

		Rectangle newBounds = getGraphicalRepresentation().getLabelBounds(getDrawingView(), getScale());
		if (newBounds.isEmpty()) {
			newBounds = getGraphicalRepresentation().getLabelBoundsWithAlignement(newBounds.getLocation(), 20, (int) (getFontMetrics(getFont()).getHeight()*getScale()));
		}
		/*Rectangle newBounds;
		if (getGraphicalRepresentation().hasText()) {
			AffineTransform at = AffineTransform.getScaleInstance(getScale(), getScale());
			if (getGraphicalRepresentation().getTextStyle().getOrientation() != 0) at.concatenate(AffineTransform.getRotateInstance(Math.toRadians(getGraphicalRepresentation().getTextStyle().getOrientation())));
			Font font = getGraphicalRepresentation().getTextStyle().getFont().deriveFont(at);
			FontMetrics fm = getDrawingView().getFontMetrics(font);
			int height = 0;
			int width = 0;
			if (getGraphicalRepresentation().getIsMultilineAllowed()) {
				StringTokenizer st = new StringTokenizer(getGraphicalRepresentation().getText(),LINE_SEPARATOR);
				while (st.hasMoreTokens()) {
					height += fm.getHeight();
					width = Math.max(width, fm.stringWidth(st.nextToken())+4*FGEConstants.CONTROL_POINT_SIZE);
				}
			}
			else {
				height = fm.getHeight();
				width = fm.stringWidth(getGraphicalRepresentation().getText())+4*FGEConstants.CONTROL_POINT_SIZE;
			}
			if (getGraphicalRepresentation().getTextStyle().getOrientation() != 0)
				height = (int)Math.max(height,height+width*Math.sin(Math.toRadians(getGraphicalRepresentation().getTextStyle().getOrientation())));
			Point center;
			try {
			center= getGraphicalRepresentation().getLabelViewCenter(getScale());
			newBounds = new Rectangle(center.x-width/2,center.y-height/2,width,height);
			}
			catch (IllegalArgumentException e) {
				logger.warning("Unexpected exception: "+e);
				newBounds = new Rectangle(0,0,0,0);
			}
		}
		else {
			newBounds = new Rectangle(0,0,0,0);
		}*/

		/*if (isEditing) {
			newBounds.x -= 3;
			newBounds.y -= 3;
			newBounds.width += 6;
			newBounds.height += 6;
		}*/

		return newBounds;
	}

	private boolean isEditing = false;
	private JTextComponent textComponent;

	public void stopEdition()
	{
		if (!isEditing)
			return;
		
		// If not continous edition, do it now
		if (!getGraphicalRepresentation().getContinuousTextEditing()) 
			getGraphicalRepresentation().setText(textComponent.getText());

		isEditing = false;
		logger.info("Stop edition of "+getGraphicalRepresentation()+" getController()="+getController());

		if (getController() != null) getController().resetEditedLabel();

		if (logger.isLoggable(Level.FINE)) logger.fine("Stop edition of "+getGraphicalRepresentation());
		remove(textComponent);
		updateBounds();
		if (getGraphicalRepresentation() == null || getGraphicalRepresentation().isDeleted()) return;
		getGraphicalRepresentation().notifyLabelHasBeenEdited();

		//enableMouseListeners();

		setDoubleBuffered(true);
		revalidate();
		getPaintManager().resetTemporaryObjects();
		getPaintManager().invalidate(getGraphicalRepresentation());
		getPaintManager().repaint(getDrawingView());
	}

	private int hOffset;
	private int vOffset;

	public void startEdition()
	{
		if(!getGraphicalRepresentation().getIsLabelEditable())return;
		logger.info("Start edition of "+getGraphicalRepresentation());

		if (logger.isLoggable(Level.FINE)) logger.fine("Start edition of "+getGraphicalRepresentation());

		isEditing = true;

		//disableMouseListeners();

		AffineTransform at = AffineTransform.getScaleInstance(getScale(), getScale());
		if (getGraphicalRepresentation().getTextStyle().getOrientation() != 0) at.concatenate(AffineTransform.getRotateInstance(Math.toRadians(getGraphicalRepresentation().getTextStyle().getOrientation())));
		Font font = getGraphicalRepresentation().getTextStyle().getFont().deriveFont(at);
		if (getGraphicalRepresentation().getIsMultilineAllowed()) {
			//textComponent = new JTextArea(getGraphicalRepresentation().getText());
			final JTextPane text=new JTextPane();
			text.setOpaque(false);
			StyledDocument doc = text.getStyledDocument();
			//doc.putProperty(DefaultEditorKit.EndOfLineStringProperty, "\n");
			SimpleAttributeSet set =new SimpleAttributeSet();
			if (getGraphicalRepresentation().getTextAlignment() == TextAlignment.CENTER)
				StyleConstants.setAlignment(set,StyleConstants.ALIGN_CENTER);
			else if (getGraphicalRepresentation().getTextAlignment() == TextAlignment.LEFT)
				StyleConstants.setAlignment(set,StyleConstants.ALIGN_LEFT);
			else if (getGraphicalRepresentation().getTextAlignment() == TextAlignment.RIGHT)
				StyleConstants.setAlignment(set,StyleConstants.ALIGN_RIGHT);
			StyleConstants.setFontFamily(set,font.getFamily());
			StyleConstants.setFontSize(set,(int)(font.getSize()*getScale()));
			if (font.isBold()) StyleConstants.setBold(set,true);
			if (font.isItalic()) StyleConstants.setItalic(set,true);
			if (getGraphicalRepresentation().getTextStyle().getColor()!=null)
				StyleConstants.setForeground(set, getGraphicalRepresentation().getTextStyle().getColor());
			/*if (getGraphicalRepresentation() instanceof ShapeGraphicalRepresentation
					&& ((ShapeGraphicalRepresentation<?>)getGraphicalRepresentation()).getBackground()!=null
					&& ((ShapeGraphicalRepresentation<?>)getGraphicalRepresentation()).getBackground().getColor()!=null)
				StyleConstants.setBackground(set, ((ShapeGraphicalRepresentation<?>)getGraphicalRepresentation()).getBackground().getColor());
			else if (getGraphicalRepresentation().getTextStyle().getBackgroundColor()!=null)
				StyleConstants.setBackground(set, getGraphicalRepresentation().getTextStyle().getBackgroundColor());*/
			text.setParagraphAttributes(set,true);
			try {
				doc.insertString(0, getGraphicalRepresentation().getText(), set);
			} catch (BadLocationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			textComponent = text;
		}
		else {
			textComponent = new JTextField(getGraphicalRepresentation().getText());
			textComponent.setBorder(BorderFactory.createEmptyBorder());
			((JTextField)textComponent).setHorizontalAlignment(SwingConstants.CENTER);
			((JTextField)textComponent).addFocusListener(new FocusAdapter() {
				@Override
				public void focusLost(FocusEvent e)
				{
					stopEdition();
				}
			});

			((JTextField)textComponent).addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent event)
				{
					stopEdition();
				}
			});
			textComponent.setFont(font);
			textComponent.setOpaque(false);
			if (getGraphicalRepresentation().getTextStyle().getColor()!=null)
				textComponent.setForeground(getGraphicalRepresentation().getTextStyle().getColor());
			 /*if (getGraphicalRepresentation() instanceof ShapeGraphicalRepresentation
						&& ((ShapeGraphicalRepresentation<?>)getGraphicalRepresentation()).getBackground()!=null
						&& ((ShapeGraphicalRepresentation<?>)getGraphicalRepresentation()).getBackground().getColor()!=null)
					textComponent.setBackground(((ShapeGraphicalRepresentation<?>)getGraphicalRepresentation()).getBackground().getColor());
			else if (getGraphicalRepresentation().getTextStyle().getBackgroundColor()!=null)
				textComponent.setBackground(getGraphicalRepresentation().getTextStyle().getBackgroundColor());*/
		}
		textComponent.setDoubleBuffered(false);
		setDoubleBuffered(false);
		vOffset = (int) (textComponent.getInsets().top*getScale());
		hOffset = ((textComponent.getInsets().left-textComponent.getInsets().right));
		textComponent.setBorder(BorderFactory.createEmptyBorder());
		//textComponent.setBorder(BorderFactory.createLineBorder(Color.BLACK,1));
		textComponent.setForeground(getGraphicalRepresentation().getTextStyle().getColor());
		setBounds(computeBounds());
		/*FontMetrics fm = getDrawingView().getFontMetrics(font);
		int height = fm.getHeight();*/
		textComponent.setBounds(hOffset,vOffset,getWidth(),getHeight());

		LabelDocumentListener listener = new LabelDocumentListener();

		textComponent.addKeyListener(listener);

		textComponent.getDocument().addDocumentListener(listener);
		textComponent.addCaretListener(listener);
		/*textComponent.getDocument().addDocumentListener(new DocumentListener() {
			public void insertUpdate(DocumentEvent event)
			{
				getGraphicalRepresentation().setText(textComponent.getText());
				updateBounds();
				textComponent.setBounds(0,vOffset,getWidth(),getHeight()+vOffset);
			}

			public void removeUpdate(DocumentEvent event)
			{
				getGraphicalRepresentation().setText(textComponent.getText());
				updateBounds();
				textComponent.setBounds(0,vOffset,getWidth(),getHeight()+vOffset);
			}

			public void changedUpdate(DocumentEvent event)
			{
				getGraphicalRepresentation().setText(textComponent.getText());
				updateBounds();
				textComponent.setBounds(0,vOffset,getWidth(),getHeight()+vOffset);
			}
		});*/

		add(textComponent);
		textComponent.setOpaque(false);
		textComponent.requestFocus();
		textComponent.selectAll();
		getController().setEditedLabel(this);
		getGraphicalRepresentation().notifyLabelWillBeEdited();
		getPaintManager().invalidate(getGraphicalRepresentation());

		revalidate();

		getPaintManager().addToTemporaryObjects(getGraphicalRepresentation());
		getPaintManager().repaint(getDrawingView());

	}

	class LabelDocumentListener extends KeyAdapter implements DocumentListener, CaretListener
	{
		private boolean wasEdited = false;
		private boolean enterPressed = false;

        @Override
		public void keyPressed(KeyEvent e)
        {
             if (e.getKeyCode() == KeyEvent.VK_ENTER) {
            	 enterPressed = true;
            	 if (!wasEdited) {
            		 stopEdition();
            	 }
             }
         }

        @Override
		public void keyReleased(KeyEvent e)
        {
             if (e.getKeyCode() == KeyEvent.VK_ENTER) {
            	 enterPressed = false;
            	 if (!wasEdited) {
            		 stopEdition();
            	 }
             } else if (e.getKeyChar()!=KeyEvent.CHAR_UNDEFINED && !e.isActionKey()) {
            	 wasEdited = true;
             }
         }

        @Override
		public void insertUpdate(DocumentEvent event)
		{
        	if(!isEditing)
        		return;
			if (getGraphicalRepresentation().getIsMultilineAllowed()) {
				// Hack to detect trivial edition (RETURN pressed)
				//logger.info("event: "+event);
				if (!wasEdited && enterPressed) {
					return;
				}
			}
			wasEdited = true;
			if (getGraphicalRepresentation().getContinuousTextEditing()) 
				getGraphicalRepresentation().setText(textComponent.getText());
			updateBounds();
			textComponent.setBounds(hOffset,vOffset,getWidth(),getHeight());
		}

		@Override
		public void removeUpdate(DocumentEvent event)
		{
        	if(!isEditing)
        		return;
			if (getGraphicalRepresentation().getIsMultilineAllowed()) {
				// Hack to detect trivial edition (RETURN pressed)
				//logger.info("event: "+event);
				if (!wasEdited && enterPressed) {
					return;
				}
				//if (!wasEdited) return;
			}
			wasEdited = true;
			if (getGraphicalRepresentation().getContinuousTextEditing()) 
				getGraphicalRepresentation().setText(textComponent.getText());
			updateBounds();
			textComponent.setBounds(hOffset,vOffset,getWidth(),getHeight());
		}

		@Override
		public void changedUpdate(DocumentEvent event)
		{
        	if(!isEditing)
        		return;
			wasEdited = true;
			if (getGraphicalRepresentation().getContinuousTextEditing()) 
				getGraphicalRepresentation().setText(textComponent.getText());
			updateBounds();
			textComponent.setBounds(hOffset,vOffset,getWidth(),getHeight());
		}

		@Override
		public void caretUpdate(CaretEvent e)
		{
			// If the whole text is selected, we say that the text was not edited
			int start = textComponent.getSelectionStart();
			int end = textComponent.getSelectionEnd();
			if (start!=end) {
				if ((end==0 && start==textComponent.getDocument().getLength())
						||start==0 && end == textComponent.getDocument().getLength())
					wasEdited = false;
			} else {// Otherwise, it means that user has moved manually the cursor, and we consider he wants to edit the text
				wasEdited = true;
			}
		}

	}

	@Override
	public void registerPalette(DrawingPalette aPalette)
	{
		// Not applicable
	}

	@Override
	public FGEPaintManager getPaintManager()
	{
		return getDrawingView().getPaintManager();
	}

	public boolean isEditing()
	{
		return isEditing;
	}

	@Override
	public String getToolTipText(MouseEvent event)
	{
		return getController().getToolTipText();
	}

	public LabelViewMouseListener getMouseListener()
	{
		return mouseListener;
	}

	public JTextComponent getTextComponent()
	{
		return textComponent;
	}

}
