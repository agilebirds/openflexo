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
package org.openflexo.foundation.cg.version.action;

import java.util.Vector;
import java.util.logging.Logger;

import org.openflexo.foundation.FlexoEditor;
import org.openflexo.foundation.FlexoException;
import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.action.FlexoActionType;
import org.openflexo.foundation.cg.CGFile;
import org.openflexo.foundation.cg.CGObject;
import org.openflexo.foundation.cg.action.AbstractGCAction;
import org.openflexo.foundation.cg.version.AbstractCGFileVersion;
import org.openflexo.foundation.cg.version.CGVersionIdentifier;
import org.openflexo.foundation.rm.SaveResourceException;
import org.openflexo.foundation.rm.cg.ContentSource;
import org.openflexo.foundation.rm.cg.ContentSource.ContentSourceType;

public class RevertToHistoryVersion extends AbstractGCAction<RevertToHistoryVersion, CGObject> {

	private static final Logger logger = Logger.getLogger(RevertToHistoryVersion.class.getPackage().getName());

	public static FlexoActionType<RevertToHistoryVersion, CGObject, CGObject> actionType = new FlexoActionType<RevertToHistoryVersion, CGObject, CGObject>(
			"revert_to_version", versionningMenu, versionningActionsGroup, FlexoActionType.NORMAL_ACTION_TYPE) {

		/**
		 * Factory method
		 */
		@Override
		public RevertToHistoryVersion makeNewAction(CGObject focusedObject, Vector<CGObject> globalSelection, FlexoEditor editor) {
			return new RevertToHistoryVersion(focusedObject, globalSelection, editor);
		}

		@Override
		public boolean isVisibleForSelection(CGObject object, Vector<CGObject> globalSelection) {
			return ((object != null) && ((object instanceof CGFile) || (object instanceof AbstractCGFileVersion)));
		}

		@Override
		public boolean isEnabledForSelection(CGObject object, Vector<CGObject> globalSelection) {
			if (object == null) {
				return false;
			}
			if (object instanceof CGFile) {
				return (((CGFile) object).getRepository().getManageHistory())
						&& ((CGFile) object).getGenerationStatus().isGenerationAvailable();
			}
			if (object instanceof AbstractCGFileVersion) {
				return (((AbstractCGFileVersion) object).getCGFile().getRepository().getManageHistory())
						&& (((AbstractCGFileVersion) object).getCGFile()).getGenerationStatus().isGenerationAvailable();
			}
			return false;
		}

	};

	static {
		FlexoModelObject.addActionForClass(RevertToHistoryVersion.actionType, CGFile.class);
		FlexoModelObject.addActionForClass(RevertToHistoryVersion.actionType, AbstractCGFileVersion.class);
	}

	RevertToHistoryVersion(CGObject focusedObject, Vector<CGObject> globalSelection, FlexoEditor editor) {
		super(actionType, focusedObject, globalSelection, editor);
	}

	@Override
	protected void doAction(Object context) throws SaveResourceException, FlexoException {
		logger.info("Override with version " + getVersionId() + " for " + getFocusedObject());

		getCGFile().overrideWith(getSource(), doItNow());

	}

	private boolean _doItNow = false;

	public boolean doItNow() {
		return _doItNow;
	}

	public void setDoItNow(boolean doItNow) {
		_doItNow = doItNow;
	}

	private ContentSource _contentSource = null;

	public ContentSource getSource() {
		if (_contentSource == null) {
			_contentSource = ContentSource.getContentSource(ContentSourceType.HistoryVersion, getVersionId());
		}
		return _contentSource;
	}

	private CGVersionIdentifier versionId;

	public CGVersionIdentifier getVersionId() {
		if (getFocusedObject() instanceof AbstractCGFileVersion) {
			versionId = ((AbstractCGFileVersion) getFocusedObject()).getVersionId();
		}
		return versionId;
	}

	public void setVersionId(CGVersionIdentifier versionId) {
		this.versionId = versionId;
		if (_contentSource != null) {
			_contentSource.setVersion(versionId);
		}
	}

	public CGFile getCGFile() {
		if (getFocusedObject() instanceof CGFile) {
			return (CGFile) getFocusedObject();
		}
		if (getFocusedObject() instanceof AbstractCGFileVersion) {
			return ((AbstractCGFileVersion) getFocusedObject()).getCGFile();
		}
		return null;
	}

}
