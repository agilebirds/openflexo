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
package org.openflexo.technologyadapter.owl.gui;

import java.util.logging.Logger;

import javax.swing.SwingUtilities;

import org.openflexo.components.widget.FIBPropertySelector;
import org.openflexo.technologyadapter.owl.model.OWLOntology;
import org.openflexo.technologyadapter.owl.model.OWLProperty;

/**
 * Widget allowing to select an {@link OWLProperty}.<br>
 * 
 * @see FIBPropertySelector
 * 
 * @author sguerin
 * 
 */
public class FIBOWLPropertySelector extends FIBPropertySelector {
	@SuppressWarnings("hiding")
	static final Logger logger = Logger.getLogger(FIBOWLPropertySelector.class.getPackage().getName());

	private boolean showOWLAndRDFConcepts = false;

	public FIBOWLPropertySelector(OWLProperty editedObject) {
		super(editedObject);
	}

	@Override
	public OWLOntology getContext() {
		return (OWLOntology) super.getContext();
	}

	public boolean getShowOWLAndRDFConcepts() {
		return showOWLAndRDFConcepts;
	}

	@CustomComponentParameter(name = "showOWLAndRDFConcepts", type = CustomComponentParameter.Type.OPTIONAL)
	public void setShowOWLAndRDFConcepts(boolean showOWLAndRDFConcepts) {
		this.showOWLAndRDFConcepts = showOWLAndRDFConcepts;
		update();
	}

	@Override
	public OWLOntologyBrowserModel getModel() {
		if (model == null) {
			model = new OWLOntologyBrowserModel(getContext()) {
				@Override
				public void recomputeStructure() {
					super.recomputeStructure();
					SwingUtilities.invokeLater(new Runnable() {
						@Override
						public void run() {
							getPropertyChangeSupport().firePropertyChange("model", null, getModel());
						}
					});
				}
			};
			model.setStrictMode(getStrictMode());
			model.setHierarchicalMode(getHierarchicalMode());
			model.setDisplayPropertiesInClasses(getDisplayPropertiesInClasses());
			model.setRootClass(getRootClass());
			model.setDomain(getDomain());
			model.setRange(getRange());
			model.setDataType(getDataType());
			model.setShowClasses(false);
			model.setShowIndividuals(false);
			model.setShowObjectProperties(getSelectObjectProperties());
			model.setShowDataProperties(getSelectDataProperties());
			model.setShowAnnotationProperties(getSelectAnnotationProperties());
			((OWLOntologyBrowserModel) model).setShowOWLAndRDFConcepts(getShowOWLAndRDFConcepts());
			/*System.out.println("Recomputing...");
			System.out.println("context=" + getContext());
			System.out.println("getStrictMode()=" + getStrictMode());
			System.out.println("getHierarchicalMode()=" + getHierarchicalMode());
			System.out.println("getDisplayPropertiesInClasses()=" + getDisplayPropertiesInClasses());
			System.out.println("getRootClass()=" + getRootClass());
			System.out.println("getDomain()=" + getDomain());
			System.out.println("getRange()=" + getRange());
			System.out.println("getDataType()=" + getDataType());
			System.out.println("getSelectObjectProperties()=" + getSelectObjectProperties());
			System.out.println("getSelectDataProperties()=" + getSelectDataProperties());
			System.out.println("getSelectAnnotationProperties()=" + getSelectAnnotationProperties());
			System.out.println("getShowOWLAndRDFConcepts()=" + getShowOWLAndRDFConcepts());
			model.recomputeStructure();*/
		}
		return (OWLOntologyBrowserModel) model;
	}

	// Please uncomment this for a live test
	// Never commit this uncommented since it will not compile on continuous build
	// To have icon, you need to choose "Test interface" in the editor (otherwise, flexo controller is not insanciated in EDIT mode)
	/*public static void main(String[] args) {
		FIBAbstractEditor editor = new FIBAbstractEditor() {
			@Override
			public Object[] getData() {
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
				// "http://www.w3.org/2002/07/owl");
						"http://www.thalesgroup.com/ontologies/sepel-ng/SEPELOutputModel1.owl");
				// "http://www.openflexo.org/test/TestProperties.owl");
				// "http://www.w3.org/2000/01/rdf-schema");
				o.loadWhenUnloaded();

				FIBOWLPropertySelector selector = new FIBOWLPropertySelector(null);

				selector.setContext(o);
				selector.setHierarchicalMode(true); // false
				selector.setSelectAnnotationProperties(true);
				selector.setSelectObjectProperties(true);
				selector.setSelectDataProperties(true);
				selector.setStrictMode(true);
				selector.setDomainClassURI("http://www.thalesgroup.com/ontologies/sepel-ng/SEPELOutputModel1.owl#RootClassForOutputModel1");
				// selector.setRangeClassURI("http://www.thalesgroup.com/ontologies/sepel-ng/SEPELOutputModel1.owl#EmSubMode");
				return makeArray(selector);
			}

			@Override
			public File getFIBFile() {
				return FIB_FILE;
			}

			@Override
			public FIBController makeNewController(FIBComponent component) {
				return new FlexoFIBController<FIBViewPointSelector>(component);
			}
		};
		editor.launch();
	}*/
}
