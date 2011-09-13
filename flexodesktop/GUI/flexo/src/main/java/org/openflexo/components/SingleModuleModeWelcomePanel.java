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

import java.awt.Dimension;
import java.awt.Graphics;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.SwingConstants;

import org.openflexo.FlexoCst;
import org.openflexo.icon.IconLibrary;
import org.openflexo.module.Module;
import org.openflexo.module.ModuleLoader;


/**
 * Welcome Panel in the context of SINGLE MODULE MODE
 * 
 * @author sguerin
 */
public class SingleModuleModeWelcomePanel extends WelcomePanel
{

    private final JLabel moduleTitleLabel;

    public SingleModuleModeWelcomePanel()
    {
        super();

        moduleTitleLabel = new JLabel(ModuleLoader.getSingleModuleModeModule().getLocalizedName(), SwingConstants.CENTER);
        moduleTitleLabel.setFont(FlexoCst.BIG_FONT);
        add(moduleTitleLabel);

        //flexoLogo.setBounds(175, 20, 230, 80);
        titleLabel.setBounds(95, 125, 400, 30);
        versionLabel.setBounds(190, 150, 200, 30);
        moduleTitleLabel.setBounds(125, 165, 300, 30);

        setPreferredSize(new Dimension(560, 460));
        icon = IconLibrary.WELCOME_BACKGROUND;
    }

    private final ImageIcon icon;
    @Override
	protected void paintComponent(Graphics g)
	{
		//  Dispaly image at at full size
		g.drawImage(icon.getImage(), 0, 0, null);

		//  Scale image to size of component
//		Dimension d = getSize();
//		g.drawImage(icon.getImage(), 0, 0, d.width, d.height, null);

		//  Fix the image position in the scroll pane
//		Point p = scrollPane.getViewport().getViewPosition();
//		g.drawImage(icon.getImage(), p.x, p.y, null);

		super.paintComponent(g);
	}
    
    @Override
	public Module getFirstlaunchedModule()
    {
        return null;
    }
}
