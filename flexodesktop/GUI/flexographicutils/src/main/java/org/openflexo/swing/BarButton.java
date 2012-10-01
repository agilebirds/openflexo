package org.openflexo.swing;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JButton;

import org.openflexo.toolbox.ToolBox;

public class BarButton extends JButton {
	public BarButton(Action a) {
		this();
		setAction(a);
	}

	public BarButton(Icon icon) {
		this(null, icon);
	}

	public BarButton(String text, Icon icon) {
		super(text, icon);
		setEnabled(true);
		setFocusable(false);
		setBorderPainted(ToolBox.getPLATFORM() != ToolBox.MACOS);
		setRolloverEnabled(true);
		setContentAreaFilled(false);
		setOpaque(false);
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
	}

	public BarButton(String text) {
		this(text, null);
	}

	public BarButton() {
		this(null, null);
	}

	@Override
	public void setContentAreaFilled(boolean b) {
		b |= isSelected();
		super.setContentAreaFilled(b);
		setOpaque(b);
	}

	@Override
	public void setSelected(boolean b) {
		super.setSelected(b);
		setContentAreaFilled(b);
	}

}