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
import java.io.IOException;
import java.util.Enumeration;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jdom2.DocType;
import org.openflexo.drm.DocItem;
import org.openflexo.drm.DocItemFolder;
import org.openflexo.drm.DocResourceCenter;
import org.openflexo.foundation.KVCFlexoObject;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.localization.Language;
import org.openflexo.toolbox.FileUtils;

public class HSMap extends KVCFlexoObject {

	protected static final Logger logger = Logger.getLogger(HSMap.class.getPackage().getName());

	private final DocResourceCenter _drc;
	protected Language _language;
	private Vector<HSMapEntry> _entries;
	public String version = "1.0";
	protected File _mapFile;
	org.openflexo.localization.Language lang;

	protected HelpSetConfiguration configuration;

	public HSMap(DocResourceCenter drc, Language language, File mapFile, HelpSetConfiguration config) {
		_drc = drc;
		_language = language;
		_mapFile = mapFile;
		lang = org.openflexo.localization.Language.get(_language.getIdentifier());
		configuration = config;
		getEntries();
	}

	public Vector<HSMapEntry> getEntries() {
		if (_entries == null) {
			_entries = new Vector<HSMapEntry>();
			_entries.add(new HSMapEntry(DRMHelpSet.TOP_LEVEL_FOLDER, DRMHelpSet.TOP_LEVEL_IMAGE));
			for (DocItem next : _drc.getAllItems()) {
				if (next.isIncluded(configuration)) {
					if (!next.getIsEmbedded() && next.isPublished()) {
						logger.fine("Generate map entry for " + next);
						_entries.add(new HSMapEntry(next));
					}
				} else {
					if (logger.isLoggable(Level.FINE)) {
						logger.fine("Ignoring " + next);
					}
				}
			}
		}
		return _entries;
	}

	public class HSMapEntry extends KVCFlexoObject {
		public DocItem docItem;
		public String url;
		public String target;

		public HSMapEntry(DocItem docItem) {
			this.docItem = docItem;
			url = "HTML/" + docItem.getRelativePath() + ".html";
			target = docItem.getIdentifier();
		}

		public HSMapEntry(String aTarget, String url) {
			target = aTarget;
			this.url = url;
		}

