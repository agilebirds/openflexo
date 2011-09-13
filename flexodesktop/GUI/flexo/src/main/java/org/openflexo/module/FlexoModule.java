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
package org.openflexo.module;

import java.awt.Frame;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import org.openflexo.components.ProgressWindow;
import org.openflexo.components.SaveDialog;
import org.openflexo.foundation.DataFlexoObserver;
import org.openflexo.foundation.DataModification;
import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.FlexoObservable;
import org.openflexo.foundation.InspectorGroup;
import org.openflexo.foundation.rm.FlexoProject;
import org.openflexo.foundation.rm.FlexoResource;
import org.openflexo.foundation.rm.FlexoResourceData;
import org.openflexo.foundation.rm.FlexoStorageResource;
import org.openflexo.foundation.rm.ResourceRemoved;
import org.openflexo.foundation.rm.SaveResourceException;
import org.openflexo.foundation.rm.SaveResourceExceptionList;
import org.openflexo.foundation.rm.SaveResourcePermissionDeniedException;
import org.openflexo.foundation.utils.FlexoProgress;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.view.FlexoFrame;
import org.openflexo.view.ModuleBar;
import org.openflexo.view.controller.FlexoController;
import org.openflexo.view.controller.InteractiveFlexoEditor;
import org.openflexo.view.controller.SelectionManagingController;
import org.openflexo.view.menu.WindowMenu;

/**
 * Abstract class defining a Flexo Module. A Flexo Module is an application
 * component part of the Flexo Application Suite and dedicated to a particular
 * purpose.
 *
 * @author sguerin
 */
public abstract class FlexoModule implements DataFlexoObserver
{

    static final Logger logger = Logger.getLogger(FlexoModule.class.getPackage().getName());

    protected FlexoController _controller = null;

    protected FlexoFrame _frame = null;

    private InteractiveFlexoEditor _editor = null;

    private boolean isActive = false;

    private static FlexoModule _activeModule = null;

    /**
     * Hashtable where all the resources used by this module are stored, with
     * associated key which is a String identifying the resource
     * (resourceIdentifier)
     */
    private final Hashtable<String,FlexoResource> usedResources;

    public FlexoModule(InteractiveFlexoEditor projectEditor, FlexoController controller)
    {
        super();
        _editor = projectEditor;
        projectEditor.getProject().addObserver(this);
        _controller = controller;
        _controller.setModule(this);
        _frame = controller.getFlexoFrame();
        _frame.setTitle(getName());
        _frame.setModule(this);
        usedResources = new Hashtable<String, FlexoResource>();
        _controller.initInspectors();
        if (projectEditor.getProject() != null)
        	retain(projectEditor.getProject());
    }

    public  FlexoModule(InteractiveFlexoEditor projectEditor)
    {
        super();
        _editor = projectEditor;
        if (projectEditor.getProject() != null)
        	projectEditor.getProject().addObserver(this);
        usedResources = new Hashtable<String, FlexoResource>();
        if (projectEditor.getProject() != null)
        	retain(projectEditor.getProject());
    }

    protected void setFlexoController(FlexoController controller)
    {
        _controller = controller;
        _controller.setModule(this);
        _frame = controller.getFlexoFrame();
        // _frame.setTitle(getName() + " " + getVersion());
        _frame.setModule(this);
        _controller.initInspectors();
    }

    protected void setFlexoFrame(FlexoFrame frame)
    {
        _frame = frame;
        _frame.setModule(this);
    }

    public FlexoController getFlexoController()
    {
        if (_controller == null) {
            if (logger.isLoggable(Level.WARNING))
                logger.warning("Module '" + getName() + "' NOT CORRECTELY LOADED !");
        }
        return _controller;
    }

    public FlexoFrame getFlexoFrame()
    {
        if (_frame == null) {
            if (logger.isLoggable(Level.WARNING))
                logger.warning("Module '" + getName() + "' NOT CORRECTELY LOADED !");
        }
        return _frame;
    }

    public abstract InspectorGroup[] getInspectorGroups();

    public final String getName() {
    	return getModule().getLocalizedName();
    }

    public final String getShortName() {
    	return getModule().getShortName();
    }

