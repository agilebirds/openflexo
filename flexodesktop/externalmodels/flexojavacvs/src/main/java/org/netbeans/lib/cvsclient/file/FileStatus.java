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

/**
 * This class provides constants for file statuses.
 * 
 * @author Robert Greig
 */
public class FileStatus {
	/**
	 * Returns the corresponding FileStatus constant for the specified String.
	 */
	public static FileStatus getStatusForString(String statusString) {
		if (statusString == null) {
			return null;
		}

		if (statusString.equals(ADDED.toString())) {
			return ADDED;
		}
		if (statusString.equals(REMOVED.toString())) {
			return REMOVED;
		}
		if (statusString.equals(MODIFIED.toString())) {
			return MODIFIED;
		}
		if (statusString.equals(UP_TO_DATE.toString())) {
			return UP_TO_DATE;
		}
		if (statusString.equals(NEEDS_CHECKOUT.toString())) {
			return NEEDS_CHECKOUT;
		}
		if (statusString.equals(NEEDS_MERGE.toString())) {
			return NEEDS_MERGE;
		}
		if (statusString.equals(NEEDS_PATCH.toString())) {
			return NEEDS_PATCH;
		}
		if (statusString.equals(HAS_CONFLICTS.toString())) {
			return HAS_CONFLICTS;
		}
		if (statusString.equals(UNRESOLVED_CONFLICT.toString())) {
			return UNRESOLVED_CONFLICT;
		}
		if (statusString.equals(UNKNOWN.toString())) {
			return UNKNOWN;
		}
		if (statusString.equals(INVALID.toString())) {
			return INVALID;
		}
		return null;
	}

	/**
	 * The Added status, i.e. the file has been added to the repository but not committed yet.
	 */
	public static final FileStatus ADDED = new FileStatus("Locally Added"); // NOI18N

	/**
	 * The Removed status, i.e. the file has been removed from the repository but not committed yet
	 */
	public static final FileStatus REMOVED = new FileStatus("Locally Removed"); // NOI18N

	/**
	 * The locally modified status, i.e. the file has been modified locally and is out of sync with the repository
	 */
	public static final FileStatus MODIFIED = new FileStatus("Locally Modified"); // NOI18N

	/**
	 * The up-to-date status, i.e. the file is in sync with the repository
	 */
	public static final FileStatus UP_TO_DATE = new FileStatus("Up-to-date"); // NOI18N

	/**
	 * The "needs checkout" status, i.e. the file is out of sync with the repository and needs to be updated
	 */
	public static final FileStatus NEEDS_CHECKOUT = new FileStatus("Needs Checkout"); // NOI18N

	/**
	 * The "needs patch" status, i.e. the file is out of sync with the repository and needs to be patched
	 */
	public static final FileStatus NEEDS_PATCH = new FileStatus("Needs Patch"); // NOI18N

	/**
	 * The "needs merge" status, i.e. the file is locally modified and the file in the repository has been modified too
	 */
	public static final FileStatus NEEDS_MERGE = new FileStatus("Needs Merge"); // NOI18N

	/**
	 * The "conflicts" status, i.e. the file has been merged and now has conflicts that need resolved before it can be checked-in
	 */
	public static final FileStatus HAS_CONFLICTS = new FileStatus("File had conflicts on merge"); // NOI18N

	/**
	 * A file with the same name as this new file has been added to the repository from a second workspace. This file will need to be moved
	 * out of the way to allow an update to complete.
	 */
	public static final FileStatus UNRESOLVED_CONFLICT = new FileStatus("Unresolved Conflict"); // NOI18N

	/**
	 * The unknown status, i.e. the file is not known to the CVS repository.
	 */
	public static final FileStatus UNKNOWN = new FileStatus("Unknown"); // NOI18N

	/**
	 * The entry is invalid, appropriate file in CVS repository can not be found.
	 */
	public static final FileStatus INVALID = new FileStatus("Entry Invalid"); // NOI18N

	private final String statusString;

	/**
	 * Do not construct a FileStatus object youself, but use one of the static constants.
	 */
	private FileStatus(String statusString) {
		this.statusString = statusString;
	}

	/**
	 * Returns the String representation for thiz.
	 */
	@Override
	public String toString() {
		return statusString;
	}
}