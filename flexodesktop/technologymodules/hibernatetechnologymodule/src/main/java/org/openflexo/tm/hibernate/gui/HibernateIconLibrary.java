/**
 * 
 */
package org.openflexo.tm.hibernate.gui;

import javax.swing.ImageIcon;

import org.openflexo.foundation.sg.implmodel.TechnologyModelObject;
import org.openflexo.tm.hibernate.impl.HibernateAttribute;
import org.openflexo.tm.hibernate.impl.HibernateEntity;
import org.openflexo.tm.hibernate.impl.HibernateEnum;
import org.openflexo.tm.hibernate.impl.HibernateEnumContainer;
import org.openflexo.tm.hibernate.impl.HibernateImplementation;
import org.openflexo.tm.hibernate.impl.HibernateModel;
import org.openflexo.tm.hibernate.impl.HibernateRelationship;

/**
 * 
 * @author Nicolas Daniels
 */
public class HibernateIconLibrary {

	public static final ImageIcon REPOSITORY_ICON = new ImageIcon(
			HibernateIconLibrary.class.getResource("/Hibernate/Icons/SmallRepository.gif"));
	public static final ImageIcon MODEL_ICON = new ImageIcon(HibernateIconLibrary.class.getResource("/Hibernate/Icons/SmallModel.gif"));
	public static final ImageIcon ENTITYAUTO_ICON = new ImageIcon(
			HibernateIconLibrary.class.getResource("/Hibernate/Icons/SmallEntityAuto.gif"));
	public static final ImageIcon ENTITYMANUAL_ICON = new ImageIcon(
			HibernateIconLibrary.class.getResource("/Hibernate/Icons/SmallEntityManual.gif"));
	public static final ImageIcon ATTRIBUTE_ICON = new ImageIcon(
			HibernateIconLibrary.class.getResource("/Hibernate/Icons/SmallAttribute.gif"));
	public static final ImageIcon RELATIONSHIP_ICON = new ImageIcon(
			HibernateIconLibrary.class.getResource("/Hibernate/Icons/SmallRelationship.gif"));
	public static final ImageIcon ENUMCONTAINER_ICON = new ImageIcon(
			HibernateIconLibrary.class.getResource("/Hibernate/Icons/SmallEnumContainer.gif"));
	public static final ImageIcon ENUMAUTO_ICON = new ImageIcon(
			HibernateIconLibrary.class.getResource("/Hibernate/Icons/SmallEnumAuto.gif"));
	public static final ImageIcon ENUMMANUAL_ICON = new ImageIcon(
			HibernateIconLibrary.class.getResource("/Hibernate/Icons/SmallEnumManual.gif"));

	/**
	 * Retrieve the icon to use for the specified Hibernate model object.
	 * 
	 * @param object
	 * @return the retrieved icon if any, null otherwise.
	 */
	public static ImageIcon getIconForHibernateObject(TechnologyModelObject object) {
		if (object instanceof HibernateImplementation) {
			return REPOSITORY_ICON;
		}
		if (object instanceof HibernateModel) {
			return MODEL_ICON;
		}
		if (object instanceof HibernateEntity) {
			if (((HibernateEntity) object).getLinkedFlexoModelObject() != null) {
				return ENTITYAUTO_ICON;
			}
			return ENTITYMANUAL_ICON;
		}
		if (object instanceof HibernateAttribute) {
			return ATTRIBUTE_ICON;
		}
		if (object instanceof HibernateRelationship) {
			return RELATIONSHIP_ICON;
		}

		if (object instanceof HibernateEnumContainer) {
			return ENUMCONTAINER_ICON;
		}
		if (object instanceof HibernateEnum) {
			if (((HibernateEnum) object).getLinkedFlexoModelObject() != null) {
				return ENUMAUTO_ICON;
			}
			return ENUMMANUAL_ICON;
		}

		return null;
	}
}
