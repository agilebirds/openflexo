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

import java.awt.Component;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.openflexo.inspector.model.InspectorModel;
import org.openflexo.inspector.model.TabModel;
import org.openflexo.inspector.widget.DenaliWidget;
import org.openflexo.inspector.widget.InnerTabWidgetView;
import org.openflexo.localization.FlexoLocalization;

/**
 * Main view for the inspector
 *
 * @author bmangez, sguerin
 */
public class InspectorModelView extends JTabbedPane implements ChangeListener
{

    private static final Logger logger = Logger.getLogger(InspectorModelView.class.getPackage().getName());

    private Vector<TabModelView> tabViews;

    private Vector<TabModel> tabs;

    //private Vector<TabModel> tabsToHide;

    private Vector<InnerTabWidgetView> _widgets;

    private InspectableObject _inspectable;

    private InspectingWidget _inspectingWidget;

    private InspectorModel _model;

    public InspectorModelView(InspectorModel _model, InspectingWidget inspectingWidget)
    {
        super();
        setOpaque(false);
        _inspectingWidget = inspectingWidget;
        _widgets = new Vector<InnerTabWidgetView>();
        _extraTabs = new Vector<TabModel>();
        _extraTabViews = new Hashtable<TabModel, TabModelView>();
        //tabsToHide = new Vector<TabModel>();
        this._model = _model;
        build(_model, _inspectingWidget);
        addChangeListener(this);
    }

    public void setInspectedObject(InspectableObject inspectable)
    {
        if (_inspectable == null || !_inspectable.equals(inspectable)) {
            performObserverSwitch(inspectable);
            _inspectable = inspectable;
            refreshAllConditionals();
        } else {
            // Only update values
            updateFromModel();
        }
    }

    public void updateFromModel()
    {
    	/*setBackground(InspectorCst.BACK_COLOR);
    	System.err.println(getBackground());*/
    	for (Enumeration e = _widgets.elements(); e.hasMoreElements();) {
            InnerTabWidgetView widget = (InnerTabWidgetView) e.nextElement();
            if (widget instanceof DenaliWidget) {
                if (!((DenaliWidget)widget).widgetHasFocus()) {
                  	if (((DenaliWidget)widget).isWidgetVisible())
                  		((DenaliWidget)widget).updateWidgetFromModel();
                }
            } else
                widget.updateWidgetFromModel();
        }
        refreshAllConditionals();
    }


    private void refreshAllConditionals()
    {
        if (logger.isLoggable(Level.FINE))
            logger.fine("REFRESH conditionals");
        for (Enumeration en=tabViews.elements(); en.hasMoreElements();) {
            TabModelView next = (TabModelView)en.nextElement();
            next.valueChange(_inspectable);
        }
        refreshTabVisibility();
    }

    public void valueChange(Object newValue, DenaliWidget widget)
    {
        if (logger.isLoggable(Level.FINE))
            logger.fine("valueChange with "+newValue+" for "+widget);
        for (Enumeration en=tabViews.elements(); en.hasMoreElements();) {
            TabModelView next = (TabModelView)en.nextElement();
            next.valueChange(newValue,widget);
        }
        refreshTabVisibility();
   }

