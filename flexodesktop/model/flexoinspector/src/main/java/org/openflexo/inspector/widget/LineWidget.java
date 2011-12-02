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
package org.openflexo.inspector.widget;

import java.awt.Component;
import java.awt.FlowLayout;
import java.util.AbstractList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JComponent;
import javax.swing.JPanel;

import org.openflexo.inspector.AbstractController;
import org.openflexo.inspector.InspectableObject;
import org.openflexo.inspector.TabModelView;
import org.openflexo.inspector.model.GroupModel;
import org.openflexo.inspector.model.InnerTabWidget;
import org.openflexo.inspector.model.PropertyModel;
import org.openflexo.inspector.widget.DenaliWidget.WidgetLayout;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.logging.FlexoLogger;

/**
 * @author gpolet
 * 
 */
public class LineWidget extends JPanel implements InnerTabWidgetView {
	private static final Logger logger = FlexoLogger.getLogger(LineWidget.class.getPackage().getName());
	private GroupModel model;
	private Vector<DenaliWidget> _widgets;
	private Vector<DenaliWidget> _visibleWidgets;
	private Vector<DenaliWidget> _invisibleWidgets;

	private AbstractController _controller;
	private TabModelView tabModelView;
	private int indexInTab;

	private FlowLayout layout;

	/**
	 * @param model
	 * @param controller
	 */
	public LineWidget(GroupModel model, AbstractController controller) {
		super(new FlowLayout());
		setOpaque(false);
		layout = (FlowLayout) getLayout();
		if (model.hasValueForParameter("layout")) {
			String s = model.getValueForParameter("layout");
			if (s.toLowerCase().equals("left")) {
				layout.setAlignment(FlowLayout.LEFT);
			} else if (s.toLowerCase().equals("center")) {
				layout.setAlignment(FlowLayout.CENTER);
			} else if (s.toLowerCase().equals("right")) {
				layout.setAlignment(FlowLayout.RIGHT);
			} else {
				if (logger.isLoggable(Level.WARNING)) {
					logger.warning("Unknown layout: " + s + " using align LEFT");
				}
			}
		}
		_controller = controller;
		_widgets = new Vector<DenaliWidget>();
		_visibleWidgets = new Vector<DenaliWidget>();
		_invisibleWidgets = new Vector<DenaliWidget>();
		setName(FlexoLocalization.localizedForKey(model.name, this));
		// setBackground(InspectorCst.BACK_COLOR);
		this.model = model;
		this._controller = controller;
		init();
	}

	private void init() {
		Iterator<PropertyModel> it = orderProperties().iterator();

		while (it.hasNext()) {
			PropertyModel propModel = it.next();

			DenaliWidget widget = (DenaliWidget) DenaliWidget.instance(propModel, _controller);
			add(widget.getDynamicComponent());
			_visibleWidgets.add(widget);
			_widgets.add(widget);
		}
		validate();
	}

	/**
	 * @param model2
	 * @return
	 */
	private AbstractList<PropertyModel> orderProperties() {
		Vector<PropertyModel> lines = new Vector<PropertyModel>();
		Iterator<PropertyModel> it = model.getProperties().values().iterator();
		while (it.hasNext()) {
			PropertyModel propModel = it.next();
			if (lines.size() == 0) {
				lines.add(propModel);
			} else {
				Enumeration en = lines.elements();
				boolean notInserted = true;
				int propIndex = propModel.getIndex();
				while (en.hasMoreElements() && notInserted) {
					PropertyModel curProp = (PropertyModel) en.nextElement();
					if (curProp.getIndex() > propIndex) {
						lines.add(lines.indexOf(curProp), propModel);
						notInserted = false;
					}
				}
				if (notInserted) {
					lines.add(propModel);
				}
			}
		}
		return lines;
	}

	/**
	 * Overrides getDynamicComponent
	 * 
	 * @see org.openflexo.inspector.widget.InnerTabWidgetView#getDynamicComponent()
	 */
	@Override
	public JComponent getDynamicComponent() {
		return this;
	}

	/**
	 * Overrides dependsOfProperty
	 * 
	 * @see org.openflexo.inspector.widget.InnerTabWidgetView#dependsOfProperty(org.openflexo.inspector.widget.DenaliWidget)
	 */
	@Override
	public boolean dependsOfProperty(DenaliWidget widget) {
		return false;
	}

