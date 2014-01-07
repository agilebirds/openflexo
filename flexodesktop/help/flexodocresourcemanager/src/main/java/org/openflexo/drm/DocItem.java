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

import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.Vector;
import java.util.logging.Logger;

import org.openflexo.drm.action.ApproveVersion;
import org.openflexo.drm.dm.StructureModified;
import org.openflexo.drm.helpset.HelpSetConfiguration;
import org.openflexo.foundation.validation.FixProposal;
import org.openflexo.foundation.validation.ProblemIssue;
import org.openflexo.foundation.validation.Validable;
import org.openflexo.foundation.validation.ValidationError;
import org.openflexo.foundation.validation.ValidationIssue;
import org.openflexo.foundation.validation.ValidationRule;
import org.openflexo.foundation.validation.ValidationWarning;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.localization.Language;

public class DocItem extends DRMObject {

	static final Logger logger = Logger.getLogger(DocItem.class.getPackage().getName());

	public static final int NO_DOCUMENTED = 0;

	public static final int APPROVING_PENDING = 1;

	public static final int AVAILABLE_NEWER_VERSION_PENDING = 2;

	public static final int AVAILABLE_UP_TO_DATE = 3;

	// String identifier for this item: name will be used as folder name in FS
	private String identifier;

	// Parent DocItem, related to inheritance
	private DocItem inheritanceParentItem;

	// Vector of DocItem: childs of this item, related to inheritance
	private Vector<DocItem> inheritanceChildItems;

	// Parent DocItem, related to embedding
	private DocItem embeddingParentItem;

	// Vector of DocItem: childs of this item, related to embedding
	private Vector<DocItem> embeddingChildItems;

	// Vector of DocItem: related to items
	private Vector<DocItem> relatedToItems;

	// Localized titles for those items, where key is the identifier of the
	// language, as String
	private Map<String, String> titles;

	// Versions registered for this item, as a Vector of DocItemVersion
	private List<DocItemVersion> versions;

	// History for this item, as a Vector of DocItemAction
	private Vector<DocItemAction> actions;

	private String shortHTMLDescription;
	private String fullHTMLDescription;

	private boolean _isEmbedded = false;

	private boolean _isHidden = false;

	public boolean getIsEmbedded() {
		return _isEmbedded;
	}

	public void setIsEmbedded(boolean isEmbedded) {
		_isEmbedded = isEmbedded;
	}

	public boolean getIsHidden() {
		return _isHidden;
	}

	public boolean isPublished() {
		return !getIsHidden() && titles.size() > 0 && hasBeenApproved()
				&& (getFolder() == null || getFolder().getPrimaryDocItem() == this || getFolder().isPublished());
	}

	public void setIsHidden(boolean isHidden) {
		_isHidden = isHidden;
	}

	public DocItem() {
		super();
		identifier = null;
		inheritanceParentItem = null;
		inheritanceChildItems = new Vector<DocItem>();
		embeddingParentItem = null;
		embeddingChildItems = new Vector<DocItem>();
		relatedToItems = new Vector<DocItem>();
		titles = new TreeMap<String, String>();
		versions = new Vector<DocItemVersion>();
		actions = new Vector<DocItemAction>();
	}

	public static DocItem createDocItem(String anIdentifier, String aDescription, DocItemFolder folder, boolean isEmbedded) {
		DocResourceCenter docResourceCenter = folder.getDocResourceCenter();
		if (docResourceCenter.getItemNamed(anIdentifier) != null) {
			logger.warning("Could not create doc item " + anIdentifier + ": duplicated identifier");
			return docResourceCenter.getItemNamed(anIdentifier);
		}
		logger.fine("Create DocItem " + anIdentifier + " in " + folder.getIdentifier());
		DocItem returned = new DocItem();
		returned.identifier = anIdentifier;
		if (folder != null) {
			folder.addToItems(returned);
		}
		returned._isEmbedded = isEmbedded;
		returned._isHidden = true;
		return returned;
	}

