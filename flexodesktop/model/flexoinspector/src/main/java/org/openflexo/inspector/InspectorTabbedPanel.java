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
package org.openflexo.inspector;

import java.awt.BorderLayout;
import java.awt.Component;
import java.util.Hashtable;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.FocusManager;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import org.openflexo.inspector.model.InspectorModel;
import org.openflexo.inspector.selection.EmptySelection;
import org.openflexo.inspector.selection.InspectorSelection;
import org.openflexo.inspector.selection.MultipleSelection;
import org.openflexo.inspector.selection.UniqueSelection;
import org.openflexo.inspector.widget.DenaliWidget;


public class InspectorTabbedPanel extends JPanel implements InspectingWidget
{
	private static final Logger logger = Logger.getLogger(InspectorTabbedPanel.class.getPackage().getName());

	private JLabel _nothingToInspectLabel;
	private JLabel _multipleSelectionLabel;
	private JLabel _nonApplicableLabel;

	private JComponent currentPane;
	public InspectorModelView currentTabPanel;
    private InspectorController _controller;

	public InspectorTabbedPanel(InspectorController controller)
	{
		super(new BorderLayout());
		_controller = controller;
		_inspectorPanels = new Hashtable<String,InspectorModelView>();
		_nothingToInspectLabel = new JLabel(getController().getNothingToInspectLabel(),SwingConstants.CENTER);
		_multipleSelectionLabel = new JLabel(getController().getMultipleSelectionLabel(),SwingConstants.CENTER);
		_nonApplicableLabel = new JLabel(getController().getNonApplicableLabel(),SwingConstants.CENTER);
		setTabPanelToNone();
	}

	/*@Override
	public void remove(Component comp)
	{
		super.remove(comp);
		if (comp instanceof InspectorModelView) {
			((InspectorModelView)comp).removeExtraTabs();
		}
		else if (comp instanceof JScrollPane) {
			if (((JScrollPane)comp).getViewport().getView() instanceof InspectorModelView) {
				((InspectorModelView)((JScrollPane)comp).getViewport().getView()).removeExtraTabs();
			}
		}
	}*/
	
	protected void setTabPanel(InspectorModelView tabPanel)
    {
        Component c = FocusManager.getCurrentManager().getFocusOwner();
        if (c != null && (c instanceof JLabel || c instanceof JTextField)) {
        }
        if (tabPanel != null) {
        	JScrollPane _currentScrollPane;
        	if (currentPane != null) {
        		remove(currentPane);
        	}
        	// If there is only one tab, directely insert this tab instead of JTabbedPane
        	// GPO: The code below DOES NOT work (and causes issues in JTabbedPane in Java5)
        	// See org.openflexo.inspector.InspectorModelView.removeTabAt(int)
        	// See also Bug 1006487
        	/*if (tabPanel.getTabsNb() == 1) {
          		_currentScrollPane = getScrollPane(tabPanel.getTabAtIndex(0));
        	}
        	else {*/
          		_currentScrollPane = getScrollPane(tabPanel);
        	//}
        	currentPane = _currentScrollPane;
        	add(_currentScrollPane, BorderLayout.CENTER);
        	validate();
        	repaint();
        	currentTabPanel = tabPanel;
        }
    }

    protected void setTabPanelToNone()
    {
        if (currentPane != null) {
            remove(currentPane);
        }
        currentPane = _nothingToInspectLabel;
        add(currentPane, BorderLayout.CENTER);
        validate();
        repaint();
        currentTabPanel = null;
    }

    protected void setTabPanelToMultiple()
    {
        if (currentPane != null) {
            remove(currentPane);
        }
        currentPane = _multipleSelectionLabel;
        add(currentPane, BorderLayout.CENTER);
        validate();
        repaint();
        currentTabPanel = null;
    }

    protected void setTabPanelToNonApplicable()
    {
        if (currentPane != null) {
            remove(currentPane);
        }
        currentPane = _nonApplicableLabel;
        add(currentPane, BorderLayout.CENTER);
        validate();
        repaint();
        currentTabPanel = null;
    }

    private JScrollPane getScrollPane(JComponent content)
    {
        JScrollPane answer = new JScrollPane(content);
        //content.setPreferredSize(new Dimension(getSize().height - 40, getSize().width - 20));
        return answer;
    }


	@Override
	public void newSelection(InspectorSelection selection) {
		if (logger.isLoggable(Level.FINE))
			logger.fine("newSelection() with " + selection);
		InspectorSelection inspectorSelection = selection;
		if (inspectorSelection instanceof EmptySelection) {
			setTabPanelToNone();
		} else if (inspectorSelection instanceof MultipleSelection) {
			setTabPanelToMultiple();
		} else if (inspectorSelection instanceof UniqueSelection) {
			InspectableObject inspectedObject = ((UniqueSelection) inspectorSelection).getInspectedObject();
			//logger.info("Inspect "+inspectedObject);
			String inspectorName = _controller.getInspectorName(inspectedObject, ((UniqueSelection) inspectorSelection).getInspectionContext());
			if (inspectorName != null) {
				nextFocusedWidget = null;
				nextFocusedTab = null;
				InspectorModelView inspectorModelView = getInspectorTabPanelForInspectable(inspectedObject, inspectorName);
				setTabPanel(inspectorModelView);
				if (inspectorModelView != null/* && inspectorModelView.getTabCount() > 1*/) {
				if (activeTabName != null)
					nextFocusedTab = inspectorModelView.getTabModelViewForName(activeTabName);
				if (nextFocusedTab != null && currentTabPanel.indexOfComponent(nextFocusedTab) > -1) {
					currentTabPanel.setSelectedComponent(nextFocusedTab);
					if (nextFocusedWidget != null) {
						nextFocusedWidget.getDynamicComponent().grabFocus();
					}
				}
				if (currentTabPanel != null)
					nextFocusedTab = currentTabPanel.getSelectedComponent();
				}
			}
			else {
				setTabPanelToNonApplicable();
			}
		}
	}

