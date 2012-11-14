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

import java.util.Vector;

import org.openflexo.diff.ComputeDiff.AdditionChange;
import org.openflexo.diff.ComputeDiff.DiffChange;
import org.openflexo.diff.ComputeDiff.ModificationChange;
import org.openflexo.diff.ComputeDiff.RemovalChange;
import org.openflexo.diff.DelimitingMethod;
import org.openflexo.diff.DiffSource;

public class MergeChange {
	public enum MergeChangeSource {
		Left, Right, Conflict
	}

	public enum MergeChangeType {
		Addition, Removal, Modification
	}

	public enum MergeChangeAction {
		AutomaticMergeResolving, KeepChange, IgnoreChange, ChooseLeft, ChooseRight, ChooseBothLeftFirst, ChooseBothRightFirst, ChooseNone, CustomEditing, Undecided
	}

	public enum ChangeCategory {
		LEFT_ADDITION, LEFT_MODIFICATION, LEFT_REMOVAL, SMART_CONFLICT_RESOLVED, SMART_CONFLICT_UNRESOLVED, CUSTOM_EDITING_RESOLVED, CUSTOM_EDITING_UNRESOLVED, ADD_CONFLICT_RESOLVED, ADD_CONFLICT_UNRESOLVED, CONFLICT_RESOLVED, CONFLICT_UNRESOLVED, DEL_CONFLICT_RESOLVED, DEL_CONFLICT_UNRESOLVED, RIGHT_ADDITION, RIGHT_MODIFICATION, RIGHT_REMOVAL
	}

	public static final ChangeCategory[] CONFLICT_RESOLVED_CATEGORIES = new ChangeCategory[] { ChangeCategory.SMART_CONFLICT_RESOLVED,
			ChangeCategory.CUSTOM_EDITING_RESOLVED, ChangeCategory.ADD_CONFLICT_RESOLVED, ChangeCategory.CONFLICT_RESOLVED,
			ChangeCategory.DEL_CONFLICT_RESOLVED };
	public static final ChangeCategory[] CONFLICT_UNRESOLVED_CATEGORIES = new ChangeCategory[] { ChangeCategory.SMART_CONFLICT_UNRESOLVED,
			ChangeCategory.CUSTOM_EDITING_UNRESOLVED, ChangeCategory.ADD_CONFLICT_UNRESOLVED, ChangeCategory.CONFLICT_UNRESOLVED,
			ChangeCategory.DEL_CONFLICT_UNRESOLVED };
	public static final ChangeCategory[] LEFT_CATEGORIES = new ChangeCategory[] { ChangeCategory.LEFT_ADDITION,
			ChangeCategory.LEFT_MODIFICATION, ChangeCategory.LEFT_REMOVAL };
	public static final ChangeCategory[] RIGHT_CATEGORIES = new ChangeCategory[] { ChangeCategory.RIGHT_ADDITION,
			ChangeCategory.RIGHT_MODIFICATION, ChangeCategory.RIGHT_REMOVAL };

	protected int first0, last0;
	protected int first1, last1;
	protected int first2, last2;
	protected int firstMergeIndex, lastMergeIndex;
	protected String addedString;
	protected String removedString;
	private MergeChangeType mergeChangeType;
	private MergeChangeSource mergeChangeSource;
	private MergeChangeAction mergeChangeAction;

	private MergeChangeResult customHandEdition = null;
	private MergeChangeResult automaticMergeResult = null;

	protected static MergeChange makeRightMergeChange(DiffChange aChange, int leftToOriginal) {
		MergeChange returned = new MergeChange();
		returned.first0 = aChange.getFirst0() - leftToOriginal;
		returned.last0 = aChange.getLast0() - leftToOriginal;
		returned.first1 = aChange.getFirst0();
		returned.last1 = aChange.getLast0();
		returned.first2 = aChange.getFirst1();
		returned.last2 = aChange.getLast1();
		if (aChange instanceof ModificationChange) {
			returned.mergeChangeType = MergeChangeType.Modification;
		}
		if (aChange instanceof AdditionChange) {
			returned.mergeChangeType = MergeChangeType.Addition;
		}
		if (aChange instanceof RemovalChange) {
			returned.mergeChangeType = MergeChangeType.Removal;
		}
		returned.mergeChangeSource = MergeChangeSource.Right;
		returned.mergeChangeAction = MergeChangeAction.KeepChange;
		return returned;
	}

