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
package org.openflexo.components;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.Enumeration;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;

import org.openflexo.swing.VerticalLayout;
import org.openflexo.view.FlexoDialog;

import org.openflexo.localization.FlexoLocalization;

/**
 * Component used to choose frameworks for the Data Model
 * 
 * @author sguerin
 */
public class AskFrameworkDialog extends FlexoDialog {

	protected Vector checkBoxVector;

	protected File _dataModelDirectory;

	/**
	 * @deprecated
	 * @param dataModelDirectory
	 */
	@Deprecated
	public AskFrameworkDialog(File dataModelDirectory) {
		super();
		_dataModelDirectory = dataModelDirectory;
		JLabel question = new JLabel(FlexoLocalization.localizedForKey("please_select_framework_your_project_depends_on"));
		JLabel hint1 = new JLabel(FlexoLocalization.localizedForKey("select_at_least_denali_fundation_and_denali_flexo"));
		JLabel hint2 = new JLabel(FlexoLocalization.localizedForKey("select_denali_contento_if_you_are_defining_a_new_contento_service"));
		getContentPane().setLayout(new VerticalLayout());
		getContentPane().add(question);
		getContentPane().add(hint1);
		getContentPane().add(hint2);
		setSize(300, 800);
		checkBoxVector = new Vector();
		JButton confirmButton = new JButton(FlexoLocalization.localizedForKey("confirm"));
		JButton cancelButton = new JButton(FlexoLocalization.localizedForKey("cancel"));
		cancelButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				dispose();
			}
		});
		confirmButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Enumeration en = checkBoxVector.elements();
				while (en.hasMoreElements()) {
					JCheckBox item = (JCheckBox) en.nextElement();
					/*if (item.isSelected()) {
					    try {
					        FileUtils.copyDirFromDirToDir(item.getText(), FlexoCst.DENALI_FOUNDATION_DIR, _dataModelDirectory);
					    } catch (IOException e1) {
					        // Warns about the exception
					        e1.printStackTrace();
					    }
					}*/
				}
				dispose();
			}
		});
		getContentPane().add(cancelButton);
		getContentPane().add(confirmButton);
		setModal(true);
		show();
	}

	private static boolean isDefaultSelected(String n) {
		return n.equals("DenaliFoundation") || n.equals("DenaliFlexo");
	}

}
