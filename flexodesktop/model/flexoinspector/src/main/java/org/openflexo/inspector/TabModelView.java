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

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.Box;
import javax.swing.JPanel;


import org.openflexo.inspector.model.GroupModel;
import org.openflexo.inspector.model.InnerTabWidget;
import org.openflexo.inspector.model.PropertyModel;
import org.openflexo.inspector.model.TabModel;
import org.openflexo.inspector.widget.CustomWidget;
import org.openflexo.inspector.widget.DenaliWidget;
import org.openflexo.inspector.widget.InnerTabWidgetView;
import org.openflexo.inspector.widget.LineWidget;
import org.openflexo.inspector.widget.DenaliWidget.WidgetLayout;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.toolbox.ToolBox;

/**
 * View representing a tab in th inspector
 *
 * @author bmangez,sguerin
 */
public class TabModelView extends JPanel
{

    private static final Logger logger = Logger.getLogger(DenaliWidget.class.getPackage().getName());

    // ==========================================================================
    // ============================= Variables ==================================
    // ==========================================================================

    private Vector<InnerTabWidgetView> _widgets;
    private Vector<InnerTabWidgetView> _visibleWidgets;
    private Vector<InnerTabWidgetView> _invisibleWidgets;
    private TabModel _tabModel;
    private InspectorModelView _inspectorModelView;

    private AbstractController _controller;

   // ==========================================================================
    // ============================= Constructor ================================
    // ==========================================================================

    public TabModelView(TabModel model, InspectorModelView inspectorModelView, AbstractController controller)
    {
        super();
        _controller = controller;
        _tabModel = model;
        _widgets = new Vector<InnerTabWidgetView>();
        _visibleWidgets = new Vector<InnerTabWidgetView>();
        _invisibleWidgets = new Vector<InnerTabWidgetView>();
        _inspectorModelView = inspectorModelView;
        setName(FlexoLocalization.localizedForKey(model.name,this));
        if (ToolBox.getPLATFORM()!=ToolBox.MACOS)
        	setBackground(Color.WHITE);
        init(model, inspectorModelView);
    }

    public DenaliWidget getInspectorWidgetFor (String propertyName)
    {
    	for (InnerTabWidgetView widget : _widgets) {
    		if (widget instanceof DenaliWidget && ((DenaliWidget)widget).getPropertyModel().name.equals(propertyName))
    			return (DenaliWidget)widget;
    	}
    	return null;
    }

    /**
     * Overrides getPreferredSize
     * @see javax.swing.JComponent#getPreferredSize()
     */
    @Override
    public Dimension getPreferredSize()
    {
        if (_inspectorModelView==null || _inspectorModelView.getSelectedComponent()==this)
            return super.getPreferredSize();
        else
            return new Dimension(0,0);
    }

    private void init(TabModel model, InspectorModelView inspectorModelView)
    {
        GridBagLayout gridbag;

        gridbag = new GridBagLayout();
        setLayout(gridbag);

        Iterator<InnerTabWidget> it = orderProperties(model).iterator();
        int i = 0;

        while (it.hasNext()) {
            InnerTabWidget propModel = it.next();

            InnerTabWidgetView widget = DenaliWidget.instance(propModel,_controller);
            widget.setTabModelView(this,i++);

            addField(gridbag,widget,widget.shouldExpandHorizontally(),widget.shouldExpandVertically());

            _visibleWidgets.add(widget);
            ((Component)widget).setVisible(true);
            _widgets.add(widget);

            /*if (inspectorModelView != null) {
                inspectorModelView.addToWidgets(widget);
            }*/

            if (logger.isLoggable(Level.FINE)) logger.fine("Widget: "+widget.getXMLModel().getIndex()+" "+widget.getClass().getSimpleName()+":"+widget.getName());
        }

        registerWidgets();
        
        // Put a glue even if foundComponentAllowingVerticalExpansion is true,
        // in case of component allowing expansion is hidden because of a "depends"
        //if (!foundComponentAllowingVerticalExpansion) {
            GridBagConstraints c = new GridBagConstraints();
            c.fill = GridBagConstraints.BOTH;
            c.weightx = 1.0;
            c.weighty = 0.1;
            c.gridwidth = GridBagConstraints.REMAINDER;
            c.gridheight = GridBagConstraints.REMAINDER;
            c.anchor = GridBagConstraints.CENTER;
            Component glue = Box.createGlue();
            gridbag.setConstraints(glue, c);
            add(glue);
       //}

        validate();
    }

    private boolean _widgetsAreRegisteredInInspectorModelView = false;
    
