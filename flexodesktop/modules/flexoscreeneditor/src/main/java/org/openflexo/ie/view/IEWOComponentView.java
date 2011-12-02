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

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;
import java.util.logging.Level;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JViewport;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.openflexo.foundation.DataModification;
import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.FlexoObservable;
import org.openflexo.foundation.GraphicalFlexoObserver;
import org.openflexo.foundation.ObjectDeleted;
import org.openflexo.foundation.ie.ComponentInstance;
import org.openflexo.foundation.ie.IEObject;
import org.openflexo.foundation.ie.IEWOComponent;
import org.openflexo.foundation.ie.cl.TabComponentDefinition;
import org.openflexo.foundation.ie.widget.IEBIRTWidget;
import org.openflexo.foundation.ie.widget.IEBlocWidget;
import org.openflexo.foundation.ie.widget.IEBrowserWidget;
import org.openflexo.foundation.ie.widget.IEButtonWidget;
import org.openflexo.foundation.ie.widget.IECheckBoxWidget;
import org.openflexo.foundation.ie.widget.IEDropDownWidget;
import org.openflexo.foundation.ie.widget.IEDynamicImage;
import org.openflexo.foundation.ie.widget.IEFileUploadWidget;
import org.openflexo.foundation.ie.widget.IEHTMLTableWidget;
import org.openflexo.foundation.ie.widget.IEHeaderWidget;
import org.openflexo.foundation.ie.widget.IEHyperlinkWidget;
import org.openflexo.foundation.ie.widget.IELabelWidget;
import org.openflexo.foundation.ie.widget.IEMultimediaWidget;
import org.openflexo.foundation.ie.widget.IERadioButtonWidget;
import org.openflexo.foundation.ie.widget.IEReusableWidget;
import org.openflexo.foundation.ie.widget.IESequenceTR;
import org.openflexo.foundation.ie.widget.IESequenceTab;
import org.openflexo.foundation.ie.widget.IESequenceWidget;
import org.openflexo.foundation.ie.widget.IEStringWidget;
import org.openflexo.foundation.ie.widget.IETDWidget;
import org.openflexo.foundation.ie.widget.IETRWidget;
import org.openflexo.foundation.ie.widget.IETabWidget;
import org.openflexo.foundation.ie.widget.IETextAreaWidget;
import org.openflexo.foundation.ie.widget.IETextFieldWidget;
import org.openflexo.foundation.ie.widget.IEWidget;
import org.openflexo.foundation.ie.widget.IEWysiwygWidget;
import org.openflexo.ie.view.controller.IEController;
import org.openflexo.ie.view.widget.DisplayableBindingValue;
import org.openflexo.ie.view.widget.IEBIRTWidgetView;
import org.openflexo.ie.view.widget.IEBlocWidgetView;
import org.openflexo.ie.view.widget.IEBrowserWidgetView;
import org.openflexo.ie.view.widget.IEButtonWidgetView;
import org.openflexo.ie.view.widget.IECheckBoxWidgetView;
import org.openflexo.ie.view.widget.IEDropDownWidgetView;
import org.openflexo.ie.view.widget.IEFileUploadWidgetView;
import org.openflexo.ie.view.widget.IEHeaderWidgetView;
import org.openflexo.ie.view.widget.IEHyperlinkWidgetView;
import org.openflexo.ie.view.widget.IEImageWidgetView;
import org.openflexo.ie.view.widget.IELabelWidgetView;
import org.openflexo.ie.view.widget.IEMultimediaWidgetView;
import org.openflexo.ie.view.widget.IERadioButtonWidgetView;
import org.openflexo.ie.view.widget.IEReusableWidgetView;
import org.openflexo.ie.view.widget.IESequenceTRWidgetView;
import org.openflexo.ie.view.widget.IESequenceWidgetWidgetView;
import org.openflexo.ie.view.widget.IEStringWidgetView;
import org.openflexo.ie.view.widget.IETDWidgetView;
import org.openflexo.ie.view.widget.IETabContainerWidgetView;
import org.openflexo.ie.view.widget.IETabWidgetView;
import org.openflexo.ie.view.widget.IETextAreaWidgetView;
import org.openflexo.ie.view.widget.IETextFieldWidgetView;
import org.openflexo.ie.view.widget.IEWidgetView;
import org.openflexo.ie.view.widget.IEWysiwygWidgetView;
import org.openflexo.selection.SelectionListener;
import org.openflexo.view.FlexoPerspective;
import org.openflexo.view.SelectionSynchronizedModuleView;

