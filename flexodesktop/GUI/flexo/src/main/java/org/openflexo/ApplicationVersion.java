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

package org.openflexo;

import java.io.FileInputStream;

import org.apache.commons.io.IOUtils;
import org.openflexo.toolbox.FileResource;

public class ApplicationVersion {
	public static String BUSINESS_APPLICATION_VERSION = "1.5.2";
	// Must be like x.x.x or x.x or x.xalpha or x.xbeta or x.x.xRCxx or x.x.xalpha x.x.xbeta
	// This field must be non-final because it will be generated during the build procedure.
	// If you declare this field final, then the compiler will copy it's value directly and you will not see the value of the build
	public static String BUILD_ID = "dev";

	public static String COMMIT_ID = "dev";

	// Next block is not necessary in production and should not be used in production
	static {
		FileInputStream headInputStream = null;
		FileInputStream commitInputStream = null;
		try {
			FileResource HEAD = new FileResource(".git/HEAD");
			if (HEAD.exists()) {
				headInputStream = new FileInputStream(HEAD);
				String headContent = IOUtils.toString(headInputStream);
				int index = headContent.indexOf("ref:");
				if (index > -1) {
					headContent = headContent.substring("ref:".length()).trim();
				}
				FileResource commit = new FileResource(".git/" + headContent);
				if (commit.exists()) {
					commitInputStream = new FileInputStream(commit);
					COMMIT_ID = IOUtils.toString(commitInputStream).trim();
					System.err.println("Found COMMIT ID " + COMMIT_ID);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			IOUtils.closeQuietly(headInputStream);
			IOUtils.closeQuietly(commitInputStream);
		}
	}

}
