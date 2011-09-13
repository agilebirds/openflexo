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
package org.openflexo.view.menu;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.Observable;
import java.util.Observer;

import javax.help.CSH;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;

import org.openflexo.ch.TrackComponentCHForHelpSubmission;
import org.openflexo.ch.TrackComponentCHForHelpView;
import org.openflexo.components.AboutDialog;
import org.openflexo.drm.DocResourceManager;


import org.openflexo.help.FlexoHelp;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.module.Module;
import org.openflexo.module.ModuleLoader;
import org.openflexo.toolbox.ToolBox;
import org.openflexo.view.controller.FlexoController;

/**
 * Automatic builded 'Help' menu for modules
 * 
 * @author sguerin
 */
public class HelpMenu extends FlexoMenu implements Observer
{

    private JMenuItem generalHelp;

    private JMenuItem flexoHelp;

    private JMenuItem modelingHelp;

    private JMenuItem helpOn;
    
    private JMenuItem submitHelpFor;
    
    private JMenuItem aboutFlexo;

    private JMenuItem[] modulesHelp;

    private ActionListener helpActionListener;
    
    public HelpMenu(final FlexoController controller)
    {
        super("help", controller);

        helpActionListener = new CSH.DisplayHelpFromSource(FlexoHelp.getHelpBroker());
        
        generalHelp = new JMenuItem();
        generalHelp.setAccelerator(ToolBox.getPLATFORM()==ToolBox.MACOS?KeyStroke.getKeyStroke(KeyEvent.VK_HELP,0):KeyStroke.getKeyStroke(KeyEvent.VK_F1,0));
        generalHelp.setText(FlexoLocalization.localizedForKey("general_help", generalHelp));
        CSH.setHelpIDString(generalHelp, DocResourceManager.instance().getDocResourceCenterItem().getIdentifier());
        generalHelp.addActionListener(helpActionListener);
        add(generalHelp);

        flexoHelp = new JMenuItem();
        flexoHelp.setText(FlexoLocalization.localizedForKey("flexo_help", flexoHelp));
        CSH.setHelpIDString(flexoHelp, DocResourceManager.instance().getFlexoToolSetItem().getIdentifier());
         flexoHelp.addActionListener(helpActionListener);
        add(flexoHelp);

        modelingHelp = new JMenuItem();
        modelingHelp.setText(FlexoLocalization.localizedForKey("modeling_help", modelingHelp));
        CSH.setHelpIDString(modelingHelp, DocResourceManager.instance().getFlexoModelItem().getIdentifier());
        modelingHelp.addActionListener(helpActionListener);
        add(modelingHelp);
        
         addSeparator();
        modulesHelp = new JMenuItem[ModuleLoader.availableModules().size()];
        for (int i = 0; i < ModuleLoader.availableModules().size(); i++) {
            Module module = ModuleLoader.availableModules().elementAt(i);
            modulesHelp[i] = new JMenuItem();
            modulesHelp[i].setText(FlexoLocalization.localizedForKey(module.getName(), modulesHelp[i]));
            CSH.setHelpIDString(modulesHelp[i], module.getHelpTopic());
            modulesHelp[i].addActionListener(helpActionListener);
            add(modulesHelp[i]);
        }

        addSeparator();
        helpOn = new JMenuItem();
        helpOn.setText(FlexoLocalization.localizedForKey("help_on", helpOn));
        //helpOn.addActionListener(new CSH.DisplayHelpAfterTracking(FlexoHelp.getHelpBroker()));
        helpOn.addActionListener(new ActionListener() {
           @Override
		public void actionPerformed(ActionEvent e) {
               new TrackComponentCHForHelpView(controller.getFlexoFrame());
            }
        });
        add(helpOn);
        
        if (ModuleLoader.allowsDocSubmission()) {
            submitHelpFor = new JMenuItem();
            submitHelpFor.setText(FlexoLocalization.localizedForKey("submit_help_for", submitHelpFor));
            //helpOn.addActionListener(new CSH.DisplayHelpAfterTracking(FlexoHelp.getHelpBroker()));
            submitHelpFor.addActionListener(new ActionListener() {
                @Override
				public void actionPerformed(ActionEvent e) {
                    new TrackComponentCHForHelpSubmission(controller.getFlexoFrame());
                }
            });
            add(submitHelpFor);
        }
        
        if (ToolBox.getPLATFORM()!=ToolBox.MACOS) {
            addSeparator();
            aboutFlexo = new JMenuItem();
            aboutFlexo.setText(FlexoLocalization.localizedForKey("about_flexo",aboutFlexo));
            aboutFlexo.addActionListener(new ActionListener() {

                @Override
				public void actionPerformed(ActionEvent e)
                {
                    AboutDialog about = new AboutDialog();
                    about.setVisible(true);
                }
                
            });
            add(aboutFlexo);
        }
        
        FlexoHelp.instance.addObserver(this);
   }

    @Override
	public void update(Observable observable, Object arg) 
    {
        if (observable instanceof FlexoHelp) {
            generalHelp.removeActionListener(helpActionListener);
            flexoHelp.removeActionListener(helpActionListener);
            modelingHelp.removeActionListener(helpActionListener);
            for (JMenuItem item : modulesHelp) {
                item.removeActionListener(helpActionListener);
            }
            helpActionListener = new CSH.DisplayHelpFromSource(FlexoHelp.getHelpBroker());
            generalHelp.addActionListener(helpActionListener);
            flexoHelp.addActionListener(helpActionListener);
            modelingHelp.addActionListener(helpActionListener);
            for (JMenuItem item : modulesHelp) {
                item.addActionListener(helpActionListener);
            }
       }
    }
}
