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
package org.openflexo.components.tabularbrowser;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Enumeration;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;


import org.openflexo.ColorCst;
import org.openflexo.FlexoCst;
import org.openflexo.components.browser.ElementTypeBrowserFilter;
import org.openflexo.components.browser.BrowserFilter.BrowserFilterStatus;
import org.openflexo.components.browser.view.BrowserActionSource;
import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.action.FlexoActionType;
import org.openflexo.icon.IconLibrary;
import org.openflexo.localization.FlexoLocalization;

public class TabularBrowserFooter extends JPanel {
    
    protected static final Logger logger = Logger.getLogger(TabularBrowserFooter.class.getPackage().getName());

    protected TabularBrowserView _tabularBrowserView;
    protected JButton plusButton;
    protected JButton minusButton;
    protected JButton optionsButton;    
    protected JPopupMenu popupMenu = null;
    
    public TabularBrowserFooter (TabularBrowserView tabularBrowserView)
    {
        super();
        _tabularBrowserView = tabularBrowserView;
        setBorder(BorderFactory.createEmptyBorder());
        setBackground(ColorCst.GUI_BACK_COLOR);
        setLayout(new BorderLayout());
        //setPreferredSize(new Dimension(FlexoCst.MINIMUM_BROWSER_VIEW_WIDTH,FlexoCst.MINIMUM_BROWSER_CONTROL_PANEL_HEIGHT));
        setPreferredSize(new Dimension(FlexoCst.MINIMUM_BROWSER_VIEW_WIDTH,20));
        
        JPanel plusMinusPanel = new JPanel(new FlowLayout(FlowLayout.LEFT,0,0));
        plusMinusPanel.setBackground(ColorCst.GUI_BACK_COLOR);
        plusMinusPanel.setBorder(BorderFactory.createEmptyBorder());
        
        plusButton = new JButton(IconLibrary.BROWSER_PLUS_ICON);
        plusButton.setBackground(ColorCst.GUI_BACK_COLOR);
        plusButton.addActionListener(new ActionListener() {
            @Override
			public void actionPerformed(ActionEvent e) {
                if (!hasMultiplePlusActions()) {
                    plusPressed();
                    plusButton.setIcon(IconLibrary.BROWSER_PLUS_ICON);
               }
            }
            
        });
        plusButton.setBorder(BorderFactory.createEmptyBorder());
        plusButton.setDisabledIcon(IconLibrary.BROWSER_PLUS_DISABLED_ICON);
        //plusButton.setSelectedIcon(FlexoCst.BROWSER_PLUS_SELECTED_ICON);
        plusButton.addMouseListener(new MouseAdapter() {
            @Override
			public void mousePressed(MouseEvent mouseEvent) {
                if (plusButton.isEnabled())
                    plusButton.setIcon(IconLibrary.BROWSER_PLUS_SELECTED_ICON);
                if (hasMultiplePlusActions()) {
                    getPlusActionMenu().show(mouseEvent.getComponent(),
                            mouseEvent.getX(),
                            mouseEvent.getY());
                    plusButton.setIcon(IconLibrary.BROWSER_PLUS_ICON);
                }            
            }
            @Override
			public void mouseReleased(MouseEvent mouseEvent) {
                if (plusButton.isEnabled())
                    plusButton.setIcon(IconLibrary.BROWSER_PLUS_ICON);
                if (hasMultiplePlusActions()) {
                    getPlusActionMenu().show(mouseEvent.getComponent(),
                            mouseEvent.getX(),
                            mouseEvent.getY());
                }            
            }
        });
        //FCH.setHelpItem(plusButton,"plus");

        
        minusButton = new JButton(IconLibrary.BROWSER_MINUS_ICON);
        minusButton.setBackground(ColorCst.GUI_BACK_COLOR);
        minusButton.addActionListener(new ActionListener() {
            @Override
			public void actionPerformed(ActionEvent e) {
                // Multiple DELETE ACTION popup menu not implemented yet
                // If you need it, do the same as for ADD ACTION
                minusPressed();
                minusButton.setIcon(IconLibrary.BROWSER_MINUS_ICON);
             }
            
        });
        minusButton.setBorder(BorderFactory.createEmptyBorder());
        minusButton.setDisabledIcon(IconLibrary.BROWSER_MINUS_DISABLED_ICON);
        //minusButton.setSelectedIcon(FlexoCst.BROWSER_MINUS_SELECTED_ICON);
        minusButton.addMouseListener(new MouseAdapter() {
            @Override
			public void mousePressed(MouseEvent mouseEvent) {
                if (minusButton.isEnabled())
                    minusButton.setIcon(IconLibrary.BROWSER_MINUS_SELECTED_ICON);
            }
            @Override
			public void mouseReleased(MouseEvent mouseEvent) {
                if (minusButton.isEnabled())
                    minusButton.setIcon(IconLibrary.BROWSER_MINUS_ICON);
            }
        });
        //FCH.setHelpItem(minusButton,"minus");
       
        plusMinusPanel.add(plusButton);
        plusMinusPanel.add(minusButton);
        
        add(plusMinusPanel,BorderLayout.WEST);
        optionsButton = new JButton(IconLibrary.BROWSER_OPTIONS_ICON);
        optionsButton.setBorder(BorderFactory.createEmptyBorder());
        optionsButton.setDisabledIcon(IconLibrary.BROWSER_OPTIONS_DISABLED_ICON);
        add(optionsButton,BorderLayout.EAST);
        
        optionsButton.addMouseListener(new MouseAdapter() {
            @Override
			public void mousePressed(MouseEvent mouseEvent) {
                if (hasOptionalFilters()) {
                    optionsButton.setIcon(IconLibrary.BROWSER_OPTIONS_SELECTED_ICON);
                    getPopupMenu().show(mouseEvent.getComponent(),
                            mouseEvent.getX(),
                            mouseEvent.getY());
                }
            }
            @Override
			public void mouseReleased(MouseEvent mouseEvent) {
                if (hasOptionalFilters()) {
                    getPopupMenu().show(mouseEvent.getComponent(),
                            mouseEvent.getX(),
                            mouseEvent.getY());
                }
            }
        });
        
        // if (browserView.getBrowser().getOptionalFilters().size() == 0)
        optionsButton.setEnabled(hasOptionalFilters());
        //FCH.setHelpItem(optionsButton,"options");
       
        if (logger.isLoggable(Level.FINE))
            logger.fine("Browser "+tabularBrowserView.getBrowser()+" has "+tabularBrowserView.getBrowser().getConfigurableElementTypeFilters().size()+" filters");
        
        handleSelectionCleared();
                
        //validate();
    }
        