	protected static MergeChange makeLeftMergeChange(DiffChange aChange, int originalToRight) {
		MergeChange returned = new MergeChange();
		returned.first0 = aChange.getFirst0();
		returned.last0 = aChange.getLast0();
		returned.first1 = aChange.getFirst1();
		returned.last1 = aChange.getLast1();
		returned.first2 = aChange.getFirst1() - originalToRight;
		returned.last2 = aChange.getLast1() - originalToRight;
		if (aChange instanceof ModificationChange) {
			returned.mergeChangeType = MergeChangeType.Modification;
		}
		if (aChange instanceof AdditionChange) {
			returned.mergeChangeType = MergeChangeType.Removal;
		}
		if (aChange instanceof RemovalChange) {
			returned.mergeChangeType = MergeChangeType.Addition;
		}
		returned.mergeChangeSource = MergeChangeSource.Left;
		returned.mergeChangeAction = MergeChangeAction.KeepChange;
		return returned;
	}

	protected static MergeChange makeMergeChange(Vector<MergeChange> changes, MergeChangeAction defaultAction) {
		MergeChange returned = new MergeChange();
		for (MergeChange change : changes) {
			if (returned.getFirst0() == -1 || returned.getFirst0() >= change.getFirst0() && change.getFirst0() <= change.getLast0()) {
				returned.setFirst0(change.getFirst0());
			}
			if (returned.getFirst1() == -1 || returned.getFirst1() >= change.getFirst1() && change.getFirst1() <= change.getLast1()) {
				returned.setFirst1(change.getFirst1());
			}
			if (returned.getFirst2() == -1 || returned.getFirst2() >= change.getFirst2() && change.getFirst2() <= change.getLast2()) {
				returned.setFirst2(change.getFirst2());
			}
			if (returned.getLast0() == -1 || returned.getLast0() <= change.getLast0() && change.getFirst0() <= change.getLast0()) {
				returned.setLast0(change.getLast0());
			}
			if (returned.getLast1() == -1 || returned.getLast1() <= change.getLast1() && change.getFirst1() <= change.getLast1()) {
				returned.setLast1(change.getLast1());
			}
			if (returned.getLast2() == -1 || returned.getLast2() <= change.getLast2() && change.getFirst2() <= change.getLast2()) {
				returned.setLast2(change.getLast2());
			}
		}
		returned.mergeChangeSource = MergeChangeSource.Conflict;
		returned.mergeChangeAction = defaultAction;
		return returned;
	}

	protected static MergeChange makeLeftMergeChange(Vector<DiffChange> changes, int originalToRight, Vector<DiffChange> oppositeChanges,
			int leftToOriginal, MergeChangeAction defaultAction) {
		Vector<MergeChange> changesToMerge = new Vector<MergeChange>();
		for (DiffChange diffChange : changes) {
			MergeChange change = makeLeftMergeChange(diffChange, originalToRight);
			changesToMerge.add(change);
		}
		MergeChange returned = makeMergeChange(changesToMerge, defaultAction);
		changesToMerge = new Vector<MergeChange>();
		for (DiffChange diffChange : oppositeChanges) {
			MergeChange change = makeRightMergeChange(diffChange, leftToOriginal);
			changesToMerge.add(change);
		}
		MergeChange oppositeMerge = makeMergeChange(changesToMerge, defaultAction);
		if (oppositeMerge.getFirst1() >= returned.getFirst1() && oppositeMerge.getLast1() <= returned.getLast1()) {
			int d = oppositeMerge.getLast2() - oppositeMerge.getFirst2() - oppositeMerge.getLast1() + oppositeMerge.getFirst1();
			if (Merge.debug) {
				System.out.println("Including case from left d=" + d);
			}
			returned.setLast2(returned.getLast2() + d);
			returned.delta = d;
		}
		return returned;
	}

