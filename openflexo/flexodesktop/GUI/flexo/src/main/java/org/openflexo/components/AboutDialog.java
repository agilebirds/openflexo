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
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;

import org.openflexo.FlexoCst;
import org.openflexo.components.MultiModuleModeWelcomePanel.ModuleSelectionListener;

import org.openflexo.localization.FlexoLocalization;
import org.openflexo.module.Module;
import org.openflexo.module.ModuleLoader;
import org.openflexo.toolbox.ToolBox;
import org.openflexo.view.FlexoDialog;
import org.openflexo.view.FlexoFrame;

/**
 * 'About' window
 *
 * @author sguerin
 */
public class AboutDialog extends FlexoDialog implements ModuleSelectionListener
{

    protected WelcomePanel welcomePanel;

    public AboutDialog()
    {
        super(FlexoFrame.getActiveFrame(), FlexoCst.BUSINESS_APPLICATION_VERSION_NAME, true);
        setUndecorated(true);
        getContentPane().setBackground(FlexoCst.DARK_BLUE_FLEXO_COLOR);
        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(welcomePanel = ModuleLoader.getWelcomePanel(),BorderLayout.CENTER);
        if (welcomePanel instanceof MultiModuleModeWelcomePanel) {
        	((MultiModuleModeWelcomePanel)welcomePanel).addToModuleSelectionListener(this);
        }
        addButtons();
        pack();
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        setLocation((dim.width - getSize().width) / 2, (dim.height - getSize().height) / 2);
    }

    protected void addButtons()
    {
        JButton closeButton;
        closeButton = new JButton(FlexoLocalization.localizedForKey("close"));
        closeButton.addActionListener(new ActionListener() {
            @Override
			public void actionPerformed(ActionEvent e)
            {
                dispose();
            }
        });
        welcomePanel.add(closeButton);
        if (welcomePanel instanceof MultiModuleModeWelcomePanel) {
            closeButton.setBounds(140, 360+((MultiModuleModeWelcomePanel)welcomePanel).getScrollPaneHeight(), 140, 30);
        } else if (welcomePanel instanceof SingleModuleModeWelcomePanel) {
            closeButton.setBounds(140, 440, 140, 30);
        }
        JButton licenceButton;
        licenceButton = new JButton(FlexoLocalization.localizedForKey("licence"));
        licenceButton.setToolTipText(FlexoLocalization.localizedForKey("open_licence_agreement"));
        licenceButton.addActionListener(new ActionListener() {
        	@Override
			public void actionPerformed(ActionEvent e)
        	{
        		ToolBox.openFile(ModuleLoader.getUserType().getLicenceFile());
        		dispose();
        	}
        });
        welcomePanel.add(licenceButton);
        if (welcomePanel instanceof MultiModuleModeWelcomePanel) {
        	licenceButton.setBounds(300, 360+((MultiModuleModeWelcomePanel)welcomePanel).getScrollPaneHeight(), 140, 30);
        } else if (welcomePanel instanceof SingleModuleModeWelcomePanel) {
        	licenceButton.setBounds(300, 440, 140, 30);
        }
        getRootPane().setDefaultButton(closeButton);
    }

	@Override
	public void moduleSelected(Module selectedModule)
	{
	}
}