		public void generate() {
			if (docItem != null) {
				File fileToGenerate = new File(_mapFile.getParentFile(), url);
				fileToGenerate.getParentFile().mkdirs();
				StringBuffer contents = new StringBuffer();
				String title;
				if (docItem.getTitle(_language) != null) {
					title = docItem.getTitle(_language);
				} else {
					title = docItem.getIdentifier();
				}
				StringBuilder sb = new StringBuilder();
				DocItemFolder folder = docItem.getFolder();
				while (folder != null && !folder.getDirectory().equals(docItem.getDocResourceCenter().getFolder().getDirectory())) {
					sb.append("../");
					folder = folder.getFolder();
				}
				contents.append("<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.0.1//EN\">\n");
				contents.append("<HTML>\n");
				contents.append("<HEAD>\n");
				contents.append("<TITLE>");
				contents.append(title);
				contents.append("</TITLE>\n");
				contents.append("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\">\n");
				contents.append("<link rel=\"stylesheet\" type=\"text/css\" href=\"").append(sb).append("FlexoHelpMasterStyle.css\">\n");
				contents.append("</HEAD>\n");
				contents.append("<BODY BGCOLOR=\"#ffffff\">\n");
				contents.append("<H1>" + title + "</H1>\n");
				contents.append("<HR>\n");

				String inheritanceText = "";
				DocItem inheritanceParent = docItem.getInheritanceParentItem();
				DocItem firstNonHiddenParent = null;
				while (inheritanceParent != null) {
					if (!inheritanceParent.isPublished()) {
						if (inheritanceParent.getLastApprovedActionForLanguage(_language) != null) {
							inheritanceText = inheritanceParent.getLastApprovedActionForLanguage(_language).getVersion() + "<br>"
									+ inheritanceText;
						}
					} else if (firstNonHiddenParent == null) {
						firstNonHiddenParent = inheritanceParent;
					}
					inheritanceParent = inheritanceParent.getInheritanceParentItem();
				}
				contents.append(inheritanceText);

				if (docItem.getLastApprovedActionForLanguage(_language) != null) {
					contents.append(docItem.getLastApprovedActionForLanguage(_language).getVersion());
				} else {
					contents.append(FlexoLocalization.localizedForKeyAndLanguage("no_documentation", lang));
				}

				/*for (Enumeration en=docItem.getEmbeddingChildItems().elements(); en.hasMoreElements();) {
				   DocItem next = (DocItem)en.nextElement();
				   if (next.isIncluded(configuration)) {
					   if (next.getIsEmbedded()) {
						   String subItemTitle;
						   if (next.getTitle(_language) != null) {
							   subItemTitle = next.getTitle(_language);
						   }
						   else {
							   subItemTitle = next.getIdentifier();
						   }
						   contents.append("<H2>"+subItemTitle+"</H2>\n");
						   if (next.getLastApprovedActionForLanguage(_language) != null) {
							   contents.append(next.getLastApprovedActionForLanguage(_language).getVersion().getFullHTMLDescription());
						   }
						   else {
							   contents.append(FlexoLocalization.localizedForKeyAndLanguage("no_documentation",lang));
						   }
					   }
				   }
				}*/

				DocItem currentDocItem = docItem;
				while (currentDocItem != null) {
					boolean hasEmbeddedItemsToInclude = false;
					StringBuffer embeddedItemsAtThisLevel = new StringBuffer();
					for (Enumeration en = currentDocItem.getEmbeddingChildItems().elements(); en.hasMoreElements();) {
						DocItem next = (DocItem) en.nextElement();
						if (next.isIncluded(configuration)) {
							if (next.getIsEmbedded() && next.isPublished()) {
								hasEmbeddedItemsToInclude = true;
								String subItemTitle;
								if (next.getTitle(_language) != null) {
									subItemTitle = next.getTitle(_language);
								} else {
									subItemTitle = next.getIdentifier();
								}
								embeddedItemsAtThisLevel.append("<H3>" + subItemTitle + "</H3>\n");
								if (next.getLastApprovedActionForLanguage(_language) != null) {
									embeddedItemsAtThisLevel.append(next.getLastApprovedActionForLanguage(_language));
								}/*
									else {
									   embeddedItemsAtThisLevel.append(FlexoLocalization.localizedForKeyAndLanguage("no_documentation",lang));
									}*/
							}
						}
					}
					if (hasEmbeddedItemsToInclude) {
						if (currentDocItem == docItem) { // We are at this level
							if (embeddedItemsAtThisLevel.length() > 0) {
								contents.append("<H2>" + FlexoLocalization.localizedForKeyAndLanguage("declared_attributes", lang)
										+ "</H2>\n");
								contents.append(embeddedItemsAtThisLevel.toString());
							}
						} else { // Inherited items
							if (embeddedItemsAtThisLevel.length() > 0) {
								String inheritedItemsTitle;
								if (!currentDocItem.isPublished()) {
									inheritedItemsTitle = currentDocItem.getTitle(_language);
								} else {
									inheritedItemsTitle = currentDocItem.getHTMLLinkFrom(docItem, _language);
								}
								contents.append("<H2>" + FlexoLocalization.localizedForKeyAndLanguage("attributes_inherited_from", lang)
										+ " " + inheritedItemsTitle + "</H2>\n");
								contents.append(embeddedItemsAtThisLevel.toString());
							}
						}
					}

					currentDocItem = currentDocItem.getInheritanceParentItem();
				}

				contents.append("<HR>\n");
				String inheritanceParentHTMLFragment = getInheritanceParentHTMLFragment(firstNonHiddenParent);
				if (inheritanceParentHTMLFragment != null) {
					contents.append(inheritanceParentHTMLFragment);
				}
				String embeddingParentHTMLFragment = getEmbeddingParentHTMLFragment();
				if (embeddingParentHTMLFragment != null) {
					contents.append(embeddingParentHTMLFragment);
				}
				String inheritanceChildsHTMLFragment = getInheritanceChildsHTMLFragment();
				if (inheritanceChildsHTMLFragment != null) {
					contents.append(inheritanceChildsHTMLFragment);
				}
				String embeddingChildsHTMLFragment = getEmbeddingChildsHTMLFragment();
				if (embeddingChildsHTMLFragment != null) {
					contents.append(embeddingChildsHTMLFragment);
				}
				String relatedToItemsHTMLFragment = getRelatedToItemsHTMLFragment();
				if (relatedToItemsHTMLFragment != null) {
					contents.append(relatedToItemsHTMLFragment);
				}

				contents.append("\n</BODY>\n");
				contents.append("</HTML>\n");
				try {
					FileUtils.saveToFile(fileToGenerate, contents.toString());
				} catch (IOException e) {
					// Warns about the exception
					logger.warning("Exception raised: " + e.getClass().getName() + " when writing " + fileToGenerate.getAbsolutePath()
							+ ". See console for details.");
					e.printStackTrace();
				}
			}
		}

		private String getInheritanceParentHTMLFragment(DocItem firstNonHiddenParent) {
			if (firstNonHiddenParent == null) {
				return null;
			}
			StringBuffer returned = new StringBuffer();
			returned.append("<p>");
			returned.append("<b>" + FlexoLocalization.localizedForKeyAndLanguage("extends", lang) + "</b> ");
			returned.append(firstNonHiddenParent.getHTMLLinkFrom(docItem, _language));
			returned.append("</p>");
			return returned.toString();
		}

