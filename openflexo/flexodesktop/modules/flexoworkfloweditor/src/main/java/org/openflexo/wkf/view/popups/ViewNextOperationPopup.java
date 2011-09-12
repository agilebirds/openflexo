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
package org.openflexo.wkf.view.popups;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.openflexo.ColorCst;
import org.openflexo.foundation.wkf.Status;
import org.openflexo.foundation.wkf.node.ActionNode;
import org.openflexo.foundation.wkf.node.OperationNode;
import org.openflexo.foundation.wkf.node.WorkflowPathToOperationNode;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.swing.VerticalLayout;
import org.openflexo.view.FlexoDialog;
import org.openflexo.wkf.controller.WKFController;


/**
 * @author gpolet
 *
 */
public class ViewNextOperationPopup extends FlexoDialog
{

	protected WKFController controller;

	protected ActionNode node;

	/**
	 * 
	 */
	public ViewNextOperationPopup(Frame owner, WKFController controller, ActionNode node)
	{
		super(owner);
		this.node = node;
		this.controller = controller;
		setTitle(FlexoLocalization.localizedForKey("next_operations_for_node ")+node.getName());
		initUI(node.getNextOperationsForAction());
		validate();
		doLayout();
		pack();
		if (getWidth()<300) {
			setSize(300, getHeight());
		}
		setVisible(true);
	}

	/**
	 * @param v
	 */
	private void initUI(List<WorkflowPathToOperationNode> v)
	{
		setMinimumSize(new Dimension(300,150));
		setLayout(new VerticalLayout(4,4,4));
		getContentPane().setBackground(ColorCst.GUI_BACK_COLOR);
		setBackground(ColorCst.GUI_BACK_COLOR);

		List<WorkflowPathToOperationNode> pathWithOperationNodeList = new ArrayList<WorkflowPathToOperationNode>();
		for(WorkflowPathToOperationNode workflowPath : v)
		{
			if(workflowPath.getOperationNode() != null) {
				pathWithOperationNodeList.add(workflowPath);
			}
		}

		if (pathWithOperationNodeList.size()==0) {
			JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER,1,1));
			panel.setBackground(ColorCst.GUI_BACK_COLOR);
			JLabel l = new JLabel(FlexoLocalization.localizedForKey("no_next_operations"));
			l.setBackground(ColorCst.GUI_BACK_COLOR);
			panel.add(l);
			add(panel);
		} else {
			Iterator<WorkflowPathToOperationNode> en = pathWithOperationNodeList.iterator();
			while (en.hasNext()) {
				WorkflowPathToOperationNode o = en.next();
				Status status = o.getNewStatusForProcess(o.getOperationNode().getProcess());
				if (status == null) {
					status = o.getOperationNode().getProcess().getStatusList().getDefaultStatus();
				}
				add(new OperationPanel(o.getOperationNode(), o.getNewStatusForProcess(o.getOperationNode().getProcess())));
			}
		}
		JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER,1,1));
		panel.setBackground(ColorCst.GUI_BACK_COLOR);
		JButton button = new JButton(FlexoLocalization.localizedForKey("ok"));
		button.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e)
			{
				dispose();
			}

		});
		button.setOpaque(false);
		panel.add(button);
		add(panel);
		getRootPane().setDefaultButton(button);
	}

	public ActionNode getNode()
	{
		return node;
	}

	public void setNode(ActionNode node)
	{
		this.node = node;
	}

	private class OperationPanel extends JPanel {

		protected OperationNode operation;

		protected Status status;

		protected JLabel label;

		/**
		 * @param o
		 * @param status
		 */
		public OperationPanel(OperationNode o, Status status)
		{
			this.operation = o;
			this.status = status;
			initUI();
		}

		private void initUI() {
			setBackground(ColorCst.GUI_BACK_COLOR);
			StringBuilder sb = new StringBuilder();
			if (operation.getProcess()!=node.getProcess()) {
				sb.append(operation.getProcess().getName()).append(">");
			}
			if (operation.getAbstractActivityNode()!=node.getAbstractActivityNode()) {
				sb.append(operation.getAbstractActivityNode().getName()).append(">");
			}
			sb.append(operation.getName()).append(": ").append(operation.hasWOComponent()?operation.getWOComponentName():FlexoLocalization.localizedForKey("no_component"));
			if (status != null) {
				sb.append(" - Status: "+status.getName());
			}
			label = new JLabel();
			label.setBackground(ColorCst.GUI_BACK_COLOR);
			label.setText(sb.toString());
			label.addMouseListener(new MouseListener() {

				@Override
				public void mouseClicked(MouseEvent e)
				{
					controller.selectAndFocusObject(operation);
				}

				@Override
				public void mouseEntered(MouseEvent e)
				{
					label.setForeground(Color.BLUE);
					label.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.BLUE));
				}

				@Override
				public void mouseExited(MouseEvent e)
				{
					label.setForeground(Color.BLACK);
					label.setBorder(null);
				}

				@Override
				public void mousePressed(MouseEvent e)
				{

				}

				@Override
				public void mouseReleased(MouseEvent e)
				{

				}

			});
			add(label);
		}
	}
}
