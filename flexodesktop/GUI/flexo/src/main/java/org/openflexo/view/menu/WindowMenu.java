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
package org.openflexo.view.menu;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.AbstractAction;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;

import org.openflexo.FlexoCst;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.module.FlexoModule;
import org.openflexo.module.Module;
import org.openflexo.module.ModuleLoader;
import org.openflexo.module.ModuleLoadingException;
import org.openflexo.view.FlexoRelativeWindow;
import org.openflexo.view.controller.ConsistencyCheckingController;
import org.openflexo.view.controller.FlexoController;

/**
 * Automatic builded 'Windows' menu for modules
 * 
 * @author sguerin
 */
public class WindowMenu extends FlexoMenu implements PropertyChangeListener {

	private static final Logger logger = Logger.getLogger(WindowMenu.class.getPackage().getName());

	// ==========================================================================
	// =========================== Instance variables
	// ===========================
	// ==========================================================================

	public FlexoMenuItem mainWindowItem;

	public JCheckBoxMenuItem inspectorWindowItem;

	public JCheckBoxMenuItem preferencesWindowItem;

	public JCheckBoxMenuItem checkConsistencyWindowItem;

	private int windowFirstIndex = -1;

	private Map<FlexoRelativeWindow, RelativeWindowItem> relativeWindowItems;

	protected CloseModuleItem closeModuleItem;

	private JMenu loadWindowMenu;

	/**
	 * Hashtable where key is the class object representing module and value a JMenuItem
	 */
	private Map<Module, JMenuItem> _loadWindowMenuItems = new Hashtable<Module, JMenuItem>();

	/**
	 * Hashtable where key is the class object representing module and value a Hashtable of JCheckBoxMenuItem
	 */
	private Map<Module, JCheckBoxMenuItem> _availableWindowMenuItems = new Hashtable<Module, JCheckBoxMenuItem>();

	protected FlexoMenuItem controlPanelItem;

	protected BrowserItem browserItem;

	protected PaletteItem paletteItem;

	// ==========================================================================
	// ============================= Constructor
	// ================================
	// ==========================================================================

	public WindowMenu(FlexoController controller, Module module) {
		super("window", controller);
		if (logger.isLoggable(Level.FINE)) {
			logger.fine("Build NEW module menu for " + module.getName());
		}
		for (Enumeration<FlexoModule> e = getModuleLoader().loadedModules(); e.hasMoreElements();) {
			Module next = e.nextElement().getModule();
			createNewAvailableModuleItem(next);
		}
		addSeparator();

		loadWindowMenu = new JMenu();
		loadWindowMenu.setText(FlexoLocalization.localizedForKey("load_module", loadWindowMenu));
		add(loadWindowMenu);

		for (Module next : getModuleLoader().unloadedButAvailableModules()) {
			createNewLoadModuleItem(next);
		}
		add(closeModuleItem = new CloseModuleItem());

		addSeparator();

		add(inspectorWindowItem = new InspectorWindowItem());
		add(preferencesWindowItem = new PreferencesWindowItem());
		windowFirstIndex = getItemCount();
		if (controller instanceof ConsistencyCheckingController) {
			add(checkConsistencyWindowItem = new CheckConsistencyWindowItem());
			windowFirstIndex = getItemCount();
		}
		relativeWindowItems = new Hashtable<FlexoRelativeWindow, RelativeWindowItem>();

		addSeparator();
		add(controlPanelItem = new ControlPanelItem());
		add(browserItem = new BrowserItem());
		add(paletteItem = new PaletteItem());

		addMenuListener(new MenuListener() {

			@Override
			public void menuSelected(MenuEvent e) {
				updateWindowState();
			}

			@Override
			public void menuDeselected(MenuEvent e) {
			}

			@Override
			public void menuCanceled(MenuEvent e) {
			}

		});
		getModuleLoader().getPropertyChangeSupport().addPropertyChangeListener(ModuleLoader.MODULE_LOADED, this);
		getModuleLoader().getPropertyChangeSupport().addPropertyChangeListener(ModuleLoader.MODULE_UNLOADED, this);
		getModuleLoader().getPropertyChangeSupport().addPropertyChangeListener(ModuleLoader.MODULE_ACTIVATED, this);
	}

	@Override
	protected ModuleLoader getModuleLoader() {
		return getController().getModuleLoader();
	}