		private String getEmbeddingParentHTMLFragment() {
			if (docItem.getEmbeddingParentItem() == null) {
				return null;
			}
			StringBuffer returned = new StringBuffer();
			returned.append("<p>");
			returned.append("<b>" + FlexoLocalization.localizedForKeyAndLanguage("found_in", lang) + "</b> ");
			returned.append(docItem.getEmbeddingParentItem().getHTMLLinkFrom(docItem, _language));
			returned.append("</p>");
			return returned.toString();
		}

		private String getInheritanceChildsHTMLFragment() {
			int inheritanceChildItemCount = 0;
			for (DocItem aDocItem : docItem.getInheritanceChildItems()) {
				if (aDocItem.isIncluded(configuration)) {
					inheritanceChildItemCount++;
				}
			}
			if (inheritanceChildItemCount == 0) {
				return null;
			}
			StringBuffer returned = new StringBuffer();
			returned.append("<p>");
			returned.append("<b>" + FlexoLocalization.localizedForKeyAndLanguage("inheritance_child_items", lang) + "</b> ");
			boolean isFirst = true;
			for (Enumeration en = docItem.getDerivedInheritanceChildItems().elements(); en.hasMoreElements();) {
				DocItem next = (DocItem) en.nextElement();
				if (next.isPublished()) {
					returned.append((isFirst ? "" : ", ") + next.getHTMLLinkFrom(docItem, _language));
					isFirst = false;
				}
			}
			returned.append("</p>");
			return returned.toString();
		}

		private String getEmbeddingChildsHTMLFragment() {
			Vector<DocItem> embeddedChilds = new Vector<DocItem>();
			for (DocItem next : docItem.getEmbeddingChildItems()) {
				if (!next.getIsEmbedded()) {
					embeddedChilds.add(next);
				}
			}
			if (embeddedChilds.size() == 0) {
				return null;
			}
			StringBuffer returned = new StringBuffer();
			returned.append("<p>");
			returned.append("<b>" + FlexoLocalization.localizedForKeyAndLanguage("embedding_child_items", lang) + "</b> ");
			boolean isFirst = true;
			for (Enumeration en = embeddedChilds.elements(); en.hasMoreElements();) {
				DocItem next = (DocItem) en.nextElement();
				if (next.isPublished()) {
					returned.append((isFirst ? "" : ", ") + next.getHTMLLinkFrom(docItem, _language));
					isFirst = false;
				}
			}
			returned.append("</p>");
			return returned.toString();
		}

		private String getRelatedToItemsHTMLFragment() {
			if (docItem.getRelatedToItems().size() == 0) {
				return null;
			}
			StringBuffer returned = new StringBuffer();
			returned.append("<p>");
			returned.append("<b>" + FlexoLocalization.localizedForKeyAndLanguage("related_to_items", lang) + "</b> ");
			boolean isFirst = true;
			for (Enumeration en = docItem.getRelatedToItems().elements(); en.hasMoreElements();) {
				DocItem next = (DocItem) en.nextElement();
				if (next.isPublished()) {
					returned.append((isFirst ? "" : ", ") + next.getHTMLLinkFrom(docItem, _language));
					isFirst = false;
				}
			}
			returned.append("</p>");
			return returned.toString();
		}
	}

	protected void generate() {
		/*try {
			FileOutputStream out = new FileOutputStream(_mapFile);
			XMLCoder.encodeObjectWithMapping(this, getMapMapping(), out, getMapDocType());
			out.flush();
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		for (Enumeration en = getEntries().elements(); en.hasMoreElements();) {
			HSMapEntry next = (HSMapEntry) en.nextElement();
			// if (next.docItem.isIncluded(configuration)) {
			next.generate();
			// }
		}*/
	}

	/*private static XMLMapping _mapMapping;

	public static XMLMapping getMapMapping() {
		if (_mapMapping == null) {
			File hsMapModelFile;
			hsMapModelFile = new FileResource("Models/HSMapModel.xml");
			if (!hsMapModelFile.exists()) {
				if (logger.isLoggable(Level.WARNING)) {
					logger.warning("File " + hsMapModelFile.getAbsolutePath() + " doesn't exist. Maybe you have to check your paths ?");
				}
				return null;
			} else {
				try {
					_mapMapping = new XMLMapping(hsMapModelFile);
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
		return _mapMapping;
	}*/

	public static DocType getMapDocType() {
		return new DocType("map", "-//Sun Microsystems Inc.//DTD JavaHelp Map Version 1.0//EN",
				"http://java.sun.com/products/javahelp/map_1_0.dtd");
	}

}
