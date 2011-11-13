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

import org.openflexo.diff.ComputeDiff.DiffChange;
import org.openflexo.diff.DiffSource;

public abstract class AbstractAutomaticMergeResolvingRule implements AutomaticMergeResolvingRule {
	@Override
	public abstract boolean isApplicable(MergeChange change);

	@Override
	public abstract String getMergedResult(MergeChange change);

	public static String relativeLeftTokenAt(MergeChange change, int relativeIndex) throws IndexOutOfBoundsException {
		DiffSource leftSource = change.getMerge().getLeftSource();
		if (change.getFirst0() + relativeIndex < 0) {
			throw new IndexOutOfBoundsException();
		}
		if (change.getFirst0() + relativeIndex >= leftSource.tokensCount()) {
			throw new IndexOutOfBoundsException();
		}
		return leftSource.tokenAt(change.getFirst0() + relativeIndex).getToken();
	}

	public static String relativeRightTokenAt(MergeChange change, int relativeIndex) throws IndexOutOfBoundsException {
		DiffSource rightSource = change.getMerge().getRightSource();
		if (change.getFirst2() + relativeIndex < 0) {
			throw new IndexOutOfBoundsException();
		}
		if (change.getFirst2() + relativeIndex >= rightSource.tokensCount()) {
			throw new IndexOutOfBoundsException();
		}
		return rightSource.tokenAt(change.getFirst2() + relativeIndex).getToken();
	}

	public static String relativeOriginalTokenAt(MergeChange change, int relativeIndex) throws IndexOutOfBoundsException {
		DiffSource originalSource = change.getMerge().getOriginalSource();
		if (change.getFirst1() + relativeIndex < 0) {
			throw new IndexOutOfBoundsException();
		}
		if (change.getFirst1() + relativeIndex >= originalSource.tokensCount()) {
			throw new IndexOutOfBoundsException();
		}
		return originalSource.tokenAt(change.getFirst1() + relativeIndex).getToken();
	}

	public static String relativeLeftTokenAt(DiffChange change, DiffSource leftSource, int relativeIndex) throws IndexOutOfBoundsException {
		if (change.getFirst0() + relativeIndex < 0) {
			throw new IndexOutOfBoundsException();
		}
		if (change.getFirst0() + relativeIndex >= leftSource.tokensCount()) {
			throw new IndexOutOfBoundsException();
		}
		return leftSource.tokenAt(change.getFirst0() + relativeIndex).getToken();
	}

	public static String relativeRightTokenAt(DiffChange change, DiffSource rightSource, int relativeIndex)
			throws IndexOutOfBoundsException {
		if (change.getFirst1() + relativeIndex < 0) {
			throw new IndexOutOfBoundsException();
		}
		if (change.getFirst1() + relativeIndex >= rightSource.tokensCount()) {
			throw new IndexOutOfBoundsException();
		}
		return rightSource.tokenAt(change.getFirst1() + relativeIndex).getToken();
	}

}
