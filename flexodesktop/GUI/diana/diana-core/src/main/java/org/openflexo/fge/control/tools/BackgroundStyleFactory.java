package org.openflexo.fge.control.tools;

import java.awt.Color;
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
import org.openflexo.fge.TextureBackgroundStyle;
import org.openflexo.fge.TextureBackgroundStyle.TextureType;
import org.openflexo.fge.control.DianaInteractiveViewer;

/**
 * Convenient class used to manipulate BackgroundStyle
 * 
 * @author sylvain
 * 
 */
public class BackgroundStyleFactory implements StyleFactory<BackgroundStyle> {

	private static final Logger logger = Logger.getLogger(BackgroundStyleFactory.class.getPackage().getName());

	private static final String DELETED = "deleted";
	private AbstractInspectedBackgroundStyle<?> backgroundStyle;
	private Color color1 = Color.RED;
	private Color color2 = Color.WHITE;
	private ColorGradientDirection gradientDirection = ColorGradientDirection.NORTH_SOUTH;
	private TextureType textureType = TextureType.TEXTURE1;
	private File imageFile;
	private PropertyChangeSupport pcSupport;
	private FGEModelFactory fgeFactory;

	private DianaInteractiveViewer<?, ?, ?> controller;

	public BackgroundStyleFactory(DianaInteractiveViewer<?, ?, ?> controller) {
		pcSupport = new PropertyChangeSupport(this);
		this.controller = controller;
		fgeFactory = controller.getFactory();
		backgroundStyle = new InspectedColorBackgroundStyle(controller, controller.getFactory().makeColoredBackground(
				FGEConstants.DEFAULT_BACKGROUND_COLOR));
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
		return backgroundStyle;
	}

	public void setBackgroundStyle(AbstractInspectedBackgroundStyle<?> backgroundStyle) {
		if (this.backgroundStyle != backgroundStyle) {
			BackgroundStyle oldBackgroundStyle = this.backgroundStyle;
			this.backgroundStyle = backgroundStyle;
			pcSupport.firePropertyChange("backgroundStyle", oldBackgroundStyle, backgroundStyle);
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
			setBackgroundStyle(makeEmptyBackground());
			break;
		case COLOR:
			setBackgroundStyle(makeColoredBackground());
			break;
		case COLOR_GRADIENT:
			setBackgroundStyle(makeColorGradientBackground());
			break;
		case TEXTURE:
			setBackgroundStyle(makeTexturedBackground());
			break;
		case IMAGE:
			setBackgroundStyle(makeImageBackground());
			break;
		default:
			break;
		}

		pcSupport.firePropertyChange("backgroundStyleType", oldBackgroundStyleType, getBackgroundStyleType());
	}

	@Override
	public BackgroundStyle makeNewStyle() {
		return fgeFactory.makeColoredBackground(color1);
		// return (BackgroundStyle) getCurrentStyle().cloneObject();
	}

	/**
	 * Make a new background style as empty background (invisible)
	 * 
	 * @return a newly created BackgroundStyle
	 */
	public InspectedColorBackgroundStyle makeEmptyBackground() {
		return null;
	}

	/**
	 * Make a new background style as plain colored background
	 * 
	 * @return a newly created BackgroundStyle
	 */
	public InspectedColorBackgroundStyle makeColoredBackground() {
		return new InspectedColorBackgroundStyle(controller, fgeFactory.makeColoredBackground(color1));
	}

	/**
	 * Make a new background style as color gradient with two colors
	 * 
	 * @return a newly created BackgroundStyle
	 */
	public InspectedColorBackgroundStyle makeColorGradientBackground() {
		return null;
	}

	/**
	 * Make a new background style as textured background with two colors
	 * 
	 * @return a newly created BackgroundStyle
	 */
	public InspectedColorBackgroundStyle makeTexturedBackground() {
		return null;
	}

	/**
	 * Make a new background style as image background, given a file encoding image
	 * 
	 * @param imageFile
	 *            the file where image is located (most image format allowed)
	 * 
	 * @return a newly created BackgroundStyle
	 */
	public InspectedColorBackgroundStyle makeImageBackground() {
		return null;
	}

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
}