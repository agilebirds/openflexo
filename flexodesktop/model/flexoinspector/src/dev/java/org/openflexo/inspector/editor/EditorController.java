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
package org.openflexo.inspector.editor;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Hashtable;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openflexo.inspector.model.InspectorModel;
import org.openflexo.toolbox.FileResource;
import org.openflexo.xmlcode.InvalidModelException;
import org.openflexo.xmlcode.StringEncoder;
import org.openflexo.xmlcode.XMLCoder;
import org.openflexo.xmlcode.XMLDecoder;
import org.openflexo.xmlcode.XMLMapping;

public class EditorController {

	private static final Logger logger = Logger.getLogger(EditorController.class.getPackage().getName());

	private Hashtable<String, InspectorModel> _inspectors;

	protected static XMLMapping _inspectorMapping;

	public EditorController() {
		_inspectors = new Hashtable<String, InspectorModel>();
	}

	public static XMLMapping getInspectorMapping() {
		if (_inspectorMapping == null) {
			// File mappingFile = new File
			// ("../FlexoInspector/Models/InspectorModel.xml");
			File mappingFile = new FileResource("Models/InspectorModel.xml");
			if (!mappingFile.exists()) {
				if (logger.isLoggable(Level.SEVERE)) {
					logger.severe("Could not find file: " + mappingFile.getAbsolutePath());
				}
			}
			try {
				_inspectorMapping = new XMLMapping(mappingFile);
			} catch (InvalidModelException e) {
				// Warns about the exception
				if (logger.isLoggable(Level.SEVERE)) {
					logger.severe("Exception raised: " + e + " for file " + mappingFile.getAbsolutePath() + ". See console for details.");
				}
				e.printStackTrace();
			} catch (Exception e) {
				// Warns about the exception
				if (logger.isLoggable(Level.SEVERE)) {
					logger.severe("Exception raised: " + e.getClass().getName() + " for file " + mappingFile.getAbsolutePath() + ". See console for details.");
				}
				e.printStackTrace();
			}
		}
		return _inspectorMapping;
	}

	public InspectorModel importInspectorFile(File inspectorFile) throws FileNotFoundException {
		try {
			if (getInspectorMapping() != null) {
				InspectorModel inspectorModel = (InspectorModel) XMLDecoder.decodeObjectWithMapping(new FileInputStream(inspectorFile),
						getInspectorMapping(), this);
				_inspectors.put(inspectorFile.getName(), inspectorModel);
				if (logger.isLoggable(Level.FINE)) {
					if (logger.isLoggable(Level.FINE)) {
						logger.fine("Getting this " + XMLCoder.encodeObjectWithMapping(inspectorModel, getInspectorMapping(),
								StringEncoder.getDefaultInstance()));
					}
				}
				return inspectorModel;
			}
		} catch (FileNotFoundException e) {
			throw e;
		} catch (Exception e) {
			if (logger.isLoggable(Level.WARNING)) {
				logger.warning("Exception raised during inspector import: " + e + "\nFile path is: " + inspectorFile.getAbsolutePath());
			}
			e.printStackTrace();
		}

		return null;
	}

}
