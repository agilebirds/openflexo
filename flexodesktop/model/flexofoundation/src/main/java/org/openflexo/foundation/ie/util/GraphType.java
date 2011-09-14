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
package org.openflexo.foundation.ie.util;

import org.openflexo.toolbox.FileResource;
import org.openflexo.toolbox.ImageIconResource;

public enum GraphType {
	
	SideBySideBar {
		@Override
		public String getImageName() {
			return "SideBySideBar.png";
		}
	},
	StackedBar {
		@Override
		public String getImageName() {
			return "StackedBar.png";
		}
	},
	PercentStackedBar {
		@Override
		public String getImageName() {
			return "PercentStackedBar.png";
		}
	},
	OverlayLine {
		@Override
		public String getImageName() {
			return "OverlayLine.png";
		}
	},
	StackedLine {
		@Override
		public String getImageName() {
			return "StackedLine.png";
		}
	},
	PercentStackedLine {
		@Override
		public String getImageName() {
			return "PercentStackedLine.png";
		}
	},
	OverlayArea {
		@Override
		public String getImageName() {
			return "OverlayArea.png";
		}
	},
	StackedArea {
		@Override
		public String getImageName() {
			return "StackedArea.png";
		}
	},
	PercentStackedArea {
		@Override
		public String getImageName() {
			return "PercentStackedArea.png";
		}
	},
	Pie {
		@Override
		public String getImageName() {
			return "Pie.png";
		}
	},
	StandardMeter {
		@Override
		public String getImageName() {
			return "StandardMeter.png";
		}
	},
	SuperImposedMeter {
		@Override
		public String getImageName() {
			return "SuperImposedMeter.png";
		}
	},
	Scatter {
		@Override
		public String getImageName() {
			return "Scatter.png";
		}
	},
	BarStickStock {
		@Override
		public String getImageName() {
			return "BarStickStock.png";
		}
	},
	CandleStickStock {
		@Override
		public String getImageName() {
			return "CandleStickStock.png";
		}
	},
	BarAndLineCombination {
		@Override
		public String getImageName() {
			return "BarAndLineCombination.png";
		}
	},
	LineChartMultipleYAxis {
		@Override
		public String getImageName() {
			return "LineChartMultipleYAxis.png";
		}
	},
	BarChartMultipleYSeries {
		@Override
		public String getImageName() {
			return "BarChartMultipleYSeries.png";
		}
	};
	private static final String PALETTE_DIRECTORY = "Config/IEPalette/BIRT/";

	private ImageIconResource icon;
	private FileResource fileResource;
	
	public abstract String getImageName();
	
	public ImageIconResource getIcon() {
		if (icon==null)
			icon = new ImageIconResource(PALETTE_DIRECTORY+getImageName());
		return icon;
	}
	
	public FileResource getFileResource() {
		if (fileResource==null)
			fileResource = new FileResource(PALETTE_DIRECTORY+getImageName());
		return fileResource;
	}
	
}