package org.openflexo.components.widget;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;

import org.openflexo.fib.controller.FIBController;
import org.openflexo.fib.editor.FIBAbstractEditor;
import org.openflexo.fib.model.FIBComponent;
import org.openflexo.foundation.ontology.FlexoOntology;
import org.openflexo.foundation.resource.FlexoResourceCenter;
import org.openflexo.foundation.resource.LocalResourceCenterImplementation;
import org.openflexo.logging.FlexoLoggingManager;
import org.openflexo.toolbox.FileResource;
import org.openflexo.view.controller.FlexoFIBController;

public class FIBOntologyBrowserEDITOR extends FIBAbstractEditor {

	private static FIBOntologyBrowser selector;

	@Override
	public Object[] getData() {
		return makeArray(selector);
	}

	@Override
	public File getFIBFile() {
		return FIBOntologyBrowser.FIB_FILE;
	}

	@Override
	public FIBController makeNewController(FIBComponent component) {
		return new FlexoFIBController(component);
	}

	// To have icon, you need to choose "Test interface" in the editor (otherwise, flexo controller is not insanciated in EDIT mode)
	public static void main(String[] args) {

		try {
			FlexoLoggingManager.initialize(-1, true, null, Level.INFO, null);
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		FlexoResourceCenter testResourceCenter = LocalResourceCenterImplementation
				.instanciateTestLocalResourceCenterImplementation(new FileResource("TestResourceCenter"));
		// selector.setContext(resourceCenter.retrieveBaseOntologyLibrary().getFlexoConceptOntology());
		FlexoOntology o = testResourceCenter.retrieveBaseOntologyLibrary().getOntology(
		// "http://www.thalesgroup.com/ontologies/sepel-ng/MappingSpecifications.owl");
		// "http://www.cpmf.org/ontologies/cpmfInstance");
		// "http://www.agilebirds.com/openflexo/ontologies/FlexoConceptsOntology.owl");
				"http://www.w3.org/2002/07/owl");
		// "http://www.w3.org/2000/01/rdf-schema");
		o.loadWhenUnloaded();
		selector = new FIBOntologyBrowser(o);
		selector.setOntology(o);
		selector.setHierarchicalMode(false); // false
		selector.setShowAnnotationProperties(true);
		selector.setShowObjectProperties(true);
		selector.setShowDataProperties(true);
		selector.setShowClasses(true);
		selector.setShowIndividuals(true);
		selector.setStrictMode(false);
		main(FIBOntologyBrowserEDITOR.class);
	}

	/*public static void main2(String[] args) {
		JFrame frame = new JFrame();
		FlexoResourceCenter resourceCenter = FlexoResourceCenterService.instance().getFlexoResourceCenter();
		// selector.setContext(resourceCenter.retrieveBaseOntologyLibrary().getFlexoConceptOntology());
		FlexoOntology o = resourceCenter.retrieveBaseOntologyLibrary().getOntology(
		// "http://www.thalesgroup.com/ontologies/sepel-ng/MappingSpecifications.owl");
				"http://www.w3.org/2002/07/owl");
		// "http://www.cpmf.org/ontologies/cpmfInstance");
		o.loadWhenUnloaded();
		System.out.println("ontology: " + o);
		FIBOntologyBrowser browser = new FIBOntologyBrowser(o);
		browser.setOntology(o);
		frame.getContentPane().add(browser);
		frame.pack();
		frame.setVisible(true);
	}*/

}
