/** Copyright (c) 2012, THALES SYSTEMES AEROPORTES - All Rights Reserved
 * Author : Gilles Besançon
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
 * Contributors :
 *
 */
package org.openflexo.technologyadapter.emf.model;

import java.lang.reflect.Field;

import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.junit.Test;
import org.openflexo.foundation.ontology.util.FlexoOntologyUtility;
import org.openflexo.technologyadapter.emf.metamodel.EMFMetaModel;
import org.openflexo.technologyadapter.emf.metamodel.io.EMFMetaModelPackageReaderWriter;
import org.openflexo.technologyadapter.emf.model.EMFModel;
import org.openflexo.technologyadapter.emf.model.io.EMFModelResourceReaderWriter;

/**
 * Test EMF Ecore.
 * 
 * @author gbesancon
 */
public class TestEMFEcore {

	@Test
	public void testReaderWriter() {
		String jarFilepath = "D:\\gbe\\tools\\eclipse-4.2.1\\plugins";

		String inputFilepath = "D:\\gbe\\workspaces\\workspace-openflexo\\ecore_project\\m\\test.uml";
		String resourceFactoryClassname = "org.eclipse.uml2.uml.resource.UMLResource$Factory";
		String ePackageClassname = "org.eclipse.uml2.uml.UMLPackage";

		EMFMetaModel mm = null;
		EMFModel m = null;

		try {
			// Load ClassLoader
			ClassLoader classLoader = ClassLoaderUtility
					.getClassLoader(JarUtility.getJars(jarFilepath), ClassLoader.getSystemClassLoader());

			// Load MetaModel.
			Class<?> ePackageClass = Class.forName(ePackageClassname, true, classLoader);
			Field ePackageField = ePackageClass.getField("eINSTANCE");
			EPackage ePackage = (EPackage) ePackageField.get(null);

			EMFMetaModelPackageReaderWriter mmrw = new EMFMetaModelPackageReaderWriter();
			mm = mmrw.load(ePackage);
			System.out.println(FlexoOntologyUtility.toString(mm));

			// Load Model.
			if (mm != null) {
				Class<?> resourceFactoryClass = Class.forName(resourceFactoryClassname, true, classLoader);
				Field resourceFactoryField = resourceFactoryClass.getField("INSTANCE");
				Resource.Factory resourceFactory = (Resource.Factory) resourceFactoryField.get(null);
				Resource inputResource = EMFIOUtility.load(inputFilepath, ePackage, resourceFactory);

				EMFModelResourceReaderWriter mrw = new EMFModelResourceReaderWriter(mm);
				if (inputResource != null) {
					m = mrw.load(inputResource);
					System.out.println(FlexoOntologyUtility.toString(m));
				}

				/* Modify model by action. */
				modifyModel(m);

				System.out.println(FlexoOntologyUtility.toString(m));
				mrw.save(m, inputResource);

				modifyModel2(m);

				System.out.println(FlexoOntologyUtility.toString(m));
				mrw.save(m, inputResource);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