     public void handleOptionalFilterAdded() 
    {
        optionsButton.setEnabled(hasOptionalFilters());
        if (popupMenu!=null)
            popupMenu.setVisible(false);
        popupMenu = null;
    }

    protected boolean hasOptionalFilters()
    {
        return _tabularBrowserView.getBrowser().getConfigurableElementTypeFilters().size() > 0;
    }
    
    protected JPopupMenu getPopupMenu()
    {
        if (popupMenu == null) {
            popupMenu = makePopupMenu();
        }
        return popupMenu;
    }
     
    
    private JPopupMenu makePopupMenu()
    {
        if (logger.isLoggable(Level.FINE))
            logger.fine("Browser "+_tabularBrowserView.getBrowser()+" has now "+_tabularBrowserView.getBrowser().getConfigurableElementTypeFilters().size()+" filters");

        JPopupMenu returned = new JPopupMenu() {
            @Override
			public void setVisible(boolean b) {
                 super.setVisible(b);
                if (!b) 
                    optionsButton.setIcon(IconLibrary.BROWSER_OPTIONS_ICON);
           }
            @Override
			public void menuSelectionChanged(boolean isIncluded) {
                 super.menuSelectionChanged(true);
            }
        };
        
        // Make the popup menu
        //popupMenu.add(new JCheckBoxMenuItem("coucou",FlexoCst.BROWSER_MINUS_ICON,true));
        //popupMenu.add(new JCheckBoxMenuItem("coucou2",FlexoCst.BROWSER_MINUS_ICON,false));
        for (Enumeration e = _tabularBrowserView.getBrowser().getConfigurableElementTypeFilters().elements(); e.hasMoreElements();) {
            ElementTypeBrowserFilter filter = (ElementTypeBrowserFilter) e.nextElement();
            returned.add(new TabularBrowserFilterMenuItem(filter));
       }
        
        returned.addSeparator();
        JMenuItem closeMenuItem = new JMenuItem(FlexoLocalization.localizedForKey("close"));
        returned.add(closeMenuItem);
        closeMenuItem.addActionListener(new ActionListener() {
           @Override
		public void actionPerformed(ActionEvent e) {
               popupMenu.setVisible(false);
            }
        });
        return returned;
    }
   
    protected void handleSelectionChanged()
    {
        FlexoModelObject focusedObject = getFocusedObject();
        Vector globalSelection = buildGlobalSelection();
        if (_tabularBrowserView.getEditor() == null) {
        	plusButton.setEnabled(false);
        	minusButton.setEnabled(false);
        }
        else {
        	plusButton.setEnabled ((focusedObject != null) && (getActionTypesWithAddType(focusedObject).size() > 0));
        	minusButton.setEnabled ((focusedObject != null) && (getActionTypesWithDeleteType(focusedObject,globalSelection).size() > 0));
        }
        plusActionMenuNeedsRecomputed = true;
    }
    
    private Vector getActionTypesWithAddType(FlexoModelObject focusedObject)
    {
        Vector returned = new Vector();
        for (Enumeration en = focusedObject.getActionList().elements(); en.hasMoreElements();) {
            FlexoActionType next = (FlexoActionType) en.nextElement();
            if (next.getActionCategory() == FlexoActionType.ADD_ACTION_TYPE) {
                if (next.isVisible(focusedObject, null,_tabularBrowserView.getEditor())) {
                    if (next.isEnabled(focusedObject, null,_tabularBrowserView.getEditor())) {
                        returned.add(next);
                    }
                }
            }
        }
        return returned;
    }
    
