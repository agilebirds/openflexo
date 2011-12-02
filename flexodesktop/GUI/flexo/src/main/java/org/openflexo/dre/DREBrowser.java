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
package org.openflexo.dre;

import java.util.Enumeration;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openflexo.GeneralPreferences;
import org.openflexo.components.browser.BrowserConfiguration;
import org.openflexo.components.browser.BrowserElement;
import org.openflexo.components.browser.BrowserElementFactory;
import org.openflexo.components.browser.BrowserElementType;
import org.openflexo.components.browser.BrowserFilter.BrowserFilterStatus;
import org.openflexo.components.browser.ConfigurableProjectBrowser;
import org.openflexo.components.browser.ProjectBrowser;
import org.openflexo.drm.DRMObject;
import org.openflexo.drm.DocItem;
import org.openflexo.drm.DocItemFolder;
import org.openflexo.drm.DocResourceCenter;
import org.openflexo.drm.DocResourceManager;
import org.openflexo.drm.Language;
import org.openflexo.drm.dm.DRMDataModification;
import org.openflexo.foundation.DataModification;
import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.FlexoObservable;
import org.openflexo.foundation.param.DynamicDropDownParameter;
import org.openflexo.foundation.rm.FlexoProject;
import org.openflexo.view.controller.SelectionManagingController;

/**
 * Define what to put in the browser for this module
 * 
 * @author yourname
 * 
 */
public class DREBrowser extends ConfigurableProjectBrowser {

	protected static final Logger logger = Logger.getLogger(DREBrowser.class.getPackage().getName());

	/*  public static final Icon DOC_FOLDER_ICON = new ImageIconResource("Resources/IE/Folder.gif");
	  public static final Icon DOC_ITEM_ICON = new ImageIconResource("Resources/DRE/DocItem.gif");
	  public static final Icon UNDOCUMENTED_DOC_ITEM_ICON = new ImageIconResource("Resources/DRE/UndocumentedDocItem.gif");
	  public static final Icon APPROVING_PENDING_DOC_ITEM_ICON = new ImageIconResource("Resources/DRE/ApprovingPendingDocItem.gif");
	  public static final Icon AVAILABLE_NEW_VERSION_PENDING_DOC_ITEM_ICON = new ImageIconResource("Resources/DRE/AvailableNewVersionPendingDocItem.gif");
	*/

	// ================================================
	// ================= Variables ===================
	// ================================================

	protected SelectionManagingController _controller;

	private DynamicDropDownParameter<Language> _availableLanguages;

	// ================================================
	// ================ Constructor ===================
	// ================================================

	public DREBrowser(SelectionManagingController controller) {
		super(makeBrowserConfiguration(controller.getProject(), DocResourceManager.instance().getDocResourceCenter()), controller
				.getSelectionManager() /* Remove this parameter if you don't want browser synchronized with selection */);
		_controller = controller;
		DocResourceCenter drc = DocResourceManager.instance().getDocResourceCenter();
		Language currentLanguage = DocResourceManager.instance().getLanguage(GeneralPreferences.getLanguage());
		DocItem.COMPARATOR.currentLanguage = currentLanguage;
		_availableLanguages = new DynamicDropDownParameter<Language>("language", "language", drc.getLanguages(), currentLanguage) {

			@Override
			public void setValue(Language value) {
				super.setValue(value);
				update();
				DocItem.COMPARATOR.currentLanguage = value;
			}

		};
		_availableLanguages.setFormatter("localizedName");
		_availableLanguages.setShowReset(false);
		update();
	}

	public static BrowserConfiguration makeBrowserConfiguration(final FlexoProject project, final DocResourceCenter docResourceCenter) {
		BrowserConfiguration returned = new DREBrowserConfiguration(project, docResourceCenter);
		return returned;
	}

	private static class DREBrowserConfiguration implements BrowserConfiguration {
		private FlexoProject _project;
		private DREBrowserElementFactory _factory;
		protected DocResourceCenter _docResourceCenter;

		protected DREBrowserConfiguration(FlexoProject project, DocResourceCenter docResourceCenter) {
			super();
			_project = project;
			_docResourceCenter = docResourceCenter;
			_factory = new DREBrowserElementFactory();
		}

