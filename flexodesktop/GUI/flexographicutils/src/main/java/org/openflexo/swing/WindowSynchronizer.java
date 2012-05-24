package org.openflexo.swing;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.Window;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.ArrayList;
import java.util.List;

public class WindowSynchronizer implements ComponentListener, WindowListener {

	private List<Window> synchronizedWindows;
	private List<Window> shownWindows;
	private Window activeWindow;

	private Dimension size;
	private Point location;

	public WindowSynchronizer() {
		synchronizedWindows = new ArrayList<Window>();
		shownWindows = new ArrayList<Window>();
	}

	public void addToSynchronizedWindows(Window window) {
		if (!synchronizedWindows.contains(window)) {
			synchronizedWindows.add(window);
			if (window.isVisible()) {
				shownWindows.add(window);
			}
			window.addWindowListener(this);
			window.addComponentListener(this);
		}
	}

	public void removeFromSynchronizedWindows(Window window) {
		synchronizedWindows.remove(window);
		shownWindows.remove(window);
		window.removeWindowListener(this);
		window.removeComponentListener(this);
	}

	@Override
	public void windowOpened(WindowEvent e) {
	}

	@Override
	public void windowClosing(WindowEvent e) {
	}

	@Override
	public void windowClosed(WindowEvent e) {
		resetIfNeeded(e);
	}

	@Override
	public void windowIconified(WindowEvent e) {
	}

	@Override
	public void windowDeiconified(WindowEvent e) {
	}

	@Override
	public void windowActivated(WindowEvent e) {
		activeWindow = e.getWindow();
		if (activeWindow != null) {
			if (size != null) {
				activeWindow.setSize(size);
			} else {
				size = activeWindow.getSize();
			}
			if (location != null) {
				activeWindow.setLocation(location);
			} else {
				location = activeWindow.getLocation();
			}
		}
	}

	@Override
	public void windowDeactivated(WindowEvent e) {
		resetIfNeeded(e);
	}

	protected void resetIfNeeded(WindowEvent e) {
		if (e.getWindow() == activeWindow) {
			activeWindow = null;
		}
	}

	@Override
	public void componentResized(ComponentEvent e) {
		if (e.getComponent() == activeWindow) {
			size = activeWindow.getSize();
			location = activeWindow.getLocation();
			updateSizeForWindowsBut(activeWindow);
		}

	}

	private void updateSizeForWindowsBut(Window activeWindow) {
		for (Window w : shownWindows) {
			if (w != activeWindow) {
				w.setSize(size);
			}
		}
	}

	@Override
	public void componentMoved(ComponentEvent e) {
		if (e.getComponent() == activeWindow) {
			size = activeWindow.getSize();
			location = activeWindow.getLocation();
			updateLocationForWindowsBut(activeWindow);
		}
	}

	private void updateLocationForWindowsBut(Window activeWindow) {
		for (Window w : shownWindows) {
			if (w != activeWindow) {
				w.setLocation(location);
			}
		}
	}

	@Override
	public void componentShown(ComponentEvent e) {
		shownWindows.add((Window) e.getComponent());
	}

	@Override
	public void componentHidden(ComponentEvent e) {
		shownWindows.remove(e.getComponent());
	}

}
