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
package org.openflexo.ie.view.popups;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;

import org.openflexo.components.widget.DMEntitySelector;
import org.openflexo.components.widget.ProcessSelector;
import org.openflexo.foundation.dm.DMEntity;
import org.openflexo.foundation.ie.IERegExp;
import org.openflexo.foundation.ie.cl.FlexoComponentFolder;
import org.openflexo.foundation.ie.cl.action.AddComponent;
import org.openflexo.foundation.rm.FlexoProject;
import org.openflexo.foundation.wkf.FlexoProcess;
import org.openflexo.icon.IconLibrary;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.swing.JRadioButtonWithIcon;
import org.openflexo.toolbox.FileResource;
import org.openflexo.toolbox.ToolBox;
import org.openflexo.view.FlexoDialog;
import org.openflexo.view.controller.FlexoController;

/**
 * Popup allowing to choose and create a new component
 * 
 * @author sguerin
 * 
 */
public class AskNewComponentDialog extends FlexoDialog implements ActionListener
{

	public static final FileResource REUSABLECOMPONENT_ICON_FILE = new FileResource("Icons/Model/SE/ReusableComponent.gif");

	public static final FileResource WOD_COMPONENT_ICON = new FileResource("Icons/Model/SE/wocomponent_wiz.gif");

	public static final FileResource COMPONENT_INSTANCE_ICON = new FileResource("Icons/Model/SE/ReusableComponentInstance.gif");

	public static final FileResource OPERATION_COMPONENT_ICON = new FileResource("Icons/Model/SE/SmallOperationComponent.gif");

	public static final FileResource POPUP_COMPONENT_ICON = new FileResource("Icons/Model/SE/SmallPopupComponent.gif");

	public static final FileResource THUMBNAIL_COMPONENT_ICON = new FileResource("Icons/Model/SE/SmallTabComponent.gif");

	private static final String OPERATION_COMPONENT = "OPERATION_COMPONENT";

	private static final String POPUP_COMPONENT = "POPUP_COMPONENT";

	private static final String PARTIAL_COMPONENT = "PARTIAL_COMPONENT";

	private static final String TAB_COMPONENT = "TAB_COMPONENT";

	private static final String DATA_COMPONENT = "DATA_COMPONENT";

	private static final String MONITORING_SCREEN = "MONITORING_SCREEN";

	private static final String MONITORING_COMPONENT = "MONITORING_COMPONENT";

	private final FlexoProject _project;

	private String currentChoice;

	private final ProcessSelector monitoringScreenProcessSelector;

	private final ProcessSelector monitoringComponentProcessSelector;

	private final DMEntitySelector<DMEntity> dataComponentEntitySelector;

	private final JTextField newComponentNameTF;

