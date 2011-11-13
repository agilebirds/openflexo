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
package org.openflexo.swing;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

import org.openflexo.toolbox.FileUtils;

public class ImageUtils {

	public enum ImageType {
		JPG, PNG, GIF;

		public String getExtension() {
			return name().toLowerCase();
		}
	}

	public static BufferedImage createImageFromComponent(Component componentToPrint) {
		BufferedImage bi = new BufferedImage(componentToPrint.getWidth(), componentToPrint.getHeight(), BufferedImage.TYPE_INT_ARGB);
		Graphics2D graphics = bi.createGraphics();
		componentToPrint.print(graphics);
		return bi;
	}

	public static void saveImageToFile(RenderedImage image, File dest, ImageType type) throws IOException {
		if (type == null)
			type = ImageType.PNG;
		if (!dest.exists())
			FileUtils.createNewFile(dest);
		ImageIO.write(image, type.getExtension(), dest);
	}

	public static BufferedImage loadImageFromFile(File source) {
		try {
			return ImageIO.read(source);
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	public static ImageIcon getThumbnail(ImageIcon src, int maxWidth) {
		if (src != null) {
			if (src.getIconWidth() > maxWidth) {
				return new ImageIcon(src.getImage().getScaledInstance(maxWidth, -1, Image.SCALE_SMOOTH));
			} else { // no need to miniaturize
				return src;
			}
		}
		return null;
	}

	public static ImageIcon resize(ImageIcon src, Dimension size) {
		if (src != null)
			return new ImageIcon(src.getImage().getScaledInstance(size.width, size.height, Image.SCALE_SMOOTH));
		return null;
	}
}
