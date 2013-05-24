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
package org.openflexo.view;

import java.awt.BorderLayout;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.File;
import java.util.logging.Logger;

import javax.swing.JPanel;
import javax.swing.JScrollPane;

import org.openflexo.fib.FIBLibrary;
import org.openflexo.fib.controller.FIBController;
import org.openflexo.fib.model.DataBinding;
import org.openflexo.fib.model.FIBBrowserAction;
import org.openflexo.fib.model.FIBComponent;
import org.openflexo.fib.model.listener.FIBMouseClickListener;
import org.openflexo.fib.view.FIBView;
import org.openflexo.foundation.DataModification;
import org.openflexo.foundation.DefaultFlexoEditor;
import org.openflexo.foundation.FlexoEditor;
import org.openflexo.foundation.FlexoEditor.FlexoEditorFactory;
import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.FlexoObservable;
import org.openflexo.foundation.GraphicalFlexoObserver;
import org.openflexo.foundation.action.FlexoAction;
import org.openflexo.foundation.action.FlexoActionType;
import org.openflexo.foundation.resource.DefaultResourceCenterService;
import org.openflexo.foundation.resource.FlexoResourceCenterService;
import org.openflexo.foundation.rm.FlexoProject;
import org.openflexo.foundation.rm.FlexoResourceManager;
import org.openflexo.foundation.utils.FlexoProgress;
import org.openflexo.foundation.utils.ProjectInitializerException;
import org.openflexo.foundation.utils.ProjectLoadingCancelledException;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.toolbox.HasPropertyChangeSupport;
import org.openflexo.toolbox.PropertyChangeListenerRegistrationManager;
import org.openflexo.view.controller.FlexoController;
import org.openflexo.view.controller.FlexoFIBController;

/**
 * Please comment this class
 * 
 * @author sguerin
 * 
 */
public class FlexoFIBView extends JPanel implements GraphicalFlexoObserver, HasPropertyChangeSupport, PropertyChangeListener {
	static final Logger logger = Logger.getLogger(FlexoFIBView.class.getPackage().getName());

	private static final String DELETED = "deleted";

	private Object dataObject;

	public class FIBBrowserActionAdapter extends FIBBrowserAction {

		private FlexoActionType actionType;

		public FIBBrowserActionAdapter(FlexoActionType<?, ?, ?> actionType) {
			super();
			this.actionType = actionType;
			setMethod(new DataBinding("action.performAction(selected)"));
			setIsAvailable(new DataBinding("action.isAvailable(selected)"));
		}

		public Object performAction(Object selected) {
			if (selected instanceof FlexoModelObject) {
				FlexoAction action = actionType.makeNewAction((FlexoModelObject) selected, null, controller.getEditor());
				action.doAction();
			}
			return null;
		}

		public boolean isAvailable(Object selected) {
			if (selected instanceof FlexoModelObject) {
				return controller.getEditor().isActionVisible(actionType, (FlexoModelObject) selected, null)
						&& controller.getEditor().isActionEnabled(actionType, (FlexoModelObject) selected, null);
			}
			return false;
		}

		@Override
		public String getName() {
			return actionType.getUnlocalizedName();
		}

		@Override
		public ActionType getActionType() {
			if (actionType.getActionCategory() == FlexoActionType.ADD_ACTION_TYPE) {
				return ActionType.Add;
			} else if (actionType.getActionCategory() == FlexoActionType.DELETE_ACTION_TYPE) {
				return ActionType.Delete;
			} else {
				return ActionType.Custom;
			}
		}

	}

	private FlexoController controller;
	private FIBView fibView;
	private FlexoFIBController fibController;
	private FIBComponent fibComponent;

	private PropertyChangeSupport pcSupport;

	protected PropertyChangeListenerRegistrationManager manager = new PropertyChangeListenerRegistrationManager();

	public FlexoFIBView(Object representedObject, FlexoController controller, File fibFile, FlexoProgress progress) {
		this(representedObject, controller, fibFile, false, progress);
	}

	public FlexoFIBView(Object representedObject, FlexoController controller, File fibFile, boolean addScrollBar, FlexoProgress progress) {
		this(representedObject, controller, FIBLibrary.instance().retrieveFIBComponent(fibFile), addScrollBar, progress);
	}

	public FlexoFIBView(Object representedObject, FlexoController controller, String fibResourcePath, FlexoProgress progress) {
		this(representedObject, controller, fibResourcePath, false, progress);
	}

	public FlexoFIBView(Object representedObject, FlexoController controller, String fibResourcePath, boolean addScrollBar,
			FlexoProgress progress) {
		this(representedObject, controller, FIBLibrary.instance().retrieveFIBComponent(fibResourcePath), addScrollBar, progress);
	}

