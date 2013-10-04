package org.openflexo.fge;

import java.awt.Color;

import org.openflexo.model.annotations.Getter;
import org.openflexo.model.annotations.ModelEntity;
import org.openflexo.model.annotations.PropertyIdentifier;
import org.openflexo.model.annotations.Setter;
import org.openflexo.model.annotations.XMLAttribute;
import org.openflexo.model.annotations.XMLElement;

/**
 * Represents a plain colored background
 * 
 * @author sylvain
 * 
 */
@ModelEntity
@XMLElement(xmlTag = "ColorBackgroundStyle")
public interface ColorBackgroundStyle extends BackgroundStyle {

	@PropertyIdentifier(type = Color.class)
	public static final String COLOR_KEY = "color";

	public static GRParameter<Color> COLOR = GRParameter.getGRParameter(ColorBackgroundStyle.class, COLOR_KEY, Color.class);

	@Getter(value = COLOR_KEY)
	@XMLAttribute
	public java.awt.Color getColor();

	@Setter(value = COLOR_KEY)
	public void setColor(java.awt.Color aColor);

}
