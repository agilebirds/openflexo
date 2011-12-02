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
package org.openflexo.xmlcode;

import java.io.File;

public class TestFileFinder {

	private static final String pathWorkspace = System.getProperty("user.dir") + "/../FlexoXMLCoDe/src/test/resources/";
	private static final String pathHudson = "tmp/tests/FlexoResources/";

	public static File findTestFile(String fileName) {
		String fullPath1 = pathWorkspace + fileName;
		File reply = new File(fullPath1);
		if (reply.exists()) {
			return reply;
		}
		return new File(pathHudson + fileName);
	}
}
