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

import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

import org.openflexo.FlexoCst;
import org.openflexo.GeneralPreferences;
import org.openflexo.ch.FCH;
import org.openflexo.components.ProgressWindow;
import org.openflexo.foundation.DataModification;
import org.openflexo.foundation.FlexoEditor;
import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.FlexoObservable;
import org.openflexo.foundation.GraphicalFlexoObserver;
import org.openflexo.foundation.NameChanged;
import org.openflexo.foundation.action.FlexoActionSource;
import org.openflexo.foundation.rm.ResourceStatusModification;
import org.openflexo.icon.IconLibrary;
import org.openflexo.module.FlexoModule;
import org.openflexo.module.ModuleLoader;
import org.openflexo.module.ModuleLoadingException;
import org.openflexo.module.ProjectLoader;
import org.openflexo.prefs.FlexoPreferences;
import org.openflexo.toolbox.ToolBox;
import org.openflexo.view.controller.FlexoController;
import org.openflexo.view.controller.model.ControllerModel;

/**
 * Abstract main frame used in the context of an application module
 * 
 * @author sguerin
 */

public final class FlexoFrame extends JFrame implements GraphicalFlexoObserver, FlexoActionSource, PropertyChangeListener {

	private final class FlexoModuleWindowListener extends WindowAdapter {

		@Override
		public void windowActivated(WindowEvent e) {
			if (!(e.getOppositeWindow() instanceof ProgressWindow) && getModuleLoader().isLoaded(getModule().getModule())) {
				switchToModule();
			}
		}

		@Override
		public void windowClosing(WindowEvent event) {
			close();
		}
	}

	static final Logger logger = Logger.getLogger(FlexoFrame.class.getPackage().getName());

	private FlexoController _controller;

	private List<FlexoRelativeWindow> _relativeWindows;

	private List<FlexoRelativeWindow> _displayedRelativeWindows;

	private ComponentListener windowResizeListener;

	private MouseListener mouseListener;

	private WindowListener windowListener;

	public static FlexoFrame getActiveFrame() {
		return getActiveFrame(true);
	}

	public static FlexoFrame getActiveFrame(boolean createDefaultIfNull) {
		for (Frame frame : getFrames()) {
			if (frame.isActive()) {
				if (frame instanceof FlexoFrame) {
					return (FlexoFrame) frame;
				} else if (frame instanceof FlexoRelativeWindow) {
					((FlexoRelativeWindow) frame).getParentFrame();
				} else {
					if (logger.isLoggable(Level.WARNING)) {
						logger.warning("Found active frame " + frame.getTitle() + " which is not a FlexoFrame nor a Relative Window.");
					}
				}
				// We break since there won't be any other active frame.
				break;
			}
		}
		return createDefaultIfNull ? getDefaultFrame() : null;
	}

	public static Frame getOwner(Frame owner) {
		return owner != null ? owner : getActiveFrame();
	}

	private static FlexoFrame defaultFrame;

	private static FlexoFrame getDefaultFrame() {
		if (defaultFrame == null) {
			defaultFrame = new FlexoFrame();
			defaultFrame.setUndecorated(true);
			defaultFrame.setBounds(Toolkit.getDefaultToolkit().getScreenSize().width / 2,
					Toolkit.getDefaultToolkit().getScreenSize().height / 2, 0, 0);
			defaultFrame.setResizable(false);
			defaultFrame.setVisible(true);
		}
		return defaultFrame;
	}

