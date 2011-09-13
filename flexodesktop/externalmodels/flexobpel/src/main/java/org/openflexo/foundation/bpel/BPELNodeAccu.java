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

import java.util.Vector;

import org.openflexo.foundation.wkf.node.AbstractNode;


public class BPELNodeAccu {

	private Vector<AbstractNode> nodes;
	private int lookingForIf=0;
	private int lookingForAnd=0;
	
	Vector<AbstractNode> handledNodes=new Vector<AbstractNode>();
	
	public Vector<AbstractNode> getNodes() {
		return nodes;
	}
	
	public void setNodes(Vector<AbstractNode> n) {
		nodes=n;
	}
	
	public void setNodes(Vector<AbstractNode> n, boolean b) {
		nodes=n;
		if (b) lookingForIf++;
	}
	
	public boolean lookingForIf() {
		return lookingForIf!=0;
	}
	
	public void setLookingForIf(boolean b) {
		if (b) lookingForIf++;
		else lookingForIf--;
	}
	
	public boolean lookingForAnd() {
		return lookingForAnd!=0;
	}
	
	public void setLookingForAnd(boolean b) {
		if(b) lookingForAnd++;
		else lookingForAnd--;
	}
	
	public BPELNodeAccu() {
		nodes=new Vector<AbstractNode>();
	}
	
	public void nodeIsHandled(AbstractNode n) throws BPELInvalidModelException {
		if (handledNodes.contains(n)) {
			throw new BPELInvalidModelException("An unresolvable cycle has been detected in the Flexo Process. This is not allowed for BPEL generation.");
		}
		else {
			handledNodes.add(n);
		}
	}
	
	public boolean hasBeenHandled(AbstractNode n) {
		return handledNodes.contains(n);
	}
	
}
