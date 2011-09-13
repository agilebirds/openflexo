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
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import org.openflexo.FlexoCst;
import org.openflexo.icon.IconLibrary;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.module.Module;
import org.openflexo.module.ModuleLoader;

/**
 * Abstract Welcome Panel
 *
 * @author sguerin
 */
public abstract class WelcomePanel extends JPanel
{

    protected JLabel titleLabel;

    protected JLabel versionLabel;

    //protected JLabel comment1Label;

    //protected JLabel comment2Label;

    //protected JLabel comment3Label;

    //protected JLabel flexoLogo;

    protected WelcomePanel()
    {
        super();
        setOpaque(false);
        setFont(FlexoCst.NORMAL_FONT);
        titleLabel = new JLabel(FlexoLocalization.localizedForKey("welcome_to_flexo")+" "+ModuleLoader.getUserType().getBusinessName2(), SwingConstants.CENTER);
        titleLabel.setFont(FlexoCst.TITLE_FONT);
        titleLabel.setForeground(FlexoCst.WELCOME_FLEXO_COLOR);
        versionLabel = new JLabel("Version " + FlexoCst.BUSINESS_APPLICATION_VERSION+ " (build " + FlexoCst.BUILD_ID+")", SwingConstants.CENTER);
        versionLabel.setFont(FlexoCst.BIG_FONT);
        versionLabel.setForeground(FlexoCst.WELCOME_FLEXO_COLOR);
        /*comment1Label = new JLabel(FlexoLocalization.localizedForKey("you_can_submit_your_improvements_and_bug_reports"), SwingConstants.CENTER);
        comment1Label.setFont(FlexoCst.SMALL_FONT);
        comment2Label = new JLabel(FlexoLocalization.localizedForKey("by_editing_the_article_workfloweditor_improvement_request"), SwingConstants.CENTER);
        comment2Label.setFont(FlexoCst.SMALL_FONT);
        comment3Label = new JLabel(FlexoLocalization.localizedForKey("diko_section_devtools_workfloweditor"), SwingConstants.CENTER);
        comment3Label.setFont(FlexoCst.SMALL_FONT);*/
        //flexoLogo = new JLabel(FlexoCst.LOGIN_IMAGE);

        //setBorder(BorderFactory.createRaisedBevelBorder());

        // adjust size and set layout
        setPreferredSize(new Dimension(580, 560));
        setLayout(null);

        // add components
        //add(flexoLogo);
        add(titleLabel);
        add(versionLabel);
        /*add(comment1Label);
        add(comment2Label);
        add(comment3Label);*/
        icon = IconLibrary.WELCOME_BACKGROUND;

    }
    private final ImageIcon icon;
    @Override
	protected void paintComponent(Graphics g)
	{
		//  Dispaly image at at full size
    	if(!(this instanceof SingleModuleModeWelcomePanel)){
    		g.drawImage(icon.getImage(), 0, 0, null);
    	}
		//  Scale image to size of component
//		Dimension d = getSize();
//		g.drawImage(icon.getImage(), 0, 0, d.width, d.height, null);

		//  Fix the image position in the scroll pane
//		Point p = scrollPane.getViewport().getViewPosition();
//		g.drawImage(icon.getImage(), p.x, p.y, null);

		super.paintComponent(g);
	}

    public abstract Module getFirstlaunchedModule();
}
