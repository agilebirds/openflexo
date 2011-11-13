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

import org.openflexo.foundation.Inspectors;
import org.openflexo.foundation.wkf.FlexoProcess;
import org.openflexo.foundation.wkf.edge.ContextualEdgeStarting;
import org.openflexo.foundation.wkf.edge.FlexoPostCondition;
import org.openflexo.foundation.xml.FlexoProcessBuilder;
import org.openflexo.localization.FlexoLocalization;

public class SWITCHOperator extends OperatorNode implements ContextualEdgeStarting {

	/**
	 * Constructor used during deserialization
	 */
	public SWITCHOperator(FlexoProcessBuilder builder) {
		this(builder.process);
		initializeDeserialization(builder);
	}

	/**
	 * Default constructor
	 */
	public SWITCHOperator(FlexoProcess process) {
		super(process);
	}

	@Override
	public String getDefaultName() {
		return FlexoLocalization.localizedForKey("SWITCH");
	}

	@Override
	public String getInspectorName() {
		return Inspectors.WKF.OPERATOR_SWITCH_INSPECTOR;
	}

	@Override
	public Vector<FlexoPostCondition<AbstractNode, AbstractNode>> getOutgoingPostConditions() {
		// TODO not implemented yet (manage here an hashtable value/post)
		return null;
	}

	@Override
	public void setOutgoingPostConditions(Vector<FlexoPostCondition<AbstractNode, AbstractNode>> postConditions) {
		// TODO not implemented yet (manage here an hashtable value/post)
	}

	@Override
	public void addToOutgoingPostConditions(FlexoPostCondition post) {
		// Not applicable
		logger.warning("addToOutgoingPostConditions() called in SWITCHOperator with no context");
	}

	@Override
	public void addToOutgoingPostConditions(FlexoPostCondition post, Object outputContext) {
		logger.warning("NOT IMPLEMENTED: addToOutgoingPostConditions() called in SWITCHOperator with context=" + outputContext);
	}

	@Override
	public void removeFromOutgoingPostConditions(FlexoPostCondition post) {
		// TODO not implemented yet (manage here an hashtable value/post)
	}

}
