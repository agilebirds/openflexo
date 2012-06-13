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

import org.openflexo.FlexoCst;
import org.openflexo.dre.AbstractDocItemView;
import org.openflexo.dre.DREBrowser;
import org.openflexo.dre.controller.action.DREControllerActionInitializer;
import org.openflexo.dre.view.DREFrame;
import org.openflexo.dre.view.DREMainPane;
import org.openflexo.dre.view.listener.DREKeyEventListener;
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
import org.openflexo.foundation.validation.ValidationModel;
import org.openflexo.inspector.InspectableObject;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.module.FlexoModule;
import org.openflexo.selection.SelectionManager;
import org.openflexo.view.FlexoMainPane;
import org.openflexo.view.FlexoPerspective;
import org.openflexo.view.ModuleView;
import org.openflexo.view.controller.ConsistencyCheckingController;
import org.openflexo.view.controller.ControllerActionInitializer;
import org.openflexo.view.controller.FlexoController;
import org.openflexo.view.controller.SelectionManagingController;
import org.openflexo.view.listener.FlexoActionButton;
import org.openflexo.view.menu.FlexoMenuBar;

/**
 * Controller for this module
 * 
 * @author yourname
 */
public class DREController extends FlexoController implements SelectionManagingController, ConsistencyCheckingController, FlexoActionSource {

	static final Logger logger = Logger.getLogger(DREController.class.getPackage().getName());

	// ================================================
	// ============= Instance variables ===============
	// ================================================

	// private DREMainPane _mainPane;

	public final FlexoPerspective DRE_PERSPECTIVE = new DREPerspective(this);

	protected DREMenuBar _DREMenuBar;

	protected DREFrame _frame;

	protected DREKeyEventListener _DREKeyEventListener;

	private DRESelectionManager _selectionManager;

	private DREBrowser _browser;

	// ================================================
	// ================ Constructor ===================
	// ================================================

	/**
	 * Default constructor
	 */
	public DREController(FlexoModule module) throws Exception {
		super(module.getEditor(), module);
		addToPerspectives(DRE_PERSPECTIVE);
		setDefaultPespective(DRE_PERSPECTIVE);
		_DREMenuBar = (DREMenuBar) createAndRegisterNewMenuBar();
		_DREKeyEventListener = new DREKeyEventListener(this);
		_frame = new DREFrame(FlexoCst.BUSINESS_APPLICATION_VERSION_NAME, this, _DREKeyEventListener, _DREMenuBar);
		init(_frame, _DREKeyEventListener, _DREMenuBar);

		// At this point the InspectorController is not yet loaded
		_selectionManager = new DRESelectionManager(this);

		_browser = new DREBrowser(this);

		DocResourceManager.instance().setEditor(getEditor());
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

	/**
	 * Init inspectors
	 */
	@Override
	public void initInspectors() {
		super.initInspectors();
		_selectionManager.addObserver(getSharedInspectorController());
	}

	public void loadRelativeWindows() {
		// Build eventual relative windows
	}

	// ================================================
	// ============== Instance method =================
	// ================================================

	public DREFrame getMainFrame() {
		return _frame;
	}

	public DREMenuBar getEditorMenuBar() {
		return _DREMenuBar;
	}

	public void showBrowser() {
		if (getMainPane() != null) {
			((DREMainPane) getMainPane()).showBrowser();
		}
	}

	public void hideBrowser() {
		if (getMainPane() != null) {
			((DREMainPane) getMainPane()).hideBrowser();
		}
	}

	@Override
	protected FlexoMainPane createMainPane() {
		return new DREMainPane(getEmptyPanel(), getMainFrame(), this);
	}

	protected AbstractDocItemView docItemView;

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

	public DREKeyEventListener getKeyEventListener() {
		return _DREKeyEventListener;
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
			_generateHelpSetButton = new FlexoActionButton(GenerateHelpSet.actionType, this, getEditor());
			_generateHelpSetButton.setText(FlexoLocalization.localizedForKey("generate", _generateHelpSetButton));
			_customActionPanel.add(_generateHelpSetButton);
			_customActionPanel.add(getSaveDocumentationCenterButton());
		}
		return _customActionPanel;
	}

	protected class SaveButton extends JButton implements GraphicalFlexoObserver {
		protected SaveButton() {
			super();
			setAction(SaveDocumentationCenter.actionType);
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

	// ================================================
	// ============ Selection management ==============
	// ================================================

	@Override
	public SelectionManager getSelectionManager() {
		return getDRESelectionManager();
	}

	public DRESelectionManager getDRESelectionManager() {
		return _selectionManager;
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
		// TODO: Implements this
		setCurrentEditedObjectAsModuleView(object);
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
	// ============ Exception management ==============
	// ================================================

	@Override
	public boolean handleException(InspectableObject inspectable, String propertyName, Object value, Throwable exception) {
		// TODO: Handles here exceptions that may be thrown through the inspector
		return super.handleException(inspectable, propertyName, value, exception);
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
		return getDRESelectionManager().getFocusedObject();
	}

	/**
	 * Overrides getGlobalSelection
	 * 
	 * @see org.openflexo.foundation.action.FlexoActionSource#getGlobalSelection()
	 */
	@Override
	public Vector getGlobalSelection() {
		return getDRESelectionManager().getSelection();
	}
}
