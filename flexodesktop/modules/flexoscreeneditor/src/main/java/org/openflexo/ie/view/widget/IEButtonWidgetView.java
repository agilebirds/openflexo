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
import java.io.File;
import java.util.logging.Logger;

import javax.swing.ImageIcon;

import org.openflexo.foundation.DataModification;
import org.openflexo.foundation.FlexoObservable;
import org.openflexo.foundation.ie.dm.ButtonRemoved;
import org.openflexo.foundation.ie.widget.IEButtonWidget;
import org.openflexo.foundation.utils.FlexoCSS;
import org.openflexo.ie.view.IEWOComponentView;
import org.openflexo.ie.view.controller.IEController;
import org.openflexo.swing.MouseResizer;
import org.openflexo.swing.MouseResizer.MouseResizerDelegate;
import org.openflexo.toolbox.WRLocator;

/**
 * @author bmangez
 * 
 *         TODO To change the template for this generated type comment go to Window - Preferences - Java - Code Style - Code Templates
 */
public class IEButtonWidgetView extends IEWidgetView<IEButtonWidget> implements MouseResizerDelegate {

	private static final Logger logger = Logger.getLogger(IEButtonWidgetView.class.getPackage().getName());

	private File imageIconFile;
	private ImageIcon imageIcon;
	private Image image;
	private MouseResizer resizer;

	/**
	 * @param model
	 */
	public IEButtonWidgetView(IEController ieController, IEButtonWidget model, boolean addDnDSupport, IEWOComponentView componentView) {
		super(ieController, model, addDnDSupport, componentView);
		// File imageIconFile = getImageIconPath(model);
		setBorder(null);
		setOpaque(false);
		computeImage();
		resizer = new MouseResizer(this, this);
	}

	private void computeImage() {
		imageIconFile = WRLocator.locate(getModel().getProject().getProjectDirectory(), getModel().getImageName(),
				getCSSName() == null ? FlexoCSS.CONTENTO.getName() : getCSSName());
		if (imageIconFile == null) {
			imageIconFile = WRLocator.AGILE_BIRDS_LOGO;
		}
		imageIcon = new ImageIcon(imageIconFile.getAbsolutePath());
		image = imageIcon.getImage();
	}

	protected Image getImage() {
		return image;
	}

	@Override
	public boolean isDragEnabled() {
		if (resizer.getMode() != MouseResizer.ResizeMode.NONE) {
			return false;
		}
		return super.isDragEnabled();
	}

	/*private void createViewForButtonWithName(String imageName) {
	    if (_label==null)
	        _label = new JLabel();
	    File imageIconFile = null;
	    if (imageName!=null) 
	        imageIconFile = WRLocator.locate(imageName,getCSSName()==null?FlexoCSS.CONTENTO.getName():getCSSName());
	    if (imageIconFile != null) {
	        if (logger.isLoggable(Level.FINE))
	            logger.fine("Found icon " + imageIconFile.getAbsolutePath());
	        _label.setText(null);
	        _label.setIcon(new ImageIcon(imageIconFile.getAbsolutePath()));
	        _label.setOpaque(false);
	        _label.setPreferredSize(new Dimension(_label.getIcon().getIconWidth(), _label.getIcon().getIconHeight()));
	    } else {
	        if (logger.isLoggable(Level.WARNING) && imageName!=null)
	            logger.warning("Image with name "+imageName+" could not be found");
	        _label.setIcon(null);
	        _label.setText(FlexoLocalization.localizedForKey("image_not_found",_label));
	    }
	    if (_label.getParent()==null)
	        add(_label, BorderLayout.CENTER);
	}*/

	@Override
	public void paint(Graphics g) {
		g.drawImage(getImage(), 0, 0, getSize().width, getSize().height, null);
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
			height = Math.round((float) width / (float) getModel().getImageInformation().getWidth()
					* getModel().getImageInformation().getHeight());
			d = new Dimension(width, height);
		} else {
			d = new Dimension(getModel().getWidthPixel(), getModel().getHeightPixel());
		}
		return d;
	}

	private int getPixelWidthUsingPercentage() {
		int width;
		if (getParent() instanceof IESequenceWidgetWidgetView) {
			width = ((IESequenceWidgetWidgetView) getParent()).getAvailableWidth();
		} else {
			width = getParent().getSize().width;
		}
		width = width * getModel().getWidthPercentage() / 100;
		return width;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Observer#update(java.util.Observable, java.lang.Object)
	 */

	@Override
	public void update(FlexoObservable arg0, DataModification arg1) {
		if (getModel() == arg0 && arg1 instanceof ButtonRemoved) {
			delete();
			revalidate();
			repaint();
		} else if (getModel() == arg0 && arg1.propertyName() == "file") {
			computeImage();
			revalidate();
			repaint();
		} else if (getModel() == arg0 && (arg1.propertyName() == "heightPixel" || arg1.propertyName() == "widthPixel")) {
			revalidate();
			repaint();
		} else if (getModel() == arg0 && (arg1.propertyName() == "usePercentage" || arg1.propertyName() == "widthPercentage")) {
			revalidate();
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
		if (getModel().getUsePercentage()) {
			int widthPercentage = getModel().getWidthPercentage();
			int w = getPixelWidthUsingPercentage();
			int newWidthPercentage = widthPercentage + (int) ((double) deltaX / (double) w * 100);
			if (newWidthPercentage != widthPercentage) {
				getModel().setWidthPercentage(newWidthPercentage);
			}
		} else {
			Dimension d = new Dimension(getModel().getWidthPixel(), getModel().getHeightPixel());
			if (deltaY != 0) {
				getModel().setHeightPixel(d.height + deltaY);
			}
			if (deltaX != 0) {
				getModel().setWidthPixel(d.width + deltaX);
			}
		}
	}

}
