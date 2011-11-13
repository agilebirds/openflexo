/**
 * Metaphase Editor - WYSIWYG HTML Editor Component
 * Copyright (C) 2010  Rudolf Visagie
 * Full text of license can be found in com/metaphaseeditor/LICENSE.txt
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 * The author can be contacted at metaphase.editor@gmail.com.
 */

package com.metaphaseeditor.action;

import com.metaphaseeditor.MetaphaseEditorPanel;
import java.awt.event.ActionEvent;
import java.util.Enumeration;
import java.util.Vector;

import javax.swing.JTextPane;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyledEditorKit;
import javax.swing.text.html.HTML;

public class ClearFormattingAction extends StyledEditorKit.StyledTextAction {

	private MetaphaseEditorPanel editorPanel;

	public ClearFormattingAction(MetaphaseEditorPanel editorPanel, String actionName) {
		super(actionName);
		this.editorPanel = editorPanel;
	}

	public void actionPerformed(ActionEvent ae) {
		JTextPane textPane = editorPanel.getHtmlTextPane();
		int internalTextLength;
		Vector skipAttributesList = new Vector();
		skipAttributesList.add(HTML.Attribute.SRC.toString());
		skipAttributesList.add(HTML.Attribute.BORDER.toString());
		skipAttributesList.add(HTML.Attribute.WIDTH.toString());
		skipAttributesList.add(HTML.Attribute.HEIGHT.toString());
		skipAttributesList.add(HTML.Attribute.ALT.toString());

		String selText = textPane.getSelectedText();
		int caretOffset = textPane.getSelectionStart();

		// clear all paragraph attributes in selection
		SimpleAttributeSet sasText = new SimpleAttributeSet(textPane.getParagraphAttributes());
		for (Enumeration en = sasText.getAttributeNames(); en.hasMoreElements();) {
			Object elm = en.nextElement();
			sasText.removeAttribute(sasText.getAttribute(elm));
		}
		textPane.setParagraphAttributes(sasText, true);

		// clear all character attributes in selection
		sasText = null;
		if (selText != null) {
			internalTextLength = selText.length();
		} else {
			internalTextLength = 0;
		}

		mainLoop: for (int i = caretOffset; i <= caretOffset + internalTextLength; i++) {
			textPane.setCaretPosition(i);
			sasText = new SimpleAttributeSet(textPane.getCharacterAttributes().copyAttributes());
			Enumeration attributeNames = sasText.getAttributeNames();
			while (attributeNames.hasMoreElements()) {
				Object entryKey = attributeNames.nextElement();
				for (int j = 0; j < skipAttributesList.size(); j++) {
					if (entryKey.toString().equals(skipAttributesList.get(j)))
						continue mainLoop;
				}
				if (!entryKey.toString().equals(HTML.Attribute.NAME.toString())) {
					sasText.removeAttribute(entryKey);
				}
			}
			textPane.select(i, i + 1);
			textPane.setCharacterAttributes(sasText, true);
		}
		editorPanel.refreshAfterAction();
	}
}
