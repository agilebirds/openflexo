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
import org.openflexo.foundation.validation.Validable;
import org.openflexo.foundation.validation.ValidationError;
import org.openflexo.foundation.validation.ValidationIssue;
import org.openflexo.foundation.validation.ValidationRule;
import org.openflexo.foundation.wkf.FlexoProcess;
import org.openflexo.foundation.wkf.edge.FlexoPostCondition;
import org.openflexo.foundation.wkf.edge.InternalMessageInEdge;
import org.openflexo.foundation.wkf.node.AbstractNode;
import org.openflexo.foundation.wkf.node.ActivityNode;
import org.openflexo.foundation.xml.FlexoProcessBuilder;
import org.openflexo.localization.FlexoLocalization;

/**
 * 'New instance' port associated to a PortRegistery associated to a SubProcessNode
 * 
 * @author sguerin
 * 
 */
public final class NewPort extends AbstractInPort {

	protected static final Logger logger = Logger.getLogger(NewPort.class.getPackage().getName());

	// ==========================================================================
	// ============================= Variables
	// ==================================
	// ==========================================================================

	/**
	 * Constructor used during deserialization
	 */
	public NewPort(FlexoProcessBuilder builder) {
		this(builder.process);
		initializeDeserialization(builder);
	}

	/**
	 * Default constructor
	 */
	public NewPort(FlexoProcess process) {
		super(process);
	}

	/**
	 * Constructor with process and name
	 */
	public NewPort(FlexoProcess process, String aName) {
		this(process);
		setPortRegistery(process.getPortRegistery());
		setName(aName);
	}

	@Override
	public String getPrefixForFullQualifiedName() {
		return "NEW_PORT";
	}

	@Override
	public String getInspectorName() {
		return Inspectors.WKF.NEW_PORT_INSPECTOR;
	}

	@Override
	public String getDefaultName() {
		return getDefaultInitialName();
	}

	public static String getDefaultInitialName() {
		return FlexoLocalization.localizedForKey("new_instance_port_name");
	}

	// ==========================================================================
	// ============================= Validation
	// =================================
	// ==========================================================================

	@Override
	public boolean isCorrectelyLinked() {
		boolean isCorrectelyLinked = false;
		boolean foundValidEdge = false;
		for (Enumeration e = getOutgoingPostConditions().elements(); e.hasMoreElements();) {
			InternalMessageInEdge next = (InternalMessageInEdge) e.nextElement();
			AbstractNode nextNode = next.getNextNode();
			if ((nextNode instanceof ActivityNode) && (((ActivityNode) nextNode).isBeginNode())) {
				if (!foundValidEdge) {
					foundValidEdge = true;
					isCorrectelyLinked = true;
				} else {
					isCorrectelyLinked = false;
				}
			} else {
				isCorrectelyLinked = false;
			}
		}
		return isCorrectelyLinked;
	}

	protected Vector getInvalidOutgoingPostConditions() {
		Vector<FlexoPostCondition> returnedVector = new Vector<FlexoPostCondition>();
		// boolean isCorrectelyLinked = false;
		boolean foundValidEdge = false;
		for (Enumeration<FlexoPostCondition<AbstractNode, AbstractNode>> e = getOutgoingPostConditions().elements(); e.hasMoreElements();) {
			FlexoPostCondition<AbstractNode, AbstractNode> next = e.nextElement();
			AbstractNode nextNode = next.getNextNode();
			if ((nextNode instanceof ActivityNode) && (((ActivityNode) nextNode).isBeginNode())) {
				if (!foundValidEdge) {
					foundValidEdge = true;
					// isCorrectelyLinked = true;
				} else {
					// isCorrectelyLinked = false;
					returnedVector.add(next);
				}
			} else {
				// isCorrectelyLinked = false;
				returnedVector.add(next);
			}
		}
		return returnedVector;
	}

	public static class NewPortMustBeLinkedToABeginNode extends ValidationRule {
		public NewPortMustBeLinkedToABeginNode() {
			super(NewPort.class, "new_port_must_be_linked_to_a_begin_node");
		}

		@Override
		public ValidationIssue applyValidation(final Validable object) {
			final NewPort port = (NewPort) object;

			if (!port.isCorrectelyLinked()) {
				Vector invalidEdges = port.getInvalidOutgoingPostConditions();
				ValidationError error;
				if (invalidEdges.size() == 0) {
					error = new ValidationError(this, object, "port_has_no_outgoing_edges");
				} else {
					error = new ValidationError(this, object, "port_has_invalid_outgoing_edges");
					error.addToFixProposals(new DeleteInvalidEdges(invalidEdges));
				}
				return error;
			}
			return null;
		}

		public static class DeleteInvalidEdges extends FixProposal {
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
		return "new_port";
	}

}
