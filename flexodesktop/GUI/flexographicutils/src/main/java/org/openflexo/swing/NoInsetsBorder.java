package org.openflexo.swing;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Insets;

import javax.swing.border.Border;

public class NoInsetsBorder implements Border {

	private Border delegate;

	public NoInsetsBorder(Border delegate) {
		super();
		this.delegate = delegate;
	}

	@Override
	public Insets getBorderInsets(Component c) {
		return new Insets(0, 0, 0, 0);
	}

	@Override
	public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
		delegate.paintBorder(c, g, x, y, width, height);
	}

	@Override
	public boolean isBorderOpaque() {
		return delegate.isBorderOpaque();
	}

}