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
package org.openflexo.generator.action;

import java.util.Vector;

import org.openflexo.foundation.FlexoEditor;
import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.action.FlexoActionType;
import org.openflexo.foundation.action.FlexoGUIAction;
import org.openflexo.foundation.cg.CGFile;
import org.openflexo.foundation.cg.CGObject;
import org.openflexo.foundation.cg.action.AbstractGCAction;
import org.openflexo.foundation.cg.version.CGVersionIdentifier;
import org.openflexo.foundation.rm.cg.AbstractGeneratedFile;
import org.openflexo.foundation.rm.cg.ContentSource;
import org.openflexo.foundation.rm.cg.ContentSource.ContentSourceType;
import org.openflexo.generator.file.AbstractCGFile;

public class ShowFileVersion extends FlexoGUIAction<ShowFileVersion, CGFile, CGObject> {

	public static class ShowFileVersionActionType extends FlexoActionType<ShowFileVersion, CGFile, CGObject> {
		private ContentSourceType _source;

		protected ShowFileVersionActionType(String actionName, ContentSourceType source) {
			super(actionName, AbstractGCAction.SHOW_MENU, FlexoActionType.defaultGroup, FlexoActionType.NORMAL_ACTION_TYPE);
			_source = source;
		}

		/**
		 * Factory method
		 */
		@Override
		public ShowFileVersion makeNewAction(CGFile focusedObject, Vector<CGObject> globalSelection, FlexoEditor editor) {
			return new ShowFileVersion(this, focusedObject, globalSelection, editor);
		}

		@Override
		protected boolean isVisibleForSelection(CGFile file, Vector<CGObject> globalSelection) {
			return file instanceof AbstractCGFile;
		}

		@Override
		protected boolean isEnabledForSelection(CGFile file, Vector<CGObject> globalSelection) {
			if (!isVisibleForSelection(file, globalSelection)) {
				return false;
			}
			if (_source == ContentSourceType.PureGeneration) {
				return file.getGenerationStatus().isGenerationAvailable();
			} else if (_source == ContentSourceType.GeneratedMerge) {
				return file.getGenerationStatus().isGenerationAvailable();
			} else if (_source == ContentSourceType.ContentOnDisk) {
				return file.hasVersionOnDisk();
			} else if (_source == ContentSourceType.ResultFileMerge) {
				return file.getGenerationStatus().isGenerationAvailable();
			} else if (_source == ContentSourceType.LastGenerated) {
				return file.hasVersionOnDisk();
			} else if (_source == ContentSourceType.LastAccepted) {
				return file.hasVersionOnDisk();
			} else if (_source == ContentSourceType.HistoryVersion) {
				return (file.getRepository().getManageHistory() && file.getResource().isLoaded()
						&& file.getGeneratedResourceData() instanceof AbstractGeneratedFile && ((AbstractGeneratedFile) file
						.getGeneratedResourceData()).getHistory().getReleasesVersion().size() > 0);
			}
			return false;
		}

		public ContentSourceType getSourceType() {
			return _source;
		}

	}

	public static final ShowFileVersionActionType showPureGeneration = new ShowFileVersionActionType("pure_generation",
			ContentSourceType.PureGeneration);
	public static final ShowFileVersionActionType showGeneratedMerge = new ShowFileVersionActionType("generated_merge",
			ContentSourceType.GeneratedMerge);
	public static final ShowFileVersionActionType showContentOnDisk = new ShowFileVersionActionType("content_on_disk",
			ContentSourceType.ContentOnDisk);
	public static final ShowFileVersionActionType showResultFileMerge = new ShowFileVersionActionType("result_file_merge",
			ContentSourceType.ResultFileMerge);
	public static final ShowFileVersionActionType showLastGenerated = new ShowFileVersionActionType("last_generated_version",
			ContentSourceType.LastGenerated);
	public static final ShowFileVersionActionType showLastAccepted = new ShowFileVersionActionType("last_accepted_version",
			ContentSourceType.LastAccepted);
	public static final ShowFileVersionActionType showHistoryVersion = new ShowFileVersionActionType("history_version",
			ContentSourceType.HistoryVersion);

	static {
		FlexoModelObject.addActionForClass(showPureGeneration, CGFile.class);
		FlexoModelObject.addActionForClass(showGeneratedMerge, CGFile.class);
		FlexoModelObject.addActionForClass(showContentOnDisk, CGFile.class);
		FlexoModelObject.addActionForClass(showResultFileMerge, CGFile.class);
		FlexoModelObject.addActionForClass(showLastGenerated, CGFile.class);
		FlexoModelObject.addActionForClass(showLastAccepted, CGFile.class);
		FlexoModelObject.addActionForClass(showHistoryVersion, CGFile.class);
	}

	public static ShowFileVersionActionType getActionTypeFor(ContentSourceType contentSource) {
		if (contentSource == ContentSourceType.PureGeneration) {
			return showPureGeneration;
		} else if (contentSource == ContentSourceType.GeneratedMerge) {
			return showGeneratedMerge;
		} else if (contentSource == ContentSourceType.ResultFileMerge) {
			return showResultFileMerge;
		} else if (contentSource == ContentSourceType.ContentOnDisk) {
			return showContentOnDisk;
		} else if (contentSource == ContentSourceType.LastGenerated) {
			return showLastGenerated;
		} else if (contentSource == ContentSourceType.LastAccepted) {
			return showLastAccepted;
		} else if (contentSource == ContentSourceType.HistoryVersion) {
			return showHistoryVersion;
		}
		return null;
	}

	ShowFileVersion(ShowFileVersionActionType actionType, CGFile focusedObject, Vector<CGObject> globalSelection, FlexoEditor editor) {
		super(actionType, focusedObject, globalSelection, editor);
	}

	@Override
	public ShowFileVersionActionType getActionType() {
		return (ShowFileVersionActionType) super.getActionType();
	}

	private ContentSource _contentSource = null;

	public ContentSource getSource() {
		if (_contentSource == null) {
			_contentSource = ContentSource.getContentSource(getActionType().getSourceType(), getVersionId());
		}
		return _contentSource;
	}

	private CGVersionIdentifier versionId;

	public CGVersionIdentifier getVersionId() {
		return versionId;
	}

	public void setVersionId(CGVersionIdentifier versionId) {
		this.versionId = versionId;
		if (_contentSource != null) {
			_contentSource.setVersion(versionId);
		}
	}

}
