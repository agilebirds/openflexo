/**
 * 
 */
package org.openflexo.tm.hibernate.gui.controller;

import java.util.Vector;

import org.openflexo.fib.model.FIBComponent;
import org.openflexo.foundation.action.FlexoAction;
import org.openflexo.tm.hibernate.gui.action.CreateHibernateModelAction;
import org.openflexo.tm.hibernate.impl.HibernateImplementation;
import org.openflexo.view.controller.FlexoController;

/**
 * 
 * @author Nicolas Daniels
 */
public class HibernateImplementationController extends HibernateFibController {

	public HibernateImplementationController(FIBComponent component, FlexoController controller) {
		super(component, controller);
	}

	public void performCreateModel() {
		Vector<HibernateImplementation> globalSelection = new Vector<HibernateImplementation>();
		FlexoAction<?, ?, ?> action = CreateHibernateModelAction.actionType.makeNewAction((HibernateImplementation) getDataObject(),
				globalSelection, getEditor());
		action.doAction();
	}
}
