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
package org.openflexo.technologyadapter.excel.gui;

import java.util.logging.Logger;

import javax.swing.ImageIcon;

import org.openflexo.foundation.technologyadapter.TechnologyObject;
import org.openflexo.technologyadapter.excel.model.ExcelCell;
import org.openflexo.technologyadapter.excel.model.ExcelRow;
import org.openflexo.technologyadapter.excel.model.ExcelSheet;
import org.openflexo.technologyadapter.excel.model.ExcelWorkbook;
import org.openflexo.toolbox.ImageIconResource;

public class ExcelIconLibrary {

	private static final Logger logger = Logger.getLogger(ExcelIconLibrary.class.getPackage().getName());

	public static final ImageIconResource EXCEL_TECHNOLOGY_BIG_ICON = new ImageIconResource("Icons/ExcelBig.png");
	public static final ImageIconResource EXCEL_TECHNOLOGY_ICON = new ImageIconResource("Icons/ExcelSmall.png");
	public static final ImageIconResource EXCEL_GRAPHICAL_ACTION_ICON = new ImageIconResource("Icons/GraphicalActionIcon.png");
	
	public static ImageIcon iconForObject(Class<? extends TechnologyObject> objectClass) {
		if (ExcelWorkbook.class.isAssignableFrom(objectClass)) {
			return EXCEL_TECHNOLOGY_ICON;
		} else if (ExcelCell.class.isAssignableFrom(objectClass)) {
			return EXCEL_TECHNOLOGY_ICON;
		} else if (ExcelSheet.class.isAssignableFrom(objectClass)) {
			return EXCEL_TECHNOLOGY_ICON;
		} else if (ExcelRow.class.isAssignableFrom(objectClass)) {
			return EXCEL_TECHNOLOGY_ICON;
		} 
		logger.warning("No icon for " + objectClass);
		return null;
	}

}
