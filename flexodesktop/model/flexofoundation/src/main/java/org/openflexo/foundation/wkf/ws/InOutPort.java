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
package org.openflexo.foundation.wkf.ws;

import java.util.Enumeration;
import java.util.Vector;
import java.util.logging.Logger;

import org.openflexo.foundation.Inspectors;
import org.openflexo.foundation.validation.FixProposal;
import org.openflexo.foundation.validation.ValidationIssue;
import org.openflexo.foundation.validation.ValidationRule;
import org.openflexo.foundation.validation.ValidationWarning;
import org.openflexo.foundation.wkf.FlexoProcess;
import org.openflexo.foundation.wkf.edge.FlexoPostCondition;
import org.openflexo.foundation.wkf.edge.InternalMessageInEdge;
import org.openflexo.foundation.wkf.node.AbstractNode;
import org.openflexo.foundation.xml.FlexoProcessBuilder;
import org.openflexo.localization.FlexoLocalization;

/**
 * Input/output data port associated to a PortRegistery associated to a SubProcessNode This kind of port is used to modelize synchronous
 * call.
 * 
 * @author sguerin
 * 
 */
public final class InOutPort extends AbstractInPort implements OutputPort {

	@SuppressWarnings("unused")
	private static final Logger logger = Logger.getLogger(InOutPort.class.getPackage().getName());

	/**
	 * Stores the incoming post conditions, as a Vector of PortEntry
	 */
	private final Vector<FlexoPostCondition> _incomingPostConditions;

	/**
	 * Stores the input message definition
	 */
	private MessageDefinition _outputMessageDefinition;

	// ==========================================================================
	// ============================= Constructor
	// ================================
	// ==========================================================================

	/**
	 * Constructor used during deserialization
	 */
	public InOutPort(FlexoProcessBuilder builder) {
		this(builder.process);
		initializeDeserialization(builder);
	}

	/**
	 * Constructor with process and name
	 */
	public InOutPort(FlexoProcess process, String aName) {
		this(process);
		setPortRegistery(process.getPortRegistery());
		setName(aName);
	}

	/**
	 * Default constructor
	 */
	public InOutPort(FlexoProcess process) {
		super(process);
		_incomingPostConditions = new Vector<FlexoPostCondition>();
	}

	@Override
	public String getPrefixForFullQualifiedName() {
		return "IN_OUT_PORT";
	}

	@Override
	public String getInspectorName() {
		return Inspectors.WKF.IN_OUT_PORT_INSPECTOR;
	}

	@Override
	public String getDefaultName() {
		return getDefaultInitialName();
	}

	public static String getDefaultInitialName() {
		return FlexoLocalization.localizedForKey("in_out_port_name");
	}

	@Override
	public boolean isOutPort() {
		return true;
	}

	@Override
	public MessageDefinition getOutputMessageDefinition() {
		if (_outputMessageDefinition == null) {
			_outputMessageDefinition = new MessageDefinition(getProcess(), this);
			_outputMessageDefinition.setIsOutputMessageDefinition();
			_outputMessageDefinition.setName(_outputMessageDefinition.getDefaultName());
		}
		return _outputMessageDefinition;
	}

	@Override
	public void setOutputMessageDefinition(MessageDefinition aMessageDefinition) {
		if (aMessageDefinition != _outputMessageDefinition) {
			if (aMessageDefinition != null) {
				aMessageDefinition.setPort(this);
				_outputMessageDefinition = aMessageDefinition;
				_outputMessageDefinition.setIsOutputMessageDefinition();
			} else {
				_outputMessageDefinition = null;
			}
			setChanged();
		}
	}

	@Override
	public boolean mayHaveIncomingPostConditions() {
		return true;
	}

	// ==========================================================================
	// ============================= Validation
	// =================================
	// ==========================================================================

	@Override
	public boolean isCorrectelyLinked() {
		return getOutgoingPostConditions().size() > 0 && getInvalidOutgoingPostConditions().size() == 0;
	}

	protected Vector<FlexoPostCondition<AbstractNode, AbstractNode>> getInvalidOutgoingPostConditions() {
		Vector<FlexoPostCondition<AbstractNode, AbstractNode>> returnedVector = new Vector<FlexoPostCondition<AbstractNode, AbstractNode>>();
		for (Enumeration<FlexoPostCondition<AbstractNode, AbstractNode>> e = getOutgoingPostConditions().elements(); e.hasMoreElements();) {
			FlexoPostCondition<AbstractNode, AbstractNode> next = e.nextElement();
			if (!next.isEdgeValid()) {
				returnedVector.add(next);
			}
		}
		return returnedVector;
	}

	public static class InOutPortMustBeLinkedToAtLeastAnActivityNode extends
			ValidationRule<InOutPortMustBeLinkedToAtLeastAnActivityNode, InOutPort> {
		public InOutPortMustBeLinkedToAtLeastAnActivityNode() {
			super(InOutPort.class, "in_out_port_must_be_linked_to_at_least_an_activity_node");
		}

		@Override
		public ValidationIssue<InOutPortMustBeLinkedToAtLeastAnActivityNode, InOutPort> applyValidation(InOutPort port) {
			if (!port.isCorrectelyLinked()) {
				Vector invalidEdges = port.getInvalidOutgoingPostConditions();
				ValidationWarning<InOutPortMustBeLinkedToAtLeastAnActivityNode, InOutPort> error;
				if (invalidEdges.size() == 0) {
					error = new ValidationWarning<InOutPortMustBeLinkedToAtLeastAnActivityNode, InOutPort>(this, port,
							"port_has_no_outgoing_edges");
				} else {
					error = new ValidationWarning<InOutPortMustBeLinkedToAtLeastAnActivityNode, InOutPort>(this, port,
							"port_has_invalid_outgoing_edges");
					error.addToFixProposals(new DeleteInvalidEdges(invalidEdges));
				}
				return error;
			}
			return null;
		}

		public static class DeleteInvalidEdges extends FixProposal<InOutPortMustBeLinkedToAtLeastAnActivityNode, InOutPort> {
			public Vector invalidEdges;

			public DeleteInvalidEdges(Vector invalidEdges) {
				super("delete_invalid_edges");
				this.invalidEdges = invalidEdges;
			}

			@Override
			protected void fixAction() {
				for (Enumeration e = invalidEdges.elements(); e.hasMoreElements();) {
					InternalMessageInEdge next = (InternalMessageInEdge) e.nextElement();
					next.delete();
				}
			}
		}

	}

	/**
	 * Overrides getClassNameKey
	 * 
	 * @see org.openflexo.foundation.FlexoModelObject#getClassNameKey()
	 */
	@Override
	public String getClassNameKey() {
		return "in_out_port";
	}

}
