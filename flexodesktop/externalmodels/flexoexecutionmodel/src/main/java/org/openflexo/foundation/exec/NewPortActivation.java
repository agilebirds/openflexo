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
package org.openflexo.foundation.exec;

import org.openflexo.antar.ControlGraph;
import org.openflexo.antar.Procedure;
import org.openflexo.antar.ProcedureCall;
import org.openflexo.antar.Type;
import org.openflexo.antar.expr.Variable;
import org.openflexo.foundation.exec.inst.CreateProcessInstance;
import org.openflexo.foundation.wkf.ws.NewPort;

public class NewPortActivation extends PortActivation<NewPort> {

	protected NewPortActivation(NewPort port) {
		super(port);
	}

	@Override
	protected String getProcedureName() {
		return "newProcessInstance" + getPort().getFlexoID();
	}

	@Override
	protected ControlGraph makeControlGraph(boolean interprocedural) throws InvalidModelException, NotSupportedException {
		return makeSequentialControlGraph(new CreateProcessInstance(getPort().getProcess()), new ProcedureCall(PortDesactivation
				.getDesactivationPortBuilder(getPort()).makeProcedure()));
	}

	@Override
	protected Procedure makeProcedure() throws InvalidModelException, NotSupportedException {
		Procedure returned = new Procedure(getProcedureName(), makeControlGraph(true), getProcedureComment(),
				new Procedure.ProcedureParameter(new Variable("ec"), new Type("com.webobjects.eocontrol.EOEditingContext")));
		return returned;
	}

}
