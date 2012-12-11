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
package org.openflexo.view.controller;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.HeadlessException;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.File;
import java.io.IOException;
import java.net.ConnectException;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.rmi.RemoteException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;

import javax.naming.InvalidNameException;
import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.KeyStroke;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.xml.rpc.ServiceException;

import org.openflexo.AdvancedPrefs;
import org.openflexo.ApplicationContext;
import org.openflexo.FlexoCst;
import org.openflexo.GeneralPreferences;
import org.openflexo.antar.binding.TypeUtils;
import org.openflexo.components.ProgressWindow;
import org.openflexo.components.validation.ConsistencyCheckDialog;
import org.openflexo.fib.FIBLibrary;
import org.openflexo.fib.controller.FIBController.Status;
import org.openflexo.fib.controller.FIBDialog;
import org.openflexo.foundation.DataModification;
import org.openflexo.foundation.DocType;
import org.openflexo.foundation.FlexoEditor;
import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.FlexoObservable;
import org.openflexo.foundation.FlexoObserver;
import org.openflexo.foundation.FlexoServiceManager;
import org.openflexo.foundation.InspectorGroup;
import org.openflexo.foundation.Inspectors;
import org.openflexo.foundation.action.FlexoActionType;
import org.openflexo.foundation.action.SetPropertyAction;
import org.openflexo.foundation.cg.templates.CGTemplateObject;
import org.openflexo.foundation.dm.DMObject;
import org.openflexo.foundation.dm.DuplicateClassNameException;
import org.openflexo.foundation.ie.IEObject;
import org.openflexo.foundation.ie.IEWOComponent;
import org.openflexo.foundation.ie.cl.ComponentDefinition;
import org.openflexo.foundation.ontology.IFlexoOntologyObject;
import org.openflexo.foundation.resource.RepositoryFolder;
import org.openflexo.foundation.rm.DuplicateResourceException;
import org.openflexo.foundation.rm.FlexoProject;
import org.openflexo.foundation.rm.ProjectClosedNotification;
import org.openflexo.foundation.rm.ViewPointResource;
import org.openflexo.foundation.technologyadapter.FlexoMetaModel;
import org.openflexo.foundation.technologyadapter.FlexoMetaModelResource;
import org.openflexo.foundation.technologyadapter.FlexoModel;
import org.openflexo.foundation.technologyadapter.FlexoModelResource;
import org.openflexo.foundation.technologyadapter.TechnologyAdapter;
import org.openflexo.foundation.toc.TOCObject;
import org.openflexo.foundation.utils.FlexoProgress;
import org.openflexo.foundation.validation.Validable;
import org.openflexo.foundation.validation.ValidationModel;
import org.openflexo.foundation.validation.ValidationRule;
import org.openflexo.foundation.validation.ValidationRuleSet;
import org.openflexo.foundation.view.AbstractViewObject;
import org.openflexo.foundation.viewpoint.ViewPointLibraryObject;
import org.openflexo.foundation.wkf.FlexoProcess;
import org.openflexo.foundation.wkf.WKFObject;
import org.openflexo.foundation.wkf.WorkflowModelObject;
import org.openflexo.foundation.wkf.node.OperationNode;
import org.openflexo.foundation.wkf.utils.OperationAssociatedWithComponentSuccessfully;
import org.openflexo.icon.CGIconLibrary;
import org.openflexo.icon.DEIconLibrary;
import org.openflexo.icon.DGIconLibrary;
import org.openflexo.icon.DMEIconLibrary;
import org.openflexo.icon.IconFactory;
import org.openflexo.icon.IconLibrary;
import org.openflexo.icon.IconMarker;
import org.openflexo.icon.SEIconLibrary;
import org.openflexo.icon.VEIconLibrary;
import org.openflexo.icon.VPMIconLibrary;
import org.openflexo.icon.WKFIconLibrary;
import org.openflexo.inspector.InspectableObject;
import org.openflexo.inspector.InspectorDelegate;
import org.openflexo.inspector.InspectorExceptionHandler;
import org.openflexo.inspector.InspectorNotFoundHandler;
import org.openflexo.inspector.InspectorSinglePanel;
import org.openflexo.inspector.InspectorWindow;
import org.openflexo.inspector.ModuleInspectorController;
import org.openflexo.inspector.selection.EmptySelection;
import org.openflexo.kvc.KeyValueCoding;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.model.exceptions.ModelDefinitionException;
import org.openflexo.model.factory.ModelFactory;
import org.openflexo.module.FlexoModule;
import org.openflexo.module.ModuleLoader;
import org.openflexo.module.ProjectLoader;
import org.openflexo.prefs.PreferencesController;
import org.openflexo.prefs.PreferencesHaveChanged;
import org.openflexo.prefs.PreferencesWindow;
import org.openflexo.rm.ResourceManagerWindow;
import org.openflexo.selection.SelectionManager;
import org.openflexo.toolbox.FileResource;
import org.openflexo.toolbox.HasPropertyChangeSupport;
import org.openflexo.toolbox.PropertyChangeListenerRegistrationManager;
import org.openflexo.toolbox.ToolBox;
import org.openflexo.utils.CancelException;
import org.openflexo.utils.TooManyFailedAttemptException;
import org.openflexo.view.FlexoDialog;
import org.openflexo.view.FlexoFrame;
import org.openflexo.view.FlexoMainPane;
import org.openflexo.view.FlexoRelativeWindow;
import org.openflexo.view.ModuleView;
import org.openflexo.view.controller.WebServiceURLDialog.PPMWSClientParameter;
import org.openflexo.view.controller.model.ControllerModel;
import org.openflexo.view.controller.model.FlexoPerspective;
import org.openflexo.view.menu.FlexoMenuBar;
import org.openflexo.ws.client.PPMWebService.PPMWebServiceAuthentificationException;
import org.openflexo.ws.client.PPMWebService.PPMWebServiceClient;

/**
 * Abstract controller defined for an application module
 * 
 * @author benoit, sylvain
 */
