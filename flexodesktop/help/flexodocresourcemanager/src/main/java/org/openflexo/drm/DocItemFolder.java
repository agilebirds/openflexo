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

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;
import java.util.logging.Logger;

import org.openflexo.drm.dm.DocItemAdded;
import org.openflexo.drm.dm.DocItemFolderAdded;
import org.openflexo.drm.dm.StructureModified;
import org.openflexo.drm.helpset.HelpSetConfiguration;
import org.openflexo.foundation.validation.FixProposal;
import org.openflexo.foundation.validation.ParameteredFixProposal;
import org.openflexo.foundation.validation.ProblemIssue;
import org.openflexo.foundation.validation.Validable;
import org.openflexo.foundation.validation.ValidationError;
import org.openflexo.foundation.validation.ValidationIssue;
import org.openflexo.foundation.validation.ValidationRule;
import org.openflexo.localization.FlexoLocalization;

public class DocItemFolder extends DRMObject {

	private static final Logger logger = Logger.getLogger(DocItemFolder.class.getPackage().getName());

	// String identifier for this folder: name will be used as folder name in FS
	private String identifier;

	// Vector of DocItemFolder: childs
	private Vector<DocItemFolder> childFolders;

	// Vector of DocItem: childs
	private Vector<DocItem> items;

	private HashMap<String, DocItem> itemCache;

	private DocItem _primaryDocItem;

	public DocItemFolder() {
		super();
		identifier = null;
		childFolders = new Vector<DocItemFolder>();
		items = new Vector<DocItem>();
		itemCache = new HashMap<String, DocItem>();
	}

	public static DocItemFolder createDocItemFolder(String anIdentifier, String aDescription, DocItemFolder parent,
			DocResourceCenter docResourceCenter) {
		logger.info("Create DocItemFolder " + anIdentifier + " in " + parent);
		DocItemFolder returned = new DocItemFolder();
		returned.identifier = anIdentifier;
		if (parent != null) {
			parent.addToChildFolders(returned);
		}
		returned.createDefaultPrimaryDocItem();
		return returned;
	}

	@Override
	public String toString() {
		return "folder:" + getIdentifier();
	}

	/**
	 * Overrides delete
	 * 
	 * @see org.openflexo.foundation.FlexoModelObject#delete()
	 */
	@Override
	public boolean delete() {
		Enumeration en = ((Vector) childFolders.clone()).elements();
		while (en.hasMoreElements()) {
			DocItemFolder f = (DocItemFolder) en.nextElement();
			f.delete();
		}
		en = ((Vector) items.clone()).elements();
		while (en.hasMoreElements()) {
			DocItem it = (DocItem) en.nextElement();
			it.delete();
		}
		if (getFolder() != null) {
			getFolder().removeFromChildFolders(this);
		}
		itemCache = null;
		return super.delete();
	}

	private File directory;

	public File getDirectory() {
		if (directory == null) {
			if (getFolder() != null) {
				File parent = getFolder().getDirectory();
				directory = new File(parent, identifier);
			}
		}
		return directory;
	}

	@Override
	public String getIdentifier() {
		return identifier;
	}

	public void setIdentifier(String identifier) {
		this.identifier = identifier;
		setChanged();
	}

	public DocItem getPrimaryDocItem() {
		if (_primaryDocItem == null) {
			// Try to lookup
			if (primaryDocItemId != null) {
				setPrimaryDocItemId(primaryDocItemId);
			}
		}
		return _primaryDocItem;
	}

	public void setPrimaryDocItem(DocItem primaryDocItem) {
		if (primaryDocItem.getFolder() == this) {
			_primaryDocItem = primaryDocItem;
			if (primaryDocItem != null) {
				primaryDocItemId = primaryDocItem.getIdentifier();
			}
			setChanged();
		}
	}

	public boolean isPublished() {
		return getPrimaryDocItem() != null && getPrimaryDocItem().isPublished();
	}

	public String getSerializationIdentifier() {
		return (getFolder() != null ? getFolder().getSerializationIdentifier() : "Folder: ") + getIdentifier();
	}

	public String getPrimaryDocItemId() {
		if (getPrimaryDocItem() != null) {
			return getPrimaryDocItem().getIdentifier();
		}
		return primaryDocItemId;
	}

	private String primaryDocItemId;

