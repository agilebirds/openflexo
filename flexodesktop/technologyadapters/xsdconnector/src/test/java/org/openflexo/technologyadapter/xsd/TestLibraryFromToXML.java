package org.openflexo.technologyadapter.xsd;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.openflexo.ApplicationContext;
import org.openflexo.TestApplicationContext;
import org.openflexo.foundation.FlexoException;
import org.openflexo.foundation.FlexoTestCase;
import org.openflexo.foundation.resource.DirectoryResourceCenter;
import org.openflexo.foundation.resource.FileSystemBasedResourceCenter;
import org.openflexo.foundation.resource.FlexoResourceCenter;
import org.openflexo.foundation.resource.ResourceLoadingCancelledException;
import org.openflexo.foundation.rm.ResourceDependencyLoopException;
import org.openflexo.foundation.rm.SaveResourceException;
import org.openflexo.technologyadapter.xml.model.IXMLIndividual;
import org.openflexo.technologyadapter.xsd.metamodel.XSDMetaModel;
import org.openflexo.technologyadapter.xsd.metamodel.XSOntProperty;
import org.openflexo.technologyadapter.xsd.model.XMLXSDModel;
import org.openflexo.technologyadapter.xsd.model.XSOntIndividual;
import org.openflexo.technologyadapter.xsd.model.XSPropertyValue;
import org.openflexo.technologyadapter.xsd.rm.XMLModelRepository;
import org.openflexo.technologyadapter.xsd.rm.XMLXSDFileResource;
import org.openflexo.technologyadapter.xsd.rm.XSDMetaModelRepository;
import org.openflexo.technologyadapter.xsd.rm.XSDMetaModelResource;
import org.openflexo.toolbox.FileResource;

public class TestLibraryFromToXML extends FlexoTestCase {

	private static final java.util.logging.Logger logger = org.openflexo.logging.FlexoLogger.getLogger(TestLibraryFromToXML.class
			.getPackage().getName());

	private static final String FILE_NAME = "library";
	private static final String LIBRARY_URI = "http://www.example.org/Library#Library";
	private static final String BOOK_URI = "http://www.example.org/Library#Book";
	private static final String BOOK_TITLE_URI = "http://www.example.org/Library/Book#title";
	private static final String LIB_NAME_URI = "http://www.example.org/Library/LibraryType#name";
	private static final String LIB_BOOKS_URI = "http://www.example.org/Library/LibraryType#books";

	private static ApplicationContext testApplicationContext;
	private static XSDTechnologyAdapter xsdAdapter;
	private static FlexoResourceCenter resourceCenter;
	private static XSDMetaModelRepository mmRepository;
	private static XMLModelRepository modelRepository;
	private static String baseDirName;

	public TestLibraryFromToXML(String name) {
		super(name);
	}

	private static final void dumpIndividual(IXMLIndividual<XSOntIndividual, XSOntProperty> indiv, String prefix) {

		System.out.println(prefix + "Indiv : " + indiv.getName() + "  ==> " + indiv.getUUID());

		for (XSOntProperty x : indiv.getAttributes()) {
			XSPropertyValue pv = ((XSOntIndividual) indiv).getPropertyValue(x);
			List<? extends Object> values = null;

			if (pv != null){
				values =pv.getValues();
			}

			if (x.isSimpleAttribute()) {
				System.out.print(prefix + "   Data attr : " + x.getName() );
				if (values != null){
					System.out.println("  =  " + values.toString());
				}
				else {
					System.out.println("");
				}
			} else {
				System.out.println(prefix + "   Object attr : " + x.getName() );
				if (values != null){
					for (Object o : values){
						XSOntIndividual child = (XSOntIndividual) o;
						dumpIndividual((IXMLIndividual<XSOntIndividual, XSOntProperty>) child, prefix + "      ");
					}
				}

			}
		}

		System.out.println(prefix + "--- Dumping Children");
		for (IXMLIndividual<XSOntIndividual, XSOntProperty> x : indiv.getChildren()) {
			if (x != indiv) {
				dumpIndividual(x, prefix + "     ");
			}
			else {
				logger.info("NON MAIS NON!!!! CELA NE DOIT PAS ARRIVER");
			}
		}

		System.out.println("");
		System.out.flush();
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
		xsdAdapter = testApplicationContext.getTechnologyAdapterService().getTechnologyAdapter(XSDTechnologyAdapter.class);
		mmRepository = (XSDMetaModelRepository) resourceCenter.getRepository(XSDMetaModelRepository.class, xsdAdapter);
		modelRepository = (XMLModelRepository) resourceCenter.getRepository(XMLModelRepository.class, xsdAdapter);
		baseDirName = ((DirectoryResourceCenter) resourceCenter).getDirectory().getCanonicalPath();
		assertNotNull(modelRepository);
		assertNotNull(mmRepository);
		assertEquals(3, mmRepository.getAllResources().size());
		assertTrue(modelRepository.getAllResources().size() > 2);
	}

