/*
 * (c) Copyright 2010-2011 AgileBirds
 *
 * This file is part of OpenFlexo.
 *
 * OpenFlexo is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * OpenFlexo is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with OpenFlexo. If not, see <http://www.gnu.org/licenses/>.
 *
 */
package org.openflexo.fge.graphics;

import java.awt.GradientPaint;
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
import java.io.File;
import java.util.Observable;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.ImageIcon;

import org.openflexo.fge.GraphicalRepresentation;
import org.openflexo.fge.GraphicalRepresentation.GRParameter;
import org.openflexo.fge.notifications.FGENotification;
import org.openflexo.inspector.HasIcon;
import org.openflexo.toolbox.ImageIconResource;
import org.openflexo.xmlcode.XMLSerializable;

import sun.awt.image.ImageRepresentation;
import sun.awt.image.ToolkitImage;

public abstract class BackgroundStyle extends Observable implements XMLSerializable,Cloneable
{
    private static final Logger logger = Logger.getLogger(BackgroundStyle.class.getPackage().getName());

	private transient GraphicalRepresentation graphicalRepresentation;

	private boolean useTransparency = false;
	private float transparencyLevel = 0.5f; // Between 0.0 and 1.0
	
	public static enum Parameters implements GRParameter {
		color,
		color1,
		color2,
		direction,
		textureType,
		imageFile,
		deltaX,
		deltaY,
		imageBackgroundType,
		scaleX,
		scaleY,
		fitToShape,
		imageBackgroundColor,
		transparencyLevel,
		useTransparency;
	}
	
	public static BackgroundStyle makeEmptyBackground()
	{
		return new None();
	}
	
	public static BackgroundStyle makeColoredBackground(java.awt.Color aColor)
	{
		return new Color(aColor);
	}
	
	public static BackgroundStyle makeColorGradientBackground(
			java.awt.Color color1,
			java.awt.Color color2,
			ColorGradient.ColorGradientDirection direction)
	{
		return new ColorGradient(color1,color2,direction);
	}
	
	public static BackgroundStyle makeTexturedBackground(Texture.TextureType type,java.awt.Color aColor1, java.awt.Color aColor2)
	{
		return new Texture(type,aColor1,aColor2);
	}
	
	public static BackgroundImage makeImageBackground(File imageFile)
	{
		return new BackgroundImage(imageFile);
	}
	
	public static BackgroundImage makeImageBackground(ImageIcon image)
	{
		return new BackgroundImage(image);
	}
	
	public static BackgroundStyle makeBackground(BackgroundStyleType type)
	{
		if (type == BackgroundStyleType.NONE) return makeEmptyBackground();
		else if (type == BackgroundStyleType.COLOR) return makeColoredBackground(java.awt.Color.WHITE);
		else if (type == BackgroundStyleType.COLOR_GRADIENT) return makeColorGradientBackground(java.awt.Color.WHITE, java.awt.Color.BLACK, org.openflexo.fge.graphics.BackgroundStyle.ColorGradient.ColorGradientDirection.SOUTH_EAST_NORTH_WEST);
		else if (type == BackgroundStyleType.TEXTURE) return makeTexturedBackground(org.openflexo.fge.graphics.BackgroundStyle.Texture.TextureType.TEXTURE1,java.awt.Color.RED, java.awt.Color.WHITE);
		else if (type == BackgroundStyleType.IMAGE) return makeImageBackground((File)null);
		return null;
	}
	
	public abstract Paint getPaint(GraphicalRepresentation gr, double scale);
	
	public abstract BackgroundStyleType getBackgroundStyleType();

	/*public GraphicalRepresentation getGraphicalRepresentation()
	{
		return graphicalRepresentation;
	}

	public void setGraphicalRepresentation(
			GraphicalRepresentation graphicalRepresentation)
	{
		this.graphicalRepresentation = graphicalRepresentation;
	}*/

	@Override
	public abstract String toString();
	
	

	public static enum BackgroundStyleType
	{
		NONE,
		COLOR,
		COLOR_GRADIENT,
		TEXTURE,
		IMAGE
	}
	
	public static class None extends BackgroundStyle
	{
		@Override
		public Paint getPaint(GraphicalRepresentation gr, double scale)
		{
			return null;
		}
		
