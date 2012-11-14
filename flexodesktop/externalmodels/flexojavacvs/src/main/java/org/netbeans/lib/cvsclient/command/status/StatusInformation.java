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
package org.netbeans.lib.cvsclient.command.status;

import java.io.File;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.netbeans.lib.cvsclient.command.FileInfoContainer;
import org.netbeans.lib.cvsclient.file.FileStatus;

/**
 * Describes status information for a file. This is the result of doing a cvs status command. The fields in instances of this object are
 * populated by response handlers.
 * 
 * @author Robert Greig
 */
public class StatusInformation extends FileInfoContainer {
	// Fields =================================================================

	private File file;
	private FileStatus status;
	private String workingRevision;
	private String repositoryRevision;
	private String repositoryFileName;
	private String stickyDate;
	private String stickyOptions;
	private String stickyTag;

	/**
	 * Hold key pairs of existing tags.
	 */
	private List tags;

	private StringBuffer symNamesBuffer;

	public StatusInformation() {
		setAllExistingTags(null);
	}

	/**
	 * Getter for property file.
	 * 
	 * @return Value of property file.
	 */
	@Override
	public File getFile() {
		return file;
	}

	/**
	 * Setter for property file.
	 * 
	 * @param file
	 *            New value of property file.
	 */
	public void setFile(File file) {
		this.file = file;
	}

	/**
	 * Getter for property status.
	 * 
	 * @return Value of property status.
	 */
	public FileStatus getStatus() {
		return status;
	}

	/**
	 * Setter for property status.
	 * 
	 * @param status
	 *            New value of property status.
	 */
	public void setStatus(FileStatus status) {
		this.status = status;
	}

	/**
	 * Returns the status as a String. The String returned are definitely the static-final-instances.
	 */
	public String getStatusString() {
		if (status == null) {
			return null;
		}

		return status.toString();
	}

	/**
	 * Sets the status by the specified string.
	 */
	public void setStatusString(String statusString) {
		setStatus(FileStatus.getStatusForString(statusString));
	}

	/**
	 * Getter for property workingRevision.
	 * 
	 * @return Value of property workingRevision.
	 */
	public String getWorkingRevision() {
		return workingRevision;
	}

	/**
	 * Setter for property workingRevision.
	 * 
	 * @param workingRevision
	 *            New value of property workingRevision.
	 */
	public void setWorkingRevision(String workingRevision) {
		this.workingRevision = workingRevision;
	}

	/**
	 * Getter for property repositoryRevision.
	 * 
	 * @return Value of property repositoryRevision.
	 */
	public String getRepositoryRevision() {
		return repositoryRevision;
	}

	/**
	 * Setter for property repositoryRevision.
	 * 
	 * @param repositoryRevision
	 *            New value of property repositoryRevision.
	 */
	public void setRepositoryRevision(String repositoryRevision) {
		this.repositoryRevision = repositoryRevision;
	}

	/**
	 * Getter for property repositoryFileName.
	 * 
	 * @return Value of property repositoryFileName.
	 */
	public String getRepositoryFileName() {
		return repositoryFileName;
	}

	/**
	 * Setter for property repositoryFileName.
	 * 
	 * @param repositoryRevision
	 *            New value of property repositoryFileName.
	 */
	public void setRepositoryFileName(String repositoryFileName) {
		this.repositoryFileName = repositoryFileName;
	}

	/**
	 * Getter for property stickyTag.
	 * 
	 * @return Value of property stickyTag.
	 */
	public String getStickyTag() {
		return stickyTag;
	}

	/**
	 * Setter for property stickyTag.
	 * 
	 * @param stickyTag
	 *            New value of property stickyTag.
	 */
	public void setStickyTag(String stickyTag) {
		this.stickyTag = stickyTag;
	}

	/**
	 * Getter for property stickyDate.
	 * 
	 * @return Value of property stickyDate.
	 */
	public String getStickyDate() {
		return stickyDate;
	}

	/**
	 * Setter for property stickyDate.
	 * 
	 * @param stickyDate
	 *            New value of property stickyDate.
	 */
	public void setStickyDate(String stickyDate) {
		this.stickyDate = stickyDate;
	}

	/**
	 * Getter for property stickyOptions.
	 * 
	 * @return Value of property stickyOptions.
	 */
	public String getStickyOptions() {
		return stickyOptions;
	}

