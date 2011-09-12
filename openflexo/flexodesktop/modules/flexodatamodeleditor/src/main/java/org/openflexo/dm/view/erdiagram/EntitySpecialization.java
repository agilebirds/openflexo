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
import org.openflexo.foundation.dm.DMType;

public class EntitySpecialization {

	private DMEntity specializedEntity;
	private DMEntity parentEntity;
	private DMType specialization;
	
	public EntitySpecialization(DMEntity specializedEntity, DMType specialization)
	{
		super();
		this.specializedEntity = specializedEntity;
		this.specialization = specialization;
		this.parentEntity = specialization.getBaseEntity();
	}

	public DMEntity getParentEntity() 
	{
		return parentEntity;
	}

	public DMEntity getSpecializedEntity() 
	{
		return specializedEntity;
	}

	public DMType getSpecialization()
	{
		return specialization;
	}
	
	public String getLabel()
	{
		String returned = null;
		if (getSpecialization().equals(getSpecializedEntity().getParentType())) returned = "extends";
		if (getSpecializedEntity().getImplementedTypes().contains(getSpecialization())) returned = "implements";
		if (getSpecialization().isGeneric()) returned += " "+getSpecialization().getSimplifiedStringRepresentation();
		return returned;
	}
}
