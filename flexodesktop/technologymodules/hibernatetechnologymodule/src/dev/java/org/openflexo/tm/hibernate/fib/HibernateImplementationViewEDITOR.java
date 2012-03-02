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
package org.openflexo.tm.hibernate.fib;

import java.io.File;

import org.openflexo.fib.ProjectDialogEDITOR;
import org.openflexo.fib.editor.FIBAbstractEditor;
import org.openflexo.foundation.FlexoEditor;
import org.openflexo.foundation.rm.FlexoProject;
import org.openflexo.foundation.sg.implmodel.ImplementationModel;
import org.openflexo.foundation.sg.implmodel.exception.TechnologyModuleCompatibilityCheckException;
import org.openflexo.tm.persistence.gui.view.PersistenceImplementationView;
import org.openflexo.tm.hibernate.impl.HibernateImplementation;
import org.openflexo.toolbox.FileResource;


public class HibernateImplementationViewEDITOR extends ProjectDialogEDITOR {

	public static void main(String[] args) {
		FIBAbstractEditor editor = new FIBAbstractEditor() {
			@Override
			public Object[] getData() {
				FlexoEditor editor = loadProject(new FileResource("Prj/TestSG.prj"));
				FlexoProject project = editor.getProject();
				ImplementationModel im = project.getGeneratedSources().getImplementationModels().firstElement().getImplementationModel();

				HibernateImplementation hibernateImplementation = (HibernateImplementation) im.getTechnologyModule("Hibernate");
				if (hibernateImplementation == null) {
					try {
						hibernateImplementation = new HibernateImplementation(im);
					} catch (TechnologyModuleCompatibilityCheckException e) {
						throw new RuntimeException(e);
					}
				}

				return makeArray(hibernateImplementation);
			}

			@Override
			public File getFIBFile() {
				return new FileResource("src/main/resources" + PersistenceImplementationView.HIBERNATE_IMPLEMENTATION_VIEW_FIB_RESOURCE_PATH);
			}
		};
		editor.launch();
	}
}
