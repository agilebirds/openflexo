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
import java.awt.Container;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GraphicsEnvironment;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JColorChooser;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.AttributeSet;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;

/**
 * FontChooserBean allows the user to select a font in a similar way to a FileSelectionDialog.
 * 
 * @see gnu.lgpl.License for license details. The copyright to this program is held by it's authors.
 * @author Joe Walker [joe at eireneh dot com]
 */
public class FontChooser extends JDialog implements ActionListener {
	public static final Font NORMAL_FONT = new Font("SansSerif", Font.PLAIN, 11);

	public static void main(String[] args) {
		FontChooser fc = new FontChooser((Frame) null, NORMAL_FONT);
		fc.setVisible(true);
		Font f = fc.getNewFont();
		System.out.println(f);
	}

	public static Font showDialog(Window parent, Font currentFont) {
		if (parent != null && !(parent instanceof Frame) && !(parent instanceof Dialog))
			parent = null;
		FontChooser fc;
		if (parent instanceof Frame)
			fc = new FontChooser((Frame) parent, currentFont);
		else if (parent instanceof Dialog)
			fc = new FontChooser((Dialog) parent, currentFont);
		else
			fc = new FontChooser((Frame) null, currentFont);
		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		fc.setLocation((dim.width - fc.getSize().width) / 2, (dim.height - fc.getSize().height) / 2);
		fc.setVisible(true);
		Font f = fc.getNewFont();
		if (f == null)
			return null;
		return f;
	}

	// FontChooser.java
	// A font chooser that allows users to pick a font by name, size, style, and
	// color. The color selection is provided by a JColorChooser pane. This
	// dialog builds an AttributeSet suitable for use with JTextPane.
	//

	JColorChooser colorChooser;

	JComboBox fontName;

	JCheckBox fontBold, fontItalic;

	JTextField fontSize;

	JLabel previewLabel;

	SimpleAttributeSet attributes;

	Font newFont;

	Color newColor;

	private static final String[] names = GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames();

	public FontChooser(Frame parent, Font initialFont) {
		super(parent, "Font Chooser", true);
		init(initialFont);
	}

	public FontChooser(Dialog parent, Font initialFont) {
		super(parent, "Font Chooser", true);
		init(initialFont);
	}

