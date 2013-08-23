package org.openflexo.swing;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.LayoutManager;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

import org.apache.commons.lang3.SystemUtils;

/**
 * this layout manager automatically displays a set of components on a row (typically JButton). All children components get the same size
 * (maximum preferred size amongst each child components). If not specified, components will be aligned according to default value of LAF.
 * If not specified, LTR/RTL is automatically chosen by the LAF.
 * 
 * I.e., on most platforms, buttons are centered by default and are laid out from left to right. Except on MacOS LAF, where buttons are
 * aligned on the right and laid out from right to left.
 * 
 * @author Guillaume
 * 
 */
public class ButtonLayout implements LayoutManager, SwingConstants {
	private Boolean leftToRight;
	private int alignment;
	private int hgap;

	public ButtonLayout() {
		this(10);
	}

	public ButtonLayout(int hgap) {
		this(-1, hgap);
	}

	public ButtonLayout(int alignment, int hgap) {
		this(alignment, hgap, null);
	}

	public ButtonLayout(int alignment, int hgap, Boolean leftToRight) {
		this.alignment = alignment;
		this.hgap = hgap;
		this.leftToRight = leftToRight;
	}

	@Override
	public void addLayoutComponent(String name, Component comp) {

	}

	@Override
	public void removeLayoutComponent(Component comp) {

	}

	@Override
	public Dimension preferredLayoutSize(Container parent) {
		Insets insets = parent.getInsets();
		int ncomponents = parent.getComponentCount();
		if (ncomponents == 0) {
			return new Dimension();
		}
		int maxWidth = 0;
		int maxHeight = 0;
		for (Component c : parent.getComponents()) {
			maxWidth = Math.max(maxWidth, c.getPreferredSize().width);
			maxHeight = Math.max(maxHeight, c.getPreferredSize().height);
		}
		return new Dimension(maxWidth * ncomponents + (ncomponents - 1) * hgap + insets.left + insets.right, maxHeight + insets.top
				+ insets.bottom);
	}

	@Override
	public Dimension minimumLayoutSize(Container parent) {
		return preferredLayoutSize(parent);
	}

	@Override
	public void layoutContainer(Container parent) {
		Insets insets = parent.getInsets();
		int ncomponents = parent.getComponentCount();
		if (ncomponents == 0) {
			return;
		}
		int maxWidth = 0;
		int maxHeight = 0;
		for (Component c : parent.getComponents()) {
			maxWidth = Math.max(maxWidth, c.getPreferredSize().width);
			maxHeight = Math.max(maxHeight, c.getPreferredSize().height);
		}
		int requiredWidth = maxWidth * ncomponents + (ncomponents - 1) * hgap + insets.left + insets.right;
		boolean direction = leftToRight != null && leftToRight || leftToRight == null && !SystemUtils.IS_OS_MAC;
		int x;
		int availableWidth = parent.getWidth();
		switch (alignment) {
		case SwingConstants.LEFT:
			x = direction ? insets.left : requiredWidth - maxWidth - insets.right;
			break;
		case SwingConstants.RIGHT:
			x = direction ? availableWidth - requiredWidth + insets.left : availableWidth - insets.right - maxWidth;
			break;
		default:
			if (direction) {
				x = (availableWidth - requiredWidth + insets.left + insets.right) / 2;
			} else {
				x = (availableWidth + requiredWidth - insets.left - insets.right) / 2 - maxWidth;
			}
			break;
		}
		int y = insets.top;
		for (Component c : parent.getComponents()) {
			c.setBounds(x, y, maxWidth, maxHeight);
			if (direction) {
				x += hgap + maxWidth;
			} else {
				x -= hgap + maxWidth;
			}
		}
	}

	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				JFrame frame = new JFrame();
				frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				frame.setLayout(new GridBagLayout());
				GridBagConstraints gbc = new GridBagConstraints();
				gbc.gridwidth = GridBagConstraints.REMAINDER;
				gbc.fill = GridBagConstraints.BOTH;
				gbc.weightx = 1.0;
				gbc.weighty = 1.0;
				frame.add(createButtonPanel(new ButtonLayout()), gbc);
				frame.add(createButtonPanel(new ButtonLayout(ButtonLayout.LEFT, 10)), gbc);
				frame.add(createButtonPanel(new ButtonLayout(ButtonLayout.CENTER, 10)), gbc);
				frame.add(createButtonPanel(new ButtonLayout(ButtonLayout.RIGHT, 10)), gbc);
				frame.add(createButtonPanel(new ButtonLayout(ButtonLayout.LEFT, 10, null)), gbc);
				frame.add(createButtonPanel(new ButtonLayout(ButtonLayout.CENTER, 10, null)), gbc);
				frame.add(createButtonPanel(new ButtonLayout(ButtonLayout.RIGHT, 10, null)), gbc);
				frame.add(createButtonPanel(new ButtonLayout(ButtonLayout.LEFT, 10, true)), gbc);
				frame.add(createButtonPanel(new ButtonLayout(ButtonLayout.CENTER, 10, true)), gbc);
				frame.add(createButtonPanel(new ButtonLayout(ButtonLayout.RIGHT, 10, true)), gbc);
				frame.add(createButtonPanel(new ButtonLayout(ButtonLayout.LEFT, 10, false)), gbc);
				frame.add(createButtonPanel(new ButtonLayout(ButtonLayout.CENTER, 10, false)), gbc);
				frame.add(createButtonPanel(new ButtonLayout(ButtonLayout.RIGHT, 10, false)), gbc);
				frame.pack();
				frame.setSize(frame.getWidth() + 100, frame.getHeight() + 100);
				frame.setVisible(true);
			}

			public JPanel createButtonPanel(ButtonLayout layout) {
				JPanel buttonPanel = new JPanel(layout);
				buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
				buttonPanel.add(new JButton("OK"));
				buttonPanel.add(new JButton("Cancel"));
				buttonPanel.add(new JButton("Perform a long name operation"));
				return buttonPanel;
			}
		});
	}
}
