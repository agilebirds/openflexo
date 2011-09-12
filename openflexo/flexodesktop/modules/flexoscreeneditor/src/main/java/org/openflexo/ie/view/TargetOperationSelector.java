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
package org.openflexo.ie.view;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.FontMetrics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Enumeration;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.border.TitledBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import org.openflexo.swing.VerticalLayout;
import org.openflexo.view.controller.FlexoController;


import org.openflexo.components.widget.ProcessSelector;
import org.openflexo.foundation.DataModification;
import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.FlexoObservable;
import org.openflexo.foundation.FlexoObserver;
import org.openflexo.foundation.ie.menu.FlexoItemMenu;
import org.openflexo.foundation.ie.widget.IESequenceTab;
import org.openflexo.foundation.ie.widget.IETabWidget;
import org.openflexo.foundation.wkf.FlexoProcess;
import org.openflexo.foundation.wkf.OperationChange;
import org.openflexo.foundation.wkf.dm.DisplayProcessSet;
import org.openflexo.foundation.wkf.node.OperationNode;
import org.openflexo.localization.FlexoLocalization;

class TargetOperationSelector extends JPanel implements FlexoObserver
{
    /**
     * 
     */
    private final FlexoMenuItemView view;

    protected FlexoItemMenu model;

    private JPanel urlPanel;

    JTextField url;

    private JPanel radios;

    private JRadioButton internalPage;

    private JRadioButton externalPage;

    private JPanel process;

    private JPanel operationPanel;

    private JPanel popupOption;

    protected ProcessSelector selector;

    protected JComboBox operations;

    private JLabel operationLabel;

    protected JCheckBox openInNewWindow;

    protected JTextField width;

    protected JTextField height;

    protected JTextField windowName;

    protected JComboBox tabs;

    protected boolean updatingModel = false;

