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

import org.openflexo.localization.FlexoLocalization;

public class DefaultAutomaticMergeResolvingModel extends AutomaticMergeResolvingModel {
	public DefaultAutomaticMergeResolvingModel() {
		super();
		addToPrimaryRules(SAME_CONCURRENT_CHANGES);
		addToDetailedRules(SAME_CONCURRENT_CHANGES);
	}

	public static final AutomaticMergeResolvingRule SAME_CONCURRENT_CHANGES = new AutomaticMergeResolvingRule() {
		@Override
		public String getMergedResult(MergeChange change) {
			return change.getLeftText();
		}

		@Override
		public boolean isApplicable(MergeChange change) {
			// System.out.println("change.getLeftText()="+change.getLeftText());
			// System.out.println("change.getRightText()="+change.getRightText());
			// System.out.println("return="+change.getLeftText().equals(change.getRightText()));
			return change.getLeftText().equals(change.getRightText());
		}

		@Override
		public String getDescription() {
			return "concurrent_changes_for_same_text";
		}
	};

	@Override
	protected String localizedForKey(String key) {
		return FlexoLocalization.localizedForKey(key);
	}

}
