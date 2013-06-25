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
import java.io.IOException;
import java.util.UUID;
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
import org.openflexo.technologyadapter.xml.model.XMLAttribute;
import org.openflexo.technologyadapter.xml.model.XMLIndividual;
import org.openflexo.technologyadapter.xml.model.XMLModel;
import org.openflexo.technologyadapter.xml.model.XMLTechnologyContextManager;
import org.openflexo.technologyadapter.xml.model.XMLType;
import org.openflexo.technologyadapter.xml.rm.XMLFileResource;
import org.openflexo.technologyadapter.xml.rm.XMLFileResourceImpl;
import org.openflexo.toolbox.FileResource;

public class TestXML extends FlexoTestCase {

	protected static final Logger logger = Logger.getLogger(TestPopulateDKV.class.getPackage().getName());

	private static ApplicationContext testApplicationContext;
	private static XMLTechnologyAdapter xmlAdapter;
	private static FlexoResourceCenter resourceCenter;
	private static ModelRepository<FlexoResource<XMLModel>, XMLModel, XMLModel, XMLTechnologyAdapter> modelRepository;
	private static MetaModelRepository<FlexoResource<XMLModel>, XMLModel, XMLModel, XMLTechnologyAdapter> metamodelRepository;
	private static String baseDirName;
	
	public TestXML(String name) {
		super(name);
	}

	private static final void dumpIndividual(XMLIndividual indiv, String prefix){

		System.out.println(prefix + "Indiv : " + indiv.getName() + "  ==> " + indiv.getUUID());
		for (XMLAttribute a : indiv.getAttributes()){
			System.out.println(prefix +"  attr: " + a.getName() + " = " + a.getValue().toString());
		}
		System.out.println();
		for (XMLIndividual x: indiv.getChildren()) dumpIndividual(x,prefix +"-----");
	}


	private static final void dumpTypes(XMLModel model){
		for (XMLType t : model.getTypes()){
			System.out.println("Inferred Type: " + t.getName()+" -> "+ t.getFullyQualifiedName() + "[" +t.getURI()+ "]");
		}
		System.out.println();
	}
	/**
	 * Instanciate test ResourceCenter
	 * @throws IOException 
	 */
	public void test0LoadTestResourceCenter() throws IOException {
		log("test0LoadTestResourceCenter()");
		testApplicationContext = new TestApplicationContext(new FileResource("src/test/resources/"));
		resourceCenter = new DirectoryResourceCenter(new File("src/test/resources/"));
		testApplicationContext.getResourceCenterService().addToResourceCenters(resourceCenter);
		resourceCenter.initialize(testApplicationContext.getTechnologyAdapterService());
		xmlAdapter = testApplicationContext.getTechnologyAdapterService().getTechnologyAdapter(XMLTechnologyAdapter.class);
		modelRepository = resourceCenter.getModelRepository(xmlAdapter);
		metamodelRepository = resourceCenter.getMetaModelRepository(xmlAdapter);
		baseDirName=((DirectoryResourceCenter)resourceCenter).getDirectory().getCanonicalPath();
		assertNotNull(modelRepository);
		assertNotNull(metamodelRepository);
		assertEquals(0, metamodelRepository.getAllResources().size());
		assertTrue(modelRepository.getAllResources().size()>3);
	}

	public void test1LoadFileAndDump() {

		log("test1LoadFileAndDump()");

		assertNotNull(modelRepository);

		XMLFileResource modelRes = (XMLFileResource) modelRepository.getResource("file:" + baseDirName + "/XML/example_library_1.xml");
		assertNotNull(modelRes);
		assertFalse(modelRes.isLoaded());
		assertNotNull(modelRes.getModelData());
		assertTrue(modelRes.isLoaded());

		// dumpTypes(modelRes.getModel());

		assertNotNull(modelRes.getModel().getTypeFromURI("#Library"));

		// dumpIndividual(modelRes.getModelData().getRoot(),"");


	}

	public void test2LoadFileAndDump() {

		log("test2LoadFileAndDump()");

		assertNotNull(modelRepository);

		XMLFileResource  modelRes = (XMLFileResource ) modelRepository.getResource("file:" + baseDirName + "/XML/example_library_2.xml");
		assertNotNull(modelRes);
		assertFalse(modelRes.isLoaded());
		assertNotNull(modelRes.getModelData());
		assertTrue(modelRes.isLoaded());

		// dumpTypes(modelRes.getModel());
		
		assertNotNull(modelRes.getModel().getTypeFromURI("http://www.example.org/Library#Library"));

		// dumpIndividual(modelRes.getModelData().getRoot(),"");
	}

	public void test3LoadFileAndDump() {

		log("test3LoadFileAndDump()");

		assertNotNull(modelRepository);

		XMLFileResource  modelRes = (XMLFileResource ) modelRepository.getResource("file:" + baseDirName + "/XML/example_library_3.xml");
		assertNotNull(modelRes);
		assertFalse(modelRes.isLoaded());
		assertNotNull(modelRes.getModelData());
		assertTrue(modelRes.isLoaded());

		assertNotNull(modelRes.getModel().getTypeFromURI("http://www.example.org/Library#Library"));


		 // dumpTypes(modelRes.getModel());
		// dumpIndividual(modelRes.getModelData().getRoot(),"");
	}

	public void test1CreateNewFile() throws Exception {

		log("test1CreateNewFile()");
		
			assertNotNull(modelRepository);

			String fileUUID = UUID.randomUUID().toString();
			String fileName = "src/test/resources/GenXML/example_File_"+fileUUID+".xml";

			File xmlFile = new File(fileName);

			XMLFileResource  modelRes = XMLFileResourceImpl.makeXMLFileResource(xmlFile, (XMLTechnologyContextManager) xmlAdapter.getTechnologyContextManager());

			XMLModel aModel = modelRes.getModel();

			XMLType aType = new XMLType ("http://montest.com","Blob","tst:Blob",aModel);
			aModel.addType(aType);
			aType = new XMLType ("http://zutalors.com","Blib","pt:Blib",aModel);
			aModel.addType(aType);

			XMLIndividual rootIndividual = aModel.createIndividual(aModel.getTypeFromURI("http://montest.com#Blob"));
			aModel.setRoot(rootIndividual);
			XMLIndividual anIndividual = aModel.createIndividual(aType);
			rootIndividual.addChild(anIndividual);
			

			assertNotNull(anIndividual);
			
			modelRes.save(null);
			

	}
}