	/**
	 * Overrides delete
	 * 
	 * @see org.openflexo.foundation.FlexoModelObject#delete()
	 */
	@Override
	public boolean delete() {
		Enumeration en = ((Vector) embeddingChildItems.clone()).elements();
		while (en.hasMoreElements()) {
			DocItem it = (DocItem) en.nextElement();
			it.delete();
		}
		en = ((Vector) relatedToItems.clone()).elements();
		while (en.hasMoreElements()) {
			DocItem it = (DocItem) en.nextElement();
			it.removeFromRelatedToItems(this);
		}
		en = ((Vector) inheritanceChildItems.clone()).elements();
		while (en.hasMoreElements()) {
			DocItem it = (DocItem) en.nextElement();
			it.removeFromInheritanceChildItems(this);
		}
		if (embeddingParentItem != null) {
			embeddingParentItem.removeFromEmbeddingChildItems(this);
		}
		if (inheritanceParentItem != null) {
			inheritanceParentItem.removeFromInheritanceChildItems(this);
		}
		if (getFolder() != null) {
			getFolder().removeFromItems(this);
		}
		return super.delete();
	}

	@Override
	public String getIdentifier() {
		return identifier;
	}

	public void setIdentifier(String identifier) {
		String old = this.identifier;
		this.identifier = identifier;
		setChanged();
		if (getFolder() != null) {
			getFolder().notifyItemHasBeenRenamedTo(this, old, identifier);
		}
	}

	private boolean embeddingItemsNeedsReordering = true;

	private Vector<DocItem> orderedEmbeddingChildItems;

	public Vector<DocItem> getOrderedEmbeddingChildItems() {
		if (embeddingItemsNeedsReordering) {
			orderedEmbeddingChildItems = new Vector<DocItem>();
			orderedEmbeddingChildItems.addAll(embeddingChildItems);
			Collections.sort(orderedEmbeddingChildItems, DocItem.COMPARATOR);
			embeddingItemsNeedsReordering = false;
		}
		return orderedEmbeddingChildItems;
	}

	public Vector<DocItem> getEmbeddingChildItems() {
		return embeddingChildItems;
	}

	public void setEmbeddingChildItems(Vector<DocItem> embeddingChildItems) {
		this.embeddingChildItems = embeddingChildItems;
		setChanged();
		embeddingItemsNeedsReordering = true;
	}

	public void addToEmbeddingChildItems(DocItem item) {
		if (!embeddingChildItems.contains(item)) {
			embeddingChildItems.add(item);
			item.setEmbeddingParentItem(this);
			setChanged();
			notifyObservers(new StructureModified());
			embeddingItemsNeedsReordering = true;
		}
	}

	public void removeFromEmbeddingChildItems(DocItem item) {
		if (embeddingChildItems.contains(item)) {
			item.setEmbeddingParentItem(null);
			embeddingChildItems.remove(item);
			setChanged();
			notifyObservers(new StructureModified());
			embeddingItemsNeedsReordering = true;
		}
	}

	public DocItem getEmbeddingParentItem() {
		return embeddingParentItem;
	}

	public void setEmbeddingParentItem(DocItem newParent) {
		DocItem oldParent = embeddingParentItem;
		if (newParent != oldParent) {
			embeddingParentItem = newParent;
			if (oldParent != null) {
				oldParent.removeFromEmbeddingChildItems(this);
			}
			if (newParent != null) {
				newParent.addToEmbeddingChildItems(this);
			}
		}
		setChanged();

	}

	/**
	 * Returns direct childs relating to inheritance Take isHidden property under account by searching deeply
	 * 
	 * @return
	 */
	public Vector<DocItem> getDerivedInheritanceChildItems() {
		Vector<DocItem> returned = new Vector<DocItem>();
		for (Enumeration en = getInheritanceChildItems().elements(); en.hasMoreElements();) {
			DocItem next = (DocItem) en.nextElement();
			if (!next.isPublished()) {
				returned.addAll(next.getDerivedInheritanceChildItems());
			} else {
				returned.add(next);
			}
		}
		return returned;
	}

	public Vector<DocItem> getInheritanceChildItems() {
		return inheritanceChildItems;
	}

	public void setInheritanceChildItems(Vector<DocItem> inheritanceChildItems) {
		this.inheritanceChildItems = inheritanceChildItems;
	}

	public DocItem getInheritanceParentItem() {
		return inheritanceParentItem;
	}

