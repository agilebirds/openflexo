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

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.logging.Logger;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumn;


import org.openflexo.foundation.wkf.node.NodeCompound;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.toolbox.ToolBox;
import org.openflexo.view.FlexoDialog;
import org.openflexo.wkf.controller.ReviewCopiedWOModel;

/**
 * Please comment this class
 * 
 * @author sguerin
 * 
 */
public class AskForCopyWOComponents extends FlexoDialog implements ActionListener
{

    private static final Logger logger = Logger.getLogger(AskForCopyWOComponents.class.getPackage().getName());

    ReviewCopiedWOModel _reviewCopiedWOModel;

    private int returned;

    public static final int IGNORE_WO = 0;

    public static final int MAKE_NEW_WO = 1;

    public static final int DUPLICATE_WO = 2;

    public static final int GENERALIZE_WO = 3;

    public static final int CANCEL = 4;

    private static final String IGNORE_WO_CHOICE = "IGNORE_WO";

    private static final String MAKE_NEW_WO_CHOICE = "MAKE_NEW_WO";

    private static final String DUPLICATE_WO_CHOICE = "DUPLICATE_WO";

    private static final String GENERALIZE_WO_CHOICE = "GENERALIZE_WO";

    private JTable reviewTable;

    public AskForCopyWOComponents(NodeCompound compound)
    {
        super();
        returned = IGNORE_WO;
        setTitle(FlexoLocalization.localizedForKey("select_how_to_copy_wo_components"));
        getContentPane().setLayout(new BorderLayout());
        _reviewCopiedWOModel = new ReviewCopiedWOModel(compound);

        JLabel hint1 = new JLabel(FlexoLocalization.localizedForKey("your_selection_contains_operation_nodes_with_an_attached_wo"), SwingConstants.CENTER);
        JLabel question = new JLabel(FlexoLocalization.localizedForKey("what_do_you_want_to_do"), SwingConstants.CENTER);

        // Create the radio buttons.
        JRadioButton ignoreWOButton = new JRadioButton(FlexoLocalization.localizedForKey("ignore_contained_wo"));
        ignoreWOButton.setSelected(true);
        ignoreWOButton.addActionListener(this);
        ignoreWOButton.setActionCommand(IGNORE_WO_CHOICE);
        JRadioButton newWOButton = new JRadioButton(FlexoLocalization.localizedForKey("make_new_wo"));
        newWOButton.addActionListener(this);
        newWOButton.setActionCommand(MAKE_NEW_WO_CHOICE);
        JRadioButton duplicateWOButton = new JRadioButton(FlexoLocalization.localizedForKey("duplicate_contained_wo"));
        duplicateWOButton.addActionListener(this);
        duplicateWOButton.setActionCommand(DUPLICATE_WO_CHOICE);
        JRadioButton generalizeWOButton = new JRadioButton(FlexoLocalization.localizedForKey("generalize_contained_wo"));
        generalizeWOButton.addActionListener(this);
        generalizeWOButton.setActionCommand(GENERALIZE_WO_CHOICE);

        // Group the radio buttons.
        ButtonGroup group = new ButtonGroup();
        group.add(ignoreWOButton);
        group.add(newWOButton);
        group.add(duplicateWOButton);
        group.add(generalizeWOButton);

        // Make the choice panel
        JPanel choicePanel = new JPanel();
        choicePanel.setLayout(new GridLayout(4, 1));
        choicePanel.add(ignoreWOButton);
        choicePanel.add(newWOButton);
        choicePanel.add(duplicateWOButton);
        choicePanel.add(generalizeWOButton);
        choicePanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel textPanel = new JPanel();
        textPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        textPanel.setLayout(new BorderLayout());
        textPanel.add(hint1, BorderLayout.NORTH);
        textPanel.add(question, BorderLayout.CENTER);
        textPanel.add(choicePanel, BorderLayout.SOUTH);

        reviewTable = new JTable(_reviewCopiedWOModel);
        reviewTable.setDefaultRenderer(String.class, new TableCellRenderer());
        for (int i = 0; i < _reviewCopiedWOModel.getColumnCount(); i++) {
            TableColumn col = reviewTable.getColumnModel().getColumn(i);
            col.setPreferredWidth(getPreferedColumnSize(i));
        }
        reviewTable.setEnabled(false);

        JScrollPane scrollPane = new JScrollPane(reviewTable);
        JPanel resourcesPanel = new JPanel();
        resourcesPanel.setLayout(new BorderLayout());
        resourcesPanel.add(reviewTable.getTableHeader(), BorderLayout.NORTH);
        resourcesPanel.add(scrollPane, BorderLayout.CENTER);
        resourcesPanel.setPreferredSize(new Dimension(500, 200));

        JPanel controlPanel = new JPanel();
        controlPanel.setLayout(new FlowLayout());

        JButton confirmButton = new JButton(FlexoLocalization.localizedForKey("validate"));
        JButton cancelButton = new JButton(FlexoLocalization.localizedForKey("cancel"));
        JButton selectAllButton = new JButton(FlexoLocalization.localizedForKey("select_all"));
        JButton deselectAllButton = new JButton(FlexoLocalization.localizedForKey("deselect_all"));

        cancelButton.addActionListener(new ActionListener() {
            @Override
			public void actionPerformed(ActionEvent e)
            {
                returned = CANCEL;
                dispose();
            }
        });
        confirmButton.addActionListener(new ActionListener() {
            @Override
			public void actionPerformed(ActionEvent e)
            {
                dispose();
            }
        });
        selectAllButton.addActionListener(new ActionListener() {
            @Override
			public void actionPerformed(ActionEvent e)
            {
                _reviewCopiedWOModel.selectAll();
            }
        });
        deselectAllButton.addActionListener(new ActionListener() {
            @Override
			public void actionPerformed(ActionEvent e)
            {
                _reviewCopiedWOModel.deselectAll();
            }
        });
        getRootPane().setDefaultButton(confirmButton);
        if (ToolBox.getPLATFORM()==ToolBox.MACOS) {
        	controlPanel.add(selectAllButton);
            controlPanel.add(deselectAllButton);
	        controlPanel.add(cancelButton);
	        controlPanel.add(confirmButton);
        } else {
        	controlPanel.add(confirmButton);
        	controlPanel.add(cancelButton);
        	controlPanel.add(selectAllButton);
            controlPanel.add(deselectAllButton);
        }

        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BorderLayout());

