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
import org.openflexo.technologyadapter.excel.model.AExcelModelObjectImpl;
import org.openflexo.technologyadapter.excel.model.ExcelModel;
import org.openflexo.technologyadapter.excel.model.ExcelMetaModel;
import org.openflexo.technologyadapter.excel.viewpoint.ExcelCellPatternRole;
import org.openflexo.technologyadapter.excel.viewpoint.ExcelRowPatternRole;
import org.openflexo.technologyadapter.excel.viewpoint.ExcelSheetPatternRole;
import org.openflexo.technologyadapter.excel.viewpoint.ExcelWorkbookPatternRole;
import org.openflexo.technologyadapter.excel.viewpoint.editionaction.AddExcelCell;
import org.openflexo.technologyadapter.excel.viewpoint.editionaction.AddExcelRow;
import org.openflexo.technologyadapter.excel.viewpoint.editionaction.AddExcelSheet;
import org.openflexo.technologyadapter.excel.viewpoint.editionaction.AddExcelWorkbook;
import org.openflexo.toolbox.FileResource;
import org.openflexo.view.controller.ControllerActionInitializer;
import org.openflexo.view.controller.TechnologyAdapterController;

public class ExcelAdapterController extends TechnologyAdapterController<ExcelTechnologyAdapter> {

	static final Logger logger = Logger.getLogger(ExcelAdapterController.class.getPackage().getName());

	@Override
	public Class<ExcelTechnologyAdapter> getTechnologyAdapterClass() {
		return ExcelTechnologyAdapter.class;
	}

	@Override
	public void initializeActions(ControllerActionInitializer actionInitializer) {

		actionInitializer.getController().getModuleInspectorController().loadDirectory(new FileResource("Inspectors/Excel"));
	}

	/**
	 * Return icon representing underlying technology, required size is 32x32
	 * 
	 * @return
	 */
	@Override
	public ImageIcon getTechnologyBigIcon() {
		return ExcelIconLibrary.Excel_TECHNOLOGY_BIG_ICON;
	}

	/**
	 * Return icon representing underlying technology
	 * 
	 * @return
	 */
	@Override
	public ImageIcon getTechnologyIcon() {
		return ExcelIconLibrary.Excel_TECHNOLOGY_ICON;
	}

	/**
	 * Return icon representing a model of underlying technology
	 * 
	 * @return
	 */
	@Override
	public ImageIcon getModelIcon() {
		return ExcelIconLibrary.Excel_FILE_ICON;
	}

	/**
	 * Return icon representing a model of underlying technology
	 * 
	 * @return
	 */
	@Override
	public ImageIcon getMetaModelIcon() {
		return ExcelIconLibrary.Excel_FILE_ICON;
	}

	/**
	 * Return icon representing supplied ontology object
	 * 
	 * @param object
	 * @return
	 */
	@Override
	public ImageIcon getIconForOntologyObject(Class<? extends IFlexoOntologyObject> objectClass) {
		if (AExcelModelObjectImpl.class.isAssignableFrom(objectClass))
			return ExcelIconLibrary.iconForObject((Class<? extends AExcelModelObjectImpl>) objectClass);
		return null;
	}

	/**
	 * Return icon representing supplied property value
	 * 
	 * @param object
	 * @return
	 */
	@Override
	public ImageIcon getIconForPropertyValue(Class<? extends IFlexoOntologyPropertyValue> objectClass) {
		return null;
	}

	/**
	 * Return icon representing supplied pattern role
	 * 
	 * @param object
	 * @return
	 */
	@Override
	public ImageIcon getIconForPatternRole(Class<? extends PatternRole> patternRoleClass) {
		if (ExcelSheetPatternRole.class.isAssignableFrom(patternRoleClass)) {
			return getIconForOntologyObject(ExcelSheetPatternRole.class);
		} else if (ExcelRowPatternRole.class.isAssignableFrom(patternRoleClass)) {
			return getIconForOntologyObject(ExcelRowPatternRole.class);
		}
		else if (ExcelCellPatternRole.class.isAssignableFrom(patternRoleClass)) {
			return getIconForOntologyObject(ExcelCellPatternRole.class);
		}
		else if (ExcelWorkbookPatternRole.class.isAssignableFrom(patternRoleClass)) {
			return getIconForOntologyObject(ExcelWorkbookPatternRole.class);
		}
		return null;
	}

	/**
	 * Return icon representing supplied edition action
	 * 
	 * @param object
	 * @return
	 */
	@Override
	public ImageIcon getIconForEditionAction(Class<? extends EditionAction> editionActionClass) {
		if (AddExcelWorkbook.class.isAssignableFrom(editionActionClass)) {
			return IconFactory.getImageIcon(getIconForOntologyObject(ExcelWorkbookPatternRole.class), IconLibrary.DUPLICATE);
		} else if (AddExcelSheet.class.isAssignableFrom(editionActionClass)) {
			return IconFactory.getImageIcon(getIconForOntologyObject(ExcelSheetPatternRole.class), IconLibrary.DUPLICATE);
		}
		else if (AddExcelRow.class.isAssignableFrom(editionActionClass)) {
			return IconFactory.getImageIcon(getIconForOntologyObject(ExcelRomPatternRole.class), IconLibrary.DUPLICATE);
		}
		else if (AddExcelCell.class.isAssignableFrom(editionActionClass)) {
			return IconFactory.getImageIcon(getIconForOntologyObject(ExcelCellPatternRole.class), IconLibrary.DUPLICATE);
		}
		return super.getIconForEditionAction(editionActionClass);
	}

	@Override
	public OntologyBrowserModel makeOntologyBrowserModel(IFlexoOntology context) {
		if (context instanceof ExcelMetaModel) {
			return new ExcelMetaModelBrowserModel((ExcelMetaModel) context);
		} else if (context instanceof ExcelModel) {
			return new ExcelModelBrowserModel((ExcelModel) context);
		} else {
			logger.warning("Unexpected " + context);
			return null;
		}
	}

}