	private void init(Font initialFont) {
		setSize(350, 175);
		attributes = new SimpleAttributeSet();
		if (initialFont != null) {
			StyleConstants.setFontFamily(attributes, initialFont.getFamily());
			StyleConstants.setBold(attributes, initialFont.isBold());
			StyleConstants.setItalic(attributes, initialFont.isItalic());
			StyleConstants.setFontSize(attributes, initialFont.getSize());
			// StyleConstants.setForeground(a, fg)(attributes, initialFont.getSize());
		}
		// Make sure that any way the user cancels the window does the right
		// thing
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				closeAndCancel();
			}
		});

		// Start the long process of setting up our interface
		Container c = getContentPane();
		c.setLayout(new VerticalLayout());
		JPanel fontPanel = new JPanel();
		fontName = new JComboBox(names);
		if (initialFont != null) {
			fontName.setSelectedItem(initialFont.getFamily());
		} else {
			fontName.setSelectedIndex(1);
		}
		fontName.addActionListener(this);
		if (initialFont != null) {
			fontSize = new JTextField("" + initialFont.getSize(), 4);
		} else {
			fontSize = new JTextField("12", 4);
		}

		fontSize.setHorizontalAlignment(SwingConstants.RIGHT);
		fontSize.getDocument().addDocumentListener(new DocumentListener() {

			@Override
			public void changedUpdate(DocumentEvent e) {
				actionPerformed(null);
			}

			@Override
			public void insertUpdate(DocumentEvent e) {
				actionPerformed(null);
			}

			@Override
			public void removeUpdate(DocumentEvent e) {
				actionPerformed(null);
			}

		});

		fontBold = new JCheckBox("Bold");
		if (initialFont != null) {
			fontBold.setSelected(initialFont.isBold());
		} else {
			fontBold.setSelected(false);
		}

		fontBold.addActionListener(this);

		fontItalic = new JCheckBox("Italic");
		fontItalic.addActionListener(this);
		if (initialFont != null) {
			fontItalic.setSelected(initialFont.isItalic());
		} else {
			fontItalic.setSelected(false);
		}
		fontPanel.add(new JLabel(" Font: "));
		fontPanel.add(fontName);

		JPanel fontPanelParameters = new JPanel();

		fontPanelParameters.add(new JLabel(" Size: "));
		fontPanelParameters.add(fontSize);
		fontPanelParameters.add(fontBold);
		fontPanelParameters.add(fontItalic);

		c.add(fontPanel/*, BorderLayout.NORTH*/);
		c.add(fontPanelParameters);

		JPanel previewPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
		previewLabel = new JLabel("Here's a sample of this font.");
		updatePreviewFont();
		// previewLabel.setForeground(colorChooser.getColor());
		previewPanel.add(previewLabel/*, BorderLayout.CENTER*/);
		// Add in the Ok and Cancel buttons for our dialog box
		c.add(previewPanel);
		JButton okButton = new JButton("Ok");
		okButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent ae) {
				closeAndSave();
			}
		});
		JButton cancelButton = new JButton("Cancel");
		cancelButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent ae) {
				closeAndCancel();
			}
		});

		JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
		controlPanel.add(okButton);
		controlPanel.add(cancelButton);
		c.add(controlPanel, BorderLayout.SOUTH);

		// Give the preview label room to grow.
		previewPanel.setMinimumSize(new Dimension(100, 100));
		previewPanel.setPreferredSize(new Dimension(100, 100));

		c.add(previewPanel/*, BorderLayout.SOUTH*/);
	}

	// Ok, something in the font changed, so figure that out and make a
	// new font for the preview label
	@Override
	public void actionPerformed(ActionEvent ae) {
		// Check the name of the font
		if (!StyleConstants.getFontFamily(attributes).equals(fontName.getSelectedItem())) {
			StyleConstants.setFontFamily(attributes, (String) fontName.getSelectedItem());
		}
		// Check the font size (no error checking yet)
		try {
			if (StyleConstants.getFontSize(attributes) != Integer.parseInt(fontSize.getText())) {
				StyleConstants.setFontSize(attributes, Integer.parseInt(fontSize.getText()));
			}
		} catch (NumberFormatException e) {
		}
		// Check to see if the font should be bold
		if (StyleConstants.isBold(attributes) != fontBold.isSelected()) {
			StyleConstants.setBold(attributes, fontBold.isSelected());
		}
		// Check to see if the font should be italic
		if (StyleConstants.isItalic(attributes) != fontItalic.isSelected()) {
			StyleConstants.setItalic(attributes, fontItalic.isSelected());
		}
		// and update our preview label
		updatePreviewFont();
	}

	// Get the appropriate font from our attributes object and update
	// the preview label
	protected void updatePreviewFont() {
		String name = StyleConstants.getFontFamily(attributes);
		boolean bold = StyleConstants.isBold(attributes);
		boolean ital = StyleConstants.isItalic(attributes);
		int size = StyleConstants.getFontSize(attributes);

		// Bold and italic don't work properly in beta 4.
		Font f = new Font(name, (bold ? Font.BOLD : 0) + (ital ? Font.ITALIC : 0), size);
		previewLabel.setFont(f);
	}

	// Get the appropriate color from our chooser and update previewLabel
	protected void updatePreviewColor() {
		previewLabel.setForeground(colorChooser.getColor());
		// Manually force the label to repaint
		previewLabel.repaint();
	}

	public Font getNewFont() {
		return newFont;
	}

	public Color getNewColor() {
		return newColor;
	}

	public AttributeSet getAttributes() {
		return attributes;
	}

	public void closeAndSave() {
		// Save font & color information
		newFont = previewLabel.getFont();
		newColor = previewLabel.getForeground();

		// Close the window
		setVisible(false);
	}

	public void closeAndCancel() {
		// Erase any font information and then close the window
		newFont = null;
		newColor = null;
		setVisible(false);
	}

}
