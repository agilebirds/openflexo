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
package org.netbeans.lib.cvsclient.command.remove;

import java.io.File;

import org.netbeans.lib.cvsclient.command.FileInfoContainer;

/**
 * Describes remove information for a file. This is the result of doing a cvs remove command. The fields in instances of this object are
 * populated by response handlers.
 * 
 * @author Milos Kleint
 */
public class RemoveInformation extends FileInfoContainer {
	private File file;
	private boolean removed;

	public RemoveInformation() {
	}

	@Override
	public File getFile() {
		return file;
	}

	public void setFile(File file) {
		this.file = file;
	}

	public void setRemoved(boolean rem) {
		removed = rem;
	}

	public boolean isRemoved() {
		return removed;
	}

	/**
	 * Return a string representation of this object. Useful for debugging.
	 */
	@Override
	public String toString() {
		StringBuffer buf = new StringBuffer(30);
		buf.append("  "); // NOI18N
		buf.append(file != null ? file.getAbsolutePath() : "null"); // NOI18N
		return buf.toString();
	}
}
