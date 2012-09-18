package org.openflexo.foundation.ontology.xsd;

import java.io.File;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;

import org.openflexo.foundation.FlexoTestCase;
import org.openflexo.foundation.ontology.xsd.rm.ImportedXSOntology;

public class TestLibrary extends FlexoTestCase {

	private static final String FILE_NAME = "library";

	private static final java.util.logging.Logger logger = org.openflexo.logging.FlexoLogger.getLogger(TestLibrary.class.getPackage()
			.getName());

	public static File openTestXSD(String filename) {
		// TODO Use resource manager?
		// tip to do it, look at: LocalResourceCenterImpl.findOntologies
		File result = null;
		result = new File("src/test/resources/XSD/" + filename + ".xsd");
		if (result.exists() == false) {
			if (logger.isLoggable(Level.WARNING)) {
				logger.warning("Failed to load file " + filename + ".xsd");
			}
		}
		return result;
	}

	public static void xsoObject(XSOntObject obj, StringBuffer buffer) {
		buffer.append("Name: ").append(obj.getName());
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
		for (XSOntDataProperty xsoDP : lib.getDataProperties()) {
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

	public static void attributeRestrictionListing(XSOntology lib, StringBuffer buffer) {
		buffer.append("Attribute restrictions\n");
		for (XSOntClass xsoClass : lib.getClasses()) {
			Set<XSOntAttributeRestriction> attributeRestrictions = new HashSet<XSOntAttributeRestriction>();
			for (XSOntClass superClass : xsoClass.getSuperClasses()) {
				if (superClass instanceof XSOntAttributeRestriction) {
					attributeRestrictions.add((XSOntAttributeRestriction) superClass);
				}
			}
			if (attributeRestrictions.isEmpty() == false) {
				xsoObject(xsoClass, buffer);
				for (XSOntAttributeRestriction restriction : attributeRestrictions) {
					buffer.append("    ").append(restriction.getDisplayableDescription()).append("\n");
				}
			}
		}
	}

	public void testLibrary() {
		StringBuffer buffer = new StringBuffer();
		XSOntology lib = new ImportedXSOntology("http://www.openflexo.org/test/XSD/library.owl", openTestXSD(FILE_NAME), null);
		assertNotNull(lib);
		lib.loadWhenUnloaded();
		assertTrue(lib.isLoaded());
		if (lib.isLoaded() == false) {
			if (logger.isLoggable(Level.WARNING)) {
				logger.warning("Failed to load.");
			}
		} else {
			generalInfos(lib, buffer);
			classListing(lib, buffer);
			dataPropertyListing(lib, buffer);
			objectPropertyListing(lib, buffer);
			attributeRestrictionListing(lib, buffer);

			if (logger.isLoggable(Level.INFO)) {
				logger.info(buffer.toString());
			}
		}
	}

}
