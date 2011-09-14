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
package org.openflexo.fib.view.widget.table;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;

import org.openflexo.fib.controller.FIBController;
import org.openflexo.fib.model.FIBTable;
import org.openflexo.fib.model.FIBTableAction;
import org.openflexo.fib.utils.FIBIconLibrary;
import org.openflexo.fib.view.widget.FIBTableWidget;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.toolbox.ToolBox;

public class FIBTableWidgetFooter extends JPanel
{

    protected static final Logger logger = Logger.getLogger(FIBTableWidgetFooter.class.getPackage().getName());

	public static final Color GUI_BACK_COLOR = ToolBox.getPLATFORM()==ToolBox.MACOS ? null : Color.WHITE;

    public static final int MINIMUM_BROWSER_VIEW_WIDTH = 200;
    
    protected FIBTableWidget _widget;
    protected FIBTable _fibTable;
    protected FIBTableModel _tableModel;
    
    protected JButton plusButton;

    protected JButton minusButton;

    protected JButton optionsButton;

    protected JPopupMenu popupMenu = null;

    /**
     * Stores controls: key is the JButton and value the
     * FIBTableActionListener
     */
    //private Hashtable<JButton,FIBTableActionListener> _controls;

    public FIBTableWidgetFooter(FIBTable fibTable, FIBTableModel tableModel, FIBTableWidget widget)
    {
        super();
        _widget = widget;
        _tableModel = tableModel;
        _fibTable = fibTable;
        
        initializeActions(fibTable,tableModel);
               
        setBorder(BorderFactory.createEmptyBorder());
        setBackground(GUI_BACK_COLOR);
        setLayout(new BorderLayout());
        // setPreferredSize(new
        // Dimension(FlexoCst.MINIMUM_BROWSER_VIEW_WIDTH,FlexoCst.MINIMUM_BROWSER_CONTROL_PANEL_HEIGHT));
        setPreferredSize(new Dimension(MINIMUM_BROWSER_VIEW_WIDTH, 20));

        JPanel plusMinusPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        plusMinusPanel.setBackground(GUI_BACK_COLOR);
        plusMinusPanel.setBorder(BorderFactory.createEmptyBorder());

        plusButton = new JButton(FIBIconLibrary.BROWSER_PLUS_ICON);
        plusButton.setBackground(GUI_BACK_COLOR);
        plusButton.addActionListener(new ActionListener() {
            @Override
			public void actionPerformed(ActionEvent e)
            {
                if (!hasMultiplePlusActions()) {
                    plusPressed();
                    plusButton.setIcon(FIBIconLibrary.BROWSER_PLUS_ICON);
                }
            }

        });
        plusButton.setBorder(BorderFactory.createEmptyBorder());
        plusButton.setDisabledIcon(FIBIconLibrary.BROWSER_PLUS_DISABLED_ICON);
        // plusButton.setSelectedIcon(FlexoCst.BROWSER_PLUS_SELECTED_ICON);
        plusButton.addMouseListener(new MouseAdapter() {
            @Override
			public void mousePressed(MouseEvent mouseEvent)
            {
                if (plusButton.isEnabled()) {
					plusButton.setIcon(FIBIconLibrary.BROWSER_PLUS_SELECTED_ICON);
				}
                if (hasMultiplePlusActions()) {
                    getPlusActionMenu().show(mouseEvent.getComponent(), mouseEvent.getX(), mouseEvent.getY());
                    plusButton.setIcon(FIBIconLibrary.BROWSER_PLUS_ICON);
                }
            }

            @Override
			public void mouseReleased(MouseEvent mouseEvent)
            {
                if (plusButton.isEnabled()) {
					plusButton.setIcon(FIBIconLibrary.BROWSER_PLUS_ICON);
				}
                if (hasMultiplePlusActions()) {
                    getPlusActionMenu().show(mouseEvent.getComponent(), mouseEvent.getX(), mouseEvent.getY());
                }
            }
        });

        minusButton = new JButton(FIBIconLibrary.BROWSER_MINUS_ICON);
        minusButton.setBackground(GUI_BACK_COLOR);
        minusButton.addActionListener(new ActionListener() {
            @Override
			public void actionPerformed(ActionEvent e)
            {
                if (!hasMultipleMinusActions()) {
                    minusPressed();
                    minusButton.setIcon(FIBIconLibrary.BROWSER_MINUS_ICON);
                }
             }

        });
        minusButton.setBorder(BorderFactory.createEmptyBorder());
        minusButton.setDisabledIcon(FIBIconLibrary.BROWSER_MINUS_DISABLED_ICON);
        // minusButton.setSelectedIcon(FlexoCst.BROWSER_MINUS_SELECTED_ICON);
        minusButton.addMouseListener(new MouseAdapter() {
            @Override
			public void mousePressed(MouseEvent mouseEvent)
            {
               if (minusButton.isEnabled()) {
				minusButton.setIcon(FIBIconLibrary.BROWSER_MINUS_SELECTED_ICON);
			}
               if (hasMultipleMinusActions()) {
                   getMinusActionMenu().show(mouseEvent.getComponent(), mouseEvent.getX(), mouseEvent.getY());
                   minusButton.setIcon(FIBIconLibrary.BROWSER_MINUS_ICON);
               }
            }

            @Override
			public void mouseReleased(MouseEvent mouseEvent)
            {
                if (minusButton.isEnabled()) {
					minusButton.setIcon(FIBIconLibrary.BROWSER_MINUS_ICON);
				}
                if (hasMultipleMinusActions()) {
                	getMinusActionMenu().show(mouseEvent.getComponent(), mouseEvent.getX(), mouseEvent.getY());
                }
            }
        });

        plusMinusPanel.add(plusButton);
        plusMinusPanel.add(minusButton);

        add(plusMinusPanel, BorderLayout.WEST);
        optionsButton = new JButton(FIBIconLibrary.BROWSER_OPTIONS_ICON);
        optionsButton.setBorder(BorderFactory.createEmptyBorder());
        optionsButton.setDisabledIcon(FIBIconLibrary.BROWSER_OPTIONS_DISABLED_ICON);
        add(optionsButton, BorderLayout.EAST);

        optionsButton.addMouseListener(new MouseAdapter() {
            @Override
			public void mousePressed(MouseEvent mouseEvent)
            {
                if (optionsButton.isEnabled()) {
					optionsButton.setIcon(FIBIconLibrary.BROWSER_OPTIONS_SELECTED_ICON);
				}
                getOptionActionMenu().show(mouseEvent.getComponent(), mouseEvent.getX(), mouseEvent.getY());
             }

            @Override
			public void mouseReleased(MouseEvent mouseEvent)
            {
                if (optionsButton.isEnabled()) {
					optionsButton.setIcon(FIBIconLibrary.BROWSER_OPTIONS_ICON);
				}
                getOptionActionMenu().show(mouseEvent.getComponent(), mouseEvent.getX(), mouseEvent.getY());
           }

         });
 
        handleSelectionCleared();

        validate();
    }