	public void setPrimaryDocItemId(String primaryDocItemId) {
		this.primaryDocItemId = primaryDocItemId;
		DocItem item = getItemNamed(primaryDocItemId);
		if (item == null) {
			this.primaryDocItemId = primaryDocItemId;
		} else if (item.getFolder() == this) {
			_primaryDocItem = item;
		}
	}

	public void createDefaultPrimaryDocItem() {
		_primaryDocItem = DocItem.createDocItem(getIdentifier(), FlexoLocalization.localizedForKey("no_description"), this, false);
	}

	private boolean itemsNeedsReordering = true;
	private Vector<DocItem> orderedItems;

	public Vector getOrderedItems() {
		if (itemsNeedsReordering) {
			orderedItems = new Vector<DocItem>();
			orderedItems.addAll(items);
			Collections.sort(orderedItems, DocItem.COMPARATOR);
			itemsNeedsReordering = false;
		}
		return orderedItems;
	}

	public Vector<DocItem> getItems() {
		if (isSerializing()) {
			return getOrderedItems();
		}
		return items;
	}

	public void setItems(Vector<DocItem> items) {
		this.items = items;
		setChanged();
		itemsNeedsReordering = true;
	}

	public void addToItems(DocItem item) {
		items.add(item);
		itemCache.put(item.getIdentifier(), item);
		item.setFolder(this);
		itemsNeedsReordering = true;
		setChanged();
		notifyObservers(new DocItemAdded(item));
	}

	public void removeFromItems(DocItem item) {
		items.remove(item);
		itemCache.remove(item.getIdentifier());
		itemsNeedsReordering = true;
		setChanged();
	}

	protected void reorderItems() {
		itemsNeedsReordering = true;
		setChanged();
		notifyObservers(new StructureModified());
	}

	public static DocItemFolderComparator COMPARATOR = new DocItemFolderComparator();

	public static class DocItemFolderComparator implements Comparator<DocItemFolder> {
		@Override
		public int compare(DocItemFolder o1, DocItemFolder o2) {
			return o1.getIdentifier().compareTo(o2.getIdentifier());
		}

	}

	private boolean childFolderNeedsReordering = true;
	private Vector<DocItemFolder> orderedChildFolders;

	public Vector<DocItemFolder> getOrderedChildFolders() {
		if (childFolderNeedsReordering) {
			orderedChildFolders = new Vector<DocItemFolder>();
			orderedChildFolders.addAll(childFolders);
			Collections.sort(orderedChildFolders, COMPARATOR);
			childFolderNeedsReordering = false;
		}
		return orderedChildFolders;
	}

	public Vector<DocItemFolder> getChildFolders() {
		if (isSerializing()) {
			return getOrderedChildFolders();
		}
		return childFolders;
	}

	public void setChildFolders(Vector<DocItemFolder> childFolders) {
		this.childFolders = childFolders;
		setChanged();
		childFolderNeedsReordering = true;
	}

	public void addToChildFolders(DocItemFolder itemFolder) {
		childFolders.add(itemFolder);
		itemFolder.setFolder(this);
		childFolderNeedsReordering = true;
		setChanged();
		notifyObservers(new DocItemFolderAdded(itemFolder));
	}

	public void removeFromChildFolders(DocItemFolder item) {
		childFolders.remove(item);
		childFolderNeedsReordering = true;
		setChanged();
	}

	public void notifyStructureChanged() {
		setChanged();
		notifyObservers(new StructureModified());
	}

	public boolean isRootFolder() {
		return getDocResourceCenter().getFolder() == this;
	}

	public String getNextDefautItemName() {
		String baseName = "item";
		String testMe = baseName;
		int test = 0;
		while (getItemNamed(testMe) != null) {
			test++;
			testMe = baseName + test;
		}
		return testMe;
	}

	public String getNextDefautItemFolderName() {
		String baseName = "Folder";
		String testMe = baseName;
		int test = 0;
		while (getItemFolderNamed(testMe) != null) {
			test++;
			testMe = baseName + test;
		}
		return testMe;
	}

	public DocItem getItemNamed(String itemIdentifier) {
		DocItem returned = itemCache.get(itemIdentifier);
		if (returned != null) {
			return returned;
		}
		for (Enumeration en = getChildFolders().elements(); en.hasMoreElements();) {
			DocItemFolder nextFolder = (DocItemFolder) en.nextElement();
			returned = nextFolder.getItemNamed(itemIdentifier);
			if (returned != null) {
				return returned;
			}
		}
		return null;
	}

