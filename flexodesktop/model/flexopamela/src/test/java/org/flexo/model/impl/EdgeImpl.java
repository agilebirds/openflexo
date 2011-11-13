package org.flexo.model.impl;

import org.flexo.model.Edge;

public abstract class EdgeImpl extends FlexoModelObjectImpl implements Edge {

	public EdgeImpl() {
		super();
		// (new Exception("Build EdgeImpl "+Integer.toHexString(hashCode()))).printStackTrace();
	}

	@Override
	public String toString() {
		return "EdgeImpl " + getName() + " id=" + getFlexoID() + " startNode=" + getStartNode() + " endNode=" + getEndNode();
	}

}