	public void test0LibraryFromXML() throws ParserConfigurationException, TransformerException, FileNotFoundException,
	ResourceLoadingCancelledException, ResourceDependencyLoopException, FlexoException {

		log("test0LibraryFromXML()");

		assertNotNull(mmRepository);
		assertNotNull(modelRepository);

		XMLXSDFileResource libraryRes = modelRepository.getResource("file:" + baseDirName + "/XML/example_library_1.xml");

		XSDMetaModelResource mmLibraryRes = mmRepository.getResource("http://www.example.org/Library");

		XMLXSDModel mLib = libraryRes.getModel();

		libraryRes.setMetaModelResource(mmLibraryRes);
		mmLibraryRes.loadResourceData(null);
		libraryRes.loadResourceData(null);

		assertNotNull(mLib);
		assertTrue(mLib.getResource().isLoaded());

		dumpIndividual(mLib.getRoot(), "---");

	}

	public void test1LibraryToXML() throws ParserConfigurationException, TransformerException, FileNotFoundException,
	ResourceLoadingCancelledException, ResourceDependencyLoopException, FlexoException {

		log("test1LibraryToXML()");

		assertNotNull(mmRepository);
		assertNotNull(modelRepository);

		XSDMetaModelResource mmLibraryRes = mmRepository.getResource("http://www.example.org/Library");

		if (!mmLibraryRes.isLoaded()) {
			mmLibraryRes.loadResourceData(null);
		}
		XSDMetaModel mmLib = mmLibraryRes.getMetaModelData();

		assertNotNull(mmLib);
		assertTrue(mmLib.getResource().isLoaded());

		if (mmLib.getResource().isLoaded() == false) {
			if (logger.isLoggable(Level.WARNING)) {
				logger.warning("Failed to load.");
			}
		} else {

			XMLXSDFileResource libRes = (XMLXSDFileResource) xsdAdapter.createNewXMLFile((FileSystemBasedResourceCenter) resourceCenter,
					"GenXML", "library.xml", mmLibraryRes);

			XMLXSDModel lib = libRes.getModel();

			// TODO : this wont work anymore as we suppressed name's significance for XSOntIndividual
			XSOntIndividual library = lib.createOntologyIndividual(mmLib.getClass(LIBRARY_URI));
			lib.setRoot(library);
			library.addToPropertyValue(mmLib.getProperty(LIB_NAME_URI), "My Library");
			XSOntIndividual book1 = lib.createOntologyIndividual(mmLib.getClass(BOOK_URI));
			book1.addToPropertyValue(mmLib.getProperty(BOOK_TITLE_URI), "My First Book");
			XSOntIndividual book2 = lib.createOntologyIndividual(mmLib.getClass(BOOK_URI));
			book2.addToPropertyValue(mmLib.getProperty(BOOK_TITLE_URI), "My Second Book");
			library.addToPropertyValue(mmLib.getProperty(LIB_BOOKS_URI), book1);
			library.addToPropertyValue(mmLib.getProperty(LIB_BOOKS_URI), book2);

			try {
				lib.save();
			} catch (SaveResourceException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
