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

package org.netbeans.lib.cvsclient.command.diff;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.netbeans.lib.cvsclient.command.FileInfoContainer;

/**
 * Describes diff information for 2 fversions of a file. This is the result of doing a cvs diff command. The fields in instances of this
 * object are populated by response handlers.
 * 
 * @author Milos Kleint
 */
public class DiffInformation extends FileInfoContainer {
	private File file;

	private String repositoryFileName;

	private String rightRevision;

	private String leftRevision;

	private String parameters;

	/**
	 * List of changes stored here
	 */
	private final List changesList = new ArrayList();

	private Iterator iterator;

	public DiffInformation() {
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
	 * Return a string representation of this object. Useful for debugging.
	 */
	@Override
	public String toString() {
		StringBuffer buf = new StringBuffer(30);
		buf.append("\nFile: " + (file != null ? file.getAbsolutePath() : "null")); // NOI18N
		buf.append("\nRCS file: " + repositoryFileName); // NOI18N
		buf.append("\nRevision: " + leftRevision); // NOI18N
		if (rightRevision != null) {
			buf.append("\nRevision: " + rightRevision); // NOI18N
		}
		buf.append("\nParameters: " + parameters); // NOI18N
		// buf.append(differences.toString());
		return buf.toString();
	}

	/**
	 * Getter for property rightRevision.
	 * 
	 * @return Value of property rightRevision.
	 */
	public String getRightRevision() {
		return rightRevision;
	}

	/**
	 * Setter for property rightRevision.
	 * 
	 * @param rightRevision
	 *            New value of property rightRevision.
	 */
	public void setRightRevision(String rightRevision) {
		this.rightRevision = rightRevision;
	}

	/**
	 * Getter for property leftRevision.
	 * 
	 * @return Value of property leftRevision.
	 */
	public String getLeftRevision() {
		return leftRevision;
	}

	/**
	 * Setter for property leftRevision.
	 * 
	 * @param leftRevision
	 *            New value of property leftRevision.
	 */
	public void setLeftRevision(String leftRevision) {
		this.leftRevision = leftRevision;
	}

	public String getParameters() {
		return parameters;
	}

	public void setParameters(String parameters) {
		this.parameters = parameters;
	}

	public DiffChange createDiffChange() {
		return new DiffChange();
	}

	public void addChange(DiffChange change) {
		changesList.add(change);
	}

	public DiffChange getFirstChange() {
		iterator = changesList.iterator();
		return getNextChange();
	}

	public DiffChange getNextChange() {
		if (iterator == null) {
			return null;
		}
		if (!iterator.hasNext()) {
			return null;
		}
		return (DiffChange) iterator.next();
	}

	public class DiffChange {
		public static final int ADD = 0;
		public static final int DELETE = 1;
		public static final int CHANGE = 2;

		protected int type;
		private int leftBeginning = -1;
		private int leftEnd = -1;
		private final List leftDiff = new ArrayList();
		private int rightBeginning = -1;
		private int rightEnd = -1;
		private final List rightDiff = new ArrayList();

		public DiffChange() {
		}

		public void setType(int typeChange) {
			// System.out.println("type=" + typeChange);
			type = typeChange;
		}

		public int getType() {
			return type;
		}

		public void setLeftRange(int min, int max) {
			// System.out.println("setLeftRange() min=" + min + "  max=" +max);
			leftBeginning = min;
			leftEnd = max;
		}

		public void setRightRange(int min, int max) {
			// System.out.println("setRightRange() min=" + min + "  max=" +max);
			rightBeginning = min;
			rightEnd = max;
		}

		public int getMainBeginning() {
			return rightBeginning;
		}

		public int getRightMin() {
			return rightBeginning;
		}

		public int getRightMax() {
			return rightEnd;
		}

		public int getLeftMin() {
			return leftBeginning;
		}

		public int getLeftMax() {
			return leftEnd;
		}

		public boolean isInRange(int number, boolean left) {
			if (left) {
				return number >= leftBeginning && number <= leftEnd;
			}

			return number >= rightBeginning && number <= rightEnd;
		}

		public String getLine(int number, boolean left) {
			if (left) {
				int index = number - leftBeginning;
				if (index < 0 || index >= leftDiff.size()) {
					return null;
				}
				String line = (String) leftDiff.get(index);
				return line;
			} else {
				int index = number - rightBeginning;
				if (index < 0 || index >= rightDiff.size()) {
					return null;
				}
				String line = (String) rightDiff.get(index);
				return line;
			}
		}

		public void appendLeftLine(String diffLine) {
			leftDiff.add(diffLine);
		}

		public void appendRightLine(String diffLine) {
			rightDiff.add(diffLine);
		}
	}
}