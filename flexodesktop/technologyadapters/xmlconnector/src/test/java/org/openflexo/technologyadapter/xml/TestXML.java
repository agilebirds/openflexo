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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.UUID;
import java.util.logging.Logger;

import org.openflexo.ApplicationContext;
import org.openflexo.TestApplicationContext;
import org.openflexo.foundation.FlexoException;
import org.openflexo.foundation.OpenflexoRunTimeTestCase;
import org.openflexo.foundation.resource.DirectoryResourceCenter;
import org.openflexo.foundation.resource.FlexoResourceCenter;
import org.openflexo.foundation.resource.ResourceLoadingCancelledException;
import org.openflexo.technologyadapter.xml.model.IXMLIndividual;
import org.openflexo.technologyadapter.xml.model.XMLAttribute;
import org.openflexo.technologyadapter.xml.model.XMLIndividual;
import org.openflexo.technologyadapter.xml.model.XMLModel;
import org.openflexo.technologyadapter.xml.model.XMLTechnologyContextManager;
import org.openflexo.technologyadapter.xml.model.XMLType;
import org.openflexo.technologyadapter.xml.rm.XMLFileResource;
import org.openflexo.technologyadapter.xml.rm.XMLFileResourceImpl;
import org.openflexo.technologyadapter.xml.rm.XMLModelRepository;
import org.openflexo.toolbox.FileResource;

public class TestXML extends OpenflexoRunTimeTestCase {

	protected static final Logger logger = Logger.getLogger(TestXML.class.getPackage().getName());

	private static ApplicationContext testApplicationContext;
	private static XMLTechnologyAdapter xmlAdapter;
	private static FlexoResourceCenter resourceCenter;
	private static XMLModelRepository modelRepository;
	private static String baseDirName;

	private static final void dumpIndividual(IXMLIndividual<XMLIndividual, XMLAttribute> indiv, String prefix) {

		System.out.println(prefix + "Indiv : " + indiv.getName() + "  ==> " + indiv.getUUID());
		for (XMLAttribute a : indiv.getAttributes()) {
			System.out.println(prefix + "    * attr: " + a.getName() + " = " + a.getValue().toString());
		}
		for (IXMLIndividual<XMLIndividual, XMLAttribute> x : indiv.getChildren())
			dumpIndividual(x, prefix + "      ");
		System.out.flush();
	}

	private static final void dumpTypes(XMLModel model) {
		for (XMLType t : model.getTypes()) {
			System.out.println("Inferred Type: " + t.getName() + " -> " + t.getFullyQualifiedName() + "[" + t.getURI() + "]");
			System.out.println("");
			System.out.flush();
		}
	}

	/**
	 * Instanciate test ResourceCenter
	 * 
	 * @throws IOException
	 */
	public void test0LoadTestResourceCenter() throws IOException {
		log("test0LoadTestResourceCenter()");
		testApplicationContext = new TestApplicationContext(new FileResource("src/test/resources/"));
		resourceCenter = new DirectoryResourceCenter(new File("src/test/resources/"));
		testApplicationContext.getResourceCenterService().addToResourceCenters(resourceCenter);
		resourceCenter.initialize(testApplicationContext.getTechnologyAdapterService());
		xmlAdapter = testApplicationContext.getTechnologyAdapterService().getTechnologyAdapter(XMLTechnologyAdapter.class);
		modelRepository = (XMLModelRepository) resourceCenter.getRepository(XMLModelRepository.class, xmlAdapter);
		baseDirName = ((DirectoryResourceCenter) resourceCenter).getDirectory().getCanonicalPath();
		assertNotNull(modelRepository);
		assertTrue(modelRepository.getAllResources().size() > 3);
	}

	public void test0LoadFileAndDump() throws FileNotFoundException, ResourceLoadingCancelledException, FlexoException {

		log("test1LoadFileAndDump()");

		assertNotNull(modelRepository);

		XMLFileResource modelRes = modelRepository.getResource("file:" + baseDirName + "/XML/example_library_0.xml");
		assertNotNull(modelRes);
		assertFalse(modelRes.isLoaded());
		assertNotNull(modelRes.getModelData());
		assertNotNull(modelRes.loadResourceData(null));
		assertTrue(modelRes.isLoaded());

		// dumpTypes(modelRes.getModel());

		assertNotNull(modelRes.getModel().getTypeFromURI("#Library"));

		dumpIndividual(modelRes.getModelData().getRoot(), "");

	}

