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

import org.netbeans.lib.cvsclient.command.Builder;
import org.netbeans.lib.cvsclient.event.EventManager;
import org.netbeans.lib.cvsclient.event.FileInfoEvent;

/**
 * Handles the building of a diff information object and the firing of events when complete objects are built.
 * 
 * @author Milos Kleint
 */
public class SimpleDiffBuilder implements Builder {

	/**
	 * The event manager to use
	 */
	protected EventManager eventManager;

	protected DiffCommand diffCommand;
	/**
	 * The diff object that is currently being built
	 */
	protected DiffInformation diffInformation;

	/**
	 * The directory in which the file being processed lives. This is relative to the local directory
	 */
	protected String fileDirectory;

	protected boolean readingDiffs = false;
	private static final String UNKNOWN = ": I know nothing about"; // NOI18N
	private static final String CANNOT_FIND = ": cannot find"; // NOI18N
	private static final String UNKNOWN_TAG = ": tag"; // NOI18N
	private static final String EXAM_DIR = ": Diffing"; // NOI18N

	private static final String FILE = "Index: "; // NOI18N
	private static final String RCS_FILE = "RCS file: "; // NOI18N
	private static final String REVISION = "retrieving revision "; // NOI18N
	private static final String PARAMETERS = "diff "; // NOI18N
	private DiffInformation.DiffChange currentChange;

	public SimpleDiffBuilder(EventManager eventMan, DiffCommand diffComm) {
		eventManager = eventMan;
		diffCommand = diffComm;
	}

	@Override
	public void outputDone() {
		if (diffInformation != null) {
			if (currentChange != null) {
				diffInformation.addChange(currentChange);
				currentChange = null;
			}
			eventManager.fireCVSEvent(new FileInfoEvent(this, diffInformation));
			diffInformation = null;
			readingDiffs = false;
		}
	}

	@Override
	public void parseLine(String line, boolean isErrorMessage) {
		if (readingDiffs) {
			if (line.startsWith(FILE)) {
				outputDone();
			} else {
				processDifferences(line);
				return;
			}
		}
		if (line.indexOf(UNKNOWN) >= 0) {
			eventManager.fireCVSEvent(new FileInfoEvent(this, diffInformation));
			diffInformation = null;
			return;
		}
		if (line.indexOf(EXAM_DIR) >= 0) {
			fileDirectory = line.substring(line.indexOf(EXAM_DIR) + EXAM_DIR.length()).trim();
			return;
		}
		if (line.startsWith(FILE)) {
			processFile(line.substring(FILE.length()));
			return;
		}
		if (line.startsWith(RCS_FILE)) {
			processRCSfile(line.substring(RCS_FILE.length()));
			return;
		}
		if (line.startsWith(REVISION)) {
			processRevision(line.substring(REVISION.length()));
			return;
		}
		if (line.startsWith(PARAMETERS)) {
			processParameters(line.substring(PARAMETERS.length()));
			readingDiffs = true;
			return;
		}
	}

	/*        protected void processDifferences(String line) {
	            diffInformation.addToDifferences(line);
	        }
	 */
	protected void processFile(String line) {
		outputDone();
		diffInformation = createDiffInformation();
		String fileName = line.trim();
		if (fileName.startsWith("no file")) { // NOI18N
			fileName = fileName.substring(8);
		}
		diffInformation.setFile(new File(diffCommand.getLocalDirectory(),
		// ((fileDirectory!=null)?fileDirectory: "") + File.separator +
				fileName));
	}

	protected void processRCSfile(String line) {
		if (diffInformation == null) {
			return;
		}
		diffInformation.setRepositoryFileName(line.trim());
	}

	protected void processRevision(String line) {
		if (diffInformation == null) {
			return;
		}
		line = line.trim();
		// first REVISION line is the from-file, the second is the to-file
		if (diffInformation.getLeftRevision() != null) {
			diffInformation.setRightRevision(line);
		} else {
			diffInformation.setLeftRevision(line);
		}
	}

	protected void processParameters(String line) {
		if (diffInformation == null) {
			return;
		}
		diffInformation.setParameters(line.trim());
	}

	public DiffInformation createDiffInformation() {
		return new DiffInformation();
	}

	protected void assignType(DiffInformation.DiffChange change, String line) {
		int index = 0;
		int cIndex = line.indexOf('c');
		if (cIndex > 0) {
			// change type of change
			change.setType(DiffInformation.DiffChange.CHANGE);
			index = cIndex;
		} else {
			int aIndex = line.indexOf('a');
			if (aIndex > 0) {
				// add type of change
				change.setType(DiffInformation.DiffChange.ADD);
				index = aIndex;
			} else {
				int dIndex = line.indexOf('d');
				if (dIndex > 0) {
					// delete type of change
					change.setType(DiffInformation.DiffChange.DELETE);
					index = dIndex;
				}
			}
		}
		String left = line.substring(0, index);
		// System.out.println("left part of change=" + left);
		change.setLeftRange(getMin(left), getMax(left));
		String right = line.substring(index + 1);
		// System.out.println("right part of change=" + right);
		change.setRightRange(getMin(right), getMax(right));
	}

	private int getMin(String line) {
		String nums = line;
		int commaIndex = nums.indexOf(',');
		if (commaIndex > 0) {
			nums = nums.substring(0, commaIndex);
		}
		int min;
		try {
			min = Integer.parseInt(nums);
		} catch (NumberFormatException exc) {
			min = 0;
		}
		// System.out.println("Min=" + min);
		return min;
	}

	private int getMax(String line) {
		String nums = line;
		int commaIndex = nums.indexOf(',');
		if (commaIndex > 0) {
			nums = nums.substring(commaIndex + 1);
		}
		int max;
		try {
			max = Integer.parseInt(nums);
		} catch (NumberFormatException exc) {
			max = 0;
		}
		// System.out.println("Max=" + max);
		return max;
	}

	protected void processDifferences(String line) {
		char firstChar = line.charAt(0);
		if (firstChar >= '0' && firstChar <= '9') {
			// we got a new difference here
			// System.out.println("new Change=" + line);
			if (currentChange != null) {
				diffInformation.addChange(currentChange);
			}
			currentChange = diffInformation.createDiffChange();
			assignType(currentChange, line);
		}
		if (firstChar == '<') {
			// System.out.println("Left line=" + line);
			currentChange.appendLeftLine(line.substring(2));
		}
		if (firstChar == '>') {
			// System.out.println("right line=" + line);
			currentChange.appendRightLine(line.substring(2));
		}

	}

	@Override
	public void parseEnhancedMessage(String key, Object value) {
	}

}
