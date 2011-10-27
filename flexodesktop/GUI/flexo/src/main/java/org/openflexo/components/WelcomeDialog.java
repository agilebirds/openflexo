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
import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.security.AccessController;
import java.util.logging.Logger;

import javax.swing.BorderFactory;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.UIManager;
import javax.swing.plaf.metal.MetalLookAndFeel;

import org.openflexo.AdvancedPrefs;
import org.openflexo.FlexoCst;
import org.openflexo.GeneralPreferences;
import org.openflexo.icon.IconLibrary;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.logging.FlexoLogger;
import org.openflexo.module.Module;
import org.openflexo.module.ModuleLoader;
import org.openflexo.toolbox.ToolBox;

import sun.security.action.GetPropertyAction;

import com.sun.java.swing.plaf.windows.WindowsClassicLookAndFeel;

/**
 * Allow to choose between creating a new project or opening an existing one
 * 
 * @author sguerin
 */
public class WelcomeDialog extends AboutDialog {

	private static final Logger logger = FlexoLogger.getLogger(WelcomeDialog.class.getPackage().getName());

	public static final int NEW_PROJECT = 0;

	public static final int OPEN_PROJECT = 1;

	public static final int QUIT = 2;

	public static final int OPEN_LAST_PROJECT = 3;

	public static final int OPEN_MODULE = 4;

	protected int result;

	private JScrollPane scroll = null;

	protected JList fileList;

	private JLabel fileListLabel;

	public WelcomeDialog() {
		super();
		moduleSelected(welcomePanel.getFirstlaunchedModule());
	}

	JButton openButton;
	JButton newButton;
	JButton openModuleButton;
	JButton quitButton;

	@Override
	protected void addButtons() {
		if (GeneralPreferences.getLastOpenedProjects().size() > 0) {
			fileListLabel = new JLabel(FlexoLocalization.localizedForKey("last_opened_projects"));
			fileListLabel.setForeground(FlexoCst.WELCOME_FLEXO_COLOR);
			fileList = new JList(GeneralPreferences.getLastOpenedProjects());
			fileList.setCellRenderer(new DefaultListCellRenderer() {

				@Override
				public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
					JLabel l = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
					File f = (File) value;
					l.setText(f.getName());
					l.setIcon(IconLibrary.OPENFLEXO_NOTEXT_16);
					l.setToolTipText(f.getAbsolutePath() + " - " + FlexoLocalization.localizedForKey("double_click_to_open"));
					if (!isSelected) {
						l.setBackground(FlexoCst.WELCOME_FLEXO_BG_LIST_COLOR);
					}
					return l;
				}

			});
			fileList.addMouseListener(new MouseAdapter() {

				private File lastSelected = null;

				/**
				 * Overrides mouseClicked
				 * 
				 * @see java.awt.event.MouseAdapter#mouseClicked(java.awt.event.MouseEvent)
				 */
				@Override
				public void mouseClicked(MouseEvent e) {
					if (e.getClickCount() == 1) {
						if (fileList.getSelectedValue() != null && fileList.getSelectedValue() == lastSelected) {
							fileList.clearSelection();
							e.consume();
						}
					}
					lastSelected = (File) fileList.getSelectedValue();
					if (e.getClickCount() > 1 && fileList.isEnabled()) {
						File project = (File) fileList.getSelectedValue();
						if (project != null) {
							GeneralPreferences.addToLastOpenedProjects(project);
						}
						result = OPEN_LAST_PROJECT;
						// firstLaunchedModule = welcomePanel.getFirstlaunchedModule();
						dispose();
					}
				}
			});
			scroll = new JScrollPane(fileList);
			scroll.setBackground(FlexoCst.WELCOME_FLEXO_BG_LIST_COLOR);
			scroll.setBorder(BorderFactory.createEtchedBorder());

			welcomePanel.add(fileListLabel);
			welcomePanel.add(scroll);
		}

