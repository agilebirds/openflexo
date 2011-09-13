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
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Map;

import javax.swing.JTextPane;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Element;
import javax.swing.text.MutableAttributeSet;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyledEditorKit;
import javax.swing.text.html.HTML;
import javax.swing.text.html.HTMLDocument;

public class AddAttributesAction extends StyledEditorKit.StyledTextAction {

    private Map<String, String> attributes;
    private MetaphaseEditorPanel editorPanel;

    public AddAttributesAction(MetaphaseEditorPanel editorPanel, String actionName, Map<String, String> attributes) {
            super(actionName);
            this.editorPanel = editorPanel;
            this.attributes = attributes;
    }

    private AttributeSet getNewAttributes(AttributeSet attributeSet) {
        MutableAttributeSet newAttrs = new SimpleAttributeSet(attributeSet);
        if (attributes != null) {
            Iterator iterator = attributes.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry entry = (Map.Entry)iterator.next();
                newAttrs.addAttribute(entry.getKey(), entry.getValue());
            }
        }
        return newAttrs;
    }

    public void addParagraphAttributes(HTMLDocument htmlDocument, int start, int end) {
        // add attributes to paragraph
        Element element = htmlDocument.getParagraphElement(start);
        AttributeSet newAttrs = getNewAttributes(element.getAttributes());
        htmlDocument.setParagraphAttributes(start, end - start, newAttrs, true);
    }

    public void addListItemAttributes(HTMLDocument htmlDocument, int start, int end) {
        // add attributes to a listitem if we're currently within one
        Element charElement = htmlDocument.getCharacterElement(start);
        if (charElement != null) {
            Element impliedParagraph = charElement.getParentElement();
            if (impliedParagraph != null) {
                Element listElement = impliedParagraph.getParentElement();
                if (listElement.getName().equals("li")) {
                    StringBuffer attrBuffer = new StringBuffer();
                    if (attributes != null) {                        
                        Iterator iterator = attributes.entrySet().iterator();
                        while (iterator.hasNext()) {
                            Map.Entry entry = (Map.Entry)iterator.next();
                            attrBuffer.append(' ');
                            attrBuffer.append(entry.getKey());
                            attrBuffer.append("=\"");
                            attrBuffer.append(entry.getValue());
                            attrBuffer.append("\"");
                        }                        
                    }
                    // re-add the existing attributes to the list item
                    AttributeSet listElementAttrs = listElement.getAttributes();
                    Enumeration currentAttrEnum = listElementAttrs.getAttributeNames();
                    while (currentAttrEnum.hasMoreElements()) {
                        Object attrName = currentAttrEnum.nextElement();
                        if (!attributes.containsKey(attrName.toString())) {
                            Object attrValue = listElement.getAttributes().getAttribute(attrName);
                            if ((attrName instanceof String || attrName instanceof HTML.Attribute) && attrValue instanceof String) {
                                attrBuffer.append(' ');
                                attrBuffer.append(attrName.toString());
                                attrBuffer.append("=\"");
                                attrBuffer.append(attrValue);
                                attrBuffer.append("\"");
                            }
                        }
                    }
                    try {
                        String text = htmlDocument.getText(listElement.getStartOffset(), listElement.getEndOffset() - listElement.getStartOffset());
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

    public void actionPerformed(ActionEvent ae) {
        JTextPane textPane = editorPanel.getHtmlTextPane();
        HTMLDocument htmlDocument = (HTMLDocument) textPane.getDocument();

        int start = textPane.getSelectionStart();
        int end = textPane.getSelectionEnd();
        
        addParagraphAttributes(htmlDocument, start, end);
        addListItemAttributes(htmlDocument, start, end);
        
        editorPanel.refreshAfterAction();
    }
}
