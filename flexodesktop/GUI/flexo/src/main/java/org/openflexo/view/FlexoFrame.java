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
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.Enumeration;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

import org.openflexo.ColorCst;
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
import org.openflexo.module.ProjectLoader;
import org.openflexo.prefs.FlexoPreferences;
import org.openflexo.toolbox.ToolBox;
import org.openflexo.view.controller.FlexoController;
import org.openflexo.view.controller.SelectionManagingController;
import org.openflexo.view.listener.FlexoKeyEventListener;
import org.openflexo.view.menu.FlexoMenuBar;

/**
 * Abstract main frame used in the context of an application module
 * 
 * @author sguerin
 */

public abstract class FlexoFrame extends JFrame implements GraphicalFlexoObserver, FocusListener, FlexoActionSource {

	private final class FlexoModuleWindowListener extends WindowAdapter {

		@Override
		public void windowDeiconified(WindowEvent e) {
			activateModuleIfPossible(e);
		}

		@Override
		public void windowGainedFocus(WindowEvent e) {
			activateModuleIfPossible(e);
		}

		@Override
		public void windowActivated(WindowEvent e) {
			activateModuleIfPossible(e);
		}

		private void activateModuleIfPossible(WindowEvent e) {
			if (!(e.getOppositeWindow() instanceof ProgressWindow) && getModuleLoader().isLoaded(_module.getModule())) {
				getModuleLoader().activateModule(_module.getModule());
			}
		}

		@Override
		public void windowClosing(WindowEvent event) {
			if (getModule().close()) {
				dispose();
			}
		}
	}

	static final Logger logger = Logger.getLogger(FlexoFrame.class.getPackage().getName());

	protected FlexoModule _module;

	private FlexoController _controller;

	private FlexoMenuBar _menuBar;

	private FlexoKeyEventListener _keyEventListener;

	private Vector<FlexoRelativeWindow> _relativeWindows;

	private Vector<FlexoRelativeWindow> _displayedRelativeWindows;

	private ComponentListener windowResizeListener;

	private MouseListener mouseListener;

	private WindowListener windowListener;

	public static FlexoFrame getActiveFrame() {
		return getActiveFrame(true);
	}

	public static FlexoFrame getActiveFrame(boolean createDefaultIfNull) {
		return FlexoModule.getActiveModule() != null ? FlexoModule.getActiveModule().getFlexoFrame()
				: createDefaultIfNull ? getDefaultFrame() : null;
	}

	public static Frame getOwner(Frame owner) {
		return owner != null ? owner : getActiveFrame();
	}

	private static FlexoFrame defaultFrame;