	public void test1LoadFileAndDump() throws FileNotFoundException, ResourceLoadingCancelledException, FlexoException {

		log("test1LoadFileAndDump()");

		assertNotNull(modelRepository);

		XMLFileResource modelRes = modelRepository.getResource("file:" + baseDirName + "/XML/example_library_1.xml");
		assertNotNull(modelRes);
		assertFalse(modelRes.isLoaded());
		assertNotNull(modelRes.getModelData());
		assertNotNull(modelRes.loadResourceData(null));
		assertTrue(modelRes.isLoaded());

		// dumpTypes(modelRes.getModel());

		assertNotNull(modelRes.getModel().getTypeFromURI("#Library"));

		dumpIndividual(modelRes.getModelData().getRoot(), "");

	}

	public void test2LoadFileAndDump() throws FileNotFoundException, ResourceLoadingCancelledException, FlexoException {

		log("test2LoadFileAndDump()");

		assertNotNull(modelRepository);

		XMLFileResource modelRes = modelRepository.getResource("file:" + baseDirName + "/XML/example_library_2.xml");
		assertNotNull(modelRes);
		assertFalse(modelRes.isLoaded());
		assertNotNull(modelRes.getModelData());
		assertNotNull(modelRes.loadResourceData(null));
		assertTrue(modelRes.isLoaded());

		// dumpTypes(modelRes.getModel());

		assertNotNull(modelRes.getModel().getTypeFromURI("http://www.example.org/Library#Library"));

		// dumpIndividual(modelRes.getModelData().getRoot(),"");
	}

	public void test3LoadFileAndDump() throws FileNotFoundException, ResourceLoadingCancelledException, FlexoException {

		log("test3LoadFileAndDump()");

		assertNotNull(modelRepository);

		XMLFileResource modelRes = modelRepository.getResource("file:" + baseDirName + "/XML/example_library_3.xml");
		assertNotNull(modelRes);
		assertFalse(modelRes.isLoaded());
		assertNotNull(modelRes.getModelData());
		assertNotNull(modelRes.loadResourceData(null));
		assertTrue(modelRes.isLoaded());

		assertNotNull(modelRes.getModel().getTypeFromURI("http://www.example.org/Library#Library"));

		// dumpTypes(modelRes.getModel());
		// dumpIndividual(modelRes.getModelData().getRoot(),"");
	}

	public void test1CreateNewFile() throws Exception {

		log("test1CreateNewFile()");

		assertNotNull(modelRepository);

		String fileUUID = UUID.randomUUID().toString();
		String fileName = "src/test/resources/GenXML/example_File_" + fileUUID + ".xml";

		File xmlFile = new File(fileName);

		XMLFileResource modelRes = XMLFileResourceImpl.makeXMLFileResource(xmlFile,
				(XMLTechnologyContextManager) xmlAdapter.getTechnologyContextManager());

		XMLModel aModel = modelRes.getModel();
		aModel.setNamespace("http://montest.com", "tst");

		XMLType aType = new XMLType("http://montest.com", "Blob", "tst:Blob", aModel);
		aModel.addType(aType);
		aType = new XMLType("http://zutalors.com", "Blib", "pt:Blib", aModel);
		aModel.addType(aType);

		XMLIndividual rootIndividual = (XMLIndividual) aModel.addNewIndividual(aModel.getTypeFromURI("http://montest.com#Blob"));
		aModel.setRoot(rootIndividual);
		XMLIndividual anIndividual = (XMLIndividual) aModel.addNewIndividual(aType);
		XMLAttribute anAttr = (XMLAttribute) anIndividual.createAttribute("name", String.class, "Mon velo court");
		rootIndividual.addChild(anIndividual);
		anIndividual = (XMLIndividual) aModel.addNewIndividual(aType);
		anAttr = (XMLAttribute) anIndividual.createAttribute("name", String.class, "Pan");
		anAttr = (XMLAttribute) anIndividual.createAttribute("ID", String.class, "17");
		rootIndividual.addChild(anIndividual);

		assertNotNull(anIndividual);

		dumpTypes(modelRes.getModel());
		dumpIndividual(modelRes.getModel().getRoot(), "");

		modelRes.save(null);

	}
}
