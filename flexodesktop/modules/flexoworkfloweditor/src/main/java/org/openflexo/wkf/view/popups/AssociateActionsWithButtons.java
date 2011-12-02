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
import java.awt.Component;
import java.awt.ComponentOrientation;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListCellRenderer;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;

import org.openflexo.ColorCst;
import org.openflexo.foundation.ie.widget.IEButtonWidget;
import org.openflexo.foundation.ie.widget.IEHyperlinkWidget;
import org.openflexo.foundation.ie.widget.IEWidget;
import org.openflexo.foundation.utils.FlexoCSS;
import org.openflexo.foundation.wkf.FlexoProcess;
import org.openflexo.foundation.wkf.node.ActionNode;
import org.openflexo.foundation.wkf.node.OperationNode;
import org.openflexo.foundation.wkf.utils.OperationAssociatedWithComponentSuccessfully;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.logging.FlexoLogger;
import org.openflexo.module.FlexoModule;
import org.openflexo.swing.ImageUtils;
import org.openflexo.swing.VerticalLayout;
import org.openflexo.toolbox.FlexoBoolean;
import org.openflexo.toolbox.WRLocator;
import org.openflexo.view.FlexoDialog;

/**
 * To refactored!
 * 
 * @author gpolet
 * 
 */
public class AssociateActionsWithButtons extends FlexoDialog {

	public static final int CANCEL = -1;

	public static final int IGNORE = -2;

	public static final int OK = 1;

	protected static final Logger logger = FlexoLogger.getLogger(AssociateActionsWithButtons.class.getPackage().getName());

	private OperationAssociatedWithComponentSuccessfully exception;

	protected Hashtable<IEHyperlinkWidget, JCheckBox> checkBoxes;

	/**
	 * Key: JComboBox Value: AbstractWidgetInterface
	 */
	protected Hashtable<JComboBox, IEHyperlinkWidget> comboBoxes;

	/**
	 * Key: AbstractWidgetInterface Value: ActionNode
	 */
	protected Hashtable<IEHyperlinkWidget, ActionNode> associations;

	protected Vector<IEHyperlinkWidget> buttons;

	protected Vector<ActionNode> actions;

	protected FlexoBoolean[] insertActionNode;

	protected boolean cleanActions = false;

	protected OperationNode currentOperation;

	protected int retval = CANCEL;

	private JPanel mainPane;

	public static final ActionNode dummyNew = new ActionNode((FlexoProcess) null);

