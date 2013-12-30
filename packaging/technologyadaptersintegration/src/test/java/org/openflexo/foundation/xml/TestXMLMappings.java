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
package org.openflexo.foundation.xml;

import java.util.logging.Logger;

import junit.framework.TestCase;

@Deprecated
public class TestXMLMappings extends TestCase {

	protected static final Logger logger = Logger.getLogger(TestXMLMappings.class.getPackage().getName());

	/*	private XMLSerializationService xmlMappings;

		public TestXMLMappings() {
			super("TestXMLMappings");
		}

		@Override
		protected void setUp() throws Exception {
			super.setUp();
			xmlMappings = XMLSerializationService.createInstance();
			xmlMappings.initialize();
		}

		public void testRMMappings() {
			checkClassModels(FlexoProject.class);
		}

		public void testWorkflowMappings() {
			checkClassModels(FlexoWorkflow.class);
		}

		public void testProcessMappings() {
			checkClassModels(FlexoProcess.class);
		}

		public void testComponentMappings() {
			checkClassModels(IEWOComponent.class);
		}

		public void testNavigationMenuMappings() {
			checkClassModels(FlexoNavigationMenu.class);
		}

		public void testComponentLibraryMappings() {
			checkClassModels(FlexoComponentLibrary.class);
		}

		public void testDataModelMappings() {
			checkClassModels(DMModel.class);
		}

		public void testDKVMappings() {
			checkClassModels(DKVModel.class);
		}

		public void testWSLibraryMappings() {
			checkClassModels(FlexoWSLibrary.class);
		}

		public void testGeneratedCodeMappings() {
			checkClassModels(GeneratedCode.class);
		}

		public void testGeneratedDocMappings() {
			checkClassModels(GeneratedDoc.class);
		}

		public void testTOCDataMappings() {
			checkClassModels(TOCData.class);
		}

		public void testViewPointMappings() {
			checkClassModels(ViewPoint.class);
		}

		public void testVirtualModelMappings() {
			checkClassModels(VirtualModel.class);
		}

		public void testDiagramSpecificationModelMappings() {
			checkClassModels(DiagramSpecification.class);
		}

		public void testExampleDiagramModelMappings() {
			checkClassModels(ExampleDiagram.class);
		}

		public void testDiagramPaletteModelMappings() {
			checkClassModels(DiagramPalette.class);
		}

		public void testViewMappings() {
			checkClassModels(View.class);
		}

		public void testVirtualModelInstanceMappings() {
			checkClassModels(VirtualModelInstance.class);
		}

		public void testDiagramMappings() {
			checkClassModels(Diagram.class);
		}

		private void checkClassModels(Class aClass) {
			boolean testFails = false;
			ClassModels classModels = xmlMappings.getModelsForClass(aClass);
			logger.info("-----------> Check class models for " + aClass.getName());
			for (FlexoVersion version : classModels.getAvailableVersions()) {
				try {
					XMLMapping mapping = xmlMappings.getMappingForClassAndVersion(aClass, version);
					if (mapping != null) {
						logger.info("Successfully decoded mapping for class " + aClass.getSimpleName() + ", version " + version);
					} else {
						logger.warning("Failed decoded mapping for class " + aClass.getSimpleName() + ", version " + version);
						testFails = true;
					}
					checkXMLMapping(mapping);
				} catch (Exception e) {
					e.printStackTrace();
					logger.warning("Failed decoded mapping for class " + aClass.getSimpleName() + ", version " + version + " " + e.getMessage());
					testFails = true;
				}
			}
			if (testFails) {
				fail();
			}
		}

		protected void checkXMLMapping(XMLMapping mapping) throws Exception {
			Iterator<ModelEntity> i = mapping.allModelEntitiesStoredByXMLTags();
			while (i.hasNext()) {
				ModelEntity me = i.next();
				checkModelEntity(me);
			}
		}

		private void checkModelEntity(ModelEntity me) throws Exception {
			checkProperties(me);
			checkAbstracticity(me);
		}

		private void checkProperties(ModelEntity me) {
			Enumeration<ModelProperty> en = me.getModelProperties();
			while (en.hasMoreElements()) {
				ModelProperty mp = en.nextElement();
				mp.getKeyValueProperty();
			}

		}

		private void checkAbstracticity(ModelEntity me) throws Exception {
			String className = me.getName();
			Class<?> klass = Class.forName(className);
			if (!me.isAbstract() && Modifier.isAbstract(klass.getModifiers())) {
				fail(me.getName() + " is declared as not abstract but class is not instanciable");
			}
		}*/
}
