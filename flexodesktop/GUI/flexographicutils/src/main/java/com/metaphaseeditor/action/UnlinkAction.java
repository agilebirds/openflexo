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
import javax.swing.text.Element;
import javax.swing.text.StyledEditorKit;
import javax.swing.text.html.HTMLDocument;

/**
 *
 * @author Rudolf Visagie
 */
public class UnlinkAction extends StyledEditorKit.StyledTextAction {
    private MetaphaseEditorPanel editorPanel;

    public UnlinkAction(MetaphaseEditorPanel editorPanel, String actionName) {
            super(actionName);
            this.editorPanel = editorPanel;
    }

    public void actionPerformed(ActionEvent ae) {
        JTextPane textPane = editorPanel.getHtmlTextPane();
        HTMLDocument htmlDocument = (HTMLDocument) textPane.getDocument();

        int start = textPane.getSelectionStart();

        Element element = null;
        for (
                element = htmlDocument.getCharacterElement(start);
                element != null && !element.getName().equals("a") && !element.getName().equals("content");
                ) {
            element = element.getParentElement();
        }
        if (element != null) {
            try {
                String text = htmlDocument.getText(element.getStartOffset(), element.getEndOffset() - element.getStartOffset());
                htmlDocument.setOuterHTML(element, text);
                editorPanel.refreshAfterAction();
            } catch (IOException ex) {
                throw new MetaphaseEditorException(ex.getMessage(), ex);
            } catch (BadLocationException ex) {
                throw new MetaphaseEditorException(ex.getMessage(), ex);
            }
        }
    }
}