	public DocItemFolder getItemFolderNamed(String itemFolderIdentifier) {
		for (Enumeration en = getChildFolders().elements(); en.hasMoreElements();) {
			DocItemFolder next = (DocItemFolder) en.nextElement();
			if (next.getIdentifier().equals(itemFolderIdentifier)) {
				return next;
			}
		}
		return null;
	}

	/**
	 * Return a vector of all embedded objects at this level does NOT include itself
	 * 
	 * @return a Vector of Validable objects
	 */
	@Override
	public List<? extends DRMObject> getEmbeddedValidableObjects() {
		List<DRMObject> returned = new ArrayList<DRMObject>();
		returned.addAll(getChildFolders());
		returned.addAll(getItems());
		return returned;
	}

	public String getRelativePath() {
		return (getFolder() != null ? getFolder().getRelativePath() + "/" : "") + getIdentifier();
	}

	public boolean isAncestorOf(DocItem anItem) {
		if (anItem.getFolder() == this) {
			return true;
		}
		for (Enumeration en = getChildFolders().elements(); en.hasMoreElements();) {
			DocItemFolder next = (DocItemFolder) en.nextElement();
			if (next.isAncestorOf(anItem)) {
				return true;
			}
		}
		return false;
	}

	// ==========================================================================
	// ============================= Validation =================================
	// ==========================================================================

	public static class DocItemFolderMustHavePrimaryItem extends ValidationRule {
		public DocItemFolderMustHavePrimaryItem() {
			super(DocItemFolder.class, "documentation_folder_must_have_primary_doc_item");
		}

		@Override
		public ValidationIssue applyValidation(final Validable object) {
			final DocItemFolder folder = (DocItemFolder) object;
			ProblemIssue issue = null;
			if (folder.getPrimaryDocItem() == null) {
				issue = new ValidationError(this, object, "doc_item_folder_($object.identifier)_has_no_primary_doc_item");
				issue.addToFixProposals(new CreateDefaultPrimaryDocItem(folder));
				issue.addToFixProposals(new SetPrimaryDocItem(folder));
			}
			return issue;
		}

		public static class CreateDefaultPrimaryDocItem extends FixProposal {
			public DocItemFolder folder;

			public CreateDefaultPrimaryDocItem(DocItemFolder aFolder) {
				super("create_default_primary_doc_item");
				folder = aFolder;
			}

			@Override
			protected void fixAction() {
				folder.createDefaultPrimaryDocItem();
			}
		}

		public static class SetPrimaryDocItem extends ParameteredFixProposal {
			public DocItemFolder folder;

			public SetPrimaryDocItem(DocItemFolder aFolder) {
				super("set_primary_doc_item", buildParameters(aFolder));
				folder = aFolder;
			}

			private static ParameterDefinition[] buildParameters(DocItemFolder aFolder) {
				ParameterDefinition[] returned = new ParameterDefinition[1];
				returned[0] = new DocItemParameter("docItem", "doc_item_to_choose", null);
				return returned;
			}

			@Override
			protected void fixAction() {
				DocItem newPrimaryDocItem = (DocItem) getValueForParameter("docItem");
				folder.setPrimaryDocItem(newPrimaryDocItem);
			}

		}

	}

	public boolean isIncluded(HelpSetConfiguration configuration) {
		if (isDirectelyIncluded(configuration)) {
			return true;
		}
		if (isPartiallyIncluded(configuration)) {
			return true;
		}
		return false;
	}

	protected boolean isDirectelyIncluded(HelpSetConfiguration configuration) {
		if (configuration.getDocItemFolders().contains(this)) {
			return true;
		}
		if (getFolder() != null) {
			return getFolder().isDirectelyIncluded(configuration);
		}
		return false;
	}

	protected boolean isPartiallyIncluded(HelpSetConfiguration configuration) {
		for (DocItemFolder folder : getChildFolders()) {
			if (folder.isDirectelyIncluded(configuration)) {
				return true;
			}
			if (folder.isPartiallyIncluded(configuration)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * @param docItem
	 * @param old
	 * @param identifier2
	 */
	public void notifyItemHasBeenRenamedTo(DocItem docItem, String old, String identifier2) {
		if (old != null) {
			itemCache.remove(old);
		}
		itemCache.put(identifier2, docItem);
	}

}
