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
package org.openflexo.diff.merge;

import java.util.List;
import java.util.Observable;
import java.util.Vector;

import org.openflexo.diff.ComputeDiff;
import org.openflexo.diff.ComputeDiff.DiffChange;
import org.openflexo.diff.ComputeDiff.DiffReport;
import org.openflexo.diff.DelimitingMethod;
import org.openflexo.diff.DiffSource;
import org.openflexo.diff.merge.MergeChange.ChangeCategory;
import org.openflexo.diff.merge.MergeChange.MergeChangeAction;
import org.openflexo.diff.merge.MergeChange.MergeChangeResult;
import org.openflexo.diff.merge.MergeChange.MergeChangeSource;
import org.openflexo.diff.merge.MergeChange.MergeChangeType;

public class Merge extends Observable implements IMerge {

	protected static final boolean debug = false;

	private static final MergeChangeAction DEFAULT_ACTION = MergeChangeAction.Undecided;

	private DiffSource _original;
	private DiffSource _left;
	private DiffSource _right;

	private Vector<MergeChange> changes;

	private MergedDocumentType _docType;

	public Merge(DiffSource original, DiffSource left, DiffSource right, MergedDocumentType docType) {
		super();
		_docType = docType;
		DelimitingMethod delimitingMethod = original.getDelimitingMethod();
		if (left.getDelimitingMethod() != delimitingMethod || right.getDelimitingMethod() != delimitingMethod) {
			throw new IllegalArgumentException("Cannot merge between sources with different delimiting method");
		}
		_original = original;
		_left = left;
		_right = right;
		changes = new Vector<MergeChange>();
		computeChanges();
	}

	public Merge(String original, String left, String right, MergedDocumentType docType) {
		this(new DiffSource(original), new DiffSource(left), new DiffSource(right), docType);
	}

	protected void recompute() {
		changes.clear();
		computeChanges();
	}

	public void delete() {
		deleteObservers();
		changes.clear();
		_original = null;
		_left = null;
		_right = null;
	}

	public boolean isReallyConflicting() {
		for (MergeChange change : getChanges()) {
			if (change.getMergeChangeSource() == MergeChange.MergeChangeSource.Conflict) {
				return true;
			}
		}
		return false;
	}

	private boolean isResolvedNeedsRecomputing = true;
	private boolean isResolved;

	@Override
	public boolean isResolved() {
		if (isResolvedNeedsRecomputing) {
			isResolved = true;
			for (MergeChange change : getChanges()) {
				if (!change.isResolved()) {
					isResolved = false;
					break;
				}
			}
			isResolvedNeedsRecomputing = false;
		}
		return isResolved;
	}

	protected void addChange(MergeChange change) {
		changes.add(change);
		change.setMerge(this);
	}

	@Override
	public Vector<MergeChange> getChanges() {
		return changes;
	}

	@Override
	public int getLeftChangeCount() {
		int returned = 0;
		for (MergeChange change : getChanges()) {
			if (change.getMergeChangeSource() == MergeChangeSource.Left) {
				returned++;
			}
		}
		return returned;
	}

	@Override
	public int getRightChangeCount() {
		int returned = 0;
		for (MergeChange change : getChanges()) {
			if (change.getMergeChangeSource() == MergeChangeSource.Right) {
				returned++;
			}
		}
		return returned;
	}

	@Override
	public int getConflictsChangeCount() {
		int returned = 0;
		for (MergeChange change : getChanges()) {
			if (change.getMergeChangeSource() == MergeChangeSource.Conflict) {
				returned++;
			}
		}
		return returned;
	}

	@Override
	public int getResolvedConflictsChangeCount() {
		int returned = 0;
		for (MergeChange change : getChanges()) {
			if (change.getMergeChangeSource() == MergeChangeSource.Conflict) {
				if (change.isResolved()) {
					returned++;
				}
			}
		}
		return returned;
	}

	public int getUnresolvedConflictsChangeCount() {
		int returned = 0;
		for (MergeChange change : getChanges()) {
			if (change.getMergeChangeSource() == MergeChangeSource.Conflict) {
				if (!change.isResolved()) {
					returned++;
				}
			}
		}
		return returned;
	}

	@Override
	public String toString() {
		if (changes.size() == 0) {
			return "Merge: no changes";
		} else {
			StringBuffer returned = new StringBuffer();
			for (MergeChange c : getChanges()) {
				returned.append(c);
			}
			return returned.toString();
		}
	}

