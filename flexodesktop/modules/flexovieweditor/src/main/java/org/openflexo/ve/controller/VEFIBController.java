package org.openflexo.ve.controller;

import org.openflexo.fib.model.FIBComponent;
import org.openflexo.view.controller.FlexoFIBController;

/**
 * Represents the controller used in VE (ViewEditor)<br>
 * Extends FlexoFIBController by supporting features relative to VE module
 * 
 * 
 * @author sylvain
 */
public class VEFIBController extends FlexoFIBController {

	public VEFIBController(FIBComponent component) {
		super(component);
	}

	public VEFIBController(FIBComponent component, VEController controller) {
		super(component, controller);
	}

	@Override
	public VEController getFlexoController() {
		return (VEController) super.getFlexoController();
	}

}