	protected FlexoFIBView(Object dataObject, FlexoController controller, FIBComponent fibComponent, boolean addScrollBar,
			FlexoProgress progress) {
		super(new BorderLayout());
		this.dataObject = dataObject;
		this.controller = controller;
		this.fibComponent = fibComponent;

		if (dataObject instanceof HasPropertyChangeSupport) {
			manager.addListener(this, (HasPropertyChangeSupport) dataObject);
		} else if (dataObject instanceof FlexoObservable) {
			((FlexoObservable) dataObject).addObserver(this);
		}

		pcSupport = new PropertyChangeSupport(this);

		initializeFIBComponent();

		fibController = createFibController(fibComponent, controller);

		if (progress != null) {
			progress.setProgress(FlexoLocalization.localizedForKey("build_view"));
		}

		fibView = fibController.buildView(fibComponent);

		if (progress != null) {
			progress.setProgress(FlexoLocalization.localizedForKey("init_view"));
		}

		fibController.setDataObject(dataObject);

		if (this instanceof FIBMouseClickListener) {
			fibView.getController().addMouseClickListener((FIBMouseClickListener) this);
		}

		if (addScrollBar) {
			add(new JScrollPane(fibView.getJComponent()), BorderLayout.CENTER);
		} else {
			add(fibView.getJComponent(), BorderLayout.CENTER);
		}

		validate();
		revalidate();

		if (progress != null) {
			progress.hideWindow();
		}
	}

	/**
	 * Create the Fib Controller to be used for this view. Can be overrided to add functionalities to this Fib View.
	 * 
	 * @param fibComponent
	 * @param controller
	 * @return the newly created FlexoFIBController
	 */
	protected FlexoFIBController createFibController(FIBComponent fibComponent, FlexoController controller) {
		FIBController returned = FIBController.instanciateController(fibComponent, FlexoLocalization.getMainLocalizer());
		if (returned instanceof FlexoFIBController) {
			((FlexoFIBController) returned).setFlexoController(controller);
			return (FlexoFIBController) returned;
		} else if (fibComponent.getControllerClass() != null) {
			logger.warning("Controller for component " + fibComponent + " is not an instanceof FlexoFIBController");
		}
		return fibController = new FlexoFIBController(fibComponent, controller);
	}

	public FlexoController getFlexoController() {
		return controller;
	}

	@Override
	public void update(FlexoObservable observable, DataModification dataModification) {
		/*
		 * if (dataModification instanceof ObjectDeleted) { if (dataModification.oldValue() == getOntologyObject()) { deleteModuleView(); }
		 * } else if (dataModification.propertyName()!=null && dataModification.propertyName().equals("name")) {
		 * getOEController().getFlexoFrame().updateTitle(); updateTitlePanel(); }
		 */

	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		// TODO !
		logger.info("propertyChange in FlexoFIBView: " + evt);
	}

	public Object getDataObject() {
		return dataObject;
	}

	public void setDataObject(Object object) {
		if (this.dataObject instanceof HasPropertyChangeSupport) {
			manager.removeListener(this, (HasPropertyChangeSupport) this.dataObject);
		} else if (this.dataObject instanceof FlexoObservable) {
			((FlexoObservable) this.dataObject).deleteObserver(this);
		}
		dataObject = object;
		if (dataObject instanceof HasPropertyChangeSupport) {
			manager.addListener(this, (HasPropertyChangeSupport) this.dataObject);
		} else if (dataObject instanceof FlexoObservable) {
			((FlexoObservable) dataObject).addObserver(this);
		}
		fibController.setDataObject(object, true);
	}

	public FIBComponent getFIBComponent() {
		return fibComponent;
	}

	public FIBView getFIBView() {
		return fibView;
	}

	public FlexoFIBController getFIBController() {
		return fibController;
	}

	public FIBView getFIBView(String componentName) {
		return fibController.viewForComponent(componentName);
	}

	public void deleteView() {
		if (this instanceof FIBMouseClickListener && fibView.getController() != null) {
			fibView.getController().removeMouseClickListener((FIBMouseClickListener) this);
		}
		fibView.delete();
		if (dataObject instanceof FlexoObservable) {
			((FlexoObservable) dataObject).deleteObserver(this);
		}
		manager.delete();
		getPropertyChangeSupport().firePropertyChange(DELETED, false, true);
	}

	/**
	 * Returns flag indicating if this view is itself responsible for scroll management When not, Flexo will manage it's own scrollbar for
	 * you
	 * 
	 * @return
	 */
	public boolean isAutoscrolled() {
		return false;
	}

	/**
	 * This method is a hook which is called just before to initialize FIBView and FIBController, and allow to programmatically define,
	 * check or redefine component
	 */
	protected void initializeFIBComponent() {
	}

	@Override
	public PropertyChangeSupport getPropertyChangeSupport() {
		return pcSupport;
	}

	@Override
	public String getDeletedProperty() {
		return DELETED;
	}

	// test purposes
	public static FlexoEditor loadProject(File prjDir) {
		FlexoResourceCenterService resourceCenter = DefaultResourceCenterService.getNewInstance();
		FlexoEditor editor = null;
		try {
			editor = FlexoResourceManager.initializeExistingProject(prjDir, EDITOR_FACTORY, resourceCenter);
		} catch (ProjectLoadingCancelledException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ProjectInitializerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (editor == null) {
			System.exit(-1);
		}
		return editor;
	}

	// test purposes
	protected static final FlexoEditorFactory EDITOR_FACTORY = new FlexoEditorFactory() {
		@Override
		public DefaultFlexoEditor makeFlexoEditor(FlexoProject project) {
			return new FlexoTestEditor(project);
		}
	};

	// test purposes
	public static class FlexoTestEditor extends DefaultFlexoEditor {
		public FlexoTestEditor(FlexoProject project) {
			super(project);
		}

	}

}
