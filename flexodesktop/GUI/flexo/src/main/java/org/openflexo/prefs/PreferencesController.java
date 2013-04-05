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
package org.openflexo.prefs;

import java.io.File;
import java.io.FileInputStream;
import java.util.Hashtable;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openflexo.foundation.DataModification;
import org.openflexo.foundation.FlexoObservable;
import org.openflexo.foundation.FlexoObserver;
import org.openflexo.inspector.AbstractController;
import org.openflexo.inspector.HelpDelegate;
import org.openflexo.inspector.InspectableObject;
import org.openflexo.inspector.InspectingWidget;
import org.openflexo.inspector.InspectorDelegate;
import org.openflexo.inspector.InspectorModelView;
import org.openflexo.inspector.InspectorWidgetConfiguration;
import org.openflexo.inspector.model.InspectorMapping;
import org.openflexo.inspector.model.InspectorModel;
import org.openflexo.inspector.model.TabModel;
import org.openflexo.inspector.selection.InspectorSelection;
import org.openflexo.module.UserType;
import org.openflexo.view.controller.FlexoInspectorController;
import org.openflexo.xmlcode.XMLCoder;
import org.openflexo.xmlcode.XMLDecoder;

/**
 * Controller dedicated to preferences: there is only one instance of this controller
 * 
 * @author sguerin
 */
public class PreferencesController implements FlexoObserver, AbstractController, InspectingWidget {

	private static final Logger logger = Logger.getLogger(PreferencesController.class.getPackage().getName());

	// ==========================================================================
	// ============================= Instance Variables
	// =========================
	// ==========================================================================
	private Vector<String> _multiValuesAttributes;

	private Hashtable<String, String> _propertyListAttributes;

	private static PreferencesController _current;

	private InspectorModelView _inpectorPanel;

	private InspectorModel _inspector;

	private PreferencesWindow _preferencesWindow;

	private FlexoPreferences _prefs;

	private InspectorWidgetConfiguration inspectorConfiguration;

	// ==========================================================================
	// ============================= Constructor
	// ================================
	// ==========================================================================

	protected PreferencesController(FlexoPreferences prefs) throws Exception {

		super();

		_multiValuesAttributes = new Vector<String>();
		_propertyListAttributes = new Hashtable<String, String>();

		_inspector = new InspectorModel();
		_inspector.title = "Preferences";

		_prefs = prefs;

		prefs.addObserver(this);
	}

	private static PreferencesController createInstance(FlexoPreferences prefs) {
		try {
			_current = new PreferencesController(prefs);
			for (ContextPreferences next : prefs.contextualPreferences.values()) {
				_current.importInspectorFile(next.getName(), next.getInspectorFile());
			}
		} catch (Exception e) {
			if (logger.isLoggable(Level.WARNING)) {
				logger.warning(e.getMessage());
			}
			e.printStackTrace();
			return null;
		}
		return _current;
	}

	public static PreferencesController instance() {
		if (_current == null) {
			createInstance(FlexoPreferences.instance());
		}
		return _current;
	}

	public static boolean hasInstance() {
		return _current != null;
	}

	public static void register(ContextPreferences cp) {
		instance().importInspectorFile(cp.getName(), cp.getInspectorFile());
	}

	public boolean importInspectorFile(String name, File inspectorFile) {
		try {
			if (InspectorMapping.getInstance() != null) {
				TabModel tabModel = (TabModel) XMLDecoder.decodeObjectWithMapping(new FileInputStream(inspectorFile),
						InspectorMapping.getInstance(), instance());
				tabModel.name = name;
				tabModel.index = new Integer(index);
				index++;
				importInspector(name, tabModel);
				if (logger.isLoggable(Level.FINER)) {
					if (logger.isLoggable(Level.FINE)) {
						logger.fine("Importing preferences inspector:\n"
								+ XMLCoder.encodeObjectWithMapping(tabModel, InspectorMapping.getInstance()));
					}
				}
				return true;
			}
		} catch (Exception e) {
			if (logger.isLoggable(Level.WARNING)) {
				logger.warning("Exception raised during inspector import: " + e);
			}
			e.printStackTrace();
		}

		return false;
	}

	private int index = 1;