	/**
	 * Setter for property stickyOptions.
	 * 
	 * @param stickyOptions
	 *            New value of property stickyOptions.
	 */
	public void setStickyOptions(String stickyOptions) {
		this.stickyOptions = stickyOptions;
	}

	public void addExistingTag(String tagName, String revisionNumber) {
		if (symNamesBuffer == null) {
			symNamesBuffer = new StringBuffer();
		}
		symNamesBuffer.append(tagName);
		symNamesBuffer.append(" "); // NOI18N
		symNamesBuffer.append(revisionNumber);
		symNamesBuffer.append("\n"); // NOI18N
	}

	private void createSymNames() {
		tags = new LinkedList();

		if (symNamesBuffer == null) {
			return;
		}

		int length = 0;
		int lastLength = 0;
		while (length < symNamesBuffer.length()) {
			while (length < symNamesBuffer.length() && symNamesBuffer.charAt(length) != '\n') {
				length++;
			}

			if (length > lastLength) {
				String line = symNamesBuffer.substring(lastLength, length);
				String symName = line.substring(0, line.indexOf(' '));
				String revisionNumber = line.substring(line.indexOf(' ') + 1);
				SymName newName = new SymName();
				newName.setTag(symName);
				newName.setRevision(revisionNumber);
				tags.add(newName);
				lastLength = length + 1;
				length++;
			}
		}

		symNamesBuffer = null;
	}

	public List getAllExistingTags() {
		if (tags == null) {
			createSymNames();
		}
		return tags;
	}

	public void setAllExistingTags(List tags) {
		this.tags = tags;
	}

	/**
	 * Search the symbolic names by number of revision. If not found, return null.
	 */
	public List getSymNamesForRevision(String revNumber) {
		if (tags == null) {
			createSymNames();
		}

		List list = new LinkedList();

		for (Iterator it = tags.iterator(); it.hasNext();) {
			StatusInformation.SymName item = (StatusInformation.SymName) it.next();
			if (item.getRevision().equals(revNumber)) {
				list.add(item);
			}
		}
		return list;
	}

	/**
	 * Search the symbolic names by name of tag (symbolic name). If not found, return null.
	 */
	public StatusInformation.SymName getSymNameForTag(String tagName) {
		if (tags == null) {
			createSymNames();
		}

		for (Iterator it = tags.iterator(); it.hasNext();) {
			StatusInformation.SymName item = (StatusInformation.SymName) it.next();
			if (item.getTag().equals(tagName)) {
				return item;
			}
		}
		return null;
	}

	/**
	 * Return a string representation of this object. Useful for debugging.
	 */
	@Override
	public String toString() {
		StringBuffer buf = new StringBuffer();
		buf.append("\nFile: "); // NOI18N
		buf.append(file != null ? file.getAbsolutePath() : "null"); // NOI18N
		buf.append("\nStatus is: "); // NOI18N
		buf.append(getStatusString());
		buf.append("\nWorking revision: "); // NOI18N
		buf.append(workingRevision);
		buf.append("\nRepository revision: "); // NOI18N
		buf.append("\nSticky date: "); // NOI18N
		buf.append(stickyDate);
		buf.append("\nSticky options: "); // NOI18N
		buf.append(stickyOptions);
		buf.append("\nSticky tag: "); // NOI18N
		buf.append(stickyTag);
		if (tags != null && tags.size() > 0) {
			// we are having some tags to print
			buf.append("\nExisting Tags:"); // NOI18N
			for (Iterator it = tags.iterator(); it.hasNext();) {
				buf.append("\n  "); // NOI18N
				buf.append(it.next().toString());
			}
		}
		return buf.toString();
	}

	/**
	 * An inner class storing information about a symbolic name. Consists of a pair of Strings. tag + revision.
	 */
	public static class SymName {
		private String tag;
		private String revision;

		public SymName() {
		}

		public String getTag() {
			return tag;
		}

		public void setTag(String symName) {
			tag = symName;
		}

		public void setRevision(String rev) {
			revision = rev;
		}

		public String getRevision() {
			return revision;
		}

		@Override
		public String toString() {
			return getTag() + " : " + getRevision(); // NOI18N
		}
	}
}
