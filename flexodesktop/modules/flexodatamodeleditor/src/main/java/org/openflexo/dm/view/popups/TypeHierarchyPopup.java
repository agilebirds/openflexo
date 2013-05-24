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
package org.openflexo.dm.view.popups;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;

import org.openflexo.FlexoCst;
import org.openflexo.dm.view.TypeHierarchyPanel;
import org.openflexo.dm.view.controller.DMController;
import org.openflexo.foundation.dm.DMEntity;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.view.FlexoDialog;

/**
 * Popup allowing to choose some objects of a given type from Flexo model objects hierarchy
 * 
 * @author sguerin
 * 
 */
public class TypeHierarchyPopup extends FlexoDialog {

	private JTextArea _descriptionTA;

	protected TypeHierarchyPanel hierarchyPanel;

	public TypeHierarchyPopup(final DMEntity entity, DMController controller) {
		super(controller.getFlexoFrame());

		String title = FlexoLocalization.localizedForKey("type_hierarchy_for") + " " + entity.getName();
		String description = FlexoLocalization.localizedForKey("type_hierarchy_description");
		setTitle(title);
		getContentPane().setLayout(new BorderLayout());

		JPanel topPanel = new JPanel();

		_descriptionTA = new JTextArea(3, 40);
		_descriptionTA.setLineWrap(true);
		_descriptionTA.setWrapStyleWord(true);
		_descriptionTA.setFont(FlexoCst.MEDIUM_FONT);
		_descriptionTA.setEditable(false);
		_descriptionTA.setBorder(BorderFactory.createEmptyBorder(20, 50, 20, 50));
		_descriptionTA.setText(description);

		JLabel titleLabel = new JLabel(title);
		titleLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

		topPanel.setLayout(new BorderLayout());
		topPanel.add(titleLabel, BorderLayout.NORTH);
		topPanel.add(_descriptionTA, BorderLayout.CENTER);

		hierarchyPanel = new TypeHierarchyPanel(entity, controller);
		hierarchyPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

		JPanel controlPanel = new JPanel();
		controlPanel.setLayout(new FlowLayout());

		JButton closeButton = new JButton(FlexoLocalization.localizedForKey("close"));

		closeButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				dispose();
			}
		});
		controlPanel.add(closeButton);
		closeButton.setSelected(true);

		JPanel contentPanel = new JPanel();
		contentPanel.setLayout(new BorderLayout());

		contentPanel.add(topPanel, BorderLayout.NORTH);
		contentPanel.add(hierarchyPanel, BorderLayout.CENTER);
		contentPanel.add(controlPanel, BorderLayout.SOUTH);

		getContentPane().add(contentPanel, BorderLayout.CENTER);

		setModal(false);
		validate();
		getRootPane().setDefaultButton(closeButton);
		pack();
		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		setLocation((dim.width - getSize().width) / 2, (dim.height - getSize().height) / 2 - 100);

		setVisible(true);

	}

}
