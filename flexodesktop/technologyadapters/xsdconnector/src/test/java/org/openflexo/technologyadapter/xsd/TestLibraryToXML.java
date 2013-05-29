package org.openflexo.technologyadapter.xsd;

import java.io.FileNotFoundException;
import java.util.logging.Level;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.openflexo.foundation.FlexoTestCase;
import org.openflexo.foundation.ontology.DuplicateURIException;
import org.openflexo.technologyadapter.xsd.model.XSDMetaModel;
import org.openflexo.technologyadapter.xsd.model.XSOntIndividual;
import org.openflexo.technologyadapter.xsd.model.XSOntology;

public class TestLibraryToXML extends FlexoTestCase {

	private static final java.util.logging.Logger logger = org.openflexo.logging.FlexoLogger.getLogger(TestLibraryToXML.class.getPackage()
			.getName());

	private static final String FILE_NAME = "library";
	private static final String LIBRARY_URI = "http://www.example.org/library#Library";
	private static final String BOOK_URI = "http://www.example.org/library#Book";
	private static final String BOOK_TITLE_URI = "http://www.example.org/library/Book#title";

	public void testLibraryToXML() throws DuplicateURIException, ParserConfigurationException, TransformerException {
		// StringBuffer buffer = new StringBuffer();
		XSOntology lib = null;
		try {
			lib = new XSDMetaModel("http://www.openflexo.org/test/XSD/library.owl", TestLibrary.openTestXSD(FILE_NAME), null);
		} catch (FileNotFoundException e) {
			fail(e.getMessage());
		}
		assertNotNull(lib);
		lib.loadWhenUnloaded();
		assertTrue(lib.isLoaded());
		if (lib.isLoaded() == false) {
			if (logger.isLoggable(Level.WARNING)) {
				logger.warning("Failed to load.");
			}
		} else {
			// TODO : this wont work anymore as we suppressed name's significance for XSOntIndividual
			XSOntIndividual library = lib.createOntologyIndividual( lib.getClass(LIBRARY_URI));
			XSOntIndividual book1 = lib.createOntologyIndividual(lib.getClass(BOOK_URI));
			book1.addToPropertyValue(lib.getProperty(BOOK_TITLE_URI), "My First Book");
			XSOntIndividual book2 = lib.createOntologyIndividual( lib.getClass(BOOK_URI));
			book2.addToPropertyValue(lib.getProperty(BOOK_TITLE_URI), "My Second Book");
			library.addChild(book1);
			library.addChild(book2);

			if (logger.isLoggable(Level.INFO)) {
				TransformerFactory transformerFactory = TransformerFactory.newInstance(
						"com.sun.org.apache.xalan.internal.xsltc.trax.TransformerFactoryImpl", null);
				System.out.println(transformerFactory.getClass().getName());
				System.out.println(transformerFactory.getClass().getClassLoader());
				Transformer transformer = transformerFactory.newTransformer();
				DOMSource source = new DOMSource(lib.toXML());
				StreamResult result = new StreamResult(System.out);
				transformer.transform(source, result);
				// logger.info(source.toString());
			}
		}
	}
}
