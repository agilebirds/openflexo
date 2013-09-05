package org.openflexo.fge;

import java.awt.Color;

import org.openflexo.fge.impl.ColorGradientBackgroundStyleImpl;
import org.openflexo.model.annotations.Getter;
import org.openflexo.model.annotations.ImplementationClass;
import org.openflexo.model.annotations.ModelEntity;
import org.openflexo.model.annotations.PropertyIdentifier;
import org.openflexo.model.annotations.Setter;
import org.openflexo.model.annotations.XMLAttribute;
import org.openflexo.model.annotations.XMLElement;

/**
 * Represents a background colored with a linear gradient between two colors
 * 
 * @author sylvain
 * 
 */
@ModelEntity
@ImplementationClass(ColorGradientBackgroundStyleImpl.class)
@XMLElement(xmlTag = "ColorGradientBackgroundStyle")
public interface ColorGradientBackgroundStyle extends BackgroundStyle {

	@PropertyIdentifier(type = Color.class)
	public static final String COLOR1_KEY = "color1";
	@PropertyIdentifier(type = Color.class)
	public static final String COLOR2_KEY = "color2";
	@PropertyIdentifier(type = ColorGradientDirection.class)
	public static final String DIRECTION_KEY = "direction";

	public static GRParameter<Color> COLOR1 = GRParameter.getGRParameter(ColorGradientBackgroundStyle.class, COLOR1_KEY, Color.class);
	public static GRParameter<Color> COLOR2 = GRParameter.getGRParameter(ColorGradientBackgroundStyle.class, COLOR2_KEY, Color.class);
	public static GRParameter<ColorGradientDirection> DIRECTION = GRParameter.getGRParameter(ColorGradientBackgroundStyle.class,
			DIRECTION_KEY, ColorGradientDirection.class);

	public static enum ColorGradientDirection {
		NORTH_SOUTH, WEST_EAST, SOUTH_EAST_NORTH_WEST, SOUTH_WEST_NORTH_EAST
	}

	@Getter(value = COLOR1_KEY)
	@XMLAttribute
	public java.awt.Color getColor1();

	@Setter(value = COLOR1_KEY)
	public void setColor1(java.awt.Color aColor);

	@Getter(value = COLOR2_KEY)
	@XMLAttribute
	public java.awt.Color getColor2();

	@Setter(value = COLOR2_KEY)
	public void setColor2(java.awt.Color aColor);

	@Getter(value = DIRECTION_KEY)
	@XMLAttribute
	public ColorGradientDirection getDirection();

	@Setter(value = DIRECTION_KEY)
	public void setDirection(ColorGradientDirection aDirection);

}