		@Override
		public BackgroundStyleType getBackgroundStyleType()
		{
			return BackgroundStyleType.NONE;
		}
		
		@Override
		public String toString()
		{
			return "BackgroundStyle.NONE";
		}
	}
	
	public static class Color extends BackgroundStyle
	{
		private java.awt.Color color;
		
		public Color() 
		{
			color = java.awt.Color.WHITE;
		}
		
		public Color(java.awt.Color aColor) 
		{
			color = aColor;
		}
		
		@Override
		public Paint getPaint(GraphicalRepresentation gr, double scale)
		{
			return color;
		}

		public java.awt.Color getColor()
		{
			return color;
		}

		@Override
		public BackgroundStyleType getBackgroundStyleType()
		{
			return BackgroundStyleType.COLOR;
		}
		
		public void setColor(java.awt.Color aColor) 
		{
			if (requireChange(this.color, aColor)) {
				java.awt.Color oldColor = color;
				this.color = aColor;
				setChanged();
				notifyObservers(new FGENotification(Parameters.color,oldColor,aColor));
			}
		}


		@Override
		public String toString()
		{
			return "BackgroundStyle.COLOR("+getColor()+")";
		}
		
		private boolean requireChange (Object oldObject, Object newObject)
		{
			if (oldObject == null) {
				if (newObject == null) return false;
				else return true;
			}
			return !oldObject.equals(newObject);
		}

	}

	public static class ColorGradient extends BackgroundStyle
	{
		private java.awt.Color color1;
		private java.awt.Color color2;
		private ColorGradientDirection direction;
		
		public ColorGradient() 
		{
			this(java.awt.Color.WHITE, java.awt.Color.BLACK, org.openflexo.fge.graphics.BackgroundStyle.ColorGradient.ColorGradientDirection.SOUTH_EAST_NORTH_WEST);
		}

		public ColorGradient(java.awt.Color aColor1, java.awt.Color aColor2, ColorGradientDirection aDirection) 
		{
			super();
			this.color1 = aColor1;
			this.color2 = aColor2;
			this.direction = aDirection;
		}

		public static enum ColorGradientDirection
		{
			NORTH_SOUTH,
			WEST_EAST,
			SOUTH_EAST_NORTH_WEST,
			SOUTH_WEST_NORTH_EAST
		}
		
		@Override
		public Paint getPaint(GraphicalRepresentation gr, double scale)
		{
			switch (direction) {
			case SOUTH_EAST_NORTH_WEST:
				return new GradientPaint(0,0,color1,gr.getViewWidth(scale),gr.getViewHeight(scale),color2);
			case SOUTH_WEST_NORTH_EAST:
				return new GradientPaint(0,gr.getViewHeight(scale),color1,gr.getViewWidth(scale),0,color2);
			case WEST_EAST:
				return new GradientPaint(0,0.5f*gr.getViewHeight(scale),color1,gr.getViewWidth(scale),0.5f*gr.getViewHeight(scale),color2);
			case NORTH_SOUTH:
				return new GradientPaint(0.5f*gr.getViewWidth(scale),0,color1,0.5f*gr.getViewWidth(scale),gr.getViewHeight(scale),color2);
			default:
				return new GradientPaint(0,0,color1,gr.getViewWidth(scale),gr.getViewHeight(scale),color2);
			}			
		}

		@Override
		public BackgroundStyleType getBackgroundStyleType()
		{
			return BackgroundStyleType.COLOR_GRADIENT;
		}

		public java.awt.Color getColor1()
		{
			return color1;
		}

		public void setColor1(java.awt.Color aColor)
		{
			if (requireChange(this.color1, aColor)) {
				java.awt.Color oldColor = color1;
				this.color1 = aColor;
				setChanged();
				notifyObservers(new FGENotification(Parameters.color1,oldColor,aColor));
			}
		}

		public java.awt.Color getColor2()
		{
			return color2;
		}

		public void setColor2(java.awt.Color aColor) 
		{
			if (requireChange(this.color2, aColor)) {
				java.awt.Color oldColor = color2;
				this.color2 = aColor;
				setChanged();
				notifyObservers(new FGENotification(Parameters.color2,oldColor,aColor));
			}
		}

		public ColorGradientDirection getDirection()
		{
			return direction;
		}

