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
package org.openflexo.fib.controller;

import java.awt.Component;
import java.awt.Window;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.Vector;
import java.util.logging.Logger;

import javax.swing.SwingUtilities;

import org.openflexo.antar.binding.AbstractBinding.BindingEvaluationContext;
import org.openflexo.antar.binding.BindingVariable;
import org.openflexo.fib.model.FIBBrowser;
import org.openflexo.fib.model.FIBButton;
import org.openflexo.fib.model.FIBCheckBox;
import org.openflexo.fib.model.FIBColor;
import org.openflexo.fib.model.FIBComponent;
import org.openflexo.fib.model.FIBContainer;
import org.openflexo.fib.model.FIBCustom;
import org.openflexo.fib.model.FIBDropDown;
import org.openflexo.fib.model.FIBFile;
import org.openflexo.fib.model.FIBFont;
import org.openflexo.fib.model.FIBHtmlEditor;
import org.openflexo.fib.model.FIBImage;
import org.openflexo.fib.model.FIBLabel;
import org.openflexo.fib.model.FIBList;
import org.openflexo.fib.model.FIBLocalizedDictionary;
import org.openflexo.fib.model.FIBNumber;
import org.openflexo.fib.model.FIBPanel;
import org.openflexo.fib.model.FIBRadioButtonList;
import org.openflexo.fib.model.FIBTab;
import org.openflexo.fib.model.FIBTabPanel;
import org.openflexo.fib.model.FIBTable;
import org.openflexo.fib.model.FIBTextArea;
import org.openflexo.fib.model.FIBTextField;
import org.openflexo.fib.model.FIBWidget;
import org.openflexo.fib.model.listener.FIBMouseClickListener;
import org.openflexo.fib.model.listener.FIBSelectionListener;
import org.openflexo.fib.view.FIBView;
import org.openflexo.fib.view.FIBWidgetView;
import org.openflexo.fib.view.container.FIBPanelView;
import org.openflexo.fib.view.container.FIBTabPanelView;
import org.openflexo.fib.view.container.FIBTabView;
import org.openflexo.fib.view.widget.FIBBrowserWidget;
import org.openflexo.fib.view.widget.FIBButtonWidget;
import org.openflexo.fib.view.widget.FIBCheckBoxWidget;
import org.openflexo.fib.view.widget.FIBColorWidget;
import org.openflexo.fib.view.widget.FIBCustomWidget;
import org.openflexo.fib.view.widget.FIBDropDownWidget;
import org.openflexo.fib.view.widget.FIBFileWidget;
import org.openflexo.fib.view.widget.FIBFontWidget;
import org.openflexo.fib.view.widget.FIBHtmlEditorWidget;
import org.openflexo.fib.view.widget.FIBImageWidget;
import org.openflexo.fib.view.widget.FIBLabelWidget;
import org.openflexo.fib.view.widget.FIBListWidget;
import org.openflexo.fib.view.widget.FIBNumberWidget;
import org.openflexo.fib.view.widget.FIBRadioButtonListWidget;
import org.openflexo.fib.view.widget.FIBTableWidget;
import org.openflexo.fib.view.widget.FIBTextAreaWidget;
import org.openflexo.fib.view.widget.FIBTextFieldWidget;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.localization.Language;
import org.openflexo.toolbox.StringUtils;

public class FIBController<T> extends Observable implements BindingEvaluationContext, Observer {

	private static final Logger logger = Logger.getLogger(FIBController.class.getPackage().getName());

	private T dataObject;
	private final FIBComponent rootComponent;
	private final Hashtable<FIBComponent,FIBView> views;
	private FIBSelectable selectionLeader;
	private FIBSelectable lastFocusedSelectable;

	private FIBWidgetView focusedWidget;

	private FIBViewFactory viewFactory;

	public enum Status
	{
		RUNNING,
		VALIDATED,
		CANCELED,
		ABORTED,
		RESET,
		YES,
		NO,
		QUIT,
		OTHER
	}

	private Status status = Status.RUNNING;

	private final Vector<FIBSelectionListener> selectionListeners;
	private final Vector<FIBMouseClickListener> mouseClickListeners;


	public FIBController(FIBComponent rootComponent)
	{
		this.rootComponent = rootComponent;
		views = new Hashtable<FIBComponent, FIBView>();
		selectionListeners = new Vector<FIBSelectionListener>();
		mouseClickListeners = new Vector<FIBMouseClickListener>();
		viewFactory = new DefaultFIBViewFactory();
	}

