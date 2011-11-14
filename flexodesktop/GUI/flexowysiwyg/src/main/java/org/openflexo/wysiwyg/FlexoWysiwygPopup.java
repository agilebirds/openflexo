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

import java.awt.Dimension;
import java.awt.HeadlessException;
import java.awt.Toolkit;
import java.io.File;

import javax.swing.JFrame;
import javax.swing.WindowConstants;

import org.openflexo.toolbox.FileResource;

public class FlexoWysiwygPopup extends JFrame {

	protected FlexoWysiwyg wysiwyg;

	protected EditableHtmlWidget model;

	/**
	 * Creates a JFrame for the Wysiwyg component, with the menu bar. This version of the wysiwyg does not allow the user to import images
	 * or to create HTML forms.
	 * 
	 * @param targetWidget
	 *            the IE widget responsible for getting and setting the HTML content of the editor (cannot be null).
	 * @param cssFile
	 *            the CSS file to apply on the document.
	 * @see FlexoWysiwyg
	 */
	public FlexoWysiwygPopup(EditableHtmlWidget model, FileResource cssFile) throws HeadlessException {

		super();
		this.model = model;
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		if (model == null) {
			throw new NullPointerException("targetWidget cannot be null");
		}

		this.wysiwyg = new FlexoWysiwyg(model.getValue(), cssFile, true) {
			@Override
			public void notifyTextChanged() {
				updateModelFromWidget();
			}
		};
		// remove support for images
		wysiwyg.setRemovedToolbarItems("insertImageButton");
		wysiwyg.setRemovedMenuItems("insertInsertImageMenuItem");
		wysiwyg.setPreferredSize(new Dimension(830, 500));

		setContentPane(wysiwyg);
		// setAlwaysOnTop(true);
		pack();
		// center frame
		Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
		setLocation((int) (d.getWidth() - getWidth()) / 2, (int) (d.getHeight() - getHeight()) / 2);
		setVisible(true);
		toFront();
	}

	@Override
	public void dispose() {
		updateModelFromWidget();
		super.dispose();
	}

	public FlexoWysiwyg getWysiwyg() {
		return wysiwyg;
	}

	/**
	 * @param targetWidget
	 */
	protected void updateModelFromWidget() {
		model.setValue(wysiwyg.getBodyContent());
	}

	public static void main(String[] args) throws Exception {

		File documentBaseFolder = new File("/Users/ajasselette/Desktop/WysiwygTest/");
		if (!documentBaseFolder.exists()) {
			documentBaseFolder.mkdir();
		}

		EditableHtmlWidget targetWidget = new EditableHtmlWidget() {
			@Override
			public String getValue() {
				return "<html><body>Test</body></html>";
			}

			@Override
			public void setValue(String value) {
				System.out.println("CHANGE:\n" + value);
			}
		};
		FlexoWysiwygPopup popup = new FlexoWysiwygPopup(targetWidget, new FileResource(""));
		popup.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
	}
}
