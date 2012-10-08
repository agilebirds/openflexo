package org.openflexo.swing;

import java.awt.Color;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import org.openflexo.icon.UtilsIconLibrary;
import org.openflexo.toolbox.ToolBox;

public class BarButton extends JButton {

	private boolean internallySelected = false;
	private Color defaultBackgroundColor;

	public BarButton(Action a) {
		this();
		setAction(a);
	}

	public BarButton(Icon icon) {
		this(null, icon);
	}

	public BarButton(String text, Icon icon) {
		super(text, icon);
		addMouseListener(new MouseAdapter() {
			@Override
			public void mouseEntered(MouseEvent e) {
				if (isEnabled()) {
					setContentAreaFilled(true);
				}
			}

			@Override
			public void mouseExited(MouseEvent e) {
				setContentAreaFilled(false);
			}
		});
		updateInternalUI();
	}

	@Override
	public void updateUI() {
		super.updateUI();
		updateInternalUI();
	}

	private void updateInternalUI() {
		setEnabled(true);
		setFocusable(false);
		setBorderPainted(false);
		setRolloverEnabled(true);
		setContentAreaFilled(false);
		setOpaque(false);
		defaultBackgroundColor = getBackground();
	}

	public BarButton(String text) {
		this(text, null);
	}

	public BarButton() {
		this(null, null);
	}

	@Override
	public void setContentAreaFilled(boolean b) {
		b |= internallySelected;
		super.setContentAreaFilled(b);
		setOpaque(b);
	}

	@Override
	public void setSelected(boolean b) {
		internallySelected = b;
		if (ToolBox.isMacOSLaf()) {
			if (b) {
				setBackground(defaultBackgroundColor.darker());
			} else {
				setBackground(defaultBackgroundColor);
			}
		} else {
			super.setSelected(b);
		}
		setContentAreaFilled(b);
	}

	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run() {
				JFrame frame = new JFrame();
				JMenuBar panel = new JMenuBar();
				panel.setBackground(UIManager.getDefaults().getColor("ToolBar.floatingForeground"));
				frame.add(panel);
				for (int i = 0; i < 10; i++) {
					BarButton bar = new BarButton(UtilsIconLibrary.UK_FLAG);
					bar.setSelected(i == 1);
					panel.add(bar);
				}
				frame.pack();
				frame.setVisible(true);
			}
		});
	}

}