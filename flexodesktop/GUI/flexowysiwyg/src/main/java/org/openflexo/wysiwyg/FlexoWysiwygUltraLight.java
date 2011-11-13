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
package org.openflexo.wysiwyg;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.JToolBar;

import sferyx.administration.editors.CustomEditorPane;

public abstract class FlexoWysiwygUltraLight extends FlexoWysiwygLight {

	/**
	 * Creates the ultra light" version of wysiwyg component with only one toolbar and a set of the 10 most useful options. This class must
	 * implement <code>textChanged(String htmlText)</code> to be concrete.
	 * 
	 * @param isUltraLight
	 *            if true, will create ultra light version of the wysiwyg.
	 */
	public FlexoWysiwygUltraLight(boolean isViewSourceAvailable) {
		this(null, isViewSourceAvailable);
	}

	/**
	 * Creates the ultra light" version of wysiwyg component with only one toolbar and a set of the 10 most useful options. This class must
	 * implement <code>textChanged(String htmlText)</code> to be concrete.
	 * 
	 * @param isUltraLight
	 *            if true, will create ultra light version of the wysiwyg.
	 * @param htmlContent
	 *            if not null, will initialize the wysiwyg with this HTML content.
	 */
	public FlexoWysiwygUltraLight(String htmlContent, boolean isViewSourceAvailable) {

		super(htmlContent, isViewSourceAvailable);
		setShortcutToolbarVisible(false); // hide the editing toolbar
		setRemovedToolbarItems("headingStyles, fontsList, fontSizes, styleClasses, superscriptButton, subscriptButton, alignLeftButton, alignCenterButton, alignRightButton, alignJustifyButton, increaseIndentButton, decreaseIndentButton, setForegroundButton");
		// we need to get some buttons that were on the editing toolbar to put them on the formatting toolbar
		JToolBar editingToolbar = getEditingToolBar();
		JToolBar formattingToolbar = getFormattingToolBar();
		editingToolbar.add(new JToolBar.Separator());
		JButton currentIcon = null;
		for (Component c : editingToolbar.getComponents()) {
			if (c instanceof JButton) {
				currentIcon = (JButton) c;
				// very bourin but the tooltip text is the only identifier I can use (no name nor actionName on components)
				if (currentIcon.getToolTipText().equals("undo") || currentIcon.getToolTipText().equals("redo")
						|| currentIcon.getToolTipText().equals("insert hyperlink")) {
					formattingToolbar.add(currentIcon);
				}
			}
		}
		setSourceEditorVisible(false);
		if (getIsViewSourceAvailable()) {
			JButton switchViewButton = createMenuButton(getFormattingToolBar(), "switch view", "switchView",
					getSharedIcon("page-properties"));
			switchViewButton.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					switchView();
				}
			});
		}
	}

	public void switchView() {

		if (isSourceEditorVisible()) {// switch to classic view
			for (Component c : getMainTabbedPane().getComponents()) { // ...get the editor tab content by browsing the content of the
																		// mainTabbedPane
				if (c instanceof JScrollPane && ((JScrollPane) c).getViewport().getView() instanceof CustomEditorPane) {
					getMainTabbedPane().setSelectedIndex(getMainTabbedPane().indexOfComponent(c)); // diplay it
				}
			}
			setSourceEditorVisible(false);
		} else { // switch to html source view,
			setSourceEditorVisible(true);
			for (Component c : getMainTabbedPane().getComponents()) { // ...get the source html tab content by browsing the content of the
																		// mainTabbedPane
				if (c instanceof JScrollPane && ((JScrollPane) c).getViewport().getView() instanceof JTextPane) {
					getMainTabbedPane().setSelectedIndex(getMainTabbedPane().indexOfComponent(c)); // diplay it
				}
			}
		}
		revalidate();
		repaint();
	}
}