	protected int delta = 0;

	protected static MergeChange makeRightMergeChange(Vector<DiffChange> changes, int leftToOriginal, Vector<DiffChange> oppositeChanges,
			int originalToRight, MergeChangeAction defaultAction) {
		Vector<MergeChange> changesToMerge = new Vector<MergeChange>();
		for (DiffChange diffChange : changes) {
			MergeChange change = makeRightMergeChange(diffChange, leftToOriginal);
			changesToMerge.add(change);
		}
		MergeChange returned = makeMergeChange(changesToMerge, defaultAction);
		changesToMerge = new Vector<MergeChange>();
		for (DiffChange diffChange : oppositeChanges) {
			MergeChange change = makeLeftMergeChange(diffChange, originalToRight);
			changesToMerge.add(change);
		}
		MergeChange oppositeMerge = makeMergeChange(changesToMerge, defaultAction);
		if (oppositeMerge.getFirst1() >= returned.getFirst1() && oppositeMerge.getLast1() <= returned.getLast1()) {
			int d = oppositeMerge.getLast0() - oppositeMerge.getFirst0() - oppositeMerge.getLast1() + oppositeMerge.getFirst1();
			if (Merge.debug) {
				System.out.println("Including case from right d=" + d);
			}
			returned.setLast0(returned.getLast0() + d);
			returned.delta = d;
		}
		return returned;
	}

	protected MergeChange() {
		first0 = -1;
		last0 = -1;
		first1 = -1;
		last1 = -1;
		first2 = -1;
		last2 = -1;
	}

	public int getFirst0() {
		return first0;
	}

	public int getFirst2() {
		return first2;
	}

	public int getLast0() {
		return last0;
	}

	public int getLast2() {
		return last2;
	}

	private String _debug;

	public String toNiceString() {
		return getMergeChangeTypeAsString() + " " + getMergeChangeSourceAsString() + " " + first0 + "," + last0 + "/" + first1 + ","
				+ last1 + "/" + first2 + "," + last2;
	}

	public String toDebugString() {
		return toNiceString() + " " + getDebug();
	}

	@Override
	public String toString() {
		if (Merge.debug) {
			return toDebugString();
		}
		return toNiceString();
	}

	private String getMergeChangeTypeAsString() {
		if (getMergeChangeType() == MergeChangeType.Addition) {
			return "ADDITION";
		} else if (getMergeChangeType() == MergeChangeType.Removal) {
			return "REMOVAL";
		} else if (getMergeChangeType() == MergeChangeType.Modification) {
			return "MODIFICATION";
		}
		return "???";
	}

	private String getMergeChangeSourceAsString() {
		if (getMergeChangeSource() == MergeChangeSource.Left) {
			return "LEFT";
		} else if (getMergeChangeSource() == MergeChangeSource.Right) {
			return "RIGHT";
		} else if (getMergeChangeSource() == MergeChangeSource.Conflict) {
			return "CONFLICT";
		}
		return "???";
	}

	public MergeChangeSource getMergeChangeSource() {
		return mergeChangeSource;
	}

	public MergeChangeType getMergeChangeType() {
		return mergeChangeType;
	}

	public void setMergeChangeSource(MergeChangeSource mergeChangeSource) {
		this.mergeChangeSource = mergeChangeSource;
	}

	public void setMergeChangeType(MergeChangeType mergeChangeType) {
		this.mergeChangeType = mergeChangeType;
	}

	public void setFirst0(int first0) {
		this.first0 = first0;
	}

	public void setFirst2(int first1) {
		this.first2 = first1;
	}

	public void setLast0(int last0) {
		this.last0 = last0;
	}

	public void setLast2(int last1) {
		this.last2 = last1;
	}

	public int getFirst1() {
		return first1;
	}

	public void setFirst1(int first1) {
		this.first1 = first1;
	}

	public int getLast1() {
		return last1;
	}

	public void setLast1(int last1) {
		this.last1 = last1;
	}

	public String getDebug() {
		return _debug;
	}

