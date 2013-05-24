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
package org.openflexo.ve.controller;

import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JLabel;

import org.openflexo.FlexoCst;
import org.openflexo.components.widget.FIBOntologyLibraryBrowser;
import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.ontology.FlexoOntology;
import org.openflexo.foundation.ontology.ImportedOWLOntology;
import org.openflexo.foundation.ontology.ProjectOntology;
import org.openflexo.foundation.ontology.owl.OWLOntology;
import org.openflexo.foundation.rm.FlexoProject;
import org.openflexo.icon.VEIconLibrary;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.ve.view.OntologyView;
import org.openflexo.view.ModuleView;
import org.openflexo.view.controller.FlexoController;
import org.openflexo.view.controller.model.FlexoPerspective;

public class OntologyPerspective extends FlexoPerspective {

	private final VEController _controller;

	private final FIBOntologyLibraryBrowser ontologyLibraryBrowser;

	private final JLabel infoLabel;

	/**
	 * @param controller
	 *            TODO
	 * @param name
	 */
	public OntologyPerspective(VEController controller) {
		super("ontology_perspective");
		_controller = controller;
		ontologyLibraryBrowser = new FIBOntologyLibraryBrowser(null, controller);
		infoLabel = new JLabel("Ontology perspective");
		infoLabel.setFont(FlexoCst.SMALL_FONT);
		setTopLeftView(ontologyLibraryBrowser);
	}

	/**
	 * Overrides getIcon
	 * 
	 * @see org.openflexo.view.controller.model.FlexoPerspective#getActiveIcon()
	 */
	@Override
	public ImageIcon getActiveIcon() {
		return VEIconLibrary.VE_OP_ACTIVE_ICON;
	}

	@Override
	public FlexoModelObject getDefaultObject(FlexoModelObject proposedObject) {
		if (proposedObject instanceof FlexoOntology) {
			return proposedObject;
		}
		return (FlexoModelObject) _controller.getProject().getProjectOntology();
	}

	@Override
	public boolean hasModuleViewForObject(FlexoModelObject object) {
		return object instanceof FlexoOntology;
	}

	@Override
	public ModuleView<?> createModuleViewForObject(FlexoModelObject object, FlexoController controller) {
		if (object instanceof OWLOntology) {
			((OWLOntology) object).loadWhenUnloaded();
			return new OntologyView((OWLOntology) object, (VEController) controller, this);
		}
		return null;
	}

	@Override
	public JComponent getFooter() {
		return infoLabel;
	}

	public String getWindowTitleforObject(FlexoModelObject object) {
		if (object instanceof ProjectOntology) {
			return FlexoLocalization.localizedForKey("project_ontology");
		}
		if (object instanceof ImportedOWLOntology) {
			return ((ImportedOWLOntology) object).getName();
		}
		return object.getFullyQualifiedName();
	}

	public void setProject(FlexoProject project) {
		if (project != null) {
			ontologyLibraryBrowser.setDataObject(project.getProjectOntologyLibrary());
		} else {
			ontologyLibraryBrowser.setDataObject(null);
		}

	}

}