    public final String getDescription() {
    	return getModule().getLocalizedDescription();
    }

    public static FlexoModule getActiveModule()
    {
        return _activeModule;
    }

    public static boolean isRunningTest(){
    	return ((getActiveModule()!=null) &&
    			((getActiveModule().getEditor()) != null) &&
    		((getActiveModule().getEditor()).isTestEditor()));
    }

    public boolean isActive()
    {
        return isActive;
    }

    public void focusOn()
    {
        if (logger.isLoggable(Level.FINE))
            logger.fine("Module " + getName() + " receive focus ON");
        processFocusOn();
        int state = getFlexoFrame().getExtendedState();
        state &= ~Frame.ICONIFIED;
        getFlexoFrame().setExtendedState(state);
        getFlexoFrame().setVisible(true);
        SwingUtilities.invokeLater(new Runnable(){
            /**
             * Overrides run
             * @see java.lang.Runnable#run()
             */
            @Override
			public void run()
            {
                getFlexoFrame().toFront();
            }
        });
    }

    public void notifyFocusOn()
    {
        if (!isActive()) {
            if (logger.isLoggable(Level.FINE))
                logger.fine("Module " + getName() + " receive notification for focus ON");
            processFocusOn();
            getFlexoFrame().setRelativeVisible(true);
        }
    }

    public void notifyFocusGained()
    {
        if (logger.isLoggable(Level.FINE))
            logger.fine("notifyFocusGained() for " + this + ": Transition is " + desactivatingModule + " to " + activatingModule);
        if (ignoreFocusGainedNotifications) {
            if (System.currentTimeMillis() - dateWhenFocusGainedWasLocked > 3000) {
                // After 3 seconds, FocusGained locking time-out and is
                // inconditionnaly reset
                if (logger.isLoggable(Level.FINE))
                    logger.fine("TIME-OUT expired: UNLOCKING FocusOn ignoring for " + this);
                ignoreFocusGainedNotifications = false;
            }
        }
        if (!ignoreFocusGainedNotifications) {
            notifyFocusOn();
        } else {
            if (logger.isLoggable(Level.FINE))
                logger.fine("IGNORING notifyFocusGained() for " + this);
        }
        tryToUnlock();
    }

    /*
     * public void notifyRelativeWindowFocusGained() { tryToUnlock(); }
     */

    private void tryToUnlock()
    {
        if (activatingModule == this) {
            if (logger.isLoggable(Level.FINE))
                logger.fine("UNLOCKING FocusOn ignoring for " + desactivatingModule);
            if (desactivatingModule != null)
                desactivatingModule.ignoreFocusGainedNotifications = false;
            activatingModule = null;
            desactivatingModule = null;
        }
    }

    boolean ignoreFocusGainedNotifications = false;

    public void focusOff()
    {
        if (logger.isLoggable(Level.FINE))
            logger.fine("Module " + getName() + " receive focus OFF");
        if (getFlexoController() instanceof SelectionManagingController) {
            if (logger.isLoggable(Level.FINE))
                logger.fine("Module " + getName() + " is loosing focus : reseting selection");
            //((SelectionManagingController) getFlexoController()).getSelectionManager().resetSelection();
        }
        _activeModule = null;
        isActive = false;
        ignoreFocusGainedNotifications = true;
        dateWhenFocusGainedWasLocked = System.currentTimeMillis();
        getFlexoFrame().setRelativeVisible(false);
        if (_controller.getConsistencyCheckWindow() != null)
            _controller.getConsistencyCheckWindow().setVisible(false);
    }

    private FlexoModule activatingModule;

    private FlexoModule desactivatingModule;

    private long dateWhenFocusGainedWasLocked = 0;

