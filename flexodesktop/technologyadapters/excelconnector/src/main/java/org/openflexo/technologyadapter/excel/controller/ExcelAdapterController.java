package org.openflexo.technologyadapter.excel.controller;

import javax.swing.ImageIcon;

import org.openflexo.foundation.FlexoObject;
import org.openflexo.foundation.technologyadapter.TechnologyObject;
import org.openflexo.foundation.viewpoint.PatternRole;
import org.openflexo.technologyadapter.excel.ExcelTechnologyAdapter;
import org.openflexo.technologyadapter.excel.gui.ExcelIconLibrary;
import org.openflexo.technologyadapter.excel.model.ExcelCell;
import org.openflexo.technologyadapter.excel.model.ExcelRow;
import org.openflexo.technologyadapter.excel.model.ExcelSheet;
import org.openflexo.technologyadapter.excel.model.ExcelWorkbook;
import org.openflexo.technologyadapter.excel.viewpoint.ExcelCellPatternRole;
import org.openflexo.technologyadapter.excel.viewpoint.ExcelRowPatternRole;
import org.openflexo.technologyadapter.excel.viewpoint.ExcelSheetPatternRole;
import org.openflexo.technologyadapter.excel.viewpoint.ExcelWorkbookPatternRole;
import org.openflexo.toolbox.FileResource;
import org.openflexo.view.EmptyPanel;
import org.openflexo.view.ModuleView;
import org.openflexo.view.controller.ControllerActionInitializer;
import org.openflexo.view.controller.FlexoController;
import org.openflexo.view.controller.TechnologyAdapterController;
import org.openflexo.view.controller.model.FlexoPerspective;

public class ExcelAdapterController extends TechnologyAdapterController<ExcelTechnologyAdapter> {

	@Override
	public Class<ExcelTechnologyAdapter> getTechnologyAdapterClass() {
		return ExcelTechnologyAdapter.class;
	}

	@Override
	public void initializeActions(ControllerActionInitializer actionInitializer) {
		actionInitializer.getController().getModuleInspectorController().loadDirectory(new FileResource("Inspectors/Excel"));
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
		if (ExcelWorkbookPatternRole.class.isAssignableFrom(patternRoleClass)) {
			return getIconForTechnologyObject(ExcelWorkbook.class);
		}
		if (ExcelSheetPatternRole.class.isAssignableFrom(patternRoleClass)) {
			return getIconForTechnologyObject(ExcelSheet.class);
		}
		if (ExcelCellPatternRole.class.isAssignableFrom(patternRoleClass)) {
			return getIconForTechnologyObject(ExcelCell.class);
		}
		if (ExcelRowPatternRole.class.isAssignableFrom(patternRoleClass)) {
			return getIconForTechnologyObject(ExcelRow.class);
		}
		return null;
	}

	@Override
	public boolean hasModuleViewForObject(FlexoObject object) {
		// TODO
		return false;
	}

	@Override
	public <T extends FlexoObject> ModuleView<T> createModuleViewForObject(T object, FlexoController controller,
			FlexoPerspective perspective) {
		// TODO
		return new EmptyPanel<T>(controller, perspective, object);
	}

}
