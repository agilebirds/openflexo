package org.openflexo.fge;

import java.awt.Image;
import java.awt.Paint;
import java.io.File;

import javax.swing.ImageIcon;

import org.openflexo.fge.notifications.FGENotification;

public class BackgroundImageBackgroundStyle extends BackgroundStyle {
	private File imageFile;
	private Image image;

	public BackgroundImageBackgroundStyle() {
		this((File) null);
	}

	public BackgroundImageBackgroundStyle(File imageFile) {
		super();
		setImageFile(imageFile);
	}

	public BackgroundImageBackgroundStyle(ImageIcon image) {
		super();
		this.image = image.getImage();
	}

	@Override
	public Paint getPaint(GraphicalRepresentation gr, double scale) {
		return java.awt.Color.WHITE;
	}

	@Override
	public BackgroundStyleType getBackgroundStyleType() {
		return BackgroundStyleType.IMAGE;
	}

	public File getImageFile() {
		return imageFile;
	}

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
			notifyObservers(new FGENotification(Parameters.imageFile, oldFile, anImageFile));
		}
	}

	public Image getImage() {
		return image;
	}

	private BackgroundImageBackgroundStyle.ImageBackgroundType imageBackgroundType = ImageBackgroundType.TRANSPARENT;
	private double scaleX = 1.0;
	private double scaleY = 1.0;
	private double deltaX = 0.0;
	private double deltaY = 0.0;
	private java.awt.Color imageBackgroundColor = java.awt.Color.WHITE;
	private boolean fitToShape = false;

	public static enum ImageBackgroundType {
		OPAQUE, TRANSPARENT
	}

	public java.awt.Color getImageBackgroundColor() {
		return imageBackgroundColor;
	}

	public void setImageBackgroundColor(java.awt.Color aColor) {
		if (requireChange(this.imageBackgroundColor, aColor)) {
			java.awt.Color oldColor = imageBackgroundColor;
			this.imageBackgroundColor = aColor;
			setChanged();
			notifyObservers(new FGENotification(Parameters.imageBackgroundColor, oldColor, aColor));
		}
	}

	public double getDeltaX() {
		return deltaX;
	}

	public void setDeltaX(double aDeltaX) {
		if (requireChange(this.deltaX, aDeltaX)) {
			double oldDeltaX = this.deltaX;
			this.deltaX = aDeltaX;
			setChanged();
			notifyObservers(new FGENotification(Parameters.deltaX, oldDeltaX, deltaX));
		}
	}

	public double getDeltaY() {
		return deltaY;
	}

	public void setDeltaY(double aDeltaY) {
		if (requireChange(this.deltaY, aDeltaY)) {
			double oldDeltaY = this.deltaY;
			this.deltaY = aDeltaY;
			setChanged();
			notifyObservers(new FGENotification(Parameters.deltaY, oldDeltaY, deltaY));
		}
	}

	public BackgroundImageBackgroundStyle.ImageBackgroundType getImageBackgroundType() {
		return imageBackgroundType;
	}

	public void setImageBackgroundType(BackgroundImageBackgroundStyle.ImageBackgroundType anImageBackgroundType) {
		if (requireChange(this.imageBackgroundType, anImageBackgroundType)) {
			BackgroundImageBackgroundStyle.ImageBackgroundType oldImageBackgroundType = this.imageBackgroundType;
			this.imageBackgroundType = anImageBackgroundType;
			setChanged();
			notifyObservers(new FGENotification(Parameters.imageBackgroundType, oldImageBackgroundType, anImageBackgroundType));
		}
	}

	public double getScaleX() {
		return scaleX;
	}

	public void setScaleX(double aScaleX) {
		if (requireChange(this.scaleX, aScaleX)) {
			double oldScaleX = this.scaleX;
			// logger.info(toString()+": Sets scaleX from "+oldScaleX+" to "+aScaleX);
			this.scaleX = aScaleX;
			setChanged();
			notifyObservers(new FGENotification(Parameters.scaleX, oldScaleX, scaleX));
		}
	}

	public void setScaleXNoNotification(double aScaleX) {
		if (requireChange(this.scaleX, aScaleX)) {
			this.scaleX = aScaleX;
		}
	}

	public double getScaleY() {
		return scaleY;
	}

	public void setScaleY(double aScaleY) {
		if (requireChange(this.scaleY, aScaleY)) {
			double oldScaleY = this.scaleY;
			// logger.info(toString()+": Sets scaleY from "+oldScaleY+" to "+aScaleY);
			this.scaleY = aScaleY;
			setChanged();
			notifyObservers(new FGENotification(Parameters.scaleY, oldScaleY, scaleY));
		}
	}

	public void setScaleYNoNotification(double aScaleY) {
		if (requireChange(this.scaleY, aScaleY)) {
			this.scaleY = aScaleY;
		}
	}

	public boolean getFitToShape() {
		return fitToShape;
	}

	public void setFitToShape(boolean aFlag) {
		if (requireChange(this.fitToShape, aFlag)) {
			boolean oldValue = fitToShape;
			this.fitToShape = aFlag;
			setChanged();
			notifyObservers(new FGENotification(Parameters.fitToShape, oldValue, aFlag));
		}
	}

	@Override
	public String toString() {
		return "BackgroundStyle.IMAGE(" + getImageFile() + ")";
	}

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