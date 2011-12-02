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
package org.openflexo.icon;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.util.Hashtable;

import javax.swing.ImageIcon;

public class IconFactory {

	// icons with marker

	private static ImageIcon mergeImageIcon(ImageIcon background, IconMarker marker) {
		ImageIcon foreground = marker.getImage();
		int posX = marker.getPX();
		int posY = marker.getPY();
		int newWidth = Math.max(background.getIconWidth(), posX + foreground.getIconWidth());
		int newHeight = Math.max(background.getIconHeight(), posY + foreground.getIconHeight());
		BufferedImage result = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = result.createGraphics();
		Image img1 = background.getImage();
		Image img2 = foreground.getImage();
		/*	    g.setColor(Color.WHITE);
			    g.fillRect(0,0,newWidth,newHeight);*/
		g.drawImage(img1, 0, 0, null, null);
		g.drawImage(img2, posX, posY, null, null);
		g.dispose();
		return new ImageIcon(result);
	}

	private static final Hashtable<ImageIcon, Hashtable<Integer, ImageIcon>> _storedIcons = new Hashtable<ImageIcon, Hashtable<Integer, ImageIcon>>();

	public static ImageIcon getImageIcon(ImageIcon icon, IconMarker... iconMarkers) {
		Hashtable<Integer, ImageIcon> knownIcons = _storedIcons.get(icon);
		if (knownIcons == null) {
			knownIcons = new Hashtable<Integer, ImageIcon>();
			_storedIcons.put(icon, knownIcons);
		}
		int code = 0;
		for (int i = 0; i < iconMarkers.length; i++) {
			code += iconMarkers[i].getID();
		}
		ImageIcon returned = knownIcons.get(code);
		if (returned == null) {
			// logger.info("Compute icon "+code);
			ImageIcon resultImage = icon;
			for (int i = 0; i < iconMarkers.length; i++) {
				resultImage = mergeImageIcon(resultImage, iconMarkers[i]);
			}
			knownIcons.put(code, resultImage);
			returned = resultImage;
		} else {
			// logger.info("Retrieve icon "+code);
		}
		return returned;
	}

	// disabled icons

	private static Hashtable<ImageIcon, ImageIcon> _disabledIcons;

	public static ImageIcon getDisabledIcon(ImageIcon imageIcon) {
		if (_disabledIcons == null) {
			_disabledIcons = new Hashtable<ImageIcon, ImageIcon>();
		}
		ImageIcon returned = _disabledIcons.get(imageIcon);
		if (returned == null) {
			returned = buildDisabledIcon(imageIcon);
			_disabledIcons.put(imageIcon, returned);
		}
		return returned;
	}

	private static ImageIcon buildDisabledIcon(ImageIcon imageIcon) {
		BufferedImage result = new BufferedImage(imageIcon.getIconWidth(), imageIcon.getIconHeight(), BufferedImage.TYPE_INT_RGB);
		Graphics2D g = result.createGraphics();
		g.setColor(Color.WHITE);
		g.fillRect(0, 0, imageIcon.getIconWidth(), imageIcon.getIconHeight());
		g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.3f));
		g.setPaint(Color.WHITE);
		g.drawImage(imageIcon.getImage(), 0, 0, Color.WHITE, null);
		g.dispose();
		return new ImageIcon(result);
	}
}
