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

import org.openflexo.diff.DelimitingMethod;
import org.openflexo.diff.DiffSource;

public class DetailedMerge extends Merge {

	private MergeChange _change;

	public DetailedMerge(MergeChange change) {
		super(new DiffSource(change.getOriginalText(), change.getMerge().getDocumentType().getDelimitingMethod()), new DiffSource(
				change.getTokenizedLeftText(), change.getMerge().getDocumentType().getDelimitingMethod()), new DiffSource(
				change.getTokenizedRightText(), change.getMerge().getDocumentType().getDelimitingMethod()), change.getMerge()
				.getDocumentType());
		_change = change;
		// computeChanges();
	}

	@Override
	public DelimitingMethod getDelimitingMethod() {
		return getDocumentType().getDelimitingMethod();
	}

	public Merge getParentMerge() {
		if (_change != null) {
			return _change.getMerge();
		}
		return null;
	}

	public MergeChange getChange() {
		return _change;
	}

	@Override
	public MergedDocumentType getDocumentType() {
		if (getParentMerge() != null) {
			return getParentMerge().getDocumentType();
		}
		return super.getDocumentType();
	}

}
