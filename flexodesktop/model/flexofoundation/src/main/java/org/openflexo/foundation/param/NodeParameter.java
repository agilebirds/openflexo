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
package org.openflexo.foundation.param;

import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.wkf.node.AbstractNode;
import org.openflexo.inspector.widget.DenaliWidget;

public class NodeParameter extends ParameterDefinition<AbstractNode> {

	private NodeSelectingConditional _nodeSelectingConditional;

	public NodeParameter(String name, String label, AbstractNode defaultValue) {
		super(name, label, defaultValue);
		addParameter("className", "org.openflexo.components.widget.NodeInspectorWidget");
		_nodeSelectingConditional = null;
	}

	@Override
	public String getWidgetName() {
		return DenaliWidget.CUSTOM;
	}

	private FlexoModelObject _rootObject;

	public void setRootObject(FlexoModelObject rootObject) {
		_rootObject = rootObject;
		addParameter("root", "params." + getName() + ".rootObject");
	}

	public FlexoModelObject getRootObject() {
		return _rootObject;
	}

	public boolean isAcceptableNode(AbstractNode aNode) {
		if (_nodeSelectingConditional != null)
			return _nodeSelectingConditional.isSelectable(aNode);
		return true;
	}

	public void setNodeSelectingConditional(NodeSelectingConditional nodeSelectingConditional) {
		_nodeSelectingConditional = nodeSelectingConditional;
		addParameter("isSelectable", "params." + getName() + ".isAcceptableNode");
	}

	public abstract static class NodeSelectingConditional {
		public abstract boolean isSelectable(AbstractNode node);
	}

}
