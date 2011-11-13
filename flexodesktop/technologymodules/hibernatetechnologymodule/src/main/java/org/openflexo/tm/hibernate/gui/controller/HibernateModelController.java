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
			entity.setName("Entity" + (getDataObject().getEntities().size() > 0 ? getDataObject().getEntities().size() : ""));

			getDataObject().addToEntities(entity);

			// Create default primary key
			HibernateAttribute attribute = new HibernateAttribute(entity.getImplementationModel());
			attribute.setName("id");
			attribute.setType(HibernateAttributeType.LONG);
			attribute.setPrimaryKey(true);
			attribute.setNotNull(true);
			attribute.setUnique(true);
			entity.addToAttributes(attribute);

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

		HibernateAttribute attribute = new HibernateAttribute(entity.getImplementationModel());
		try {
			attribute.setName("attribute" + (entity.getAttributes().size() > 0 ? entity.getAttributes().size() : ""));
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		attribute.setType(HibernateAttributeType.STRING);
		entity.addToAttributes(attribute);
		return attribute;
	}

	/**
	 * Create a new relationship for the specified entity.
	 * 
	 * @param entity
	 * @return the newly created relationship.
	 */
	public HibernateRelationship performCreateRelationship(HibernateEntity entity) {
		HibernateRelationship relationship = new HibernateRelationship(entity.getImplementationModel());
		entity.addToRelationships(relationship);
		return relationship;
	}
}