	public FIBView<FIBComponent, ?> buildView()
	{
		return buildView(rootComponent);
	}

	public FIBViewFactory getViewFactory()
	{
		return viewFactory;
	}

	public void setViewFactory(FIBViewFactory viewFactory)
	{
		this.viewFactory = viewFactory;
	}

	public void registerView(FIBView view)
	{
		views.put(view.getComponent(),view);
	}

	public void unregisterView(FIBView view)
	{
		views.remove(view.getComponent());
	}

	public FIBView viewForComponent(FIBComponent component)
	{
		return views.get(component);
	}

	public Enumeration<FIBView> getViews()
	{
		return views.elements();
	}

	@Override
	public Object getValue(BindingVariable variable)
	{
		if (variable.getVariableName() == null) {
			return null;
		}
		if (variable.getVariableName().equals("data")) {
			return dataObject;
		}
		for (FIBComponent c : views.keySet()) {
			if (variable.getVariableName().equals(c.getName())) {
				return viewForComponent(c).getDynamicModel();
			}
		}
		if (variable.getVariableName().equals("controller")) {
			return this;
		}
		return variable.getBindingValue(null,this);
	}

	public FIBComponent getRootComponent()
	{
		return rootComponent;
	}

	public FIBView getRootView()
	{
		return viewForComponent(getRootComponent());
	}

	public T getDataObject()
	{
		return dataObject;
	}

	public void setDataObject(T anObject)
	{
		setDataObject(anObject,false);
	}

	public void updateWithoutDataObject()
	{
		setDataObject(null,true);
	}

	public void setDataObject(T anObject, boolean forceUpdate)
	{
		if (forceUpdate || anObject != dataObject) {
			if (dataObject instanceof Observable) {
				((Observable)dataObject).deleteObserver(this);
			}
			dataObject = anObject;
			getRootView().updateDataObject(anObject);
			if (dataObject instanceof Observable) {
				((Observable)dataObject).addObserver(this);
			}
		}
	}

	public static FIBController instanciateController(FIBComponent fibComponent)
	{
		FIBController returned = null;
		if (fibComponent.getControllerClass() != null) {
			try {
				Constructor c = fibComponent.getControllerClass().getConstructor(FIBComponent.class);
				returned = (FIBController)c.newInstance(fibComponent);
			} catch (SecurityException e) {
				logger.warning("SecurityException: Could not instanciate "+fibComponent.getControllerClass());
			} catch (NoSuchMethodException e) {
				logger.warning("NoSuchMethodException: Could not instanciate "+fibComponent.getControllerClass());
			} catch (IllegalArgumentException e) {
				logger.warning("IllegalArgumentException: Could not instanciate "+fibComponent.getControllerClass());
			} catch (InstantiationException e) {
				logger.warning("InstantiationException: Could not instanciate "+fibComponent.getControllerClass());
			} catch (IllegalAccessException e) {
				logger.warning("IllegalAccessException: Could not instanciate "+fibComponent.getControllerClass());
			} catch (InvocationTargetException e) {
				logger.warning("InvocationTargetException: Could not instanciate "+fibComponent.getControllerClass());
			}
		}
		if (returned == null) {
			returned = new FIBController(fibComponent);
		}
		return returned;
	}


	public static FIBView makeView(FIBComponent fibComponent)
	{
		return makeView(fibComponent,instanciateController(fibComponent));
	}

	public static FIBView makeView(FIBComponent fibComponent, FIBController controller)
	{
		return controller.buildView(fibComponent);
	}

	public final <M extends FIBComponent> FIBView<M,?> buildView(M fibComponent)
	{
		if (fibComponent instanceof FIBContainer) {
			return buildContainer((FIBContainer)fibComponent);
		}
		else if (fibComponent instanceof FIBWidget) {
			FIBWidgetView widgetView = buildWidget((FIBWidget)fibComponent);
			if (widgetView != null) {
				return widgetView;
			}
		}
		return null;
	}

	protected final FIBView buildContainer(FIBContainer fibContainer)
	{
		FIBView returned = makeContainer(fibContainer);
		returned.updateGraphicalProperties();
		return returned;
	}

