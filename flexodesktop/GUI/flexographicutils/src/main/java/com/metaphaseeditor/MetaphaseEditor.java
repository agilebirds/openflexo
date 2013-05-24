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
import java.awt.Dimension;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Map;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import javax.swing.text.AttributeSet;

import com.metaphaseeditor.MetaphaseEditorConfiguration.MetaphaseEditorOption;

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

	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				JFrame frame = new JFrame(MetaphaseEditor.class.getSimpleName());
				frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				MetaphaseEditorConfiguration configuration = new MetaphaseEditorConfiguration();
				Class<?> klass = MetaphaseEditorPanel.class;
				int i = 0;
				configuration.addToOptions(new MetaphaseEditorOption(MetaphaseEditorPanel.BACKGROUND_COLOR_BUTTON_KEY, i++, 1));
				for (Field field : klass.getDeclaredFields()) {
					if (Modifier.isFinal(field.getModifiers()) && Modifier.isStatic(field.getModifiers())
							&& field.getType() == String.class) {
						try {
							configuration.addToOptions(new MetaphaseEditorOption((String) field.get(null), i++, 1));
						} catch (IllegalArgumentException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (IllegalAccessException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}
				MetaphaseEditor editorPane = new MetaphaseEditor(configuration);
				editorPane.setPreferredSize(new Dimension(600, 400));
				editorPane.setDocument(SAMPLE);
				frame.add(new JScrollPane(editorPane));
				frame.pack();
				frame.setVisible(true);
			}
		});
	}

	private static final String SAMPLE = "<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\">\n" + "<html>\n" + "  <head>\n"
			+ "    <title>I've got a bike</title>\n" + "  </head>\n" + "\n" + "  <body>\n" + "    <h1>Bike</h1>\n" + "    <p>\n"
			+ "      I've got a <b>bike</b>. You can ride it if you like. It's got a <u>basket</u>, a bell that rings and\n"
			+ "      Things to make it look good. I'd give it to you if I could, but I borrowed it.\n" + "    <p align=\"center\">\n"
			+ "      <i>You're the kind of girl that fits in with my world.<br>\n"
			+ "      I'll give you anything, ev'rything if you want things.</i>\n" + "    <p>\n"
			+ "      I've got a <b>cloak</b>. It's a bit of a <u>joke</u>.\n" + "      There's a tear up the front. It's red and black.\n"
			+ "      I've had it for months.<br>\n" + "      If you think it could look good, then I guess it should.\n" + "    <ul>\n"
			+ "      <li>You're the kind of girl\n" + "      <li>that fits in with my world\n" + "      <li>I'll give you anything\n"
			+ "      <li>ev'rything if you want things.\n" + "    </ul>\n" + "    <hr>\n" + "    <p>\n"
			+ "      I know a <b>mouse</b>, and he hasn't got a house.\n" + "      I don't know why. I call him <u>Gerald</u>.\n"
			+ "      He's getting rather old, but he's a good mouse.\n" + "    <ol>\n" + "      <li>You're the kind of girl\n"
			+ "      <li>that fits in with my world\n" + "      <li>I'll give you anything\n"
			+ "      <li>ev'rything if you want things.\n" + "    </ol>\n" + "    <pre>\n" + "      I've got a clan of gingerbread men.\n"
			+ "      Here a man, there a man, lots of gingerbread men.\n" + "      Take a couple if you wish. They're on the dish.\n"
			+ "    </pre>\n" + "    <p align=\"center\">\n" + "      <i>You're the kind of girl that fits in with my world.<br>\n"
			+ "      I'll give you anything, ev'rything if you want things.</i>\n" + "    <p>\n"
			+ "      I know a room of musical tunes. Some rhyme, some ching. Most of them are clockwork.\n"
			+ "      Let's go into the other room and make them work.\n" + "    <p>\n"
			+ "    <p align=\"right\"><font size=\"+3\"><i>Pink Floyd</i></font></p>\n" + "  </body>\n" + "</html>\n" + "";

}
