package org.openflexo.dgmodule.controller;

import org.openflexo.doceditor.controller.DEFIBController;
import org.openflexo.fib.model.FIBComponent;

/**
 * Represents the controller of a FIBComponent in the context of DocumentationGenerator module<br>
 * Extends DEFIBController by supporting features relative to DE module
 * 
 * 
 * @author sylvain
 */
public class DGFIBController extends DEFIBController {

	public DGFIBController(FIBComponent component) {
		super(component);
	}

	public DGFIBController(FIBComponent component, DGController controller) {
		super(component, controller);
	}

	@Override
	public DGController getFlexoController() {
		return (DGController) super.getFlexoController();
	}

}
