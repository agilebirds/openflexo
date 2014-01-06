package org.openflexo.fge.impl;

import java.awt.Image;
import java.io.File;

import javax.swing.ImageIcon;

import org.openflexo.fge.BackgroundImageBackgroundStyle;
import org.openflexo.fge.notifications.FGEAttributeNotification;

public abstract class BackgroundImageBackgroundStyleImpl extends BackgroundStyleImpl implements BackgroundImageBackgroundStyle {

	private File imageFile;
	private Image image;

	public BackgroundImageBackgroundStyleImpl() {
		this((ImageIcon) null);
	}

	public BackgroundImageBackgroundStyleImpl(File imageFile) {
		super();
		setImageFile(imageFile);
	}

	public BackgroundImageBackgroundStyleImpl(ImageIcon image) {
		super();
		if (image != null) {
			this.image = image.getImage();
		}
	}

	@Override
	public BackgroundStyleType getBackgroundStyleType() {
		return BackgroundStyleType.IMAGE;
	}

	@Override
	public File getImageFile() {
		return imageFile;
	}

	@Override
	public void setImageFile(File anImageFile) {
		if (requireChange(this.imageFile, anImageFile)) {
			File oldFile = imageFile;
			imageFile = anImageFile;
			if (anImageFile != null && anImageFile.exists()) {
				image = new ImageIcon(anImageFile.getAbsolutePath()).getImage();
			} else {
				image = null;
			}
			setChanged();
			notifyObservers(new FGEAttributeNotification(IMAGE_FILE, oldFile, anImageFile));
		}
	}

	@Override
	public Image getImage() {
		return image;
	}

	@Override
	public void setImage(Image image) {
		this.image = image;
	}

	private BackgroundImageBackgroundStyle.ImageBackgroundType imageBackgroundType = ImageBackgroundType.TRANSPARENT;
	private double scaleX = 1.0;
	private double scaleY = 1.0;
	private double deltaX = 0.0;
	private double deltaY = 0.0;
	private java.awt.Color imageBackgroundColor = java.awt.Color.WHITE;
	private boolean fitToShape = false;

	@Override
	public java.awt.Color getImageBackgroundColor() {
		return imageBackgroundColor;
	}

	@Override
	public void setImageBackgroundColor(java.awt.Color aColor) {
		if (requireChange(this.imageBackgroundColor, aColor)) {
			java.awt.Color oldColor = imageBackgroundColor;
			this.imageBackgroundColor = aColor;
			setChanged();
			notifyObservers(new FGEAttributeNotification(IMAGE_BACKGROUND_COLOR, oldColor, aColor));
		}
	}

	@Override
	public double getDeltaX() {
		return deltaX;
	}

	@Override
	public void setDeltaX(double aDeltaX) {
		if (requireChange(this.deltaX, aDeltaX)) {
			double oldDeltaX = this.deltaX;
			this.deltaX = aDeltaX;
			setChanged();
			notifyObservers(new FGEAttributeNotification(DELTA_X, oldDeltaX, deltaX));
		}
	}

	@Override
	public double getDeltaY() {
		return deltaY;
	}

	@Override
	public void setDeltaY(double aDeltaY) {
		if (requireChange(this.deltaY, aDeltaY)) {
			double oldDeltaY = this.deltaY;
			this.deltaY = aDeltaY;
			setChanged();
			notifyObservers(new FGEAttributeNotification(DELTA_Y, oldDeltaY, deltaY));
		}
	}

	@Override
	public BackgroundImageBackgroundStyle.ImageBackgroundType getImageBackgroundType() {
		return imageBackgroundType;
	}

	@Override
	public void setImageBackgroundType(BackgroundImageBackgroundStyle.ImageBackgroundType anImageBackgroundType) {
		if (requireChange(this.imageBackgroundType, anImageBackgroundType)) {
			BackgroundImageBackgroundStyle.ImageBackgroundType oldImageBackgroundType = this.imageBackgroundType;
			this.imageBackgroundType = anImageBackgroundType;
			setChanged();
			notifyObservers(new FGEAttributeNotification(IMAGE_BACKGROUND_TYPE, oldImageBackgroundType, anImageBackgroundType));
		}
	}

	@Override
	public double getScaleX() {
		return scaleX;
	}

	@Override
	public void setScaleX(double aScaleX) {
		if (requireChange(this.scaleX, aScaleX)) {
			double oldScaleX = this.scaleX;
			// logger.info(toString()+": Sets scaleX from "+oldScaleX+" to "+aScaleX);
			this.scaleX = aScaleX;
			setChanged();
			notifyObservers(new FGEAttributeNotification(SCALE_X, oldScaleX, scaleX));
		}
	}

	@Override
	public void setScaleXNoNotification(double aScaleX) {
		if (requireChange(this.scaleX, aScaleX)) {
			this.scaleX = aScaleX;
		}
	}

	@Override
	public double getScaleY() {
		return scaleY;
	}

	@Override
	public void setScaleY(double aScaleY) {
		if (requireChange(this.scaleY, aScaleY)) {
			double oldScaleY = this.scaleY;
			// logger.info(toString()+": Sets scaleY from "+oldScaleY+" to "+aScaleY);
			this.scaleY = aScaleY;
			setChanged();
			notifyObservers(new FGEAttributeNotification(SCALE_Y, oldScaleY, scaleY));
		}
	}

	@Override
	public void setScaleYNoNotification(double aScaleY) {
		if (requireChange(this.scaleY, aScaleY)) {
			this.scaleY = aScaleY;
		}
	}

	@Override
	public boolean getFitToShape() {
		return fitToShape;
	}

	@Override
	public void setFitToShape(boolean aFlag) {
		if (requireChange(this.fitToShape, aFlag)) {
			boolean oldValue = fitToShape;
			this.fitToShape = aFlag;
			setChanged();
			notifyObservers(new FGEAttributeNotification(FIT_TO_SHAPE, oldValue, aFlag));
		}
	}

	/*@Override
	public String toString() {
		return "BackgroundStyle.IMAGE(" + getImageFile() + ")";
	}*/

	private boolean requireChange(Object oldObject, Object newObject) {
		if (oldObject == null) {
			if (newObject == null) {
				return false;
			} else {
				return true;
			}
		}
		return !oldObject.equals(newObject);
	}

}
