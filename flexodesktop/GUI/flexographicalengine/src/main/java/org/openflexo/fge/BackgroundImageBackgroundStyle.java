package org.openflexo.fge;

import java.awt.Color;
import java.awt.Image;
import java.io.File;

import org.openflexo.fge.impl.BackgroundImageBackgroundStyleImpl;
import org.openflexo.model.annotations.Getter;
import org.openflexo.model.annotations.ImplementationClass;
import org.openflexo.model.annotations.ModelEntity;
import org.openflexo.model.annotations.PropertyIdentifier;
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

	@PropertyIdentifier(type = File.class)
	public static final String IMAGE_FILE_KEY = "imageFile";
	@PropertyIdentifier(type = Double.class)
	public static final String SCALE_X_KEY = "scaleX";
	@PropertyIdentifier(type = Double.class)
	public static final String SCALE_Y_KEY = "scaleY";
	@PropertyIdentifier(type = Double.class)
	public static final String DELTA_X_KEY = "deltaX";
	@PropertyIdentifier(type = Double.class)
	public static final String DELTA_Y_KEY = "deltaY";
	@PropertyIdentifier(type = Boolean.class)
	public static final String FIT_TO_SHAPE_KEY = "fitToShape";
	@PropertyIdentifier(type = ImageBackgroundType.class)
	public static final String IMAGE_BACKGROUND_TYPE_KEY = "imageBackgroundType";
	@PropertyIdentifier(type = Color.class)
	public static final String IMAGE_BACKGROUND_COLOR_KEY = "imageBackgroundColor";

	public static GRParameter<File> IMAGE_FILE = GRParameter.getGRParameter(BackgroundImageBackgroundStyle.class, IMAGE_FILE_KEY,
			File.class);
	public static GRParameter<Double> SCALE_X = GRParameter.getGRParameter(BackgroundImageBackgroundStyle.class, SCALE_X_KEY, Double.class);
	public static GRParameter<Double> SCALE_Y = GRParameter.getGRParameter(BackgroundImageBackgroundStyle.class, SCALE_Y_KEY, Double.class);
	public static GRParameter<Double> DELTA_X = GRParameter.getGRParameter(BackgroundImageBackgroundStyle.class, DELTA_X_KEY, Double.class);
	public static GRParameter<Double> DELTA_Y = GRParameter.getGRParameter(BackgroundImageBackgroundStyle.class, DELTA_Y_KEY, Double.class);
	public static GRParameter<Boolean> FIT_TO_SHAPE = GRParameter.getGRParameter(BackgroundImageBackgroundStyle.class, FIT_TO_SHAPE_KEY,
			Boolean.class);
	public static GRParameter<ImageBackgroundType> IMAGE_BACKGROUND_TYPE = GRParameter.getGRParameter(BackgroundImageBackgroundStyle.class,
			IMAGE_BACKGROUND_TYPE_KEY, ImageBackgroundType.class);
	public static GRParameter<Color> IMAGE_BACKGROUND_COLOR = GRParameter.getGRParameter(BackgroundImageBackgroundStyle.class,
			IMAGE_BACKGROUND_COLOR_KEY, Color.class);

	public static enum ImageBackgroundType {
		OPAQUE, TRANSPARENT
	}

	@Override
	public BackgroundStyleType getBackgroundStyleType();

	@Getter(value = IMAGE_FILE_KEY)
	@XMLAttribute
	public File getImageFile();

	@Setter(value = IMAGE_FILE_KEY)
	public void setImageFile(File anImageFile);

	@Getter(value = IMAGE_BACKGROUND_COLOR_KEY)
	@XMLAttribute
	public java.awt.Color getImageBackgroundColor();

	@Setter(value = IMAGE_BACKGROUND_COLOR_KEY)
	public void setImageBackgroundColor(java.awt.Color aColor);

	@Getter(value = DELTA_X_KEY, defaultValue = "0.0")
	@XMLAttribute
	public double getDeltaX();

	@Setter(value = DELTA_X_KEY)
	public void setDeltaX(double aDeltaX);

	@Getter(value = DELTA_Y_KEY, defaultValue = "0.0")
	@XMLAttribute
	public double getDeltaY();

	@Setter(value = DELTA_Y_KEY)
	public void setDeltaY(double aDeltaY);

	@Getter(value = IMAGE_BACKGROUND_TYPE_KEY)
	@XMLAttribute
	public ImageBackgroundType getImageBackgroundType();

	@Setter(value = IMAGE_BACKGROUND_TYPE_KEY)
	public void setImageBackgroundType(ImageBackgroundType anImageBackgroundType);

	@Getter(value = SCALE_X_KEY, defaultValue = "1.0")
	@XMLAttribute
	public double getScaleX();

	@Setter(value = SCALE_X_KEY)
	public void setScaleX(double aScaleX);

	@Getter(value = SCALE_Y_KEY, defaultValue = "1.0")
	@XMLAttribute
	public double getScaleY();

	@Setter(value = SCALE_Y_KEY)
	public void setScaleY(double aScaleY);

	@Getter(value = FIT_TO_SHAPE_KEY, defaultValue = "false")
	@XMLAttribute
	public boolean getFitToShape();

	@Setter(value = FIT_TO_SHAPE_KEY)
	public void setFitToShape(boolean aFlag);

	public Image getImage();

	public void setImage(Image image);

	public void setScaleXNoNotification(double aScaleX);

	public void setScaleYNoNotification(double aScaleY);

}
