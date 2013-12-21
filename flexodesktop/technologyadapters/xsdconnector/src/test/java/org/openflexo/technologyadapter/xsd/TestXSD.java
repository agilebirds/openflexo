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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.logging.Logger;

import org.openflexo.foundation.OpenflexoRunTimeTestCase;
import org.openflexo.foundation.TestFlexoServiceManager;
import org.openflexo.foundation.resource.DirectoryResourceCenter;
import org.openflexo.foundation.resource.FlexoResourceCenter;
import org.openflexo.technologyadapter.xsd.rm.XMLModelRepository;
import org.openflexo.technologyadapter.xsd.rm.XSDMetaModelRepository;
import org.openflexo.technologyadapter.xsd.rm.XSDMetaModelResource;
import org.openflexo.toolbox.FileResource;

public class TestXSD extends OpenflexoRunTimeTestCase {

	protected static final Logger logger = Logger.getLogger(TestXSD.class.getPackage().getName());

	private static TestFlexoServiceManager testServiceManager;
	private static XSDTechnologyAdapter xsdAdapter;
	private static FlexoResourceCenter<?> resourceCenter;
	private static XSDMetaModelRepository mmRepository;
	private static XMLModelRepository modelRepository;

	/**
	 * Instanciate test ResourceCenter
	 */
	public void test0LoadTestResourceCenter() {
		log("test0LoadTestResourceCenter()");
		testServiceManager = new TestFlexoServiceManager(new FileResource("src/test/resources/XSD"));
		xsdAdapter = testServiceManager.getTechnologyAdapterService().getTechnologyAdapter(XSDTechnologyAdapter.class);
		resourceCenter = new DirectoryResourceCenter(new File("src/test/resources/"));
		testServiceManager.getResourceCenterService().addToResourceCenters(resourceCenter);
		mmRepository = resourceCenter.getRepository(XSDMetaModelRepository.class, xsdAdapter);
		modelRepository = resourceCenter.getRepository(XMLModelRepository.class, xsdAdapter);
		assertNotNull(mmRepository);
		assertNotNull(modelRepository);
		assertEquals(3, mmRepository.getAllResources().size());
	}

	public void test1LibraryMetaModelPresentAndLoaded() {
		log("test1LibraryMetaModelPresentAndLoaded()");
		XSDMetaModelResource libraryRes = mmRepository.getResource("http://www.example.org/Library");
		assertNotNull(libraryRes);
		// TODO
		// assertFalse(libraryRes.isLoaded());
		assertNotNull(libraryRes.getMetaModelData());
		// assertTrue(libraryRes.isLoaded());

		logger.info("Classes: " + libraryRes.getMetaModelData().getClasses());

		// TODO: implement tests
		logger.warning("Please perform some checks here");

	}

	public void test2MavenMetaModelPresentAndLoaded() {
		log("test2MavenMetaModelPresentAndLoaded()");
		XSDMetaModelResource mavenRes = mmRepository.getResource("http://maven.apache.org/POM/4.0.0");
		assertNotNull(mavenRes);
		// TODO
		// assertFalse(mavenRes.isLoaded());
		assertNotNull(mavenRes.getMetaModelData());
		assertTrue(mavenRes.isLoaded());

		logger.info("Classes: " + mavenRes.getMetaModelData().getClasses());

		// TODO: implement tests
		logger.warning("Please perform some checks here");

	}

}
