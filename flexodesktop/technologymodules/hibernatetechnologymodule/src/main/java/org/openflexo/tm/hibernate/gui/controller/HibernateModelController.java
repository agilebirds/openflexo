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
public class HibernateModelController extends HibernateFibController {

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
			if (getDataObject() instanceof HibernateModel) {
				HibernateModel dataObject = (HibernateModel) getDataObject();
				HibernateEntity entity = new HibernateEntity(dataObject.getImplementationModel());
				entity.setName("Entity" + (dataObject.getEntities().size() > 0 ? dataObject.getEntities().size() : ""));

				dataObject.addToEntities(entity);

				// Create default primary key
				HibernateAttribute attribute = new HibernateAttribute(entity.getImplementationModel());
				attribute.setName("id");
				attribute.setType(HibernateAttributeType.LONG);
				attribute.setPrimaryKey(true);
				attribute.setNotNull(true);
				attribute.setUnique(true);
				entity.addToAttributes(attribute);

				return entity;
			}
			throw new RuntimeException("Data object is not a Hibernate Model");
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
