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
package org.openflexo.localization;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collections;
import java.util.Comparator;
import java.util.Hashtable;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableCellRenderer;

/**
 * Please comment this class
 * 
 * @author sguerin
 * 
 */
public class LocalizedEditor extends JDialog
{
    public static Color BACK_COLOR;

    protected Vector<Character> allFirstChar;
    protected Hashtable<String,LocalizedEditorModel> _editorModels;
    protected Hashtable<String,JTable> _tables;
    protected Hashtable<Integer,String> _charForIndexes;
    
    //protected LocalizedEditorModel _editorModel = null;

    //protected JTable localizedTable;
    protected JTabbedPane tabbedPane;

    int selectedRow;

    protected JButton deleteEntryButton;

    public LocalizedEditor()
    {
        super((Frame) null, FlexoLocalization.localizedForKey("localized_editor"));
        
        tabbedPane = new JTabbedPane();
        
        _editorModels = new Hashtable<String,LocalizedEditorModel>();
        _tables = new Hashtable<String,JTable>();
        _charForIndexes = new Hashtable<Integer,String>() ;
        
        int currentIndex = 0;
        
        allFirstChar = FlexoLocalization.getAllFirstChar();
        Collections.sort(allFirstChar, new Comparator<Character>() {
            @Override
			public int compare(Character o1, Character o2) {
                return o1-o2;
            }           
        });

        for (char aChar : allFirstChar) {
            LocalizedEditorModel editorModel = new LocalizedEditorModel(aChar);
            installEditorModel(editorModel,String.valueOf(aChar));
            _charForIndexes.put(currentIndex, String.valueOf(aChar));
            currentIndex++;
        }

        LocalizedEditorModel editorModel = new LocalizedEditorModel();
        installEditorModel(editorModel,"warnings");
        _charForIndexes.put(currentIndex, "warnings");
        
        JPanel viewerPanel = new JPanel(new BorderLayout());

        viewerPanel.setBackground(BACK_COLOR);
        viewerPanel.add(controlPanel(), BorderLayout.NORTH);
        viewerPanel.add(tabbedPane, BorderLayout.CENTER);
        viewerPanel.add(buttonPanel(), BorderLayout.SOUTH);

        getContentPane().add(viewerPanel);
        setSize(800, 600);
        setLocation(100, 100);

    }

    
    private void installEditorModel(LocalizedEditorModel editorModel, String modelTitle){
        _editorModels.put(modelTitle, editorModel);

        JTable localizedTable = new JTable(editorModel);
        _tables.put(modelTitle, localizedTable);

        localizedTable.setDefaultRenderer(String.class, new LocalizedEditorCellRenderer());
        localizedTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        ListSelectionModel rowSM = localizedTable.getSelectionModel();
        rowSM.addListSelectionListener(new ListSelectionListener() {
            @Override
			public void valueChanged(ListSelectionEvent e)
            {
                // Ignore extra messages.
                if (e.getValueIsAdjusting())
                    return;
                ListSelectionModel lsm = (ListSelectionModel) e.getSource();
                if (lsm.isSelectionEmpty()) {
                    // no rows are selected
                } else {
                    selectedRow = lsm.getMinSelectionIndex();
                    deleteEntryButton.setEnabled(true);
                }
            }
        });

        JScrollPane scrollpane = new JScrollPane(localizedTable);

        tabbedPane.add(""+modelTitle, scrollpane);

        
    }
    protected JPanel buttonPanel()
    {
        JPanel answer = new JPanel(new FlowLayout());
        answer.setBackground(BACK_COLOR);
        JButton closeButton = new JButton(FlexoLocalization.localizedForKey("close"));
        closeButton.addActionListener(new ActionListener() {
            @Override
			public void actionPerformed(ActionEvent e)
            {
                setVisible(false);
            }
        });
        closeButton.setOpaque(false);
        answer.add(closeButton);
        JButton applyButton;
        applyButton = new JButton();
        applyButton.setText(FlexoLocalization.localizedForKey("apply", applyButton));
        applyButton.addActionListener(new ActionListener() {
            @Override
			public void actionPerformed(ActionEvent e)
            {
                FlexoLocalization.updateGUILocalized();
            }
        });
        applyButton.setOpaque(false);
        answer.add(applyButton);
        JButton saveButton;
        saveButton = new JButton();
        saveButton.setText(FlexoLocalization.localizedForKey("save", saveButton));
        saveButton.addActionListener(new ActionListener() {
            @Override
			public void actionPerformed(ActionEvent e)
            {
                FlexoLocalization.saveAllDictionaries();
            }
        });
        saveButton.setOpaque(false);
        answer.add(saveButton);
        JButton refreshButton;
        refreshButton = new JButton();
        refreshButton.setText(FlexoLocalization.localizedForKey("refresh", refreshButton));
        refreshButton.addActionListener(new ActionListener() {
            @Override
			public void actionPerformed(ActionEvent e)
            {
                for (char aChar : allFirstChar) {
                    LocalizedEditorModel newModel = new LocalizedEditorModel(aChar);
                    _tables.get(String.valueOf(aChar)).setModel(newModel);
                    _editorModels.put(String.valueOf(aChar), newModel);
                }
                
                LocalizedEditorModel editorModel = new LocalizedEditorModel();
                _tables.get("warnings").setModel(editorModel);
                _editorModels.put(String.valueOf("warnings"), editorModel);
            }
        });
        refreshButton.setOpaque(false);
        answer.add(refreshButton);
        deleteEntryButton = new JButton();
        deleteEntryButton.setText(FlexoLocalization.localizedForKey("delete_entry", deleteEntryButton));
        deleteEntryButton.addActionListener(new ActionListener() {
            @Override
			public void actionPerformed(ActionEvent e)
            {
                String selectedChar = _charForIndexes.get(tabbedPane.getSelectedIndex());
                LocalizedEditorModel editorModel = _editorModels.get(selectedChar);
                JTable table = _tables.get(selectedChar);
                FlexoLocalization.removeEntry((String) editorModel.getValueAt(selectedRow, 0));
                LocalizedEditorModel newModel = new LocalizedEditorModel(String.valueOf(selectedChar));
                _editorModels.put(String.valueOf(selectedChar), newModel);
                table.setModel(newModel);
            }
        });
        deleteEntryButton.setOpaque(false);
        deleteEntryButton.setEnabled(false);
        answer.add(deleteEntryButton);
        return answer;
    }