    public void processFocusOn()
    {
        if (logger.isLoggable(Level.FINE))
            logger.fine("processFocusOn() called:  ActiveModule=" + _activeModule);
        if (logger.isLoggable(Level.FINE))
            logger.fine("processFocusOn() called:  this=" + this);
        if ((_activeModule != null) && (_activeModule != this)) {
            desactivatingModule = _activeModule;
            if (logger.isLoggable(Level.FINE))
                logger.fine(this + ": desactivatingModule=" + desactivatingModule);
            _activeModule.focusOff();
        }
        if (_activeModule != this) {
            setAsActiveModule();
        }
        boolean selectDefaultObject = false;
        if (getDefaultObjectToSelect() != null
                && (getFlexoController().getCurrentDisplayedObjectAsModuleView() == null || getFlexoController()
                        .getCurrentDisplayedObjectAsModuleView() == getDefaultObjectToSelect())) {
            if (getFlexoController() instanceof SelectionManagingController) {
                if (((SelectionManagingController) getFlexoController()).getSelectionManager().getFocusedObject() == null) {
                    selectDefaultObject = true;
                }
            } else {
                selectDefaultObject = true;
            }
        }
        if (selectDefaultObject)
            getFlexoController().setCurrentEditedObjectAsModuleView(getDefaultObjectToSelect());
        else if (getFlexoController() instanceof SelectionManagingController) {
            if (logger.isLoggable(Level.FINE))
                logger.fine("Module " + getName() + " is loosing focus : reseting selection");
            ((SelectionManagingController) getFlexoController()).getSelectionManager().setSelectedObjects(new Vector<FlexoModelObject>(((SelectionManagingController) getFlexoController()).getSelectionManager().getSelection()));
        }
    }

    private void setAsActiveModule()
    {
        if (logger.isLoggable(Level.FINE))
            logger.fine("Module " + getName() + " receive focusOn");
        WindowMenu.notifySwitchToModule(getModule());
        ModuleBar.notifyStaticallySwitchToModule(getModule());
        isActive = true;
        _activeModule = this;
        activatingModule = this;
        if (logger.isLoggable(Level.FINE))
            logger.fine(this + ": activatingModule=" + activatingModule);
        if (getFlexoController() instanceof SelectionManagingController) {
            if (logger.isLoggable(Level.FINE))
                logger.fine("Module " + getName() + " receive focus ON, set inspected object in sync with selection");
            // ((SelectionManagingController)
            // getFlexoController()).getSelectionManager().updateInspectorManagement();
            ((SelectionManagingController) getFlexoController()).getSelectionManager().fireUpdateSelection();
        }

    }

	@Override
	protected void finalize() throws Throwable {
		if (logger.isLoggable(Level.INFO))
			logger.info("Finalizing Module "+getClass().getSimpleName());
		super.finalize();
	}

