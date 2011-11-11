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
package org.openflexo.fps.automerge;

import org.openflexo.diff.ComputeDiff;
import org.openflexo.diff.DiffSource;
import org.openflexo.diff.ComputeDiff.DiffChange;
import org.openflexo.diff.ComputeDiff.DiffReport;
import org.openflexo.diff.merge.AbstractAutomaticMergeResolvingRule;
import org.openflexo.diff.merge.DetailedMerge;
import org.openflexo.diff.merge.MergeChange;

public class IgnoreRightUnsignificantChangesRemovedByOpposite extends AbstractAutomaticMergeResolvingRule {

	private static final String QUOTE = "\"";

	private String[] _attributesToIgnore;

	public IgnoreRightUnsignificantChangesRemovedByOpposite(String... attributesToIgnore) {
		super();
		_attributesToIgnore = attributesToIgnore;
	}

	@Override
	public String getMergedResult(MergeChange change) {
		return "";
	}

	@Override
	public boolean isApplicable(MergeChange change) {
		if (change.getMerge() instanceof DetailedMerge)
			return false;
		if (!change.getLeftText().equals(""))
			return false;
		DiffSource originalSource = new DiffSource(change.getOriginalText(), change.getMerge().getDocumentType().getDelimitingMethod());
		DiffSource rightSource = new DiffSource(change.getRightText(), change.getMerge().getDocumentType().getDelimitingMethod());
		DiffReport diffs = ComputeDiff.diff(originalSource, rightSource);
		for (DiffChange c : diffs.getChanges()) {
			boolean isUnsignificantChange = false;
			for (String attributeName : _attributesToIgnore) {
				if (isXMLAttributeValueConflict(c, originalSource, rightSource, attributeName)) {
					// System.out.println ("DiffChange: "+c+" is attribute value conflict for "+attributeName);
					isUnsignificantChange = true;
				} else if (extractContainerAttributeValueFromRight(c, rightSource, attributeName) != null) {
					// System.out.println ("DiffChange: "+c+" is inside value conflict for "+attributeName);
					isUnsignificantChange = true;
				}
			}
			if (!isUnsignificantChange) {
				// System.out.println ("DiffChange: "+c+" is signifiant");
				return false;
			}
		}
		return true;
	}

	@Override
	public String getDescription() {
		return "ignore_unsignificant_changes_removed_by_other_party";
	}

	public static boolean isXMLAttributeValueConflict(DiffChange change, DiffSource leftSource, DiffSource rightSource, String attributeName) {
		try {
			if ((change.getFirst0() == change.getLast0()) && (change.getFirst1() == change.getLast1())) {
				if ((relativeLeftTokenAt(change, leftSource, -3).equals(attributeName))
						&& (relativeLeftTokenAt(change, leftSource, -2).equals("="))
						&& (relativeLeftTokenAt(change, leftSource, -1).equals(QUOTE))
						&& (relativeLeftTokenAt(change, leftSource, 1).equals(QUOTE))
						&& (relativeRightTokenAt(change, rightSource, -3).equals(attributeName))
						&& (relativeRightTokenAt(change, rightSource, -2).equals("="))
						&& (relativeRightTokenAt(change, rightSource, -1).equals(QUOTE))
						&& (relativeRightTokenAt(change, rightSource, 1).equals(QUOTE))) {
					return true;
				}
			}
		} catch (IndexOutOfBoundsException e) {
			// Return false
		}
		return false;
	}

	protected static String extractContainerAttributeValueFromLeft(DiffChange change, DiffSource leftSource, String attributeName) {
		int startTokenRelativeIndex = 0;
		boolean startIndexFound = false;
		while (!startIndexFound) {
			try {
				if (relativeLeftTokenAt(change, leftSource, startTokenRelativeIndex).equals(QUOTE)) {
					startIndexFound = true;
				} else {
					startTokenRelativeIndex--;
				}
			} catch (IndexOutOfBoundsException e) {
				return null;
			}
		}

		int endTokenRelativeIndex = 0;
		boolean endIndexFound = false;
		while (!endIndexFound) {
			try {
				if (relativeLeftTokenAt(change, leftSource, endTokenRelativeIndex).equals(QUOTE)) {
					endIndexFound = true;
				} else {
					endTokenRelativeIndex++;
				}
			} catch (IndexOutOfBoundsException e) {
				return null;
			}
		}

		try {
			if ((relativeLeftTokenAt(change, leftSource, startTokenRelativeIndex - 2).equals(attributeName))
					&& (relativeLeftTokenAt(change, leftSource, startTokenRelativeIndex - 1).equals("="))) {

				StringBuffer sb = new StringBuffer();
				for (int i = startTokenRelativeIndex + 1; i < endTokenRelativeIndex; i++) {
					sb.append(leftSource.tokenAt(change.getFirst0() + i).getFullString());
				}
				return sb.toString();
			}
		} catch (IndexOutOfBoundsException e) {
			return null;
		}

		return null;
	}

	protected static String extractContainerAttributeValueFromRight(DiffChange change, DiffSource rightSource, String attributeName) {
		int startTokenRelativeIndex = 0;
		boolean startIndexFound = false;
		while (!startIndexFound) {
			try {
				if (relativeRightTokenAt(change, rightSource, startTokenRelativeIndex).equals(QUOTE)) {
					startIndexFound = true;
				} else {
					startTokenRelativeIndex--;
				}
			} catch (IndexOutOfBoundsException e) {
				return null;
			}
		}

		int endTokenRelativeIndex = 0;
		boolean endIndexFound = false;
		while (!endIndexFound) {
			try {
				if (relativeRightTokenAt(change, rightSource, endTokenRelativeIndex).equals(QUOTE)) {
					endIndexFound = true;
				} else {
					endTokenRelativeIndex++;
				}
			} catch (IndexOutOfBoundsException e) {
				return null;
			}
		}

		try {
			if ((relativeRightTokenAt(change, rightSource, startTokenRelativeIndex - 2).equals(attributeName))
					&& (relativeRightTokenAt(change, rightSource, startTokenRelativeIndex - 1).equals("="))) {

				StringBuffer sb = new StringBuffer();
				for (int i = startTokenRelativeIndex + 1; i < endTokenRelativeIndex; i++) {
					sb.append(rightSource.tokenAt(change.getFirst1() + i).getFullString());
				}
				return sb.toString();
			}
		} catch (IndexOutOfBoundsException e) {
			return null;
		}

		return null;
	}

}
