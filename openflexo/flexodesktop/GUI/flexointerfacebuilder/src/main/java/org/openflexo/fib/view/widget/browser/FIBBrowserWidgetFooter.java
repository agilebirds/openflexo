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
package org.openflexo.fib.view.widget.browser;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.event.MenuKeyEvent;
import javax.swing.event.MenuKeyListener;

import org.openflexo.fib.controller.FIBController;
import org.openflexo.fib.model.FIBBrowser;
import org.openflexo.fib.model.FIBBrowserAction;
import org.openflexo.fib.model.FIBBrowserElement;
import org.openflexo.fib.utils.FIBIconLibrary;
import org.openflexo.fib.view.widget.FIBBrowserWidget;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.toolbox.ToolBox;

public class FIBBrowserWidgetFooter extends JPanel implements MouseListener, WindowListener
{

    protected static final Logger logger = Logger.getLogger(FIBBrowserWidgetFooter.class.getPackage().getName());

	public static final Color GUI_BACK_COLOR = ToolBox.getPLATFORM()==ToolBox.MACOS ? null : Color.WHITE;

	public static final int MINIMUM_BROWSER_VIEW_WIDTH = 200;
    
    protected FIBBrowserWidget _widget;
    protected FIBBrowser _fibBrowser;
    protected FIBBrowserModel _browserModel;
    
    protected JButton plusButton;
    protected JButton minusButton;
    protected JButton optionsButton;
    protected JButton filtersButton;

   // protected JPopupMenu popupMenu = null;

    private Hashtable<FIBBrowserElement,Hashtable<FIBBrowserAction,FIBBrowserActionListener>> _addActions;
    private Hashtable<FIBBrowserElement,Hashtable<FIBBrowserAction,FIBBrowserActionListener>> _removeActions;
    private Hashtable<FIBBrowserElement,Hashtable<FIBBrowserAction,FIBBrowserActionListener>> _otherActions;
    

    /**
     * Stores controls: key is the JButton and value the
     * FIBTableActionListener
     */
    //private Hashtable<JButton,FIBTableActionListener> _controls;

