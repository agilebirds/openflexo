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
package org.openflexo.foundation.wkf.node;

import java.util.Vector;
import java.util.logging.Logger;

import org.openflexo.foundation.Inspectors;
import org.openflexo.foundation.wkf.FlexoProcess;
import org.openflexo.foundation.wkf.edge.FlexoPostCondition;
import org.openflexo.foundation.wkf.ws.ServiceInterface;
import org.openflexo.foundation.xml.FlexoProcessBuilder;
import org.openflexo.toolbox.EmptyVector;


/**
 * Entry point to access WebService calls
 * 
 * @author sguerin
 * 
 */
public class WSCallSubProcessNode extends SubProcessNode {

	@SuppressWarnings("unused")
	private static final Logger logger = Logger.getLogger(WSCallSubProcessNode.class.getPackage().getName());

	/**
	 * Constructor used during deserialization
	 */
	public WSCallSubProcessNode(FlexoProcessBuilder builder) {
		this(builder.process);
		initializeDeserialization(builder);
	}

	/**
	 * Default constructor
	 */
	public WSCallSubProcessNode(FlexoProcess process) {
		super(process);
	}

	/**
	 * Dynamic constructor with ServiceInterface...
	 */
	public WSCallSubProcessNode(FlexoProcess process, ServiceInterface _interface) {
		this(process);
		setServiceInterface(_interface);
	}

	/**
	 * Overrides delete
	 * 
	 * @see org.openflexo.foundation.wkf.node.AbstractActivityNode#delete()
	 */
	@Override
	public final void delete() {
		super.delete();
		deleteObservers();
	}

	@Override
	public boolean isAcceptableAsSubProcess(FlexoProcess process, FlexoProcess parentProcess) {

		return (!forceWSCallConsistency) && super.isAcceptableAsSubProcess(process, parentProcess);
	}

	@Override
	public String getInspectorName() {
		return Inspectors.WKF.WS_CALL_SUB_PROCESS_NODE_INSPECTOR;
	}

	@Override
	public boolean mightHaveOperationPetriGraph() {
		return false;
	}

	private static final Vector<FlexoPostCondition<AbstractNode, AbstractNode>> NO_OUTGOING_POST_CONDITIONS = new EmptyVector<FlexoPostCondition<AbstractNode, AbstractNode>>();

	@Override
	public Vector<FlexoPostCondition<AbstractNode, AbstractNode>> getOutgoingPostConditions() {
		return NO_OUTGOING_POST_CONDITIONS;
	}

}
