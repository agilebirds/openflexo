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
package test;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;

import cb.parser.PetalParser;
import cb.petal.PetalFile;
import cb.util.PetalTree;

/**
 * Display petal file visually. Property names are displayed as tool tips.
 * 
 * @version $Id: Test3.java,v 1.3 2011/09/12 11:47:32 gpolet Exp $
 * @author <A HREF="mailto:markus.dahm@berlin.de">M. Dahm</A>
 */
public class Test3 {
	public static void main(String[] args) throws Exception {
		PetalParser parser;

		if (args.length == 0) {
			parser = PetalParser.createParser(System.in);
		} else {
			parser = PetalParser.createParser(args[0]);
		}

		PetalFile tree = parser.parse();

		JTree jtree = new PetalTree(tree);
		final JFrame frame = new JFrame();
		JPanel panel = new JPanel();
		panel.setLayout(new java.awt.BorderLayout());
		frame.setContentPane(panel);

		JScrollPane scroll = new JScrollPane();
		scroll.setPreferredSize(new java.awt.Dimension(400, 500));
		scroll.setViewportView(jtree);

		panel.add(scroll, java.awt.BorderLayout.CENTER);

		frame.addWindowListener(new java.awt.event.WindowAdapter() {
			@Override
			public void windowClosing(java.awt.event.WindowEvent event) {
				frame.setVisible(false);
				frame.dispose();
				System.exit(0);
			}
		});

		frame.pack();
		frame.setVisible(true);
	}
}
