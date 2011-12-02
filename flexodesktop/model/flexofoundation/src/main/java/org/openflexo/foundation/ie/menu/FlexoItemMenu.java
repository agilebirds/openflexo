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
package org.openflexo.foundation.ie.menu;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.lang.StringUtils;
import org.openflexo.foundation.DataModification;
import org.openflexo.foundation.DeletableObject;
import org.openflexo.foundation.FlexoObservable;
import org.openflexo.foundation.FlexoObserver;
import org.openflexo.foundation.ObjectDeleted;
import org.openflexo.foundation.action.FlexoActionType;
import org.openflexo.foundation.bindings.ComponentBindingDefinition;
import org.openflexo.foundation.ie.ComponentInstance;
import org.openflexo.foundation.ie.ComponentInstanceOwner;
import org.openflexo.foundation.ie.IEObject;
import org.openflexo.foundation.ie.IObject;
import org.openflexo.foundation.ie.TabComponentInstance;
import org.openflexo.foundation.ie.cl.ComponentDefinition;
import org.openflexo.foundation.ie.cl.FlexoComponentFolder;
import org.openflexo.foundation.ie.cl.TabComponentDefinition;
import org.openflexo.foundation.ie.dm.IEDataModification;
import org.openflexo.foundation.ie.menu.action.AddMenu;
import org.openflexo.foundation.ie.menu.action.MoveMenuDown;
import org.openflexo.foundation.ie.menu.action.MoveMenuUp;
import org.openflexo.foundation.ie.menu.action.MoveMenuUpperLevel;
import org.openflexo.foundation.ie.menu.dm.MenuItemAdded;
import org.openflexo.foundation.ie.menu.dm.MenuItemRemoved;
import org.openflexo.foundation.ie.menu.dm.ReOrderedMenuItem;
import org.openflexo.foundation.ie.util.FolderType;
import org.openflexo.foundation.rm.DuplicateResourceException;
import org.openflexo.foundation.rm.FlexoProject;
import org.openflexo.foundation.rm.XMLStorageResourceData;
import org.openflexo.foundation.validation.Validable;
import org.openflexo.foundation.validation.ValidationError;
import org.openflexo.foundation.validation.ValidationIssue;
import org.openflexo.foundation.validation.ValidationModel;
import org.openflexo.foundation.validation.ValidationReport;
import org.openflexo.foundation.validation.ValidationRule;
import org.openflexo.foundation.wkf.FlexoProcess;
import org.openflexo.foundation.wkf.InvalidParentProcessException;
import org.openflexo.foundation.wkf.WKFObject;
import org.openflexo.foundation.wkf.dm.DisplayOperationSet;
import org.openflexo.foundation.wkf.dm.DisplayProcessSet;
import org.openflexo.foundation.wkf.node.OperationNode;
import org.openflexo.foundation.wkf.utils.OperationAssociatedWithComponentSuccessfully;
import org.openflexo.foundation.xml.FlexoNavigationMenuBuilder;
import org.openflexo.inspector.InspectableObject;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.toolbox.EmptyVector;

public class FlexoItemMenu extends IEObject implements DeletableObject, Validable, InspectableObject, ComponentInstanceOwner, FlexoObserver {
	private static final Logger logger = Logger.getLogger(FlexoItemMenu.class.getPackage().getName());

	private FlexoNavigationMenu _navigationMenu;

	private Vector<FlexoItemMenu> _subItems;

	private FlexoItemMenu _father;

	private String _menuLabel;

	private String _popupWidth;

	private String _popupHeight;

	private boolean _isPageTarget = true;

	private String _popupWindowName;

	private FlexoProcess process;

	private OperationNode operation;

	private long processFlexoID = -1;

	private long operationFlexoID = -1;

	private String url;

	private boolean useUrl;

	private TabComponentInstance _tabComponentInstance;

	public FlexoItemMenu(FlexoProject project) {
		super(project);
		_subItems = new Vector<FlexoItemMenu>();
	}

	/**
	 * Creates a new FlexoComponentFolder with default values (public API outside XML serialization)
	 * 
	 * @param workflow
	 * @throws InvalidParentProcessException
	 */
	public FlexoItemMenu(FlexoNavigationMenu menu, String menuLabel, FlexoItemMenu father, FlexoProject prj) {
		this(prj);
		_navigationMenu = menu;
		_menuLabel = menuLabel;
		setFather(father);
	}

