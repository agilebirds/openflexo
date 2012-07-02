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
import java.util.logging.Logger;

import org.openflexo.foundation.FlexoEditor;
import org.openflexo.foundation.FlexoException;
import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.action.FlexoActionType;
import org.openflexo.foundation.cg.CGObject;
import org.openflexo.foundation.cg.GenerationRepository;
import org.openflexo.foundation.cg.version.CGVersionIdentifier;
import org.openflexo.foundation.rm.SaveResourceException;
import org.openflexo.foundation.rm.cg.ContentSource;
import org.openflexo.foundation.rm.cg.ContentSource.ContentSourceType;
import org.openflexo.generator.AbstractProjectGenerator;
import org.openflexo.generator.exception.GenerationException;
import org.openflexo.generator.file.AbstractCGFile;
import org.openflexo.localization.FlexoLocalization;

public class OverrideWithVersion extends MultipleFileGCAction<OverrideWithVersion> {

	private static final Logger logger = Logger.getLogger(OverrideWithVersion.class.getPackage().getName());

	public static class OverrideWithVersionActionType extends MultipleFileGCActionType<OverrideWithVersion> {
		private ContentSourceType _source;

		protected OverrideWithVersionActionType(String actionName, ContentSourceType source) {
			super(actionName, MERGE_MENU, OVERRIDE_GROUP, FlexoActionType.NORMAL_ACTION_TYPE);
			_source = source;
		}

		/**
		 * Factory method
		 */
		@Override
		public OverrideWithVersion makeNewAction(CGObject focusedObject, Vector<CGObject> globalSelection, FlexoEditor editor) {
			return new OverrideWithVersion(this, focusedObject, globalSelection, editor);
		}

		@Override
		protected boolean accept(AbstractCGFile file) {
			if (file == null || file.getResource() == null) {
				return false;
			}
			if (_source == ContentSourceType.PureGeneration) {
				return file.getGenerationStatus().isGenerationAvailable();
			} else if (_source == ContentSourceType.GeneratedMerge) {
				return file.getGenerationStatus().isGenerationAvailable();
			} else if (_source == ContentSourceType.LastGenerated) {
				return file.hasVersionOnDisk();
			} else if (_source == ContentSourceType.LastAccepted) {
				return file.hasVersionOnDisk();
			}
			return false;
		}

		public ContentSourceType getSourceType() {
			return _source;
		}

	}

	public static final OverrideWithVersionActionType overrideWithPureGeneration = new OverrideWithVersionActionType(
			"override_with_pure_generation", ContentSourceType.PureGeneration);
	public static final OverrideWithVersionActionType overrideWithGeneratedMerge = new OverrideWithVersionActionType(
			"override_with_generated_merge", ContentSourceType.GeneratedMerge);
	public static final OverrideWithVersionActionType overrideWithLastGenerated = new OverrideWithVersionActionType(
			"override_with_last_generated_version", ContentSourceType.LastGenerated);
	public static final OverrideWithVersionActionType overrideWithLastAccepted = new OverrideWithVersionActionType(
			"override_with_last_accepted_version", ContentSourceType.LastAccepted);

	static {
		FlexoModelObject.addActionForClass(overrideWithPureGeneration, CGObject.class);
		FlexoModelObject.addActionForClass(overrideWithGeneratedMerge, CGObject.class);
		FlexoModelObject.addActionForClass(overrideWithLastGenerated, CGObject.class);
		FlexoModelObject.addActionForClass(overrideWithLastAccepted, CGObject.class);
		FlexoModelObject.addActionForClass(CancelOverrideWithVersion.actionType, CGObject.class);
	}

	public static OverrideWithVersionActionType getActionTypeFor(ContentSourceType contentSource) {
		if (contentSource == ContentSourceType.PureGeneration) {
			return overrideWithPureGeneration;
		} else if (contentSource == ContentSourceType.GeneratedMerge) {
			return overrideWithGeneratedMerge;
		} else if (contentSource == ContentSourceType.LastGenerated) {
			return overrideWithLastGenerated;
		} else if (contentSource == ContentSourceType.LastAccepted) {
			return overrideWithLastAccepted;
		}
		return null;
	}

	/*	private class OverrideWithVersionForFile extends OverrideWithVersion.CGFileRunnable {

			private ContentSource content;
			private boolean doItNow;
			
			public OverrideWithVersionForFile(AbstractCGFile file, ContentSource content, boolean doItNow) {
				super(file);
				this.content = content;
				this.doItNow = doItNow;
			}

			@Override
			public String getLocalizedName() {
				return FlexoLocalization.localizedForKey("override") +  " " + file.getFileName();
			}

			public void run() {
				logger.info(FlexoLocalization.localizedForKey("override") +  " " + file.getFileName());
				try {
					file.overrideWith(content,doItNow);
				} catch (SaveResourceException e) {
					actionFailed(this, e, null);
				} catch (FlexoException e) {
					actionFailed(this, e, null);
				}			
			}
			
		}
	*/
	OverrideWithVersion(OverrideWithVersionActionType actionType, CGObject focusedObject, Vector<CGObject> globalSelection,
			FlexoEditor editor) {
		super(actionType, focusedObject, globalSelection, editor);
	}

	@Override
	public OverrideWithVersionActionType getActionType() {
		return (OverrideWithVersionActionType) super.getActionType();
	}

	public ContentSourceType getSourceType() {
		return getActionType().getSourceType();
	}

	@Override
	protected void doImpl(Object context) throws GenerationException, SaveResourceException, FlexoException {
		logger.info("Override with version " + getSourceType() + " for " + getFocusedObject());
		AbstractProjectGenerator<? extends GenerationRepository> pg = getProjectGenerator();
		pg.setAction(this);

		// Do it a files level
		makeFlexoProgress(
				FlexoLocalization.localizedForKey("generate") + " " + getFilesToOverride().size() + " "
						+ FlexoLocalization.localizedForKey("files") + " " + FlexoLocalization.localizedForKey("into") + " "
						+ getRepository().getDirectory().getAbsolutePath(), getFilesToOverride().size() + 1);

		for (AbstractCGFile file : getFilesToOverride()) {
			file.overrideWith(getSource(), doItNow());
			// addJob(new OverrideWithVersionForFile(file,getSource(),doItNow()));
		}
		// waitForAllJobsToComplete();
		if (doItNow()) {
			setProgress(FlexoLocalization.localizedForKey("save_rm"));
			getRepository().getProject().getFlexoRMResource().saveResourceData();
		}
		hideFlexoProgress();
	}

	private Vector<AbstractCGFile> _filesToOverride;

	public Vector<AbstractCGFile> getFilesToOverride() {
		if (_filesToOverride == null) {
			_filesToOverride = getSelectedCGFilesOnWhyCurrentActionShouldApply();
		}
		return _filesToOverride;
	}

	public void setFilesToOverride(Vector<AbstractCGFile> someFiles) {
		_filesToOverride = someFiles;
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

	public boolean requiresThreadPool() {
		return true;
	}

}
