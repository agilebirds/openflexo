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
package org.openflexo.technologyadapter.xsd;

import java.io.File;
import java.util.logging.Logger;

import org.openflexo.ApplicationContext;
import org.openflexo.TestApplicationContext;
import org.openflexo.foundation.FlexoTestCase;
import org.openflexo.foundation.dkv.TestPopulateDKV;
import org.openflexo.foundation.resource.DirectoryResourceCenter;
import org.openflexo.foundation.resource.FlexoResource;
import org.openflexo.foundation.resource.FlexoResourceCenter;
import org.openflexo.foundation.technologyadapter.MetaModelRepository;
import org.openflexo.foundation.technologyadapter.ModelRepository;
import org.openflexo.technologyadapter.xsd.model.XMLModel;
import org.openflexo.technologyadapter.xsd.model.XSDMetaModel;
import org.openflexo.technologyadapter.xsd.model.XSDMetaModelRepository;
import org.openflexo.technologyadapter.xsd.rm.XSDMetaModelResource;
import org.openflexo.toolbox.FileResource;

public class TestXSD extends FlexoTestCase {

	protected static final Logger logger = Logger.getLogger(TestPopulateDKV.class.getPackage().getName());

	private static ApplicationContext testApplicationContext;
	private static XSDTechnologyAdapter xsdAdapter;
	private static FlexoResourceCenter resourceCenter;
	private static MetaModelRepository<XSDMetaModelResource, XMLModel, XSDMetaModel, XSDTechnologyAdapter> mmRepository;
	private static ModelRepository<FlexoResource<XMLModel>, XMLModel, XSDMetaModel, XSDTechnologyAdapter> modelRepository;

	public TestXSD(String name) {
		super(name);
	}

	/**
	 * Instanciate test ResourceCenter
	 */
	public void test0LoadTestResourceCenter() {
		log("test0LoadTestResourceCenter()");
		testApplicationContext = new TestApplicationContext(new FileResource("src/test/resources/XSD"));
		resourceCenter = new DirectoryResourceCenter(new File("src/test/resources/XSD"));
		testApplicationContext.getResourceCenterService().addToResourceCenters(resourceCenter);
		resourceCenter.initialize(testApplicationContext.getTechnologyAdapterService());
		xsdAdapter = testApplicationContext.getTechnologyAdapterService().getTechnologyAdapter(XSDTechnologyAdapter.class);
		mmRepository = resourceCenter.getMetaModelRepository(xsdAdapter);
		modelRepository = resourceCenter.getModelRepository(xsdAdapter);
		assertNotNull(mmRepository);
		assertNotNull(modelRepository);
		assertEquals(3, mmRepository.getAllResources().size());
		assertEquals(3, modelRepository.getAllResources().size());
	}

	public void test1LibraryMetaModelPresentAndLoaded() {

		log("test1LibraryMetaModelPresentAndLoaded()");

		assertNotNull(mmRepository);
		assertNotNull(modelRepository);

		XSDMetaModelResource libraryRes = (XSDMetaModelResource) mmRepository.getResource("http://www.openflexo.org/XSD/library.xsd");
		assertNotNull(libraryRes);
		assertFalse(libraryRes.isLoaded());
		assertNotNull(libraryRes.getMetaModelData());
		assertTrue(libraryRes.isLoaded());

		logger.info("Classes: " + libraryRes.getMetaModelData().getClasses());

		// TODO: implement tests
		logger.warning("Please perform some checks here");

	}

	public void test2MavenMetaModelPresentAndLoaded() {
		log("test2MavenMetaModelPresentAndLoaded()");
		assertNotNull(mmRepository);
		assertNotNull(modelRepository);

		XSDMetaModelResource mavenRes = (XSDMetaModelResource) mmRepository.getResource("http://www.openflexo.org/XSD/maven-v4_0_0.xsd");
		assertNotNull(mavenRes);
		assertFalse(mavenRes.isLoaded());
		assertNotNull(mavenRes.getMetaModelData());
		assertTrue(mavenRes.isLoaded());

		logger.info("Classes: " + mavenRes.getMetaModelData().getClasses());

		// TODO: implement tests
		logger.warning("Please perform some checks here");
	}
}