	public FlexoItemMenu(FlexoNavigationMenuBuilder builder) {
		this(builder.getProject());
		_navigationMenu = builder.navigationMenu;
		initializeDeserialization(builder);
	}

	public static FlexoItemMenu createNewRootMenu(FlexoNavigationMenu menu) {
		return createNewMenu(menu, null, FlexoLocalization.localizedForKey("default_page"));
	}

	public static FlexoItemMenu createNewMenu(FlexoNavigationMenu menu, FlexoItemMenu parentMenu, String menuName) {
		FlexoItemMenu newMenu = new FlexoItemMenu(menu, menuName, parentMenu, menu.getProject());
		if (parentMenu != null) {
			parentMenu.addToSubItems(newMenu);
		} else {
			if (logger.isLoggable(Level.INFO)) {
				logger.info("NEW ROOT MENU");
			}
			menu.setRootMenu(newMenu);
		}

		return newMenu;
	}

	/**
	 * Overrides delete
	 * 
	 * @see org.openflexo.foundation.FlexoModelObject#delete()
	 */
	@Override
	public final void delete() {
		if (isRootMenu()) {
			if (logger.isLoggable(Level.WARNING)) {
				logger.warning("Root menu cannot be deleted");
			}
			return;
		}
		removeTabComponentInstance();
		if (logger.isLoggable(Level.FINE)) {
			logger.fine("Deletion of menu item " + _menuLabel);
		}
		Enumeration<FlexoItemMenu> en = getSubItems().elements();
		while (en.hasMoreElements()) {
			FlexoItemMenu element = en.nextElement();
			element.delete();
		}
		if (getFather() != null) {
			getFather().removeFromSubItems(this);
		}
		super.delete();
		deleteObservers();
	}

	@Override
	public void finalizeDeserialization(Object builder) {
		getOperation(); // Forces to resolve process and operation
		super.finalizeDeserialization(builder);
	}

	@Override
	protected Vector<FlexoActionType> getSpecificActionListForThatClass() {
		Vector<FlexoActionType> returned = super.getSpecificActionListForThatClass();
		returned.add(AddMenu.actionType);
		returned.add(MoveMenuUp.actionType);
		returned.add(MoveMenuDown.actionType);
		returned.add(MoveMenuUpperLevel.actionType);
		return returned;
	}

	/**
	 * Returns reference to the main object in which this XML-serializable object is contained relating to storing scheme: here it's the
	 * navigation menu
	 * 
	 * @return
	 */
	@Override
	public XMLStorageResourceData getXMLResourceData() {
		return getNavigationMenu();
	}

	@Override
	public IEObject getParent() {
		return getFather();
	}

	public FlexoItemMenu getFather() {
		return _father;
	}

	public void setFather(FlexoItemMenu f) {
		_father = f;
	}

	public boolean isRootMenu() {
		return getFather() == null;
	}

	public void setMenuLabel(String label) {
		String old = _menuLabel;
		_menuLabel = label;
		setChanged();
		notifyObservers(new IEDataModification("menuLabel", old, label));
	}

	public String getMenuLabel() {
		return _menuLabel;
	}

	public int getIndex() {
		return getFather().getSubItems().indexOf(this) + 1;
	}

	public void setSubItems(Vector<FlexoItemMenu> v) {
		_subItems = v;
		setChanged();
	}

	public Vector<FlexoItemMenu> getSubItems() {
		return _subItems;
	}

