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
package org.openflexo.antar;

import java.util.Vector;
import java.util.logging.Logger;

import org.openflexo.logging.FlexoLogger;

public class Class implements AlgorithmicUnit {

	@SuppressWarnings("unused")
	private static final Logger logger = FlexoLogger.getLogger(Class.class.getPackage().getName());

	private String className;
	private String groupName;
	private Vector<Procedure> procedures;
	private String comment;

	public Class(String className, String groupName) {
		super();
		this.className = className;
		this.groupName = groupName;
		this.procedures = new Vector<Procedure>();
	}

	public Class(String className, String groupName, Procedure... procedures) {
		this(className, groupName);
		for (Procedure p : procedures) {
			addProcedure(p);
		}
	}

	public Class(String className, String groupName, Vector<Procedure> procedures) {
		this(className, groupName);
		for (Procedure p : procedures) {
			addProcedure(p);
		}
	}

	public Vector<Procedure> getProcedures() {
		return procedures;
	}

	public void setProcedures(Vector<Procedure> procedures) {
		this.procedures = procedures;
	}

	public void addProcedure(Procedure procedure) {
		procedures.add(procedure);
	}

	public void removeProcedure(Procedure procedure) {
		procedures.remove(procedure);
	}

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	public String getGroupName() {
		return groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

}
