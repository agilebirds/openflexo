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

import org.openflexo.foundation.dm.eo.DMEOEntity;
import org.openflexo.foundation.dm.eo.DMEOModel;

/**
 * @author gpolet
 * 
 */
public class DMEOModelStatistics extends FlexoStatistics<DMEOModel> {

	private int eoAttributeCount = -1;

	private int eoRelationshipCount = -1;

	private int eoEntityCount = -1;

	/**
	 * @param object
	 */
	public DMEOModelStatistics(DMEOModel object) {
		super(object);
		refresh();
	}

	public int getEoAttributeCount() {
		return eoAttributeCount;
	}

	private void setEoAttributeCount(int attributeCount) {
		int old = this.eoAttributeCount;
		this.eoAttributeCount = attributeCount;
		if (old != attributeCount) {
			setChanged();
			notifyObservers(new StatModification("eoAttributeCount", old, attributeCount));
		}
	}

	public int getEoRelationshipCount() {
		return eoRelationshipCount;
	}

	private void setEoRelationshipCount(int relationshipCount) {
		int old = this.eoRelationshipCount;
		this.eoRelationshipCount = relationshipCount;
		if (old != relationshipCount) {
			setChanged();
			notifyObservers(new StatModification("eoRelationshipCount", old, relationshipCount));
		}
	}

	public int getEoEntityCount() {
		return eoEntityCount;
	}

	private void setEoEntityCount(int entityCount) {
		int old = this.eoEntityCount;
		this.eoEntityCount = entityCount;
		if (old != entityCount) {
			setChanged();
			notifyObservers(new StatModification("eoEntityCount", old, entityCount));
		}
	}

	/**
	 * Overrides refresh
	 * 
	 * @see org.openflexo.foundation.stats.FlexoStatistics#refresh()
	 */
	@Override
	public void refresh() {
		int aCount = 0;
		int rCount = 0;
		for (DMEOEntity e : getObject().getEntities()) {
			e.getStatistics().refresh();
			aCount += e.getStatistics().getEoAttributeCount();
			rCount += e.getStatistics().getEoRelationshipCount();
		}
		setEoEntityCount(getObject().getEntities().size());
		setEoAttributeCount(aCount);
		setEoRelationshipCount(rCount);
	}

}
