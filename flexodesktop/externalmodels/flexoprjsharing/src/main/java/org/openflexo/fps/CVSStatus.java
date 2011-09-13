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
package org.openflexo.fps;

import org.openflexo.localization.FlexoLocalization;

public enum CVSStatus 
{
	UpToDate,  // Remote status: up-to-date
	LocallyModified,  // Locally modified
	LocallyAdded,	// Unresolved conflicts (no entry for file)
	LocallyRemoved,	// Locally removed
	RemotelyModified,	// Remotely modified
	RemotelyAdded,	// Remotely added
	RemotelyRemoved,	// Remotely removed
	Conflicting,	// File had conflicts on merge
	ConflictingAdded,	// File had conflicts on merge, added both sides
	ConflictingRemoved,	// File had conflicts on merge, removed both sides
	MarkedAsMerged,	// File had conflicts on merge, but now conflicts are declared as solved
	//Erased,		// Needs Checkout
	//NoFile,		// Needs Checkout (no entry for file)
	//Unknown;	// Unresolved conflicts (no entry for file)
	CVSIgnored,  // Ignored file
	Removed, // Removed file
	Unknown; // ???
	
	public boolean isUpToDate()
	{
		return this == UpToDate;
	}

	public boolean isUnknown()
	{
		return this == Unknown;
	}

	public boolean isIgnored()
	{
		return this == CVSIgnored;
	}

	public boolean isLocallyModified()
	{
		return ((this == LocallyModified)
				|| (this == LocallyAdded)
				|| (this == LocallyRemoved)
				|| (this == MarkedAsMerged)
				|| isConflicting());
	}

	public boolean isRemotelyModified()
	{
		return ((this == RemotelyModified)
				|| (this == RemotelyAdded)
				|| (this == RemotelyRemoved)
				|| isConflicting());
	}

	public boolean isConflicting()
	{
		return ((this == Conflicting)
				|| (this == ConflictingAdded)
				|| (this == ConflictingRemoved));
	}
	
	public boolean isUnderCVS()
	{
		return (this != CVSIgnored
				&& this != Unknown
				&& this != LocallyAdded);
	}
	
	public String getLocalizedStringRepresentation()
	{
		return FlexoLocalization.localizedForKey("CVSStatus_"+getUnlocalizedStringRepresentation());
	}

	public String getUnlocalizedStringRepresentation()
	{
		return toString();
	}
}
