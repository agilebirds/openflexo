package org.openflexo.fge;

import javax.swing.ImageIcon;

import org.openflexo.fge.impl.TextureBackgroundStyleImpl;
import org.openflexo.inspector.HasIcon;
import org.openflexo.model.annotations.Getter;
import org.openflexo.model.annotations.ImplementationClass;
import org.openflexo.model.annotations.ModelEntity;
import org.openflexo.model.annotations.Setter;
import org.openflexo.model.annotations.XMLAttribute;
import org.openflexo.model.annotations.XMLElement;
import org.openflexo.toolbox.ImageIconResource;

/**
 * Represents a textured background defined with a texture and two colors
 * 
 * @author sylvain
 * 
 */
@ModelEntity
@ImplementationClass(TextureBackgroundStyleImpl.class)
@XMLElement(xmlTag = "TexturedBackgroundStyle")
public interface TextureBackgroundStyle extends BackgroundStyle {

	public static final String COLOR1 = "color1";
	public static final String COLOR2 = "color2";
	public static final String TEXTURE_TYPE = "textureType";

	public static enum TextureType implements HasIcon {
		TEXTURE1,
		TEXTURE2,
		TEXTURE3,
		TEXTURE4,
		TEXTURE5,
		TEXTURE6,
		TEXTURE7,
		TEXTURE8,
		TEXTURE9,
		TEXTURE10,
		TEXTURE11,
		TEXTURE12,
		TEXTURE13,
		TEXTURE14,
		TEXTURE15,
		TEXTURE16;

		public ImageIcon getImageIcon() {
			return new ImageIconResource("Motifs/Motif" + (ordinal() + 1) + ".gif");
		}

		@Override
		public ImageIcon getIcon() {
			return getImageIcon();
		}
	}

	@Getter(value = TEXTURE_TYPE)
	@XMLAttribute
	public TextureType getTextureType();

	@Setter(value = TEXTURE_TYPE)
	public void setTextureType(TextureType aTextureType);

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

}