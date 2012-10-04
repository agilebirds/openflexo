package org.openflexo.swing;

import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JButton;

public class ImageButton extends JButton {

	public ImageButton() {
		this(null, null);
	}

	public ImageButton(Action a) {
		this();
		setAction(a);
	}

	public ImageButton(Icon icon) {
		super(null, icon);
		updateUI();
	}

	public ImageButton(String text, Icon icon) {
		super(text, icon);
	}

	public ImageButton(String text) {
		this(text, null);
	}

	@Override
	public void updateUI() {
		super.updateUI();
		setBorder(BorderFactory.createEmptyBorder());
	}

}