	public AssociateActionsWithButtons(Vector<IEHyperlinkWidget> buttons, Vector<ActionNode> actions, OperationNode node,
			OperationAssociatedWithComponentSuccessfully exception)// Watch
	// out buttons are also Hyperlink
	{
		super(FlexoModule.getActiveModule().getFlexoFrame());
		this.buttons = buttons;
		this.actions = actions;
		this.exception = exception;
		currentOperation = node;
		this.actions.insertElementAt(dummyNew, 0);
		insertActionNode = new FlexoBoolean[buttons.size()];
		associations = new Hashtable<IEHyperlinkWidget, ActionNode>();
		Iterator i = buttons.iterator();
		while (i.hasNext()) {
			IEHyperlinkWidget element = (IEHyperlinkWidget) i.next();
			associations.put(element, dummyNew);
		}
		initUI();
		/* Get the UI ready */
		validate();
		pack();
		Insets insets = Toolkit.getDefaultToolkit().getScreenInsets(getGraphicsConfiguration());
		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		if (getSize().height > Toolkit.getDefaultToolkit().getScreenSize().height
				- (insets.bottom + insets.top + getInsets().bottom + getInsets().top)) {
			getContentPane().remove(mainPane);
			JScrollPane pane = new JScrollPane(mainPane, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
					ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
			pane.setPreferredSize(new Dimension(getSize().width + 20, Toolkit.getDefaultToolkit().getScreenSize().height
					- (insets.bottom + insets.top + getInsets().bottom + getInsets().top)));
			getContentPane().add(pane);
			validate();
			pack();
			setLocation((dim.width - getSize().width) / 2, insets.top);
		} else {
			setLocation((dim.width - getSize().width) / 2, (dim.height - getSize().height) / 2);
		}
	}

	private void initUI() {
		/* Initialize components */
		checkBoxes = new Hashtable<IEHyperlinkWidget, JCheckBox>();
		comboBoxes = new Hashtable<JComboBox, IEHyperlinkWidget>();
		mainPane = new JPanel(new VerticalLayout(4, 4, 4));
		JPanel selectAllPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
		selectAllPanel.setOpaque(false);
		JCheckBox selectAll = new JCheckBox(FlexoLocalization.localizedForKey("select_all"));
		selectAll.setOpaque(false);
		selectAll.setHorizontalTextPosition(SwingConstants.RIGHT);
		selectAll.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (e.getSource() instanceof JCheckBox) {
					JCheckBox c = ((JCheckBox) e.getSource());
					if (c.isSelected()) {
						Enumeration<JCheckBox> en = checkBoxes.elements();
						while (en.hasMoreElements()) {
							JCheckBox box = en.nextElement();
							if (!box.isSelected()) {
								box.setSelected(true);
							}
						}
						c.setText(FlexoLocalization.localizedForKey("all"));
					} else {
						Enumeration<JCheckBox> en = checkBoxes.elements();
						while (en.hasMoreElements()) {
							JCheckBox box = en.nextElement();
							if (box.isSelected()) {
								box.setSelected(false);
							}
						}
						c.setText(FlexoLocalization.localizedForKey("all"));
					}
				}
			}
		});
		selectAllPanel.add(selectAll);
		mainPane.add(selectAllPanel);
		Iterator i = buttons.iterator();
		int j = 0;
		while (i.hasNext()) {
			IEHyperlinkWidget widget = (IEHyperlinkWidget) i.next();
			insertActionNode[j] = new FlexoBoolean(widget.getIsMandatoryFlexoAction());
			ButtonPanel panel = new ButtonPanel(widget, insertActionNode[j]);
			mainPane.add(panel);
			j++;
		}
		Enumeration en = comboBoxes.keys();
		while (en.hasMoreElements()) {
			JComboBox combo = (JComboBox) en.nextElement();
			if (combo.getSelectedItem() != dummyNew) {
				Enumeration en1 = comboBoxes.keys();
				while (en1.hasMoreElements()) {
					JComboBox combo1 = (JComboBox) en1.nextElement();
					if (combo1 != combo) {
						combo1.removeItem(combo.getSelectedItem());
					}
				}
			}
		}
		boolean select = true;
		Enumeration<JCheckBox> en1 = checkBoxes.elements();
		while (select && en1.hasMoreElements()) {
			JCheckBox box = en1.nextElement();
			select &= box.isSelected();
		}
		if (select) {
			selectAll.doClick();
		}
		JPanel panel;

		// OK Button
		JButton b = new JButton();
		b.setOpaque(false);
		b.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (logger.isLoggable(Level.FINE)) {
					logger.fine("Pressed ok");
				}
				synchronizeCheckboxes();
				retval = OK;
				dispose();
			}
		});
		b.setText(FlexoLocalization.localizedForKey("ok", b));
		panel = new JPanel();
		panel.add(b);
		getRootPane().setDefaultButton(b);
		// Ignore Button
		if (getException() != null) {
			b = new JButton();
			b.setOpaque(false);
			b.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent arg0) {
					if (logger.isLoggable(Level.FINE)) {
						logger.fine("Pressed ignore");
					}
					retval = IGNORE;
					dispose();
				}
			});
			b.setText(FlexoLocalization.localizedForKey("ignore", b));
			panel.add(b);
		}
		// Cancel button
		b = new JButton();
		b.setOpaque(false);
		b.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (logger.isLoggable(Level.FINE)) {
					logger.fine("Pressed cancel");
				}
				retval = CANCEL;
				dispose();
			}
		});
		b.setText(FlexoLocalization.localizedForKey("cancel", b));
		panel.add(b);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		// Buttons panel
		panel.setBackground(ColorCst.GUI_BACK_COLOR);
		panel.setOpaque(true);

		getContentPane().setBackground(ColorCst.GUI_BACK_COLOR);
		mainPane.setBackground(ColorCst.GUI_BACK_COLOR);
		mainPane.add(panel);
		getContentPane().setVisible(true);
		getContentPane().add(mainPane);
		/* Initialize dialog */
		setModal(true);
		setResizable(false);
		setTitle(FlexoLocalization.localizedForKey("associate_buttons_with_actions", this));
	}

	protected JComboBox getComboBox(String name) {
		final JComboBox box = new JComboBox();
		if (name != null) {
			box.setName(name);
		}
		Enumeration en = actions.elements();
		while (en.hasMoreElements()) {
			Object element = en.nextElement();
			box.addItem(element);
		}
		box.setSelectedItem(dummyNew);
		box.setRenderer(new ActionNodeListCellRendrer());

		box.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				JComboBox cb = box;
				IEHyperlinkWidget w = comboBoxes.get(cb);
				ActionNode old = associations.get(w);
				ActionNode newNode = (ActionNode) cb.getSelectedItem();
				if (old != dummyNew) {
					// Re-add the old value to other combo's
					Enumeration en = comboBoxes.keys();
					while (en.hasMoreElements()) {
						JComboBox combo = (JComboBox) en.nextElement();
						if (combo != cb) {
							combo.addItem(old);
						}
					}
				}

				if (newNode != dummyNew) {
					// Remove value from all other combo's
					Enumeration en = comboBoxes.keys();
					while (en.hasMoreElements()) {
						JComboBox combo = (JComboBox) en.nextElement();
						if (combo != cb) {
							combo.removeItem(newNode);
						}
					}
				}
				associations.put(w, newNode);
			}
		});
		return box;
	}

	private void synchronizeCheckboxes() {
		Iterator i = buttons.iterator();
		int j = 0;
		while (i.hasNext()) {
			IEHyperlinkWidget widget = (IEHyperlinkWidget) i.next();
			insertActionNode[j].setValue(checkBoxes.get(widget).isSelected());
			j++;
		}
	}

	public class ActionNodeListCellRendrer extends JLabel implements ListCellRenderer {

		/**
		 * Overrides getListCellRendererComponent
		 * 
		 * @see javax.swing.ListCellRenderer#getListCellRendererComponent(javax.swing.JList, java.lang.Object, int, boolean, boolean)
		 */
		@Override
		public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
			if (value == dummyNew) {
				setText(FlexoLocalization.localizedForKey("new_action_to_create", this));
			} else {
				String s = null;
				String tooltip = null;
				ActionNode node = (ActionNode) value;
				s = node.getNodeName();
				if (s == null || s.trim().length() == 0) {
					s = node.getDefaultName();
				}
				tooltip = node.getDescription();
				setText(s);
				if (tooltip != null && tooltip.trim().length() > 0) {
					setToolTipText(tooltip);
				}
			}
			if (isSelected) {
				setBackground(list.getSelectionBackground());
				setForeground(list.getSelectionForeground());
			} else {
				setBackground(list.getBackground());
				setForeground(list.getForeground());
			}
			setFont(list.getFont());
			setPreferredSize(new Dimension(getFontMetrics(getFont()).stringWidth(getText()) + 15, getFontMetrics(getFont()).getHeight()));
			setOpaque(true);
			return this;
		}

	}

	public Hashtable getAssociations() {
		return associations;
	}

	public boolean isCleanActions() {
		return cleanActions;
	}

	public int getButtonPressed() {
		return retval;
	}

	public class ButtonPanel extends JPanel {

		private IEHyperlinkWidget button;

		protected FlexoBoolean insertActionNode;

		public ButtonPanel(IEHyperlinkWidget w, FlexoBoolean linkToBeginEnd) {
			button = w;
			this.insertActionNode = linkToBeginEnd;
			initPanel();
		}

		private void initPanel() {
			setLayout(new GridLayout(1, 3));
			((GridLayout) getLayout()).setHgap(4);
			((GridLayout) getLayout()).setVgap(4);
			setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
			setOpaque(true);
			setBackground(ColorCst.GUI_BACK_COLOR);
			String s = null;
			String tooltip = null;
			File icon = null;
			if (button instanceof IEButtonWidget) {
				IEButtonWidget b = (IEButtonWidget) button;
				s = b.getNameOrCalculatedLabel();
				if (s == null || s.trim().length() == 0) {
					s = b.getBeautifiedName();
				}
				tooltip = b.getDescription();
				if (tooltip == null || tooltip.trim().length() == 0) {
					tooltip = b.getTooltip();
				}
				FlexoCSS css = b.getFlexoCSS();
				icon = WRLocator.locate(b.getProject().getProjectDirectory(), b.getImageName(), css == null ? FlexoCSS.CONTENTO.getName()
						: css.getName());
			} else {
				IEHyperlinkWidget h = button;
				s = h.getNameOrCalculatedLabel();
				tooltip = h.getDescription();
				if (tooltip == null || tooltip.trim().length() == 0) {
					tooltip = h.getTooltip();
				}
			}
			if (button.getIsMandatoryFlexoAction()) {
				s = s + " (" + FlexoLocalization.localizedForKey("short_mandatory_sign") + ")";
			}
			// The Text
			JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 1, 1));
			final JCheckBox box = new JCheckBox();
			box.setToolTipText(FlexoLocalization.localizedForKey("insert_action_node", box));
			box.setSelected(insertActionNode.getValue());
			box.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					// insertActionNode.setValue(box.isSelected());
				}

			});
			box.setBackground(ColorCst.GUI_BACK_COLOR);
			panel.add(box);
			checkBoxes.put(button, box);
			JLabel label = new JLabel();
			label.setText(s);
			if (tooltip != null && tooltip.trim().length() > 0) {
				label.setToolTipText(tooltip);
			}
			panel.add(label);
			panel.setOpaque(true);
			panel.setBackground(ColorCst.GUI_BACK_COLOR);
			add(panel);
			// The icon (if there is one)
			JPanel middlePanel = new JPanel();
			middlePanel.setLayout(new FlowLayout(FlowLayout.LEFT, 1, 1));
			middlePanel.setBackground(ColorCst.GUI_BACK_COLOR);
			JLabel iconLabel = new JLabel();
			if (icon != null && icon.exists()) {
				ImageIcon ii = new ImageIcon(icon.getAbsolutePath());
				if (ii.getIconHeight() > 50 || ii.getIconWidth() > 100) {
					ii = ImageUtils.getThumbnail(ii, 35);
				}
				iconLabel.setIcon(ii);
				iconLabel.setBackground(ColorCst.GUI_BACK_COLOR);
			} else {
				iconLabel = new JLabel();
				iconLabel.setForeground(Color.BLUE);
				iconLabel.setText(button.getValue());
				iconLabel.setFont(IEHyperlinkWidget.getButtonFont());
				if (button.isCustomButton()) {
					Color color = ((IEWidget) button).getProject().getCssSheet().getButtonColor();
					if (color == null) {
						color = Color.BLACK;
					}
					iconLabel.setBackground(color);
					iconLabel.setForeground(Color.WHITE);
					iconLabel.setOpaque(true);
					iconLabel.setBorder(BorderFactory.createMatteBorder(0, 10, 1, 10, color));
					iconLabel.setVerticalTextPosition(SwingConstants.CENTER);
					iconLabel.setHorizontalAlignment(SwingConstants.CENTER);
				} else {
					iconLabel.setBackground(ColorCst.GUI_BACK_COLOR);
				}
			}
			middlePanel.add(iconLabel);
			add(middlePanel);
			// The dropdown
			JComboBox combo = getComboBox(s);
			comboBoxes.put(combo, button);
			Enumeration en = actions.elements();
			while (en.hasMoreElements()) {
				ActionNode action = (ActionNode) en.nextElement();
				if (action.getAssociatedButtonWidget() == button) {
					combo.setSelectedItem(action);
					box.setSelected(false);
					insertActionNode.setValue(false);
					break;
				}
			}
			panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 1, 1));
			panel.setOpaque(true);
			panel.setBackground(ColorCst.GUI_BACK_COLOR);
			panel.add(combo);
			add(panel);
		}
	}

	public OperationNode getCurrentOperation() {
		return currentOperation;
	}

	public Vector getActions() {
		return actions;
	}

	public Vector getButtons() {
		return buttons;
	}

	public FlexoBoolean[] getInsertActionNode() {
		return insertActionNode;
	}

	public OperationAssociatedWithComponentSuccessfully getException() {
		return exception;
	}

}
