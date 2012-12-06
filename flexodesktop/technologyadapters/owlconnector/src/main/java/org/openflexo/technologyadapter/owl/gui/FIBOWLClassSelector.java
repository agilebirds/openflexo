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

import org.openflexo.components.widget.FIBClassSelector;
import org.openflexo.technologyadapter.owl.model.OWLClass;
import org.openflexo.technologyadapter.owl.model.OWLOntology;

/**
 * Widget allowing to select an {@link OWLClass}<br>
 * 
 * @see FIBClassSelector
 * 
 * @author sguerin
 * 
 */
@SuppressWarnings("serial")
public class FIBOWLClassSelector extends FIBClassSelector {
	static final Logger logger = Logger.getLogger(FIBOWLClassSelector.class.getPackage().getName());

	private boolean showOWLAndRDFConcepts = false;

	public FIBOWLClassSelector(OWLClass editedObject) {
		super(editedObject);
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
	public OWLOntology getContext() {
		return (OWLOntology) super.getContext();
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
			model.setDisplayPropertiesInClasses(false);
			model.setRootClass(getRootClass());
			model.setShowClasses(true);
			model.setShowIndividuals(false);
			model.setShowObjectProperties(false);
			model.setShowDataProperties(false);
			model.setShowAnnotationProperties(false);
			((OWLOntologyBrowserModel) model).setShowOWLAndRDFConcepts(showOWLAndRDFConcepts);
			model.recomputeStructure();
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
						"http://www.agilebirds.com/openflexo/ontologies/UML/UML2.owl");
				// "http://www.w3.org/2000/01/rdf-schema");
				o.loadWhenUnloaded();

				FIBOWLClassSelector selector = new FIBOWLClassSelector(null);

				selector.setContext(o);
				selector.setHierarchicalMode(true); // false
				selector.setStrictMode(true);
				return makeArray(selector);
			}

			@Override
			public File getFIBFile() {
				return FIB_FILE;
			}

			@Override
			public FIBController<?> makeNewController(FIBComponent component) {
				return new FlexoFIBController<FIBViewPointSelector>(component);
			}
		};
		editor.launch();
	}*/

}