	@Override
	public MergeChange changeBefore(MergeChange change) {
		int index = changes.indexOf(change);
		if (index >= 1) {
			return changes.get(index - 1);
		}
		return null;
	}

	@Override
	public DiffSource getLeftSource() {
		return _left;
	}

	@Override
	public DiffSource getOriginalSource() {
		return _original;
	}

	@Override
	public DiffSource getRightSource() {
		return _right;
	}

	public void setLeftSource(DiffSource leftSource) {
		_left = leftSource;
		recompute();
	}

	public void setOriginalSource(DiffSource originalSource) {
		_original = originalSource;
		recompute();
	}

	public void setRightSource(DiffSource rightSource) {
		_right = rightSource;
		recompute();
	}

	private boolean intersect(DiffChange aLeftChange, DiffChange aRightChange) {
		int l1 = aLeftChange.getFirst1();
		int l2 = aLeftChange.getLast1();
		int r1 = aRightChange.getFirst0();
		int r2 = aRightChange.getLast0();
		if (l1 == r1 && l2 == r2) {
			return true;
		}
		return l1 >= r1 && l1 <= r2 || l2 >= r1 && l2 <= r2 || l1 <= r1 && l2 >= r2;
	}

	private DiffChange getNextLeftChangesFromLine(int lineNb, DiffReport leftReport) {
		for (DiffChange change : leftReport.getChanges()) {
			if (change.getFirst1() >= lineNb && !processedChanges.contains(change)) {
				return change;
			}
		}
		return null;
	}

	private DiffChange getNextRightChangesFromLine(int lineNb, DiffReport rightReport) {
		for (DiffChange change : rightReport.getChanges()) {
			if (change.getFirst0() >= lineNb && !processedChanges.contains(change)) {
				return change;
			}
		}
		return null;
	}

	private int leftToOriginal = 0;
	private int originalToRight = 0;
	private Vector<DiffChange> processedChanges;

