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
package org.netbeans.lib.cvsclient.file;

import java.io.File;
import java.io.IOException;

import org.netbeans.lib.cvsclient.util.BugLog;

/**
 * @author Thomas Singer
 * @version Nov 17, 2001
 */
public class WindowsFileReadOnlyHandler implements FileReadOnlyHandler {
	// Implemented ============================================================

	/**
	 * Makes the specified file read-only or writable, depending on the specified readOnly flag.
	 * 
	 * @throws IOException
	 *             if something gone wrong
	 */
	@Override
	public void setFileReadOnly(File file, boolean readOnly) throws IOException {
		if (file == null) {
			throw new IllegalArgumentException("file must not be null"); // NOI18N
		}

		try {
			String[] arguments = { "attrib", // NOI18N
					readOnly ? "+r" : "-r", // NOI18N
					file.getName() };
			Process process = Runtime.getRuntime().exec(arguments, null, file.getParentFile());
			process.waitFor();
		} catch (InterruptedException ex) {
			BugLog.getInstance().showException(ex);
		}
	}
}
