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
package org.openflexo.oe.controller;

import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.openflexo.FlexoCst;
import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.ontology.FlexoOntology;
import org.openflexo.foundation.ontology.ImportedOntology;
import org.openflexo.foundation.ontology.OntologyObject;
import org.openflexo.foundation.ontology.ProjectOntology;
import org.openflexo.icon.VEIconLibrary;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.oe.view.OntologyPerspectiveBrowserView;
import org.openflexo.oe.view.OntologyView;
import org.openflexo.view.EmptyPanel;
import org.openflexo.view.FlexoPerspective;
import org.openflexo.view.ModuleView;
import org.openflexo.view.controller.FlexoController;

public class OntologyPerspective extends FlexoPerspective<OntologyObject>
{

	private final OEController _controller;
	
	private final OntologyPerspectiveBrowserView _ontologyPerspectiveBrowserView;
	
	private final JLabel infoLabel;
	
	private static final JPanel EMPTY_RIGHT_VIEW = new JPanel();

	/**
	 * @param controller TODO
	 * @param name
	 */
	public OntologyPerspective(OEController controller)
	{
		super("ontology_perspective");
		_controller = controller;
		_ontologyPerspectiveBrowserView = new OntologyPerspectiveBrowserView(controller);
		
		infoLabel = new JLabel("Info label");
		infoLabel.setFont(FlexoCst.SMALL_FONT);
	}

	/**
	 * Overrides getIcon
	 *
	 * @see org.openflexo.view.FlexoPerspective#getActiveIcon()
	 */
	@Override
	public ImageIcon getActiveIcon()
	{
		return VEIconLibrary.VE_OP_ACTIVE_ICON;
	}

	/**
	 * Overrides getSelectedIcon
	 *
	 * @see org.openflexo.view.FlexoPerspective#getSelectedIcon()
	 */
	@Override
	public ImageIcon getSelectedIcon()
	{
		return VEIconLibrary.VE_OP_SELECTED_ICON;
	}

	@Override
	public FlexoOntology getDefaultObject(FlexoModelObject proposedObject) 
	{
		if (proposedObject instanceof FlexoOntology) {
			return (FlexoOntology)proposedObject;
		}
		return _controller.getProject().getProjectOntology();
	}

	@Override
	public boolean hasModuleViewForObject(FlexoModelObject object)
	{
		return (object instanceof FlexoOntology);
	}


	@Override
	public ModuleView<? extends OntologyObject> createModuleViewForObject(OntologyObject object, FlexoController controller)
	{
		if (object instanceof FlexoOntology) {
			((FlexoOntology)object).loadWhenUnloaded();
			return new OntologyView((FlexoOntology)object,(OEController)controller,this);
		}
		return new EmptyPanel<OntologyObject>(controller,this,object);
	}

	@Override
	public boolean doesPerspectiveControlLeftView() 
	{
		return true;
	}
	
	@Override
	public JComponent getLeftView() 
	{
		return _ontologyPerspectiveBrowserView;
	}

	@Override
	public JComponent getFooter() 
	{
		return infoLabel;
	}


	@Override
	public boolean doesPerspectiveControlRightView() 
	{
		return true;
	}

	@Override
	public JComponent getRightView() 
	{
		return EMPTY_RIGHT_VIEW;
	}
	
	@Override
	public boolean isAlwaysVisible() 
	{
		return true;
	}

	public String getWindowTitleforObject(FlexoModelObject object) 
	{
		if (object instanceof ProjectOntology) {
			return FlexoLocalization.localizedForKey("project_ontology");
		}
		if (object instanceof ImportedOntology) {
			return ((ImportedOntology)object).getName();
		}
		return object.getFullyQualifiedName();
	}
	

}