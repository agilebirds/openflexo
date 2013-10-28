package org.openflexo.fge.control.tools;

import java.awt.Color;
import java.awt.Image;
import java.beans.PropertyChangeSupport;
import java.io.File;
import java.util.List;
import java.util.logging.Logger;

import org.openflexo.fge.BackgroundImageBackgroundStyle;
import org.openflexo.fge.BackgroundStyle;
import org.openflexo.fge.BackgroundStyle.BackgroundStyleType;
import org.openflexo.fge.ColorBackgroundStyle;
import org.openflexo.fge.ColorGradientBackgroundStyle;
import org.openflexo.fge.ColorGradientBackgroundStyle.ColorGradientDirection;
import org.openflexo.fge.Drawing.DrawingTreeNode;
import org.openflexo.fge.Drawing.ShapeNode;
import org.openflexo.fge.FGEConstants;
import org.openflexo.fge.FGEModelFactory;
import org.openflexo.fge.NoneBackgroundStyle;
import org.openflexo.fge.TextureBackgroundStyle;
import org.openflexo.fge.TextureBackgroundStyle.TextureType;
import org.openflexo.fge.control.DianaInteractiveViewer;

/**
 * Convenient class used to manipulate BackgroundStyle
 * 
 * @author sylvain
 * 
 */
public class BackgroundStyleFactory implements StyleFactory<BackgroundStyle, BackgroundStyleType> {

	private static final Logger logger = Logger.getLogger(BackgroundStyleFactory.class.getPackage().getName());

	private static final String DELETED = "deleted";
	// private AbstractInspectedBackgroundStyle<?> backgroundStyle;

	private BackgroundStyleType backgroundStyleType = BackgroundStyleType.COLOR;

	private InspectedNoneBackgroundStyle noneBackgroundStyle;
	private InspectedColorBackgroundStyle colorBackgroundStyle;
	private InspectedColorGradientBackgroundStyle colorGradientBackgroundStyle;
	private InspectedTextureBackgroundStyle textureBackgroundStyle;
	private InspectedBackgroundImageBackgroundStyle backgroundImageBackgroundStyle;

	/*private Color color1 = Color.RED;
	private Color color2 = Color.WHITE;
	private ColorGradientDirection gradientDirection = ColorGradientDirection.NORTH_SOUTH;
	private TextureType textureType = TextureType.TEXTURE1;
	private File imageFile;*/

	private PropertyChangeSupport pcSupport;
	private FGEModelFactory fgeFactory;

	private DianaInteractiveViewer<?, ?, ?> controller;

	public BackgroundStyleFactory(DianaInteractiveViewer<?, ?, ?> controller) {
		pcSupport = new PropertyChangeSupport(this);
		this.controller = controller;
		fgeFactory = controller.getFactory();
		noneBackgroundStyle = new InspectedNoneBackgroundStyle(controller, controller.getFactory().makeEmptyBackground());
		colorBackgroundStyle = new InspectedColorBackgroundStyle(controller, controller.getFactory().makeColoredBackground(
				FGEConstants.DEFAULT_BACKGROUND_COLOR));
		colorGradientBackgroundStyle = new InspectedColorGradientBackgroundStyle(controller, controller.getFactory()
				.makeColorGradientBackground(FGEConstants.DEFAULT_BACKGROUND_COLOR, Color.WHITE,
						ColorGradientDirection.SOUTH_EAST_NORTH_WEST));
		textureBackgroundStyle = new InspectedTextureBackgroundStyle(controller, controller.getFactory().makeTexturedBackground(
				TextureType.TEXTURE1, FGEConstants.DEFAULT_BACKGROUND_COLOR, Color.WHITE));
		noneBackgroundStyle = new InspectedNoneBackgroundStyle(controller, controller.getFactory().makeEmptyBackground());
		backgroundImageBackgroundStyle = new InspectedBackgroundImageBackgroundStyle(controller, controller.getFactory()
				.makeImageBackground(FGEConstants.DEFAULT_IMAGE));
	}

