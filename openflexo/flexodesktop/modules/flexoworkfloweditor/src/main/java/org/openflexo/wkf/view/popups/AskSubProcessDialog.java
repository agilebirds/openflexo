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
import java.awt.Container;
import java.awt.DefaultFocusTraversalPolicy;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Enumeration;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;


import org.openflexo.foundation.wkf.FlexoProcess;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.toolbox.ToolBox;
import org.openflexo.view.FlexoDialog;

/**
 * Popup allowing to choose a way to select an existing sub-process or create a
 * new one
 * 
 * @author sguerin
 * 
 */
public class AskSubProcessDialog extends FlexoDialog implements ActionListener
{

    protected int returned;

    public static final int CANCEL = 0;

    public static final int VALIDATE_EXISTING_PROCESS = 1;

    public static final int VALIDATE_NEW_PROCESS = 2;

    private static final String EXISTING_PROCESS = "EXISTING_PROCESS";

    private static final String NEW_PROCESS = "NEW_PROCESS";

    private JComboBox existingProcessChoice;

    protected JTextField newProcessChoice;
    
    protected JButton confirmButton;
    
    protected JButton cancelButton;

    public int getStatus()
    {
        return returned;
    }

    public AskSubProcessDialog(FlexoProcess process)
    {
        super();
        returned = VALIDATE_NEW_PROCESS;

        Vector<String> availableProcesses = new Vector<String>();
        for (Enumeration e = process.getSubProcesses().elements(); e.hasMoreElements();) {
            FlexoProcess p = (FlexoProcess) e.nextElement();
            availableProcesses.add(p.getName());
        }

        setTitle(FlexoLocalization.localizedForKey("select_a_choice"));
        getContentPane().setLayout(new BorderLayout());

        // Create the radio buttons.
        JRadioButton existingProcessButton = new JRadioButton(FlexoLocalization.localizedForKey("bind_existing_sub_process"));
        existingProcessButton.setSelected(false);
        existingProcessButton.addActionListener(this);
        existingProcessButton.setActionCommand(EXISTING_PROCESS);
        JRadioButton newProcessButton = new JRadioButton(FlexoLocalization.localizedForKey("create_new_sub_process"));
        newProcessButton.addActionListener(this);
        newProcessButton.setActionCommand(NEW_PROCESS);
        // Group the radio buttons.
        ButtonGroup group = new ButtonGroup();
        group.add(existingProcessButton);
        group.add(newProcessButton);
        newProcessButton.setSelected(true);
        
        existingProcessChoice = new JComboBox(availableProcesses);
        existingProcessChoice.setEnabled(false);
        newProcessChoice = new JTextField(15);
        newProcessChoice.setEnabled(true);
        newProcessChoice.getDocument().addDocumentListener(new DocumentListener(){
            @Override
			public void changedUpdate(DocumentEvent e){
                removeUpdate(e);
            }
            @Override
			public void insertUpdate(DocumentEvent e){
                getRootPane().setDefaultButton(confirmButton);
            }
            @Override
			public void removeUpdate(DocumentEvent e){
                if(newProcessChoice.getText()!=null && !newProcessChoice.getText().equals("")){
                    getRootPane().setDefaultButton(confirmButton);
                }else{
                    getRootPane().setDefaultButton(cancelButton);
                }
            }
        });
        //newProcessChoice.setFocusable(true);
        //this.setFocusable(true);
        //newProcessChoice.setRequestFocusEnabled(true);
        setFocusTraversalPolicy(new MyFocusTraversalPolicy(newProcessChoice));
        JPanel choicePanel = new JPanel();

        if (availableProcesses.size() > 0) {
            choicePanel.setLayout(new GridLayout(2, 2));
        } else {
            choicePanel.setLayout(new FlowLayout());
        }
        if (availableProcesses.size() > 0) {
            choicePanel.add(existingProcessButton);
            choicePanel.add(existingProcessChoice);
        }
        choicePanel.add(newProcessButton);
        choicePanel.add(newProcessChoice);
        choicePanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JPanel controlPanel = new JPanel();
        controlPanel.setLayout(new FlowLayout());

        confirmButton = new JButton(FlexoLocalization.localizedForKey("validate"));
        cancelButton = new JButton(FlexoLocalization.localizedForKey("cancel"));
        rootPane.setDefaultButton(cancelButton);
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
        if (ToolBox.getPLATFORM()==ToolBox.MACOS) {
	        controlPanel.add(cancelButton);
	        controlPanel.add(confirmButton);
        } else {
        	controlPanel.add(confirmButton);
        	controlPanel.add(cancelButton);
        }

        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BorderLayout());

        contentPanel.add(choicePanel, BorderLayout.CENTER);
        contentPanel.add(controlPanel, BorderLayout.SOUTH);

        getContentPane().add(contentPanel, BorderLayout.CENTER);

        setModal(true);
        validate();
        pack();
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        setLocation((dim.width - getSize().width) / 2, (dim.height - getSize().height) / 2);
        setVisible(true);
    }

    /** Listens to the radio buttons. */

    @Override
	public void actionPerformed(ActionEvent e)
    {
        if (e.getActionCommand().equals(EXISTING_PROCESS)) {
            existingProcessChoice.setEnabled(true);
            newProcessChoice.setEnabled(false);
            returned = VALIDATE_EXISTING_PROCESS;
        } else if (e.getActionCommand().equals(NEW_PROCESS)) {
            newProcessChoice.setEnabled(true);
            existingProcessChoice.setEnabled(false);
            newProcessChoice.requestFocusInWindow();
            returned = VALIDATE_NEW_PROCESS;
        }
    }

    public String getSubProcessName()
    {
        if (returned == VALIDATE_EXISTING_PROCESS) {
            return (String) existingProcessChoice.getSelectedItem();
        } else if (returned == VALIDATE_NEW_PROCESS) {
            return newProcessChoice.getText();
        }
        return null;
    }

    private class MyFocusTraversalPolicy extends DefaultFocusTraversalPolicy{

        private Component _firstComponent;
        public MyFocusTraversalPolicy(Component firstComponent){
            super();
            _firstComponent = firstComponent;
        }
        /* (non-Javadoc)
         * @see java.awt.ContainerOrderFocusTraversalPolicy#getFirstComponent(java.awt.Container)
         */
        @Override
		public Component getFirstComponent(Container arg0)
        {
            return _firstComponent;
        }
        
        
    }
}