    private void refreshTabVisibility()
    {
    	Component selectComponent = getSelectedComponent();
    	int selectedIndex = getSelectedIndex();
    	String title = selectedIndex>-1?getTitleAt(selectedIndex):null;
    	boolean hasChanged = false;
        int count=0;
        try {
        	removeChangeListener(this);
			for (TabModelView view : tabViews) {
				boolean tabIsVisible = getController().isTabPanelVisible(view.getTabModel(), getInspectedObject());
				if (view.hasVisibleWidgets() && tabIsVisible) {
					if (view.getParent() == null) {
						try {
							add(view, Math.min(count, getTabCount()));
							hasChanged = true;
						} catch (IndexOutOfBoundsException e) {
							e.printStackTrace();
							// BMA : catch an exception that seems to be caused by a bug in the APPLE JVM implementation
							if (getComponentCount() > 0)
								setSelectedIndex(0);
							revalidate();
							repaint();
						}
					}
					count++;
				} else if (!view.hasVisibleWidgets() || !tabIsVisible) {
					try {
						if (view.getParent() != null) {
							remove(view);
							hasChanged = true;
						}
					} catch (IndexOutOfBoundsException e) {
						if (logger.isLoggable(Level.WARNING))
							logger.warning("Index Out Of Bounds Exception occured. Set level to FINE on logger named '" + logger.getName()
									+ "' to see stacktrace: " + e.getMessage());
						if (logger.isLoggable(Level.FINE))
							logger.log(Level.FINE, "Exception occured: " + e.getMessage(), e);
						if (getComponentCount() > 0)
							setSelectedIndex(0);
						revalidate();
						repaint();
					}
				}
			}
		} finally {
			addChangeListener(this);
		}
        // AJA : commented with approval of BMA to avoid a bug in the wysiwyg (kind of deadlock)
        // GPO : uncommented the next line because it is essential and logical since we potentially have added/removed components to/from
		// the tabbed pane
        if (hasChanged)
        	validate();
        boolean componentHasBeenSelected = false;
        if (selectedIndex>-1) {
        	if (selectComponent!=null && indexOfComponent(selectComponent)>-1) {
        		setSelectedComponent(selectComponent);
        		componentHasBeenSelected = true;
        	} else if (title!=null) {
        		for(int i=0;i<getTabCount() && !componentHasBeenSelected;i++) {
        			if(title.equals(getTitleAt(i))) {
        				setSelectedIndex(i);
        				componentHasBeenSelected = true;
        			}
        		}
        	}
        	if (!componentHasBeenSelected && selectedIndex>-1) {
        		setSelectedIndex(Math.min(getTabCount()-1, selectedIndex));
        		componentHasBeenSelected = true;
        	}
        }

    	/*boolean hasChanged = false;
		for (TabModelView view : tabViews) {
			if (getController().isTabPanelVisible(view.getTabModel(), getInspectedObject())) {
				if (!view.isVisible()) {
					view.setVisible(true);
					hasChanged = true;
				}
			}
			else {
				if (view.isVisible()) {
					view.setVisible(false);
					hasChanged = true;
				}
			}
		}
		if (hasChanged) {
			revalidate();
			repaint();
		}*/

		/*
        int count=0;
        try {
        	removeChangeListener(this);
			for (TabModelView view : tabViews) {
				if (view.hasVisibleWidgets() && !getTabsToHide().contains(view.getTabModel())) {
					if (view.getParent() == null) {
						try {
							add(view, Math.min(count, getTabCount()));
						} catch (IndexOutOfBoundsException e) {
							// BMA : catch an exception that seems to be caused by a bug in the APPLE JVM implementation
							if (getComponentCount() > 0)
								setSelectedIndex(0);
							revalidate();
							repaint();
						}
					}
					count++;
				} else if (!view.hasVisibleWidgets() || getTabsToHide().contains(view.getTabModel())) {
					try {
						if (view.getParent() != null)
							remove(view);
					} catch (IndexOutOfBoundsException e) {
						if (logger.isLoggable(Level.WARNING))
							logger.warning("Index Out Of Bounds Exception occured. Set level to FINE on logger named '" + logger.getName()
									+ "' to see stacktrace: " + e.getMessage());
						if (logger.isLoggable(Level.FINE))
							logger.log(Level.FINE, "Exception occured: " + e.getMessage(), e);
						if (getComponentCount() > 0)
							setSelectedIndex(0);
						revalidate();
						repaint();
					}
				}
			}
		} finally {
			addChangeListener(this);
		}
        // AJA : commented with approval of BMA to avoid a bug in the wysiwyg (kind of deadlock)
        // GPO : uncommented the next line because it is essential and logical since we potentially have added/removed components to/from
		// the tabbed pane
        validate();*/
    }

    public InspectableObject getInspectedObject()
    {
        return _inspectable;
    }

    public AbstractController getController()
    {
        return _inspectingWidget.getController();
    }

    private void build(InspectorModel inspectorModel, InspectingWidget inspectingWidget)
    {
        setName(FlexoLocalization.localizedForKey(inspectorModel.title,this));
        tabs = buildTabs(inspectorModel,inspectingWidget.getController());
        tabViews = new Vector<TabModelView>();
        Enumeration en = tabs.elements();
        while (en.hasMoreElements()) {
            TabModel temp = (TabModel) en.nextElement();
            TabModelView tabModelView = new TabModelView(temp, this, inspectingWidget.getController());
            tabViews.add(tabModelView);
            if (inspectingWidget instanceof InspectorTabbedPanel) {
                if (temp.name.equals(((InspectorTabbedPanel)inspectingWidget).getLastInspectedTabName())) {
                	((InspectorTabbedPanel)inspectingWidget).setNextFocusedTab(tabModelView);
                }
            }
            add(tabModelView);
        }
    }

