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
package org.openflexo.technologyadapter.xsd.gui;

import java.util.logging.Logger;

import javax.swing.ImageIcon;

import org.openflexo.technologyadapter.xsd.metamodel.XSDMetaModel;
import org.openflexo.technologyadapter.xsd.metamodel.XSOntClass;
import org.openflexo.technologyadapter.xsd.metamodel.XSOntDataProperty;
import org.openflexo.technologyadapter.xsd.metamodel.XSOntObjectProperty;
import org.openflexo.technologyadapter.xsd.metamodel.XSOntProperty;
import org.openflexo.technologyadapter.xsd.model.AbstractXSOntObject;
import org.openflexo.technologyadapter.xsd.model.XMLModel;
import org.openflexo.technologyadapter.xsd.model.XSOntIndividual;
import org.openflexo.toolbox.ImageIconResource;

public class XSDIconLibrary {

	private static final Logger logger = Logger.getLogger(XSDIconLibrary.class.getPackage().getName());

	public static final ImageIconResource XSD_TECHNOLOGY_BIG_ICON = new ImageIconResource("Icons/XSDTechnology32.png");
	public static final ImageIconResource XSD_TECHNOLOGY_ICON = new ImageIconResource("Icons/XSDTechnology.png");
	public static final ImageIconResource XSD_FILE_ICON = new ImageIconResource("Icons/XSDFile.png");
	public static final ImageIconResource XML_FILE_ICON = new ImageIconResource("Icons/XMLFile.png");
	public static final ImageIconResource XSD_CLASS_ICON = new ImageIconResource("Icons/XSDClass.png");
	public static final ImageIconResource XSD_INDIVIDUAL_ICON = new ImageIconResource("Icons/XSDIndividual.png");
	public static final ImageIconResource XSD_PROPERTY_ICON = new ImageIconResource("Icons/XSDProperty.png");
	public static final ImageIconResource XSD_DATAPROPERTY_ICON = new ImageIconResource("Icons/XSDDataProperty.png");
	public static final ImageIconResource XSD_OBJECTPROPERTY_ICON = new ImageIconResource("Icons/XSDObjectProperty.png");

	public static ImageIcon iconForObject(Class<? extends AbstractXSOntObject> objectClass) {
		if (XSDMetaModel.class.isAssignableFrom(objectClass)) {
			return XSD_FILE_ICON;
		} else if (XMLModel.class.isAssignableFrom(objectClass)) {
			return XML_FILE_ICON;
		} else if (XSOntClass.class.isAssignableFrom(objectClass)) {
			return XSD_CLASS_ICON;
		} else if (XSOntIndividual.class.isAssignableFrom(objectClass)) {
			return XSD_INDIVIDUAL_ICON;
		} else if (XSOntDataProperty.class.isAssignableFrom(objectClass)) {
			return XSD_DATAPROPERTY_ICON;
		}else if (XSOntObjectProperty.class.isAssignableFrom(objectClass)) {
			return XSD_OBJECTPROPERTY_ICON;
		}
		logger.warning("No icon for " + objectClass);
		return null;
	}
}
