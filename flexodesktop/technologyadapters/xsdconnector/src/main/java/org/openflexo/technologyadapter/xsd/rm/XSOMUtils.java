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
package org.openflexo.technologyadapter.xsd.rm;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;

import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import com.sun.xml.xsom.XSSchemaSet;
import com.sun.xml.xsom.parser.XSOMParser;

public class XSOMUtils {

	private static final java.util.logging.Logger logger = org.openflexo.logging.FlexoLogger.getLogger(XSOMUtils.class.getPackage()
			.getName());

	public static XSSchemaSet read(File xsdFile) {
		// TODO: check if all XML files should be read in the same way
		if (logger.isLoggable(Level.INFO)) {
			logger.info("Loading an XML file " + xsdFile.getName());
		}
		XSOMParser parser = new XSOMParser();
		parser.setErrorHandler(new ErrorHandler() {

			@Override
			public void error(SAXParseException exception) throws SAXException {
				if (logger.isLoggable(Level.WARNING)) {
					logger.warning("XSOM-Error: " + exception.getMessage());
				}
			}

			@Override
			public void fatalError(SAXParseException exception) throws SAXException {
				if (logger.isLoggable(Level.WARNING)) {
					logger.warning("XSOM-Fatal: " + exception.getMessage());
				}
			}

			@Override
			public void warning(SAXParseException exception) throws SAXException {
				if (logger.isLoggable(Level.WARNING)) {
					logger.warning("XSOM-Warning: " + exception.getMessage());
				}
			}

		});

		try {
			parser.parse(xsdFile);
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		XSSchemaSet result = null;
		try {
			result = parser.getResult();
		} catch (SAXException e) {
			e.printStackTrace();
		}
		return result;
	}

}
