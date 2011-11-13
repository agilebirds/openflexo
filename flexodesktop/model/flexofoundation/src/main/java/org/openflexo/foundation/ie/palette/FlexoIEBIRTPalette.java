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

import org.openflexo.foundation.ie.util.GraphType;
import org.openflexo.foundation.ie.widget.IEBIRTWidget;
import org.openflexo.foundation.rm.FlexoProject;
import org.openflexo.foundation.utils.FlexoCSS;

public class FlexoIEBIRTPalette extends FlexoIEPalette<FlexoIEBIRTPalette.FlexoIEBIRTWidget> {

	public FlexoIEBIRTPalette(FlexoProject project) {
		super(project);
	}

	@Override
	public boolean resizeScreenshots() {
		return true;
	}

	@Override
	protected void loadWidgets() {
		for (GraphType type : GraphType.values()) {
			Properties props = new Properties();
			props.put(PaletteAttribute.XML.getAttributeTag(), "<IEBIRTWidget type=\"" + type.name() + "\"/>");
			props.put(PaletteAttribute.TARGET_CLASS_MODEL.getAttributeTag(), IEBIRTWidget.class.getName());
			getWidgets().add(new FlexoIEBIRTWidget(type, props));
		}
	}

	public class FlexoIEBIRTWidget extends FlexoIEPalette<FlexoIEBIRTWidget>.FlexoIEPaletteWidget {

		private GraphType type;

		public FlexoIEBIRTWidget(GraphType type, Properties props) {
			super(type.name(), props);
			this.type = type;
		}

		@Override
		public File getScreenshotFile(FlexoCSS css) {
			return type.getFileResource();
		}

	}
}
