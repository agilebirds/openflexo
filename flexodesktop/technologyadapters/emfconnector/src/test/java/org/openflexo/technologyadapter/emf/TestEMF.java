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
package org.openflexo.technologyadapter.emf;

import java.util.logging.Logger;

import org.openflexo.ApplicationContext;
import org.openflexo.TestApplicationContext;
import org.openflexo.foundation.FlexoTestCase;
import org.openflexo.foundation.dkv.TestPopulateDKV;
import org.openflexo.foundation.resource.FlexoResource;
import org.openflexo.foundation.resource.FlexoResourceCenter;
import org.openflexo.foundation.technologyadapter.MetaModelRepository;
import org.openflexo.foundation.technologyadapter.ModelRepository;
import org.openflexo.technologyadapter.emf.metamodel.EMFMetaModel;
import org.openflexo.technologyadapter.emf.model.EMFModel;
import org.openflexo.technologyadapter.emf.rm.EMFMetaModelResource;
import org.openflexo.toolbox.FileResource;

public class TestEMF extends FlexoTestCase {

	protected static final Logger logger = Logger.getLogger(TestPopulateDKV.class.getPackage().getName());

	private static ApplicationContext testApplicationContext;
	private static EMFTechnologyAdapter emfAdapter;
	private static FlexoResourceCenter resourceCenter;
	private static MetaModelRepository<FlexoResource<EMFMetaModel>, EMFModel, EMFMetaModel, EMFTechnologyAdapter> mmRepository;
	private static ModelRepository<FlexoResource<EMFModel>, EMFModel, EMFMetaModel, EMFTechnologyAdapter> modelRepository;

	public TestEMF(String name) {
		super(name);
	}

	/**
	 * Instanciate test ResourceCenter
	 */
	public void test0LoadTestResourceCenter() {
		log("test0LoadTestResourceCenter()");
		testApplicationContext = new TestApplicationContext(new FileResource("src/test/resources/EMF"));
		emfAdapter = testApplicationContext.getTechnologyAdapterService().getTechnologyAdapter(EMFTechnologyAdapter.class);
		resourceCenter = testApplicationContext.getResourceCenterService().getResourceCenters().get(0);
		mmRepository = resourceCenter.getMetaModelRepository(emfAdapter);
		modelRepository = resourceCenter.getModelRepository(emfAdapter);
		assertNotNull(mmRepository);
		assertNotNull(modelRepository);
		assertEquals(2, mmRepository.getAllResources().size());
	}

	public void test1MyEMFMetaModelPresentAndLoaded() {
		log("test1LibraryMetaModelPresentAndLoaded()");
		EMFMetaModelResource myEMFMetaModelRes = (EMFMetaModelResource) mmRepository.getResource("http://my.emf.meta.model");
		assertNotNull(myEMFMetaModelRes);
		assertFalse(myEMFMetaModelRes.isLoaded());
		assertNotNull(myEMFMetaModelRes.getMetaModel());
		assertTrue(myEMFMetaModelRes.isLoaded());

		logger.info("Classes: " + myEMFMetaModelRes.getMetaModel().getClasses());

		// TODO: implement tests
		logger.warning("Please perform some checks here");

	}

}
