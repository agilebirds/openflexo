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
package org.openflexo.dre.controller;

/*
 * Created on <date> by <yourname>
 *
 * Flexo Application Suite
 * (c) Denali 2003-2006
 */
import java.awt.FlowLayout;
import java.util.Vector;
import java.util.logging.Logger;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;

import org.openflexo.dre.AbstractDocItemView;
import org.openflexo.dre.DREBrowser;
import org.openflexo.dre.controller.action.DREControllerActionInitializer;
import org.openflexo.dre.view.DREMainPane;
import org.openflexo.dre.view.DocItemView;
import org.openflexo.dre.view.menu.DREMenuBar;
import org.openflexo.drm.DRMObject;
import org.openflexo.drm.DocItem;
import org.openflexo.drm.DocItemFolder;
import org.openflexo.drm.DocResourceManager;
import org.openflexo.drm.action.GenerateHelpSet;
import org.openflexo.drm.action.SaveDocumentationCenter;
import org.openflexo.drm.dm.DocResourceCenterIsModified;
import org.openflexo.drm.dm.DocResourceCenterIsSaved;
import org.openflexo.foundation.DataModification;
import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.FlexoObservable;
import org.openflexo.foundation.GraphicalFlexoObserver;
import org.openflexo.foundation.action.FlexoActionSource;
import org.openflexo.foundation.rm.FlexoProject;
import org.openflexo.foundation.validation.ValidationModel;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.module.FlexoModule;
import org.openflexo.selection.SelectionManager;
import org.openflexo.view.FlexoMainPane;
import org.openflexo.view.ModuleView;
import org.openflexo.view.controller.ControllerActionInitializer;
import org.openflexo.view.controller.FlexoController;
import org.openflexo.view.controller.action.EditionAction;
import org.openflexo.view.controller.model.FlexoPerspective;
import org.openflexo.view.listener.FlexoActionButton;
import org.openflexo.view.menu.FlexoMenuBar;

/**
 * Controller for this module
 * 
 * @author yourname
 */

public class DREController extends FlexoController implements FlexoActionSource {

	static final Logger logger = Logger.getLogger(DREController.class.getPackage().getName());

	public final FlexoPerspective DRE_PERSPECTIVE = new DREPerspective(this);

	private DREBrowser _browser;

	// ================================================
	// ================ Constructor ===================
	// ================================================

	/**
	 * Default constructor
	 */
	public DREController(FlexoModule module) {
		super(module);
	}

	@Override
	protected void initializePerspectives() {
		_browser = new DREBrowser(this);
		addToPerspectives(DRE_PERSPECTIVE);
	}

	@Override
	protected SelectionManager createSelectionManager() {
		return new DRESelectionManager(this);
	}

	@Override
	public ControllerActionInitializer createControllerActionInitializer() {
		return new DREControllerActionInitializer(this);
	}

	/**
	 * Creates a new instance of MenuBar for the module this controller refers to
	 * 
	 * @return
	 */
	@Override
	protected FlexoMenuBar createNewMenuBar() {
		return new DREMenuBar(this);
	}

	@Override
	protected FlexoMainPane createMainPane() {
		return new DREMainPane(this);
	}

	@Override
	public FlexoModelObject getDefaultObjectToSelect(FlexoProject project) {
		return null;
	}

	protected DocItemView docItemView;

	@Override
	public ModuleView<?> moduleViewForObject(FlexoModelObject object, boolean recalculateViewIfRequired) {
		ModuleView<?> returned = super.moduleViewForObject(object, recalculateViewIfRequired);
		if (returned instanceof AbstractDocItemView) {
			((AbstractDocItemView) returned).setDocItem((DocItem) object);
		}
		return returned;
	}

	public DocResourceManager getDocResourceManager() {
		return DocResourceManager.instance();
	}

	public DREBrowser getDREBrowser() {
		return _browser;
	}

	private JButton _saveDocumentationCenterButton;
	private JButton _generateHelpSetButton;
	private JPanel _customActionPanel;

	/**
	 * Returns a custom component to be added to control panel in main pane
	 * 
	 * @return
	 */
	public JComponent getAdditionalActionPanel() {
		if (_customActionPanel == null) {
			_customActionPanel = new JPanel(new FlowLayout());
			_generateHelpSetButton = new FlexoActionButton(GenerateHelpSet.actionType, this, this);
			_generateHelpSetButton.setText(FlexoLocalization.localizedForKey("generate", _generateHelpSetButton));
			_customActionPanel.add(_generateHelpSetButton);
			_customActionPanel.add(getSaveDocumentationCenterButton());
		}
		return _customActionPanel;
	}

	protected class SaveButton extends JButton implements GraphicalFlexoObserver {
		protected SaveButton() {
			super();
			setAction(new EditionAction<SaveDocumentationCenter, DRMObject, DRMObject>(SaveDocumentationCenter.actionType,
					DREController.this));
			setText(FlexoLocalization.localizedForKey("save", this));
			getDocResourceManager().getDocResourceCenter().addObserver(this);
		}

		@Override
		public void update(FlexoObservable observable, DataModification dataModification) {
			if (dataModification instanceof DocResourceCenterIsModified) {
				logger.fine("Update 'save' button with DocResourceCenterIsModified");
				setEnabled(true);
			}
			if (dataModification instanceof DocResourceCenterIsSaved) {
				logger.fine("Update 'save' button with DocResourceCenterIsSaved");
				setEnabled(false);
			}
		}
	}

	public JButton getSaveDocumentationCenterButton() {
		if (_saveDocumentationCenterButton == null) {
			_saveDocumentationCenterButton = new SaveButton();
			// _saveDocumentationCenterButton.setAction(SaveDocumentationCenter.actionType);
			// _saveDocumentationCenterButton.setText(FlexoLocalization.localizedForKey("save",_saveDocumentationCenterButton));
		}
		return _saveDocumentationCenterButton;
	}

	@Override
	public String getWindowTitleforObject(FlexoModelObject object) {
		// Overriden to improve performance !!!!
		if (object instanceof DocItem) {
			return AbstractDocItemView.getTitleForDocItem((DocItem) object);
		} else if (object instanceof DocItemFolder) {
			if (((DocItemFolder) object).isRootFolder()) {
				return FlexoLocalization.localizedForKey("flexo_documentation_resource_center");
			}
			return ((DocItemFolder) object).getIdentifier();
		}
		return null;
	}

	// ================================================
	// ============ Validation management =============
	// ================================================

	@Override
	public ValidationModel getDefaultValidationModel() {
		return getDocResourceManager().getDRMValidationModel();
	}

	/**
	 * Overrides getFocusedObject
	 * 
	 * @see org.openflexo.foundation.action.FlexoActionSource#getFocusedObject()
	 */
	@Override
	public FlexoModelObject getFocusedObject() {
		return getSelectionManager().getFocusedObject();
	}

	/**
	 * Overrides getGlobalSelection
	 * 
	 * @see org.openflexo.foundation.action.FlexoActionSource#getGlobalSelection()
	 */
	@Override
	public Vector<FlexoModelObject> getGlobalSelection() {
		return getSelectionManager().getSelection();
	}
}
