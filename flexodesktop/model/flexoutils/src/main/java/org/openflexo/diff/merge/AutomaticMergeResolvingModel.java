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

import org.openflexo.diff.merge.MergeChange.MergeChangeSource;

public class AutomaticMergeResolvingModel implements AutomaticMergeResolvingModelInterface {
	private Vector<AutomaticMergeResolvingRule> _primaryRules;
	private Vector<AutomaticMergeResolvingRule> _detailedRules;

	public AutomaticMergeResolvingModel() {
		super();
		_primaryRules = new Vector<AutomaticMergeResolvingRule>();
		_detailedRules = new Vector<AutomaticMergeResolvingRule>();
	}

	public void addToPrimaryRules(AutomaticMergeResolvingRule aRule) {
		_primaryRules.add(aRule);
	}

	public void addToDetailedRules(AutomaticMergeResolvingRule aRule) {
		_detailedRules.add(aRule);
	}

	@Override
	public boolean resolve(MergeChange change) {
		// System.out.println("Resolve "+change);
		if (change.getMergeChangeSource() == MergeChangeSource.Conflict) {
			if (change.getMerge() instanceof DetailedMerge) {
				// This is a change from a detailed merge,
				// review all detailed rules
				for (AutomaticMergeResolvingRule rule : _detailedRules) {
					if (rule.isApplicable(change)) {
						// This rule apply, ok return result
						// return rule.getMergedResult(change);
						change.setAutomaticResolvedMerge(rule.getMergedResult(change));
						change.setAutomaticMergeReason(localizedForKey(rule.getDescription()));
						return true;
					}
				}
				// Not resolvable
				return false;
				// return null;
			} else {
				// This is a change that might be tokenized
				// Before to analyse deeply, look if a primary
				// rule may resolve conflict
				for (AutomaticMergeResolvingRule rule : _primaryRules) {
					if (rule.isApplicable(change)) {
						// This rule apply, ok return result
						// return rule.getMergedResult(change);
						change.setAutomaticResolvedMerge(rule.getMergedResult(change));
						change.setAutomaticMergeReason(localizedForKey(rule.getDescription()));
						return true;
					}
				}
				DetailedMerge detailedMerge = change.getDetailedMerge();
				for (MergeChange c : detailedMerge.getChanges()) {
					if (!resolve(c)) {
						// At least one change was not resolvable, return null
						return false;
						// return null;
					}
				}
				// Arriving here means that all changes were resolved,
				// and thus that the supplied change is resolved
				change.setAutomaticResolvedMerge(detailedMerge.getMergedSource().getText());
				change.setAutomaticMergeReason(localizedForKey("all_changes_are_resolved_by_detailed_analysis"));
				return true;
			}
		} else {
			return true;
			// return change.getMergeChangeResult().merge;
		}
	}

	protected String localizedForKey(String key) {
		return key;
	}
}
