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
package org.openflexo.foundation.ie.palette;

import java.io.File;
import java.util.Properties;

import org.openflexo.foundation.ie.widget.IEButtonWidget;
import org.openflexo.foundation.rm.FlexoProject;
import org.openflexo.foundation.utils.FlexoCSS;
import org.openflexo.toolbox.FileResource;

public class FlexoIEImagePalette extends FlexoIEPalette<FlexoIEImagePalette.FlexoIEImage> {

	protected static final File IMAGES_DIRECTORY = new FileResource("Config/Images");

	public FlexoIEImagePalette(FlexoProject project) {
		super(project);
	}

	@Override
	public boolean resizeScreenshots() {
		return false;
	}

	@Override
	protected void loadWidgets() {
		File[] files = IMAGES_DIRECTORY.listFiles();
		for (int i = 0; i < files.length; i++) {
			File file = files[i];
			if (file.isFile()
					&& (file.getName().toLowerCase().endsWith(".gif") || file.getName().toLowerCase().endsWith(".png") || file.getName()
							.toLowerCase().endsWith(".jpg"))) {
				String fileName = getProject().imageNameForFile(file);
				Properties props = new Properties();
				props.put(PaletteAttribute.XML.getAttributeTag(), "<IEButton imageName=\"" + fileName
						+ "\" inspector=\"Button.inspector\" />");
				props.put(PaletteAttribute.TARGET_CLASS_MODEL.getAttributeTag(), IEButtonWidget.class.getName());
				getWidgets().add(new FlexoIEImage(fileName.startsWith("_") ? fileName.substring(1) : fileName, props, file));
			}
		}
		files = new File(IMAGES_DIRECTORY, FlexoCSS.CONTENTO.getName()).listFiles();
		for (int i = 0; i < files.length; i++) {
			File file = files[i];
			if (file.isFile()
					&& (file.getName().toLowerCase().endsWith(".gif") || file.getName().toLowerCase().endsWith(".png") || file.getName()
							.toLowerCase().endsWith(".jpg"))) {
				String fileName = getProject().imageNameForFile(file);
				Properties props = new Properties();
				props.put(PaletteAttribute.XML.getAttributeTag(), "<IEButton imageName=\"" + fileName
						+ "\" inspector=\"Button.inspector\" />");
				props.put(PaletteAttribute.TARGET_CLASS_MODEL.getAttributeTag(), IEButtonWidget.class.getName());
				getWidgets().add(new FlexoIEImage(fileName.startsWith("_") ? fileName.substring(1) : fileName, props, fileName));
			}
		}
	}

	public class FlexoIEImage extends FlexoIEPalette<FlexoIEImage>.FlexoIEPaletteWidget {

		private String screenshotName;

		public FlexoIEImage(String name, Properties props, File screenshot) {
			super(name, props);
			this.screenshotFile = screenshot;
		}

		public FlexoIEImage(String name, Properties props, String screenshotName) {
			super(name, props);
			this.screenshotName = screenshotName;
		}

		@Override
		public File getScreenshotFile(FlexoCSS css) {
			if (css == null /*|| css == FlexoCSS.BLUEWAVE*/)
				css = FlexoCSS.CONTENTO;
			if (screenshotName == null)
				return super.getScreenshotFile(css);
			else
				return new File(IMAGES_DIRECTORY, css.getName() + "/" + css.getName() + screenshotName);
		}
	}

}