	public String getWOAUrl() {
		String woaUrl = "";
		if (getUseUrl()) {
			woaUrl = '"' + getUrl() + '"';
		} else {
			/* if (getIsPageTarget()) { */
			if (getOperation() != null && getOperation().hasWOComponent()) {
				StringBuilder args = new StringBuilder();
				for (ComponentBindingDefinition cbd : getOperation().getComponentDefinition().getBindingDefinitions()) {
					if (cbd.getIsMandatory()) {
						/*
						 * if(getOperation().getComponentInstance().getBinding(cbd).getBindingValue()!=null){
						 * args.append(", "+getOperation().getComponentInstance().getBinding(cbd).getBindingValue().getCodeStringRepresentation()); }else
						 */
						args.append(", ").append(cbd.getType().getDefaultValue());
					}
				}
				if (getOperation().getComponentInstance().getWOComponent().getFirstTabContainerTitle() != null) {
					args.append(", null");
				}

				woaUrl = getOperation().getComponentDefinition().getName() + ".getUrlForOperation(context()" + args.toString() + ","
						+ getOperation().getComponentInstance().getFlexoID() + ")";

			} else {
				woaUrl = "\"#\"";
				/*
				 * } else if (getOperation() != null && getOperation().hasWOComponent()) { String w = getPopupWidth(); String h = getPopupHeight(); String selectedTab = "";
				 * if(getOperation().getComponentInstance().getComponentDefinition().getFirstTabContainerTitle()!=null) selectedTab = ", null"; woaUrl = "\"window.open(\\\\'\"+" +
				 * getOperation().getComponentDefinition().getName() + ".getUrlForOperation(context()"+selectedTab+","+getOperation().getComponentInstance().getFlexoID()+")+\"\\\\'" + ",\\\\'" +
				 * (getPopupWindowName() != null ? getPopupWindowName() : getOperation().getComponentDefinition().getName()) + "\\\\',\\\\'width=" + w + ",height=" + h +
				 * ",directories=no,fullscreen=no,location=no,menubar=no,resizable=yes,scrollbars=yes,status=yes,titlebar=no,toolbar=no\\\\',\\\\'\\\\')\""; }
				 */
			}
		}
		return woaUrl;
	}

	public void addToSubItems(FlexoItemMenu item) {
		_subItems.add(item);
		item.setFather(this);
		setChanged();
		notifyObservers(new MenuItemAdded(item));
	}

	public void removeFromSubItems(FlexoItemMenu item) {
		_subItems.remove(item);
		setChanged();
		notifyObservers(new MenuItemRemoved(item));
	}

	public boolean getIsPageTarget() {
		return _isPageTarget;
	}

	public void setIsPageTarget(boolean v) {
		_isPageTarget = v;
		setChanged();
		notifyObservers(new IEDataModification("isPageTarget", null, new Boolean(v)));
	}

	public void switchItems(FlexoItemMenu item1, FlexoItemMenu item2) {
		if (item1 != null && item2 != null && _subItems.indexOf(item1) > -1 && _subItems.indexOf(item2) > -1
				&& _subItems.indexOf(item1) != _subItems.indexOf(item2)) {
			if (_subItems.indexOf(item1) > _subItems.indexOf(item2)) {
				switchItems(item2, item1);
			} else {
				int i1 = _subItems.indexOf(item1);
				int i2 = _subItems.indexOf(item2);
				_subItems.remove(item1);
				_subItems.remove(item2);
				_subItems.insertElementAt(item2, i1);
				_subItems.insertElementAt(item1, i2);
			}
		}
		setChanged();
		notifyObservers(new ReOrderedMenuItem());
	}

	public String getPopupHeight() {
		return _popupHeight;
	}

	public void setPopupHeight(String height) {
		String old = _popupHeight;
		_popupHeight = height;
		setChanged();
		notifyObservers(new IEDataModification("popupHeight", old, height));
	}

	public String getPopupWidth() {
		return _popupWidth;
	}

	public void setPopupWidth(String width) {
		String old = _popupWidth;
		_popupWidth = width;
		setChanged();
		notifyObservers(new IEDataModification("popupWidth", old, _popupWidth));
	}

	public String getPopupWindowName() {
		return _popupWindowName;
	}

	public void setPopupWindowName(String windowName) {
		String old = _popupWindowName;
		_popupWindowName = windowName;
		setChanged();
		notifyObservers(new IEDataModification("popupWindowName", old, _popupWindowName));
	}

	/**
	 * Return a Vector of embedded IEObjects at this level. NOTE that this is NOT a recursive method
	 * 
	 * @return a Vector of IEObject instances
	 */
	@Override
	public Vector<IObject> getEmbeddedIEObjects() {
		Vector answer = new Vector();
		answer.addAll(_subItems);
		return answer;
	}

	public FlexoNavigationMenu getNavigationMenu() {
		return _navigationMenu;
	}

	@Override
	public String getFullyQualifiedName() {
		return "FLEXO_ITEM_MENU.NOT_IMPLEMENTED." + getMenuLabel();
	}

