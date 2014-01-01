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
package org.openflexo.components.widget;

import java.io.File;
import java.util.logging.Logger;

import org.openflexo.foundation.FlexoProject;
import org.openflexo.module.ProjectLoader;
import org.openflexo.toolbox.FileResource;

/**
 * Widget allowing to select a ViewPoint
 * 
 * @author sguerin
 * 
 */
public class FIBProjectSelector extends FIBFlexoObjectSelector<FlexoProject> {
	static final Logger logger = Logger.getLogger(FIBProjectSelector.class.getPackage().getName());

	public static FileResource FIB_FILE = new FileResource("Fib/ProjectSelector.fib");

	private ProjectLoader projectLoader;

	public FIBProjectSelector(FlexoProject editedObject) {
		super(editedObject);
		getTextField().setColumns(25);
	}

	@Override
	public File getFIBFile() {
		return FIB_FILE;
	}

	@Override
	public Class<FlexoProject> getRepresentedType() {
		return FlexoProject.class;
	}

	@Override
	public String renderedString(FlexoProject editedObject) {
		if (editedObject != null) {
			return editedObject.getDisplayName();
		}
		return "";
	}

	public ProjectLoader getProjectLoader() {
		return projectLoader;
	}

	public void setProjectLoader(ProjectLoader projectLoader) {
		ProjectLoader old = this.projectLoader;
		this.projectLoader = projectLoader;
		getPropertyChangeSupport().firePropertyChange("projectLoader", old, projectLoader);
	}

	// Please uncomment this for a live test
	// Never commit this uncommented since it will not compile on continuous build
	// To have icon, you need to choose "Test interface" in the editor (otherwise, flexo controller is not instantiated in EDIT mode)
	/*
	public static void main(String[] args) {
		FIBAbstractEditor editor = new FIBAbstractEditor() {
			@Override
			public Object[] getData() {
				FlexoProject project = Mockito.mock(FlexoProject.class);
				Mockito.when(project.getProjectName()).thenReturn("Root project");
				FlexoProject subProject1 = Mockito.mock(FlexoProject.class);
				Mockito.when(subProject1.getProjectName()).thenReturn("Sub project 1");
				FlexoProject subProject2 = Mockito.mock(FlexoProject.class);
				Mockito.when(subProject2.getProjectName()).thenReturn("Sub project 2");
				ProjectData projectData = Mockito.mock(ProjectData.class);
				Mockito.when(project.getProjectData()).thenReturn(projectData);
				ModelFactory factory;
				try {
					factory = new ModelFactory(FlexoProjectReference.class);
					FlexoProjectReference ref1 = factory.newInstance(FlexoProjectReference.class);
					ref1.init(subProject1);
					FlexoProjectReference ref2 = factory.newInstance(FlexoProjectReference.class);
					ref1.init(subProject2);
					Mockito.when(projectData.getImportedProjects()).thenReturn(Arrays.asList(ref1, ref2));
				} catch (ModelDefinitionException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				ProjectLoader projectLoader = Mockito.mock(ProjectLoader.class);
				Mockito.when(projectLoader.getRootProjects()).thenReturn(Arrays.asList(project));
				mockPropertyChangeSupport(projectLoader);
				mockPropertyChangeSupport(project);
				mockPropertyChangeSupport(subProject1);
				mockPropertyChangeSupport(subProject2);
				FIBProjectSelector selector = new FIBProjectSelector(null);
				selector.setProjectLoader(projectLoader);
				return makeArray(selector);
			}

			private void mockPropertyChangeSupport(HasPropertyChangeSupport changeSupport) {
				Mockito.when(changeSupport.getPropertyChangeSupport()).thenReturn(new PropertyChangeSupport(changeSupport));
			}

			@Override
			public File getFIBFile() {
				return FIB_FILE;
			}

			@Override
			public FIBController makeNewController(FIBComponent component) {
				return new FlexoFIBController(component);
			}
		};
		editor.launch();
	}
	*/
}