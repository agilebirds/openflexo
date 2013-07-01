package org.openflexo.technologyadapter.xsd;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.openflexo.ApplicationContext;
import org.openflexo.TestApplicationContext;
import org.openflexo.foundation.FlexoException;
import org.openflexo.foundation.FlexoTestCase;
import org.openflexo.foundation.dkv.TestPopulateDKV;
import org.openflexo.foundation.ontology.DuplicateURIException;
import org.openflexo.foundation.ontology.IFlexoOntologyFeatureAssociation;
import org.openflexo.foundation.resource.DirectoryResourceCenter;
import org.openflexo.foundation.resource.FileSystemBasedResourceCenter;
import org.openflexo.foundation.resource.FlexoResource;
import org.openflexo.foundation.resource.FlexoResourceCenter;
import org.openflexo.foundation.resource.ResourceLoadingCancelledException;
import org.openflexo.foundation.rm.ResourceDependencyLoopException;
import org.openflexo.foundation.rm.SaveResourceException;
import org.openflexo.foundation.technologyadapter.MetaModelRepository;
import org.openflexo.foundation.technologyadapter.ModelRepository;
import org.openflexo.foundation.technologyadapter.TechnologyContextManager;
import org.openflexo.technologyadapter.xml.model.IXMLIndividual;
import org.openflexo.technologyadapter.xsd.metamodel.XSDMetaModel;
import org.openflexo.technologyadapter.xsd.metamodel.XSOntProperty;
import org.openflexo.technologyadapter.xsd.model.XMLXSDModel;
import org.openflexo.technologyadapter.xsd.model.XSOntIndividual;
import org.openflexo.technologyadapter.xsd.model.XSOntology;
import org.openflexo.technologyadapter.xsd.model.XSPropertyValue;
import org.openflexo.technologyadapter.xsd.rm.XMLXSDFileResource;
import org.openflexo.technologyadapter.xsd.rm.XSDMetaModelResource;
import org.openflexo.technologyadapter.xsd.rm.XSDMetaModelResourceImpl;
import org.openflexo.toolbox.FileResource;

public class TestLibraryFromToXML extends FlexoTestCase {

	private static final java.util.logging.Logger logger = org.openflexo.logging.FlexoLogger.getLogger(TestLibraryFromToXML.class.getPackage()
			.getName());

	private static final String FILE_NAME = "library";
	private static final String LIBRARY_URI = "http://www.example.org/Library#Library";
	private static final String BOOK_URI = "http://www.example.org/Library#Book";
	private static final String BOOK_TITLE_URI = "http://www.example.org/Library/Book#title";


	private static ApplicationContext testApplicationContext;
	private static XSDTechnologyAdapter xsdAdapter;
	private static FlexoResourceCenter resourceCenter;
	private static ModelRepository<FlexoResource<XMLXSDModel>, XMLXSDModel, XSDMetaModel, XSDTechnologyAdapter> modelRepository;
	private static MetaModelRepository<FlexoResource<XSDMetaModel>, XMLXSDModel, XSDMetaModel, XSDTechnologyAdapter> metamodelRepository;
	private static String baseDirName;


	private static final void dumpIndividual(IXMLIndividual<XSOntIndividual,XSOntProperty> indiv, String prefix){

		System.out.println(prefix + "Indiv : " + indiv.getName() + "  ==> " + indiv.getUUID());

		for (XSOntProperty x: indiv.getAttributes()){
			System.out.print (prefix + "   attr : " + x.getName() +" [ " + x.isSimpleAttribute() + "]" );
			if (x.isSimpleAttribute())
				System.out.println("  =  " +  ((XSOntIndividual) indiv).getPropertyValue(x).getValues().toString());
			else 
				System.out.println();
		}

		System.out.println("");
		for (IXMLIndividual<XSOntIndividual,XSOntProperty> x: indiv.getChildren()) dumpIndividual(x,prefix +"     ");

		System.out.println("");
		System.out.flush();
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
		xsdAdapter = testApplicationContext.getTechnologyAdapterService().getTechnologyAdapter(XSDTechnologyAdapter.class);
		modelRepository = resourceCenter.getModelRepository(xsdAdapter);
		metamodelRepository = resourceCenter.getMetaModelRepository(xsdAdapter);
		baseDirName=((DirectoryResourceCenter)resourceCenter).getDirectory().getCanonicalPath();
		assertNotNull(modelRepository);
		assertNotNull(metamodelRepository);
		assertEquals(3, metamodelRepository.getAllResources().size());
		assertTrue(modelRepository.getAllResources().size()>2);
	}

	public void test0LibraryFromXML() throws ParserConfigurationException, TransformerException, FileNotFoundException, ResourceLoadingCancelledException, ResourceDependencyLoopException, FlexoException {

		log("test0LibraryFromXML()");

		assertNotNull(metamodelRepository);
		assertNotNull(modelRepository);

		XMLXSDFileResource libraryRes = (XMLXSDFileResource) modelRepository.getResource("file:" + baseDirName + "/XML/example_library_1.xml");

		XSDMetaModelResource mmLibraryRes = (XSDMetaModelResource) metamodelRepository.getResource("http://www.example.org/Library");

		XMLXSDModel mLib = libraryRes.getModel();

		libraryRes.setMetaModelResource(mmLibraryRes);
		mmLibraryRes.loadResourceData(null);
		libraryRes.loadResourceData(null);

		assertNotNull(mLib);
		assertTrue(((XMLXSDModel) mLib).getResource().isLoaded());

		dumpIndividual(mLib.getRoot(), "---");


	}

	public void test1LibraryToXML() throws ParserConfigurationException, TransformerException, FileNotFoundException, ResourceLoadingCancelledException, ResourceDependencyLoopException, FlexoException {

		log("test1LibraryToXML()");

		assertNotNull(metamodelRepository);
		assertNotNull(modelRepository);

		XSDMetaModelResource mmLibraryRes = (XSDMetaModelResource) metamodelRepository.getResource("http://www.example.org/Library");

		if (!mmLibraryRes.isLoaded()){
			mmLibraryRes.loadResourceData(null);
		}
		XSDMetaModel mmLib = mmLibraryRes.getMetaModelData();

		assertNotNull(mmLib);
		assertTrue(((XSDMetaModel) mmLib).getResource().isLoaded());

		if (((XSDMetaModel) mmLib).getResource().isLoaded() == false) {
			if (logger.isLoggable(Level.WARNING)) {
				logger.warning("Failed to load.");
			}
		} else {


			XMLXSDFileResource libRes = (XMLXSDFileResource) xsdAdapter.createEmptyModel((FileSystemBasedResourceCenter) resourceCenter, "GenXML", "library.xml", mmLibraryRes, xsdAdapter.getTechnologyContextManager());

			XMLXSDModel lib = (XMLXSDModel) libRes.getModel();

			// TODO : this wont work anymore as we suppressed name's significance for XSOntIndividual
			XSOntIndividual library = lib.createOntologyIndividual( mmLib.getClass(LIBRARY_URI));
			lib.setRoot(library);
			XSOntIndividual book1 = lib.createOntologyIndividual(mmLib.getClass(BOOK_URI));
			book1.addToPropertyValue(lib.getProperty(BOOK_TITLE_URI), "My First Book");
			XSOntIndividual book2 = lib.createOntologyIndividual( mmLib.getClass(BOOK_URI));
			book2.addToPropertyValue(lib.getProperty(BOOK_TITLE_URI), "My Second Book");
			library.addChild(book1);
			library.addChild(book2);

			try {
				lib.save();
			} catch (SaveResourceException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