	protected void updateWindowState() {
		if (getController().getInspectorWindow() != null) {
			inspectorWindowItem.setState(getController().getInspectorWindow().isVisible());
		}
		if (getController().getPreferencesWindow() != null) {
			preferencesWindowItem.setState(getController().getPreferencesWindow().isVisible());
		}
		if (checkConsistencyWindowItem != null) {
			if (getController().getConsistencyCheckWindow(false) != null) {
				checkConsistencyWindowItem.setState(getController().getConsistencyCheckWindow().isVisible());
			} else {
				checkConsistencyWindowItem.setEnabled(false);
			}
		}
		for (Map.Entry<FlexoRelativeWindow, RelativeWindowItem> next : relativeWindowItems.entrySet()) {
			next.getValue().setState(next.getValue().action.window.isVisible());
		}
	}

	public void addFlexoRelativeWindowMenu(FlexoRelativeWindow window) {
		if (logger.isLoggable(Level.FINE)) {
			logger.fine("addFlexoRelativeWindowMenu() with " + window);
		}
		RelativeWindowItem relativeWindowItem;
		insert(relativeWindowItem = new RelativeWindowItem(window), windowFirstIndex);
		relativeWindowItems.put(window, relativeWindowItem);
	}

	public void removeFlexoRelativeWindowMenu(FlexoRelativeWindow window) {
		RelativeWindowItem relativeWindowItem = relativeWindowItems.get(window);
		if (relativeWindowItem != null) {
			remove(relativeWindowItem);
			relativeWindowItems.remove(window);
		} else {
			if (logger.isLoggable(Level.WARNING)) {
				logger.warning("Inconsistent data in WindowMenu");
			}
		}
	}

	public void renameFlexoRelativeWindowMenu(FlexoRelativeWindow window, String name) {
		RelativeWindowItem relativeWindowItem = relativeWindowItems.get(window);

		if (relativeWindowItem != null) {
			relativeWindowItem.setText(name);
		} else {
			if (logger.isLoggable(Level.WARNING)) {
				logger.warning("Inconsistent data in WindowMenu");
			}
		}
	}

	// ==========================================================================
	// ============================= InspectorWindow
	// ============================
	// ==========================================================================

	public class InspectorWindowItem extends JCheckBoxMenuItem {

		public InspectorWindowItem() {
			super();
			InspectorWindowAction action = new InspectorWindowAction();
			setAction(action);
			KeyStroke accelerator = KeyStroke.getKeyStroke(KeyEvent.VK_I, FlexoCst.META_MASK);
			setAccelerator(accelerator);
			getController().registerActionForKeyStroke(action, accelerator);
			setText(FlexoLocalization.localizedForKey("inspector", this));
		}

	}

	public class InspectorWindowAction extends AbstractAction {
		public InspectorWindowAction() {
			super();
		}

		@Override
		public void actionPerformed(ActionEvent arg0) {
			getController().showInspector();
		}

	}

	// ==========================================================================
	// ============================= PreferencesWindow
	// ============================
	// ==========================================================================

	public class PreferencesWindowItem extends JCheckBoxMenuItem {

		public PreferencesWindowItem() {
			super();
			PreferencesWindowAction action = new PreferencesWindowAction();
			setAction(action);
			setText(FlexoLocalization.localizedForKey("preferences", this));
		}
	}

	public class PreferencesWindowAction extends AbstractAction {
		public PreferencesWindowAction() {
			super();
		}

		@Override
		public void actionPerformed(ActionEvent arg0) {
			getController().showPreferences();
		}

	}

	// ==========================================================================
	// ========================= CheckConsistencyWindow
	// =========================
	// ==========================================================================

	public class CheckConsistencyWindowItem extends JCheckBoxMenuItem {

		public CheckConsistencyWindowItem() {
			super();
			CheckConsistencyWindowAction action = new CheckConsistencyWindowAction();
			setAction(action);
			setText(FlexoLocalization.localizedForKey("validation_window", this));
		}

	}

	public class CheckConsistencyWindowAction extends AbstractAction {
		public CheckConsistencyWindowAction() {
			super();
		}

		@Override
		public void actionPerformed(ActionEvent arg0) {
			if (getController().getConsistencyCheckWindow() != null) {
				getController().getConsistencyCheckWindow().setVisible(true);
			}
		}

	}

	// ==========================================================================
	// ==========================================================================
	// =============================== RelativeWindow
	// ===============================
	// ==========================================================================

	public class RelativeWindowItem extends JCheckBoxMenuItem {
		protected RelativeWindowAction action;

		public RelativeWindowItem(FlexoRelativeWindow window) {
			super();
			action = new RelativeWindowAction(window);
			setAction(action);
			setText(FlexoLocalization.localizedForKey(window.getName(), this));
		}

	}

	public class RelativeWindowAction extends AbstractAction {

