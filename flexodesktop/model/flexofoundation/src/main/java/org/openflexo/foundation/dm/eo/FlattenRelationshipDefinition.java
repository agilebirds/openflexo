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
package org.openflexo.foundation.dm.eo;

import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openflexo.foundation.bindings.BindingDefinition;
import org.openflexo.foundation.bindings.BindingValue;
import org.openflexo.foundation.bindings.BindingDefinition.BindingDefinitionType;
import org.openflexo.foundation.dm.DMType;

public class FlattenRelationshipDefinition extends BindingValue {

	private static final Logger logger = Logger.getLogger(FlattenRelationshipDefinition.class.getPackage().getName());

	private DMEOEntity _sourceEntity;

	public FlattenRelationshipDefinition(DMEOEntity sourceEntity, DMEORelationship owner) {
		super(new BindingDefinition("flattenRelationshipDefinition", DMType.makeObjectDMType(sourceEntity.getProject()), sourceEntity,
				BindingDefinitionType.GET, true), owner);
		_sourceEntity = sourceEntity;
	}

	public FlattenRelationshipDefinition(DMEORelationship owner, String definition) {
		super(new BindingDefinition("flattenRelationshipDefinition", DMType.makeObjectDMType(owner.getProject()), owner.getEntity(),
				BindingDefinitionType.GET, true), owner);
		_sourceEntity = owner.getEntity();
		decodeFromStringRepresentation(definition);
	}

	public boolean isDefinitionValid() {
		return isBindingValid();
	}

	@Override
	public boolean isBindingValid() {
		return _bindingPath.size() > 1;
	}

	@Override
	protected boolean isBindingValidWithoutBindingDefinition() {
		return isBindingValid();
	}

	@Override
	public String getStringRepresentation() {
		boolean isFirst = true;
		StringBuilder sb = new StringBuilder("");
		for (BindingPathElement element : _bindingPath) {
			if (element != null)
				sb.append((isFirst ? "" : ".") + element.getSerializationRepresentation());
			else
				sb.append(".null");
			isFirst = false;
		}
		return sb.toString();
	}

	private void decodeFromStringRepresentation(String definition) {
		if (logger.isLoggable(Level.FINE))
			logger.fine("Decoding as " + definition);
		_bindingPath.clear();
		StringTokenizer st = new StringTokenizer(definition, ".");
		DMEOEntity current = _sourceEntity;
		while (st.hasMoreTokens()) {
			String next = st.nextToken();
			if (current == null) {
				logger.warning("Could not find relationship '" + next + "' because entity has been deleted");
				return;
			}
			DMEORelationship relationship = current.getRelationshipNamed(next);
			if (relationship == null) {
				logger.warning("Could not find relationship '" + next + "' for entity " + current);
				return;
			} else {
				setBindingPathElementAtIndex(relationship, _bindingPath.size());
				current = relationship.getDestinationEntity();
			}
		}
	}

	@Override
	public DMEORelationship getBindingPathElementAtIndex(int i) {
		return (DMEORelationship) super.getBindingPathElementAtIndex(i);
	}

	@Override
	public DMEORelationship getBindingPathLastElement() {
		return (DMEORelationship) super.getBindingPathLastElement();
	}

	@Override
	public FlattenRelationshipDefinition clone() {
		FlattenRelationshipDefinition clone = new FlattenRelationshipDefinition(_sourceEntity, (DMEORelationship) getOwner());
		clone.decodeFromStringRepresentation(getStringRepresentation());
		return clone;
	}

}
