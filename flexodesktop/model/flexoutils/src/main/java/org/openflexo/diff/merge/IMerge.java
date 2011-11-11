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
import java.util.Observer;
import java.util.Vector;

import org.openflexo.diff.DiffSource;
import org.openflexo.diff.merge.MergeChange.ChangeCategory;

public interface IMerge {

	public boolean isResolved();

	public int getLeftChangeCount();

	public int getRightChangeCount();

	public int getConflictsChangeCount();

	public int getResolvedConflictsChangeCount();

	public String getMergedText();

	public void deleteObserver(Observer observer);

	public void addObserver(Observer observer);

	public DiffSource getLeftSource();

	public DiffSource getOriginalSource();

	public DiffSource getRightSource();

	public DiffSource getMergedSource();

	public Vector<MergeChange> getChanges();

	public MergeChange changeBefore(MergeChange change);

	public Vector<MergeChange> filteredChangeList(List<ChangeCategory> selectedCategories);

}