	public boolean isChildOf(FlexoItemMenu anItemMenu) {
		FlexoItemMenu it = this;
		while (it != null) {
			if (it.equals(anItemMenu)) {
				return true;
			}
			it = it.getFather();
		}
		return false;
	}

	/**
	 * Overrides getAllEmbeddedDeleted
	 * 
	 * @see org.openflexo.foundation.DeletableObject#getAllEmbeddedDeleted()
	 */
	@Override
	public Vector<WKFObject> getAllEmbeddedDeleted() {
		return EmptyVector.EMPTY_VECTOR(WKFObject.class);
	}

	public Vector<FlexoItemMenu> getAllItemMenus(Vector<FlexoItemMenu> menus) {
		menus.add(this);
		Enumeration<FlexoItemMenu> en = getSubItems().elements();
		while (en.hasMoreElements()) {
			en.nextElement().getAllItemMenus(menus);
		}
		return menus;
	}

	/**
	 * Overrides getClassNameKey
	 * 
	 * @see org.openflexo.foundation.FlexoModelObject#getClassNameKey()
	 */
	@Override
	public String getClassNameKey() {
		return "flexo_item_menu";
	}

	public OperationNode getOperation() {
		if (operation == null && operationFlexoID > -1) {
			if (getProcess() != null) {
				operation = getProcess().getOperationNodeWithFlexoID(operationFlexoID);
				if (operation == null) {
					operationFlexoID = -1;
					setChanged();
				} else {
					operation.addObserver(this);
				}

			} else if (logger.isLoggable(Level.WARNING)) {
				logger.warning("This is weird, an operation has been set but not its process.");
			}
		}
		return operation;
	}

	public void setOperation(OperationNode displayOperation) {
		if (operation == displayOperation) {
			return;
		}
		OperationNode old = this.operation;
		if (old != null) {
			old.deleteObserver(this);
		}
		this.operation = displayOperation;
		if (displayOperation != null) {
			displayOperation.addObserver(this);
			operationFlexoID = displayOperation.getFlexoID();
		} else {
			operationFlexoID = -1;
		}
		setTabComponent(null);
		if (isRootMenu()) {
			getProject().setFirstOperation(displayOperation);
		}
		setChanged();
		notifyObservers(new DisplayOperationSet(old, displayOperation));
	}

	public FlexoProcess getProcess() {
		if (process == null && processFlexoID > -1) {
			process = getProject().getFlexoWorkflow().getLocalFlexoProcessWithFlexoID(processFlexoID);
			if (process == null) {
				if (logger.isLoggable(Level.WARNING)) {
					logger.warning("Could not find process with flexoID " + processFlexoID);
				}
				processFlexoID = -1;
				setOperation(null);
				setChanged();
			} else {
				process.addObserver(this);
			}
		}
		return process;
	}

	public void setProcess(FlexoProcess displayProcess) {
		if (this.process == displayProcess) {
			return;
		}
		FlexoProcess old = this.process;
		if (old != null) {
			old.deleteObserver(this);
		}
		this.process = displayProcess;
		if (displayProcess != null) {
			process.addObserver(this);
			processFlexoID = displayProcess.getFlexoID();
		} else {
			processFlexoID = -1;
		}
		setOperation(null);
		getNavigationMenu().getFlexoResource().clearDependancies();
		getNavigationMenu().getFlexoResource().rebuildDependancies();
		setChanged();
		notifyObservers(new DisplayProcessSet(old, displayProcess));
	}

	public boolean isAcceptableAsDisplayProcess(FlexoProcess process) {
		return process != null && !process.isImported();
	}

	public long getOperationFlexoID() {
		if (getOperation() != null) {
			return getOperation().getFlexoID();
		} else {
			return -1;
		}
	}

	public void setOperationFlexoID(long displayOperationFlexoID) {
		if (getOperation() != null) {
			operation = null;
		}
		this.operationFlexoID = displayOperationFlexoID;
	}

	public long getProcessFlexoID() {
		if (getProcess() != null) {
			return getProcess().getFlexoID();
		} else {
			return -1;
		}
	}

	public void setProcessFlexoID(long displayProcessFlexoID) {
		if (getProcess() != null) {
			process = null;
		}
		this.processFlexoID = displayProcessFlexoID;
	}