		protected FlexoRelativeWindow window;

		public RelativeWindowAction(FlexoRelativeWindow aWindow) {
			super();
			window = aWindow;
		}

		@Override
		public void actionPerformed(ActionEvent arg0) {
			window.setVisible(true);
		}
	}

	public class SwitchToModuleAction extends AbstractAction {
		private Module _module;

		private JCheckBoxMenuItem _menuItem;

		public SwitchToModuleAction(Module module) {
			super();
			_module = module;
		}

		@Override
		public void actionPerformed(ActionEvent arg0) {
			try {
				getController().getModuleLoader().switchToModule(_module);
			} catch (ModuleLoadingException e) {
				FlexoController.notify("Cannot load module." + e.getMessage());
				return;
			}
			_menuItem.setState(getController().getModuleLoader().isActive(_module));
			_menuItem.setIcon(_module.getSmallIcon());
		}

		public void setItem(JCheckBoxMenuItem menuItem) {
			_menuItem = menuItem;
		}
	}

	public class LoadModuleAction extends AbstractAction {
		private Module module;

		public LoadModuleAction(Module module) {
			super();
			this.module = module;
		}

		@Override
		public void actionPerformed(ActionEvent arg0) {

			try {
				getModuleLoader().switchToModule(module);
			} catch (ModuleLoadingException e) {
				FlexoController.notify("Cannot load module." + e.getMessage());
				return;
			}
		}
	}

	private void notifyNewActiveModule(Module module) {
		for (Map.Entry<Module, JCheckBoxMenuItem> e : _availableWindowMenuItems.entrySet()) {
			Module next = e.getKey();
			JCheckBoxMenuItem menuItem = e.getValue();
			if (next.getSmallIcon() != null) {
				menuItem.setIcon(next.getSmallIcon());
			}
			menuItem.setState(next == module);
		}
	}

	private void notifyModuleHasBeenLoaded(Module module) {

		Component[] menuComponents = loadWindowMenu.getMenuComponents();
		for (int i = 0; i < menuComponents.length; i++) {
			if (logger.isLoggable(Level.FINE)) {
				logger.fine("Component: " + ((JMenuItem) menuComponents[i]).getText());
			}
		}
		JMenuItem toRemove = _loadWindowMenuItems.get(module);
		if (toRemove != null) {
			loadWindowMenu.remove(toRemove);
		} else {
			if (logger.isLoggable(Level.WARNING)) {
				logger.warning("No _loadWindowMenuItems for module " + module.getName());
			}
		}
		_loadWindowMenuItems.remove(module);
		if (logger.isLoggable(Level.FINE)) {
			logger.fine("loadWindowMenu contains now:");
		}
		menuComponents = loadWindowMenu.getMenuComponents();
		for (int i = 0; i < menuComponents.length; i++) {
			if (logger.isLoggable(Level.FINE)) {
				logger.fine("Component: " + ((JMenuItem) menuComponents[i]).getText());
			}
		}

		// add to available
		createNewAvailableModuleItem(module);
	}

	private void notifyModuleHasBeenUnloaded(Module module) {
		// add to loadWindowMenu
		createNewLoadModuleItem(module);

		// remove from available
		remove(_availableWindowMenuItems.get(module));
		_availableWindowMenuItems.remove(module);
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		if (evt.getPropertyName().equals(ModuleLoader.MODULE_LOADED)) {
			notifyModuleHasBeenLoaded((Module) evt.getNewValue());
		} else if (evt.getPropertyName().equals(ModuleLoader.MODULE_UNLOADED)) {
			notifyModuleHasBeenUnloaded((Module) evt.getOldValue());
		} else if (evt.getPropertyName().equals(ModuleLoader.MODULE_ACTIVATED)) {
			notifyNewActiveModule((Module) evt.getNewValue());
		}
	}

	private JCheckBoxMenuItem createNewAvailableModuleItem(Module module) {
		if (logger.isLoggable(Level.FINE)) {
			logger.fine("ADD TO new available Module : " + module.getName());
		}
		SwitchToModuleAction action;
		JCheckBoxMenuItem menuItem = new JCheckBoxMenuItem(action = new SwitchToModuleAction(module));
		action.setItem(menuItem);
		menuItem.setText(FlexoLocalization.localizedForKey(module.getName(), menuItem));
		if (module.getSmallIcon() != null) {
			menuItem.setIcon(module.getSmallIcon());
		}

		insert(menuItem, 0);
		_availableWindowMenuItems.put(module, menuItem);
		return menuItem;
	}

