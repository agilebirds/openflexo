package org.openflexo.fge;

import org.openflexo.fge.impl.ColorBackgroundStyleImpl;
import org.openflexo.model.annotations.Getter;
import org.openflexo.model.annotations.ImplementationClass;
import org.openflexo.model.annotations.ModelEntity;
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
@ImplementationClass(ColorBackgroundStyleImpl.class)
@XMLElement(xmlTag = "ColorBackgroundStyle")
public interface ColorBackgroundStyle extends BackgroundStyle {

	public static final String COLOR = "color";

	@Getter(value = COLOR)
	@XMLAttribute
	public java.awt.Color getColor();

	@Setter(value = COLOR)
	public void setColor(java.awt.Color aColor);

}
