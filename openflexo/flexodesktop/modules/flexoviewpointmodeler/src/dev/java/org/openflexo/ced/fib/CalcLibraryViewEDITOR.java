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
package org.openflexo.ced.fib;

import java.io.File;

import org.openflexo.ced.CEDCst;
import org.openflexo.fib.editor.FIBAbstractEditor;
import org.openflexo.foundation.FlexoResourceCenter;
import org.openflexo.foundation.ontology.calc.CalcLibrary;
import org.openflexo.module.ModuleLoader;


public class CalcLibraryViewEDITOR {

	public static void main(String[] args)
	{
		FIBAbstractEditor editor = new FIBAbstractEditor() {
			@Override
			public Object[] getData() {
				FlexoResourceCenter resourceCenter = ModuleLoader.getFlexoResourceCenter(true);
				CalcLibrary calcLibrary = resourceCenter.retrieveCalcLibrary();
				return makeArray(calcLibrary);
			}
			@Override
			public File getFIBFile() {
				return CEDCst.CALC_LIBRARY_VIEW_FIB;
			}
		};
		editor.launch();
	}
}
