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
package org.openflexo.foundation.wkf.edge;

import java.util.logging.Logger;

import org.openflexo.foundation.wkf.FlexoProcess;
import org.openflexo.foundation.wkf.node.AbstractNode;
import org.openflexo.foundation.wkf.ws.PortRegistery;

/**
 * Abstract edge linking a FlexoNode and a FlexoPort, inside a FlexoProcess
 * 
 * @author sguerin
 * 
 */
public abstract class InternalMessageEdge<S extends AbstractNode, E extends AbstractNode> extends MessageEdge<S, E> {

	@SuppressWarnings("unused")
	private static final Logger logger = Logger.getLogger(InternalMessageEdge.class.getPackage().getName());

	/**
	 * Default constructor
	 */
	public InternalMessageEdge(FlexoProcess process) {
		super(process);
	}

	public abstract PortRegistery getPortRegistery();

	/**
	 * Note: Means that this could be casted to InputPort
	 */
	@Override
	public boolean isInputPort() {
		if (getFlexoPort() != null)
			return getFlexoPort().isInPort();
		return false;
	}

	/**
	 * Note: Means that this could be casted to OutputPort
	 */
	@Override
	public boolean isOutputPort() {
		if (getFlexoPort() != null)
			return getFlexoPort().isOutPort();
		return false;
	}

}
