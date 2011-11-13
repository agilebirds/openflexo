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
import java.util.Collections;
import java.util.Comparator;
import java.util.Properties;

import org.openflexo.foundation.ie.util.HyperlinkType;
import org.openflexo.foundation.ie.widget.IEButtonWidget;
import org.openflexo.foundation.rm.FlexoProject;
import org.openflexo.foundation.rm.FlexoWebServerFileResource;

public class FlexoIECustomImagePalette extends FlexoIEPalette<FlexoIECustomImagePalette.FlexoIECustomImage> {

	public FlexoIECustomImagePalette(FlexoProject project) {
		super(project);
	}

	@Override
	public boolean resizeScreenshots() {
		return true;
	}

	@Override
	protected void loadWidgets() {
		for (FlexoWebServerFileResource r : getProject().getSpecificImageResources()) {
			File f = r.getFile();
			if (!f.exists())
				continue;
			Properties props = new Properties();
			props.put(PaletteAttribute.XML.getAttributeTag(), "<IEButton imageName=\"" + getProject().imageNameForFile(f)
					+ "\" inspector=\"Button.inspector\"  hyperlink_type=\"" + HyperlinkType.IMAGE.getSerializationRepresentation()
					+ "\" isMandatoryFlexoAction=\"false\" />");
			props.put(PaletteAttribute.TARGET_CLASS_MODEL.getAttributeTag(), IEButtonWidget.class.getName());
			FlexoIECustomImage w = new FlexoIECustomImage(f.getName(), props, r);
			getWidgets().add(w);
		}
		Collections.sort(getWidgets(), new Comparator<FlexoIECustomImage>() {
			@Override
			public int compare(FlexoIECustomImage o1, FlexoIECustomImage o2) {
				return o1.getName().compareTo(o2.getName());
			}
		});
	}

	public class FlexoIECustomImage extends FlexoIEPalette<FlexoIECustomImage>.FlexoIEPaletteWidget {

		private FlexoWebServerFileResource resource;

		public FlexoIECustomImage(String name, Properties props, FlexoWebServerFileResource resource) {
			super(name, props);
			this.resource = resource;
			this.screenshotFile = resource.getFile();
		}

		@Override
		public void deleteWidget() {
			super.deleteWidget();
			resource.delete(true);
			getProject().resetAvailableImages();
		}

		@Override
		public boolean canDeleteWidget() {
			return true;
		}
	}
}
