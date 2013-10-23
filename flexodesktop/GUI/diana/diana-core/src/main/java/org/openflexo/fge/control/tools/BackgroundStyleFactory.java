package org.openflexo.fge.control.tools;

import java.awt.Color;
import java.beans.PropertyChangeSupport;
import java.io.File;

import org.openflexo.fge.BackgroundImageBackgroundStyle;
import org.openflexo.fge.BackgroundStyle;
import org.openflexo.fge.BackgroundStyle.BackgroundStyleType;
import org.openflexo.fge.ColorBackgroundStyle;
import org.openflexo.fge.ColorGradientBackgroundStyle;
import org.openflexo.fge.ColorGradientBackgroundStyle.ColorGradientDirection;
import org.openflexo.fge.FGECoreUtils;
import org.openflexo.fge.FGEModelFactory;
import org.openflexo.fge.TextureBackgroundStyle;
import org.openflexo.fge.TextureBackgroundStyle.TextureType;
import org.openflexo.toolbox.HasPropertyChangeSupport;

/**
 * Convenient class used to manipulate BackgroundStyle
 * 
 * @author sylvain
 * 
 */
public class BackgroundStyleFactory implements HasPropertyChangeSupport {
	private static final String DELETED = "deleted";
	private BackgroundStyle backgroundStyle;
	private Color color1 = Color.RED;
	private Color color2 = Color.WHITE;
	private ColorGradientDirection gradientDirection = ColorGradientDirection.NORTH_SOUTH;
	private TextureType textureType = TextureType.TEXTURE1;
	private File imageFile;
	private PropertyChangeSupport pcSupport;
	private FGEModelFactory fgeFactory;

	public BackgroundStyleFactory(BackgroundStyle backgroundStyle) {
		pcSupport = new PropertyChangeSupport(this);
		this.backgroundStyle = backgroundStyle;
		if (backgroundStyle != null) {
			fgeFactory = backgroundStyle.getFactory();
		}
		if (fgeFactory == null) {
			// logger.warning("Could not find any FGE factory, creating a default one");
			fgeFactory = FGECoreUtils.TOOLS_FACTORY;
		}
	}

	@Override
	public PropertyChangeSupport getPropertyChangeSupport() {
		return pcSupport;
	}

	public void delete() {
		getPropertyChangeSupport().firePropertyChange(DELETED, false, true);
		pcSupport = null;
	}

	@Override
	public String getDeletedProperty() {
		return DELETED;
	}

	public BackgroundStyle getBackgroundStyle() {
		return backgroundStyle;
	}

	public void setBackgroundStyle(BackgroundStyle backgroundStyle) {
		BackgroundStyle oldBackgroundStyle = this.backgroundStyle;
		this.backgroundStyle = backgroundStyle;
		pcSupport.firePropertyChange("backgroundStyle", oldBackgroundStyle, backgroundStyle);
	}

	public BackgroundStyleType getBackgroundStyleType() {
		return backgroundStyle.getBackgroundStyleType();
	}

	public void setBackgroundStyleType(BackgroundStyleType backgroundStyleType) {
		// logger.info("setBackgroundStyleType with " + backgroundStyleType);
		BackgroundStyleType oldBackgroundStyleType = getBackgroundStyleType();

		if (oldBackgroundStyleType == backgroundStyleType) {
			return;
		}

		switch (getBackgroundStyleType()) {
		case NONE:
			break;
		case COLOR:
			color1 = ((ColorBackgroundStyle) backgroundStyle).getColor();
			break;
		case COLOR_GRADIENT:
			color1 = ((ColorGradientBackgroundStyle) backgroundStyle).getColor1();
			color2 = ((ColorGradientBackgroundStyle) backgroundStyle).getColor2();
			gradientDirection = ((ColorGradientBackgroundStyle) backgroundStyle).getDirection();
			break;
		case TEXTURE:
			color1 = ((TextureBackgroundStyle) backgroundStyle).getColor1();
			color2 = ((TextureBackgroundStyle) backgroundStyle).getColor2();
			textureType = ((TextureBackgroundStyle) backgroundStyle).getTextureType();
			break;
		case IMAGE:
			imageFile = ((BackgroundImageBackgroundStyle) backgroundStyle).getImageFile();
			break;
		default:
			break;
		}

		switch (backgroundStyleType) {
		case NONE:
			setBackgroundStyle(fgeFactory.makeEmptyBackground());
			break;
		case COLOR:
			setBackgroundStyle(fgeFactory.makeColoredBackground(color1));
			break;
		case COLOR_GRADIENT:
			setBackgroundStyle(fgeFactory.makeColorGradientBackground(color1, color2, gradientDirection));
			break;
		case TEXTURE:
			setBackgroundStyle(fgeFactory.makeTexturedBackground(textureType, color1, color2));
			break;
		case IMAGE:
			setBackgroundStyle(fgeFactory.makeImageBackground(imageFile));
			break;
		default:
			break;
		}

		pcSupport.firePropertyChange("backgroundStyleType", oldBackgroundStyleType, getBackgroundStyleType());
	}

}