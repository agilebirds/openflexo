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
package org.openflexo.dm.view.erdiagram;

import org.openflexo.foundation.dm.DMEntity;
import org.openflexo.foundation.dm.DMProperty;
import org.openflexo.foundation.dm.eo.DMEORelationship;

public class RelationshipRepresentation {

	private DMProperty property;
	private DMEntity sourceEntity;
	private DMEntity destinationEntity;
	private DMProperty inverseProperty;

	public RelationshipRepresentation(DMProperty aProperty) {
		super();
		property = aProperty;
		sourceEntity = property.getEntity();
		destinationEntity = property.getType().getBaseEntity();
		if (property instanceof DMEORelationship) {
			inverseProperty = ((DMEORelationship) property).getInverse();
		}
	}

	public DMProperty getProperty() {
		return property;
	}

	public DMEntity getDestinationEntity() {
		return destinationEntity;
	}

	public DMProperty getInverseProperty() {
		return inverseProperty;
	}

	public DMEntity getSourceEntity() {
		return sourceEntity;
	}

	public boolean isInverseDeclaration(DMProperty aProperty) {
		return inverseProperty == aProperty;
	}
}
