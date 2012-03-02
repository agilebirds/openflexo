/**
 * 
 */
package org.openflexo.tm.persistence.gui.controller;

import javax.swing.ImageIcon;

import org.openflexo.fib.model.FIBComponent;
import org.openflexo.foundation.sg.implmodel.TechnologyModelObject;
import org.openflexo.tm.persistence.gui.PersistenceIconLibrary;
import org.openflexo.view.controller.FlexoController;
import org.openflexo.view.controller.FlexoFIBController;

/**
 * 
 * @author Nicolas Daniels
 */
public class PersistenceFibController<O extends TechnologyModelObject> extends FlexoFIBController<O> {

	public PersistenceFibController(FIBComponent component, FlexoController controller) {
		super(component, controller);
	}

	/**
	 * @see org.openflexo.tm.persistence.gui.PersistenceIconLibrary#getIconForHibernateObject(TechnologyModelObject)
	 * 
	 * @param object
	 * @return
	 */
	public ImageIcon getIconForHibernateObject(TechnologyModelObject object) {
		return PersistenceIconLibrary.getIconForHibernateObject(object);
	}
}
