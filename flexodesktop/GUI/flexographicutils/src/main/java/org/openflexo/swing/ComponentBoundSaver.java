package org.openflexo.swing;

import java.awt.Component;
import java.awt.Rectangle;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;

public abstract class ComponentBoundSaver implements ComponentListener {

	private final Component component;

	private Thread boundsSaver;

	public ComponentBoundSaver(Component component) {
		this.component = component;
		component.addComponentListener(this);
	}

	@Override
	public void componentResized(ComponentEvent e) {
		saveBoundsWhenPossible();
	}

	@Override
	public void componentMoved(ComponentEvent e) {
		saveBoundsWhenPossible();
	}

	@Override
	public void componentShown(ComponentEvent e) {

	}

	@Override
	public void componentHidden(ComponentEvent e) {

	}

	private void saveBoundsWhenPossible() {
		if (!component.isVisible()) {
			return;
		}
		if (boundsSaver != null) {
			boundsSaver.interrupt();// Resets thread sleep
			return;
		}

		boundsSaver = new Thread(new Runnable() {
			/**
			 * Overrides run
			 * 
			 * @see java.lang.Runnable#run()
			 */
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
				saveBounds(component.getBounds());
				boundsSaver = null;
			}
		});
		boundsSaver.start();
	}

	public abstract void saveBounds(Rectangle bounds);

}
