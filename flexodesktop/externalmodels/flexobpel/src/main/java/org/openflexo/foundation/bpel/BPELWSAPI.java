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
package org.openflexo.foundation.bpel;

import org.openflexo.antar.ControlGraph;
import org.openflexo.antar.Instruction;
import org.openflexo.foundation.wkf.ws.FlexoPort;


public class BPELWSAPI extends Instruction {
	
	public FlexoPort associatedPort;
	public ControlGraph graph;
	
	
	public BPELWSAPI(FlexoPort p) {
		associatedPort=p;
		graph=null;
	}
	
	public void setControlGraph(ControlGraph cg) {
		graph=cg;
	}
	
	public ControlGraph getControlGraph() {
		return graph;
	}
	
	@Override
	public String toString() {
		String toReturn=new String();
		toReturn+= "BPEL API : \n";
		toReturn+= graph.toString();
		return toReturn;
	}
	
	@Override
	public Instruction clone() {
		return null;
	}

}
