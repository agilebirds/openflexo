package org.openflexo.fge.graphics;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.Paint;
import java.awt.Rectangle;
import java.awt.TexturePaint;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.awt.image.FilteredImageSource;
import java.awt.image.ImageProducer;
import java.awt.image.RGBImageFilter;
import java.util.logging.Level;

import javax.swing.ImageIcon;

import org.openflexo.fge.GraphicalRepresentation;
import org.openflexo.fge.notifications.FGENotification;
import org.openflexo.inspector.HasIcon;
import org.openflexo.toolbox.ImageIconResource;

import sun.awt.image.ImageRepresentation;
import sun.awt.image.ToolkitImage;

public class TextureBackgroundStyle extends BackgroundStyle {
	private TextureBackgroundStyle.TextureType textureType;
	private java.awt.Color color1;
	private java.awt.Color color2;
	private BufferedImage coloredTexture;
	private ToolkitImage coloredImage;

	public TextureBackgroundStyle() {
		this(TextureType.TEXTURE1, java.awt.Color.WHITE, java.awt.Color.BLACK);
	}

	public TextureBackgroundStyle(TextureBackgroundStyle.TextureType aTextureType, java.awt.Color aColor1, java.awt.Color aColor2) {
		super();
		textureType = aTextureType;
		this.color1 = aColor1;
		this.color2 = aColor2;
		rebuildColoredTexture();
	}

	private void rebuildColoredTexture() {
		if (textureType == null) {
			return;
		}
		final Image initialImage = textureType.getImageIcon().getImage();
		TextureBackgroundStyle.ColorSwapFilter imgfilter = new ColorSwapFilter(java.awt.Color.BLACK, color1, java.awt.Color.WHITE, color2) {
			@Override
			public void imageComplete(int status) {
				super.imageComplete(status);
				coloredTexture = new BufferedImage(initialImage.getWidth(null), initialImage.getHeight(null), BufferedImage.TYPE_INT_ARGB);
				Graphics gi = coloredTexture.getGraphics();
				gi.drawImage(coloredImage, 0, 0, null);
			}
		};

		ImageProducer producer = new FilteredImageSource(initialImage.getSource(), imgfilter);
		coloredImage = (ToolkitImage) Toolkit.getDefaultToolkit().createImage(producer);
		ImageRepresentation consumer = new ImageRepresentation(coloredImage, null, true);
		producer.addConsumer(consumer);
		try {
			producer.startProduction(consumer);
		} catch (RuntimeException e) {
			logger.warning("Unexpected exception: " + e);
		}

	}

	private BufferedImage getColoredTexture() {
		if (coloredTexture == null) {
			rebuildColoredTexture();
			/*int tests = 10; // Time-out = 1s
			while (coloredTexture == null && tests>=0) {
				try {
					tests--;
					Thread.sleep(1);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			if (coloredTexture == null) {
				logger.warning("Could not compute colored texture");
				Image initialImage = textureType.getImageIcon().getImage();
				System.out.println("initialImage="+initialImage+" initialImage.getWidth(null)="+initialImage.getWidth(null));
				coloredTexture = 
					new BufferedImage(
							initialImage.getWidth(null), 
							initialImage.getHeight(null), 
							BufferedImage.TYPE_INT_ARGB);
				Graphics gi = coloredTexture.getGraphics();
				gi.drawImage(initialImage, 0, 0, null);
			}*/
		}
		return coloredTexture;

	}

	static class ColorSwapFilter extends RGBImageFilter {
		private int target1;
		private int replacement1;
		private int target2;
		private int replacement2;

		public ColorSwapFilter(java.awt.Color target1, java.awt.Color replacement1, java.awt.Color target2, java.awt.Color replacement2) {
			this.target1 = target1.getRGB();
			this.replacement1 = replacement1.getRGB();
			this.target2 = target2.getRGB();
			this.replacement2 = replacement2.getRGB();
		}

		@Override
		public int filterRGB(int x, int y, int rgb) {
			// if (x==0 && y==0) logger.info("Starting convert image");
			// if (x==15 && y==15) logger.info("Finished convert image");
			if (rgb == target1) {
				return replacement1;
			} else if (rgb == target2) {
				return replacement2;
			}
			return rgb;
		}

		@Override
		public void imageComplete(int status) {
			super.imageComplete(status);
			if (logger.isLoggable(Level.FINE)) {
				logger.fine("imageComplete status=" + status);
			}
		}

	}

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

	@Override
	public Paint getPaint(GraphicalRepresentation gr, double scale) {
		return new TexturePaint(getColoredTexture(), new Rectangle(0, 0, getColoredTexture().getWidth(), getColoredTexture().getHeight()));
	}

	@Override
	public BackgroundStyleType getBackgroundStyleType() {
		return BackgroundStyleType.TEXTURE;
	}

	public TextureBackgroundStyle.TextureType getTextureType() {
		return textureType;
	}

	public void setTextureType(TextureBackgroundStyle.TextureType aTextureType) {
		if (requireChange(this.textureType, aTextureType)) {
			TextureBackgroundStyle.TextureType oldTexture = textureType;
			this.textureType = aTextureType;
			rebuildColoredTexture();
			setChanged();
			notifyObservers(new FGENotification(Parameters.textureType, oldTexture, aTextureType));
		}
	}

	public java.awt.Color getColor1() {
		return color1;
	}

	public void setColor1(java.awt.Color aColor) {
		if (requireChange(this.color1, aColor)) {
			java.awt.Color oldColor = color1;
			this.color1 = aColor;
			rebuildColoredTexture();
			setChanged();
			notifyObservers(new FGENotification(Parameters.color1, oldColor, aColor));
		}
	}

	public java.awt.Color getColor2() {
		return color2;
	}

	public void setColor2(java.awt.Color aColor) {
		if (requireChange(this.color2, aColor)) {
			java.awt.Color oldColor = color2;
			this.color2 = aColor;
			rebuildColoredTexture();
			setChanged();
			notifyObservers(new FGENotification(Parameters.color2, oldColor, aColor));
		}
	}

	@Override
	public String toString() {
		return "BackgroundStyle.TEXTURE(" + getColor1() + "," + getColor2() + "," + getTextureType() + ")";
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