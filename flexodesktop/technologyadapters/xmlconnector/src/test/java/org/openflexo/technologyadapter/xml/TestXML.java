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
package org.openflexo.technologyadapter.xml;

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
import org.openflexo.technologyadapter.xml.model.XMLIndividual;
import org.openflexo.technologyadapter.xml.model.XMLModel;
import org.openflexo.technologyadapter.xml.rm.XMLFileResource;
import org.openflexo.toolbox.FileResource;

public class TestXML extends FlexoTestCase {

	protected static final Logger logger = Logger.getLogger(TestPopulateDKV.class.getPackage().getName());

	private static ApplicationContext testApplicationContext;
	private static XMLTechnologyAdapter xmlAdapter;
	private static FlexoResourceCenter resourceCenter;
	private static ModelRepository<FlexoResource<XMLModel>, XMLModel, XMLModel, XMLTechnologyAdapter> modelRepository;
	private static MetaModelRepository<FlexoResource<XMLModel>, XMLModel, XMLModel, XMLTechnologyAdapter> metamodelRepository;

	public TestXML(String name) {
		super(name);
	}
	
	private static final void dumpIndividual(XMLIndividual indiv, String prefix){

		System.out.println(prefix + "Indiv : " + indiv.getName() + "  ==> " + indiv.getURI());
		for (XMLIndividual x: indiv.getChildren()) dumpIndividual(x,prefix +"-----");
	}
	

	/**
	 * Instanciate test ResourceCenter
	 */
	public void test0LoadTestResourceCenter() {
		log("test0LoadTestResourceCenter()");
		testApplicationContext = new TestApplicationContext(new FileResource("src/test/resources/XML"));
		resourceCenter = new DirectoryResourceCenter(new File("src/test/resources/XML"));
		testApplicationContext.getResourceCenterService().addToResourceCenters(resourceCenter);
		resourceCenter.initialize(testApplicationContext.getTechnologyAdapterService());
		xmlAdapter = testApplicationContext.getTechnologyAdapterService().getTechnologyAdapter(XMLTechnologyAdapter.class);
		modelRepository = resourceCenter.getModelRepository(xmlAdapter);
		metamodelRepository = resourceCenter.getMetaModelRepository(xmlAdapter);
		assertNotNull(modelRepository);
		assertNotNull(metamodelRepository);
		assertEquals(0, metamodelRepository.getAllResources().size());
		assertEquals(1, modelRepository.getAllResources().size());
	}

	public void test1LoadFileAndDump() {

		log("test1LoadFileAndDump()");

		assertNotNull(modelRepository);

		XMLFileResource modelRes = (XMLFileResource) modelRepository.getResource("file:/u01/data/Projets/openflexo/1.6/Git/openflexo/flexodesktop/technologyadapters/xmlconnector/src/test/resources/XML/example_library_1.xml");
		assertNotNull(modelRes);
		assertFalse(modelRes.isLoaded());
		assertNotNull(modelRes.getModelData());
		assertTrue(modelRes.isLoaded());

		dumpIndividual(modelRes.getModelData().getRoot(),"");

	}

	public void test2MavenMetaModelPresentAndLoaded() {
		log("test2MavenMetaModelPresentAndLoaded()");
		assertNotNull(modelRepository);

		// TODO: implement tests
		logger.warning("Please perform some checks here");
	}
}

