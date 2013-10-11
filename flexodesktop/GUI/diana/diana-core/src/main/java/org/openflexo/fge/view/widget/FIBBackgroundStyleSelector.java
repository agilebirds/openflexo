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
package org.openflexo.fge.view.widget;

import java.awt.Color;
import java.beans.PropertyChangeSupport;
import java.io.File;

import javax.swing.JComponent;

import org.openflexo.fge.BackgroundImageBackgroundStyle;
import org.openflexo.fge.BackgroundStyle;
import org.openflexo.fge.BackgroundStyle.BackgroundStyleType;
import org.openflexo.fge.ColorBackgroundStyle;
import org.openflexo.fge.ColorGradientBackgroundStyle;
import org.openflexo.fge.ColorGradientBackgroundStyle.ColorGradientDirection;
import org.openflexo.fge.FGECoreUtils;
import org.openflexo.fge.FGEModelFactory;
import org.openflexo.fge.TextureBackgroundStyle;
import org.openflexo.fge.TextureBackgroundStyle.TextureType;
import org.openflexo.fib.model.FIBCustom.FIBCustomComponent;
import org.openflexo.toolbox.FileResource;
import org.openflexo.toolbox.HasPropertyChangeSupport;

/**
 * Widget allowing to view and edit a BackgroundStyle
 * 
 * @author sguerin
 * 
 */
// TODO: suppress reference to Swing (when FIB library will be independant from SWING technology)
public interface FIBBackgroundStyleSelector<C extends JComponent> extends FIBCustomComponent<BackgroundStyle, C> {

	public static FileResource FIB_FILE = new FileResource("Fib/BackgroundStylePanel.fib");

	/**
	 * Convenient class use to manipulate BackgroundStyle
	 * 
	 * @author sylvain
	 * 
	 */
	public static class BackgroundStyleFactory implements HasPropertyChangeSupport {
		private static final String DELETED = "deleted";
		private BackgroundStyle backgroundStyle;
		private Color color1 = Color.RED;
		private Color color2 = Color.WHITE;
		private ColorGradientDirection gradientDirection = ColorGradientDirection.NORTH_SOUTH;
		private TextureType textureType = TextureType.TEXTURE1;
		private File imageFile;
		private PropertyChangeSupport pcSupport;
		private FGEModelFactory fgeFactory;

		public BackgroundStyleFactory(BackgroundStyle backgroundStyle) {
			pcSupport = new PropertyChangeSupport(this);
			this.backgroundStyle = backgroundStyle;
			if (backgroundStyle != null) {
				fgeFactory = backgroundStyle.getFactory();
			}
			if (fgeFactory == null) {
				// logger.warning("Could not find any FGE factory, creating a default one");
				fgeFactory = FGECoreUtils.TOOLS_FACTORY;
			}
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

		public BackgroundStyle getBackgroundStyle() {
			return backgroundStyle;
		}

		public void setBackgroundStyle(BackgroundStyle backgroundStyle) {
			BackgroundStyle oldBackgroundStyle = this.backgroundStyle;
			this.backgroundStyle = backgroundStyle;
			pcSupport.firePropertyChange("backgroundStyle", oldBackgroundStyle, backgroundStyle);
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
				setBackgroundStyle(fgeFactory.makeEmptyBackground());
				break;
			case COLOR:
				setBackgroundStyle(fgeFactory.makeColoredBackground(color1));
				break;
			case COLOR_GRADIENT:
				setBackgroundStyle(fgeFactory.makeColorGradientBackground(color1, color2, gradientDirection));
				break;
			case TEXTURE:
				setBackgroundStyle(fgeFactory.makeTexturedBackground(textureType, color1, color2));
				break;
			case IMAGE:
				setBackgroundStyle(fgeFactory.makeImageBackground(imageFile));
				break;
			default:
				break;
			}

			pcSupport.firePropertyChange("backgroundStyleType", oldBackgroundStyleType, getBackgroundStyleType());
		}

	}

}