		@Override
		public FlexoProject getProject() {
			return _project;
		}

		@Override
		public void configure(ProjectBrowser browser) {
			_factory.configure(browser);
		}

		@Override
		public FlexoModelObject getDefaultRootObject() {
			return _docResourceCenter.getRootFolder();
		}

		@Override
		public BrowserElementFactory getBrowserElementFactory() {
			return _factory;
		}

		protected class DREBrowserElementFactory implements BrowserElementFactory {
			/* public BrowserElementType DOC_ITEM_FOLDER = new DocItemFolderElementType();
			 public DocItemElementType UNDOCUMENTED_DOC_ITEM = new UndocumentedDocItemElementType();
			 public DocItemElementType APPROVING_PENDING_DOC_ITEM = new ApprovingPendingDocItemElementType();
			 public DocItemElementType AVAILABLE_NEW_VERSION_PENDING_DOC_ITEM = new AvailableNewVersionPendingDocItemElementType();
			 public DocItemElementType UP_TO_DATE_DOC_ITEM = new UpToDateDocItemElementType();*/

			public void configure(ProjectBrowser browser) {
				browser.setFilterStatus(BrowserElementType.UNDOCUMENTED_DOC_ITEM, BrowserFilterStatus.OPTIONAL_INITIALLY_SHOWN);
				browser.setFilterStatus(BrowserElementType.APPROVING_PENDING_DOC_ITEM, BrowserFilterStatus.OPTIONAL_INITIALLY_SHOWN);
				browser.setFilterStatus(BrowserElementType.AVAILABLE_NEW_VERSION_PENDING_DOC_ITEM,
						BrowserFilterStatus.OPTIONAL_INITIALLY_SHOWN);
				browser.setFilterStatus(BrowserElementType.UP_TO_DATE_DOC_ITEM, BrowserFilterStatus.OPTIONAL_INITIALLY_SHOWN);
			}

			/*public class DocItemFolderElementType extends BrowserElementType
			{

			    public String getName()
			    {
			        return "doc_item_folder";
			    }

			    public Class getElementClass()
			    {
			        return DocItemFolderElement.class;
			    }

			    public Icon getIcon()
			    {
			        return DOC_FOLDER_ICON;
			    }
			}

			public abstract class DocItemElementType extends BrowserElementType
			{

			     public Class getElementClass()
			    {
			        return DocItemElement.class;
			    }

			    public Icon getIcon()
			    {
			        return DOC_ITEM_ICON;
			    }
			}

			public class UndocumentedDocItemElementType extends DocItemElementType
			{

			    public String getName()
			    {
			        return "undocumented_doc_item";
			    }

			    public Icon getIcon()
			    {
			        return UNDOCUMENTED_DOC_ITEM_ICON;
			    }
			}

			public class ApprovingPendingDocItemElementType extends DocItemElementType
			{

			    public String getName()
			    {
			        return "approving_pending_doc_item";
			    }

			    public Icon getIcon()
			    {
			        return APPROVING_PENDING_DOC_ITEM_ICON;
			    }
			}

			public class AvailableNewVersionPendingDocItemElementType extends DocItemElementType
			{

			    public String getName()
			    {
			        return "available_new_version_pending_doc_item";
			    }

			    public Icon getIcon()
			    {
			        return AVAILABLE_NEW_VERSION_PENDING_DOC_ITEM_ICON;
			    }
			}

			public class UpToDateDocItemElementType extends DocItemElementType
			{

			    public String getName()
			    {
			        return "available_uptodate_doc_item";
			    }

			    public Icon getIcon()
			    {
			        return DOC_ITEM_ICON;
			    }
			}
			*/

