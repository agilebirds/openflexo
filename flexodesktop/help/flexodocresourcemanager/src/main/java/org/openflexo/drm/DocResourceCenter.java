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
package org.openflexo.drm;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Vector;

import org.openflexo.drm.dm.DocResourceCenterIsModified;
import org.openflexo.drm.dm.DocResourceCenterIsSaved;
import org.openflexo.foundation.rm.DuplicateResourceException;
import org.openflexo.foundation.rm.FlexoProject;
import org.openflexo.foundation.rm.FlexoResource;
import org.openflexo.foundation.rm.FlexoStorageResource;
import org.openflexo.foundation.rm.FlexoXMLStorageResource;
import org.openflexo.foundation.rm.XMLStorageResourceData;
import org.openflexo.xmlcode.StringEncoder;
import org.openflexo.xmlcode.XMLCoder;

public class DocResourceCenter extends DRMObject implements XMLStorageResourceData {

	// Vector of Language objects
	private Vector<Language> languages;

	// Vector of Authors objects
	private Vector<Author> authors;

	// Root folder
	private DocItemFolder rootFolder;

	public DocResourceCenter(DRMBuilder builder) {
		this();
		builder.docResourceCenter = this;
		initializeDeserialization(builder);
	}

	public DocResourceCenter() {
		super(null);
		_docResourceCenter = this;
		rootFolder = null;
		languages = new Vector<Language>();
		authors = new Vector<Author>();
	}

	public Vector<Author> getAuthors() {
		return authors;
	}

	public void setAuthors(Vector<Author> authors) {
		this.authors = authors;
		setChanged();
	}

	public void addToAuthors(Author author) {
		authors.add(author);
		setChanged();
	}

	public void removeFromAuthors(Author author) {
		authors.remove(author);
		setChanged();
	}

	public void addToLanguages(Language language) {
		languages.add(language);
		setChanged();
	}

	public void removeFromLanguages(Language language) {
		languages.remove(language);
		setChanged();
	}

	public Vector<Language> getLanguages() {
		return languages;
	}

	public void setLanguages(Vector<Language> languages) {
		this.languages = languages;
		setChanged();
	}

	public DocItemFolder getRootFolder() {
		return rootFolder;
	}

	public void setRootFolder(DocItemFolder rootFolder) {
		this.rootFolder = rootFolder;
		setChanged();
	}

	protected static DocResourceCenter createDefaultDocResourceCenter() {
		DocResourceCenter returned = new DocResourceCenter();
		Language english = Language.createLanguage("ENGLISH", "english", returned);
		Language french = Language.createLanguage("FRENCH", "french", returned);
		Author author = Author.createAuthor(System.getProperty("user.name"), "Noname", "Nomail", returned);
		returned.addToLanguages(english);
		returned.addToLanguages(french);
		returned.addToAuthors(author);
		DocItemFolder rootFolder = DocItemFolder.createDocItemFolder(DocResourceManager.DOC_RESOURCE_CENTER,
				"Root folder for Flexo documentation", null, returned);
		returned.setRootFolder(rootFolder);
		DocItemFolder flexoModelFolder = DocItemFolder.createDocItemFolder(DocResourceManager.FLEXO_MODEL, "Description of Flexo model",
				rootFolder, returned);
		DocItemFolder flexoToolSetFolder = DocItemFolder.createDocItemFolder(DocResourceManager.FLEXO_TOOL_SET,
				"Description for the FlexoToolSet application", rootFolder, returned);
		DocItemFolder abstractModuleFolder = DocItemFolder.createDocItemFolder(DocResourceManager.ABSTRACT_MODULE,
				"Description of what is a module", flexoToolSetFolder, returned);
		DocItem abstractMainPaneItem = DocItem.createDocItem(DocResourceManager.ABSTRACT_MAIN_PANE, "Description of what is the main pane",
				abstractModuleFolder, returned, false);
		DocItem abstractControlPanelItem = DocItem.createDocItem(DocResourceManager.ABSTRACT_CONTROL_PANEL,
				"Description of what is the control panel", abstractModuleFolder, returned, false);
		DocItem abstractLeftViewItem = DocItem.createDocItem(DocResourceManager.ABSTRACT_LEFT_VIEW, "Description of what is the left view",
				abstractModuleFolder, returned, false);
		DocItem abstractRightViewItem = DocItem.createDocItem(DocResourceManager.ABSTRACT_RIGHT_VIEW,
				"Description of what is the right view", abstractModuleFolder, returned, false);
		abstractModuleFolder.getPrimaryDocItem().addToEmbeddingChildItems(abstractMainPaneItem);
		abstractModuleFolder.getPrimaryDocItem().addToEmbeddingChildItems(abstractControlPanelItem);
		abstractModuleFolder.getPrimaryDocItem().addToEmbeddingChildItems(abstractLeftViewItem);
		abstractModuleFolder.getPrimaryDocItem().addToEmbeddingChildItems(abstractRightViewItem);

		/*DocItem item1 = DocItem.createDocItem("Item1","a description for the item1",folder1,returned);
		item1.setTitle("This is a title for item 1",english);
		item1.setTitle("C'est un titre pour l'item 1",french);
		DocItemVersion version = DocItemVersion.createVersion(item1,"1.0",english,"hop en full","hop en short",returned);
		item1.submitVersion(version,author,returned);*/
		return returned;
	}