    private Vector getActionTypesWithDeleteType(FlexoModelObject focusedObject,Vector globalSelection)
    {
         Vector returned = new Vector();
        for (Enumeration en = focusedObject.getActionList().elements(); en.hasMoreElements();) {
            FlexoActionType next = (FlexoActionType) en.nextElement();
            if (next.getActionCategory() == FlexoActionType.DELETE_ACTION_TYPE) {
                if (next.isVisible(null, globalSelection,_tabularBrowserView.getEditor())) {
                    if (next.isEnabled(null, globalSelection,_tabularBrowserView.getEditor())) {
                        returned.add(next);
                    }
                }
            }
        }
        return returned;
        
    }
    
    protected void handleSelectionCleared() 
    {
        plusButton.setEnabled(false);
        minusButton.setEnabled(false);
        plusActionMenuNeedsRecomputed = true;
    }
    
    protected void plusPressed()
    {
        if (logger.isLoggable(Level.FINE))
            logger.fine("Pressed on plus");
        FlexoActionType actionType = (FlexoActionType)getActionTypesWithAddType(getFocusedObject()).firstElement();
        actionType.actionPerformed(new ActionEvent(new BrowserActionSource(_tabularBrowserView.getBrowser(),getFocusedObject(),null,_tabularBrowserView.getEditor()),1,"TabularBrowserFooter-PlusPressed"));
    }
    
    protected boolean hasMultiplePlusActions()
    {
        if (getFocusedObject() == null) return false;
        return getActionTypesWithAddType(getFocusedObject()).size() > 1;
    }

    private JPopupMenu plusActionMenu = null;
    private boolean plusActionMenuNeedsRecomputed = true;
    
    protected JPopupMenu getPlusActionMenu()
    {
        if (plusActionMenuNeedsRecomputed) {
            plusActionMenu = new JPopupMenu();
            if (logger.isLoggable(Level.FINE))
                logger.fine("Build plus menu");
            for (Enumeration en=getActionTypesWithAddType(getFocusedObject()).elements(); en.hasMoreElements();) {
                final FlexoActionType action = (FlexoActionType)en.nextElement();
                JMenuItem menuItem = new JMenuItem(action.getLocalizedName());
                menuItem.addActionListener(new ActionListener() {
                    @Override
					public void actionPerformed(ActionEvent e) {
                        action.actionPerformed(new ActionEvent(new BrowserActionSource(_tabularBrowserView.getBrowser(),getFocusedObject(),null,_tabularBrowserView.getEditor()),1,"TabularBrowserFooter-PlusPressed"));
                     }
                 });
                plusActionMenu.add(menuItem);
            }
            plusActionMenuNeedsRecomputed = false;
        }
        return plusActionMenu;
    }
    
 
    protected void minusPressed()
    {
        if (logger.isLoggable(Level.FINE))
        logger.fine("Pressed on minus");
        Vector globalSelection = buildGlobalSelection();
        FlexoActionType actionType = (FlexoActionType)getActionTypesWithDeleteType(getFocusedObject(),globalSelection).firstElement();
        actionType.actionPerformed(new ActionEvent(new BrowserActionSource(_tabularBrowserView.getBrowser(),null,globalSelection,_tabularBrowserView.getEditor()),2,"TabularBrowserFooter-MinusPressed"));       
    }

    /*private boolean hasMultipleMinusActions()
    {
        Vector globalSelection = buildGlobalSelection();
        return getActionTypesWithDeleteType(getFocusedObject(),globalSelection).size() > 1;
    }*/


    /**
     * Returns focused object, considering focused object is the last selected object. 
     * If none object are selected, return null.
     */
    public FlexoModelObject getFocusedObject() 
    {
         return _tabularBrowserView.getSelectedObject();
    }

    private Vector buildGlobalSelection() 
    {
        Vector returned = new Vector();
        returned.addAll(_tabularBrowserView.getSelectedObjects());
        return returned;
    }
    
    public class TabularBrowserFilterMenuItem extends JCheckBoxMenuItem implements ActionListener
    {
        protected ElementTypeBrowserFilter _filter;

        public TabularBrowserFilterMenuItem(ElementTypeBrowserFilter filter)
        {
            super(filter.getLocalizedName(),filter.getIcon(),filter.getStatus() == BrowserFilterStatus.SHOW);
            _filter = filter;
            addActionListener(this);
        }

        @Override
		public void actionPerformed(ActionEvent e) 
        {
            if (_filter != null) {
                _filter.setStatus((isSelected() ? BrowserFilterStatus.SHOW : BrowserFilterStatus.HIDE));
                if (_tabularBrowserView.getBrowser() != null) _tabularBrowserView.getBrowser().update(); 
                _tabularBrowserView.getTreeTable().treeStructureChanged();
            }
        }
    }

}
