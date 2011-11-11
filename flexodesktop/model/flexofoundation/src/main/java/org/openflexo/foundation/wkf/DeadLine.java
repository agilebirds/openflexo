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
package org.openflexo.foundation.wkf;

import java.util.Vector;
import java.util.logging.Logger;

import org.openflexo.foundation.DeletableObject;
import org.openflexo.foundation.wkf.action.AddDeadline;
import org.openflexo.foundation.xml.FlexoProcessBuilder;
import org.openflexo.inspector.InspectableObject;

/**
 * Represents a deadline in a process
 * 
 * @author sguerin
 * @deprecated since version 1.2
 * 
 */
@Deprecated
public final class DeadLine extends WKFObject implements DeletableObject, InspectableObject, LevelledObject {

	@SuppressWarnings("unused")
	private static final Logger logger = Logger.getLogger(DeadLine.class.getPackage().getName());

	private String deadLineName;

	private String deadLineDescription;

	// ==========================================================================
	// ============================= Constructor
	// ================================
	// ==========================================================================

	/**
	 * Constructor used during deserialization
	 */
	public DeadLine(FlexoProcessBuilder builder) {
		this(builder.process);
		initializeDeserialization(builder);
	}

	/**
	 * Default constructor
	 */
	public DeadLine(FlexoProcess process) {
		super(process);
	}

	/**
	 * Default constructor
	 */
	public DeadLine(FlexoProcess process, String deadLineName) {
		this(process);
		setName(deadLineName);
	}

	@Override
	public String getFullyQualifiedName() {
		return getProcess().getFullyQualifiedName() + ".DEADLINE." + getName();
	}

	/**
	 * Default inspector name
	 */
	@Override
	public String getInspectorName() {
		return "DeadLine.inspector";
	}

	@Override
	protected Vector getSpecificActionListForThatClass() {
		Vector returned = super.getSpecificActionListForThatClass();
		returned.add(AddDeadline.actionType);
		return returned;
	}

	@Override
	public String getName() {
		return deadLineName;
	}

	@Override
	public void setName(String aName) {
		deadLineName = aName;
	}

	@Override
	public String getDescription() {
		return deadLineDescription;
	}

	@Override
	public void setDescription(String aDescription) {
		deadLineDescription = aDescription;
	}

	public DeadLineList getDeadLineList() {
		if (getProcess() != null) {
			return getProcess().getDeadLineList();
		}
		return null;
	}

	/**
	 * Return a Vector of all embedded WKFObjects
	 * 
	 * @return a Vector of WKFObject instances
	 */
	@Override
	public Vector getAllEmbeddedWKFObjects() {
		Vector returned = new Vector();
		returned.add(this);
		return returned;
	}

	/**
	 * Returns the level of a Deadline (which is {@link FlexoLevel.PROCESS}).
	 * 
	 * @see org.openflexo.foundation.wkf.LevelledObject#getLevel()
	 */
	@Override
	public FlexoLevel getLevel() {
		return FlexoLevel.PROCESS;
	}

	// ==========================================================================
	// ================================= Delete ===============================
	// ==========================================================================

	@Override
	public final void delete() {
		super.delete();
		deleteObservers();
	}

	/**
	 * Build and return a vector of all the objects that will be deleted during process deletion
	 * 
	 * @param aVector
	 *            of DeletableObject
	 */
	@Override
	public Vector<WKFObject> getAllEmbeddedDeleted() {
		return getAllEmbeddedWKFObjects();
	}

	/**
	 * Overrides getClassNameKey
	 * 
	 * @see org.openflexo.foundation.FlexoModelObject#getClassNameKey()
	 */
	@Override
	public String getClassNameKey() {
		return "deadline";
	}

}
