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

import org.openflexo.foundation.Inspectors;
import org.openflexo.foundation.validation.ValidationError;
import org.openflexo.foundation.validation.ValidationIssue;
import org.openflexo.foundation.validation.ValidationRule;
import org.openflexo.foundation.wkf.FlexoProcess;
import org.openflexo.foundation.wkf.edge.FlexoPostCondition;
import org.openflexo.foundation.xml.FlexoProcessBuilder;
import org.openflexo.localization.FlexoLocalization;

public class ExclusiveEventBasedOperator extends CommonOutputOperatorNode {

	/**
	 * Constructor used during deserialization
	 */
	public ExclusiveEventBasedOperator(FlexoProcessBuilder builder)
	{
		this(builder.process);
		initializeDeserialization(builder);
	}

	/**
	 * Default constructor
	 */
	public ExclusiveEventBasedOperator(FlexoProcess process)
	{
		super(process);
	}

	@Override
	public String getInspectorName()
	{
		return Inspectors.WKF.OPERATOR_EXCLUSIVE_EVENT_INSPECTOR;
	}

	@Override
	public String getDefaultName() {
		return FlexoLocalization.localizedForKey("EVENT_BASED");
	}

	public static class NodeAfterEventBasedGatewayRules extends ValidationRule<NodeAfterEventBasedGatewayRules, ExclusiveEventBasedOperator> {

		public NodeAfterEventBasedGatewayRules() {
			super(ExclusiveEventBasedOperator.class, "node_after_event_based_gateway_rules");
		}

		@Override
		public ValidationIssue<NodeAfterEventBasedGatewayRules, ExclusiveEventBasedOperator> applyValidation(ExclusiveEventBasedOperator operator) {
			boolean seenEvent = false;
			boolean seenActivity = false;
			boolean seenOthers = false;
			for(FlexoPostCondition<AbstractNode,AbstractNode> post:operator.getOutgoingPostConditions()) {
				if (post.getEndNode() instanceof EventNode) {
					seenEvent = true;
				} else if(post.getEndNode().getNode() instanceof FlexoNode && ((FlexoNode)post.getEndNode().getNode()).getAbstractActivityNode()!=null) {
					seenActivity = true;
				} else {
					seenOthers = true;
				}
			}
			if (seenEvent && seenActivity)
				return new ValidationError<NodeAfterEventBasedGatewayRules, ExclusiveEventBasedOperator>(this, operator, "event_gateway_output_must_be_either_intermediate_catching_events_or_activities_but_not_both");
			if (seenOthers)
				return new ValidationError<NodeAfterEventBasedGatewayRules, ExclusiveEventBasedOperator>(this, operator, "event_gateway_output_must_be_either_intermediate_catching_events_or_activities");
			return null;
		}

	}

}
