package org.openflexo.fib.editor.test;
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



import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.Observable;
import java.util.Vector;

import org.openflexo.fib.editor.FIBAbstractEditor;
import org.openflexo.toolbox.FileResource;


public class TestClassSelector {

	public static FileResource FIB_FILE = new FileResource("TestFIB/TestClassSelector.fib");

	public static void main(String[] args)
	{
		FIBAbstractEditor editor = new FIBAbstractEditor() {
			public Object[] getData()
			{
				return FIBAbstractEditor.makeArray(LoadedClassesInfo.instance);
			}
			public File getFIBFile() {
				return FIB_FILE;
			}
		};
		/*editor.addAction("change_name",new ActionListener() {		
			@Override
			public void actionPerformed(ActionEvent e) {
				class1.setName("new_class_1_name");
			}
		});*/
		editor.launch();
	}
	
}
