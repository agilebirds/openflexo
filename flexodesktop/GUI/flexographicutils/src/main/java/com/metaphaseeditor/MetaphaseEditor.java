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

package com.metaphaseeditor;

import java.awt.BorderLayout;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.Map;

import javax.swing.JComponent;
import javax.swing.JPopupMenu;
import javax.swing.text.AttributeSet;

/**
 * 
 * @author Rudolf Visagie
 */
public class MetaphaseEditor extends JComponent {

	public static final String PROP_DOCUMENT = "document";

	private PropertyChangeSupport propertySupport;

	private MetaphaseEditorPanel editorPanel;

	public MetaphaseEditor(MetaphaseEditorConfiguration configuration) {
		editorPanel = new MetaphaseEditorPanel(configuration);
		setLayout(new BorderLayout());
		add(editorPanel, BorderLayout.CENTER);
		propertySupport = new PropertyChangeSupport(this);
	}

	public String getDocument() {
		return editorPanel.getDocument();
	}

	public void setDocument(String value) {
		String oldValue = editorPanel.getDocument();
		editorPanel.setDocument(value);
		propertySupport.firePropertyChange(PROP_DOCUMENT, oldValue, value);
	}

	public JPopupMenu getContextMenu() {
		return editorPanel.getContextMenu();
	}

	public AttributeSet getSelectedParagraphAttributes() {
		return editorPanel.getSelectedParagraphAttributes();
	}

	public void addAttributesToSelectedParagraph(Map<String, String> attributes) {
		editorPanel.addAttributesToSelectedParagraph(attributes);
	}

	public void removeAttributesFromSelectedParagraph(String[] attributeNames) {
		editorPanel.removeAttributesFromSelectedParagraph(attributeNames);
	}

	public void addContextMenuListener(ContextMenuListener contextMenuListener) {
		editorPanel.addContextMenuListener(contextMenuListener);
	}

	public void removeContextMenuListener(ContextMenuListener contextMenuListener) {
		editorPanel.removeContextMenuListener(contextMenuListener);
	}

	public void setEditorToolTipText(String string) {
		editorPanel.setEditorToolTipText(string);
	}

	public void addStyleSheetRule(String rule) {
		editorPanel.addStyleSheetRule(rule);
	}

	public void addEditorMouseMotionListener(EditorMouseMotionListener mouseMotionListener) {
		editorPanel.addEditorMouseMotionListener(mouseMotionListener);
	}

	public void removeEditorMouseMotionListener(EditorMouseMotionListener mouseMotionListener) {
		editorPanel.removeEditorMouseMotionListener(mouseMotionListener);
	}

	@Override
	public void addPropertyChangeListener(PropertyChangeListener listener) {
		propertySupport.addPropertyChangeListener(listener);
	}

	@Override
	public void removePropertyChangeListener(PropertyChangeListener listener) {
		propertySupport.removePropertyChangeListener(listener);
	}

	public void setCustomDictionaryFilename(String customDictionaryFilename) {
		editorPanel.setCustomDictionaryFilename(customDictionaryFilename);
	}

	public String getCustomDictionaryFilename() {
		return editorPanel.getCustomDictionaryFilename();
	}

	public void setDictionaryVersion(SpellCheckDictionaryVersion spellCheckDictionaryVersion) {
		editorPanel.setDictionaryVersion(spellCheckDictionaryVersion);
	}

	public SpellCheckDictionaryVersion getDictionaryVersion() {
		return editorPanel.getDictionaryVersion();
	}
}