	public boolean importInspector(String name, TabModel inspectorTab) {
		if (logger.isLoggable(Level.FINE)) {
			logger.fine("Importing preferences inspector at index " + inspectorTab.index);
		}
		_inspector.getTabs().put(inspectorTab.index, inspectorTab);
		FlexoInspectorController.cleanInspectorModel(_inspector);
		rebuildTabPanel();
		return true;
	}

	public void showPreferences() {
		getPreferencesWindow(true);
		update();
		getPreferencesWindow().setVisible(true);
	}

	public void resetWindow() {
		if (_preferencesWindow != null) {
			_preferencesWindow.reset();
		}
	}

	public PreferencesWindow getPreferencesWindow() {
		return getPreferencesWindow(true);
	}

	public PreferencesWindow getPreferencesWindow(boolean create) {
		if (_preferencesWindow == null && create) {
			_preferencesWindow = new PreferencesWindow();
		}
		return _preferencesWindow;
	}

	// ==========================================================================
	// ============================= Observer
	// ===================================
	// ==========================================================================

	@Override
	public void update(FlexoObservable arg0, DataModification arg1) {
		if (logger.isLoggable(Level.FINE)) {
			logger.fine("update in PreferencesController with " + arg0 + " and " + arg1);
		}
		if (arg1 instanceof PreferencesHaveChanged) {
			// Nothing special for now
		}
		if (_preferencesWindow != null) {
			_preferencesWindow.setTabPanel(getInspectorTabPanel());
			_preferencesWindow.enableSaveAndRevertButtons();
		}
	}

	public void update() {
		update(_prefs, null);
	}

	// ==========================================================================
	// ============================= Instance Methods
	// ===========================
	// ==========================================================================
	public void addToPropertyListAttributes(String name, String pListName) {
		_propertyListAttributes.put(name, pListName);
	}

	public void addToMultiValuesAttributes(String name) {
		_multiValuesAttributes.add(name);
	}

	public boolean isPropertyListAttribute(String attName) {
		return _propertyListAttributes.contains(attName);
	}

	public boolean isMultiValuesAttribute(String attName) {
		return _multiValuesAttributes.contains(attName);
	}

	public String propListNameForAttName(String attName) {
		return _propertyListAttributes.get(attName);
	}

	private InspectorModelView getInspectorTabPanel() {
		if (_inpectorPanel == null) {
			if (logger.isLoggable(Level.FINE)) {
				logger.fine("Building new InspectorModelView");
			}
			_inpectorPanel = new InspectorModelView(getInspectorModel(), this);
			_inpectorPanel.setInspectedObject(_prefs);
		}
		return _inpectorPanel;
	}

	private InspectorModelView rebuildTabPanel() {
		if (logger.isLoggable(Level.FINE)) {
			logger.fine("rebuildTabPanelForInspectable()");
		}
		FlexoInspectorController.cleanInspectorModel(getInspectorModel());
		_inpectorPanel = new InspectorModelView(getInspectorModel(), this);
		_inpectorPanel.setInspectedObject(_prefs);
		update();
		return _inpectorPanel;
	}

	public InspectorModel getInspectorModel() {
		if (logger.isLoggable(Level.FINER)) {
			if (logger.isLoggable(Level.FINE)) {
				logger.finer(_inspector.toString());
			}
		}
		return _inspector;
	}

	@Override
	public InspectorDelegate getDelegate() {
		// Handle object with key-value coding
		return null;
	}

	@Override
	public HelpDelegate getHelpDelegate() {
		// No help for this kind of controller
		return null;
	}

	@Override
	public void notifiedActiveTabChange(String newActiveTabName) {
		// Dont care
	}

	@Override
	public void notifiedInspectedObjectChange(InspectableObject newInspectedObject) {
		// Dont care
	}

	@Override
	public PreferencesController getController() {
		return this;
	}

	@Override
	public void newSelection(InspectorSelection selection) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean isTabPanelVisible(TabModel tab, InspectableObject inspectable) {
		return true;
	}

	@Override
	public InspectorWidgetConfiguration getConfiguration() {
		if (inspectorConfiguration == null) {
			inspectorConfiguration = new InspectorWidgetConfiguration() {

				@Override
				public boolean showViewSourceButtonInWysiwyg() {
					return UserType.isDevelopperRelease() || UserType.isMaintainerRelease();
				}

			};
		}
		return null;
	}

	@Override
	public boolean handleException(InspectableObject inspectable, String propertyName, Object value, Throwable exception) {
		// TODO Auto-generated method stub
		return false;
	}

}