	public void setDebug(String debug) {
		_debug = debug;
	}

	private Merge _merge;

	public String getOriginalText() {
		StringBuffer sb = new StringBuffer();
		for (String line : getOriginalTextLines()) {
			sb.append(line + "\n");
		}
		return sb.toString();
	}

	private String _tokenizedLeftText = null;

	public String getTokenizedLeftText() {
		if (_tokenizedLeftText == null) {
			StringBuffer sb = new StringBuffer();
			for (String line : getLeftTextLines()) {
				sb.append(line + "\n");
			}
			_tokenizedLeftText = sb.toString();
		}
		return _tokenizedLeftText;
	}

	private String _tokenizedRightText = null;

	public String getTokenizedRightText() {
		if (_tokenizedRightText == null) {
			StringBuffer sb = new StringBuffer();
			for (String line : getRightTextLines()) {
				sb.append(line + "\n");
			}
			_tokenizedRightText = sb.toString();
		}
		return _tokenizedRightText;
	}

	public String[] getOriginalTextLines() {
		String[] returned = new String[getLast1() - getFirst1() + 1];
		;
		for (int i = getFirst1(); i <= getLast1(); i++) {
			returned[i - getFirst1()] = _merge.getOriginalSource().tokenValueAt(i);
		}
		return returned;
	}

	public String[] getLeftTextLines() {
		String[] returned = new String[getLast0() - getFirst0() + 1];
		;
		for (int i = getFirst0(); i <= getLast0(); i++) {
			returned[i - getFirst0()] = _merge.getLeftSource().tokenValueAt(i);
		}
		return returned;
	}

	public String[] getRightTextLines() {
		String[] returned = new String[getLast2() - getFirst2() + 1];
		;
		for (int i = getFirst2(); i <= getLast2(); i++) {
			returned[i - getFirst2()] = _merge.getRightSource().tokenValueAt(i);
		}
		return returned;
	}

	public class MergeChangeResult {
		public String merge;
		public int tokensNb;
		private String[] _significativeTokens;

		public MergeChangeResult(String merge, int tokensNb) {
			this.merge = merge;
			this.tokensNb = tokensNb;
		}

		public String[] getSignificativeTokens() {
			if (_significativeTokens == null) {
				_significativeTokens = new DiffSource(merge, getDelimitingMethod()).getSignificativeTokens();
			}
			return _significativeTokens;
		}
	}

	private String _leftText = null;

	public String getLeftText() {
		if (_leftText == null) {
			_leftText = _merge.getLeftSource().extractText(getFirst0(), getLast0());
		}
		return _leftText;
	}

	private String _rightText = null;

	public String getRightText() {
		if (_rightText == null) {
			_rightText = _merge.getRightSource().extractText(getFirst2(), getLast2());
		}
		return _rightText;
	}