public class IEWOComponentView extends IEPanel implements GraphicalFlexoObserver, IEViewManaging,
		SelectionSynchronizedModuleView<ComponentInstance>, ChangeListener, Layoutable {

	public boolean holdsNextComputedPreferredSize = false;
	private Hashtable<IEWidgetView, Dimension> _storedPrefSize;
	public JLabel title;
	private Hashtable<IEObject, IEWidgetView> _widgetViews;
	private Hashtable<IEReusableWidget, IEReusableWidgetView> _reusableWidgetViews;
	private Layoutable lastLayoutInvoker;
	private boolean isDisplayed = false;
	private IEWOComponent _model;
	private ComponentInstance _instance;
	// CommentZone commentZone;
	// protected JPanel titlePanel;
	public static final Font TITLE_FONT = new Font("SansSerif", Font.BOLD, 16);
	public static final Color DEFAULT_BG_COLOR = Color.WHITE;
	public DropZoneTopComponent dropZone;

	public static final Font LABEL_FONT = new Font("Verdana", Font.PLAIN, 11);

	public static final Font LABEL_BOLD_FONT = new Font("Verdana", Font.BOLD, 13);

	public IEWOComponentView(IEController iecontroller, ComponentInstance ci) {
		super(iecontroller);
		int widgetsCount = ci.getComponentDefinition().getWOComponent().getAllEmbeddedIEObjects().size();
		int over = widgetsCount / 5;
		_storedPrefSize = new Hashtable<IEWidgetView, Dimension>(widgetsCount + over, 0.4f);
		_widgetViews = new Hashtable<IEObject, IEWidgetView>(widgetsCount + over, 0.4f);
		_reusableWidgetViews = new Hashtable<IEReusableWidget, IEReusableWidgetView>();
		_instance = ci;
		IEWOComponent model = ci.getComponentDefinition().getWOComponent();
		_model = model;
		title = new JLabel((model).getName(), SwingConstants.CENTER);
		title.setFont(TITLE_FONT);
		title.setBackground(Color.WHITE);
		model.addObserver(this);
		addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				if ((!e.isConsumed()) && (getIEController().getSelectionManager() != null)) {
					getIEController().getSelectionManager().getContextualMenuManager().processMousePressed(e);
				}

			}

			@Override
			public void mouseReleased(MouseEvent e) {
				if ((!e.isConsumed()) && (getIEController().getSelectionManager() != null)) {
					getIEController().getSelectionManager().getContextualMenuManager().processMouseReleased(e);
				}
			}
		});
		addMouseMotionListener(new MouseMotionAdapter() {
			@Override
			public void mouseMoved(MouseEvent e) {
				if ((!e.isConsumed()) && (getIEController().getSelectionManager() != null)) {
					getIEController().getSelectionManager().getContextualMenuManager().processMouseMoved(e);
				}

			}
		});
		setLayout(new BorderLayout());
		setBackground(DEFAULT_BG_COLOR);
		dropZone = new DropZoneTopComponent(iecontroller, ci.getWOComponent().getRootSequence(), this);
		add(dropZone, BorderLayout.CENTER);

	}

	/**
	 * Overrides delete
	 * 
	 * @see org.openflexo.view.ModuleView#deleteModuleView()
	 */
	@Override
	public void deleteModuleView() {
		getIEController().removeModuleView(this);
		getModel().deleteObserver(this);
		logger.info("Component view deleted !");
		if (dropZone != null) {
			dropZone.delete();
		}
	}

	public void clearPrefSize() {
		_storedPrefSize.clear();
	}

	public void storePrefSize(IEWidgetView view, Dimension value) {
		_storedPrefSize.put(view, value);
	}

	public Dimension storedPrefSize(IEWidgetView view) {
		return _storedPrefSize.get(view);
	}

	public void removeStoredSizeForWidgetView(IEWidgetView view) {
		_storedPrefSize.remove(view);
	}

	public void removeFrowWidgetViews(IEWidget w) {
		if (_widgetViews.containsKey(w)) {
			removeStoredSizeForWidgetView(_widgetViews.get(w));
			_widgetViews.remove(w);
		}
	}

	public void registerViewForWidget(IEWidget w, IEWidgetView v) {
		_widgetViews.put(w, v);
		if (w instanceof IEReusableWidget) {
			_reusableWidgetViews.put((IEReusableWidget) w, (IEReusableWidgetView) v);
		}
	}

	@Override
	public IEWidgetView findViewForModel(IEObject object) {
		IEWidgetView reply = _widgetViews.get(object);
		return reply;
	}

	public IEWidgetView getViewForWidget(IEWidget insertedWidget, boolean addDnDSupport) {
		IEWidgetView view = _widgetViews.get(insertedWidget);
		if (view == null) {
			view = createView(getIEController(), insertedWidget, addDnDSupport);
			if (view != null) {
				_widgetViews.put(insertedWidget, view);
			}
		}
		if (view == null) {
			if (logger.isLoggable(Level.WARNING)) {
				logger.warning("view is null for widget : " + insertedWidget + " in component : " + this);
			}
		}
		return view;
	}

	public IEWidgetView createView(IEController controller, IEWidget insertedWidget, boolean addDnDSupport) {

		if (insertedWidget == null) {
			if (logger.isLoggable(Level.WARNING)) {
				logger.warning("Can't create view for a null model");
			}
			new Exception().printStackTrace();
			return null;
		}
		if (logger.isLoggable(Level.FINE)) {
			logger.fine("createView for:" + insertedWidget);
		}
		if (insertedWidget instanceof IEBlocWidget) {
			return new IEBlocWidgetView(controller, (IEBlocWidget) insertedWidget, addDnDSupport, this);
		}
		if (insertedWidget instanceof IESequenceWidget) {
			if (((IESequenceWidget) insertedWidget).getNonSequenceParent() instanceof IEWOComponent) {
				return new DropZoneTopComponent(controller, (IESequenceWidget) insertedWidget, this);
			} else {
				return new IESequenceWidgetWidgetView(controller, (IESequenceWidget) insertedWidget, true, this);
			}
		}
		if (insertedWidget instanceof IETDWidget) {
			return new IETDWidgetView(controller, ((IETDWidget) insertedWidget).getSequenceWidget(), this);
		}
		if (insertedWidget instanceof IESequenceTab) {
			return new IETabContainerWidgetView(controller, (IESequenceTab) insertedWidget, addDnDSupport, this);
		}
		if (insertedWidget instanceof IEHTMLTableWidget) {
			return new IESequenceTRWidgetView(controller, ((IEHTMLTableWidget) insertedWidget).getSequenceTR(), addDnDSupport, this);
		}
		if (insertedWidget instanceof IEDynamicImage) {
			return new IEImageWidgetView(controller, (IEDynamicImage) insertedWidget, addDnDSupport, this);
		}
		if (insertedWidget instanceof IEButtonWidget) {
			return new IEButtonWidgetView(controller, (IEButtonWidget) insertedWidget, addDnDSupport, this);
		}
		if (insertedWidget instanceof IELabelWidget) {
			return new IELabelWidgetView(controller, (IELabelWidget) insertedWidget, addDnDSupport, this);
		}
		if (insertedWidget instanceof IEMultimediaWidget) {
			return new IEMultimediaWidgetView(controller, (IEMultimediaWidget) insertedWidget, addDnDSupport, this);
		}
		if (insertedWidget instanceof IEStringWidget) {
			return new IEStringWidgetView(controller, (IEStringWidget) insertedWidget, addDnDSupport, this);
		}
		if (insertedWidget instanceof IETextFieldWidget) {
			return new IETextFieldWidgetView(controller, (IETextFieldWidget) insertedWidget, addDnDSupport, this);
		}
		if (insertedWidget instanceof IETextAreaWidget) {
			return new IETextAreaWidgetView(controller, (IETextAreaWidget) insertedWidget, addDnDSupport, this);
		}
		if (insertedWidget instanceof IERadioButtonWidget) {
			return new IERadioButtonWidgetView(controller, (IERadioButtonWidget) insertedWidget, addDnDSupport, this);
		}
		if (insertedWidget instanceof IEHyperlinkWidget) {
			return new IEHyperlinkWidgetView(controller, (IEHyperlinkWidget) insertedWidget, addDnDSupport, this);
		}
		if (insertedWidget instanceof IEHeaderWidget) {
			return new IEHeaderWidgetView(controller, (IEHeaderWidget) insertedWidget, addDnDSupport, this);
		}
		if (insertedWidget instanceof IEFileUploadWidget) {
			return new IEFileUploadWidgetView(controller, (IEFileUploadWidget) insertedWidget, addDnDSupport, this);
		}
		if (insertedWidget instanceof IEDropDownWidget) {
			return new IEDropDownWidgetView(controller, (IEDropDownWidget) insertedWidget, addDnDSupport, this);
		}
		if (insertedWidget instanceof IECheckBoxWidget) {
			return new IECheckBoxWidgetView(controller, (IECheckBoxWidget) insertedWidget, addDnDSupport, this);
		}
		if (insertedWidget instanceof IEWysiwygWidget) {
			return new IEWysiwygWidgetView(controller, (IEWysiwygWidget) insertedWidget, addDnDSupport, this);
		}
		if (insertedWidget instanceof IETabWidget) {
			return new IETabWidgetView(controller, (IETabWidget) insertedWidget, this);
		}
		if (insertedWidget instanceof IEReusableWidget) {
			return new IEReusableWidgetView(controller, (IEReusableWidget) insertedWidget, addDnDSupport, this);
		}
		if (insertedWidget instanceof IEBrowserWidget) {
			return new IEBrowserWidgetView(controller, (IEBrowserWidget) insertedWidget, addDnDSupport, this);
		}
		if (insertedWidget instanceof IESequenceTR) {
			return new IESequenceTRWidgetView(controller, (IESequenceTR) insertedWidget, addDnDSupport, this);
		}
		if (insertedWidget instanceof IEBIRTWidget) {
			return new IEBIRTWidgetView(controller, (IEBIRTWidget) insertedWidget, addDnDSupport, this);
		}

		if (logger.isLoggable(Level.WARNING)) {
			logger.warning("Can't create view for model :" + insertedWidget.getClass());
		}

		return null;
	}

	public void notifyAllViewsToHoldTheirNextComputedPreferredSize(Layoutable invoker) {
		if (!isDisplayed || lastLayoutInvoker != null) {
			return;
		}
		lastLayoutInvoker = invoker;
		holdsNextComputedPreferredSize = true;
		// Enumeration<IEWidgetView> en = _widgetViews.elements();
		// while (en.hasMoreElements()) {
		// en.nextElement().setHoldsNextComputedPreferredSize();
		// }
	}

	public void resetAllViewsPreferredSize(Layoutable invoker) {
		if (invoker != lastLayoutInvoker) {
			return;
		}
		holdsNextComputedPreferredSize = false;
		clearPrefSize();
		// Enumeration<IEWidgetView> en = _widgetViews.elements();
		// while (en.hasMoreElements()) {
		// en.nextElement().resetPreferredSize();
		// }
		lastLayoutInvoker = null;
	}

	/**
     *
     */
	public void updatePreferredSize() {
		if (logger.isLoggable(Level.FINE)) {
			logger.fine("Update wo component view preferred size");
		}
		setPreferredSize(getPreferredSize());
		if (getParent() != null) {
			getParent().doLayout();
			getParent().repaint();
		}
	}

	/**
	 * Overrides willShow
	 * 
	 * @see org.openflexo.view.ModuleView#willShow()
	 */
	@Override
	public void willShow() {
		if (logger.isLoggable(Level.INFO)) {
			logger.info("WOComponent will show..." + _widgetViews.size());
		}
		propagateResize();
		isDisplayed = true;
	}

	/**
	 * Overrides willHide
	 * 
	 * @see org.openflexo.view.ModuleView#willHide()
	 */
	@Override
	public void willHide() {
		isDisplayed = false;
	}

	/**
	 * Overrides propagateResize
	 * 
	 * @see org.openflexo.ie.view.Layoutable#propagateResize()
	 */
	@Override
	public void propagateResize() {
		Component[] c = getComponents();
		for (int i = 0; i < c.length; i++) {
			if (c[i] instanceof Layoutable) {
				((Layoutable) c[i]).propagateResize();
			}
		}
	}

	// ===============================================================
	// ======== SelectionSynchronizedModuleView implementation =======
	// ===============================================================

	private Vector<IESelectable> _selectedViews = new Vector<IESelectable>();

	/**
	 * Return all the views representing current selection represented IN THIS VIEW (this is not the selection of the selection manager), as
	 * a Vector of IESelectable
	 * 
	 * @return a Vector of IESelectable
	 */
	public Vector<IESelectable> getSelectedViews() {
		return _selectedViews;
	}

	/**
	 * Notified that supplied object has been added to selection
	 * 
	 * @param object
	 *            : the object that has been added to selection
	 */
	@Override
	public void fireObjectSelected(FlexoModelObject object) {
		if (object instanceof IEWidget && !(object instanceof IETRWidget)) {
			IESelectable view = findViewForModel((IEWidget) object);
			if (view != null) {
				view.setIsSelected(true);
				if (!_selectedViews.contains(view)) {
					_selectedViews.add(view);
				}
			} else {
				if (logger.isLoggable(Level.FINE)) {
					logger.fine("model " + object + " is selected, but I cannot find it's view...:-( in " + this
							+ " this message is normal if the view is located in a PartialComponent");
				}
			}
		}
	}

	/**
	 * Notified that supplied object has been removed from selection
	 * 
	 * @param object
	 *            : the object that has been removed from selection
	 */
	@Override
	public void fireObjectDeselected(FlexoModelObject object) {
		if (object instanceof IEWidget) {
			IEWidgetView view = findViewForModel((IEObject) object);
			if (view != null) {
				(view).setIsSelected(false);
				if (_selectedViews.contains(view)) {
					_selectedViews.remove(view);
				}
			}
		}
	}

	/**
	 * Notified selection has been resetted
	 */
	@Override
	public void fireResetSelection() {
		for (Enumeration en = _selectedViews.elements(); en.hasMoreElements();) {
			IESelectable next = (IESelectable) en.nextElement();
			next.setIsSelected(false);
		}
		_selectedViews.clear();
	}

	/**
	 * Notified that the selection manager is performing a multiple selection
	 */
	@Override
	public void fireBeginMultipleSelection() {
	}

	/**
	 * Notified that the selection manager has finished to perform a multiple selection
	 */
	@Override
	public void fireEndMultipleSelection() {
	}

	/**
	 * Returns flag indicating if this view is itself responsible for scroll management When not, Flexo will manage it's own scrollbar for
	 * you
	 * 
	 * @return
	 */
	@Override
	public boolean isAutoscrolled() {
		return false;
	}

	@Override
	public ComponentInstance getRepresentedObject() {
		return getComponentInstance();
	}

	@Override
	public FlexoPerspective<ComponentInstance> getPerspective() {
		return getIEController().COMPONENT_EDITOR_PERSPECTIVE;
	}

	public IEWOComponent getModel() {
		return _model;
	}

	public ComponentInstance getComponentInstance() {
		return _instance;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see java.util.Observer#update(java.util.Observable, java.lang.Object)
	 */
	@Override
	public void update(FlexoObservable observable, DataModification dataModification) {
		if (dataModification instanceof ObjectDeleted) {
			if (dataModification.oldValue() == getModel()) {
				deleteModuleView();
			}
		} else if (observable instanceof IEWOComponent) {
			title.setText(((IEWOComponent) observable).getName());
		}
	}

	@Override
	public Dimension getPreferredSize() {
		return new Dimension(getMaxWidth(), getMaxHeight());
	}

	protected int getMaxHeight() {
		if (dropZone == null) {
			return Toolkit.getDefaultToolkit().getScreenSize().height - 100;
		}
		return dropZone.getPreferredSize().height + 100;
	}

	/**
	 * @param c
	 * @return
	 */
	public int getMaxWidth() {
		if (getParent() instanceof JViewport) {
			return ((JViewport) getParent()).getVisibleRect().width;
		} else if (getParent() instanceof JPanel && ((JPanel) getParent()).getWidth() > 0) {
			return ((JPanel) getParent()).getWidth();
		}
		int answer = 0;
		for (int i = 0; i < getComponents().length; i++) {
			int candidate = SwingUtilities.convertPoint(getComponent(i), getComponent(i).getX(), getComponent(i).getY(), this).x
					+ getComponent(i).getWidth();
			if (candidate > answer) {
				answer = candidate;
			}
		}
		return answer;
	}

	/**
	 * Overrides getHoldsNextComputedPreferredSize
	 * 
	 * @see org.openflexo.ie.view.Layoutable#getHoldsNextComputedPreferredSize()
	 */
	@Override
	public boolean getHoldsNextComputedPreferredSize() {
		return false;
	}

	/**
	 * Overrides resetPreferredSize
	 * 
	 * @see org.openflexo.ie.view.Layoutable#resetPreferredSize()
	 */
	@Override
	public void resetPreferredSize() {

	}

	@Override
	public void stateChanged(ChangeEvent e) {
		propagateResize();
		doLayout();
		repaint();
	}

	/**
	 * Overrides setHoldsNextComputedPreferredSize
	 * 
	 * @see org.openflexo.ie.view.Layoutable#setHoldsNextComputedPreferredSize()
	 */
	@Override
	public void setHoldsNextComputedPreferredSize() {

	}

	@Override
	public List<SelectionListener> getSelectionListeners() {
		Vector<SelectionListener> reply = new Vector<SelectionListener>();
		reply.add(this);
		if (_reusableWidgetViews.size() > 0) {
			for (IEReusableWidget reusableWidget : _reusableWidgetViews.keySet()) {
				reply.add(_reusableWidgetViews.get(reusableWidget).getReusableWidgetComponentView());
			}
		}
		return reply;
	}

	public void setSelectedTab(TabComponentDefinition tab) {
		if (tab == null) {
			if (logger.isLoggable(Level.SEVERE)) {
				logger.severe("Who tried that? you can not pass null as an argument. Returning immediately.");
			}
			return;
		}
		Enumeration<IEObject> en = _widgetViews.keys();
		while (en.hasMoreElements()) {
			IEObject w = en.nextElement();
			if (w instanceof IETabWidget) {
				if (((IETabWidget) w).getTabComponentDefinition() == tab) {
					IETabWidgetView view = (IETabWidgetView) _widgetViews.get(w);
					if (view.getParent() instanceof JTabbedPane) {
						try {
							((JTabbedPane) view.getParent()).setSelectedComponent(view);
							return;
						} catch (RuntimeException e) {
							if (logger.isLoggable(Level.SEVERE)) {
								logger.severe("This is weird, I found the tab, then asked to its parent to select it, but the parent threw an exception.");
							}
						}
					}
				}
			}
		}
		if (logger.isLoggable(Level.INFO)) {
			logger.info("Sorry, but the tab " + tab.getName() + " cannot be found.");
		}
	}

	public void notifyDisplayPrefHasChanged() {
		for (IEWidgetView widgetView : _widgetViews.values()) {
			if (widgetView instanceof DisplayableBindingValue) {
				((DisplayableBindingValue) widgetView).updateDisplayedValue();
			}
		}

	}

}