public abstract class FlexoController implements FlexoObserver, InspectorNotFoundHandler, InspectorExceptionHandler,
		PropertyChangeListener, HasPropertyChangeSupport {

	static final Logger logger = Logger.getLogger(FlexoController.class.getPackage().getName());

	public static final String DISPOSED = "disposed";

	public static boolean USE_NEW_INSPECTOR_SCHEME = false;
	public static boolean USE_OLD_INSPECTOR_SCHEME = true;

	public static final String MODULE_VIEWS = "moduleViews";

	public boolean useNewInspectorScheme() {
		return USE_NEW_INSPECTOR_SCHEME;
	}

	public boolean useOldInspectorScheme() {
		return USE_OLD_INSPECTOR_SCHEME;
	}

	private PropertyChangeSupport propertyChangeSupport;

	private boolean disposed = false;

	private final Map<FlexoPerspective, Map<FlexoProject, Map<FlexoModelObject, ModuleView<?>>>> loadedViews;

	private final List<ModuleView<?>> moduleViews;

	private ConsistencyCheckDialog consistencyCheckWindow;

	protected FlexoModule module;

	protected FlexoMenuBar menuBar;

	protected SelectionManager selectionManager;

	private ControllerActionInitializer controllerActionInitializer;

	protected FlexoFrame flexoFrame;

	private FlexoMainPane mainPane;

	private ResourceManagerWindow rmWindow;

	private ControllerModel controllerModel;

	private final List<FlexoMenuBar> registeredMenuBar = new ArrayList<FlexoMenuBar>();

	private FlexoDocInspectorController docInspectorController = null;

	private FlexoSharedInspectorController inspectorController;

	private ModuleInspectorController mainInspectorController;

	protected PropertyChangeListenerRegistrationManager manager = new PropertyChangeListenerRegistrationManager();

	/**
	 * Constructor
	 */
	protected FlexoController(FlexoModule module) {
		super();
		ProgressWindow.setProgressInstance(FlexoLocalization.localizedForKey("init_module_controller"));
		this.module = module;
		loadedViews = new Hashtable<FlexoPerspective, Map<FlexoProject, Map<FlexoModelObject, ModuleView<?>>>>();
		moduleViews = new ArrayList<ModuleView<?>>();
		controllerModel = new ControllerModel(module.getApplicationContext(), module);
		propertyChangeSupport = new PropertyChangeSupport(this);
		manager.new PropertyChangeListenerRegistration(ProjectLoader.EDITOR_ADDED, this, getProjectLoader());
		manager.new PropertyChangeListenerRegistration(this, controllerModel);
		flexoFrame = createFrame();
		controllerActionInitializer = createControllerActionInitializer();
		registerShortcuts(controllerActionInitializer);
		menuBar = createAndRegisterNewMenuBar();
		selectionManager = createSelectionManager();
		flexoFrame.setJMenuBar(menuBar);
		flexoFrame.getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
				.put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "escape");
		flexoFrame.getRootPane().getActionMap().put("escape", new AbstractAction() {

			@Override
			public void actionPerformed(ActionEvent e) {
				cancelCurrentAction();
			}
		});
		mainPane = createMainPane();
		getFlexoFrame().getContentPane().add(mainPane, BorderLayout.CENTER);
		initInspectors();
		initializePerspectives();
		if (getModule().getModule().requireProject()) {
			if (getModuleLoader().getLastActiveEditor() != null) {
				controllerModel.setCurrentEditor(getModuleLoader().getLastActiveEditor());
			}
		} else {
			controllerModel.setCurrentEditor(getApplicationContext().getApplicationEditor());
		}
		GeneralPreferences.getPreferences().addObserver(this);
	}

	protected abstract void initializePerspectives();

	public final ControllerModel getControllerModel() {
		return controllerModel;
	}

	protected abstract SelectionManager createSelectionManager();

	/**
	 * Creates a new instance of MenuBar for the module this controller refers to
	 * 
	 * @return
	 */
	protected abstract FlexoMenuBar createNewMenuBar();

	protected FlexoFrame createFrame() {
		return new FlexoFrame(this);
	}

	public final SelectionManager getSelectionManager() {
		return selectionManager;
	}

	public final FlexoMenuBar getMenuBar() {
		return menuBar;
	}

	public final ApplicationContext getApplicationContext() {
		return getModule().getApplicationContext();
	}

	public final ProjectLoader getProjectLoader() {
		return getApplicationContext().getProjectLoader();
	}

	public final ModuleLoader getModuleLoader() {
		return getApplicationContext().getModuleLoader();
	}

	/**
	 * Creates a new instance of MenuBar for the module this controller refers to
	 * 
	 * @return
	 */
	public ControllerActionInitializer createControllerActionInitializer() {
		return new ControllerActionInitializer(this);
	}

	public ControllerActionInitializer getControllerActionInitializer() {
		return controllerActionInitializer;
	}

	/**
	 * Creates and register a new instance of MenuBar for the module this controller refers to
	 * 
	 * @return
	 */
	public final FlexoMenuBar createAndRegisterNewMenuBar() {
		FlexoMenuBar returned = createNewMenuBar();
		registeredMenuBar.add(returned);
		if (getFlexoFrame() != null) {
			for (FlexoRelativeWindow next : getFlexoFrame().getRelativeWindows()) {
				returned.getWindowMenu().addFlexoRelativeWindowMenu(next);
			}
		}
		return returned;
	}

	public void notifyNewFlexoRelativeWindow(FlexoRelativeWindow w) {
		for (FlexoMenuBar next : registeredMenuBar) {
			next.getWindowMenu().addFlexoRelativeWindowMenu(w);
		}
	}

	public void notifyRemoveFlexoRelativeWindow(FlexoRelativeWindow w) {
		for (FlexoMenuBar next : registeredMenuBar) {
			next.getWindowMenu().removeFlexoRelativeWindowMenu(w);
		}
	}

	public void notifyRenameFlexoRelativeWindow(FlexoRelativeWindow w, String title) {
		for (FlexoMenuBar next : registeredMenuBar) {
			next.getWindowMenu().renameFlexoRelativeWindowMenu(w, title);
		}
	}

	/**
	 *
	 */
	public void initInspectors() {
		if (useOldInspectorScheme()) {
			if (inspectorController == null) {
				inspectorController = new FlexoSharedInspectorController(this);
			}
			/*
			 * if (getInspectorWindow() != null) { getInspectorWindow().setAlwaysOnTop(GeneralPreferences.getInspectorAlwaysOnTop()); }
			 */
			getSelectionManager().addObserver(getSharedInspectorController());
			getSharedInspectorController().addInspectorExceptionHandler(this);
			loadAllModuleInspectors();
		}
		if (useNewInspectorScheme()) {
			loadInspectorGroup(getModule().getShortName().toUpperCase());
			getSelectionManager().addObserver(getModuleInspectorController());
		}
		docInspectorController = new FlexoDocInspectorController(this);
		getSelectionManager().addObserver(docInspectorController);
	}

	public ModuleInspectorController getModuleInspectorController() {
		if (mainInspectorController == null) {
			mainInspectorController = new ModuleInspectorController(this);
		}
		return mainInspectorController;
	}

	protected void loadInspectorGroup(String inspectorGroup) {
		File inspectorsDir = new FileResource("Inspectors/" + inspectorGroup);
		getModuleInspectorController().loadDirectory(inspectorsDir);
	}

	public FlexoDocInspectorController getDocInspectorController() {
		return docInspectorController;
	}

	public InspectorSinglePanel getDocInspectorPanel() {
		if (getDocInspectorController() != null) {
			return getDocInspectorController().getDocInspectorPanel();
		}
		return null;
	}

	/**
	 * Return doc inspector panel, after having it disconnected from its actual parent
	 * 
	 * @return
	 */
	public final JPanel getDisconnectedDocInspectorPanel() {
		/*if (getDocInspectorPanel().getParent() != null) {
			getDocInspectorPanel().getParent().remove(getDocInspectorPanel());
		}*/
		return getDocInspectorPanel();
	}

	public FlexoFrame getFlexoFrame() {
		return flexoFrame;
	}

	public FlexoModule getModule() {
		return module;
	}

	protected final void setEditor(FlexoEditor editor) {
		controllerModel.setCurrentEditor(editor);
	}

	protected void updateEditor(FlexoEditor from, FlexoEditor to) {
		if (from instanceof InteractiveFlexoEditor) {
			((InteractiveFlexoEditor) from).unregisterControllerActionInitializer(getControllerActionInitializer());
		}
		if (to instanceof InteractiveFlexoEditor) {
			((InteractiveFlexoEditor) to).registerControllerActionInitializer(getControllerActionInitializer());
		}
	}

	public abstract FlexoModelObject getDefaultObjectToSelect(FlexoProject project);

	public FlexoProject getProject() {
		if (getEditor() != null) {
			return getEditor().getProject();
		}
		return null;
	}

	public File getProjectDirectory() {
		return getProject().getProjectDirectory();
	}

	public FlexoSharedInspectorController getSharedInspectorController() {
		return inspectorController;
	}

	public InspectorWindow getInspectorWindow() {
		if (getSharedInspectorController() != null) {
			return getSharedInspectorController().getInspectorWindow();
		}
		return null;
	}

	private FlexoMenuBar inspectorMenuBar;

	public FlexoMenuBar getInspectorMenuBar() {
		if (inspectorMenuBar == null) {
			inspectorMenuBar = createAndRegisterNewMenuBar();
		}
		return inspectorMenuBar;
	}

	public static void showError(String msg) throws HeadlessException {
		showError(FlexoLocalization.localizedForKey("error"), msg);
	}

	public static void showError(String title, String msg) throws HeadlessException {
		showMessageDialog(msg, title, JOptionPane.ERROR_MESSAGE);
	}

	public static void notify(String msg) throws HeadlessException {
		showMessageDialog(msg, FlexoLocalization.localizedForKey("confirmation"), JOptionPane.INFORMATION_MESSAGE);
	}

	public static boolean notifyWithCheckbox(String title, String msg, String checkboxText, boolean defaultValue) {
		JPanel root = new JPanel(new BorderLayout());
		Icon msgIcon = UIManager.getDefaults().getIcon("OptionPane.informationIcon");
		JLabel notifyIcon = new JLabel(msgIcon);
		notifyIcon.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		JLabel label = new JLabel(msg);
		label.setBorder(BorderFactory.createEmptyBorder(10, 10, 0, 10));
		label.setHorizontalAlignment(SwingConstants.LEFT);
		JCheckBox box = new JCheckBox(checkboxText, defaultValue);
		box.setBorder(BorderFactory.createEmptyBorder(1, 20, 10, 10));
		JPanel centerPanel = new JPanel(new BorderLayout());
		centerPanel.add(label, BorderLayout.NORTH);
		centerPanel.add(box, BorderLayout.SOUTH);
		JButton ok = new JButton("ok"/*FlexoLocalization.localizedForKey("ok")*/);
		JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
		buttonPanel.add(ok);
		root.add(notifyIcon, BorderLayout.WEST);
		root.add(centerPanel, BorderLayout.CENTER);
		root.add(buttonPanel, BorderLayout.SOUTH);
		final FlexoDialog dialog = new FlexoDialog();
		if (title != null) {
			dialog.setTitle(title);
		}
		ok.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				dialog.dispose();
			}

		});
		dialog.setResizable(false);
		dialog.add(root);
		dialog.pack();
		dialog.setVisible(true);
		return box.isSelected();
	}

	public static int ask(String msg) throws HeadlessException {
		return showConfirmDialog(msg, FlexoLocalization.localizedForKey("information"), JOptionPane.YES_NO_OPTION,
				JOptionPane.QUESTION_MESSAGE);
	}

	public static boolean confirmWithWarning(String msg) throws HeadlessException {
		return showOptionDialog(FlexoFrame.getActiveFrame(), msg, FlexoLocalization.localizedForKey("information"),
				JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE, null, new Object[] { FlexoLocalization.localizedForKey("yes"),
						FlexoLocalization.localizedForKey("no") }, FlexoLocalization.localizedForKey("no")) == JOptionPane.YES_OPTION;
	}

	public static boolean confirm(String msg) throws HeadlessException {
		return ask(msg) == JOptionPane.YES_OPTION;
	}

	public static int confirmYesNoCancel(String localizedMessage) {
		return showOptionDialog(FlexoFrame.getActiveFrame(), localizedMessage, localizedMessage, JOptionPane.YES_NO_CANCEL_OPTION,
				JOptionPane.QUESTION_MESSAGE, null, null, null);
	}

	public static String askForString(String msg) throws HeadlessException {
		return showInputDialog(msg, FlexoLocalization.localizedForKey("information"), JOptionPane.QUESTION_MESSAGE);
	}

	public static String askForString(Component parentComponent, String msg) throws HeadlessException {
		return showInputDialog(parentComponent, msg, FlexoLocalization.localizedForKey("information"), JOptionPane.OK_CANCEL_OPTION);
	}

	public static String askForStringMatchingPattern(String msg, Pattern pattern, String localizedPattern) {
		String result = askForString(msg);
		while (result != null && !pattern.matcher(result).matches()) {
			notify(localizedPattern);
			result = askForString(msg);
		}
		return result;
	}

	public static int selectOption(String msg, String[] options, String initialOption) {
		return showOptionDialog(FlexoFrame.getActiveFrame(), msg, FlexoLocalization.localizedForKey("confirmation"),
				JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, initialOption);
	}

	public static int selectOption(String msg, String initialOption, String... options) {
		return showOptionDialog(FlexoFrame.getActiveFrame(), msg, FlexoLocalization.localizedForKey("confirmation"),
				JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, initialOption);
	}

	public void showInspector() {
		if (useOldInspectorScheme()) {
			/*
			 * if (!getInspectorWindow().isActive()) { int state = getInspectorWindow().getExtendedState(); state &= ~Frame.ICONIFIED;
			 * getInspectorWindow().setExtendedState(state); }
			 */

			getInspectorWindow().setVisible(true);
		}

		if (useNewInspectorScheme()) {
			getModuleInspectorController().getInspectorDialog().setVisible(true);
		}

	}

	public void resetInspector() {

		if (useOldInspectorScheme()) {
			getInspectorWindow().newSelection(new EmptySelection());
		} else {
			getModuleInspectorController().resetInspector();
		}
	}

	public PreferencesWindow getPreferencesWindow() {
		return PreferencesController.instance().getPreferencesWindow();
	}

	public void showPreferences() {
		PreferencesController.instance().showPreferences();
	}

	public void registerShortcuts(ControllerActionInitializer controllerInitializer) {
		for (final Entry<FlexoActionType<?, ?, ?>, ActionInitializer<?, ?, ?>> entry : controllerInitializer.getActionInitializers()
				.entrySet()) {
			KeyStroke accelerator = entry.getValue().getShortcut();
			if (accelerator != null) {
				registerActionForKeyStroke(new AbstractAction() {

					@Override
					public void actionPerformed(ActionEvent e) {
						FlexoModelObject focusedObject = getSelectionManager().getFocusedObject();
						Vector<FlexoModelObject> globalSelection = getSelectionManager().getSelection();
						FlexoActionType actionType = entry.getKey();
						if (TypeUtils.isAssignableTo(focusedObject, actionType.getFocusedObjectType())
								&& (globalSelection == null || TypeUtils.isAssignableTo(globalSelection,
										actionType.getGlobalSelectionType()))) {
							getEditor().performActionType(actionType, focusedObject, globalSelection, e);
						}
					}
				}, accelerator, entry.getKey().getUnlocalizedName());
			}
		}
	}

	public void registerActionForKeyStroke(AbstractAction action, KeyStroke accelerator, String actionName) {
		String key = actionName;
		getFlexoFrame().getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(accelerator, key);
		getFlexoFrame().getRootPane().getActionMap().put(key, action);
		if (accelerator.getKeyCode() == FlexoCst.DELETE_KEY_CODE) {
			int keyCode = FlexoCst.BACKSPACE_DELETE_KEY_CODE;
			getFlexoFrame().getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
					.put(KeyStroke.getKeyStroke(keyCode, accelerator.getModifiers()), key);
		}
	}

	protected void loadAllModuleInspectors() {
		// Load flexo inspectors
		if (getModule().getInspectorGroups() != null) {
			ProgressWindow.setProgressInstance(FlexoLocalization.localizedForKey("load_inspectors"));
			for (InspectorGroup group : getModule().getInspectorGroups()) {
				getSharedInspectorController().loadInspectors(group);
			}
			getSharedInspectorController().updateSuperInspectors();
		}
	}

	@Override
	public void inspectorNotFound(String inspectorName) {
		InspectorGroup inspectorGroup = Inspectors.inspectorGroupForInspector(inspectorName);
		if (inspectorGroup != null) {
			boolean openedWindow = false;
			if (!ProgressWindow.hasInstance()) {
				ProgressWindow.showProgressWindow(FlexoLocalization.localizedForKey("load_required_inspectors"), 3);
				openedWindow = true;
			}
			ProgressWindow.setProgressInstance(FlexoLocalization.localizedForKey("load_required_inspectors"));
			getSharedInspectorController().loadInspectors(inspectorGroup);
			ProgressWindow.setProgressInstance(FlexoLocalization.localizedForKey("update_inspector"));
			getSharedInspectorController().updateSuperInspectors();
			if (openedWindow) {
				ProgressWindow.hideProgressWindow();
			}
		}
	}

	/**
	 * Tries to handle an exception raised during object inspection.<br>
	 * 
	 * @param inspectable
	 *            the object on which exception was raised
	 * @param propertyName
	 *            the concerned property name
	 * @param value
	 *            the value that raised an exception
	 * @param exception
	 *            the exception that was raised
	 * @return a boolean indicating if this handler has handled this exception, or not
	 */
	@Override
	public boolean handleException(InspectableObject inspectable, String propertyName, Object value, Throwable exception) {
		if (inspectable instanceof FlexoProcess && exception instanceof DuplicateResourceException) {
			if (propertyName.equals("name")) {
				boolean isOK = false;
				while (!isOK) {
					String newName = askForString(FlexoLocalization
							.localizedForKey("invalid_name_process_already_exists_please_choose_an_other_one"));
					if (newName != null) {
						try {
							((FlexoProcess) inspectable).setName(newName);
							isOK = true;
						} catch (DuplicateResourceException e) {
						} catch (InvalidNameException e) {
						}
					} else {
						return true;
					}

				}
				return true;
			}
		} else if (inspectable instanceof OperationNode && exception instanceof DuplicateResourceException) {
			if (propertyName.equals("WOComponentName")) {
				boolean isOK = false;
				while (!isOK) {
					String newName = askForString(FlexoLocalization
							.localizedForKey("invalid_name_component_already_exists_please_choose_an_other_one"));
					if (newName != null) {
						try {
							((OperationNode) inspectable).setWOComponentName(newName);
							isOK = true;
						} catch (DuplicateResourceException e) {
						} catch (OperationAssociatedWithComponentSuccessfully e) {
							// TODO: FIXME
							if (logger.isLoggable(Level.INFO)) {
								logger.info("FIXME: handle new component association in FlexoController.class");
							}
						}
					} else {
						return true;
					}
				}
				return true;
			}
		} else if (inspectable instanceof ComponentDefinition && exception instanceof DuplicateResourceException) {
			if (propertyName.equals("name")) {
				boolean isOK = false;
				while (!isOK) {
					String newName = askForString(FlexoLocalization
							.localizedForKey("invalid_name_component_already_exists_please_choose_an_other_one"));
					if (newName != null) {
						try {
							((ComponentDefinition) inspectable).setName(newName);
							isOK = true;
						} catch (DuplicateResourceException e) {
						} catch (DuplicateClassNameException e) {
							notify(e.getLocalizedMessage());
						} catch (InvalidNameException e) {
							notify(FlexoLocalization.localizedForKey("invalid_component_name"));
						}
					} else {
						return true;
					}
				}
				return true;
			}
		} else if ((inspectable instanceof IEWOComponent || inspectable instanceof ComponentDefinition)
				&& exception instanceof InvalidNameException) {
			if (propertyName.equals("name")) {
				notify(FlexoLocalization.localizedForKey("invalid_component_name"));
				return true;
			}
		}

		return false;
	}

	public ConsistencyCheckDialog getConsistencyCheckWindow() {
		return getConsistencyCheckWindow(true);
	}

	public ConsistencyCheckDialog getConsistencyCheckWindow(boolean create) {
		if (create && getDefaultValidationModel() != null) {
			if (consistencyCheckWindow == null || consistencyCheckWindow.isDisposed()) {
				consistencyCheckWindow = new ConsistencyCheckDialog(this);
			}
		}
		return consistencyCheckWindow;
	}

	public void consistencyCheck(Validable objectToValidate) {
		if (getDefaultValidationModel() != null) {
			initializeValidationModel();
			getConsistencyCheckWindow(true).setVisible(true);
			getConsistencyCheckWindow(true).consistencyCheck(objectToValidate);
		}
	}

	public void initializeValidationModel() {
		ValidationModel validationModel = getDefaultValidationModel();
		if (validationModel != null) {
			for (int i = 0; i < validationModel.getSize(); i++) {
				ValidationRuleSet ruleSet = validationModel.getElementAt(i);
				for (ValidationRule<?, ?> rule : ruleSet.getRules()) {
					rule.setIsEnabled(GeneralPreferences.isValidationRuleEnabled(rule));
				}
			}
		}
	}

	/**
	 * Brings up a dialog with a specified icon, where the initial choice is determined by the <code>initialValue</code> parameter and the
	 * number of choices is determined by the <code>optionType</code> parameter.
	 * <p>
	 * If <code>optionType</code> is <code>YES_NO_OPTION</code>, or <code>YES_NO_CANCEL_OPTION</code> and the <code>options</code> parameter
	 * is <code>null</code>, then the options are supplied by the look and feel.
	 * <p>
	 * The <code>messageType</code> parameter is primarily used to supply a default icon from the look and feel.
	 * 
	 * @param parentComponent
	 *            determines the <code>Frame</code> in which the dialog is displayed; if <code>null</code>, or if the
	 *            <code>parentComponent</code> has no <code>Frame</code>, a default <code>Frame</code> is used
	 * @param message
	 *            the <code>Object</code> to display
	 * @param title
	 *            the title string for the dialog
	 * @param optionType
	 *            an integer designating the options available on the dialog: <code>YES_NO_OPTION</code>, or
	 *            <code>YES_NO_CANCEL_OPTION</code>
	 * @param messageType
	 *            an integer designating the kind of message this is, primarily used to determine the icon from the pluggable Look and Feel:
	 *            <code>ERROR_MESSAGE</code>, <code>INFORMATION_MESSAGE</code>, <code>WARNING_MESSAGE</code>, <code>QUESTION_MESSAGE</code>,
	 *            or <code>PLAIN_MESSAGE</code>
	 * @param icon
	 *            the icon to display in the dialog
	 * @param options
	 *            an array of objects indicating the possible choices the user can make; if the objects are components, they are rendered
	 *            properly; non-<code>String</code> objects are rendered using their <code>toString</code> methods; if this parameter is
	 *            <code>null</code>, the options are determined by the Look and Feel
	 * @param initialValue
	 *            the object that represents the default selection for the dialog; only meaningful if <code>options</code> is used; can be
	 *            <code>null</code>
	 * @return an integer indicating the option chosen by the user, or <code>CLOSED_OPTION</code> if the user closed the dialog
	 * @exception HeadlessException
	 *                if <code>GraphicsEnvironment.isHeadless</code> returns <code>true</code>
	 * @see java.awt.GraphicsEnvironment#isHeadless
	 */
	private static synchronized int showOptionDialog(Component parentComponent, Object message, String title, int optionType,
			int messageType, Icon icon, Object[] options, Object initialValue) throws HeadlessException {
		if (parentComponent == null) {
			if (ProgressWindow.hasInstance()) {
				parentComponent = ProgressWindow.instance();
			}
		}
		final Component parent = parentComponent;
		JOptionPane pane = null;
		boolean isLocalized = false;
		Object[] availableOptions = null;
		if (optionType == JOptionPane.OK_CANCEL_OPTION && options == null) {
			availableOptions = new Object[] { FlexoLocalization.localizedForKey("OK"), FlexoLocalization.localizedForKey("cancel") };
			pane = new JOptionPane(message, messageType, optionType, icon, availableOptions, availableOptions[0]) {
				@Override
				public int getMaxCharactersPerLineCount() {
					return FlexoController.getMaxCharactersPerLine(parent, this);
				}
			};
			isLocalized = true;
			// pane.setInitialSelectionValue();
		} else if (optionType == JOptionPane.YES_NO_OPTION && options == null) {
			availableOptions = new Object[] { FlexoLocalization.localizedForKey("yes"), FlexoLocalization.localizedForKey("no") };
			pane = new JOptionPane(message, messageType, optionType, icon, availableOptions, availableOptions[0]) {
				@Override
				public int getMaxCharactersPerLineCount() {
					return FlexoController.getMaxCharactersPerLine(parent, this);
				}
			};
			isLocalized = true;
			// pane.setInitialSelectionValue(availableOptions[1]);
		} else if (optionType == JOptionPane.YES_NO_CANCEL_OPTION && options == null) {
			availableOptions = new Object[] { FlexoLocalization.localizedForKey("yes"), FlexoLocalization.localizedForKey("no"),
					FlexoLocalization.localizedForKey("cancel") };
			pane = new JOptionPane(message, messageType, optionType, icon, availableOptions, availableOptions[0]) {
				@Override
				public int getMaxCharactersPerLineCount() {
					return FlexoController.getMaxCharactersPerLine(parent, this);
				}
			};
			isLocalized = true;
			// pane.setInitialSelectionValue(availableOptions[1]);
		} else {
			pane = new JOptionPane(message, messageType, optionType, icon, options, initialValue) {
				@Override
				public int getMaxCharactersPerLineCount() {
					return FlexoController.getMaxCharactersPerLine(parent, this);
				}
			};
			pane.setInitialValue(initialValue);
		}

		pane.setComponentOrientation((parentComponent == null ? FlexoFrame.getActiveFrame() : parentComponent).getComponentOrientation());

		pane.setMessageType(messageType);
		final JDialog dialog = pane.createDialog(parentComponent, title);
		Container content = dialog.getContentPane();
		JScrollPane scroll = new JScrollPane(content, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
				ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		dialog.setContentPane(scroll);
		dialog.addKeyListener(new KeyAdapter() {
			@Override
			public void keyTyped(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
					dialog.setVisible(false);
				}
			}
		});
		dialog.validate();
		dialog.pack();
		Window window = null;
		if (parentComponent instanceof Window) {
			window = (Window) parentComponent;
		}
		if (window == null && parentComponent != null) {
			window = SwingUtilities.getWindowAncestor(parentComponent);
		}
		Dimension maxDim;
		if (window != null && window.isVisible()) {
			maxDim = new Dimension(Math.min(dialog.getWidth(), window.getGraphicsConfiguration().getDevice().getDefaultConfiguration()
					.getBounds().width), Math.min(dialog.getHeight(), window.getGraphicsConfiguration().getDevice()
					.getDefaultConfiguration().getBounds().height));
		} else {
			maxDim = new Dimension(Math.min(dialog.getWidth(), Toolkit.getDefaultToolkit().getScreenSize().width), Math.min(
					dialog.getHeight(), Toolkit.getDefaultToolkit().getScreenSize().height));
		}
		dialog.setSize(maxDim);
		if (window != null && window.isVisible()) {
			Point locationOnScreen = window.getLocationOnScreen();
			if (locationOnScreen.x < 0) {
				locationOnScreen.x = 0;
			}
			if (locationOnScreen.y < 0) {
				locationOnScreen.y = 0;
			}
			Dimension dim = new Dimension(locationOnScreen.x + window.getWidth() / 2, locationOnScreen.y + window.getHeight() / 2);
			dialog.setLocation(Math.max(dim.width - dialog.getSize().width / 2, 0), Math.max(dim.height - dialog.getSize().height / 2, 0));
		} else {
			Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
			dialog.setLocation(Math.max((dim.width - dialog.getSize().width) / 2, 0),
					Math.max((dim.height - dialog.getSize().height) / 2, 0));
		}
		pane.selectInitialValue();
		dialog.setVisible(true);
		// pane.selectInitialValue();
		dialog.dispose();
		Object selectedValue = pane.getValue();

		if (selectedValue == null) {
			return JOptionPane.CLOSED_OPTION;
		}
		if (isLocalized) {
			for (int counter = 0, maxCounter = availableOptions.length; counter < maxCounter; counter++) {
				if (optionType == JOptionPane.OK_CANCEL_OPTION) {
					if (availableOptions[counter].equals(selectedValue)) {
						if (counter == 0) {
							return JOptionPane.OK_OPTION;
						}
						if (counter == 1) {
							return JOptionPane.CANCEL_OPTION;
						}
					}
				} else if (optionType == JOptionPane.YES_NO_OPTION) {
					if (availableOptions[counter].equals(selectedValue)) {
						if (counter == 0) {
							return JOptionPane.YES_OPTION;
						}
						if (counter == 1) {
							return JOptionPane.NO_OPTION;
						}
					}
				} else if (optionType == JOptionPane.YES_NO_CANCEL_OPTION) {
					if (availableOptions[counter].equals(selectedValue)) {
						if (counter == 0) {
							return JOptionPane.YES_OPTION;
						}
						if (counter == 1) {
							return JOptionPane.NO_OPTION;
						}
						if (counter == 2) {
							return JOptionPane.CANCEL_OPTION;
						}
					}
				}
			}
		}
		if (options == null) {
			if (selectedValue instanceof Integer) {
				return ((Integer) selectedValue).intValue();
			}
			return JOptionPane.CLOSED_OPTION;
		}
		for (int counter = 0, maxCounter = options.length; counter < maxCounter; counter++) {
			if (options[counter].equals(selectedValue)) {
				return counter;
			}
		}
		return JOptionPane.CLOSED_OPTION;
	}

	protected static int getMaxCharactersPerLine(Component parent, JOptionPane pane) {
		if (pane.getMessage() instanceof String && ((String) pane.getMessage()).startsWith("<html>")) {
			return Integer.MAX_VALUE;
		}
		Window w = null;
		if (parent != null) {
			w = SwingUtilities.getWindowAncestor(parent);
		}
		int width = 0;
		if (w != null) {
			width = w.getGraphicsConfiguration().getDevice().getDisplayMode().getWidth();
		} else {
			width = Toolkit.getDefaultToolkit().getScreenSize().width;
		}
		int availableWidth = width;
		if (pane.getIcon() != null) {
			availableWidth -= pane.getIcon().getIconWidth();
		} else {
			Icon icon = UIManager.getIcon("OptionPane.errorIcon");
			availableWidth -= icon != null ? icon.getIconWidth() : 0;
		}
		return availableWidth / pane.getFontMetrics(UIManager.getFont("Label.font")).charWidth('W');
	}

	private static void showMessageDialog(Object message, String title, int messageType) throws HeadlessException {
		showMessageDialog(message, title, messageType, null);
	}

	private static void showMessageDialog(Object message, String title, int messageType, Icon icon) throws HeadlessException {
		showOptionDialog(FlexoFrame.getActiveFrame(), message, title, JOptionPane.DEFAULT_OPTION, messageType, icon, null, null);
	}

	private static int showConfirmDialog(Object message, String title, int optionType, int messageType) throws HeadlessException {
		return showConfirmDialog(message, title, optionType, messageType, null);
	}

	private static int showConfirmDialog(Object message, String title, int optionType, int messageType, Icon icon) throws HeadlessException {
		return showOptionDialog(FlexoFrame.getActiveFrame(), message, title, optionType, messageType, icon, null, null);
	}

	private static String showInputDialog(Object message, String title, int messageType) throws HeadlessException {
		return (String) showInputDialog(FlexoFrame.getActiveFrame(), message, title, messageType, null, null, null);
	}

	private static String showInputDialog(Component parentComponent, Object message, String title, int messageType)
			throws HeadlessException {
		return (String) showInputDialog(parentComponent, message, title, messageType, null, null, null);
	}

	private static Object showInputDialog(Component parentComponent, Object message, String title, int messageType, Icon icon,
			Object[] selectionValues, Object initialSelectionValue) throws HeadlessException {
		if (parentComponent == null) {
			if (ProgressWindow.hasInstance()) {
				parentComponent = ProgressWindow.instance();
			}
		}
		Object[] availableOptions = new Object[] { FlexoLocalization.localizedForKey("OK"), FlexoLocalization.localizedForKey("cancel") };
		JOptionPane pane = new JOptionPane(message, messageType, JOptionPane.OK_CANCEL_OPTION, icon, availableOptions, availableOptions[0]);

		pane.setWantsInput(true);
		pane.setSelectionValues(selectionValues);
		pane.setInitialSelectionValue(initialSelectionValue);
		pane.setComponentOrientation((parentComponent == null ? FlexoFrame.getActiveFrame() : parentComponent).getComponentOrientation());
		pane.setMessageType(messageType);
		JDialog dialog = pane.createDialog(parentComponent, title);
		pane.selectInitialValue();

		dialog.validate();
		dialog.pack();
		if (parentComponent == null) {
			Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
			dialog.setLocation((dim.width - dialog.getSize().width) / 2, (dim.height - dialog.getSize().height) / 2);
		}

		dialog.setVisible(true);
		dialog.dispose();

		Object val = pane.getValue();

		for (int counter = 0, maxCounter = availableOptions.length; counter < maxCounter; counter++) {
			if (availableOptions[counter].equals(val)) {
				if (counter == 1) {
					return null;
				}
			}

		}

		Object value = pane.getInputValue();
		if (value == JOptionPane.UNINITIALIZED_VALUE) {
			return null;
		}
		return value;
	}

	public void switchToPerspective(FlexoPerspective perspective) {
		controllerModel.setCurrentPerspective(perspective);
	}

	public final FlexoModelObject getDefaultObjectForPerspective(FlexoModelObject currentObjectAsModuleView, FlexoPerspective perspective) {
		return perspective.getDefaultObject(currentObjectAsModuleView);
	}

	/**
	 * Return current displayed object, assuming that current displayed view represents returned object (for example the process for WKF
	 * module)
	 * 
	 * @return the FlexoModelObject
	 */
	public FlexoModelObject getCurrentDisplayedObjectAsModuleView() {
		// logger.info("getCurrentModuleView()="+getCurrentModuleView());
		if (getCurrentModuleView() != null) {
			return getCurrentModuleView().getRepresentedObject();
		}
		return null;
	}

	/**
	 * Returns an initialized view (build and initialize a new one, or return the stored one) representing supplied object. An additional
	 * flag indicates if this view must be build if not already existent.
	 * 
	 * @param object
	 * @param createViewIfRequired
	 * @return an initialized ModuleView instance
	 */
	public ModuleView<?> moduleViewForObject(FlexoModelObject object, boolean createViewIfRequired) {
		if (object == null) {
			return null;
		}
		Map<FlexoProject, Map<FlexoModelObject, ModuleView<?>>> perpsectiveViews = getLoadedViewsForPerspective(getCurrentPerspective());
		Map<FlexoModelObject, ModuleView<?>> projectViews = perpsectiveViews.get(object.getProject());
		if (projectViews == null) {
			perpsectiveViews.put(object.getProject(), projectViews = new HashMap<FlexoModelObject, ModuleView<?>>());
			if (object.getProject() != null) {
				manager.new PropertyChangeListenerRegistration(ProjectClosedNotification.CLOSE, this, object.getProject());
			}
		}
		ModuleView<?> moduleView = projectViews.get(object);
		if (moduleView == null) {
			if (createViewIfRequired && controllerModel.getCurrentPerspective().hasModuleViewForObject(object)) {
				moduleView = createModuleViewForObjectAndPerspective(object, controllerModel.getCurrentPerspective());
				if (moduleView != null) {
					FlexoModelObject representedObject = moduleView.getRepresentedObject();
					if (representedObject == null) {
						if (logger.isLoggable(Level.WARNING)) {
							logger.warning("Module view: " + moduleView.getClass().getName() + " does not return its represented object");
						}
						representedObject = object;
					}
					projectViews.put(representedObject, moduleView);
					moduleViews.add(moduleView);
					propertyChangeSupport.firePropertyChange(MODULE_VIEWS, null, moduleView);
				}
			}
		}
		return moduleView;
	}

	/**
	 * Returns an initialized view (build and initialize a new one, or return the stored one) representing supplied object.If not already
	 * existant, build the view.
	 * 
	 * @param object
	 * @return an initialized ModuleView instance
	 */
	public ModuleView<?> moduleViewForObject(FlexoModelObject object) {
		return moduleViewForObject(object, true);
	}

	/**
	 * Creates a new view dor supplied object, or null if this object is not representable in this module
	 * 
	 * @param object
	 * @param perspective
	 *            TODO
	 * @return a newly created and initialized ModuleView instance
	 */
	protected final ModuleView<?> createModuleViewForObjectAndPerspective(FlexoModelObject object, FlexoPerspective perspective) {
		if (perspective == null) {
			return null;
		} else {
			return perspective.createModuleViewForObject(object, this);
		}
	}

	@SuppressWarnings("unchecked")
	public final boolean hasViewForObjectAndPerspective(FlexoModelObject object, FlexoPerspective perspective) {
		if (object == null) {
			return false;
		}
		try {
			return _hasViewForObjectAndPerspective(object, perspective);
		} catch (ClassCastException e) {
			// Don't worry here
			// This class cast exception is expected here, this test was intended for this goal
			if (logger.isLoggable(Level.FINE)) {
				logger.fine("Perspective " + perspective + " is not supposed to represent objects of class "
						+ object.getClass().getSimpleName());
			}
			return false;
		}
	}

	public final boolean _hasViewForObjectAndPerspective(FlexoModelObject object, FlexoPerspective perspective) {
		return perspective.hasModuleViewForObject(object);
	}

	/**
	 * Sets supplied object to be the main object represented as the current view for this module (for example the process for WKF module).
	 * Does nothing if supplied object is not representable in this module
	 * 
	 * @param object
	 * @return an initialized ModuleView instance
	 */
	public final void setCurrentEditedObjectAsModuleView(FlexoModelObject object) {
		getControllerModel().setCurrentObject(object);
	}

	/**
	 * Sets supplied object to be the main object represented as the current view for this module (for example the process for WKF module)
	 * and supplied perspective. Does nothing if supplied object is not representable in this module
	 * 
	 * @param object
	 * @return an initialized ModuleView instance
	 */
	public void setCurrentEditedObjectAsModuleView(FlexoModelObject object, FlexoPerspective perspective) {
		controllerModel.setCurrentPerspective(perspective);
		controllerModel.setCurrentObject(object);
	}

	public void addToPerspectives(FlexoPerspective perspective) {
		controllerModel.addToPerspectives(perspective);
	}

	/**
	 * Return currently displayed ModuleView
	 * 
	 * @return
	 */
	public ModuleView<?> getCurrentModuleView() {
		if (mainPane != null) {
			return mainPane.getModuleView();
		}
		return null;
	}

	/**
	 * Returns MainPane for this module
	 * 
	 * @return a FlexoMainPane instance
	 */
	public FlexoMainPane getMainPane() {
		return mainPane;
	}

	public boolean hasMainPane() {
		return mainPane != null;
	}

	/**
	 * Creates FlexoMainPane instance for this module.
	 * 
	 * @return a newly created FlexoMainPane instance
	 */
	protected abstract FlexoMainPane createMainPane();

	/*********
	 * VIEWS *
	 *********/

	/**
	 * Handle removing of supplied ModuleView from the control panel
	 * 
	 * @param aView
	 *            the view to remove
	 */
	public void removeModuleView(ModuleView<?> aView) {
		if (aView.getRepresentedObject() != null) {
			Map<FlexoProject, Map<FlexoModelObject, ModuleView<?>>> map = getLoadedViewsForPerspective(aView.getPerspective());
			Map<FlexoModelObject, ModuleView<?>> map2 = map.get(aView.getRepresentedObject().getProject());
			if (map2.get(aView.getRepresentedObject()) == aView) {// Let's make sure we remove the proper
																	// view!
				map2.remove(aView.getRepresentedObject());
			}
			while (moduleViews.remove(aView)) {
				;
			}
			propertyChangeSupport.firePropertyChange(MODULE_VIEWS, aView, null);
		}
	}

	protected Map<FlexoProject, Map<FlexoModelObject, ModuleView<?>>> getLoadedViewsForPerspective(FlexoPerspective p) {
		Map<FlexoProject, Map<FlexoModelObject, ModuleView<?>>> map = loadedViews.get(p);
		if (map == null) {
			loadedViews.put(p, map = new HashMap<FlexoProject, Map<FlexoModelObject, ModuleView<?>>>());
		}
		return map;
	}

	protected Collection<ModuleView<?>> getAllLoadedViewsForPerspective(FlexoPerspective p) {
		return getModuleViews(p, null, null);
	}

	/**
	 * Returns all the views currently loaded across all projects and all perspectives.
	 * 
	 * @return all the views currently loaded.
	 */
	protected Collection<ModuleView<?>> getAllLoadedViews() {
		return getModuleViews(null, null, null);
	}

	public <E extends ModuleView<?>> Collection<E> getModuleViews(FlexoPerspective perspective, FlexoProject project,
			Class<E> moduleViewType) {

		List<E> views = new ArrayList<E>();
		if (perspective != null) {
			Map<FlexoProject, Map<FlexoModelObject, ModuleView<?>>> map = getLoadedViewsForPerspective(perspective);
			appendViews(map, views, project, moduleViewType);
		} else {
			for (Map.Entry<FlexoPerspective, Map<FlexoProject, Map<FlexoModelObject, ModuleView<?>>>> e : loadedViews.entrySet()) {
				appendViews(e.getValue(), views, project, moduleViewType);
			}
		}
		return views;
	}

	private <E extends ModuleView<?>> void appendViews(Map<FlexoProject, Map<FlexoModelObject, ModuleView<?>>> views, List<E> list,
			FlexoProject project, Class<E> moduleViewType) {
		if (project != null) {
			appendViews(views.get(project), list, moduleViewType);
		} else {
			for (Map.Entry<FlexoProject, Map<FlexoModelObject, ModuleView<?>>> e : views.entrySet()) {
				appendViews(e.getValue(), list, moduleViewType);
			}
		}
	}

	@SuppressWarnings("unchecked")
	private <E extends ModuleView<?>> void appendViews(Map<FlexoModelObject, ModuleView<?>> views, List<E> list, Class<E> moduleViewType) {
		if (moduleViewType == null) {
			moduleViewType = (Class<E>) ModuleView.class;
		}
		for (Map.Entry<FlexoModelObject, ModuleView<?>> e : views.entrySet()) {
			if (moduleViewType.isAssignableFrom(e.getValue().getClass())) {
				list.add((E) e.getValue());
			}
		}
	}

	/**
	 * Shows control panel
	 */
	public void showControlPanel() {
		if (mainPane != null) {
			mainPane.showControlPanel();
		}
	}

	/**
	 * Hides control panel
	 */
	public void hideControlPanel() {
		if (mainPane != null) {
			mainPane.hideControlPanel();
		}
	}

	public void updateRecentProjectMenu() {
		if (menuBar != null) {
			menuBar.getFileMenu(this).updateRecentProjectMenu();
		}
	}

	/**
	 * Returns a custom component to be added to control panel in main pane Default implementation returns null, override it when required
	 * 
	 * @return
	 */
	public final JComponent getCustomActionPanel() {
		return null;
	}

	public abstract String getWindowTitleforObject(FlexoModelObject object);

	public String getWindowTitle() {
		String projectTitle = getModule().getModule().requireProject() && getProject() != null ? " - " + getProject().getProjectName()
				+ " - " + getProjectDirectory().getAbsolutePath() : "";
		if (getCurrentModuleView() != null) {
			return getModule().getName() + " : " + getWindowTitleforObject(getCurrentDisplayedObjectAsModuleView()) + projectTitle;
		} else {
			if (getModule() == null) {
				return FlexoCst.BUSINESS_APPLICATION_VERSION_NAME + projectTitle;
			} else {
				return FlexoCst.BUSINESS_APPLICATION_VERSION_NAME + " - " + getModule().getName() + projectTitle;
			}
		}
	}

	public void cancelCurrentAction() {
		if (logger.isLoggable(Level.INFO)) {
			logger.info("Escape was pressed but the current controller does not do anything about it");
		}
	}

	public class FlexoControllerInspectorDelegate implements InspectorDelegate {

		private KeyValueCoding target;

		private String key;

		private String localizedPropertyName;

		/**
		 * Overrides setObjectValue
		 * 
		 * @see org.openflexo.inspector.InspectorDelegate#setObjectValue(java.lang.Object)
		 */
		@Override
		public boolean setObjectValue(Object value) {

			if (target != null) {
				if (target instanceof FlexoModelObject) {
					SetPropertyAction action = SetPropertyAction.actionType.makeNewAction((FlexoModelObject) target,
							new Vector<FlexoModelObject>(), getEditor());
					action.setKey(key);
					action.setValue(value);
					action.setLocalizedPropertyName(localizedPropertyName);
					action.doAction();
					return action.hasActionExecutionSucceeded() && action.getThrownException() == null;
				} else if (target != null) {
					target.setObjectForKey(value, key);
				} else {
					if (logger.isLoggable(Level.SEVERE)) {
						logger.severe("Target object is not a FlexoModelObject, I cannot set the value for that object");
					}
				}
			} else if (logger.isLoggable(Level.WARNING)) {
				logger.warning("Target object is null for key " + key + ". We should definitely investigate this.");
			}
			return false;
		}

		/**
		 * Overrides handlesObjectOfClass
		 * 
		 * @see org.openflexo.inspector.InspectorDelegate#handlesObjectOfClass(java.lang.Class)
		 */
		@Override
		public boolean handlesObjectOfClass(Class<?> c) {
			return KeyValueCoding.class.isAssignableFrom(c);
		}

		/**
		 * Overrides setKey
		 * 
		 * @see org.openflexo.inspector.InspectorDelegate#setKey(java.lang.String)
		 */
		@Override
		public void setKey(String path) {
			this.key = path;
		}

		/**
		 * Overrides setObject
		 * 
		 * @see org.openflexo.inspector.InspectorDelegate#setObject(org.openflexo.inspector.InspectableObject)
		 */
		@Override
		public void setTarget(KeyValueCoding object) {
			this.target = object;
		}

		/**
		 * Overrides performAction
		 * 
		 * @see org.openflexo.inspector.InspectorDelegate#performAction(java.lang.String, java.lang.Object)
		 */
		@Override
		public boolean performAction(ActionEvent e, String actionName, Object object) {
			if (object instanceof FlexoModelObject) {
				FlexoModelObject m = (FlexoModelObject) object;
				for (FlexoActionType<?, ?, ?> actionType : m.getActionList()) {
					if (actionType.getUnlocalizedName().equals(actionName)) {
						return getEditor().performActionType((FlexoActionType<?, FlexoModelObject, FlexoModelObject>) actionType, m,
								(Vector<FlexoModelObject>) null, e).hasActionExecutionSucceeded();
					}
				}
			}
			return false;
		}

		/**
		 * Overrides setLocalizedPropertyName
		 * 
		 * @see org.openflexo.inspector.InspectorDelegate#setLocalizedPropertyName(java.lang.String)
		 */
		@Override
		public void setLocalizedPropertyName(String name) {
			localizedPropertyName = name;
		}

	}

	@Override
	public PropertyChangeSupport getPropertyChangeSupport() {
		return propertyChangeSupport;
	}

	@Override
	public String getDeletedProperty() {
		return DISPOSED;
	}

	public boolean isDisposed() {
		return disposed;
	}

	public void dispose() {
		if (getSelectionManager() != null) {
			if (getSharedInspectorController() != null) {
				getSelectionManager().deleteObserver(getSharedInspectorController());
			}
			if (getDocInspectorController() != null) {
				getSelectionManager().deleteObserver(getDocInspectorController());
			}
		}
		manager.delete();
		GeneralPreferences.getPreferences().deleteObserver(this);
		mainPane.dispose();
		if (consistencyCheckWindow != null && !consistencyCheckWindow.isDisposed()) {
			consistencyCheckWindow.dispose();
		}
		if (useOldInspectorScheme()) {
			getSharedInspectorController().getInspectorWindow().dispose();
		}
		if (mainInspectorController != null) {
			mainInspectorController.delete();
		}
		for (ModuleView<?> view : getAllLoadedViews()) {
			try {
				view.deleteModuleView();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		if (rmWindow != null) {
			rmWindow.dispose();
			rmWindow = null;
		}
		registeredMenuBar.clear();
		if (PreferencesController.hasInstance()) {
			PreferencesController.instance().getPreferencesWindow().dispose();
		}
		if (flexoFrame != null) {
			flexoFrame.disposeAll();
		}
		if (menuBar != null) {
			menuBar.dispose();
		}
		for (FlexoEditor editor : controllerModel.getEditors()) {
			if (editor instanceof InteractiveFlexoEditor) {
				((InteractiveFlexoEditor) editor).unregisterControllerActionInitializer(getControllerActionInitializer());
			}
		}
		controllerModel.delete();
		loadedViews.clear();
		disposed = true;
		if (propertyChangeSupport != null) {
			propertyChangeSupport.firePropertyChange(DISPOSED, false, true);
		}
		setEditor(null);
		propertyChangeSupport = null;
		inspectorMenuBar = null;
		docInspectorController = null;
		consistencyCheckWindow = null;
		flexoFrame = null;
		mainPane = null;
		menuBar = null;
		module = null;
	}

	@Override
	protected void finalize() throws Throwable {
		if (logger.isLoggable(Level.INFO)) {
			logger.info("Finalizing controller " + getClass().getSimpleName());
		}
		super.finalize();
	}

	public PPMWebServiceClient getWSClient() {
		return getWSClient(false);
	}

	public PPMWebServiceClient getWSClient(boolean forceDialog) {
		ModelFactory factory;
		try {
			factory = new ModelFactory(PPMWSClientParameter.class);
		} catch (ModelDefinitionException e) {
			e.printStackTrace();
			throw new Error("Improperly configured PPMWSClientParameter. ", e);
		}
		FlexoServerInstance instance = FlexoServerInstanceManager.getInstance().getAddressBook()
				.getInstanceWithID(AdvancedPrefs.getWebServiceInstance());
		PPMWSClientParameter params = factory.newInstance(PPMWSClientParameter.class);
		params.setWSInstance(instance);
		params.setWSLogin(AdvancedPrefs.getWebServiceLogin());
		params.setWSPassword(AdvancedPrefs.getWebServiceMd5Password());
		params.setWSURL(AdvancedPrefs.getWebServiceUrl());
		params.setRemember(AdvancedPrefs.getRememberAndDontAskWebServiceParamsAnymore());
		if (params.getWSInstance() != null && !params.getWSInstance().getID().equals(FlexoServerInstance.OTHER_ID)) {
			params.setWSURL(params.getWSInstance().getWSURL());
		}
		String oldMD5Password = params.getWSPassword();
		if (forceDialog || !params.getRemember() || params.getWSURL() == null || params.getWSLogin() == null
				|| params.getWSPassword() == null || !isWSUrlValid(params.getWSURL()) || urlSeemsIncorrect(params.getWSURL())) {
			WebServiceURLDialog data = new WebServiceURLDialog();
			data.setClientParameter(params);
			FIBDialog<WebServiceURLDialog> dialog = FIBDialog.instanciateAndShowDialog(WebServiceURLDialog.FIB_FILE, data, getFlexoFrame(),
					true, FlexoLocalization.getMainLocalizer());
			if (dialog.getStatus() == Status.VALIDATED) {
				if (params.getWSInstance() != null && !params.getWSInstance().getID().equals(FlexoServerInstance.OTHER_ID)) {
					params.setWSURL(params.getWSInstance().getWSURL());
				}
				try {
					String password = params.getWSPassword();
					if (oldMD5Password == null || !oldMD5Password.equals(password)) {
						params.setWSPassword(ToolBox.getMd5Hash(params.getWSPassword()));
					}
				} catch (NoSuchAlgorithmException e1) {
					e1.printStackTrace();
					FlexoController.notify(e1.getMessage());
					AdvancedPrefs.setRememberAndDontAskWebServiceParamsAnymore(false);
					AdvancedPrefs.save();
					return null;
				}
				if (params.getWSInstance() != null) {
					AdvancedPrefs.setWebServiceInstance(params.getWSInstance().getID());
				}
				if (params.getWSURL() != null) {
					AdvancedPrefs.setWebServiceUrl(params.getWSURL());
				}
				if (params.getWSLogin() != null) {
					AdvancedPrefs.setWebServiceLogin(params.getWSLogin());
				}
				if (params.getWSPassword() != null) {
					AdvancedPrefs.setWebServiceMd5Password(params.getWSPassword());
				}
				AdvancedPrefs.setRememberAndDontAskWebServiceParamsAnymore(params.getRemember());
				AdvancedPrefs.save();
			} else {
				return null;
			}
		}

		// now that we have the parameters. We have to invoke the WS

		PPMWebServiceClient client = null;
		try {
			client = new PPMWebServiceClient(params.getWSURL(), params.getWSLogin(), params.getWSPassword());
		} catch (ServiceException e1) {
			e1.printStackTrace();
			FlexoController.notify(e1.getMessage());
			AdvancedPrefs.setRememberAndDontAskWebServiceParamsAnymore(false);
			AdvancedPrefs.save();
			return null;
		}
		return client;
	}

	public void handleWSException(RemoteException e) {
		if (e.getCause() instanceof TooManyFailedAttemptException) {
			throw (TooManyFailedAttemptException) e.getCause();
		}
		if (e.getCause() instanceof CancelException) {
			throw (CancelException) e.getCause();
		}
		if (logger.isLoggable(Level.SEVERE)) {
			logger.log(Level.SEVERE, "An error ocurred " + (e.getMessage() == null ? "no message" : e.getMessage()), e);
		}
		if (e.getMessage() != null && e.getMessage().startsWith("redirect")) {
			String location = null;
			if (e.getMessage().indexOf("Location") > -1) {
				location = e.getMessage().substring(e.getMessage().indexOf("Location") + 9).trim();
			}
			FlexoController.notify(FlexoLocalization.localizedForKey("could_not_connect_to_web_sevice") + ": "
					+ FlexoLocalization.localizedForKey("the_url_seems_incorrect")
					+ (location != null ? "\n" + FlexoLocalization.localizedForKey("try_with_this_one") + " " + location : ""));
			return;
		}
		if (e instanceof PPMWebServiceAuthentificationException) {
			handleWSException((PPMWebServiceAuthentificationException) e);
		} else if (e.getCause() instanceof ConnectException) {
			FlexoController.notify(FlexoLocalization.localizedForKey("connection_error")
					+ (e.getCause().getMessage() != null ? " (" + e.getCause().getMessage() + ")" : ""));
		} else if (e.getMessage() != null && "(500)Apple WebObjects".equals(e.getMessage())
				|| e.getMessage().startsWith("No such operation")) {
			FlexoController.notify(FlexoLocalization.localizedForKey("could_not_connect_to_web_sevice") + ": "
					+ FlexoLocalization.localizedForKey("the_url_seems_incorrect"));
		} else if (e.toString() != null && e.toString().startsWith("javax.net.ssl.SSLHandshakeException")) {
			FlexoController.notify(FlexoLocalization.localizedForKey("connection_error") + ": " + e);
		} else if (e.detail instanceof SocketTimeoutException) {
			FlexoController.notify(FlexoLocalization.localizedForKey("connection_timeout"));
		} else if (e.detail instanceof IOException || e.getCause() instanceof IOException) {
			IOException ioEx = (IOException) (e.detail instanceof IOException ? e.detail : e.getCause());
			FlexoController.notify(FlexoLocalization.localizedForKey("connection_error") + ": "
					+ FlexoLocalization.localizedForKey(ioEx.getClass().getSimpleName()) + " " + ioEx.getMessage());
		} else {
			if (e.getMessage() != null && e.getMessage().indexOf("Content is not allowed in prolog") > -1) {
				FlexoController
						.notify("Check your connection url in FlexoPreferences > Advanced.\n It seems wrong.\nsee logs for details.");
			} else {
				FlexoController
						.notify(FlexoLocalization.localizedForKey("webservice_remote_error")
								+ " \n"
								+ (e.getMessage() == null || "java.lang.NullPointerException".equals(e.getMessage()) ? "Check your connection parameters.\nThe service may be temporary unavailable."
										: e.getMessage()));
			}
		}
	}

	public void handleWSException(ServiceException e) {
		FlexoController.notify(FlexoLocalization.localizedForKey("webservice_connection_failed"));
	}

	public void handleWSException(PPMWebServiceAuthentificationException e) {
		FlexoController.notify(FlexoLocalization.localizedForKey("webservice_authentification_failed") + ": "
				+ FlexoLocalization.localizedForKey(e.getMessage1()));
	}

	private boolean isWSUrlValid(final String wsURL) {
		return wsURL != null
				&& (wsURL.toLowerCase().startsWith("http://") && wsURL.charAt(7) != '/' || wsURL.toLowerCase().startsWith("https://")
						&& wsURL.charAt(8) != '/');
	}

	private boolean urlSeemsIncorrect(String wsURL) {
		try {
			URL url = new URL(wsURL);
			// Bug 1007330 Fix
			return url.getAuthority() == null;
		} catch (MalformedURLException e) {
			return true;
		}
	}

	public void objectWasClicked(Object object) {
		logger.info("Object was clicked: " + object);
	}

	public void objectWasRightClicked(Object object, MouseEvent e) {
		logger.info("Object was right-clicked: " + object + "event=" + e);
		if (object instanceof FlexoModelObject) {
			getSelectionManager().getContextualMenuManager().showPopupMenuForObject((FlexoModelObject) object, (Component) e.getSource(),
					e.getPoint());
		}
	}

	public void objectWasDoubleClicked(Object object) {
		logger.info("Object was double-clicked: " + object);
		if (object instanceof FlexoModelObject && getCurrentPerspective().hasModuleViewForObject((FlexoModelObject) object)) {
			// Try to display object in view
			selectAndFocusObject((FlexoModelObject) object);
		}
	}

	public boolean displayInspectorTabForContext(String context) {
		// logger.info("Enquiring inspector tab display for context=" + context + "... Answering NO");
		return false;
	}

	public FlexoProgress willLoad(File fibFile) {

		if (!FIBLibrary.instance().componentIsLoaded(fibFile)) {
			FlexoProgress progress = ProgressWindow.makeProgressWindow(FlexoLocalization.localizedForKey("loading_interface..."), 3);
			progress.setProgress("loading_component");
			FIBLibrary.instance().retrieveFIBComponent(fibFile);
			progress.setProgress("build_interface");
			return progress;
		}
		return null;
	}

	public FlexoProgress willLoad(String fibResourcePath) {

		if (!FIBLibrary.instance().componentIsLoaded(fibResourcePath)) {
			FlexoProgress progress = ProgressWindow.makeProgressWindow(FlexoLocalization.localizedForKey("loading_interface..."), 3);
			progress.setProgress("loading_component");
			FIBLibrary.instance().retrieveFIBComponent(fibResourcePath);
			progress.setProgress("build_interface");
			return progress;
		}
		return null;
	}

	@Override
	public void update(FlexoObservable observable, DataModification dataModification) {
		if (observable == GeneralPreferences.getPreferences()) {
			if (dataModification instanceof PreferencesHaveChanged) {
				String key = ((PreferencesHaveChanged) dataModification).propertyName();
				if (GeneralPreferences.LANGUAGE_KEY.equals(key)) {
					getFlexoFrame().updateTitle();
				} else if (GeneralPreferences.LAST_OPENED_PROJECTS_1.equals(key) || GeneralPreferences.LAST_OPENED_PROJECTS_2.equals(key)
						|| GeneralPreferences.LAST_OPENED_PROJECTS_3.equals(key) || GeneralPreferences.LAST_OPENED_PROJECTS_4.equals(key)
						|| GeneralPreferences.LAST_OPENED_PROJECTS_5.equals(key)) {
					updateRecentProjectMenu();
				}
			}
		}
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		if (evt.getSource() == getControllerModel()) {
			if (evt.getPropertyName().equals(ControllerModel.CURRENT_EDITOR)) {
				updateEditor((FlexoEditor) evt.getOldValue(), (FlexoEditor) evt.getNewValue());
			}
		} else if (evt.getSource() == getProjectLoader()) {
			if (evt.getPropertyName().equals(ProjectLoader.EDITOR_ADDED)) {
				if (getModuleLoader().getActiveModule() == getModule()) {
					setEditor((FlexoEditor) evt.getNewValue());
				}
			}
		} else if (evt.getSource() instanceof FlexoProject && evt.getPropertyName().equals(ProjectClosedNotification.CLOSE)) {
			for (Map<FlexoProject, Map<FlexoModelObject, ModuleView<?>>> map : new ArrayList<Map<FlexoProject, Map<FlexoModelObject, ModuleView<?>>>>(
					loadedViews.values())) {
				Map<FlexoModelObject, ModuleView<?>> map2 = map.get(evt.getSource());
				if (map2 != null) {
					for (ModuleView<?> view : new ArrayList<ModuleView<?>>(map2.values())) {
						view.deleteModuleView();
					}
					loadedViews.remove(evt.getSource());
				}
			}
		}
	}

	public ResourceManagerWindow getRMWindow(FlexoProject project) {
		if (rmWindow != null) {
			if (rmWindow.getProject() == project) {
				return rmWindow;
			}
			rmWindow.dispose();
		}
		return rmWindow = new ResourceManagerWindow(project);
	}

	public void selectAndFocusObject(FlexoModelObject object) {
		if (object instanceof FlexoProject) {
			getControllerModel().setCurrentProject((FlexoProject) object);
		} else {
			setCurrentEditedObjectAsModuleView(object);
		}
	}

	public ValidationModel getDefaultValidationModel() {
		return null;
	}

	public final FlexoPerspective getCurrentPerspective() {
		return getControllerModel().getCurrentPerspective();
	}

	public final FlexoEditor getEditor() {
		return getControllerModel().getCurrentEditor();
	}

	/**
	 * Return the technology-specific controller for supplied technology adapter
	 * 
	 * @param technologyAdapter
	 * @return
	 */
	public static <TA extends TechnologyAdapter<?, ?>> TechnologyAdapterController<TA> getTechnologyAdapterController(TA technologyAdapter) {
		if (technologyAdapter != null) {
			FlexoServiceManager sm = technologyAdapter.getTechnologyAdapterService().getFlexoServiceManager();
			if (sm != null) {
				TechnologyAdapterControllerService service = sm.getService(TechnologyAdapterControllerService.class);
				if (service != null) {
					return service.getTechnologyAdapterController(technologyAdapter);
				}
			}
		}
		return null;
	}

	public ImageIcon iconForObject(Object object) {
		ImageIcon iconForObject = statelessIconForObject(object);
		if (iconForObject != null && object instanceof FlexoModelObject && ((FlexoModelObject) object).getProject() != getProject()) {
			iconForObject = IconFactory.getImageIcon(iconForObject, new IconMarker[] { IconLibrary.IMPORT });
		}
		return iconForObject;
	}

	public static ImageIcon statelessIconForObject(Object object) {
		if (object instanceof WorkflowModelObject) {
			return WKFIconLibrary.iconForObject((WorkflowModelObject) object);
		} else if (object instanceof WKFObject) {
			return WKFIconLibrary.iconForObject((WKFObject) object);
		} else if (object instanceof IEObject) {
			return SEIconLibrary.iconForObject((IEObject) object);
		} else if (object instanceof DMObject) {
			return DMEIconLibrary.iconForObject((DMObject) object);
		} else if (object instanceof ViewPointLibraryObject) {
			return VPMIconLibrary.iconForObject((ViewPointLibraryObject) object);
		} else if (object instanceof ViewPointResource) {
			return VPMIconLibrary.iconForObject((ViewPointResource) object);
		} else if (object instanceof AbstractViewObject) {
			return VEIconLibrary.iconForObject((AbstractViewObject) object);
		} /*else if (object instanceof OntologyLibrary) {
			return OntologyIconLibrary.ONTOLOGY_LIBRARY_ICON;
			}*/else if (object instanceof RepositoryFolder) {
			return IconLibrary.FOLDER_ICON;
		} else if (object instanceof TechnologyAdapter<?, ?>) {
			TechnologyAdapterController<?> tac = getTechnologyAdapterController((TechnologyAdapter<?, ?>) object);
			if (tac != null) {
				return tac.getTechnologyIcon();
			}
		} else if (object instanceof FlexoModel<?, ?>) {
			TechnologyAdapterController<?> tac = getTechnologyAdapterController(((FlexoModel<?, ?>) object).getTechnologyAdapter());
			if (tac != null) {
				return tac.getModelIcon();
			}
		} else if (object instanceof FlexoModelResource<?, ?>) {
			TechnologyAdapterController<?> tac = getTechnologyAdapterController(((FlexoModelResource<?, ?>) object).getTechnologyAdapter());
			if (tac != null) {
				return tac.getModelIcon();
			}
		} else if (object instanceof FlexoMetaModel<?>) {
			TechnologyAdapterController<?> tac = getTechnologyAdapterController(((FlexoMetaModel<?>) object).getTechnologyAdapter());
			if (tac != null) {
				return tac.getMetaModelIcon();
			}
		} else if (object instanceof FlexoMetaModelResource<?, ?>) {
			TechnologyAdapterController<?> tac = getTechnologyAdapterController(((FlexoMetaModelResource<?, ?>) object)
					.getTechnologyAdapter());
			if (tac != null) {
				return tac.getMetaModelIcon();
			}
		} else if (object instanceof IFlexoOntologyObject) {
			TechnologyAdapterController<?> tac = getTechnologyAdapterController(((IFlexoOntologyObject) object).getTechnologyAdapter());
			if (tac != null) {
				return tac.getIconForOntologyObject(((IFlexoOntologyObject) object).getClass());
			}
		} else if (object instanceof TOCObject) {
			return DEIconLibrary.iconForObject((TOCObject) object);
		} else if (object instanceof CGTemplateObject) {
			return DGIconLibrary.iconForObject((CGTemplateObject) object);
		} else if (object instanceof DocType) {
			return CGIconLibrary.TARGET_ICON;
		} else if (object instanceof FlexoProject) {
			return IconLibrary.OPENFLEXO_NOTEXT_16;
		}
		logger.warning("Sorry, no icon defined for " + object + " " + (object != null ? object.getClass() : ""));
		return null;
	}
}
