package org.openflexo.swing;

import java.awt.Font;

import javax.swing.event.ChangeListener;

public interface FontSelectionModel {
	Font getSelectedFont();

	void setSelectedFont(Font font);

	void addChangeListener(ChangeListener listener);

	void removeChangeListener(ChangeListener listener);
}