        contentPanel.add(textPanel, BorderLayout.NORTH);
        contentPanel.add(resourcesPanel, BorderLayout.CENTER);
        contentPanel.add(controlPanel, BorderLayout.SOUTH);

        getContentPane().add(contentPanel, BorderLayout.CENTER);

        setModal(true);
        setSize(1000, 200);
        validate();
        pack();
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        setLocation((dim.width - getSize().width) / 2, (dim.height - getSize().height) / 2);
        show();
    }

    public int getStatus()
    {
        return returned;
    }

    /** Listens to the radio buttons. */

    @Override
	public void actionPerformed(ActionEvent e)
    {
        if (e.getActionCommand().equals(IGNORE_WO_CHOICE)) {
            returned = IGNORE_WO;
            reviewTable.setEnabled(false);
        } else if (e.getActionCommand().equals(MAKE_NEW_WO_CHOICE)) {
            returned = MAKE_NEW_WO;
            reviewTable.setEnabled(true);
        } else if (e.getActionCommand().equals(DUPLICATE_WO_CHOICE)) {
            returned = DUPLICATE_WO;
            reviewTable.setEnabled(true);
        } else if (e.getActionCommand().equals(GENERALIZE_WO_CHOICE)) {
            returned = GENERALIZE_WO;
            reviewTable.setEnabled(true);
        }
    }

    public int getPreferedColumnSize(int arg0)
    {
        switch (arg0) {
        case 0:
            return 25; // checkbox
        case 1:
            return 150; // name
        case 2:
            return 150; // WO name
        case 3:
            return 150; // New WO name
        default:
            return 50;
        }
    }

    public ReviewCopiedWOModel getReviewCopiedWOModel()
    {
        return _reviewCopiedWOModel;
    }

    protected class TableCellRenderer extends DefaultTableCellRenderer
    {
        @Override
		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column)
        {
            Component component = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            component.setEnabled(table.isEnabled());
            return component;
        }
    }
}