	protected void computeChanges() {
		DiffReport leftReport = ComputeDiff.diff(_left, _original);
		DiffReport rightReport = ComputeDiff.diff(_original, _right);
		if (debug) {
			System.out.println("left-diff:\n" + leftReport);
		}
		if (debug) {
			System.out.println("right-diff:\n" + rightReport);
		}

		leftToOriginal = 0;
		originalToRight = 0;

		int currentLineNb = 0;
		int last = -1;
		processedChanges = new Vector<DiffChange>();
		int lastProcessedChange = processedChanges.size();

		boolean originalIsEmpty = _original.getTextTokens().length == 0;

		while (currentLineNb < _original.getTextTokens().length || originalIsEmpty) {

			originalIsEmpty = false;

			if (currentLineNb == last && lastProcessedChange == processedChanges.size()) {
				new Exception().printStackTrace();
				System.err.println("Current:" + currentLineNb + " stopped on infinite loop");
				return;
			} else {
				last = currentLineNb;
				lastProcessedChange = processedChanges.size();
			}
			//
			DiffChange rightChange = getNextRightChangesFromLine(currentLineNb, rightReport);
			DiffChange leftChange = getNextLeftChangesFromLine(currentLineNb, leftReport);
			if (rightChange == null && leftChange == null) {
				currentLineNb = _original.getTextTokens().length;
			} else {
				if (debug) {
					System.out.println("=============================");
				}
				if (debug) {
					System.out.println("BEGIN currentLineNb=" + currentLineNb);
				}
				if (debug) {
					System.out.println("Next right=" + rightChange);
				}
				if (debug) {
					System.out.println("Next left=" + leftChange);
				}
				if (debug) {
					System.out.println("BEFORE NEW INSERTION leftToOriginal=" + leftToOriginal);
				}
				if (debug) {
					System.out.println("BEFORE NEW INSERTION originalToRight=" + originalToRight);
				}
				// At last one not null
				if (rightChange == null) {
					// Add left one
					MergeChange newChange = addLeftChange(leftChange);
					currentLineNb = newChange.getLast1() + 1;
					if (debug) {
						System.out.println("ADD LEFT CHANGE: " + leftChange + " > " + newChange);
					}
				} else if (leftChange == null) {
					// Add right one
					MergeChange newChange = addRightChange(rightChange);
					currentLineNb = newChange.getLast1() + 1;
					if (debug) {
						System.out.println("ADD RIGHT CHANGE: " + rightChange + " > " + newChange);
					}
				} else {
					// Still both side
					if (intersect(leftChange, rightChange)) {
						if (debug) {
							System.out.println("Begin MERGING : " + leftChange + " and " + rightChange);
						}
						Vector<DiffChange> leftChangesToMerge = new Vector<DiffChange>();
						Vector<DiffChange> rightChangesToMerge = new Vector<DiffChange>();
						leftChangesToMerge.add(leftChange);
						if (debug) {
							System.out.println("Add LEFT : " + leftChange);
						}
						rightChangesToMerge.add(rightChange);
						if (debug) {
							System.out.println("Add RIGHT : " + rightChange);
						}
						DiffChange result = new ComputeDiff.ModificationChange();
						DiffChange nextRightChange;
						DiffChange nextLeftChange;
						boolean moreMerges;
						result.setFirst0(Math.max(leftChange.getFirst1(), rightChange.getFirst0()));
						result.setLast0(Math.max(leftChange.getLast1(), rightChange.getLast0()));
						result.setFirst1(Math.max(leftChange.getFirst1(), rightChange.getFirst0()));
						result.setLast1(Math.max(leftChange.getLast1(), rightChange.getLast0()));
						if (debug) {
							System.out.println("result=" + result);
						}
						do {
							moreMerges = false;
							// next right
							int currentLastLineRight = rightChange.getLast0() + 1;
							boolean foundAnOtherOne = true;
							while (foundAnOtherOne) {
								foundAnOtherOne = false;
								nextRightChange = getNextRightChangesFromLine(currentLastLineRight, rightReport);
								if (debug) {
									System.out.println("Examining nextRightChange: " + nextRightChange);
								}
								if (nextRightChange != null && intersect(result, nextRightChange) && nextRightChange != rightChange) {
									if (debug) {
										System.out.println("Add nextRightChange: " + nextRightChange);
									}
									rightChangesToMerge.add(nextRightChange);
									rightChange = nextRightChange;
									currentLastLineRight = rightChange.getLast0();
									moreMerges = true;
									result.setLast0(Math.max(result.getLast0(), rightChange.getLast0()));
									result.setLast1(result.getLast0());
									foundAnOtherOne = true;
								}
							}
							// next left
							int currentLastLineLeft = leftChange.getLast1() + 1;
							foundAnOtherOne = true;
							while (foundAnOtherOne) {
								foundAnOtherOne = false;
								nextLeftChange = getNextLeftChangesFromLine(currentLastLineLeft, leftReport);
								if (debug) {
									System.out.println("Examining nextLeftChange: " + nextLeftChange);
								}
								if (nextLeftChange != null && intersect(nextLeftChange, result) && nextLeftChange != leftChange) {
									if (debug) {
										System.out.println("Add nextLeftChange: " + nextLeftChange);
									}
									leftChangesToMerge.add(nextLeftChange);
									leftChange = nextLeftChange;
									currentLastLineLeft = leftChange.getLast1();
									moreMerges = true;
									result.setLast1(Math.max(result.getLast1(), leftChange.getLast1()));
									result.setLast0(result.getLast1());
									foundAnOtherOne = true;
								}
							}
						} while (moreMerges);
						MergeChange newChange = addMergedChanges(leftChangesToMerge, rightChangesToMerge);
						currentLineNb = newChange.getLast1() + 1;
						if (debug) {
							System.out.println("ADD MERGED CHANGES:  > " + newChange);
						}
					} else {
						if (leftChange.getFirst1() <= rightChange.getFirst0()) {
							MergeChange newChange = addLeftChange(leftChange);
							currentLineNb = newChange.getLast1() + 1;
							if (debug) {
								System.out.println("ADD LEFT CHANGE: " + leftChange + " > " + newChange);
							}
						} else {
							MergeChange newChange = addRightChange(rightChange);
							currentLineNb = newChange.getLast1() + 1;
							if (debug) {
								System.out.println("ADD RIGHT CHANGE: " + rightChange + " > " + newChange);
							}
						}
					}
				}
			}
			if (debug) {
				System.out.println("AFTER NEW INSERTION leftToOriginal=" + leftToOriginal);
			}
			if (debug) {
				System.out.println("AFTER NEW INSERTION originalToRight=" + originalToRight);
			}
			if (debug) {
				System.out.println("END currentLineNb=" + currentLineNb);
			}
		}

		mergeNeedsRecomputing = true;

		// System.err.println("left: "+getLeftSource().hashCode());
		// System.err.println("left: "+this.hashCode()+" "+getLeftSource().getSourceString());

		setChanged();
		notifyObservers(new MergeRecomputed());

	}

