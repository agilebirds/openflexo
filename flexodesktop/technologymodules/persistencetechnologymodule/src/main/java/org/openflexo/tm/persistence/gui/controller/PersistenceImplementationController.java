/**
 * 
 */
package org.openflexo.tm.persistence.gui.controller;

import java.util.Vector;

import org.openflexo.fib.model.FIBComponent;
import org.openflexo.foundation.action.FlexoAction;
import org.openflexo.tm.persistence.gui.action.CreateHibernateModelAction;
import org.openflexo.tm.persistence.impl.PersistenceImplementation;
import org.openflexo.view.controller.FlexoController;

/**
 * 
 * @author Nicolas Daniels
 */
public class PersistenceImplementationController extends PersistenceFibController<PersistenceImplementation> {

	public PersistenceImplementationController(FIBComponent component, FlexoController controller) {
		super(component, controller);
	}

	public void performCreateModel() {
		Vector<PersistenceImplementation> globalSelection = new Vector<PersistenceImplementation>();
		globalSelection.add(getDataObject());
		FlexoAction<?, ?, ?> action = CreateHibernateModelAction.actionType.makeNewAction(getDataObject(), globalSelection, getEditor());
		action.doAction();
	}
}