	public AskNewComponentDialog(FlexoProject project, FlexoComponentFolder folder)
	{
		super();

		_project = project;

		setTitle(FlexoLocalization.localizedForKey("creates_new_component"));
		getContentPane().setLayout(new BorderLayout());

		// Create the radio buttons.
		JRadioButton operationComponentButton = new JRadioButtonWithIcon(FlexoLocalization.localizedForKey("operation_component"),
				OPERATION_COMPONENT_ICON, true);
		operationComponentButton.addActionListener(this);
		operationComponentButton.setActionCommand(OPERATION_COMPONENT);
		currentChoice = OPERATION_COMPONENT;

		JRadioButton popupComponentButton = new JRadioButtonWithIcon(FlexoLocalization.localizedForKey("popup_component"),
				POPUP_COMPONENT_ICON);
		popupComponentButton.addActionListener(this);
		popupComponentButton.setActionCommand(POPUP_COMPONENT);
		/*
		 * JRadioButton partialComponentButton = new
		 * JRadioButtonWithIcon(FlexoLocalization
		 * .localizedForKey("partial_component"), REUSABLECOMPONENT_ICON_FILE);
		 * partialComponentButton.addActionListener(this);
		 * partialComponentButton.setActionCommand(PARTIAL_COMPONENT);
		 */
		JRadioButton tabComponentButton = new JRadioButtonWithIcon(FlexoLocalization.localizedForKey("tab_component"),
				THUMBNAIL_COMPONENT_ICON);
		tabComponentButton.addActionListener(this);
		tabComponentButton.setActionCommand(TAB_COMPONENT);

		/* Disabled unused buttons
        JRadioButton dataComponentButton = new JRadioButtonWithIcon(FlexoLocalization.localizedForKey("data_component"),
                REUSABLECOMPONENT_ICON_FILE);
        dataComponentButton.addActionListener(this);
        dataComponentButton.setActionCommand(DATA_COMPONENT);
        dataComponentButton.setEnabled(false);
        dataComponentButton.setToolTipText(FlexoLocalization.localizedForKey("not_yet_implemented"));

        JRadioButton monitoringScreenButton = new JRadioButtonWithIcon(FlexoLocalization.localizedForKey("monitoring_screen"),
                REUSABLECOMPONENT_ICON_FILE);
        monitoringScreenButton.addActionListener(this);
        monitoringScreenButton.setActionCommand(MONITORING_SCREEN);
        monitoringScreenButton.setEnabled(true);
        monitoringScreenButton.setToolTipText(FlexoLocalization.localizedForKey("not_yet_implemented"));

        JRadioButton monitoringComponentButton = new JRadioButtonWithIcon(FlexoLocalization.localizedForKey("monitoring_component"),
                REUSABLECOMPONENT_ICON_FILE);
        monitoringComponentButton.addActionListener(this);
        monitoringComponentButton.setActionCommand(MONITORING_COMPONENT);
        monitoringComponentButton.setEnabled(false);
        monitoringComponentButton.setToolTipText(FlexoLocalization.localizedForKey("not_yet_implemented"));
		 */
		// Group the radio buttons.
		ButtonGroup group = new ButtonGroup();
		group.add(operationComponentButton);
		group.add(popupComponentButton);
		// group.add(partialComponentButton);
		group.add(tabComponentButton);
		/* Disabled unused buttons
        group.add(dataComponentButton);
        group.add(monitoringScreenButton);
        group.add(monitoringComponentButton);
		 */
		newComponentNameTF = IERegExp.getJavaClassNameValidationTextField(20);
		newComponentNameTF.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e)
			{
				performOK();
			}

		});
		JPanel componentNameChoice = new JPanel();
		componentNameChoice.setLayout(new FlowLayout());
		componentNameChoice.add(new JLabel(FlexoLocalization.localizedForKey("component_name")));
		componentNameChoice.add(newComponentNameTF);
		componentNameChoice.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

		monitoringScreenProcessSelector = new ProcessSelector(project, project.getFlexoWorkflow().getRootFlexoProcess());
		monitoringComponentProcessSelector = new ProcessSelector(project, project.getFlexoWorkflow().getRootFlexoProcess());
		dataComponentEntitySelector = new DMEntitySelector<DMEntity>(project, (DMEntity)null, DMEntity.class);

		JPanel choicePanel = new JPanel();
		choicePanel.setLayout(new GridLayout(3, 2));
		/* Disabled unused buttons
        choicePanel.setLayout(new GridLayout(7, 2));
		 */
		choicePanel.add(operationComponentButton);
		operationComponentButton.setFocusable(false);
		choicePanel.add(new JPanel());
		choicePanel.add(popupComponentButton);
		popupComponentButton.setFocusable(false);
		choicePanel.add(new JPanel());
		// choicePanel.add(partialComponentButton);
		// choicePanel.add(new JPanel());
		choicePanel.add(tabComponentButton);
		tabComponentButton.setFocusable(false);
		choicePanel.add(new JPanel());
		/* Disabled unused buttons
        choicePanel.add(dataComponentButton);
        // TODO FIXME: When dataComponentScreen, monitoringScreen and
        // monitoringComponent will be implemented, the following lines need to
        // be uncommented and the 3 matching "choicePanel.add(new JPanel());"
        // must be removed consequently
        // choicePanel.add(dataComponentEntitySelector);
        choicePanel.add(new JPanel());
        choicePanel.add(monitoringScreenButton);
        choicePanel.add(monitoringScreenProcessSelector);
        //choicePanel.add(new JPanel());
        choicePanel.add(monitoringComponentButton);
        // choicePanel.add(monitoringComponentProcessSelector);
        choicePanel.add(new JPanel());
		 */
		choicePanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

		JPanel controlPanel = new JPanel();
		controlPanel.setLayout(new FlowLayout());

		JButton confirmButton = new JButton(FlexoLocalization.localizedForKey("validate"));
		JButton cancelButton = new JButton(FlexoLocalization.localizedForKey("cancel"));

		cancelButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e)
			{
				dispose();
			}
		});
		confirmButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e)
			{
				performOK();
			}
		});
		if (ToolBox.getPLATFORM()==ToolBox.MACOS) {
			controlPanel.add(cancelButton);
			controlPanel.add(confirmButton);
		} else {
			controlPanel.add(confirmButton);
			controlPanel.add(cancelButton);
		}

		JPanel contentPanel = new JPanel();
		contentPanel.setLayout(new BorderLayout());

		contentPanel.add(componentNameChoice, BorderLayout.NORTH);
		contentPanel.add(choicePanel, BorderLayout.CENTER);
		contentPanel.add(controlPanel, BorderLayout.SOUTH);

		getContentPane().add(contentPanel, BorderLayout.CENTER);
		getRootPane().setDefaultButton(confirmButton);
		setModal(true);
		validate();
		pack();
		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		setLocation((dim.width - getSize().width) / 2, (dim.height - getSize().height) / 2);
		newComponentNameTF.grabFocus();
		setVisible(true);
	}

	private boolean statusOK = false;

	protected void performOK()
	{
		if (checkComponentValidity()) {
			statusOK = true;
			dispose();
		}
	}

	/** Listens to the radio buttons. */

	@Override
	public void actionPerformed(ActionEvent e)
	{
		currentChoice = e.getActionCommand();
	}

	private String newComponentName;

	public boolean checkComponentValidity()
	{
		newComponentName = newComponentNameTF.getText();
		if(newComponentName!=null && newComponentName.equals(DEFAULT_ROCK_COMPONENT_NAME)){
			JFrame frame = new JFrame();
			frame.getContentPane().add(new JLabel(IconLibrary.DEV_TEAM_ICON));
			frame.pack();
			frame.setVisible(true);
			frame.toFront();
			return false;
		}
		if (newComponentName == null || !newComponentName.matches(IERegExp.JAVA_CLASS_NAME_REGEXP)) {
			FlexoController.notify(FlexoLocalization.localizedForKey("must_start_with_a_letter_followed_by_any_letter_or_number"));
			return false;
		}
		if (!_project.getFlexoComponentLibrary().isValidForANewComponentName(newComponentName)) {
			FlexoController.notify(FlexoLocalization.localizedForKey("this_name_isalready_used_by_another_component"));
			return false;
		}
		if (currentChoice == null) {
			return false;
		}

		if (currentChoice.equals(OPERATION_COMPONENT)) {
			componentType = AddComponent.ComponentType.OPERATION_COMPONENT;
		} else if (currentChoice.equals(POPUP_COMPONENT)) {
			componentType = AddComponent.ComponentType.POPUP_COMPONENT;
		} else if (currentChoice.equals(PARTIAL_COMPONENT)) {
			componentType = AddComponent.ComponentType.PARTIAL_COMPONENT;
		} else if (currentChoice.equals(TAB_COMPONENT)) {
			componentType = AddComponent.ComponentType.TAB_COMPONENT;
		} else if (currentChoice.equals(DATA_COMPONENT)) {
			componentType = AddComponent.ComponentType.DATA_COMPONENT;
		} else if (currentChoice.equals(MONITORING_SCREEN)) {
			componentType = AddComponent.ComponentType.MONITORING_SCREEN;
		} else if (currentChoice.equals(MONITORING_COMPONENT)) {
			componentType = AddComponent.ComponentType.MONITORING_COMPONENT;
		}
		return componentType != null;
	}

	private AddComponent.ComponentType componentType = null;

	public AddComponent.ComponentType getComponentType()
	{
		return componentType;
	}

	public boolean hasBeenValidated()
	{
		return statusOK;
	}

	private static final String DEFAULT_ROCK_COMPONENT_NAME = "Hard Rock Hallelujah";

	public String getNewComponentName() {
		return newComponentName;
	}

	public DMEntity getDataComponentEntity()
	{
		return dataComponentEntitySelector.getEditedObject();
	}

	public FlexoProcess getMonitoringComponentProcess()
	{
		return monitoringComponentProcessSelector.getEditedObject();
	}

	public FlexoProcess getMonitoringScreenProcess()
	{
		return monitoringScreenProcessSelector.getEditedObject();
	}
}