    private Hashtable<String,InspectorModelView> _inspectorPanels;

    private InspectorModelView getInspectorTabPanelForInspectable(InspectableObject inspectable, String inspectorName)
    {
        InspectorModelView answer = getInspectorTabPanelForName(inspectorName);
        if (answer == null)
        	return null;
        answer.updateExtraTabs(inspectable.inspectionExtraTabs());
        answer.setInspectedObject(inspectable);
        // answer.doLayout();
        answer.updateUI();
        return answer;
    }

	/**
	 * @param inspectorName
	 * @return
	 */
	private InspectorModelView getInspectorTabPanelForName(String inspectorName) {
		InspectorModelView answer = _inspectorPanels.get(inspectorName);
        if (answer == null) {
            InspectorModel inspectorModel = getController().getInspectorModel(inspectorName);
            if (inspectorModel != null) {
                answer = new InspectorModelView(inspectorModel, this);
                _inspectorPanels.put(inspectorName, answer);
            } else {
                return null;
            }
        }
		return answer;
	}

	@Override
	public InspectorController getController() {
		return _controller;
	}

    private TabModelView nextFocusedTab;

    private String activeTabName;

    private DenaliWidget nextFocusedWidget;

    private String lastInspectedTabName;

    private String lastInspectedPropertyName;

    /**
     * @param widget
     */
    public void widgetGetFocus(DenaliWidget widget)
    {
        lastInspectedPropertyName = widget.getObservedPropertyName();
        lastInspectedTabName = widget.getObservedTabName();
    }

    public TabModelView getNextFocusedTab()
    {
        return nextFocusedTab;
    }

    public void setNextFocusedTab(TabModelView nxtFocusedTab)
    {
        this.nextFocusedTab = nxtFocusedTab;
    }

    public DenaliWidget getNextFocusedWidget()
    {
        return nextFocusedWidget;
    }

    public void setNextFocusedWidget(DenaliWidget nxtFocusedWidget)
    {
        this.nextFocusedWidget = nxtFocusedWidget;
    }

    public String getLastInspectedTabName()
    {
        return lastInspectedTabName;
    }

    public void setLastInspectedTabName(String lstInspectedTabName)
    {
        this.lastInspectedTabName = lstInspectedTabName;
    }

    public String getLastInspectedPropertyName()
    {
        return lastInspectedPropertyName;
    }

    public void setLastInspectedPropertyName(String lstInspectedPropertyName)
    {
        this.lastInspectedPropertyName = lstInspectedPropertyName;
    }

	@Override
	public void notifiedInspectedObjectChange(InspectableObject newInspectedObject)
	{
		getController().notifiedInspectedObjectChange(newInspectedObject);
	}

	@Override
	public void notifiedActiveTabChange(String newActiveTabName)
	{
		activeTabName = newActiveTabName;
		if (logger.isLoggable(Level.FINE)) logger.fine("SET activeTabName="+newActiveTabName);
 	}

	private Vector<InspectorModel> getAllExtendingInspectors(String inspectorName) {
		InspectorModel model = getController().getInspectorModel(inspectorName);
		if (model!=null) {
			Vector<InspectorModel> models = new Vector<InspectorModel>();
			addAllExtendingModels(model,models);
			return models;
		} else {
			if (logger.isLoggable(Level.WARNING))
				logger.warning("Inspector "+inspectorName+" could not be found");
			return null;
		}
	}

	private void addAllExtendingModels(InspectorModel model, Vector<InspectorModel> models) {
		models.add(model);
		for(InspectorModel i:model.extendingInspectors) {
			addAllExtendingModels(i, models);
		}
	}

	/*public void showTabWithNameInInspectorNamed(String tabName, String inspectorName) {
		Vector<InspectorModel> v = getAllExtendingInspectors(inspectorName);
		if (v==null)
			return;
		for(InspectorModel model: v) {
			InspectorModelView view = getInspectorTabPanelForName(model.inspectorFile.getName());
			if (view!=null) {
				view.removeFromTabsToHide(tabName);
			}
		}
	}

	public void hideTabWithNameInInspectorNamed(String tabName, String inspectorName) {
		Vector<InspectorModel> v = getAllExtendingInspectors(inspectorName);
		if (v==null)
			return;
		for(InspectorModel model: v) {
			InspectorModelView view = getInspectorTabPanelForName(model.inspectorFile.getName());
			if (view!=null) {
				view.addToTabsToHide(tabName);
			}
		}
	}*/

	public void refresh()
	{
		if (currentTabPanel != null)
			currentTabPanel.updateFromModel();
	}
}
