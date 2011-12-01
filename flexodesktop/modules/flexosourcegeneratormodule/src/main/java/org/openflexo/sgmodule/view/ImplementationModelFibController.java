/**
 * 
 */
package org.openflexo.sgmodule.view;

import java.util.Vector;

import org.openflexo.fib.model.FIBComponent;
import org.openflexo.foundation.action.FlexoAction;
import org.openflexo.foundation.sg.implmodel.ImplementationModel;
import org.openflexo.sg.action.CreateTechnologyModuleImplementation;
import org.openflexo.view.controller.FlexoController;
import org.openflexo.view.controller.FlexoFIBController;

/**
 *
 * @author Nicolas Daniels
 */
public class ImplementationModelFibController extends FlexoFIBController<ImplementationModel> {

	public ImplementationModelFibController(FIBComponent component, FlexoController controller) {
		super(component, controller);
	}

	public void performAddTechnologyModule() {
		Vector<ImplementationModel> globalSelection = new Vector<ImplementationModel>();
		globalSelection.add(getDataObject());
		FlexoAction<?, ?, ?> action = CreateTechnologyModuleImplementation.actionType.makeNewAction(getDataObject(), globalSelection, getEditor());
		action.doAction();
	}
}