    protected void handleSelectionChanged()
    {
        //System.out.println("handleSelectionChanged");
        plusActionMenuNeedsRecomputed = true;
        minusActionMenuNeedsRecomputed = true;
        optionsActionMenuNeedsRecomputed = true;
 
        if (hasMultiplePlusActions()) {
			plusButton.setEnabled(true);
		} else {
        	boolean isActive = false;
        	for (FIBTableAction action : _addActions.keySet()) {
        		FIBTableActionListener actionListener = _addActions.get(action);
        		if (actionListener.isActive(_tableModel.getSelectedObject())) {
					isActive = true;
				}
        	}
        	plusButton.setEnabled(isActive);
        }

        boolean isMinusActive = false;
        for (FIBTableAction action : _removeActions.keySet()) {
        	FIBTableActionListener actionListener = _removeActions.get(action);
        	if (actionListener.isActive(_tableModel.getSelectedObject())) {
        		isMinusActive = true;
        	}
         }
        minusButton.setEnabled(isMinusActive);

        optionsButton.setEnabled(_otherActions.size() > 0);
        
        /*FlexoModelObject focusedObject = getFocusedObject();
        Vector<FlexoModelObject> globalSelection = buildGlobalSelection();
        plusButton.setEnabled((focusedObject != null) && (getActionTypesWithAddType(focusedObject).size() > 0));
        minusButton.setEnabled((focusedObject != null) && (getActionTypesWithDeleteType(focusedObject, globalSelection).size() > 0));
        plusActionMenuNeedsRecomputed = true;*/
    }