	public MergeChangeResult getMergeChangeResult() {
		if (mergeChangeSource == MergeChangeSource.Left) {
			if (mergeChangeAction == MergeChangeAction.KeepChange) {
				return new MergeChangeResult(getLeftText(), getLast0() - getFirst0() + 1);
			} else if (mergeChangeAction == MergeChangeAction.IgnoreChange) {
				return new MergeChangeResult(getRightText(), getLast2() - getFirst2() + 1);
			} else {
				System.err.println("Inconsistent data in Merge " + this + " action:" + mergeChangeAction);
				return null;
			}
		} else if (mergeChangeSource == MergeChangeSource.Right) {
			if (mergeChangeAction == MergeChangeAction.KeepChange) {
				return new MergeChangeResult(getRightText(), getLast2() - getFirst2() + 1);
			} else if (mergeChangeAction == MergeChangeAction.IgnoreChange) {
				return new MergeChangeResult(getLeftText(), getLast0() - getFirst0() + 1);
			} else {
				System.err.println("Inconsistent data in Merge " + this + " action:" + mergeChangeAction);
				return null;
			}
		} else if (mergeChangeSource == MergeChangeSource.Conflict) {
			if (mergeChangeAction == MergeChangeAction.ChooseLeft) {
				return new MergeChangeResult(getLeftText(), getLast0() - getFirst0() + 1);
			} else if (mergeChangeAction == MergeChangeAction.ChooseRight) {
				return new MergeChangeResult(getRightText(), getLast2() - getFirst2() + 1);
			} else if (mergeChangeAction == MergeChangeAction.ChooseNone) {
				return new MergeChangeResult("", 0);
			} else if (mergeChangeAction == MergeChangeAction.ChooseBothLeftFirst) {
				return new MergeChangeResult(getLeftText() + getRightText(), getLast0() - getFirst0() + 1 + getLast2() - getFirst2() + 1);
			} else if (mergeChangeAction == MergeChangeAction.ChooseBothRightFirst) {
				return new MergeChangeResult(getRightText() + getLeftText(), getLast0() - getFirst0() + 1 + getLast2() - getFirst2() + 1);
			} else if (mergeChangeAction == MergeChangeAction.CustomEditing) {
				if (customHandEdition != null) {
					return customHandEdition;
				}
				return new MergeChangeResult("", 0);
			} else if (mergeChangeAction == MergeChangeAction.AutomaticMergeResolving) {
				if (getAutomaticResolvedMerge() != null) {
					return getAutomaticResolvedMerge();
				}
				// If automerge failed, take left
				return new MergeChangeResult(getLeftText(), getLast0() - getFirst0() + 1);
				// If automerge failed, choose none
				// return new MergeChangeResult("",0);
				// If automerge failed, take both, left first
				/*return new MergeChangeResult(
						getLeftText()
						+getRightText(),
						getLast0()-getFirst0()+1+getLast2()-getFirst2()+1);	*/
			} else if (mergeChangeAction == MergeChangeAction.Undecided) {
				return new MergeChangeResult("", 0);
			} else {
				System.err.println("Inconsistent data in Merge " + this + " action:" + mergeChangeAction);
				return null;
			}
		}
		return new MergeChangeResult("", 0);
	}

	public Merge getMerge() {
		return _merge;
	}

	public void setMerge(Merge merge) {
		_merge = merge;
	}

	public int getFirstMergeIndex() {
		return firstMergeIndex;
	}

	public void setFirstMergeIndex(int firstMerge) {
		this.firstMergeIndex = firstMerge;
	}

	public int getLastMergeIndex() {
		return lastMergeIndex;
	}

	public void setLastMergeIndex(int lastMerge) {
		this.lastMergeIndex = lastMerge;
	}

	public MergeChangeAction getMergeChangeAction() {
		return mergeChangeAction;
	}

	public void setMergeChangeAction(MergeChangeAction mergeChangeAction) {
		this.mergeChangeAction = mergeChangeAction;
		triedToAutomaticallyResolve = false;
		getMerge().notifyMergeChange(this);
	}

	public MergeChangeResult getCustomHandEdition() {
		return customHandEdition;
	}

	public void setCustomHandEdition(String customHandEdition) {
		DiffSource diffSource = new DiffSource(customHandEdition, getDelimitingMethod());
		this.customHandEdition = new MergeChangeResult(customHandEdition, diffSource.tokensCount());
	}

	public DelimitingMethod getDelimitingMethod() {
		return getMerge().getDelimitingMethod();
	}

	private boolean triedToAutomaticallyResolve = false;

	private void tryToAutomaticallyResolve() {
		if (!triedToAutomaticallyResolve && getMerge().getDocumentType().getAutomaticMergeResolvingModel() != null) {
			getMerge().getDocumentType().getAutomaticMergeResolvingModel().resolve(this);
			triedToAutomaticallyResolve = true;
			/*String mergeResult = "Yes";
			//= getMerge().getDocumentType().getAutomaticMergeResolvingModel().resolve(this);
			if (mergeResult != null) {
				DiffSource diffSource = new DiffSource(mergeResult,getDelimitingMethod());
				automaticMergeResult = new MergeChangeResult(mergeResult,diffSource.tokensCount());
			}
			else {
				automaticMergeResult = null;
			}*/
		}
	}

