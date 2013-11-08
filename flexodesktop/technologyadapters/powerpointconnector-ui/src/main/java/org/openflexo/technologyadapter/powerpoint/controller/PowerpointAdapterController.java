package org.openflexo.technologyadapter.powerpoint.controller;

import javax.swing.ImageIcon;

import org.openflexo.foundation.FlexoObject;
import org.openflexo.foundation.technologyadapter.TechnologyObject;
import org.openflexo.foundation.viewpoint.PatternRole;
import org.openflexo.technologyadapter.powerpoint.PowerpointTechnologyAdapter;
import org.openflexo.technologyadapter.powerpoint.gui.PowerpointIconLibrary;
import org.openflexo.technologyadapter.powerpoint.model.PowerpointShape;
import org.openflexo.technologyadapter.powerpoint.model.PowerpointSlide;
import org.openflexo.technologyadapter.powerpoint.model.PowerpointSlideshow;
import org.openflexo.technologyadapter.powerpoint.view.PowerpointSlideshowView;
import org.openflexo.technologyadapter.powerpoint.viewpoint.PowerpointShapePatternRole;
import org.openflexo.technologyadapter.powerpoint.viewpoint.PowerpointSlidePatternRole;
import org.openflexo.toolbox.FileResource;
import org.openflexo.view.EmptyPanel;
import org.openflexo.view.ModuleView;
import org.openflexo.view.controller.ControllerActionInitializer;
import org.openflexo.view.controller.FlexoController;
import org.openflexo.view.controller.TechnologyAdapterController;
import org.openflexo.view.controller.model.FlexoPerspective;

public class PowerpointAdapterController extends TechnologyAdapterController<PowerpointTechnologyAdapter> {

	@Override
	public Class<PowerpointTechnologyAdapter> getTechnologyAdapterClass() {
		return PowerpointTechnologyAdapter.class;
	}

	@Override
	public void initializeActions(ControllerActionInitializer actionInitializer) {
		actionInitializer.getController().getModuleInspectorController().loadDirectory(new FileResource("Inspectors/Excel"));
	}

	@Override
	public ImageIcon getTechnologyBigIcon() {
		return PowerpointIconLibrary.POWERPOINT_TECHNOLOGY_BIG_ICON;
	}

	@Override
	public ImageIcon getTechnologyIcon() {
		return PowerpointIconLibrary.POWERPOINT_TECHNOLOGY_ICON;
	}

	@Override
	public ImageIcon getModelIcon() {
		// TODO Auto-generated method stub
		return PowerpointIconLibrary.POWERPOINT_TECHNOLOGY_ICON;
	}

	@Override
	public ImageIcon getMetaModelIcon() {
		return PowerpointIconLibrary.POWERPOINT_TECHNOLOGY_ICON;
	}

	@Override
	public ImageIcon getIconForTechnologyObject(Class<? extends TechnologyObject> objectClass) {
		return PowerpointIconLibrary.iconForObject(objectClass);
	}

	@Override
	public ImageIcon getIconForPatternRole(Class<? extends PatternRole<?>> patternRoleClass) {
		if (PowerpointSlidePatternRole.class.isAssignableFrom(patternRoleClass)) {
			return getIconForTechnologyObject(PowerpointSlide.class);
		}
		if (PowerpointShapePatternRole.class.isAssignableFrom(patternRoleClass)) {
			return getIconForTechnologyObject(PowerpointShape.class);
		}
		return null;
	}

	@Override
	public boolean hasModuleViewForObject(TechnologyObject object) {
		if (object instanceof PowerpointSlideshow) {
			return true;
		}
		return false;
	}

	@Override
	public String getWindowTitleforObject(TechnologyObject object) {
		if (object instanceof PowerpointSlide) {
			return ((PowerpointSlide) object).getName();
		}
		return object.toString();
	}

	@Override
	public <T extends FlexoObject> ModuleView<T> createModuleViewForObject(T object, FlexoController controller,
			FlexoPerspective perspective) {
		if (object instanceof PowerpointSlideshow) {
			return (ModuleView<T>) new PowerpointSlideshowView((PowerpointSlideshow) object, controller, perspective);
		}
		return new EmptyPanel<T>(controller, perspective, object);
	}

}