    protected JPanel controlPanel()
    {
        JPanel answer = new JPanel(new FlowLayout());
        answer.setBackground(BACK_COLOR);

        JLabel titleLabel = new JLabel(FlexoLocalization.localizedForKey("localized_editor"));
        answer.add(titleLabel);
        // answer.setSize(800,100);
        return answer;

    }

    protected class LocalizedEditorCellRenderer extends DefaultTableCellRenderer
    {
        @Override
		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column)
        {
            Component returned = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            if (column == 0) {
                if (LocalizedEditorModel.isKeyValid((String) value)) {
                    returned.setForeground(Color.BLACK);
                } else {
                    returned.setForeground(Color.RED);
                }
            } else {
                LocalizedEditorModel model = (LocalizedEditorModel)table.getModel();
                String key = (String) model.getValueAt(row, 0);
                if (!LocalizedEditorModel.isKeyValid(key)) {
                    returned.setForeground(Color.RED);
                } else if (LocalizedEditorModel.isValueValid(key, (String) value)) {
                    returned.setForeground(Color.BLACK);
                } else {
                    returned.setForeground(Color.ORANGE);
                }
            }
            if (isSelected) {
                returned.setBackground(Color.LIGHT_GRAY);
            } else {
                returned.setBackground(Color.WHITE);
            }
            return returned;
        }
    }
}
