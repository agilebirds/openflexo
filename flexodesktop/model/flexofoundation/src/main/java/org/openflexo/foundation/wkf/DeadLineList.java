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

import java.util.Enumeration;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openflexo.foundation.DeletableObject;
import org.openflexo.foundation.wkf.action.AddDeadline;
import org.openflexo.foundation.wkf.dm.DeadLineInserted;
import org.openflexo.foundation.wkf.dm.DeadLineRemoved;
import org.openflexo.foundation.xml.FlexoProcessBuilder;

/**
 * Represents a list of deadlines of a process
 * 
 * @author sguerin
 * @deprecated since version 1.2
 * 
 */
@Deprecated
public final class DeadLineList extends WKFObject implements DeletableObject, LevelledObject {

	private Vector _deadLines;

	private static final Logger logger = Logger.getLogger(DeadLineList.class.getPackage().getName());

	// ==========================================================================
	// ============================= Constructor
	// ================================
	// ==========================================================================

	/**
	 * Constructor used during deserialization
	 */
	public DeadLineList(FlexoProcessBuilder builder) {
		this(builder.process);
		initializeDeserialization(builder);
	}

	/**
	 * Default constructor
	 */
	public DeadLineList(FlexoProcess process) {
		super(process);
		_deadLines = new Vector();
	}

	@Override
	public String getFullyQualifiedName() {
		return getProcess().getFullyQualifiedName() + ".DEADLINE_LIST";
	}

	public DeadLine deadlineWithName(String aName) {
		for (Enumeration e = getDeadLines().elements(); e.hasMoreElements();) {
			DeadLine temp = (DeadLine) e.nextElement();
			if (temp.getName().equals(aName)) {
				return temp;
			}
		}
		if (logger.isLoggable(Level.WARNING))
			logger.warning("Could not find deadline named " + aName);
		return null;
	}

	@Override
	protected Vector getSpecificActionListForThatClass() {
		Vector returned = super.getSpecificActionListForThatClass();
		returned.add(AddDeadline.actionType);
		return returned;
	}

	public Vector getDeadLines() {
		return _deadLines;
	}

	public void setDeadLines(Vector deadLines) {
		_deadLines = deadLines;
	}

	public void addToDeadLines(DeadLine aDeadLine) {
		_deadLines.add(aDeadLine);
		setChanged();
		notifyObservers(new DeadLineInserted(aDeadLine, getProcess()));
	}

	public void removeFromDeadLines(DeadLine aDeadLine) {
		_deadLines.remove(aDeadLine);
		aDeadLine.delete();
		setChanged();
		notifyObservers(new DeadLineRemoved(aDeadLine, getProcess()));
	}

	/**
	 * Return a Vector of all embedded WKFObjects
	 * 
	 * @return a Vector of WKFObject instances
	 */
	@Override
	public Vector getAllEmbeddedWKFObjects() {
		return getDeadLines();
	}

	/**
	 * Returns the level of a DeadLineList (which is {@link FlexoLevel.PROCESS}).
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
		_deadLines.removeAllElements();
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
		return "deadline_list";
	}

}
