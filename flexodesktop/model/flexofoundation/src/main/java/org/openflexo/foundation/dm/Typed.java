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

import org.openflexo.foundation.DataFlexoObserver;
import org.openflexo.foundation.DataModification;
import org.openflexo.foundation.FlexoObservable;
import org.openflexo.foundation.dm.dm.DMDataModification;

/**
 * Implemented by all classes defining a type as an DMEntity
 * 
 * @author sguerin
 * 
 */
public interface Typed extends DataFlexoObserver, DMTypeOwner
{

    public DMType getType();

    public void setType(DMType type);
    
    @Override
	public void update(FlexoObservable observable, DataModification dataModification);
    
	public static abstract class TypeChangeNotification extends DMDataModification
	{
		private DMType _type;
		
		protected TypeChangeNotification(DMType type)
		{
			super("type",type,type);
			_type = type;
		}

		public DMType getType() {
			return _type;
		}
	}

	public static class TypeResolved extends TypeChangeNotification
	{		
		protected TypeResolved(DMType type)
		{
			super(type);
		}
	}

 }