	private static FlexoFrame getDefaultFrame() {
		if (defaultFrame == null) {
			defaultFrame = new FlexoFrame() {

			};
			defaultFrame.setUndecorated(true);
			defaultFrame.pack();
			defaultFrame.setBounds(Toolkit.getDefaultToolkit().getScreenSize().width / 2,
					Toolkit.getDefaultToolkit().getScreenSize().height / 2, 0, 0);
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

	public FlexoFrame(String title, FlexoController controller, FlexoKeyEventListener keyEventListener, FlexoMenuBar menuBar) {
		super(title);
		if (defaultFrame != null) {
			disposeDefaultFrameWhenPossible();
		}
		if (controller.getProject() != null) {
			controller.getProject().addObserver(this);
		}
		if (ToolBox.getPLATFORM() != ToolBox.WINDOWS) {
			setIconImage(controller.getModule().getModule().getBigIcon().getImage());
		} else {
			setIconImage(IconLibrary.OPENFLEXO_NOTEXT_128.getImage());
		}
		setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		setBackground(ColorCst.GUI_BACK_COLOR);
		_controller = controller;
		_keyEventListener = keyEventListener;
		_menuBar = menuBar;
		setJMenuBar(_menuBar);
		setResizable(true);
		setFocusable(true);
		_relativeWindows = new Vector<FlexoRelativeWindow>();
		_displayedRelativeWindows = new Vector<FlexoRelativeWindow>();

		if (controller.getProject() != null) {
			getRootPane().putClientProperty(WINDOW_MODIFIED, ProjectLoader.someResourcesNeedsSaving(getController().getProject()));
		}

		/**
		 * Listeners
		 */
		addFocusListener(this);
		addKeyListener(keyEventListener);
		addWindowListener(windowListener = new FlexoModuleWindowListener());
		addMouseListener(mouseListener = new MouseAdapter() {

			@Override
			public void mousePressed(MouseEvent e) {
				if (getModuleLoader().isLoaded(_module.getModule())) {
					getModuleLoader().activateModule(_module.getModule());
				}
			}

		});
	}

	/**
	 * @return Returns the controller.
	 */
	public FlexoController getController() {
		return _controller;
	}

	public FlexoModule getModule() {
		return _module;
	}

	public void setModule(FlexoModule module) {
		_module = module;
	}

	public FlexoKeyEventListener getKeyEventListener() {
		return _keyEventListener;
	}

	public String getLocalizedName() {
		return getModule().getName();
	}

	// ==========================================================================
	// ========================= Relative windows
	// ===============================
	// ==========================================================================

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
		for (Enumeration<FlexoRelativeWindow> e = new Vector<FlexoRelativeWindow>(_relativeWindows).elements(); e.hasMoreElements();) {
			FlexoRelativeWindow next = e.nextElement();
			next.dispose();
		}
		_relativeWindows.clear();
		if (_controller != null) {
			if (_controller.getProject() != null) {
				_controller.getProject().deleteObserver(this);
			}
			if (_controller.getConsistencyCheckWindow() != null) {
				_controller.getConsistencyCheckWindow().dispose();
			}
			_controller = null;
		}
		if (_keyEventListener != null) {
			removeKeyListener(_keyEventListener);
		}
		if (windowListener != null) {
			removeWindowListener(windowListener);
		}
		if (mouseListener != null) {
			removeMouseListener(mouseListener);
		}
		removeFocusListener(this);
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
		_keyEventListener = null;
		windowListener = null;
		_menuBar = null;
		_module = null;
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
		if (getController().getProject() == null) {
			return;
		}
		if (logger.isLoggable(Level.FINE)) {
			logger.fine("Update in " + getClass().getName() + " getController().getProject().getUnsavedStorageResources().size()="
					+ getController().getProject().getUnsavedStorageResources(false).size());
		}
		if (ToolBox.getPLATFORM() == ToolBox.MACOS && dataModification instanceof ResourceStatusModification) {
			getRootPane().putClientProperty(WINDOW_MODIFIED, ProjectLoader.someResourcesNeedsSaving(getController().getProject()));
		}
	}

	@Override
	public void focusGained(FocusEvent event) {
		if (getModule() != null) {
			getModule().notifyFocusGained();
		}
	}

	@Override
	public void focusLost(FocusEvent event) {
	}

	@Override
	public void setVisible(boolean mainFrameIsVisible) {
		boolean old = isVisible();
		if (getController() != null) {
			if (!old && mainFrameIsVisible
					&& GeneralPreferences.getBoundForFrameWithID(getController().getModule().getShortName() + "Frame") != null) {
				if (windowResizeListener != null) {
					removeComponentListener(windowResizeListener);
				}
				Rectangle bounds = GeneralPreferences.getBoundForFrameWithID(getController().getModule().getShortName() + "Frame");

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
				int state = GeneralPreferences.getFrameStateForFrameWithID(getController().getModule().getShortName() + "Frame");
				if (state != -1
						&& ((state & Frame.MAXIMIZED_BOTH) == Frame.MAXIMIZED_BOTH
								|| (state & Frame.MAXIMIZED_HORIZ) == Frame.MAXIMIZED_HORIZ || (state & Frame.MAXIMIZED_VERT) == Frame.MAXIMIZED_VERT)) {
					setExtendedState(GeneralPreferences.getFrameStateForFrameWithID(getController().getModule().getShortName() + "Frame"));
				}
				if (windowResizeListener != null) {
					addComponentListener(windowResizeListener);
				}
			}
			if (mainFrameIsVisible && getModule() != null && getModule().isActive() || !mainFrameIsVisible) {
				setRelativeVisible(mainFrameIsVisible);
				super.setVisible(mainFrameIsVisible);
				if (mainFrameIsVisible) {
					if (getModule() != null) {
						getModule().notifyFocusOn();
					}
				}
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

					@Override
					public void componentShown(ComponentEvent e) {
						if (getModuleLoader().isLoaded(_module.getModule())) {
							getModuleLoader().activateModule(_module.getModule());
						}
					}

				};
				addComponentListener(windowResizeListener);
			}
		} else {
			super.setVisible(mainFrameIsVisible);
		}
	}

	private ModuleLoader getModuleLoader() {
		return ModuleLoader.instance();
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
			for (Enumeration e = _displayedRelativeWindows.elements(); e.hasMoreElements();) {
				FlexoRelativeWindow next = (FlexoRelativeWindow) e.nextElement();
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
			for (Enumeration e = _displayedRelativeWindows.elements(); e.hasMoreElements();) {
				FlexoRelativeWindow next = (FlexoRelativeWindow) e.nextElement();
				if (next.isShowing()) {
					next.setVisibleNoParentFrameNotification(false);
				}
			}
		}
	}

	public void updateTitle() {
		String projectTitle = _controller.getProject() != null ? " - " + _controller.getProject().getProjectName() + " - "
				+ _controller.getProjectDirectory().getAbsolutePath() : "";
		if (getController().getCurrentModuleView() != null && getModule() != null) {
			setTitle(getModule().getName() + " : " + getViewTitle() + projectTitle);
		} else {
			if (getModule() == null) {
				setTitle(FlexoCst.BUSINESS_APPLICATION_VERSION_NAME + projectTitle);
			} else {
				setTitle(FlexoCst.BUSINESS_APPLICATION_VERSION_NAME + " - " + getModule().getName() + projectTitle);
			}
		}
	}

	public String getViewTitle() {
		return getController().getWindowTitleforObject(getController().getCurrentModuleView().getRepresentedObject());
	}

	public Vector<FlexoRelativeWindow> getRelativeWindows() {
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
		if (getController() instanceof SelectionManagingController) {
			return ((SelectionManagingController) getController()).getSelectionManager().getFocusedObject();
		}
		return null;
	}

	@Override
	public Vector getGlobalSelection() {
		if (getController() instanceof SelectionManagingController) {
			return ((SelectionManagingController) getController()).getSelectionManager().getSelection();
		}
		return null;
	}

}