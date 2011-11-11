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
package org.openflexo.drm.helpset;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.parsers.ParserConfigurationException;

import org.jdom.DocType;
import org.openflexo.drm.DocItem;
import org.openflexo.drm.DocResourceCenter;
import org.openflexo.drm.Language;
import org.openflexo.foundation.FlexoObject;
import org.openflexo.toolbox.FileResource;
import org.openflexo.xmlcode.InvalidModelException;
import org.openflexo.xmlcode.XMLCoder;
import org.openflexo.xmlcode.XMLMapping;
import org.openflexo.xmlcode.XMLSerializable;
import org.xml.sax.SAXException;

public class HSIndex extends FlexoObject implements XMLSerializable {

	private static final Logger logger = Logger.getLogger(HSIndex.class.getPackage().getName());

	private DocResourceCenter _drc;
	private Language _language;
	private Vector<HSIndexEntry> _rootEntries;
	public String version = "1.0";
	private File _indexFile;

	private HelpSetConfiguration configuration;

	public HSIndex(DocResourceCenter drc, Language language, File indexFile, HelpSetConfiguration config) {
		_drc = drc;
		_language = language;
		_indexFile = indexFile;
		configuration = config;
		getRootEntries();
	}

	public Vector<HSIndexEntry> getRootEntries() {
		if (_rootEntries == null) {
			_rootEntries = new Vector<HSIndexEntry>();
			for (DocItem next : _drc.getAllItems()) {
				if (next.isIncluded(configuration)) {
					if ((next.getInheritanceParentItem() == null) && (!next.getIsEmbedded()) && (next.isPublished())) {
						logger.fine("Generate index entry for " + next);
						_rootEntries.add(new HSIndexEntry(next));
					}
				} else {
					logger.fine("Ignoring " + next);
				}
			}
		}
		return _rootEntries;
	}

	public class HSIndexEntry extends FlexoObject implements XMLSerializable {
		public DocItem docItem;
		public Vector entryChilds;
		public String text;

		public HSIndexEntry(DocItem docItem) {
			this.docItem = docItem;
			text = docItem.getTitle(_language);
			if (text == null) {
				text = docItem.getIdentifier();
			}

			entryChilds = new Vector();
			for (Enumeration en = docItem.getInheritanceChildItems().elements(); en.hasMoreElements();) {
				DocItem next = (DocItem) en.nextElement();
				if (next.isPublished())
					entryChilds.add(new HSIndexEntry(next));
			}

		}
	}

	protected void generate() {
		try {
			FileOutputStream out = new FileOutputStream(_indexFile);
			XMLCoder.encodeObjectWithMapping(this, getIndexMapping(), out, getIndexDocType());
			out.flush();
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static XMLMapping _indexMapping;

	public static XMLMapping getIndexMapping() {
		if (_indexMapping == null) {
			File hsIndexModelFile;
			hsIndexModelFile = new FileResource("Models/HSIndexModel.xml");
			if (!hsIndexModelFile.exists()) {
				if (logger.isLoggable(Level.WARNING))
					logger.warning("File " + hsIndexModelFile.getAbsolutePath() + " doesn't exist. Maybe you have to check your paths ?");
				return null;
			} else {
				try {
					_indexMapping = new XMLMapping(hsIndexModelFile);
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
		}
		return _indexMapping;
	}

	public static DocType getIndexDocType() {
		return new DocType("index", "-//Sun Microsystems Inc.//DTD JavaHelp Index Version 1.0//EN",
				"http://java.sun.com/products/javahelp/index_1_0.dtd");
	}

}