		public void setDirection(ColorGradientDirection aDirection)
		{
			if (requireChange(this.direction, aDirection)) {
				ColorGradientDirection oldTexture = direction;
				this.direction = aDirection;
				setChanged();
				notifyObservers(new FGENotification(Parameters.direction,oldTexture,aDirection));
			}
		}

		@Override
		public String toString()
		{
			return "BackgroundStyle.COLOR_GRADIENT("+getColor1()+","+getColor2()+","+getDirection()+")";
		}
		
		private boolean requireChange (Object oldObject, Object newObject)
		{
			if (oldObject == null) {
				if (newObject == null) return false;
				else return true;
			}
			return !oldObject.equals(newObject);
		}

	}

	public static class Texture extends BackgroundStyle
	{
		private TextureType textureType;
		private java.awt.Color color1;
		private java.awt.Color color2;
		private BufferedImage coloredTexture;
		private ToolkitImage coloredImage;
		
		public Texture() 
		{
			this(TextureType.TEXTURE1,java.awt.Color.WHITE, java.awt.Color.BLACK);
		}
		
		public Texture(TextureType aTextureType,java.awt.Color aColor1, java.awt.Color aColor2) 
		{
			super();
			textureType = aTextureType;
			this.color1 = aColor1;
			this.color2 = aColor2;
			rebuildColoredTexture();
		}
		
		private void rebuildColoredTexture()
		{
			if (textureType == null) return;
			 final Image initialImage = textureType.getImageIcon().getImage();
			 ColorSwapFilter imgfilter = 
				new ColorSwapFilter(java.awt.Color.BLACK, color1, java.awt.Color.WHITE, color2)  {
				 @Override
				public void imageComplete(int status)
				{
					super.imageComplete(status);
					coloredTexture = 
						new BufferedImage(
								initialImage.getWidth(null), 
								initialImage.getHeight(null), 
								BufferedImage.TYPE_INT_ARGB);
					Graphics gi = coloredTexture.getGraphics();
					gi.drawImage(coloredImage, 0, 0, null);
				}
			 };
			
			ImageProducer producer = new FilteredImageSource(initialImage.getSource(),imgfilter);
			coloredImage = (ToolkitImage)Toolkit.getDefaultToolkit().createImage(producer);
			ImageRepresentation consumer = new ImageRepresentation(coloredImage,null,true);
			producer.addConsumer(consumer);
			try {
			producer.startProduction(consumer);
			}
			catch (RuntimeException e) {
				logger.warning("Unexpected exception: "+e);
			}
			
		}
		
		private BufferedImage getColoredTexture()
		{
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
		
	    static class ColorSwapFilter extends RGBImageFilter 
	    {
	        private int target1;
	        private int replacement1;
	        private int target2;
	        private int replacement2;
	        
	       
	        public ColorSwapFilter(java.awt.Color target1, 
	        		java.awt.Color replacement1,
	        		java.awt.Color target2, 
	        		java.awt.Color replacement2) 
	        {
	        	this.target1 = target1.getRGB();
	        	this.replacement1 = replacement1.getRGB();
	        	this.target2= target2.getRGB();
	        	this.replacement2 = replacement2.getRGB();
	        }
	        
	        

	        @Override
			public int filterRGB(int x, int y, int rgb) 
	        {
	        	//if (x==0 && y==0) logger.info("Starting convert image");
	        	//if (x==15 && y==15) logger.info("Finished convert image");
	            if(rgb == target1) {
	                return replacement1;
	            }
	            else if(rgb == target2) {
	                return replacement2;
	            }
	            return rgb;
	        }
	        
	        @Override
	        public void imageComplete(int status)
	        {
	        	super.imageComplete(status);
	        	if (logger.isLoggable(Level.FINE)) logger.fine("imageComplete status="+status);
	        }
	        
	    }

		public static enum TextureType implements HasIcon
		{
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
			
			public ImageIcon getImageIcon()
			{
				return new ImageIconResource("Motifs/Motif"+(ordinal()+1)+".gif");
			}

			@Override
			public ImageIcon getIcon()
			{
				return getImageIcon();
			}
			
		}
		
		@Override
		public Paint getPaint(GraphicalRepresentation gr, double scale)
		{
			return new TexturePaint(getColoredTexture(),
					new Rectangle(0,0,getColoredTexture().getWidth(),getColoredTexture().getHeight()));
		}
		
		@Override
		public BackgroundStyleType getBackgroundStyleType()
		{
			return BackgroundStyleType.TEXTURE;
		}

		public TextureType getTextureType()
		{
			return textureType;
		}

		public void setTextureType(TextureType aTextureType)
		{
			if (requireChange(this.textureType, aTextureType)) {
				TextureType oldTexture = textureType;
				this.textureType = aTextureType;
				rebuildColoredTexture();
				setChanged();
				notifyObservers(new FGENotification(Parameters.textureType,oldTexture,aTextureType));
			}
		}

		public java.awt.Color getColor1()
		{
			return color1;
		}

		public void setColor1(java.awt.Color aColor)
		{
			if (requireChange(this.color1, aColor)) {
				java.awt.Color oldColor = color1;
				this.color1 = aColor;
				rebuildColoredTexture();
				setChanged();
				notifyObservers(new FGENotification(Parameters.color1,oldColor,aColor));
			}
		}

		public java.awt.Color getColor2()
		{
			return color2;
		}

		public void setColor2(java.awt.Color aColor) 
		{
			if (requireChange(this.color2, aColor)) {
				java.awt.Color oldColor = color2;
				this.color2 = aColor;
				rebuildColoredTexture();
				setChanged();
				notifyObservers(new FGENotification(Parameters.color2,oldColor,aColor));
			}
		}
		
		@Override
		public String toString()
		{
			return "BackgroundStyle.TEXTURE("+getColor1()+","+getColor2()+","+getTextureType()+")";
		}

		private boolean requireChange (Object oldObject, Object newObject)
		{
			if (oldObject == null) {
				if (newObject == null) return false;
				else return true;
			}
			return !oldObject.equals(newObject);
		}
	}

