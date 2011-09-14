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
package org.openflexo.sg.generationdef;

import java.io.File;
import java.io.IOException;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.parsers.ParserConfigurationException;

import org.openflexo.toolbox.FileResource;
import org.openflexo.xmlcode.InvalidModelException;
import org.openflexo.xmlcode.XMLDecoder;
import org.openflexo.xmlcode.XMLMapping;
import org.openflexo.xmlcode.XMLSerializable;
import org.xml.sax.SAXException;


/**
 * This class contains the parsing result of the main.xml file from a technology module (main.xml is generated from main.xml.vm)
 * 
 * @author Nicolas Daniels
 */
public class GenerationDefinition implements XMLSerializable {

	private static final Logger logger = Logger.getLogger(GenerationDefinition.class.getPackage().getName());

	public String timestamp;
	public Vector<SymbolicPathEntry> symbolicPaths;
	public Vector<FileEntry> files;

	/**
	 * Parse an XML file and retrieve the according generation definition.
	 * 
	 * @param generationResult
	 * @return the created GenerationDefinition
	 */
	public static GenerationDefinition retrieveGenerationDefinition(String generationResult) {
		try {
			GenerationDefinition returned = (GenerationDefinition) XMLDecoder.decodeObjectWithMapping(generationResult, get_GENERATION_MODEL());
			return returned;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
		}
		return null;
	}

	// Used during deserialization
	public GenerationDefinition() {
	}

	private static XMLMapping GENERATION_MODEL;

	protected static XMLMapping get_GENERATION_MODEL() {
		if (GENERATION_MODEL == null) {
			File generationModelFile = new FileResource("Models/GenerationDefinitionModel.xml");
			try {
				GENERATION_MODEL = new XMLMapping(generationModelFile);
			} catch (InvalidModelException e) {
				// Warns about the exception
				if (logger.isLoggable(Level.WARNING))
					logger.warning("Exception raised: " + e.getClass().getName() + ". See console for details.");
				e.printStackTrace();
			} catch (IOException e) {
				// Warns about the exception
				if (logger.isLoggable(Level.WARNING))
					logger.warning("Exception raised: " + e.getClass().getName() + ". See console for details.");
				e.printStackTrace();
			} catch (SAXException e) {
				// Warns about the exception
				if (logger.isLoggable(Level.WARNING))
					logger.warning("Exception raised: " + e.getClass().getName() + ". See console for details.");
				e.printStackTrace();
			} catch (ParserConfigurationException e) {
				// Warns about the exception
				if (logger.isLoggable(Level.WARNING))
					logger.warning("Exception raised: " + e.getClass().getName() + ". See console for details.");
				e.printStackTrace();
			}
		}
		return GENERATION_MODEL;
	}
}
