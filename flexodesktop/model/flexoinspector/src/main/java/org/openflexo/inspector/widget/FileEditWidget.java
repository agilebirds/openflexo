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
package org.openflexo.inspector.widget;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import org.openflexo.inspector.AbstractController;
import org.openflexo.inspector.model.PropertyModel;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.swing.FlexoFileChooser;

/**
 * Represents a widget able to edit a File, or a String representing a File or a StringConvertable object
 * 
 * @author sguerin
 */
public class FileEditWidget extends DenaliWidget {

	static final Logger logger = Logger.getLogger(FileEditWidget.class.getPackage().getName());

	protected JPanel _mySmallPanel;

	protected JButton _chooseButton;

	protected JTextField _currentDirectoryLabel;

	protected File _file = null;

	public static final String MODE = "mode";
	public static final String FILTER = "filter";
	public static final String TITLE = "title";

	public static final String OPEN = "open";
	public static final String SAVE = "save";
	private static final int OPEN_MODE = JFileChooser.OPEN_DIALOG;
	private static final int SAVE_MODE = JFileChooser.SAVE_DIALOG;

	private static final int DEFAULT_MODE = OPEN_MODE;

	protected int mode;
	protected String filter;
	protected String title;

	public FileEditWidget(PropertyModel model, AbstractController controller) {
		super(model, controller);

		if (model.hasValueForParameter(MODE)) {
			if (model.getValueForParameter(MODE).equalsIgnoreCase(OPEN)) {
				mode = OPEN_MODE;
			} else if (model.getValueForParameter(MODE).equalsIgnoreCase(SAVE)) {
				mode = SAVE_MODE;
			} else {
				mode = OPEN_MODE;
			}
		} else {
			mode = DEFAULT_MODE;
		}

		if (model.hasValueForParameter(FILTER)) {
			filter = model.getValueForParameter(FILTER);
		} else {
			filter = null;
		}

		if (model.hasValueForParameter(TITLE)) {
			title = model.getValueForParameter(TITLE);
		} else {
			title = null;
		}

		_mySmallPanel = new JPanel(new BorderLayout());
		_chooseButton = new JButton();
		_chooseButton.setText(FlexoLocalization.localizedForKey("choose", _chooseButton));
		addActionListenerToChooseButton();
		_currentDirectoryLabel = new JTextField("");
		_currentDirectoryLabel.setMinimumSize(MINIMUM_SIZE);
		_currentDirectoryLabel.setPreferredSize(MINIMUM_SIZE);
		_currentDirectoryLabel.setEditable(false);
		_currentDirectoryLabel.setEnabled(true);
		_currentDirectoryLabel.setFont(new Font("SansSerif", Font.PLAIN, 10));
		_mySmallPanel.add(_currentDirectoryLabel, BorderLayout.CENTER);
		_mySmallPanel.add(_chooseButton, BorderLayout.EAST);
		getDynamicComponent().addFocusListener(new WidgetFocusListener(this));

	}

	protected void configureFileChooser(FlexoFileChooser chooser) {
		chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		chooser.setDialogTitle(title == null ? FlexoLocalization.localizedForKey("select_a_file") : FlexoLocalization
				.localizedForKey(title));
		chooser.setFileFilterAsString(filter);
		chooser.setDialogType(mode);

		System.setProperty("apple.awt.fileDialogForDirectories", "false");
	}

	public void addActionListenerToChooseButton() {
		_chooseButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Window parent = SwingUtilities.getWindowAncestor(_chooseButton);
				// get destination directory
				FlexoFileChooser chooser = new FlexoFileChooser(parent);
				if (_file != null) {
					chooser.setCurrentDirectory(_file);
					if (!_file.isDirectory()) {
						chooser.setSelectedFile(_file);
					}
				}
				configureFileChooser(chooser);

				if (mode == OPEN_MODE) {
					if (chooser.showOpenDialog(_chooseButton) == JFileChooser.APPROVE_OPTION) {
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
				} else if (mode == SAVE_MODE) {
					if (chooser.showSaveDialog(_chooseButton) == JFileChooser.APPROVE_OPTION) {
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
	public Class getDefaultType() {
		return File.class;
	}

	@Override
	public synchronized void updateWidgetFromModel() {
		widgetUpdating = true;
		Object object = getObjectValue();
		if (object != null) {
			if (object instanceof File) {
				setFile((File) object);
			} else if (typeIsString()) {
				setFile(new File((String) object));
			} else if (typeIsStringConvertable()) {
				setFile(new File(getTypeConverter().convertToString(object)));
			} else {
				if (logger.isLoggable(Level.WARNING)) {
					logger.warning("Property " + _propertyModel.name + " is supposed to be a File or a String, not a " + object);
				}
			}
		}
		widgetUpdating = false;
	}

	/**
	 * Update the model given the actual state of the widget
	 */
	@Override
	public synchronized void updateModelFromWidget() {
		if (logger.isLoggable(Level.FINE)) {
			logger.finer("updateModelFromWidget() file=" + _file + " getType()=" + getType());
		}
		if (_file != null) {
			if (typeIsStringConvertable()) {
				setObjectValue(getTypeConverter().convertFromString(_file.getAbsolutePath()));
			} else if (typeIsString()) {
				setObjectValue(_file.getAbsolutePath());
			} else if (File.class.isAssignableFrom(getType())) {
				setObjectValue(_file);
			}
		}
	}

	@Override
	public JComponent getDynamicComponent() {
		return _mySmallPanel;
	}

	protected void setFile(File aFile) {
		_file = aFile;
		if (_file != null) {
			_currentDirectoryLabel.setText(_file.getAbsolutePath());
			_currentDirectoryLabel.setToolTipText(_file.getAbsolutePath());
		}
	}

}