    /**
     * Close Module after asking confirmation Review unsaved and save Unload in
     * module loader
     *
     * @return boolean indicating if close was performed
     */
    public boolean close()
    /*
	 * This method is used to request the closing of module (either because it has been hit in the menu or the red-cross of the current
	 * window has been pressed. To perform the real closing of the module, closeWithoutConfirmation() must be called. Once
	 * closeWithoutConfirmation has been called, there are no way back. Therefore any call to closeWithoutConfirmation() must be followed by
	 * a "return true"
	 */
    {
        boolean isLastModule = false;
        Enumeration en = ModuleLoader.loadedModules();
        if (en.hasMoreElements()) en.nextElement();
        if (!en.hasMoreElements())
            isLastModule = true;
        if (isLastModule) {
            if (someResourcesNeedsSaving()) {
                try {
                    SaveDialog reviewer = new SaveDialog(getFlexoFrame(), ModuleLoader.getProject());
                    if (reviewer.getRetval() == JOptionPane.YES_OPTION) {
                        ProgressWindow.showProgressWindow(FlexoLocalization.localizedForKey("saving"), 1);
                        reviewer.saveProject(ProgressWindow.instance());
                        ProgressWindow.hideProgressWindow();
                        closeWithoutConfirmation();
                        return true;
                    } else if (reviewer.getRetval() == JOptionPane.NO_OPTION) {
                        closeWithoutConfirmation();
                        return true;
                    } else {
                        return false;
                    }

                } catch (SaveResourceException e) {
                    e.printStackTrace();
                    ProgressWindow.hideProgressWindow();
                    if (FlexoController.confirm(FlexoLocalization.localizedForKey("error_during_saving") + "\n"
                            + FlexoLocalization.localizedForKey("would_you_like_to_close_anyway"))) {
                        closeWithoutConfirmation();
                        return true;
                    } else
                        return false;
                }
            } else {
                if (FlexoController.confirm(FlexoLocalization.localizedForKey("really_quit"))) {
                    closeWithoutConfirmation();
                    return true;
                } else
                    return false;
            }
        } else { // There are still other modules left
            /*if (someResourcesNeedsSaving()) {
                try {
                    ProgressWindow.showProgressWindow(FlexoLocalization.localizedForKey("saving"), 1);
                    if (saveWithReview(ProgressWindow.instance())) {
                        ProgressWindow.hideProgressWindow();
                        closeWithoutConfirmation();
                        return true;
                    } else {
                        ProgressWindow.hideProgressWindow();
                        return false;
                    }
                } catch (SaveResourcePermissionDeniedException e) {
                    ProgressWindow.hideProgressWindow();
                    if (FlexoController.confirm(FlexoLocalization.localizedForKey("error_during_saving") + "\n"
                            + FlexoLocalization.localizedForKey("would_you_like_to_close_anyway"))) {
                        closeWithoutConfirmation();
                        return true;
                    } else
                        return false;
                } catch (SaveResourceException e) {
                    e.printStackTrace();
                    ProgressWindow.hideProgressWindow();
                    if (FlexoController.confirm(FlexoLocalization.localizedForKey("error_during_saving") + "\n"
                            + FlexoLocalization.localizedForKey("would_you_like_to_close_anyway"))) {
                        closeWithoutConfirmation();
                        return true;
                    } else
                        return false;
                }
            } else {*/
                closeWithoutConfirmation();// Unloads the module
                return true; // Since there is nothing to save and that Flexo
                // has other windows opened to access it, we
                // close the module and that's it!!!
            //}
        }
    }

    public void closeWithoutConfirmation()
    {
        closeWithoutConfirmation(true);
    }

    public void closeWithoutConfirmation(boolean quitIfNoModuleLeft)
    {
        if (logger.isLoggable(Level.INFO))
            logger.info("Closing module " + getName());
        moduleWillClose();
        focusOff();
        if (_controller!=null)
        	_controller.dispose();
        else
        	if (logger.isLoggable(Level.WARNING))
				logger.warning("Called twice closeWithoutConfirmation on "+this);
        _controller = null;
        for (Enumeration e = usedResources.elements(); e.hasMoreElements();) {
            releaseResource((FlexoResource) e.nextElement());
        }
        if (ModuleLoader.isLoaded(getModule())) {
            ModuleLoader.unloadModule(getModule());
        }
        // Is there some modules loaded ?
        Enumeration leftModules = ModuleLoader.loadedModules();
        if (leftModules.hasMoreElements()) {
            ModuleLoader.switchToModule(((FlexoModule) leftModules.nextElement()).getModule());
        } else {
            _activeModule = null;
            if (quitIfNoModuleLeft) {
                ModuleLoader.quit(false);
            }
        }

    }

    public void moduleWillClose()
    {
    }

    public Module getModule()
    {
        return Module.getModule(getClass());
    }

    /**
     * Called to "tell" to this module is using this resource, and that this
     * resource must consequently be retained by this module
     *
     * @param resource
     */
    public void retainResource(FlexoResource resource)
    {
        if (logger.isLoggable(Level.FINE))
            logger.fine(getModule().getName() + " now retains " + resource);
        if (!usedResources.containsValue(resource)) {
            usedResources.put(resource.getResourceIdentifier(), resource);
            if (logger.isLoggable(Level.FINE))
                logger.fine("Resource " + resource.getResourceIdentifier() + " is retained by module " + getName());
        }
    }

    public boolean isRetaining(FlexoResource resource)
    {
    	if (usedResources == null || resource == null || resource.getResourceIdentifier() == null) return false;
        return (usedResources.get(resource.getResourceIdentifier()) != null);
    }