    protected TargetOperationSelector(FlexoMenuItemView v)
    {
        this.view = v;
        this.model = view._model;
        this.setLayout(new VerticalLayout());
        process = new JPanel(new FlowLayout(FlowLayout.LEFT));
        operationPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        popupOption = new JPanel(new FlowLayout(FlowLayout.LEFT));
        radios = new JPanel(new FlowLayout(FlowLayout.LEFT));
        urlPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        makeProcessSelector();
        insertRadios();
        makeUrlPanel();
        add(radios);
        insertPanels();
        update(this.model, null);
        this.model.addObserver(this);
        TitledBorder b = BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder());
        b.setTitle(FlexoLocalization.localizedForKey("page", b));
        setBorder(b);
    }

    /**
     * 
     */
    private void makeUrlPanel()
    {
        JLabel label = new JLabel();
        label.setText(FlexoLocalization.localizedForKey("url", label));
        url = new JTextField();
        url.getDocument().addDocumentListener(new DocumentListener() {
            @Override
			public void insertUpdate(DocumentEvent e)
            {
                updateUrl();
            }

            @Override
			public void removeUpdate(DocumentEvent e)
            {
                updateUrl();
            }

            @Override
			public void changedUpdate(DocumentEvent e)
            {
                updateUrl();
            }

            private void updateUrl()
            {
                updatingModel = true;
                model.setUrl(url.getText());
                updatingModel = false;

            }
        });
        url.setPreferredSize(new Dimension(200, 18));
        urlPanel.add(label);
        urlPanel.add(url);
    }

    /**
     * 
     */
    private void insertRadios()
    {
        internalPage = new JRadioButton();
        externalPage = new JRadioButton();
        internalPage.setText(FlexoLocalization.localizedForKey("internal_page", internalPage));
        externalPage.setText(FlexoLocalization.localizedForKey("external_page", externalPage));
        internalPage.addActionListener(new ActionListener() {

            @Override
			public void actionPerformed(ActionEvent e)
            {
                updateRadios();
            }

        });
        externalPage.addActionListener(new ActionListener() {

            @Override
			public void actionPerformed(ActionEvent e)
            {
                updateRadios();
            }

        });
        ButtonGroup bg = new ButtonGroup();
        bg.add(internalPage);
        bg.add(externalPage);
        radios.add(internalPage);
        radios.add(externalPage);
    }

    protected void updateRadios()
    {
        model.setUseUrl(externalPage.isSelected());
        insertPanels();
    }

    private void makeProcessSelector()
    {
        selector = new ProcessSelector(model.getProject(), model.getProcess()) {
            /**
             * Overrides apply
             * 
             * @see org.openflexo.components.widget.AbstractBrowserSelector#apply()
             */
            @Override
			public void apply()
            {
                super.apply();
                model.setProcess(getEditedObject());
            }
            
            @Override
            public boolean isSelectable(FlexoModelObject object) {
            	boolean isSelectable=super.isSelectable(object);
            	if (isSelectable) {
            		if (object instanceof FlexoProcess)
            			return model.isAcceptableAsDisplayProcess((FlexoProcess) object);
            	}
            	return isSelectable;
            }
        };
        selector.setPreferredSize(new Dimension(200, 25));
        JLabel label = new JLabel();
        label.setText(FlexoLocalization.localizedForKey("process", label));
        process.add(label);
        process.add(selector);
        process.validate();
        makeOperationsSelector();
    }

    private void makeOperationsSelector()
    {
        if (operations != null && operations.getParent() != null) {
            operations.getParent().remove(operations);
        }
        if (tabs != null && tabs.getParent() != null) {
            tabs.getParent().remove(tabs);
        }
        Vector<OperationNode> v;
        if (this.model.getProcess() != null)
            v = this.model.getProcess().getAllOperationNodesWithComponent();
        else
            v = new Vector<OperationNode>();
        v.insertElementAt(null, 0);
        operations = new JComboBox(v);
        operations.setRenderer(new DefaultListCellRenderer() {
            /**
             * Overrides getListCellRendererComponent
             * 
             * @see javax.swing.DefaultListCellRenderer#getListCellRendererComponent(javax.swing.JList,
             *      java.lang.Object, int, boolean, boolean)
             */
            @Override
			public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus)
            {
                JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value == null) {
                    label.setText(FlexoLocalization.localizedForKey("no_selection", label));
                } else {
                    label.setText(((OperationNode) value).getName());
                }
                FontMetrics fm = label.getFontMetrics(label.getFont());
                label.setPreferredSize(new Dimension(fm.stringWidth(label.getText()) + 15, fm.getHeight()));
                label.setBorder(BorderFactory.createEmptyBorder(0, 4, 0, 2));
                return label;
            }
        });
        operations.addActionListener(new ActionListener() {

            @Override
			public void actionPerformed(ActionEvent e)
            {
                if (operations.getSelectedItem() == null)
                    model.setOperation(null);
                else
                    model.setOperation((OperationNode) operations.getSelectedItem());
            }

        });
        if (operationLabel == null) {
            operationLabel = new JLabel();
            operationLabel.setText(FlexoLocalization.localizedForKey("Operation", operationLabel));
            operationPanel.add(operationLabel);
        }
        operationPanel.add(operations);
        tabs = new JComboBox();
        tabs.setRenderer(new DefaultListCellRenderer() {
            /**
             * Overrides getListCellRendererComponent
             * 
             * @see javax.swing.DefaultListCellRenderer#getListCellRendererComponent(javax.swing.JList,
             *      java.lang.Object, int, boolean, boolean)
             */
            @Override
			public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus)
            {
                JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value != null) {
                    if (value==null)
                        label.setText(FlexoLocalization.localizedForKey("no_selection", label));
                    else
                        label.setText(((IETabWidget) value).getTitle());
                }
                FontMetrics fm = label.getFontMetrics(label.getFont());
                label.setPreferredSize(new Dimension(fm.stringWidth(label.getText()) + 15, fm.getHeight()));
                label.setBorder(BorderFactory.createEmptyBorder(0, 4, 0, 2));
                return label;
            }
        });
        tabs.addActionListener(new ActionListener() {

            @Override
			public void actionPerformed(ActionEvent e)
            {
                if (tabs.getSelectedItem() == null)
                    model.setTabComponent(null);
                else
                    model.setTabComponent(((IETabWidget) tabs.getSelectedItem()).getTabComponentDefinition());
            }
        });
        operationPanel.add(tabs);
        operationPanel.validate();
        makePopupPanel();
        updateTab();
    }

    private void makePopupPanel()
    {
        if (openInNewWindow == null) {
            openInNewWindow = new JCheckBox();
            openInNewWindow.addActionListener(new ActionListener() {

                @Override
				public void actionPerformed(ActionEvent e)
                {
                    updatingModel = true;
                    model.setIsPageTarget(!openInNewWindow.isSelected());
                    updatingModel = false;
                }
            });
        }
        if (width == null) {
            width = new JTextField();
            width.setPreferredSize(new Dimension(50, 18));
            width.getDocument().addDocumentListener(new DocumentListener() {

                @Override
				public void insertUpdate(DocumentEvent e)
                {
                    updateWidth();
                }

                @Override
				public void removeUpdate(DocumentEvent e)
                {
                    updateWidth();
                }

                @Override
				public void changedUpdate(DocumentEvent e)
                {
                    updateWidth();
                }

                private void updateWidth()
                {
                    updatingModel = true;
                    try {
                        if (width.getText() == null || width.getText().trim().length() == 0) {
                            model.setPopupHeight(null);
                            updatingModel = false;
                            return;
                        }
                        Integer i = new Integer(width.getText());
                        if (i.intValue() < 0)
                            throw new NumberFormatException();
                        model.setPopupWidth(width.getText());
                    } catch (NumberFormatException e1) {
                        FlexoController.notify("enter_a_positive_number");
                        revert();
                    }
                    updatingModel = false;

                }
            });
        }

        if (height == null) {
            height = new JTextField();
            height.setPreferredSize(new Dimension(50, 18));
            height.getDocument().addDocumentListener(new DocumentListener() {

                @Override
				public void insertUpdate(DocumentEvent e)
                {
                    updateHeight();
                }

                @Override
				public void removeUpdate(DocumentEvent e)
                {
                    updateHeight();
                }

                @Override
				public void changedUpdate(DocumentEvent e)
                {
                    updateHeight();
                }

                private void updateHeight()
                {
                    updatingModel = true;
                    try {
                        if (height.getText() == null || height.getText().trim().length() == 0) {
                            model.setPopupHeight(null);
                            updatingModel = false;
                            return;
                        }
                        Integer i = new Integer(height.getText());
                        if (i.intValue() < 0)
                            throw new NumberFormatException();
                        model.setPopupHeight(height.getText());
                    } catch (NumberFormatException e1) {
                        FlexoController.notify("enter_a_positive_number");
                        revert();
                    }
                    updatingModel = false;
                }
            });
        }
        if (windowName == null) {
            windowName = new JTextField();
            windowName.setPreferredSize(new Dimension(200, 18));
            windowName.getDocument().addDocumentListener(new DocumentListener() {
                @Override
				public void insertUpdate(DocumentEvent e)
                {
                    updateWindowName();
                }

                @Override
				public void removeUpdate(DocumentEvent e)
                {
                    updateWindowName();
                }

                @Override
				public void changedUpdate(DocumentEvent e)
                {
                    updateWindowName();
                }

                private void updateWindowName()
                {
                    updatingModel = true;
                    model.setPopupWindowName(windowName.getText());
                    updatingModel = false;
                }
            });

        }
        if (openInNewWindow.getParent() == null) {
            JLabel label = new JLabel();
            label.setText(FlexoLocalization.localizedForKey("open_in_new_window", label));
            popupOption.add(label);
            popupOption.add(openInNewWindow);
        }
        if (width.getParent() == null) {
            JLabel label = new JLabel();
            label.setText(FlexoLocalization.localizedForKey("width", label));
            popupOption.add(label);
            popupOption.add(width);
        }
        if (height.getParent() == null) {
            JLabel label = new JLabel();
            label.setText(FlexoLocalization.localizedForKey("height", label));
            popupOption.add(label);
            popupOption.add(height);
        }
        if (windowName.getParent() == null) {
            JLabel label = new JLabel();
            label.setText(FlexoLocalization.localizedForKey("window_name", label));
            popupOption.add(label);
            popupOption.add(windowName);
        }
        popupOption.validate();
    }

    private void insertPanels()
    {
        if (!model.getUseUrl()) {
            if (urlPanel.getParent() != null)
                remove(urlPanel);
            if (process.getParent() == null)
                add(process, 1);
            if (model.getProcess() != null) {
                if (operationPanel.getParent() == null)
                    add(operationPanel, 2);
                if (this.model.getOperation() != null) {
                    if (popupOption.getParent() == null)
                        add(popupOption, 3);
                } else {
                    if (popupOption.getParent() != null)
                        remove(popupOption);
                }
            } else {
                if (operationPanel.getParent() != null)
                    remove(operationPanel);

                if (popupOption.getParent() != null)
                    remove(popupOption);
            }
        } else {
            if (process.getParent() != null)
                remove(process);
            if (operationPanel.getParent() != null)
                remove(operationPanel);
            if (popupOption.getParent() != null)
                remove(popupOption);
            if (urlPanel.getParent() == null)
                add(urlPanel);
        }
        revalidate();
        doLayout();
        repaint();
    }

    protected void revert()
    {
        SwingUtilities.invokeLater(new Runnable() {

            @Override
			public void run()
            {
                updateValuesFromModel();
            }

        });
    }

    protected void updateValuesFromModel()
    {
        url.setText(this.model.getUrl());
        width.setText(this.model.getPopupWidth());
        height.setText(this.model.getPopupHeight());
        windowName.setText(this.model.getPopupWindowName());
        openInNewWindow.setSelected(!this.model.getIsPageTarget());
        selector.setEditedObject(this.model.getProcess());
        if (this.model.getOperation() != null) {
            operations.setSelectedItem(this.model.getOperation());
        } else {
            operations.setSelectedItem(null);
        }
        internalPage.setSelected(!model.getUseUrl());
        externalPage.setSelected(model.getUseUrl());
        updateTab();
    }

    /**
     * 
     */
    private void updateTab()
    {
        if (this.model.getOperation() != null) {
            if (this.model.getOperation().getOperationComponent() != null && this.model.getOperation().getOperationComponent().getWOComponent().hasTabContainer()) {
                if (tabs != null && tabs.getParent() == null) {
                    operationPanel.add(tabs);
                    operationPanel.validate();
                }
                Vector<IETabWidget> v = new Vector<IETabWidget>();
                Enumeration<IESequenceTab> en = this.model.getOperation().getOperationComponent().getWOComponent().getAllTabContainers()
                        .elements();
                while (en.hasMoreElements()) {
                    IESequenceTab tab = en.nextElement();
                    v.addAll(tab.getAllTabs());
                }
                v.insertElementAt(null, 0);
                tabs.setModel(new DefaultComboBoxModel(v));
                if (this.model.getTabComponent() != null) {
                    for (int i = 0; i < tabs.getModel().getSize(); i++) {
                        IETabWidget tw = (IETabWidget) tabs.getModel().getElementAt(i);
                        if (tw!=null && tw.getComponentInstance()!=null && tw.getComponentInstance().getComponentDefinition() == this.model.getTabComponent()) {
                            tabs.setSelectedIndex(i);
                            break;
                        }
                    }
                } else
                    tabs.setSelectedItem(null);
            } else {
                if (tabs != null && tabs.getParent() != null) {
                    tabs.getParent().remove(tabs);
                }
            }
                
        } else {
            if (tabs != null && tabs.getParent() != null) {
                tabs.getParent().remove(tabs);
            }
        }
    }

    /**
     * Overrides update
     * 
     * @see org.openflexo.foundation.FlexoObserver#update(org.openflexo.foundation.FlexoObservable,
     *      org.openflexo.foundation.DataModification)
     */
    @Override
	public void update(FlexoObservable observable, DataModification dataModification)
    {
        if (updatingModel)
            return;
        if (observable == this.model) {
            updateValuesFromModel();
            insertPanels();
            if (dataModification instanceof DisplayProcessSet) {
                if (((DisplayProcessSet) dataModification).oldValue() != null)
                    ((FlexoProcess) ((DisplayProcessSet) dataModification).oldValue()).deleteObserver(this);
                makeOperationsSelector();
                if (this.model.getProcess() != null) {
                    this.model.getProcess().addObserver(this);
                }
            }
        }
        if (observable == this.model.getProcess()) {
            if (dataModification instanceof OperationChange)
                makeOperationsSelector();
        }
    }
    
    public void refresh()
    {
        updateValuesFromModel();
        insertPanels();
    }
}