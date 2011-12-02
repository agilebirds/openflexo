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
package org.openflexo.ie.view;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Enumeration;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.border.TitledBorder;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableModel;

import org.openflexo.components.widget.ImageFileSelector;
import org.openflexo.foundation.FlexoException;
import org.openflexo.foundation.ie.action.ImportImage;
import org.openflexo.foundation.ie.cl.OperationComponentDefinition;
import org.openflexo.foundation.ie.menu.FlexoItemMenu;
import org.openflexo.foundation.rm.FlexoProject.ImageFile;
import org.openflexo.icon.SEIconLibrary;
import org.openflexo.ie.view.controller.IEController;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.swing.VerticalLayout;
import org.openflexo.view.controller.FlexoController;

/**
 * @author gpolet Created on 8 sept. 2005
 */
public class FlexoMenuRootItemView extends FlexoMenuItemView {

	protected static final Logger logger = Logger.getLogger(FlexoMenuRootItemView.class.getPackage().getName());

	/**
	 * @param model
	 */
	public FlexoMenuRootItemView(FlexoItemMenu model, IEController ctrl) {
		super(model, ctrl);
		add(new GlobalMenuConfigurationPanel());
	}

	private class GlobalMenuConfigurationPanel extends JPanel {

		JCheckBox useDefaultImage;

		JFileChooser imageFileChooser;

		ImageFileSelector choose;

		JTable table;

		JScrollPane scrollPane;

		JButton addButton;

		JButton removeButton;

		JLabel image;

		JComboBox profilePageList;

		/**
         *
         */
		public GlobalMenuConfigurationPanel() {
			super(new VerticalLayout());
			initUI();
			add(useDefaultImage);

			JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
			panel.add(choose);
			panel.add(image);
			add(panel);

			/*add(scrollPane);

			panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
			panel.add(addButton);
			panel.add(removeButton);
			add(panel);*/
			TitledBorder b = BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder());
			b.setTitle(FlexoLocalization.localizedForKey("global_menu", b));
			setBorder(b);
		}

