package org.openflexo.fge.impl;

import org.openflexo.fge.TextureBackgroundStyle;
import org.openflexo.fge.notifications.FGEAttributeNotification;

public abstract class TextureBackgroundStyleImpl extends BackgroundStyleImpl implements TextureBackgroundStyle {

	private TextureBackgroundStyle.TextureType textureType;
	private java.awt.Color color1;
	private java.awt.Color color2;

	public TextureBackgroundStyleImpl() {
		this(TextureType.TEXTURE1, java.awt.Color.WHITE, java.awt.Color.BLACK);
	}

	public TextureBackgroundStyleImpl(TextureBackgroundStyle.TextureType aTextureType, java.awt.Color aColor1, java.awt.Color aColor2) {
		super();
		textureType = aTextureType;
		this.color1 = aColor1;
		this.color2 = aColor2;
		// rebuildColoredTexture();
	}

	@Override
	public BackgroundStyleType getBackgroundStyleType() {
		return BackgroundStyleType.TEXTURE;
	}

	@Override
	public TextureBackgroundStyle.TextureType getTextureType() {
		return textureType;
	}

	@Override
	public void setTextureType(TextureBackgroundStyle.TextureType aTextureType) {
		if (requireChange(this.textureType, aTextureType)) {
			TextureBackgroundStyle.TextureType oldTexture = textureType;
			this.textureType = aTextureType;
			// rebuildColoredTexture();
			setChanged();
			notifyObservers(new FGEAttributeNotification(TEXTURE_TYPE, oldTexture, aTextureType));
		}
	}

	@Override
	public java.awt.Color getColor1() {
		return color1;
	}

	@Override
	public void setColor1(java.awt.Color aColor) {
		if (requireChange(this.color1, aColor)) {
			java.awt.Color oldColor = color1;
			this.color1 = aColor;
			// rebuildColoredTexture();
			setChanged();
			notifyObservers(new FGEAttributeNotification(COLOR1, oldColor, aColor));
		}
	}

	@Override
	public java.awt.Color getColor2() {
		return color2;
	}

	@Override
	public void setColor2(java.awt.Color aColor) {
		if (requireChange(this.color2, aColor)) {
			java.awt.Color oldColor = color2;
			this.color2 = aColor;
			// rebuildColoredTexture();
			setChanged();
			notifyObservers(new FGEAttributeNotification(COLOR2, oldColor, aColor));
		}
	}

	/*@Override
	public String toString() {
		return "BackgroundStyle.TEXTURE(" + getColor1() + "," + getColor2() + "," + getTextureType() + ")";
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