			@Override
			public BrowserElement makeNewElement(FlexoModelObject object, ProjectBrowser browser, BrowserElement parent) {
				if (object instanceof DocItemFolder) {
					return new DocItemFolderElement((DocItemFolder) object, browser, parent);
				} else if (object instanceof DocItem) {
					Language currentLanguage;
					if (browser instanceof DREBrowser) {
						currentLanguage = ((DREBrowser) browser).getActiveLanguage();
					} else {
						currentLanguage = DocResourceManager.instance().getLanguage(GeneralPreferences.getLanguage());
					}
					int status = ((DocItem) object).getStatusForLanguage(currentLanguage);
					if (status == DocItem.NO_DOCUMENTED) {
						return new DocItemElement((DocItem) object, BrowserElementType.UNDOCUMENTED_DOC_ITEM, browser, parent);
					} else if (status == DocItem.APPROVING_PENDING) {
						return new DocItemElement((DocItem) object, BrowserElementType.APPROVING_PENDING_DOC_ITEM, browser, parent);
					} else if (status == DocItem.AVAILABLE_NEWER_VERSION_PENDING) {
						return new DocItemElement((DocItem) object, BrowserElementType.AVAILABLE_NEW_VERSION_PENDING_DOC_ITEM, browser,
								parent);
					} else if (status == DocItem.AVAILABLE_UP_TO_DATE) {
						return new DocItemElement((DocItem) object, BrowserElementType.UP_TO_DATE_DOC_ITEM, browser, parent);
					}
					return null;
				}
				return null;
			}

			private abstract class AbstractDREElement extends BrowserElement {
				public AbstractDREElement(DRMObject object, BrowserElementType elementType, ProjectBrowser browser, BrowserElement parent) {
					super(object, elementType, browser, parent);
				}

				@Override
				public void update(FlexoObservable observable, DataModification dataModification) {
					if (logger.isLoggable(Level.FINE)) {
						logger.fine(getClass().getName() + " receive DataModification " + dataModification.getClass().getName());
					}
					if ((_browser != null) && (dataModification instanceof DRMDataModification)) {
						refreshWhenPossible();
					} else {
						super.update(observable, dataModification);
					}
				}

			}

			private class DocItemFolderElement extends AbstractDREElement {
				public DocItemFolderElement(DocItemFolder docItemFolder, ProjectBrowser browser, BrowserElement parent) {
					super(docItemFolder, BrowserElementType.DOC_ITEM_FOLDER, browser, parent);
				}

				@Override
				protected void buildChildrenVector() {
					for (Enumeration en = getDocItemFolder().getOrderedChildFolders().elements(); en.hasMoreElements();) {
						DocItemFolder next = (DocItemFolder) en.nextElement();
						addToChilds(next);
					}
					for (Enumeration en = getDocItemFolder().getOrderedItems().elements(); en.hasMoreElements();) {
						DocItem next = (DocItem) en.nextElement();
						if ((next.getEmbeddingParentItem() == null) || (next.getEmbeddingParentItem().getFolder() != next.getFolder())) {
							addToChilds(next);
						}
					}
				}

				protected DocItemFolder getDocItemFolder() {
					return (DocItemFolder) getObject();
				}

				@Override
				public String getName() {
					return getDocItemFolder().getIdentifier();
				}

			}

			private class DocItemElement extends AbstractDREElement {
				public DocItemElement(DocItem docItem, BrowserElementType docItemElementType, ProjectBrowser browser, BrowserElement parent) {
					super(docItem, docItemElementType, browser, parent);
				}

				@Override
				public String getName() {
					if (getProjectBrowser() instanceof DREBrowser) {
						String title = getDocItem().getTitle(((DREBrowser) getProjectBrowser()).getActiveLanguage());
						if (title != null && !title.trim().equals("")) {
							return getDocItem().getTitle(((DREBrowser) getProjectBrowser()).getActiveLanguage());
						}
					}
					return "<" + getDocItem().getIdentifier() + ">";
				}

				@Override
				protected void buildChildrenVector() {
					for (Enumeration en = getDocItem().getOrderedEmbeddingChildItems().elements(); en.hasMoreElements();) {
						DocItem next = (DocItem) en.nextElement();
						addToChilds(next);
					}
				}

				protected DocItem getDocItem() {
					return (DocItem) getObject();
				}

			}

		}
	}

	public DynamicDropDownParameter<Language> getAvailableLanguages() {
		return _availableLanguages;
	}

	public Language getActiveLanguage() {
		if (_availableLanguages != null) {
			return _availableLanguages.getValue();
		} else {
			return DocResourceManager.instance().getLanguage(GeneralPreferences.getLanguage());
		}
	}
}
