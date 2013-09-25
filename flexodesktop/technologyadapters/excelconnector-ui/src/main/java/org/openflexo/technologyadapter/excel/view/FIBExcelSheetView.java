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
package org.openflexo.technologyadapter.excel.view;

import java.util.logging.Logger;

import org.openflexo.technologyadapter.excel.model.ExcelSheet;
import org.openflexo.toolbox.FileResource;
import org.openflexo.view.SelectionSynchronizedFIBView;
import org.openflexo.view.controller.FlexoController;

/**
 * Widget allowing to edit/view a ExcelSheet.<br>
 * 
 * @author sguerin
 * 
 */
@SuppressWarnings("serial")
public class FIBExcelSheetView extends SelectionSynchronizedFIBView {
	static final Logger logger = Logger.getLogger(FIBExcelSheetView.class.getPackage().getName());

	public static final FileResource FIB_FILE = new FileResource("Fib/ExcelSheetPanel.fib");

	public FIBExcelSheetView(ExcelSheet sheet, FlexoController controller) {
		super(sheet, controller, FIB_FILE);
	}

}