	public void setInheritanceParentItem(DocItem newParent) {
		DocItem oldParent = inheritanceParentItem;
		if (newParent != oldParent) {
			inheritanceParentItem = newParent;
			if (oldParent != null) {
				oldParent.removeFromInheritanceChildItems(this);
			}
			if (newParent != null) {
				newParent.addToInheritanceChildItems(this);
			}
			setChanged();
		}
	}

	public void addToInheritanceChildItems(DocItem item) {
		if (!inheritanceChildItems.contains(item)) {
			inheritanceChildItems.add(item);
			item.setInheritanceParentItem(this);
			setChanged();
			notifyObservers(new StructureModified());
		}
	}

	public void removeFromInheritanceChildItems(DocItem item) {
		if (inheritanceChildItems.contains(item)) {
			item.setInheritanceParentItem(null);
			inheritanceChildItems.remove(item);
			setChanged();
			notifyObservers(new StructureModified());
		}
	}

	public Vector<DocItem> getRelatedToItems() {
		return relatedToItems;
	}

	public void setRelatedToItems(Vector<DocItem> relatedToItems) {
		this.relatedToItems = relatedToItems;
	}

	public void addToRelatedToItems(DocItem item) {
		if (!relatedToItems.contains(item)) {
			relatedToItems.add(item);
			item.relatedToItems.add(this);
			setChanged();
		}
	}

	public void removeFromRelatedToItems(DocItem item) {
		relatedToItems.remove(item);
		item.relatedToItems.remove(this);
		setChanged();
	}

	public Map<String, String> _getTitles() {
		return titles;
	}

	public void _setTitles(Map<String, String> titles) {
		this.titles = new TreeMap<String, String>(titles);
	}

	public void _setTitleForKey(String title, String languageId) {
		if (getFolder() != null) {
			getFolder().reorderItems();
		}
		titles.put(languageId, title);
	}

	public void _removeTitleWithKey(String languageId) {
		titles.remove(languageId);
	}

	public String getTitle(Language language) {
		return titles.get(language.getIdentifier());
	}

	public void setTitle(String title, Language language) {
		_setTitleForKey(title, language.getIdentifier());
		setChanged();
	}

	public List<DocItemVersion> getVersions() {
		return versions;
	}

	public void setVersions(List<DocItemVersion> versions) {
		this.versions = versions;
		setChanged();
	}

	public void addToVersions(DocItemVersion version) {
		versions.add(version);
		setChanged();
	}

	public void removeFromVersions(DocItemVersion version) {
		versions.remove(version);
		setChanged();
	}

	public Vector<DocItemAction> getActions() {
		return actions;
	}

	public void setActions(Vector<DocItemAction> actions) {
		this.actions = actions;
	}

	public void addToActions(DocItemAction action) {
		actions.add(action);
		// action.setItem(this);
		setChanged();
		notifyObservers(new StructureModified());
		if (getFolder() != null) {
			getFolder().notifyStructureChanged();
		}
	}

	public void removeFromActions(DocItemAction action) {
		// action.setItem(null);
		actions.remove(action);
		setChanged();
		notifyObservers(new StructureModified());
		if (getFolder() != null) {
			getFolder().notifyStructureChanged();
		}
	}

	public DocItemAction submitVersion(DocItemVersion version, Author author, DocResourceCenter docResourceCenter) {
		DocItemAction action = DocItemAction.createSubmitAction(version, author, docResourceCenter);
		addToActions(action);
		addToVersions(version);
		return action;
	}

	public DocItemAction reviewVersion(DocItemVersion version, Author author, DocResourceCenter docResourceCenter) {
		DocItemAction action = DocItemAction.createReviewAction(version, author, docResourceCenter);
		addToActions(action);
		addToVersions(version);
		return action;
	}

	public DocItemAction approveVersion(DocItemVersion version, Author author, DocResourceCenter docResourceCenter) {
		DocItemAction action = DocItemAction.createApproveAction(version, author, docResourceCenter);
		addToActions(action);
		return action;
	}

