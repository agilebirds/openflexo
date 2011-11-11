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
package org.openflexo.foundation.dm;

import java.util.Iterator;
import java.util.Vector;

import org.openflexo.foundation.dm.eo.model.EOAttribute;
import org.openflexo.foundation.dm.eo.model.EOEntity;
import org.openflexo.foundation.dm.eo.model.EORelationship;

/**
 * @author bmangez
 * 
 *         TODO To change the template for this generated type comment go to Window - Preferences - Java - Code Style - Code Templates
 */
public class EOEntityDescriptor {
	public EOEntityDescriptor(EOEntity _entity) {
		super();
		entity = _entity;

	}

	public EOAttribute attributeNamed(String name) {
		return entity.attributeNamed(name);
	}

	public EORelationship relationshipNamed(String name) {
		return entity.relationshipNamed(name);
	}

	@Override
	public String toString() {
		return entity.getName();
	}

	public Vector<String> attributesList() {
		if (_attributesList == null) {
			_attributesList = new Vector<String>();
			Iterator<EOAttribute> i = entity.getAttributes().iterator();
			while (i.hasNext()) {
				_attributesList.add((i.next()).getName());
			}
		}
		return _attributesList;
	}

	private Vector<String> _attributesList;

	public Vector<String> relationshipsList() {
		if (_relationshipsList == null) {
			_relationshipsList = new Vector<String>();
			Iterator<EORelationship> i = entity.getRelationships().iterator();
			while (i.hasNext()) {
				_relationshipsList.add((i.next()).getName());
			}
		}
		return _relationshipsList;
	}

	private Vector<String> _relationshipsList;

	public EOEntity entity;
}
