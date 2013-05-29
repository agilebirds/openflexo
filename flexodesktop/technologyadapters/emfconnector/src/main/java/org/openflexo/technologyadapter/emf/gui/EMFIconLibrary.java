/*
 * (c) Copyright 2010-2011 AgileBirds
 *
 * This file is part of OpenFlexo.
 *
 * OpenFlexo is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * OpenFlexo is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with OpenFlexo. If not, see <http://www.gnu.org/licenses/>.
 *
 */
package org.openflexo.technologyadapter.emf.gui;

import java.util.logging.Logger;

import javax.swing.ImageIcon;

import org.openflexo.foundation.ontology.IFlexoOntologyObject;
import org.openflexo.foundation.ontology.IFlexoOntologyPropertyValue;
import org.openflexo.technologyadapter.emf.metamodel.EMFAttributeDataProperty;
import org.openflexo.technologyadapter.emf.metamodel.EMFAttributeObjectProperty;
import org.openflexo.technologyadapter.emf.metamodel.EMFClassClass;
import org.openflexo.technologyadapter.emf.metamodel.EMFEnumClass;
import org.openflexo.technologyadapter.emf.metamodel.EMFEnumIndividual;
import org.openflexo.technologyadapter.emf.metamodel.EMFMetaModel;
import org.openflexo.technologyadapter.emf.metamodel.EMFReferenceObjectProperty;
import org.openflexo.technologyadapter.emf.model.EMFModel;
import org.openflexo.technologyadapter.emf.model.EMFObjectIndividual;
import org.openflexo.technologyadapter.emf.model.EMFObjectIndividualAttributeDataPropertyValue;
import org.openflexo.technologyadapter.emf.model.EMFObjectIndividualAttributeObjectPropertyValue;
import org.openflexo.technologyadapter.emf.model.EMFObjectIndividualReferenceObjectPropertyValue;
import org.openflexo.toolbox.ImageIconResource;

public class EMFIconLibrary {

	private static final Logger logger = Logger.getLogger(EMFIconLibrary.class.getPackage().getName());

	public static final ImageIconResource EMF_TECHNOLOGY_BIG_ICON = new ImageIconResource("src/main/resources/Icons/EMFTechnology32.png");
	public static final ImageIconResource EMF_TECHNOLOGY_ICON = new ImageIconResource("src/main/resources/Icons/EMFTechnology.png");
	public static final ImageIconResource ECORE_FILE_ICON = new ImageIconResource("src/main/resources/Icons/EMFTechnology.png");
	public static final ImageIconResource EMF_FILE_ICON = new ImageIconResource("src/main/resources/Icons/EMFTechnology.png");
	public static final ImageIconResource EMF_CLASS_ICON = new ImageIconResource("src/main/resources/Icons/EClass.gif");
	public static final ImageIconResource EMF_ENUM_ICON = new ImageIconResource("src/main/resources/Icons/EEnum.gif");
	public static final ImageIconResource EMF_ENUM_LITERAL_ICON = new ImageIconResource("src/main/resources/Icons/EEnumLiteral.gif");
	public static final ImageIconResource EMF_INDIVIDUAL_ICON = new ImageIconResource("src/main/resources/Icons/EObject.gif");
	public static final ImageIconResource EMF_REFERENCE_ICON = new ImageIconResource("src/main/resources/Icons/EReference.gif");
	public static final ImageIconResource EMF_ATTRIBUTE_ICON = new ImageIconResource("src/main/resources/Icons/EAttribute.gif");

	public static ImageIcon iconForObject(Class<? extends IFlexoOntologyObject> objectClass) {
		if (EMFMetaModel.class.isAssignableFrom(objectClass)) {
			return ECORE_FILE_ICON;
		} else if (EMFModel.class.isAssignableFrom(objectClass)) {
			return EMF_FILE_ICON;
		} else if (EMFClassClass.class.isAssignableFrom(objectClass)) {
			return EMF_CLASS_ICON;
		} else if (EMFEnumClass.class.isAssignableFrom(objectClass)) {
			return EMF_ENUM_ICON;
		} else if (EMFEnumIndividual.class.isAssignableFrom(objectClass)) {
			return EMF_ENUM_LITERAL_ICON;
		} else if (EMFObjectIndividual.class.isAssignableFrom(objectClass)) {
			return EMF_INDIVIDUAL_ICON;
		} else if (EMFReferenceObjectProperty.class.isAssignableFrom(objectClass)) {
			return EMF_REFERENCE_ICON;
		} else if (EMFAttributeDataProperty.class.isAssignableFrom(objectClass)) {
			return EMF_ATTRIBUTE_ICON;
		} else if (EMFAttributeObjectProperty.class.isAssignableFrom(objectClass)) {
			return EMF_ATTRIBUTE_ICON;
		} else if (EMFObjectIndividualAttributeDataPropertyValue.class.isAssignableFrom(objectClass)) {
			return EMF_ATTRIBUTE_ICON;
		} else if (EMFObjectIndividualAttributeObjectPropertyValue.class.isAssignableFrom(objectClass)) {
			return EMF_ATTRIBUTE_ICON;
		} else if (EMFObjectIndividualReferenceObjectPropertyValue.class.isAssignableFrom(objectClass)) {
			return EMF_REFERENCE_ICON;
		}
		logger.warning("No icon for " + objectClass);
		return null;
	}

	public static ImageIcon iconForPropertyValue(Class<? extends IFlexoOntologyPropertyValue> objectClass) {
		if (EMFObjectIndividualAttributeDataPropertyValue.class.isAssignableFrom(objectClass)) {
			return EMF_ATTRIBUTE_ICON;
		} else if (EMFObjectIndividualAttributeObjectPropertyValue.class.isAssignableFrom(objectClass)) {
			return EMF_ATTRIBUTE_ICON;
		} else if (EMFObjectIndividualReferenceObjectPropertyValue.class.isAssignableFrom(objectClass)) {
			return EMF_REFERENCE_ICON;
		}
		logger.warning("No icon for " + objectClass);
		return null;
	}

}
