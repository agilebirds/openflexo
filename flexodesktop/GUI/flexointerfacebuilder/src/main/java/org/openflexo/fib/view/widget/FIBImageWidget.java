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
package org.openflexo.fib.view.widget;

import java.awt.Image;
import java.awt.image.ImageObserver;
import java.util.logging.Logger;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;

import org.openflexo.fib.controller.FIBController;
import org.openflexo.fib.model.FIBImage;
import org.openflexo.fib.view.FIBWidgetView;
import org.openflexo.swing.ImageUtils;

public class FIBImageWidget extends FIBWidgetView<FIBImage, JLabel, Image> implements ImageObserver {

	private static final Logger logger = Logger.getLogger(FIBImageWidget.class.getPackage().getName());

	private JLabel labelWidget;

	public FIBImageWidget(FIBImage model, FIBController controller) {
		super(model, controller);
		if (model.getData().isValid()) {
			labelWidget = new JLabel(" ");
		} else {
			labelWidget = new JLabel();
		}
		updateFont();
		updateAlign();
		updateImage();
		// updatePreferredSize();

	}

	@Override
	public synchronized boolean updateWidgetFromModel() {
		if (modelUpdating) {
			return false;
		}
		widgetUpdating = true;
		updateImage();
		widgetUpdating = false;
		return false;
	}

	/**
	 * Update the model given the actual state of the widget
	 */
	@Override
	public synchronized boolean updateModelFromWidget() {
		// Read only component
		return false;
	}

	@Override
	public JLabel getJComponent() {
		return labelWidget;
	}

	@Override
	public JLabel getDynamicJComponent() {
		return labelWidget;
	}

	protected void updateAlign() {
		labelWidget.setHorizontalAlignment(getWidget().getAlign().getAlign());
	}

	protected void updateImage() {
		if (getWidget().getData().isValid()) {
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					if (!isDeleted()) {
						Image image = getValue();
						updateImageDefaultSize(image);
						labelWidget.setIcon(makeImageIcon(image));
					}
				}
			});
		} else if (getWidget().getImageFile() != null) {
			if (getWidget().getImageFile().exists()) {
				Image image = ImageUtils.loadImageFromFile(getWidget().getImageFile());
				updateImageDefaultSize(image);
				labelWidget.setIcon(makeImageIcon(image));
			}
		}
	}

	private ImageIcon makeImageIcon(Image image) {
		if (image == null) {
			return null;
		}
		if (getWidget() == null) {
			return null;
		}
		switch (getWidget().getSizeAdjustment()) {
		case OriginalSize:
			return new ImageIcon(image);
		case FitToAvailableSize:
			return new ImageIcon(image.getScaledInstance(getJComponent().getWidth(), getJComponent().getHeight(), Image.SCALE_SMOOTH));
		case FitToAvailableSizeRespectRatio:
			int imageWidth = image.getWidth(this);
			int imageHeight = image.getHeight(this);
			if (imageWidth <= 0 || imageHeight <= 0) {
				synchronized (this) {
					logger.fine("Image is not ready, waiting...");
					computeImageLater = true;
					return null;
				}
			}
			if (getJComponent().getWidth() == 0 || getJComponent().getHeight() == 0) {
				SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {
						updateImage();
					}
				});
				return new ImageIcon(image);
			}
			double widthRatio = (double) getJComponent().getWidth() / imageWidth;
			double heightRatio = (double) getJComponent().getHeight() / imageHeight;
			double ratio = widthRatio < heightRatio ? widthRatio : heightRatio;
			int newWidth = (int) (imageWidth * ratio);
			int newHeight = (int) (imageHeight * ratio);
			return new ImageIcon(image.getScaledInstance(newWidth, newHeight, Image.SCALE_SMOOTH));
		case AdjustDimensions:
			return new ImageIcon(image.getScaledInstance(getWidget().getImageWidth(), getWidget().getImageHeight(), Image.SCALE_SMOOTH));
		case AdjustWidth:
			return new ImageIcon(image.getScaledInstance(getWidget().getImageWidth(), -1, Image.SCALE_SMOOTH));
		case AdjustHeight:
			return new ImageIcon(image.getScaledInstance(-1, getWidget().getImageHeight(), Image.SCALE_SMOOTH));
		default:
			return null;
		}
	}

	private boolean computeImageLater = false;

	private void updateImageDefaultSize(Image image) {
		if (getWidget() == null || image == null) {
			return;
		}
		if (getWidget().getImageWidth() == null) {
			getWidget().setImageWidth(image.getWidth(this));
		}
		if (getWidget().getImageHeight() == null) {
			getWidget().setImageHeight(image.getHeight(this));
		}

	}

	@Override
	public synchronized boolean imageUpdate(Image img, int infoflags, int x, int y, int width, int height) {
		updateImageDefaultSize(img);
		if (computeImageLater) {
			logger.fine("Image can now be displayed");
			computeImageLater = false;
			updateImage();
		}
		return false;
	}

}
