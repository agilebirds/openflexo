package org.openflexo.swing;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.event.KeyEvent;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;

public class GlassPaneWrapper extends JPanel {

	public GlassPaneWrapper(JComponent wrappedComponent) {
		super(new BorderLayout());
		super.addImpl(wrappedComponent, BorderLayout.CENTER, 0);
	}

	@Override
	public boolean contains(int x, int y) {
		return false;
	}

	@Override
	protected boolean processKeyBinding(KeyStroke ks, KeyEvent e, int condition, boolean pressed) {
		return false;
	}

	@Override
	public void addNotify() {
		super.addNotify();
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				repaint();
			}
		});
	}

	@Override
	protected void addImpl(Component comp, Object constraints, int index) {

	}

	@Override
	public void paint(Graphics g) {
		super.paint(g);
		g.setColor(new Color(240, 240, 240, 128));
		g.fillRect(0, 0, getWidth(), getHeight());
	}
}