	public DocItemAction refuseVersion(DocItemVersion version, Author author, DocResourceCenter docResourceCenter) {
		DocItemAction action = DocItemAction.createRefuseAction(version, author, docResourceCenter);
		addToActions(action);
		return action;
	}

	@Override
	public String toString() {
		return "item:" + getIdentifier();
	}

	public String getRelativePath() {
		return getFolder().getRelativePath() + "/" + getIdentifier();
	}

	public String getRelativePathFrom(DocItem aStartPosition) {
		String prefixPath = "";
		// boolean prefixPathIsFirst = true;
		String postfixPath = "";
		// boolean postfixPathIsFirst = true;
		DocItemFolder commonAncestor = aStartPosition.getFolder();
		while (!commonAncestor.isAncestorOf(this)) {
			prefixPath += "../";
			commonAncestor = commonAncestor.getFolder();
			// prefixPathIsFirst = false;
		}
		// OK, common ancestor is found
		DocItemFolder currentFolder = getFolder();
		while (currentFolder != commonAncestor) {
			postfixPath = currentFolder.getIdentifier() + "/" + postfixPath;
			currentFolder = currentFolder.getFolder();
			// postfixPathIsFirst = false;
		}
		// Now, need to concatenate
		return prefixPath + postfixPath + getIdentifier();
	}

	public String getHTMLLinkFrom(DocItem aStartPosition, Language language) {
		StringBuffer returned = new StringBuffer();
		String title = getTitle(language);
		if (title == null) {
			title = getIdentifier();
		}
		returned.append("<a href=\"");
		returned.append(getRelativePathFrom(aStartPosition) + ".html");
		returned.append("\">");
		returned.append(title);
		returned.append("</a>");
		return returned.toString();
	}

	public int getStatusForLanguage(Language language) {
		DocItemAction lastAction = getLastActionForLanguage(language);
		DocItemAction lastApprovedAction = getLastApprovedActionForLanguage(language);
		DocItemAction lastPendingAction = getLastPendingActionForLanguage(language);
		if (lastAction == null) {
			return NO_DOCUMENTED;
		}
		if (lastApprovedAction == null) {
			return APPROVING_PENDING;
		}
		if (lastPendingAction != null
				&& lastPendingAction.getVersion().getVersion().isGreaterThan(lastApprovedAction.getVersion().getVersion())) {
			return AVAILABLE_NEWER_VERSION_PENDING;
		} else {
			return AVAILABLE_UP_TO_DATE;
		}
	}

	public String getLocalizedStatusForLanguage(Language language) {
		switch (getStatusForLanguage(language)) {
		case NO_DOCUMENTED:
			return FlexoLocalization.localizedForKey("no_documentation");
		case APPROVING_PENDING:
			return FlexoLocalization.localizedForKey("approving_pending");
		case AVAILABLE_NEWER_VERSION_PENDING:
			return FlexoLocalization.localizedForKey("available_newer_version_pending");
		case AVAILABLE_UP_TO_DATE:
			return FlexoLocalization.localizedForKey("available_and_up_to_date");
		default:
			return "???";
		}
	}

	public boolean hasBeenApproved() {
		for (Enumeration en = getActions().elements(); en.hasMoreElements();) {
			DocItemAction next = (DocItemAction) en.nextElement();
			if (next.isApproved()) {
				return true;
			}
		}
		return false;
	}

	public DocItemAction getLastApprovedActionForLanguage(Language language) {
		DocItemAction returned = null;
		for (Enumeration en = getActions().elements(); en.hasMoreElements();) {
			DocItemAction next = (DocItemAction) en.nextElement();
			if (/*next.getVersion().getLanguage() == language &&*/next.isApproved()) {
				returned = next;
			}
		}
		return returned;
	}

	public DocItemAction getLastPendingActionForLanguage(Language language) {
		DocItemAction returned = null;
		for (Enumeration en = getActions().elements(); en.hasMoreElements();) {
			DocItemAction next = (DocItemAction) en.nextElement();
			if (/*next.getVersion().getLanguage() == language &&*/next.isPending()) {
				returned = next;
			}
		}
		return returned;
	}

