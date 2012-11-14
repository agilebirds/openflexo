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

import java.awt.event.ActionEvent;

import javax.swing.JTextPane;
import javax.swing.text.MutableAttributeSet;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.html.CSS;
import javax.swing.text.html.HTMLEditorKit;

import com.metaphaseeditor.MetaphaseEditorPanel;

/**
 * 
 * @author Rudolf Visagie
 */
public class DecreaseIndentAction extends HTMLEditorKit.StyledTextAction {
	private Float currentIndent = null;
	private int newIndent = 0;
	private MetaphaseEditorPanel editorPanel;

	public DecreaseIndentAction(String actionName, MetaphaseEditorPanel editorPanel) {
		super(actionName);
		this.editorPanel = editorPanel;

	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (!this.isEnabled()) {
			return;
		}

		JTextPane htmlTextPane = editorPanel.getHtmlTextPane();
		MutableAttributeSet sas = new SimpleAttributeSet(htmlTextPane.getParagraphAttributes());
		currentIndent = null;
		if (sas.getAttribute(CSS.Attribute.MARGIN_LEFT) != null) {
			currentIndent = new Float(sas.getAttribute(CSS.Attribute.MARGIN_LEFT).toString());
		}

		if (currentIndent != null) {
			newIndent = currentIndent.intValue() - 30;
			currentIndent = new Float(newIndent);
			// enforce min size of 0
			if (newIndent < 0) {
				newIndent = 0;
				currentIndent = new Float(newIndent);
			}
			// if indent size = 0, remove attribute for clearer code
			if (newIndent == 0) {
				sas.removeAttribute(CSS.Attribute.MARGIN_LEFT);
				setParagraphAttributes(htmlTextPane, sas, true);
			} else {
				sas.removeAttribute(CSS.Attribute.MARGIN_LEFT);
				sas.addAttribute(StyleConstants.LeftIndent, new Float(newIndent));
				setParagraphAttributes(htmlTextPane, sas, true);
			}
		}
	}
}
