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
package org.openflexo.tm.hibernate.impl.enums;

/**
 * Enumeration of relationship propagations. This describes behavior between POJO entity and database records.
 * 
 * @author Emmanuel Koch
 */
public enum HibernateCascade {

	/** All entity modifications are applied at database level. */
	ALL,

	/** When some entities are removed from the POJO relationship, their database records will also be deleted. */
	DELETE,

	/** When the record of the entity is updated, the records of its relationship will also be updated. */
	UPDATE,

	/** If some new entities are added in the relationship, they also will be inserted in the database. */
	INSERT,

	/** If the entity is deleted, also delete all entities of this relationship (i.e. delete cascade at database level). */
	DELETE_ORPHAN;

}