	/**
	 * Overrides displayLabel
	 * 
	 * @see org.openflexo.inspector.widget.InnerTabWidgetView#displayLabel()
	 */
	@Override
	public boolean displayLabel() {
		return false;
	}

	/**
	 * Overrides getLabel
	 * 
	 * @see org.openflexo.inspector.widget.InnerTabWidgetView#getLabel()
	 */
	@Override
	public Component getLabel() {
		return null;
	}

	/**
	 * Overrides getWidgetLayout
	 * 
	 * @see org.openflexo.inspector.widget.InnerTabWidgetView#getWidgetLayout()
	 */
	@Override
	public WidgetLayout getWidgetLayout() {
		return null;
	}

	/**
	 * Overrides getXMLModel
	 * 
	 * @see org.openflexo.inspector.widget.InnerTabWidgetView#getXMLModel()
	 */
	@Override
	public InnerTabWidget getXMLModel() {
		return model;
	}

	/**
	 * Overrides isStillVisible
	 * 
	 * @see org.openflexo.inspector.widget.InnerTabWidgetView#isStillVisible(java.lang.Object, java.lang.String)
	 */
	@Override
	public boolean isStillVisible(Object newValue, String observedPropertyName) {
		return true;
	}

	/**
	 * Overrides setController
	 * 
	 * @see org.openflexo.inspector.widget.InnerTabWidgetView#setController(org.openflexo.inspector.AbstractController)
	 */
	@Override
	public void setController(AbstractController controller) {
		this._controller = controller;
	}

	/**
	 * Overrides setTabModelView
	 * 
	 * @see org.openflexo.inspector.widget.InnerTabWidgetView#setTabModelView(org.openflexo.inspector.TabModelView, int)
	 */
	@Override
	public void setTabModelView(TabModelView view, int i) {
		this.tabModelView = view;
		this.indexInTab = i;
		for (DenaliWidget w : _widgets) {
			w.setTabModelView(view, i);
		}
	}

	/**
	 * Overrides shouldExpandHorizontally
	 * 
	 * @see org.openflexo.inspector.widget.InnerTabWidgetView#shouldExpandHorizontally()
	 */
	@Override
	public boolean shouldExpandHorizontally() {
		return true;
	}

	/**
	 * Overrides shouldExpandVertically
	 * 
	 * @see org.openflexo.inspector.widget.InnerTabWidgetView#shouldExpandVertically()
	 */
	@Override
	public boolean shouldExpandVertically() {
		return false;
	}

	/**
	 * Overrides switchObserved
	 * 
	 * @see org.openflexo.inspector.widget.InnerTabWidgetView#switchObserved(org.openflexo.inspector.InspectableObject)
	 */
	@Override
	public void switchObserved(InspectableObject newInspectable) {
		Enumeration<DenaliWidget> en = _widgets.elements();
		while (en.hasMoreElements()) {
			(en.nextElement()).switchObserved(newInspectable);
		}
	}

