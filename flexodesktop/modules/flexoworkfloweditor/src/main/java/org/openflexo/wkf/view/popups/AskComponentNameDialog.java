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


import org.openflexo.foundation.ie.IERegExp;
import org.openflexo.foundation.ie.cl.FlexoComponentLibrary;
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
public class AskComponentNameDialog extends FlexoDialog implements ActionListener
{

    protected int returned;

    public static final int CANCEL = 0;

    public static final int VALIDATE_EXISTING_COMPONENT = 1;

    public static final int VALIDATE_NEW_COMPONENT = 2;

    private static final String EXISTING_COMPONENT = "EXISTING_COMPONENT";

    private static final String NEW_COMPONENT = "NEW_COMPONENT";

    private JComboBox existingOperationComponentChoice;

    protected JTextField newOperationComponentChoice;
    
    protected JButton confirmButton;
    
    protected JButton cancelButton;

    public int getStatus()
    {
        return returned;
    }

    public AskComponentNameDialog(FlexoComponentLibrary lib)
    {
        super();
        returned = VALIDATE_NEW_COMPONENT;

        Vector availableTabs = (Vector)lib.getOperationsComponentList().clone();
        

        setTitle(FlexoLocalization.localizedForKey("select_a_choice"));
        getContentPane().setLayout(new BorderLayout());

        // Create the radio buttons.
        final JRadioButton existingTabButton = new JRadioButton(FlexoLocalization.localizedForKey("bind_existing_operation_component"));
        existingTabButton.setSelected(false);
        existingTabButton.addActionListener(this);
        existingTabButton.setActionCommand(EXISTING_COMPONENT);
        JRadioButton newTabButton = new JRadioButton(FlexoLocalization.localizedForKey("create_new_operation_component"));
        newTabButton.addActionListener(this);
        newTabButton.setActionCommand(NEW_COMPONENT);
        // Group the radio buttons.
        ButtonGroup group = new ButtonGroup();
        group.add(existingTabButton);
        group.add(newTabButton);
        newTabButton.setSelected(true);
        
        existingOperationComponentChoice = new JComboBox(availableTabs);
        existingOperationComponentChoice.setEnabled(false);
        newOperationComponentChoice = IERegExp.getJavaClassNameValidationTextField(15);//new JTextField(15);
        newOperationComponentChoice.getDocument().addDocumentListener(new DocumentListener(){
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
                if(newOperationComponentChoice.getText()!=null && !newOperationComponentChoice.getText().equals("")){
                    getRootPane().setDefaultButton(confirmButton);
                }else{
                    getRootPane().setDefaultButton(cancelButton);
                }
            }
        });
        existingOperationComponentChoice.addActionListener(new ActionListener() {

            @Override
			public void actionPerformed(ActionEvent e)
            {
                existingTabButton.doClick();
            }
            
        });
        //newProcessChoice.setFocusable(true);
        //this.setFocusable(true);
        //newProcessChoice.setRequestFocusEnabled(true);
        setFocusTraversalPolicy(new MyFocusTraversalPolicy(newOperationComponentChoice));
        JPanel choicePanel = new JPanel();

        if (availableTabs.size() > 0) {
            choicePanel.setLayout(new GridLayout(2, 2));
        } else {
            choicePanel.setLayout(new FlowLayout());
        }
        choicePanel.add(newTabButton);
        choicePanel.add(newOperationComponentChoice);
        if (availableTabs.size() > 0) {
            choicePanel.add(existingTabButton);
            choicePanel.add(existingOperationComponentChoice);
        }
        choicePanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JPanel controlPanel = new JPanel();
        controlPanel.setLayout(new FlowLayout());

        confirmButton = new JButton(FlexoLocalization.localizedForKey("validate"));
        cancelButton = new JButton(FlexoLocalization.localizedForKey("cancel"));
        getRootPane().setDefaultButton(confirmButton);
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
        show();
        //requestFocus();
        //KeyboardFocusManager.getCurrentKeyboardFocusManager().clearGlobalFocusOwner();
        //newProcessChoice.requestFocus();
        //newProcessChoice.requestFocusInWindow();
        //newProcessChoice.setCaretPosition(0);
    }

    /** Listens to the radio buttons. */

    @Override
	public void actionPerformed(ActionEvent e)
    {
        if (e.getActionCommand().equals(EXISTING_COMPONENT)) {
            existingOperationComponentChoice.setEnabled(true);
            newOperationComponentChoice.setEnabled(false);
            returned = VALIDATE_EXISTING_COMPONENT;
        } else if (e.getActionCommand().equals(NEW_COMPONENT)) {
            newOperationComponentChoice.setEnabled(true);
            existingOperationComponentChoice.setEnabled(false);
            newOperationComponentChoice.requestFocusInWindow();
            returned = VALIDATE_NEW_COMPONENT;
        }
    }

    public String getWOComponentName()
    {
        if (returned == VALIDATE_EXISTING_COMPONENT) {
            return existingOperationComponentChoice.getSelectedItem().toString();
        } else if (returned == VALIDATE_NEW_COMPONENT) {
            return newOperationComponentChoice.getText();
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
