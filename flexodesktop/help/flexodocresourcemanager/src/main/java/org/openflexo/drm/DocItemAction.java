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

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;

import org.openflexo.localization.FlexoLocalization;

public class DocItemAction extends DRMObject {

	private DocItemVersion itemVersion;
	private String authorId;
	private Date actionDate;
	private ActionType actionType;
	private String note;

	public DocItemAction() {
		super();
	}

	public static DocItemAction createSubmitAction(DocItemVersion version, Author author, DocResourceCenter docResourceCenter) {
		DocItemAction newAction = new DocItemAction();
		newAction.itemVersion = version;
		newAction.authorId = author.getIdentifier();
		newAction.actionDate = new Date();
		newAction.actionType = ActionType.SUBMITTED;
		return newAction;
	}

	public static DocItemAction createReviewAction(DocItemVersion version, Author author, DocResourceCenter docResourceCenter) {
		DocItemAction newAction = new DocItemAction();
		newAction.itemVersion = version;
		newAction.authorId = author.getIdentifier();
		newAction.actionDate = new Date();
		newAction.actionType = ActionType.REVIEWED;
		return newAction;
	}

	public static DocItemAction createApproveAction(DocItemVersion version, Author author, DocResourceCenter docResourceCenter) {
		DocItemAction newAction = new DocItemAction();
		newAction.itemVersion = version;
		newAction.authorId = author.getIdentifier();
		newAction.actionDate = new Date();
		newAction.actionType = ActionType.APPROVED;
		return newAction;
	}

	public static DocItemAction createRefuseAction(DocItemVersion version, Author author, DocResourceCenter docResourceCenter) {
		DocItemAction newAction = new DocItemAction();
		newAction.itemVersion = version;
		newAction.authorId = author.getIdentifier();
		newAction.actionDate = new Date();
		newAction.actionType = ActionType.REFUSED;
		return newAction;
	}

	public Date getActionDate() {
		return actionDate;
	}

	public void setActionDate(Date actionDate) {
		this.actionDate = actionDate;
		setChanged();
	}

	public ActionType getActionType() {
		return actionType;
	}

	public void setActionType(ActionType actionType) {
		this.actionType = actionType;
		setChanged();
	}

	public String getAuthorId() {
		return authorId;
	}

	public void setAuthorId(String authorId) {
		this.authorId = authorId;
		setChanged();
	}

	public DocItem getItem() {
		return itemVersion.getDocItem();
	}

	public DocItemVersion getVersion() {
		return itemVersion;
	}

	public void setVersion(DocItemVersion version) {
		itemVersion = version;
	}

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
		setChanged();
	}

	public String getLocalizedName() {
		return FlexoLocalization
				.localizedForKeyWithParams(
						"($version.version)/($version.languageId)_($localizedActionType)_on_($localizedSmallActionDate)_by_($authorId)_($statusName)",
						this);
	}

	public String getLocalizedActionType() {
		return FlexoLocalization.localizedForKey(getActionType().getName());
	}

	public String getLocalizedSmallActionDate() {
		// Typically "dd/MM/yyyy" in french, "MM/dd, yyyy" in english
		return new SimpleDateFormat(FlexoLocalization.localizedForKey("doc_item_action_date_format_simple")).format(getActionDate());
	}

	public String getLocalizedFullActionDate() {
		// Typically "dd/MM/yyyy" in french, "MM/dd, yyyy" in english
		return new SimpleDateFormat(FlexoLocalization.localizedForKey("doc_item_action_date_format_extended")).format(getActionDate());
	}

	public boolean isApproved() {
		for (Enumeration en = getItem().getActions().elements(); en.hasMoreElements();) {
			DocItemAction next = (DocItemAction) en.nextElement();
			if (next.getVersion() == getVersion() && next.getActionType() == ActionType.APPROVED) {
				return true;
			}
		}
		return false;
	}

	public boolean isPending() {
		if (isProposal()) {
			return !isApproved() && !isRefused();
		}
		return false;
	}

	public boolean isProposal() {
		return getActionType() == ActionType.SUBMITTED || getActionType() == ActionType.REVIEWED;
	}

	public boolean isRefused() {
		for (Enumeration en = getItem().getActions().elements(); en.hasMoreElements();) {
			DocItemAction next = (DocItemAction) en.nextElement();
			if (next.getVersion() == getVersion() && next.getActionType() == ActionType.REFUSED) {
				return true;
			}
		}
		return false;
	}

	public String getStatusName() {
		if (!isProposal()) {
			return "";
		}
		if (isApproved()) {
			return FlexoLocalization.localizedForKey("[approved]");
		}
		if (isRefused()) {
			return FlexoLocalization.localizedForKey("[refused]");
		}
		if (isPending()) {
			return FlexoLocalization.localizedForKey("[pending]");
		}
		return "";
	}

	/**
	 * Overrides getIdentifier
	 * 
	 * @see org.openflexo.drm.DRMObject#getIdentifier()
	 */
	@Override
	public String getIdentifier() {
		return getActionType().getName() + "_ON_" + getVersion();
	}

}
