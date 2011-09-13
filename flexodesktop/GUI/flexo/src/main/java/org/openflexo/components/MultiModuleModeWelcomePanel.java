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

import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.MouseEvent;
import java.util.Enumeration;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.openflexo.FlexoCst;
import org.openflexo.GeneralPreferences;
import org.openflexo.module.Module;
import org.openflexo.module.ModuleLoader;
import org.openflexo.toolbox.ToolBox;

/**
 * Welcome Panel in the context of MULTI MODULE MODE
 * 
 * @author sguerin
 */
public class MultiModuleModeWelcomePanel extends WelcomePanel {
	private JScrollPane scrollPaneList;

	private JList modulesList;

	private JTextField releaseType;

	private JLabel releaseTypeLabel;

	protected Module firstLaunchedModule;

	private int scrollPaneHeight = -1;

	protected class ModuleRenderer extends DefaultListCellRenderer {

		/*
		 * This is the only method defined by ListCellRenderer. We just reconfigure the Jlabel each time we're called.
		 */
		@Override
		public Component getListCellRendererComponent(JList list, Object value, // value
				// to
				// display
				int index, // cell index
				boolean isSelected, // is the cell selected
				boolean chf) // the list and the cell have the focus
		{
			/*
			 * The DefaultListCellRenderer class will take care of the JLabels text property, it's foreground and background colors, and so
			 * on.
			 */
			Module module = (Module) value;
			super.getListCellRendererComponent(list, value, index, isSelected, chf);
			setText(module.getLocalizedName());
			setIcon(module.getActiveIcon());
			setBorder(BorderFactory.createEmptyBorder(1, 4, 1, 2));
			if (isSelected) {
				firstLaunchedModule = module;
			}
			if (!isSelected)
				setBackground(FlexoCst.WELCOME_FLEXO_BG_LIST_COLOR);
			return this;
		}
	}

	public MultiModuleModeWelcomePanel() {
		super();
		setFont(FlexoCst.NORMAL_FONT);

		// Vector modulesVector = ModuleLoader.allKnownModules();
		Vector<Module> modulesVector = ModuleLoader.availableModules();
		// construct modules array
		Module[] modules = new Module[modulesVector.size()];
		int i = 0;
		for (Enumeration<Module> e = modulesVector.elements(); e.hasMoreElements(); i++) {
			modules[i] = e.nextElement();
		}

		// construct components
		modulesList = new JList(modules) {
			@Override
			public String getToolTipText(MouseEvent evt) {
				// Get item index
				int index = locationToIndex(evt.getPoint());
				// Get item
				Module module = (Module) getModel().getElementAt(index);
				// Return the tool tip text
				return module.getLocalizedDescription();
			}
		};
		modulesList.setVisibleRowCount(modulesVector.size());
		modulesList.setCellRenderer(new ModuleRenderer());
		modulesList.setBackground(FlexoCst.WELCOME_FLEXO_BG_LIST_COLOR);
		modulesList.addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e) {
				for (ModuleSelectionListener l : moduleSelectionListeners) {
					l.moduleSelected((Module) modulesList.getSelectedValue());
				}
			}
		});
		modulesList.setSelectedIndex(0);
		if (GeneralPreferences.getFavoriteModuleName() != null) {
			for (int j = 0; j < modules.length; j++) {
				if (modules[j].getName().equals(GeneralPreferences.getFavoriteModuleName()))
					modulesList.setSelectedIndex(j);
			}
		}

		/*
		 * modulesList.addMouseListener(new MouseAdapter() { /** Overrides mouseClicked
		 * 
		 * @see java.awt.event.MouseAdapter#mouseClicked(java.awt.event.MouseEvent)
		 */
		/*
		 * public void mouseClicked(MouseEvent e) { if (e.getClickCount()==2) } });
		 */
		scrollPaneList = new JScrollPane(modulesList);
		scrollPaneList.setWheelScrollingEnabled(true);
		scrollPaneList.setBackground(FlexoCst.WELCOME_FLEXO_BG_LIST_COLOR);
		scrollPaneList.setBorder(BorderFactory.createEtchedBorder());
		/*
		 * releaseTypeLabel = new JLabel(FlexoLocalization.localizedForKey("release_type")); releaseType = new JTextField(10);
		 * releaseType.setOpaque(false); releaseType.setHorizontalAlignment(SwingConstants.CENTER);
		 * releaseType.setText(ModuleLoader.getReleaseName()); releaseType.setEditable(false);
		 */
		// adjust size and set layout
		setLayout(null);

		// add components
		add(scrollPaneList);
		// add(releaseTypeLabel);
		// add(releaseType);

		scrollPaneHeight = (ToolBox.getPLATFORM() == ToolBox.MACOS ? 16 : 2) + ((int) modulesList.getCellBounds(0, 0).getHeight() + 1) * modules.length;
		// set component bounds (only needed by Absolute Positioning)
		// flexoLogo.setBounds(175, 20, 230, 80);
		scrollPaneList.setBounds(120, 200, 340, scrollPaneHeight);
		titleLabel.setBounds(95, 125, 400, 30);
		versionLabel.setBounds(165, 150, 250, 30);
		/*
		 * comment1Label.setBounds(75, 340 + scrollPaneHeight, 400, 15); comment2Label.setBounds(75, 350 + scrollPaneHeight, 400, 15);
		 * comment3Label.setBounds(75, 360 + scrollPaneHeight, 400, 15);
		 */
		// releaseTypeLabel.setBounds(170, 172, 85, 25);
		// releaseType.setBounds(260, 170, 150, 22);
		setPreferredSize(new Dimension(580, 400 + scrollPaneHeight));
	}

	@Override
	public Module getFirstlaunchedModule() {
		if (firstLaunchedModule == null)
			firstLaunchedModule = (Module) modulesList.getSelectedValue();
		return firstLaunchedModule;
	}

	public int getScrollPaneHeight() {
		return scrollPaneHeight;
	}

	private Vector<ModuleSelectionListener> moduleSelectionListeners = new Vector<ModuleSelectionListener>();

	public void addToModuleSelectionListener(ModuleSelectionListener l) {
		moduleSelectionListeners.add(l);
	}

	public void removeFromModuleSelectionListener(ModuleSelectionListener l) {
		moduleSelectionListeners.remove(l);
	}

	public static interface ModuleSelectionListener {
		public void moduleSelected(Module selectedModule);
	}
}
