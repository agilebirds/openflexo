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
package org.openflexo.foundation.utils;

import java.util.Arrays;
import java.util.Comparator;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openflexo.logging.FlexoLogger;

/**
 * @author gpolet
 * 
 */
public class FlexoIndexManager {
	private static final Logger logger = FlexoLogger.getLogger(FlexoIndexManager.class.getPackage().getName());

	public static final IndexComparator INDEX_COMPARATOR = new IndexComparator();

	public static void switchIndexForKey(int oldVal, int newVal, Sortable sortable) {
		if (oldVal == newVal)// Does not move
			return;
		Object[] collection = sortable.getCollection();
		if (collection != null) {
			if (newVal > collection.length) {
				sortable.setIndexValue(collection.length);
				newVal = collection.length;
			}
			if (oldVal < 1) {
				reIndexObjectOfArray(sortable.getCollection());
				return;
			}
			if (newVal < 1)
				newVal = 1;
			if (oldVal > collection.length)
				oldVal = collection.length;
			if (oldVal > newVal) {// Goes up
				collection = sortArray(sortable.getCollection());
				for (int i = newVal - 1; i < oldVal; i++) {
					Sortable s = (Sortable) collection[i];
					if (s != sortable)
						s.setIndexValue(s.getIndexValue() + 1);
					else
						s.setIndexValue(newVal);
				}
			} else { // Goes down
				collection = sortArray(sortable.getCollection());
				for (int i = oldVal - 1; i < newVal; i++) {
					Sortable s = (Sortable) collection[i];
					if (s != sortable)
						s.setIndexValue(s.getIndexValue() - 1);
					else
						s.setIndexValue(newVal);
				}
			}
		}
	}

	/**
	 * Reindexes all objects of the array. It will first sort the array based on the current indexes then it will recompute all indexes so
	 * that they follow each other
	 * 
	 * @param array
	 *            - an array of Sortable
	 */
	public static <E extends Sortable> void reIndexObjectOfArray(E[] array) {
		if (array == null) {
			if (logger.isLoggable(Level.WARNING))
				logger.warning("Attempt to reindex a null array");
			return;
		}
		array = sortArray(array);
		for (int i = 0; i < array.length; i++) {
			Sortable sortable = array[i];
			sortable.setIndexValue(i + 1);
		}
	}

	public static <E extends Sortable> E[] sortArray(E[] array) {
		if (array == null)
			return null;
		if (array.length == 0)
			return array;
		Arrays.sort(array, INDEX_COMPARATOR);
		return array;
	}

	public static class IndexComparator implements Comparator<Sortable> {

		/**
		 * Overrides compare
		 * 
		 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
		 */
		@Override
		public int compare(Sortable o1, Sortable o2) {
			return o1.getIndexValue() - o2.getIndexValue();
		}

	}
}
