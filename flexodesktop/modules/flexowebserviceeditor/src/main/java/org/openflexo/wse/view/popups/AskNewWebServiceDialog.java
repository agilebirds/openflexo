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
package org.openflexo.wse.view.popups;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.filechooser.FileFilter;

import org.openflexo.FlexoCst;
import org.openflexo.foundation.FlexoProject;
import org.openflexo.foundation.ws.action.AbstractCreateNewWebService;
import org.openflexo.foundation.ws.action.CreateNewWebService;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.swing.FileSelector;
import org.openflexo.swing.JRadioButtonWithIcon;
import org.openflexo.toolbox.FileResource;
import org.openflexo.toolbox.ResourceLocator;
import org.openflexo.view.FlexoDialog;
import org.openflexo.view.controller.FlexoController;

/**
 * Popup allowing to choose and create a new repository
 * 
 * @author sguerin
 * @deprecated
 */
@Deprecated
public class AskNewWebServiceDialog extends FlexoDialog implements ActionListener {

	public static final File EXTERNAL_REPOSITORY_ICON_FILE = new FileResource("Resources/DM/SmallExternalRepository.gif");

	public static final File DM_REPOSITORY_ICON_FILE = new FileResource("Resources/DM/SmallRepository.gif");

	public static final File DM_EOREPOSITORY_ICON_FILE = new FileResource("Resources/DM/SmallEORepository.gif");

	protected JTextField newWebServiceNameTF;

	private JTextArea webserviceDescriptionTA;

	protected FileSelector wsdlFileSelector;

	public static final int CANCEL = 0;

	public static final int VALIDATE = 1;

	protected int returnedStatus = CANCEL;

	protected JPanel choicePanel;

	protected JRadioButton externalWSButton;
	protected JRadioButton internalWSButton;

