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

import java.lang.reflect.Modifier;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.logging.Logger;

import org.openflexo.foundation.FlexoTestCase;
import org.openflexo.foundation.cg.GeneratedCode;
import org.openflexo.foundation.cg.GeneratedDoc;
import org.openflexo.foundation.dkv.DKVModel;
import org.openflexo.foundation.dm.DMModel;
import org.openflexo.foundation.ie.IEWOComponent;
import org.openflexo.foundation.ie.cl.FlexoComponentLibrary;
import org.openflexo.foundation.ie.menu.FlexoNavigationMenu;
import org.openflexo.foundation.rm.FlexoProject;
import org.openflexo.foundation.toc.TOCData;
import org.openflexo.foundation.view.ViewLibrary;
import org.openflexo.foundation.wkf.FlexoProcess;
import org.openflexo.foundation.wkf.FlexoWorkflow;
import org.openflexo.foundation.ws.FlexoWSLibrary;
import org.openflexo.foundation.xml.FlexoXMLMappings.ClassModels;
import org.openflexo.toolbox.FlexoVersion;
import org.openflexo.xmlcode.ModelEntity;
import org.openflexo.xmlcode.ModelProperty;
import org.openflexo.xmlcode.XMLMapping;

public class TestXMLMappings extends FlexoTestCase {

	protected static final Logger logger = Logger.getLogger(TestXMLMappings.class.getPackage().getName());

	private FlexoXMLMappings xmlMappings;

	public TestXMLMappings() {
		super("TestXMLMappings");
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		xmlMappings = new FlexoXMLMappings();
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

	public void testShemaLibraryMappings() {
		checkClassModels(ViewLibrary.class);
	}

	public void testShemaMappings() {
		// checkClassModels(View.class);
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

	private void checkClassModels(Class aClass) {
		boolean testFails = false;
		StringBuilder sb = new StringBuilder();
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
					sb.append("Failed decoded mapping for class ").append(aClass.getSimpleName()).append(", version ").append(version)
							.append("\n");
				}
				checkXMLMapping(mapping);
			} catch (Exception e) {
				e.printStackTrace();
				logger.warning("Failed decoded mapping for class " + aClass.getSimpleName() + ", version " + version + " " + e.getMessage());
				sb.append("Failed decoded mapping for class ").append(aClass.getSimpleName()).append(", version ").append(version)
						.append("\n");
				sb.append(e.getMessage());
				testFails = true;
			}
		}
		if (testFails) {
			fail(sb.toString());
		}
	}

	protected void checkXMLMapping(XMLMapping mapping) throws Exception {
		Iterator<ModelEntity> i = mapping.allModelEntities();
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
	}
}