    private Vector<TabModel> buildTabs(InspectorModel inspectorModel, AbstractController c)
    {
        Vector<TabModel> answer = new Vector<TabModel>();
        Vector<InspectorModel> inspectorHierarchy = createInspectorHierarchy(inspectorModel);
        Vector<Vector<TabModel>> vectorOfVectorOfTabs = createVectorOfVector(inspectorHierarchy);
        Enumeration<Vector<TabModel>> en = vectorOfVectorOfTabs.elements();
        while (en.hasMoreElements()) {
            TabModel tab = TabModel.createMergedModel(en.nextElement(), c);
            if (tab.getProperties().size() > 0) {
                answer.add(tab);
                tab.setInspectorModel(inspectorModel);
            }
        }
        return answer;
    }

    private Vector<Vector<TabModel>> createVectorOfVector(Vector inspectorHierarchy)
    {
        Vector<Vector<TabModel>> answer = new Vector<Vector<TabModel>>();
        Hashtable<String,Vector<TabModel>> allReadyStoredTabs = new Hashtable<String,Vector<TabModel>>();
        Enumeration en = inspectorHierarchy.elements();
        while (en.hasMoreElements()) {
            InspectorModel currentInspectorModel = (InspectorModel) en.nextElement();
            Iterator tabIter = currentInspectorModel.getTabs().values().iterator();
            while (tabIter.hasNext()) {
                TabModel currentTab = (TabModel) tabIter.next();
                if (allReadyStoredTabs.get(currentTab.name) == null) {
                    Vector<TabModel> v = new Vector<TabModel>();
                    v.add(currentTab);
                    allReadyStoredTabs.put(currentTab.name, v);
                } else {
                    Vector<TabModel> v = allReadyStoredTabs.get(currentTab.name);
                    v.add(currentTab);
                }
            }
        }
        int tabCount = allReadyStoredTabs.size();
        while (allReadyStoredTabs.size() > 0) {
            Enumeration en2 = allReadyStoredTabs.keys();
            String key = (String) en2.nextElement();
            Vector<TabModel> v = allReadyStoredTabs.remove(key);
            insertVectorInAnswer(v, answer);
        }
        if (answer.size() != tabCount) {
            if (logger.isLoggable(Level.WARNING))
                logger.warning("Error...missing a tab...");
        }
        return answer;
    }

    private void insertVectorInAnswer(Vector<TabModel> vToInsert, Vector<Vector<TabModel>> container)
    {
        if (container.size() == 0) {
            container.add(vToInsert);
        } else {
            int indexToInsert = vToInsert.elementAt(0).index.intValue();
            Enumeration<Vector<TabModel>> en = container.elements();
            boolean isInserted = false;
            while (!isInserted) {
            	Vector<TabModel> currentV = en.nextElement();
                int indexOfCurrent = currentV.elementAt(0).index.intValue();
                if (indexToInsert < indexOfCurrent) {
                    container.add(container.indexOf(currentV), vToInsert);
                    isInserted = true;
                } else {
                    if (!en.hasMoreElements()) {
                        container.add(vToInsert);
                        isInserted = true;
                    }
                }
            }
        }
    }

    private Vector<InspectorModel> createInspectorHierarchy(InspectorModel startingModel)
    {
        Vector<InspectorModel> answer = new Vector<InspectorModel>();
        InspectorModel fatherModel = startingModel;
        while (fatherModel != null) {
            answer.add(0, fatherModel);
            fatherModel = fatherModel.getSuperInspector();
        }
        return answer;
    }

    private void performObserverSwitch(InspectableObject newInspectable)
    {
        Enumeration en = _widgets.elements();
        while (en.hasMoreElements()) {
            ((InnerTabWidgetView) en.nextElement()).switchObserved(newInspectable);
        }
        _inspectingWidget.notifiedInspectedObjectChange(newInspectable);
    }

    public void addToWidgets(InnerTabWidgetView widget)
    {
        _widgets.add(widget);
    }

    public void removeFromWidgets(InnerTabWidgetView widget)
    {
        _widgets.remove(widget);
    }

    @Override
	public void stateChanged(ChangeEvent e)
    {
    	if (ignoreStateChanged) return;
        updateFromModel();
        if (_inspectingWidget!=null && getSelectedComponent()!=null && getSelectedComponent().getTabModel()!=null)
        	_inspectingWidget.notifiedActiveTabChange(getSelectedComponent().getTabModel().name);
        //logger.info("state changed with "+e);
    }

    @Override
	public TabModelView getSelectedComponent()
    {
    	return (TabModelView)super.getSelectedComponent();
    }

    public TabModelView getTabModelViewForName(String name)
    {
    	for (TabModelView tabView : tabViews) {
    		if (tabView.getTabModel().name.equals(name)) return tabView;
    	}
    	return null;
    }