    /**
     * Called to "tell" to this module is using this resource data, and that
     * this corresponding resource must consequently be retained by this module
     *
     * @param resourceData
     */
    public void retain(FlexoResourceData resourceData)
    {
        if (logger.isLoggable(Level.FINE))
            logger.fine(getModule().getName() + " now retains " + resourceData);
        if (resourceData != null) {
            retainResource(resourceData.getFlexoResource());
        } else {
            if (logger.isLoggable(Level.SEVERE))
            	logger.severe("try to retain an null resource data");
        }
    }

    /**
     * Called to "tell" that this module is no more using this resource, and
     * that this resource can consequently be released by this module
     *
     * @param resource
     */
    public void releaseResource(FlexoResource resource)
    {
        if (usedResources.containsValue(resource)) {
            usedResources.remove(resource.getResourceIdentifier());
            if (logger.isLoggable(Level.FINE))
                logger.fine("Resource " + resource.getResourceIdentifier() + " has been released by module " + getName());
        }
    }

    /**
     * Called to "tell" that this module is no more using this resource data,
     * and that this corresponding resource can consequently be released by this
     * module
     *
     * @param resourceData
     */
    public void release(FlexoResourceData resourceData)
    {
        releaseResource(resourceData.getFlexoResource());
    }

    /**
     * Save all ressources for this Module, without launching review panel, nor
     * confirmation. If some error occurs, notify them in a panel
     *
     * @throws SaveResourceException
     */
    public void save()
    {
        if (logger.isLoggable(Level.INFO))
            logger.info("Saving resources for module " + getName());
        if (someResourcesNeedsSaving()) {
            try {
                saveWithoutReview();
            } catch (SaveResourceExceptionList e) {
                String errorFiles = e.errorFilesList();
                FlexoController.notify(FlexoLocalization.localizedForKey("error_during_saving") + "\n"
                        + FlexoLocalization.localizedForKey("following_files_have_not_been_saved") + "\n" + errorFiles);
            }
        }

    }

    /**
     * Save all ressources for this Module, without launching review panel
     * Display a confirmation when specified If some error occurs, notify in a
     * panel
     *
     * @throws SaveResourceException
     */
    public void save(boolean showConfirm)
    {
        save(showConfirm, false);
    }

    /**
     * Save all ressources for this Module, launch a review panel when
     * specified, display a confirmation when specified If some error occurs,
     * notify in a panel
     *
     * @throws SaveResourceException
     */
    public void save(boolean showConfirm, boolean makeReview)
    {
        if (logger.isLoggable(Level.INFO))
            logger.info("Saving resources for module " + getName());
        if (someResourcesNeedsSaving()) {
            try {
                if (makeReview) {
                    ProgressWindow.showProgressWindow(FlexoLocalization.localizedForKey("saving"), 1);
                    saveWithReview(showConfirm, ProgressWindow.instance());
                    ProgressWindow.hideProgressWindow();
                } else {
                    saveWithoutReview();
                    if (showConfirm) {
                        FlexoController.notify("Save complete.");
                    }
                }
            } catch (SaveResourceExceptionList e) {
                ProgressWindow.hideProgressWindow();
                String errorFiles = e.errorFilesList();
                FlexoController.notify(FlexoLocalization.localizedForKey("error_during_saving") + "\n"
                        + FlexoLocalization.localizedForKey("following_files_have_not_been_saved") + "\n" + errorFiles);
            } catch (SaveResourcePermissionDeniedException e) {
                ProgressWindow.hideProgressWindow();
                FlexoController.showError(FlexoLocalization.localizedForKey("error_during_saving") + "\n"
                        + FlexoLocalization.localizedForKey("reason_permission_denied"));
            } catch (SaveResourceException e) {
                ProgressWindow.hideProgressWindow();
                e.printStackTrace();
            }
        }

    }

