package org.openflexo.vpm.controller;

import org.openflexo.fib.model.FIBComponent;
import org.openflexo.foundation.viewpoint.EditionPattern;
import org.openflexo.foundation.viewpoint.PatternRole;
import org.openflexo.foundation.viewpoint.action.CreatePatternRole;
import org.openflexo.view.controller.FlexoFIBController;

/**
 * Represents the controller of a FIBComponent in the context of DocumentationEditor module<br>
 * Extends FlexoFIBController by supporting features relative to DE module
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

	public PatternRole createPatternRole(EditionPattern editionPattern) {
		CreatePatternRole newPatternRole = CreatePatternRole.actionType.makeNewAction(editionPattern, null, getEditor());
		newPatternRole.doAction();
		return newPatternRole.getNewPatternRole();
	}
}
