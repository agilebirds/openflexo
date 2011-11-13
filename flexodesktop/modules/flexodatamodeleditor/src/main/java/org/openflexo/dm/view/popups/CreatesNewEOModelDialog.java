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
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.BorderFactory;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.filechooser.FileFilter;

import org.openflexo.swing.FileSelector;
import org.openflexo.toolbox.ToolBox;
import org.openflexo.view.FlexoDialog;
import org.openflexo.view.controller.FlexoController;

import org.openflexo.foundation.dm.action.CreateDMEOModel;
import org.openflexo.foundation.dm.eo.DMEOAdaptorType;
import org.openflexo.foundation.rm.FlexoProject;
import org.openflexo.foundation.rm.ProjectRestructuration;
import org.openflexo.localization.FlexoLocalization;

/**
 * Popup allowing to choose parameters to creates a new EOModel
 * 
 * @author sguerin
 * 
 */
public class CreatesNewEOModelDialog extends FlexoDialog {

	protected int returned;

	private JPanel newEntryCreationPanel;

	private JComboBox adaptorTypeChooser;

	private FileSelector directorySelector;

	private JTextField eoModelNameTextField;

	private JTextField userNameTextField;

	private JTextField passwdTextField;

	private JTextField databaseTextField;

	private JTextField pluginTextField;

	private JTextField driverTextField;

	public static final int CANCEL = 0;

	public static final int VALIDATE = 1;

	public CreatesNewEOModelDialog(FlexoProject project) {
		this(project, FlexoController.getActiveFrame());
	}

	public CreatesNewEOModelDialog(FlexoProject project, Frame parent) {
		super(parent);
		returned = CANCEL;

		setTitle(FlexoLocalization.localizedForKey("creates_new_eomodel"));

		newEntryCreationPanel = new JPanel();
		newEntryCreationPanel.setLayout(new GridLayout(8, 2));

		adaptorTypeChooser = new JComboBox(DMEOAdaptorType.availableValues());
		adaptorTypeChooser.setRenderer(new DefaultListCellRenderer() {
			@Override
			public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
				// TODO Auto-generated method stub
				Component answer = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
				if (answer instanceof JLabel) {
					((JLabel) answer).setText(((DMEOAdaptorType) value).getName());
				}
				return answer;
			}
		});

		newEntryCreationPanel.add(new JLabel(FlexoLocalization.localizedForKey("adaptor") + " ", SwingConstants.RIGHT));
		newEntryCreationPanel.add(adaptorTypeChooser);

		newEntryCreationPanel.add(new JLabel(FlexoLocalization.localizedForKey("eomodel_file") + " ", SwingConstants.RIGHT));
		newEntryCreationPanel.add(eoModelNameTextField = new JTextField("", 15));

		directorySelector = new FileSelector(ProjectRestructuration.getExpectedDataModelDirectory(project.getProjectDirectory()),
				new FileFilter() {
					@Override
					public boolean accept(File f) {
						return f.isDirectory();
					}

					@Override
					public String getDescription() {
						return FlexoLocalization.localizedForKey("directories");
					}
				}, JFileChooser.DIRECTORIES_ONLY, JFileChooser.OPEN_DIALOG);

		newEntryCreationPanel.add(new JLabel(FlexoLocalization.localizedForKey("directory") + " ", SwingConstants.RIGHT));
		newEntryCreationPanel.add(directorySelector);

		newEntryCreationPanel.add(new JLabel(FlexoLocalization.localizedForKey("user_name") + " ", SwingConstants.RIGHT));
		newEntryCreationPanel.add(userNameTextField = new JTextField("", 15));
		newEntryCreationPanel.add(new JLabel(FlexoLocalization.localizedForKey("password") + " ", SwingConstants.RIGHT));
		newEntryCreationPanel.add(passwdTextField = new JTextField("", 15));
		newEntryCreationPanel.add(new JLabel(FlexoLocalization.localizedForKey("database_server_url") + " ", SwingConstants.RIGHT));
		newEntryCreationPanel.add(databaseTextField = new JTextField("", 15));
		newEntryCreationPanel.add(new JLabel(FlexoLocalization.localizedForKey("plugin") + " ", SwingConstants.RIGHT));
		newEntryCreationPanel.add(pluginTextField = new JTextField("", 15));
		newEntryCreationPanel.add(new JLabel(FlexoLocalization.localizedForKey("driver") + " ", SwingConstants.RIGHT));
		newEntryCreationPanel.add(driverTextField = new JTextField("", 15));

		newEntryCreationPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

		getContentPane().setLayout(new BorderLayout());

		JPanel controlPanel = new JPanel();
		controlPanel.setLayout(new FlowLayout());

		JButton confirmButton = new JButton(FlexoLocalization.localizedForKey("validate"));
		JButton cancelButton = new JButton(FlexoLocalization.localizedForKey("cancel"));

		cancelButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				returned = CANCEL;
				dispose();
			}
		});
		confirmButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (checkEOModelOKForCreation()) {
					returned = VALIDATE;
					dispose();
				}
			}
		});
		if (ToolBox.getPLATFORM() == ToolBox.MACOS) {
			controlPanel.add(cancelButton);
			controlPanel.add(confirmButton);
		} else {
			controlPanel.add(confirmButton);
			controlPanel.add(cancelButton);
		}

		JPanel contentPanel = new JPanel();
		contentPanel.setLayout(new BorderLayout());

		contentPanel.add(newEntryCreationPanel, BorderLayout.CENTER);
		contentPanel.add(controlPanel, BorderLayout.SOUTH);

		getContentPane().add(contentPanel, BorderLayout.CENTER);
		getRootPane().setDefaultButton(confirmButton);
		setModal(true);
		validate();
		pack();
		/*        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		        setLocation((dim.width - getSize().width) / 2, (dim.height - getSize().height - 100) / 2);*/
		setVisible(true);
	}

	public int getStatus() {
		return returned;
	}

	public String getEOModelName() {
		String answer = eoModelNameTextField.getText();
		if (!answer.endsWith(".eomodeld")) {
			answer += ".eomodeld";
		}
		return answer;
	}

	public String getUserName() {
		return userNameTextField.getText();
	}

	public String getPassword() {
		return passwdTextField.getText();
	}

	public String getDatabaseServerURL() {
		return databaseTextField.getText();
	}

	public String getPlugin() {
		return pluginTextField.getText();
	}

	public String getDriver() {
		return driverTextField.getText();
	}

	public DMEOAdaptorType getAdaptorType() {
		return (DMEOAdaptorType) adaptorTypeChooser.getSelectedItem();
	}

	public File getEOModelFile() {
		return new File(getEOModelDirectory(), getEOModelName());
	}

	public File getEOModelDirectory() {
		return directorySelector.getEditedFile();
	}

	protected boolean checkEOModelOKForCreation() {
		String newName = eoModelNameTextField.getText();
		if ((newName == null) || (newName.trim().equals(""))) {
			FlexoController.notify(FlexoLocalization.localizedForKey("please_supply_a_valid_name"));
			return false;
		}

		File file = getEOModelDirectory();
		if (file == null) {
			FlexoController.notify(FlexoLocalization.localizedForKey("please_supply_a_valid_file"));
			return false;
		}
		return true;
	}

	public static int displayDialog(CreateDMEOModel flexoAction, FlexoProject project, Frame owner) {
		// flexoAction.setProject(project);
		CreatesNewEOModelDialog dialog = new CreatesNewEOModelDialog(project, owner);
		if (dialog.getStatus() == VALIDATE) {
			flexoAction.setEOModelFile(dialog.getEOModelFile());
			flexoAction.setAdaptorType(dialog.getAdaptorType());
			flexoAction.setDatabaseServerURL(dialog.getDatabaseServerURL());
			flexoAction.setDriver(dialog.getDriver());
			flexoAction.setPlugin(dialog.getPlugin());
			flexoAction.setUserName(dialog.getUserName());
			flexoAction.setPassword(dialog.getPassword());
		}
		return dialog.getStatus();
	}

}
