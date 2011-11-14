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

package org.openflexo.swing;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GraphicsEnvironment;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.openflexo.localization.FlexoLocalization;
import org.openflexo.swing.FontSelector.FontSelectionModel;

public class JFontChooser extends JPanel {

	private JPanel mainPanel;
	private JPanel previewPanel;
	private JLabel previewLabel;
	JList fontNameList;
	JList fontStyleList;
	JList fontSizeList;

	private static String PLAIN = "plain";
	private static String BOLD = "bold";
	private static String ITALIC = "italic";
	private static String BOLD_ITALIC = "bold_italic";

	private static String[] sizes = new String[] { "2", "4", "6", "8", "9", "10", "11", "12", "13", "14", "16", "18", "20", "22", "24",
			"30", "36", "48", "72" };

	private static String[] styles = new String[] { PLAIN, BOLD, ITALIC, BOLD_ITALIC };

	private FontSelectionModel _fsm;

	public JFontChooser(FontSelectionModel fsm) {
		super(new BorderLayout());

		_fsm = fsm;

		fontNameList = new JList(GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames()) {
			@Override
			public Dimension getPreferredScrollableViewportSize() {
				return new Dimension(175, 144);
			}
		};
		fontNameList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		fontNameList.addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e) {
				fontNameList.removeListSelectionListener(this);
				fontNameChanged((String) fontNameList.getSelectedValue());
				fontNameList.addListSelectionListener(this);
			}
		});

		fontStyleList = new JList(styles) {
			@Override
			public Dimension getPreferredScrollableViewportSize() {
				return new Dimension(100, 144);
			}
		};
		fontStyleList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		fontStyleList.setCellRenderer(new DefaultListCellRenderer() {

			@Override
			public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
				JLabel returned = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
				returned.setText(FlexoLocalization.localizedForKey(returned.getText()));
				return returned;
			}
		});
		fontStyleList.addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e) {
				fontStyleList.removeListSelectionListener(this);
				fontStyleChanged((String) fontStyleList.getSelectedValue());
				fontStyleList.addListSelectionListener(this);
			}

		});

		fontSizeList = new JList(sizes) {
			@Override
			public Dimension getPreferredScrollableViewportSize() {
				return new Dimension(25, 144);
			}
		};
		fontSizeList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		fontSizeList.addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e) {
				fontSizeList.removeListSelectionListener(this);
				fontSizeChanged(Integer.parseInt((String) fontSizeList.getSelectedValue()));
				fontSizeList.addListSelectionListener(this);
			}

		});

		mainPanel = new JPanel(new FlowLayout());
		mainPanel.add(new JScrollPane(fontNameList));
		mainPanel.add(new JScrollPane(fontStyleList));
		mainPanel.add(new JScrollPane(fontSizeList));

		add(mainPanel, BorderLayout.CENTER);

		previewPanel = new JPanel(new BorderLayout());
		previewPanel.setPreferredSize(new Dimension(300, 40));
		previewPanel.setBackground(Color.WHITE);

		previewLabel = new JLabel(fontDescription(fsm.getSelectedFont()), SwingConstants.CENTER);
		previewPanel.add(previewLabel, BorderLayout.CENTER);

		add(previewPanel, BorderLayout.NORTH);

		updateWithFont(fsm.getSelectedFont());
	}

	public static String fontDescription(Font aFont) {
		if (aFont == null) {
			return "null";
		}
		return aFont.getFontName() + ", " + localizedFontStyle(aFont) + ", " + aFont.getSize() + "pt";
	}

	public static String localizedFontStyle(Font aFont) {
		if (aFont == null) {
			return "null";
		}
		return FlexoLocalization.localizedForKey(fontStyle(aFont));
	}

	private static String fontStyle(Font aFont) {
		if (aFont == null) {
			return "null";
		}
		if (aFont.isBold() && aFont.isItalic()) {
			return BOLD_ITALIC;
		}
		if (aFont.isBold()) {
			return BOLD;
		}
		if (aFont.isItalic()) {
			return ITALIC;
		}
		return PLAIN;
	}

	private static int fontStyle(String style) {
		if (PLAIN.equals(style)) {
			return Font.PLAIN;
		}
		if (ITALIC.equals(style)) {
			return Font.ITALIC;
		}
		if (BOLD.equals(style)) {
			return Font.BOLD;
		}
		if (BOLD_ITALIC.equals(style)) {
			return Font.ITALIC | Font.BOLD;
		}
		return Font.PLAIN;
	}

	private void updateWithFont(Font aFont) {
		fontNameList.setSelectedValue(aFont.getFontName(), true);
		fontStyleList.setSelectedValue(fontStyle(aFont), true);
		fontSizeList.setSelectedValue("" + aFont.getSize(), true);
		previewLabel.setFont(aFont);
		previewLabel.setText(fontDescription(aFont));
	}

	void fontNameChanged(String fontName) {
		Font newFont = new Font(fontName, _fsm.getSelectedFont().getStyle(), _fsm.getSelectedFont().getSize());
		_fsm.setSelectedFont(newFont);
		updateWithFont(newFont);
	}

	void fontStyleChanged(String fontStyle) {
		Font newFont = new Font(_fsm.getSelectedFont().getFontName(), fontStyle(fontStyle), _fsm.getSelectedFont().getSize());
		_fsm.setSelectedFont(newFont);
		updateWithFont(newFont);
	}

	void fontSizeChanged(int fontSize) {
		Font newFont = new Font(_fsm.getSelectedFont().getFontName(), _fsm.getSelectedFont().getStyle(), fontSize);
		_fsm.setSelectedFont(newFont);
		updateWithFont(newFont);
	}

}
