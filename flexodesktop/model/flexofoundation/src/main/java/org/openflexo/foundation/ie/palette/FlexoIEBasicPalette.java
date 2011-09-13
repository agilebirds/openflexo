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
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;
import java.util.Properties;

import org.openflexo.foundation.rm.FlexoProject;
import org.openflexo.foundation.utils.FlexoCSS;
import org.openflexo.toolbox.FileResource;


public class FlexoIEBasicPalette extends FlexoIEPalette<FlexoIEBasicPalette.FlexoIEBasicWidget> {

	public static final FileResource PALETTE_DIRECTORY =new FileResource("Config/IEPalette/Basic");
	
	public FlexoIEBasicPalette(FlexoProject project) {
		super(project);
	}
	
	@Override
	public boolean resizeScreenshots() {
		return false;
	}
	
	@Override
	protected void loadWidgets() {
		File[] files = PALETTE_DIRECTORY.listFiles(new FilenameFilter() {
			@Override
			public boolean accept(File dir, String name) {
				return name.endsWith(".properties");
			}
		});
		for (int i = 0; i < files.length; i++) {
			File file = files[i];
			Properties props = new Properties();
			try {
				props.load(new FileInputStream(file));
				getWidgets().add(new FlexoIEBasicWidget(file.getName().substring(0,file.getName().length()-".properties".length()),props));
			} catch (FileNotFoundException e) {
				e.printStackTrace();
				continue;
			} catch (IOException e) {
				e.printStackTrace();
				continue;
			} catch (RuntimeException re) {
				re.printStackTrace();
				continue;
			}
		}
		Collections.sort(getWidgets(), new Comparator<FlexoIEBasicWidget>() {
			@Override
			public int compare(FlexoIEBasicWidget o1, FlexoIEBasicWidget o2) {
				return o1.getIndex()-o2.getIndex();
			}
		});
	}
	
	public class FlexoIEBasicWidget extends FlexoIEPalette<FlexoIEBasicWidget>.FlexoIEPaletteWidget {

		private int index;
		
		public FlexoIEBasicWidget(String name, Properties props) {
			super(name, props);
			index = Integer.parseInt(props.getProperty("index"));
		}
		
		public int getIndex() {
			return index;
		}

		@Override
		public File getScreenshotFile(FlexoCSS css) {
			return new File(PALETTE_DIRECTORY,css.getName()+"/"+getName()+".gif");
		}
		
	}
}
