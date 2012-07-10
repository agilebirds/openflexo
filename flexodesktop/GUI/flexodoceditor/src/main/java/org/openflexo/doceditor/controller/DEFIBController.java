package org.openflexo.doceditor.controller;

import org.openflexo.fib.model.FIBComponent;
import org.openflexo.view.controller.FlexoFIBController;

/**
 * Represents the controller of a FIBComponent in the context of DocumentationEditor module<br>
 * Extends FlexoFIBController by supporting features relative to DE module
 * 
 * 
 * @author sylvain
 */
public class DEFIBController extends FlexoFIBController {

	public DEFIBController(FIBComponent component) {
		super(component);
	}

	public DEFIBController(FIBComponent component, DEController controller) {
		super(component, controller);
	}

	@Override
	public DEController getFlexoController() {
		return (DEController) super.getFlexoController();
	}

	public void coucou() {
		System.out.println("Coucou");
	}
}