	public class MergeRecomputed {

	}

	private MergeChange addLeftChange(DiffChange leftChange) {
		MergeChange newChange = MergeChange.makeLeftMergeChange(leftChange, originalToRight);
		processedChanges.add(leftChange);
		newChange.setMergeChangeSource(MergeChange.MergeChangeSource.Left);
		addChange(newChange);
		leftToOriginal += leftChange.getLast1() - leftChange.getFirst1() - (leftChange.getLast0() - leftChange.getFirst0());
		newChange.setDebug("[" + leftChange.toNiceString(false) + "], leftToOriginal=" + leftToOriginal + " originalToRight="
				+ originalToRight);
		// return leftChange.getLast1()+1/*-leftToOriginal*/;
		return newChange;
	}

	private MergeChange addRightChange(DiffChange rightChange) {
		MergeChange newChange = MergeChange.makeRightMergeChange(rightChange, leftToOriginal);
		processedChanges.add(rightChange);
		newChange.setMergeChangeSource(MergeChange.MergeChangeSource.Right);
		addChange(newChange);
		originalToRight += rightChange.getLast0() - rightChange.getFirst0() - (rightChange.getLast1() - rightChange.getFirst1());
		newChange.setDebug("[" + rightChange.toNiceString(false) + "], leftToOriginal=" + leftToOriginal + " originalToRight="
				+ originalToRight);
		// return rightChange.getLast0()+1/*-originalToRight*/;
		return newChange;
	}

	private MergeChange addMergedChanges(Vector<DiffChange> leftVector, Vector<DiffChange> rightVector) {
		String mergeDebug = "";
		boolean isFirst = true;

		MergeChangeAction defaultAction;
		if (getDocumentType() != null && getDocumentType().getAutomaticMergeResolvingModel() != null) {
			defaultAction = MergeChangeAction.AutomaticMergeResolving;
		} else {
			defaultAction = DEFAULT_ACTION;
		}

		MergeChange leftMerges = MergeChange.makeLeftMergeChange(leftVector, originalToRight, rightVector, leftToOriginal, defaultAction);
		for (DiffChange change : leftVector) {
			processedChanges.add(change);
			MergeChange debug = MergeChange.makeLeftMergeChange(change, originalToRight);
			mergeDebug += (isFirst ? "" : ",") + debug.toNiceString();
			isFirst = false;
		}

		MergeChange rightMerges = MergeChange.makeRightMergeChange(rightVector, leftToOriginal, leftVector, originalToRight, defaultAction);
		for (DiffChange change : rightVector) {
			processedChanges.add(change);
			MergeChange debug = MergeChange.makeRightMergeChange(change, leftToOriginal);
			mergeDebug += "," + debug.toNiceString();
		}

		if (debug) {
			System.out.println("Left merges: " + leftMerges);
		}
		if (debug) {
			System.out.println("Right merges: " + rightMerges);
		}

		// Special case when merging a deletion from the right
		if (rightMerges.getFirst2() > rightMerges.getLast2()) {
			leftMerges.setFirst2(rightMerges.getFirst2());
			leftMerges.setLast2(rightMerges.getLast2());
		}

		// Special case when merging a deletion from the left
		if (leftMerges.getFirst0() > leftMerges.getLast0()) {
			rightMerges.setFirst0(leftMerges.getFirst0());
			rightMerges.setLast0(leftMerges.getLast0());
		}

		Vector<MergeChange> both = new Vector<MergeChange>();
		both.add(leftMerges);
		both.add(rightMerges);
		MergeChange newChange = MergeChange.makeMergeChange(both, defaultAction);

		originalToRight += rightMerges.getLast1() - rightMerges.getFirst1() - (rightMerges.getLast2() - rightMerges.getFirst2());// +leftMerges.delta;
		leftToOriginal += leftMerges.getLast1() - leftMerges.getFirst1() - (leftMerges.getLast0() - leftMerges.getFirst0());// +rightMerges.delta;

		newChange.setMergeChangeSource(MergeChangeSource.Conflict);
		newChange.setMergeChangeType(MergeChangeType.Modification);
		addChange(newChange);

		newChange.setDebug("[" + mergeDebug + "], leftToOriginal=" + leftToOriginal + " originalToRight=" + originalToRight);

		return newChange;

	}

