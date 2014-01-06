package org.openflexo.technologyadapter.xsd;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openflexo.ApplicationContext;
import org.openflexo.TestApplicationContext;
import org.openflexo.foundation.OpenflexoRunTimeTestCase;
import org.openflexo.foundation.resource.DirectoryResourceCenter;
import org.openflexo.foundation.resource.FlexoResourceCenter;
import org.openflexo.technologyadapter.xsd.metamodel.XSDMetaModel;
import org.openflexo.technologyadapter.xsd.metamodel.XSOntClass;
import org.openflexo.technologyadapter.xsd.metamodel.XSOntDataProperty;
import org.openflexo.technologyadapter.xsd.metamodel.XSOntObjectProperty;
import org.openflexo.technologyadapter.xsd.model.AbstractXSOntObject;
import org.openflexo.technologyadapter.xsd.model.XSDTechnologyContextManager;
import org.openflexo.technologyadapter.xsd.model.XSOntology;
import org.openflexo.technologyadapter.xsd.rm.XMLModelRepository;
import org.openflexo.technologyadapter.xsd.rm.XSDMetaModelRepository;
import org.openflexo.technologyadapter.xsd.rm.XSDMetaModelResource;
import org.openflexo.technologyadapter.xsd.rm.XSDMetaModelResourceImpl;

public class TestLibrary extends OpenflexoRunTimeTestCase {

	private static final String FILE_NAME = "library";

	protected static final Logger logger = Logger.getLogger(TestLibrary.class.getPackage().getName());

	private static XSDTechnologyAdapter xsdAdapter;
	private static XSDTechnologyContextManager xsdContextManager;
	private static FlexoResourceCenter<?> resourceCenter;
	private static XSDMetaModelRepository mmRepository;
	private static XMLModelRepository modelRepository;
	private static ApplicationContext testApplicationContext;

	public static File openTestXSD(String filename) throws FileNotFoundException {
		// TODO Use resource manager?
		// tip to do it, look at: LocalResourceCenterImpl.findOntologies
		File result = null;
		result = new File("src/test/resources/XSD/" + filename + ".xsd");
		if (result.exists() == false) {
			if (logger.isLoggable(Level.WARNING)) {
				logger.warning("Failed to load file " + filename + ".xsd");
				throw new FileNotFoundException(result.getAbsolutePath());
			}
		}
		return result;
	}

	public static void xsoObject(AbstractXSOntObject obj, StringBuffer buffer) {
		buffer.append(obj.getClass().getSimpleName());
		buffer.append(" Name: ").append(obj.getName());
		buffer.append(" URI: ").append(obj.getURI()).append("\n");
	}

	public static void generalInfos(XSOntology lib, StringBuffer buffer) {
		buffer.append("* General infos *\n");
		buffer.append("Name: ").append(lib.getName()).append("\n");
		buffer.append("URI: ").append(lib.getURI()).append("\n");
		buffer.append("\n");
	}

	public static void classListing(XSOntology lib, StringBuffer buffer) {
		assertNotNull(lib.getClasses());
		assertFalse(lib.getClasses().isEmpty());
		buffer.append("Classes\n");
		for (XSOntClass xsoClass : lib.getClasses()) {
			xsoObject(xsoClass, buffer);
		}
		buffer.append("\n");
	}

	public static void dataPropertyListing(XSOntology lib, StringBuffer buffer) {
		assertNotNull(lib.getDataProperties());
		assertFalse(lib.getDataProperties().isEmpty());
		buffer.append("Data properties\n");
		for (XSOntDataProperty xsoDP : ((XSDMetaModel) lib).getDataProperties()) {
			xsoObject(xsoDP, buffer);
		}
		buffer.append("\n");
	}

	public static void objectPropertyListing(XSOntology lib, StringBuffer buffer) {
		assertNotNull(lib.getObjectProperties());
		assertFalse(lib.getObjectProperties().isEmpty());
		buffer.append("Object properties\n");
		for (XSOntObjectProperty xsoOP : lib.getObjectProperties()) {
			xsoObject(xsoOP, buffer);
		}
		buffer.append("\n");
	}

	/**
	 * Instanciate test ResourceCenter
	 * 
	 * @throws IOException
	 */
	public void test0LoadTestResourceCenter() throws IOException {
		log("test0LoadTestResourceCenter()");
		testApplicationContext = new TestApplicationContext();
		resourceCenter = new DirectoryResourceCenter(new File("src/test/resources/"));
		testApplicationContext.getResourceCenterService().addToResourceCenters(resourceCenter);
		resourceCenter.initialize(testApplicationContext.getTechnologyAdapterService());
		xsdAdapter = testApplicationContext.getTechnologyAdapterService().getTechnologyAdapter(XSDTechnologyAdapter.class);
		xsdContextManager = xsdAdapter.getTechnologyContextManager();
		mmRepository = resourceCenter.getRepository(XSDMetaModelRepository.class, xsdAdapter);
		modelRepository = resourceCenter.getRepository(XMLModelRepository.class, xsdAdapter);
		((DirectoryResourceCenter) resourceCenter).getDirectory().getCanonicalPath();
		assertNotNull(modelRepository);
		assertNotNull(mmRepository);
		assertEquals(3, mmRepository.getAllResources().size());
		assertTrue(modelRepository.getAllResources().size() > 2);
	}

	public void test1Library() {
		StringBuffer buffer = new StringBuffer();
		XSDMetaModel lib = null;
		XSDMetaModelResource libRes = null;
		testApplicationContext = new TestApplicationContext();

		try {
			libRes = XSDMetaModelResourceImpl.makeXSDMetaModelResource(openTestXSD(FILE_NAME),
					"http://www.openflexo.org/test/XSD/library.owl", xsdContextManager);
			lib = new XSDMetaModel("http://www.openflexo.org/test/XSD/library.owl", openTestXSD(FILE_NAME), xsdAdapter);
			lib.setResource(libRes);
			libRes.setResourceData(lib);
		} catch (FileNotFoundException e) {
			fail(e.getMessage());
		}
		assertNotNull(lib);
		((XSDMetaModelResourceImpl) lib.getResource()).loadWhenUnloaded();
		assertTrue(lib.getResource().isLoaded());
		if (lib.getResource().isLoaded() == false) {
			if (logger.isLoggable(Level.WARNING)) {
				logger.warning("Failed to load.");
				fail();
			}
		} else {
			generalInfos(lib, buffer);
			classListing(lib, buffer);
			dataPropertyListing(lib, buffer);
			objectPropertyListing(lib, buffer);

			if (logger.isLoggable(Level.INFO)) {
				logger.info(buffer.toString());
			}
		}
	}

}
