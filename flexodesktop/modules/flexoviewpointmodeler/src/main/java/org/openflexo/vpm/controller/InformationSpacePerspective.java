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
package org.openflexo.vpm.controller;

import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JLabel;

import org.openflexo.FlexoCst;
import org.openflexo.components.widget.FIBInformationSpaceBrowser;
import org.openflexo.foundation.FlexoObject;
import org.openflexo.foundation.ontology.IFlexoOntology;
import org.openflexo.icon.VPMIconLibrary;
import org.openflexo.view.EmptyPanel;
import org.openflexo.view.ModuleView;
import org.openflexo.view.controller.FlexoController;
import org.openflexo.view.controller.model.FlexoPerspective;
import org.openflexo.vpm.view.OntologyView;

public class InformationSpacePerspective extends FlexoPerspective {

	private final VPMController _controller;

	// private final FIBOntologyLibraryBrowser ontologyLibraryBrowser;

	private final JLabel infoLabel;

	private FIBInformationSpaceBrowser informationSpaceBrowser;

	/**
	 * @param controller
	 * @param name
	 */
	public InformationSpacePerspective(VPMController controller) {
		super("information_space_perspective");
		_controller = controller;

		informationSpaceBrowser = new FIBInformationSpaceBrowser(controller.getApplicationContext(), controller);

		/*_browser = new CalcLibraryBrowser(controller);
		_browserView = new CEDBrowserView(_browser, _controller, SelectionPolicy.ParticipateToSelection) {
			@Override
			public void treeDoubleClick(FlexoModelObject object) {
				super.treeDoubleClick(object);
				if (object instanceof ViewPoint) {
					focusOnViewPoint((ViewPoint) object);
					// System.out.println(((OntologyCalc)object).getXMLRepresentation());
				}
			}
		};*/
		setTopLeftView(informationSpaceBrowser);

		// ontologyLibraryBrowser = new FIBOntologyLibraryBrowser(controller.getBaseOntologyLibrary(), controller);
		infoLabel = new JLabel("Information space perspective");
		infoLabel.setFont(FlexoCst.SMALL_FONT);
		// setTopLeftView(ontologyLibraryBrowser);
	}

	/**
	 * Overrides getIcon
	 * 
	 * @see org.openflexo.view.controller.model.FlexoPerspective#getActiveIcon()
	 */
	@Override
	public ImageIcon getActiveIcon() {
		return VPMIconLibrary.INFORMATION_SPACE_ICON;
	}

	/**
	 * Overrides getSelectedIcon
	 * 
	 * @see org.openflexo.view.controller.model.FlexoPerspective#getSelectedIcon()
	 */
	@Override
	public ImageIcon getSelectedIcon() {
		return VPMIconLibrary.INFORMATION_SPACE_ICON;
	}

	@Override
	public FlexoObject getDefaultObject(FlexoObject proposedObject) {
		if (hasModuleViewForObject(proposedObject)) {
			return proposedObject;
		}
		// return _controller.getBaseOntologyLibrary();
		return null;
	}

	@Override
	public boolean hasModuleViewForObject(FlexoObject object) {
		return object instanceof IFlexoOntology /*|| object == _controller.getBaseOntologyLibrary()*/;
	}

	@Override
	public ModuleView<? extends FlexoObject> createModuleViewForObject(FlexoObject object, FlexoController controller) {
		if (object instanceof IFlexoOntology) {
			// ((IFlexoOntology) object).loadWhenUnloaded();
			return new OntologyView((IFlexoOntology) object, (VPMController) controller, this);
		}
		return new EmptyPanel<FlexoObject>(controller, this, object);
	}

	@Override
	public JComponent getFooter() {
		return infoLabel;
	}

	public String getWindowTitleforObject(FlexoObject object) {
		/*if (object instanceof OntologyLibrary) {
			return FlexoLocalization.localizedForKey("ontology_library");
		}
		if (object instanceof OWLMetaModel) {
			return ((OWLMetaModel) object).getName();
		}*/
		return object.getFullyQualifiedName();
	}

}