	public DocItemFolder getModelFolder() {
		return getRootFolder().getItemFolderNamed("FlexoModel");
	}

	public DocItemFolder getFTSFolder() {
		return getRootFolder().getItemFolderNamed("FlexoToolSet");
	}

	@Override
	public void save() {
		FileOutputStream out = null;
		try {
			initializeSerialization();
			out = new FileOutputStream(DocResourceManager.instance().getDRMFile());
			String old = StringEncoder.getDateFormat();
			StringEncoder.setDateFormat("HH:mm:ss dd/MM/yyyy");
			XMLCoder.encodeObjectWithMapping(this, DocResourceManager.getDRMMapping(), out);
			StringEncoder.setDateFormat(old);
			out.flush();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (out != null) {
					out.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
			finalizeSerialization();
		}
		setChanged();
		notifyObservers(new DocResourceCenterIsSaved());
		clearIsModified(false);
	}

	public Author getUser() {
		Author returned = getAuthorNamed(System.getProperty("user.name"));
		if (returned == null) {
			returned = Author.createAuthor(System.getProperty("user.name"), "Noname", "Nomail", this);
		}
		return returned;
	}

	public Author getAuthorNamed(String authorIdentifier) {
		for (Enumeration en = getAuthors().elements(); en.hasMoreElements();) {
			Author next = (Author) en.nextElement();
			if (next.getIdentifier().equals(authorIdentifier)) {
				return next;
			}
		}
		return null;
	}

	public Language getLanguageNamed(String languageIdentifier) {
		for (Enumeration en = getLanguages().elements(); en.hasMoreElements();) {
			Language next = (Language) en.nextElement();
			if (next.getIdentifier().equalsIgnoreCase(languageIdentifier)) {
				return next;
			}
		}
		return null;
	}

	public DocItem getItemNamed(String aName) {
		if (getRootFolder() == null) {
			return null;
		}
		return getRootFolder().getItemNamed(aName);
	}

	@Override
	public FlexoXMLStorageResource getFlexoXMLFileResource() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public FlexoStorageResource getFlexoResource() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setFlexoResource(FlexoResource resource) throws DuplicateResourceException {
		// TODO Auto-generated method stub
	}

	@Override
	public void setProject(FlexoProject aProject) {
		// TODO Auto-generated method stub
	}

	/**
	 * Return a vector of all embedded objects at this level does NOT include itself
	 * 
	 * @return a Vector of Validable objects
	 */
	@Override
	public Vector<DocItemFolder> getEmbeddedValidableObjects() {
		Vector<DocItemFolder> returned = new Vector<DocItemFolder>();
		returned.add(getRootFolder());
		return returned;
	}

	@Override
	public synchronized void setIsModified() {
		boolean notifyObservers = false;
		if (!isModified()) {
			notifyObservers = true;
		}
		super.setIsModified();
		if (notifyObservers) {
			setChanged();
			notifyObservers(new DocResourceCenterIsModified());
			setChanged();
		}
	}

	public Vector getAllFoldersAndItems() {
		return getAllEmbeddedValidableObjects();
	}

	public Vector<DocItem> getAllItems() {
		Vector<DocItem> returned = new Vector<DocItem>();
		for (Enumeration en = getAllFoldersAndItems().elements(); en.hasMoreElements();) {
			DRMObject next = (DRMObject) en.nextElement();
			if (next instanceof DocItem) {
				returned.add((DocItem) next);
			}
		}
		return returned;
	}

	@Override
	public String getClassNameKey() {
		return "doc_resource_center";
	}

	/**
	 * Overrides getIdentifier
	 * 
	 * @see org.openflexo.drm.DRMObject#getIdentifier()
	 */
	@Override
	public String getIdentifier() {
		return "DocResourceCenter";
	}

}
