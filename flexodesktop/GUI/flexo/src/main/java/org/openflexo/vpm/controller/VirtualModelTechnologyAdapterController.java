package org.openflexo.vpm.controller;

import javax.swing.ImageIcon;

import org.openflexo.foundation.FlexoObject;
import org.openflexo.foundation.technologyadapter.TechnologyObject;
import org.openflexo.foundation.viewpoint.AddEditionPatternInstance;
import org.openflexo.foundation.viewpoint.DeleteAction;
import org.openflexo.foundation.viewpoint.EditionAction;
import org.openflexo.foundation.viewpoint.EditionPatternInstancePatternRole;
import org.openflexo.foundation.viewpoint.PatternRole;
import org.openflexo.foundation.viewpoint.SelectEditionPatternInstance;
import org.openflexo.foundation.viewpoint.VirtualModelTechnologyAdapter;
import org.openflexo.icon.IconFactory;
import org.openflexo.icon.IconLibrary;
import org.openflexo.icon.VEIconLibrary;
import org.openflexo.view.EmptyPanel;
import org.openflexo.view.ModuleView;
import org.openflexo.view.controller.ControllerActionInitializer;
import org.openflexo.view.controller.FlexoController;
import org.openflexo.view.controller.TechnologyAdapterController;
import org.openflexo.view.controller.model.FlexoPerspective;

public class VirtualModelTechnologyAdapterController extends TechnologyAdapterController<VirtualModelTechnologyAdapter> {

	@Override
	public Class<VirtualModelTechnologyAdapter> getTechnologyAdapterClass() {
		return VirtualModelTechnologyAdapter.class;
	}

	@Override
	public void initializeActions(ControllerActionInitializer actionInitializer) {
	}

	@Override
	public ImageIcon getTechnologyBigIcon() {
		// TODO
		return VEIconLibrary.VIRTUAL_MODEL_INSTANCE_ICON;
	}

	/**
	 * Return icon representing underlying technology
	 * 
	 * @return
	 */
	@Override
	public ImageIcon getTechnologyIcon() {
		return VEIconLibrary.VIRTUAL_MODEL_INSTANCE_ICON;
	}

	/**
	 * Return icon representing a model of underlying technology
	 * 
	 * @return
	 */
	@Override
	public ImageIcon getModelIcon() {
		return VEIconLibrary.VIRTUAL_MODEL_INSTANCE_ICON;
	}

	/**
	 * Return icon representing a model of underlying technology
	 * 
	 * @return
	 */
	@Override
	public ImageIcon getMetaModelIcon() {
		return VEIconLibrary.VIRTUAL_MODEL_INSTANCE_ICON;
	}

	/**
	 * Return icon representing supplied ontology object
	 * 
	 * @param object
	 * @return
	 */
	@Override
	public ImageIcon getIconForTechnologyObject(Class<? extends TechnologyObject> objectClass) {
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
		if (EditionPatternInstancePatternRole.class.isAssignableFrom(patternRoleClass)) {
			return VEIconLibrary.EDITION_PATTERN_INSTANCE_ICON;
		}
		return null;
	}

	@Override
	public ImageIcon getIconForEditionAction(Class<? extends EditionAction> editionActionClass) {
		if (AddEditionPatternInstance.class.isAssignableFrom(editionActionClass)) {
			return IconFactory.getImageIcon(VEIconLibrary.EDITION_PATTERN_INSTANCE_ICON, IconLibrary.DUPLICATE);
		} else if (SelectEditionPatternInstance.class.isAssignableFrom(editionActionClass)) {
			return IconFactory.getImageIcon(VEIconLibrary.EDITION_PATTERN_INSTANCE_ICON, IconLibrary.IMPORT);
		} else if (DeleteAction.class.isAssignableFrom(editionActionClass)) {
			return IconFactory.getImageIcon(VEIconLibrary.EDITION_PATTERN_INSTANCE_ICON, IconLibrary.DELETE);
		}
		return super.getIconForEditionAction(editionActionClass);
	}

	@Override
	public boolean hasModuleViewForObject(FlexoObject object) {
		// TODO not applicable
		return false;
	}

	@Override
	public <T extends FlexoObject> ModuleView<T> createModuleViewForObject(T object, FlexoController controller,
			FlexoPerspective perspective) {
		// TODO not applicable
		return new EmptyPanel<T>(controller, perspective, object);
	}

}
