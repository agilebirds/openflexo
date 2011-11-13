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
package org.openflexo.sgmodule.view;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.io.File;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;

import org.openflexo.localization.FlexoLocalization;

public class ImageDiffPanel extends JLayeredPane {

	private JPanel leftPanel;
	private JPanel rightPanel;
	private JPanel _separator;
	private JScrollPane scrollPane;

	public ImageDiffPanel(File leftImage, File rightImage, String leftLabel, String rightLabel) {
		setLayout(new BorderLayout());
		JPanel topPanel = new JPanel(new BorderLayout());

		if ((leftLabel != null) || (rightLabel != null)) {

			JPanel labelPanels = new JPanel(new GridLayout(1, 2));
			JLabel _leftLabel = new JLabel(leftLabel, SwingConstants.CENTER);
			JLabel _rightLabel = new JLabel(rightLabel, SwingConstants.CENTER);
			labelPanels.add(_leftLabel);
			labelPanels.add(_rightLabel);
			labelPanels.validate();
			topPanel.add(labelPanels, BorderLayout.SOUTH);
		}
		topPanel.validate();
		leftPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
		JLabel left = new JLabel();
		if (leftImage != null && leftImage.exists()) {
			left.setIcon(new ImageIcon(leftImage.getAbsolutePath()));
		} else {
			left.setText(FlexoLocalization.localizedForKey("file_does_not_exist"));
		}
		leftPanel.add(left);
		rightPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
		JLabel right = new JLabel();
		if (rightImage != null && rightImage.exists()) {
			right.setIcon(new ImageIcon(rightImage.getAbsolutePath()));
		} else {
			right.setText(FlexoLocalization.localizedForKey("file_does_not_exist"));
		}
		rightPanel.add(right);

		JComponent separator = getSeparator();

		GridBagLayout gridbag = new GridBagLayout();
		GridBagConstraints c = new GridBagConstraints();
		JPanel centerPane = new JPanel();
		centerPane.setLayout(gridbag);

		// c.gridwidth = 1;
		// c.gridheight = 1;
		c.weightx = 0.0;
		c.anchor = GridBagConstraints.NORTHWEST;
		c.fill = GridBagConstraints.NONE;
		// c.gridwidth = 1;
		gridbag.setConstraints(leftPanel, c);
		centerPane.add(leftPanel);

		c.weightx = 1.0;
		c.weighty = 1.0;
		// c.gridwidth = 1;
		// c.gridheight = 1;
		c.anchor = GridBagConstraints.WEST;
		c.fill = GridBagConstraints.BOTH;
		// c.gridwidth = GridBagConstraints.REMAINDER;
		gridbag.setConstraints(left, c);
		centerPane.add(left);

		// c.gridwidth = 1;
		// c.gridheight = 1;
		c.weightx = 0.0;
		c.anchor = GridBagConstraints.CENTER;
		c.fill = GridBagConstraints.VERTICAL;
		// c.gridwidth = 1;
		gridbag.setConstraints(separator, c);
		centerPane.add(separator);

		c.weightx = 1.0;
		c.weighty = 1.0;
		// c.gridwidth = 1;
		// c.gridheight = 1;
		c.anchor = GridBagConstraints.EAST;
		c.fill = GridBagConstraints.BOTH;
		// c.gridwidth = GridBagConstraints.REMAINDER;
		gridbag.setConstraints(right, c);
		centerPane.add(right);

		// c.gridwidth = 1;
		// c.gridheight = 1;
		c.weightx = 0.0;
		c.anchor = GridBagConstraints.NORTHEAST;
		c.fill = GridBagConstraints.NONE;
		// c.gridwidth = 1;
		gridbag.setConstraints(rightPanel, c);
		centerPane.add(rightPanel);
		centerPane.validate();
		add(topPanel, BorderLayout.NORTH);

		JPanel comparePanel = new JPanel(new BorderLayout());
		scrollPane = new JScrollPane(centerPane, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
				ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		comparePanel.add(scrollPane, BorderLayout.CENTER);
		scrollPane.getVerticalScrollBar().setUnitIncrement(10);
		scrollPane.getVerticalScrollBar().setBlockIncrement(50);
		// JPanel scrollBars = new JPanel(new GridLayout(1,2));
		// scrollBars.add(_leftTextArea.getHorizontalScrollBar());
		// scrollBars.add(_rightTextArea.getHorizontalScrollBar());
		// scrollBars.validate();
		//
		// comparePanel.add(scrollBars,BorderLayout.SOUTH);
		comparePanel.validate();

		add(comparePanel, BorderLayout.CENTER);
		validate();

	}

	private JComponent getSeparator() {
		if (_separator == null) {
			_separator = new JPanel();
			_separator.setLayout(new BoxLayout(_separator, BoxLayout.Y_AXIS));
			_separator.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));
		}
		return _separator;
	}

}