	@Override
	public void update(FlexoObservable observable, DataModification obj) {
		if (observable == getOperation()) {
			if (obj instanceof ObjectDeleted) {
				setOperation(null);
			} else if (obj.propertyName() != null && obj.propertyName().equals("flexoID")) {
				setChanged();
			}
		} else if (observable == getProcess()) {
			if (obj instanceof ObjectDeleted) {
				setProcess(null);
			} else if (obj.propertyName() != null && obj.propertyName().equals("flexoID")) {
				setChanged();
			}
		}
		super.update(observable, obj);
	}

	public static class RootItemMustBeBound extends ValidationRule<RootItemMustBeBound, FlexoItemMenu> {

		public RootItemMustBeBound() {
			super(FlexoItemMenu.class, "root_menu_item_must_be_bound_to_an_operation");
		}

		@Override
		public ValidationIssue<RootItemMustBeBound, FlexoItemMenu> applyValidation(FlexoItemMenu menu) {
			ValidationIssue<RootItemMustBeBound, FlexoItemMenu> err = null;
			if (menu.isRootMenu()) {
				if (menu.getProcess() == null || menu.getOperation() == null) {
					err = new ValidationError<RootItemMustBeBound, FlexoItemMenu>(this, menu,
							"root_menu_item_must_be_bound_to_an_operation");
				}
			}
			return err;
		}
	}

	public static class MenuMustDefineAnOperation extends ValidationRule<MenuMustDefineAnOperation, FlexoItemMenu> {

		public MenuMustDefineAnOperation() {
			super(FlexoItemMenu.class, "menu_item_must_be_bound_to_an_operation");
		}

		@Override
		public ValidationIssue<MenuMustDefineAnOperation, FlexoItemMenu> applyValidation(FlexoItemMenu menu) {
			ValidationIssue<MenuMustDefineAnOperation, FlexoItemMenu> err = null;
			if (!menu.getUseUrl() && (menu.getProcess() == null || menu.getOperation() == null)) {
				err = new ValidationError<MenuMustDefineAnOperation, FlexoItemMenu>(this, menu, "menu_item_must_be_bound_to_an_operation");
			}
			return err;
		}
	}

	@Override
	public ValidationModel getDefaultValidationModel() {
		if (getProject() != null) {
			return getProject().getIEValidationModel();
		} else {
			if (logger.isLoggable(Level.WARNING)) {
				logger.warning("Could not access to project !");
			}
		}
		return null;
	}

	/**
	 * Returns a flag indicating if this object is valid according to default validation model
	 * 
	 * @return boolean
	 */
	@Override
	public boolean isValid() {
		return isValid(getDefaultValidationModel());
	}

	/**
	 * Returns a flag indicating if this object is valid according to specified validation model
	 * 
	 * @return boolean
	 */
	@Override
	public boolean isValid(ValidationModel validationModel) {
		return validationModel.isValid(this);
	}

	/**
	 * Validates this object by building new ValidationReport object Default validation model is used to perform this validation.
	 */
	@Override
	public ValidationReport validate() {
		return validate(getDefaultValidationModel());
	}

	/**
	 * Validates this object by building new ValidationReport object Supplied validation model is used to perform this validation.
	 */
	@Override
	public ValidationReport validate(ValidationModel validationModel) {
		return validationModel.validate(this);
	}

	/**
	 * Validates this object by appending eventual issues to supplied ValidationReport. Default validation model is used to perform this
	 * validation.
	 * 
	 * @param report
	 *            , a ValidationReport object on which found issues are appened
	 */
	@Override
	public void validate(ValidationReport report) {
		validate(report, getDefaultValidationModel());
	}

	/**
	 * Validates this object by appending eventual issues to supplied ValidationReport. Supplied validation model is used to perform this
	 * validation.
	 * 
	 * @param report
	 *            , a ValidationReport object on which found issues are appened
	 */
	@Override
	public void validate(ValidationReport report, ValidationModel validationModel) {
		validationModel.validate(this, report);
	}

	/**
	 * @param menuLabel
	 * @return
	 */
	public FlexoItemMenu getMenuLabeled(String menuLabel) {
		if (this.getMenuLabel() != null && this.getMenuLabel().equals(menuLabel)) {
			return this;
		}
		Vector<FlexoItemMenu> v = getSubItems();
		Enumeration<FlexoItemMenu> en = v.elements();
		while (en.hasMoreElements()) {
			FlexoItemMenu menu = en.nextElement();
			FlexoItemMenu ret = menu.getMenuLabeled(menuLabel);
			if (ret != null) {
				return ret;
			}
		}
		return null;
	}

