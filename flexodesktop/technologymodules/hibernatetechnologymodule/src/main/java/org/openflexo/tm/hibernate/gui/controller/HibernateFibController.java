/**
 * 
 */
package org.openflexo.tm.hibernate.gui.controller;

import javax.swing.ImageIcon;

import org.openflexo.fib.model.FIBComponent;
import org.openflexo.foundation.sg.implmodel.TechnologyModelObject;
import org.openflexo.tm.hibernate.gui.HibernateIconLibrary;
import org.openflexo.view.controller.FlexoController;
import org.openflexo.view.controller.FlexoFIBController;

/**
 * 
 * @author Nicolas Daniels
 */
public class HibernateFibController extends FlexoFIBController {

	public HibernateFibController(FIBComponent component, FlexoController controller) {
		super(component, controller);
	}

	/**
	 * @see HibernateIconLibrary#getIconForHibernateObject(TechnologyModelObject)
	 * 
	 * @param object
	 * @return
	 */
	public ImageIcon getIconForHibernateObject(TechnologyModelObject object) {
		return HibernateIconLibrary.getIconForHibernateObject(object);
	}
}
