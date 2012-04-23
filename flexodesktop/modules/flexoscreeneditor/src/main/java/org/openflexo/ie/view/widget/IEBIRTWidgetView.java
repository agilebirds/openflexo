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
package org.openflexo.ie.view.widget;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.util.logging.Logger;

import javax.swing.JComponent;

import org.openflexo.foundation.DataModification;
import org.openflexo.foundation.FlexoObservable;
import org.openflexo.foundation.ObjectDeleted;
import org.openflexo.foundation.ie.widget.IEBIRTWidget;
import org.openflexo.ie.view.IEWOComponentView;
import org.openflexo.ie.view.controller.IEController;
import org.openflexo.swing.MouseResizer;
import org.openflexo.swing.MouseResizer.MouseResizerDelegate;

/**
 * @author bmangez
 * 
 *         TODO To change the template for this generated type comment go to Window - Preferences - Java - Code Style - Code Templates
 */
public class IEBIRTWidgetView extends IEWidgetView<IEBIRTWidget> implements MouseResizerDelegate {

	private static final Logger logger = Logger.getLogger(IEBIRTWidgetView.class.getPackage().getName());

	private Image image;
	private MouseResizer resizer;

	/**
	 * @param model
	 */
	public IEBIRTWidgetView(IEController ieController, IEBIRTWidget model, boolean addDnDSupport, IEWOComponentView componentView) {
		super(ieController, model, addDnDSupport, componentView);
		setBorder(null);
		setOpaque(false);
		computeImage();
		resizer = new MouseResizer(this, this);
	}

	private void computeImage() {
		image = getModel().getGraphType().getIcon().getImage();
	}

	@Override
	public boolean isDragEnabled() {
		if (resizer.getMode() != MouseResizer.ResizeMode.NONE) {
			return false;
		}
		return super.isDragEnabled();
	}

	private int getPixelWidthUsingPercentage() {
		int width;
		if (getParent() instanceof IESequenceWidgetWidgetView) {
			width = ((IESequenceWidgetWidgetView) getParent()).getAvailableWidth();
		} else {
			width = getParent().getSize().width;
		}
		width = width * getModel().getPercentage() / 100;
		return width;
	}

	@Override
	public void paint(Graphics g) {
		g.drawImage(image, 0, 0, getSize().width, getSize().height, null);
		super.paint(g);
	}

	/**
	 * Overrides getPreferredSize
	 * 
	 * @see javax.swing.JComponent#getPreferredSize()
	 */
	@Override
	public Dimension getPreferredSize() {
		Dimension d;
		if (getModel().getUsePercentage()) {
			int width, height;
			width = getPixelWidthUsingPercentage();
			height = width * getModel().getGraphType().getIcon().getIconHeight() / getModel().getGraphType().getIcon().getIconWidth();
			d = new Dimension(width, height);
		} else {
			d = new Dimension(getModel().getWidthPixel(), getModel().getHeightPixel());
		}
		return d;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Observer#update(java.util.Observable, java.lang.Object)
	 */

	@Override
	public void update(FlexoObservable arg0, DataModification arg1) {
		if (getModel() == arg0 && arg1 instanceof ObjectDeleted) {
			JComponent parent = (JComponent) getParent();
			delete();
			if (parent != null) { // Usually, parent TD will already have done this
				parent.validate();
				parent.repaint();
			}
		} else if (getModel() == arg0
				&& (arg1.propertyName() == "heightPixel" || arg1.propertyName() == "widthPixel" || arg1.propertyName() == "usePercentage" || arg1
						.propertyName() == "percentage")) {
			revalidate();
			repaint();
		} else if (getModel() == arg0 && "graphType".equals(arg1.propertyName())) {
			computeImage();
			repaint();
		} else {
			super.update(arg0, arg1);
		}
	}

	@Override
	public void resizeBy(int deltaX, int deltaY) {
	}

	@Override
	public void resizeDirectlyBy(int deltaX, int deltaY) {
		Dimension d = new Dimension(getModel().getWidthPixel(), getModel().getHeightPixel());
		if (getModel().getUsePercentage()) {
			int percentage = getModel().getPercentage();
			int w = getWidth();
			int newPercentage = percentage + (int) ((double) deltaX / (double) w * 100);
			if (newPercentage != percentage) {
				getModel().setPercentage(newPercentage);
			}
		} else {
			if (deltaY != 0) {
				getModel().setHeightPixel(d.height + deltaY);
			}
			if (deltaX != 0) {
				getModel().setWidthPixel(d.width + deltaX);
			}
		}
	}

}