	protected final FIBView makeContainer(FIBContainer fibContainer)
	{
		return getViewFactory().makeContainer(fibContainer);
	}

	protected final FIBWidgetView buildWidget(final FIBWidget fibWidget)
	{
		final FIBWidgetView returned = makeWidget(fibWidget);
		returned.getDynamicJComponent().addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e)
			{
				fireMouseClicked(returned.getDynamicModel(),e.getClickCount());
				if (fibWidget.hasClickAction() && e.getClickCount() == 1) {
					// Detected click associated with action
					fibWidget.getClickAction().execute(FIBController.this);
				}
				else if (fibWidget.hasDoubleClickAction() && e.getClickCount() == 2) {
					// Detected double-click associated with action
					fibWidget.getDoubleClickAction().execute(FIBController.this);
				}
			}
		});
		if (StringUtils.isNotEmpty(fibWidget.getTooltipText())) {
			returned.getDynamicJComponent().setToolTipText(fibWidget.getTooltipText());
		}
		returned.updateGraphicalProperties();
		return returned;
	}

	protected final FIBWidgetView makeWidget(FIBWidget fibWidget)
	{
		return getViewFactory().makeWidget(fibWidget);
	}

	@Override
	public void update(Observable o, Object arg)
	{
		//System.out.println("Received "+arg);
		getRootView().updateDataObject(dataObject);
	}

	private Window retrieveWindow()
	{
		Component c = SwingUtilities.getRoot(getRootView().getJComponent());
		if (c instanceof Window) {
			return (Window)c;
		}
		return null;
	}

	public void validateAndDispose()
	{
		status = Status.VALIDATED;
		Window w = retrieveWindow();
		if (w != null) {
			w.dispose();
		}
	}

	public void cancelAndDispose()
	{
		status = Status.CANCELED;
		Window w = retrieveWindow();
		if (w != null) {
			w.dispose();
		}
	}

	public void abortAndDispose()
	{
		status = Status.ABORTED;
		Window w = retrieveWindow();
		if (w != null) {
			w.dispose();
		}
	}

	public void resetAndDispose()
	{
		status = Status.RESET;
		Window w = retrieveWindow();
		if (w != null) {
			w.dispose();
		}
	}

	public void chooseYesAndDispose()
	{
		status = Status.YES;
		Window w = retrieveWindow();
		if (w != null) {
			w.dispose();
		}
	}

	public void chooseNoAndDispose()
	{
		status = Status.NO;
		Window w = retrieveWindow();
		if (w != null) {
			w.dispose();
		}
	}

	public void chooseQuitAndDispose()
	{
		status = Status.QUIT;
		Window w = retrieveWindow();
		if (w != null) {
			w.dispose();
		}
	}

	public Status getStatus()
	{
		return status;
	}

	public void setStatus(Status status)
	{
		this.status = status;
	}

	public FIBLocalizedDictionary getLocalizer()
	{
		return getRootComponent().retrieveFIBLocalizedDictionary();
	}

	public void switchToLanguage(Language language)
	{
		FlexoLocalization.setCurrentLanguage(language);
		getRootView().updateLanguage();
	}

	public void searchNewLocalizationEntries()
	{
		logger.fine("Search new localization entries");
		Language currentLanguage = FlexoLocalization.getCurrentLanguage();
		getRootComponent().retrieveFIBLocalizedDictionary().beginSearchNewLocalizationEntries();
		for (Language language : Language.availableValues()) {
			switchToLanguage(language);
		}
		getRootComponent().retrieveFIBLocalizedDictionary().endSearchNewLocalizationEntries();
		getRootComponent().retrieveFIBLocalizedDictionary().refresh();
		switchToLanguage(currentLanguage);
		setChanged();
		notifyObservers();
	}

	public void refreshLocalized()
	{
		setChanged();
		notifyObservers();
	}

	public FIBSelectable getSelectionLeader()
	{
		return selectionLeader;
	}

	public FIBSelectable getLastFocusedSelectable()
	{
		return lastFocusedSelectable;
	}

	public FIBWidgetView getFocusedWidget()
	{
		return focusedWidget;
	}

	public void setFocusedWidget(FIBWidgetView newFocusedWidget)
	{
		//logger.info("Focused widget is now "+newFocusedWidget.getComponent()+" was="+(focusedWidget!=null?focusedWidget.getComponent():null));
		if (newFocusedWidget != focusedWidget) {
			FIBWidgetView oldFocusedWidget = focusedWidget;
			focusedWidget = newFocusedWidget;
			if (oldFocusedWidget != null) {
				oldFocusedWidget.getJComponent().repaint();
			}
			if (newFocusedWidget != null) {
				newFocusedWidget.getJComponent().repaint();
			}
			if (newFocusedWidget.isSelectableComponent()) {
				lastFocusedSelectable = newFocusedWidget.getSelectableComponent();
				if (lastFocusedSelectable.synchronizedWithSelection()) {
					selectionLeader = newFocusedWidget.getSelectableComponent();
					logger.info("Selection LEADER is now "+selectionLeader);
					fireSelectionChanged((FIBSelectable)newFocusedWidget);
				}
			}
		}
	}

	public boolean isFocused(FIBWidgetView widget)
	{
		return focusedWidget == widget;
	}

	public void updateSelection(FIBSelectable widget, List<Object> oldSelection, List<Object> newSelection)
	{
		if (widget == selectionLeader) {
			fireSelectionChanged(widget);
			List<Object> objectsToRemoveFromSelection = new Vector<Object>();
			List<Object> objectsToAddToSelection = new Vector<Object>();
			if (oldSelection != null) {
				objectsToRemoveFromSelection.addAll(oldSelection);
			}
			for (Object o : newSelection) {
				if (oldSelection != null && oldSelection.contains(o)) {
					objectsToRemoveFromSelection.remove(o);
				}
				else {
					objectsToAddToSelection.add(o);
				}
			}
			Enumeration<FIBView> en = getViews();
			while (en.hasMoreElements()) {
				FIBView v = en.nextElement();
				if (v.isSelectableComponent()
						&& v.getSelectableComponent() != selectionLeader
						&& v.getSelectableComponent().synchronizedWithSelection()) {
					for (Object o : objectsToAddToSelection) {
						if (v.getSelectableComponent().mayRepresent(o)) {
							v.getSelectableComponent().objectAddedToSelection(o);
						}
					}
					for (Object o : objectsToRemoveFromSelection) {
						if (v.getSelectableComponent().mayRepresent(o)) {
							v.getSelectableComponent().objectRemovedFromSelection(o);
						}
					}
				}
			}
		}
	}

	public void objectAddedToSelection(Object o)
	{
		logger.fine("FIBController: objectAddedToSelection(): "+o);
		Enumeration<FIBView> en = getViews();
		while (en.hasMoreElements()) {
			FIBView v = en.nextElement();
			if (v.isSelectableComponent() && v.getSelectableComponent().synchronizedWithSelection()) {
				if (v.getSelectableComponent().mayRepresent(o)) {
					v.getSelectableComponent().objectAddedToSelection(o);
					selectionLeader = v.getSelectableComponent();
					lastFocusedSelectable = selectionLeader;
				}
			}
		}

	}

	public void objectRemovedFromSelection(Object o)
	{
		logger.fine("FIBController: objectRemovedFromSelection(): "+o);
		Enumeration<FIBView> en = getViews();
		while (en.hasMoreElements()) {
			FIBView v = en.nextElement();
			if (v.isSelectableComponent() && v.getSelectableComponent().synchronizedWithSelection()) {
				if (v.getSelectableComponent().mayRepresent(o)) {
					v.getSelectableComponent().objectRemovedFromSelection(o);
				}
			}
		}
	}

	public void selectionCleared()
	{
		logger.fine("FIBController: selectionCleared()");
		Enumeration<FIBView> en = getViews();
		while (en.hasMoreElements()) {
			FIBView v = en.nextElement();
			if (v.isSelectableComponent() && v.getSelectableComponent().synchronizedWithSelection()) {
				v.getSelectableComponent().selectionResetted();
			}
		}
	}

	private void fireSelectionChanged(FIBSelectable leader)
	{
		// External synchronization
		for (FIBSelectionListener l : selectionListeners) {
			if (selectionLeader != null) {
				l.selectionChanged(selectionLeader.getSelection());
			}
		}
	}

	public void fireMouseClicked(FIBComponentDynamicModel dm, int clickCount)
	{
		for (FIBMouseClickListener l : mouseClickListeners) {
			l.mouseClicked(dm, clickCount);
		}
	}

	public void addSelectionListener(FIBSelectionListener l)
	{
		selectionListeners.add(l);
	}

	public void removeSelectionListener(FIBSelectionListener l)
	{
		selectionListeners.remove(l);
	}

	public void addMouseClickListener(FIBMouseClickListener l)
	{
		mouseClickListeners.add(l);
	}

	public void removeMouseClickListener(FIBMouseClickListener l)
	{
		mouseClickListeners.remove(l);
	}

	protected class DefaultFIBViewFactory implements FIBViewFactory
	{
		@Override
		public FIBView makeContainer(FIBContainer fibContainer)
		{
			if (fibContainer instanceof FIBTab) {
				return new FIBTabView((FIBTab)fibContainer,FIBController.this);
			}
			else if (fibContainer instanceof FIBPanel) {
				return new FIBPanelView((FIBPanel)fibContainer,FIBController.this);
			}
			else if (fibContainer instanceof FIBTabPanel) {
				return new FIBTabPanelView((FIBTabPanel)fibContainer,FIBController.this);
			}
			return null;
		}

		@Override
		public FIBWidgetView makeWidget(FIBWidget fibWidget)
		{
			if (fibWidget instanceof FIBTextField) {
				return new FIBTextFieldWidget((FIBTextField)fibWidget,FIBController.this);
			}
			if (fibWidget instanceof FIBTextArea) {
				return new FIBTextAreaWidget((FIBTextArea)fibWidget,FIBController.this);
			}
			if (fibWidget instanceof FIBHtmlEditor) {
				return new FIBHtmlEditorWidget((FIBHtmlEditor)fibWidget,FIBController.this);
			}
			if (fibWidget instanceof FIBLabel) {
				return new FIBLabelWidget((FIBLabel)fibWidget,FIBController.this);
			}
			if (fibWidget instanceof FIBImage) {
				return new FIBImageWidget((FIBImage)fibWidget,FIBController.this);
			}
			if (fibWidget instanceof FIBCheckBox) {
				return new FIBCheckBoxWidget((FIBCheckBox)fibWidget,FIBController.this);
			}
			if (fibWidget instanceof FIBTable) {
				return new FIBTableWidget((FIBTable)fibWidget,FIBController.this);
			}
			if (fibWidget instanceof FIBBrowser) {
				return new FIBBrowserWidget((FIBBrowser)fibWidget,FIBController.this);
			}
			if (fibWidget instanceof FIBDropDown) {
				return new FIBDropDownWidget((FIBDropDown)fibWidget,FIBController.this);
			}
			if (fibWidget instanceof FIBList) {
				return new FIBListWidget((FIBList)fibWidget,FIBController.this);
			}
			if (fibWidget instanceof FIBNumber) {
				FIBNumber w = (FIBNumber)fibWidget;
				switch (w.getNumberType()) {
				case ByteType:
					return new FIBNumberWidget.FIBByteWidget(w,FIBController.this);
				case ShortType:
					return new FIBNumberWidget.FIBShortWidget(w,FIBController.this);
				case IntegerType:
					return new FIBNumberWidget.FIBIntegerWidget(w,FIBController.this);
				case LongType:
					return new FIBNumberWidget.FIBLongWidget(w,FIBController.this);
				case FloatType:
					return new FIBNumberWidget.FIBFloatWidget(w,FIBController.this);
				case DoubleType:
					return new FIBNumberWidget.FIBDoubleWidget(w,FIBController.this);
				default:
					break;
				}
			}
			if (fibWidget instanceof FIBColor) {
				return new FIBColorWidget((FIBColor)fibWidget,FIBController.this);
			}
			if (fibWidget instanceof FIBFont) {
				return new FIBFontWidget((FIBFont)fibWidget,FIBController.this);
			}
			if (fibWidget instanceof FIBFile) {
				return new FIBFileWidget((FIBFile)fibWidget,FIBController.this);
			}
			if (fibWidget instanceof FIBButton) {
				return new FIBButtonWidget((FIBButton)fibWidget,FIBController.this);
			}
			if (fibWidget instanceof FIBRadioButtonList) {
				return new FIBRadioButtonListWidget((FIBRadioButtonList) fibWidget, FIBController.this);
			}
			if (fibWidget instanceof FIBCustom) {
				return new FIBCustomWidget((FIBCustom)fibWidget,FIBController.this);
			}
			return null;
		}

	}

}
