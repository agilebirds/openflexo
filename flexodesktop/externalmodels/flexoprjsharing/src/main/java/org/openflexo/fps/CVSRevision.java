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
package org.openflexo.fps;

import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;

import org.netbeans.lib.cvsclient.command.log.LogInformation;
import org.openflexo.localization.FlexoLocalization;

public class CVSRevision extends FPSObject {

	private CVSRevisionIdentifier _identifier;
	private CVSFile _file;
	private String _contents;
	private LogInformation.Revision _revisionInfo;

	protected CVSRevision(CVSRevisionIdentifier identifier, CVSFile file) {
		super();
		_identifier = identifier;
		_file = file;
	}

	@Override
	public String getClassNameKey() {
		return "cvs_revision";
	}

	@Override
	public boolean isContainedIn(FPSObject obj) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String getInspectorName() {
		// No inspectable alone
		return null;
	}

	public CVSFile getFile() {
		return _file;
	}

	public CVSRevisionIdentifier getIdentifier() {
		return _identifier;
	}

	public String getContents() {
		return _contents;
	}

	protected void setContents(String contents) {
		_contents = contents;
	}

	public Date getDate() {
		if (_revisionInfo != null) {
			return _revisionInfo.getDate();
		}
		return null;
	}

	public String getDateAsString() {
		if (getDate() != null) {
			return (new SimpleDateFormat("dd/MM HH:mm:ss")).format(getDate());
		}
		return FlexoLocalization.localizedForKey("unknown");
	}

	public String getAuthor() {
		if (_revisionInfo != null) {
			return _revisionInfo.getAuthor();
		}
		return null;
	}

	public String getState() {
		if (_revisionInfo != null) {
			return _revisionInfo.getState();
		}
		return null;
	}

	public String getLines() {
		if (_revisionInfo != null) {
			return _revisionInfo.getLines();
		}
		return null;
	}

	public String getCommitID() {
		if (_revisionInfo != null) {
			return _revisionInfo.getCommitID();
		}
		return null;
	}

	public int getAddedLines() {
		if (_revisionInfo != null) {
			return _revisionInfo.getAddedLines();
		}
		return 0;
	}

	public int getRemovedLines() {
		if (_revisionInfo != null) {
			return _revisionInfo.getRemovedLines();
		}
		return 0;
	}

	public String getMessage() {
		if (_revisionInfo != null) {
			return _revisionInfo.getMessage();
		}
		return null;
	}

	public String getBranches() {
		if (_revisionInfo != null) {
			return _revisionInfo.getBranches();
		}
		return null;
	}

	protected LogInformation.Revision getRevisionInfo() {
		return _revisionInfo;
	}

	protected void setRevisionInfo(LogInformation.Revision revisionInfo) {
		_revisionInfo = revisionInfo;
	}

	protected static RevisionComparator COMPARATOR = new RevisionComparator();

	/**
	 * Used to sort properties according to name alphabetic ordering
	 * 
	 * @author sguerin
	 * 
	 */
	protected static class RevisionComparator implements Comparator<CVSRevision> {

		/**
		 * Implements
		 * 
		 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
		 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
		 */
		@Override
		public int compare(CVSRevision o1, CVSRevision o2) {
			return CVSRevisionIdentifier.COMPARATOR.compare(o1.getIdentifier(), o2.getIdentifier());
		}

	}

}
