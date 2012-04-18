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
package org.openflexo.fib.view.widget;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.logging.Logger;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import org.openflexo.fib.controller.FIBController;
import org.openflexo.fib.model.FIBFile;
import org.openflexo.fib.model.FIBModelObject;
import org.openflexo.fib.view.FIBWidgetView;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.swing.FlexoFileChooser;
import org.openflexo.toolbox.StringUtils;
import org.openflexo.toolbox.ToolBox;

/**
 * Represents a widget able to edit a File, or a String representing a File or a StringConvertable object
 * 
 * @author sguerin
 */
public class FIBFileWidget extends FIBWidgetView<FIBFile, JTextField, File> {

	static final Logger logger = Logger.getLogger(FIBFileWidget.class.getPackage().getName());

	protected JPanel fileChooserPanel;

	protected JButton chooseButton;

	protected JTextField currentDirectoryLabel;

	protected File _file = null;

	protected FIBFile.FileMode mode;
	protected String filter;
	protected String title;
	protected Boolean isDirectory;
	protected File defaultDirectory;
	protected int columns;

	private static final int DEFAULT_COLUMNS = 10;

	public FIBFileWidget(FIBFile model, FIBController controller) {
		super(model, controller);

		mode = model.mode != null ? model.mode : FIBFile.FileMode.OpenMode;
		filter = model.filter;
		title = model.title;
		isDirectory = model.isDirectory;
		defaultDirectory = model.defaultDirectory != null ? model.defaultDirectory : new File(System.getProperty("user.dir"));

		fileChooserPanel = new JPanel(new BorderLayout());
		fileChooserPanel.setOpaque(false);
		chooseButton = new JButton();
		chooseButton.setText(FlexoLocalization.localizedForKey(FIBModelObject.LOCALIZATION, "choose", chooseButton));
		addActionListenerToChooseButton();
		currentDirectoryLabel = new JTextField("");
		currentDirectoryLabel.setColumns(model.columns != null ? model.columns : DEFAULT_COLUMNS);
		currentDirectoryLabel.setMinimumSize(MINIMUM_SIZE);
		currentDirectoryLabel.setPreferredSize(MINIMUM_SIZE);
		currentDirectoryLabel.setEditable(false);
		currentDirectoryLabel.setEnabled(true);
		currentDirectoryLabel.setFont(new Font("SansSerif", Font.PLAIN, 10));
		fileChooserPanel.add(currentDirectoryLabel, BorderLayout.CENTER);
		fileChooserPanel.add(chooseButton, BorderLayout.EAST);
		fileChooserPanel.addFocusListener(this);
		if (ToolBox.getPLATFORM() != ToolBox.MACOS) {
			fileChooserPanel.setBorder(BorderFactory.createEmptyBorder(TOP_COMPENSATING_BORDER, LEFT_COMPENSATING_BORDER,
					BOTTOM_COMPENSATING_BORDER, RIGHT_COMPENSATING_BORDER));
		}
		setFile(null);

	}

	protected void configureFileChooser(FlexoFileChooser chooser) {
		if (!isDirectory) {
			// System.out.println("Looking for files");
			chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
			chooser.setDialogTitle(StringUtils.isEmpty(title) ? FlexoLocalization.localizedForKey(FIBModelObject.LOCALIZATION,
					"select_a_file") : FlexoLocalization.localizedForKey(getController().getLocalizer(), title));
			chooser.setFileFilterAsString(filter);
			chooser.setDialogType(mode.getMode());
			System.setProperty("apple.awt.fileDialogForDirectories", "false");
		} else {
			// System.out.println("Looking for directories");
			chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			chooser.setDialogTitle(StringUtils.isEmpty(title) ? FlexoLocalization.localizedForKey(FIBModelObject.LOCALIZATION,
					"select_directory") : FlexoLocalization.localizedForKey(getController().getLocalizer(), title));
			chooser.setFileFilterAsString(filter);
			chooser.setDialogType(mode.getMode());
			System.setProperty("apple.awt.fileDialogForDirectories", "true");
		}
	}

	public void addActionListenerToChooseButton() {
		chooseButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Window parent = SwingUtilities.getWindowAncestor(chooseButton);
				// get destination directory
				FlexoFileChooser chooser = new FlexoFileChooser(parent);
				if (_file != null) {
					chooser.setCurrentDirectory(_file);
					if (!_file.isDirectory()) {
						chooser.setSelectedFile(_file);
					}
				}
				configureFileChooser(chooser);

				switch (mode) {
				case OpenMode:
					if (chooser.showOpenDialog(chooseButton) == JFileChooser.APPROVE_OPTION) {
						// a dir has been picked...

						try {
							setFile(chooser.getSelectedFile());
							updateModelFromWidget();
						} catch (Exception e1) {
							e1.printStackTrace();
						}
					} else {
						// cancelled, return.
					}
					break;

				case SaveMode:
					if (chooser.showSaveDialog(chooseButton) == JFileChooser.APPROVE_OPTION) {
						// a dir has been picked...
						try {
							setFile(chooser.getSelectedFile());
							updateModelFromWidget();
						} catch (Exception e1) {
							e1.printStackTrace();
						}
					} else {
						// cancelled, return.
					}
					break;

				default:
					break;
				}
			}
		});
	}

	public void performUpdate(Object newValue) {
		if (newValue instanceof File) {
			setFile((File) newValue);
		} else if (newValue instanceof String) {
			setFile(new File((String) newValue));
		}
	}

	@Override
	public synchronized boolean updateWidgetFromModel() {
		if (notEquals(getValue(), _file)) {
			widgetUpdating = true;
			if (getValue() instanceof File) {
				setFile(getValue());
			} else if (getValue() == null) {
				setFile(null);
			}
			widgetUpdating = false;
			return true;
		}
		return false;
	}

	/**
	 * Update the model given the actual state of the widget
	 */
	@Override
	public synchronized boolean updateModelFromWidget() {
		if (notEquals(getValue(), _file)) {
			modelUpdating = true;
			setValue(_file);
			modelUpdating = false;
			return true;
		}
		return false;
	}

	@Override
	public JPanel getJComponent() {
		return fileChooserPanel;
	}

	@Override
	public JTextField getDynamicJComponent() {
		return currentDirectoryLabel;
	}

	protected void setFile(File aFile) {
		_file = aFile;
		if (_file != null) {
			currentDirectoryLabel.setEnabled(true);
			currentDirectoryLabel.setText(_file.getAbsolutePath());
			currentDirectoryLabel.setToolTipText(_file.getAbsolutePath());
		} else {
			currentDirectoryLabel.setEnabled(false);
			currentDirectoryLabel.setText(FlexoLocalization.localizedForKey(FIBModelObject.LOCALIZATION, "no_file"));
		}
	}

}