	@Override
	public String getInspectorName() {
		return "FlexoItemMenu.inspector";
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
		setChanged();
		notifyObservers(new IEDataModification("url", null, url));
	}

	public boolean getUseUrl() {
		return useUrl;
	}

	public void setUseUrl(boolean useUrl) {
		this.useUrl = useUrl;
		setChanged();
		notifyObservers(new IEDataModification("useUrl", null, useUrl));
	}

	// TABS
	public String getTabComponentName() {
		if (getTabComponent() != null) {
			return getTabComponent().getComponentName();
		}
		return null;
	}

	public void setTabComponentName(String aComponentName) throws DuplicateResourceException, OperationAssociatedWithComponentSuccessfully {
		if (getTabComponentName() != null && getTabComponentName().equals(aComponentName)) {
			return;
		}
		if (_tabComponentInstance == null && ((aComponentName == null) || (aComponentName.trim().equals("")))) {
			return;
		}
		ComponentDefinition foundComponent = getProject().getFlexoComponentLibrary().getComponentNamed(aComponentName);
		TabComponentDefinition newComponent = null;
		if (foundComponent instanceof TabComponentDefinition) {
			newComponent = (TabComponentDefinition) foundComponent;
		} else if (foundComponent != null) {
			if (logger.isLoggable(Level.WARNING)) {
				logger.warning("Found a component named " + aComponentName + " but this is not a TabComponent. Aborting.");
			}
			throw new DuplicateResourceException(aComponentName);
		}
		if (newComponent == null) {
			if (logger.isLoggable(Level.INFO)) {
				logger.info("Creating a new Component named:" + aComponentName);
			}
			FlexoComponentFolder selectedFolder = getProject().getFlexoComponentLibrary().getRootFolder()
					.getFolderTyped(FolderType.TAB_FOLDER);
			newComponent = new TabComponentDefinition(aComponentName, getProject().getFlexoComponentLibrary(), selectedFolder, getProject());
		}
		setTabComponent(newComponent);
	}

	public TabComponentDefinition getTabComponent() {
		if (_tabComponentInstance != null) {
			return _tabComponentInstance.getComponentDefinition();
		}
		return null;
	}

	public void setTabComponent(TabComponentDefinition aComponentDefinition) {
		if ((_tabComponentInstance != null) && (_tabComponentInstance.getComponentDefinition() == aComponentDefinition)) {
			return;
		}
		if (_tabComponentInstance != null && aComponentDefinition == null) {
			removeTabComponentInstance();
		}
		if (aComponentDefinition != null) {
			setTabMenuComponentInstance(new TabComponentInstance(aComponentDefinition, this));
		}
	}

	public TabComponentInstance getTabMenuComponentInstance() {
		return _tabComponentInstance;
	}

	public void setTabMenuComponentInstance(TabComponentInstance tabComponentInstance) {
		if (_tabComponentInstance != null) {
			removeTabComponentInstance();
		}
		if (tabComponentInstance.getComponentDefinition() != null) {
			_tabComponentInstance = tabComponentInstance;
			_tabComponentInstance.setItemMenu(this);
			setChanged();
			notifyObservers(new DataModification(-1, "tabMenuComponentInstance", null, _tabComponentInstance));
		} else if (logger.isLoggable(Level.SEVERE)) {
			logger.severe("TabComponentInstance does not have a component definition for component named "
					+ tabComponentInstance.getComponentName());
		}
	}

	public void removeTabComponentInstance() {
		if (_tabComponentInstance != null) {
			_tabComponentInstance.delete();
			ComponentInstance oldComponentInstance = _tabComponentInstance;
			_tabComponentInstance = null;
			setChanged();
			notifyObservers(new DataModification(-1, "tabMenuComponentInstance", oldComponentInstance, null));
		}
	}

	public Hashtable<String, String> getLocalizableProperties() {
		if (StringUtils.isNotEmpty(getMenuLabel())) {
			Hashtable<String, String> reply = new Hashtable<String, String>();
			reply.put("menuLabel", getMenuLabel());
			return reply;
		}
		return null;
	}
}
