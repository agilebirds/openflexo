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
package org.openflexo.foundation.exec.inst;

import org.openflexo.foundation.wkf.FlexoProcess;

public class CreateProcessInstance extends CustomInstruction {

	private FlexoProcess process;

	public CreateProcessInstance(FlexoProcess process) {
		super();
		this.process = process;
		setInlineComment("Create new ProcessInstance for process " + getProcess().getName());
	}

	public FlexoProcess getProcess() {
		return process;
	}

	@Override
	public String toString() {
		return "[CreateProcessInstance:" + process.getName() + "]";
	}

	@Override
	public String getJavaStringRepresentation() {
		return "createProcessInstance(" + getProcess().getFlexoID() + ");";
	}

	@Override
	public CreateProcessInstance clone() {
		CreateProcessInstance returned = new CreateProcessInstance(process);
		returned.setHeaderComment(getHeaderComment());
		returned.setInlineComment(getInlineComment());
		return returned;
	}

}
