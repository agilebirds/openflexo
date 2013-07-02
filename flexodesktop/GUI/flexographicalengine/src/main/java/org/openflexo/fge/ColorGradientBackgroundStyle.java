package org.openflexo.fge;

import org.openflexo.fge.impl.ColorGradientBackgroundStyleImpl;
import org.openflexo.model.annotations.Getter;
import org.openflexo.model.annotations.ImplementationClass;
import org.openflexo.model.annotations.ModelEntity;
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

	public static final String COLOR1 = "color1";
	public static final String COLOR2 = "color2";
	public static final String DIRECTION = "direction";

	public static enum ColorGradientDirection {
		NORTH_SOUTH, WEST_EAST, SOUTH_EAST_NORTH_WEST, SOUTH_WEST_NORTH_EAST
	}

	@Getter(value = COLOR1)
	@XMLAttribute
	public java.awt.Color getColor1();

	@Setter(value = COLOR1)
	public void setColor1(java.awt.Color aColor);

	@Getter(value = COLOR2)
	@XMLAttribute
	public java.awt.Color getColor2();

	@Setter(value = COLOR2)
	public void setColor2(java.awt.Color aColor);

	@Getter(value = DIRECTION)
	@XMLAttribute
	public ColorGradientDirection getDirection();

	@Setter(value = DIRECTION)
	public void setDirection(ColorGradientDirection aDirection);

}