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
package org.openflexo.foundation.ie;

import java.io.PrintStream;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

import javax.swing.tree.TreeNode;

import org.openflexo.foundation.ie.cl.ComponentDefinition;
import org.openflexo.foundation.ie.widget.IEHyperlinkWidget;
import org.openflexo.foundation.ie.widget.IEReusableWidget;
import org.openflexo.foundation.ie.widget.IETabWidget;

public class InstanceTree implements TreeNode {

	private ComponentInstance _node;
	private Vector<InstanceTree> children;
	private InstanceTree _rootNode;
	private InstanceTree _father;
	private Vector<IEHyperlinkWidget> localButtons;
	private Hashtable<IEHyperlinkWidget, Vector<ButtonInComponentInstance>> buttonMap;
	private OperationComponentInstanceTree _operationComponentInstanceTree;

	protected InstanceTree(ComponentInstance node, InstanceTree rootTree, InstanceTree father,
			OperationComponentInstanceTree operationComponentInstanceTree) {
		super();
		children = new Vector<InstanceTree>();
		localButtons = new Vector<IEHyperlinkWidget>();
		_operationComponentInstanceTree = operationComponentInstanceTree;
		_node = node;
		if (rootTree == null) {
			_rootNode = this;
			buttonMap = new Hashtable<IEHyperlinkWidget, Vector<ButtonInComponentInstance>>();
		} else {
			_rootNode = rootTree;
		}
		_operationComponentInstanceTree.registerInstance(this);
		buildChildren();
	}

	private void registerButton(ButtonInComponentInstance couple) {
		Vector<ButtonInComponentInstance> allOccurenceOfButton = buttonMap.get(couple.getButton());
		if (allOccurenceOfButton == null) {
			allOccurenceOfButton = new Vector<ButtonInComponentInstance>();
			buttonMap.put(couple.getButton(), allOccurenceOfButton);
		}
		if (!allOccurenceOfButton.contains(couple)) {
			allOccurenceOfButton.add(couple);
		}
	}

	public OperationComponentInstance getOperationComponentInstance() {
		return (OperationComponentInstance) _rootNode._node;
	}

	public ComponentDefinition getComponentDefinition() {
		return _node.getComponentDefinition();
	}

	public ComponentInstance getComponentInstance() {
		return _node;
	}

	private void buildChildren() {
		Enumeration<IObject> en = _node.getWOComponentEmbeddedIEObjects().elements();
		IObject temp = null;
		while (en.hasMoreElements()) {
			temp = en.nextElement();
			if (temp instanceof IETabWidget) {
				children.add(new InstanceTree(((IETabWidget) temp).getComponentInstance(), _rootNode, this, _operationComponentInstanceTree));
			} else if (temp instanceof IEReusableWidget) {
				children.add(new InstanceTree(((IEReusableWidget) temp).getReusableComponentInstance(), _rootNode, this,
						_operationComponentInstanceTree));
			} else if (temp instanceof IEHyperlinkWidget) {
				IEHyperlinkWidget button = (IEHyperlinkWidget) temp;
				if (getOperationComponentInstance().getActionNodeForButton(button) != null) {
					localButtons.add(button);
					_rootNode.registerButton(new ButtonInComponentInstance(button, _node));
				}
			}
		}
	}

	private int getDepth() {
		if (getParent() == null) {
			return 0;
		}
		return _father.getDepth() + 1;
	}

	public StringBuffer toString(StringBuffer buf) {
		int d = getDepth();
		for (int i = 0; i < d; i++) {
			buf.append("\t");
		}
		buf.append(_node.getFullyQualifiedName());
		buf.append("(buttons : ");
		Enumeration<IEHyperlinkWidget> en = localButtons.elements();
		while (en.hasMoreElements()) {
			buf.append(en.nextElement().getBeautifiedName());
			if (en.hasMoreElements()) {
				buf.append(" , ");
			}
		}
		buf.append(")\n");
		Enumeration<InstanceTree> en2 = children.elements();
		while (en2.hasMoreElements()) {
			buf.append(en2.nextElement().toString(buf));
		}
		return buf;
	}

	public void print(PrintStream out) {
		int d = getDepth();
		for (int i = 0; i < d; i++) {
			out.print("\t");
		}
		out.print(_node.getFullyQualifiedName());
		out.print("\n(buttons : ");
		Enumeration<IEHyperlinkWidget> en = localButtons.elements();
		while (en.hasMoreElements()) {
			out.print(en.nextElement().getBeautifiedName());
			if (en.hasMoreElements()) {
				out.print(" , ");
			}
		}
		out.println(")\n");
		Enumeration<InstanceTree> en2 = children.elements();
		while (en2.hasMoreElements()) {
			en2.nextElement().print(out);
		}
	}

	@Override
	public Enumeration children() {
		return children.elements();
	}

	@Override
	public boolean getAllowsChildren() {
		return true;
	}

	@Override
	public TreeNode getChildAt(int arg0) {
		return children.get(arg0);
	}

	@Override
	public int getChildCount() {
		return children.size();
	}

	@Override
	public int getIndex(TreeNode arg0) {
		return children.indexOf(arg0);
	}

	@Override
	public TreeNode getParent() {
		return _father;
	}

	@Override
	public boolean isLeaf() {
		return getChildCount() == 0;
	}

	private class ButtonInComponentInstance {

		private IEHyperlinkWidget _button;
		private ComponentInstance _ci;

		public ButtonInComponentInstance(IEHyperlinkWidget button, ComponentInstance ci) {
			super();
			_ci = ci;
			_button = button;
		}

		public IEHyperlinkWidget getButton() {
			return _button;
		}

		public ComponentInstance getComponentInstance() {
			return _ci;
		}

		@Override
		public boolean equals(Object obj) {
			if (obj instanceof ButtonInComponentInstance) {
				return ((ButtonInComponentInstance) obj).getButton().equals(getButton())
						&& ((ButtonInComponentInstance) obj).getComponentInstance().equals(getComponentInstance());
			}
			return false;
		}

	}

}
