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

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;
import java.util.logging.Logger;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;

import org.openflexo.FlexoCst;
import org.openflexo.components.browser.BrowserConfiguration;
import org.openflexo.components.widget.MultipleObjectSelector;
import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.rm.FlexoProject;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.toolbox.ToolBox;
import org.openflexo.view.FlexoDialog;

/**
 * Popup allowing to choose some objects of a given type from Flexo model objects hierarchy
 * 
 * @author sguerin
 * 
 */
public class MultipleObjectSelectorPopup extends FlexoDialog {

	// TODO class must be typed !

	private static final Logger logger = Logger.getLogger(MultipleObjectSelectorPopup.class.getPackage().getName());

	private JTextArea _descriptionTA;

	public static final int CANCEL = 0;

	public static final int VALIDATE = 1;

	protected int returnedStatus = CANCEL;

	protected MultipleObjectSelector<? extends FlexoModelObject> choicePanel;
	protected JPanel centerPanel;

	private BrowserConfiguration _browserConfiguration;

	public BrowserConfiguration getBrowserConfiguration() {
		return _browserConfiguration;
	}

	protected String getPopupTitle() {
		return _title;
	}

	protected String getValidateButtonLabel() {
		return FlexoLocalization.localizedForKey(_unlocalizedValidateButtonLabel);
	}

	private String _title;
	private String _unlocalizedValidateButtonLabel;

	public MultipleObjectSelectorPopup(String title, String label, String description, BrowserConfiguration browserConfiguration,
			FlexoProject project, Frame owner) {
		this(title, label, description, "validate", browserConfiguration, project, owner);
	}

	public MultipleObjectSelectorPopup(String title, String label, String description, String unlocalizedValidateButtonLabel,
			BrowserConfiguration browserConfiguration, FlexoProject project, Frame owner) {
		super(owner);

		_browserConfiguration = browserConfiguration;
		_unlocalizedValidateButtonLabel = unlocalizedValidateButtonLabel;

		_title = title;

		setTitle(getPopupTitle());
		getContentPane().setLayout(new BorderLayout());

		JPanel topPanel = new JPanel();

		_descriptionTA = new JTextArea(3, 40);
		_descriptionTA.setLineWrap(true);
		_descriptionTA.setWrapStyleWord(true);
		_descriptionTA.setFont(FlexoCst.MEDIUM_FONT);
		_descriptionTA.setEditable(false);
		_descriptionTA.setBorder(BorderFactory.createEmptyBorder(20, 50, 20, 50));
		_descriptionTA.setText(description);

		topPanel.setLayout(new BorderLayout());

		if (label != null) {
			JLabel titleLabel = new JLabel(label, SwingConstants.CENTER);
			titleLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
			topPanel.add(titleLabel, BorderLayout.NORTH);
		}

		topPanel.add(_descriptionTA, BorderLayout.CENTER);

		choicePanel = new MultipleObjectSelector<FlexoModelObject>(browserConfiguration, null,
				new MultipleObjectSelector.ObjectSelectabilityDelegate<FlexoModelObject>() {
					@Override
					public boolean isSelectable(FlexoModelObject object) {
						// TODO: type this !!!
						return true;
					}
				});
		choicePanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

		JPanel controlPanel = new JPanel();
		controlPanel.setLayout(new FlowLayout());

		JButton confirmButton = new JButton(getValidateButtonLabel());
		JButton cancelButton = new JButton(FlexoLocalization.localizedForKey("cancel"));

		cancelButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				performCancel();
			}
		});
		confirmButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				performConfirm();
			}
		});
		if (ToolBox.getPLATFORM() == ToolBox.MACOS) {
			controlPanel.add(cancelButton);
			controlPanel.add(confirmButton);
		} else {
			controlPanel.add(confirmButton);
			controlPanel.add(cancelButton);
		}
		// confirmButton.setSelected(true);

		centerPanel = new JPanel();
		centerPanel.setLayout(new BorderLayout());

		centerPanel.add(choicePanel, BorderLayout.CENTER);
		if (getAdditionalPanel() != null) {
			centerPanel.add(getAdditionalPanel(), BorderLayout.SOUTH);
		}

		JPanel contentPanel = new JPanel();
		contentPanel.setLayout(new BorderLayout());

		contentPanel.add(topPanel, BorderLayout.NORTH);
		contentPanel.add(centerPanel, BorderLayout.CENTER);
		contentPanel.add(controlPanel, BorderLayout.SOUTH);

		getContentPane().add(contentPanel, BorderLayout.CENTER);
		getRootPane().setDefaultButton(confirmButton);
		setModal(true);
		validate();
		pack();
		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		setLocation((dim.width - getSize().width) / 2, (dim.height - getSize().height) / 2 - 100);
	}

	public JPanel getAdditionalPanel() {
		return null;
	}

	public void performConfirm() {
		returnedStatus = VALIDATE;
		dispose();
	}

	public void performCancel() {
		returnedStatus = CANCEL;
		dispose();
	}

	public int getStatus() {
		return returnedStatus;
	}

	public Vector<? extends FlexoModelObject> getSelectedObjects() {
		return choicePanel.getSelectedObjects();
	}

}