	public DocItemAction getLastActionForLanguage(Language language) {
		DocItemAction returned = null;
		for (Enumeration en = getActions().elements(); en.hasMoreElements();) {
			DocItemAction next = (DocItemAction) en.nextElement();
			// if (next.getVersion().getLanguage() == language) {
			returned = next;
			// }
		}
		return returned;
	}

	public DocItemVersion getValidVersionForLanguage(Language language) {
		DocItemAction lastApproved = getLastApprovedActionForLanguage(language);
		if (lastApproved != null) {
			return lastApproved.getVersion();
		}
		return null;
	}

	public static DocItemComparator COMPARATOR = new DocItemComparator();

	public static class DocItemComparator implements Comparator<DocItem> {
		public Language currentLanguage;

		@Override
		public int compare(DocItem docItem1, DocItem docItem2) {
			if (currentLanguage != null) {
				String string1 = docItem1.getTitle(currentLanguage);
				if (string1 == null) {
					string1 = docItem1.getIdentifier();
				}
				String string2 = docItem2.getTitle(currentLanguage);
				if (string2 == null) {
					string2 = docItem2.getIdentifier();
				}
				return string1.compareTo(string2);
			} else {
				return docItem1.getIdentifier().compareTo(docItem2.getIdentifier());
			}
		}

	}

	// ==========================================================================
	// ============================= Validation
	// =================================
	// ==========================================================================

	public static class DocumentationShouldBeUpToDate extends ValidationRule {
		private final Language _language;

		public DocumentationShouldBeUpToDate(Language language) {
			super(DocItem.class, "documentation_should_be_up_to_date_for_language");
			_language = language;
		}

		public String getLanguageIdentifier() {
			if (_language != null) {
				return _language.getIdentifier();
			}
			return "";
		}

		@Override
		public String getLocalizedName() {
			return FlexoLocalization.localizedForKeyWithParams("documentation_should_be_up_to_date_for_language_($languageIdentifier)",
					this);
		}

		@Override
		public ValidationIssue applyValidation(final Validable object) {
			final DocItem item = (DocItem) object;
			ProblemIssue issue = null;
			if (item.getStatusForLanguage(_language) == NO_DOCUMENTED) {
				issue = new ValidationError(this, object,
						"doc_item_($object.identifier)_has_no_documentation_for_language_($validationRule.languageIdentifier)");
			} else if (item.getStatusForLanguage(_language) == APPROVING_PENDING) {
				issue = new ValidationError(this, object,
						"doc_item_($object.identifier)_has_unvalidated_documentation_proposal_for_language_($validationRule.languageIdentifier)");
				issue.addToFixProposals(new ApprovePendingVersion(item.getLastPendingActionForLanguage(_language).getVersion()));
			} else if (item.getStatusForLanguage(_language) == AVAILABLE_NEWER_VERSION_PENDING) {
				issue = new ValidationWarning(this, object,
						"doc_item_($object.identifier)_is_available_for_language_($validationRule.languageIdentifier)_but_a_newer_version_is_pending");
				issue.addToFixProposals(new ApprovePendingVersion(item.getLastPendingActionForLanguage(_language).getVersion()));
			}
			return issue;
		}

		public static class ApprovePendingVersion extends FixProposal {
			public DocItemVersion version;

			public ApprovePendingVersion(DocItemVersion aVersion) {
				super("approve_pending_version");
				version = aVersion;
			}

			@Override
			protected void fixAction() {
				logger.warning("This implementation is not correct: you should not use FlexoAction primitive from the model !");
				// TODO: Please implement this better later
				// Used editor will be null
				ApproveVersion approveAction = ApproveVersion.actionType.makeNewAction(version.getDocItem(), null);
				approveAction.setVersion(version);
				approveAction.doAction();
			}
		}

	}

	public boolean isIncluded(HelpSetConfiguration configuration) {
		return getFolder().isIncluded(configuration);
	}

	public String getShortHTMLDescription() {
		return shortHTMLDescription;
	}

	public void setShortHTMLDescription(String shortHTMLDescription) {
		this.shortHTMLDescription = shortHTMLDescription;
	}

	public String getFullHTMLDescription() {
		return fullHTMLDescription;
	}

	public void setFullHTMLDescription(String fullHTMLDescription) {
		this.fullHTMLDescription = fullHTMLDescription;
	}
}
