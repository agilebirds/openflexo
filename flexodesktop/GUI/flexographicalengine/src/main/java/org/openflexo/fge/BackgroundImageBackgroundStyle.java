package org.openflexo.fge;

import java.awt.Image;
import java.io.File;

import org.openflexo.fge.impl.BackgroundImageBackgroundStyleImpl;
import org.openflexo.model.annotations.Getter;
import org.openflexo.model.annotations.ImplementationClass;
import org.openflexo.model.annotations.ModelEntity;
import org.openflexo.model.annotations.Setter;
import org.openflexo.model.annotations.XMLAttribute;
import org.openflexo.model.annotations.XMLElement;

/**
 * Represents a background filled with an image
 * 
 * @author sylvain
 * 
 */
@ModelEntity
@ImplementationClass(BackgroundImageBackgroundStyleImpl.class)
@XMLElement(xmlTag = "ImageBackgroundStyle")
public interface BackgroundImageBackgroundStyle extends BackgroundStyle {

	public static final String IMAGE_FILE = "imageFile";
	public static final String SCALE_X = "scaleX";
	public static final String SCALE_Y = "scaleY";
	public static final String DELTA_X = "deltaX";
	public static final String DELTA_Y = "deltaY";
	public static final String FIT_TO_SHAPE = "fitToShape";
	public static final String IMAGE_BACKGROUND_TYPE = "imageBackgroundType";
	public static final String IMAGE_BACKGROUND_COLOR = "imageBackgroundColor";

	public static enum ImageBackgroundType {
		OPAQUE, TRANSPARENT
	}

	@Override
	public BackgroundStyleType getBackgroundStyleType();

	@Getter(value = IMAGE_FILE)
	@XMLAttribute
	public File getImageFile();

	@Setter(value = IMAGE_FILE)
	public void setImageFile(File anImageFile);

	@Getter(value = IMAGE_BACKGROUND_COLOR)
	@XMLAttribute
	public java.awt.Color getImageBackgroundColor();

	@Setter(value = IMAGE_BACKGROUND_COLOR)
	public void setImageBackgroundColor(java.awt.Color aColor);

	@Getter(value = DELTA_X, defaultValue = "0.0")
	@XMLAttribute
	public double getDeltaX();

	@Setter(value = DELTA_X)
	public void setDeltaX(double aDeltaX);

	@Getter(value = DELTA_Y, defaultValue = "0.0")
	@XMLAttribute
	public double getDeltaY();

	@Setter(value = DELTA_Y)
	public void setDeltaY(double aDeltaY);

	@Getter(value = IMAGE_BACKGROUND_TYPE)
	@XMLAttribute
	public ImageBackgroundType getImageBackgroundType();

	@Setter(value = IMAGE_BACKGROUND_TYPE)
	public void setImageBackgroundType(ImageBackgroundType anImageBackgroundType);

	@Getter(value = SCALE_X, defaultValue = "1.0")
	@XMLAttribute
	public double getScaleX();

	@Setter(value = SCALE_X)
	public void setScaleX(double aScaleX);

	@Getter(value = SCALE_Y, defaultValue = "1.0")
	@XMLAttribute
	public double getScaleY();

	@Setter(value = SCALE_Y)
	public void setScaleY(double aScaleY);

	@Getter(value = FIT_TO_SHAPE, defaultValue = "false")
	@XMLAttribute
	public boolean getFitToShape();

	@Setter(value = FIT_TO_SHAPE)
	public void setFitToShape(boolean aFlag);

	public Image getImage();

	public void setImage(Image image);

	public void setScaleXNoNotification(double aScaleX);

	public void setScaleYNoNotification(double aScaleY);

}