	public static class BackgroundImage extends BackgroundStyle
	{
		private File imageFile;	
		private Image image;
		
		public BackgroundImage() 
		{
			this((File)null);
		}
		
		public BackgroundImage(File imageFile) 
		{
			super();
			setImageFile(imageFile);
		}
		
		public BackgroundImage(ImageIcon image) 
		{
			super();
			this.image = image.getImage();
		}

		@Override
		public Paint getPaint(GraphicalRepresentation gr, double scale)
		{
			return java.awt.Color.WHITE;
		}
		
		@Override
		public BackgroundStyleType getBackgroundStyleType()
		{
			return BackgroundStyleType.IMAGE;
		}

		public File getImageFile()
		{
			return imageFile;
		}

		public void setImageFile(File anImageFile)
		{
			if (requireChange(this.imageFile, anImageFile)) {
				File oldFile = imageFile;
				imageFile = anImageFile;
				if (anImageFile != null && anImageFile.exists()) {
					image = (new ImageIcon(anImageFile.getAbsolutePath())).getImage();
				}
				else image = null;
				setChanged();
				notifyObservers(new FGENotification(Parameters.imageFile,oldFile,anImageFile));
			}
		}

		public Image getImage()
		{
			return image;
		}

		private ImageBackgroundType imageBackgroundType = ImageBackgroundType.TRANSPARENT;
		private double scaleX = 1.0;
		private double scaleY = 1.0;
		private double deltaX = 0.0;
		private double deltaY = 0.0;
		private java.awt.Color imageBackgroundColor = java.awt.Color.WHITE;
		private boolean fitToShape = false;
		
		public static enum ImageBackgroundType
		{
			OPAQUE,
			TRANSPARENT
		}

		public java.awt.Color getImageBackgroundColor()
		{
			return imageBackgroundColor;
		}

		public void setImageBackgroundColor(java.awt.Color aColor)
		{
			if (requireChange(this.imageBackgroundColor, aColor)) {
				java.awt.Color oldColor = imageBackgroundColor;
				this.imageBackgroundColor = aColor;
				setChanged();
				notifyObservers(new FGENotification(Parameters.imageBackgroundColor,oldColor,aColor));
			}
		}

		public double getDeltaX()
		{
			return deltaX;
		}

		public void setDeltaX(double aDeltaX)
		{
			if (requireChange(this.deltaX, aDeltaX)) {
				double oldDeltaX = this.deltaX;
				this.deltaX = aDeltaX;
				setChanged();
				notifyObservers(new FGENotification(Parameters.deltaX,oldDeltaX,deltaX));
			}
		}

		public double getDeltaY()
		{
			return deltaY;
		}

