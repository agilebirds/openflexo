package org.openflexo.fib;
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
import org.openflexo.foundation.DefaultFlexoEditor;
import org.openflexo.foundation.FlexoEditor;
import org.openflexo.foundation.FlexoEditor.FlexoEditorFactory;
import org.openflexo.foundation.rm.FlexoProject;
import org.openflexo.foundation.rm.FlexoResourceManager;
import org.openflexo.foundation.utils.ProjectInitializerException;
import org.openflexo.foundation.utils.ProjectLoadingCancelledException;
import org.openflexo.toolbox.FileResource;


public class TestProjectBrowser {

	public static FileResource PRJ_FILE = new FileResource("Prj/TestBrowser.prj");
	public static FileResource FIB_FILE = new FileResource("Fib/ProjectBrowser.fib");

	protected static final FlexoEditorFactory EDITOR_FACTORY = new FlexoEditorFactory() {
		public DefaultFlexoEditor makeFlexoEditor(FlexoProject project) {
			return new DefaultFlexoEditor(project);
		}
	};

	public static void main(String[] args)
	{
		FIBAbstractEditor editor = new FIBAbstractEditor() {
			public Object[] getData()
			{
				return FIBAbstractEditor.makeArray(loadProject());
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
	
	public static FlexoProject loadProject()
	{
		FlexoProject project = null;
		FlexoEditor editor;
		File projectFile = PRJ_FILE;
		//File projectFile = new File("C:\\Documents and Settings\\gpolet.DENALI\\Desktop\\FlexoProjects\\test_forFlexoServer_1.1\\test.prj");
		//File projectFile = new File("/Users/sylvain/Documents/TestsFlexo/TestBindingSelector.prj");
		//File projectFile = new File("/Users/sylvain/Documents/TestsFlexo/BA/TestActivityGroup.prj");
		//File projectFile = new File("/Users/sylvain/Documents/TestsFlexo/BA/TestOperatorConversion/TestOperator.prj");
		//File projectFile = new File("/Users/sylvain/Documents/TestsFlexo/BA/TestSPNodesConversion/TestSPNodesConversion.prj");
		//File projectFile = new File("/Users/sylvain/Documents/TestsFlexo/BA/TestInducedEdges.prj");
		//FileResource projectFile = new FileResource("src/dev/resources/TestFGE.prj");
		System.out.println("Found project "+projectFile.getAbsolutePath());
		try {
			editor = FlexoResourceManager.initializeExistingProject(projectFile,EDITOR_FACTORY,null);
			project = editor.getProject();
		} catch (ProjectLoadingCancelledException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ProjectInitializerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("Successfully loaded project "+projectFile.getAbsolutePath());

		return project;
	}

}
