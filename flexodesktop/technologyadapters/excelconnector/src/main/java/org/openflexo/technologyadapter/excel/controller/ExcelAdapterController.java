package org.openflexo.technologyadapter.excel.controller;

import java.util.logging.Logger;

import javax.swing.ImageIcon;

import openflexo.technologyadapter.excel.ExcelTechnologyAdapter;

import org.openflexo.components.widget.OntologyBrowserModel;
import org.openflexo.foundation.ontology.IFlexoOntology;
import org.openflexo.foundation.ontology.IFlexoOntologyObject;
import org.openflexo.foundation.ontology.IFlexoOntologyPropertyValue;
import org.openflexo.foundation.viewpoint.EditionAction;
import org.openflexo.foundation.viewpoint.PatternRole;
import org.openflexo.icon.IconFactory;
import org.openflexo.icon.IconLibrary;
import org.openflexo.technologyadapter.excel.model.semantics.ExcelMetaModel;
import org.openflexo.technologyadapter.excel.model.semantics.ExcelModel;
import org.openflexo.technologyadapter.excel.viewpoint.ExcelCellPatternRole;
import org.openflexo.technologyadapter.excel.viewpoint.ExcelRowPatternRole;
import org.openflexo.technologyadapter.excel.viewpoint.ExcelSheetPatternRole;
import org.openflexo.technologyadapter.excel.viewpoint.ExcelWorkbookPatternRole;
import org.openflexo.toolbox.FileResource;
import org.openflexo.view.controller.ControllerActionInitializer;
import org.openflexo.view.controller.TechnologyAdapterController;

public class ExcelAdapterController extends TechnologyAdapterController<ExcelTechnologyAdapter> {

	@Override
	public Class<ExcelTechnologyAdapter> getTechnologyAdapterClass() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void initializeActions(ControllerActionInitializer actionInitializer) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public ImageIcon getTechnologyBigIcon() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ImageIcon getTechnologyIcon() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ImageIcon getModelIcon() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ImageIcon getMetaModelIcon() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ImageIcon getIconForOntologyObject(
			Class<? extends IFlexoOntologyObject> objectClass) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ImageIcon getIconForPropertyValue(
			Class<? extends IFlexoOntologyPropertyValue> objectClass) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ImageIcon getIconForPatternRole(
			Class<? extends PatternRole> patternRoleClass) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public OntologyBrowserModel makeOntologyBrowserModel(IFlexoOntology context) {
		// TODO Auto-generated method stub
		return null;
	}

	

}
