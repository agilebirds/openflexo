/*
 * (c) Copyright 2010-2012 AgileBirds
 * (c) Copyright 2012-2013 Openflexo
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


package org.openflexo.technologyadapter.xml.gui;

import java.util.logging.Logger;

import javax.swing.ImageIcon;

import org.openflexo.technologyadapter.xml.model.XMLIndividual;
import org.openflexo.technologyadapter.xml.model.XMLModel;
import org.openflexo.toolbox.ImageIconResource;

public class XMLIconLibrary {

	private static final Logger logger = Logger.getLogger(XMLIconLibrary.class.getPackage().getName());

	public static final ImageIconResource XSD_TECHNOLOGY_BIG_ICON = new ImageIconResource("Icons/XMLTechnology32.png");
	public static final ImageIconResource XSD_TECHNOLOGY_ICON = new ImageIconResource("Icons/XMLTechnology.png");
	public static final ImageIconResource XML_FILE_ICON = new ImageIconResource("Icons/XMLFile.png");
	public static final ImageIconResource XML_INDIVIDUAL_ICON = new ImageIconResource("Icons/XMLIndividual.png");

	public static ImageIcon iconForObject(Class objectClass) {
		if (XMLModel.class.isAssignableFrom(objectClass)) {
			return XML_FILE_ICON;
		} else if (XMLIndividual.class.isAssignableFrom(objectClass)) {
			return XML_INDIVIDUAL_ICON;
		}
		logger.warning("No icon for " + objectClass);
		return null;
	}
}
