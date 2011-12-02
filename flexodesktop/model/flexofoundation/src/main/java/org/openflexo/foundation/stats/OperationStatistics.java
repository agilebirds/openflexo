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
package org.openflexo.foundation.stats;

import java.util.Vector;

import org.openflexo.foundation.wkf.node.ActionNode;
import org.openflexo.foundation.wkf.node.OperationNode;

/**
 * @author gpolet
 * 
 */
public class OperationStatistics extends FlexoStatistics<OperationNode> {

	private int actionCount = -1;

	private int realActionCount = -1;

	/**
	 * @param object
	 */
	public OperationStatistics(OperationNode object) {
		super(object);
		refresh();
	}

	public int getActionCount() {
		return actionCount;
	}

	private void setActionCount(int actionCount) {
		int old = this.actionCount;
		this.actionCount = actionCount;
		if (old != actionCount) {
			setChanged();
			notifyObservers(new StatModification("actionCount", old, actionCount));
		}
	}

	public int getRealActionCount() {
		return realActionCount;
	}

	private void setRealActionCount(int realActionCount) {
		int old = this.realActionCount;
		this.realActionCount = realActionCount;
		if (old != realActionCount) {
			setChanged();
			notifyObservers(new StatModification("realActionCount", old, realActionCount));
		}
	}

	/**
	 * Overrides refresh
	 * 
	 * @see org.openflexo.foundation.stats.FlexoStatistics#refresh()
	 */
	@Override
	public void refresh() {
		Vector<ActionNode> actions = getObject().getAllEmbeddedActionNodes();
		setActionCount(actions.size());
		int aCount = 0;
		for (ActionNode act : actions) {
			if (!act.isBeginOrEndNode()) {
				aCount += 1;
			}
		}
		setRealActionCount(aCount);
	}

}
