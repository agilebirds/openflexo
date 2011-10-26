/**
 * 
 */
package org.openflexo.tm.hibernate.gui.controller;

import org.openflexo.fib.model.FIBComponent;
import org.openflexo.tm.hibernate.impl.HibernateAttribute;
import org.openflexo.tm.hibernate.impl.HibernateEntity;
import org.openflexo.tm.hibernate.impl.HibernateModel;
import org.openflexo.tm.hibernate.impl.HibernateRelationship;
import org.openflexo.tm.hibernate.impl.enums.HibernateAttributeType;
import org.openflexo.view.controller.FlexoController;

/**
 * 
 * @author Nicolas Daniels
 */
public class HibernateModelController extends HibernateFibController<HibernateModel> {

	public HibernateModelController(FIBComponent component, FlexoController controller) {
		super(component, controller);
	}

	/**
	 * Create a new entity for the linked view model.
	 * 
	 * @return the newly created entity
	 */
	public HibernateEntity performCreateEntity() {

		try {
			HibernateEntity entity = new HibernateEntity(getDataObject().getImplementationModel());

			int i = 0;
			while (true) {
				if (getDataObject().getEntity("Entity" + (i == 0 ? "" : i)) == null) {
					entity.setName("Entity" + (i == 0 ? "" : i));
					break;
				}

				i++;
			}

			getDataObject().addToEntities(entity);

			entity.createDefaultPrimaryKey();

			return entity;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Create a new attribute for the specified entity.
	 * 
	 * @param entity
	 * @return the newly created attribute.
	 */
	public HibernateAttribute performCreateAttribute(HibernateEntity entity) {

		try {
			HibernateAttribute attribute = new HibernateAttribute(entity.getImplementationModel());

			int i = 0;
			while (true) {
				if (entity.getAttribute("attribute" + (i == 0 ? "" : i)) == null) {
					attribute.setName("attribute" + (i == 0 ? "" : i));
					break;
				}

				i++;
			}

			attribute.setType(HibernateAttributeType.STRING);
			entity.addToAttributes(attribute);
			return attribute;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Create a new relationship for the specified entity.
	 * 
	 * @param entity
	 * @return the newly created relationship.
	 */
	public HibernateRelationship performCreateRelationship(HibernateEntity entity) {
		try {
			HibernateRelationship relationship = new HibernateRelationship(entity.getImplementationModel());

			int i = 0;
			while (true) {
				if (entity.getRelationship("relationship" + (i == 0 ? "" : i)) == null) {
					relationship.setName("relationship" + (i == 0 ? "" : i));
					break;
				}

				i++;
			}

			entity.addToRelationships(relationship);
			return relationship;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
