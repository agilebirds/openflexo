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
package org.netbeans.lib.cvsclient.command.commit;

import org.netbeans.lib.cvsclient.command.DefaultFileInfoContainer;

/**
 * Describes commit information for a file. This is the result of doing a cvs commit command. The fields in instances of this object are
 * populated by response handlers.
 * 
 * @author Milos Kleint
 */
public class CommitInformation extends DefaultFileInfoContainer {

	public static final String ADDED = "Added"; // NOI18N
	public static final String REMOVED = "Removed"; // NOI18N
	public static final String CHANGED = "Changed"; // NOI18N
	public static final String UNKNOWN = "Unknown"; // NOI18N
	public static final String TO_ADD = "To-be-added"; // NOI18N

	/**
	 * The new revision (for "Added" and "Changed") or old revision (for "Removed").
	 */
	private String revision;

	public CommitInformation() {
	}

	/**
	 * Getter for property revision.
	 * 
	 * @return Value of property revision.
	 */
	public String getRevision() {
		return revision;
	}

	/**
	 * Setter for property revision.
	 * 
	 * @param revision
	 *            New value of property revision.
	 */
	public void setRevision(String revision) {
		this.revision = revision;
	}
}