		private void initUI() {
			image = new JLabel();
			ImageFile logo = updateLogo();
			useDefaultImage = new JCheckBox();
			useDefaultImage.setText(FlexoLocalization.localizedForKey("use_default_image", useDefaultImage));
			updateCheckbox();
			useDefaultImage.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					_model.getNavigationMenu().setUseDefaultImage(useDefaultImage.isSelected());
					choose.setEnabled(!useDefaultImage.isSelected());

				}
			});
			TableModel dataModel = new AbstractTableModel() {
				@Override
				public int getColumnCount() {
					return 2;
				}

				@Override
				public int getRowCount() {
					String s = _model.getNavigationMenu().getButtons();
					if (s == null) {
						return 0;
					}
					return s.split(";").length;
				}

				@Override
				public Object getValueAt(int rowIndex, int columnIndex) {
					if (columnIndex > 1) {
						if (logger.isLoggable(Level.WARNING)) {
							logger.warning("Attempt to obtain an object of a column that does not exist: " + columnIndex);
						}
						return null;
					}
					if (rowIndex < getRowCount()) {
						if (columnIndex == 0) {
							return _model.getNavigationMenu().getButtons().split(";")[rowIndex];
						} else if (_model.getNavigationMenu().getActions() != null) {
							return _model.getNavigationMenu().getActions().split(";")[rowIndex];
						} else {
							return null;
						}
					} else {
						if (logger.isLoggable(Level.WARNING)) {
							logger.warning("Attemp to obtain an object of row that does not exist: " + rowIndex);
						}
						return null;
					}
				}

			};
			table = new JTable(dataModel);

			scrollPane = new JScrollPane(table);
			choose = new ImageFileSelector(_model.getProject(), new ImageFileSelector.ImageImporter() {

				@Override
				public void importImage(ActionEvent e) {
					ImportImage importImage = ImportImage.actionType.makeNewAction(_model.getProject(), null,
							FlexoMenuRootItemView.this.controller.getEditor());
					try {
						importImage.doAction(e);
					} catch (FlexoException e1) {
						e1.printStackTrace();
					}
				}

			}, logo, true) {
				@Override
				public void apply() {
					_model.getNavigationMenu().setLogo(getEditedObject());
					super.apply();
					// TODO: refactor the whole view to use observers (mainly in super-class)
					updateLogo();
					updateChooserPreferredSize();
				}
			};
			choose.setEnabled(!_model.getNavigationMenu().getUseDefaultImage());
			updateChooserPreferredSize();
			addButton = new JButton("+");
			addButton.setToolTipText(FlexoLocalization.localizedForKey("add_a_button"));
			addButton.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {

					String s = FlexoController.askForString(FlexoLocalization.localizedForKey("enter_new_button_name"));
					if ((s == null) || s.trim().equals("")) {
						return;
					} else {
						Object[] possibilities = new String[FlexoMenuRootItemView.this._model.getProject().getFlexoComponentLibrary()
								.getOperationsComponentList().size()];
						Enumeration en = FlexoMenuRootItemView.this._model.getProject().getFlexoComponentLibrary()
								.getOperationsComponentList().elements();
						int i = 0;
						while (en.hasMoreElements()) {
							OperationComponentDefinition element = (OperationComponentDefinition) en.nextElement();
							possibilities[i++] = element.getComponentName();
						}

						String da = (String) JOptionPane.showInputDialog(GlobalMenuConfigurationPanel.this,
								FlexoLocalization.localizedForKey("choose_a_direct_action"),
								FlexoLocalization.localizedForKey("choose_a_direct_action"), JOptionPane.PLAIN_MESSAGE, null,
								possibilities, null);
						if (da == null) {
							da = " ";
						}
						String b = _model.getNavigationMenu().getButtons();
						if ((b == null) || b.trim().equals("")) {
							b = s;
						} else {
							b += ";" + s;
						}
						_model.getNavigationMenu().setButtons(b);
						b = _model.getNavigationMenu().getActions();
						if ((b == null) || b.trim().equals("")) {
							b = da;
						} else {
							b += ";" + da;
						}
						_model.getNavigationMenu().setActions(b);
						table.revalidate();
						table.repaint();
					}
				}
			});

			removeButton = new JButton("-");
			removeButton.setToolTipText(FlexoLocalization.localizedForKey("remove_a_button"));
			removeButton.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					if (_model.getNavigationMenu().getButtons() == null) {
						return;
					}
					int[] rows = table.getSelectedRows();
					String[] s = _model.getNavigationMenu().getButtons().split(";");
					StringBuffer sb = new StringBuffer();
					for (int i = 0; i < s.length; i++) {
						int j;
						for (j = 0; j < rows.length; j++) {
							if (rows[j] == i) {
								break;
							}
						}
						if (j == rows.length) {
							sb.append(s[i]);
							if (i + 1 != s.length) {
								sb.append(';');
							}
						}
					}
					_model.getNavigationMenu().setButtons(sb.toString());
					s = _model.getNavigationMenu().getActions().split(";");
					sb = new StringBuffer();
					for (int i = 0; i < s.length; i++) {
						int j;
						for (j = 0; j < rows.length; j++) {
							if (rows[j] == i) {
								break;
							}
						}
						if (j == rows.length) {
							sb.append(s[i]);
							if (i + 1 != s.length) {
								sb.append(';');
							}
						}
					}
					_model.getNavigationMenu().setActions(sb.toString());
					table.repaint();
				}
			});
		}

		/**
		 *
		 */
		private void updateCheckbox() {
			useDefaultImage.setSelected(_model.getNavigationMenu().getUseDefaultImage());
		}

		/**
		 * @return
		 */
		protected ImageFile updateLogo() {
			ImageFile logo = _model.getNavigationMenu().getLogo();
			if ((logo == null) || (logo.getImageFile() == null)) {
				image.setIcon(SEIconLibrary.NO_IMAGE);
			} else {
				ImageIcon icon = new ImageIcon(logo.getImageFile().getAbsolutePath());
				image.setIcon(icon);
			}
			return logo;
		}

		/**
		 *
		 */
		protected void updateChooserPreferredSize() {
			if (choose.getEditedObject() == null) {
				choose.getTextField().setPreferredSize(new Dimension(100, 20));
			} else {
				choose.getTextField().setPreferredSize(null);
			}
		}

	}

}
