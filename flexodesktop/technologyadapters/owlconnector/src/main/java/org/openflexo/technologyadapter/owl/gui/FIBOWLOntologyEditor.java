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

import org.openflexo.components.widget.FIBOntologyEditor;
import org.openflexo.fib.model.FIBCustom.FIBCustomComponent.CustomComponentParameter;
import org.openflexo.technologyadapter.owl.model.OWLOntology;
import org.openflexo.view.controller.FlexoController;

/**
 * Widget allowing to edit/view an {@link OWLOntology}.<br>
 * 
 * @author sguerin
 * 
 */
public class FIBOWLOntologyEditor extends FIBOntologyEditor {
	static final Logger logger = Logger.getLogger(FIBOWLOntologyEditor.class.getPackage().getName());

	private boolean showOWLAndRDFConcepts = false;

	public FIBOWLOntologyEditor(OWLOntology ontology, FlexoController controller) {
		super(null, controller);
	}

	@Override
	public OWLOntology getOntology() {
		return (OWLOntology) super.getOntology();
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
			model = new OWLOntologyBrowserModel(getOntology()) {
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
			model.setShowClasses(getShowClasses());
			model.setShowIndividuals(getShowIndividuals());
			model.setShowObjectProperties(getShowObjectProperties());
			model.setShowDataProperties(getShowDataProperties());
			model.setShowAnnotationProperties(getShowAnnotationProperties());
			((OWLOntologyBrowserModel) model).setShowOWLAndRDFConcepts(showOWLAndRDFConcepts);
			model.recomputeStructure();
		}
		return (OWLOntologyBrowserModel) model;
	}

}
