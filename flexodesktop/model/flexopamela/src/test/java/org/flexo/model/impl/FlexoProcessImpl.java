package org.flexo.model.impl;

import org.flexo.model.AbstractNode;
import org.flexo.model.Edge;
import org.flexo.model.FlexoProcess;

public abstract class FlexoProcessImpl extends FlexoModelObjectImpl implements FlexoProcess {

	@Override
	public String toString() {
		return "FlexoProcessImpl id=" + getFlexoID() + " name=" + getName() + " nodes=" + getNodes();
	}

	@Override
	public AbstractNode getNodeNamed(String name) {
		for (AbstractNode n : getNodes()) {
			if (n.getName() != null && n.getName().equals(name)) {
				return n;
			}
		}
		return null;
	}

	@Override
	public Edge getEdgeNamed(String name) {
		for (AbstractNode n : getNodes()) {
			for (Edge e : n.getIncomingEdges()) {
				if (e.getName() != null && e.getName().equals(name)) {
					return e;
				}
			}
			for (Edge e : n.getOutgoingEdges()) {
				if (e.getName() != null && e.getName().equals(name)) {
					return e;
				}
			}
		}
		return null;
	}

}
