package org.openflexo.technologyadapter.excel.controller;

import javax.swing.ImageIcon;

import org.openflexo.components.widget.OntologyBrowserModel;
import org.openflexo.foundation.ontology.IFlexoOntology;
import org.openflexo.foundation.technologyadapter.TechnologyObject;
import org.openflexo.foundation.viewpoint.PatternRole;
import org.openflexo.technologyadapter.excel.ExcelTechnologyAdapter;
import org.openflexo.technologyadapter.excel.gui.ExcelIconLibrary;
import org.openflexo.view.controller.ControllerActionInitializer;
import org.openflexo.view.controller.TechnologyAdapterController;

public class ExcelAdapterController extends TechnologyAdapterController<ExcelTechnologyAdapter> {

	@Override
	public Class<ExcelTechnologyAdapter> getTechnologyAdapterClass() {
		return ExcelTechnologyAdapter.class;
	}

	@Override
	public void initializeActions(ControllerActionInitializer actionInitializer) {
		// TODO Auto-generated method stub

	}

	@Override
	public ImageIcon getTechnologyBigIcon() {
		return ExcelIconLibrary.EXCEL_TECHNOLOGY_BIG_ICON;
	}

	@Override
	public ImageIcon getTechnologyIcon() {
		return ExcelIconLibrary.EXCEL_TECHNOLOGY_ICON;
	}

	@Override
	public ImageIcon getModelIcon() {
		// TODO Auto-generated method stub
		return ExcelIconLibrary.EXCEL_TECHNOLOGY_ICON;
	}

	@Override
	public ImageIcon getMetaModelIcon() {
		return ExcelIconLibrary.EXCEL_TECHNOLOGY_ICON;
	}

	@Override
	public ImageIcon getIconForTechnologyObject(Class<? extends TechnologyObject> objectClass) {
		return ExcelIconLibrary.iconForObject(objectClass);
	}

	@Override
	public ImageIcon getIconForPatternRole(Class<? extends PatternRole> patternRoleClass) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public OntologyBrowserModel makeOntologyBrowserModel(IFlexoOntology context) {
		// TODO Auto-generated method stub
		return null;
	}

}