		public void setDeltaY(double aDeltaY)
		{
			if (requireChange(this.deltaY, aDeltaY)) {
				double oldDeltaY = this.deltaY;
				this.deltaY = aDeltaY;
				setChanged();
				notifyObservers(new FGENotification(Parameters.deltaY,oldDeltaY,deltaY));
			}
		}

		public ImageBackgroundType getImageBackgroundType()
		{
			return imageBackgroundType;
		}

		public void setImageBackgroundType(ImageBackgroundType anImageBackgroundType)
		{
			if (requireChange(this.imageBackgroundType, anImageBackgroundType)) {
				ImageBackgroundType oldImageBackgroundType = this.imageBackgroundType;
				this.imageBackgroundType = anImageBackgroundType;
				setChanged();
				notifyObservers(new FGENotification(Parameters.imageBackgroundType,oldImageBackgroundType,anImageBackgroundType));
			}
		}

		public double getScaleX()
		{
			return scaleX;
		}

		public void setScaleX(double aScaleX)
		{
			if (requireChange(this.scaleX, aScaleX)) {
				double oldScaleX = this.scaleX;
				//logger.info(toString()+": Sets scaleX from "+oldScaleX+" to "+aScaleX);
				this.scaleX = aScaleX;
				setChanged();
				notifyObservers(new FGENotification(Parameters.scaleX,oldScaleX,scaleX));
			}
		}

		public void setScaleXNoNotification(double aScaleX)
		{
			if (requireChange(this.scaleX, aScaleX)) {
				this.scaleX = aScaleX;
			}
		}

		public double getScaleY()
		{
			return scaleY;
		}

		public void setScaleY(double aScaleY)
		{
			if (requireChange(this.scaleY, aScaleY)) {
				double oldScaleY = this.scaleY;
				//logger.info(toString()+": Sets scaleY from "+oldScaleY+" to "+aScaleY);
				this.scaleY = aScaleY;
				setChanged();
				notifyObservers(new FGENotification(Parameters.scaleY,oldScaleY,scaleY));
			}
		}

		public void setScaleYNoNotification(double aScaleY)
		{
			if (requireChange(this.scaleY, aScaleY)) {
				this.scaleY = aScaleY;
			}
		}

		public boolean getFitToShape()
		{
			return fitToShape;
		}

		public void setFitToShape(boolean aFlag)
		{
			if (requireChange(this.fitToShape, aFlag)) {
				boolean oldValue = fitToShape;
				this.fitToShape = aFlag;
				setChanged();
				notifyObservers(new FGENotification(Parameters.fitToShape,oldValue,aFlag));
			}
		}

		@Override
		public String toString()
		{
			return "BackgroundStyle.IMAGE("+getImageFile()+")";
		}

		private boolean requireChange (Object oldObject, Object newObject)
		{
			if (oldObject == null) {
				if (newObject == null) return false;
				else return true;
			}
			return !oldObject.equals(newObject);
		}

	}

	public float getTransparencyLevel()
	{
		return transparencyLevel;
	}

	public void setTransparencyLevel(float aLevel)
	{
		if (requireChange(this.transparencyLevel, aLevel)) {
			float oldValue = transparencyLevel;
			this.transparencyLevel = aLevel;
			setChanged();
			notifyObservers(new FGENotification(Parameters.transparencyLevel,oldValue,aLevel));
		}
	}

	public boolean getUseTransparency()
	{
		return useTransparency;
	}

	public void setUseTransparency(boolean aFlag)
	{
		if (requireChange(this.useTransparency, aFlag)) {
			boolean oldValue = useTransparency;
			this.useTransparency = aFlag;
			setChanged();
			notifyObservers(new FGENotification(Parameters.useTransparency,oldValue,aFlag));
		}
	}
		
	@Override
	public BackgroundStyle clone()
	{
		try {
			BackgroundStyle returned = (BackgroundStyle)super.clone();
			returned.graphicalRepresentation = null;
			return returned;
		} catch (CloneNotSupportedException e) {
			// cannot happen since we are clonable
			e.printStackTrace();
			return null;
		}
	}

	private boolean requireChange (Object oldObject, Object newObject)
	{
		if (oldObject == null) {
			if (newObject == null) return false;
			else return true;
		}
		return !oldObject.equals(newObject);
	}


}
