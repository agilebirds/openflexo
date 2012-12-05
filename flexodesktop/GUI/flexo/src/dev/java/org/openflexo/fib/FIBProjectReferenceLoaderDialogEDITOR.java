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
import java.util.ArrayList;
import java.util.List;

import org.openflexo.fib.editor.FIBAbstractEditor;
import org.openflexo.foundation.rm.FlexoProjectReference;
import org.openflexo.model.ModelContext;
import org.openflexo.model.annotations.ModelEntity;
import org.openflexo.model.annotations.Setter;
import org.openflexo.model.exceptions.ModelDefinitionException;
import org.openflexo.model.factory.ModelFactory;
import org.openflexo.module.InteractiveFlexoProjectReferenceLoader;
import org.openflexo.module.InteractiveFlexoProjectReferenceLoader.ProjectReferenceFileAssociation;
import org.openflexo.module.InteractiveFlexoProjectReferenceLoader.ProjectReferenceLoaderData;

public class FIBProjectReferenceLoaderDialogEDITOR extends FIBAbstractEditor {

	@ModelEntity
	public static interface FlexoProjectReferenceImpl extends FlexoProjectReference {

		@Setter(NAME)
		public void setName(String name);

		@Setter(URI)
		public void setURI(String uri);

		@Setter(VERSION)
		public void setVersion(String version);

		@Setter(REVISION)
		public void setRevision(Long revision);

	}

	@Override
	public Object[] getData() {
		List<ProjectReferenceFileAssociation> associations = new ArrayList<InteractiveFlexoProjectReferenceLoader.ProjectReferenceFileAssociation>();
		try {
			ModelContext context = new ModelContext(FlexoProjectReferenceImpl.class, ProjectReferenceFileAssociation.class);
			ModelFactory factory = new ModelFactory(context);
			for (int i = 0; i < 5; i++) {
				FlexoProjectReferenceImpl ref = factory.newInstance(FlexoProjectReferenceImpl.class);
				ref.init(null);
				ref.setURI("http://coucou+" + (i + 1));
				ref.setName("Prout " + (i + 1));
				ref.setVersion("Une version " + (i + 1) + " à la C..");
				ref.setRevision(132456L);
				ProjectReferenceFileAssociation newInstance = factory.newInstance(ProjectReferenceFileAssociation.class);
				newInstance.init(ref, null);
				associations.add(newInstance);
			}
		} catch (ModelDefinitionException e) {
			e.printStackTrace();
		}
		InteractiveFlexoProjectReferenceLoader.ProjectReferenceLoaderData data = new ProjectReferenceLoaderData(null, associations);
		return makeArray(data);
	}

	@Override
	public File getFIBFile() {
		return InteractiveFlexoProjectReferenceLoader.FIB_FILE;
	}

	public static void main(String[] args) {
		main(FIBProjectReferenceLoaderDialogEDITOR.class);
	}
}