	private static void disposeDefaultFrameWhenPossible() {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				disposeDefaultFrame();
			}
		});
	}

	private static void disposeDefaultFrame() {
		Frame f = defaultFrame;
		if (f != null) {
			boolean isDisposable = true;
			for (Window w : f.getOwnedWindows()) {
				isDisposable &= !w.isVisible();
			}
			if (isDisposable) {
				f.setVisible(false);
				f.dispose();
				defaultFrame = null;
			} else {
				disposeDefaultFrameWhenPossible();
			}
		}
	}

	private FlexoFrame() {
		super(FlexoCst.BUSINESS_APPLICATION_VERSION_NAME);
		setIconImage(IconLibrary.OPENFLEXO_NOTEXT_128.getImage());
	}

	public FlexoFrame(FlexoController controller) {
		super();
		_controller = controller;
		_relativeWindows = new Vector<FlexoRelativeWindow>();
		_displayedRelativeWindows = new Vector<FlexoRelativeWindow>();
		_controller.getControllerModel().getPropertyChangeSupport().addPropertyChangeListener(ControllerModel.CURRENT_EDITOR, this);
		_controller.getControllerModel().getPropertyChangeSupport().addPropertyChangeListener(ControllerModel.CURRENT_PERPSECTIVE, this);
		_controller.getControllerModel().getPropertyChangeSupport().addPropertyChangeListener(ControllerModel.CURRENT_OBJECT, this);
		if (defaultFrame != null) {
			disposeDefaultFrameWhenPossible();
		}
		if (ToolBox.getPLATFORM() != ToolBox.WINDOWS) {
			setIconImage(controller.getModule().getModule().getBigIcon().getImage());
		} else {
			setIconImage(IconLibrary.OPENFLEXO_NOTEXT_128.getImage());
		}
		setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		setResizable(true);
		setFocusable(true);

		/**
		 * Listeners
		 */
		addWindowListener(windowListener = new FlexoModuleWindowListener());
		Rectangle bounds = GeneralPreferences.getBoundForFrameWithID(getController().getModule().getShortName() + "Frame");
		if (bounds != null) {
			// In case we remove a screen (if you go from 3 to 2 screen, go to hell, that's all you deserve ;-))
			if (GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices().length == 1) {
				Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
				if (screen.width <= bounds.x) {
					bounds.x = 0;
				} else if (screen.height <= bounds.y) {
					bounds.y = 0;
				}
			}
			setBounds(bounds);
		}
		int state = GeneralPreferences.getFrameStateForFrameWithID(getController().getModule().getShortName() + "Frame");
		if (state != -1
				&& ((state & Frame.MAXIMIZED_BOTH) == Frame.MAXIMIZED_BOTH || (state & Frame.MAXIMIZED_HORIZ) == Frame.MAXIMIZED_HORIZ || (state & Frame.MAXIMIZED_VERT) == Frame.MAXIMIZED_VERT)) {
			setExtendedState(GeneralPreferences.getFrameStateForFrameWithID(getController().getModule().getShortName() + "Frame"));
		}
	}

	public FlexoModule getModule() {
		return getController().getModule();
	}

	protected void switchToModule() {
		try {
			getModuleLoader().switchToModule(getModule().getModule());
		} catch (ModuleLoadingException e1) {
			e1.printStackTrace();
		}
	}

	private ModuleLoader getModuleLoader() {
		return getController().getModuleLoader();
	}

	/**
	 * @return Returns the controller.
	 */
	public FlexoController getController() {
		return _controller;
	}

	public String getLocalizedName() {
		return getModule().getName();
	}

	public void addToRelativeWindows(FlexoRelativeWindow aRelativeWindow) {
		if (!_relativeWindows.contains(aRelativeWindow)) {
			_relativeWindows.add(aRelativeWindow);
		}
	}

	public void removeFromRelativeWindows(FlexoRelativeWindow aRelativeWindow) {
		if (_relativeWindows.contains(aRelativeWindow)) {
			_relativeWindows.remove(aRelativeWindow);
		}
		removeFromDisplayedRelativeWindows(aRelativeWindow);
	}

	public void disposeAll() {
		for (FlexoRelativeWindow next : new ArrayList<FlexoRelativeWindow>(_relativeWindows)) {
			next.dispose();
		}
		_relativeWindows.clear();
		if (_controller != null) {
			_controller.getControllerModel().getPropertyChangeSupport().removePropertyChangeListener(ControllerModel.CURRENT_EDITOR, this);
			_controller.getControllerModel().getPropertyChangeSupport()
					.removePropertyChangeListener(ControllerModel.CURRENT_PERPSECTIVE, this);
			_controller.getControllerModel().getPropertyChangeSupport().removePropertyChangeListener(ControllerModel.CURRENT_OBJECT, this);
			if (_controller.getProject() != null) {
				_controller.getProject().deleteObserver(this);
			}
			_controller = null;
		}
		if (windowListener != null) {
			removeWindowListener(windowListener);
			windowListener = null;
		}
		if (mouseListener != null) {
			removeMouseListener(mouseListener);
		}
		setJMenuBar(null);
		if (getContentPane() != null) {
			getContentPane().removeAll();
		}
		if (logger.isLoggable(Level.INFO)) {
			logger.info("Disposing " + this);
		}
		if (ProgressWindow.hasInstance()) {
			ProgressWindow.hideProgressWindow();
		}
		dispose();
	}

	public void addToDisplayedRelativeWindows(FlexoRelativeWindow aRelativeWindow) {
		if (!_displayedRelativeWindows.contains(aRelativeWindow)) {
			_displayedRelativeWindows.add(aRelativeWindow);
		}
		if (logger.isLoggable(Level.FINE)) {
			logger.finer("addToRelativeWindows with " + aRelativeWindow);
		}
	}

	public void removeFromDisplayedRelativeWindows(FlexoRelativeWindow aRelativeWindow) {
		if (_displayedRelativeWindows.contains(aRelativeWindow)) {
			_displayedRelativeWindows.remove(aRelativeWindow);
		}
		if (logger.isLoggable(Level.FINE)) {
			logger.finer("removeFromRelativeWindows with " + aRelativeWindow);
		}
	}

	static final String WINDOW_MODIFIED = "windowModified";

	@Override
	public void update(final FlexoObservable observable, final DataModification dataModification) {
		if (getController() == null) {
			observable.deleteObserver(this);
			return;
		}
		if (getController().getProject() == null) {
			return;
		}
		if (!SwingUtilities.isEventDispatchThread()) {
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					update(observable, dataModification);
				}
			});
			return;
		}
		if (dataModification instanceof NameChanged) {
			updateTitle();
		} else if ("projectDirectory".equals(dataModification.propertyName())) {
			updateTitle();
		}
		if (ToolBox.getPLATFORM() == ToolBox.MACOS && dataModification instanceof ResourceStatusModification) {
			getRootPane().putClientProperty(WINDOW_MODIFIED, ProjectLoader.someResourcesNeedsSaving(getController().getProject()));
		}
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		if (evt.getPropertyName().equals(ControllerModel.CURRENT_EDITOR)) {
			FlexoEditor old = (FlexoEditor) evt.getOldValue();
			FlexoEditor newValue = (FlexoEditor) evt.getNewValue();
			if (old != null && old.getProject() != null) {
				old.getProject().deleteObserver(this);
			}
			if (newValue != null && newValue.getProject() != null) {
				newValue.getProject().addObserver(this);
			}
			updateTitle();
		}
	}

	@Override
	public void setVisible(boolean mainFrameIsVisible) {
		if (getController() != null) {
			if (mainFrameIsVisible && getModule() != null && getModule().isActive() || !mainFrameIsVisible) {
				setRelativeVisible(mainFrameIsVisible);
				super.setVisible(mainFrameIsVisible);
			}
			if (windowResizeListener == null) {
				windowResizeListener = new ComponentAdapter() {

					@Override
					public void componentMoved(ComponentEvent e) {
						saveBoundsInPreferenceWhenPossible();
					}

					@Override
					public void componentResized(ComponentEvent e) {
						saveBoundsInPreferenceWhenPossible();
					}

				};
				addComponentListener(windowResizeListener);
			}
		} else {
			super.setVisible(mainFrameIsVisible);
		}
	}

	public void setRelativeVisible(boolean relativeWindowsAreVisible) {
		if (relativeWindowsAreVisible) {
			showRelativeWindows();
		} else {
			hideRelativeWindows();
		}
	}

	private int showRelativeWindows() {
		int returned = 0;
		if (_displayedRelativeWindows != null) {
			for (FlexoRelativeWindow next : _displayedRelativeWindows) {
				if (!next.isShowing()) {
					next.setVisibleNoParentFrameNotification(true);
					returned++;
				}
			}
		}
		return returned;
	}

	private void hideRelativeWindows() {
		if (_displayedRelativeWindows != null) {
			for (FlexoRelativeWindow next : _displayedRelativeWindows) {
				if (next.isShowing()) {
					next.setVisibleNoParentFrameNotification(false);
				}
			}
		}
	}

	public void updateTitle() {
		setTitle(getController().getWindowTitle());
	}

	public List<FlexoRelativeWindow> getRelativeWindows() {
		return _relativeWindows;
	}

	@Override
	public void validate() {
		super.validate();
		FCH.validateWindow(this);
	}

	private Thread boundsSaver;

	protected synchronized void saveBoundsInPreferenceWhenPossible() {
		if (!isVisible()) {
			return;
		}
		if (boundsSaver != null) {
			boundsSaver.interrupt();// Resets thread sleep
			return;
		}

		boundsSaver = new Thread(new Runnable() {
			@Override
			public void run() {
				boolean go = true;
				while (go) {
					try {
						go = false;
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						go = true;// interruption is used to reset sleep.
					}
				}
				saveBoundsInPreference();
			}
		});
		boundsSaver.start();
	}

	protected void saveBoundsInPreference() {
		if (getController() == null) {
			return;
		}
		int state = getExtendedState();
		if (state == -1 || (state & Frame.MAXIMIZED_BOTH) != Frame.MAXIMIZED_BOTH
				&& (state & Frame.MAXIMIZED_HORIZ) != Frame.MAXIMIZED_HORIZ && (state & Frame.MAXIMIZED_VERT) != Frame.MAXIMIZED_VERT) {
			GeneralPreferences.setBoundForFrameWithID(getController().getModule().getShortName() + "Frame", getBounds());
		}
		GeneralPreferences.setFrameStateForFrameWithID(getController().getModule().getShortName() + "Frame", getExtendedState());
		FlexoPreferences.savePreferences(true);
		boundsSaver = null;
	}

	@Override
	public FlexoEditor getEditor() {
		return getController().getEditor();
	}

	@Override
	public FlexoModelObject getFocusedObject() {
		return getController().getSelectionManager().getFocusedObject();
	}

	@Override
	public Vector<FlexoModelObject> getGlobalSelection() {
		return getController().getSelectionManager().getSelection();
	}

	public void close() {
		if (getModule().close()) {
			dispose();
		}
	}

}