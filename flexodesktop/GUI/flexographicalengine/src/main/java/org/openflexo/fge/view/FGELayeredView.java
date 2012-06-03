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

import java.awt.Component;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.swing.JLayeredPane;

public abstract class FGELayeredView<O> extends JLayeredPane implements FGEView<O> {

	private static final Logger logger = Logger.getLogger(FGELayeredView.class.getPackage().getName());
	private BufferedViewHelper paintDelegate;

	@Override
	public abstract DrawingView<?> getDrawingView();

	public FGELayeredView() {
		this.paintDelegate = new BufferedViewHelper(this);
	}

	@Override
	public void delete() {
		if (paintDelegate != null) {
			paintDelegate.delete();
		}
		paintDelegate = null;
	}

	@Override
	public final void paint(Graphics g) {
		paintDelegate.paint(g);
	}

	@Override
	public void superPaint(Graphics g) {
		super.paint(g);
	}

	@Override
	public void doUnbufferedPaint(Graphics g) {
		// TODO Auto-generated method stub

	}

	@Override
	public BufferedViewHelper getPaintDelegate() {
		return paintDelegate;
	}

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

}
