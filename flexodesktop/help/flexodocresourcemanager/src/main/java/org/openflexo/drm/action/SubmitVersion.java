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
package org.openflexo.drm.action;

import java.util.Vector;
import java.util.logging.Logger;

import org.openflexo.drm.Author;
import org.openflexo.drm.DocItem;
import org.openflexo.drm.DocItemAction;
import org.openflexo.drm.DocItemVersion;
import org.openflexo.drm.DocResourceManager;
import org.openflexo.foundation.FlexoEditor;
import org.openflexo.foundation.FlexoObject.FlexoObjectImpl;
import org.openflexo.foundation.action.FlexoAction;
import org.openflexo.foundation.action.FlexoActionType;

public class SubmitVersion extends FlexoAction<SubmitVersion, DocItem, DocItem> {

	private static final Logger logger = Logger.getLogger(SubmitVersion.class.getPackage().getName());

	public static FlexoActionType<SubmitVersion, DocItem, DocItem> actionType = new FlexoActionType<SubmitVersion, DocItem, DocItem>(
			"submit_version", FlexoActionType.defaultGroup, FlexoActionType.NORMAL_ACTION_TYPE) {

		/**
		 * Factory method
		 */
		@Override
		public SubmitVersion makeNewAction(DocItem focusedObject, Vector<DocItem> globalSelection, FlexoEditor editor) {
			return new SubmitVersion(focusedObject, globalSelection, editor);
		}

		@Override
		public boolean isVisibleForSelection(DocItem object, Vector<DocItem> globalSelection) {
			return true;
		}

		@Override
		public boolean isEnabledForSelection(DocItem object, Vector<DocItem> globalSelection) {
			return object != null && object instanceof DocItem;
		}

	};

	static {
		FlexoObjectImpl.addActionForClass(actionType, DocItem.class);
	}

	private DocItem _docItem;
	private DocItemVersion _version;
	private Author _author;
	private DocItemAction _newAction;
	private String _note;

	SubmitVersion(DocItem focusedObject, Vector<DocItem> globalSelection, FlexoEditor editor) {
		super(actionType, focusedObject, globalSelection, editor);
	}

	@Override
	protected void doAction(Object context) {
		if (getVersion() != null) {

			DocItemAction lastAction = getDocItem().getLastActionForLanguage(getVersion().getLanguage());
			if (lastAction == null) {
				logger.info("SubmitVersion");
				_newAction = DocItemAction.createSubmitAction(getVersion(), getAuthor(), getDocItem().getDocResourceCenter());
				_newAction.setNote(getNote());
				getDocItem().addToActions(_newAction);
				getDocItem().addToVersions(getVersion());
				DocResourceManager.instance().getSessionSubmissions().addToSubmissionActions(_newAction);
			} else {
				logger.info("ReviewVersion");
				_newAction = DocItemAction.createReviewAction(getVersion(), getAuthor(), getDocItem().getDocResourceCenter());
				_newAction.setNote(getNote());
				getDocItem().addToActions(_newAction);
				getDocItem().addToVersions(getVersion());
				DocResourceManager.instance().getSessionSubmissions().addToSubmissionActions(_newAction);
			}
		}
	}

	public DocItem getDocItem() {
		if (_docItem == null) {
			if (getFocusedObject() != null && getFocusedObject() instanceof DocItem) {
				_docItem = getFocusedObject();
			}
		}
		return _docItem;
	}

	public void setDocItem(DocItem docItem) {
		_docItem = docItem;
	}

	public Author getAuthor() {
		return _author;
	}

	public void setAuthor(Author author) {
		_author = author;
	}

	public DocItemAction getNewAction() {
		return _newAction;
	}

	public DocItemVersion getVersion() {
		return _version;
	}

	public void setVersion(DocItemVersion version) {
		_version = version;
	}

	public String getNote() {
		return _note;
	}

	public void setNote(String note) {
		_note = note;
	}

}
