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
package org.openflexo.ie.view.controller;

import java.awt.Component;
import java.awt.Cursor;
import java.awt.Image;
import java.awt.Point;
import java.awt.Toolkit;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.Serializable;
import java.util.logging.Logger;

import javax.naming.InvalidNameException;
import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JSplitPane;

import org.openflexo.foundation.FlexoEditor;
import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.FlexoObserver;
import org.openflexo.foundation.InvalidArgumentException;
import org.openflexo.foundation.dkv.DKVModel;
import org.openflexo.foundation.dkv.DKVObject;
import org.openflexo.foundation.dkv.DuplicateDKVObjectException;
import org.openflexo.foundation.dkv.Key;
import org.openflexo.foundation.ie.ComponentInstance;
import org.openflexo.foundation.ie.IEObject;
import org.openflexo.foundation.ie.IEWOComponent;
import org.openflexo.foundation.ie.OperationComponentInstance;
import org.openflexo.foundation.ie.cl.DuplicateFolderNameException;
import org.openflexo.foundation.ie.cl.FlexoComponentFolder;
import org.openflexo.foundation.ie.menu.FlexoItemMenu;
import org.openflexo.foundation.ie.operator.IEOperator;
import org.openflexo.foundation.ie.widget.ColumnIsNotEmpty;
import org.openflexo.foundation.ie.widget.IETextAreaWidget;
import org.openflexo.foundation.ie.widget.IEWidget;
import org.openflexo.foundation.ie.widget.InvalidOperation;
import org.openflexo.foundation.ie.widget.InvalidPercentage;
import org.openflexo.foundation.ie.widget.NotEnoughRoomOnTheLeft;
import org.openflexo.foundation.ie.widget.NotEnoughRoomOnTheRight;
import org.openflexo.foundation.ie.widget.RowIsNotEmpty;
import org.openflexo.foundation.rm.FlexoProject;
import org.openflexo.foundation.validation.ValidationModel;
import org.openflexo.icon.IconLibrary;
import org.openflexo.ie.IEPreferences;
import org.openflexo.ie.menu.IEMenuBar;
import org.openflexo.ie.view.ComponentBrowserView;
import org.openflexo.ie.view.ComponentLibraryBrowserView;
import org.openflexo.ie.view.IEContainer;
import org.openflexo.ie.view.IEMainPane;
import org.openflexo.ie.view.IEPanel;
import org.openflexo.ie.view.IESelectable;
import org.openflexo.ie.view.IEWOComponentView;
import org.openflexo.ie.view.controller.action.IEControllerActionInitializer;
import org.openflexo.ie.view.controller.dnd.IEDTListener;
import org.openflexo.ie.view.dkv.DKVEditorBrowser;
import org.openflexo.ie.view.dkv.DKVModelView;
import org.openflexo.ie.view.palette.IEDSWidget;
import org.openflexo.ie.view.widget.IEWidgetView;
import org.openflexo.inspector.InspectableObject;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.module.FlexoModule;
import org.openflexo.module.UserType;
import org.openflexo.module.external.ExternalIEController;
import org.openflexo.print.PrintManager;
import org.openflexo.print.PrintManagingController;
import org.openflexo.selection.SelectionManager;
import org.openflexo.utils.FlexoSplitPaneLocationSaver;
import org.openflexo.view.FlexoMainPane;
import org.openflexo.view.ModuleView;
import org.openflexo.view.controller.ConsistencyCheckingController;
import org.openflexo.view.controller.ControllerActionInitializer;
import org.openflexo.view.controller.FlexoController;
import org.openflexo.view.menu.FlexoMenuBar;

/**
 * The main controller for the IE module
 * 
 * @author benoit, sylvain
 */

