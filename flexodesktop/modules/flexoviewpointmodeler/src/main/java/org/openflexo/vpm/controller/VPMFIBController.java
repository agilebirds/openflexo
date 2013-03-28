package org.openflexo.vpm.controller;

import org.openflexo.fib.model.FIBComponent;
import org.openflexo.foundation.technologyadapter.ModelSlot;
import org.openflexo.foundation.viewpoint.EditionAction;
import org.openflexo.foundation.viewpoint.EditionPattern;
import org.openflexo.foundation.viewpoint.EditionSchemeObject;
import org.openflexo.foundation.viewpoint.PatternRole;
import org.openflexo.foundation.viewpoint.VirtualModel;
import org.openflexo.foundation.viewpoint.action.CreateEditionAction;
import org.openflexo.foundation.viewpoint.action.CreateModelSlot;
import org.openflexo.foundation.viewpoint.action.CreatePatternRole;
import org.openflexo.view.controller.FlexoFIBController;

/**
 * Represents the controller used in VPM (ViewPointModeller)<br>
 * Extends FlexoFIBController by supporting features relative to VPM module
 * 
 * 
 * @author sylvain
 */
public class VPMFIBController extends FlexoFIBController {

	public VPMFIBController(FIBComponent component) {
		super(component);
	}

	public VPMFIBController(FIBComponent component, VPMController controller) {
		super(component, controller);
	}

	@Override
	public VPMController getFlexoController() {
		return (VPMController) super.getFlexoController();
	}

	public ModelSlot createModelSlot(VirtualModel virtualModel) {
		CreateModelSlot createModelSlot = CreateModelSlot.actionType.makeNewAction(virtualModel, null, getEditor());
		createModelSlot.doAction();
		return createModelSlot.getNewModelSlot();
	}

	public PatternRole createPatternRole(EditionPattern editionPattern) {
		CreatePatternRole createPatternRole = CreatePatternRole.actionType.makeNewAction(editionPattern, null, getEditor());
		createPatternRole.doAction();
		return createPatternRole.getNewPatternRole();
	}

	public EditionAction createEditionAction(EditionSchemeObject object) {
		CreateEditionAction createEditionAction = CreateEditionAction.actionType.makeNewAction(object, null, getEditor());
		createEditionAction.doAction();
		return createEditionAction.getNewEditionAction();
	}
}
