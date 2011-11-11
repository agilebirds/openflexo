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

import java.util.Vector;
import java.util.logging.Logger;

import org.openflexo.foundation.Inspectors;
import org.openflexo.foundation.bindings.Bindable;
import org.openflexo.foundation.validation.ValidationIssue;
import org.openflexo.foundation.validation.ValidationRule;
import org.openflexo.foundation.validation.ValidationWarning;
import org.openflexo.foundation.wkf.FlexoLevel;
import org.openflexo.foundation.wkf.FlexoProcess;
import org.openflexo.foundation.wkf.WKFObject;
import org.openflexo.foundation.wkf.node.AbstractNode;
import org.openflexo.foundation.wkf.node.OperatorNode;
import org.openflexo.foundation.wkf.ws.AbstractMessageDefinition;
import org.openflexo.foundation.wkf.ws.FlexoPort;
import org.openflexo.foundation.wkf.ws.MessageBindings;

/**
 * Abstract edge used to link FlexoNode with ports or portmaps and carrying a message
 * 
 * @author sguerin
 * 
 */
public abstract class MessageEdge<S extends AbstractNode, E extends AbstractNode> extends FlexoPostCondition<S, E> implements Bindable {

	@SuppressWarnings("unused")
	private static final Logger logger = Logger.getLogger(MessageEdge.class.getPackage().getName());

	private MessageBindings _inputMessage = null;

	private MessageBindings _outputMessage = null;

	/**
	 * Default constructor
	 */
	public MessageEdge(FlexoProcess process) {
		super(process);
	}

	@Override
	public FlexoLevel getLevel() {
		return FlexoLevel.ACTIVITY;
	}

	@Override
	public String getInspectorName() {
		return Inspectors.WKF.MESSAGE_EDGE_INSPECTOR;
	}

	public abstract FlexoPort getFlexoPort();

	public MessageBindings getInputMessage() {
		if (getInputMessageDefinition() != null && isInputPort() && (_inputMessage == null)) {
			_inputMessage = new MessageBindings(this, getInputMessageDefinition());
		}
		return _inputMessage;
	}

	public void setInputMessage(MessageBindings messageBindings) {
		messageBindings.setMessageDefinition(getInputMessageDefinition());
		messageBindings.setMessageEdge(this);
		_inputMessage = messageBindings;
	}

	public MessageBindings getOutputMessage() {
		if (getOutputMessageDefinition() != null && isOutputPort() && (_outputMessage == null)) {
			_outputMessage = new MessageBindings(this, getOutputMessageDefinition());
		}
		return _outputMessage;
	}

	public void setOutputMessage(MessageBindings messageBindings) {
		messageBindings.setMessageDefinition(getOutputMessageDefinition());
		messageBindings.setMessageEdge(this);
		_outputMessage = messageBindings;
	}

	public boolean getHasInputMessage() {
		return getInputMessageDefinition() != null;
	}

	public void setHasInputMessage() {
		// Read-only property
	}

	public boolean getHasOutputMessage() {
		return getOutputMessageDefinition() != null;
	}

	public void setHasOutputMessage() {
		// Read-only property
	}

	public abstract boolean isInputPort();

	public abstract boolean isOutputPort();

	public abstract AbstractMessageDefinition getInputMessageDefinition();

	public abstract AbstractMessageDefinition getOutputMessageDefinition();

	public void lookupMessageDefinition() {
		if (_inputMessage != null)
			_inputMessage.setMessageDefinition(getInputMessageDefinition());
		if (_outputMessage != null)
			_outputMessage.setMessageDefinition(getOutputMessageDefinition());

	}

	/**
	 * Return a Vector of all embedded WKFObjects
	 * 
	 * @return a Vector of WKFObject instances
	 */
	@Override
	public Vector<WKFObject> getAllEmbeddedWKFObjects() {
		Vector<WKFObject> returned = super.getAllEmbeddedWKFObjects();
		if (getInputMessage() != null)
			returned.add(getInputMessage());
		if (getOutputMessage() != null)
			returned.add(getOutputMessage());
		return returned;
	}

	public static class MessageEdgeCannotBeLinkedWithGateway extends
			ValidationRule<MessageEdgeCannotBeLinkedWithGateway, MessageEdge<AbstractNode, AbstractNode>> {
		public MessageEdgeCannotBeLinkedWithGateway() {
			super(MessageEdge.class, "MessageEdgeCannotBeLinkedWithGateway");
		}

		@Override
		public ValidationIssue<MessageEdgeCannotBeLinkedWithGateway, MessageEdge<AbstractNode, AbstractNode>> applyValidation(
				MessageEdge<AbstractNode, AbstractNode> post) {
			if (post.getStartNode() instanceof OperatorNode || post.getEndNode() instanceof OperatorNode) {
				return new ValidationWarning<MessageEdgeCannotBeLinkedWithGateway, MessageEdge<AbstractNode, AbstractNode>>(this, post,
						"MessageEdgeCannotBeLinkedWithGateway");
			}
			return null;
		}
	}
}