   /* private String lastInspectedPropertyName;

    private String lastInspectedTabName;

     public void widgetGetFocus(DenaliWidget widget)
    {
        lastInspectedPropertyName = widget.getObservedPropertyName();
        lastInspectedTabName = widget.getObservedTabName();
    }

    public String getLastInspectedPropertyName()
    {
        return lastInspectedPropertyName;
    }

    public void setLastInspectedPropertyName(String lstInspectedPropertyName)
    {
        this.lastInspectedPropertyName = lstInspectedPropertyName;
    }

    public String getLastInspectedTabName()
    {
        return lastInspectedTabName;
    }

    public void setLastInspectedTabName(String lstInspectedTabName)
    {
        this.lastInspectedTabName = lstInspectedTabName;
    }
*/

	public InspectingWidget getInspectingWidget() {
		return _inspectingWidget;
	}

	/*public Vector<TabModel> getTabsToHide() {
		return tabsToHide;
	}

	public void setTabsToHide(Vector<TabModel> tabsToHide) {
		this.tabsToHide = tabsToHide;
	}

	public void addToTabsToHide(String tabName) {
		for (TabModel tab: tabs) {
			if (tabName.equals(tab.name)) {
				tabsToHide.add(tab);
				refreshTabVisibility();
				return;
			}
		}
	}

	public void removeFromTabsToHide(String tabName) {
		for (TabModel tab: tabsToHide) {
			if (tabName.equals(tab.name)) {
				tabsToHide.remove(tab);
				refreshTabVisibility();
				return;
			}
		}
	}*/

	public int getTabsNb()
	{
		return tabViews.size();
	}

    public TabModelView getTabAtIndex(int i)
    {
    	return tabViews.get(i);
    }

	public Vector<TabModelView> getTabViews()
	{
		return tabViews;
	}
	
	private Vector<TabModel> _extraTabs;
	private Hashtable<TabModel,TabModelView> _extraTabViews;
	
	boolean ignoreStateChanged = false;
	
	public void updateExtraTabs(Vector<TabModel> extraTabs)
	{
		if (extraTabs == null) return;
		
		boolean tabChanged = false;
		
		ignoreStateChanged = true;
		
		Vector<TabModel> extraTabsToRemove = new Vector<TabModel>();
		extraTabsToRemove.addAll(_extraTabs);
		for (TabModel t : extraTabs) {
			if (extraTabsToRemove.contains(t)) {
				// Already contained, do nothing
				extraTabsToRemove.remove(t);
			}
			else {
				logger.fine("Adding tab "+t.name);
				TabModelView tabView = retrieveExtraTabModelView(t);
				tabView.registerWidgets();
				insertTab(t.name, null, tabView, null, t.index);				
				_extraTabs.add(t);
				tabChanged = true;
			}
		}
		for (TabModel t : extraTabsToRemove) {
			logger.fine("Removing tab "+t.name);
			TabModelView tabView = retrieveExtraTabModelView(t);
			tabView.unregisterWidgets();
			remove(tabView);
			_extraTabs.remove(t);
			/*for (Enumeration<InnerTabWidgetView> e = retrieveExtraTabModelView(t).getAllWidgets(); e.hasMoreElements();) {
				InnerTabWidgetView w = e.nextElement();
				if (w instanceof DenaliWidget) {
					DenaliWidget widget = (DenaliWidget)w;
					if (widget.getModel() != null) {
						//System.out.println("JE vire l'observer");
						widget.getModel().deleteInspectorObserver(widget);
					}
				}
			}*/
			tabChanged = true;
		}
		
		if (tabChanged) {
			SwingUtilities.invokeLater(new Runnable() {			
				@Override
				public void run() {
					setSelectedIndex(0);
				}
			});
 		}
	
		ignoreStateChanged = false;

		/*_extraTabs = extraTabs;
		if (extraTabs != null) {
			for (TabModel t : extraTabs) {
				System.out.println("Je rajoute un tab "+t.name);
				insertTab(t.name, null, retrieveExtraTabModelView(t), null, t.index);

			}
		}*/
		
	}

	/*public void removeExtraTabs()
	{
		System.out.println("Je vire les tabs en plus");
		if (_extraTabs != null) {
			for (TabModel t : _extraTabs) {
				System.out.println("Je rajoute un tab "+t.name);
				remove(retrieveExtraTabModelView(t));
			}
		}
	}*/
	
	private TabModelView retrieveExtraTabModelView(TabModel tab)
	{
		TabModelView returned = _extraTabViews.get(tab);
		if (returned == null) {
			//System.out.println("Je cree la TabModelView pour "+tab.name);
			returned = new TabModelView(tab, this, _inspectingWidget.getController());
			_extraTabViews.put(tab, returned);
		}
		return returned;
	}
	
}