    public FIBBrowserWidgetFooter(FIBBrowser fibBrowser, FIBBrowserModel browserModel, FIBBrowserWidget widget)
    {
        super();
        _widget = widget;
        _browserModel = browserModel;
        _fibBrowser = fibBrowser;
        
        
        initializeActions(fibBrowser, browserModel);
               
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
                if (!hasMultiplePlusActions(currentElement)) {
                    plusPressed(currentElement);
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
                if (plusButton.isEnabled())
                    plusButton.setIcon(FIBIconLibrary.BROWSER_PLUS_SELECTED_ICON);
                if (hasMultiplePlusActions(currentElement)) {
                    getPlusActionMenu(currentElement).show(mouseEvent.getComponent(), mouseEvent.getX(), mouseEvent.getY());
                    plusButton.setIcon(FIBIconLibrary.BROWSER_PLUS_ICON);
                }
            }

            @Override
			public void mouseReleased(MouseEvent mouseEvent)
            {
                if (plusButton.isEnabled())
                    plusButton.setIcon(FIBIconLibrary.BROWSER_PLUS_ICON);
                if (hasMultiplePlusActions(currentElement)) {
                    getPlusActionMenu(currentElement).show(mouseEvent.getComponent(), mouseEvent.getX(), mouseEvent.getY());
                }
            }
        });

        minusButton = new JButton(FIBIconLibrary.BROWSER_MINUS_ICON);
        minusButton.setBackground(GUI_BACK_COLOR);
        minusButton.addActionListener(new ActionListener() {
            @Override
			public void actionPerformed(ActionEvent e)
            {
                if (!hasMultipleMinusActions(currentElement)) {
                    minusPressed(currentElement);
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
               if (minusButton.isEnabled())
                    minusButton.setIcon(FIBIconLibrary.BROWSER_MINUS_SELECTED_ICON);
               if (hasMultipleMinusActions(currentElement)) {
                   getMinusActionMenu(currentElement).show(mouseEvent.getComponent(), mouseEvent.getX(), mouseEvent.getY());
                   minusButton.setIcon(FIBIconLibrary.BROWSER_MINUS_ICON);
               }
            }

            @Override
			public void mouseReleased(MouseEvent mouseEvent)
            {
                if (minusButton.isEnabled())
                    minusButton.setIcon(FIBIconLibrary.BROWSER_MINUS_ICON);
                if (hasMultipleMinusActions(currentElement)) {
                	getMinusActionMenu(currentElement).show(mouseEvent.getComponent(), mouseEvent.getX(), mouseEvent.getY());
                }
            }
        });

        plusMinusPanel.add(plusButton);
        plusMinusPanel.add(minusButton);

        add(plusMinusPanel, BorderLayout.WEST);
        
        optionsButton = new JButton(FIBIconLibrary.BROWSER_OPTIONS_ICON);
        optionsButton.setBorder(BorderFactory.createEmptyBorder());
        optionsButton.setDisabledIcon(FIBIconLibrary.BROWSER_OPTIONS_DISABLED_ICON);

        optionsButton.addMouseListener(new MouseAdapter() {
        	@Override
        	public void mousePressed(MouseEvent mouseEvent)
        	{
        		if (optionsButton.isEnabled()) {
        			optionsButton.setIcon(FIBIconLibrary.BROWSER_OPTIONS_SELECTED_ICON);
        			getOptionActionMenu(currentElement).show(mouseEvent.getComponent(), mouseEvent.getX(), mouseEvent.getY());
        		}
        	}

        	@Override
        	public void mouseReleased(MouseEvent mouseEvent)
        	{
        		if (optionsButton.isEnabled()) {
        			optionsButton.setIcon(FIBIconLibrary.BROWSER_OPTIONS_ICON);
        			getOptionActionMenu(currentElement).show(mouseEvent.getComponent(), mouseEvent.getX(), mouseEvent.getY());
        		}
        	}

         });
 
        filtersButton = new JButton(FIBIconLibrary.BROWSER_FILTERS_ICON);
        filtersButton.setBorder(BorderFactory.createEmptyBorder());
        filtersButton.setDisabledIcon(FIBIconLibrary.BROWSER_FILTERS_DISABLED_ICON);
 
        filtersButton.addMouseListener(new MouseAdapter() {
        	@Override
        	public void mousePressed(MouseEvent mouseEvent)
        	{
                if (hasFilters()) {
                	filtersButton.setIcon(FIBIconLibrary.BROWSER_FILTERS_SELECTED_ICON);
                    getFiltersPopupMenu().show(mouseEvent.getComponent(), mouseEvent.getX(), mouseEvent.getY());
                }
        	}

        	@Override
        	public void mouseReleased(MouseEvent mouseEvent)
        	{
                if (hasFilters() && (filtersPopupMenu == null || !filtersPopupMenu.isVisible())) {
                	getFiltersPopupMenu().show(mouseEvent.getComponent(), mouseEvent.getX(), mouseEvent.getY());
                	getFiltersPopupMenu().grabFocus();
                }
        	}

        });

        filtersButton.setEnabled(hasFilters());
 
        JPanel optionsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        optionsPanel.setBackground(GUI_BACK_COLOR);
        optionsPanel.setBorder(BorderFactory.createEmptyBorder());
        optionsPanel.add(optionsButton);
        optionsPanel.add(filtersButton);
        
        add(optionsPanel, BorderLayout.EAST);
 
       handleSelectionCleared();

        validate();
    }

    private FIBBrowserElement currentElement;
    
    protected void switchToElement(FIBBrowserElement element)
    {
    	currentElement = element;
    }
    
    
    protected void handleSelectionChanged(FIBBrowserElement element)
    {
        //System.out.println("handleSelectionChanged");
    	
    	switchToElement(element);
    	
        plusActionMenuNeedsRecomputed = true;
        minusActionMenuNeedsRecomputed = true;
        optionsActionMenuNeedsRecomputed = true;
 
        if (element == null) {
        	plusButton.setEnabled(false);
        	minusButton.setEnabled(false);
        	optionsButton.setEnabled(false);
        }
        
        else {

        	if (hasMultiplePlusActions(element)) plusButton.setEnabled(true);
        	else {
        		boolean isActive = false;
        		for (FIBBrowserAction action : _addActions.get(element).keySet()) {
        			FIBBrowserActionListener actionListener = _addActions.get(element).get(action);
        			if (actionListener.isActive(_browserModel.getSelectedObject())) isActive = true;
        		}
        		plusButton.setEnabled(isActive);
        	}

        	boolean isMinusActive = false;
        	for (FIBBrowserAction action : _removeActions.get(element).keySet()) {
        		FIBBrowserActionListener actionListener = _removeActions.get(element).get(action);
        		if (actionListener.isActive(_browserModel.getSelectedObject())) {
        			isMinusActive = true;
        		}
        	}
        	minusButton.setEnabled(isMinusActive);

        	optionsButton.setEnabled(_otherActions.size() > 0);
        }
    }

    protected void handleSelectionCleared()
    {
    	handleSelectionChanged(null);
    	
     }

    void plusPressed(FIBBrowserElement element)
    {
        for (FIBBrowserAction action : _addActions.get(element).keySet()) {
        	FIBBrowserActionListener actionListener = _addActions.get(element).get(action);
        	if (actionListener.isActive(_browserModel.getSelectedObject())) {
               	actionListener.performAction(_browserModel.getSelectedObject());
        	}
        }
    }

    void minusPressed(FIBBrowserElement element)
    {
        for (FIBBrowserAction action : _removeActions.get(element).keySet()) {
         	FIBBrowserActionListener actionListener = _removeActions.get(element).get(action);
        	if (actionListener.isActive(_browserModel.getSelectedObject())) {
         		//actionListener.performAction(_tableModel.getSelectedObject(), _tableModel.getSelectedObjects());
               	actionListener.performAction(_browserModel.getSelectedObject());
       	}
       }
   }

 
    boolean hasMultiplePlusActions(FIBBrowserElement element)
    {
    	if (element == null) return false;
        return _addActions.get(element).size() > 1;
    }

    boolean hasMultipleMinusActions(FIBBrowserElement element)
    {
    	if (element == null) return false;
        return _removeActions.get(element).size() > 1;
    }

    private JPopupMenu plusActionMenu = null;
    private JPopupMenu minusActionMenu = null;
    private JPopupMenu optionsActionMenu = null;

    private boolean plusActionMenuNeedsRecomputed = true;
    private boolean minusActionMenuNeedsRecomputed = true;
    private boolean optionsActionMenuNeedsRecomputed = true;

    private JPopupMenu getPlusActionMenu(FIBBrowserElement element)
    {
         if (plusActionMenuNeedsRecomputed) {
            plusActionMenu = new JPopupMenu();
            if (logger.isLoggable(Level.FINE))
                logger.fine("Build plus menu");
            
        	for (FIBBrowserAction action : _addActions.get(element).keySet()) {
        		FIBBrowserActionListener actionListener = _addActions.get(element).get(action);
        		actionListener.setSelectedObject(_browserModel.getSelectedObject());
        		//actionListener.setSelectedObjects(_tableModel.getSelectedObjects());
                JMenuItem menuItem = new JMenuItem(getLocalized(action.getName()));
                menuItem.addActionListener(actionListener);
                plusActionMenu.add(menuItem);
                menuItem.setEnabled(actionListener.isActive(_browserModel.getSelectedObject()));
        	}
 
        	plusActionMenuNeedsRecomputed = false;
        }
        return plusActionMenu;
    }

    private JPopupMenu getMinusActionMenu(FIBBrowserElement element)
    {
    	if (minusActionMenuNeedsRecomputed) {
    		minusActionMenu = new JPopupMenu();

    		for (FIBBrowserAction action : _removeActions.get(element).keySet()) {
    			FIBBrowserActionListener actionListener = _removeActions.get(element).get(action);
    			actionListener.setSelectedObject(_browserModel.getSelectedObject());
    			//actionListener.setSelectedObjects(_tableModel.getSelectedObjects());
    			JMenuItem menuItem = new JMenuItem(getLocalized(action.getName()));
    			menuItem.addActionListener(actionListener);
    			minusActionMenu.add(menuItem);
    			menuItem.setEnabled(actionListener.isActive(_browserModel.getSelectedObject()));
    		}

    		minusActionMenuNeedsRecomputed = false;
    	}
    	return minusActionMenu;
    }

    private JPopupMenu getOptionActionMenu(FIBBrowserElement element)
    {
    	if (optionsActionMenuNeedsRecomputed) {
    		optionsActionMenu = new JPopupMenu();
    		if (logger.isLoggable(Level.FINE))
    			logger.fine("Build options menu");

    		for (FIBBrowserAction action : _otherActions.get(element).keySet()) {
    			FIBBrowserActionListener actionListener = _otherActions.get(element).get(action);
    			actionListener.setSelectedObject(_browserModel.getSelectedObject());
    			//actionListener.setSelectedObjects(_tableModel.getSelectedObjects());
    			JMenuItem menuItem = new JMenuItem(getLocalized(action.getName()));
    			menuItem.addActionListener(actionListener);
    			optionsActionMenu.add(menuItem);
    			menuItem.setEnabled(actionListener.isActive(_browserModel.getSelectedObject()));
    		}

        	optionsActionMenuNeedsRecomputed = false;
        }
        return optionsActionMenu;
    }

    
    private void initializeActions(FIBBrowser browser, FIBBrowserModel browserModel)
    {
      	_addActions = new Hashtable<FIBBrowserElement, Hashtable<FIBBrowserAction,FIBBrowserActionListener>>();
    	_removeActions = new Hashtable<FIBBrowserElement, Hashtable<FIBBrowserAction,FIBBrowserActionListener>>();
    	_otherActions = new Hashtable<FIBBrowserElement, Hashtable<FIBBrowserAction,FIBBrowserActionListener>>();
 
    	for (FIBBrowserElement element : browser.getElements()) {
    	
     		Hashtable<FIBBrowserAction, FIBBrowserActionListener> addActions = new Hashtable<FIBBrowserAction, FIBBrowserActionListener>();
    		Hashtable<FIBBrowserAction, FIBBrowserActionListener> removeActions = new Hashtable<FIBBrowserAction, FIBBrowserActionListener>();
    		Hashtable<FIBBrowserAction, FIBBrowserActionListener> otherActions = new Hashtable<FIBBrowserAction, FIBBrowserActionListener>();

    		for (Enumeration en = element.getActions().elements(); en.hasMoreElements();) {
    			FIBBrowserAction plAction = (FIBBrowserAction) en.nextElement();
    			FIBBrowserActionListener plActionListener = new FIBBrowserActionListener(plAction, browserModel,_widget.getController());
    			if (plActionListener.isAddAction()) {
    				addActions.put(plAction, plActionListener);
    			} else if (plActionListener.isRemoveAction()) {
    				removeActions.put(plAction, plActionListener);
    			} else if (plActionListener.isCustomAction()) {
    				otherActions.put(plAction, plActionListener);
    			}
    		}
    		
    		_addActions.put(element,addActions);
    		_removeActions.put(element,removeActions);
    		_otherActions.put(element,otherActions);
    	}
    }

    public void delete()
    {
    	if (_fibBrowser != null) {
    		for (FIBBrowserElement element : _fibBrowser.getElements()) {
    			if (_addActions != null && _addActions.get(element) != null) {
    				for (FIBBrowserAction a : _addActions.get(element).keySet()) {
    					_addActions.get(element).get(a).delete();
    				}
    			}
    			if (_removeActions != null && _removeActions.get(element) != null) {
    				for (FIBBrowserAction a : _removeActions.get(element).keySet()) {
    					_removeActions.get(element).get(a).delete();
    				}
    			}
    			if (_otherActions != null && _otherActions.get(element) != null) {
    				for (FIBBrowserAction a : _otherActions.get(element).keySet()) {
    					_otherActions.get(element).get(a).delete();
    				}
    			}
    		}
    	}
    	_widget = null;
		_fibBrowser = null;
		_browserModel = null;
	}

    protected void setFocusedObject(Object object)
    {
    	logger.fine("Footer: set focused object with "+object);
    	
    	if (object == null) {
    		handleSelectionCleared();
    		return;
    	}
    	
    	FIBBrowserElement element = _fibBrowser.elementForClass(object.getClass());
    	
    	//logger.info("Set model with "+model);
       	for (FIBBrowserAction action : _addActions.get(element).keySet()) {
    		FIBBrowserActionListener actionListener = _addActions.get(element).get(action);
    		actionListener.setModel(object);
       	}   	
       	for (FIBBrowserAction action : _removeActions.get(element).keySet()) {
    		FIBBrowserActionListener actionListener = _removeActions.get(element).get(action);
    		actionListener.setModel(object);
       	}   	
       	for (FIBBrowserAction action : _otherActions.get(element).keySet()) {
    		FIBBrowserActionListener actionListener = _otherActions.get(element).get(action);
    		actionListener.setModel(object);
       	}   	
       	handleSelectionChanged(element);
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

	private JPopupMenu filtersPopupMenu;
	
	   protected JPopupMenu getFiltersPopupMenu()
	    {
	        if (filtersPopupMenu == null) {
	        	filtersPopupMenu = makeFiltersPopupMenu();
	        }
	        for (Component menuItem : filtersPopupMenu.getComponents()) {
	        	if (menuItem instanceof FIBBrowserFilterMenuItem) {
	        		((FIBBrowserFilterMenuItem)menuItem).update();
	        	}
	        }
	        return filtersPopupMenu;
	    }

	   private boolean hasFilters()
	   {
	        for (FIBBrowserElement el : _browserModel.getElementTypes().keySet()) {
	        	if (el.getFiltered()) return true;
	        }
	        return false;
	   }
	   
	    private JPopupMenu makeFiltersPopupMenu()
	    {

	        JPopupMenu returned = new JPopupMenu() {
	            @Override
				public void setVisible(boolean b)
	            {
	            	if (b && !isVisible())
	            		addPopupClosers(getWindow(FIBBrowserWidgetFooter.this));
	            	else if (!b && isVisible())
	            		removePopupClosers(getWindow(FIBBrowserWidgetFooter.this));
	                super.setVisible(b);
	                if (!b)
	                    filtersButton.setIcon(FIBIconLibrary.BROWSER_FILTERS_ICON);
	            }

	            @Override
				public void menuSelectionChanged(boolean isIncluded)
	            {
	                super.menuSelectionChanged(true);
	            }
	        };

	        for (FIBBrowserElement el : _browserModel.getElementTypes().keySet()) {
	        	if (el.getFiltered()) {
	        		FIBBrowserElementType et =  _browserModel.getElementTypes().get(el);
	        		returned.add(new FIBBrowserFilterMenuItem(et));
	        	}
	        }
	        
	        returned.addMenuKeyListener(new MenuKeyListener() {

	            @Override
				public void menuKeyPressed(MenuKeyEvent e)
	            {
	                if (e.getKeyCode() == KeyEvent.VK_ESCAPE && filtersPopupMenu != null && filtersPopupMenu.isVisible()) {
	                    closeFiltersPopupMenu();
	                }
	            }

	            @Override
				public void menuKeyReleased(MenuKeyEvent e)
	            {
	            }

	            @Override
				public void menuKeyTyped(MenuKeyEvent e)
	            {
	            }

	        });
	        return returned;
	    }

	    protected void closeFiltersPopupMenu() 
	    {
	    	logger.info("closeFiltersPopupMenu()");
			if (filtersPopupMenu!=null && filtersPopupMenu.isVisible()) {
				filtersPopupMenu.setVisible(false);
			}
			filtersPopupMenu = null;
	    }

		/**
		 * Copied directly from BasicPopupMenuUI - PK 06-08-04
		 *
		 * @param c
		 *            componenet of which we want to find the owning window
		 * @return the window that is contins after plenty of leves the component c
		 */
		protected Window getWindow(Component c) 
		{
			Component w = c;
			while (!(w instanceof Window) && (w != null)) {
				w = w.getParent();
			}
			return (Window) w;
		}


	    /**
		 * Added mouselistners to each component of the root container c, exept this
		 * button, and the calendar popup, because mouseclicks in them are not
		 * supposed to clsoe the popup.
		 *
		 * @param c
		 *            the root container
		 */
		protected void addPopupClosers(Container c) 
		{
			if (c == getWindow(this) && c!=null) {
				if (logger.isLoggable(Level.FINE))
					logger.finer("addPopupClosers");
	            ((Window)c).addWindowListener(this);
			}
			if (c != filtersPopupMenu && c!=null) {
				c.addMouseListener(this);
				for (int i = 0; i < c.getComponents().length; i++)
					addPopupClosers((Container) c.getComponents()[i]);
			}
		}

		/**
		 * Added mouselistners to each component of the root container c, exept this
		 * button, and the calendar popup, because mouseclicks in them are not
		 * supposed to clsoe the popup.
		 *
		 * @param c
		 *            the root container
		 */
		protected void removePopupClosers(Container c) 
		{
			if (c instanceof Window) {
				if (logger.isLoggable(Level.FINE))
					logger.finer("removePopupClosers");
	           ((Window)c).removeWindowListener(this);
			}
			if (c != filtersPopupMenu && c != null) {
				c.removeMouseListener(this);
				for (int i = 0; i < c.getComponents().length; i++)
					removePopupClosers((Container) c.getComponents()[i]);
			}
		}

		@Override
		public void mouseClicked(MouseEvent e) {
			if (e.getSource()!=filtersPopupMenu && e.getSource()!=filtersButton)
				closeFiltersPopupMenu();
		}

		@Override
		public void mouseEntered(MouseEvent e) {}

		@Override
		public void mouseExited(MouseEvent e) {}

		@Override
		public void mousePressed(MouseEvent e) {}

		@Override
		public void mouseReleased(MouseEvent e) {}

		@Override
		public void windowActivated(WindowEvent e) {}

		@Override
		public void windowClosed(WindowEvent e) {}

		@Override
		public void windowClosing(WindowEvent e) {closeFiltersPopupMenu();}

		@Override
		public void windowDeactivated(WindowEvent e) {closeFiltersPopupMenu();}

		@Override
		public void windowDeiconified(WindowEvent e) {}

		@Override
		public void windowIconified(WindowEvent e) {closeFiltersPopupMenu();}

		@Override
		public void windowOpened(WindowEvent e) {}


}
