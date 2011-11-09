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
package org.openflexo.fib;

import java.io.File;
import java.util.Locale;

import org.openflexo.fib.editor.FIBAbstractEditor;
import org.openflexo.view.controller.WebServiceURLDialog;


public class PPMWSParameterEDITOR {


	public static void main(String[] args)
	{
		System.err.println(Locale.getDefault().getDisplayName());
		FIBAbstractEditor editor = new FIBAbstractEditor() {
			@Override
			public Object[] getData()
			{
				WebServiceURLDialog o = new WebServiceURLDialog();
				System.out.println("WebServiceURLDialog="+o);
				System.out.println("AddressBook="+o.getAddressBook().getInstances());
				return FIBAbstractEditor.makeArray(o);
			}
			@Override
			public File getFIBFile() {
				return WebServiceURLDialog.FIB_FILE;
			}
		};
		editor.launch();
	}
}
