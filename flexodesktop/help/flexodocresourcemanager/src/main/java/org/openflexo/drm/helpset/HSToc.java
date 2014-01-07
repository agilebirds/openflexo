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
import java.util.Enumeration;
import java.util.Vector;
import java.util.logging.Logger;

import org.jdom2.DocType;
import org.openflexo.drm.DocItem;
import org.openflexo.drm.DocItemFolder;
import org.openflexo.drm.DocResourceCenter;
import org.openflexo.foundation.KVCFlexoObject;
import org.openflexo.localization.Language;

public class HSToc extends KVCFlexoObject {

	protected static final Logger logger = Logger.getLogger(HSToc.class.getPackage().getName());

	private final DocResourceCenter _drc;
	protected Language _language;
	private final HSTocEntry _rootEntry;
	public String version = "2.0";
	private final File _tocFile;

	protected HelpSetConfiguration configuration;

	public HSToc(DocResourceCenter drc, Language language, File tocFile, HelpSetConfiguration config) {
		_drc = drc;
		_language = language;
		_tocFile = tocFile;
		configuration = config;
		_rootEntry = new HSTocEntry(drc.getFolder());
		_rootEntry.image = "toplevelfolder";
		_rootEntry.expand = true;
		for (HSTocEntry next : _rootEntry.childs) {
			next.expand = true;
		}
	}

	public HSTocEntry getRootEntry() {
		return _rootEntry;
	}

	public class HSTocEntry extends KVCFlexoObject {
		public DocItem docItem;
		public Vector<HSTocEntry> childs;
		public String image;
		public String text;
		public boolean expand = false;
		public String presentationtype;
		public String presentationname;

		public String getText() {
			String returned = docItem.getTitle(_language);
			if (returned == null) {
				returned = docItem.getIdentifier();
			}
			return returned;
		}

		public String getTarget() {
			// if (image != null) return null;
			return docItem.getIdentifier();
		}

		public HSTocEntry(DocItemFolder folder) {
			if (folder.getPrimaryDocItem() == null) {
				logger.warning("Folder: " + folder.getIdentifier() + " has no primary doc item defined. Creates a default one.");
				folder.createDefaultPrimaryDocItem();
			}
			docItem = folder.getPrimaryDocItem();
			text = docItem.getTitle(_language);
			if (text == null) {
				text = docItem.getIdentifier();
			}
			childs = new Vector<HSTocEntry>();
			for (Enumeration en = docItem.getOrderedEmbeddingChildItems().elements(); en.hasMoreElements();) {
				DocItem next = (DocItem) en.nextElement();
				if (next.isIncluded(configuration)) {
					if (!next.getIsEmbedded() && next.isPublished() && next.getFolder() == folder) {
						childs.add(new HSTocEntry(next));
					}
				}
			}
			for (Enumeration en = folder.getOrderedItems().elements(); en.hasMoreElements();) {
				DocItem next = (DocItem) en.nextElement();
				if (next.isIncluded(configuration)) {
					if (next.getEmbeddingParentItem() == null && next != folder.getPrimaryDocItem() && next.isPublished()) {
						childs.add(new HSTocEntry(next));
					}
				}
			}
			for (Enumeration en = folder.getOrderedChildFolders().elements(); en.hasMoreElements();) {
				DocItemFolder next = (DocItemFolder) en.nextElement();
				if (next.isIncluded(configuration) && next.getPrimaryDocItem() != null && next.getPrimaryDocItem().isPublished()) {
					logger.fine("Generate toc entry for " + next);
					childs.add(new HSTocEntry(next));
				} else {
					logger.fine("Ignore " + next);
				}
			}

		}

		public HSTocEntry(DocItem item) {
			docItem = item;
			text = docItem.getTitle(_language);
			if (text == null) {
				text = docItem.getIdentifier();
			}
			childs = new Vector<HSTocEntry>();
			for (Enumeration en = item.getOrderedEmbeddingChildItems().elements(); en.hasMoreElements();) {
				DocItem next = (DocItem) en.nextElement();
				if (next.isIncluded(configuration)) {
					if (!next.getIsEmbedded() && next.isPublished()) {
						childs.add(new HSTocEntry(next));
					}
				}
			}
		}
	}

	protected void generate() {
		/*try {
			FileOutputStream out = new FileOutputStream(_tocFile);
			XMLCoder.encodeObjectWithMapping(this, getTocMapping(), out, getTocDocType());
			out.flush();
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
		}*/
	}

	/*	private static XMLMapping _tocMapping;

		public static XMLMapping getTocMapping() {
			if (_tocMapping == null) {
				File hsTocModelFile;
				hsTocModelFile = new FileResource("Models/HSTocModel.xml");
				if (!hsTocModelFile.exists()) {
					if (logger.isLoggable(Level.WARNING)) {
						logger.warning("File " + hsTocModelFile.getAbsolutePath() + " doesn't exist. Maybe you have to check your paths ?");
					}
					return null;
				} else {
					try {
						_tocMapping = new XMLMapping(hsTocModelFile);
					} catch (InvalidModelException e) {
						// Warns about the exception
						if (logger.isLoggable(Level.WARNING)) {
							logger.warning("Exception raised: " + e.getClass().getName() + ". See console for details.");
						}
						e.printStackTrace();
					} catch (IOException e) {
						// Warns about the exception
						if (logger.isLoggable(Level.WARNING)) {
							logger.warning("Exception raised: " + e.getClass().getName() + ". See console for details.");
						}
						e.printStackTrace();
					} catch (SAXException e) {
						// Warns about the exception
						if (logger.isLoggable(Level.WARNING)) {
							logger.warning("Exception raised: " + e.getClass().getName() + ". See console for details.");
						}
						e.printStackTrace();
					} catch (ParserConfigurationException e) {
						// Warns about the exception
						if (logger.isLoggable(Level.WARNING)) {
							logger.warning("Exception raised: " + e.getClass().getName() + ". See console for details.");
						}
						e.printStackTrace();
					}
				}
			}
			return _tocMapping;
		}*/

	public static DocType getTocDocType() {
		return new DocType("toc", "-//Sun Microsystems Inc.//DTD JavaHelp TOC Version 2.0//EN", "../dtd/toc_2_0.dtd");
	}

}
