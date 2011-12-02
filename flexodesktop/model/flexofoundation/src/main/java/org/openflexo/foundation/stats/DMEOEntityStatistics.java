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

/**
 * @author gpolet
 * 
 */
public class DMEOEntityStatistics extends FlexoStatistics<DMEOEntity> {

	private int eoAttributeCount = -1;

	private int eoRelationshipCount = -1;

	/**
     * 
     */
	public DMEOEntityStatistics(DMEOEntity object) {
		super(object);
		refresh();
	}

	/**
	 * Overrides refresh
	 * 
	 * @see org.openflexo.foundation.stats.FlexoStatistics#refresh()
	 */
	@Override
	public void refresh() {
		setEoAttributeCount(getObject().getAttributes().size());
		setEoRelationshipCount(getObject().getRelationships().size());
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

}