	private JMenuItem createNewLoadModuleItem(Module module) {
		if (logger.isLoggable(Level.FINE)) {
			logger.fine("ADD to loadable Module : " + module.getName());
		}
		JMenuItem menuItem = new JMenuItem(new LoadModuleAction(module));
		menuItem.setText(FlexoLocalization.localizedForKey(module.getName(), menuItem));
		menuItem.setIcon(module.getSmallIcon());
		loadWindowMenu.add(menuItem);
		_loadWindowMenuItems.put(module, menuItem);
		return menuItem;
	}

	// ==========================================================================
	// ============================= CloseModule
	// ================================
	// ==========================================================================

	public class CloseModuleItem extends JMenuItem {

		public CloseModuleItem() {
			super(new CloseModuleAction());
			setText(FlexoLocalization.localizedForKey("close_module", this));
		}

	}

	public class CloseModuleAction extends AbstractAction {
		public CloseModuleAction() {
			super();
		}

		@Override
		public void actionPerformed(ActionEvent arg0) {
			getModuleLoader().closeModule(getController().getModule());
		}
	}

	// =============================================================
	// ================ Hide/Show Control Panel ====================
	// =============================================================

	public class ControlPanelItem extends FlexoMenuItem {
		public ControlPanelItem() {
			super(new ControlPanelAction(), "hide_control_panel", null, getController(), true);
			((ControlPanelAction) getAction()).setItem(this);
		}

	}

	public class ControlPanelAction extends AbstractAction {
		private boolean isShowed = true;

		private ControlPanelItem _item;

		public ControlPanelAction() {
			super();
		}

		public void setItem(ControlPanelItem item) {
			_item = item;
		}

		@Override
		public void actionPerformed(ActionEvent arg0) {
			if (isShowed) {
				getController().hideControlPanel();
				_item.setText(FlexoLocalization.localizedForKey("show_control_panel"));
				isShowed = false;
			} else {
				getController().showControlPanel();
				_item.setText(FlexoLocalization.localizedForKey("hide_control_panel"));
				isShowed = true;
			}
		}

	}

	// =============================================================
	// =================== Hide/Show Palette =======================
	// =============================================================

	public String getHidePaletteString() {
		return "hide_palette";
	}

	public String getShowPaletteString() {
		return "show_palette";
	}

	public class PaletteItem extends FlexoMenuItem {
		public PaletteItem() {
			super(new PaletteAction(), getHidePaletteString(), null, getController(), true);
			((PaletteAction) getAction()).setItem(this);
		}

		public void updateText(boolean showed) {
			if (!showed) {
				setText(FlexoLocalization.localizedForKey(getShowPaletteString()));
			} else {
				setText(FlexoLocalization.localizedForKey(getHidePaletteString()));
			}
		}

	}

	public class PaletteAction extends AbstractAction {
		private PaletteItem _item;
		private boolean isShowed;

		public PaletteAction() {
			super();
		}

		public void setItem(PaletteItem item) {
			_item = item;
		}

		@Override
		public void actionPerformed(ActionEvent arg0) {
			isShowed = getController().rightViewIsVisible();
			if (isShowed) {
				getController().hideRightView();
				isShowed = false;
			} else {
				getController().showRightView();
				isShowed = true;
			}
			_item.updateText(isShowed);
		}

	}

	// ==========================================================================
	// ==================== Hide/Show Process Browser
	// ===========================
	// ==========================================================================

	public class BrowserItem extends FlexoMenuItem {
		public BrowserItem() {
			super(new BrowserAction(), "hide_browser", null, getController(), true);
			((BrowserAction) getAction()).setItem(this);
		}

		public void updateText(boolean showed) {
			if (!showed) {
				setText(FlexoLocalization.localizedForKey("show_browser"));
			} else {
				setText(FlexoLocalization.localizedForKey("hide_browser"));
			}
		}

		public BrowserAction getProcessBrowserAction() {
			return (BrowserAction) getAction();
		}
	}

	public class BrowserAction extends AbstractAction {
		private boolean isShowed;

		private BrowserItem _item;

		public BrowserAction() {
			super();
		}

		public void setItem(BrowserItem item) {
			_item = item;
		}

		@Override
		public void actionPerformed(ActionEvent arg0) {
			isShowed = getController().leftViewIsVisible();
			if (isShowed) {
				getController().hideLeftView();
				isShowed = false;
			} else {
				getController().showLeftView();
				isShowed = true;
			}
			_item.updateText(isShowed);
		}

	}

	public BrowserItem getBrowserItem() {
		return browserItem;
	}

	public PaletteItem getPaletteItem() {
		return paletteItem;
	}

}