    protected void handleSelectionCleared()
    {
    	handleSelectionChanged();
    	
        /*System.out.println("handleSelectionCleared");
        
        plusButton.setEnabled(false);
        minusButton.setEnabled(false);
 
        plusActionMenuNeedsRecomputed = true;
        minusActionMenuNeedsRecomputed = true;
        optionsActionMenuNeedsRecomputed = true;
 */
    }

    void plusPressed()
    {
        for (FIBTableAction action : _addActions.keySet()) {
        	FIBTableActionListener actionListener = _addActions.get(action);
        	if (actionListener.isActive(_tableModel.getSelectedObject())) {
               	actionListener.performAction(_tableModel.getSelectedObject());
        	}
        }
    }

    void minusPressed()
    {
        for (FIBTableAction action : _removeActions.keySet()) {
         	FIBTableActionListener actionListener = _removeActions.get(action);
        	if (actionListener.isActive(_tableModel.getSelectedObject())) {
         		//actionListener.performAction(_tableModel.getSelectedObject(), _tableModel.getSelectedObjects());
               	actionListener.performAction(_tableModel.getSelectedObject());
       	}
       }
   }

 
    boolean hasMultiplePlusActions()
    {
        return _addActions.size() > 1;
    }

    boolean hasMultipleMinusActions()
    {
        return _removeActions.size() > 1;
    }

    private JPopupMenu plusActionMenu = null;
    private JPopupMenu minusActionMenu = null;
    private JPopupMenu optionsActionMenu = null;

    private boolean plusActionMenuNeedsRecomputed = true;
    private boolean minusActionMenuNeedsRecomputed = true;
    private boolean optionsActionMenuNeedsRecomputed = true;

    private JPopupMenu getPlusActionMenu()
    {
         if (plusActionMenuNeedsRecomputed) {
            plusActionMenu = new JPopupMenu();
            if (logger.isLoggable(Level.FINE)) {
				logger.fine("Build plus menu");
			}
            
        	for (FIBTableAction action : _addActions.keySet()) {
        		FIBTableActionListener actionListener = _addActions.get(action);
        		actionListener.setSelectedObject(_tableModel.getSelectedObject());
        		//actionListener.setSelectedObjects(_tableModel.getSelectedObjects());
                JMenuItem menuItem = new JMenuItem(getLocalized(action.getName()));
                menuItem.addActionListener(actionListener);
                plusActionMenu.add(menuItem);
                menuItem.setEnabled(actionListener.isActive(_tableModel.getSelectedObject()));
        	}
 
        	plusActionMenuNeedsRecomputed = false;
        }
        return plusActionMenu;
    }

    private JPopupMenu getMinusActionMenu()
    {
    	if (minusActionMenuNeedsRecomputed) {
    		minusActionMenu = new JPopupMenu();

    		for (FIBTableAction action : _removeActions.keySet()) {
    			FIBTableActionListener actionListener = _removeActions.get(action);
    			actionListener.setSelectedObject(_tableModel.getSelectedObject());
    			//actionListener.setSelectedObjects(_tableModel.getSelectedObjects());
    			JMenuItem menuItem = new JMenuItem(getLocalized(action.getName()));
    			menuItem.addActionListener(actionListener);
    			minusActionMenu.add(menuItem);
    			menuItem.setEnabled(actionListener.isActive(_tableModel.getSelectedObject()));
    		}

    		minusActionMenuNeedsRecomputed = false;
    	}
    	return minusActionMenu;
    }