    /**
     * Save all ressources for this Module, after reviewing
     *
     * @return boolean indicating if saving was confirmed
     * @throws SaveResourceException
     */
    private void saveWithoutReview() throws SaveResourceExceptionList
    {
        SaveResourceExceptionList listOfRaisedExceptions = null;
        for (Enumeration en = getUnsavedStorageResources().elements(); en.hasMoreElements();) {
            try {
                ((FlexoStorageResource) en.nextElement()).saveResourceData();
            } catch (SaveResourceException e) {
                // Warns about the exception
                if (logger.isLoggable(Level.WARNING))
                    logger.warning("Could not save resource: exception raised: " + e.getClass().getName() + ". See console for details.");
                e.printStackTrace();
                if (listOfRaisedExceptions == null) {
                    listOfRaisedExceptions = new SaveResourceExceptionList(e);
                } else {
                    listOfRaisedExceptions.registerNewException(e);
                }
            }
        }
        if (listOfRaisedExceptions != null) {
            throw listOfRaisedExceptions;
        }

    }

    /**
     * Save all ressources for this Module, after reviewing
     *
     * @return boolean indicating if saving was confirmed
     * @throws SaveResourceException
     * @throws SaveResourceException
     */
    private boolean saveWithReview(FlexoProgress progress) throws SaveResourceException
    {
        return saveWithReview(false, progress);
    }

    /**
     * Save all ressources for this Module, after reviewing Show confirmation of
     * saving, when required
     *
     * @return boolean indicating if saving was confirmed
     * @throws SaveResourceException
     * @throws SaveResourceException
     */
    private boolean saveWithReview(boolean showConfirm, FlexoProgress progress) throws SaveResourceException
    {
        if (logger.isLoggable(Level.INFO))
            logger.info("Saving resources for module " + getName());
        if (someResourcesNeedsSaving()) {
            SaveDialog reviewer = new SaveDialog(getActiveModule()!=null?getActiveModule().getFlexoFrame():null, getProject(), this);
            if (reviewer.getRetval() == JOptionPane.YES_OPTION) {
                reviewer.saveProject(progress);
                return true;
            } else if (reviewer.getRetval() == JOptionPane.NO_OPTION)
                return true;
            return false;
        }
        return true;
    }

    /**
     * Return boolean indicating if some resources need saving
     */
    protected boolean someResourcesNeedsSaving()
    {
        if (getProject() != null)
            return getProject().getUnsavedStorageResources(false).size() > 0;
        return false;
    }

    /**
     * Returns a Vector of FlexoStorageResource, corresponding to all unsaved
     * file resources retained by this module
     *
     * @return
     */
    public Vector<FlexoStorageResource> getUnsavedStorageResources()
    {
        Vector<FlexoStorageResource> returned = new Vector<FlexoStorageResource>();
        for (Enumeration e = getLoadedStorageResources().elements(); e.hasMoreElements();) {
            FlexoStorageResource resource = (FlexoStorageResource) e.nextElement();
            if (resource.needsSaving()) {
                returned.add(resource);
            }
        }
        return returned;
    }

    /**
     * Returns a Vector of FlexoStorageResource, corresponding to all unsaved
     * file resources retained by this module
     *
     * @return
     */
    public Vector<FlexoStorageResource> getLoadedStorageResources()
    {
        Vector<FlexoStorageResource> returned = new Vector<FlexoStorageResource>();
        for (Enumeration e = usedResources.elements(); e.hasMoreElements();) {
            FlexoResource resource = (FlexoResource) e.nextElement();
            if ((resource instanceof FlexoStorageResource) && (((FlexoStorageResource) resource).isLoaded())) {
                returned.add((FlexoStorageResource) resource);
            }
        }
        return returned;
    }

    /**
     * Returns hashtable where all the resources used by this module are stored,
     * with associated key which is a String identifying the resource
     * (resourceIdentifier)
     */
    public Hashtable getUsedResources()
    {
        return usedResources;
    }

    @Override
	public void update(FlexoObservable observable, DataModification dataModification)
    {
        if (logger.isLoggable(Level.FINE))
            logger.fine("update with " + dataModification);
        if (dataModification instanceof ResourceRemoved) {
            if (logger.isLoggable(Level.INFO))
                logger.info("Release resource " + ((ResourceRemoved) dataModification).getRemovedResource());
            releaseResource(((ResourceRemoved) dataModification).getRemovedResource());
        }
    }

    public FlexoProject getProject()
    {
        return _editor.getProject();
    }

    public abstract FlexoModelObject getDefaultObjectToSelect();

	public InteractiveFlexoEditor getEditor() {
		return _editor;
	}

}
