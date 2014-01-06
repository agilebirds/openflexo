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
package org.openflexo.foundation.xml;

/*
 * XMLUtils.java
 * Project WorkflowEditor
 *
 * Created by benoit on Mar 1, 2004
 */

import java.io.File;
import java.io.FileInputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openflexo.foundation.FlexoProject;
import org.openflexo.foundation.wkf.node.AbstractNode;
import org.openflexo.xmlcode.XMLDecoder;
import org.openflexo.xmlcode.XMLMapping;

/**
 * Some static util methods for xml parsing.
 * 
 * @author benoit, sylvain
 */
public class XMLUtils2 {

	private static final Logger logger = Logger.getLogger(XMLUtils2.class.getPackage().getName());

	public static AbstractNode getNodeFromFile(File xmlFile, FlexoProject project) {
		AbstractNode node;
		try {
			if (logger.isLoggable(Level.FINE)) {
				logger.fine("Trying to load " + xmlFile.getAbsolutePath());
			}
			XMLMapping wkfMapping = project.getXmlMappings().getWKFMapping();
			node = (AbstractNode) XMLDecoder.decodeObjectWithMapping(new FileInputStream(xmlFile), wkfMapping, new FlexoProcessBuilder(
					project), project.getStringEncoder());
			if (logger.isLoggable(Level.FINE)) {
				logger.fine("Succeeded loading palette element: " + xmlFile.getName());
			}
		} catch (Exception e) {
			if (logger.isLoggable(Level.WARNING)) {
				logger.warning("Failed loading palette element: " + xmlFile.getAbsolutePath());
			}
			e.printStackTrace();
			node = null;
		}
		return node;

	}

}
