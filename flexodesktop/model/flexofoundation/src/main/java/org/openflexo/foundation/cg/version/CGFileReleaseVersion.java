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
package org.openflexo.foundation.cg.version;

import java.io.File;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Vector;
import java.util.logging.Logger;

import org.openflexo.foundation.Inspectors;
import org.openflexo.foundation.cg.CGFile;
import org.openflexo.foundation.cg.dm.CGFileReleaseCleaned;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.toolbox.FileUtils;

public class CGFileReleaseVersion extends AbstractCGFileVersion {

	private static final Logger logger = Logger.getLogger(CGFileReleaseVersion.class.getPackage().getName());

	private Vector<CGFileIntermediateVersion> _intermediateVersions;

	private CGRelease _release;
	private boolean _needsSorting = true;

	/**
	 * Default constructor
	 */
	public CGFileReleaseVersion(CGFile cgFile, CGRelease release, File file) {
		super(cgFile, (release != null ? release.getVersionIdentifier() : CGVersionIdentifier.DEFAULT_VERSION_ID()), file);
		_release = release;
		_intermediateVersions = new Vector<CGFileIntermediateVersion>();
	}

	@Override
	public String getClassNameKey() {
		return "file_release_version";
	}

	@Override
	public String getInspectorName() {
		return Inspectors.GENERATORS.FILE_RELEASE_VERSION_INSPECTOR;
	}

	public Vector<CGFileIntermediateVersion> getIntermediateVersions() {
		if (_needsSorting) {
			Collections.sort(_intermediateVersions, new Comparator<CGFileIntermediateVersion>() {
				@Override
				public int compare(CGFileIntermediateVersion o1, CGFileIntermediateVersion o2) {
					return CGVersionIdentifier.COMPARATOR.compare(o1.getVersionId(), o2.getVersionId());
				}
			});
			_needsSorting = false;
		}
		return _intermediateVersions;
	}

	public void setIntermediateVersions(Vector<CGFileIntermediateVersion> intermediateVersions) {
		_intermediateVersions = intermediateVersions;
		_needsSorting = true;
	}

	public void addToIntermediateVersions(CGFileIntermediateVersion intermediateVersion) {
		_intermediateVersions.add(intermediateVersion);
		_needsSorting = true;
	}

	public void removeFromIntermediateVersions(CGFileIntermediateVersion intermediateVersion) {
		_intermediateVersions.remove(intermediateVersion);
		_needsSorting = true;
	}

	@Override
	public String getStringRepresentation() {
		return getVersionId().major + "." + getVersionId().minor + " - " + getName();
	}

	@Override
	public String getName() {
		return _release.getName();
	}

	@Override
	public String getDescription() {
		return _release.getDescription();
	}

	@Override
	public Date getDate() {
		return _release.getDate();
	}

	@Override
	public String getUserId() {
		return _release.getUserId();
	}

	public static class BeforeFirstRelease extends CGFileReleaseVersion {
		/**
		 * Default constructor
		 */
		public BeforeFirstRelease(CGFile file) {
			super(file, null, null);
		}

		@Override
		public String getInspectorName() {
			return Inspectors.GENERATORS.BEFORE_FIRST_RELEASE_INSPECTOR;
		}

		@Override
		public String getHelpText() {
			return FlexoLocalization.localizedForKey("before_first_release_help_text");
		}

		@Override
		public String getName() {
			return FlexoLocalization.localizedForKey("before_first_release");
		}

		@Override
		public String getDescription() {
			return getHelpText();
		}

		@Override
		public Date getDate() {
			return getCGFile().getProject().getCreationDate();
		}

		@Override
		public String getUserId() {
			return getCGFile().getProject().getCreationUserId();
		}

		@Override
		public String getStringRepresentation() {
			return getName();
		}

	}

	public void clean() {
		logger.info("Cleaning " + getStringRepresentation());
		for (CGFileIntermediateVersion intermediateVersion : _intermediateVersions) {
			if (!FileUtils.recursiveDeleteFile(intermediateVersion.getFile())) {
				logger.warning("Could not delete file " + intermediateVersion.getFile());
			}
			intermediateVersion.delete();
		}
		_intermediateVersions.clear();
		setChanged();
		notifyObservers(new CGFileReleaseCleaned(this));
	}

}