    private JPopupMenu getOptionActionMenu()
    {
    	if (optionsActionMenuNeedsRecomputed) {
    		optionsActionMenu = new JPopupMenu();
    		if (logger.isLoggable(Level.FINE)) {
				logger.fine("Build plus menu");
			}

    		for (FIBTableAction action : _otherActions.keySet()) {
    			FIBTableActionListener actionListener = _otherActions.get(action);
    			actionListener.setSelectedObject(_tableModel.getSelectedObject());
    			//actionListener.setSelectedObjects(_tableModel.getSelectedObjects());
    			JMenuItem menuItem = new JMenuItem(getLocalized(action.getName()));
    			menuItem.addActionListener(actionListener);
    			optionsActionMenu.add(menuItem);
    			menuItem.setEnabled(actionListener.isActive(_tableModel.getSelectedObject()));
    		}

        	optionsActionMenuNeedsRecomputed = false;
        }
        return optionsActionMenu;
    }

    
    private Hashtable<FIBTableAction,FIBTableActionListener> _addActions;
    private Hashtable<FIBTableAction,FIBTableActionListener> _removeActions;
    private Hashtable<FIBTableAction,FIBTableActionListener> _otherActions;
    
    private void initializeActions(FIBTable fibTable, FIBTableModel tableModel)
    {
    	_addActions = new Hashtable<FIBTableAction, FIBTableActionListener>();
    	_removeActions = new Hashtable<FIBTableAction, FIBTableActionListener>();
    	_otherActions = new Hashtable<FIBTableAction, FIBTableActionListener>();
           	
        for (Enumeration en = fibTable.getActions().elements(); en.hasMoreElements();) {
        	FIBTableAction plAction = (FIBTableAction) en.nextElement();
            FIBTableActionListener plActionListener = new FIBTableActionListener(plAction, tableModel,_widget.getController());
            if (plActionListener.isAddAction()) {
            	_addActions.put(plAction, plActionListener);
            } else if (plActionListener.isRemoveAction()) {
            	_removeActions.put(plAction, plActionListener);
            } else if (plActionListener.isCustomAction()) {
            	_otherActions.put(plAction, plActionListener);
            }
        }
    }
    
	public void delete()
	{
		for (FIBTableAction a : _addActions.keySet()) {
			_addActions.get(a).delete();
		}
		for (FIBTableAction a : _removeActions.keySet()) {
			_removeActions.get(a).delete();
		}
		for (FIBTableAction a : _otherActions.keySet()) {
			_otherActions.get(a).delete();
		}

		_widget = null;
		_fibTable = null;
		_tableModel = null;
	}

    public Enumeration<FIBTableActionListener> getAddActionListeners()
    {
    	return _addActions.elements();
    }
    
    protected void setModel(Object model)
    {
    	//logger.info("Set model with "+model);
       	for (FIBTableAction action : _addActions.keySet()) {
    		FIBTableActionListener actionListener = _addActions.get(action);
    		actionListener.setModel(model);
       	}   	
       	for (FIBTableAction action : _removeActions.keySet()) {
    		FIBTableActionListener actionListener = _removeActions.get(action);
    		actionListener.setModel(model);
       	}   	
       	for (FIBTableAction action : _otherActions.keySet()) {
    		FIBTableActionListener actionListener = _otherActions.get(action);
    		actionListener.setModel(model);
       	}   	
       	handleSelectionChanged();
      /* for (Enumeration en = _controls.elements(); en.hasMoreElements();) {
            FIBTableActionListener actionListener = (FIBTableActionListener) en.nextElement();
        	actionListener.setModel(model);
        }
        updateControls(null);*/
    }

    public FIBController getController()
    {
    	return _widget.getController();
    }
    
	public String getLocalized(String key)
	{
		return FlexoLocalization.localizedForKey(getController().getLocalizer(),key);
	}

}
