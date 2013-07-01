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
import java.io.FileNotFoundException;
import java.util.logging.Logger;

import org.openflexo.ApplicationContext;
import org.openflexo.TestApplicationContext;
import org.openflexo.foundation.FlexoException;
import org.openflexo.foundation.FlexoTestCase;
import org.openflexo.foundation.dkv.TestPopulateDKV;
import org.openflexo.foundation.resource.DirectoryResourceCenter;
import org.openflexo.foundation.resource.FlexoResource;
import org.openflexo.foundation.resource.FlexoResourceCenter;
import org.openflexo.foundation.resource.ResourceLoadingCancelledException;
import org.openflexo.foundation.rm.ResourceDependencyLoopException;
import org.openflexo.foundation.technologyadapter.MetaModelRepository;
import org.openflexo.foundation.technologyadapter.ModelRepository;
import org.openflexo.technologyadapter.xml.model.XMLType;
import org.openflexo.technologyadapter.xsd.metamodel.XSDMetaModel;
import org.openflexo.technologyadapter.xsd.metamodel.XSOntClass;
import org.openflexo.technologyadapter.xsd.metamodel.XSOntProperty;
import org.openflexo.technologyadapter.xsd.model.XMLXSDModel;
import org.openflexo.technologyadapter.xsd.rm.XSDMetaModelRepository;
import org.openflexo.technologyadapter.xsd.rm.XSDMetaModelResource;
import org.openflexo.toolbox.FileResource;

public class TestXSD extends FlexoTestCase {

	protected static final Logger logger = Logger.getLogger(TestXSD.class.getPackage().getName());

	private static ApplicationContext testApplicationContext;
	private static XSDTechnologyAdapter xsdAdapter;
	private static FlexoResourceCenter resourceCenter;
	private static MetaModelRepository<XSDMetaModelResource, XMLXSDModel, XSDMetaModel, XSDTechnologyAdapter> mmRepository;
	private static ModelRepository<FlexoResource<XMLXSDModel>, XMLXSDModel, XSDMetaModel, XSDTechnologyAdapter> modelRepository;

	public TestXSD(String name) {
		super(name);
	}



	private static final void dumpTypes(XSDMetaModel model){
		for (XSOntClass t : model.getClasses()){
			System.out.println("Known Type: " + t.getName()+" -> "+ t.getFullyQualifiedName() + "[" +t.getURI()+ "]");
			for (XSOntProperty p : t.getPropertiesTakingMySelfAsDomain()){
				System.out.println("     prop : " + p.getName()+" -> "+ p.getRange().getName() + "[" +p.getURI()+ "]");

			}
		}
		System.out.println();
		System.out.flush();
		System.out.flush();
	}

	/**
	 * Instanciate test ResourceCenter
	 */
	public void test0LoadTestResourceCenter() {
		log("test0LoadTestResourceCenter()");
		testApplicationContext = new TestApplicationContext(new FileResource("src/test/resources"));
		resourceCenter = new DirectoryResourceCenter(new File("src/test/resources"));
		testApplicationContext.getResourceCenterService().addToResourceCenters(resourceCenter);
		resourceCenter.initialize(testApplicationContext.getTechnologyAdapterService());
		xsdAdapter = testApplicationContext.getTechnologyAdapterService().getTechnologyAdapter(XSDTechnologyAdapter.class);
		mmRepository = resourceCenter.getMetaModelRepository(xsdAdapter);
		modelRepository = resourceCenter.getModelRepository(xsdAdapter);
		assertNotNull(mmRepository);
		assertNotNull(modelRepository);
		assertTrue(mmRepository.getAllResources().size()>2);
		assertTrue(modelRepository.getAllResources().size()>2);
	}

	public void test1LibraryMetaModelPresentAndLoaded() throws FileNotFoundException, ResourceLoadingCancelledException, ResourceDependencyLoopException, FlexoException {

		log("test1LibraryMetaModelPresentAndLoaded()");

		assertNotNull(mmRepository);
		assertNotNull(modelRepository);

		XSDMetaModelResource libraryRes = (XSDMetaModelResource) mmRepository.getResource("http://www.example.org/Library");
		assertNotNull(libraryRes);
		if (!libraryRes.isLoaded()) {
			assertNotNull(libraryRes.loadResourceData(null));
		}
		assertTrue(libraryRes.isLoaded());

		logger.info("Classes: " );
		dumpTypes(libraryRes.getMetaModelData());

		// TODO: implement tests
		logger.warning("Please perform some checks here");

	}

	public void test2MavenMetaModelPresentAndLoaded() {
		log("test2MavenMetaModelPresentAndLoaded()");
		assertNotNull(mmRepository);
		assertNotNull(modelRepository);

		XSDMetaModelResource mavenRes = (XSDMetaModelResource) mmRepository.getResource("http://maven.apache.org/POM/4.0.0");
		assertNotNull(mavenRes);
		assertNotNull(mavenRes.getMetaModelData());

		logger.info("Classes: " );
		dumpTypes(mavenRes.getMetaModelData());

		// TODO: implement tests
		logger.warning("Please perform some checks here");
	}

}

