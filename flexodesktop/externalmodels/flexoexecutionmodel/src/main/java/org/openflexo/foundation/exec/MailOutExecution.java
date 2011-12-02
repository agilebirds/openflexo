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
import org.openflexo.foundation.exec.inst.SendMail;
import org.openflexo.foundation.wkf.node.EventNode;
import org.openflexo.toolbox.ToolBox;

public class MailOutExecution extends EventNodeExecution<EventNode> {

	public MailOutExecution(EventNode node) {
		super(node);
	}

	@Override
	public ControlGraph makeSpecificControlGraph(boolean interprocedural) throws NotSupportedException, InvalidModelException {
		return new SendMail(getEventNode());
	}

	@Override
	protected String getProcedureName() {
		return "mailOut_" + ToolBox.capitalize(ToolBox.getJavaName(getEventNode().getName())) + "_" + getEventNode().getFlexoID();
	}

}
