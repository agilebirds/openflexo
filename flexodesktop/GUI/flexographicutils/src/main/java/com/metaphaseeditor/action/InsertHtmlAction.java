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

import com.metaphaseeditor.MetaphaseEditorException;
import com.metaphaseeditor.MetaphaseEditorPanel;
import java.awt.event.ActionEvent;
import java.io.IOException;

import javax.swing.JTextPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.StyledEditorKit;
import javax.swing.text.html.HTML.Tag;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;

public class InsertHtmlAction extends StyledEditorKit.StyledTextAction {

	private MetaphaseEditorPanel editorPanel;
	private String html;
	private Tag htmlTag;
	private HTMLEditorKit editorKit = new HTMLEditorKit();

	public InsertHtmlAction(MetaphaseEditorPanel editorPanel, String actionName, String html, Tag htmlTag) {
		super(actionName);
		this.editorPanel = editorPanel;
		this.html = html;
		this.htmlTag = htmlTag;
	}

	public void actionPerformed(ActionEvent ae) {
		try {
			JTextPane textPane = editorPanel.getHtmlTextPane();
			HTMLDocument doc = (HTMLDocument) textPane.getDocument();
			int pos = textPane.getCaretPosition();
			editorKit.insertHTML(doc, pos, html, 0, 0, htmlTag);
			editorPanel.refreshAfterAction();
		} catch (IOException e) {
			throw new MetaphaseEditorException(e.getMessage(), e);
		} catch (BadLocationException e) {
			throw new MetaphaseEditorException(e.getMessage(), e);
		}
	}
}