	public boolean isResolved() {
		if (mergeChangeSource == MergeChangeSource.Left || mergeChangeSource == MergeChangeSource.Right) {
			return true;
		}
		// else conflict
		if (mergeChangeAction == MergeChangeAction.Undecided) {
			return false;
		}
		if (mergeChangeAction == MergeChangeAction.AutomaticMergeResolving) {
			tryToAutomaticallyResolve();
			return automaticMergeResult != null;
		}
		if (mergeChangeAction == MergeChangeAction.CustomEditing) {
			return customHandEdition != null;
		}
		return true;
	}

	public MergeChangeResult getAutomaticResolvedMerge() {
		tryToAutomaticallyResolve();
		return automaticMergeResult;
	}

	protected void setAutomaticResolvedMerge(String mergeResult) {
		DiffSource diffSource = new DiffSource(mergeResult, getDelimitingMethod());
		automaticMergeResult = new MergeChangeResult(mergeResult, diffSource.tokensCount());
	}

	private DetailedMerge _detailedMerge;

	public DetailedMerge getDetailedMerge() {
		if (_detailedMerge == null) {
			_detailedMerge = new DetailedMerge(this);
		}
		return _detailedMerge;
	}

	private String _automaticMergeReason = null;

	public String getAutomaticMergeReason() {
		return _automaticMergeReason;
	}

	protected void setAutomaticMergeReason(String automaticMergeReason) {
		_automaticMergeReason = automaticMergeReason;
	}

	public ChangeCategory category() {
		if (getMergeChangeSource() == MergeChange.MergeChangeSource.Left) {
			if (getMergeChangeType() == MergeChange.MergeChangeType.Addition) {
				return ChangeCategory.LEFT_ADDITION;
			} else if (getMergeChangeType() == MergeChange.MergeChangeType.Modification) {
				return ChangeCategory.LEFT_MODIFICATION;
			} else if (getMergeChangeType() == MergeChange.MergeChangeType.Removal) {
				return ChangeCategory.LEFT_REMOVAL;
			}
		} else if (getMergeChangeSource() == MergeChange.MergeChangeSource.Conflict) {
			if (getMergeChangeAction() == MergeChangeAction.AutomaticMergeResolving) {
				if (isResolved()) {
					return ChangeCategory.SMART_CONFLICT_RESOLVED;
				} else {
					return ChangeCategory.SMART_CONFLICT_UNRESOLVED;
				}
			} else if (getMergeChangeAction() == MergeChangeAction.CustomEditing) {
				if (isResolved()) {
					return ChangeCategory.CUSTOM_EDITING_RESOLVED;
				} else {
					return ChangeCategory.CUSTOM_EDITING_UNRESOLVED;
				}
			}
			if (getMergeChangeType() == MergeChange.MergeChangeType.Addition) {
				if (isResolved()) {
					return ChangeCategory.ADD_CONFLICT_RESOLVED;
				} else {
					return ChangeCategory.ADD_CONFLICT_UNRESOLVED;
				}
			} else if (getMergeChangeType() == MergeChange.MergeChangeType.Modification) {
				if (isResolved()) {
					return ChangeCategory.CONFLICT_RESOLVED;
				} else {
					return ChangeCategory.CONFLICT_UNRESOLVED;
				}
			} else if (getMergeChangeType() == MergeChange.MergeChangeType.Removal) {
				if (isResolved()) {
					return ChangeCategory.DEL_CONFLICT_RESOLVED;
				} else {
					return ChangeCategory.DEL_CONFLICT_UNRESOLVED;
				}
			}
		} else if (getMergeChangeSource() == MergeChange.MergeChangeSource.Right) {
			if (getMergeChangeType() == MergeChange.MergeChangeType.Addition) {
				return ChangeCategory.RIGHT_ADDITION;
			} else if (getMergeChangeType() == MergeChange.MergeChangeType.Modification) {
				return ChangeCategory.RIGHT_MODIFICATION;
			} else if (getMergeChangeType() == MergeChange.MergeChangeType.Removal) {
				return ChangeCategory.RIGHT_REMOVAL;
			}
		}
		return null;
	}

}
