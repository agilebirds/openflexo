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
package org.openflexo.foundation.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openflexo.foundation.xml.FlexoXMLMappings;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.toolbox.FlexoVersion;

public class FlexoProjectUtil {

	private static final Logger logger = Logger.getLogger(FlexoProjectUtil.class.getPackage().getName());

	/**
	 * @param projectDirectory
	 * @return false whenever .version is less than 1.3 or project .version higher than current FlexoVersion.
	 */
	public static boolean isProjectOpenable(File projectDirectory) throws UnreadableProjectException {
		FlexoVersion version = getVersion(projectDirectory);
		if (version != null && version.major == 1 && version.minor < 3) {
			throw new UnreadableProjectException(FlexoLocalization.localizedForKey("project_is_too_old_please_use_intermediary_versions"));
		}
		if (currentFlexoVersionIsSmallerThanLastVersion(projectDirectory)) {
			throw new UnreadableProjectException(
					FlexoLocalization.localizedForKey("current_flexo_version_is_smaller_than_last_used_to_open_this_project"));
		}
		return true;
	}

	/**
	 * @param projectDirectory
	 *            some directory
	 * @return the FlexoVersion for projectDirectory or null if the projectDirectory don't contains any .version file.
	 */
	public static FlexoVersion getVersion(File projectDirectory) {
		File f = getVersionFile(projectDirectory);
		StringBuilder sb = new StringBuilder();
		byte[] b = new byte[512];
		FileInputStream fis;
		try {
			fis = new FileInputStream(f);
		} catch (FileNotFoundException e) {
			if (logger.isLoggable(Level.FINE)) {
				logger.fine(".version file not found in " + projectDirectory.getAbsolutePath());
			}
			return null;
		}
		int i = 0;
		while (i > -1) {
			try {
				i = fis.read(b);
				if (i > -1) {
					sb.append(new String(b, 0, i, "UTF-8"));
				}
			} catch (IOException e) {
				e.printStackTrace();
				try {
					fis.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				return null;
			}
		}
		try {
			fis.close();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		return new FlexoVersion(sb.toString());
	}

	/**
	 * @param projectDirectory
	 *            the project directory
	 * @return
	 */
	public static boolean currentFlexoVersionIsSmallerThanLastVersion(File projectDirectory) {
		File f = getVersionFile(projectDirectory);
		if (!f.exists()) {
			return false;
		} else {
			FlexoVersion v = getVersion(projectDirectory);
			// bidouille so that Version will accept 1.0.1RC1 as bigger than
			// 1.0.1beta
			if (logger.isLoggable(Level.FINE)) {
				logger.fine("Version is " + v);
			}
			return FlexoXMLMappings.latestRelease().isLesserThan(v);
		}
	}

	/**
	 * @param projectDirectory
	 * @return
	 */
	private static File getVersionFile(File projectDirectory) {
		String versionFileName = ".version";
		File f = new File(projectDirectory, versionFileName);
		return f;
	}

}