    protected void registerWidgets()
    {
    	if (!_widgetsAreRegisteredInInspectorModelView && _inspectorModelView != null) {
    		for (InnerTabWidgetView widget : _widgets) {
    			_inspectorModelView.addToWidgets(widget);
    		}
    		_widgetsAreRegisteredInInspectorModelView = true;
    		//System.out.println("Registered widgets in TabModelView");
    	}
    }

    protected void unregisterWidgets()
    {
    	if (_widgetsAreRegisteredInInspectorModelView && _inspectorModelView != null) {
    		for (InnerTabWidgetView widget : _widgets) {
    			_inspectorModelView.removeFromWidgets(widget);
    		}
       		_widgetsAreRegisteredInInspectorModelView = false;
    		//System.out.println("Un-registered widgets in TabModelView");
    	}
   }
    
   private void addField(GridBagLayout gridbag, InnerTabWidgetView widget, boolean expandX, boolean expandY)
    {
	   GridBagConstraints c = new GridBagConstraints();
	   c.insets = new Insets(3, 3, 3, 3);
	   if (widget.displayLabel()) {
		   if (widget.getWidgetLayout() == WidgetLayout.LABEL_NEXTTO_WIDGET_LAYOUT) {
			   c.fill = GridBagConstraints.NONE;
			   c.weightx = 0; //1.0;
			   c.gridwidth = GridBagConstraints.RELATIVE;
			   c.anchor = GridBagConstraints.NORTHEAST;
		   }
		   else if (widget.getWidgetLayout() == WidgetLayout.LABEL_ABOVE_WIDGET_LAYOUT) {
			   c.fill = GridBagConstraints.NONE;
			   c.weightx = 1.0;
			   c.gridwidth = GridBagConstraints.REMAINDER;
			   c.anchor = GridBagConstraints.CENTER;
		   }
		   gridbag.setConstraints(widget.getLabel(), c);
		   add(widget.getLabel());
	   }
	   if (expandX) {
		   c.fill = GridBagConstraints.BOTH;
		   c.anchor = GridBagConstraints.CENTER;
		   if (expandY) {
			   c.weighty = 1.0;
			   // c.gridheight = GridBagConstraints.RELATIVE;
		   }
	   } else {
            c.fill = GridBagConstraints.NONE;
            if (!widget.displayLabel()) {
                //GPO: if there are no label and the widget does not expand horizontally, then we center it on its line
                c.anchor = GridBagConstraints.CENTER;
            } else
                c.anchor = GridBagConstraints.WEST;
        }
	   c.weightx = 1.0; //2.0;
	   c.gridwidth = GridBagConstraints.REMAINDER;
	   gridbag.setConstraints(widget.getDynamicComponent(), c);
	   add(widget.getDynamicComponent());
    }



    // OLD IMPLEMENTATION WITH VERTICAL LAYOUT
    /*private void init(TabModel model, InspectorModelView inspectorModelView)
    {
        Iterator it = model.getProperties().values().iterator();
        biggestLabelLength = (biggestLabelLength + 3) * 8;
        setLayout(new VerticalLayout(1, 1, 1));

       it = orderProperties(model.getProperties().values().iterator()).iterator();
        int i = 0;

        while (it.hasNext()) {
            PropertyModel propModel = (PropertyModel) it.next();


            DenaliWidget widget = DenaliWidget.instance(propModel,_controller);
            widget.setTabModelView(this,i++);

            FontMetrics fm = widget.getLabel().getFontMetrics(widget.getLabel().getFont());
            biggestLabelLength = Math.max(biggestLabelLength, fm.stringWidth(widget.getLabel().getText()));

            add(widgetPanel(widget));
            _visibleWidgets.add(widget);

            _widgets.add(widget);

            if (inspectorModelView != null) {
                inspectorModelView.addToWidgets(widget);
            }
        }
        for (Enumeration e = _visibleWidgets.elements(); e.hasMoreElements();) {
            DenaliWidget widget = (DenaliWidget) e.nextElement();
            widget.getLabel().setPreferredSize(new Dimension(biggestLabelLength, 24));
        }

        validate();
    }*/

    /*public static JPanel widgetPanel(DenaliWidget widget)
    {
    		if (widget.getWidgetLayout()==DenaliWidget.LABEL_ABOVE_WIDGET_LAYOUT){
    			if (widget instanceof PropertyListWidget) {
    				return new PropertyListWidgetPanel((PropertyListWidget) widget);
    			}
    			else return new SpanWidgetPanel(widget);
    		}
    		else {
            return new WidgetPanel(widget);
        }
    }*/