		openButton = new JButton(FlexoLocalization.localizedForKey("open_project"));
		// openButton.setToolTipText(FlexoLocalization.localizedForKey("open_a_dialog_to_choose_a_project"));
		openButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (fileList != null && fileList.getSelectedValue() != null) {
					GeneralPreferences.addToLastOpenedProjects((File) fileList.getSelectedValue());
					result = OPEN_LAST_PROJECT;
					// firstLaunchedModule = welcomePanel.getFirstlaunchedModule();
					dispose();
				} else {
					result = OPEN_PROJECT;
					// firstLaunchedModule = welcomePanel.getFirstlaunchedModule();
					dispose();
				}
			}
		});
		newButton = new JButton(FlexoLocalization.localizedForKey("new_project"));
		newButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				result = NEW_PROJECT;
				// firstLaunchedModule = welcomePanel.getFirstlaunchedModule();
				dispose();
			}
		});
		openModuleButton = new JButton(FlexoLocalization.localizedForKey("open_module"));
		openModuleButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				result = OPEN_MODULE;
				dispose();
			}
		});
		quitButton = new JButton(FlexoLocalization.localizedForKey("quit"));
		quitButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				result = QUIT;
				dispose();
				ModuleLoader.quit(false);
			}
		});

		JPanel buttonsPanel = new JPanel(new FlowLayout());

		/*
		 * openButton.setBackground(FlexoCst.DARK_BLUE_FLEXO_COLOR); newButton.setBackground(FlexoCst.DARK_BLUE_FLEXO_COLOR);
		 * openModuleButton.setBackground(FlexoCst.DARK_BLUE_FLEXO_COLOR); licenceButton.setBackground(FlexoCst.DARK_BLUE_FLEXO_COLOR);
		 * quitButton.setBackground(FlexoCst.DARK_BLUE_FLEXO_COLOR);
		 */

		openButton.setOpaque(false);
		newButton.setOpaque(false);
		openModuleButton.setOpaque(false);
		quitButton.setOpaque(false);
		boolean isButtonBackgroundPainted = false;
		if (AdvancedPrefs.getLookAndFeelString() != null && AdvancedPrefs.getLookAndFeelString().indexOf("Windows") > 0) {
			Toolkit toolkit = Toolkit.getDefaultToolkit();
			Boolean themeActive = (Boolean) toolkit.getDesktopProperty("win.xpstyle.themeActive");
			if (themeActive == null) {
				themeActive = Boolean.FALSE;
			}
			if (themeActive.booleanValue()) {
				GetPropertyAction propertyAction = new GetPropertyAction("swing.noxp");
				if (AccessController.doPrivileged(propertyAction) == null &&
						// ThemeReader.isThemed() &&
						!(UIManager.getLookAndFeel() instanceof WindowsClassicLookAndFeel)) {
					isButtonBackgroundPainted = true; // Complicated but this is one of the cases where the bg_color of buttons is painted
				}
			}
		}
		isButtonBackgroundPainted |= UIManager.getLookAndFeel() instanceof MetalLookAndFeel;// Easy one
		isButtonBackgroundPainted |= UIManager.getLookAndFeel().getName().indexOf("Nimbus") > -1;// Easy one
		isButtonBackgroundPainted |= ToolBox.getPLATFORM() == ToolBox.MACOS;// Always true for MAC OS
		if (!isButtonBackgroundPainted) { // If the background is not painted, then we are sure that the background will be DARK_BLUE and
			// therefore we can force the text color to ensure a proper contrast between background and
			// foreground color.
			openButton.setForeground(Color.WHITE);
			newButton.setForeground(Color.WHITE);
			openModuleButton.setForeground(Color.WHITE);
			quitButton.setForeground(Color.WHITE);
		}
		buttonsPanel.add(openButton);
		buttonsPanel.add(newButton);
		buttonsPanel.add(openModuleButton);
		buttonsPanel.add(quitButton);
		buttonsPanel.setOpaque(false);
		getContentPane().add(buttonsPanel, BorderLayout.SOUTH);

		/*
		 * welcomePanel.add(openButton); welcomePanel.add(newButton); welcomePanel.add(quitButton); if (welcomePanel instanceof
		 * MultiModuleModeWelcomePanel) { if (scroll != null) { fileListLabel.setBounds(170, 210 + ((MultiModuleModeWelcomePanel)
		 * welcomePanel).getScrollPaneHeight(), 200, 25); scroll.setBounds(170, 242 + ((MultiModuleModeWelcomePanel)
		 * welcomePanel).getScrollPaneHeight(), 240, 3 + ((int) fileList.getCellBounds(0, 0).getHeight() + 1) *
		 * fileList.getModel().getSize()); } openButton.setBounds(60, 360 + ((MultiModuleModeWelcomePanel)
		 * welcomePanel).getScrollPaneHeight(), 140, 30); newButton.setBounds(210, 360 + ((MultiModuleModeWelcomePanel)
		 * welcomePanel).getScrollPaneHeight(), 140, 30); quitButton.setBounds(360, 360 + ((MultiModuleModeWelcomePanel)
		 * welcomePanel).getScrollPaneHeight(), 140, 30); } else if (welcomePanel instanceof SingleModuleModeWelcomePanel) { if (scroll !=
		 * null) { fileListLabel.setBounds(170, 232, 200, 25); scroll.setBounds(170, 260, 240, 3 + (int) (fileList.getCellBounds(0,
		 * 0).getHeight() + 1) * fileList.getModel().getSize()); } openButton.setBounds(60, 440, 140, 30); newButton.setBounds(210, 440,
		 * 140, 30); quitButton.setBounds(360, 440, 140, 30); }
		 */

		if (welcomePanel instanceof MultiModuleModeWelcomePanel) {
			if (scroll != null) {
				fileListLabel.setBounds(170, 210 + ((MultiModuleModeWelcomePanel) welcomePanel).getScrollPaneHeight(), 200, 25);
				scroll.setBounds(120, 242 + ((MultiModuleModeWelcomePanel) welcomePanel).getScrollPaneHeight(), 340, 3 + ((int) fileList
						.getCellBounds(0, 0).getHeight() + 1) * fileList.getModel().getSize());
			}
		} else if (welcomePanel instanceof SingleModuleModeWelcomePanel) {
			if (scroll != null) {
				fileListLabel.setBounds(170, 232, 200, 25);
				scroll.setBounds(120, 260, 340, 3 + (int) (fileList.getCellBounds(0, 0).getHeight() + 1) * fileList.getModel().getSize());
			}
		}
	}

	public int getProjectSelectionChoice() {
		setVisible(true);
		toFront();
		return result;
	}

	public Module getFirstlaunchedModule() {
		return welcomePanel.getFirstlaunchedModule();
	}

	@Override
	public void moduleSelected(Module selectedModule) {
		if (selectedModule == null) {
			return;
		}
		// logger.info("Module "+selectedModule+" has been selected");
		openButton.setVisible(selectedModule.requireProject());
		newButton.setVisible(selectedModule.requireProject());
		openModuleButton.setVisible(selectedModule.requireProject());
		if (fileList != null) {
			fileList.setEnabled(selectedModule.requireProject());
		}
	}

}
