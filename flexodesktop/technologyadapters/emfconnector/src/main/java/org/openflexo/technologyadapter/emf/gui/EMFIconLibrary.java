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
import org.openflexo.toolbox.ImageIconResource;

public class EMFIconLibrary {

	private static final Logger logger = Logger.getLogger(EMFIconLibrary.class.getPackage().getName());

	public static final ImageIconResource EMF_TECHNOLOGY_BIG_ICON = new ImageIconResource("src/main/resources/Icons/EMFTechnology32.png");
	public static final ImageIconResource EMF_TECHNOLOGY_ICON = new ImageIconResource("src/main/resources/Icons/EMFTechnology.png");
	public static final ImageIconResource XSD_FILE_ICON = new ImageIconResource("src/main/resources/Icons/XSDFile.png");
	public static final ImageIconResource XML_FILE_ICON = new ImageIconResource("src/main/resources/Icons/XMLFile.png");
	public static final ImageIconResource XSD_CLASS_ICON = new ImageIconResource("src/main/resources/Icons/OntologyClass.png");
	public static final ImageIconResource XSD_INDIVIDUAL_ICON = new ImageIconResource("src/main/resources/Icons/OntologyIndividual.png");
	public static final ImageIconResource XSD_PROPERTY_ICON = new ImageIconResource("src/main/resources/Icons/OntologyObjectProperty.png");

	public static ImageIcon iconForObject(Class<? extends IFlexoOntologyObject> objectClass) {
		/*if (XSDMetaModel.class.isAssignableFrom(objectClass)) {
			return XSD_FILE_ICON;
		} else if (XMLModel.class.isAssignableFrom(objectClass)) {
			return XML_FILE_ICON;
		} else if (XSOntClass.class.isAssignableFrom(objectClass)) {
			return XSD_CLASS_ICON;
		} else if (XSOntIndividual.class.isAssignableFrom(objectClass)) {
			return XSD_INDIVIDUAL_ICON;
		} else if (XSOntProperty.class.isAssignableFrom(objectClass)) {
			return XSD_PROPERTY_ICON;
		}*/
		logger.warning("No icon for " + objectClass);
		return null;
	}
}
