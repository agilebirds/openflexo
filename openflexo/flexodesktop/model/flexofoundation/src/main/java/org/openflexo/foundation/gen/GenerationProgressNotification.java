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
package org.openflexo.foundation.gen;

import org.openflexo.foundation.DataModification;

public class GenerationProgressNotification extends DataModification{

	private boolean _isFine=false;
	
	public GenerationProgressNotification(int modificationType, Object oldValue, Object newValue) {
		super(modificationType, oldValue, newValue);
	}

	public GenerationProgressNotification(String message) {
		super(123456, null, message);
	}
	
	public GenerationProgressNotification(String message,boolean isFine) {
		super(123456, null, message);
		_isFine = isFine;
	}
	
	
	public String getProgressMessage(){
		return (String)newValue();
	}
	
	public boolean getIsFineMessage(){
		return _isFine;
	}
}