    private Vector<InnerTabWidget> orderProperties(TabModel model)
    {
        Vector<InnerTabWidget> answer = new Vector<InnerTabWidget>();
        Iterator<PropertyModel> it=model.getProperties().values().iterator();
        while (it.hasNext()) {
            PropertyModel propModel = it.next();
            if (answer.size() == 0) {
                answer.add(propModel);
            } else {
                Enumeration en = answer.elements();
                boolean notInserted = true;
                int propIndex = propModel.getIndex();
                while (en.hasMoreElements() && notInserted) {
                    PropertyModel curProp = (PropertyModel) en.nextElement();
                    if (curProp.getIndex() > propIndex) {
                        answer.add(answer.indexOf(curProp), propModel);
                        notInserted = false;
                    }
                }
                if (notInserted) {
                    answer.add(propModel);
                }
            }
        }
        Iterator<GroupModel> i=model.getGroups().values().iterator();
        while (i.hasNext()) {
            GroupModel lineModel = i.next();
            if (answer.size() == 0) {
                answer.add(lineModel);
            } else {
                Enumeration<InnerTabWidget> en = answer.elements();
                boolean notInserted = true;
                int lineIndex = lineModel.getIndex();
                while (en.hasMoreElements() && notInserted) {
                    InnerTabWidget curProp = en.nextElement();
                    if (curProp.getIndex() > lineIndex) {
                        answer.add(answer.indexOf(curProp), lineModel);
                        notInserted = false;
                    }
                }
                if (notInserted) {
                    answer.add(lineModel);
                }
            }
        }
        return answer;
    }

    //public int biggestLabelLength = 0;

    public AbstractController getController()
    {
        return _controller;
    }

    /**
     * @param newValue
     * @param widget
     */
    public void valueChange(InspectableObject object)
    {
        if (logger.isLoggable(Level.FINE))
            logger.fine ("valueChange() in TabModelView for inspectable "+object);
        Enumeration<InnerTabWidgetView> en = _visibleWidgets.elements();
        DenaliWidget cur = null;
        Vector<DenaliWidget> widgetsToHide = new Vector<DenaliWidget>();
        Vector<DenaliWidget> widgetsToShow = new Vector<DenaliWidget>();
        while (en.hasMoreElements()) {
            InnerTabWidgetView inner = en.nextElement();
            if (inner instanceof DenaliWidget)
                cur = (DenaliWidget)inner;
            else if (inner instanceof LineWidget) {
                ((LineWidget)inner).valueChange(object);
                continue;
            } else {
                if (logger.isLoggable(Level.SEVERE))
                    logger.severe("Unknown class "+inner.getClass().getSimpleName());
                continue;
            }
            boolean isStillVisible = cur.isVisible(object);
            if (!isStillVisible) {
                widgetsToHide.add(cur);
            }
            //logger.info ("WAS visible: "+cur.getObservedPropertyName()+" still visible "+isStillVisible);
        }
        en = _invisibleWidgets.elements();
        while (en.hasMoreElements()) {
            InnerTabWidgetView inner = en.nextElement();
            if (inner instanceof DenaliWidget)
                cur = (DenaliWidget)inner;
            else
                continue;
            boolean isStillInvisible = !cur.isVisible(object);
            if (!isStillInvisible) {
                widgetsToShow.add(cur);
            }
         //   logger.info ("WAS invisible: "+cur.getObservedPropertyName()+" still invisible "+isStillInvisible);
        }
        if (widgetsToHide.size() > 0) {
            processToHiding(widgetsToHide);
        }
        if (widgetsToShow.size() > 0) {
            processToShowing(widgetsToShow);
        }
        validate();
        repaint();
    }

