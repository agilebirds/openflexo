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

import java.awt.AlphaComposite;
import java.awt.Component;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import java.awt.Transparency;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JComponent;
import javax.swing.JLayeredPane;

public abstract class FGELayeredView<O> extends JLayeredPane implements FGEView<O> {

	private static final Logger logger = Logger.getLogger(FGELayeredView.class.getPackage().getName());

	@Override
	public abstract DrawingView<?> getDrawingView();

	/**
	 * Sets the layer attribute on the specified component, making it the bottommost component in that layer. Should be called before adding
	 * to parent.
	 * 
	 * @param c
	 *            the Component to set the layer for
	 * @param layer
	 *            an int specifying the layer to set, where lower numbers are closer to the bottom
	 */
	public void setLayer(FGEView c, int layer) {
		setLayer((Component) c, layer, -1);
	}

	/**
	 * Returns the layer attribute for the specified Component.
	 * 
	 * @param c
	 *            the Component to check
	 * @return an int specifying the component's current layer
	 */
	public int getLayer(FGEView c) {
		return super.getLayer((Component) c);
	}

	/**
	 * Moves the component to the top of the components in its current layer (position 0).
	 * 
	 * @param c
	 *            the Component to move
	 * @see #setPosition(Component, int)
	 */
	public void toFront(FGEView c) {
		super.moveToFront((Component) c);
	}

	/**
	 * Moves the component to the bottom of the components in its current layer (position -1).
	 * 
	 * @param c
	 *            the Component to move
	 * @see #setPosition(Component, int)
	 */
	public void toBack(FGEView c) {
		super.moveToBack((Component) c);
	}

	public List<FGEView<?>> getViewsInLayer(int layer) {
		List<FGEView<?>> returned = new ArrayList<FGEView<?>>();
		for (Component c : super.getComponentsInLayer(layer)) {
			if (c instanceof FGEView) {
				returned.add((FGEView<?>) c);
			}
		}
		return returned;
	}

	public void add(ShapeView<?> view) {
		// logger.info("add "+view);
		view.setBackground(getBackground());
		if (view.getLabelView() != null) {
			add(view.getLabelView(), view.getLayer(), -1);
		}
		add(view, view.getLayer(), -1);
		if (getDrawingView() != null) {
			getDrawingView().getContents().put(view.getGraphicalRepresentation(), view);
		}
	}

	public void remove(ShapeView<?> view) {
		if (view.getLabelView() != null) {
			remove(view.getLabelView());
		}
		remove((Component) view);
		if (getDrawingView() != null) {
			getDrawingView().getContents().remove(view.getGraphicalRepresentation());
		}
	}

	public void add(ConnectorView<?> view) {
		view.setBackground(getBackground());
		if (view.getLabelView() != null) {
			add(view.getLabelView(), view.getLayer(), -1);
		}
		add(view, view.getLayer(), -1);
		if (getDrawingView() != null) {
			getDrawingView().getContents().put(view.getGraphicalRepresentation(), view);
		}
	}

	public void remove(ConnectorView<?> view) {
		if (view.getLabelView() != null) {
			remove(view.getLabelView());
		}
		remove((Component) view);
		if (getDrawingView() != null) {
			getDrawingView().getContents().remove(view.getGraphicalRepresentation());
		}
	}
	
	private BufferedImage screenshot;

	public BufferedImage getScreenshot() {
		if (screenshot == null) {
			captureScreenshot();
		}
		return screenshot;
	}

	public void captureScreenshot() {
		JComponent lbl = this;
		getController().disablePaintingCache();
		try {
			Rectangle bounds = new Rectangle(getBounds());
			if (getLabelView() != null) {
				bounds = bounds.union(getLabelView().getBounds());
			}
			GraphicsConfiguration gc = getGraphicsConfiguration();
			if (gc == null) {
				gc = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration();
			}
			screenshot = gc.createCompatibleImage(bounds.width, bounds.height, Transparency.TRANSLUCENT);// buffered image
			// reference passing
			// the label's ht
			// and width
			Graphics2D graphics = screenshot.createGraphics();// creating the graphics for buffered image
			graphics.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.7f)); // Sets the Composite for the Graphics2D
																								// context
			lbl.print(graphics); // painting the graphics to label
			/*if (this.getGraphicalRepresentation().getBackground() instanceof BackgroundImage) {
				graphics.drawImage(((BackgroundImage)this.getGraphicalRepresentation().getBackground()).getImage(),0,0,null);
			}*/
			if (getLabelView() != null) {
				Rectangle r = getLabelView().getBounds();
				getLabelView().print(graphics.create(r.x - bounds.x, r.y - bounds.y, r.width, r.height));
			}
			graphics.dispose();
			if (logger.isLoggable(Level.INFO)) {
				logger.info("Captured image on " + this);
			}
		} finally {
			getController().enablePaintingCache();
		}
	}

}
