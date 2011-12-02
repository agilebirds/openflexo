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
import java.io.IOException;
import java.util.Enumeration;

import javax.swing.JTextPane;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Element;
import javax.swing.text.MutableAttributeSet;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyledEditorKit;
import javax.swing.text.html.HTML;
import javax.swing.text.html.HTMLDocument;

import com.metaphaseeditor.MetaphaseEditorException;
import com.metaphaseeditor.MetaphaseEditorPanel;

public class RemoveAttributesAction extends StyledEditorKit.StyledTextAction {

	private String[] attributeNames;
	private MetaphaseEditorPanel editorPanel;

	public RemoveAttributesAction(MetaphaseEditorPanel editorPanel, String actionName, String[] attributeNames) {
		super(actionName);
		this.editorPanel = editorPanel;
		this.attributeNames = attributeNames;
	}

	private boolean removeAttribute(Object attributeName) {
		if (attributeNames != null) {
			for (int i = 0; i < attributeNames.length; i++) {
				if (attributeNames[i].equals(attributeName)) {
					return true;
				}
			}
		}
		return false;
	}

	private void removeParagraphAttributes(HTMLDocument htmlDocument, int start, int end) {
		Element element = htmlDocument.getParagraphElement(start);
		AttributeSet attributeSet = element.getAttributes();
		MutableAttributeSet newAttrs = new SimpleAttributeSet();
		Enumeration enumeration = attributeSet.getAttributeNames();
		while (enumeration.hasMoreElements()) {
			Object attributeKey = enumeration.nextElement();
			String attributeName = attributeKey.toString();
			boolean includeAttr = true;
			if (attributeNames != null) {
				for (int i = 0; i < attributeNames.length; i++) {
					if (attributeNames[i].equals(attributeName)) {
						includeAttr = false;
					}
				}
			}
			if (includeAttr) {
				newAttrs.addAttribute(attributeKey, attributeSet.getAttribute(attributeKey));
			}
		}

		htmlDocument.setParagraphAttributes(start, end - start, newAttrs, true);
	}

	private void removeListItemAttributes(HTMLDocument htmlDocument, int start) {
		// remove attributes from a listitem if we're currently within one
		Element charElement = htmlDocument.getCharacterElement(start);
		if (charElement != null) {
			Element impliedParagraph = charElement.getParentElement();
			if (impliedParagraph != null) {
				StringBuffer attrBuffer = new StringBuffer();
				Element listElement = impliedParagraph.getParentElement();
				if (listElement.getName().equals("li")) {
					AttributeSet listElementAttrs = listElement.getAttributes();
					Enumeration currentAttrEnum = listElementAttrs.getAttributeNames();
					while (currentAttrEnum.hasMoreElements()) {
						Object attrName = currentAttrEnum.nextElement();
						Object attrValue = listElement.getAttributes().getAttribute(attrName);
						if ((attrName instanceof String || attrName instanceof HTML.Attribute) && attrValue instanceof String
								&& !removeAttribute(attrName.toString())) {
							attrBuffer.append(' ');
							attrBuffer.append(attrName.toString());
							attrBuffer.append("=\"");
							attrBuffer.append(attrValue);
							attrBuffer.append("\"");
						}
					}
					try {
						String text = htmlDocument.getText(listElement.getStartOffset(),
								listElement.getEndOffset() - listElement.getStartOffset());
						htmlDocument.setOuterHTML(listElement, "<li" + attrBuffer.toString() + ">" + text + "</li>");
					} catch (IOException ex) {
						throw new MetaphaseEditorException(ex.getMessage(), ex);
					} catch (BadLocationException ex) {
						throw new MetaphaseEditorException(ex.getMessage(), ex);
					}
				}
			}
		}
	}

	@Override
	public void actionPerformed(ActionEvent ae) {
		JTextPane textPane = editorPanel.getHtmlTextPane();
		HTMLDocument htmlDocument = (HTMLDocument) textPane.getDocument();

		int start = textPane.getSelectionStart();
		int end = textPane.getSelectionEnd();

		removeParagraphAttributes(htmlDocument, start, end);
		removeListItemAttributes(htmlDocument, start);

		editorPanel.refreshAfterAction();
	}
}
