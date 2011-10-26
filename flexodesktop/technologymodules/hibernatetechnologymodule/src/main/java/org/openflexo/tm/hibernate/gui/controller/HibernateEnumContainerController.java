/**
 * 
 */
package org.openflexo.tm.hibernate.gui.controller;

import org.openflexo.fib.model.FIBComponent;
import org.openflexo.tm.hibernate.impl.HibernateEnum;
import org.openflexo.tm.hibernate.impl.HibernateEnumContainer;
import org.openflexo.tm.hibernate.impl.HibernateEnumValue;
import org.openflexo.view.controller.FlexoController;

/**
 * 
 * @author Nicolas Daniels
 */
public class HibernateEnumContainerController extends HibernateFibController<HibernateEnumContainer> {

	public HibernateEnumContainerController(FIBComponent component, FlexoController controller) {
		super(component, controller);
	}

	/**
	 * Create a new enum for the linked view enum container.
	 * 
	 * @return the newly created enum
	 */
	public HibernateEnum performCreateEnum() {

		try {
			HibernateEnum hibernateEnum = new HibernateEnum(getDataObject().getImplementationModel());

			int i = 0;
			while (true) {
				if (getDataObject().getHibernateEnum("Enum" + (i == 0 ? "" : i)) == null) {
					hibernateEnum.setName("Enum" + (i == 0 ? "" : i));
					break;
				}

				i++;
			}

			getDataObject().addToHibernateEnums(hibernateEnum);

			return hibernateEnum;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Create a new enum value for the specified enum.
	 * 
	 * @param hibernateEnum
	 * @return the newly created enum value.
	 */
	public HibernateEnumValue performCreateEnumValue(HibernateEnum hibernateEnum) {

		try {
			HibernateEnumValue hibernateEnumValue = new HibernateEnumValue(hibernateEnum.getImplementationModel());
			hibernateEnumValue.setName("value" + (hibernateEnum.getHibernateEnumValues().size() > 0 ? hibernateEnum.getHibernateEnumValues().size() : ""));
			hibernateEnum.addToHibernateEnumValues(hibernateEnumValue);
			return hibernateEnumValue;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

	}
}
