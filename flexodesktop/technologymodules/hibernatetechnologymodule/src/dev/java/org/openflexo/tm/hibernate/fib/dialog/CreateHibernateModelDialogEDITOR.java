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
package org.openflexo.tm.hibernate.fib.dialog;

import java.io.File;

import org.openflexo.fib.ProjectDialogEDITOR;
import org.openflexo.fib.editor.FIBAbstractEditor;
import org.openflexo.foundation.FlexoEditor;
import org.openflexo.foundation.FlexoProject;
import org.openflexo.foundation.sg.implmodel.ImplementationModel;
import org.openflexo.foundation.sg.implmodel.exception.TechnologyModuleCompatibilityCheckException;
import org.openflexo.tm.hibernate.gui.action.CreateHibernateModelAction;
import org.openflexo.tm.hibernate.gui.action.CreateHibernateModelActionInitializer;
import org.openflexo.tm.hibernate.impl.HibernateImplementation;
import org.openflexo.toolbox.FileResource;


public class CreateHibernateModelDialogEDITOR extends ProjectDialogEDITOR {

	
	public static void main(String[] args)
	{
		FIBAbstractEditor editor = new FIBAbstractEditor() {
			public Object[] getData() 
			{
				FlexoEditor editor = loadProject(new FileResource("Prj/TestSG.prj"));
				FlexoProject project = editor.getProject();

				try {
					ImplementationModel im = project.getGeneratedSources().getImplementationModels().firstElement().getImplementationModel();
					HibernateImplementation hibernateImplementation = new HibernateImplementation(im);
					CreateHibernateModelAction action = CreateHibernateModelAction.actionType.makeNewAction(hibernateImplementation, null, editor);
					return makeArray(action);
				} catch (TechnologyModuleCompatibilityCheckException e) {
					throw new RuntimeException(e); 
				}
			}

			public File getFIBFile() {
				return new FileResource("src/main/resources" + CreateHibernateModelActionInitializer.HIBERNATE_CREATEMODEL_DIALOG_FIB_RESOURCE_PATH);
			}
		};
		editor.launch();
	}
	

}