	public FGEModelFactory getFGEFactory() {
		return fgeFactory;
	}

	public void setFGEFactory(FGEModelFactory fgeFactory) {
		this.fgeFactory = fgeFactory;
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

	@Override
	public AbstractInspectedBackgroundStyle<?> getCurrentStyle() {
		return getBackgroundStyle();
	}

	public AbstractInspectedBackgroundStyle<?> getBackgroundStyle() {
		switch (backgroundStyleType) {
		case NONE:
			return noneBackgroundStyle;
		case COLOR:
			return colorBackgroundStyle;
		case COLOR_GRADIENT:
			return colorGradientBackgroundStyle;
		case TEXTURE:
			return textureBackgroundStyle;
		case IMAGE:
			return backgroundImageBackgroundStyle;
		default:
			return null;
		}

	}

	/**
	 * Equals method allowing null values
	 * 
	 * @param oldObject
	 * @param newObject
	 * @return
	 */
	protected boolean requireChange(Object oldObject, Object newObject) {
		if (oldObject == null) {
			if (newObject == null) {
				return false;
			} else {
				return true;
			}
		}
		return !oldObject.equals(newObject);
	}

	public BackgroundStyleType getStyleType() {
		return backgroundStyleType;
	}

	public void setStyleType(BackgroundStyleType backgroundStyleType) {
		BackgroundStyleType oldBackgroundStyleType = getStyleType();

		if (oldBackgroundStyleType == backgroundStyleType) {
			return;
		}

		this.backgroundStyleType = backgroundStyleType;
		pcSupport.firePropertyChange(STYLE_CLASS_CHANGED, oldBackgroundStyleType, getStyleType());
		pcSupport.firePropertyChange("backgroundStyleType", oldBackgroundStyleType, getStyleType());
	}

	@Override
	public BackgroundStyle makeNewStyle(BackgroundStyle oldStyle) {
		BackgroundStyle returned = null;
		switch (backgroundStyleType) {
		case NONE:
			returned = noneBackgroundStyle.cloneStyle();
			break;
		case COLOR:
			ColorBackgroundStyle returnedColor = (ColorBackgroundStyle) colorBackgroundStyle.cloneStyle();
			if (oldStyle instanceof ColorGradientBackgroundStyle) {
				returnedColor.setColor(((ColorGradientBackgroundStyle) oldStyle).getColor1());
			}
			if (oldStyle instanceof TextureBackgroundStyle) {
				returnedColor.setColor(((TextureBackgroundStyle) oldStyle).getColor1());
			}
			returned = returnedColor;
			break;
		case COLOR_GRADIENT:
			ColorGradientBackgroundStyle returnedColorGradient = (ColorGradientBackgroundStyle) colorGradientBackgroundStyle.cloneStyle();
			if (oldStyle instanceof ColorBackgroundStyle) {
				returnedColorGradient.setColor1(((ColorBackgroundStyle) oldStyle).getColor());
			}
			if (oldStyle instanceof TextureBackgroundStyle) {
				returnedColorGradient.setColor1(((TextureBackgroundStyle) oldStyle).getColor1());
				returnedColorGradient.setColor2(((TextureBackgroundStyle) oldStyle).getColor2());
			}
			returned = returnedColorGradient;
			break;
		case TEXTURE:
			TextureBackgroundStyle returnedTexture = (TextureBackgroundStyle) textureBackgroundStyle.cloneStyle();
			if (oldStyle instanceof ColorBackgroundStyle) {
				returnedTexture.setColor1(((ColorBackgroundStyle) oldStyle).getColor());
			}
			if (oldStyle instanceof ColorGradientBackgroundStyle) {
				returnedTexture.setColor1(((ColorGradientBackgroundStyle) oldStyle).getColor1());
				returnedTexture.setColor2(((ColorGradientBackgroundStyle) oldStyle).getColor2());
			}
			returned = returnedTexture;
			break;
		case IMAGE:
			returned = backgroundImageBackgroundStyle.cloneStyle();
			break;
		default:
			break;
		}

		if (oldStyle != null) {
			returned.setUseTransparency(oldStyle.getUseTransparency());
			returned.setTransparencyLevel(oldStyle.getTransparencyLevel());
		}

		return returned;

	}

	/*public InspectedColorBackgroundStyle makeEmptyBackground() {
		return null;
	}

	public InspectedColorBackgroundStyle makeColoredBackground() {
		return new InspectedColorBackgroundStyle(controller, fgeFactory.makeColoredBackground(color1));
	}

	public InspectedColorBackgroundStyle makeColorGradientBackground() {
		return null;
	}

	public InspectedColorBackgroundStyle makeTexturedBackground() {
		return null;
	}

	public InspectedColorBackgroundStyle makeImageBackground() {
		return null;
	}*/

	protected abstract class AbstractInspectedBackgroundStyle<BS extends BackgroundStyle> extends InspectedStyle<BS> implements
			BackgroundStyle {

		protected AbstractInspectedBackgroundStyle(DianaInteractiveViewer<?, ?, ?> controller, BS defaultValue) {
			super(controller, defaultValue);
		}

		@Override
		public float getTransparencyLevel() {
			return getPropertyValue(BackgroundStyle.TRANSPARENCY_LEVEL);
		}

		@Override
		public void setTransparencyLevel(float aLevel) {
			setPropertyValue(BackgroundStyle.TRANSPARENCY_LEVEL, aLevel);
		}

		@Override
		public boolean getUseTransparency() {
			return getPropertyValue(BackgroundStyle.USE_TRANSPARENCY);
		}

		@Override
		public void setUseTransparency(boolean aFlag) {
			setPropertyValue(BackgroundStyle.USE_TRANSPARENCY, aFlag);
		}

		@Override
		public List<ShapeNode<?>> getSelection() {
			return getController().getSelectedShapes();
		}

	}

	protected class InspectedNoneBackgroundStyle extends AbstractInspectedBackgroundStyle<NoneBackgroundStyle> implements
			NoneBackgroundStyle {

		protected InspectedNoneBackgroundStyle(DianaInteractiveViewer<?, ?, ?> controller, NoneBackgroundStyle defaultValue) {
			super(controller, defaultValue);
		}

		@Override
		public BackgroundStyleType getBackgroundStyleType() {
			return BackgroundStyleType.NONE;
		}

		@Override
		public NoneBackgroundStyle getStyle(DrawingTreeNode<?, ?> node) {
			if (node instanceof ShapeNode) {
				if (((ShapeNode<?>) node).getBackgroundStyle() instanceof NoneBackgroundStyle) {
					return (NoneBackgroundStyle) ((ShapeNode<?>) node).getBackgroundStyle();
				}
			}
			return null;
		}
	}

	protected class InspectedColorBackgroundStyle extends AbstractInspectedBackgroundStyle<ColorBackgroundStyle> implements
			ColorBackgroundStyle {

		protected InspectedColorBackgroundStyle(DianaInteractiveViewer<?, ?, ?> controller, ColorBackgroundStyle defaultValue) {
			super(controller, defaultValue);
		}

		@Override
		public BackgroundStyleType getBackgroundStyleType() {
			return BackgroundStyleType.COLOR;
		}

		@Override
		public Color getColor() {
			return getPropertyValue(ColorBackgroundStyle.COLOR);
		}

		@Override
		public void setColor(Color aColor) {
			setPropertyValue(ColorBackgroundStyle.COLOR, aColor);
		}

		@Override
		public ColorBackgroundStyle getStyle(DrawingTreeNode<?, ?> node) {
			if (node instanceof ShapeNode) {
				if (((ShapeNode<?>) node).getBackgroundStyle() instanceof ColorBackgroundStyle) {
					return (ColorBackgroundStyle) ((ShapeNode<?>) node).getBackgroundStyle();
				}
			}
			return null;
		}
	}

	protected class InspectedColorGradientBackgroundStyle extends AbstractInspectedBackgroundStyle<ColorGradientBackgroundStyle> implements
			ColorGradientBackgroundStyle {

		protected InspectedColorGradientBackgroundStyle(DianaInteractiveViewer<?, ?, ?> controller,
				ColorGradientBackgroundStyle defaultValue) {
			super(controller, defaultValue);
		}

		@Override
		public BackgroundStyleType getBackgroundStyleType() {
			return BackgroundStyleType.COLOR_GRADIENT;
		}

		@Override
		public ColorGradientBackgroundStyle getStyle(DrawingTreeNode<?, ?> node) {
			if (node instanceof ShapeNode) {
				if (((ShapeNode<?>) node).getBackgroundStyle() instanceof ColorGradientBackgroundStyle) {
					return (ColorGradientBackgroundStyle) ((ShapeNode<?>) node).getBackgroundStyle();
				}
			}
			return null;
		}

		@Override
		public Color getColor1() {
			return getPropertyValue(ColorGradientBackgroundStyle.COLOR1);
		}

		@Override
		public void setColor1(Color aColor) {
			setPropertyValue(ColorGradientBackgroundStyle.COLOR1, aColor);
		}

		@Override
		public Color getColor2() {
			return getPropertyValue(ColorGradientBackgroundStyle.COLOR2);
		}

		@Override
		public void setColor2(Color aColor) {
			setPropertyValue(ColorGradientBackgroundStyle.COLOR2, aColor);
		}

		@Override
		public ColorGradientDirection getDirection() {
			return getPropertyValue(ColorGradientBackgroundStyle.DIRECTION);
		}

		@Override
		public void setDirection(ColorGradientDirection aDirection) {
			setPropertyValue(ColorGradientBackgroundStyle.DIRECTION, aDirection);
		}
	}

	protected class InspectedTextureBackgroundStyle extends AbstractInspectedBackgroundStyle<TextureBackgroundStyle> implements
			TextureBackgroundStyle {

		protected InspectedTextureBackgroundStyle(DianaInteractiveViewer<?, ?, ?> controller, TextureBackgroundStyle defaultValue) {
			super(controller, defaultValue);
		}

		@Override
		public BackgroundStyleType getBackgroundStyleType() {
			return BackgroundStyleType.TEXTURE;
		}

		@Override
		public TextureBackgroundStyle getStyle(DrawingTreeNode<?, ?> node) {
			if (node instanceof ShapeNode) {
				if (((ShapeNode<?>) node).getBackgroundStyle() instanceof TextureBackgroundStyle) {
					return (TextureBackgroundStyle) ((ShapeNode<?>) node).getBackgroundStyle();
				}
			}
			return null;
		}

		@Override
		public TextureType getTextureType() {
			return getPropertyValue(TextureBackgroundStyle.TEXTURE_TYPE);
		}

		@Override
		public void setTextureType(TextureType aTextureType) {
			setPropertyValue(TextureBackgroundStyle.TEXTURE_TYPE, aTextureType);
		}

		@Override
		public Color getColor1() {
			return getPropertyValue(TextureBackgroundStyle.COLOR1);
		}

		@Override
		public void setColor1(Color aColor) {
			setPropertyValue(TextureBackgroundStyle.COLOR1, aColor);
		}

		@Override
		public Color getColor2() {
			return getPropertyValue(TextureBackgroundStyle.COLOR2);
		}

		@Override
		public void setColor2(Color aColor) {
			setPropertyValue(TextureBackgroundStyle.COLOR2, aColor);
		}
	}

	protected class InspectedBackgroundImageBackgroundStyle extends AbstractInspectedBackgroundStyle<BackgroundImageBackgroundStyle>
			implements BackgroundImageBackgroundStyle {

		protected InspectedBackgroundImageBackgroundStyle(DianaInteractiveViewer<?, ?, ?> controller,
				BackgroundImageBackgroundStyle defaultValue) {
			super(controller, defaultValue);
		}

		@Override
		public BackgroundStyleType getBackgroundStyleType() {
			return BackgroundStyleType.IMAGE;
		}

		@Override
		public BackgroundImageBackgroundStyle getStyle(DrawingTreeNode<?, ?> node) {
			if (node instanceof ShapeNode) {
				if (((ShapeNode<?>) node).getBackgroundStyle() instanceof BackgroundImageBackgroundStyle) {
					return (BackgroundImageBackgroundStyle) ((ShapeNode<?>) node).getBackgroundStyle();
				}
			}
			return null;
		}

		@Override
		public File getImageFile() {
			return getPropertyValue(BackgroundImageBackgroundStyle.IMAGE_FILE);
		}

		@Override
		public void setImageFile(File anImageFile) {
			setPropertyValue(BackgroundImageBackgroundStyle.IMAGE_FILE, anImageFile);
		}

		@Override
		public Color getImageBackgroundColor() {
			return getPropertyValue(BackgroundImageBackgroundStyle.IMAGE_BACKGROUND_COLOR);
		}

		@Override
		public void setImageBackgroundColor(Color aColor) {
			setPropertyValue(BackgroundImageBackgroundStyle.IMAGE_BACKGROUND_COLOR, aColor);
		}

		@Override
		public double getDeltaX() {
			return getPropertyValue(BackgroundImageBackgroundStyle.DELTA_X);
		}

		@Override
		public void setDeltaX(double aDeltaX) {
			setPropertyValue(BackgroundImageBackgroundStyle.DELTA_X, aDeltaX);
		}

		@Override
		public double getDeltaY() {
			return getPropertyValue(BackgroundImageBackgroundStyle.DELTA_Y);
		}

		@Override
		public void setDeltaY(double aDeltaY) {
			setPropertyValue(BackgroundImageBackgroundStyle.DELTA_Y, aDeltaY);
		}

		@Override
		public ImageBackgroundType getImageBackgroundType() {
			return getPropertyValue(BackgroundImageBackgroundStyle.IMAGE_BACKGROUND_TYPE);
		}

		@Override
		public void setImageBackgroundType(ImageBackgroundType anImageBackgroundType) {
			setPropertyValue(BackgroundImageBackgroundStyle.IMAGE_BACKGROUND_TYPE, anImageBackgroundType);
		}

		@Override
		public double getScaleX() {
			return getPropertyValue(BackgroundImageBackgroundStyle.SCALE_X);
		}

		@Override
		public void setScaleX(double aScaleX) {
			setPropertyValue(BackgroundImageBackgroundStyle.SCALE_X, aScaleX);
		}

		@Override
		public double getScaleY() {
			return getPropertyValue(BackgroundImageBackgroundStyle.SCALE_Y);
		}

		@Override
		public void setScaleY(double aScaleY) {
			setPropertyValue(BackgroundImageBackgroundStyle.SCALE_Y, aScaleY);
		}

		@Override
		public boolean getFitToShape() {
			return getPropertyValue(BackgroundImageBackgroundStyle.FIT_TO_SHAPE);
		}

		@Override
		public void setFitToShape(boolean aFlag) {
			setPropertyValue(BackgroundImageBackgroundStyle.FIT_TO_SHAPE, aFlag);
		}

		@Override
		public Image getImage() {
			return null;
		}

		@Override
		public void setImage(Image image) {
			// TODO Auto-generated method stub

		}

		@Override
		public void setScaleXNoNotification(double aScaleX) {
			// TODO Auto-generated method stub

		}

		@Override
		public void setScaleYNoNotification(double aScaleY) {
			// TODO Auto-generated method stub

		}
	}

}