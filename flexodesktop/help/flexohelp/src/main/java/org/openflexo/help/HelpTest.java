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
package org.openflexo.help;

import javax.help.CSH;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

/**
 * Please comment this class
 * 
 * @author sguerin
 * 
 */
public class HelpTest {
	JFrame f;

	JMenuItem overviewHelp;

	JMenuItem wkfModuleHelp;

	JMenuItem ieModuleHelp;

	JMenuItem dmModuleHelp;

	JMenuItem cgfModuleHelp;

	public HelpTest() {
		f = new JFrame("Test help");
		JButton button = new JButton("DM Module");
		f.getContentPane().add(button);
		JMenuBar mbar = new JMenuBar();
		// menus Fichier et Aide
		JMenu help = new JMenu("Aide");
		// ajout d un item dans le menu Aide
		help.add(overviewHelp = new JMenuItem("Overview"));
		help.add(wkfModuleHelp = new JMenuItem("WKFModule"));
		// ajout des menu a la barre de menu
		mbar.add(help);
		// creation des objetsHelpSet et HelpBroker
		// affectation de l aide au composant
		CSH.setHelpIDString(overviewHelp, "top");
		CSH.setHelpIDString(wkfModuleHelp, "wkf-module");
		CSH.setHelpIDString(button, "dm-module");
		// gestion des evenements
		overviewHelp.addActionListener(new CSH.DisplayHelpFromSource(FlexoHelp.getHelpBroker()));
		wkfModuleHelp.addActionListener(new CSH.DisplayHelpFromSource(FlexoHelp.getHelpBroker()));
		button.addActionListener(new CSH.DisplayHelpFromSource(FlexoHelp.getHelpBroker()));
		// attachement de la barre de menu a la fenetre
		f.setJMenuBar(mbar);
		f.setSize(500, 300);
		f.setVisible(true);
	}

	public static void main(String argv[]) {
		new HelpTest();
	}

}