	/**
	 * @param newValue
	 * @param widget
	 */
	public void valueChange(InspectableObject object) {
		if (logger.isLoggable(Level.FINE)) {
			logger.fine("valueChange() in TabModelView for inspectable " + object);
		}
		Enumeration en = _visibleWidgets.elements();
		DenaliWidget cur = null;
		Vector<DenaliWidget> widgetsToHide = new Vector<DenaliWidget>();
		Vector<DenaliWidget> widgetsToShow = new Vector<DenaliWidget>();
		while (en.hasMoreElements()) {
			cur = (DenaliWidget) en.nextElement();
			boolean isStillVisible = cur.isVisible(object);
			if (!isStillVisible) {
				widgetsToHide.add(cur);
			}
			// logger.info ("WAS visible: "+cur.getObservedPropertyName()+" still visible "+isStillVisible);
		}
		en = _invisibleWidgets.elements();
		while (en.hasMoreElements()) {
			cur = (DenaliWidget) en.nextElement();
			boolean isStillInvisible = !cur.isVisible(object);
			if (!isStillInvisible) {
				widgetsToShow.add(cur);
			}
			// logger.info ("WAS invisible: "+cur.getObservedPropertyName()+" still invisible "+isStillInvisible);
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
	public void valueChange(Object newValue, DenaliWidget widget) {
		// System.out.println ("valueChange() with "+newValue+" for "+widget.getObservedPropertyName());
		if (logger.isLoggable(Level.FINE)) {
			logger.fine("valueChange() in TabModelView for property " + widget.getObservedPropertyName() + " which receive " + newValue);
		}
		Enumeration<DenaliWidget> en = _visibleWidgets.elements();
		DenaliWidget cur = null;
		Vector<DenaliWidget> widgetsToHide = new Vector<DenaliWidget>();
		Vector<DenaliWidget> widgetsToShow = new Vector<DenaliWidget>();
		while (en.hasMoreElements()) {
			cur = en.nextElement();
			boolean isStillVisible = cur.isStillVisible(newValue, widget.getObservedPropertyName());
			if (!isStillVisible) {
				widgetsToHide.add(cur);
			}
			// System.out.println (""+cur.getObservedPropertyName()+": dependsOfProperty(widget)="+cur.dependsOfProperty(widget));
			if (cur.dependsOfProperty(widget)) {
				cur.updateWidgetFromModel();
				if (cur instanceof CustomWidget) {
					((CustomWidget) cur).performModelUpdating();

				}
			}
			// logger.info ("WAS visible: "+cur.getObservedPropertyName()+" still visible "+isStillVisible);
		}
		en = _invisibleWidgets.elements();
		while (en.hasMoreElements()) {
			InnerTabWidgetView inner = en.nextElement();
			if (inner instanceof DenaliWidget) {
				cur = (DenaliWidget) inner;
			} else {
				continue;
			}
			boolean isStillInvisible = cur.isStillInvisible(newValue, widget.getObservedPropertyName());
			if (!isStillInvisible) {
				widgetsToShow.add(cur);
			}
			// logger.info ("WAS invisible: "+cur.getObservedPropertyName()+" still invisible "+isStillInvisible);
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

	private void processToHiding(Vector<DenaliWidget> widgetsToHide) {
		if (logger.isLoggable(Level.FINE)) {
			logger.fine("processToHiding() for " + model.name);
		}
		DenaliWidget cur = null;
		Enumeration<DenaliWidget> en = widgetsToHide.elements();
		while (en.hasMoreElements()) {
			cur = en.nextElement();
			if (logger.isLoggable(Level.FINE)) {
				logger.fine("try to hide " + cur.getObservedPropertyName() + " at index: " + cur.getIndexInTab());
			}
			for (int i = 0; i < getComponentCount(); i++) {
				if (getComponent(i) == cur.getDynamicComponent()) {
					if (cur.getLabel() != null) {
						cur.getLabel().setVisible(false);
					}
					getComponent(i).setVisible(false);
					_visibleWidgets.remove(cur);
					_invisibleWidgets.add(cur);
					if (logger.isLoggable(Level.FINE)) {
						logger.fine("HIDE: " + cur.getObservedPropertyName());
					}
				}
			}
		}
	}

	private void processToShowing(Vector<DenaliWidget> widgetsToShow) {
		if (logger.isLoggable(Level.FINE)) {
			logger.fine("processToShowing() for " + model.name);
		}
		DenaliWidget cur = null;
		Enumeration<DenaliWidget> en = widgetsToShow.elements();
		while (en.hasMoreElements()) {
			cur = en.nextElement();
			if (logger.isLoggable(Level.FINE)) {
				logger.fine("try to show " + cur.getObservedPropertyName() + " at index: " + cur.getIndexInTab());
			}
			cur.setTabModelView(tabModelView, indexInTab);
			cur.getDynamicComponent().setVisible(true);
			if (cur.getLabel() != null) {
				cur.getLabel().setVisible(true);
			}
			_visibleWidgets.add(cur);
			_invisibleWidgets.remove(cur);
			if (logger.isLoggable(Level.FINE)) {
				logger.fine("SHOW: " + cur.getObservedPropertyName());
			}
		}
	}

	/**
	 * Overrides updateWidgetFromModel
	 * 
	 * @see org.openflexo.inspector.widget.InnerTabWidgetView#updateWidgetFromModel()
	 */
	@Override
	public void updateWidgetFromModel() {

	}

}