	protected void notifyMergeChange(MergeChange change) {
		isResolvedNeedsRecomputing = true;
		updateMergedSource();
		// System.out.println("On a choisi une nouvelle action pour le merge "+hashCode());
		setChanged();
		notifyObservers(change);
	}

	private boolean mergeNeedsRecomputing = true;
	private DiffSource _merge;

	@Override
	public synchronized DiffSource getMergedSource() {
		if (mergeNeedsRecomputing) {
			updateMergedSource();
		}
		// System.out.println("_merge.getSourceString() pour "+hashCode()+" :\n"+_merge.getSourceString());
		return _merge;
	}

	@Override
	public String getMergedText() {
		return getMergedSource().getText();
	}

	private synchronized void updateMergedSource() {
		String mergeAsString = computeMerge();
		// System.out.println("mergeAsString="+mergeAsString);
		if (_merge == null) {
			_merge = new DiffSource(mergeAsString, getDelimitingMethod());
		} else {
			_merge.updateWith(mergeAsString);
		}
		mergeNeedsRecomputing = false;
	}

	private String computeMerge() {
		if (debug) {
			System.out.println("computeMerge() in Merge");
		}
		StringBuffer sb = new StringBuffer();
		int currentLine = 0;
		int addedLines = 0;
		for (MergeChange change : getChanges()) {
			appendLines(sb, currentLine, change.getFirst1());
			addedLines += change.getFirst1() - currentLine;
			MergeChangeResult changeResult = change.getMergeChangeResult();
			sb.append(changeResult.merge);
			// System.out.println("change.getMergeChangeResult():================\n"+"Tokens: "+changeResult.tokensNb+"\n"+changeResult.merge);
			/*String[] changeResult = change.getMergeChangeResult();
			appendLines (sb,changeResult);*/
			change.setFirstMergeIndex(addedLines);
			change.setLastMergeIndex(addedLines + changeResult.tokensNb - 1);
			addedLines += changeResult.tokensNb;
			currentLine = change.getLast1() + 1;
		}
		appendLines(sb, currentLine, getOriginalSource().getTextTokens().length);
		addedLines += getOriginalSource().getTextTokens().length - currentLine;
		return sb.toString();
	}

	private void appendLines(StringBuffer sb, int begin, int end) {
		for (int i = begin; i < end; i++) {
			sb.append(getOriginalSource().tokenAt(i).getFullString());
		}
	}

	// Overriden in DetailedMerge
	public DelimitingMethod getDelimitingMethod() {
		return DelimitingMethod.LINES;
	}

	public MergedDocumentType getDocumentType() {
		return _docType;
	}

	@Override
	public Vector<MergeChange> filteredChangeList(List<ChangeCategory> selectedCategories) {
		Vector<MergeChange> reply = new Vector<MergeChange>();
		for (MergeChange item : changes) {
			if (selectedCategories.contains(item.category())) {
				reply.add(item);
			}
		}
		return reply;
	}

	public static void main(String[] args) {
		DiffSource orig = new DiffSource("{\n" + "        \"WebObjects Release\" = \"WebObjects 5.0\";\n"
				+ "        encoding = NSUTF8StringEncoding;\n" + "        variables = {};\n" + "}", DelimitingMethod.PLIST);
		DiffSource left = new DiffSource("{\n" + "        \"WebObjects Release\" = \"WebObjects 5.0\";\n"
				+ "        encoding = NSUTF8StringEncoding;\n" + "        variables = {};\n" + "}", DelimitingMethod.PLIST);
		DiffSource right = new DiffSource("{\n" + "        \"WebObjects Release\" = \"WebObjects 5.0\";\n"
				+ "        encoding = NSUTF8StringEncoding;\n" + "        variables = {};\n" + "}", DelimitingMethod.PLIST);
		Merge merge = new Merge(orig, left, right, DefaultMergedDocumentType.PLIST);
	}
}