    /**
     * @param newValue
     * @param widget
     */
    public void valueChange(Object newValue, DenaliWidget widget)
    {
        //System.out.println ("valueChange() with "+newValue+" for "+widget.getObservedPropertyName());
        if (logger.isLoggable(Level.FINE))
            logger.fine ("valueChange() in TabModelView for property "+widget.getObservedPropertyName()+" which receive "+newValue);
        Enumeration<InnerTabWidgetView> en = _visibleWidgets.elements();
        DenaliWidget cur = null;
        Vector<DenaliWidget> widgetsToHide = new Vector<DenaliWidget>();
        Vector<DenaliWidget> widgetsToShow = new Vector<DenaliWidget>();
        while (en.hasMoreElements()) {
            InnerTabWidgetView inner = en.nextElement();
            if (inner instanceof DenaliWidget)
                cur = (DenaliWidget)inner;
            else if (inner instanceof LineWidget) {
                ((LineWidget)inner).valueChange(newValue,widget);
                continue;
            } else {
                if (logger.isLoggable(Level.SEVERE))
                    logger.severe("Unknown class "+inner.getClass().getSimpleName());
                continue;
            }
            boolean isStillVisible = cur.isStillVisible(newValue, widget.getObservedPropertyName());
            if (!isStillVisible) {
                widgetsToHide.add(cur);
            }
            //System.out.println (""+cur.getObservedPropertyName()+": dependsOfProperty(widget)="+cur.dependsOfProperty(widget));
            if (cur.dependsOfProperty(widget)) {
            	if (cur.isWidgetVisible())
            		cur.updateWidgetFromModel();
                if (cur instanceof CustomWidget) {
                    ((CustomWidget)cur).performModelUpdating();
                }
            }
            //logger.info ("WAS visible: "+cur.getObservedPropertyName()+" still visible "+isStillVisible);
        }
        en = _invisibleWidgets.elements();
        while (en.hasMoreElements()) {
            InnerTabWidgetView inner = en.nextElement();
            if (inner instanceof DenaliWidget)
                cur = (DenaliWidget)inner;
            else
                continue;
            boolean isStillInvisible = cur.isStillInvisible(newValue, widget.getObservedPropertyName());
            if (!isStillInvisible) {
                widgetsToShow.add(cur);
            }
         //   logger.info ("WAS invisible: "+cur.getObservedPropertyName()+" still invisible "+isStillInvisible);
        }
        if (widgetsToHide.size() > 0) {
            processToHiding(widgetsToHide);
        }
        if (widgetsToShow.size() > 0) {
            processToShowing(widgetsToShow);
        }
        // AJA : changed updateUI to revalidate repaint;
        revalidate();
        repaint();
    }

    private void processToHiding(Vector<DenaliWidget> widgetsToHide)
    {
        if (logger.isLoggable(Level.FINE))
            logger.fine("processToHiding() for "+_tabModel.name);
        DenaliWidget cur = null;
        Enumeration<DenaliWidget> en = widgetsToHide.elements();
        while (en.hasMoreElements()) {
            cur = en.nextElement();
            if (logger.isLoggable(Level.FINE))
                logger.fine("try to hide "+cur.getObservedPropertyName()+" at index: "+cur.getIndexInTab());
            for (int i = 0; i < getComponentCount(); i++) {
            	if (getComponent(i) == cur.getDynamicComponent()) {
                    if (cur.getLabel() != null) cur.getLabel().setVisible(false);
                    getComponent(i).setVisible(false);
                    _visibleWidgets.remove(cur);
                    _invisibleWidgets.add(cur);
                    if (logger.isLoggable(Level.FINE))
                        logger.fine ("HIDE: "+cur.getObservedPropertyName());
                }
            }
        }
    }

    private void processToShowing(Vector<DenaliWidget> widgetsToShow)
    {
        if (logger.isLoggable(Level.FINE))
            logger.fine("processToShowing() for "+_tabModel.name);
        DenaliWidget cur = null;
        Enumeration<DenaliWidget> en = widgetsToShow.elements();
        while (en.hasMoreElements()) {
            cur = en.nextElement();
            cur.performUpdate();
            if (logger.isLoggable(Level.FINE))
                logger.fine("try to show "+cur.getObservedPropertyName()+" at index: "+cur.getIndexInTab());
            cur.setTabModelView(this);
            cur.getDynamicComponent().setVisible(true);
            if (cur.getLabel() != null) cur.getLabel().setVisible(true);
            _visibleWidgets.add(cur);
            _invisibleWidgets.remove(cur);
            if (logger.isLoggable(Level.FINE))
                logger.fine ("SHOW: "+cur.getObservedPropertyName());
        }
    }


    /**
     * Must be called outside the context of full-inspector
     *
     * @param newInspectable
     */
    public void performObserverSwitch(InspectableObject newInspectable)
    {
    	Enumeration<InnerTabWidgetView> en = _widgets.elements();
    	while (en.hasMoreElements()) {
    		(en.nextElement()).switchObserved(newInspectable);
    	}
    }

    public InspectorModelView getInspectorModelView()
    {
        return _inspectorModelView;
    }

	public TabModel getTabModel() {
		return _tabModel;
	}

    public boolean hasVisibleWidgets()
    {
        return _visibleWidgets.size()>0;
    }
    
	protected Enumeration<InnerTabWidgetView> getAllWidgets()
	{
		return _widgets.elements();
	}
	
	public void requestFocusInFirstWidget()
	{
		if (_widgets.size() > 0) {
			_widgets.firstElement().getDynamicComponent().requestFocus();
			//System.out.println("Request focus for "+_widgets.firstElement().getDynamicComponent());
		}
	}

	public void requestFocusInSecondWidget()
	{
		if (_widgets.size() > 1) {
			_widgets.elementAt(1).getDynamicComponent().requestFocus();
			//System.out.println("Request focus for "+_widgets.elementAt(1).getDynamicComponent());
		}
	}

}