public class IEController extends FlexoController implements ConsistencyCheckingController, Serializable, FlexoObserver,
		ExternalIEController, PrintManagingController, PropertyChangeListener {

	protected static final Logger logger = Logger.getLogger(IEController.class.getPackage().getName());

	private static final Image DROP_OK_IMAGE = IconLibrary.DROP_OK_CURSOR.getImage();

	private static final Image DROP_KO_IMAGE = IconLibrary.DROP_KO_CURSOR.getImage();

	private static final Cursor dropOK = Toolkit.getDefaultToolkit().createCustomCursor(DROP_OK_IMAGE, new Point(16, 16), "Drop OK");

	private static final Cursor dropKO = Toolkit.getDefaultToolkit().createCustomCursor(DROP_KO_IMAGE, new Point(16, 16), "Drop KO");

	private ComponentLibraryBrowser _componentLibraryBrowser;

	private ComponentBrowser _componentBrowser;

	private MenuEditorBrowser _menuEditorBrowser;

	private DKVEditorBrowser _dkvEditorBrowser;

	private IEContainer _currentlyDroppingTarget;

	public final ComponentPerspective COMPONENT_EDITOR_PERSPECTIVE;
	public final DefaultValuePerspective EXAMPLE_VALUE_PERSPECTIVE;
	public final MenuPerspective MENU_EDITOR_PERSPECTIVE;
	public final DKVPerspective DKV_EDITOR_PERSPECTIVE;

	private ComponentLibraryBrowserView _componentLibraryBrowserView;
	private ComponentBrowserView _componentBrowserView;
	private JSplitPane splitPaneWithBrowsers;

	// ==========================================================
	// =================== Perspectives =========================
	// ==========================================================

	/**
	 * Default constructor
	 * 
	 * @param workflowFile
	 * @throws Exception
	 */
	public IEController(FlexoModule module) {
		super(module);
		_componentLibraryBrowser = new ComponentLibraryBrowser(this);
		_componentBrowser = new ComponentBrowser(this);
		_menuEditorBrowser = new MenuEditorBrowser(this);
		_dkvEditorBrowser = new DKVEditorBrowser(this);
		IEPreferences.getPreferences().getPropertyChangeSupport().addPropertyChangeListener(IEPreferences.SHOW_BINDINGVALUE_KEY, this);
		_componentLibraryBrowserView = new ComponentLibraryBrowserView(this);// new
		_componentBrowserView = new ComponentBrowserView(this);// new
		splitPaneWithBrowsers = new JSplitPane(JSplitPane.VERTICAL_SPLIT, _componentLibraryBrowserView, _componentBrowserView);
		new FlexoSplitPaneLocationSaver(splitPaneWithBrowsers, "IEBrowsersLeftSplitPane", 0.5d);
		splitPaneWithBrowsers.setBorder(BorderFactory.createEmptyBorder());
		splitPaneWithBrowsers.setName(FlexoLocalization.localizedForKey("Library", splitPaneWithBrowsers));

		addToPerspectives(COMPONENT_EDITOR_PERSPECTIVE = new ComponentPerspective(this));
		EXAMPLE_VALUE_PERSPECTIVE = new DefaultValuePerspective(this);
		if (UserType.isDevelopperRelease() || UserType.isMaintainerRelease()) {
			addToPerspectives(EXAMPLE_VALUE_PERSPECTIVE);
		}
		addToPerspectives(MENU_EDITOR_PERSPECTIVE = new MenuPerspective(this));
		addToPerspectives(DKV_EDITOR_PERSPECTIVE = new DKVPerspective(this));
	}

	@Override
	protected SelectionManager createSelectionManager() {
		return new IESelectionManager(this);
	}

	public IESelectionManager getIESelectionManager() {
		return (IESelectionManager) getSelectionManager();
	}

	public boolean currentPaletteIsBasicPalette() {
		return COMPONENT_EDITOR_PERSPECTIVE.getCurrentPalette() != null
				&& COMPONENT_EDITOR_PERSPECTIVE.getCurrentPalette().currentPaletteIsBasicPalette();
	}

	@Override
	public void setEditor(FlexoEditor projectEditor) {
		super.setEditor(projectEditor);
		_componentLibraryBrowser.setRootObject(projectEditor != null ? projectEditor.getProject().getFlexoComponentLibrary() : null);
	}

	@Override
	public FlexoModelObject getDefaultObjectToSelect(FlexoProject project) {
		return project.getFlexoComponentLibrary();
	}

	@Override
	public void dispose() {
		_componentLibraryBrowser.setRootObject(null);
		COMPONENT_EDITOR_PERSPECTIVE.disposePalettes();
		super.dispose();
	}

	public JSplitPane getSplitPaneWithBrowsers() {
		return splitPaneWithBrowsers;
	}

	@Override
	public ControllerActionInitializer createControllerActionInitializer() {
		return new IEControllerActionInitializer(this);
	}

	@Override
	public ValidationModel getDefaultValidationModel() {
		if (getMainPane() != null && getMainPane().getModuleView() instanceof DKVModelView) {
			return getProject().getDKVValidationModel();
		}
		return getProject().getIEValidationModel();
	}

	/**
	 * Creates a new instance of MenuBar for the module this controller refers to
	 * 
	 * @return
	 */
	@Override
	protected FlexoMenuBar createNewMenuBar() {
		return new IEMenuBar(this);
	}

	public ComponentLibraryBrowser getComponentLibraryBrowser() {
		return _componentLibraryBrowser;
	}

	public ComponentBrowser getComponentBrowser() {
		return _componentBrowser;
	}

	public MenuEditorBrowser getMenuEditorBrowser() {
		return _menuEditorBrowser;
	}

	@Override
	protected FlexoMainPane createMainPane() {
		return new IEMainPane(this);
	}

	public void setSelectedComponent(ComponentInstance component) {
		setCurrentEditedObjectAsModuleView(component);
	}

	public void setSelectedMenu(FlexoItemMenu menu) {
		setCurrentEditedObjectAsModuleView(menu);

	}

	public void setSelectedDKVObject(DKVObject dkvObject) {
		setCurrentEditedObjectAsModuleView(dkvObject.getDkvModel());

	}

	/**
	 * @return
	 */
	public ComponentInstance getCurrentEditedComponent() {
		if (getCurrentModuleView() instanceof IEWOComponentView) {
			return ((IEWOComponentView) getCurrentModuleView()).getComponentInstance();
		}
		return null;
	}

	public JComponent getCurrentEditZone() {
		return (JComponent) getCurrentModuleView();
	}

	/**
	 * Select the view representing supplied object, if this view exists. Try all to really display supplied object, even if required view
	 * is not the current displayed view
	 * 
	 * @param object
	 *            : the object to focus on
	 */
	@Override
	public void selectAndFocusObject(FlexoModelObject object) {
		if (object instanceof IEOperator && ((IEOperator) object).getOperatedSequence() != null) {
			setSelectedComponent(((IEWidget) object).getWOComponent().getComponentDefinition().getDummyComponentInstance());
			getSelectionManager().setSelectedObject(((IEOperator) object).getOperatedSequence());
		} else if (object instanceof IEWidget) {
			setSelectedComponent(((IEWidget) object).getWOComponent().getComponentDefinition().getDummyComponentInstance());
			getSelectionManager().setSelectedObject(object);
		} else if (object instanceof IEWOComponent) {
			setSelectedComponent(((IEWOComponent) object).getComponentDefinition().getDummyComponentInstance());
			getSelectionManager().setSelectedObject(object);
		} else if (object instanceof ComponentInstance) {
			setSelectedComponent((ComponentInstance) object);
			getSelectionManager().setSelectedObject(object);
		} else {
			getSelectionManager().setSelectedObject(object);
		}
	}

	// ==========================================================================
	// ===================== Model to view conversions =========================
	// ==========================================================================

	public IESelectable selectableViewForObject(IEObject object) {
		IEPanel view = viewForObject(object);

		if (view != null) {
			if (view instanceof IESelectable) {
				return (IESelectable) view;
			}
		}
		return null;
	}

	public IEPanel viewForObject(IEObject object) {
		return viewForObject(object, false);
	}

	public IEPanel viewForObject(IEObject object, boolean performDeepSearch) {
		IEPanel returned = null;
		if (getCurrentEditZone() instanceof IEWOComponentView) {
			if (object instanceof IEWidget) {
				returned = ((IEWOComponentView) getCurrentEditZone()).findViewForModel(object);
			} else if (getCurrentEditedComponent() != null && object == getCurrentEditedComponent().getWOComponent()) {
				returned = (IEWOComponentView) getCurrentEditZone();
			}
			if (returned == null && performDeepSearch) {
				returned = searchRecursivelyViewForObjectInComponent(getCurrentEditZone(), object);
			}
		}
		return returned;
	}

	private IEPanel searchRecursivelyViewForObjectInComponent(JComponent component, IEObject object) {
		for (Component c : component.getComponents()) {
			if (c instanceof IEWidgetView && ((IEWidgetView) c).getModel() == object) {
				return (IEWidgetView) c;
			}
			if (c instanceof JComponent) {
				IEPanel p = searchRecursivelyViewForObjectInComponent((JComponent) c, object);
				if (p != null) {
					return p;
				}
			}
		}
		return null;
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		if (evt.getSource() == IEPreferences.getPreferences()) {
			if (evt.getPropertyName().equals(IEPreferences.SHOW_BINDINGVALUE_KEY)) {
				for (ModuleView<?> v : getAllLoadedViewsForPerspective(COMPONENT_EDITOR_PERSPECTIVE)) {
					if (v instanceof IEWOComponentView) {
						((IEWOComponentView) v).notifyDisplayPrefHasChanged();
					}
				}
			}
		} else {
			super.propertyChange(evt);
		}
	}

	public DKVEditorBrowser getDkvEditorBrowser() {
		return _dkvEditorBrowser;
	}

	/**
	 * Overrides handleException
	 * 
	 * @see org.openflexo.view.controller.FlexoController#handleException(org.openflexo.inspector.InspectableObject, java.lang.String,
	 *      java.lang.Object, java.lang.Throwable)
	 */
	@Override
	public boolean handleException(InspectableObject inspectable, String propertyName, Object value, Throwable exception) {
		if (inspectable instanceof IETextAreaWidget && exception instanceof InvalidArgumentException) {
			notify(FlexoLocalization.localizedForKey("invalid_number_of_rows"));
			return true;
		} else if (exception instanceof RowIsNotEmpty) {
			notify(FlexoLocalization.localizedForKey("row_") + ((RowIsNotEmpty) exception).getRow()
					+ FlexoLocalization.localizedForKey("_is_not_empty"));
			return true;
		} else if (exception instanceof ColumnIsNotEmpty) {
			notify(FlexoLocalization.localizedForKey("col_") + ((ColumnIsNotEmpty) exception).getColumn()
					+ FlexoLocalization.localizedForKey("_is_not_empty"));
			return true;
		} else if (exception instanceof InvalidOperation) {
			notify(FlexoLocalization.localizedForKey("invalid_operation:")
					+ FlexoLocalization.localizedForKey(((InvalidOperation) exception).getMessage()));
			return true;
		} else if (exception instanceof InvalidPercentage) {
			notify(FlexoLocalization.localizedForKey("invalid_percentage:") + ((InvalidPercentage) exception).getPercentage());
			return true;
		} else if (exception instanceof NotEnoughRoomOnTheRight) {
			notify(FlexoLocalization.localizedForKey("cannot_increase_width:no_more_room_on_the_right"));
			return true;
		} else if (exception instanceof NotEnoughRoomOnTheLeft) {
			notify(FlexoLocalization.localizedForKey("cannot_increase_width:no_more_room_on_the_left"));
			return true;
		} else if (exception instanceof DuplicateFolderNameException) {
			notify(FlexoLocalization.localizedForKey("there_is_already_a_folder_with that name"));
			return true;
		} else if (exception instanceof InvalidNameException) {
			notify(FlexoLocalization.localizedForKey("this_name_is_not_valid"));
			return true;
		} else if (exception instanceof DuplicateDKVObjectException) {
			if (inspectable instanceof Key) {
				notify(FlexoLocalization.localizedForKey("there_is_already_a_key_named") + " " + value);
				return true;
			}
		}
		return super.handleException(inspectable, propertyName, value, exception);
	}

	public IEContainer getCurrentlyDroppingTarget() {
		return _currentlyDroppingTarget;
	}

	public void setCurrentlyDroppingTarget(IEContainer droppingTarget) {
		if (droppingTarget == null) {
			if (_currentlyDroppingTarget == null) {
				return;
			}
			_currentDropTargetAsChanged = true;
		} else {
			if (droppingTarget.equals(_currentlyDroppingTarget)) {
				return;
			}
			_currentDropTargetAsChanged = true;
		}
		_currentlyDroppingTarget = droppingTarget;
	}

	private boolean _currentDropTargetAsChanged = false;

	public boolean currentDropTargetAsChanged() {
		if (_currentDropTargetAsChanged) {
			_currentDropTargetAsChanged = false;
			return true;
		}
		return false;
	}

	public Cursor getCurrentDragCursor(IEDSWidget _model) {
		if (_currentlyDroppingTarget != null
				&& IEDTListener.isValidTargetClassForDropTargetContainer(_currentlyDroppingTarget.getContainerModel(),
						_model.getTargetClassModel(), _model.isTopComponent())) {
			return dropOK;
		} else {
			return dropKO;
		}

	}

	public Cursor getCurrentDragCursor(IEWidget _model) {
		if (_currentlyDroppingTarget != null
				&& IEDTListener.isValidTargetClassForDropTargetContainer(_currentlyDroppingTarget.getContainerModel(), _model.getClass(),
						_model.isTopComponent())) {
			return dropOK;
		} else {
			return dropKO;
		}

	}

	/**
	 * @param folder
	 */
	public void setSelectedFolder(FlexoComponentFolder folder) {
		setCurrentEditedObjectAsModuleView(folder);
	}

	@Override
	public PrintManager getPrintManager() {
		return PrintManager.getPrintManager();
	}

	public IEPanel getComponentForWidgetInCurrentComponent(IEWidget widget) {
		if (widget == null) {
			return null;
		}
		return viewForObject(widget);
	}

	@Override
	public String getWindowTitleforObject(FlexoModelObject object) {
		if (object instanceof ComponentInstance) {
			ComponentInstance ci = (ComponentInstance) object;
			if (ci.getComponentDefinition() == null) {
				return "???";
			}
			if (ci instanceof OperationComponentInstance) {
				return ci.getComponentDefinition().getName() + " (" + FlexoLocalization.localizedForKey("operation") + " "
						+ ((OperationComponentInstance) ci).getOperationNode().getName() + ")";
			} else {
				return ci.getComponentDefinition().getName();
			}
		} else if (object instanceof FlexoItemMenu) {
			return FlexoLocalization.localizedForKey("menu") + " " + ((FlexoItemMenu) object).getMenuLabel();
		} else if (object instanceof DKVModel) {
			return FlexoLocalization.localizedForKey("dkv_model");
		}
		return null;
	}

}