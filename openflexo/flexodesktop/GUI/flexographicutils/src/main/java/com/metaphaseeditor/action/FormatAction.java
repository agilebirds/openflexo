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
import java.util.Iterator;
import java.util.Map;

import javax.swing.JTextPane;
import javax.swing.text.Element;
import javax.swing.text.MutableAttributeSet;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledEditorKit;
import javax.swing.text.html.HTML;
import javax.swing.text.html.HTMLDocument;

public class FormatAction extends StyledEditorKit.StyledTextAction {

    private HTML.Tag htmlTag;
    private Map<String, String> attributes;
    private MetaphaseEditorPanel editorPanel;

    public FormatAction(MetaphaseEditorPanel editorPanel, String actionName, HTML.Tag htmlTag) {
            super(actionName);
            this.editorPanel = editorPanel;
            this.htmlTag = htmlTag;
            attributes = null;
    }

    public FormatAction(MetaphaseEditorPanel editorPanel, String actionName, HTML.Tag htmlTag, Map<String, String> attributes) {
            super(actionName);
            this.editorPanel = editorPanel;
            this.htmlTag = htmlTag;
            this.attributes = attributes;
    }

    public FormatAction(MetaphaseEditorPanel editorPanel, String actionName, Map<String, String> attributes) {
            super(actionName);
            this.editorPanel = editorPanel;
            htmlTag = null;
            this.attributes = attributes;
    }

    public void actionPerformed(ActionEvent ae) {
        JTextPane textPane = editorPanel.getHtmlTextPane();
        HTMLDocument htmlDocument = (HTMLDocument) textPane.getDocument();

        int start = textPane.getSelectionStart();
        int end = textPane.getSelectionEnd();

        Element element = htmlDocument.getParagraphElement(start);
        MutableAttributeSet newAttrs = new SimpleAttributeSet(element.getAttributes());
        if (htmlTag != null) {
            newAttrs.addAttribute(StyleConstants.NameAttribute, htmlTag);
        }
        if (attributes != null) {
            Iterator iterator = attributes.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry entry = (Map.Entry)iterator.next();
                newAttrs.addAttribute(entry.getKey(), entry.getValue());
            }
        }

        htmlDocument.setParagraphAttributes(start, end - start, newAttrs, true);
        editorPanel.refreshAfterAction();
    }
}