	public AskNewWebServiceDialog(CreateNewWebService flexoAction, Frame owner) {
		super(owner);
		_flexoAction = flexoAction;

		setTitle(FlexoLocalization.localizedForKey("ws_add_new_webService"));
		getContentPane().setLayout(new BorderLayout());

		// Create the radio buttons.
		externalWSButton = new JRadioButtonWithIcon(FlexoLocalization.localizedForKey("ws_external_webservice"), DM_REPOSITORY_ICON_FILE,
				true);
		externalWSButton.addActionListener(this);
		externalWSButton.setActionCommand(AbstractCreateNewWebService.EXTERNAL_WS);

		internalWSButton = new JRadioButtonWithIcon(FlexoLocalization.localizedForKey("ws_internal_webservice"), DM_REPOSITORY_ICON_FILE);
		internalWSButton.addActionListener(this);
		internalWSButton.setActionCommand(AbstractCreateNewWebService.INTERNAL_WS);

		wsdlFileSelector = new FileSelector(ResourceLocator.getUserHomeDirectory(), new FileFilter() {
			@Override
			public boolean accept(File f) {
				// return (f.getName().endsWith(".mdl"));
				return true;
			}

			@Override
			public String getDescription() {
				return FlexoLocalization.localizedForKey("wsdl_file");
			}
		}, JFileChooser.FILES_ONLY) {
			@Override
			public void fireEditedObjectChanged() {
				super.fireEditedObjectChanged();
				if (getEditedObject() != null && getEditedObject() instanceof File && newWebServiceNameTF != null) {
					String newSelectedName = getEditedObject().getName();
					String newName = null;
					if (newSelectedName.indexOf(".") > 0) {
						newName = newSelectedName.substring(0, newSelectedName.indexOf("."));
					} else {
						newName = newSelectedName;
					}
					newWebServiceNameTF.setText(newName);
				}
				externalWSButton.setSelected(true);
				selectWebServiceType(AbstractCreateNewWebService.EXTERNAL_WS);
			}
		};

		// Group the radio buttons.
		ButtonGroup group = new ButtonGroup();
		group.add(externalWSButton);
		group.add(internalWSButton);

		newWebServiceNameTF = new JTextField(20);
		newWebServiceNameTF.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				performConfirm();
			}

		});

		JPanel topPanel = new JPanel();
		JPanel webServiceNamePanel = new JPanel();
		webServiceNamePanel.setLayout(new FlowLayout());
		webServiceNamePanel.add(new JLabel(FlexoLocalization.localizedForKey("ws_webservice_name")));
		webServiceNamePanel.add(newWebServiceNameTF);
		webServiceNamePanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

		webserviceDescriptionTA = new JTextArea(3, 40);
		webserviceDescriptionTA.setLineWrap(true);
		webserviceDescriptionTA.setWrapStyleWord(true);
		webserviceDescriptionTA.setFont(FlexoCst.MEDIUM_FONT);
		webserviceDescriptionTA.setEditable(false);
		webserviceDescriptionTA.setBorder(BorderFactory.createEmptyBorder(20, 50, 20, 50));

		topPanel.setLayout(new BorderLayout());
		topPanel.add(webServiceNamePanel, BorderLayout.NORTH);
		topPanel.add(webserviceDescriptionTA, BorderLayout.CENTER);

		init();

		choicePanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

		JPanel controlPanel = new JPanel();
		controlPanel.setLayout(new FlowLayout());

		JButton confirmButton = new JButton(FlexoLocalization.localizedForKey("validate"));
		JButton cancelButton = new JButton(FlexoLocalization.localizedForKey("cancel"));

		cancelButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				returnedStatus = CANCEL;
				dispose();
			}
		});
		confirmButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				performConfirm();
			}
		});
		controlPanel.add(cancelButton);
		controlPanel.add(confirmButton);
		confirmButton.setSelected(true);

		JPanel contentPanel = new JPanel();
		contentPanel.setLayout(new BorderLayout());

		contentPanel.add(topPanel, BorderLayout.NORTH);
		contentPanel.add(choicePanel, BorderLayout.CENTER);
		contentPanel.add(controlPanel, BorderLayout.SOUTH);

		getContentPane().add(contentPanel, BorderLayout.CENTER);
		getRootPane().setDefaultButton(confirmButton);
		setModal(true);
		validate();
		pack();
		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		setLocation((dim.width - getSize().width) / 2, (dim.height - getSize().height) / 2 - 100);
		setVisible(true);
	}

	protected void init() {
		choicePanel = new JPanel();
		choicePanel.setLayout(new GridLayout(2, 2));
		choicePanel.add(externalWSButton);
		choicePanel.add(wsdlFileSelector);

		choicePanel.add(internalWSButton);
		choicePanel.add(new JPanel());

		selectWebServiceType(AbstractCreateNewWebService.EXTERNAL_WS);
	}

	public void performConfirm() {
		if (checkWebServiceOKForCreation()) {
			returnedStatus = VALIDATE;
			dispose();
		}
		if (returnedStatus == VALIDATE) {

		}
	}

	/** Listens to the radio buttons. */

	@Override
	public void actionPerformed(ActionEvent e) {
		selectWebServiceType(e.getActionCommand());
	}

	protected void selectWebServiceType(String wsType) {
		_flexoAction.setWebServiceType(wsType);
		if (wsType.equals(AbstractCreateNewWebService.INTERNAL_WS)) {
			webserviceDescriptionTA.setText(FlexoLocalization.localizedForKey("ws_create_internal_ws_description"));
		} else if (wsType.equals(AbstractCreateNewWebService.EXTERNAL_WS)) {
			webserviceDescriptionTA.setText(FlexoLocalization.localizedForKey("ws_create_external_ws_description"));
		}
	}

	protected boolean checkWebServiceOKForCreation() {
		String newWebServiceName = newWebServiceNameTF.getText();
		if (newWebServiceName == null || newWebServiceName.trim().equals("")) {
			FlexoController.notify(FlexoLocalization.localizedForKey("please_supply_a_valid_name"));
			return false;
		}
		_flexoAction.setNewWebServiceName(newWebServiceName);
		String currentChoice = _flexoAction.getWebServiceType();
		if (currentChoice == null) {
			return false;
		}
		if (_flexoAction.getProject().getFlexoWSLibrary().getWSServiceNamed(newWebServiceName) != null) {
			FlexoController.showError(FlexoLocalization.localizedForKey("wsgroup_already_registered"));
			return false;
		}
		if (currentChoice.equals(AbstractCreateNewWebService.EXTERNAL_WS)) {

			File newWSDLFile = wsdlFileSelector.getEditedFile();
			if (newWSDLFile != null) {
				_flexoAction.setWsdlFile(newWSDLFile);
				return true;
			} else {
				FlexoController.showError(FlexoLocalization.localizedForKey("please_supply_a_valid_wsdl_file"));
				return false;
			}

		} else if (currentChoice.equals(AbstractCreateNewWebService.INTERNAL_WS)) {
			FlexoController.showError(FlexoLocalization.localizedForKey("not_implemented_yet"));
			return false;
		}
		return false;
	}

	protected int getStatus() {
		return returnedStatus;
	}

	protected CreateNewWebService _flexoAction;

	public static int displayDialog(CreateNewWebService flexoAction, FlexoProject project, Frame owner) {
		flexoAction.setProject(project);
		AskNewWebServiceDialog dialog = new AskNewWebServiceDialog(flexoAction, owner);
		return dialog.getStatus();
	}

}
